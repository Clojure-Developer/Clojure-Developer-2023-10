(ns otus-16.homework-test
  (:require [clojure.test :refer :all]
            [otus-16.homework :as sut]))

(def line1 "18.217.223.118 - - [19/Jul/2020:07:14:55 +0000] \"GET /123 HTTP/1.1\" 301 178 \"https://baneks.site/\" \"Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko\" \"-\"")

(def lines ["18.217.223.118 - - [19/Jul/2020:07:14:55 +0000] \"GET / HTTP/1.1\" 301 178 \"-\" \"Mozilla/5.0\" \"-\""
            "46.229.168.142 - - [19/Jul/2020:07:41:35 +0000] \"GET /%D0%BE%D0/ HTTP/1.1\" 200 8302 \"-\" \"Mozilla/5.0\" \"-\""
            "176.59.98.98 - - [19/Jul/2020:07:42:32 +0000] \"GET /%D0%B4%D0/ HTTP/2.0\" 200 13797 \"https://baneks.site/\" \"Mozilla/5.0\" \"-\""])

(deftest test-url-decode
  (testing
   (is (= "/" (sut/url-decode "/")))
    (is (= "/оо" (sut/url-decode "/%D0%BE%D0%BE")))
    (is (= "/уч" (sut/url-decode "/\\xD1\\x83\\xD1\\x87")))))

(deftest test-extract-url
  (testing
   (is (= "/" (sut/extract-url "GET / HTTP/1.1")))
    (is (= "/123" (sut/extract-url "/123")))))

(deftest test-parse-line
  (testing
   (is (= {:bytes-by-url {"/123" 178}, :total-bytes 178, :urls-by-referrer {"baneks.site" 1}}
          (sut/parse-line line1 "/123" "baneks.site")))))

(deftest test-read-file
  (testing
   (is (= 3 (sut/lines-of-file "test/otus_16/test-logs/file1.txt" count)))))

(deftest test-count-bytes-use-redusers
  (testing
   (is (= {:bytes-by-url {"/" 178}, :total-bytes 22277, :urls-by-referrer {"-" 2, "https://baneks.site/" 1}}
          (sut/count-bytes-use-redusers lines "/" :all)))))

(deftest count-bytes-use-pmap
  (testing
   (is (= {:bytes-by-url {"/" 178}, :total-bytes 22277, :urls-by-referrer {"-" 2, "https://baneks.site/" 1}}
          (sut/count-bytes-use-pmap lines "/" :all)))))

(deftest test-solution-file
  (testing
   (is (= {:bytes-by-url {"/" 178}, :total-bytes 22277, :urls-by-referrer {"-" 2}})
       (sut/solution-file "/" "baneks.site" "test/otus_16/test-logs/file1.txt"))))

(deftest test-deep-merge-with
  (testing
   (is (=  {:bytes-by-url {"/" 356}, :total-bytes 356, :urls-by-referrer {"-" 2}}
           (sut/deep-merge-with +
                                {:bytes-by-url {"/" 178}, :total-bytes 178, :urls-by-referrer {"-" 1}}
                                {:bytes-by-url {"/" 178}, :total-bytes 178, :urls-by-referrer {"-" 1}})))))

(deftest test-solution
  (binding [sut/*logs-dir* "test/otus_16/test-logs"]
    (testing
     (is (= 36236 (:total-bytes (sut/solution))))
      (is (= 178 (get-in (sut/solution :url "/") [:bytes-by-url "/"])))
      (is (= 2 (get-in (sut/solution :url "/" :referrer "baneks.site") [:urls-by-referrer "baneks.site"]))))))

(comment
  (run-tests *ns*))
