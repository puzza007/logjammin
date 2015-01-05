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

;; (defn spray-n-pray
;;   "Sends an event to any handlers that are interested."
;;   [config event]
;;   (let [body (parse-body (:body event))
;;         handlers (.event->handlers config body)]
;;     (if (and body handlers)
;;       (apply-handlers config event handlers)
;;       (log-missing-handlers event))))

(defrecord Poller [config client queue msgbuffer]
  component/Lifecycle

  (start [poller]
    (println "Starting new Poller.")
    (let [exit? (atom false)
          state (agent [])]
      (logging-future
        (while (not @exit?))
          (let [msgs (.receive-events client)]
            (log/debug "Handling messages: " (map :id msgs))
            (println msgs)
            ;; (with-histogram messages-fired-in
            ;;   (mapv #(when (spray-n-pray config %)
            ;;            (.delete-message client %))
            ;;         msgs))
            ))
      (println "Started poller")
      (assoc poller :exit exit?)))

  (stop [poller]
    (log/info "Stopping poller")
    (reset! (:exit poller) true)))

(defn make-poller
  "Creates a new Poller with default parameters"
  []
  (->Poller nil nil nil nil))
