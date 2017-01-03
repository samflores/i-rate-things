(set-env!
 :source-paths    #{"sass" "src/clj" "src/cljs"}
 :resource-paths  #{"resources"}
 :dependencies '[[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.293"
                  :exclusions [org.clojure/clojure]]

                 [adzerk/boot-cljs "1.7.228-2" :scope "test"]
                 [adzerk/boot-cljs-repl "0.3.3" :scope "test"]
                 [crisptrutski/boot-cljs-test "0.3.0" :scope "test"]
                 [adzerk/boot-reload "0.4.13" :scope "test"]
                 [pandeiro/boot-http "0.7.6" :scope "test"
                  :exclusions [org.clojure/clojure]]
                 [deraen/boot-sass "0.3.0" :scope "test"]

                 [org.slf4j/slf4j-nop "1.7.22" :scope "test"]
                 [org.clojure/tools.nrepl "0.2.12" :scope "test"
                  :exclusions [org.clojure/clojure]]
                 [weasel "0.7.0" :scope "test"
                  :exclusions [org.clojure/clojure
                               org.clojure/clojurescript]]
                 [com.cemerick/piggieback "0.2.1" :scope "test"
                  :exclusions [org.clojure/clojure
                               org.clojure/clojurescript]]

                 [reagent "0.6.0"
                  :exclusions [org.clojure/clojure
                               org.clojure/clojurescript]]
                 [re-frame "0.9.1"
                  :exclusions [org.clojure/clojure
                               org.clojure/clojurescript
                               reagent]]
                 [cljsjs/firebase "3.5.3-0"]])

(require
  '[adzerk.boot-cljs            :refer [cljs]]
  '[adzerk.boot-cljs-repl       :refer [cljs-repl start-repl]]
  '[adzerk.boot-reload          :refer [reload]]
  '[pandeiro.boot-http          :refer [serve]]
  '[crisptrutski.boot-cljs-test :refer [test-cljs]]
  '[deraen.boot-sass            :refer [sass]])

(deftask build []
  (comp (speak)
        (cljs)
        (sass)))

(deftask run []
  (comp (serve :port 8080)
        (watch)
        (cljs-repl)
        (reload)
        (build)))

(deftask production []
  (task-options! cljs {:optimizations :advanced}
                 sass {:output-style :compressed})
  identity)

(deftask development []
  (task-options! cljs {:optimizations :none
                       :source-map true
                       :compiler-options {:preloads '[ratings.dev]}}
                 reload {:on-jsload 'ratings.core/init})
  identity)

(deftask dev
  "Simple alias to run application in development mode"
  []
  (set-env! :source-paths #(conj % "test/cljs"))
  (comp
    (development)
    (run)))


(deftask testing []
  (set-env! :source-paths #(conj % "test/cljs"))
  identity)

;;; This prevents a name collision WARNING between the test task and
;;; clojure.core/test, a function that nobody really uses or cares
;;; about.
(ns-unmap 'boot.user 'test)

(deftask test []
  (comp (testing)
        (test-cljs :js-env :phantom
                   :exit?  true)))

(deftask auto-test []
  (comp (testing)
        (watch)
        (test-cljs :js-env :phantom)))
