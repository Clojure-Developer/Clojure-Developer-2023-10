(ns otus-02.homework.palindrome
    (:require [clojure.string :as string])
    (:import [java.lang Character]))

(defn convert-string-to-mass [st]
    (->> st
         (filter (fn [x] (Character/isLetter ^char x)))
         (string/join)
         (string/upper-case)))

(defn is-palindrome [test-string]                           ;; Why is there no question mark in func name?
    (let [letters-mass (convert-string-to-mass test-string)
          reversed-letter-mass (string/reverse letters-mass)]
        (= letters-mass reversed-letter-mass)))

(comment
    (convert-string-to-mass "фытак 92е2 п2 пак--п")
    (is-palindrome "as-ds?a")
    (is-palindrome "as-ds?aa"))