/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : Ptoceti
 * PROJECT : Obix Backbones
 * FILENAME : obixview.js
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

/**
 * Root layout for the complete IHM. Serves also as placeholder to declare all ihm dependencies.
 * 
 */
define([ 'backbone', 'marionette', 'underscore', 'jquery', 'eventaggr', 'modelbinder', 'views/progressView', 'marionette.handlebars',  'handlebars.helpers','bootstrap' ],
		function(Backbone, Marionette, _, $, ventAggr, ModelBinder, ProgressView ) {

	var ObixView = Marionette.Layout.extend({
		template : 'main',

		regions : {
			header : '#header',
			content : '#content',
			footer : '#footer'
		},
		
		initialize : function() {		
			ventAggr.on("controller:startApp", this.opStartApp, this);
			ventAggr.on("controller:updatedAbout", this.onUpdatedAbout, this);
		},
		
		opStartApp: function() {
			this.content.show(new ProgressView());
		},
		
		onUpdatedAbout: function (about) {
			
			var layout = this;
			require(['views/headerview', 'views/footerview'],function(HeaderView, FooterView){
				layout.header.show(new HeaderView({
					model : about
				}));
				
				layout.footer.show(new FooterView({
					model : about
				}));
			});
			
		}
	
	});
	
	
	return ObixView;
});
