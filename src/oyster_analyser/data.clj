(ns oyster-analyser.data
  (:require [clojure.data.csv :as csv]
            [clj-time.core :as t]
            [clj-time.format :as tf]
            [clojure.string :as s]))

(import org.joda.time.Period)

(def oyster-formatter (tf/formatter "dd-MMM-yyyy HH:mm"))

(defn- get-type
  [map line]
  (conj map
        {:type (cond (.startsWith (nth line 3) "Auto top-up") "topup"
                     (.startsWith (nth line 3) "Bus journey") "bus"
                     (.endsWith (nth line 3) "[London Overground]") "overground"
                     :else "tube")}))

(defn- make-datetime
  [date time]
  (tf/parse oyster-formatter (str date " " time)))

(defn- fix-datetime
  [date]
  (if (< (t/hour date) 4) (t/plus date (t/days 1)) date))

(defn- get-times
  [map line]
  (conj map
        {:start (fix-datetime (make-datetime (first line) (second line)))
         :end (if (not (empty? (nth line 2)))
                (fix-datetime (make-datetime (first line) (nth line 2)))
                  )}))

(defn- get-duration
  [map line]
  (conj map
        {:duration (if (not (nil? (:end map)))
                     (.getMinutes (.toStandardMinutes (Period. (:start map) (:end map)))))}))

(defn- get-from-to
  [map line]
  (conj map
        {:from nil
         :to nil}
        (cond (or (= (:type map) "tube")
                  (= (:type map) "overground"))
              (let [parts (s/split (nth line 3) #" to ")]
                {:from (first parts)
                 :to (second parts)}))))

(defn- get-cost
  [map line]
  (conj map
        {:cost (if (not (s/blank? (nth line 4))) (BigDecimal. (nth line 4)))}))

(defn- get-credit
  [map line]
  (conj map
        {:credit (if (not (s/blank? (nth line 5))) (BigDecimal. (nth line 5)))}))

(defn- make-map
  [line]
  (if (and (= (count line) 8)
           (not (= (first line) "Date")))
    (-> {}
        (get-type line)
        (get-times line)
        (get-duration line)
        (get-from-to line)
        (get-cost line)
        (get-credit line)
        )))

(defn convert
  "Converts from CSV to an array of maps"
  [csv-data]
  (remove nil? (map make-map (csv/read-csv csv-data))))
