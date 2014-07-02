(defproject process-tree "0.1.0-SNAPSHOT"
  :description "Manage your dependent processes in declarative way"
  :url "http://github.com/razum2um/process-tree"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.outr.javasysmon/javasysmon_2.10 "0.3.4"]
                 [environ "0.5.0"]]
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.4"]
                                  [expectations "2.0.6"]
                                  [lein-expectations "0.0.5"]]}}
  :plugins [[lein-expectations "0.0.7"]
            [lein-autoexpect "1.0"]])
