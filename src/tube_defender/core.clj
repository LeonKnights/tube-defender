(ns tube-defender.core
  (:require  [quil.core :refer :all]
             [simplecs.core :as sc]
             [tube-defender.key-input :only input-keys])
  (:gen-class))

;;;;;;;;;;;;;;;;components;;;;;;;;;;;;;;;;;;

(sc/defcomponent rat [] {})
(sc/defcomponent hero [] {})
(sc/defcomponent disc [] {})
(sc/defcomponent position [x y] {:x x :y y})
(sc/defcomponent volley-multiple [vm] {:vm vm})
(sc/defcomponent velocity [v] {:v v})

;;;;;;;;;;;;;;;;;;;systems;;;;;;;;;;;;;;;;;;;
(sc/defcomponentsystem rat-mover :rat
  []
  [ces entity _]
  (sc/letc ces entity
           [y [:position :y]]
           (sc/update-entity ces
                             entity
                             [:position :y] inc)))

(sc/update-entity @ces 0 [:position :y] inc)

(def ces (atom (sc/make-ces {:entities [[(hero)
                                         (position 200 500)
                                         (velocity 0)]
                                        [(disc)
                                         (position 220 480)
                                         (velocity 10)
                                         (volley-multiple 1)]
                                        [(rat)
                                         (position 20 40)
                                         (velocity 1)]
                                        [(rat)
                                         (position 30 60)
                                         (velocity 1)]]
                             :systems [(rat-mover)]})))
(def rat-ces (atom (sc/make-ces {:entities [[(rat) (position 10 2) (velocity 0)]]
                                 :systems [(rat-mover)]})))

((:fn (rat-mover)) rat-ces)
(swap! ces sc/advance-ces)

(:simplecs.core/components @ces)


(defn setup []
  (smooth)
  (no-stroke))

(defn advance-state []
  (swap! ces sc/advance-ces))

(defn draw
  [])

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (startSketch))
