(ns common.layout
  (:require
   [hiccup.page  :refer [html5 include-css]]
   [utils.url    :as url]
   [utils.config :as cfg]))

(def no-js-toggle [:script
                   (cond-> "(function(){var d=document.documentElement;if(d.classList){d.classList.remove('no-js');d.classList.add('js');}else{d.className=d.className.replace(/\\bno-js\\b/,'js');}})();"
                     (:csp-nonce cfg/config) (vector :nonce (:csp-nonce cfg/config)))])

(defn head
  "Head of the page, with `title`, `descriptions` and optional `extra-elements`"
  [{:keys [title description extra-elements]
    :or   {extra-elements []}}]
  [:head
   ;; Flip <html> class from no-js -> js ASAP (runs before first paint) if js is enabled
   [:script "(d=>d.classList.replace('no-js','js'))(document.documentElement)"]

   [:style
    (str
     ;; Hide/show sections when javascript enabled/disabled
     ;; This is done by:
     ;; 1. Annotatating js-dependent components with `js-only`  or `no-js-only`
     ;; 2. Running the script above that adds replaces html.no-js with html.js when javascript is enabled, which hides the no-js-only elements
     ;; 3. If js in not enabled, then the opposite occurs
     "html.no-js .js-only{display:none!important}"
     "html.js .no-js-only{display:none!important}"

     ;; Styles for the brother hyperlink
     "#to-brother{width:100px;height:92px;background-repeat:no-repeat;"
     "background-position:center;background-size:100px 92px;"
     "background-image:url('" (url/put-on-base "/assets/brother-me.svg") "');}"
     "#to-brother:hover{background-image:url('" (url/put-on-base "/assets/brother-to-brother.svg") "');}")]

   ;; --- Meta & base ---
   [:meta {:charset "UTF-8"}]
   [:base {:href (:base cfg/config)}]
   [:title title]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
   [:meta {:name "description" :content description}]
   [:link {:rel  "icon"
           :type "image/svg+xml"
           :href (url/put-on-base "/assets/favicon.svg")}]

   ;; --- Styles ---
   (include-css (url/put-on-base "css/style.css"))

   ;; --- Third-party (async, won’t block paint) ---
   [:script {:src "//gc.zgo.at/count.js"
             :data-goatcounter (:goat-counter-url cfg/config)
             :async true}]

   ;; Page-specific extras (e.g., preloads, exported data blobs)
   (for [p extra-elements] p)])

(defn header
  "Website name with link to root page and brother website"
  []
  [:header {:class "grid items-center grid-cols-[1fr_auto_1fr] gap-2 px-4 py-3"}
   [:a.justify-self-start {:href (:brother-link cfg/config)}
    [:div#to-brother]]
   [:a {:href  (url/put-on-base "/")
        :class "justify-self-center
                text-mytheme text-3xl font-bold
                underline-offset-4
                hover:text-mytheme/80
                select-none"}
    (:title cfg/config)]
   [:div#lang.justify-self-end]])

(def js-warning
  [:div.no-js-only.py-3.text-center
   [:strong "JavaScript is disabled - "] "some enhanced features are unavailable"])

(defn main
  "Main layout of the app - everything should inherit from here"
  [{:keys [title description extra-elements]} & content]
  (html5 {:class "no-js"}
         (head {:title            title
                :description      description
                :extra-elements   extra-elements})
         [:body
          [:div#root
           (header)
           js-warning
           content]]))
