(ns pages.landing.page
  (:require
   [hiccup.page                    :refer [include-js]]
   [utils.url                      :as url]
   [utils.data.game-stats          :as stats]
   [utils.data.core                :as data]
   [utils.data.time-log            :as tlog]
   [utils.config                   :as cfg]
   [common.layout                  :as layout]
   [pages.landing.highlights       :as highlights]
   [pages.landing.overview         :as overview]
   [pages.landing.season-table     :as season-table]
   [pages.landing.gallery-link     :as gallery-link]
   [pages.landing.game-graph       :as game-graph]))

(def profile-image-preload
  [:link {:rel "preload"
          :as "image"
          :href (url/put-on-base "/assets/profile.avif")
          :type "image/avif"
          :fetchpriority "high"}])

(defn render []
  (let [time-log (tlog/read-time-log)
        game-stats (stats/read-game-stats)
        cumulative-game-stats (stats/eval-cumulative-game-stats game-stats)]
    (layout/main
     {:title (:title-tag-landing cfg/config)
      :description "Tracking progress and achievements"
      :extra-elements [profile-image-preload
                       (data/export-data game-stats "GAME_STATS_DATA")
                       (data/export-data cumulative-game-stats "CUMULATIVE_GAME_STATS_DATA")]}
      ;; NOTE: you probably meant Tailwind's `.container` class.
      ;; Use a div with the class instead of a <container> element.
     [:div.container.mx-auto.px-4.space-y-8
      (overview/render)
      (gallery-link/render)
      [:section.grid.grid-cols-1.md:grid-cols-2
       (highlights/render)
       (season-table/render game-stats time-log)]
      (game-graph/render)]
      ;; Scripts: placed after content so they run post-parse.
     (include-js (url/put-on-base "/extern/chart.js")
                 (url/put-on-base "/js/cljs_base.js")
                 (url/put-on-base "/js/landing.js")))))
