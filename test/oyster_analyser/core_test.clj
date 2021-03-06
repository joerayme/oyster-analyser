(ns oyster-analyser.core-test
  (:require [clojure.test :refer :all]
            [oyster-analyser.core :refer :all]))

(deftest make-table-test
  (testing "table"
    (let [table (make-table {:totalDuration 788
                             :meanDuration (BigDecimal. "24.63")
                             :shortestJourney 9
                             :longestJourney 58
                             :totalCredit (BigDecimal. "12.80")
                             :totalCost (BigDecimal. "137.78")
                             :averageCost (BigDecimal. "1.78")
                             :totalJourneys 77
                             :mostPopularType '("bus" 44)})
          expected [["Total Duration" "13 hrs 8 mins"]
                    ["Avg. Duration" "24 mins"]
                    ["Shortest Journey" "9 mins"]
                    ["Longest Journey" "58 mins"]
                    ["Total Credit" "£12.80"]
                    ["Total Cost" "£137.78"]
                    ["Avg. Cost" "£1.78"]
                    ["Journeys" "77"]
                    ["Most popular mode" "Bus"]]]
      (is (= (sort-by first expected) (sort-by first table)))
      )
    )
  )
