(ns url-shortener.web
  (:require [ring.middleware.json :as middleware.json]
            [ring.middleware.params :as middleware.params]
            [ring.middleware.resource :as middleware.resource]
            [ring.middleware.keyword-params :as middleware.keyword-params]
            [ring.util.response]
            [compojure.core :as compojure]
            [url-shortener.core :as shortener]))

(defn retain
  ([url]
   (let [id (shortener/shorten! url)]
     (ring.util.response/created id {:id id})))
  ([url id]
   (if-let [id (shortener/shorten! url id)]
     (ring.util.response/created id {:id id})
     {:status 409 :body {:error (format "Short URL %s is already taken" id)}})))

(compojure/defroutes router
  (compojure/GET "/" []
    (ring.util.response/resource-response "index.html" {:root "public"}))
  
  (compojure/POST "/" [url]
    (if (empty? url)
      (ring.util.response/bad-request {:error "No `url` parameter provided"})
      (retain url)))
  
  (compojure/PUT "/:id" [id url]
    (if (empty? url)
      (ring.util.response/bad-request {:error "No `url` parameter provided"})
      (retain url id)))
  
  (compojure/GET "/:id" [id]
    (if-let [url (shortener/url-for id)]
      (ring.util.response/redirect url)
      (ring.util.response/not-found {:error "Requested URL not fount."})))
  
  (compojure/GET "/list/" []
    (ring.util.response/response {:urls (shortener/list-all)})))

(def handler
  (-> router
      (middleware.resource/wrap-resource "public")
      (middleware.params/wrap-params)
      (middleware.keyword-params/wrap-keyword-params)
      (middleware.json/wrap-json-params)
      (middleware.json/wrap-json-response)))

(comment
  (handler {:uri "/"
            :request-method :post
            :params {:url "https://github.com/Clojure-Developer/Clojure-Developer-2023-10"}})

  (handler {:uri "/clj"
            :request-method :put
            :params {:url "https://clojure.org"}})

  (router {:uri "/list/"
           :request-method :get})

  (handler {:uri "/clj"
            :request-method :get})
  )
