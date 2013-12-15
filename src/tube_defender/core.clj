(ns tube-defender.core
  (:require  [quil.core :refer :all]
             [simplecs.core :as sc]
             [tube-defender.keyinput :as ki]
             [tube-defender.render :as render])
  (:gen-class))

(def rat-rate (atom 0))

;;;;;;;;;;;;;;;;components;;;;;;;;;;;;;;;;;;

(sc/defcomponent rat [] {})
(sc/defcomponent hero [] {})
(sc/defcomponent disc [] {})
(sc/defcomponent position [x y] {:x x :y y})
(sc/defcomponent volley-multiple [vm] {:vm vm})
(sc/defcomponent velocity
                 ([v] {:v v})
                 ([x y] {:v x :x x :y y}))

;;;;;;;;;;;;;;;;;;;systems;;;;;;;;;;;;;;;;;;;
(sc/defcomponentsystem rat-mover :rat  []
  [ces entity _]
  (sc/letc ces entity
           [y [:position :y]]
           (sc/update-entity ces
                             entity
                             [:position :y] inc)))

(sc/defcomponentsystem rat-remover :rat []
  [ces entity _]
  (sc/letc ces entity
           [y [:position :y]]
           (if (< y 300) ces (sc/remove-entity ces entity))))

;;;;;;;hero mover should use key bindings instead of just calling inc
;;;;TODO. make this thing work plz
(sc/defcomponentsystem hero-mover :hero  []
  [ces entity _]
  (sc/letc ces entity
           [x [:position :x]]
           (let [left (contains? @ki/input-keys \a)
                 right (contains? @ki/input-keys \d)
                 both (and left right)]
             (cond both (sc/update-entity ces entity [:position :x] dec)
                   left (sc/update-entity ces entity [:position :x] dec)
                   right (sc/update-entity ces entity [:position :x] inc)
                   :else ces))))

;;;;;;;disc mover should use keybindings instead of just calling inc;;;;;;;
(sc/defcomponentsystem disc-mover :disc  []
  [ces entity _]
  (sc/letc ces entity
           [dx [:velocity :x]
            dy [:velocity :y]
            x  [:position :x]
            y  [:position :y]]
           (sc/update-entity
                             (sc/update-entity ces entity
                                               [:position :y] + dy)
                             entity
                             [:position :x] + dx)))

;;;;;disc position validator will make sure the junk don't fly off the dern screen;;;;;
(sc/defcomponentsystem disc-move-validator :disc []
  [ces entity _]
  (sc/letc ces entity
  [x [:position :x]
   y [:position :y]]
  ;;when not inside the bounds of the window, negate
  (when (not (and (< 0 x 500) (< 0 y 500))) (sc/update-entity (sc/update-entity ces entity [:position :y] - y) entity [:position :x] - x) )
;;if edge of screen, reverse x, if top of screen reverse y, if bottom and not on player, stop
  ;;(sc/update-entity (sc/update-entity ces entity [:position :y] inc) entity [:position :x] inc)
  ))
;;;;;;;;volley multiple should inc if disc is caught by hero, but that logic is outside of this system. all it cares about is how to do it;;;;;
(sc/defcomponentsystem volley-multiplier :disc []
  [ces entity _]
  (sc/update-entity ces entity [:volley-multiple :vm] inc))

;;;;;;;;;;;;;;;rat generator;;;;;;;;;;;;;;;;;
(sc/defsystem rat-gen  []
  [ces]
  (if (== 60 @rat-rate)
    (do (reset! rat-rate 0)    (sc/add-entity ces [(rat) (position (rand-int 1000) 0)]))
    (do (swap! rat-rate inc) ces)))






;;;;;;;;;;;;;;;;;;canonic component entity system;;;;;;;;;;;;;;;;
(def ces (atom (sc/make-ces {:entities [[(hero)
                                         (position 200 400)
                                         (velocity 0)]
                                        [(disc)
                                         (position 220 480)
                                         (velocity 0 0)
                                         (volley-multiple 1)]
                                        [(rat)
                                         (position 20 40)
                                         (velocity 1)]
                                        [(rat)
                                         (position 30 60)
                                         (velocity 1)]
                                        [(rat)
                                         (position 50 60)
                                         (velocity 1)]]
                             :systems [(rat-mover)
                                       (rat-remover)
                                       (rat-gen)
                                       (hero-mover)
                                       (disc-mover)
                                        ]})))

(defn advance-state []
  (swap! ces sc/advance-ces))

(defn core-render
  "Renders every entity in the given game state"
  [& whatevs]
  (advance-state)
  (render/render-bg)
  (render/render-hero ces)
  (render/render-rats ces)
  (render/render-disc ces)
  (render/render-hud))


(defn setup []
  (smooth)
  (frame-rate 60)
  (background 200))

(defn -main
  "main"
  [& args]
 (sketch :title "TUBE DEFENDER"
    :size [500 500]
    :setup setup
    :draw core-render
    :target :perm-frame
    :key-pressed ki/keydown-event
    :key-released ki/keyup-event))
