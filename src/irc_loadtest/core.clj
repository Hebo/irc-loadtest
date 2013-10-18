(ns irc-loadtest.core
  (:gen-class)
  (:require [aleph.tcp :as tcp]
            [lamina.core :as lamina.core]
            [gloss.core :as gloss]
            [clojure.tools.cli :refer [cli]]))

(defn connect
  [opts]
  (lamina.core/wait-for-result
    (tcp/tcp-client {:host (:host opts),
                     :port (:port opts),
                     :frame (gloss/string :utf-8 :delimiters ["\n" "\r\n"])})))
(defn send-line
    [ch, msg]
    (lamina.core/enqueue ch msg))

(defn handle-line
    "Handle lines
    TODO: respond to pings"
    [ch line]
    (println line))

(defn connect-and-send
  [conn-opts msgs]
  (let [ch (connect conn-opts)]
    (lamina.core/receive-all ch (partial handle-line ch))
    (apply lamina.core/enqueue ch msgs)
    ch))



(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [opts (cli args
                ["-p" "--port" "Listen on this port" :parse-fn #(Integer. %)]
                ["-h" "--host" "The hostname"])]
    (connect-and-send (first opts) (nth opts 1))))
