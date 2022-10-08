(ns ajchemist.imgur.alpha
  (:require
   [clojure.string :as str]
   [clj-http.client :as http]
   [user.ring.alpha :as user.ring]
   ))


(def ^:const +origin+ "https://api.imgur.com")
(def ^:const +authorize-uri+ "https://api.imgur.com/oauth2/authorize")
(def ^:const +token-uri+ "https://api.imgur.com/oauth2/token")


(def ^{:arglists '([request] [request respond raise])}
  client
  (-> http/request
    (user.ring/wrap-transform-request
      (fn [request]
        (update request :as #(or % :json))))
    (user.ring/wrap-transform-request
      (fn [request]
        (cond-> request
          (find request ::token)
          (assoc-in [:headers "Authorization"] (str "Bearer " (::token request))))))
    (user.ring/wrap-meta-response)))


(defn album-images
  [params album-id]
  (client
    (-> params
      (assoc
        :url (str +origin+ "/3/album/" album-id "/images")
        :method :get))))


(defn me-images
  [params]
  (client
    (-> params
      (assoc
        :url (str +origin+ "/3/account/me/images")
        :method :get))))
