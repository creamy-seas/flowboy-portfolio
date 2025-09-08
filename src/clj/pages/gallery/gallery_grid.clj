(ns pages.gallery.gallery-grid
  (:require [data.gallery :as gallery]))

(defn gallery-card-js
  "Single media element in grid view - interaction handled with a gallery-card listener"
  [{:keys [thumbnail description date gallery-idx]}]
  [:div.gallery-card {:gallery-idx gallery-idx}
   [:img {:src thumbnail :alt description}]
   [:p.description description]
   [:p.date date]])

(defn render-js
  "Grid of media elements passed in as list in argument.
  They are grouped by season and placed in a collapsible element"
  [gallery-data]
  [:section#gallery-grid.js-only.container.select-none.my-collapse
   (for [[season items] (gallery/group-gallery gallery-data)]
     [:div.collapse.collapse-arrow {:gallery-season-key season}
      [:summary.collapse-title season]
      [:div.collapse-content
       [:div.grid
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
  [:section.no-js-only.container.select-none.space-y-4.my-collapse
   (for [[season items] (gallery/group-gallery gallery-data)]
     [:details.collapse.collapse-arrow {:key season}
      [:summary.collapse-title season]
      [:div.collapse-content
       [:div.grid
        (map gallery-card-no-js items)]]])])

(defn render [gallery-data]
  (list (render-js gallery-data)
        (render-no-js gallery-data)))
