(ns otus-10.legacy-for-tests
    (:require [otus-10.homework :refer [bytes->string]]))

(defn byte->binary-7
    "Convert byte to binary w/o first bit"
    [byte]
    (let [tail (Integer/toString byte 2)
          head (apply str (repeat (- 7 (count tail)) "0"))]
        (str head tail)))

(defn bytes-wo-7-bit->int
    "Convert bytes array to integer w/o counting first bit in each byte"
    [coll]
    (Long/parseLong (apply str (map byte->binary-7 coll)) 2))

(defn byte->binary-8
    "Convert byte to binary"
    [byte]
    (let [tail (Integer/toString byte 2)
          head (apply str (repeat (- 8 (count tail)) "0"))]
        (str head tail)))

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
