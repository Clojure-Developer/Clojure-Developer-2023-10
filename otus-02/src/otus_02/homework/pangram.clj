(ns otus-02.homework.pangram
  (:require [clojure.string :as string]))

(def alphabet "abcdefghijklmnopqrstuvwxyz")

(defn is-pangram [test-string]

  (= alphabet
     (-> test-string
         (string/lower-case)
         (string/replace #"[\W]{1,}" "")
         (set)
         (string/join))))

