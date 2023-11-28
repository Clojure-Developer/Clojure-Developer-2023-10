(ns otus-04.homework.crossword-puzzle
  (:require [clojure.string :as str]))

;; Оригинал:
;; https://www.hackerrank.com/challenges/crossword-puzzle/problem

(defn solve
  "Возвращает решённый кроссворд. Аргумент является строкой вида

  +-++++++++
  +-++++++++
  +-++++++++
  +-----++++
  +-+++-++++
  +-+++-++++
  +++++-++++
  ++------++
  +++++-++++
  +++++-++++
  LONDON;DELHI;ICELAND;ANKARA

  Все строки вплоть до предпоследней описывают лист бумаги, а символами
  '-' отмечены клетки для вписывания букв. В последней строке перечислены
  слова, которые нужно 'вписать' в 'клетки'. Слова могут быть вписаны
  сверху-вниз или слева-направо."
  [input]
  "")

(def tst "+-++++++++\n+-++++++++\n+-++++++++\n+-----++++\n+-+++-++++\n+-+++-++++\n+++++-++++\n++------++\n+++++-++++\n+++++-++++\nLONDON;DELHI;ICELAND;ANKARA")


(print tst)

(defn parse-input [input]
  (let [lines (str/split-lines input)
        field (vec (take 10 lines))
        words (vec (str/split (last lines) #";"))]
    {:field field
     :words words}))

(parse-input tst)

(defn try-place-word
  "try to greed down + right with check '-' or curr symbol"
  [field word]
  (loop [i 0
         j 0
         success? false
         acc field
         word-rest word]
    (if (= 100 (* i j))                                     ;; mb 110
      (assoc {:success? success?} :field (if success?
                                           acc
                                           field))
      ())))
;; if good cut word-rest + change acc else return acc + word-rest to initial state
;; rework field to vec of vecs cuz can  create universal "replace func"
;; mb rework words to vec of lists cuz remove first elem?