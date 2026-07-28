(ns pages.landing.highlights
  (:require [utils.date       :as date]
            [data.highlights  :as highlights]
            [common.elements  :as common]
            [utils.url        :as url]))

(defn highlight-entry
  "Single timeline highlight."
    [idx total {:keys [date highlight age gallery-idx]}]
  [:li {:key idx}

   (when (pos? idx)
     [:hr.bg-fg.opacity-20])

   [:div.timeline-middle
    [:span.block.h-3.w-3.rounded-full.bg-mytheme]]

   [:div.timeline-end.pl-2.mb-5.w-full
    (let [content
          [:div
           [:div.flex.items-center.gap-2
            [:span.font-mono.font-semibold age]
            [:span.text-sm.text-white.opacity-60
             (str "(" (date/cast-date date "MMMM yyyy") ")")]]

           [:div.mt-1.text-sm
            highlight]]]

      (if gallery-idx
        [:a.my-hover-link
         {:href (url/put-on-base
                 (str "gallery?gallery-idx=" gallery-idx))}
         content]
        content))]

   (when (< idx (dec total))
     [:hr.bg-fg.opacity-20])])

(defn render
  "Timeline of big events."
  []
  (let [highlights (highlights/read-highlights)
        total      (count highlights)]
    [:section.p-2
     (common/fat-title "🎉 Highlights")

     [:ul.timeline.timeline-vertical.timeline-compact
      {:class "timeline-snap-icon max-h-96 overflow-y-auto pr-2"}

      (doall
       (map-indexed
        (fn [idx entry]
          (highlight-entry idx total entry))
        highlights))]]))
