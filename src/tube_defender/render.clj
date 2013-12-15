(ns tube-defender.render
  (:require [quil.core :refer :all]
            [simplecs.core :as sc]))

(def params {:screen-dimensions [400 400]
             :background-colour 32,36,35
             :blob-colour 0
             :blob-radius 10
             :screen-bounds [0 380]})

(defn render-bg
  "Render the background. Everything else to be rendered is drawn on top of this."
  []
  (fill (params :blob-colour))
  (background-float (params :background-colour))
  (fill 200)

  ;horizontal rails
  (rect 145 0 195 10)
  (rect 145 100 195 10)
  (rect 145 200 195 10)
  (rect 145 300 195 10)
  (rect 145 400 195 10)
  (rect 145 500 195 10)
  (rect 145 600 195 10)
  ;vertical rails
  (rect 150 -1 10 600)
  (rect 325 -1 10 600)
  )

(defn render-hud
  "Render the game's hud (head up display). This is drawn on top of everything else"
  []
  (text (str (deref tube-defender.keyinput/input-keys)) 10 350))


(defn draw-rat
  "Draw a rat at the given x y"
  [pos]
  (fill 130,120,98)
  (ellipse (:x pos) (:y pos) 10 30))

(defn draw-hero "draw our hero"
  [pos]
  (fill 200)
  (ellipse (+ (:x pos) 20) (- (:y pos) 50) 20 20)
  (fill 0 0 255)
  (rect (:x pos) (- (:y pos) 50) 40 40)
  (fill 220 120 120)
  (rect (:x pos) (+ (rand-int 10) (- (:y pos) 20)) 20 10)
  (rect (+ (:x pos) 40) (+ (rand-int 10) (- (:y pos) 20)) 20 10))

(defn draw-train
  "Draw a train at the given x y"
  [pos]
  (fill 255,17,0 )
  (ellipse 250 44 50 200)
  (fill 246,243,80)
  ;(triangle 250 50 22 15 200 20)
  ;(def x1 {:x 10 :y 5})
  )

(defn render-hero
  "Render the hero entities in the game"
  [ces]
  (dorun
    (let [hero-entities (sc/entities-with-component @ces :hero)
          hero-positions (map #(sc/get-component @ces % :position) hero-entities)]
         (map draw-hero hero-positions))))

(defn draw-disc
  "Draw a disc at the given x y"
  [pos]
  (fill 128 255 0)
  (ellipse (:x pos) (:y pos) 80 80))

(defn render-disc
  "Render the disc entities in the game"
  [ces]
  (dorun
    (let [disc-entities (sc/entities-with-component @ces :disc)
          disc-positions (map #(sc/get-component @ces % :position) disc-entities)]
         (map draw-disc disc-positions))))

(defn render-rats
  "Render the rat entities in the game"
  [ces]
  (dorun
    (let [rat-entities (sc/entities-with-component @ces :rat)
          rat-positions (map #(sc/get-component @ces % :position) rat-entities)]
         (map draw-rat rat-positions))))

(defn render-train
  "Render the train entities in the game"
  [ces]
  (dorun
    (let [train-entities (sc/entities-with-component @ces :train)
          train-positions (map #(sc/get-component @ces % :position) train-entities)]
      (map draw-train train-positions))))
