(ns data.core
  (:require [clojure.data.json  :as json]
            [clojure.java.io    :as io]
            [clojure.data.csv   :as csv]
            [clojure.string     :as str]))

(defn export-data
  "Supplied data is dumped to json and stored in window.[label]"
  [data ^String label]
  [:script {:type "text/javascript"}
   (str "window." label " = " (json/write-str data) ";")])

(defn read-csv
  "CSV is read with headers and rows and outputted to a list of the form
  [{:header1 val1 :header2 val2}, {...}]"
  [csv-file]
  (with-open [r (io/reader csv-file)]
    (let [[headers & rows] (csv/read-csv r)
          ks (map keyword headers)
          raw-entries (map (fn [row] (zipmap ks row)) rows)]
      (doall raw-entries))))

(defn to-camel-case [s]
  (let [[w0 & wr] (str/split s #"\s+")]
    (apply str (str/lower-case w0)
           (map str/capitalize wr))))

(defn read-org-table
  "Read in all of the tables into a vector"
  [src]
  (let [comment? #(re-matches #"^#.*" %)
        blank? #(re-matches #"^\s*" %)
        ;; Whole line is |----+----| etc
        hline? #(re-matches #"^\|[-+]+\|" %)
        parse-line (fn [line]
                     (-> (str/replace line #"^\s*\|\s*" "")
                         (str/replace #"\s*\|\s*" "|")
                         (str/split #"\|")))
        parse (fn [rdr]
                (->> (line-seq rdr)
                     (remove blank?)
                     (remove comment?)
                     (remove hline?)
                     (map parse-line)))
        [headers & rows] (with-open [r (io/reader src)]
                           (doall (parse r)))
        keywords (->> (map to-camel-case headers)
                      (map keyword))]
    (map #(zipmap keywords %) rows)))

(first (read-org-table "data/game_stats.org"))
