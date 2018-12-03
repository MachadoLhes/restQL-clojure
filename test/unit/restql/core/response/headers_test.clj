(ns restql.core.response.headers-test
  (:require [clojure.test :refer [deftest is]])
  (:use restql.core.response.headers))

(defn get-sample-result []
  {:jedis {:details {:headers {:x-type "Jedi"
                               :x-weapon "Light Saber"
                               :cache-control "max-age=500, s-maxage=1200"}}
           :result {:id 1
                    :name "Luke Skywalker"
                    :weaponId 2}}})

(defn get-sample-query []
  ^{:cache-control 900} [:from "jedis"])

(deftest map-headers-to-aliases-test
  (is (=
        [{:jedis {:x-type "Jedi"
                 :x-weapon "Light Saber"}}]
        (map map-headers-to-aliases (get-sample-result)))))

(deftest map-response-headers-to-aliases-test
  (is (=
        [{:jedis {:x-type "Jedi"
                  :x-weapon "Light Saber"}}]
        (map map-headers-to-aliases (get-sample-result)))))

(deftest has-prefix-on-key?-test
  (is (= true
         (has-prefix-on-key? "x-" [:x-type])))
  (is (= false
         (has-prefix-on-key? "x-" [:other-x-type]))))

(deftest suffixed-keyword-test
  (is (= {:x-type-alias "value"}
         (suffixed-keyword "alias" ["x-type" "value"]))))

(deftest map-suffixes-to-headers-test
  (is (= {:x-type-alias "value"}
         (map-suffixes-to-headers :alias {:x-type "value", :not-suffixed-header "value"}))))

(deftest get-alias-suffixed-headers-test
  (is (= {:x-type-alias "value"}
         (get-alias-suffixed-headers {:alias {:x-type "value", :not-suffixed-header "value"}}))))

(deftest filter-cache-control-headers-test
  (is (= {:cache-control "max-age=900"}
         (filter-cache-control-headers {:x-type-jedis "Jedi" :cache-control "max-age=900"}))))

(deftest get-cache-control-values-test
  (is (= ["max-age=900, s-maxage=1200" "max-age=400"]
         (get-cache-control-values {:jedis   {:x-type-jedis "Jedi" :cache-control "max-age=900, s-maxage=1200"}
                                    :planets {:x-type-planets "Kamino" :cache-control "max-age=400"}}))))

(deftest cache-control-values-to-map-test
  (is (= {:max-age "900" :s-maxage "1200"}
         (cache-control-values-to-map ["max-age=900" "s-maxage=1200"]))))

(deftest parse-cache-control-values-test
  (is (= [{:max-age "900" :s-maxage "1200"} {:max-age "400"}]
         (parse-cache-control-values ["max-age=900, s-maxage=1200" "max-age=400"]))))

(deftest get-response-headers-test
  (is (= {:x-type-jedis "Jedi" :x-weapon-jedis "Light Saber" :cache-control "max-age=900"}
         (get-response-headers (get-sample-query) (get-sample-result)))))
