(ns tube-defender.core
  (:require  [quil.core :refer :all]
             [simplecs.core :as sc])
  (:import java.awt.event.KeyEvent)
  (:gen-class))

(sc/defcomponent rat [] {})
(sc/defcomponent position [x y] {:x x :y y})
(sc/defcomponentsystem rat-renderer :rat [] [ces entity _] (ellipse ))


(def foo [[(rat)  (position 20 40)]
          [(rat) (position 30 60)]])
(def ces (atom (sc/make-ces {:entities foo}) ))
(def ces2 (atom (sc/make-ces {:entities [[(rat)
                                          (position 20 40)]
                                         [(rat)
                                          (position 30 60)]]})))



(map keys  [@ces2 @ces])
(= @ces @ces2)
(macroexpand '(sc/letc ces :rat [x [:position :x]
                                 y [:position :y]] (str x y)))
(sc/has-component? ces rat :position)

(sc/get-in-entity ces rat [:postion :x])


(def input-keys (atom #{}))

(def params {
        :screen-dimensions [400 400]
        :background-colour 255
        :blob-colour 0
  :blob-radius 10
  :screen-bounds [0 380]})

(defn setup []
  (def my-key (atom "oh hai"))
  (smooth)
  (no-stroke))

(defn draw
  []
  (background-float (params :background-colour))
  (fill (params :blob-colour))
  (text (str @input-keys) 10 350))

(def valid-keys {
  KeyEvent/VK_UP :up
  KeyEvent/VK_DOWN :down
  KeyEvent/VK_LEFT :left
  KeyEvent/VK_RIGHT :right
  \w :up
  \s :down
  \a :left
  \d :right})

(defn keydown-event []
  (let [key-pressed (if (= processing.core.PConstants/CODED
                           (int (raw-key)))
                          (key-code) ;;then
                          (raw-key))] ;;else
    (swap! input-keys conj key-pressed)))

(defn keyup-event []
  (let [key-released (if (= processing.core.PConstants/CODED
                           (int (raw-key)))
                          (key-code) ;;then
                          (raw-key))] ;;else
    (swap! input-keys disj key-released)))

(defn startSketch []
  (defsketch key-listener
    :title "Keyboard arrow keys demo"
    :size (params :screen-dimensions)
    :setup setup
    :draw draw
    :target :perm-frame
    :key-pressed keydown-event
    :key-released keyup-event))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (startSketch))
