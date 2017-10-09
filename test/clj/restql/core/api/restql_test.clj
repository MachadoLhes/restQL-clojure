(ns restql.core.api.restql-test
  (:require [clojure.test :refer :all]
            [clojure.core.async :refer [<!!]]
            [restql.core.api.restql :as restql]
            [cheshire.core :as json]
            [stub-http.core :refer :all]
            [slingshot.slingshot :refer [try+]])
  (:import (clojure.lang ExceptionInfo)))

(defn hero-route []
  {:status 200 :content-type "application/json" :body (json/generate-string { :hi "I'm hero" :sidekickId "A20"})}
)

(defn sidekick-route []
  {:status 200 :content-type "application/json" :body (json/generate-string { :hi "I'm sidekick"})}
)

(defn sidekick-route-404 []
  {:status 404 :content-type "text/plain" :body "404 Not Found"})

(defn execute-query [baseUrl query]
  (restql/execute-query :mappings { :hero (str baseUrl "/hero") :sidekick (str baseUrl "/sidekick") } :query query)
)

(defn execute-query-async [baseUrl query callback]
  (restql/execute-query-async :mappings { :hero (str baseUrl "/hero") :sidekick (str baseUrl "/sidekick") } :query query :callback callback))

(deftest simple-request
   (with-routes!
     {"/hero" (hero-route)}
     (let [result (execute-query uri "from hero")]
       (is (= 200 (get-in result [:hero :details :status])))
       (is (= "I'm hero" (get-in result [:hero :result :hi])))
     )
   )
)

(deftest error-request-should-throw-exception
  (with-routes!
    {"/hero" (assoc (hero-route) :status 500)}
    (is (thrown? ExceptionInfo (execute-query uri "from hero")))))

(deftest error-request-should-return-original-response
  (with-routes!
    {"/hero" (assoc (hero-route) :status 500)}
    (let [excp (try+ (execute-query uri "from hero")
                     (catch [:type :request-failed] e
                       e))
          {:keys [resource response result]} excp]
      (is (= 500 (get-in response [:details :status])))
      (is (= {:hi "I'm hero", :sidekickId "A20"} (:result response)))
      (is (= :hero resource)))))

(deftest unmapped-resource-should-throw-exception
  (is (thrown? Exception (execute-query "http://any" "from villain")))
)

(deftest error-request-with-ignore-errors-shouldnt-throw-exception
  (with-routes!
    {"/hero" (assoc (hero-route) :status 500)}
    (let [result (execute-query uri "from hero ignore-errors")]
      (is (= 500 (get-in result [:hero :details :status])))
    )
  )
)

(deftest timeout-request-shoudl-return-408
  (with-routes!
    {"/hero" (assoc (hero-route) :delay 200)}
    (let [result (execute-query uri "from hero timeout 100")]
      (is (= 408 (get-in result [:hero :details :status])))
    )
  )
)

(deftest unreachable-resource-should-return-503
  (let [result (execute-query "http://localhost:9999" "from hero ignore-errors")]
    (is (= nil (get-in result [:hero :details :status])))))

(deftest chained-call
  (with-routes!
    {"/hero" (hero-route)
     "/sidekick" (sidekick-route)}
    (let [result (execute-query uri "from hero\nfrom sidekick")]
      (is (= 200 (get-in result [:hero :details :status])))
      (is (= 200 (get-in result [:sidekick :details :status])))
    )
  )
)

(deftest should-throw-exeption-if-chainned-resource-fails
  (with-routes!
    {"/hero" (hero-route)
      "/sidekick" (sidekick-route-404)}
    (is (thrown? Exception (execute-query uri "from hero\nfrom sidekick with id = hero.sidekickId")))
  )
)

(deftest shouldnt-throw-exeption-if-chainned-resource-timeout-and-ignore-error
  (with-routes!
    {"/hero" (hero-route)}
    {"/sideck" (assoc (sidekick-route) :delay 200)}
    (let [result (execute-query uri "from hero\nfrom sidekick timeout 100 with id = hero.sidekickId ignore-errors")]
      (is (= 200 (get-in result [:hero :details :status])))
      (is (= 408 (get-in result [:sidekick :details :status])))
    )
  )
)

(deftest simple-async-request
  (with-routes!
    {"/hero" (hero-route)}
    (let [expected (atom nil)
          result (<!! (execute-query-async uri "from hero" (fn [result error] (compare-and-set! expected @expected result))))]
      (is (= 200 (get-in @expected [:hero :details :status])))
      (is (= "I'm hero" (get-in @expected [:hero :result :hi]))))))

(deftest unmapped-resource-should-throw-exception-async
  (with-routes!
    {"/hero" (hero-route)}
    (let [expected (atom nil)
          result (<!! (execute-query-async uri "from villain" (fn [result error] (compare-and-set! expected @expected error))))]
      (is (= :validation-error (:type @expected))))))

(deftest error-async-request-should-throw-exception
  (with-routes!
    {"/hero" (assoc (hero-route) :status 500)}
    (let [expected (atom nil)
          result (<!! (execute-query-async uri "from hero" (fn [result error] (compare-and-set! expected @expected error))))]
    (is (= :request-failed (:type @expected))))))