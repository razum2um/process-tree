(ns process-tree.node-test
  (:use expectations)
  (:import java.text.SimpleDateFormat)
  (:require [process-tree.node :refer :all]))

(let [date (.parse (SimpleDateFormat. "dd-MMM-yy") "02-July-14")]
  (expect (obj-to-map date getDate :day getMonth :month getYear :year)
          (in {:year (- 2014 1900) :month 6 :day 2})))

