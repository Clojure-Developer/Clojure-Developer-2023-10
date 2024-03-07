(ns otus-18.homework.pokemons
  (:require [clj-http.client :as client]
            [cheshire.core :as cheshire]
            [clojure.core.async :as a :refer [<! <!! >!
                                              chan close!
                                              go  onto-chan!
                                              thread]]))

(def base-url "https://pokeapi.co/api/v2")
(def pokemons-url (str base-url "/pokemon"))
(def type-path (str base-url "/type"))

(def n-concurency 5)
(defn extract-pokemon-name [pokemon]
  (:name pokemon))

(defn extract-type-name [pokemon-type lang]
  (->> (:names pokemon-type)
       (filter (fn [type-name] (= lang (-> type-name :language :name))))
       (first)
       :name))


(defn async-get [url]
  (thread (client/get url)))

(defn parse-str [s]
  (cheshire/parse-string s true))

(defn get-parse-xform [url xform]
  (->> (client/get url)
       :body
       parse-str
       xform))

(defn get-and-parse
  "xform may be nil"
  [url & xform]
  (go (as-> (<! (async-get url)) m
        (:body m)
        (cheshire/parse-string m true)
        (if (some? xform) ((first xform) m) m))))

; генерируем урлы получения списка покемонов, например по 20 штук
(defn generate-pokemon-urls
  ([total] (generate-pokemon-urls total 20))
  ([total batch-size]
   (loop [offset 0
          limit batch-size
          res []]
     (if (< offset total)
       (recur (+ offset batch-size)
              (min batch-size (- total offset limit))
              (conj res (str pokemons-url "?offset=" offset "&limit=" limit)))
       res))))

(defn translated-types [lang]
  (let [in> (chan 20)
        out> (chan 20)
        blocking-get-type-name (fn [u] (get-parse-xform u (fn [r] [(:name r) (extract-type-name r lang)])))]

    (a/pipeline-blocking n-concurency out> (map blocking-get-type-name) in>)

    (go (->> (<! (get-and-parse type-path))
             :results
             (map :url)
             (onto-chan! in>)))

    (let [result (<!! (a/into {} out>))]
      (close! in>)
      (close! out>)
      result)))

(defn pokemons-types [count lang]
  (let [type-names-lang (translated-types lang)
        in> (chan 20)
        mdl> (chan count)
        out> (chan count)
        async-fn (fn [url out*]
                   (go
                     (let [arr (get-parse-xform url :results)]
                       (doseq [x arr]
                         (>! out* x)))
                     (close! out*)))
        bl-parse-poke (fn [{name :name url :url}]
                        (let [body-types (get-parse-xform url :types)
                              type-names (mapv (fn [t] (type-names-lang (get-in t [:type :name]))) body-types)]
                          {:name name :types type-names}))]

    ; пайплайны
    (a/pipeline-async n-concurency mdl> async-fn in>)
    (a/pipeline-blocking n-concurency out> (map bl-parse-poke) mdl>)

    ; начало обработки
    (go (a/onto-chan! in> (generate-pokemon-urls count count)))

    (let [result (<!! (a/into [] out>))]
      (close! in>)
      (close! mdl>)
      (close! out>)
      result)))

(defn get-pokemons
  "Асинхронно запрашивает список покемонов и название типов в заданном языке. Возвращает map, где ключами являются
  имена покемонов (на английском английский), а значения - коллекция названий типов на заданном языке."
  [& {:keys [limit lang] :or {limit 50 lang "ja"}}]
  (pokemons-types limit lang))

(comment
  (pokemons-types 12 "ja"))
