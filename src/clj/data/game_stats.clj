(ns data.game-stats
  (:require [data.core :as core]))

(defn read-game-stats []
  (->> (core/read-csv "data/game_stats.csv")
       (sort-by :date compare)
       (map-indexed
        (fn [index {:keys [timeOnIceM goals passes shots carries
                           takeaways location name date] :as entry}]
          (assoc entry
                 :gameNumber (+ index 1)
                 :location location
                 :name name
                 :date date
                 :timeOnIceH (Integer/parseInt timeOnIceM)
                 :goals (Integer/parseInt goals)
                 :passes (Integer/parseInt passes)
                 :shots (Integer/parseInt shots)
                 :carries (Integer/parseInt carries)
                 :takeaways (Integer/parseInt takeaways))))))

(defn eval-cumulative-game-stats
  "Provided `game-stats` are totalled up into a cumulative by-game summary"
  [game-stats]
  (letfn [(accumulate [remaining result running]
            (if (empty? remaining)
              result
              (let [current-stats (first remaining)
                    new {:gameNumber (:gameNumber current-stats)
                         :location (:location current-stats)
                         :name (:name current-stats)
                         :date (:date current-stats)
                         :goals (+ (:goals running) (:goals current-stats))
                         :passes (+ (:passes running) (:passes current-stats))
                         :shots (+ (:shots running) (:shots current-stats))
                         :carries (+ (:carries running) (:carries current-stats))
                         :takeaways (+ (:takeaways running) (:takeaways current-stats))}
                    new-cum-list (conj result new)]
                (recur (rest remaining) new-cum-list new))))]
    (accumulate game-stats [] {:goals 0 :passes 0 :shots 0 :carries 0 :takeaways 0})))
