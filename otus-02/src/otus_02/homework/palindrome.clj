(ns otus-02.homework.palindrome
  (:require [clojure.string :as string]))


(defn is-palindrome
  [test-string]
  (as-> test-string p
        (re-seq #"[\w+]" p)
        (string/join "" p)
        (string/lower-case p)
        (= (string/reverse p) p))) 
