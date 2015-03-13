(ns oyster-analyser.core
  (:require [oyster-analyser.data :refer :all]
            [oyster-analyser.analyse :refer :all]
            [clojure.pprint :refer [pprint get-pretty-writer cl-format]]
            [clojure.string :as s])
  (:gen-class))

(def key-mapping {:totalDuration "Total Duration"
                  :averageDuration "Avg. Duration"
                  :totalCost "Total Cost"
                  :averageCost "Avg. Cost"
                  :totalJourneys "Journeys"})

(defn- print-row
  [title value type column-width]
  (binding [*out* (get-pretty-writer *out*)]
    (cl-format true (str " ~A~v,1T ~7" type)
               title
               (+ column-width 3)
               value
               )
    (prn)))

(defn- print-table [table column-width]
  (print-row "Total Duration" (:totalDuration table) "D" column-width)
  (print-row "Avg. Duration" (:averageDuration table) ",2F" column-width)
  (print-row "Total Cost" (cl-format nil "£~F" (:totalCost table)) "@A" column-width)
  (print-row "Avg. Cost" (cl-format nil "£~F" (:averageCost table)) "@A" column-width)
  (print-row "Journeys" (:totalJourneys table) "D" column-width)
  )

(defn -main
  [& args]
  (let [summary (summarise (flatten (map #(convert (slurp %)) args)))
        max-title (apply max (map count (map #(% key-mapping) (keys summary))))]
    (println)
    (print-table summary max-title)
    (println)
    ))
