(ns restql.core.api.core
  (:require [restql.core.api.restql-facade :as restql]
            [cheshire.core :as json]))

(defn concat-encoders [encoders]
  (if (nil? encoders)
    (restql/get-default-encoders)
    (into (restql/get-default-encoders) encoders)))

(defn stringify-query [query]
  (binding [*print-meta* true]
    (pr-str query)))

(defn query [& {:keys [mappings encoders query query-opts callback]}]
  (let [output (promise)]
    (restql/execute-query-async :mappings mappings
                                :encoders (concat-encoders encoders)
                                :query (stringify-query query)
                                :query-opts query-opts
                                :callback (fn [result]
                                            (let [parsed (json/parse-string result true)]
                                              (deliver output parsed)
                                              (when-not (nil? callback)
                                                (callback result)))))
    output))

(comment

  (require 'restql.core.api.core :reload-all)
  (in-ns 'restql.core.api.core)

  (query :mappings {:cart "http://acom-cart-v3.sa-east-1.elasticbeanstalk.com/cart/:id"}
         :encoders {}
         :query [:c ^{:foo "bar"} {:from :cart :with {:id 123}}]
         :query-opts {}
         :callback (fn [result] (println result)))

  (query :mappings {:cart "http://acom-cart-v3.sa-east-1.elasticbeanstalk.com/cart/:id"}
         :encoders {}
         :query [:c {:from :cart :with {:id 123}}]
         :query-opts {}
         :callback (fn [result] (println result)))


         )