(ns com.chariotsolutions.indexed-map-test
  (:require [clojure.test :refer :all]
            [com.chariotsolutions.indexed-map :refer :all]))

(deftest very-simple-test
  (testing "simple put/get of keyed value"
    (let [uut (-> (indexed-map)
                  (assoc :foo 12))]
      (is (= 12 (:foo uut))))))

(deftest reduce-kv-test
  (testing "reduce-kv interface"
    (let [uut (-> (indexed-map)
                  (assoc :foo 12))]
      (reduce-kv (fn [init k v]
                   (is (= 147 init))
                   (is (= :foo k))
                   (is (= 12 v)))
                 147 uut))))
