(ns pdg.extractor-test
  (:use pdg.extractor)
  (:use expectations))

(expect "123" (traverse {:customer {:id "123"}} [:customer :id]))
(expect {:body nil :path [] :base []}
        (extract-multiple {:customer {:id "123"}} [:customer :id]))

(expect ["1" "2"] (traverse {:lines [{:id "1"} {:id "2"}]} [:lines :id]))
(expect {:body [{:id "1"} {:id "2"}]
         :base [:lines]
         :path [:id]}
        (extract-multiple {:lines [{:id "1"} {:id "2"}]} [:lines :id]))

(expect {:body [{:product {:id 1}}]
         :path [:product :id]
         :base [:cart :lines]}
        (extract-multiple {:cart {:lines [{:product {:id 1}}]}}
                                 [:cart :lines :product :id]))

(expect false (has-multiples {:customer {:id "123"}} [:customer :id]))
(expect true  (has-multiples {:lines [{:id "1"} {:id "2"}]} [:lines :id]))

(expect true  (has-multiples [{:id "1"} {:id "2"}] []))
(expect true  (has-multiples [{:id "1"} {:id "2"}] nil))
(expect true  (has-multiples [{:id "1"} {:id "2"}] (seq [])))

(expect false (has-multiples {:id "1"} []))
(expect false (has-multiples {:id "1"} nil))
(expect false (has-multiples {:id "1"} (seq [])))
