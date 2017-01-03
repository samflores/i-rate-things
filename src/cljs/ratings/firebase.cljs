(ns ratings.firebase
  (:require [cljsjs.firebase]))

(defn init [config]
 (.initializeApp js/firebase (clj->js config))
 (.database js/firebase))

(defn listen [db path handler]
  (let [db-ref (.ref db path)]
   (.on db-ref "value" handler)))

