(ns tube-defender.core
  (:require  [quil.core :refer :all]
             [simplecs.core :as sc]
             [tube-defender.keyinput :as ki]
             [tube-defender.render :as render]
             [tube-defender.components :refer :all])
  (:gen-class))

(def rat-rate (atom 0))
(def playfield-size {:height 500
                     :width  500})

;;;;;;;;;;;;;;;;components;;;;;;;;;;;;;;;;;;

;(sc/defcomponent rat [] {})
;(sc/defcomponent hero [] {})
;(sc/defcomponent disc [in-player-hand] {:in-player-hand in-player-hand})
;(sc/defcomponent train [] {})
;(sc/defcomponent position [x y] {:x x :y y})
;(sc/defcomponent volley-multiple [vm] {:vm vm})
;(sc/defcomponent velocity
 ; ([v] {:v v})
 ; ([x y] {:v x :x x :y y}))

;;;;;;;;;;;;;;;;;;;systems;;;;;;;;;;;;;;;;;;;
(sc/defcomponentsystem rat-mover :rat  []
  [ces entity _]
  (sc/letc ces entity
           [y [:position :y]]
           (sc/update-entity ces
                             entity
                             [:position :y] inc)))


(sc/defcomponentsystem train-mover :train []
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

(sc/defcomponentsystem hero-mover :hero  []
  [ces entity _]
  (sc/letc ces entity
           [x [:position :x]]
           (let [left (contains? @ki/input-keys \a)
                 right (contains? @ki/input-keys \d)
                 both (and left right)]
             (cond both (sc/update-entity ces entity [:position :x] dec)
                   left (sc/update-entity ces entity [:position :x] - 5)
                   right (sc/update-entity ces entity [:position :x] + 5)
                   :else ces))))

;;;;;;;disc mover should use keybindings instead of just calling inc;;;;;;;
(defn set-entity-position
  "Update the x and y in position component for given entity"
  [ces entity x y]
  (sc/update-entity
    (sc/update-entity ces entity
                      [:position :y] (fn [& _] y))
    entity
    [:position :x] (fn [& _] x)))

(sc/defcomponentsystem disc-mover :disc  []
  [ces entity _]
  (sc/letc ces entity
           [dx [:velocity :x]
            dy [:velocity :y]
            x  [:position :x]
            y  [:position :y]
            disc-held-by-player [:disc :in-player-hand]]
           (if (and disc-held-by-player (contains? @ki/input-keys \space))
             (sc/update-entity (sc/update-entity (sc/update-entity ces entity  [:position :y] + dy)  entity [:position :x] + dx) entity [:disc :in-player-hand] :false
                               )
             (if disc-held-by-player
              (let [hero-entity (first (sc/entities-with-component ces :hero))
                    hero-pos (sc/get-component ces hero-entity :position)]
                (set-entity-position ces entity (:x hero-pos) (:y hero-pos)))
                (sc/update-entity
                               (sc/update-entity ces entity
                                                 [:position :y] + dy)
                               entity
                               [:position :x] + dx)))))

;;;;;disc position validator will make sure the junk don't fly off the dern screen;;;;;
(sc/defcomponentsystem disc-move-validator :disc []
  [ces entity _]
  (sc/letc ces entity
           [x [:position :x]
            y [:position :y]
            dx [:velocity :x]
            dy [:velocity :y]]
            #_(if (> x (:width playfield-size))
  ;;when not inside the bounds of the window, negate
  (when (not (and (< 0 x 500) (< 0 y 500))) (sc/update-entity (sc/update-entity ces entity [:position :y] - y) entity [:position :x] - x) )
;;if edge of screen, reverse x, if top of screen reverse y, if bottom and not on player, stop
  ;;(sc/update-entity (sc/update-entity ces entity [:position :y] inc) entity [:position :x] inc)
  )))
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
                                        [(disc true)
                                         (position 220 480)
                                         (velocity 0 0)
                                         (volley-multiple 1)]
                                        [(disc false)
                                         (position 120 480)
                                         (velocity 1 1)
                                         (volley-multiple 1)]
                                        [(rat)
                                         (position 20 40)
                                         (velocity 1)]
                                        [(rat)
                                         (position 30 60)
                                         (velocity 1)]
                                        [(rat)
                                         (position 50 60)
                                         (velocity 1)]
                                        [(train)
                                         (position 250 0)
                                         (velocity 5)]]
                             :systems [(rat-mover)
                                       (rat-remover)
                                       (rat-gen)
                                       (hero-mover)
                                       (disc-mover)
                                       (train-mover)
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
  (render/render-train ces)
  (render/render-disc ces)
  (render/render-hud))

(defn exit-on-close [sketch]
  (let [frame (-> sketch .getParent .getParent .getParent .getParent)]
    (.setDefaultCloseOperation frame javax.swing.JFrame/EXIT_ON_CLOSE)))

(defn setup []
  (smooth)
  (frame-rate 60)
  (background 200))

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
