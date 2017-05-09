(ns pdg.url-matcher-test
  (:use pdg.url-matcher)
  (:use expectations))

;;let's define some testing urls first.
(def with-id      "http://cart/:id")
(def with-two-ids "http://customer/:customer-id/address/:address-id")
(def with-composed-ids "http://customer/:customer-id/credit-card/:credit-card-id")

;;Now, to begin with, we must be able to extract the parameters name
;;out of a url pattern string.
(expect #{:id} (extract-parameters with-id))
(expect #{:customer-id :address-id} (extract-parameters with-two-ids))

;;then, we need a way to interpolate these parameters with real
;;values, received from a map
(expect "http://cart/123" 
        (interpolate with-id {:id "123"}))
(expect "http://customer/900/address/800" 
        (interpolate with-two-ids {:customer-id "900" :address-id "800"}))

;;finally, the interpolated parameters must be removed from the parameters
;;map, so they will not pop up in the query strings
(expect {:name "restql_core"}
        (dissoc-params with-id {:id "123" :name "restql_core"}))
(expect {:name "restql_core"}
        (dissoc-params with-two-ids {:customer-id "900" :address-id "800" :name "restql_core"}))
