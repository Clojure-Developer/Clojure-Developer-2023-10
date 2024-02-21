(ns otus-21.homework.core
    (:require [clojure.string :as str]
              [clojure.walk :as w]
              [clojure.zip :as z]))

(def input "$ cd /
$ ls
dir a
14848514 b.txt
8504156 c.dat
dir d
$ cd a
$ ls
dir e
29116 f
2557 g
62596 h.lst
$ cd e
$ ls
584 i
$ cd ..
$ cd ..
$ cd d
$ ls
4060174 j
8033020 d.log
5626152 d.ext
7214296 k")

(defn map-zipper [m]
    (z/zipper
        (fn [x] (or (map? x) (map? (nth x 1))))
        (fn [x] (seq (if (map? x) x (nth x 1))))
        (fn [x children]
            (if (map? x)
                (into {} children)
                (assoc x 1 (into {} children))))
        m))

(defn spaces-split [s]
    (str/split s #" "))

(def extract-arg-as-kw
    (comp keyword last spaces-split first))

(def extract-file-size
    (comp parse-long first spaces-split first))

;Maybe some is better?
(defn find-in-lazy [lazy key]
    (first (filter #(= (first (first %)) key) lazy)))

(defn find-direction [zipper re-seq]
    (let [folder-kw (extract-arg-as-kw re-seq)
          curr-layer (iterate z/right (z/down zipper))
          direction (find-in-lazy curr-layer folder-kw)]
        direction))

(defn add-dir [zipper re-seq]
    (let [folder-kw (extract-arg-as-kw re-seq)]
        (z/append-child zipper {folder-kw {}})))

(defn add-file [zipper re-seq]
    (let [file-kw (extract-arg-as-kw re-seq)
          size (extract-file-size re-seq)]
        (z/append-child zipper {file-kw size})))

(defn command-dispatch [zipper command]
    (condp (comp seq re-seq) command
        #"\$ cd /" (map-zipper (z/root zipper))
        #"\$ cd \.\." (z/up zipper)
        #"\$ cd .+" :>> (partial find-direction zipper)
        #"\$ ls" zipper
        #"dir .+" :>> (partial add-dir zipper)
        #"\d+ .+" :>> (partial add-file zipper)
        ))

(defn construct-tree [input]
    (z/root (reduce command-dispatch (map-zipper {}) (str/split-lines input))))
(defn evaluate [acc node]
    (if (map? node)
        (let [sum (reduce + (vals node))]
            (if (< sum 100000)
                (swap! acc + sum))
            sum)
        node))

(defn sum-of-sizes [input]
    "По журналу сеанса работы в терминале воссоздаёт файловую систему
  и подсчитывает сумму размеров директорий, занимающих на диске до
  100000 байт (сумма размеров не учитывает случай, когда найденные
  директории вложены друг в друга: размеры директорий всё так же
  суммируются)."
    (let [tree (construct-tree input)
          acc (atom 0)]
        (w/postwalk (partial evaluate acc) tree)
        @acc))

(comment
    (sum-of-sizes input))