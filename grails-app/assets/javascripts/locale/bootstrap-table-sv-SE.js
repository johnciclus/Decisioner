/*
 * Copyright (c) 2015-2016 Dilvan Moreira. 
 * Copyright (c) 2015-2016 John Garavito.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * Bootstrap Table Swedish translation
 * Author: C Bratt <bratt@inix.se>
 */
(function ($) {
    'use strict';

    $.fn.bootstrapTable.locales['sv-SE'] = {
        formatLoadingMessage: function () {
            return 'Laddar, vänligen vänta...';
        },
        formatRecordsPerPage: function (pageNumber) {
            return pageNumber + ' rader per sida';
        },
        formatShowingRows: function (pageFrom, pageTo, totalRows) {
            return 'Visa ' + pageFrom + ' till ' + pageTo + ' av ' + totalRows + ' rader';
        },
        formatSearch: function () {
            return 'Sök';
        },
        formatNoMatches: function () {
            return 'Inga matchande resultat funna.';
        },
        formatRefresh: function () {
            return 'Uppdatera';
        },
        formatToggle: function () {
            return 'Skifta';
        },
        formatColumns: function () {
            return 'kolumn';
        }
    };

    $.extend($.fn.bootstrapTable.defaults, $.fn.bootstrapTable.locales['sv-SE']);

})(jQuery);
