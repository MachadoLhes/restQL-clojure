(set-env!

  :target-path "target/"

  :source-paths #{"src/java"}
  :resource-paths #{"src/clj"}

  :dependencies '[[org.clojure/clojure "1.7.0"]
                  [http-kit "2.1.18"]
                  [onetom/boot-lein-generate "0.1.3" :scope "test"]
                  [ring/ring-codec "1.0.1"]
                  [expectations "2.0.9"]
                  [slingshot "0.12.2"]
                  [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                  [com.fasterxml.jackson.core/jackson-databind "2.8.5"]
                  [cheshire "5.5.0"]]

  :dev-dependencies '[[junit/junit "4.12"]])

(task-options!
  pom {:project 'pdg-core
       :version "0.1.0-SNAPSHOT"}

  aot {:namespace '[pdg-core.PdgJavaApi]}

  jar {:manifest {}})

(require 'boot.lein)

(deftask deps "genereate leiningen" []
         (boot.lein/generate))

(deftask build "build project" []
         (comp (aot)
               (javac)
               (uber)
               (jar)
               (target "target/")))