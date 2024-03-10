(ns otus-21-homework.core
  (:require [clojure.string :as str])
  (:require [clojure.zip :as zip]))

(def example
  "$ cd /
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
7214296 k
")

;; zipper tools

(defn map-zipper 
  "zipper для мап"
  [m]
  (zip/zipper
   (fn [x] (or (map? x) (map? (nth x 1))))
   (fn [x] (seq (if (map? x) x (nth x 1))))
   (fn [x children]
     (if (map? x)
       (into {} children)
       (assoc x 1 (into {} children))))
   m))

(defn iter-zip 
  "итератор для зиппера"
  [z]
  (->> z
       (iterate zip/next)
       (take-while (complement zip/end?))))


;; функции для конвертации потока команд в дерево

(defn find-node 
  "находим на текущем уровне ноду по ключу и перемещаем туда зиппер "
  [key-node loc]
  (->> loc
       (iterate zip/right)
       (drop-while #(not= (first (zip/node %)) key-node))
       first))

(defn read-dir 
  "читаем содержимое директории в мапу"
  [stream]
  (->> (take-while #(not= (first %) \$) (next stream))
       (map (fn [x]
              (let [[b a] (str/split x #" ")]
                (if (= b "dir") {a {}} {a (Integer/parseInt b)}))))
       (into {})))


(defn fill-empty-dir 
  "замещаем пустую мапу непустой"
  [loc stream]
  (-> loc
      (zip/replace {(key (zip/node loc)) (read-dir stream)})
      zip/up
      zip/down
      zip/down))

(defn action 
  "читаем текущее значение из потока и выполняем действие"
  [loc stream]
  (let [[_ a b] (str/split (first stream) #" ")]
    (if (= a "ls") (fill-empty-dir loc stream)
        (if (= b "..") (zip/up loc)
            (find-node b loc)))))



(defn stream->tree 
  "конвертируем поток команд в дерево в виде мапы"
  [input] 
  (loop [rem-stream (next (str/split-lines input))
         loc (zip/down (map-zipper {"/" {}}))]
    (if (not (seq rem-stream))
      (zip/root loc)
      (recur (drop (if (= (first rem-stream) "$ ls") 
                     (inc (count (read-dir rem-stream))) 1) rem-stream)
             (action loc rem-stream)))))

;; функции для расчета суммы размеров директорий с условием

(defn size-of-branch 
  "для текущей локации считаем размер всех элементов этой ветки"
  [loc]
  (let [all-locs-of-branch (-> loc
                               zip/node
                               map-zipper
                               iter-zip)]
    (->> all-locs-of-branch
         (filter #(int? (second (zip/node %))))
         (map (comp second zip/node))
         (reduce +))))

(defn sum-of-sizes* 
  "считаем сумму всех размеров директорий, не превышающих 100000"
  [m]
  (let [all-locs (-> m
                     map-zipper 
                     iter-zip)]
    (->> all-locs 
         (filter #(map? (second (zip/node %)))) 
         (map size-of-branch) 
         (filter #(> 100000 %)) 
         (reduce +))))

(defn sum-of-sizes [input]
  (sum-of-sizes* (stream->tree input)))

(sum-of-sizes example)
