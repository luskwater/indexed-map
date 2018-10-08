# indexed-map

Provide indexing on data in a map

## Usage

### With map values that are maps

    (ns indexed-map.demo
      "Demo use of indexed-map"
      (:require [com.chariotsolutions.indexed-map :as im :refer [indexed-map indexed-get]]))
    (defn sample-code
      []
      (let [database (-> (indexed-map [:first :last])
                         (assoc :id01 {:first "Jordan" :last "Marsh"})
                         (assoc :id02 {:first "Hunter" :last "Forest"})
                         (assoc :id02 {:first "Jordan" :last "Morgan"}))]
        (indexed-get database :first "Jordan")))
    (sample-code)
    ;;==> [{:first "Jordan", :last "Morgan"} {:first "Jordan", :last "Marsh"}]

### With map values that are atoms

    ;; Not yet implemented
    
## Performance

With 2,000,001 records in an `IndexedMap`, with *one* record with
field `:c` containing `"QQ"` ("RAW" indicates a normal Clojure map
containing the same data.)

    Searching 'Sequential for 'QQ'' in 2000001 record(s)
    "Elapsed time: 1185.022334 msecs"
    Found 1 record(s)
    
    Searching 'Sequential RAW for 'QQ'' in 2000001 record(s)
    "Elapsed time: 1131.917573 msecs"
    Found 1 record(s)
    
    Searching 'Indexed for 'QQ'' in 2000001 record(s)
    "Elapsed time: 0.036315 msecs"
    Found 1 record(s)

Searching the same two (indexed and Clojure) databases for a value
that appears 40,000 times:

    Searching 'Sequential for 'LO'' in 2000001 record(s)
    "Elapsed time: 1728.361863 msecs"
    Found 400000 record(s)

    Searching 'Sequential RAW for 'LO'' in 2000001 record(s)
    "Elapsed time: 1514.728234 msecs"
    Found 400000 record(s)

    Searching 'Indexed for 'LO'' in 2000001 record(s)
    "Elapsed time: 563.877368 msecs"
    Found 400000 record(s)

(Times on a MacBook Pro.)

## License

Copyright © 2018 Chariot Solutions LLC

Distributed under the Eclipse Public License version 1.0
