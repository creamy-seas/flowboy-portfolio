(ns pages.landing.season-table
  (:require [common.elements    :as common]
            [utils.url          :as url]))

(defn build-season-table
  "Aggregates time logs and game data for a season summary"
  [game-stats time-log]
  (let [init-entry {:games 0 :goals 0 :apples 0}
        season->stats
        (reduce (fn [cum cur]
                  (let [season (:season cur)
                        {:keys [games goals apples]} (get cum season init-entry)]
                    (assoc cum season
                           {:games (inc games)
                            :goals (+ goals (:goals cur))
                            :apples (+ apples (:apples cur))})))
                {} game-stats)
        season->stats
        (reduce (fn [cum cur]
                  (let [{:keys [season timeOnIceH]} cur
                        {:keys [games goals apples]} (get cum season init-entry)]
                    (assoc cum season
                           {:games games
                            :goals goals
                            :apples apples
                            :timeOnIceH timeOnIceH})))
                season->stats time-log)
        final (->> season->stats
                   (map (fn [[season {:keys [games goals apples timeOnIceH]}]]
                          {:season season
                           :games (when (pos? games) games)
                           :goals (when (pos? goals) goals)
                           :apples (when (pos? apples) apples)
                           :timeOnIceH timeOnIceH}))
                   (sort-by :season #(compare %2 %1)))]

    final))

(defn render
  "Season by season summary"
  [game-stats time-log]
  (let [season-table (build-season-table game-stats time-log)]
    [:section.p-2.rounded-lg.overflow-auto
     (common/fat-title "📊 Season Stats")
     [:table.table.table-compact.w-full.text-center
      [:thead {:class "bg-mytheme/90"}
       (into [:tr]
             (map-indexed (fn [idx heading]
                            [:th {:key idx} heading])
                          ["Season" "Games" "G" "A" "Ice time (h)"]))]
      (into [:tbody]
            (map-indexed (fn [idx {:keys [season games goals apples timeOnIceH]}]
                           [:tr {:key idx}
                            [:td
                             [:a.underline.my-hover-link {:href (url/put-on-base (str "gallery?season=" season))}
                              season]]
                            [:td games]
                            [:td goals]
                            [:td apples]
                            [:td timeOnIceH]])
                         season-table))]]))
