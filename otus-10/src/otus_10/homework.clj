(ns otus-10.homework
    (:require [clojure.java.io :as io])
    (:import (java.io ByteArrayOutputStream)
             (java.nio.charset Charset)))

(defn to-hex [n]
    (format "%x" n))
(defn file->bytes [file]
    (with-open [in (io/input-stream file)
                out (new ByteArrayOutputStream)]
        (io/copy in out)
        (.toByteArray out)))

(defn byte->binary-7 [byte]
    (let [tail (Integer/toString byte 2)
          head (apply str (repeat (- 7 (count tail)) "0"))]
        (str head tail)))

(defn byte->binary-8 [byte]
    (let [tail (Integer/toString byte 2)
          head (apply str (repeat (- 8 (count tail)) "0"))]
        (str head tail)))

(defn bytes-wo-7-bit->int [coll]
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

(defn read-frame-content [file header-pos size]
    (let [frame-content (take size (drop (+ 10 header-pos) file))
          text (drop 1 frame-content)
          encoding (case (first frame-content)
                       0 "ISO-8859-1"
                       1 "UTF-16"
                       2 "UTF-16BE"
                       3 "UTF-8")]
        (bytes->string text encoding)))

(comment
    (bytes->string [73 68 51])                              ;TODO to test
    (bytes-wo-7-bit->int [0 0 7 118])                       ;TODO to test
    (count (byte->binary-8 127))                            ;TODO to test
    (count (byte->binary-7 127))                            ;TODO to test
    (let [f (file->bytes "resources/test.mp3")]
        (clojure.pprint/pprint (read-id3v2-header f))
        (map byte->binary-8 (take 2 (drop 14 f)))
        (clojure.pprint/pprint (read-extended-header f))
        (bytes->string (take 4 (drop 22 f)) "UTF-16BE")
        (read-frame-header f 22)
        (read-frame-content f 22 10)
        (read-frame-header f 42)
        (read-frame-content f 42 5)
        (read-frame-header f 57)
        (read-frame-content f 57 5)
        (read-frame-header f 72)
        (read-frame-content f 72 22)
        )
    )