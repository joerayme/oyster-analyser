(ns oyster-analyser.analyse-test
  (:require [clojure.test :refer :all]
            [oyster-analyser.analyse :refer :all]
            [clj-time.core :as t]))

(defn- make-record
  [record]
  (merge {:duration nil
          :cost     (BigDecimal. 0)
          :start    nil
          :end      nil
          :type     "none"} record))

(deftest summarise-test
  (testing "duration"
    (let [results (summarise [(make-record {:duration 12})
                              (make-record {:duration 17})
                              (make-record {:duration 63})])]
      (is (= (:averageDuration results) (/ 92 3)))
      (is (= (:totalDuration results) 92))
      (is (= (:longestJourney results) 63))))
  (testing "cost"
    (let [results (summarise [(make-record {:cost (BigDecimal. "2.8")})
                              (make-record {:cost (BigDecimal. "2.3")})
                              (make-record {:cost (BigDecimal. "1.5")})])]

      (is (= (:averageCost results) (BigDecimal. "2.2")))
      (is (= (:totalCost results) (BigDecimal. "6.6")))))
  (testing "journey count"
    (let [results (summarise [(make-record {:type "tube"})
                              (make-record {:type "tube"})
                              (make-record {:type "bus"})
                              (make-record {:type "topup"})])]
      (is (= (:totalJourneys results) 3))
      (is (= (:mostPopularType results) ["tube" 2]))
      )))
