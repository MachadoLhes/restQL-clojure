(ns restql.core.hooks.core
  (:require [clojure.tools.logging :as log]
            [clojure.walk :refer [stringify-keys keywordize-keys]]))

(defn wrap-java-hook [java-hook]
  (fn [data]
    (try
      (let [hook-obj (.newInstance java-hook)]
        (.setData hook-obj (java.util.HashMap. (stringify-keys data)))
        (.execute hook-obj))
      (catch Exception e
        (log/warn "Error running hook class " (pr-str java-hook) ": " (.getMessage e))
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

(defn execute-hook-fn [hook-function query-options param-map]
  (try
    (hook-function (assoc param-map :query-options query-options))
     (catch Exception e {}))
)

(defn conj-context [context1 context2]
  (if (map? context2)
    (conj context1 context2)
    context1
  )
)

(defn execute-hook [query-options hook-name param-map]
  (let [hooks (concat-hooks query-options)]
    (if (contains? hooks hook-name)
        (->> (map #(execute-hook-fn % query-options param-map) (hooks hook-name))
             (reduce conj-context {})
             (doall)
        )
    )
  )
)

(comment
  "
  This is the hook map format restQL-core understands, where:

  + hook-type-n => a clojure function
  + class-type-n => a java Class implementing the hook
  "
  hook-format {:clojure-hooks {:before-query [hook-bq-1]
                               :after-query [hook-aq-1 hook-aq-2]
                               :before-request [hook-br-1, hook-br-2]
                               :after-request [hook-ar-1 hook-ar-2 hook-ar-3]}
               :java-hooks {:before-query [class-bq-1]
                            :after-query [class-aq-1 class-aq-2]
                            :before-request [class-br-1, class-br-2]
                            :after-request [class-ar-1]}})
