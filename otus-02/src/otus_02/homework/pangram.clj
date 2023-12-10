(ns otus-02.homework.pangram
  (:require [clojure.string :as string]))


(defn is-pangram
  [test-string]
  (as-> test-string p
    (string/lower-case p)
    (re-seq #"[\w+]" p)
    (set p)
    (string/join "" p)
    (= (count p) 26)))
