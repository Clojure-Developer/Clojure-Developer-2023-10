(ns otus-02.homework.palindrome
  (:require [clojure.string :as string]))

(defn get-only-alphabets-chars [input-string]
  (string/replace input-string #"[^A-Za-z]" ""))

(defn normalize-str [input-string]
  (->> input-string
       (get-only-alphabets-chars)
       (string/lower-case)
       (string/trim)))

(defn is-palindrome [test-string]
  (let [normalized-string (normalize-str test-string)]
    (= normalized-string (string/reverse normalized-string))))
