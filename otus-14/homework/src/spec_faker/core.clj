(ns spec-faker.core
    (:require [cheshire.core :refer :all]
              [clojure.spec.gen.alpha :as gen]
              [compojure.core :refer :all]
              [hiccup.core :refer [html]]
              [ring.adapter.jetty :refer [run-jetty]]
              [ring.middleware.json :refer [wrap-json-response]]
              [ring.middleware.keyword-params :refer :all]
              [ring.middleware.params :refer :all]
              [ring.util.response :refer [response]]
              [spec-faker.generators :refer [generators]])  ;TODO clean
    (:gen-class))

(comment
    {:key1 "spec-string"
     :key2 "spec-int64"
     :key3 "spec-bool"})
(def current-spec (atom {}))

(defn change-spec [request]
    (reset! current-spec (request :body))
    (response "OK"))

(defn generate-spec
    ([]
     (response (zipmap (keys @current-spec)
                       (map (comp gen/generate generators)
                            (vals @current-spec)))))
    ([json-string]
     (clojure.pprint/pprint json-string)
     (response (zipmap (keys (cheshire.core/decode json-string))
                       (map (comp gen/generate generators)
                            (vals (cheshire.core/decode json-string)))))))
(defroutes handler
           (context "/spec" []
               (GET "/" request (generate-spec (get-in request [:params :say])))
               (POST "/" request (change-spec request))
               (GET "/html-test" _ (html [:form {:action "http://localhost:8000/spec" :method "GET"}
                                          [:div
                                           [:label {:for "say"} "What greeting do you want to say?"]
                                           [:input#say {:name "say" :value "Hi"}]]
                                          [:button.btn.btn-primary {:type "submit"}
                                           "Submit"]]))))
(defn wrap-print-response [handler]
    (fn
        ([request]
         (print request)
         (handler request))))
(def app (-> handler
             ;wrap-json-body
             wrap-json-response
             ;wrap-json-params
             wrap-keyword-params
             wrap-params
             ;wrap-print-response
             ))

(def server (run-jetty #'app {:port 8000 :join? false}))
(comment
    (.stop server))
