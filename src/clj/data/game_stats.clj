(ns data.game-stats
  (:require [data.core :as core]
            [clojure.string :as str]))

(defn blank-stats?
  "For stats, we skip rows with missing game stats"
  [row]
  (some #(str/blank? (get row %))
        [:goals :passes :shots :carries :takeaways]))

(defn read-game-stats []
  (->> (core/read-org-table "data/game_stats.org")
       (remove blank-stats?)
       (sort-by :date compare)
       (map-indexed
        (fn [index {:keys [goals passes shots carries
                           takeaways date] :as entry}]
          (assoc entry
                 :gameNumber (+ index 1)
                 :date (re-find #"\d{4}-\d{2}-\d{2}" date)
                 :goals (Integer/parseInt goals)
                 :passes (Integer/parseInt passes)
                 :shots (Integer/parseInt shots)
                 :carries (Integer/parseInt carries)
                 :takeaways (Integer/parseInt takeaways))))))

(defn read-game-prep
  "Data for mum - she needs date, rink faceoff and depart time"
  []
  (->> (core/read-org-table "data/game_stats.org")
       (sort-by :date compare)
       (map (fn [{:keys [date location faceOff departTime]}]
              {:date (re-find #"\d{4}-\d{2}-\d{2}" date)
               :location location
               :faceOff faceOff
               :departTime departTime}))))

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
