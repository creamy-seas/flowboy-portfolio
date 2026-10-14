(ns data.highlights
  (:require [utils.date   :as date]
            [utils.config :as cfg]
            [data.gallery :as gallery]
            [data.core    :as core]))

(defn read-highlights []
  (let [gallery-data (gallery/read-gallery)]
    (->> (core/read-csv "data/highlights.csv")
         (sort-by :date #(compare %2 %1))
         (map
          (fn [{:keys [date gallery-description] :as entry}]
            (assoc entry
                   :age (let [{:keys [years months]}
                              (date/calculate-age (:bday cfg/config) date)]
                          (format "%dy %dm" years months))
                   ;; Search the gallery data for a matching description
                   :gallery-idx (some
                                 #(when (= (:description %) gallery-description) (:gallery-idx %))
                                 gallery-data)))))))
