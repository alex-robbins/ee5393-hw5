(ns ee5393-hw5.core-test
  (:require [clojure.test :refer :all]
            [ee5393-hw5.core :refer :all]))

(def g {1 #{(->Edge 2 :y false) (->Edge 4 :x true)}
        2 #{(->Edge 1 :y false) (->Edge 3 :x false)}
        3 #{(->Edge 2 :x false) (->Edge 4 :y true)}
        4 #{(->Edge 3 :y true) (->Edge 1 :x true)}})

(def f1
  "f1 = xy | x'y'"
  (map->BoolFn
   {:hi #{#{} #{:x :y}}
    :lo #{#{:y} #{:x}}}))

(def f2
  "f1 = xy' | x'y"
  (map->BoolFn
   {:hi #{#{:y} #{:x}}
    :lo #{#{} #{:x :y}}}))

(deftest path-test
  (is (path? g 1 2 #{:y}))
  (is (path? g 1 3 #{:x :y}))
  (is (not (path? g 1 2 nil)))
  (is (not (path? g 1 3 #{:x}))))

(deftest implements-test
  (is (implements? g f1 1 3))
  (is (not (implements? g f1 2 4)))
  (is (implements? g f1))
  (is (implements? g f2)))

(deftest add-edge-test
  (is (= g
         (-> nil
           (add-edge 1 2 :y false)
           (add-edge 2 3 :x false)
           (add-edge 3 4 :y true)
           (add-edge 4 1 :x true)))))

(deftest inputs-test
  (is (= #{:x :y} (inputs g))))
