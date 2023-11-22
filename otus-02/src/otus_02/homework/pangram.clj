(ns otus-02.homework.pangram
  (:require [clojure.string :as string]))

(def alphabet-set
  (set (map char (range (int \a) (inc (int \z))))))

(defn get-only-alphabets-chars [input-string]
  (string/replace input-string #"[^A-Za-z]" ""))

(defn normalize-str [input-string]
  (->> input-string
       (get-only-alphabets-chars)
       (string/lower-case)
       (string/trim)))

(defn is-pangram [test-string]
  (= alphabet-set (set (normalize-str test-string))))
