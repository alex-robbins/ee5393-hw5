(ns ee5393-hw5.core
  (:require [clojure.math.combinatorics :refer [combinations]])
  (:require [clojure.set :as set]))

(defrecord Edge [node in neg])
(defrecord BoolFn [hi lo])

(defn closed? [input {:keys [in neg]}]
  (let [neg-f (if neg not identity)]
    (neg-f (contains? input in))))

(defn adj [g input n]
  (map :node
       (filter (partial closed? input) (get g n))))

(defn path? [g node1 node2 input]
  ;; Basically a DFS using only closed edges
  (loop [explored #{} frontier [node1]]
    (if-let [node1 (peek frontier)]
      (or (= node1 node2)
          (recur (conj explored node1)
                 (into (pop frontier)
                       (remove explored (adj g input node1))))))))

(defn implements?
  "Returns a pair of nodes in g that implement f."
  ([g f]
   (some (fn [[node1 node2]] (implements? g f node1 node2))
         (combinations (keys g) 2)))
  ([g f node1 node2]
   (and
    (every? (partial path? g node1 node2) (:hi f))
    (every? (partial (complement path?) g node1 node2) (:lo f))
    [node1 node2])))

(defn add-edge [g node1 node2 in neg]
  (-> g
    (update node1 #(conj (set %) (->Edge node2 in neg)))
    (update node2 #(conj (set %) (->Edge node1 in neg)))))

(defn inputs [g]
  (set (map :in (apply set/union (vals g)))))
