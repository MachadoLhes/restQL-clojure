(ns restql.core.response.headers
  (:require [clojure.string :as string]
            [clojure.walk :refer [keywordize-keys]]))

(defn map-headers-to-aliases
  "Given a key-value pair, where key is the resource alias
   and value is it's value, extracts only the headers to a
   new map."
  [[k v]]
  (into {} {k (some-> v :details :headers)})
)

(defn map-response-headers-to-aliases
  "Return a map of :resource headers"
  [response]
  (->>
    response
    (map map-headers-to-aliases)
    (into {})
  )
)

(defn has-prefix-on-key?
  "Verify if a given key has the expected prefix"
  [prefix [key _]]
  (some-> key
          (keyword)
          (name)
          (string/starts-with? prefix)
  )
)

(defn suffixed-keyword [alias [k v]]
  (assoc {}
    (keyword (str (name k) "-" (name alias))) v
  )
)

(defn map-suffixes-to-headers [[alias headers]]
  (->>
    (filter #(has-prefix-on-key? "x-" %) headers)
    (map #(suffixed-keyword alias %))
    (into {})
  )
)

(defn get-alias-suffixed-headers
  "Given a key-value pair, where key is the resource alias
  and value is it's value, inserts the key prefix on each key
  of the headers map."
  [headers-with-aliases-key]
  (->>
    headers-with-aliases-key
    (map map-suffixes-to-headers)
    (into {})
  )
)

(defn filter-cache-control-headers [headers]
  (select-keys headers [:cache-control])
)

(defn get-cache-control-values [headers-by-aliases]
  (->>
    (map (fn [[_ headers]] (filter-cache-control-headers headers)) headers-by-aliases)
    (map vals)
    (reduce concat)
  )
)

(defn cache-control-values-to-map [cache-control-values]
  (->>
    cache-control-values
    (map #(string/split % #"="))
    (into {})
    (keywordize-keys)
  )
)

(defn parse-cache-control-values [cache-control-vals]
  (->>
    cache-control-vals
    (map #(string/split % #", "))
    (map cache-control-values-to-map)
  )
)

(defn get-cache-control-headers [headers-by-aliases]
  (->>
    headers-by-aliases
    (get-cache-control-values)
    (parse-cache-control-values)
  )
)

(defn get-cache-headers
  "Adds cache control header to header list"
  [query headers-by-aliases]
  (->
    headers-by-aliases
    (get-cache-control-headers)
  )
)

(defn get-response-headers [query result]
  (let [headers-by-aliases (map-response-headers-to-aliases result)]
    (->
      (get-alias-suffixed-headers headers-by-aliases)
      (merge (get-cache-headers query headers-by-aliases))

      ; (get-alias-siffixed-headers result)
      ; (into (additional-headers interpolated-query))
      ; (into {"Content-Type" "application/json"})
    )
  )
)
