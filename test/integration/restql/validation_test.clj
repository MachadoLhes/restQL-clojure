(ns restql.validation-test
  (:require [clojure.test :refer :all]
            [slingshot.test :refer :all]
            [restql.core.api.restql :as restql]
  )
)

(defn message? [exception,message]
    (= (:message exception) message)
)

(deftest unmapped-resource-should-throw-exception
    (is (thrown+? #(message? % "from as a keyword should reference a valid mapped resource. Error was in :non-existent") (restql/execute-query :mappings { } :query "from non-existent")))
)

(deftest mapped-resource-with-invalid-url-should-throw-exception
    (is (thrown+? #(message? % "from as a keyword should resolve to a valid URL. Error was in URL \"invalidurl\"") (restql/execute-query :mappings { :invalid "invalidurl" } :query "from invalid")))
)

(deftest domains-without-tdl-should-be-allowed
    (restql/execute-query :mappings { :no-tdl "http://intranet/resource" } :query "from no-tdl")
)