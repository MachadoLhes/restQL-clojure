(ns restql.core.async-request
  (:use [slingshot.slingshot :only [throw+]])
  (:require [clojure.core.async :as a :refer [chan go go-loop >! <!]]
            [aleph.http :as http]
            [manifold.deferred :as d]
            [restql.core.async-request-builder :as builder]
            [restql.core.query :as query]
            [restql.core.hooks.core :as hook]
            [clojure.tools.logging :as log]
            [restql.core.extractor :refer [traverse]]
            [slingshot.slingshot :refer [try+]]
            [cheshire.core :as json]
            [environ.core :refer [env]]
            [clojure.walk :refer [stringify-keys keywordize-keys]])
    (:import [java.net URLDecoder URI]))

(defn get-pool-connections-per-host
  [default]
  (if (contains? env :pool-connections-per-host) (read-string (env :pool-connections-per-host)) default))

(defn get-pool-total-connections
  [default]
  (if (contains? env :pool-total-connections) (read-string (env :pool-total-connections)) default))

(defn get-pool-max-queue-size
  [default]
  (if (contains? env :pool-max-queue-size) (read-string (env :pool-max-queue-size)) default))

(defonce client-connection-pool
  (http/connection-pool {:connections-per-host (get-pool-connections-per-host 100)
                         :total-connections (get-pool-total-connections 10000)
                         :max-queue-size (get-pool-max-queue-size 65536)}))

(defn get-service-endpoint [mappings entity]
  (if (nil? (mappings entity))
    (throw+ (str "Resource endpoint not found" entity))
    (mappings entity)))

(defn fmap [f m]
  (reduce-kv #(assoc %1 %2 (f %3)) {} m))

(defn decode-url [string]
  (try
    (URLDecoder/decode string "utf-8")
    (catch Exception e
      string)))

(defn parse-query-params
  "this function takes a request object (with :url and :query-params)
  and transforms query params that are sets into vectors"
  [request]
  (update-in request [:query-params]
             #(fmap (fn [query-param-value]
                      (if (or (sequential? query-param-value) (set? query-param-value))
                        (->> query-param-value (map decode-url) (into []))
                        (decode-url query-param-value))) %)))

(defn convert-response [{:keys [status body headers]} {:keys [debugging metadata time url params timeout resource]}]
  (let [parsed (if (string? body) body (slurp body))
        base {:status        status
              :headers       headers
              :url           url
              :metadata      metadata
              :timeout       timeout
              :params        params
              :resource      resource
              :response-time time}]
    (try
      (assoc base
        :body (json/parse-string parsed true))
      (catch Exception e
        (log/error {:message (.getMessage e)}
               "error parsing request")
        (assoc base
          :parse-error true
          :body parsed)))))

(defn get-error-status [exception]
  (cond
    (instance? clojure.lang.ExceptionInfo exception) 408
    (instance? aleph.utils.RequestTimeoutException exception) 408
    (instance? aleph.utils.ConnectionTimeoutException exception) 408
    (instance? aleph.utils.PoolTimeoutException exception) 408
    (instance? aleph.utils.ReadTimeoutException exception) 408
    (instance? aleph.utils.ProxyConnectionTimeoutException exception) 408
    :else 0))

(defn get-error-message [exception]
  (cond
    (instance? clojure.lang.ExceptionInfo exception) (.getMessage exception)
    (instance? aleph.utils.RequestTimeoutException exception) "RequestTimeoutException"
    (instance? aleph.utils.ConnectionTimeoutException exception) "ConnectionTimeoutException"
    (instance? aleph.utils.PoolTimeoutException exception) "PoolTimeoutException"
    (instance? aleph.utils.ReadTimeoutException exception) "ReadTimeoutException"
    (instance? aleph.utils.ProxyConnectionTimeoutException exception) "ProxyConnectionTimeoutException"
    :else "Internal error"))

(defn request-respond-callback [result & {:keys [request
                                                 request-timeout
                                                 time-before
                                                 query-opts
                                                 output-ch]}]
        (let [log-data {:resource (:resource request)
                        :timeout  request-timeout
                        :success  true}]
          (log/debug (assoc log-data :success true
                                 :status (:status result)
                                 :time (- (System/currentTimeMillis) time-before))
                                 "Request successful")
          (let [response (convert-response result {:debugging (:debugging query-opts)
                                                   :metadata  (:metadata request)
                                                   :resource  (:resource request)
                                                   :url       (:url request)
                                                   :params    (:query-params request)
                                                   :timeout   request-timeout
                                                   :time      (- (System/currentTimeMillis) time-before)})]
            ; After Request hook
            (try
              (hook/execute-hook query-opts :after-request (reduce-kv (fn [result k v]
                                                                      (if (= k :body)
                                                                        (assoc result k (json/generate-string v))
                                                                        (assoc result k v)))
                                                                    {} response))
            (catch Exception e
               (log/debug {:message "failure executing hook :after-request"
                       :exception e })))
            ; Send response to channel
            (go (>! output-ch response)))))

