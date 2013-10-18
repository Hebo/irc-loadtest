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


(defn print-status
  [channels]
  (println (str "Successful connections: " (count (filter (complement lamina.core/closed?)
                                                    channels))))

  ; Wait on channels to exit
  (->> (apply lamina.core/merge-channels channels)
       (partial lamina.core/wait-for-message)
       (while true)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [[opts msgs & _] (cli args
                ["-p" "--port" "Listen on this port" :parse-fn #(Integer. %)]
                ["-h" "--host" "The hostname"]
                ["-c" "--clients" "Number of clients" :default 1 :parse-fn #(Integer. %)])]
    (-> (repeatedly (:clients opts)
                (partial connect-and-send opts msgs))
        (print-status))))
