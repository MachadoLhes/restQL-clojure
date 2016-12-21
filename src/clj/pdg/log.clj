(ns pdg.log)

(defn log-info [& text]
  )

(defn log-warn [& text]
  )

(defn log-error [& text]
  )

(defmacro info [& text]
  `(log-info ~@text))

(defmacro warn [& text]
  `(log-warn ~@text))

(defmacro error [& text]
  `(log-error ~@text))

;nadiamfp@uol.com.br
