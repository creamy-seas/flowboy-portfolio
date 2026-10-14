(ns common.layout
  (:require
   [hiccup.page  :refer [html5 include-css]]
   [utils.url    :as url]
   [utils.config :as cfg]))

(def no-js-toggle [:script
                   (cond-> "(function(){var d=document.documentElement;if(d.classList){d.classList.remove('no-js');d.classList.add('js');}else{d.className=d.className.replace(/\\bno-js\\b/,'js');}})();"
                     (:csp-nonce cfg/config) (vector :nonce (:csp-nonce cfg/config)))])

(defn head
  "Head of the page, with `title`, `descriptions`, `canonical-path` of the page and optional `extra-elements`"
  [{:keys [title description canonical-path extra-elements]
    :or   {extra-elements []}}]
  [:head
   [:link {:rel "prefetch"
           :as "image"
           :href (url/put-on-base "/assets/brother-to-brother.svg")}]

   ;; Tells search engines the preferred URL to index, avoiding duplicate pages
   ;; caused by query parameters such as ?season=2025-2026.
   [:link {:rel "canonical"
           :href (url/absolute-url canonical-path)}]

   ;; Open Graph metadata for social media link previews
   [:meta {:property "og:title" :content title}]
   [:meta {:property "og:description" :content description}]
   [:meta {:property "og:type" :content "website"}]
   [:meta {:property "og:url"
           :content (url/absolute-url canonical-path)}]
   [:meta {:property "og:image"
           :content (:open-graph-img cfg/config)}]

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
   [:base {:href ""}]
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
  [:header {:class "grid items-center gap-2 px-4 py-3
                   grid-cols-1 md:grid-cols-[1fr_auto_1fr]"}
   [:a {:href (:brother-link cfg/config)
        :class "justify-self-center md:justify-self-start"}
    [:div#to-brother]]
   [:a.my-hover-link {:href (url/put-on-base "/")
                      :class "justify-self-center
               text-3xl font-bold
               underline-offset-4 select-none"}
    (:title cfg/config)]
   [:div.flex.items-center.gap-4.justify-self-center.md:justify-self-end
    [:div#lang]
    [:a {:href (str "https://www.instagram.com/" (:instagram cfg/config))
         :target "_blank"
         :rel "noopener noreferrer"}
     [:img.w-10.h-10
      {:src (url/put-on-base "/assets/instagram.svg")
       :alt "Instagram"}]]]])

(def js-warning
  [:div.no-js-only.py-3.text-center
   [:strong "JavaScript is disabled - "] "some enhanced features are unavailable"])

(defn main
  "Main layout of the app - everything should inherit from here"
  [{:keys [title description canonical-path extra-elements]} & content]
  (html5 {:class "no-js"}
         (head {:title            title
                :description      description
                :canonical-path   (or canonical-path "/")
                :extra-elements   extra-elements})
         [:body
          [:div#root
           (header)
           js-warning
           content]]))
