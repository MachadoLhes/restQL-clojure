(set-env!
  :target-path "target/"

  :source-paths #{"src/java"}
  :resource-paths #{"src/clj"}

  :repositories [["mavencentral" {:url "https://repo1.maven.org/maven2"}]
                 ["mavenstaging" {:gpg-sign true
                                  :username (System/getenv "OSSRH_USER")
                                  :password (System/getenv "OSSRH_PASSWORD")
                                  :url "https://oss.sonatype.org/service/local/staging/deploy/maven2"
                                  }]
                 ["clojars" {:url "http://clojars.org/repo"}]
                 ]

  :dependencies '[[org.clojure/clojure "1.7.0"]
                  [http-kit "2.1.18"]
                  [onetom/boot-lein-generate "0.1.3" :scope "test"]
                  [junit/junit "4.12" :scope "test"]
                  [expectations "2.0.9" :scope "test"]
                  [ring/ring-codec "1.0.1"]
                  [slingshot "0.12.2"]
                  [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                  [com.fasterxml.jackson.core/jackson-databind "2.8.5"]
                  [cheshire "5.5.0"]
                  [adzerk/bootlaces "0.1.13"]]

  )

(task-options!
  pom {:project 'com.b2wdigital/pdg-core
       :version "0.1.0"}

  aot {:namespace '[pdg-core.PdgJavaApi]}

  jar {:manifest {}})

(require 'boot.lein)

(deftask deps "genereate leiningen" []
  (boot.lein/generate))

(deftask uberjar "build project" []
  (comp (pom)
    (aot)
    (javac)
    (uber)
    (jar)
    (target "target/")))

(deftask build "build project" []
  (comp (pom)
    (aot)
    (javac)
    (jar)
    (target "target/")))

(deftask release "pushes the project to maven"
         [r repo      NAME  str "The name of the deploy repository."
          e repo-map  REPO  edn "The repository map of the deploy repository."
          P pom       PATH  str "The pom.xml file to use (see install task)."
          ]
  (let [r (get (->> (get-env :repositories) (into {})) repo)
        repo-map (merge r (when repo-map ((configure-repositories!) repo-map)))]
    (fn [next-task]
      (fn [fileset]
        (println "REPOSITORY" {:repo repo
                               :repo-map repo-map
                               :pom pom


                               })
        (next-task fileset)))))
