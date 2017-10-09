(ns restql.core.api.core
  (:require [restql.core.api.restql :as restql]
            [cheshire.core :as json]))

(defn concat-encoders [encoders]
  (if (nil? encoders)
    (restql/get-default-encoders)
    (into (restql/get-default-encoders) encoders)))

(defn query [& {:keys [mappings encoders query query-opts callback]}]
  (let [output (promise)]
    (restql/execute-parsed-query-async :mappings mappings
                                       :encoders (concat-encoders encoders)
                                       :query query
                                       :options query-opts
                                       :callback (fn [result error]
                                                   (deliver output (or result error))
                                                   (when-not (nil? callback)
                                                     (callback result error))))
    output))

