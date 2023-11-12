(ns otus-02.homework.palindrome
  (:require [clojure.string :as string]))

(defn is-palindrome [test-string] (->> test-string string/lower-case (filter #(Character/isLetter %)) (#(= % (reverse %)))))

