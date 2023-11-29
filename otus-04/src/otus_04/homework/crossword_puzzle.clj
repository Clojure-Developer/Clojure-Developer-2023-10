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
          field (vec (map vec (take 10 lines)))
          words (vec (str/split (last lines) #";"))]
        {:field field
         :words words}))

(parse-input tst)

(defn matrix-assoc
    "return copy of matrix, where elem set in i, j position"
    [matrix i j elem]
    (assoc matrix i (assoc (get matrix i) j elem)))

(defn position-suitable?
    [matrix i j elem]
    (or (= \- (get-in matrix [i j]))
        (= elem (get-in matrix [i j])))) ;;is copy-paste of (get-in acc [i j]) ok, or need to use let?

(defn try-place-word
    "try to greed (or down right) with check (or '-' curr symbol)"
    [field word]
    (let [directions {:undef {:down {:i 1 :j 0} :right {:i 0 :j 1}} ;; can assoc :undef by function
                      :right {:i 0 :j 1}
                      :down  {:i 1 :j 0}}]
        (loop [i 0
               j 0
               success? false                               ;; is it ok name for not function but variable?
               acc field
               word-rest word
               direction :undef]                            ;; TODO add some data spec for keywords?
            (if (= 100 (* i j))                             ;; mb 110
                (assoc {:success? success?} :field (if success?
                                                       acc
                                                       field))
                (if (position-suitable? acc i j (first word-rest))
                    (if (= direction :undef)
                        (cound
                            ())))))))
;; if good cut word-rest + change acc else return acc + word-rest to initial state
;; rework field to vec of vecs cuz can  create universal "replace func"
;; mb rework words to vec of lists cuz remove first elem?
;; need to mem direction, otherwise "ladder" can be produced
;; in origin mentioned that words can have length 1. Needs work?