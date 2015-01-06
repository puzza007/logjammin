(defproject logjammin "0.0.1-SNAPSHOT"
  :description "SQS to logstash poller"
  :url "https://github.com/puzza007/logjammin.git"
  :uberjar-name "logjammin.jar"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :pom-location "target/"
  :main ^:skip-aot logjammin.core
  :profiles {:uberjar {:aot :all}}
  :target-path "target/%s"
  :dependencies [[com.stuartsierra/component "0.2.2"]
                 [clj-yaml "0.4.0"]
                 [org.clojure/clojure "1.6.0"]
                 [org.clojure/data.json "0.2.5"]
                 [joda-time "2.6"]
                 [sqs-comp "0.0.2" :exclusions [joda-time]]])
