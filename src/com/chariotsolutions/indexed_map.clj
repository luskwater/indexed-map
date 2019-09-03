(ns com.chariotsolutions.indexed-map
  (:require
   [clojure.core.protocols :refer [IKVReduce]])
  (:import
   (clojure.lang IFn RT IMapEntry ISeq Associative
                 Seqable)))

(defprotocol IIMInternals
  (record-index [_ primary-key record] "Record secondary keys")
  (remove-index [_ primary-key record] "Remove record's keys from indexes")
  (keys-for [_ secondary-key search-value] "Return matching primary keys"))

(defrecord IndexedMapInternals [secondary-keys the-indexes]
  IIMInternals
  (keys-for [_ secondary-key search-value]
    (when-not (some #{secondary-key} secondary-keys)
      (throw (ex-info "Secondary key not found" {:key secondary-key
                                                 :known-keys secondary-keys})))
    (get-in the-indexes [secondary-key search-value] []))
  (record-index
    [this primary-key record]
    (assoc this :the-indexes
           (loop [idxs the-indexes
                  [pair & pairs] (seq (select-keys record secondary-keys))]
             (if-not pair
               idxs
               (recur (if (second pair)
                        (update-in idxs pair conj primary-key)
                        idxs)
                      pairs)))))
  (remove-index
    [this primary-key record]
    (assoc this :the-indexes
           (loop [idxs the-indexes
                  [pair & pairs] (seq (select-keys record secondary-keys))]
             (if-not pair
               idxs
               (recur (if (second pair)
                        (update-in idxs pair (partial remove #{primary-key}))
                        idxs)
                      pairs)))))
  Object
  (toString [_]
    (format "keys: %s; indexes: %s"
            (pr-str secondary-keys) (pr-str the-indexes))))

(defn make-internals
  [secondary-keys]
  (->IndexedMapInternals secondary-keys
                         (into {} (map #(vector % {})
                                       secondary-keys))))

(defprotocol IIndexedMap
  "Stuff for ISAM maps"
  (indexed-get [_ secondary-key value] "Return vector of matching records"))

(declare ->IndexedMap)

(deftype IndexedMap [the-map internals]
  Seqable
  (^ISeq seq [_] (seq the-map))
  ISeq
  (first [_] (first the-map))
  (next [_] (next the-map))
  (cons [_ o] (cons the-map o))
  IIndexedMap
  (indexed-get [_ secondary-key value]
    (let [primary-keys (keys-for internals secondary-key value)]
      (into [] (map the-map primary-keys))))
  Associative
  (valAt [this ^Object primary-key]
    (get the-map primary-key))
  (valAt [this ^Object primary-key ^Object the-not-found]
    (get the-map primary-key the-not-found))
  (^boolean containsKey [this ^Object primary-key]
    (let [result (.containsKey the-map primary-key)]
      (boolean result)))
  (^IMapEntry entryAt [this ^Object primary-key]
    (let [the-val (get the-map primary-key)]
      (reify IMapEntry
        (key [_] primary-key)
        (val [_] the-val))))
  (^Associative assoc [this ^Object primary-key ^Object the-val]
   (let [new-internals (record-index internals primary-key the-val)]
     (->IndexedMap (assoc the-map primary-key the-val) new-internals)))
  IKVReduce
  (kv-reduce [this f ^Object init]
    (reduce-kv f init the-map))
  Object
  (toString [_]
    (str the-map "\n" internals)))

(defn indexed-map
  "Return an indexed-map whatever that is"
  ([]
   (->IndexedMap {} (make-internals [])))
  ([secondary-keys]
   (->IndexedMap {} (make-internals secondary-keys))))
