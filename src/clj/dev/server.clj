(ns dev.server
  (:require
   [utils.config              :refer [config]]
   [clojure.string            :as str]
   [ring.adapter.jetty        :refer [run-jetty]]
   [ring.middleware.file      :refer [wrap-file]]
   [ring.middleware.file-info :refer [wrap-file-info]]
   [ring.util.response        :as resp]
   [ring.middleware.not-modified :refer [wrap-not-modified]]))

(def redirect-to-root
  (resp/redirect (:base config)))

(defn wrap-prefix
  "Strips the `prefix` from uri before passing to `handler`"
  [handler prefix]
  (let [len (count prefix)]
    (fn [{:keys [uri] :as req}]
      (when (str/starts-with? uri prefix)
        (let [sub (subs uri len)
              sub (if (or (str/blank? sub) (= sub "/"))
                    "/index.html"
                    sub)]
          (handler (assoc req :uri sub)))))))

(def static-handler
  (-> (constantly nil)
      (wrap-file "resources/public")
      ^{:clj-kondo/ignore [:deprecated-var]} wrap-file-info
      wrap-not-modified))

(def prefixed-static
  (wrap-prefix static-handler (:base config)))

(defn app [req]
  (or (prefixed-static req)
      redirect-to-root))

(defn -main [& _]
  (run-jetty app {:port 5173 :join? true}))
