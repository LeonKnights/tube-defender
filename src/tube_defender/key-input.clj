(ns tube-defender.key-input (:require [quil.core :refer :all]))

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
