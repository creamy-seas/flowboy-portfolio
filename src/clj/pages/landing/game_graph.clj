(ns pages.landing.game-graph
  (:require [common.elements :as common]))

(defn render
  "Canvas and button that will be filled out with graphjs"
  []
  [:section.max-w-4xl.mx-auto.js-only
   (common/fat-title "📈 Game Stats")
   [:div {:class "max-h-[1000px] min-h-[500px] relative"}
    [:canvas#game-graph.w-full]]
   [:div.flex.justify-center.mt-4
    [:button#toggle-game-graph {:class "px-4 py-2 bg-mytheme text-bg rounded hover:bg-fg transition"}
     "Show per-game"]]])
