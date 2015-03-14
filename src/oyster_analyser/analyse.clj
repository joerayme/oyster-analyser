(ns oyster-analyser.analyse
  (:require [oyster-analyser.data :refer :all]))

(import java.math.RoundingMode)

(defn summarise
  [data]
  (let [durations (remove nil? (map #(:duration %) data))
        total-duration (reduce + durations)
        costs (remove nil? (map #(:cost %) data))
        total-cost (if (> (count costs) 0)
                     (reduce + costs)
                     (BigDecimal. 0))]
    {:totalDuration   total-duration
     :meanDuration    (if (pos? (count durations))
                        (/ total-duration (count durations))
                        0)
     :shortestJourney (if (pos? (count durations)) (apply min durations) nil)
     :longestJourney  (if (pos? (count durations)) (apply max durations) nil)
     :totalCost       total-cost
     :averageCost     (if (pos? (count costs))
                        (.divide total-cost (BigDecimal. (count costs)) 2 RoundingMode/HALF_UP)
                        0)
     :totalJourneys   (count (filter journey? data))
     :mostPopularType (->> data
                           (filter journey?)
                           (map #(:type %))
                           frequencies
                           (#(apply max-key val %))
                           )
     }))
