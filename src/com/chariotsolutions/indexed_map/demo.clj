(ns com.chariotsolutions.indexed-map.demo
  "Figure out how an indexed map would work"
  (:require [com.chariotsolutions.indexed-map :as im :refer [indexed-map indexed-get]]))

(defn load-a-lot
  "Build an `indexed-map` of specified size"
  [n]
  (let [red-fn (fn [im [a b c]]
                 (assoc im (str a) {:a a :b b :c c}))]
    (-> (reduce red-fn (indexed-map [:a :b :c])
             (map #(vector %1 %2 %3) (range n) (cycle (range 7))
                  (cycle ["HE" "EL" "LL" "LO" "OH"])))
        ;; Add one unique record
        (assoc :only-keyword {:a 9999 :b 8 :c "QQ"}))))


(defn run-and-report
  "Run a `search-fn` through a `source` map for records with a given
  value in a particular field. Print `details` and timing. Return the
  vector containing search results."
  [details source search-fn]
  (printf "Searching '%s' in %d record(s)\n" details (count source))
  (let [result (time (doall (into #{} (search-fn source))))]
    (printf "\tFound %d record(s)\n" (count result))
    result))

(defn try-it-out
  "Search an `indexed-map` and a `hash-map` for unique records with a
  value in some field, or for many duplicates, displaying elapsed
  times using various methods of searching."
  ([]
   (try-it-out 20000))
  ([number-to-load]
   (let [test-map (load-a-lot number-to-load)
         clj-map (into {} (seq test-map))
         sequential-search-fn (fn [kwd val]
                                (fn [database]
                                  (filter #(#{val} (kwd %))
                                          (vals database))))
         sequential-QQ (sequential-search-fn :c "QQ")
         sequential-LO (sequential-search-fn :c "LO")
         sequential-single (run-and-report "Sequential for 'QQ'"
                                           test-map
                                           sequential-QQ)
         sequential-single-clj (run-and-report "Sequential RAW for 'QQ'" clj-map sequential-QQ)
         indexed-single (run-and-report "Indexed for 'QQ'" test-map
                                        (fn [database]
                                          (indexed-get database :c "QQ")))
         sequential-many (run-and-report "Sequential for 'LO'"
                                         test-map
                                         sequential-LO)
         sequential-many-clj (run-and-report "Sequential RAW for 'LO'"
                                         clj-map
                                         sequential-LO)

         indexed-many (run-and-report "Indexed for 'LO'" test-map
                                      (fn [database]
                                        (indexed-get database :c "LO")))]
     (println "Single equal? " (= sequential-single indexed-single sequential-single-clj))
     (println "Many equal? " (= sequential-many indexed-many sequential-many-clj)))))


(defn sample-code
  []
  (let [database (-> (indexed-map [:first :last])
                     (assoc :id01 {:first "Jordan" :last "Marsh"})
                     (assoc :id02 {:first "Hunter" :last "Forest"})
                     (assoc :id02 {:first "Jordan" :last "Morgan"}))]
    (indexed-get database :first "Jordan")))
