(ns oyster-analyser.core
  (:require [oyster-analyser.data :refer :all]
            [oyster-analyser.analyse :refer :all]
            [clojure.string :as s])
  (:import [java.text NumberFormat]
           [java.util Locale])
  (:gen-class))

(def ^:private currency-instance (NumberFormat/getCurrencyInstance Locale/UK))

(defn- format-duration
  [minutes]
  (cond
    (> minutes 59) (format "%.2f hrs" (float (/ minutes 60)))
    (integer? minutes) (format "%d mins" minutes)
    :else (format "%.2f mins" (float minutes))))

(def ^:private key-mapping {:totalDuration   "Total Duration"
                            :meanDuration    "Avg. Duration"
                            :shortestJourney "Shortest Journey"
                            :longestJourney  "Longest Journey"
                            :totalCost       "Total Cost"
                            :averageCost     "Avg. Cost"
                            :totalJourneys   "Journeys"
                            :mostPopularType "Most popular mode"})

(defn make-table
  [table]
  [[(:totalDuration key-mapping) (format-duration (:totalDuration table))]
   [(:meanDuration key-mapping) (format-duration (:meanDuration table))]
   [(:shortestJourney key-mapping) (format-duration (:shortestJourney table))]
   [(:longestJourney key-mapping) (format-duration (:longestJourney table))]
   [(:totalCost key-mapping) (.format currency-instance (:totalCost table))]
   [(:averageCost key-mapping) (.format currency-instance  (:averageCost table))]
   [(:totalJourneys key-mapping) (format "%d" (:totalJourneys table))]
   (let [[type cnt] (:mostPopularType table)]
     [(:mostPopularType key-mapping) (format "%s (%d%%)" type (int (* (/ cnt (:totalJourneys table)) 100)))])
   ])

(defn- print-table
  [rows]
  (when (seq rows)
    (let [ks (range (count (first rows)))
          widths (map
                   (fn [k]
                     (apply max (map #(count (str (get % k))) rows)))
                   ks)
          spacers (map #(apply str (repeat % "-")) widths)
          fmts (map #(str "%" % "s") widths)
          fmt-row (fn [leader divider trailer row]
                    (str leader
                         (apply str (interpose divider
                                               (for [[col fmt] (map vector (map #(get row %) ks) fmts)]
                                                 (format fmt (str col)))))
                         trailer))]
      (println)
      (doseq [row rows]
        (println (fmt-row " " "  " "" row)))
      (println))))

(defn -main
  [& args]
  (let [summary (summarise (flatten (map #(convert (slurp %)) args)))
        max-title (apply max (map count (map #(% key-mapping) (keys summary))))]
    (print-table (make-table summary))
    ))
