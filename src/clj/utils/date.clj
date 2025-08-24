(ns utils.date
  (:import [java.time LocalDate Period]))

; TODO: remove once everything put into data
(defn parse [^String s]
  (LocalDate/parse s))

(defn cast-date
  "Convert ISO string into supplied format"
  [^String date-str ^String format]
  (let [formatter (java.time.format.DateTimeFormatter/ofPattern format)]
    (.format (LocalDate/parse date-str) formatter)))

(defn calculate-age
  "Given a birth-date string in ISO-8601 (\"yyyy-MM-dd\"), returns age in years relative to
  pass in date, or today (default)"
  ([^String bday-str]
   (calculate-age bday-str (.toString (LocalDate/now))))
  ([^String bday-str
    ^String date-str]
   (let [bday (LocalDate/parse bday-str)
         today (LocalDate/parse date-str)
         years (-> (Period/between bday today)
                   .getYears)]
     years)))

(defn current-season
  "Returns a map with the current hockey season as two years.
   Seasons start on September 1st. E.g. today is 2025-07-18, so
   we’re still in the 2024-2025 season."
  []
  (let [today      (LocalDate/now)
        year       (.getYear today)
        sept-first (LocalDate/of year 9 1)
        start-year (if (.isBefore today sept-first)
                     (dec year)
                     year)
        end-year   (inc start-year)]
    (str start-year "-" end-year)))
