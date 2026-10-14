(ns utils.dom-operations)

(defn get-all
  "Return a Clojure seq of all nodes that match `selection`."
  [selection]
  (array-seq (.querySelectorAll js/document selection)))

(defn get-element-by-id [element]
  (.getElementById js/document element))

(defn add-listener
  "Hooks up a function to run when `element` send out an `event`"
  [element ^String event callback]
  (.addEventListener element event callback))

(defn add-click-listener-by-id
  "Hooks up a function to run when the elment with `element-id` is clicked"
  [^String element-id callback]
  (when-let [element (get-element-by-id element-id)]
    (add-listener element "click" callback)))
