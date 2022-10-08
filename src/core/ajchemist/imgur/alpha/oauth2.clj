(ns ajchemist.imgur.alpha.oauth2
  (:require
   [clojure.string :as str]
   [clj-http.client :as http]
   [ring.util.codec :as codec]
   [crypto.random :as random]
   )
  (:import
   java.time.Instant
   java.util.Date
   ))


;;


(set! *warn-on-reflection* true)


;;


(defn- coerce-to-int
  [n]
  (if (int? n)
    n
    (parse-long n)))


(defn random-state
  []
  (-> (random/base64 9) (str/replace "+" "-") (str/replace "/" "_")))


;;


(defn authorize-uri
  ^String
  [form-params profile]
  (let [^String uri (:authorize-uri profile)]
    (str uri
         (if (str/includes? uri "?") "&" "?")
         (codec/form-encode
           (-> form-params
             (update "response_type" #(or % (:authorize/response_type profile "token")))
             (update "client_id" #(or % (:client-id profile)))
             (update "state" #(or % (random-state))))))))


(defn- format-access-token
  [{{:strs [access_token expires_in refresh_token] :as body} :body}]
  (cond-> {:token access_token
           :extra-data (dissoc body "access_token" "expires_in" "refresh_token")}
    expires_in    (assoc :expires (Date/from (.plusSeconds (Instant/now) (-> expires_in coerce-to-int))))
    refresh_token (assoc :refresh-token refresh_token)))


(defn refresh-access-token
  "use with try-catch block"
  [form-params profile]
  {:pre [(find form-params "refresh_token")]}
  (format-access-token
    (http/post
      (:token-uri profile)
      {:save-request? true
       :accept        :json
       :as            :json-string-keys
       :form-params
       (-> form-params
         (update "client_id" #(or % (:client-id profile)))
         (update "client_secret" #(or % (:client-secret profile)))
         (update "grant_type" #(or % (:token/grant_type profile "refresh_token"))))})))


;;


(set! *warn-on-reflection* false)
