(ns ratings.subs
 (:require [re-frame.core :as re-frame]
           [clojure.string :refer [includes?]]))

(re-frame/reg-sub
  :ratings
  (fn [{:keys [ratings.db/current-tag ratings.db/ratings ratings.db/sorting-by ratings.db/filter-text]} _]
    (let [compare-fn (if (= sorting-by :ratings.db/rating) #(compare %2 %1) compare)
          filter-text (.toLowerCase (or filter-text ""))
          ratings (vals ratings)
          tagged (if (= current-tag "Tudo")
                   ratings
                   (filter #(some #{current-tag} (:ratings.db/tags %1)) ratings))]
      (sort-by
        sorting-by
        compare-fn
        (if (= "" filter-text)
          tagged
          (filter #(includes? (.toLowerCase (:ratings.db/name %)) filter-text) tagged))))))

(re-frame/reg-sub
  :selected-item
  (fn [db _]
    (when-let [selected-id (:ratings.db/selected-item db)]
      (get-in db [:ratings.db/ratings selected-id]))))

(re-frame/reg-sub
  :current-tag
  (fn [{:keys [ratings.db/current-tag]} _] current-tag))

(re-frame/reg-sub
  :filter-text
  (fn [{:keys [ratings.db/filter-text]} _] filter-text))

(re-frame/reg-sub
  :sort-by
  (fn [{:keys [ratings.db/sorting-by]} _] sorting-by))

(re-frame/reg-sub
  :tags
  (fn [db _]
    (->> db
         :ratings.db/ratings
         vals
         (map :ratings.db/tags)
         (reduce concat)
         distinct
         sort)))
