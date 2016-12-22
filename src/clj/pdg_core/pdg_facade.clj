(ns pdg-core.pdg-facade
  (:require [clojure.edn :as edn]
            [pdg.async-runner :as pdg]
            [pdg.validator.core :as validator]
            [pdg.transformations.select :refer [select]]
            [pdg.async-request :as request]
            [cheshire.core :as json]
            [pdg.context :as context]
            [ring.util.codec :refer [form-encode]]
            [clojure.core.async :refer [go go-loop <!! <! >! alt! timeout]]))

(defn- status-code-ok [query-response]
  (and
    (not (nil? (:status query-response)))
    (< (:status query-response) 300)))

(defn- is-success [query-response]
  (and
    (status-code-ok query-response)
    (nil? (:parse-error query-response))))

(defn- mount-url [url params]
  (str url "?" (form-encode params)))

(defn- prepare-response [query-opts query-response]
  {:details
           (if (:debugging query-opts)
             {:success (is-success query-response)
              :status  (:status query-response)
              :url (mount-url (:url query-response) (:params query-response))
              :timeout (:timeout query-response)
              :response-time (:response-time query-response)}
             {:success (is-success query-response)
              :status  (:status query-response)})
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
           (if (pdg/all-done? state)
             (make-map state query-opts)
             (recur (<! output-ch)))))

(defn- parse-query [context string]
  (->> string
      edn/read-string
      (validator/validate context)
      (partition 2)))

(defn- extract-result [parsed-query timeout-ch exception-ch query-ch]
  (go
    (alt!
      timeout-ch ([] {:error :timeout})
      exception-ch ([err] err)
      query-ch ([result]
                 (let [output (->> result
                                   (select (flatten parsed-query))
                                   json/generate-string)]
                   output)))))

(defn get-default-encoders []
  (context/get-encoders))

(defn execute-query-channel [& {:keys [mappings encoders query query-opts]}]
  (let [do-request (partial request/do-request mappings)
        parsed-query (parse-query {:mappings mappings} query)
        [output-ch exception-ch] (pdg/run do-request parsed-query encoders query-opts)
        result-ch (wait-until-finished output-ch query-opts)]
    (extract-result parsed-query (timeout 5000) exception-ch result-ch)))

(defn execute-query-sync [& {:keys [mappings encoders query query-opts]}]
  (<!! (execute-query-channel :mappings mappings
                              :encoders encoders
                              :query query
                              :query-opts query-opts)))

(defn execute-query-async [& {:keys [mappings encoders query query-opts callback]}]
  (go
    (let [result (<! (execute-query-channel :mappings mappings
                                             :encoders encoders
                                             :query query
                                             :query-opts query-opts))]
      (callback result))))