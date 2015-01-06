(ns logjammin.poller
  (:require [crap.logging :refer [logging-future]]
            [crap.logging :refer [with-histogram]]
            [clojure.data.json :as json]
            [clojure.tools.logging :as log]
            [com.stuartsierra.component :as component]
            [metrics.histograms :refer [histogram update!]]
            [metrics.meters :refer [meter mark!]]
            [crap.exceptions :refer [try+]]))


(def events-sent        (meter "events-sent"))
(def events-no-handlers (meter "events-sent-no-handlers"))
(def messages-fired-in  (histogram "messages-fired-in"))
(def send-exceptions    (meter "events-sent-exceptions"))

(defn mark-status-event! [status]
  (mark! (meter (str "events-sent-failed.status." status))))

(defn parse-body [b]
  (try
    (json/read-str b)
    (catch Exception e
      (log/error (format "Exception parsing event body as JSON: Exception: %s | Body: %s"
                   (str e) (str b))))))

(defrecord Poller [config client queue msgbuffer]
  component/Lifecycle

  (start [poller]
    (log/info "Starting new Poller.")
    (let [exit? (atom false)]
      (logging-future
        (while (not @exit?))
          (let [msgs (.receive-events client)]
            (log/debug "Handling messages: " (map :id msgs))
            ;; TODO: Send message to (.msgbuffer poller)
            ))
      (assoc poller :exit exit?)))

  (stop [poller]
    (log/info "Stopping poller")
    (reset! (:exit poller) true)))

(defn make-poller
  "Creates a new Poller with default parameters"
  []
  (->Poller nil nil nil nil))
