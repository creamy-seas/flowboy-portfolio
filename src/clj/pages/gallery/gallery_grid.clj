(ns pages.gallery.gallery-grid
  (:require [utils.data.gallery :as gallery]))

(defn gallery-card-js
  "Single media element in grid view - interaction handled with a gallery-card listener"
  [{:keys [thumbnail description date gallery-idx]}]
  [:div.gallery-card.cursor-pointer {:gallery-idx gallery-idx}
   [:img.w-full.h-32.object-cover.rounded-lg {:src thumbnail :alt description}]
   [:p.text-sm.text-center.mt-2 description]
   [:p.text-xs.text-center.text-gray-400 date]])

(defn render-js
  "Grid of media elements passed in as list in argument.
  They are grouped by season and placed in a collapsible element"
  [gallery-data]
  [:section#gallery-grid.js-only.container.select-none
   (for [[season items] (gallery/group-gallery gallery-data)]
     [:div.collapse.collapse-arrow.rounded-none.rounded-t-lg {:gallery-season-key season}
      [:summary {:class "collapse-title text-xl font-semibold bg-mytheme/80 text-bg"} season]
      [:div.collapse-content.p-2
       [:div.grid.grid-cols-2.sm:grid-cols-3.md:grid-cols-4.gap-4
        (map gallery-card-js items)]]])])

(defn gallery-card-no-js
  "With js disabled, the link is just opened in new tab"
  [{:keys [thumbnail description date src]}]
  [:a {:href src :target "_blank" :rel "noopener noreferrer" :title description}
   [:img.w-full.h-32.object-cover.rounded-lg {:src thumbnail :alt description}]
   [:p.text-sm.text-center.mt-2 description]
   [:p.text-xs.text-center.text-gray-400 date]])

(defn render-no-js
  "As JS is disabled - uses the default daisy-ui hiding mechanism"
  [gallery-data]
  [:section.no-js-only.container.select-none.space-y-4
   (for [[season items] (gallery/group-gallery gallery-data)]
     [:details.collapse.collapse-arrow.rounded-none.rounded-t-lg {:key season}
      [:summary {:class "collapse-title text-xl font-semibold bg-mytheme/80 text-bg"} season]
      [:div.collapse-content.p-2
       [:div.grid.grid-cols-2.sm:grid-cols-3.md:grid-cols-4.gap-4
        (map gallery-card-no-js items)]]])])

(defn render [gallery-data]
  (list (render-js gallery-data)
        (render-no-js gallery-data)))
