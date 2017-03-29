(ns datasetViewer.dev
  (:require [datasetViewer.core :as editor]
            [figwheel.client :as fw]))

(fw/start {:on-jsload editor/run
           :websocket-url "ws://localhost:3449/figwheel-ws"})

