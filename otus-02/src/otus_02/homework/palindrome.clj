(ns otus-02.homework.palindrome
  (:require [clojure.string :as string]))

(defn is-palindrome [test-string]
  (let [normal-str (string/trim (string/upper-case (string/replace test-string #"\W" "")))
        invert-str (string/reverse (string/trim (string/upper-case (string/replace test-string #"\W" ""))))] (= normal-str invert-str)))
 
