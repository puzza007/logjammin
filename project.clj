(defproject logjammin "0.0.1-SNAPSHOT"
  :description "SQS to logstash poller"
  :url "https://github.com/puzza007/logjammin.git"
  :uberjar-name "logjammin.jar"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :pom-location "target/"
  :main ^:skip-aot logjammin.core
  :target-path "target/%s"
  :dependencies [[com.stuartsierra/component "0.2.2"]
                 [org.clojure/clojure "1.6.0"]
                 [sqs-comp "0.0.1"]])
