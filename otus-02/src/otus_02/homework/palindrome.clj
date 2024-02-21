(ns otus-02.homework.palindrome
  (:require [clojure.string :as string]))

(defn clean [test-string]
  (-> test-string
      (string/trim)
      (string/lower-case)
      (string/replace #"[\W]{1,}", "")))

(defn is-palindrome [test-string]
  (let [clean-string (clean test-string)]
    (=
     clean-string
     (string/reverse clean-string))))



