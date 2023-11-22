(ns otus-06.core
  (:import [java.io File]))

;; * Работаем с окружением

;; ** Java way

(comment
  System)

(import '[java.util Scanner])

(comment
  ;; evaluate in REPL!
  (let [in-scanner (new Scanner System/in)]
    (println "entered " (.nextInt in-scanner)))
  )

;; ** Clojure way

(comment
  *in*

  (read-line))

(defn greet []
  (println "Enter your name: ")
  (flush)
  (let [my-name (read-line)]
    (println (str "Hello " my-name))))

(comment
  (greet))

;; ** Буферизация

(doseq [x (range 20)]
  (Thread/sleep 100)
  (pr x))

;; VS

(doseq [x (range 20)]
  (Thread/sleep 100)
  (pr x)
  (flush))

;; ** Перехват стандартных ввода и вывода

(with-in-str "Mr Wick"
  (greet))

(comment
  *out*

  (print "hello")

  (.write *out* "hello\n")

  (do
    (.write *out* "hello")
    (.append *out* \newline)
    (flush))

  )

(with-out-str
  (print "some output test"))

(def out-text
  (with-out-str
    (print "some output test")))

(comment
  *err*)

;; ** Вызов внешних программ

(require '[clojure.java.shell :as shell])

(shell/sh "ls" "-la")

;; ** Переменные окружения

(System/getenv)
(System/getenv "PWD")

;; ** System properties

(System/getProperties)
(System/getProperty "os.name")

;; ** File API

(.exists (File. "qwe"))
(.exists (new File "project.clj"))

;; * Чтение и запись файлов

;; ** Высокоуровневые обёртки

(comment
  (spit "out/sample.txt" "some test")

  (slurp "out/sample.txt")

  ;; url, reader, socket
  (count (slurp "https://www.google.com/"))
  )

;; ** Java IO

(require '[clojure.java.io :as io])

(def sample
  (io/as-file "out/sample.txt"))

(comment
  (.getName sample)
  (.getAbsolutePath sample)
  (.length sample)
  (.isDirectory sample)
  (.isFile sample)

  (def rand-file
    (io/as-file "random.txt"))

  (.exists rand-file)
  )

;; ** Reader & Writer

(comment
  (def sample-writer (io/writer sample))

  (.write sample-writer "new text")
  (.close sample-writer)

  (with-open [w (io/writer sample)]
    (.append w "my new text")
    (.append w \newline))

  (with-open [w (io/writer sample :append true)]
    (.append w "some other text")
    (.append w \newline))

  (with-open [r (io/reader sample)]
    (vec (line-seq r)))
  )

;; ** Ленивое чтение данных

(defn read-nth-line [file line-number]
  (with-open [rdr (io/reader file)]
    (nth (line-seq rdr) (dec line-number))))

(comment
  (read-nth-line "out/SampleCSVFile_1109kb.csv" 55)

  (with-open [rdr (io/reader "https://clojuredocs.org")]
    (->> (line-seq rdr)
         (map (fn [s]
                (inc (count (.getBytes s "UTF-8")))))
         (reduce +)))

  (file-seq (io/as-file "."))

  (filter #(.isFile %)
          (file-seq (io/as-file ".")))
  )

;; ** Запись в файлы и создание дирекоторий

(comment
  (spit "non-existing-dir/file.txt" "content")

  (io/make-parents "non-existing-dir/qwe/asd/file.txt")
  )

;; * Ресурсы

(io/resource "people.edn")

(->> "people.edn"
     io/resource
     slurp
     read-string
     (map :language))

(io/make-parents "result/sample.txt")

;; ** Копирование потоков

(comment
  (io/copy
   (io/file "out/sample.txt")
   (io/file "result/sample.txt"))

  (io/copy
   (io/reader "out/sample.txt")
   (io/writer "result/sample.txt"))

  (with-open [out (io/output-stream (io/file "/tmp/zeros"))]
    (.write out (byte-array 1000)))

  (with-open [in (io/input-stream (io/file "/tmp/zeros"))]
    (let [buf (byte-array 1000)
          n   (.read in buf)]
      (println "Read" n "bytes.")))

  (with-open [in  (io/input-stream (io/file "/tmp/zeros"))
              out (io/output-stream (io/file "/tmp/zeros"))]
    (io/copy in out))
  )

;; * EDN: Extensible Data Notation

(require '[clojure.edn :as edn])

(comment
  (str [{:foo 1}])

  (prn-str {:foo 123 :bar 22})

  (spit "out/data.edn"
        [{:user-id 1 :user-name "Ivan"}
         {:user-id 2 :user-name "Petr"}])

  (edn/read-string (slurp "out/data.edn"))
  )

;; ** java.io.PushbackReader

(import '[java.io PushbackReader])

(comment
  (try
    (with-open [r (io/reader "out/data.edn")]
      (edn/read (PushbackReader. r)))
    (catch Throwable error
      (.getMessage error)))
  )

;; ** Метаданные и расширяемость

(comment
  (def date
    #inst "2023-05-23")

  (type date)

  (spit "out/data.edn"
        "[#User {:id 1 :name \"Ivan\"}
        #User {:id 2 :name \"Petr\"}]")
  )

(defrecord User [id name])

(comment
  (with-open [r (io/reader "out/data.edn")]
    (let [users (edn/read
                 {:readers {'User map->User}}
                 (PushbackReader. r))]
      (type (first users))))
  )

;; ** Использование EDN для конфигурирования программы

(require '[aero.core :as aero])

(comment
  (aero/read-config (io/resource "config.edn"))
  (aero/read-config (io/resource "config.edn") {:profile :test}))
