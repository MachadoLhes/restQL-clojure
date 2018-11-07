(defproject b2wdigital/restql-core "2.6.0-SNAPSHOT"
  :description "Microservice query language"
  :url "https://github.com/B2W-Digital/restQL-core"
  :license {:name "MIT"
            :url "http://www.opensource.org/licenses/mit-license.php"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.logging "0.4.1"]
                 [aleph "0.4.6"]
                 [ring/ring-codec "1.0.1"]
                 [slingshot "0.12.2"]
                 [instaparse "1.4.8"]
                 [prismatic/schema "1.1.7"]
                 [org.clojure/core.async "0.3.443"]
                 [com.fasterxml.jackson.core/jackson-databind "2.8.5"]
                 [cheshire "5.5.0"]
                 [se.haleby/stub-http "0.2.3"]
                 [adzerk/bootlaces "0.1.13"]
                 [org.apache.httpcomponents/httpasyncclient "4.1.4"]
                 [org.clojure/tools.reader "1.0.5"]]
  :aot [restql.core.api.RestQLJavaApi]
  :profiles {:test {:dependencies [[se.haleby/stub-http "0.2.3"]]}
             :uberjar { :aot :all }}
  :plugins [[lein-cloverage "1.0.7-SNAPSHOT"]]
  :source-paths ["src/main"]
  :resource-paths ["src/resources"]
  :test-paths ["test/integration" "test/unit"]
)
