(ns restql.core.api.RestQLJavaApi
  (:gen-class
    :name restql.core.interop.ClojureRestQLApi
    :methods [^{:static true} [query [java.util.Map
                                      java.util.Map
                                      String
                                      java.util.Map] String]
              ^{:static true} [queryAsync [java.util.Map
                                           java.util.Map
                                           String
                                           java.util.Map
                                           java.util.function.Consumer] Void]
              ^{:static true} [queryFromLanguage [java.util.Map
                                                  java.util.Map
                                                  String
                                                  java.util.Map] String]
              ^{:static true} [queryAsyncFromLanguage [java.util.Map
                                                       java.util.Map
                                                       String
                                                       java.util.Map
                                                       java.util.function.Consumer] Void]])
  (:require [clojure.walk :refer [keywordize-keys stringify-keys]]
            [restql.core.log :refer [warn]]
            [restql.core.api.restql-facade :as restql]))

(defn wrap-java-encoder [java-encoder]
  (fn [data]
    (try
      (let [encoder-obj (.newInstance java-encoder)]
        (.setData encoder-obj (java.util.HashMap. (stringify-keys data)))
        (.encode encoder-obj))
      (catch Exception e
        (warn "Error in encoding class " (.getName java-encoder) ": " (.getMessage e))
        ""))))


(defn wrap-java-encoders [java-encoders-map]
  (reduce-kv (fn [result key value]
               (assoc result key (wrap-java-encoder value))) {} java-encoders-map))

(defn concat-encoders [java-encoders]
  (let [java-encoders-map (keywordize-keys (into {} java-encoders))
        default-encoders (restql/get-default-encoders)]
    (into default-encoders (wrap-java-encoders java-encoders-map))))

(defn -query [mappings encoders query query-opts]

  (let [clj-mappings (keywordize-keys (into {} mappings))
        clj-encoders (concat-encoders encoders)
        clj-query-opts (keywordize-keys (into {} query-opts))]

    (restql/execute-query-sync
      :mappings clj-mappings
      :encoders clj-encoders
      :query query
      :query-opts clj-query-opts)))

(defn -queryAsync [mappings encoders query query-opts callback]

  (let [clj-mappings (keywordize-keys (into {} mappings))
        clj-encoders (concat-encoders encoders)
        clj-query-opts (keywordize-keys (into {} query-opts))
        restql-ch (restql/execute-query-async
                    :mappings clj-mappings
                    :encoders clj-encoders
                    :query query
                    :query-opts clj-query-opts
                    :callback (fn [result]
                                (.accept callback result)))]

    nil))

(defn -queryFromLanguage [mappings encoders query query-opts]
  (let [clj-mappings (keywordize-keys (into {} mappings))
        clj-encoders (concat-encoders encoders)
        clj-query-opts (keywordize-keys (into {} query-opts))]

    (restql/execute-query-from-language-sync
      :mappings clj-mappings
      :encoders clj-encoders
      :query query
      :query-opts clj-query-opts)))

(defn -queryAsyncFromLanguage [mappings encoders query query-opts callback]
  (let [clj-mappings (keywordize-keys (into {} mappings))
        clj-encoders (concat-encoders encoders)
        clj-query-opts (keywordize-keys (into {} query-opts))
        restql-ch (restql/execute-query-from-language-async
                    :mappings clj-mappings
                    :encoders clj-encoders
                    :query query
                    :query-opts clj-query-opts
                    :callback (fn [result]
                                (.accept callback result)))]

    nil))