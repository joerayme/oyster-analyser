(ns oyster-analyser.data-test
  (:require [clojure.test :refer :all]
            [oyster-analyser.data :refer :all]
            [clj-time.core :as t]))

(def test-data "
Date,Start Time,End Time,Journey/Action,Charge,Credit,Balance,Note
01-Jan-2015,14:03,14:20,\"Angel to Oxford Circus\",2.30,,10.70,\"\"
21-Feb-2015,13:12,13:24,\"Victoria [London Underground] to Brixton [London Underground]\",2.30,,13.20,\"\"
25-Feb-2015,22:46,23:13,\"Honor Oak Park to Hoxton [London Overground]\",2.80,,18.20,\"\"
29-Jan-2015,21:45,,\"Auto top-up, Leicester Square\",,20.00,28.70,\"\"
24-Feb-2015,21:44,,\"Bus journey, route 55\",1.50,,24.60,\"\"")

(deftest convert-test
  (testing "with correctly formatted data"
    (let [result (convert test-data)]
      (is (= (:type (nth result 0)) "tube"))
      (is (= (:from (nth result 0)) "Angel"))
      (is (= (:to (nth result 0)) "Oxford Circus"))
      (is (.compareTo (:cost (nth result 0)) (BigDecimal. "2.3")))
      (is (= (:start (nth result 0)) (t/date-time 2015 1 1 14 3)))
      (is (= (:end (nth result 0)) (t/date-time 2015 1 1 14 20)))
      (is (= (:duration (nth result 0)) 17))

      (is (= (:type (nth result 1)) "tube"))
      (is (= (:from (nth result 1)) "Victoria [London Underground]"))
      (is (= (:to (nth result 1)) "Brixton [London Underground]"))
      (is (.compareTo (:cost (nth result 1)) (BigDecimal. "2.3")))
      (is (= (:start (nth result 1)) (t/date-time 2015 2 21 13 12)))
      (is (= (:end (nth result 1)) (t/date-time 2015 2 21 13 24)))
      (is (= (:duration (nth result 1)) 12))

      (is (= (:type (nth result 2)) "overground"))
      (is (= (:from (nth result 2)) "Honor Oak Park"))
      (is (= (:to (nth result 2)) "Hoxton [London Overground]"))
      (is (.compareTo (:cost (nth result 2)) (BigDecimal. "2.8")))
      (is (= (:start (nth result 2)) (t/date-time 2015 2 25 22 46)))
      (is (= (:end (nth result 2)) (t/date-time 2015 2 25 23 13)))
      (is (= (:duration (nth result 2)) 27))

      (is (= (:type (nth result 3)) "topup"))
      (is (nil? (:from (nth result 3))))
      (is (nil? (:to (nth result 3))))
      (is (nil? (:cost (nth result 3))))
      (is (= (:start (nth result 3)) (t/date-time 2015 1 29 21 45)))
      (is (nil? (:end (nth result 3))))
      (is (nil? (:duration (nth result 3))))
      (is (.compareTo (:credit (nth result 3)) (BigDecimal. "20")))

      (is (= (:type (nth result 4)) "bus"))
      (is (nil? (:from (nth result 4))))
      (is (nil? (:to (nth result 4))))
      (is (.compareTo (:cost (nth result 4)) (BigDecimal. "1.5")))
      (is (= (:start (nth result 4)) (t/date-time 2015 2 24 21 44)))
      (is (nil? (:end (nth result 4))))
      (is (nil? (:duration (nth result 4))))
      )))
