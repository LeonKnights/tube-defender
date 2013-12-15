(ns tube-defender.render
  (:require [quil.core :refer :all]
            [quil.helpers.drawing :refer [line-join-points]]
            [quil.helpers.seqs :refer [range-incl]]
            [quil.helpers.calc :refer [mul-add]]
            [simplecs.core :as sc]))

(defn custom-rand
  []
  (- 1 (pow (random 1) 5)))

(defn draw-health []

  (stroke 300 400 70)

  (let [xs (range-incl 20 480 5)
        ys (repeatedly custom-rand)
        scaled-ys (mul-add ys 60 20)
        line-args (line-join-points xs scaled-ys)]

    (dorun (map #(apply line %) line-args))))

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
  (fill 91,70,25)

  ;horizontal rails
  (rect 145 0 195 20)
  (rect 145 100 195 20)
  (rect 145 200 195 20)
  (rect 145 300 195 20)
  (rect 145 400 195 20)
  (rect 145 500 195 20)
  (rect 145 600 195 20)
  ;vertical rails
  (fill 185,183,181)
  (rect 150 -1 10 600)
  (rect 325 -1 10 600)
  (triangle 100 100 10 20 20 10)
  )

(defn render-hud
  "Render the game's hud (head up display). This is drawn on top of everything else"
  []
  (draw-health)
  (stroke (rand-int 255) (rand-int 255) (rand-int 255))
  (text (str "CATCH THE RATS! AVOID THE TRAIN") 350 (rand-int 20)))


(defn draw-rat
  "Draw a rat at the given x y"
  [pos]
  (fill 130 120 98)
  (let [x (:x pos)
        y (:y pos)]
    (triangle (+ (- 15) x) (+ y (rand-int 7))
              (+ 15 x) (+ y (rand-int 7))
              x (+ 5 y))
    (triangle (+ (- 15) x) (+ (- y 15) (rand-int 7))
              (+ 15 x)     (+ (- y 15) (rand-int 7))
              x (+ (- 5) y)))
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
  (rect (:x pos) (:y pos) 50 500)
  (fill 246,243,80)
  (triangle 250 50 22 15 200 20)
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
  (ellipse (+ (rand-int 4) (:x pos)) (+ (rand-int 4) (:y pos)) (+ (rand-int 2) 80) (+ (rand-int 2) 80)))

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
