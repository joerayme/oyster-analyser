(ns oyster-analyser.core
  (:require [oyster-analyser.data :refer :all]
            [oyster-analyser.analyse :refer :all]
            [clojure.pprint :refer [pprint get-pretty-writer cl-format]]
            [clojure.string :as s])
  (:gen-class))

(defn- format-duration
  [minutes]
  (cond
    (> minutes 59) (format "%f hrs" (/ minutes 60))
    :else (format "%d mins" minutes)))

(def key-mapping {:totalDuration "Total Duration"
                  :averageDuration "Avg. Duration"
                  :longestJourney "Longest Journey"
                  :totalCost "Total Cost"
                  :averageCost "Avg. Cost"
                  :totalJourneys "Journeys"
                  :mostPopularType "Most popular mode"})

(defn- print-row
  [title value type column-width]
  (binding [*out* (get-pretty-writer *out*)]
    (cl-format true (str " ~A~v,1T ~7" type)
               title
               (+ column-width 4)
               value
               )
    (prn)))

(defn- print-table [table column-width]
  (print-row (:totalDuration key-mapping) (:totalDuration table) "D" column-width)
  (print-row (:averageDuration key-mapping) (:averageDuration table) ",2F" column-width)
  (print-row (:longestJourney key-mapping) (format-duration (:longestJourney table)) "A" column-width)
  (print-row (:totalCost key-mapping) (cl-format nil "£~F" (:totalCost table)) "@A" column-width)
  (print-row (:averageCost key-mapping) (cl-format nil "£~F" (:averageCost table)) "@A" column-width)
  (print-row (:totalJourneys key-mapping) (:totalJourneys table) "D" column-width)
  (print-row (:mostPopularType key-mapping) (:mostPopularType table) "@A" column-width)
  )

(defn -main
  [& args]
  (let [summary (summarise (flatten (map #(convert (slurp %)) args)))
        max-title (apply max (map count (map #(% key-mapping) (keys summary))))]
    (println)
    (print-table summary max-title)
    (println)
    ))
