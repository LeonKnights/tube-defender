(ns tube-defender.core
  (:require  [quil.core :refer :all]
             [simplecs.core :as sc]
             [tube-defender.key-input :only input-keys]
             [tube-defender.render :as render])
  (:gen-class))

;;;;;;;;;;;;;;;;components;;;;;;;;;;;;;;;;;;

(sc/defcomponent rat [] {})
(sc/defcomponent hero [] {})
(sc/defcomponent disc [] {})
(sc/defcomponent position [x y] {:x x :y y})
(sc/defcomponent volley-multiple [vm] {:vm vm})
(sc/defcomponent velocity [v] {:v v})

;;;;;;;;;;;;;;;;;;;systems;;;;;;;;;;;;;;;;;;;
(sc/defcomponentsystem rat-mover :rat  []
  [ces entity _]
  (sc/letc ces entity
           [y [:position :y]]
           (sc/update-entity ces
                             entity
                             [:position :y] inc)))

#_(sc/update-entity @ces 0 [:position :y] inc)

;;;;;;;hero mover should use key bindings instead of just calling inc
(sc/defcomponentsystem hero-mover :hero  []
  [ces entity _]
  (sc/letc ces entity
           [x [:position :x]]
           (sc/update-entity ces entity [:position :x] inc)))

;;;;;;;disc mover should use keybindings instead of just calling inc;;;;;;;
(sc/defcomponentsystem disc-mover :disc []
  [ces entity _]
  (sc/letc ces entity
  [x [:position :x]
   y [:position :y]]
  (sc/update-entity ces entity [:position :x] inc)
  (sc/update-entity ces entity [:position :y] inc)))

;;;;;;;;;;;;;;;;;;canonic component entity system;;;;;;;;;;;;;;;;
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






;;;;;;;;;;;;;;;;;;;;;;;;test rat ces update;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; y should inc to 3
(let [rat-ces (atom (sc/make-ces {:entities [[(rat) (position 10 2) (velocity 0)]]
                                  :systems [(rat-mover)]}))]
  (swap! rat-ces sc/advance-ces))

;;;;;;;;;;;;;;;;;;;;;;;test hero ces update;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; x should inc to 201
(let [hero-ces (atom (sc/make-ces {:entities [[(hero) (position 200 400) (velocity 0)]]
                                   :systems [(hero-mover)]}))]
  (swap! hero-ces sc/advance-ces))

;;;;;;;;;;;;;;;;test disc ces update;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(let [disc-ces (atom (sc/make-ces {:entities [[(disc) (position 200 400) (velocity 0)]]
                                   :systems [(disc-mover)]}))]
  (swap! disc-ces sc/advance-ces))



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
  (tube-defender.key-input/startSketch))
