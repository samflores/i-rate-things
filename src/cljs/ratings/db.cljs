(ns ratings.db
 (:require [cljs.spec :as s]
           [re-frame.core :as re-frame]))

(s/def ::id integer?)
(s/def ::tags integer?)
(s/def ::name string?)
(s/def ::image-url string?)
(s/def ::tags (s/* string?))
(s/def ::comment string?)
(s/def ::rating integer?)

(s/def ::current-tag string?)
(s/def ::filter-text string?)
(s/def ::sorting-by keyword?)
(s/def ::selected-item (s/or :int integer? :nil nil?))

(s/def ::a-rating (s/keys :req [::id ::name ::rating ::image-url ::tags] :opt [::comment]))
(s/def ::ratings (s/and (s/map-of ::id ::a-rating) map?))
(s/def ::db-schema (s/keys :req [::current-tag ::filter-text ::selected-item ::sorting-by ::ratings]))

(def default-value
  {::current-tag "Tudo"
   ::filter-text ""
   ::selected-item nil
   ::sorting-by ::name
   ::ratings {}})
