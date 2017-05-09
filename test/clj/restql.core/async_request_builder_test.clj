(ns pdg.async-request-builder-test
  (:use pdg.async-request-builder)
  (:use expectations))

(def state {:done [[:cart {:headers {:Location 1}
                           :body {:id 1
                                  :lines [{:productId "123" :sku "111"}
                                          {:productId "456" :sku "222"}]
                                  :headers {:Location "Location Field"}}}]

                   [:blobs {:body [{:id "999"}
                                   {:id "888"}]}]
                   [:blebs [{:body {:id "aaa"}}
                            {:body {:id "bbb"}}]]]})

(expect [{:url "http://example/123"
          :query-params {:sku "111"}
          :resource :example
          :timeout 1000
          :headers nil}
         {:url "http://example/456"
          :resource :example
          :query-params {:sku "222"}
          :timeout 1000
          :headers nil}]

        (build-requests "http://example/:id"
                        {:from :example
                         :with {:id [:cart :lines :productId]
                                :sku [:cart :lines :sku]}}
                        {}
                        state))

;testing the search for the multiple entities
(expect #{[{:productId "123" :sku "111"}
           {:productId "456" :sku "222"}]}
         (get-multiple-entities {:from :blibs
                                 :with {:product [:cart :lines :productId]
                                        :sku     [:cart :lines :sku]}} state))

;testing expansion of multiple query
(expect "id1" (interpolate-template-item {:id "id1" :sku "sku1"}

                                         #{{:body [{:id "id1" :sku "sku1"}
                                                   {:id "id2" :sku "sku2"}]
                                            :path [:id]
                                            :fullpath [:cart :lines :id]}}

                                         [:cart :lines :id]))

;testing expansion of multiple query with complex parameters
(expect {:data "id1"
         :blabs [:cart :id]}
        (interpolate-template-item {:id "id1" :sku "sku1"}

                                   #{{:body [{:id "id1" :sku "sku1"}
                                             {:id "id2" :sku "sku2"}]
                                      :path [:id]
                                      :fullpath [:cart :lines :id]}}

                                   {:data [:cart :lines :id]
                                    :blabs [:cart :id]}))

(expect ["bla" "id1"  [:cart :id]]
        (interpolate-template-item {:id "id1" :sku "sku1"}

                                   #{{:body [{:id "id1" :sku "sku1"}
                                             {:id "id2" :sku "sku2"}]
                                      :path [:id]
                                      :fullpath [:cart :lines :id]}}

                                   ["bla" [:cart :lines :id] [:cart :id]] false))

;testing creating request with headers
(expect {:url "http://localhost:9999"
         :query-params {:id "123"}
         :resource :cart
         :timeout 1000
         :headers {"tid" "aaaaaaaaaa"}}
        (build-request "http://localhost:9999" {:from :cart :with {:id "123"} :with-headers {"tid" "aaaaaaaaaa"}} {} {:done []}))

;testing creating request without headers
(expect {:url "http://localhost:9999"
         :query-params {:id "123"}
         :resource :cart
         :timeout 1000
         :headers nil}
        (build-request "http://localhost:9999" {:from :cart :with {:id "123"} } {} {:done []}))

;testing retrieving value from state body
(expect 1 (get-reference-from-state [:cart :id] state))

;testing retrieving value from state header
(expect 1 (get-reference-from-state [:cart :headers "Location"] state))

;testing retrieving value from header body field
(expect "Location Field" (get-reference-from-state [:cart :headers :Location] state))

;testing with no multiple requests
(expect #{} (get-multiple-requests {:with {:id [:cart :id]}} state))
(expect #{} (get-multiple-paths {:with {:id [:cart :id]}} state))

;testing with simple multiple request
(expect #{[:blobs :id]} (get-multiple-paths {:with {:id [:blobs :id]}} state))
(expect #{:blobs} (get-multiple-requests {:with {:id [:blobs :id]}} state))

;testing with derived multiple request
(expect #{[:blebs :id]} (get-multiple-paths {:with {:id [:blebs :id]}} state))
(expect #{:blebs} (get-multiple-requests {:with {:id [:blebs :id]}} state))

;testing with nested multiple request
(expect #{[:cart :lines :productId]} (get-multiple-paths {:with {:id [:cart :lines :productId]}} state))
(expect #{:cart} (get-multiple-requests {:with {:id [:cart :lines :productId]}} state))

;testing with nested complex multiple request
(expect #{:cart}
        (get-multiple-requests {:with {:id {:field [:cart :lines :productId]}}}
                               state))

;testing search of multiple paths
(expect #{[:cart :lines :productId]}
        (get-multiple-paths {:with {:id {:field [:cart :lines :productId]}}}
                            state))

;testing creating request with custom timeout set
(expect {:url "http://localhost:9999"
         :query-params {:id "123"}
         :resource :cart
         :timeout 2000
         :headers nil}
        (build-request "http://localhost:9999" {:from :cart :timeout 2000 :with {:id "123"}} {} {:done []}))
