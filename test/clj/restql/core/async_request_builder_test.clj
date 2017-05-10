(ns restql.core.async-request-builder-test
  (:require [clojure.test :refer [deftest is]])
  (:use restql.core.async-request-builder))

(def state {:done [[:cart {:headers {:Location 1}
                           :body {:id 1
                                  :lines [{:productId "123" :sku "111"}
                                          {:productId "456" :sku "222"}]
                                  :headers {:Location "Location Field"}}}]

                   [:blobs {:body [{:id "999"}
                                   {:id "888"}]}]
                   [:blebs [{:body {:id "aaa"}}
                            {:body {:id "bbb"}}]]]})

(deftest build-requests-test
  (is (=
    [{:url "http://example/123"
      :query-params {:sku "111"}
      :resource :example
      :metadata nil
      :timeout nil
      :headers nil}
      {:url "http://example/456"
      :resource :example
      :metadata nil
      :query-params {:sku "222"}
      :timeout nil
      :headers nil}]
    
    (build-requests "http://example/:id"
                    {:from :example
                      :with {:id [:cart :lines :productId]
                            :sku [:cart :lines :sku]}}
                    {}
                    state))))

(deftest search-multiple-entities-test
  (is (=
    #{[{:productId "123" :sku "111"}
       {:productId "456" :sku "222"}]}

    (get-multiple-entities {:from :blibs
                            :with {:product [:cart :lines :productId]
                                   :sku     [:cart :lines :sku]}} state))))

(deftest multiple-query-expansion-test
  (is (=
    "id1"
    (interpolate-template-item {:id "id1" :sku "sku1"}

                               #{{:body [{:id "id1" :sku "sku1"}
                                         {:id "id2" :sku "sku2"}]
                                  :path [:id]
                                  :fullpath [:cart :lines :id]}}

                               [:cart :lines :id]))))

(deftest multiple-query-expansion-with-complex-parameters-test
  (is (=
    {:data "id1"
     :blabs [:cart :id]}

    (interpolate-template-item {:id "id1" :sku "sku1"}

                               #{{:body [{:id "id1" :sku "sku1"}
                                         {:id "id2" :sku "sku2"}]
                                  :path [:id]
                                  :fullpath [:cart :lines :id]}}

                               {:data [:cart :lines :id]
                                :blabs [:cart :id]}))))

(deftest multiple-query-expansion-with-vectors-test
  (is (=
    ["bla" "id1"  [:cart :id]]

    (interpolate-template-item {:id "id1" :sku "sku1"}

                               #{{:body [{:id "id1" :sku "sku1"}
                                         {:id "id2" :sku "sku2"}]
                                  :path [:id]
                                  :fullpath [:cart :lines :id]}}

                               ["bla" [:cart :lines :id] [:cart :id]] false))))

(deftest create-request-with-headers-test
  (is (=
    {:url "http://localhost:9999"
     :query-params {:id "123"}
     :resource :cart
     :metadata nil
     :timeout nil
     :headers {"tid" "aaaaaaaaaa"}}
    
    (build-request "http://localhost:9999" 
                   {:from :cart 
                    :with {:id "123"} 
                    :with-headers {"tid" "aaaaaaaaaa"}} 
                   {}
                   {:done []}))))

(deftest create-request-without-headers-test
  (is (=
    {:url "http://localhost:9999"
     :query-params {:id "123"}
     :metadata nil
     :resource :cart
     :timeout nil
     :headers nil}
    
    (build-request "http://localhost:9999" 
                   {:from :cart 
                    :with {:id "123"}}
                   {}
                   {:done []}))))

(deftest retrieving-value-from-state-test
  (is (=
    1
    (get-reference-from-state [:cart :id] state)))

  (is (=
    1
    (get-reference-from-state [:cart :headers "Location"] state)))

  (is (=
    "Location Field"
    (get-reference-from-state [:cart :headers :Location] state))))

(deftest no-multiple-requests-test
  (is (=
    #{}
    (get-multiple-requests {:with {:id [:cart :id]}} state)))

  (is (=
    #{}
    (get-multiple-paths {:with {:id [:cart :id]}} state))))


(deftest simple-multiple-request-test
  (is (=
    #{[:blobs :id]}
    (get-multiple-paths {:with {:id [:blobs :id]}} state)))

  (is (=
    #{:blobs}
    (get-multiple-requests {:with {:id [:blobs :id]}} state))))

(deftest derived-multiple-request-test
  (is (=
    #{[:blebs :id]}
    (get-multiple-paths {:with {:id [:blebs :id]}} state)))

  (is (=
    #{:blebs}
    (get-multiple-requests {:with {:id [:blebs :id]}} state))))

(deftest nested-multiple-request-test
  (is (=
    #{[:cart :lines :productId]}
    (get-multiple-paths {:with {:id [:cart :lines :productId]}} state)))

  (is (=
    #{:cart}
    (get-multiple-requests {:with {:id [:cart :lines :productId]}} state))))

(deftest nested-complex-multiple-request-test
  (is (=
    #{:cart}
    (get-multiple-requests {:with {:id {:field [:cart :lines :productId]}}} 
                           state))))

(deftest search-of-multiple-paths-test
  (is (=
    #{[:cart :lines :productId]}
    (get-multiple-paths {:with {:id {:field [:cart :lines :productId]}}}
                        state))))

(deftest create-request-with-custom-timeout-test
  (is (=
    {:url "http://localhost:9999"
     :query-params {:id "123"}
     :resource :cart
     :metadata nil
     :timeout 2000
     :headers nil}
    
    (build-request "http://localhost:9999" 
                   {:from :cart :timeout 2000 :with {:id "123"}}
                   {}
                   {:done []}))))

