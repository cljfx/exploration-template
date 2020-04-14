(ns user
  (:require [cljfx.api :as fx]
            [clojure.string :as str]
            [cljfx.coerce :as coerce])
  (:import [java.lang.reflect Field]))

(defn- find-props [lifecycle]
  (or (:props lifecycle)
      (when (coll? lifecycle) (some find-props lifecycle))))

(defn fx-help
  "Print help information about javafx/cljfx

  When called without args, prints javafx/cljfx component overview
  When called with keyword valid for :fx/type, prints it's available props
  When called with fx type and prop keywords, prints some description of that prop"
  ([]
   (println "Main javafx/cljfx component concepts:
- :stage — main window component, describes native OS window. Other types of windows:
  - dialogs — pre-configured stages for some use-cases (see examples at
    https://github.com/cljfx/cljfx/blob/master/examples/e17_dialogs.clj):
    - :dialog
    - :choice-dialog
    - :alert
    - :text-input-dialog
  - popups, such as :popup, :popup-control, :tooltip and :context-menu
- :scene — describes common window content that is shared in the scene graph, such as
  stylesheets, camera (:parallel-camera or :perspective-camera), root node. :sub-scene is
  a node that acts as a scene.
- Nodes — scene graph items, can be:
  - 2D shapes, such as :arc, :circle, :cubic-curve, :ellipse, :line, :path, :polygon,
    :polyline, :quad-curve, :rectangle, :svg-path and :text
  - 3D shapes, such as :box, :cylinder, :mesh-view, :triangle-mesh, :sphere,
    :ambient-light, :point-light and :phong-material
  - Views, such as :image-view, :progress-indicator, :progress-bar and :separator
  - :canvas that allows drawing different shapes and text
  - Layout containers, such as :h-box, :v-box, :grid-pane, :flow-pane, :stack-pane, :pane,
    :anchor-pane, :border-pane, :text-flow, :tile-pane, :group and :region (see examples
    at https://github.com/cljfx/cljfx/blob/master/examples/e07_extra_props.clj)
  - Menus — :menu-bar, :menu-button and :split-menu-button — that might contain various
    menu items, such as :menu, :menu-item, :check-menu-item, :custom-menu-item and
    :radio-menu-item
  - Text, such as :label, :text-field, :password-field and :text-area
  - Navigation, such as :titled-pane, :accordion, :split-pane, :tab-pane and :scroll-pane
  - Data input, such as :button, :toggle-button, :radio-button, :button-bar, :check-box,
    :date-picker, :choice-box, :color-picker, :combo-box, :slider, :spinner, :tool-bar and
    :hyperlink
  - Virtualized lists, such as :list-view, :pagination, :table-view, :tree-table-view
    and :tree-view
  - Charts, such as :pie-chart, :area-chart, :bar-chart, :bubble-chart, :line-chart,
    :scatter-chart, :stacked-area-chart and :stacked-bar-chart (see examples at
    https://github.com/cljfx/cljfx/blob/master/examples/e14_charts.clj)
  - Multimedia, such as :media-view, :web-view and :html-editor
- Effects that can be placed on nodes, such as :blend, :bloom, :box-blur, :color-adjust,
  :color-input, :displacement-map, :drop-shadow, :gaussian-blur, :glow, :image-input,
  :inner-shadow, :lighting, :light-distant, :light-point, :light-spot, :motion-blur,
  :perspective-transform, :reflection, :sepia-tone and :shadow
- Transforms that can be applied to nodes, such as :affine, :rotate, :scale, :shear and
  :translate"))
  ([fx-type]
   (if-let [lifecycle (fx/keyword->lifecycle fx-type)]
     (do (println "Props:")
         (->> lifecycle find-props keys sort (run! #(println "-" %))))
     (println fx-type "is not a valid :fx/type")))
  ([fx-type prop]
   (if-let [lifecycle (fx/keyword->lifecycle fx-type)]
     (if-let [prop (get (find-props lifecycle) prop)]
       (letfn [(describe-from-lifecycle [acc lifecycle]
                 (cond
                   (= lifecycle cljfx.lifecycle/dynamic)
                   (assoc acc :type 'component)

                   (= lifecycle [:cljfx.lifecycle/event-handler])
                   (assoc acc :type 'event-listener)

                   (and (vector? lifecycle) (= :cljfx.lifecycle/coerce (first lifecycle)))
                   (describe-from-lifecycle acc (second lifecycle))

                   (and (vector? lifecycle) (= :cljfx.lifecycle/many (first lifecycle)))
                   (-> acc
                       (assoc :many true)
                       (assoc-in [:extra-keys :fx/key] {})
                       (describe-from-lifecycle (second lifecycle)))

                   (and (vector? lifecycle) (= :cljfx.lifecycle/extra-props (first lifecycle)))
                   (-> acc
                       (as-> $ (reduce-kv (fn [acc k v]
                                            (assoc-in acc [:extra-keys k] (describe-prop {} v)))
                                          $
                                          (get lifecycle 2)))
                       (describe-from-lifecycle (second lifecycle)))

                   :else
                   acc))
               (describe-prop [acc prop]
                 (let [^Field field (->> (:coerce prop)
                                         class
                                         .getDeclaredFields
                                         (filter #(= "enum_type" (.getName ^Field %)))
                                         first)
                       coerce-type ({double 'double
                                     int 'int
                                     boolean 'boolean
                                     coerce/border 'cljfx.coerce/border
                                     coerce/computed-size-double [:use-computed-size 'double]
                                     coerce/color 'cljfx.coerce/color
                                     coerce/cursor 'cljfx.coerce/cursor
                                     coerce/paint 'cljfx.coerce/paint
                                     coerce/style-class '[string [string]]
                                     coerce/style '[string map]
                                     coerce/font 'cljfx.coerce/font
                                     coerce/pref-or-computed-size-double [:use-computed-size :use-pref-size 'double]
                                     coerce/insets ['number :empty {:top 'number :right 'number :bottom 'number :left 'number}]}
                                    (:coerce prop))]
                   (when field (.setAccessible field true))
                   (cond-> (describe-from-lifecycle acc (:lifecycle prop))

                           field
                           (assoc :type (->> ^Class (.get field (:coerce prop))
                                             .getEnumConstants
                                             (map #(-> ^Enum % .name str/lower-case (str/replace \_ \-) keyword))
                                             sort))

                           coerce-type
                           (assoc :type coerce-type)

                           (and (vector? (:mutator prop))
                                (= :cljfx.mutator/default (first (:mutator prop))))
                           (assoc :default (last (:mutator prop))))))
               (indent-str [indent]
                 (apply str (repeat indent \space)))
               (print-desc [indent description]
                 (when-let [type (:type description)]
                   (println (str (indent-str indent) "type: "
                                 (when (:many description) "coll of ")
                                 (if (sequential? type)
                                   (str/join " | " (map pr-str type))
                                   (pr-str type)))))
                 (when-let [extra-keys (:extra-keys description)]
                   (println (str (indent-str indent) "extra keys:"))
                   (doseq [[k v] (sort extra-keys)]
                     (println (str (indent-str indent) "  - " k))
                     (print-desc (+ indent 6) v)))
                 (when-some [default (:default description)]
                   (println "default value:\n  " (binding [*print-namespace-maps* false]
                                                   (pr-str default)))))]
         (print-desc 0 (describe-prop {} prop)))
       (println prop "is not a valid prop of" fx-type))
     (println fx-type "is not a valid :fx/type"))))
