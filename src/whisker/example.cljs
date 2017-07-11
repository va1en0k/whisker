(ns whisker.example
  (:require [rum.core :as rum]))

(defonce app-state (atom {:pos [100 200]}))

(defonce animation-state {})

(defn animate [key value & kws]
  (if-not (@animation-state key)
    (do
      (swap! animation-state assoc key [(js/Date.now) value])
      value)
    (let [[was last-value] (@animation-state key)
          now (js/Date.now)]
      last-value)))

(rum/defc graphics < rum/reactive []
  (let [state (rum/react app-state)]
    [:svg {:width "300px" :height "300px"}
     [:rect {:width "300px" :height "300px" :fill "black"}]

     [:circle {:r    "10"
               :fill "white"
               :cx   @(animate :x (first (:pos state))
                               :length 10)
               :cy   @(animate :y (second (:pos state))
                               :length 10)}]]))

(rum/defc number-input < rum/reactive [cursor]
  [:input {:value       (rum/react cursor)
           :on-change   #(reset! cursor (js/parseInt (.. % -target -value)))
           :on-key-down #(let [d (if (.-shiftKey %) 10 1)]
                           (case (.-key %)
                             "ArrowUp" (swap! cursor (partial + d))
                             "ArrowDown" (swap! cursor (fn [v] (- v d)))))}])

(rum/defc controls []
  [:div
   (number-input (rum/cursor-in app-state [:pos 0]))
   (number-input (rum/cursor-in app-state [:pos 1]))])

(rum/defc ui []
  [:div
   (graphics)
   (controls)])

(rum/mount (ui) (js/document.getElementById "app"))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
