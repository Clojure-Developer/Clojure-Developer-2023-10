(ns otus-18.homework.pokemons
    (:require
        [clojure.core.async
         :as async
         :refer [chan <!! >!! <! >! go pipeline-async close! thread pipeline]]
        [clj-http.client :as http]
        [clojure.algo.generic.functor :refer [fmap]]))

(def base-url "https://pokeapi.co/api/v2")
(def pokemons-url (str base-url "/pokemon"))
(def type-url (str base-url "/type"))
(def cache-wrapper
    (let [cache (atom {})]
        (fn [http-map]
            (if-let [cached-result (get @cache http-map)]
                cached-result
                (let [result (http/request http-map)]
                    (swap! cache assoc http-map result)
                    result)))))

(defn async-request [http-map]
    (-> http-map
        cache-wrapper
        :body
        thread))

(defn pageable->urls [pages-urls> url entities-urls>]
    (go
        (let [response (<! (async-request {:url    url
                                           :method :get
                                           :as     :json}))
              next-url (:next response)]
            (doseq [entity (:results response)]
                (>! entities-urls> (:url entity)))
            (if next-url
                (>! pages-urls> next-url)
                (close! pages-urls>))
            (close! entities-urls>))))

(defn urls->entities [url entities-content>]
    (go
        (let [response (<! (async-request {:url    url
                                           :method :get
                                           :as     :json}))]
            (>! entities-content> response)
            (close! entities-content>))))

(defn get-entities-data [initial-page-url parse-fn]
    (let [pages-urls> (chan 2)
          entities-urls> (chan 8)
          entities-data> (chan 8)
          entities-parsed> (chan 8)]
        (pipeline-async 1 entities-urls> (partial pageable->urls pages-urls>) pages-urls>)
        (pipeline-async 8 entities-data> urls->entities entities-urls>)
        (pipeline 8 entities-parsed> (map parse-fn) entities-data>)
        (>!! pages-urls> initial-page-url)
        entities-parsed>))

(defn construct-type-lang-hierarchy [name m]
    [(get-in m [:language :name])
     {name (get-in m [:name])}])

(defn parse-type-names [response]
    (let [name (:name response)]
        [name
         ;TODO transducer smells below
         (into {} (map (partial construct-type-lang-hierarchy name) (:names response)))]))

(defn parse-pokemon-types [response]
    [(:name response)
     (mapv #(get-in % [:type :name]) (:types response))])

(defn extract-types-by-lang [lang types-map]
    ;TODO transducer smells below
    (partial mapv ((reduce (partial merge-with into) {} (vals types-map)) lang)))

(defn get-pokemons
    "Асинхронно запрашивает список покемонов и название типов в заданном языке. Возвращает map, где ключами являются
  имена покемонов (на английском английский), а значения - коллекция названий типов на заданном языке."
    [& {:keys [limit lang] :or {limit 50 lang "ja"}}]
    (let [pokemon-types (->> (get-entities-data type-url parse-type-names)
                             (async/into {})
                             <!!
                             (extract-types-by-lang lang))]
        (->> (async/take limit (get-entities-data pokemons-url parse-pokemon-types))
             (async/into {})
             <!!
             (fmap pokemon-types)
             )))

(comment
    (get-pokemons))

(comment

    (fmap (partial mapv ((reduce (partial merge-with into) {} (vals (get-types))) "ja")) (get-pokemons))

    (map #(get-in (get-types) [% "en"]) ["bug" "flying"])

    (reduce (partial merge-with into) {} (vals (get-types)))


    (defn- get-types
        [& {:keys [limit lang] :or {limit 50 lang "ja"}}]
        (let [pokemon-types (->> (get-entities-data type-url parse-type-names)
                                 (into {})
                                 <!!)]
            pokemon-types))

    (def pika
        {:url          (str pokemons-url "/" "pikachu")
         :method       :get
         :as           :json
         :query-params {:lang "jp"}})

    (def pika
        {:url    type-url
         :method :get
         :as     :json})

    (go (let [start# (. System (nanoTime))
              pokemons-result (<! (async-request {:url          pokemons-url
                                                  :method       :get
                                                  :as           :json
                                                  :query-params {:lang "jp" :limit 50}}))
              pokemon-names (->> pokemons-result
                                 :body
                                 :results
                                 (map :name))]
            (println pokemon-names)
            (println (str "Elapsed time: " (/ (double (- (. System (nanoTime)) start#)) 1000000.0) " msecs"))))


    (time (-> (cache-wrapper pika)
              :body
              keys))

    (let [ch> (async-request pika)]
        (->> ch>
             <!!))

    (let [out> (get-entities-data type-url parse-type-names)]
        (go-loop []
                 (if-let [res (<! out>)]
                     (do (println res) (recur))
                     "end")))

    (get-pokemons)
    )