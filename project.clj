(defproject irc-loadtest "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [aleph "0.3.0"]
                 [org.clojure/tools.cli "0.2.4"]]
  :main irc-loadtest.core
  :profiles {:uberjar {:aot :all}})
