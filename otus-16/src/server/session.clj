(ns server.session
  (:require
   [schejulure.core :refer [schedule]]
   [flatland.useful.map :refer [remove-vals]]))


(def last-session-id
  (atom 0))


(defn next-session-id []
  (swap! last-session-id inc))


(def sessions
  (atom {}))


(defn now []
  (System/currentTimeMillis))


(defn new-session [initial]
  (let [session-id (next-session-id)
        session    (assoc initial :last-referenced (atom (now)))]
    (swap! sessions assoc session-id session)
    session-id))


(defn get-session [id]
  (let [session (@sessions id)]
    (reset! (:last-referenced session) (now))
    session))


(defn session-expiry-time []
  (- (now) (* 10 60 1000)))


(defn expired? [session]
  (< @(:last-referenced session) (session-expiry-time)))


(defn sweep-sessions []
  (swap! sessions #(remove-vals % expired?)))


(def session-sweeper
  (schedule {:min (range 0 60 5)} sweep-sessions))
