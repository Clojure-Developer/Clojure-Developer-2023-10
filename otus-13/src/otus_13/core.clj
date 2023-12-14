(ns otus-13.core
  (:require [clj-http.client :as http]
            [clojure.java.io :as io]
            [cheshire.core :as cheshire]
            [clojure.data.csv :as csv])
  (:import [java.io InputStream]))

(def host "https://httpbin.org/")

(defn make-url
  ([] host)
  ([suffix] (str host suffix)))

(slurp "https://httpbin.org/json")


;; GET, POST, PUT, DELETE

(http/get (make-url "json"))


(http/get (make-url "json")
          {:as :json})


(http/get (make-url "headers")
          {:headers {:x-otus-course "Clojure Developer"}
           :as      :json})


(http/get (make-url "headers")
          {:headers {:x-otus-course "Clojure Developer"}
           :accept  "text/html"
           :as      :json})


;; All request options

;; :url
;; :method
;; :query-params
;; :basic-auth
;; :content-type
;; :accept
;; :accept-encoding
;; :as
;; :headers
;; :body
;; :connection-timeout
;; :connection-request-timeout
;; :connection-manager
;; :cookie-store
;; :cookie-policy
;; :multipart
;; :query-string
;; :redirect-strategy
;; :max-redirects
;; :retry-handler
;; :request-method
;; :scheme
;; :server-name
;; :server-port
;; :socket-timeout
;; :uri
;; :response-interceptor
;; :proxy-host
;; :proxy-port
;; :http-client-context
;; :http-request-config
;; :http-client
;; :proxy-ignore-hosts
;; :proxy-user
;; :proxy-pass
;; :digest-auth
;; :ntlm-auth
;; :multipart-mode
;; :multipart-charset


(http/post (make-url "post")
           {:body         "{\"foo\": \"bar\"}"
            :content-type :json
            :as           :json})


;; as a urlencoded body
(http/post (make-url "post")
           {:form-params {:foo "bar"}
            :as          :json})


(http/post (make-url "post")
           {:form-params  {:foo "bar"}
            :content-type :json
            :as           :json})


(http/put (make-url "put")
          {:as :json})


(http/delete (make-url "delete")
             {:as :json})


(http/request {:url    (make-url "delete")
               :method :delete
               :as     :json})





;; coercion

;; :byte-array, :json, :json-string-keys, :transit+json, :transit+msgpack, :clojure,
;; :x-www-form-urlencoded, :stream, :reader

(def response
  (http/get (make-url "stream/10")
            {:as :stream}))


(type (:body response))

(isa? (class (:body response)) InputStream)

(-> (io/reader (:body response))
    (line-seq)
    (first)
    (cheshire/parse-string true))

(->> (io/reader (:body response))
     (line-seq)
     (mapv #(cheshire/parse-string % true)))


(let [response (http/get (make-url "stream/10") {:as :reader})]
  (with-open [reader (:body response)]
    (doall
     (for [line (line-seq reader)]
       (cheshire/parse-string line true)))))





;; custom CSV coercion
(defmethod http/coerce-response-body :csv [_request {:keys [body] :as response}]
  (if (or (http/server-error? response)
          (http/client-error? response))
    response
    (let [v (-> (slurp body :encoding "Windows-1251")
                (csv/read-csv :separator \;))]
                              (assoc response :body v))))


(-> (http/get "https://iss.moex.com/iss/engines/stock/markets.csv"
              {:as :csv})
    :body)



;; exceptions, slingshot

(http/get (make-url "status/400"))


(http/get (make-url "status/500"))


(http/get (make-url "status/500")
          {:throw-entire-message? true})


(http/get (make-url "status/500")
          {:throw-exceptions false})


(http/server-error?
 (http/get (make-url "status/500")
           {:throw-exceptions false}))


(http/get (make-url "status/250")
          {:unexceptional-status #(<= 200 % 249)})





(require '[slingshot.slingshot :refer [throw+ try+]])


(try+
 (throw+ {:foo "bar"})
 (throw+ 152)
 (throw+ (ex-info "Error" {:foo "baz"})) ;; doesn't catch (wierd)!
 (throw+ (Exception. "Exception"))
 (throw+ (Throwable. "Error")) 

 (catch [:foo "bar"] thrown-map
   (println "caught a map")
   (println thrown-map))

 (catch integer? i
   (println "caught a number")
   (println i)) 

 (catch {:data {:foo "bar"}} thrown-map
   (println "caught a map")
   (println thrown-map))
  
 (catch Exception ex
   (println "caught exception")
   (println ex))

 (catch Throwable t
   (println "caught throwable")
   (println t))
 )



(try+
 (http/get (make-url "status/403"))
 (http/get (make-url "status/404"))
 (http/get (make-url "status/500"))

 (catch [:status 403] {:keys [request-time headers body]}
   (println "403" request-time headers))

 (catch [:status 404] {:keys [request-time headers body]}
   (println "NOT Found 404" request-time headers body))

 (catch Object _
   (println "unexpected error" (:message &throw-context))))


;; async request
(http/get (make-url "delay/5")
          {:async? true :as :json}
          (fn [response] (println "response is:" (:body response)))
          (fn [exception] (println "exception message is: " (.getMessage exception))))



;; pagination
(defn get-data-page [page]
  (println "page request" page)
  (-> (http/post (make-url "anything")
                 {:as           :json
                  :content-type :json
                  :form-params  {:data (->> (range)
                                            (drop (* page 5))
                                            (take 5))
                                 :next (when (< page 10)
                                         (inc page))}})
      :body :data
      (cheshire/parse-string true)))


(defn get-paginated-data [current-page]
  (lazy-seq
   (let [page      (get-data-page current-page)
         page-data (:data page)
         next-page (:next page)]
     (cons page-data
           (when (some? next-page)
             (get-paginated-data next-page))))))

(def data
  (flatten (get-paginated-data 0)))

(type data)

(take 10 data)
(take 20 data)


(comment
  ;; example of chinking
  (def xx (map #(do (prn %) %) (range 0 100)))
  (take 1 xx)
  )


;; iteration is a seqable/reducible object (from Clojure 1.11)
(def data-2
  (->> (iteration get-data-page
                  :initk 0
                  :kf :next
                  :vf :data)
       (sequence cat)))

(take 10 data-2)


;; lazy concat
(defn lazy-concat [colls]
  (lazy-seq
   (when-first [c colls]
     (lazy-cat c (lazy-concat (rest colls))))))


(def data-3
  (->> (iteration get-data-page
                  :initk 0
                  :kf :next
                  :vf :data)
       (lazy-concat)))


(->> (get-paginated-data 0)
     (lazy-concat)
     (take 10))
