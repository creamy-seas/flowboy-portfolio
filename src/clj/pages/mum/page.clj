(ns pages.mum.page
  (:import [java.net URLEncoder])
  (:require
   [common.layout                  :as layout]
   [data.game-stats                :as data]
   [utils.date                     :as date]))

(defn- u [s] (URLEncoder/encode (str s) "UTF-8"))

(defn gmap-directions-url [dest]
  (str "https://www.google.com/maps/dir/?api=1"
       "&origin="      (u "Egham")
       "&destination=" (u dest)
       "&travelmode="  (u "driving")))

(defn hide-past-rows
  "In place as it is lightweight. Ensures client side update"
  []
  [:script
   "var today = new Date();
    var cutoff = today.getTime();
    document.querySelectorAll('tr.future-only').forEach(function(tr){
        var ms = new Date(tr.dataset.date).getTime();
        if (ms < cutoff) tr.hidden = true;
    });"])

(defn render []
  (let [game-data (data/read-game-prep)]
    (layout/main
     {:title "Mum's page"
      :description "Just for mum!"}
     [:table.table.table-compact.w-full.text-center
      [:thead {:class "bg-mytheme/90"}
       (into [:tr]
             (map-indexed (fn [idx heading]
                            [:th.text-left {:key idx} heading])
                          ["Date" "Location" "Face Off" "Arrangement"]))]
      (into [:tbody]
            (map-indexed (fn [idx {:keys [date location faceOff departTime]}]
                           [:tr.text-left.future-only {:key idx :data-date date}
                            [:td.font-medium.text-mytheme
                             (date/cast-date date "d MMM (EEEE)")]
                            [:td
                             [:a.underline.hover:text-mytheme
                              {:href (gmap-directions-url (str location " ice rink"))
                               :target "_blank"
                               :rel "noopener noreferrer"}
                              location]]
                            [:td faceOff]
                            [:td.text-mytheme departTime]])
                         game-data))]
     (hide-past-rows))))
