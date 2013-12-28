(ns tube-defender.level1
  (:require [tube-defender.components :refer :all]))

                           (def lvl {:entities [[(hero)
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
                                         (velocity 5)]]})
