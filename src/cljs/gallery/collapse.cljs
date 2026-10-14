(ns gallery.collapse
  (:require [utils.dom-operations :as dom]))

(defn open!   [el] (.add    (.-classList el) "collapse-open"))
(defn close!  [el] (.remove (.-classList el) "collapse-open"))
(defn toggle! [el] (.toggle (.-classList el) "collapse-open"))

(defn click-handler
  "Closes all the collapses **except** for the one that was clicked the closest"
  [event]
  (let [clicked-box (.closest
                     (.-currentTarget event) ".collapse[gallery-season-key]")]
    (doseq [box (dom/get-all ".collapse[gallery-season-key]")
            :when (not= box clicked-box)]
      (close! box))
    (toggle! clicked-box)))

(defn on-load
  "If ?season=2023-2024 is present in url args, open that collapse"
  []
  (when-let [season (.get (js/URLSearchParams. (.-search js/location)) "season")]
    (when-let [el (.querySelector js/document
                                  (str ".collapse[gallery-season-key=\"" season "\"]"))]
      (open! el))))

(defn ^:export init []
  (doseq [el (dom/get-all ".collapse-title")]
    (dom/add-listener el "click" click-handler))
  (on-load))

(init)
