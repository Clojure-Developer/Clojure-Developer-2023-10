(ns otus-10.homework
  (:require [clojure.java.io :as io])
  (:gen-class))

(defn read-size [^java.io.RandomAccessFile raf seek]
  (let [ba (byte-array 4)]
    (.seek raf seek)
    (.read raf ba)
    (as-> (java.nio.ByteBuffer/wrap ba) bb
      (.getInt bb))))

(defn read-header [^java.io.RandomAccessFile raf id3-seek]
  (let [id3 (let [ba (byte-array 3)]
              (.seek raf id3-seek)
              (.read raf ba)
              (String. ba))
        ver (let [ba (byte-array 2)]
              (.seek raf (+ id3-seek 3))
              (.read raf ba)
              (keyword (str "v2."  (int (first ba)))))
        size-id3 (read-size raf (+ id3-seek 6))]
    {:id3 id3 :version ver :size-id3 size-id3 :h-size 10 :id3-seek (+ 10 id3-seek) :raf raf}))

  ;; когда достигнет конца, кинет IllegalArgumentException Value out of range for char: -1
(defn find-id3-start [^java.io.RandomAccessFile raf]
  (let [iI (int \I)
        iD (int \D)
        i3 (int \3)
        lazy-3bytes-idx
        (for [i (range 0 (- (.length raf) 10))]
          (do
            (.seek raf i)
            [(.readByte raf) (.readByte raf) (.readByte raf) i]))]
    (->> lazy-3bytes-idx
         (filter (fn [[b1 b2 b3 _]]
                   (and (= iI b1) (= iD b2) (= i3 b3))))
         first
         (#(get % 3)))))

;; DEFMULTY read-str

(defmulti read-str :byte-encoding)

(defmethod read-str 0 [{ba :ba}]
  (new String ba (java.nio.charset.Charset/forName "ISO-8859-1")))

(defmethod read-str 1 [{ba :ba}]
  (new String ba (java.nio.charset.Charset/forName "UTF-16"))) ;;UTF-16LE

(defmethod read-str 2 [{ba :ba}]
  (new String ba (java.nio.charset.Charset/forName "UTF-16BE")))

(defmethod read-str 3 [{ba :ba}]
  (new String ba (java.nio.charset.Charset/forName "UTF-8")))

(defmethod read-str :default [{ba :ba}]
  (do
    (println "Unknown encoding")
    ""))

(defn split-str [str {version :version}]
  (let [splitter (case version
                   :v2.3 "/"
                   :v2.4 " "
                   nil)]
    (if (and (some? splitter) (< -1 (.indexOf str splitter)))
      (clojure.string/split str (re-pattern splitter))
      str)))

;; DEFMULTY read-frame

(defmulti read-frame
  "TALB — альбом
   TIT2 — название трека
   TYER — год выхода альбома
   TCON — жанр"
  :tag)

(defmethod read-frame :TALB [m]
  {:tag-name "Альбом:" :text (split-str (read-str m) m)})

(defmethod read-frame :TIT2 [m]
  {:tag-name  "Название трека:" :text (split-str (read-str m) m)})

(defmethod read-frame :TYER [m]
  {:tag-name  "Год выхода альбома:" :text (split-str (read-str m) m)})

(defmethod read-frame :TCON [m]
  {:tag-name  "Жанр:" :text (split-str (read-str m) m)})

(defmethod read-frame :default [m]
  {:tag-name (str (m :tag)) :skipped true})

(defn read-frames [header]
  (let [^java.io.RandomAccessFile raf (header :raf)
        id3-seek (header :id3-seek)
        size-id3 (header :size-id3)
        version (header :version)]
    (loop [seek id3-seek
           res []]
      (if (or (>= seek (+ size-id3 id3-seek)) (zero? (read-size raf (+ seek 4))))
        res
        (let [tag (let [ba (byte-array (if (= version :v2.2) 3 4))] ;; 3 2.2 ; 4 2.3 2.4
                    (.seek raf seek)
                    (.read raf ba)
                    (keyword (String. ba)))
              size (read-size raf (+ seek 4))
              byte-encoding (do
                              (.seek raf (+ seek 10))
                              (.readByte raf))
              bytes (let [ba (byte-array (- size 1))]
                      (.seek raf (+ seek 11))
                      (.read raf ba)
                      ba)
              rec {:tag tag :byte-encoding byte-encoding :ba bytes :size size :version version}]
          (recur (+ seek 10 size) (conj res rec)))))))

(defn mp3-tags [path]
  (let [raf  (java.io.RandomAccessFile. path  "r")
        id3-seek (find-id3-start raf)
        header (read-header raf id3-seek)
        frames (read-frames header)]
    (map read-frame frames)))


(defn -main [path & args]
  (println "Tags of:" path)
  (doall
   (->> path
        mp3-tags
        (filter #(not (% :skipped)))
        (map #(println (% :tag-name) (% :text))))))

(comment
  (map println (mp3-tags "/home/ivan/Документы/clj-dir/course/Clojure-Developer-2023-10/otus-10/test/otus_10/1-second-of-silence.mp3"))
  (map println (mp3-tags "/home/ivan/Документы/clj-dir/course/Clojure-Developer-2023-10/otus-10/kipelov-ja-svoboden.mp3")))


  
