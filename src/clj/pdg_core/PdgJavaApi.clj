(ns pdg-core.PdgJavaApi
  (:gen-class
    :name pdg.interop.ClojurePDGApi
    :methods [ ^{:static true} [query [java.util.Map
                                       java.util.Map
                                       String
                                       java.util.Map] String]
               ^{:static true} [queryAsync [java.util.Map
                                             java.util.Map
                                             String
                                             java.util.Map
                                             java.util.function.Consumer] Void]])
  (:require [clojure.walk :refer [keywordize-keys]]
            [pdg-core.pdg-facade :as pdg])
  )

(defn concat-encoders [java-encoders]
  ;TODO criar interface de encoder
  (pdg/get-default-encoders))

(defn -query [mappings encoders query query-opts]

  (let [clj-mappings (keywordize-keys (into {} mappings))
        clj-encoders (concat-encoders encoders)
        clj-query-opts (keywordize-keys (into {} query-opts))]

    (pdg/execute-query-sync
      :mappings clj-mappings
      :encoders clj-encoders
      :query query
      :query-opts clj-query-opts)))

(defn -queryAsync [mappings encoders query query-opts callback]

  (let [clj-mappings (keywordize-keys (into {} mappings))
        clj-encoders (concat-encoders encoders)
        clj-query-opts (keywordize-keys (into {} query-opts))]

    (pdg/execute-query-async
      :mappings clj-mappings
      :encoders clj-encoders
      :query query
      :query-opts clj-query-opts
      :callback (fn [result]
                  (.accept callback result)))))