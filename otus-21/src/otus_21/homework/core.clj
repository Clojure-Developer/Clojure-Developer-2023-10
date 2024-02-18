(ns otus-21.homework.core
    (:require [clojure.string :as str]
              [clojure.zip :as z]))

(def input "$ cd /\n$ ls\ndir a\n14848514 b.txt\n8504156 c.dat\ndir d\n$ cd a\n$ ls\ndir e\n29116 f\n2557 g\n62596 h.lst\n$ cd e\n$ ls\n584 i\n$ cd ..\n$ cd ..\n$ cd d\n$ ls\n4060174 j\n8033020 d.log\n5626152 d.ext\n7214296 k")

(def test-map
    {:a     {:e     {:i 584}
             :f     29116
             :g     2557
             :h.lst 62596}
     :b.txt 14848514
     :c.dat 8504156
     :d     {:j     4060174
             :d.log 8033020
             :d.ext 5626152
             :k     7214296}})

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

(defn find-in-lazy [lazy key]
    (first (filter #(= (first (first %)) key) lazy)))


;Maybe some is better?
(some #(when (even? %) %) [1 2 3 4])

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
        #"\$ cd /" :>> (constantly (map-zipper (z/root zipper)))
        #"\$ cd \.\." :>> (constantly (z/up zipper))
        #"\$ cd .+" :>> (partial find-direction zipper)
        #"\$ ls" :>> (constantly zipper)
        #"dir .+" :>> (partial add-dir zipper)
        #"\d+ .+" :>> (partial add-file zipper)
        ))

(defn construct-tree []
    (z/root (reduce command-dispatch (map-zipper {}) (str/split-lines input))))
(comment

    (= test-map (construct-tree))

    (-> {}
        map-zipper
        (z/append-child {:a {}})
        (z/append-child {:b.txt 14848514})
        (z/append-child [:c.dat 8504156])
        (z/append-child {:d {}})

        ;(find-direction '("$ cd a"))
        (z/down)
        (add-file '("29116 f"))
        (add-file '("2557 g"))
        (add-file '("62596 h.lst"))
        (add-dir '("dir e"))

        (add-dir '("dir e"))
        ;(z/down)
        (find-direction '("$ cd e"))
        ;(z/leftmost)
        ;(z/right)
        ;(z/right)
        ;(z/right)

        ;(#(iterate z/right %))
        #_((partial some #(when ((partial = :e) (first (first %))) %)))
        ;(find-in-lazy :e)
        )
    )

(defn sum-of-sizes [input]
    "По журналу сеанса работы в терминале воссоздаёт файловую систему
  и подсчитывает сумму размеров директорий, занимающих на диске до
  100000 байт (сумма размеров не учитывает случай, когда найденные
  директории вложены друг в друга: размеры директорий всё так же
  суммируются)."
    0)