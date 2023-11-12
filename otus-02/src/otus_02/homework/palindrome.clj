(ns otus-02.homework.palindrome
  (:require [clojure.string :as string]))

(defn polindrom-v1 [str]
  (= str (string/reverse str)))

;; Попытка реализовать оптимальный алгоритм для данной задачи,
;; в котором идет сравнение символов строки с начала с символами с конца,
;; и при первом же неудовлетворительном условии выйти с результатом false. 
(defn polindrom-v2 [input]
  (let [s (string/replace (string/lower-case input) #"[^a-z]" "")
        size (count s)
        idxs (for [i (range (/ size 2)) :while (= (nth s i) (nth s (- size i 1)))] i)
        eq-idx (if (empty? idxs) 0 (last idxs))
        size-2 (int (/ size 2))
        res (if (even? size) (= (+ eq-idx 1) size-2) (=  eq-idx size-2))]
    res))

(defn is-palindrome [test-string]
  (polindrom-v2 test-string))

(comment
  (polindrom-v2 "abba")
  (polindrom-v2 "knock on the door")

  (polindrom-v2 "") 
  (polindrom-v2 "aba")
  (polindrom-v2 "abc")

  (polindrom-v2 "abba")
  (polindrom-v2 "abc"))