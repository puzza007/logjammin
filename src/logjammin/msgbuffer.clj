(ns logjammin.msgbuffer
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.logging :as log])
  (:import [java.util Timer TimerTask]))


(def flush-period 10000)

(defn- flush-msgs [state]
  [])

(defn- ->buffer-flusher [buffer]
  (let [task (proxy [TimerTask] []
               (run []
                 ;; TODO: Send messages to logstash
                 (log/debug "Flushing buffer")
                 (println "Flushing buffer")
                 (send (:state buffer) flush-msgs)))]
    (doto (Timer.)
      (.scheduleAtFixedRate task flush-period flush-period))))

(defprotocol MsgAppender
  (append [msg-appender msg]
    "Appends a message into the buffer"))

(defrecord MsgBuffer [state]
  component/Lifecycle

  (start [msgbuffer]
    (log/info "Starting new MsgBuffer.")
    (let [state (agent [])
          msgbuffer' (assoc msgbuffer :state state)]
      (->buffer-flusher msgbuffer')
      msgbuffer'))

  (stop [_]
    (when state
      (send state (fn [_] (throw (Exception. "stop"))))))

  MsgAppender
  (append [msgbuffer msg]
    (send state conj msg)
    (when (> 100 (count @state))
      (send flush-msgs state))))
