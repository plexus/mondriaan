(ns mondriaan.core)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; poor man's hiccup

(def text-node #(js/document.createTextNode %))

(defn node [type & children]
  (let [n (js/document.createElement (name type))]
    (doseq [c children]
      (.appendChild n c))
    n))

(defn set-style [node styles]
  (if styles
    (js/Object.assign (.-style node) styles)
    node))

(defn set-attrs [node attrs]
  (set-style node (clj->js (:style attrs)))
  (js/Object.assign node (clj->js (dissoc attrs :style))))

(defn html* [type attrs children]
  (let [n (apply node type children)]
    (set-attrs n attrs)
    n))

(defn html [args]
  (if (string? args)
    (text-node args)
    (let [[type & xs] args]
      (if (map? (first xs))
        (html* type (first xs) (map html (rest xs)))
        (html* type {} (map html xs))))))

;; / poor man's hiccup
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn empty! [node]
  (loop [c (.-firstChild node)]
    (when c
      (.removeChild node c)
      (recur (.-firstChild node)))))

(extend-type js/HTMLDivElement
  IPrintWithWriter
  (-pr-writer [obj writer _opts]
    (write-all writer (.-outerHTML obj))))

(defn mcontainer [content]
  [:div {:style {:display "flex"
                 :width "100%"
                 :height "100%"
                 :border "6px solid black"}} content])

(defn mcell []
  [:div {:style {:height "auto"
                 :width "auto"
                 :display "flex"
                 :flex-direction (rand-nth ["column" "row"])
                 :background-color (rand-nth ["red" "blue" "yellow" "white" "white" "white"])
                 :flex-grow (inc (rand-int 4))
                 :flex-basis "auto"}}])

(defn hdivider []
  [:div {:style {:background-color "black"
                 :flex "0 0 6px"}}])

(defn mcells [spread depth]
  (if (= depth 0)
    (mcell)
    (into (mcell)
          (interpose (hdivider)
                     (for [_ (range spread)]
                        (mcells spread (dec depth)))))))


(defn parse-location-hash []
  (map long (re-seq #"[0-9]+" js/window.location.hash)))

(defn main []
  (let [app (js/document.getElementById "app")
        [spread depth] (parse-location-hash)]
    (empty! app)
    (.appendChild app (html (mcontainer (mcells (or spread 2) (or depth 3)))))))

(set! js/window.onhashchange #(main))

(main)
