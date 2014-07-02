(ns process-tree.utils-test
  (:use expectations)
  (:import java.text.SimpleDateFormat)
  (:require [process-tree.utils :refer :all]))

(let [date (.parse (SimpleDateFormat. "dd-MMM-yy") "02-July-14")]
  (expect (obj-to-map date getDate :day getMonth :month getYear :year)
          (in {:year (- 2014 1900) :month 6 :day 2})))

(let [coll '(1 nil 2 nil 3)]
  (expect '(1 2 3)
          (compact coll)))

