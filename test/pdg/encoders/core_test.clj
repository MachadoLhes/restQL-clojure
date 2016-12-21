(ns pdg.encoders.core-test
  (:require [slingshot.slingshot :refer [try+]])
  (:use pdg.encoders.core)
  (:use expectations))

;test simple values
(expect "10" (encode base-encoders 10))
(expect "true" (encode base-encoders true))
(expect nil (encode base-encoders nil))

;test throwins exception with unrecognized encoding
(expect :ok
        (try+
          (encode {} {:bla 123})
          (catch [:type :unrecognized-encoding] e :ok)
          (catch Object e e)))
