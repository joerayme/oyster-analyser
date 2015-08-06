(ns oyster-analyser.analyse-test
  (:require [clojure.test :refer :all]
            [oyster-analyser.analyse :refer :all]
            [oyster-analyser.data :as data]
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
                              (make-record {:duration nil})
                              (make-record {:duration 17})
                              (make-record {:duration 63})])]
      (is (= (:meanDuration results) (/ 92 3)))
      (is (= (:totalDuration results) 92))
      (is (= (:shortestJourney results) 12))
      (is (= (:longestJourney results) 63))))
  (testing "credit"
    (let [results (summarise [(make-record {:credit (BigDecimal. "2.8")})
                              (make-record {:credit (BigDecimal. "2.3")})
                              (make-record {:credit (BigDecimal. "1.5")})])]

      (is (= (:totalCredit results) (BigDecimal. "6.6")))))
  (testing "cost"
    (let [results (summarise [(make-record {:cost (BigDecimal. "2.8")})
                              (make-record {:cost (BigDecimal. "2.3")})
                              (make-record {:cost (BigDecimal. "1.5")})])]

      (is (= (:averageCost results) (BigDecimal. "2.2")))
      (is (= (:totalCost results) (BigDecimal. "6.6")))))
  (testing "journey count"
    (let [results (summarise [(make-record {:type data/TYPE_RAIL})
                              (make-record {:type data/TYPE_TOPUP})
                              (make-record {:type data/TYPE_RAIL})
                              (make-record {:type data/TYPE_TOPUP})
                              (make-record {:type data/TYPE_BUS})
                              (make-record {:type data/TYPE_TOPUP})])]
      (is (= (:totalJourneys results) 3))
      (is (= (:mostPopularType results) [data/TYPE_RAIL 2]))
      )))
