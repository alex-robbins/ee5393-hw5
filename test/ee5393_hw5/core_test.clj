(ns ee5393-hw5.core-test
  (:require [clojure.test :refer :all]
            [ee5393-hw5.core :refer :all]))

(def g {1 #{(->Edge 2 :y false) (->Edge 4 :x true)}
        2 #{(->Edge 1 :y false) (->Edge 3 :x false)}
        3 #{(->Edge 2 :x false) (->Edge 4 :y true)}
        4 #{(->Edge 3 :y true) (->Edge 1 :x true)}})

(def fs
  [(map->BoolFn ; f0 = x
    {:hi #{#{:x} #{:x :y}}
     :lo #{#{:y} #{}}})
   (map->BoolFn ; f1 = x'
     {:hi #{#{:y} #{}}
      :lo #{#{:x} #{:x :y}}})
   (map->BoolFn ; f2 = y
     {:hi #{#{:y} #{:x :y}}
      :lo #{#{:x} #{}}})
   (map->BoolFn ; f3 = y'
     {:hi #{#{:x} #{}}
      :lo #{#{:y} #{:x :y}}})
   (map->BoolFn ; f4 = xy
     {:hi #{#{:x :y}}
      :lo #{#{:x} #{:y} #{}}})
   (map->BoolFn ; f5 = xy'
     {:hi #{#{:x}}
      :lo #{#{:x :y} #{:y} #{}}})
   (map->BoolFn ; f6 = x'y
     {:hi #{#{:y}}
      :lo #{#{:x :y} #{:x} #{}}})
   (map->BoolFn ; f7 = x'y'
     {:hi #{#{}}
      :lo #{#{:x :y} #{:x} #{:y}}})
   (map->BoolFn ; f8 = x | y
     {:hi #{#{:x :y} #{:x} #{:y}}
      :lo #{#{}}})
   (map->BoolFn ; f9 = x | y'
     {:hi #{#{:x :y} #{:x} #{}}
      :lo #{#{:y}}})
   (map->BoolFn ; f10 = x' | y
     {:hi #{#{:x :y} #{:y} #{}}
      :lo #{#{:x}}})
   (map->BoolFn ; f11 = x' | y'
     {:hi #{#{:x} #{:y} #{}}
      :lo #{#{:x :y}}})
   (map->BoolFn ; f12 = xy' | x'y
     {:hi #{#{:x} #{:y}}
      :lo #{#{:x :y} #{}}})
   (map->BoolFn ; f13 = xy | x'y'
     {:hi #{#{:x :y} #{}}
      :lo #{#{:x} #{:y}}})])

(deftest path-test
  (is (path? g 1 2 #{:y}))
  (is (path? g 1 3 #{:x :y}))
  (is (not (path? g 1 2 nil)))
  (is (not (path? g 1 3 #{:x}))))

(deftest implements-test
  (is (implements? g (fs 13) 1 3))
  (is (not (implements? g (fs 13) 2 4)))
  (is (implements? g (fs 13)))
  (is (implements? g (fs 12))))

(deftest add-edge-test
  (is (= g
         (-> nil
           (add-edge 1 2 :y false)
           (add-edge 2 3 :x false)
           (add-edge 3 4 :y true)
           (add-edge 4 1 :x true)))))

(deftest inputs-test
  (is (= #{:x :y} (inputs (fs 13)))))
