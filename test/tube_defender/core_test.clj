(ns tube-defender.core-test
  (:require [clojure.test :refer :all]
            [tube-defender.core :refer :all]
            [simplecs.core :as sc]))

;;;;;;;;;;;;;;;;;;;;;;;;test rat ces update;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; y should inc to 3
(deftest rat-ces-test
  (testing "rat mover works right" (let [rat-ces (atom (sc/make-ces {:entities [[(rat) (position 10 2) (velocity 0)]]
                                  :systems [(rat-gen) (rat-mover)]}))]
    (swap! rat-ces sc/advance-ces))))


;;;;;;;;;;;;;;;;;;;;;;;test hero ces update;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; x should inc to 201
(deftest hero-ces-test (testing "hero mover works right" (let [hero-ces (atom (sc/make-ces {:entities [[(hero) (position 200 400) (velocity 0)]]
                                   :systems [(hero-mover)]}))]
  (swap! hero-ces sc/advance-ces)))
)
;;;;;;;;;;;;;;;;test disc ces update;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(deftest disc-ces-test (testing "disc mover works right" (is (let [disc-ces (atom (sc/make-ces {:entities [[(disc) (position 200 400) (velocity 10 10)]
                                              [(rat) (position 10 10) (velocity 20)]]
                                   :systems [(disc-mover)]}))]
                                                           (swap! disc-ces sc/advance-ces)))))

;;;;;;;;;;;;;;;test volley multiple incrementer;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(deftest vm-inv-test (testing "volley multiplier works right" (let [dvm-ces (atom (sc/make-ces {:entities [[(disc) (position 200 400) (velocity 0) (volley-multiple 1)]]
                                  :systems [(volley-multiplier)]}))]
                                                                (swap! dvm-ces sc/advance-ces))))

(deftest train-mover-test (testing "train can move"
                            (let [train-ces (atom (sc/make-ces {:entities
                                                                [[(train)
                                                                  (position 200 200)]]
                                                                :systems [(train-mover)]}))]
                              (swap! train-ces sc/advance-ces))))
