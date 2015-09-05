(ns oyster-analyser.core
  (:require [oyster-analyser.data :refer :all]
            [oyster-analyser.analyse :refer :all]
            [clojure.string :as s]
            [clj-time.format :as f])
  (:import [java.text NumberFormat]
           [java.util Locale])
  (:gen-class))

(def ^:private currency-instance (NumberFormat/getCurrencyInstance Locale/UK))

(defn- format-duration
  [minutes]
  (if minutes
    (cond
      (> minutes 59) (format "%d hrs %s" (int (/ minutes 60)) (format-duration (mod minutes 60)))
      :else (format "%d mins" (int minutes)))))

(defn- format-type
  [type]
  ; For some reason (case) doesn't work here...
  (cond
    (= TYPE_BOAT type) "Boat"
    (= TYPE_BUS type) "Bus"
    (= TYPE_OVERGROUND type) "Overground"
    (= TYPE_RAIL type) "Tube/Rail"
    (= TYPE_REFUND type) "Refund"
    (= TYPE_TOPUP type) "Topup"))

(def ^:private key-mapping {:totalDuration   "Total Duration"
                            :meanDuration    "Avg. Duration"
                            :shortestJourney "Shortest Journey"
                            :longestJourney  "Longest Journey"
                            :totalCredit     "Total Credit"
                            :totalCost       "Total Cost"
                            :averageCost     "Avg. Cost"
                            :totalJourneys   "Journeys"
                            :mostPopularType "Most popular mode"})

(def ^:private format-mapping {:totalDuration format-duration
                               :meanDuration format-duration
                               :shortestJourney format-duration
                               :longestJourney format-duration
                               :totalCredit #(.format currency-instance %)
                               :totalCost #(.format currency-instance %)
                               :averageCost #(.format currency-instance %)
                               :totalJourneys #(format "%d" %)
                               ;; :mostPopularType (fn [[type cnt]] (format "%s (%d journeys)" (format-type type) (int cnt)))})
                               :mostPopularType (fn [[type cnt]] (format-type type))})

(defn make-table
  [table]
  (filter identity (map
                     (fn [key] [(key key-mapping) ((key format-mapping) (key table))])
                     (keys table))))

(defn- print-table
  [rows]
  (when (seq rows)
    (let [ks (range (count (first rows)))
          widths (map
                   (fn [k]
                     (apply max (map #(count (str (get % k))) rows)))
                   ks)
          fmts (map #(str "%" % "s") widths)
          fmt-row (fn [leader divider trailer row]
                    (str leader
                         (apply str (interpose divider
                                               (for [[col fmt] (map vector (map #(get row %) ks) fmts)]
                                                 (format fmt (str col)))))
                         trailer))]
      (println)
      (doseq [row rows]
        (println (fmt-row " " "  " "" row))))))

(defn- usage
  [opts]
  (->> ["Analyses an Oyster data dump and prints out the results"
        ""
        "Usage: oyster-analyser [file ...]"]
       (s/join \newline)))


(def ^:private date-formatter (f/formatter "dd MMM yyyy"))

(defn -main
  [& args]
  (if (> (count args) 0)
    (try
      (let [data (convert (apply str (map slurp args)))
            summary (summarise data)
            max-title (apply max (map count (map #(% key-mapping) (keys summary))))]
        (prn)
        (print (format " From %s to %s"
                       (f/unparse date-formatter (:start (first (filter #(not (nil? (:start %))) data))))
                       (f/unparse date-formatter (:start (last (filter #(not (nil? (:start %))) data))))))
        (prn)
        (print-table (make-table summary))
        (print-table
          (vec (cons (into ["Week beginning"] (vals key-mapping))
                     (map (fn [data] (into [(f/unparse date-formatter (first data))]
                                           (map (fn [key] ((key format-mapping) (key (second data))))
                                                (keys (second data)))))
                          (get-week-groupings data)))))
        )
      (catch java.io.FileNotFoundException e
        (println (str "Error: " (.getMessage e)))
        (println (usage ""))))
    (do (println "Please provide some files to analyse")
        (println (usage "")))))
