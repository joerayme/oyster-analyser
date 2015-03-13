(defproject oyster-analyser "0.1.0-SNAPSHOT"
  :description "Analyses Oyster data extractions"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.csv "0.1.2"]
                 [clj-time "0.9.0"]]
  :main ^:skip-aot oyster-analyser.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:plugins [[com.jakemccrary/lein-test-refresh "0.6.0"]]}})
