(ns otus-02.homework.pangram
  (:require [clojure.string :as string]))

;;(defn is-pangram [test-string]
;;  (let [alphabet "abcdefghijklmnopqrstuvwxyz"
;;        alph-vec (map str (vec alphabet))
;;        normal-string1 (string/lower-case test-string)
;;        normal-string (map str
;;                           (filter #(> (.indexOf alphabet (str %1)) -1)
;;                                   normal-string1))
;;        chr-in-alpha-list (map #(.indexOf normal-string %)  alph-vec)]
;;    (= (count (filter #(= % -1) chr-in-alpha-list)) 0)))

(defn is-pangram [test-string]
  (let [alphabet "abcdefghijklmnopqrstuvwxyz"
        normal-string (-> test-string
                          (string/replace #"[\s\W]" "")
                          string/lower-case)]
    (= (set alphabet) (set normal-string))))
