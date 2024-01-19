(ns otus-10.homework)

(def bbs (byte-array 3))
(defn read-mp3 [path]
  (let [p (proxy [java.io.FileInputStream] ["resources/anima.mp3"]
            (read
              ([] 1)
              ([^bytes bytes] 2)
              ([^bytes bytes off len] 3))
            (toString
              ([] (str (.hashCode this)))))]
    (println (.read p))
    (println (vec bbs))
    (println (.read p bbs))
    (println (vec bbs))
    (println (.read p (byte-array 3) 0 21))
    (println p)))


(defn read-n-bytes [path count]
  (let [arr (byte-array count)]
    (with-open [is (new java.io.FileInputStream path)]
      (.read is arr)
      arr)))

(defn byte->binary-7 [byte]
  (let [tail (Integer/toString byte 2)
        head (apply str (repeat (- 7 (count tail)) "0"))]
    (str head tail)))

(defn sum-up-binary-wo-first-bit [coll]
  (Long/parseLong (apply str (map byte->binary-7 coll)) 2))

(read-mp3 "sad")
(first (read-n-bytes "resources/anima.mp3" 10))
(Integer/toString 8 2)
(byte->binary-7 8)
(sum-up-binary-wo-first-bit (drop 6 (read-n-bytes "resources/anima.mp3" 10)))

(sum-up-binary-wo-first-bit [0 0 7 118])

(Integer/toString 0x76 10)
(read-n-bytes "resources/slts.mp3" 4)

(read-n-bytes "resources/test.mp3" 9)

(read-string "0x03")
(new String (byte-array 4 [73 68 51 03]))
(vec bbs)
