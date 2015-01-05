(ns logjammin.config
  (:require [clj-yaml.core :as yaml]
            [clojure.tools.logging :as log]
            [com.stuartsierra.component :as component]))


(defn read-configuration
  "Read the YAML file into a configuration map

  The configuration file should be a YAML file containing the
  available receivers for a given event."
  [path]
  (-> path
    slurp
    yaml/parse-string))

(defrecord Configuration [conf path]

  component/Lifecycle
  (start [configuration]
    (if conf
      configuration
      (let [config-data (read-configuration path)]
        (log/info "Starting with configuration:" config-data)
        (assoc configuration :conf config-data))))

  (stop [configuration]
    ;; Nothing to do here
    ))

(defn ->conf
  "Takes a path to a YAML file and readies a Configuration record"
  [path-or-map]
  (if (string? path-or-map)
    (->Configuration nil path-or-map)
    (->Configuration path-or-map nil)))
