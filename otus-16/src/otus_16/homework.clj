(ns otus-16.homework
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.core.reducers :as r])
  (:import (java.net URLDecoder)))

(def ^:dynamic *logs-dir* "logs")

(defn lines-of-file [file fn]
  (with-open [rdr (io/reader file)]
    (fn (line-seq rdr))))

(defn url-decode [s]
  (java.net.URLDecoder/decode
   (if (str/includes? s "\\x")
     (str/replace s #"\\x" "%")
     s)))

(defn extract-url [s]
  (let [arr (str/split s #" " 3)
        url (if (= 1 (count arr))
              (first arr)
              (second arr))]
    (url-decode url)))

(defn parse-line [line url referrer]
  (let [rgx #"(\d+\.\d+\.\d+\.\d+) (\S+) (\S+) \[(.+?)\] \"(.*?)\" (\d+) (\d+) \"(.*?)\" \"(.*?)\""
        groups (re-matches rgx line)
        current-url (extract-url (nth groups 5))
        bytes (parse-long (nth groups 7))
        current-referrer (url-decode (nth groups 8))
        is-referrer (if (= :all referrer)
                      true
                      (some?  (str/index-of current-referrer referrer)))]
    {:bytes-by-url (if (or (= url :all) (= url current-url))
                     {current-url bytes}
                     {})
     :total-bytes bytes
     :urls-by-referrer (cond
                         (= referrer :all) {current-referrer 1}
                         is-referrer {referrer 1}
                         :else {})}))

(defn deep-merge-with
  [f & maps]
  (apply
   (fn m [& maps]
     (if (every? map? maps)
       (apply merge-with m maps)
       (apply f maps)))
   maps))

(defn count-bytes-use-redusers [lines url referrer]
  ;; "Elapsed time: 7621.87414 msecs" for access.log.4 121 mb
  ;; не так быстро как count-bytes-use-pmap но использует не более 2-3 ядер
  (r/fold
   (r/monoid #(deep-merge-with + %1 %2) (constantly {}))
   (r/map #(parse-line %1 url referrer) lines)))

(defn count-bytes-use-pmap [lines url referrer]
  ;; "Elapsed time: 2307.441963 msecs" for access.log.4 121 mb
  ;; быстро но использует все ядра на 100%
  (reduce #(deep-merge-with + %1 %2) (pmap #(parse-line %1 url referrer) lines)))

(defn solution-file [url referrer file]
  (lines-of-file file #(count-bytes-use-redusers %1 url referrer)))

(defn solution [& {:keys [url referrer]
                   :or   {url :all referrer :all}}]
  (println "u" url "r" referrer)
  (r/fold
   (r/monoid #(deep-merge-with + %1 %2) (constantly {}))
   (r/map #(solution-file url referrer %1)
          (filter #(.isFile %1) (file-seq (io/as-file *logs-dir*)))))
;;   {:total-bytes      12345
;;    ;; если указан параметр url, то в хэш-мапе будет только одно значение
;;    :bytes-by-url     {"some-url" 12345}
;;    ;; если указан параметр referrer, то в хэш-мапе будет только одно значение
;;    :urls-by-referrer {"some-referrer" 12345}}
  )


(comment
 ;; возможные вызовы функции
  (time (do (solution) (+ 1 1)))
  (solution)
  (solution :url "some-url")
  (solution :referrer "some-referrer")
  (solution :url "some-url" :referrer "some-referrer"))
