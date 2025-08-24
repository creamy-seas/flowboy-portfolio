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

   ;; Hide/show sections when javascript enabled/disabled
   ;; This is done by:
   ;; 1. Annotatating js-dependent components with `js-only`  or `no-js-only`
   ;; 2. Running the script above that adds replaces html.no-js with html.js when javascript is enabled, which hides the no-js-only elements
   ;; 3. If js in not enabled, then the opposite occurs
   [:style
    "html.no-js .js-only{display:none!important}
     html.js .no-js-only{display:none!important}"]

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
  "Website name with link to root page"
  []
  [:header.text-center.my-8
   [:a.text-myflame.text-3xl.font-bold
    {:class "decoration-myflame underline-offset-4 hover:text-myflame/80 select-none"
     :href (url/put-on-base "/")}
    (:title cfg/config)]
   [:div.no-js-only.py-3
    [:strong "JavaScript is disabled - "] "some enhanced features are unavailable"]])

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
           content]]))
