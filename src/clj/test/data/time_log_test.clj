(ns data.time-log-test
  (:require [clojure.test         :refer [is deftest testing]]
            [data.time-log  :as tlg]))

(deftest valid-read-time-log
  (testing "good read of time_log.csv with correct fields"
    (let [entries (tlg/read-time-log)]
      (is (> (count entries) 1)
          "Expected more than one entry")
      (is (every? #(contains? % :timeOnIceH) entries)
          "Every entry should have the :timeOnIceH key"))))
