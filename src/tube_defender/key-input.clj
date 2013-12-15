(ns tube-defender.key-input 
  (:require [quil.core :refer :all])
  (:import java.awt.event.KeyEvent))

(def input-keys (atom #{}))

(def valid-keys {
  KeyEvent/VK_UP :up
  KeyEvent/VK_DOWN :down
  KeyEvent/VK_LEFT :left
  KeyEvent/VK_RIGHT :right
  \w :up
  \s :down
  \a :left
                 \d :right})

(def params {
        :screen-dimensions [400 400]
        :background-colour 255
        :blob-colour 0
  :blob-radius 10
  :screen-bounds [0 380]})

(defn- keydown-event []
  (let [key-pressed (if (= processing.core.PConstants/CODED
                           (int (raw-key)))
                          (key-code) ;;then
                          (raw-key))] ;;else
    (swap! input-keys conj key-pressed)))

(defn- keyup-event []
  (let [key-released (if (= processing.core.PConstants/CODED
                           (int (raw-key)))
                          (key-code) ;;then
                          (raw-key))] ;;else
    (swap! input-keys disj key-released)))

(defn setup []
  (smooth)
  (frame-rate 60)
  (background 200))

(defn startSketch []
  (defsketch key-listener
    :title "Keyboard arrow keys demo"
    :size (params :screen-dimensions)
    :setup setup
    :draw tube-defender.render/render
    :target :perm-frame
    :key-pressed keydown-event
    :key-released keyup-event))

(startSketch)
