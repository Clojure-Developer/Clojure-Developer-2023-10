(ns server.sentences
  (:require
   [clojure.string :refer [trim]]))


(defn sentence-split [text]
  (map trim (re-seq #"[^\.!\?:;]+[\.!\?:;]*" text)))


(defn is-sentence? [text]
  (re-matches #"^.*[\.!\?:;]$" text))


(defn sentence-join [x y]
  (if (is-sentence? x)
    y
    (str x " " y)))


(defn strings->sentences [strings]
  (->> (mapcat sentence-split strings)
       (reductions sentence-join)
       (filter is-sentence?)))
