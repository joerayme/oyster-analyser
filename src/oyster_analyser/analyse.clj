(ns oyster-analyser.analyse
  (:require [oyster-analyser.data :refer :all]))

(import java.math.RoundingMode)

(defn summarise
  [data]
  (let [durations (remove nil? (map #(:duration %) data))
        total-duration (reduce + durations)
        costs (remove nil? (map #(:cost %) data))
        total-cost (reduce + costs)]
    {:totalDuration total-duration
     :averageDuration (/ total-duration (count durations))
     :totalCost total-cost
     :averageCost (.divide total-cost (BigDecimal. (count costs)) 2 RoundingMode/HALF_UP)
     :totalJourneys (count (filter journey? data))
     }))
