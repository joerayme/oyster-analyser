(ns oyster-analyser.core
  (:require [oyster-analyser.data :refer :all]
            [oyster-analyser.analyse :refer :all]
            [clojure.pprint :refer [pprint get-pretty-writer cl-format]]
            [clojure.string :as s])
  (:gen-class))

(def key-mapping {:totalDuration "Total Duration"
                  :averageDuration "Avg. Duration"
                  :totalCost "Total Cost"
                  :averageCost "Avg. Cost"})

(defn- print-table [aseq column-width]
  (binding [*out* (get-pretty-writer *out*)]
    (doseq [row aseq]
      (cl-format true " ~A~v,1T ~7,2F"
                 ((first row) key-mapping)
                 (+ column-width 3)
                 (second row)
                 )
      (prn))))

(defn -main
  [& args]
  (let [summary (summarise (flatten (map #(convert (slurp %)) args)))
        max-title (apply max (map count (map #(% key-mapping) (keys summary))))]
    (println)
    (print-table summary max-title)
    (println)
    ))
