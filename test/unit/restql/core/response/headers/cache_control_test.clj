(ns restql.core.response.headers.cache-control-test
  (:require [clojure.test :refer [deftest is]])
  (:use restql.core.response.headers.cache-control))

(defn get-sample-result []
  {:jedis {:details {:headers {:x-type "Jedi"
                               :x-weapon "Light Saber"
                               :cache-control "max-age=500, s-maxage=1200"}}
           :result {:id 1
                    :name "Luke Skywalker"
                    :weaponId 2}}})

(defn get-sample-minimal-result []
  {:jedis {:details {}}
           :result {:id 1
                    :name "Luke Skywalker"
                    :weaponId 2}})

(defn get-sample-query []
  ^{:cache-control 900} [:from "jedis"])

(defn get-sample-minimal-query []
  [:from "jedis"])

(defn get-max-age-meta-query []
  ^{:cache-control 900, :max-age 400, :s-maxage 1800} [:from "jedis"])

(defn get-no-meta-query []
  [:from "jedis"])

(deftest filter-cache-control-headers-test
  (is (= {:cache-control "max-age=900"}
         (filter-cache-control-headers {:x-type-jedis "Jedi" :cache-control "max-age=900"}))))

(deftest get-cache-control-values-test
  (is (= ["max-age=900, s-maxage=1200" "max-age=400"]
         (get-cache-control-values {:jedis   {:x-type-jedis "Jedi" :cache-control "max-age=900, s-maxage=1200"}
                                    :planets {:x-type-planets "Kamino" :cache-control "max-age=400"}}))))

(deftest split-cache-control-values-test
  (is (= [["max-age" "900"] ["s-maxage" "1200"]]
         (split-cache-control-values ["max-age=900" "s-maxage=1200"]))))

(deftest cache-control-values-to-map-test
  (is (= {:max-age "900" :s-maxage "1200"}
      (cache-control-values-to-map [["max-age" "900"] ["s-maxage" "1200"]]))))
  (is (= {:no-cache true}
      (cache-control-values-to-map [["no-cache"]])))

(deftest parse-cache-control-values-test
  (is (= [{:max-age "900" :s-maxage "1200"} {:max-age "400"}]
         (parse-cache-control-values ["max-age=900, s-maxage=1200" "max-age=400"]))))

(deftest get-cache-control-headers-test
  (is (= [{:max-age "900", :s-maxage "1200"} {:max-age "400"}]
          (get-cache-control-headers {:jedis   {:x-type-jedis "Jedi" :cache-control "max-age=900, s-maxage=1200"}
                                                :planets {:x-type-planets "Kamino" :cache-control "max-age=400"}})))
  (is (= [{:no-cache true} {:max-age "400"}]
    (get-cache-control-headers {:jedis   {:x-type-jedis "Jedi" :cache-control "no-cache"}
                                          :planets {:x-type-planets "Kamino" :cache-control "max-age=400"}}))))

(deftest get-minimal-cache-type-value-test
  (is (= {:max-age "400"}
    (get-minimal-cache-type-value [{:max-age "900", :s-maxage "1200"} {:max-age "400"}] :max-age)))
  (is (= {:s-maxage "1200"}
    (get-minimal-cache-type-value [{:max-age "900", :s-maxage "1200"} {:max-age "400"}] :s-maxage))))

(deftest get-query-cache-control-test
  (is (= {:max-age 900}
    (get-query-cache-control (get-sample-query)))))

(deftest has-key-in-list-of-maps-test
  (is (= true
    (has-key-in-list-of-maps :max-age [{:max-age "900", :s-maxage "1200"} {:max-age "400"}]))))

(deftest get-minimal-response-cache-control-values-test
  (is (= {:max-age "400", :s-maxage "1200"}
    (get-minimal-response-cache-control-values [{:max-age "900", :s-maxage "1200"} {:max-age "400"}]))))

(deftest check-query-for-cache-control-test
  (is (= true
    (check-query-for-cache-control (get-sample-query)))))

(deftest check-headers-for-no-cache-test
  (is (= true
    (check-headers-for-no-cache [{:max-age "900", :s-maxage "1200"} {:no-cache true}]))))

(deftest generate-cache-string-test
  (is (= "max-age=900, s-maxage=1200"
    (generate-cache-string {:max-age "900", :s-maxage "1200"}))))

(deftest get-cache-header-test
  (is (= {:cache-control "max-age=400, s-maxage=1800"}
          (get-cache-header (get-max-age-meta-query) {:jedis   {:x-type-jedis "Jedi" :cache-control "max-age=600, s-maxage=1200"}
                                                 :planets {:x-type-planets "Kamino" :cache-control "max-age=400"}})))
  (is (= {:cache-control "no-cache"}
          (get-cache-header (get-no-meta-query) {:jedis   {:x-type-jedis "Jedi" :cache-control "max-age=900, s-maxage=1200"}
                                                 :planets {:x-type-planets "Kamino" :cache-control "no-cache"}})))
  (is (= {:cache-control "max-age=400, s-maxage=1200"}
          (get-cache-header (get-no-meta-query) {:jedis   {:x-type-jedis "Jedi" :cache-control "max-age=600, s-maxage=1200"}
                                                 :planets {:x-type-planets "Kamino" :cache-control "max-age=400"}}))))
