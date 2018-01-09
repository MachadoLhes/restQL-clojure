(ns restql.core.api.response-builder
    (:require [ring.util.codec :refer [form-encode]])
)

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
        (assoc response :url (mount-url (:url query-response) (merge (:params query-response) (:forward-params query-opts)))
                        :timeout (:timeout query-response)
                        :response-time (:response-time query-response)
                        :params (merge (:params query-response) (:forward-params query-opts)))
        response))

(defn get-single-details [query-opts query-response]
    (-> {}
        (assoc :success (is-success query-response)
               :status (:status query-response)
               :headers (:headers query-response))
        (append-metadata query-response)
        (append-debug-data query-opts query-response)))

(defn- get-results [query-responses]
    (reduce-kv (fn [result resource query-response]
                   (if (sequential? query-response)
                       (assoc result resource (map :body query-response) )
                       (assoc result resource (:body query-response) ))) {} query-responses
    )
)

(defn- get-details [query-responses query-opts]
    (reduce-kv (fn [detail resource query-response]
                   (if (sequential? query-response)
                       (assoc detail resource (map #(get-details query-opts %) query-response) )
                       (assoc detail resource (get-single-details query-opts query-response) ))) {} query-responses
    )
)

(defn build [query-responses query-opts]
        { :result (get-results query-responses)
          :details (get-details query-responses query-opts) }
)