(ns restql.parser.core
  (:require [instaparse.core :as insta]
            [restql.parser.printer :refer [pretty-print]]
            [restql.parser.producer :refer [produce *restql-variables*]]
            [clojure.java.io :as io])
  (:use     [slingshot.slingshot :only [throw+]]))

(def query-parser
  (insta/parser (io/resource "grammar.ebnf") :output-format :enlive))

(defn handle-success
  "Handles parsing success"
  [result & {:keys [pretty]}]

  (if pretty
      (pretty-print result)
      result))

(defn handle-error
  "Handles any parsing errors"
  [result]

  (let [error (insta/get-failure result)]
    (throw+ {:type :parse-error
             :reason (:reason error)
             :line (:line error)
             :column (:column error)})))

(defn handle-produce
  "Produces the EDN query of a given restQL query"
  [tree context]

  (binding [*restql-variables* (if (nil? context) {} context)]
    (produce tree)))

(defn parse-query
  "Parses the restQL query"
  [query-text & {:keys [pretty context]}]

  (let [result (query-parser query-text)]
    (if (insta/failure? result)
      (handle-error result)
      (handle-success (handle-produce result context) :pretty pretty))))

(comment
  (parse-query "from cart only id, lines.id, lines.sku")
  (parse-query "from cart only id")

  ":select #{:id [:lines #{:id :sku}]}"

  (try
    (parse-query "from cart only lines.id -> match(\"abc\"), lines.sku" :context {})
    (catch Exception e
      e))

  )
