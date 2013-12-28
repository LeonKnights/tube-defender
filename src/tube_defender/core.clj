(ns tube-defender.core
  (:require  [quil.core :refer :all]
             [simplecs.core :as sc]
             [tube-defender.keyinput :as ki]
             [tube-defender.render :as render]
             [tube-defender.components :refer :all]
             [tube-defender.systems :refer :all]
             )
  (:gen-class))


(def playfield-size {:height 500
                     :width  500})

(defn exit-on-close [sketch]
  (let [frame (-> sketch .getParent .getParent .getParent .getParent)]
    (.setDefaultCloseOperation frame javax.swing.JFrame/EXIT_ON_CLOSE)))


(defn setup []
  (smooth)
  (frame-rate 60))

(defn -main
  "main"
  [& args]
 (exit-on-close (sketch :title "TUBE DEFENDER"
    :size [(:height playfield-size)
           (:width playfield-size)]
    :setup setup
    :draw core-render
    :target :perm-frame
    :key-pressed ki/keydown-event
    :key-released ki/keyup-event)))
