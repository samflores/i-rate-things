(ns ratings.views
 (:require [re-frame.core :as re-frame]
           [ratings.routes :as routes]))

(defn- dispatch [k]
 (fn  [ev]
   (.preventDefault ev)
   (re-frame/dispatch k)))

(defn- tag-item [tag]
  [:li.tag {:class tag}])

(defn- rating-stars [id n]
  [:div
   (map-indexed
     #(vector :img.star {:key (str "star-" id "-" %1)
                         :src (if (< %2 n) "gold-star.png" "white-star.png")})
     (range 5))])

(defn- thing [{:keys [ratings.db/id ratings.db/tags ratings.db/name ratings.db/rating ratings.db/image-url] :as rating}]
  [:li.item-entry {:key id}
   [:a {:href (routes/url-for :thing :id id)}
    [:article
     [:div.cover-wrapper
      [:img.cover {:src image-url}]]
     ; TODO: display an icon for each tag
     ; [:ul.tags
     ;  (map tag-item tags)]
     [:h1 name]
     [:footer (rating-stars id rating)]]]])

(defn- content []
  (let [things (re-frame/subscribe [:ratings])]
    [:ul.items-list
     (map thing @things)]))

(defn- tag-slug [tag]
  (.toLowerCase (apply str (replace {" " "-"} tag))))

(defn- nav-item [current-tag tag]
  [:li {:key tag :class (when (= tag current-tag) "selected")}
   [:a {:href (str "/tags/" (tag-slug tag))
        :on-click (dispatch [:select-tag tag])}
    tag]])

(defn- sidebar []
  (let [tags (re-frame/subscribe [:tags])
        current-tag (re-frame/subscribe [:current-tag])]
    [:aside
     [:img {:src "big-star.png"}]
     [:ul.nav
      (map (partial nav-item @current-tag) (concat ["Tudo"] @tags))]]))

(defn- sort-item [curr-field field label]
  [:li {:class (when (= curr-field field) "selected")}
   [:a {:href "#" :on-click (dispatch [:update-sorting field])}
    label]])

(defn- sorting-list []
 (let [sorting-by  (re-frame/subscribe [:sort-by])
       sort-item (partial sort-item @sorting-by)]
   [:ul.sorting
    (sort-item :ratings.db/name "Nome")
    (sort-item :ratings.db/rating "Nota")]))

(defn- header []
  (let [current-tag (re-frame/subscribe [:current-tag])
        filter-text (re-frame/subscribe [:filter-text])]
    [:header
     [:h1 (str @current-tag)]
     [sorting-list]
     [:input.search {:type "text"
                     :class (when (not= "" @filter-text) "open")
                     :spellCheck false
                     :autoComplete false
                     :on-input #(re-frame/dispatch [:update-filter (.-target.value %)])}]]))

(defn- details []
  (let [current-item (re-frame/subscribe [:selected-item])]
    (if @current-item
      [:div.overlay
       [:section.modal
        [:img.cover {:src (:ratings.db/image-url @current-item)}]
        [:div.info
         [:h1 (:ratings.db/name @current-item)]
         [rating-stars (:ratings.db/id @current-item) (:ratings.db/rating @current-item)]
         [:p (str (:ratings.db/comment @current-item))]
         [:a.button {:href (routes/url-for :listing)} "Fechar"]]]])))

(defn application []
  [:main
   [sidebar]
   [header]
   [content]
   [details]])

