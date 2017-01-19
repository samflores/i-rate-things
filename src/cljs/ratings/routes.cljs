(ns ratings.routes
  (:require [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [re-frame.core :as re-frame]))

(def routes ["/" {"" :listing
                  "about" :about
                  [:id] :thing}])

(defn- parse-url [url]
  (bidi/match-route routes url))

(defn- dispatch-route [matched-route]
  (re-frame/dispatch [:navigate matched-route]))

(defn app-routes []
  (pushy/start! (pushy/pushy dispatch-route parse-url)))

(def url-for (partial bidi/path-for routes))
