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

(defn read-file-n-bytes [path n]
    (with-open [rdr (io/input-stream path)]
        (let [byte-array (byte-array n)]
            (do (.read rdr byte-array)
                (vec byte-array)))))

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


(defn read-id3v2-header [path]
    (let [header (read-file-n-bytes path 10)
          flags-byte (nth header 5)
          file-identifier (bytes->string (take 3 header))]
        (if (not= file-identifier "ID3")
            (throw (ex-info
                       "Incorrect file identifier"
                       {:file-identifier file-identifier})))
        {:file-identifier file-identifier
         :major-version   (nth header 3)
         :revision-number (nth header 4)
         :flags           {:unsynchronisation      (bit-test flags-byte 7)
                           :extended-header        (bit-test flags-byte 6)
                           :experimental-indicator (bit-test flags-byte 5)
                           :footer-present         (bit-test flags-byte 4)}
         :size            (sync-safe-to-int (take 4 (drop 6 header)))}))

(defn read-extended-header [file]
    (let
        [flags-byte (nth file 15)]
        {:size                 (sync-safe-to-int (take 4 (drop 10 file))),
         :number-of-flag-bytes (nth file 14),
         :flags                {:update       (bit-test flags-byte 6)
                                :CRC          (bit-test flags-byte 5)
                                :restrictions (bit-test flags-byte 4)}}))

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
    (let [id3v2-header (read-id3v2-header path)
          size (:size id3v2-header)
          tag (read-file-n-bytes path size)
          has-extended-header? (get-in id3v2-header [:flags :extended-header])
          frames-start (+ 10 (if has-extended-header?
                                 (:size (read-extended-header tag))
                                 0))]
        (pprint id3v2-header)
        (if has-extended-header?
            (pprint (read-extended-header tag)))
        (loop [pos frames-start]
            (if (< (- pos 10) size)
                (let [frame-header (read-frame-header tag pos)
                      frame-content (take (:size frame-header) (drop (+ 10 pos) tag))
                      frame-representation (read-frame-content (:frame-id frame-header) frame-content)]
                    (pprint frame-representation)
                    (recur (+ pos 10 (:size frame-header))))))))

(defn -main [& args]
    (if (seq args)
        (doseq [arg args]
            (process-id3 arg))))