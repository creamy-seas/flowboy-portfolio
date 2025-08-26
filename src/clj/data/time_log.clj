(ns data.time-log
  (:require [data.core :as core]))

(defn read-time-log []
  (->> (core/read-csv "data/time_log.csv")
       (sort-by :season #(compare %2 %1))
       (map (fn [{:keys [timeOnIceH] :as entry}]
              (assoc entry :timeOnIceH (Integer/parseInt timeOnIceH))))))
