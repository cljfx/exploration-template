(ns cljfx.exploration
  (:require [cljfx.api :as fx]
            [user :refer [fx-help]]))

;; view

(defn root-view [{:keys [showing] :as state}]
  {:fx/type :stage
   :showing showing
   :on-close-request {::event ::close-window}
   :scene {:fx/type :scene
           :root {:fx/type :v-box
                  :children [{:fx/type :label
                              :text "Hello, world!"}]}}})

;; events

(defmulti handle ::event)

(defmethod handle :default [event]
  (println (::event event) (dissoc event ::event :state)))

(defmethod handle ::close-window [{:keys [state]}]
  {:set-state (assoc state :showing false)})

;; renderer setup

(defonce *state
  (atom {:showing true}))

(defonce renderer
  (fx/create-renderer
    :middleware (fx/wrap-map-desc #'root-view)
    :opts {:fx.opt/map-event-handler (-> handle
                                         (fx/wrap-co-effects
                                           {:state (fx/make-deref-co-effect *state)})
                                         (fx/wrap-effects
                                           {:set-state (fx/make-reset-effect *state)
                                            :dispatch fx/dispatch-effect}))}))

(fx/mount-renderer *state renderer)

(comment
  ;; Initial event setup binds state's :showing key to window's :showing prop. If you
  ;; close a window, you can open it again by changing atom:
  (swap! *state assoc :showing true)

  ;; You need to trigger UI refresh when modifying view components since `renderer` does
  ;; not watch them as it watches `*state`, this can be achieved by "touching" the state:
  (swap! *state identity)
  ;; Alternatively, you can just reload this namespace: `fx/mount-renderer` will also
  ;; trigger UI refresh.



  ;; when in doubt, you can use `help` function...
  ;; - to get short overall javafx/cljfx components overview:
  (fx-help)
  ;; - to list all available props on a particular built-in component
  (fx-help :v-box)
  ;; - to show some prop information
  (fx-help :v-box :children)
  (fx-help :v-box :padding))