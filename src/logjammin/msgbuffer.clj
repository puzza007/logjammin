(ns logjammin.msgbuffer
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.logging :as log]))


(defprotocol MsgAppender
  (append [msg-appender msg]
    "Appends a message into the buffer"))

(defrecord MsgBuffer [state]
  component/Lifecycle

  (start [msgbuffer]
    (log/info "Starting new MsgBuffer.")
    (let [state (agent [])]
      (assoc msgbuffer :state state)))

  (stop [_]
    )

  MsgAppender
  (append [msgbuffer msg]
    (send state conj msg)
    (when (> 100 (count @state))
      (send flush-msgs state))))

(defn- flush-msgs [state]
  [])
