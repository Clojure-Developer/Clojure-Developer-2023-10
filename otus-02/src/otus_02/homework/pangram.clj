(ns otus-02.homework.pangram
  (:require [clojure.string :as string])
  (:use [otus-02.homework.palindrome :only [convert-string-to-mass]]))


(defn is-pangram [test-string]
  (= 26
     (-> test-string
         (convert-string-to-mass)
         (set)
         (count))))

(comment
  (set "asd")
  (is-pangram "asd"))