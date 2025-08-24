(ns pages.gallery.modal
  (:require [utils.url :as url]))

(def nav-arrow-class
  ;; Very tedious css class to ensure nice clicky navigation arrows
  "absolute top-1/2 -translate-y-1/2 w-12 h-40
  flex items-center justify-center
  bg-fg/20 text-bg/40 ring-myflame/20 ring-2
  hover:bg-myflame hover:text-bg active:bg-myflame active:text-bg
  transition-colors duration-200")

(defn render
  "A popup with large display of google content in iframe - to be manipulated using js"
  []
  [:div#gallery-modal.select-none.js-only
   {:class "fixed flex hidden
            items-center justify-center p-4
            bg-black/75  inset-0 z-50"}
   [:div.modal-content
    {:class    "relative bg-bg
                p-6 rounded-lg
                w-full lg:max-w-[70vw]
                overflow-auto"
     :onclick "event.stopPropagation()"}
    [:button#gallery-modal-future.left-4  {:class (str nav-arrow-class " left-2 rounded-tl-xl rounded-bl-xl rounded-tr-sm rounded-br-sm")}
     [:span.text-3xl "‹"]]
    [:button#gallery-modal-past.right-4 {:class (str nav-arrow-class " right-2 rounded-tl-xl rounded-bl-xl rounded-tr-sm rounded-br-sm")}
     [:span.text-3xl "›"]]
    [:div.w-full.aspect-video
     [:iframe#gallery-modal-iframe.w-full.h-full
      {:style           (str "background: url(" (url/put-on-base "/assets/favicon.svg") ") center center no-repeat;")
       :src             ""
       :allowfullscreen ""
       :title           ""}]]
    [:h3#gallery-modal-description.text-xl.font-semibold.text-myflame.mt-4 ""]
    [:p#gallery-modal-date.text-sm.text-gray-400 ""]]])
