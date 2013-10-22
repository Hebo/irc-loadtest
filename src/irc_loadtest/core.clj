(ns irc-loadtest.core
  (:gen-class)
  (:require [aleph.tcp :as tcp]
            [lamina.core :as lamina]
            [gloss.core :as gloss]
            [clojure.tools.cli :refer [cli]]))

(declare connect-and-send)
(def ^:private connect-opts (atom {}))
(def ^:private connect-msgs (atom '()))

(defn connect
  [opts]
  (lamina/wait-for-result
    (tcp/tcp-client {:host (:host opts),
                     :port (:port opts),
                     :frame (gloss/string :utf-8 :delimiters ["\n" "\r\n"])})))

(defn handle-line
  "Handle lines"
  [line])

(defn handle-close
  [id]
  (println "Client" id "disconnected. Reconnecting...")
  (connect-and-send id @connect-opts @connect-msgs))

(defn connect-and-send
  [id conn-opts msgs]
  (println "Client" id "connecting!")
  (let [ch (connect conn-opts)]
    (lamina/receive-all ch handle-line)
    (lamina/on-closed ch handle-close)
    (apply lamina/enqueue ch msgs)
    ch))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [[opts msgs & _] (cli args
                             ["-p" "--port" "Listen on this port" :parse-fn #(Integer. %)]
                             ["-h" "--host" "The hostname"]
                             ["-c" "--clients" "Number of clients" :default 1 :parse-fn #(Integer. %)])]
    (reset! connect-opts opts)
    (reset! connect-msgs msgs))
  (dotimes [n (:clients @connect-opts)]
    (Thread/sleep 200)
    (connect-and-send n @connect-opts @connect-msgs))
  (println "Connections done!"))
