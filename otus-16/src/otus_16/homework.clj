(ns otus-16.homework)

(def start-state {:total-bytes       0
                  :bytes-per-url     0
                  :urls-per-referrer {}})
(def parser-state (atom start-state))

(defn reset-state []
    (reset! parser-state start-state))

(defn parse-apache-log [log-line]
    (let [regex #"(\S+) (\S+) (\S+) \[([\w:/]+\s[\+\-]\d{4})\] \"(.*?)\" (\d{3}) (\S+) \"(.*?)\" \"(.*?)\""
          matcher (re-find regex log-line)]
        (if matcher
            (let [request (nth matcher 5)
                  [method url http-version] (clojure.string/split request #" ")]
                {:ip         (nth matcher 1)
                 :identifier (nth matcher 2)
                 :userid     (nth matcher 3)
                 :time       (nth matcher 4)
                 :request    {:method       method
                              :url          url
                              :http-version http-version}
                 :status     (nth matcher 6)
                 :size       (parse-long (nth matcher 7))
                 :referrer   (nth matcher 8)
                 :user-agent (nth matcher 9)})
            :no-match)))


(defn size-reducer [lines]
    (reduce #(+ %1 (:size %2)) 0 lines))

(defn has-url? [url]
    (fn [el]
        (= (:url (:request el)) url)))

(defn has-referrer? [referrer]
    (fn [el]
        (= (:referrer el) referrer)))

(defn parse&persist-logs-portion-? [url referrer log-lines]
    (let [parsed-lines (map parse-apache-log log-lines)
          total-bytes (size-reducer parsed-lines)
          ;bytes-per-url (if (= url :all)                    ;WARNING
          ;                  total-bytes
          ;                  (size-reducer (filter (has-url? url) parsed-lines)))
          ;urls-per-referrer (if (= referrer :all)
          ;                       (count parsed-lines)
          ;                       (count (filter (has-referrer? referrer) parsed-lines)))
          ]
        (swap! parser-state
               (fn [state]
                   (-> state
                       (update :total-bytes + total-bytes)
                       #_(update :bytes-per-url + bytes-per-url)
                       #_(set/union :urls-per-referrer urls-per-referrer)
                       )))))

(defn parse&persist-logs-portion [url referrer log-lines]
    (let [total (reduce #(+ %1 (:size %2)) 0 (map parse-apache-log log-lines))]
        (swap! parser-state update :total-bytes (partial + total))))

(defn parallel-parse-file [url referrer file]
    (with-open [rdr (clojure.java.io/reader file)]
        (doall (pmap (partial parse&persist-logs-portion-? url referrer) (partition-all 100000 (line-seq rdr))))))

(defn solution [& {:keys [url referrer]
                   :or   {url :all referrer :all}}]
    (let [files (->> "logs"
                     clojure.java.io/file
                     file-seq
                     (filter #(.isFile %)))]
        (doall (pmap (partial parallel-parse-file url referrer) files)))
    (let [result @parser-state]                             ; TODO closure with atom?
        (reset-state)
        result))


(comment
    ;; возможные вызовы функции
    (time (solution))
    @parser-state
    (reset-state)
    (solution :url "some-url")
    (solution :referrer "some-referrer")
    (solution :url "some-url" :referrer "some-referrer"))
