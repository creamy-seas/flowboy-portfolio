(ns utils.data.core
  (:require [clojure.data.json  :as json]
            [clojure.java.io    :as io]
            [clojure.data.csv   :as csv]))

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
