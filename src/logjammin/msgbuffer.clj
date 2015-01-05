(ns logjammin.msgbuffer
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.logging :as log]))

(defprotocol MsgAppender
  (append [msg-appender msg]
    "Find the associated handlers for an event"))

(defrecord MsgBuffer [state]
  component/Lifecycle

  (start [msgbuffer]
    (log/info "Starting new Poller.")
    (let [state (agent [])]
      (assoc msgbuffer :state state)))

  (stop [_]
    )

  MsgAppender
  (append [msgbuffer msg]
    (send state conj msg)
    (when (> 100 (count @state))
      (send flush state))))

(defn- flush [state]
  [])
