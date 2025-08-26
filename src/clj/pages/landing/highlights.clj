(ns pages.landing.highlights
  (:require [utils.date       :as date]
            [data.highlights  :as highlights]
            [common.elements  :as common]
            [utils.url        :as url]))

(defn highlight-entry
  "Single highlight entry - if there is an associated gallery entry, provide link :)"
  [idx {:keys [date highlight age gallery-idx]}]
  (let [content [:li.mb-2 {:key idx}
                 [:div.font-semibold
                  (str "Age " age " - ")
                  [:span.italic (date/cast-date date "MMMM yyyy")]]
                 [:div.text-sm highlight]]]
    (if gallery-idx
      [:a
       {:href (url/put-on-base (str "gallery?gallery-idx=" gallery-idx))
        :class "text-mytheme hover:text-mytheme/80"}
       content]
      content)))

(defn render
  "Timeline of big events!"
  []
  (let [highlights (highlights/read-highlights)]
    [:section.p-2.rounded-lg.overflow-auto
     (common/fat-title "🎉 Highlights")
     (into [:ul.p-4]
           (map-indexed highlight-entry highlights))]))
