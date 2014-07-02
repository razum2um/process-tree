(ns process-tree.core-test
  (:use expectations)
  (:require [process-tree.core :refer :all]
            [process-tree.utils :refer :all]
            ))

(def test-config {:Xvfb {:start "Xvfb :0 -screen 0 800x600x16"
                         :dependents :fluxbox}
                  :fluxbox {:start "fluxbox"
                            :dependencies :Xvfb
                            :dependents :skype}
                  :skype  {:start "echo username password | skype --pipelogin"
                           :dependencies :fluxbox}
                  :x11vnc {:start "x11vnc -xkb -forever"
                           :dependencies :Xvfb}})

;; dependencies

(defn dependency-names
  [name]
  (let [node (build-deps name test-config)
        deps (tree-seq has-dependencies? :dependencies node)]
    (map :name deps)))

(expect '("Xvfb") (dependency-names :Xvfb))
(expect '("x11vnc" "Xvfb") (dependency-names :x11vnc))
(expect '("fluxbox" "Xvfb") (dependency-names :fluxbox))
(expect '("skype" "fluxbox" "Xvfb") (dependency-names :skype))

;; dependents

(defn dependent-names
  [name]
  (let [node (build-deps name test-config)
        deps (tree-seq has-dependents? :dependents node)]
    (reverse (map :name deps))))

(expect '("skype" "fluxbox" "Xvfb") (dependent-names :Xvfb))
(expect '("x11vnc") (dependent-names :x11vnc))
(expect '("skype" "fluxbox") (dependent-names :fluxbox))
(expect '("skype") (dependent-names :skype))

