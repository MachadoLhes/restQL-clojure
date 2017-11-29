(ns restql.core.transformations.aggregation
  (:require [clojure.string :as string]))


(defn- is-aggregation? [resource from]
  (and (not= resource from) (.contains (name resource) ".")))

(defn- find-alias [resource]
  (->
    (name resource)
    (string/split #"\.")
    (first)
    (keyword)))

(defn- find-path [resource]
  (->
    (name resource)
    (string/split #"\.")
    (second)
    (keyword)))

(defn- find-aggregations [query]
  (let [resource (first query)
        query-obj (second query)
        from (:from query-obj)]
    (if (is-aggregation? resource from)
      {:from resource :to (find-alias resource) :path (find-path resource)})))

(defn- build-aggregation [result-from result-to from to path]
  (if (seq? result-from)
    (assoc-in result-to [:result path] (map #(conj (:result %)) result-from))
    (assoc-in result-to
              [:result path] (get-in result-from [:result path]))))

(defn- replace-result [from result]
  (if (seq? (from result))
    (->>
      (map #(dissoc % :result) (from result))
      (assoc result from))
    (update-in result [from] dissoc :result)))

(defn- replace-simple-aggregation [result from-to]
  (let [from (:from from-to)
        to (:to from-to)
        path (:path from-to)]
    (->>
      (build-aggregation (from result) (to result) from to path)
      (assoc result to)
      (replace-result from))))

(defn- replace-list-aggregation [result from-to]
  (let [from (:from from-to)
        to (:to from-to)
        path (:path from-to)
        processed-result (map #(build-aggregation %1 %2 from to path) (from result) (to result))
        aggregated-result (assoc result to processed-result)]
    (->> aggregated-result
         (replace-result from))))

(defn- replace-aggregation [result from-to]
  (cond
    (nil? from-to) result
    (seq? ((:to from-to) result)) (replace-list-aggregation result from-to)
    :else (replace-simple-aggregation result from-to)))

(defn- replace-aggregations [result from-to]
  (reduce replace-aggregation result from-to))

(defn aggregate [query result]
  (->>
    (map #(find-aggregations %) query)
    (replace-aggregations result)))
