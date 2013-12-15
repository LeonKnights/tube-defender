(ns tube-defender.render
  (:require [quil.core :refer :all]
            [simplecs.core :as sc]
            [tube-defender.core :as core]))

(def params {:screen-dimensions [400 400]
             :background-colour 255
             :blob-colour 0
             :blob-radius 10
             :screen-bounds [0 380]})

(defn render-bg
  "Render the background. Everything else to be rendered is drawn on top of this."
  []
  (background-float (params :background-colour))
  (fill (params :blob-colour)))

(defn render-hud
  "Render the game's hud (head up display). This is drawn on top of everything else"
  []
  (text #_(str @input-keys) "hello" 10 350))


(defn rat
  "Draw a rat at the given x y"
  [x y]
  ())

(defn render-rats
  "Render the rat entities in the game"
  [ces]
  (let [rat-entities (sc/entities-with-component @ces :rat)
        rat-positions (map #(sc/get-component @ces % :position) rat-entities)]
    (map (fn [rat-pos] (str #_ellipse (:x rat-pos) (:y rat-pos) 10 10)) rat-positions)))

(defn render 
  "Renders every entity in the given game state"
  []
  (let [ces core/ces]
    (render-bg)
    (render-rats ces)
    (render-hud)))