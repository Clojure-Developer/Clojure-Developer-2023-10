(ns user
  (:import [org.slf4j.simple SimpleLogger]))

(System/setProperty SimpleLogger/DEFAULT_LOG_LEVEL_KEY "warn")

(defonce *server (atom nil))

(require '[url-shortener.main :as main])

(defn start-server []
  (reset! *server (main/start-server {:port 8000 :join? false})))

(defn stop-server []
  (when @*server
    (main/stop-server @*server)
    (reset! *server nil)))

(comment
  (start-server)
  (stop-server))


(comment ; test web api
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

  (sh "curl" "http://localhost:8000/list/"))
