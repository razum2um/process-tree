(ns process-tree.core-test
  (:use expectations)
  (:require [process-tree.core :refer :all]))

(def test-config {:Xvfb {:start "Xvfb :0 -screen 0 800x600x16"}
                  :fluxbox {:start "fluxbox"
                            :dependencies :Xvfb}
                  :skype  {:start "echo username password | skype --pipelogin"
                           :dependencies :fluxbox}
                  :x11vnc {:start "x11vnc -xkb -forever"
                           :dependencies :Xvfb}})


(let [skype (build-deps :skype test-config)
      dependencies (tree-seq has-dependencies? :dependencies skype)
      names (map :name dependencies)]
  (expect '("skype" "fluxbox" "Xvfb") names))

(let [x11vnc (build-deps :x11vnc test-config)
      dependencies (tree-seq has-dependencies? :dependencies x11vnc)
      names (map :name dependencies)]
  (expect '("x11vnc" "Xvfb") names))

(let [xvfb (build-deps :Xvfb test-config)
      dependents (tree-seq has-dependencies? :dependents xvfb)
      names (map :name dependents)]
  (expect '("Xvfb" "fluxbox" "skype") names))

