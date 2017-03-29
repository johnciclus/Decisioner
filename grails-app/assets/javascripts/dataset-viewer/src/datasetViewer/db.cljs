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

(ns datasetViewer.db)

(def initial-state
  {:current "editor0"
   :tool :gradient
   :views {"editor0" {:active-plane :all
                      :axial    {:x 0.5 :y 0.5 :zoom 1 :imgCoord 0.5}
                      :sagittal {:x 0.5 :y 0.5 :zoom 1 :imgCoord 0.5}
                      :frontal  {:x 0.5 :y 0.5 :zoom 1 :imgCoord 0.5}}}})
