(ns pdg.async-runner-test
  (:require [clojure.core.async :refer [<!! >! chan go]])
  (:use pdg.async-runner)
  (:use expectations))

;check is-done?
(expect true (is-done? 
               [:cart {:with {:id "123"}}]
               {:done [[:cart {:with {:id "123"}}]]
                :requested []
                :to-do []}))

(expect false (is-done? 
               [:cart {:with {:id "123"}}]
               {:done [[:customer {:with {:id "123"}}]]
                :requested []
                :to-do []}))


;check can-request?
(expect true (can-request? [:cart {:with {:id "123"}}]
                           {:done []}))

(expect true (can-request? [:cart {:with {:id [:checkout :cartId]}}]
                           {:done [[:checkout {:body {:id "321"}}]]}))

(expect false (can-request? [:cart {:with {:id [:checkout :cartId]}}]
                            {:done []}))

;check all that can request
(expect (seq [[:customer {:with {:id [:cart :id]}}] 
              [:article  {:with {:id "123"}}]])
             (all-that-can-request {:done [[:cart {:body []}]]
                                    :requested []
                                    :to-do [[:customer {:with {:id [:cart :id]}}]
                                            [:address  {:with {:customer [:customer :id]}}]
                                            [:article  {:with {:id "123"}}]]}))


