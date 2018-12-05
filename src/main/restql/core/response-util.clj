(ns restql.server.response-util)
(require '[clojure.string :as str])

(defn get-response-headers [query-response]
    (->> query-response
                        (vals)
                        (map :details)
                        (flatten)                        
                        (map :headers)))

(defn get-cache-control [headers]
    (map #(get % "cache-control") headers))

(defn parse-cache-control [cache-control]
    (->> (str/split cache-control #", ")
        (map #(str/split % #"="))))

(defn filter-max-ages-list [max-ages-list]
    (if (some #(= "max-age" %) max-ages-list)
        max-ages-list))

(defn get-max-age-value [max-age]
    (let [max-age-val (-> max-age
                        (get 1))]
        (. Integer parseInt max-age-val)))

(defn calculate-max-age [query-response]
    (let [max-ages (->> query-response
                        (get-response-headers)
                        (get-cache-control)
                        (map parse-cache-control)
                        (map #(map filter-max-ages-list %))
                        (map #(remove nil? %))
                        (map #(map get-max-age-value %))
                        (map first))]
    (apply min max-ages)))