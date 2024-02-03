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

(defn sync-safe-to-int
    "Convert bytes array to integer w/o counting first bit in each byte"
    [bytes]
    (bit-or
        (bit-shift-left (nth bytes 0) 21)
        (bit-shift-left (nth bytes 1) 14)
        (bit-shift-left (nth bytes 2) 7)
        (nth bytes 3)))

(defn bytes->string
    ([bytes]
     (bytes->string bytes (Charset/defaultCharset)))
    ([bytes encoding]
     (new String (byte-array bytes) encoding)))


(defn read-id3v2-header [file]
    (let
        [flags-byte (nth file 5)]
        {:file-identifier (bytes->string (take 3 file)),
         :major-version   (nth file 3),
         :revision-number (nth file 4),
         :flags           {:unsynchronisation      (bit-test flags-byte 0)
                           :extended-header        (bit-test flags-byte 1)
                           :experimental-indicator (bit-test flags-byte 2)
                           :footer-present         (bit-test flags-byte 3)}
         :size            (sync-safe-to-int (take 4 (drop 6 file)))}))

(defn read-extended-header [file]
    (let
        [flags-byte (nth file 15)]
        {:size                 (sync-safe-to-int (take 4 (drop 10 file))),
         :number-of-flag-bytes (nth file 14),
         :flags                {:update       (bit-test flags-byte 1)
                                :CRC          (bit-test flags-byte 2)
                                :restrictions (bit-test flags-byte 3)}}))

(defn read-frame-header [file pos]
    (let [frame-header (take 10 (drop pos file))]
        {:frame-id (bytes->string (take 4 frame-header))
         :size     (sync-safe-to-int (take 4 (drop 4 frame-header)))
         :flags    {:byte1 (nth frame-header 8)
                    :byte2 (nth frame-header 9)}}))

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