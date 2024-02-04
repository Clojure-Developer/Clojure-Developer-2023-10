(ns otus-16.homework)

(def start-state {:total-bytes      0
                  :bytes-by-url     0
                  :urls-by-referrer 0})
(def parser-state (atom start-state))

(defn reset-state []
    (reset! parser-state start-state))

(defn parse-apache-log [log-line]
    (let [regex #"(\S+) (\S+) (\S+) \[([\w:/]+\s[\+\-]\d{4})\] \"(.*?)\" (\d{3}) (\S+) \"(.*?)\" \"(.*?)\""
          matcher (re-find regex log-line)]
        (if matcher
            {:ip         (nth matcher 1)
             :identifier (nth matcher 2)
             :userid     (nth matcher 3)
             :time       (nth matcher 4)
             :request    (nth matcher 5)
             :status     (nth matcher 6)
             :size       (parse-long (nth matcher 7))
             :referrer   (nth matcher 8)
             :user-agent (nth matcher 9)}
            :no-match)))
(defn parse&persist-logs-portion [log-lines]
    (let [total (reduce #(+ %1 (:size %2)) 0 (map parse-apache-log log-lines))]
        (swap! parser-state update :total-bytes (partial + total))))

(defn parallel-parse-file [file]
    (with-open [rdr (clojure.java.io/reader file)]
        (doall (pmap parse&persist-logs-portion (partition-all 100000 (line-seq rdr))))))

(defn solution [& {:keys [url referrer]
                   :or   {url :all referrer :all}}]
    (let [futures (doall (for [file (->> "logs"
                                    clojure.java.io/file
                                    file-seq
                                    (filter #(.isFile %)))]
                      (future (parallel-parse-file file))))]
        (doseq [f futures]
            @f)
        (let [result @parser-state]
            (reset-state)
            result)))


(comment
    ;; возможные вызовы функции
    (time (solution))
    @parser-state
    (reset-state)
    (solution :url "some-url")
    (solution :referrer "some-referrer")
    (solution :url "some-url" :referrer "some-referrer"))
