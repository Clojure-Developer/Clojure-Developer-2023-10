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

(defn
    read-id3v2-header
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
    (bytes->string [73 68 51])                              ;TODO to test
    (bytes-wo-7-bit->int [0 0 7 118])                       ;TODO to test
    (count (byte->binary-8 127))                            ;TODO to test
    (count (byte->binary-7 127))                            ;TODO to test
    (let [f (file->bytes "resources/test.mp3")]
        (clojure.pprint/pprint (read-id3v2-header f)))
    )