(defn request-error-callback [exception & {:keys [request
                                                  request-timeout
                                                  time-before
                                                  query-opts
                                                  output-ch]}]
        (if (and (instance? clojure.lang.ExceptionInfo exception) (:body (.getData exception)))
          (request-respond-callback (.getData exception)
                                    :request request
                                    :request-timeout request-timeout
                                    :query-opts query-opts
                                    :time-before time-before
                                    :output-ch output-ch)
          (let [error-status (get-error-status exception)
                log-data {:resource (:resource request)
                          :timeout  request-timeout
                          :success  false}]
            (log/debug (assoc log-data :success false
                                  :status error-status
                                  :time (- (System/currentTimeMillis) time-before)))
            (let [error-data (assoc log-data :success false
                                            :status error-status
                                            :metadata (some-> request :metadata)
                                            :url (some-> request :url)
                                            :params    (:query-params request)
                                            :time (- (System/currentTimeMillis) time-before)
                                            :errordetail (pr-str (some-> exception :error)))]
              (log/error error-data "Request failed")
              ; After Request hook
              (hook/execute-hook query-opts :after-request error-data)
              ; Send error response to channel
              (go (>! output-ch {:status   error-status
                                :metadata (:metadata request)
                                :body     {:message (get-error-message exception)}}))))))

(defn make-request
  ([request query-opts]
   (let [output-ch (chan)]
     (make-request request query-opts output-ch)
     output-ch))
  ([request query-opts output-ch]
   (let [request         (parse-query-params request)
         time-before     (System/currentTimeMillis)
         request-timeout (if (nil? (:timeout request)) (:timeout query-opts) (:timeout request))
         forward         (some-> query-opts :forward-params)
         forward-params  (if (nil? forward) {} forward)
         request-map     {:url                (:url request)
                          :request-method     (:http-method request)
                          :content-type       "application/json"
                          :resource           (:resource request)
                          :connection-timeout request-timeout
                          :request-timeout    request-timeout
                          :read-timeout       request-timeout
                          :query-params       (into (:query-params request) forward-params)
                          :headers            (:headers request)
                          :time               time-before
                          :body               (some-> request :post-body)
                          :pool               client-connection-pool
                          :pool-timeout       request-timeout}]
     (log/debug request-map "Preparing request")
     ; Before Request hook
     (hook/execute-hook query-opts :before-request request-map)
     (d/on-realized (http/request request-map)
                    #(request-respond-callback %
                                              :request request
                                              :request-timeout request-timeout
                                              :query-opts query-opts
                                              :time-before time-before
                                              :output-ch output-ch)
                    #(request-error-callback %
                                              :request request
                                              :request-timeout request-timeout
                                              :query-opts query-opts
                                              :time-before time-before
                                              :output-ch output-ch)))))

(defn query-and-join [requests output-ch query-opts]
  (go-loop [[ch & others] (map #(make-request % query-opts) requests)
            result []]
    (if ch
      (recur others (conj result (<! ch)))
      (do
        (>! output-ch result)))))

(defn vector-with-nils? [v]
  (and (seq? v)
       (some nil? v)))

(defn failure? [requests]
  (or (nil? requests) (vector-with-nils? requests)))

(defn perform-request [url query-item-data state encoders result-ch query-opts]
  (let [requests (builder/build-requests url query-item-data encoders state)]
    (cond
      (failure? requests) (go (>! result-ch {:status nil :body nil}))
      (sequential? requests) (query-and-join requests result-ch query-opts)
      :else (make-request requests query-opts result-ch))))

(defn do-request-url [mappings query-item-data state encoders result-ch query-opts]
  (let [url (get-service-endpoint mappings (:from query-item-data))]
    (perform-request url query-item-data state encoders result-ch query-opts)))

(defn do-request-data [{[entity & path] :from} state result-ch]
  (go (>! result-ch (-> (query/find-query-item entity (:done state))
                        second
                        (update-in [:body] #(traverse % path))))))

(defn do-request [mappings {:keys [to-do state]} encoders exception-ch {:keys [debugging] :as query-opts}]
  (try+
    (let [[query-item-name query-item-data] to-do
          result-ch (chan 1 (map #(vector query-item-name %)))
          from (:from query-item-data)]
      (cond
        (keyword? from) (do-request-url mappings query-item-data state encoders result-ch query-opts)
        (vector? from) (do-request-data query-item-data state result-ch)
        (string? from) (throw+ {:type :invalid-resource-type}))
      result-ch)
    (catch [:type :invalid-resource] e
      (go (>! exception-ch e)))
    (catch [:type :expansion-error] e
      (go (>! exception-ch e)))
    (catch Object e
      (go (>! exception-ch e)))))
