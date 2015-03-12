(ns oyster-analyser.analyse-test
  (:require [clojure.test :refer :all]
            [oyster-analyser.analyse :refer :all]
            [clj-time.core :as t]))

(def test-data
  [{:type "tube"
    :from "Victoria [London Underground]"
    :to "Brixton [London Underground]"
    :start (t/date-time 2015 2 21 13 12)
    :end (t/date-time 2015 2 21 13 24)
    :duration 12
    :cost (BigDecimal. "2.8")}
   {:type "tube"
    :from "Angel"
    :to "Oxford Circus"
    :cost (BigDecimal. "2.3")
    :start (t/date-time 2015 1 1 14 3)
    :end (t/date-time 2015 1 1 14 20)
    :duration 17}])

(deftest summarise-test
  (testing "summary generated correctly"
    (let [results (summarise test-data)]
      (is (= (:averageDuration results) (/ 29 2)))
      (is (= (:totalDuration results) 29))
      (is (= (:averageCost results) (BigDecimal. "2.55")))
      (is (= (:totalCost results) (BigDecimal. "5.1")))
      )))
