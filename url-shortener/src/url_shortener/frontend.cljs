(ns url-shortener.frontend
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent]
            [reagent.dom.client :as rdomc]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(defonce *app-state (reagent/atom {:page :main
                                   :short-url ""}))

(defn header [text]
  [:h2 {:style {:margin "8px 4px"}}
   text])

(defn short-page []
  (let [short-url (:short-url @*app-state)]
    [:<>
     [header "Your short link"]
     [:div {:style {:margin "16px 4px"}}
      [:a {:target "_blank"
           :href short-url}
       short-url]]
     [:button.block {:on-click (fn [_e]
                                 (swap! *app-state assoc :page :main))}
      "BACK"]]))

(defn main-page []
  (let [*input-value (reagent/atom "")]
    (fn []
      [:<>
       [header "Shorten a long link"]
       [:label {:for "url-input"
                :style {:margin-left 4}}
        "Paste a long URL"]
       [:div {:style {:display "flex"}}
        [:input.block.fixed {:id "url-input"
                             :type "url"
                             :placeholder "Example: http://super-long-link.com/"
                             :style {:width "80%"}
                             :value @*input-value
                             :on-change (fn [e]
                                          (reset! *input-value (-> e .-target .-value)))}]
        [:button.block.accent
         {:on-click (fn [_e]
                      (when (seq @*input-value)
                        (go (let [response (<! (http/post "/" {:json-params {:url @*input-value}}))]
                              (if (:success response)
                                (let [id        (-> response :body :id)
                                      host      (.. js/window -location -href)
                                      short-url (str host id)]
                                  (js/console.log short-url)
                                  (reset! *app-state {:short-url short-url
                                                      :page :short}))
                                (js/console.log "Something went wrong: " response))))))}
         "SHORTEN IT"]]])))

(defn app []
  (let [page (:page @*app-state)]
    [:div.card.fixed.block {:style {:margin "0 auto"
                                    :font-family "Arial, serif"
                                    :width "800px"}}
     (case page
       :main  [main-page]
       :short [short-page]
       [header "Page not found"])]))

(defonce root-el
  (rdomc/create-root (js/document.getElementById "root")))

(defn ^:dev/after-load mountit []
  (rdomc/render root-el [app]))

(defn init []
  (mountit))
