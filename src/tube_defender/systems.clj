(ns tube-defender.systems
  (:require [simplecs.core :as sc]
            [tube-defender.keyinput :as ki]
            [tube-defender.components :refer :all]
            [tube-defender.level1 :refer :all]
            [tube-defender.render :as render]))

(def rat-rate (atom 0))

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


(def sys-map {:systems [(rat-mover)
          (rat-remover)
          (rat-gen)
          (hero-mover)
          (disc-mover)
          (train-mover)]})

(def ces (atom (sc/make-ces (merge sys-map lvl))))

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
