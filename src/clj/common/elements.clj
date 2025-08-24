(ns common.elements)

(defn fat-title
  "Regular title for sections to bring attention"
  [^String text]
  [:h2.text-2xl.font-semibold.font-myflame.mb-4
   text])
