(ns ratings.core
  (:require-macros [ratings.macros :as m])
  (:require [cljs.reader :refer [read-string]]
            [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [ratings.views :as views]
            [ratings.firebase :as firebase]
            [ratings.events]
            [ratings.subs]))

(def config (read-string (m/slurp "resources/firebase.edn")))

(defn ^:export init []
 (reagent/render-component
   [views/application]
   (.getElementById js/document "container")))

(re-frame/dispatch [:initialize])
(firebase/listen
  (firebase/init (:firebase config))
  "ratings"
  #(re-frame/dispatch [:load-ratings (js->clj (.val %))]))
