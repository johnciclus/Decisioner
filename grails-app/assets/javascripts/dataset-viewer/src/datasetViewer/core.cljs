;
; Copyright (c) Dilvan A. Moreira 2016. All rights reserved.
;
;  This file is part of ePAD2.
;
;  ePAD2 is free software: you can redistribute it and/or modify
;  it under the terms of the GNU General Public License as published by
;  the Free Software Foundation, either version 3 of the License, or
;  (at your option) any later version.
;
;  ePAD2 is distributed in the hope that it will be useful,
;  but WITHOUT ANY WARRANTY; without even the implied warranty of
;  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;  GNU General Public License for more details.
;
;  You should have received a copy of the GNU General Public License
;  along with ePAD2.  If not, see <http://www.gnu.org/licenses/>.
;

(ns datasetViewer.core
  (:require-macros [datasetViewer.macros :refer [$]])
  (:require [reagent.core :refer [render]]
            [re-frame.core :refer [reg-event-db reg-sub path dispatch dispatch-sync subscribe]]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [datasetViewer.db :refer [initial-state]]
            [re-frisk.core :refer [enable-re-frisk!]]))


;;;

;(defn clip [[a b] x] (max (min x b) a))

(defn update-all [map a b & args]
  (let [num (count args)
        map1 (update-in map a b)]
    (if (even? num)
      (if (zero? num)
        map1
        (apply update-all map1 args))
      (throw (js/Error. "update-all has one db and path and function pairs.")))))

(defn assoc-all [map a b & args]
  (let [num (count args)
        map1 (assoc-in map a b)]
    (if (even? num)
      (if (zero? num)
        map1
        (apply assoc-all map1 args))
      (throw (js/Error. "assoc-all has one db and path and function pairs.")))))




;; -- Event Handlers ----------------------------------------------------------

(reg-event-db                                               ;; setup initial state
  :initialize                                               ;; usage:  (dispatch [:initialize])
  (fn [db _]
    (merge db initial-state)))                              ;; what it returns becomes the new state

(reg-event-db
  :change-mode
  (path [:tool])  ;; this is middleware
  (fn [_ [_ value]]   ;; path middleware adjusts the first parameter
    value))

;; -- Subscription Handlers ---------------------------------------------------

(reg-sub
  :initialize
  (fn [db _]
    (db :views)))

(reg-sub
  :change-mode
  (fn [db _]
    (db :tool)))

;; -- View Components ---------------------------------------------------------

(defn button [icon tooltip selected event]
  [:div {:title tooltip}
   [ui/flat-button {:icon             icon
                    :background-color (if selected "#d0d060" (color :background-color))
                    :style            {:margin    "12px 0px"
                                       :min-width "35px"}
                    :on-touch-tap     #(dispatch event)}]])

(defn toolbar []
  (let [mode (subscribe [:change-mode])]
    (fn []
      [ui/toolbar
       [ui/toolbar-group ;{:first-child true}
        [ui/toolbar-title {:text "Tools"}]
        [button (ic/action-search) "zoom" (= @mode :zoom) [:change-mode :zoom]]
        [button (ic/maps-my-location) "scroll" (= @mode :scroll) [:change-mode :scroll]]
        [button (ic/image-brightness-6) "windowing" (= @mode :gradient) [:change-mode :gradient]]
        [button (ic/action-pan-tool) "move" (= @mode :move) [:change-mode :move]]
        [button (ic/action-polymer) "3D Edit" (= @mode :3D) [:change-mode :3D]]]])))
       ;[ui/toolbar-group ;{:last-child true}
       ; ;[ui/toolbar-separator]
       ; [button (ic/communication-call-made) "move +1" false [:inc]]
       ; [button (ic/communication-call-received) "move -1" false [:dec]]]])))
       ;

(defn simple-example []
  (let [editor-id (str "editor" 0)]

    [ui/mui-theme-provider {:mui-theme (get-mui-theme
                                         {:palette {:text-color (color :blue800)}})}
     [:div
      [ui/app-bar {:title                 "ePAD2"
                   :icon-class-name-right "muidocs-icon-navigation-expand-more"}]
      [toolbar editor-id]
      [ui/paper
       [:div {:class "editor-holder"}]]]]))



;; -- Entry Point -------------------------------------------------------------

(defn ^:export run
  []
  (dispatch-sync [:initialize])
  ;(enable-re-frisk!)
  (render [simple-example]
          (js/document.getElementById "app")))