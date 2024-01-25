(ns web-scraper.core
  (:require [clj-http.client :as http])
  (:import [org.jsoup Jsoup]
           [java.time LocalDate]))

;;
;; First look
;;
(def ^String html
  (-> (http/get "https://journal.tinkoff.ru/flows/readers-travel/")
      :body))

(def soup (Jsoup/parse ^String html))

;; Jsoup CSS selector syntax
;; https://jsoup.org/cookbook/extracting-data/selector-syntax

;; class Element
;; https://jsoup.org/apidocs/org/jsoup/nodes/Element.html

(def cards
  (.select soup "div[class^=item] > div[class^=card]"))

(comment
  (count cards)
  
  (-> (first cards)
      (.select "div[class^=header] h3[class^=title]")
      (.text)) 

  (-> (first cards)
      (.selectFirst "div[class^=header]")
      (.selectFirst "a[class^=link]")
      (.attr "href")))

;;
;; First attempt
;;
(defn get-headers [flow]
  (let [html  ^String (-> (str "https://journal.tinkoff.ru/flows/" flow)
                          (http/get)
                          :body)
        soup  (Jsoup/parse html)
        cards (.select soup "div[class^=item] > div[class^=card]")]
    (mapv (fn [card]
            (let [header (.selectFirst card "div[class^=header]")
                  title  (-> (.select header "h3[class^=title]")
                             (.text))
                  date   (-> (.selectFirst header "div[class^=info] time")
                             (.attr "datetime")
                             (LocalDate/parse))
                  link   (-> (.selectFirst header "a[class^=link]")
                             (.attr "href"))
                  author (-> (.select header "div[class^=author] div[class^=name]")
                             (.text))]
              {:title  title
               :date   date
               :author author
               :link   link}))
          cards)))

(comment
  (get-headers "readers-travel"))

;;
;; With pagination
;;
(defn get-flow-page [flow page]
  (let [html ^String (-> (format "https://journal.tinkoff.ru/flows/%s/page/%s/" flow page)
                         (http/get)
                         :body)]
    (Jsoup/parse html)))

(defn get-next-page [page current-page]
  (let [next-page (inc current-page)
        next?     (-> page
                      (.selectFirst (format "div[class^=paginator] > a[class^=link]:containsOwn(%s)" next-page))
                      (some?))]
    (when next?
      next-page)))

(defn extract-headers [page]
  (->> (.select page "div[class^=item] > div[class^=card]")
       (mapv (fn [card]
               (let [header (.selectFirst card "div[class^=header]")
                     title  (-> (.select header "h3[class^=title]")
                                (.text))
                     date   (-> (.selectFirst header "div[class^=info] time")
                                (.attr "datetime")
                                (LocalDate/parse))
                     link   (-> (.selectFirst header "a[class^=link]")
                                (.attr "href"))
                     author (-> (.select header "div[class^=author] div[class^=name]")
                                (.text))]
                 {:title  title
                  :date   date
                  :author author
                  :link   link})))))

(defn get-flow-headers [flow current-page]
  (lazy-seq
   (let [page      (get-flow-page flow (or current-page 1))
         next-page (get-next-page page current-page)
         headers   (extract-headers page)]
     (cons headers
           (when (some? next-page)
             (get-flow-headers flow next-page))))))

(comment
  (->> (get-flow-headers "readers-travel" 1)
       (flatten)
       (take 40))
  
  (->> (get-flow-headers "diary" 1)
       (flatten)
       (take 30)))









;;
;; Generalization
;;
(defprotocol PScraper
  (next-request [_ opts])
  (extract-data [_ page])
  (next-options [_ page opts])
  (continue? [_ opts]))

(defn sequential-scrapping [scraper options]
  (lazy-seq
   (let [request      (next-request scraper options)
         html         ^String (-> request http/request :body)
         page         (Jsoup/parse html)
         page-data    (extract-data scraper page)
         next-options (next-options scraper page options)]
     (cons page-data
           (when (continue? scraper next-options)
             (sequential-scrapping scraper next-options))))))


;; Headers scraper specific part
(defrecord HeadersScraper [base-url]
  PScraper
  (next-request [_ {:keys [flow page]}]
    (let [url (format "%s/flows/%s/page/%s/" base-url flow page)]
      {:method :get
       :url    url}))

  (extract-data [_ page]
    (let [cards (.select page "div[class^=item] > div[class^=card]")]
      (->> cards
           (mapv (fn [card]
                   (let [header (.selectFirst card "div[class^=header]")
                         title  (-> (.select header "h3[class^=title]")
                                    (.text))
                         date   (-> (.selectFirst header "div[class^=info] time")
                                    (.attr "datetime")
                                    (LocalDate/parse))
                         link   (-> (.selectFirst header "a[class^=link]")
                                    (.attr "href"))
                         author (-> (.select header "div[class^=author] div[class^=name]")
                                    (.text))]
                     {:title  title
                      :date   date
                      :author author
                      :link   (str base-url link)}))))))

  (next-options [_ page {current-page :page :as opts}]
    (let [next-page (inc current-page)
          next?     (-> page
                        (.selectFirst (format "div[class^=paginator] > a[class^=link]:containsOwn(%s)" next-page))
                        (some?))]
      (assoc opts :page (when next? next-page))))

  (continue? [_ opts]
    (some? (:page opts))))

(comment
  (->> (sequential-scrapping (->HeadersScraper "https://journal.tinkoff.ru")
                             {:flow "readers-travel" :page 1})
       (flatten)
       (take 30)))
