(ns otus-10.homework
  (:require [clojure.test :refer :all]
            [otus-10.legacy-for-tests :refer :all]
            [otus-10.homework :refer :all]))

(deftest test-to-hex
  (is (= "0f" (to-hex 15)))
  (is (= "ff" (to-hex 255))))

(deftest test-gen-random-str
  (is (string? (gen-random-str)))
  (is (= 5 (count (gen-random-str)))))

(deftest test-byte-binary-7
  (is (= "0000001" (byte->binary-7 1)))
  (is (= "1111111" (byte->binary-7 127))))

(deftest test-byte-binary-8
  (is (= "00000001" (byte->binary-8 1)))
  (is (= "01111111" (byte->binary-8 127))))

(deftest test-bytes-wo-7-bit->int+regression
  (is (= 127 (bytes-wo-7-bit->int [127])))
  (is (= 16256 (bytes-wo-7-bit->int [127 1])))

  (is (bytes-wo-7-bit->int [127]) (sync-safe-to-int [127]))
  (is (bytes-wo-7-bit->int [0 0 7 118]) (sync-safe-to-int [0 0 7 118])))

(deftest test-determine-encoding
  (is (= "ISO-8859-1" (determine-encoding [0]))))

(deftest test-bytes-to-id3
  (is ("ID3" (bytes->string [73 68 51]))))