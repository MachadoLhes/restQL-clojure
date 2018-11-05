(ns restql.http.client
  (:require [http.async.client :as client]))

(def client
  (atom nil))

(def default-user-agent
  {:user-agent (or (System/getenv "REQUEST_USER_AGENT") "restql-server")})

(defn create-client [& {:keys [compression-enabled
                               connection-timeout
                               idle-in-pool-timeout
                               max-conns-per-host
                               max-conns-total
                               read-timeout
                               request-timeout
                               user-agent]}]
  "Creates a new Http Client"
  (client/create-client))

(defn get-client [opts]
  (if (nil? @client)
      (reset! client (create-client opts)))
  @client)