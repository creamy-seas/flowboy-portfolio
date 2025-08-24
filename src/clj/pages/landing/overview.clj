(ns pages.landing.overview
  (:require [utils.config           :as cfg]
            [utils.data.game-stats  :as game-stats]
            [utils.data.time-log    :as tlog]
            [utils.url              :as url]))

(def overview-info
  (let [time-log (tlog/read-time-log)
        game-stats (game-stats/read-game-stats)]
    [["Age"           (:age cfg/config)]
     ["Team"          (:team cfg/config)]
     ["Season"        (:season cfg/config)]
     ["Career Games"  (count game-stats)]
     ["Career Goals"  (apply + (map :goals game-stats))]
     ["Career Hours"  (apply + (map :timeOnIceH time-log))]]))

(defn overview-table
  "Render overview-info as a flat sequence of dt/dd hiccup nodes."
  []
  (mapcat
   (fn [[label value]]
     (let [key-node [:dt.font-semibold.text-myflame (str label ":")]
           href     (when (= label "Season")
                      (url/put-on-base (str "gallery?season=" value)))
           val-node (if href
                      [:a.underline.hover:text-myflame
                       {:href href}
                       value]
                      (str value))]
       [key-node val-node]))
   overview-info))

(defn render
  "Simple row list of main stats - side by side with a profile image"
  []
  [:section {:class "flex flex-col md:flex-row items-center p-6"}
   [:img
    {:src    (url/put-on-base "/assets/profile.avif")
     :alt    "Player Photo"
     :class  "block flex-shrink-0
                order-first md:order-last
                rounded-[5%]
                mb-4 md:mb-0
                w-full md:w-[360px]
                h-auto"}]
   [:div.flex-1.space-y-2.md:space-y-4
    [:h2.text-2xl.font-bold (:name cfg/config)]
    [:dl.grid.grid-cols-2.gap-y-2
     (overview-table)]]])
