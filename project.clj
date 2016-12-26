(defproject
  com.b2wdigital/pdg-core
  "0.1.0"
  :repositories
  [["mavencentral" {:url "https://repo1.maven.org/maven2"}]
   ["mavenstaging"
    {:gpg-sign true,
     :username nil,
     :password nil,
     :url
     "https://oss.sonatype.org/service/local/staging/deploy/maven2"}]
   ["clojars" {:url "http://clojars.org/repo"}]]
  :dependencies
  [[org.clojure/clojure "1.7.0"]
   [http-kit "2.1.18"]
   [onetom/boot-lein-generate "0.1.3" :scope "test"]
   [junit "4.12" :scope "test"]
   [expectations "2.0.9" :scope "test"]
   [ring/ring-codec "1.0.1"]
   [slingshot "0.12.2"]
   [org.clojure/core.async "0.1.346.0-17112a-alpha"]
   [com.fasterxml.jackson.core/jackson-databind "2.8.5"]
   [cheshire "5.5.0"]
   [adzerk/bootlaces "0.1.13"]]
  :source-paths
  ["src/java"]
  :resource-paths
  ["src/clj"])