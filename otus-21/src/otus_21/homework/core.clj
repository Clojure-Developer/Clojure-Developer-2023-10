(ns otus-21.homework.core
  (:require [clojure.zip :as z]
            [clojure.walk :as w]
            [clojure.string :as s]))


(defn map-zipper [m]
  (z/zipper
    (fn [x] (or (map? x) (map? (nth x 1))))
    (fn [x] (seq (if (map? x) x (nth x 1))))
    (fn [x children]
      (if (map? x)
        (into {} children)
        (assoc x 1 (into {} children))))
    m))

(defn insert-dir [loc name]
  (z/append-child loc {name {}}))

(defn insert-file [loc name size]
  (z/append-child loc [name (parse-long size)]))

(defn dir-loc [name loc]
  (let [[key value] (z/node loc)]
    (if (and (= name key) (map? value)) loc)
    ))
(defn nav-to-dir [loc name]
  (some #(dir-loc name %) (->> loc z/down (iterate z/right))))
(defn parse-command
  [loc input]
  (condp re-find input
    #"\$ cd /" (map-zipper {"/" {}})
    #"\$ ls" loc
    #"dir (.+)" :>> (fn [[_ name]] (insert-dir loc name))
    #"(\d+) (.+)" :>> (fn [[_ size name]] (insert-file loc name size))
    #"cd \.\." (z/up loc)
    #"cd (.+)" :>> (fn [[_ name]] (nav-to-dir loc name))))


(defn parse [input]
  (->> input
       s/split-lines
       (reduce parse-command nil)
       z/root))

(defn dir-in-walk? [x]
  ;  [a {e {i 584}, f 29116, g 2557, h.lst 62596}]
  (if (and (vector? x) (= 2 (count x)) (map? (second x))) true)
  )
(defn sum-of-size-dirs [input max-dir-size]
  (let [result (atom 0)]
    (w/postwalk (fn [x]
                  (if (dir-in-walk? x)
                    (let
                      [name (nth x 0)
                       content (nth x 1)
                       size (apply + (vals content))]
                      (if (<= size max-dir-size) (swap! result #(+ size %)))
                      ;(println "dir " name "size" size ":" content)
                      [name size]
                      )
                    x
                    )
                  )
                (parse input))
    @result))

(defn sum-of-sizes [input]
  "По журналу сеанса работы в терминале воссоздаёт файловую систему
и подсчитывает сумму размеров директорий, занимающих на диске до
100000 байт (сумма размеров не учитывает случай, когда найденные
директории вложены друг в друга: размеры директорий всё так же
суммируются)."
  (sum-of-size-dirs input 100000))