<!--
  Copyright (c) 2016-$today.year Dilvan Moreira.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
  -->

<!--asset:javascript src="dataset-viewer/js/client.js"/-->

<script src="/assets/bower_components/webcomponentsjs/webcomponents-lite.js"></script>
<!--asset:javascript src="bower_components/webcomponentsjs/webcomponents-lite.js"/-->

<link rel="import" href="/assets/bower_components/evaluation-filter/evaluation-filter.html">
<!--asset:javascript src="bower_components/evaluation-filter/evaluation-filter.html"/-->
<!--link href="${assetPath(src: 'bower_components/evaluation-filter/evaluation-filter.html')}"/-->
<div>
    <div>
        <evaluation-filter></evaluation-filter>
    </div>
    <p>Type3</p>
    <div id="app">
        <h1>Dicom Roi Editor</h1>
    </div>

    <script type="application/javascript">

//        window.onload = function () {
//            datasetViewer.core.run();
//            //resizeCanvas();
//        };

//        function resizeCanvas() {
//            const divs = document.getElementsByClassName("editor-holder");
//            for (let i=0; i<divs.length; i++) {
//                const c = divs[i].childNodes;
//                for(let j = 0; j < c.length; j++) {
//                    if ( c[j].nodeName.toLowerCase() === "canvas" ) {
//                        c[j].resize(divs[i].clientWidth);
//                        break;
//                    }
//                }
//            }
//        }
    </script>
</div>