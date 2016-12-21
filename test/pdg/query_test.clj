(ns pdg.query-test
  (:use pdg.query)
  (:use expectations))

;get/extract dependencies
(expect #{:checkout}
        (get-dependencies [:cart {:from :cart
                                  :with {:opn "123"
                                         :id [:checkout :cartId]}}]))

(expect #{[:checkout :cartId]}
        (get-dependency-paths {:from :cart
                               :with {:opn "123"
                                      :id [:checkout :cartId]}}))

(expect #{}
        (get-dependencies [:cart {:from :cart
                                  :with {:id "123"}}]))

(expect #{}
        (get-dependency-paths {:from :cart
                               :with {:id "123"}}))

(expect #{:cart} (get-dependencies
                   [:lines {:from [:cart :lines]}]))

(expect #{[:cart :lines]}
        (get-dependency-paths {:from [:cart :lines]}))

(expect #{:cart :checkout} (get-dependencies
                   [:ex {:from :example
                         :with {:data {:json {:a [:cart :id]
                                              :b [:checkout :id]
                                              :c 3}}}}]))

(expect #{[:cart :id] [:checkout :id]}
        (get-dependency-paths {:from :example
                               :with {:data {:json {:a [:cart :id]
                                                    :b [:checkout :id]
                                                    :c 3}}}}))
