(ns ajchemist.imgur.alpha.tools-test
  (:require
   [clojure.string :as str]
   [clojure.java.shell :as jsh]
   [ajchemist.imgur.alpha :as imgur]
   [ajchemist.imgur.alpha.oauth2 :as oauth2]
   [ajchemist.imgur.alpha.tools :as tools]
   ))


(defn pass
  [pass-name]
  (str/trim-newline (:out (jsh/sh "pass" pass-name))))


(comment
  (tools/authorize
    {:client-id (pass "io/github/ajchemist/imgur/client-id")})


  (tools/refresh-token
    {:encoded       "..."
     :client-id     (pass "io/github/ajchemist/imgur/client-id")
     :client-secret (pass "io/github/ajchemist/imgur/client-secret")})


  (imgur/me-images {::imgur/token "..."})


  (imgur/me-images {::imgur/token "..."})


  "{\"data\":{\"error\":\"The access token provided is invalid.\",\"request\":\"\\/3\\/account\\/me\\/images\",\"method\":\"GET\"},\"success\":false,\"status\":403}",
  )
