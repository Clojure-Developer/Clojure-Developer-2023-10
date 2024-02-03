(ns otus-16.homework)

(def parser-state (atom {:total-bytes      0
                         :bytes-by-url     0
                         :urls-by-referrer 0}))

;(defn parallel-read
;    [^String file-path start-position end-position]
;    (with-open [file (RandomAccessFile. file-path "r")]
;        (.seek file start-position)
;        (let [byte-array (byte-array (- end-position start-position))
;              _ (.readFully file byte-array)]
;            ;; convert byte-array to string or process as needed
;            )))

(defn parse-log-line [line]
    (let [size (-> line
                   (clojure.string/replace #"\s\[.*?\]\s" " ")
                   (clojure.string/replace #"\s\".*?\"\s" " ")
                   (clojure.string/split #"\s+")
                   (nth 4)
                   parse-long
                   )]
        (swap! parser-state update :total-bytes (partial + size))))

(comment
    (parse-log-line "asd asd asf asfas ffga 12 123")
    (parse-log-line "asd asd asf asfas ffga 12 677")
    (reset! parser-state {:total-bytes      0
                          :bytes-by-url     0
                          :urls-by-referrer 0}))

(defn reset-state []
    (reset! parser-state {:total-bytes      0
                          :bytes-by-url     0
                          :urls-by-referrer 0}))

(defn solution [& {:keys [url referrer]
                   :or   {url :all referrer :all}}]
    (with-open [rdr (clojure.java.io/reader "logs/access.log.2")]
        (doall (pmap parse-log-line (line-seq rdr))))
    (let [result @parser-state]
        (reset-state)
        result))


(comment
    ;; возможные вызовы функции
    (solution)
    @parser-state
    (solution :url "some-url")
    (solution :referrer "some-referrer")
    (solution :url "some-url" :referrer "some-referrer"))
