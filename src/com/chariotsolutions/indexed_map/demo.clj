(ns com.chariotsolutions.indexed-map.demo
  "Figure out how an indexed map would work"
  (:require [com.chariotsolutions.indexed-map :as im :refer [indexed-map indexed-get]]))

(defn load-a-lot
  "Build an `indexed-map` of specified size"
  [n]
  (let [red-fn (fn [im [a b c]]
                 (assoc im (str a) {:a a :b b :c c}))]
    (reduce red-fn (indexed-map [:a :b :c])
            (map #(vector %1 %2 %3) (range n) (cycle (range 7))
                 (cycle ["HE" "EL" "LL" "LO" "OH"])))))


(defn try-it-out
  []
  (let [test-map (load-a-lot 50000)
        sequential-results
        (time (doall (into #{} (filter #(#{"OH"} (:c %))
                                       (vals test-map)))))
        indexed-results
        (time (doall (into #{} (indexed-get test-map :c "OH"))))]
    (println "Equal? " (= sequential-results indexed-results))))
