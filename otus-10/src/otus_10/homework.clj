(ns otus-10.homework
    (:require [clojure.java.io :as io]
              [clojure.walk :as w])
    (:import (java.io ByteArrayOutputStream)))


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

(defn bytes->string [bytes]
    (new String (byte-array bytes)))

(defn read-id3v2-header [file]
    (let [flags-bits (byte->binary-8 (nth 6 file))]
        {:file-identifier (bytes->string (take 3 file))
         :major-version   (nth 4 file)
         :revision-number (nth 5 file)
         :flags           {:unsynchronisation      (nth 1 flags-bits)
                           :extended-header        (nth 2 flags-bits)
                           :experimental-indicator (nth 3 flags-bits)
                           :footer-present         (nth 4 flags-bits)}
         :size            (bytes-wo-7-bit->int (take 4 (drop 6 file)))}))

(defn
    read-id3v2-header-v2
    [file]
    (let
        [flags-bits (byte->binary-8 (nth file 5))]
        {:file-identifier (bytes->string (take 3 file)),
         :major-version   (nth file 3),
         :revision-number (nth file 4),
         :flags           {:unsynchronisation      (nth flags-bits 0),
                           :extended-header        (nth flags-bits 1),
                           :experimental-indicator (nth flags-bits 2),
                           :footer-present         (nth flags-bits 3)},
         :size            (bytes-wo-7-bit->int (take 4 (drop 6 file)))}))

(comment
    (nth [1 2 3] 0)
    (bytes->string [73 68 51])                              ;TODO to test
    (byte->binary-8 17)
    (byte->binary-8 127)
    (byte->binary-7 127)
    (take 2 (byte->binary-7 127))
    (let [f (file->bytes "resources/anima.mp3")]
        (read-id3v2-header-v2 f))
    )

(w/postwalk #(if (and (list? %)
                     (= 'nth (first %))
                     #_(number? (second %)))
                (list 'nth (nth % 2) (dec (nth % 1)))
                %) '(defn read-id3v2-header [file]
                                    (let [flags-bits (byte->binary-8 (nth 6 file))]
                                        {:file-identifier (bytes->string (take 3 file))
                                         :major-version   (nth 4 file)
                                         :revision-number (nth 5 file)
                                         :flags           {:unsynchronisation      (nth 1 flags-bits)
                                                           :extended-header        (nth 2 flags-bits)
                                                           :experimental-indicator (nth 3 flags-bits)
                                                           :footer-present         (nth 4 flags-bits)}
                                         :size            (bytes-wo-7-bit->int (take 4 (drop 6 file)))})))

(let [fl (file->bytes "resources/anima.mp3")]
    (take 10 fl)
    (take 10 fl))
(Integer/toString 8 2)
(byte->binary-7 8)
(sum-up-binary-wo-first-bit (drop 6 (read-n-bytes "resources/anima.mp3" 10)))

(sum-up-binary-wo-first-bit [0 0 7 118])

(Integer/toString 0x76 10)
(seq (read-n-bytes "resources/slts.mp3" 4))

(seq (read-n-bytes "resources/test.mp3" 9))
(new String (byte-array 4 [73 68 51 03]))
(vec bbs)
