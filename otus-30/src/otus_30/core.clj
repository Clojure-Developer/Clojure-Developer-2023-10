(ns otus-30.core
  (:gen-class)
  (:require
   [com.brunobonacci.mulog :as mulog]
   [nrepl.server :as nrepl]))


;; package otus_30;
;; public class core {
;;   public static void main(String[] var0) {
;; }}

#_(defn -main [& args]
    (println "Hello world"))



(defn start-repl-server [port]
  (nrepl/start-server
   :port port
   :bind "0.0.0.0"))


(defn -main [& args]
  (start-repl-server 9999)

  (let [stop-mulog (mulog/start-publisher!
                    {:type    :console
                     :pretty? true})]

    (mulog/log ::app-started
               :timestamp (System/currentTimeMillis)
               :message "App started"
               :level :info
               :data {:args "args"})

    (println "Hello world")

    (stop-mulog)))
