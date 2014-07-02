(ns process-tree.core
  (:require [process-tree.utils :refer :all]
            [environ.core :refer [env]]
            [clojure.string :refer [trim join]])
  (:import [com.jezhumble.javasysmon JavaSysMon]))

(def config (or (env :process-tree) {}))

(defrecord ProcessNode
  [pid name command start dependents dependencies]
  Object
  ;; avoid recurcion by default
  (toString [n]
    (str "#ProcessNode{"
         (join ", " (compact (map #(str-all % ": " (% n)) '(:pid :name :start :command))))
         ", dependencies: [" (join ", " (map :name (:dependencies n)))
         "], dependents: [" (join ", " (map :name (:dependents n))) "]}")))

;; dependencies

(defn has-dependencies?
  [node]
  (not (empty? (:dependencies node))))

(defn has-dependents?
  [node]
  (not (empty? (:dependents node))))

(defn dependent-keys-for
  [key config]
  (map first
       (filter (fn [[_ v]] (some #(= % key) (compact (:dependencies v))))
                     config)))

(defn build-deps
  ([key config] (build-deps key config '()))
  ([key config dependents]
   (let [node-dependents (map #(build-deps % config) (dependent-keys-for key config))
         node-dependencies (map #(build-deps % config node-dependents) (compact (:dependencies (key config))))
         node (map->ProcessNode (merge
                                  (select-keys (key config) [:name :start])
                                  {:name (name key)
                                   :dependents (concat dependents node-dependents)
                                   :dependencies (concat node-dependencies '())}))]
     (identity node))))

(defn dependencies-for
  ":skype | <#skype> => (<#Xvfb> <#fluxbox> <#skype>)"
  [name config]
  (let [name (if (keyword? name) (identity name) (:name name))
        node (build-deps name config)
        deps (tree-seq has-dependencies? :dependencies node)]
    (reverse deps)))

(defn dependents-for
  ":Xvfb | <#Xvfb> => (<#x11vnc> <#skype> <#fluxbox> <#Xvfb>)"
  [name config]
  (let [name (if (keyword? name) (identity name) (:name name))
        node (build-deps name config)
        deps (tree-seq has-dependents? :dependents node)]
    (reverse deps)))

;; processes

(defn process-to-map
  [process]
  (if (nil? process)
    (identity {:pid nil})
    (update-in
      (obj-to-map (.processInfo process) getPid :pid getName :name getCommand :command)
      [:command]
      trim)))

(defn process-to-node
  [process]
  (when process
    (map->ProcessNode
      (process-to-map process))))

;; Find process

(defn filter-process
  [node process]
  (identity
    (or
      (= (:pid  node) (-> process .processInfo .getPid))
      (= (:name node) (-> process .processInfo .getName)))))

(defn process-root
  []
  (.find (.processTree (JavaSysMon.)) 1))

(defn process-subtree
  ([p]
   (tree-seq
     #(not (-> % .children .isEmpty))
     #(into '() (.children %))
     p)))

(defn process-tree
  ([] (process-subtree (process-root)))
  ([p] (process-subtree p)))

(defn find-process
  [node]
  (let [dependencies (map find-process (:dependencies node))
        dependents (map find-process (:dependents node))]
    (assoc (merge node
                  (process-to-map
                    (first
                      (filter
                        (partial filter-process node)
                        (process-tree)))))
           :dependencies
           dependencies
           :dependents
           dependents)))

(defn find-node
  "(find :xvfb) -> ProcessNode"
  [name]
  (find-process (build-deps name config)))

;; Fire process

(defn exec
  [cmd]
  (.exec (Runtime/getRuntime) (into-array ["/bin/sh" "-c" cmd])))

(defn start-node
  [node]
  (if (nil? (:pid node))
    (do
      (let [new-deps (doall (map start-node (:dependencies node)))]
        (do
          (exec (:start node))
          (loop [new-node (find-process node)]
            (if (nil? (:pid new-node))
              (do
                ;; (pprint (str "wait for" new-node))
                (Thread/sleep 100)
                (recur (find-process node)))))
          (assoc (find-process node) :dependencies new-deps))))
    (find-process node)))

(defn sigterm
  [pid]
  (.exec (Runtime/getRuntime)
         (into-array ["/bin/kill" "-15" (str pid)])))

(defn stop-node
  [node]
  (let [pid (:pid node)]
    (do (doall (map stop-node (dbg (:dependents node))))  ;; halt children
        (sigterm pid)                               ;; halt self
        (loop [new-node (find-process node)]
          (if (some? (dbg (:pid new-node)))
            (do
              (clojure.pprint/pprint (str "wait for term: " new-node))
              (Thread/sleep 100)
              (recur (find-process node)))))
        )))

;; Public

(defn run
  [& args]
  (map #(-> % find-node start-node) args))

(defn term
  [& args]
  (map #(-> % find-node stop-node) args))

