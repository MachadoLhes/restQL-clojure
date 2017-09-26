(ns restql.parser.core-test
  (:require [clojure.test :refer :all]
            [restql.parser.core :refer :all]))

(deftest testing-edn-string-production

  (testing "Testing simple query"
    (is (= (read-string (parse-query "from heroes as hero"))
           [:hero {:from :heroes}])))

  (testing "Testing simple query without alias"
    (is (= (read-string (parse-query "from heroes"))
           [:heroes {:from :heroes}])))

  (testing "Testing simple query with a use clause"
    (is (= (parse-query "use cache-control = 900
                                      from heroes as hero")
           "^{:cache-control 900} [:hero {:from :heroes}]")))

  (testing "Testing simple query with ignore errors"
    (is (= (parse-query "from heroes as hero ignore-errors")
           "[:hero ^{:ignore-errors \"ignore\"} {:from :heroes}]")))

  (testing "Testing multiple query"
    (is (= (read-string (parse-query "from heroes as hero
                                      from monsters as monster"))
           [:hero {:from :heroes}
            :monster {:from :monsters}])))

  (testing "Testing query with one numeric parameter"
    (is (= (read-string (parse-query "from heroes as hero with id = 123"))
           [:hero {:from :heroes :with {:id 123}}])))

  (testing "Testing query with one string parameter"
    (is (= (read-string (parse-query "from heroes as hero with id = \"123\""))
           [:hero {:from :heroes :with {:id "123"}}])))

  (testing "Testing query with variable parameter"
    (is (= (read-string (parse-query "from heroes as hero with id = $id" :context {"id" "123"}))
           [:hero {:from :heroes :with {:id "123"}}])))

  (testing "Testing query with one null parameter"
    (is (= (read-string (parse-query "from heroes as hero with id = 123, spell = null"))
           [:hero {:from :heroes :with {:id 123 :spell nil}}])))

  (testing "Testing query with one boolean parameter"
    (is (= (read-string (parse-query "from heroes as hero with magician = true"))
           [:hero {:from :heroes :with {:magician true}}])))

  (testing "Testing query with one array parameter"
    (is (= (read-string (parse-query "from heroes as hero with class = [\"warrior\", \"magician\"]"))
           [:hero {:from :heroes :with {:class ["warrior" "magician"]}}])))

  (testing "Testing query with one complex parameter"
    (is (= (read-string (parse-query "from heroes as hero with equip = {sword: 1, shield: 2}"))
           [:hero {:from :heroes :with {:equip {:sword 1 :shield 2}}}])))

  (testing "Testing query with one complex parameter with subitems"
    (is (= (read-string (parse-query "from heroes as hero with equip = {sword: {foo: \"bar\"}, shield: [1, 2, 3]}"))
           [:hero {:from :heroes :with {:equip {:sword {:foo "bar"} :shield [1 2 3]}}}])))

  (testing "Testing query with one chained parameter"
    (is (= (read-string (parse-query "from heroes as hero with id = player.id"))
           [:hero {:from :heroes :with {:id [:player :id]}}])))

  
  (testing "Testing query with one chained parameter and metadata"
    (is (= (parse-query "from heroes as hero with id = player.id -> json")
           "[:hero {:from :heroes :with {:id ^{:encoder :json} [:player :id]}}]")))

  (testing "Testing query with one chained parameter and metadata"
    (is (= (pr-str (read-string (parse-query "from heroes as hero with id = player.id -> encoder(\"json\", \"pretty\")")))
             (pr-str [:hero {:from :heroes :with {:id ^{:encoder :json :args ["pretty"]} [:player :id]}}]))))

  (testing "Testing query with headers"
    (is (= (read-string (parse-query "from heroes as hero headers Content-Type = \"application/json\" with id = 123"))
           [:hero {:from :heroes :with-headers {"Content-Type" "application/json"} :with {:id 123}}])))

  (testing "Testing query with headers and parameters"
    (is (= (read-string (parse-query "from heroes as hero headers Authorization = $auth with id = 123" :context {"auth" "abc123"}))
           [:hero {:from :heroes :with-headers {"Authorization" "abc123"} :with {:id 123}}])))

  (testing "Testing query with hidden selection"
    (is (= (read-string (parse-query "from heroes as hero with id = 1 hidden"))
           [:hero {:from :heroes :with {:id 1} :select :none}])))

  (testing "Testing query with only selection"
    (is (= (read-string (parse-query "from heroes as hero with id = 1 only id, name"))
           [:hero {:from :heroes :with {:id 1} :select #{:id :name}}])))

  (testing "Testing query with only selection of inner elements"
    (is (= (read-string (parse-query "from heroes as hero with id = 1 only skills.id, skills.name, name"))
           [:hero {:from :heroes :with {:id 1} :select #{:name [:skills #{:id :name}]}}])))

  (testing "Testing query with paramater with dot and chaining"
    (is (= (read-string (parse-query "from heroes as hero with weapon.id = weapon.id"))
           [:hero {:from :heroes :with {:weapon.id [:weapon :id]}}])))

  (testing "Testing query with only selection and a filter"
    (is (= (read-string (parse-query "from heroes as hero with id = 1 only id, name -> matches(\"foo\")"))
           [:hero {:from :heroes :with {:id 1} :select #{:id [:name {:matches "foo"}]}}])))

  (testing "Testing query with only selection and a filter with wildcard"
    (is (= (read-string (parse-query "from heroes as hero with id = 1 only id -> equals(1), *"))
           [:hero {:from :heroes :with {:id 1} :select #{[:id {:equals 1}] :* }}])))

  (testing "Testing full featured query"
    (binding [*print-meta* true]
      (is (= (pr-str (read-string (parse-query  "from product as products
                                                 headers
                                                     content-type = \"application/json\"
                                                 with
                                                     limit = product.id -> flatten -> json
                                                     fields = [\"rating\", \"tags\", \"images\", \"groups\"]
                                                 only 
                                                     id, name, cep, phone")))
             (pr-str [:products {:from :product
                                 :with-headers {"content-type" "application/json"}
                                 :with {:limit ^{:expand false :encoder :json}
                                               [:product :id]
                                       :fields ["rating" "tags" "images" "groups"]}
                                 :select #{:id :name :cep :phone}}]))))))

