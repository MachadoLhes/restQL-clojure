(ns restql.http.apache-client
  (:import org.apache.http.impl.nio.client.HttpAsyncClients
           org.apache.http.client.methods.RequestBuilder
           org.apache.http.concurrent.FutureCallback
           org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager
           org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor)
)

(def client (atom nil))

(def convert-method
  {:get     "GET"
   :delete  "DELETE"
   :head    "HEAD"
   :options "OPTIONS"
   :patch   "PATCH"
   :post    "POST"
   :put     "PUT"})

(defn get-connection-manager []
  (let [manager (PoolingNHttpClientConnectionManager. (DefaultConnectingIOReactor.))]
    (.. manager (setDefaultMaxPerRoute 100))
    (.. manager (setMaxTotal 5000))
    manager
  )
)

(defn get-client []
   (if (nil? @client)
      (let [created-client (HttpAsyncClients/createMinimal (get-connection-manager))]
        (.start created-client)
        (reset! client created-client)
      )
   )
   @client
)


(defn create-parent-callback [success-callback error-callback cancel-callback]
  (reify FutureCallback
    (failed [this exception]
      (error-callback exception)
    )
    (completed [this resp]
      (success-callback {
          :status (.. resp getStatusLine getStatusCode)
          :body (slurp (.. resp getEntity getContent))
        }
      )
    )
    (cancelled [this]
      (cancel-callback)
    )
  )
)

(defn create-request [{:keys [body
                              content-type
                              headers
                              method
                              query-params
                              url]}]
  (let [request-method (convert-method method)
        request (RequestBuilder/create request-method)]
    (.. request (setUri url))
    (.. request (build))
  )
)

(defn request [request-opts success-callback error-callback cancel-callback]
  (let [parent-callback (create-parent-callback success-callback error-callback cancel-callback)
        http-request (create-request request-opts)]
    (.. (get-client) (execute http-request parent-callback))
  )
)