(ns otus-16.homework
    (:require [clojure.set :as set]))

(def log_indices {:ip 1 :identifier 2 :userid 3 :time 4 :request 5 :status 6 :size 7 :referrer 8 :user-agent 9})

(def start-result-state {:total-bytes       0
                         :bytes-per-url     0
                         :urls-per-referrer #{}})

(defn- parse-request [request]
    (let [[method url http-version] (clojure.string/split request #" ")]
        {:method method :url url :http-version http-version}))

(defn parse-apache-log [log-line]
    (let [regex #"(\S+) (\S+) (\S+) \[([\w:/]+\s[\+\-]\d{4})\] \"(.*?)\" (\d{3}) (\S+) \"(.*?)\" \"(.*?)\""
          matcher (re-find regex log-line)]
        (if matcher
            (let [parsed-request (parse-request (nth matcher (log_indices :request)))]
                {:ip         (nth matcher (log_indices :ip))
                 :identifier (nth matcher (log_indices :identifier))
                 :userid     (nth matcher (log_indices :userid))
                 :time       (nth matcher (log_indices :time))
                 :status     (nth matcher (log_indices :status))
                 :size       (parse-long (nth matcher (log_indices :size)))
                 :referrer   (nth matcher (log_indices :referrer))
                 :user-agent (nth matcher (log_indices :user-agent))
                 :request    parsed-request})
            :no-match)))

(defn parse-logs-portion [url referrer log-lines]
    (let [parsed-lines (map parse-apache-log log-lines)]
        (reduce
            (fn [acc parsed-line]
                (let [bytes (:size parsed-line)
                      line-url (:url (:request parsed-line))
                      line-referrer (:referrer parsed-line)
                      total-bytes (+ (acc :total-bytes) bytes)
                      bytes-per-url (if (or (= url line-url) (= url :all))
                                        (+ (:bytes-per-url acc) bytes)
                                        (:bytes-per-url acc))
                      urls-per-referrer (if (or (= referrer line-referrer) (= referrer :all))
                                            (conj (:urls-per-referrer acc) line-url)
                                            (:urls-per-referrer acc))]
                    {:total-bytes       total-bytes
                     :bytes-per-url     bytes-per-url
                     :urls-per-referrer urls-per-referrer}))
            start-result-state
            parsed-lines)))


(def merge-results
    (partial merge-with (fn [x y]
                            (if (set? x)
                                (set/union x y)
                                (+ x y)))))

(defn reduce-results [coll]
    (doall (reduce merge-results start-result-state coll)))

(defn parallel-parse-file [url referrer file]
    (with-open [rdr (clojure.java.io/reader file)]
        (->> rdr
             (line-seq)
             (partition-all 100000)
             (pmap (partial parse-logs-portion url referrer))
             (reduce-results)
             (doall))))


(defn solution [& {:keys [url referrer]
                   :or   {url :all referrer :all}}]
    (let [files (->> "logs"
                     clojure.java.io/file
                     file-seq
                     (filter #(.isFile %)))
          result-with-urls (reduce-results (pmap (partial parallel-parse-file url referrer) files))]
        (update result-with-urls :urls-per-referrer count)))


(comment
    ;; возможные вызовы функции
    (time (solution))
    @number-state
    @set-state
    (reset-state)
    (time (solution :url "/random"))
    (time (solution :referrer "https://www.google.com/"))
    (time (solution :url "/%D1%81%D1%82%D0%BE%D1%8F%D1%82-%D0%BF%D0%B0%D1%81%D1%81%D0%B0%D0%B6%D0%B8%D1%80%D1%8B-%D0%B2-%D0%B0%D1%8D%D1%80%D0%BE%D0%BF%D0%BE%D1%80%D1%82%D1%83-%D0%BF%D0%BE%D1%81%D0%B0%D0%B4%D0%BE%D1%87%D0%BD%D1%8B%D0%B9-%D0%B4%D0%BE%D1%81%D0%BC%D0%BE%D1%82%D1%80/?p=4"
                    :referrer "https://www.google.com/")))
