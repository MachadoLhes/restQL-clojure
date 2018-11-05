(ns restql.main
  (:require [ring.adapter.jetty :as ring]
            [restql.http.request :as http]))

(defn handler [request respond rise]
  (respond {:status 200
            :headers {"Content-Type" "text/html"}
            :body "foi"}))
  
(ring/run-jetty handler {:port 3000 :async? true})