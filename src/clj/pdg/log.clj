(ns pdg.log)

(defn log-info [& text]
  (println "INFO " text))

(defn log-warn [& text]
  (println "WARN " text))

(defn log-error [& text]
  (println "ERROR " text))

(defmacro info [& text]
  `(log-info ~@text))

(defmacro warn [& text]
  `(log-warn ~@text))

(defmacro error [& text]
  `(log-error ~@text))
