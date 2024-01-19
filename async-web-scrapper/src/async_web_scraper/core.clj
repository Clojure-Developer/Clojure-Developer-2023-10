(ns async-web-scraper.core
  (:require [clj-http.client :as http]
            [clojure.core.async
             :as async
             :refer [<! <!! >! chan close! go pipeline pipeline-async thread]]
            [clojure.string :as str])
  (:import (org.jsoup Jsoup)))

(defn async-request [opts]
  (thread (http/request opts)))

(defn request-page [base-url href]
  (go (-> (<! (async-request {:url (str base-url href)
                              :method :get}))
          :body
          (Jsoup/parse))))

(defn extract-pagination-hrefs [page]
  (let [external? (fn [ref] (str/includes? ref "https://"))]
    (-> page
        (.select "div[class^=paginator] > a[class^=link]")
        (->> (map #(.attr % "href"))
             (remove external?)))))

(defn extract-articles-hrefs [page]
  (let [external? (fn [ref] (str/includes? ref "https://"))]
    (-> page
        (.select "div[class^=card] a[class^=link]")
        (->> (map #(.attr % "href"))
             (remove external?)))))

(defn extract-data [page]
  (let [author (-> page
                   (.select "div[class^=coAuthor] a p")
                   (.text))
        title (-> (.select page "title")
                  .text)
        text (-> page
                 (.select "div[class^=articleView] p")
                 (->> (map #(.text %))
                      (str/join "\n")))
        words-count (count (str/split text #"\s+"))]
    {:title title
     :author author
     ;; :text text
     :words-count words-count}))

(defn async-scraping [base-url & {:keys [limit-pages entrypoint]
                                  :or {limit-pages 5}}]
  (let [flow-href-c (chan)

        article-href-c (chan 20)
        article-page-c (chan 20)

        out-c (chan 100)]

    ;; request journal pages and extract hrefs to articles from them
    (pipeline-async 1 article-href-c
                    (fn [href result]
                      (go
                        (let [flow-page (<! (request-page base-url href))
                              articles-hrefs (extract-articles-hrefs flow-page)]
                          (doseq [article-href articles-hrefs]
                            (>! result article-href))
                          (close! result))))
                    flow-href-c)

    ;; request article pages by their hrefs
    (pipeline-async 1 article-page-c
                    (fn [href result]
                      (go (->> (<! (request-page base-url href))
                               (>! result))
                          (close! result)))
                    article-href-c)

    ;; extract data from articles pages
    (pipeline 4 out-c (map extract-data) article-page-c)

    (go (let [entry-page (<! (request-page base-url entrypoint))]
          (doseq [href (take limit-pages (extract-pagination-hrefs entry-page))]
            (>! flow-href-c href))
          (close! flow-href-c)))

    out-c))

(comment
  (def flow-page
    (<!! (request-page "https://journal.tinkoff.ru"
                       "/flows/readers-travel/")))

  (def article-page
    (<!! (request-page "https://journal.tinkoff.ru"
                       "/roadtrip-usa-hidden-cost/")))

  (extract-pagination-hrefs flow-page)
  (extract-articles-hrefs flow-page)
  ;; => ("/13-days-in-turkey/"
  ;;     "/travel-with-kids-to-krasnoyarsk/"
  ;;     ...
  ;;     "/roadtrip-usa-hidden-cost/")

  (extract-data article-page)

  (def ch (async-scraping "https://journal.tinkoff.ru"
                          :entrypoint "/flows/readers-travel/"
                          :limit-pages 5))

  (<!! ch)

  (count (<!! (async/into [] ch)))

  ;; => 114
  )
