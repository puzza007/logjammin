(ns logjammin.core
  (:require [com.stuartsierra.component :as component]
            [sqs-comp.client :as c]
            [logjammin.msgbuffer :as msgbuffer]
            [logjammin.config :as config]
            [logjammin.poller :as poller]))


(def running-system (atom nil))

(defn ->System [config-path]
  (component/system-map
    :config (config/->conf config-path)
    :client (component/using
              (c/make-sqs-client)
              [:config])
    :msgbuffer (msgbuffer/->MsgBuffer nil)
    :poller (component/using
              (poller/make-poller)
              [:client :msgbuffer])))

(defn -main []
  (->> (->System "")
       component/start
       (reset! running-system)))
