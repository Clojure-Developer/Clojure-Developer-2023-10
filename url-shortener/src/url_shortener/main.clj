(ns url-shortener.main
  (:require [ring.adapter.jetty :as jetty]
            [url-shortener.web :as web])
  (:import [org.eclipse.jetty.server Server]))

(set! *warn-on-reflection* true)

(defn start-server
  ([] (start-server {}))
  ([opts]
   (let [server (jetty/run-jetty #'web/handler opts)]
     (println "Server started on port:" (:port opts 80))
     server)))

(defn stop-server [server]
  (.stop ^Server server)
  (println "Server stopped"))

(defn -main [& _]
  (let [server (start-server)]
    (.addShutdownHook
     (Runtime/getRuntime)
     (Thread. ^Runnable #(stop-server server)))))

(comment
  (def server (start-server {:port 8000 :join? false}))
  (stop-server server)
  
  (require '[clojure.java.shell :refer [sh]])
  
  (sh "curl" "-X" "POST"
      "-H" "Content-Type: application/json"
      "http://localhost:8000/"
      "-d" "{\"url\": \"https://clojurescript.org/\"}")
  
  (sh "curl" "-X" "PUT"
      "-H" "Content-Type: application/json"
      "http://localhost:8000/clj"
      "-d" "{\"url\": \"https://clojure.org/\"}")
  
  (sh "curl" "-i" "http://localhost:8000/clj")
  
  (sh "curl" "http://localhost:8000/list/")
  )


