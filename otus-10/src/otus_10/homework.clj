(ns otus-10-homework.core
(:require [clojure.string :as string])
  (:require [clojure.java.io :as io]))

(def n1 "file-12926-ed090b.mp3")

(defn check-flag 
  "Проверяем n-ый бит байта в виде integer"
  [num-int ind]
  (->> num-int
       Integer/toBinaryString
       reverse
       (#(nth % ind \0))
       (= \1)))

(defn get-size 
  "Получаем размер (header, subheader or frame)"
  [arr]
  (->> (range (count arr))
       (map #(Math/pow 128 %))
       reverse
       (map * arr)
       (apply +)))

(defmulti parse-frame first)
(defmethod parse-frame 0
  [coll] (String. (byte-array (remove zero? (next coll))) "Windows-1252"))
(defmethod parse-frame 1
  [coll] (String. (byte-array (remove zero? (next coll))) "UTF-16"))
(defmethod parse-frame 2
  [coll] (String. (byte-array (remove zero? (next coll))) "UTF-16BE"))
(defmethod parse-frame 3
  [coll] (String. (byte-array (remove zero? (next coll))) "UTF-8"))


(defn get-frame 
  "парсим один фрейм"
  [stream]
  (let [[nm1 nm2 nm3 nm4 sz1 sz2 sz3 sz4 & rest] stream
        name (apply str (map char [nm1 nm2 nm3 nm4]))
        size (get-size [sz1 sz2 sz3 sz4])
        text (parse-frame (take (+ size 2) rest))]
    {:name name :size size :text text}))

(defn parse-mp3-tags [file]
(with-open [in (io/input-stream (io/file (io/resource file)))]
  (let [[_ _ _ _ _ flag-byte sz1 sz2 sz3 sz4 & rest] (.readAllBytes in)
        tags-size (get-size [sz1 sz2 sz3 sz4])
        [x1 x2 x3 x4] rest
        sub-header-size (if (check-flag flag-byte 6) (get-size [x1 x2 x3 x4]) 0)
        frames (drop sub-header-size (take tags-size rest))]
    (loop [rem-frames frames
           acc []]
      (if (or (empty? rem-frames) (zero? (first rem-frames))) acc
          (recur (drop (+ 10 (:size (get-frame rem-frames))) rem-frames)
                 (conj acc (get-frame rem-frames))))))))
