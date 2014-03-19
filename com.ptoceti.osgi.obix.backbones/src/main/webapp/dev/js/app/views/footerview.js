/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : Ptoceti
 * PROJECT : Obix Backbones
 * FILENAME : footerview.js
 * 
 * This file is part of the Ptoceti project. More information about
 * this project can be found here: http://www.ptoceti.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2013 - 2014 Ptoceti
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
define([ 'backbone', 'marionette', 'underscore', 'jquery', "i18n!nls/lobbytext", 'bootstrap' ], function(Backbone, Marionette, _, $,
		localizedLobbyText) {

	var FooterView = Marionette.ItemView.extend({

		template : 'footer',
		// el: $("#footer"),
		
		ui : {
			footerCollapseElem : "#footerCollapse",
			footerCollapseControlElem : '#footerCollapseControl'
		},
		
		events : {
			"hidden.bs.collapse #footerCollapse" : "onFooterCollapsed",
			"shown.bs.collapse #footerCollapse" : "onFooterShown",
		},
		
		initialize : function() {
			_.bindAll(this, 'onFooterCollapsed','onFooterShown');
		},
		
		onFooterCollapsed : function() {
			this.ui.footerCollapseControlElem.removeClass('glyphicon-chevron-down');
			this.ui.footerCollapseControlElem.addClass('glyphicon-chevron-right');
			
		},
		
		onFooterShown : function() {
			this.ui.footerCollapseControlElem.removeClass('glyphicon-chevron-right');
			this.ui.footerCollapseControlElem.addClass('glyphicon-chevron-down');
		},

		/*
		 * Override default behaviour of marionette serialize data to take as
		 * view data only what is necessary from About complex model.
		 */
		serializeData : function() {
			var obixVersion = this.model.getObixVersion().toJSON();
			var productUrl = this.model.getProductUrl().toJSON();
			var productName = this.model.getProductName().toJSON();
			var productVersion = this.model.getProductVersion().toJSON();
			var vendorName = this.model.getVendorName().toJSON();
			var vendorUrl = this.model.getVendorUrl().toJSON();
			var serverName = this.model.getServerName().toJSON();
			var serverBootTime = (new Date(parseInt(this.model.getServerBootTime().toJSON().val))).toLocaleString();

			var shortenedModel = {
					obixVersion : obixVersion,
					productUrl : productUrl,
					productName : productName,
					productVersion : productVersion,
					vendorName : vendorName,
					vendorUrl : vendorUrl,
					serverName : serverName,
					serverBootTime : serverBootTime
			};

			// add as well localized text
			var data = _.extend({}, shortenedModel, localizedLobbyText);
			return data;
		}

	});

	return FooterView;
});
