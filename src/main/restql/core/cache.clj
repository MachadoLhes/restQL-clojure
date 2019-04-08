(ns restql.core.cache
  (:require [environ.core :refer [env]]
            [clojure.core.memoize :as memo]))

(def DEFAULT_CACHED_COUNT (if (contains? env :cache-count) (read-string (env :cache-count)) 2000))

(defn cached
  "Verifies if a given function is cached, executing and saving on the cache
     if not cached or returning the cached value"
  [function]

  (memo/fifo function {} :fifo/threshold DEFAULT_CACHED_COUNT))

