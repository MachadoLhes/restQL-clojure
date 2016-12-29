(ns pdg.async-request
  (:use [slingshot.slingshot :only [throw+]])
  (:require [clojure.core.async :as a :refer [chan go go-loop >! <!]]
            [org.httpkit.client :as http]
            [pdg.async-request-builder :as builder]
            [pdg.query :as query]
            [pdg.log :refer [info warn error]]
            [pdg.extractor :refer [traverse]]
            [slingshot.slingshot :refer [try+]]
            [cheshire.core :as json])
  (:import [java.net URLDecoder]))

(defn get-service-endpoint [mappings entity]
  ( if ( nil? ( mappings entity ) )
    (throw+ (str "Endpoint do recurso nao encontrado" entity ))
    (mappings entity)))

(defn fmap [f m]
  (reduce-kv #(assoc %1 %2 (f %3)) {} m))

(defn decode-url [string]
  (URLDecoder/decode string "utf-8"))

(defn parse-query-params
  "this function takes a request object (with :url and :query-params)
  and transforms query params that are sets into vectors"
  [request]
    (update-in request [:query-params]
      #(fmap (fn [query-param-value]
        (if (or (sequential? query-param-value) (set? query-param-value))
          (->> query-param-value (map decode-url) (into []))
          (decode-url query-param-value))) %)))

(defn convert-response [{:keys [status body headers]} {:keys [debugging time url params timeout]} ]
  (let [parsed (if (string? body) body (slurp body))
        base {:status status
              :headers headers
              :url url
              :timeout timeout
              :params params
              :response-time time}]
    (try
      (assoc base
             :body (json/parse-string parsed true))
      (catch Exception e
        (error {:message (.getMessage e)}
               "error parsing request")
        (assoc base
               :parse-error true
               :body parsed)))))

(defn make-request
  ([request query-opts]
   (let [output-ch (chan)]
     (make-request request query-opts output-ch)
     output-ch))
  ([request query-opts output-ch]
  (let [request (parse-query-params request)
        time-before (System/currentTimeMillis)
        request-timeout (if (nil? (:timeout request)) (:timeout query-opts) (:timeout request))]
    (info {:resource (:resource request)
           :requesttimeout request-timeout
           :url (:url request)
           :queryparams (:query-params request)
           :headers (:headers request)}
           "Preparing request")
    (http/get (:url request) {:headers (:headers request)
                              :query-params (:query-params request)
                              :timeout request-timeout}
      (fn [result]
        (let [log-data {:resource (:resource request)
                        :requesttimeout request-timeout
                        :success true}]
        (if (nil? (:error result))
          (do

            (info (assoc log-data :success true
                                  :statuscode (:status result)
                                  :time (- (System/currentTimeMillis) time-before))
                  "Request successful")
            (go (>! output-ch (convert-response result {:debugging (:debugging query-opts)
                                                        :url (:url request)
                                                        :params (:query-params request)
                                                        :timeout request-timeout
                                                        :time (- (System/currentTimeMillis) time-before)}))))
          (do
            (error (assoc log-data :success false
                                   :time (- (System/currentTimeMillis) time-before)
                                   :errordetail (pr-str (:error result)))
                   "Request failed")
            (go (>! output-ch {:status 408 :body {:message "timeout"}}))))))))))

(defn query-and-join [requests output-ch query-opts]
  (go-loop [[ch & others] (map #(make-request % query-opts) requests)
            result []]
    (if ch
      (recur others (conj result (<! ch)))
      (do
        (>! output-ch result)))))

(defn vector-with-nils? [v]
  (and (vector? v)
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
        (vector?  from) (do-request-data query-item-data state result-ch)
        (string?  from) (throw+ {:type :invalid-resource-type}))
      result-ch)
    (catch [:type :invalid-resource] e
      (go (>! exception-ch e)))
    (catch [:type :expansion-error] e
      (go (>! exception-ch e)))
    (catch Object e
      (go (>! exception-ch e)))))
