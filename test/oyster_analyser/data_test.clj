(ns oyster-analyser.data-test
  (:require [clojure.test :refer :all]
            [oyster-analyser.data :refer :all]
            [clj-time.core :as t]))

(def test-data "
Date,Start Time,End Time,Journey/Action,Charge,Credit,Balance,Note
01-Jan-2015,14:03,14:20,\"Angel to Oxford Circus\",2.30,,10.70,\"\"
21-Feb-2015,00:12,00:24,\"Victoria [London Underground] to Brixton [London Underground]\",2.30,,13.20,\"\"
25-Feb-2015,22:46,23:13,\"Honor Oak Park to Hoxton [London Overground]\",2.80,,18.20,\"\"
29-Jan-2015,21:45,,\"Auto top-up, Leicester Square\",,20.00,28.70,\"\"
21-Feb-2015,23:49,00:09,\"Waterloo (Jubilee line entrance) to Old Street\",.00,,11.40,\"The fare for this journey was capped as you reached the daily charging limit for the zones used\"
24-Feb-2015,21:44,,\"Bus journey, route 55\",1.50,,24.60,\"\"
08-Mar-2015,12:23,,\"Riverboat ticket bought using pay as you go\",6.44,,7.46,\"\"")

(deftest convert-test
  (testing "with correctly formatted data"
    (let [result (convert test-data)]
      (is (= {:type "tube"
              :from "Angel"
              :to "Oxford Circus"
              :credit nil
              :cost 2.3M
              :start (t/date-time 2015 1 1 14 3)
              :end (t/date-time 2015 1 1 14 20)
              :duration 17}
             (nth result 0)))

      (is (= {:type "topup"
              :from nil
              :to nil
              :credit 20M
              :cost nil
              :start (t/date-time 2015 1 29 21 45)
              :end nil
              :duration nil}
             (nth result 1)))

      (is (= {:type "tube"
              :from "Waterloo (Jubilee line entrance)"
              :to "Old Street"
              :credit nil
              :cost (BigDecimal. "0")
              :start (t/date-time 2015 2 21 23 49)
              :end (t/date-time 2015 2 22 0 9)
              :duration 20}
             (nth result 2)))

      (is (= {:type "tube"
              :from "Victoria [London Underground]"
              :to "Brixton [London Underground]"
              :credit nil
              :cost (BigDecimal. "2.3")
              :start (t/date-time 2015 2 22 0 12)
              :end (t/date-time 2015 2 22 0 24)
              :duration 12}
             (nth result 3)))

      (is (= {:type "bus"
              :from nil
              :to nil
              :credit nil
              :cost (BigDecimal. "1.5")
              :start (t/date-time 2015 2 24 21 44)
              :end nil
              :duration nil}
             (nth result 4)))

      (is (= {:type "overground"
              :from "Honor Oak Park"
              :to "Hoxton [London Overground]"
              :credit nil
              :cost (BigDecimal. "2.8")
              :start (t/date-time 2015 2 25 22 46)
              :end (t/date-time 2015 2 25 23 13)
              :duration 27}
             (nth result 5)))

      (is (= {:type "boat"
              :from nil
              :to nil
              :credit nil
              :cost (BigDecimal. "6.44")
              :start (t/date-time 2015 3 8 12 23)
              :end nil
              :duration nil}
             (nth result 6)))
      )))

(deftest is-journey-test
  (testing "correctly identifies topups"
    (is (= (journey? {:type "tube"
                    :from "Waterloo (Jubilee line entrance)"
                    :to "Old Street"
                    :start (t/date-time 2015 2 21 23 49)
                    :end (t/date-time 2015 2 21 0 9)
                    :duration 20
                    :cost (BigDecimal. "0")
                    :credit nil})
           true))
    (is (= (journey? {:type "topup"
                    :from nil
                    :to nil
                    :start (t/date-time 2015 2 25 22 46)
                    :end nil
                    :duration nil
                    :cost nil
                    :credit (BigDecimal. "20")})
           false))))
