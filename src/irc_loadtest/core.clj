(ns irc-loadtest.core
  (:gen-class)
  (:require [aleph.tcp :as tcp]
            [lamina.core :as lamina]
            [gloss.core :as gloss]
            [clojure.tools.cli :refer [cli]]))

(defn connect
  [opts]
  (lamina/wait-for-result
    (tcp/tcp-client {:host (:host opts),
                     :port (:port opts),
                     :frame (gloss/string :utf-8 :delimiters ["\n" "\r\n"])})))

(defn handle-line
    "Handle lines
    TODO: respond to pings"
    [line]
    ; (println line))
    )

(defn handle-close
  []
  (println "channel closed!"))

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
    (dotimes [n (:clients opts)]
                    (Thread/sleep 1000)
                    (connect-and-send n opts msgs))
    (println "Connections done!")))
