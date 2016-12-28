(ns oyster-analyser.data
  (:require [clojure.data.csv :as csv]
            [clj-time.core :as t]
            [clj-time.format :as tf]
            [clojure.string :as s]))

(import org.joda.time.Period)

(def oyster-formatter (tf/formatter "dd-MMM-yyyy HH:mm"))
(def contactless-formatter (tf/formatter "dd-MMM-yy HH:mm"))

(def TYPE_TOPUP "topup")
(def TYPE_BUS "bus")
(def TYPE_OVERGROUND "overground")
(def TYPE_BOAT "boat")
(def TYPE_RAIL "rail")
(def TYPE_REFUND "refund")

(defn- get-type
  [line map]
  (conj map
        {:type (cond (.contains (s/lower-case (nth line 3)) "refund") TYPE_REFUND
                     (.startsWith (nth line 3) "Auto top-up") TYPE_TOPUP
                     (.startsWith (s/lower-case (nth line 3)) "bus journey") TYPE_BUS
                     (.endsWith (nth line 3) "[London Overground]") TYPE_OVERGROUND
                     (.startsWith (nth line 3) "Riverboat") TYPE_BOAT
                     :else TYPE_RAIL)}))

(defn- make-datetime
  [date time]
  (let [formatter (cond (< (count date) 11) contactless-formatter
                        :else oyster-formatter)]
    (tf/parse formatter (str date " " time))))

(defn- fix-datetime [date]
  """
  Oyster times between midnight and 4am count as the previous day
  so we fix them by adding a day onto the date if we're between
  those two times
  """
  (if (< (t/hour date) 4)
    (t/plus date (t/days 1))
    date))

(defn- get-times
  [line map]
  (conj map
        {:start (if (not (empty? (second line)))
                  (fix-datetime (make-datetime (first line) (second line))))
         :end (if (not (empty? (nth line 2)))
                (fix-datetime (make-datetime (first line) (nth line 2)))
                )}))

(defn- get-duration
  [line map]
  (conj map
        {:duration (if (and (not (nil? (:start map))) (not (nil? (:end map))))
                     (.getMinutes (.toStandardMinutes (Period. (:start map) (:end map)))))}))

(defn- get-from-to
  [line map]
  (conj map
        {:from nil
         :to nil}
        (cond (or (= (:type map) TYPE_RAIL)
                  (= (:type map) TYPE_OVERGROUND))
              (let [parts (s/split (nth line 3) #" to ")]
                {:from (first parts)
                 :to (second parts)}))))

(defn- get-cost
  [line map]
  (conj map
        {:cost (if (not (s/blank? (nth line 4)))
                 (let [cost (BigDecimal. (nth line 4))]
                   (if (and (< cost 0)
                            (= (count line) 7))
                     (.abs cost)
                     cost)))}))

(defn- get-credit
  [line map]
  (conj map
        {:credit (if (and (not (s/blank? (nth line 5)))
                          (= (count line) 8))
                   (BigDecimal. (nth line 5)))}))

(defn- make-map
  [line]
  (if (and (> (count line) 6)
           (not (= (first line) "Date")))
    (->> {}
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
  (sort-by :start
           (remove nil?
                   (map make-map
                        (csv/read-csv csv-data)))))

(defn journey?
  "Determines whether a record is a journey"
  [record]
  (and (not= (:type record) TYPE_TOPUP)
       (not= (:type record) TYPE_REFUND)))
