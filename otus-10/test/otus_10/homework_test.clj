(ns otus-10.homework-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [otus-10.homework :as sut]))


(def raf
  (java.io.RandomAccessFile. "test/otus_10/1-second-of-silence.mp3"  "r"))

(deftest find-id3-start-test
  (is (= 0 (sut/find-id3-start raf))))

(deftest read-size-test
  (is (= 137220 (sut/read-size raf 6))))

(deftest read-header-test
  (is (= (dissoc (sut/read-header raf 0) :raf)
         {:id3 "ID3",
          :version :v2.4,
          :size-id3 137220,
          :h-size 10,
          :id3-seek 10})))

(deftest read-str-test
  (is (= "ID3" (sut/read-str {:byte-encoding 0 :ba (.getBytes "ID3" "ISO-8859-1")})))
  (is (= "ID3" (sut/read-str {:byte-encoding 1 :ba (.getBytes "ID3" "UTF-16")})))
  (is (= "ID3" (sut/read-str {:byte-encoding 2 :ba (.getBytes "ID3" "UTF-16BE")})))
  (is (= "ID3" (sut/read-str {:byte-encoding 3 :ba (.getBytes "ID3" "UTF-8")}))))

(deftest split-str-test
  (is (= "ID3" (sut/split-str "ID3" {:version :v2.4})))
  (is (= ["one" "two"] (sut/split-str "one/two" {:version :v2.3})))
  (is (= ["one" "two"] (sut/split-str "one two" {:version :v2.4})))
  (is (= "one/two" (sut/split-str "one/two" {:version :v2.2}))))


(deftest read-frame-test
  (is (= {:tag-name "Название трека:", :text "Я свободен"} (sut/read-frame {:tag :TIT2 :byte-encoding 1 :ba (.getBytes "Я свободен" "UTF-16")})))
  (is (= {:tag-name "Альбом:", :text "101"} (sut/read-frame {:tag :TALB :byte-encoding 0 :ba (.getBytes "101" "ISO-8859-1")}))))

(deftest read-frames-test
  (is (= [:TIT2 :TPE1 :TALB :APIC]
         (mapv :tag (sut/read-frames (sut/read-header raf 0))))))

(deftest mp3-tags-test
  (is (= ["Название трека:" ":TPE1" "Альбом:" ":APIC"]
         (mapv :tag-name (sut/mp3-tags "test/otus_10/1-second-of-silence.mp3")))))

(comment
  (run-tests))
