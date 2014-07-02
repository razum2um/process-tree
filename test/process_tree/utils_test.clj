(ns process-tree.utils-test
  (:use expectations)
  (:require [process-tree.utils :refer :all]))

(let [coll '(1 nil 2 nil 3)]
  (expect '(1 2 3)
          (compact coll)))

(expect nil (str-all))
(expect nil (str-all nil "some" " " "str"))
(expect "some str" (str-all "some" " " "str"))

