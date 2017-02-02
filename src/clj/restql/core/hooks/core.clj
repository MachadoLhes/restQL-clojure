(ns restql.core.hooks.core
  (:require [restql.core.log :as log]
            [restql.core.log :refer [warn]]
            [clojure.walk :refer [stringify-keys keywordize-keys]]))

(defn wrap-java-hook [java-hook]
  (fn [data]
    (try
      (let [hook-obj (.newInstance java-hook)]
        (.setData hook-obj (java.util.HashMap. (stringify-keys data)))
        (.execute hook-obj))
      (catch Exception e
        (warn "Error running hook class " (pr-str java-hook) ": " (.getMessage e))
        ""))))

(defn wrap-java-hooks [query-options]
  (if (contains? query-options :java-hooks)
    (let [hooks (into {} (query-options :java-hooks))]
      (reduce-kv (fn [result key value]
                   (assoc result (keyword key) (map wrap-java-hook value))) {} hooks))
    {}))

(defn wrap-clojure-hooks [query-options]
  (if (contains? query-options :clojure-hooks)
    (query-options :clojure-hooks)
    {}))

(defn concat-hooks [query-options]
  (into (wrap-clojure-hooks query-options)
        (wrap-java-hooks query-options)))


(defn execute-hook [query-options hook-name param-map]
  (let [hooks (concat-hooks query-options)]
    (if (contains? hooks hook-name)
      (let [hook-fns (hooks hook-name)]
        (doseq [hook-fn hook-fns]
          (hook-fn param-map))))))
