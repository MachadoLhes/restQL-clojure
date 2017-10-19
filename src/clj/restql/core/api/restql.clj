(ns restql.core.api.restql
  (:require [restql.core.async-runner :as restql]
            [restql.core.validator.core :as validator]
            [restql.core.transformations.select :refer [select]]
            [restql.core.async-request :as request]
            [restql.core.hooks.core :as hook]
            [clojure.walk :refer [stringify-keys]]
            [cheshire.core :as json]
            [restql.core.context :as context]
            [ring.util.codec :refer [form-encode]]
            [clojure.core.async :refer [go go-loop <!! <! >! alt! alts! alt!! timeout]]
            [restql.parser.core :as parser]
            [clojure.tools.reader :as edn]
            [slingshot.slingshot :refer [throw+ try+]]))

(defn- status-code-ok [query-response]
  (and
    (not (nil? (:status query-response)))
    (< (:status query-response) 300)))

(defn- is-success [query-response]
  (and
    (status-code-ok query-response)
    (nil? (:parse-error query-response))))

(defn- mount-url [url params]
  (str url "?" (if (nil? params) "" (form-encode params))))

(defn stringify-values [a-map]
  (reduce-kv (fn [m k v] (assoc m k (str v))) {} a-map))

(defn- append-metadata [response query-response]
  (let [metadata (:metadata query-response)]
    (if (nil? metadata)
      (assoc response :metadata {})
      (assoc response :metadata (stringify-values metadata)))))

(defn append-debug-data [response query-opts query-response]
  (if (:debugging query-opts)
    (assoc response :url (mount-url (:url query-response) (:params query-response))
                    :timeout (:timeout query-response)
                    :response-time (:response-time query-response))
    response))

(defn build-details [query-opts query-response]
  (-> {}
      (assoc :success (is-success query-response)
             :status  (:status query-response)
             :headers (:headers query-response)
             :params (:params query-response))
      (append-metadata query-response)
      (append-debug-data query-opts query-response)))

(defn- prepare-response [query-opts query-response]
  {:details (build-details query-opts query-response)
   :result (:body query-response)})

(defn- make-map [{done :done} query-opts]
  (let [results (reduce (fn [res [key value]]
                          (assoc res key value)) {} done)]
    (reduce-kv (fn [result k v]
                 (if (sequential? v)
                   (assoc result k (map (partial prepare-response query-opts) v))
                   (assoc result k (prepare-response query-opts v)))) {} results)))

(defn- wait-until-finished [output-ch query-opts]
  (go-loop [state (<! output-ch)]
           (if (restql/all-done? state)
             (make-map state query-opts)
             (recur (<! output-ch)))))

(defn- parse-query [context string]
  (->> string
      edn/read-string
      (validator/validate context)
      (partition 2)))

(defn- check-status [init resource result]
  (let [details (:details result)
        success (:success details)
        status (:status details)
        ignore-errors (= "ignore" (get-in details [:metadata :ignore-errors]))]
    (cond
      ignore-errors (assoc init resource result)
      (= 408 status) (assoc init resource result)
      (false? success) (throw+ {:type :resource-failed :resource resource :response result})
      :else (assoc init resource result))))

(defn- check-all-status [result]
    (reduce-kv check-status {} result))

(defn- extract-result [parsed-query timeout-ch exception-ch query-ch]
  (go
    (alt!
      timeout-ch ([] {:error :timeout})
      exception-ch ([err] err)
      query-ch ([result]
                 (let [output (->> result
                                   (select (flatten parsed-query)))]
                   output)))))

(defn get-default-encoders []
  (context/get-encoders))

(defn- set-default-query-options [query-options]
       (into {:timeout 1000
              :global-timeout 5000} query-options))

(defn execute-query-channel [& {:keys [mappings encoders query query-opts]}]
  (let [; Before query hook
        _ (hook/execute-hook query-opts :before-query {:query         query
                                                       :query-options query-opts})
        time-before (System/currentTimeMillis)

        ; Executing query
        do-request (partial request/do-request mappings)
        query-opts (set-default-query-options query-opts)
        parsed-query (parse-query {:mappings mappings :encoders encoders} query)
        [output-ch exception-ch] (restql/run do-request parsed-query encoders query-opts)
        result-ch (wait-until-finished output-ch query-opts)
        parsed-ch (extract-result parsed-query (timeout (:global-timeout query-opts)) exception-ch result-ch)
        return-ch (go
                    (try+
                      (let [query-result (check-all-status (<! parsed-ch))
                            ; After query hook
                            _ (hook/execute-hook query-opts :after-query {:query-options query-opts
                                                                          :query         query
                                                                          :result        query-result
                                                                          :response-time (- (System/currentTimeMillis) time-before)})]
                        query-result)
                      (catch [:type :resource-failed] e
                        (>! exception-ch e))))]
    [return-ch exception-ch]))

(defn execute-parsed-query [& {:keys [mappings encoders query query-opts]}]
  (let [[result-ch exception-ch] (execute-query-channel :mappings mappings
                                                        :encoders encoders
                                                        :query query
                                                        :query-opts query-opts)]
    (alt!!
      exception-ch ([err] (throw+ err))
      result-ch ([result] result))))

(defn execute-parsed-query-async [& {:keys [mappings encoders query query-opts callback]}]
  (go
    (try+
      (let [[result-ch exception-ch] (execute-query-channel :mappings mappings
                                                            :encoders encoders
                                                            :query query
                                                            :query-opts query-opts)]
      (alt!
        exception-ch ([err] (callback nil err))
        result-ch ([result] (callback result nil))))
      (catch Object e
        (callback nil e)))))

(defn execute-query [& {:keys [mappings encoders query params options]}]
  (let [parsed-query (parser/parse-query query :context (stringify-keys params))]
    (execute-parsed-query :mappings mappings
                          :encoders encoders
                          :query parsed-query
                          :query-opts options)))

(defn execute-query-async [& {:keys [mappings encoders query params options callback]}]
  (let [parsed-query (parser/parse-query query :context (stringify-keys params))]
  (execute-parsed-query-async :mappings mappings
                              :encoders encoders
                              :query parsed-query
                              :query-opts options
                              :callback callback)))