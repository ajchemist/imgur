(ns ajchemist.imgur.alpha.tools
  (:require
   [clojure.java.browse :as browse]
   [ring.util.codec :as codec]
   [ajchemist.imgur.alpha :as imgur]
   [ajchemist.imgur.alpha.oauth2 :as oauth2]
   ))


(defn authorize
  [{:keys [client-id]}]
  (let [client-id (str client-id)]
    (browse/browse-url
      (oauth2/authorize-uri
        nil
        {:authorize-uri imgur/+authorize-uri+
         :client-id     client-id}))))


(defn refresh-token
  [{:keys [client-id client-secret encoded]}]
  (try
    (prn
      (oauth2/refresh-access-token
        (select-keys
          (codec/form-decode encoded)
          ["refresh_token"])
        {:token-uri     imgur/+token-uri+
         :client-id     client-id
         :client-secret client-secret}))
    (catch clojure.lang.ExceptionInfo e
      (let [data (ex-data e)]
        (println (ex-message e) (:status data) (get-in data [:request :http-url]) (:body data))))))
