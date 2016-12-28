(ns oyster-analyser.analyse
  (:require [oyster-analyser.data :refer :all]))

(import java.math.RoundingMode)

(defn get-most-popular-type
  [data]
  (->> data
       (filter journey?)
       (map #(:type %))
       frequencies
       (#(apply max-key val %))
       )
  )

(defn summarise
  [data]
  (let [durations (remove nil? (map #(:duration %) data))
        total-duration (reduce + durations)
        credits (remove nil? (map #(:credit %) data))
        costs (remove nil? (map #(:cost %) data))
        total-credit (if (> (count credits) 0)
                     (reduce + credits)
                     (BigDecimal. 0))
        total-cost (if (> (count costs) 0)
                     (reduce + costs)
                     (BigDecimal. 0))]
    {:totalDuration   total-duration
     :meanDuration    (if (pos? (count durations))
                        (/ total-duration (count durations))
                        0)
     :shortestJourney (if (pos? (count durations)) (apply min durations) nil)
     :longestJourney  (if (pos? (count durations)) (apply max durations) nil)
     :totalCredit     total-credit
     :totalCost       total-cost
     :averageCost     (if (pos? (count costs))
                        (.divide total-cost (BigDecimal. (count costs)) 2 RoundingMode/HALF_UP)
                        0)
     :totalJourneys   (count (filter journey? data))
     :mostPopularType (get-most-popular-type data)
     }))

(defn get-week-groupings
  [data]
  (map (fn [data] [(first data) (summarise (second data))])
       (group-by #(.withTime (.withDayOfWeek (or (:start %) (:end %)) 1) 0 0 0 0)
                 data)))
