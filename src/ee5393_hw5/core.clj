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

(defn implements-every? [g fs]
  (every? #(implements? g %) fs))

(defn add-edge [g node1 node2 in neg]
  (-> g
    (update node1 #(conj (set %) (->Edge node2 in neg)))
    (update node2 #(conj (set %) (->Edge node1 in neg)))))

(defn inputs [& fs]
  (apply set/union
         (map (fn [f]
                (apply set/union (set/union (:hi f) (:lo f))))
              fs)))

(defn rand-bool
  ([]
   (rand-bool 0.5))
  ([p]
   (boolean
    (< (rand) p))))

(defn mutate [g inputs]
  (let [in (rand-nth (vec inputs))
        neg (rand-bool)
        n-nodes (count (keys g))
        node1 (rand-int n-nodes)
        node2 (first (filter (partial not= node1)
                             [(rand-int n-nodes) n-nodes (inc n-nodes)]))]
    (add-edge g node1 node2 in neg)))

(defn fitness [fs g]
  (if (implements-every? g fs)
    (/ 2 (apply + (map count (vals g))))
    0))

(defn reaper [k n max-fit]
  (fn [[fitness g]]
    (or (>= fitness max-fit)
        (rand-bool (+ (/ k n) fitness)))))

(defn stoch-beam-gens [fs k]
  (let [inputs (apply inputs fs)
        fitness (partial fitness fs)]
    ((fn tail [gen max-fit]
       (lazy-seq
         (let [new (map (juxt fitness identity)
                        (map #(mutate (second %) inputs) gen))
               max-fit (apply max max-fit (map first new))
               next-gen (into gen new)
               next-gen (filter (reaper k (count next-gen) max-fit) next-gen)]
           (cons next-gen (tail next-gen max-fit)))))
     '([0 {}]) 0)))

(defn stoch-beam-slns [fs k]
  ((fn tail [slns]
     (lazy-seq
       (if-let [[head & more] (seq slns)]
         (cons (second head) (tail (drop-while #(= (first head) (first %))
                                               slns))))))
   (drop-while (comp zero? first)
               (map (partial apply max-key first)
                    (stoch-beam-gens fs k)))))
