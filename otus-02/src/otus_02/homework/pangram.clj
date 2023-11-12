(ns otus-02.homework.pangram
  (:require [clojure.string :as string]))

(defn is-pangram [test-string] (->> test-string string/lower-case (filter #(Character/isLetter %)) (map int) (into #{}) count (= 26)))
