(ns otus-10-homework
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


(defn parse-frame-text [coll]
  (let [[encoding-byte & rest] coll
        encoding-types ["Windows-1252" "UTF-16" "UTF16BE" "UTF-8"]]
    (String. (byte-array rest) (nth encoding-types encoding-byte))))

(defn get-frame-inside
  "парсим один фрейм"
  [stream]
  (let [[_ _ _ _ sz1 sz2 sz3 sz4 _ _ & rest] stream
        size (get-size [sz1 sz2 sz3 sz4])
        text (parse-frame-text (take size rest))]
    {:size size :text text}))

(defmulti get-frame (fn [coll] (apply str (map char (take 4 coll)))))

(defmethod get-frame "TALB" [coll] (assoc (get-frame-inside coll) :name "TALB" :description "Album/Movie/Show title"))
(defmethod get-frame "TYER" [coll] (assoc (get-frame-inside coll) :name "TYER" :description "Year"))
(defmethod get-frame "TCON" [coll] (assoc (get-frame-inside coll) :name "TCON" :description "Content type"))
(defmethod get-frame "TDRC" [coll] (assoc (get-frame-inside coll) :name "TDRC" :description "Recording time"))
(defmethod get-frame "TIT2" [coll] (assoc (get-frame-inside coll) :name "TIT2" :description "Title/songname/content description"))
(defmethod get-frame "TPE1" [coll] (assoc (get-frame-inside coll) :name "TPE1" :description "Lead performer(s)/Soloist(s)"))

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
