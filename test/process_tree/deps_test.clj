(ns process-tree.deps-test
  (:use expectations)
  (:require [process-tree.utils :refer :all]
            [process-tree.deps :refer :all]))

(def test-config {:Xvfb {:start "Xvfb :0 -screen 0 800x600x16"}
                  :fluxbox {:start "fluxbox"
                            :dependencies :Xvfb}
                  :skype  {:start "echo username password | skype --pipelogin"
                           :dependencies :fluxbox}
                  :x11vnc {:start "x11vnc -xkb -forever"
                           :dependencies :Xvfb}})

;; dependencies

(defn dependency-names
  [name]
  (map :name (dependencies-for name test-config)))

(expect '("Xvfb") (dependency-names :Xvfb))
(expect '("Xvfb" "x11vnc") (dependency-names :x11vnc))
(expect '("Xvfb" "fluxbox") (dependency-names :fluxbox))
(expect '("Xvfb" "fluxbox" "skype") (dependency-names :skype))

;; dependents

(defn dependent-names
  [name]
  (map :name (dependents-for name test-config)))

(expect '("x11vnc" "skype" "fluxbox" "Xvfb") (dependent-names :Xvfb))
(expect '("x11vnc") (dependent-names :x11vnc))
(expect '("skype" "fluxbox") (dependent-names :fluxbox))
(expect '("skype") (dependent-names :skype))

