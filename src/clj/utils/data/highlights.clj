(ns utils.data.highlights
  (:require [utils.date         :as date]
            [utils.config       :as cfg]
            [utils.data.core    :as data]))

(defn read-highlights []
  (->> (data/read-csv "data/highlights.csv")
       (sort-by :date compare)
       (map
        (fn [{:keys [date] :as entry}]
          (assoc entry
                 :age (date/calculate-age
                       (:bday cfg/config)
                       date))))))
