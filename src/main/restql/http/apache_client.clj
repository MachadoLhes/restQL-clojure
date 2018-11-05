(ns restql.http.apache-client
   (:import org.apache.http.impl.nio.client.HttpAsyncClients
             org.apache.http.client.methods.HttpGet
             org.apache.http.concurrent.FutureCallback
             org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager
            org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor)

)

(def client (atom nil))

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
         (success-callback (slurp (.. resp getEntity getContent)))
      )
      (cancelled [this]
         (cancel-callback)
      )
   )
)

(defn get-connection-manager []
   (let [manager (PoolingNHttpClientConnectionManager. (DefaultConnectingIOReactor.))]
      (.. manager (setDefaultMaxPerRoute 100))
      (.. manager (setMaxTotal 5000))
      manager
   )
)


(defn make-request [endpoint success-callback error-callback cancel-callback]
   (let [parent-callback (create-parent-callback success-callback nil nil)
         http-request (HttpGet. endpoint)]
      (.. (get-client) (execute http-request parent-callback))
   )
)