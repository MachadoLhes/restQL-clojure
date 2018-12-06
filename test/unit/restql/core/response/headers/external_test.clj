(ns restql.core.response.headers.external-test
  (:require [clojure.test :refer [deftest is]])
  (:use restql.core.response.headers.external))

(defn get-sample-result []
  {:jedis {:details {:headers {:x-type "Jedi"
                               :x-weapon "Light Saber"
                               :cache-control "max-age=500, s-maxage=1200"}}
           :result {:id 1
                    :name "Luke Skywalker"
                    :weaponId 2}}})

(defn get-sample-minimal-result []
  {:jedis {:details {}
           :result {:id 1
                    :name "Luke Skywalker"
                    :weaponId 2}}})

(defn get-sample-query []
  ^{:cache-control 900} [:from "jedis"])

(defn get-sample-minimal-query []
  [:from "jedis"])

  (defn get-max-age-meta-query []
    ^{:cache-control 900, :max-age 400, :s-maxage 1800} [:from "jedis"])

(defn get-no-meta-query []
  [:from "jedis"])

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
         (map-suffixes-to-headers [:alias {:x-type "value", :not-suffixed-header "value"}]))))

(deftest get-alias-suffixed-headers-test
  (is (= {:x-type-alias "value"}
         (get-alias-suffixed-headers {:alias {:x-type "value", :not-suffixed-header "value"}}))))
