(ns otus-10.homework
    (:require [clojure.java.io :as io]
              [clojure.java.io :refer [output-stream]]
              [clojure.pprint :refer [pprint]])
    (:import (java.io ByteArrayOutputStream)
             (java.nio.charset Charset))
    (:gen-class))

(defn to-hex [n]
    (format "%02x" n))

(defn gen-random-str []
    (apply str (repeatedly 5 #(rand-nth "abcdefghijklmnopqrstuvwxyz"))))

(defn file->bytes [file]
    (with-open [in (io/input-stream file)
                out (new ByteArrayOutputStream)]
        (io/copy in out)
        (.toByteArray out)))

(defn byte->binary-7
    "Convert byte to binary w/o first bit"
    [byte]
    (let [tail (Integer/toString byte 2)
          head (apply str (repeat (- 7 (count tail)) "0"))]
        (str head tail)))

(defn byte->binary-8
    "Convert byte to binary"
    [byte]
    (let [tail (Integer/toString byte 2)
          head (apply str (repeat (- 8 (count tail)) "0"))]
        (str head tail)))

(defn bytes-wo-7-bit->int
    "Convert bytes array to integer w/o counting first bit in each byte"
    [coll]
    (Long/parseLong (apply str (map byte->binary-7 coll)) 2))

(defn bytes->string
    ([bytes]
     (bytes->string bytes (Charset/defaultCharset)))
    ([bytes encoding]
     (new String (byte-array bytes) encoding)))


(defn read-id3v2-header [file]
    (let
        [flags-bits (byte->binary-8 (nth file 5))]
        {:file-identifier (bytes->string (take 3 file)),
         :major-version   (nth file 3),
         :revision-number (nth file 4),
         :flags           {:unsynchronisation      (nth flags-bits 0)
                           :extended-header        (nth flags-bits 1)
                           :experimental-indicator (nth flags-bits 2)
                           :footer-present         (nth flags-bits 3)}
         :size            (bytes-wo-7-bit->int (take 4 (drop 6 file)))}))

(defn read-extended-header [file]
    (let
        [flags-bits (byte->binary-8 (nth file 15))]
        {:size                 (bytes-wo-7-bit->int (take 4 (drop 10 file))),
         :number-of-flag-bytes (nth file 14),
         :flags                {:update       (nth flags-bits 1)
                                :CRC          (nth flags-bits 2)
                                :restrictions (nth flags-bits 3)}}))

(defn read-frame-header [file pos]
    (let [frame-header (take 10 (drop pos file))]
        {:frame-id (bytes->string (take 4 frame-header))
         :size     (bytes-wo-7-bit->int (take 4 (drop 4 frame-header)))
         :flags    {:byte1 (nth frame-header 8)
                    :byte2 (nth frame-header 9)}}))

(comment
    (bytes->string [73 68 51])                              ;TODO to test
    (bytes-wo-7-bit->int [0 0 7 118])                       ;TODO to test
    (count (byte->binary-8 127))                            ;TODO to test
    (count (byte->binary-7 127))                            ;TODO to test
    (let [f (file->bytes "resources/test.mp3")]
        (pprint (read-id3v2-header f))
        (pprint (read-extended-header f))
        )
    )

(defmulti read-frame-content (fn [frame-id & args] frame-id))

(comment
    (ns-unmap *ns* 'read-frame-content))

(defn determine-encoding [frame-content]
    (case (first frame-content)
        0 "ISO-8859-1"
        1 "UTF-16"
        2 "UTF-16BE"
        3 "UTF-8"
        (Charset/defaultCharset)))

; default method for parse all "T"-starting frames
(defmethod read-frame-content :default [frame-id frame-content]
    (let [text (drop 1 frame-content)
          encoding (determine-encoding frame-content)]
        {:frame-id frame-id
         :content  (bytes->string text encoding)}))

; overrided method for parse "T"-starting frame, but in a little different way (for Homework purpose)
(defmethod read-frame-content "TYER" [frame-id frame-content]
    (let [text (drop 1 frame-content)
          encoding (determine-encoding frame-content)]
        {:frame   "Год выхода альбома"
         :content (parse-long (bytes->string text encoding))}))

; method for saving pictures from tag
(defmethod read-frame-content "APIC" [frame-id frame-content]
    (let [file-name (str "resources/pics/" (gen-random-str) ".jpeg")]
        (with-open [w (output-stream file-name)]
            (.write w (byte-array (drop-while #(not= -1 %) frame-content)))) ;TODO drop-while for SOI 0xFF, 0xD8 (but how???)
        {:frame   "Attached pic"
         :content file-name}))

(defn process-id3 [path]
    (let [file (file->bytes path)
          id3v2-header (read-id3v2-header file)
          size (:size id3v2-header)
          has-extended-header? (= \1 (get-in id3v2-header [:flags :extended-header]))
          frames-start (+ 10 (if has-extended-header?
                                 (:size (read-extended-header file))
                                 0))]
        (pprint id3v2-header)
        (if has-extended-header?
            (pprint (read-extended-header file)))
        (loop [pos frames-start]
            (if (< (- pos 10) size)
                (let [frame-header (read-frame-header file pos)
                      frame-content (take (:size frame-header) (drop (+ 10 pos) file))
                      frame-representation (read-frame-content (:frame-id frame-header) frame-content)]
                    (pprint frame-representation)
                    (recur (+ pos 10 (:size frame-header))))))))

(defn -main [& args]
    (if (seq args)
        (doseq [arg args]
            (process-id3 arg))))