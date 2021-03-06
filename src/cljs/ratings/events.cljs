(ns ratings.events
 (:require [re-frame.core :as re-frame]
           [cljs.spec :as s]
           [cljs.pprint :refer [pprint]]
           [clojure.set :refer [rename-keys]]
           [ratings.routes :as routes]
           [ratings.db :as db]))

(defn check-and-throw
  [db-spec db]
  (when-not (s/valid? db-spec db)
    (throw (ex-info (str "Invalid database: " (s/explain-str db-spec db)) {}))))

(defn init-routes [_] (routes/app-routes))

(def check-spec-interceptor (re-frame/after (partial check-and-throw :ratings.db/db-schema)))
(def routes-interceptor (re-frame/after init-routes))

(def interceptors [check-spec-interceptor])

(def keys-mapping {"id" :ratings.db/id
                   "name" :ratings.db/name
                   "tags" :ratings.db/tags
                   "image-url" :ratings.db/image-url
                   "comment" :ratings.db/comment
                   "rating" :ratings.db/rating})

(defn- ns-keys [rating]
  (rename-keys rating keys-mapping))

(defn- by-id [ratings]
  (reduce #(assoc %1 (:ratings.db/id %2) %2) {} ratings))

(re-frame/reg-event-db
  :initialize
  (conj interceptors routes-interceptor)
  (fn [_ _]
    db/default-value))

(re-frame/reg-event-db
  :select-tag
  interceptors
  (fn [db [_ tag]]
    (assoc db :ratings.db/current-tag tag)))

(re-frame/reg-event-db
  :select-item
  interceptors
  (fn [db [_ item]]
    (assoc db :ratings.db/selected-item item)))

(re-frame/reg-event-db
  :update-filter
  interceptors
  (fn [db [_ text]]
    (assoc db :ratings.db/filter-text text)))

(re-frame/reg-event-db
  :update-sorting
  interceptors
  (fn [db [_ field]]
    (assoc db :ratings.db/sorting-by field)))

(re-frame/reg-event-db
  :load-ratings
  interceptors
  (fn [db [_ ratings]]
    (assoc db
           :ratings.db/ratings
           (->> ratings (remove nil?) (map ns-keys) by-id))))

(defn- send-pageview [page]
  (js/ga "set"  "page"  page)
  (js/ga "send" "pageview"))

(re-frame/reg-event-db
  :navigate
  interceptors
  (fn [db [_ {:keys [handler route-params]}]]
    (case handler
      :thing (do
               (send-pageview (str "/"  (:id route-params)))
               (assoc db :ratings.db/selected-item (js/parseInt (:id route-params))))
      :listing (do
                 (send-pageview "/")
                 (assoc db :ratings.db/selected-item nil))
      db)))
