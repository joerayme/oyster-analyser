(ns oyster-analyser.data-test
  (:require [clojure.test :refer :all]
            [oyster-analyser.data :refer :all]
            [clj-time.core :as t]))

(def test-data "
Date,Start Time,End Time,Journey/Action,Charge,Credit,Balance,Note
01-Jan-2015,14:03,14:20,\"Angel to Oxford Circus\",2.30,,10.70,\"\"
21-Feb-2015,13:12,13:24,\"Victoria [London Underground] to Brixton [London Underground]\",2.30,,13.20,\"\"
25-Feb-2015,22:46,23:13,\"Honor Oak Park to Hoxton [London Overground]\",2.80,,18.20,\"\"
24-Feb-2015,21:44,,\"Bus journey, route 55\",1.50,,24.60,\"\"")

(deftest convert-test
  (testing "with correctly formatted data"
    (let [result (convert test-data)]
      (is (= (:type (first result)) "tube"))
      (is (= (:from (first result)) "Angel"))
      (is (= (:to (first result)) "Oxford Circus"))
      (is (.compareTo (:cost (first result)) (BigDecimal. "2.3")))
      (is (= (:start (first result)) (t/date-time 2015 1 1 14 3)))
      (is (= (:end (first result)) (t/date-time 2015 1 1 14 20)))
      (is (= (:duration (first result)) 17))

      (is (= (:type (second result)) "tube"))
      (is (= (:from (second result)) "Victoria [London Underground]"))
      (is (= (:to (second result)) "Brixton [London Underground]"))
      (is (.compareTo (:cost (second result)) (BigDecimal. "2.3")))
      (is (= (:start (second result)) (t/date-time 2015 2 21 13 12)))
      (is (= (:end (second result)) (t/date-time 2015 2 21 13 24)))
      (is (= (:duration (second result)) 12))

      (is (= (:type (nth result 2)) "overground"))
      (is (= (:from (nth result 2)) "Honor Oak Park"))
      (is (= (:to (nth result 2)) "Hoxton [London Overground]"))
      (is (.compareTo (:cost (nth result 2)) (BigDecimal. "2.8")))
      (is (= (:start (nth result 2)) (t/date-time 2015 2 25 22 46)))
      (is (= (:end (nth result 2)) (t/date-time 2015 2 25 23 13)))
      (is (= (:duration (nth result 2)) 27))

      (is (= (:type (nth result 3)) "bus"))
      (is (nil? (:from (nth result 3))))
      (is (nil? (:to (nth result 3))))
      (is (.compareTo (:cost (nth result 3)) (BigDecimal. "1.5")))
      (is (= (:start (nth result 3)) (t/date-time 2015 2 24 21 44)))
      (is (nil? (:end (nth result 3))))
      (is (nil? (:duration (nth result 3))))
      )))
