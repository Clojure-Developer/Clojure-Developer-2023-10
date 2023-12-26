(ns server.core
  (:require
   [clojure.edn :as edn]
   [server.sentences :refer [strings->sentences]]
   [server.charset :refer [wrap-charset]]
   [server.translate :refer [translate]]
   [server.session :refer [new-session get-session]]
   [compojure.core :refer [defroutes GET POST PUT routes context]]
   [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
   [ring.util.response :refer [response]]
   [ring.adapter.jetty :refer [run-jetty]]))


(defn create-session []
  (let [snippets     (repeatedly promise)
        translations (delay
                      (map translate (strings->sentences (map deref snippets))))]
    (new-session
     {:snippets     snippets
      :translations translations})))


(defn accept-snippet [session n text]
  (deliver (nth (:snippets session) n) text))


(defn get-translation [session n]
  @(nth @(:translations session) n))


(defroutes app-routes
  (POST "/session/create" []
    (response (str (create-session))))

  (context "/session/:session-id" [session-id]
    (let [session (get-session (edn/read-string session-id))]
      (routes
       (PUT "/snippet/:n" [n :as {:keys [body]}]
         (accept-snippet session (edn/read-string n) (slurp body))
         (response "OK"))

       (GET "/translation/:n" [n]
         (response (get-translation session (edn/read-string n))))))))


(defn -main [& args]
  (-> (wrap-defaults app-routes api-defaults)
      (wrap-charset)
      (run-jetty {:port 3000})))
