(ns landing.game-graph
  (:require [landing.game-graph-config  :as cfg]
            [utils.dom-operations       :as dom]))

(defonce view-mode* (atom :cumulative))

(defonce per-game-stats*
  (js->clj (aget js/window "GAME_STATS_DATA")  :keywordize-keys true))

(defonce cumulative-game-stats*
  (js->clj (aget js/window "CUMULATIVE_GAME_STATS_DATA") :keywordize-keys true))

(defn current-stats []
  (if (= @view-mode* :cumulative)
    cumulative-game-stats*
    per-game-stats*))

(defn toggle-button []
  (let [btn (dom/get-element-by-id "toggle-game-graph")
        new-label (if (= @view-mode* :cumulative)
                    "Show per-game"
                    "Show cumulative")]
    (set! (.-textContent btn) new-label)))

(defn toggle-view!
  "Flip the view-mode and update the chart"
  [^js chart]
  (swap! view-mode* (fn [m] (if (= m :cumulative) :per-game :cumulative)))
  (let [stats (current-stats)]
    (aset (aget chart "data") "labels" (clj->js (mapv :gameNumber stats)))
    (aset (aget chart "data") "datasets" (clj->js (cfg/make-line-collection stats)))
    (.call (aget chart "update") chart)
    (.update chart)
    (toggle-button)))

(defn init-graph! []
  (let [ctx (.getContext (dom/get-element-by-id "game-graph") "2d")
        cfg (clj->js {:type "line"
                      :data {:labels   (mapv :gameNumber cumulative-game-stats*)
                             :datasets (cfg/make-line-collection cumulative-game-stats*)}
                      :options (cfg/chart-opts cumulative-game-stats*)})]
    (js/Chart. ctx cfg)))

(defn ^:export init []
  (let [chart (init-graph!)]
    (dom/add-click-listener-by-id "toggle-game-graph" #(toggle-view! chart))))

(init)
