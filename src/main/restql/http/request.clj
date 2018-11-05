(ns restql.http.request
  (:require [restql.http.client :as http-client]
            [http.async.client :as client]
            [http.async.client.request :as http]))

(defn teste [a]
  (let [closed (promise)
        client (http-client/get-client)
        resp (http/execute-request client (http/prepare-request :get "http://google.com/")
                                   :completed (fn [response]
                                              @(:body response)))
        _ (future (Thread/sleep 300) (client/close client) (deliver closed true))]
    resp))

(defn execute-request [{:keys [body
                              client
                              headers
                              method
                              query
                              timeout
                              url]}
                      success-callback
                      error-callback]
  "Make a http request
  Options:
    :body
    :client
    :headers
    :method 
    :query
    :timeout
    :url"
  (->> (http/prepare-request method url 
                            :body body
                            :headers headers
                            :query query
                            :timeout timeout
                            :completed success-callback
                            :error error-callback)
        (http/execute-request ((nil? client) (http-client/get-client {}) client))))