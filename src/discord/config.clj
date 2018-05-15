(ns discord.config
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log])
  (:import [java.io IOException]))

(defonce global-bot-settings "data/settings/settings.json")

;;; Saving, loading, and checking config files
(defmulti file-io
  "Manages the saving and loading of file-io files"
  (fn [filename operation & {:keys [data]}]
    operation))

(defmethod file-io :save [filename _ & {:keys [data]}]
  (spit filename (json/write-str data)))

(defmethod file-io :load [filename _]
  (try
    (json/read-str (slurp filename) :key-fn keyword)
    (catch IOException ioe
      (log/error ioe)
      [])))

(defmethod file-io :check [filename _]
  (let [f (io/as-file filename)]
    (if (not (.exists f))
      (do
        ;; Create the parent directories
        (->> f
             (.getParentFile)
             (.getAbsolutePath)
             (io/file)
             (.mkdirs))
        (.createNewFile f)))
    (.exists f)))


(defn get-prefix
  ([] (get-prefix global-bot-settings))
  ([filename] (:prefix (file-io filename :load))))

(defn get-bot-name
  ([] (get-bot-name global-bot-settings))
  ([filename] (:bot-name (file-io filename :load))))

(defn get-cog-folders
  ([] (get-cog-folders global-bot-settings))
  ([filename] (:cog-folders (file-io filename :load))))

(defn get-token
  ([] (get-token global-bot-settings))
  ([filename] (:token (file-io filename :load))))
