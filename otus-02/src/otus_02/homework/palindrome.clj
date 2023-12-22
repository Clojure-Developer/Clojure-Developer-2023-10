(ns otus-02.homework.palindrome
  (:require [clojure.string :as string]))

(defn is-palindrome [test-string]
  (let [normal-str (string/upper-case (string/replace test-string #"[^a-zA-Z0-9]" ""))
        invert-str (string/reverse normal-str)] (= normal-str invert-str)))

