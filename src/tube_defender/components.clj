(ns tube-defender.components
  (:require [simplecs.core :as sc]))

(sc/defcomponent rat [] {})

(sc/defcomponent hero [] {})

(sc/defcomponent disc [in-player-hand] {:in-player-hand in-player-hand})

(sc/defcomponent train [] {})

(sc/defcomponent position [x y] {:x x :y y})

(sc/defcomponent volley-multiple [vm] {:vm vm})

(sc/defcomponent velocity
                 ([v] {:v v})
                 ([x y] {:v x :x x :y y}))
