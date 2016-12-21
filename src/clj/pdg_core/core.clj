(ns pdg-core.core
  (:require [pdg-core.pdg-facade :as pdg]
            [cheshire.core :as json]))

(defn concat-encoders [encoders]
  (if (nil? encoders)
    (pdg/get-default-encoders)
    (into (pdg/get-default-encoders) encoders)))

(defn stringify-query [query]
  (binding [*print-meta* true]
    (pr-str query)))

(defn query [& {:keys [mappings encoders query query-opts callback]}]
  (let [output (promise)]
    (pdg/execute-query-async :mappings mappings
                             :encoders (concat-encoders encoders)
                             :query (stringify-query query)
                             :query-opts query-opts
                             :callback (fn [result]
                                         (let [parsed (json/parse-string result true)]
                                           (deliver output parsed)
                                           (when-not (nil? callback)
                                             (callback result)))))
    output))