(ns pages.gallery.modal
  (:require [utils.url :as url]))

(def nav-arrow-class
  ;; Very tedious css class to ensure nice clicky navigation arrows
  "flex-1 h-8
   flex items-center justify-center
   bg-fg/20 text-bg/60 ring-mytheme/20 ring-2
   hover:bg-mytheme hover:text-bg
   active:bg-mytheme active:text-bg
   transition-colors duration-200
   rounded-lg")

(defn render
  "A popup with a large display of gallery media."
  []
  [:div#gallery-modal.select-none.js-only
   {:class "fixed flex hidden
            items-center justify-center
            p-0 sm:p-4
            bg-black/75 inset-0 z-50"}

   [:div.modal-content
    {:class "bg-bg
             m-3 sm:p-3
             rounded-lg
             w-full lg:max-w-[67vw]
             max-h-screen overflow-auto"
     :onclick "event.stopPropagation()"}

    [:div.w-full.aspect-video.overflow-hidden
     [:img#gallery-modal-image.hidden.w-full.h-full.object-contain
      {:src ""
       :alt ""}]

     [:video#gallery-modal-video.hidden.w-full.h-full.object-contain
      {:src ""
       :controls true
       :autoplay true
       :playsinline true}]

     [:iframe#gallery-modal-iframe.hidden.block.w-full.h-full.border-0
      {:style (str "background: url("
                   (url/put-on-base "/assets/favicon.svg")
                   ") center center no-repeat;")
       :src ""
       :allow "autoplay; fullscreen; picture-in-picture"
       :allowfullscreen ""
       :title ""}]]

    [:div.flex.gap-4.mt-3
     [:button#gallery-modal-future
      {:class nav-arrow-class
       :aria-label "Show newer gallery item"}
      [:span.text-3xl "‹"]]

     [:a#gallery-modal-instagram
      {:class "flex items-center justify-center w-10 shrink-0 invisible"
       :target "_blank"
       :rel "noopener noreferrer"}
      [:img.w-8.h-8 {:src "/assets/instagram-heart.svg"}]]

     [:button#gallery-modal-past
      {:class nav-arrow-class
       :aria-label "Show older gallery item"}
      [:span.text-3xl "›"]]]

    [:h3#gallery-modal-description.text-xl.font-semibold.text-mytheme.mt-4 ""]

    [:p#gallery-modal-date.text-sm.text-gray-400 ""]]])
