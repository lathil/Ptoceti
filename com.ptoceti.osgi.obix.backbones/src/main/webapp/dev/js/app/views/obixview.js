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
define([ 'backbone', 'marionette', 'underscore', 'jquery', 'eventaggr', 'modelbinder','views/landingview', 'views/progressview', 'models/progressviewmodel','marionette.handlebars', 'handlebars.helpers','bootstrap' ],
		function(Backbone, Marionette, _, $, ventAggr, ModelBinder, LandingView, ProgressView, ProgressViewModel ) {

	var ObixView = Marionette.Layout.extend({
		template : 'main',

		regions : {
			header : '#header',
			content : '#content',
			footer : '#footer'
		},
		
		initialize : function() {		
			ventAggr.on("controller:startApp", this.showProgress, this);
		},
		
		onClose : function() {
			ventAggr.off("controller:startApp",  this.showProgress, this);
		},
		
		showProgress: function() {
			
			if (!(this.header.currentView instanceof LandingView)){
				this.header.show(new LandingView({}))
			}
			this.content.show(new ProgressView({model: new ProgressViewModel()}));
		},
		
		showIntro: function(){
			var layout = this;
			require(['views/landingview','views/introview'], function(LandingView, IntroView) {
				if (!(layout.header.currentView instanceof LandingView)){
					layout.header.show(new LandingView({}))
				}
				layout.content.show(new IntroView());
			});
		},
		
		showLogin: function(){
			var layout = this;
			require(['views/landingview','views/loginview'], function(LandingView, LoginView) {
				if (!(layout.header.currentView instanceof LandingView)){
					layout.header.show(new LandingView({}))
				}
				layout.content.show(new LoginView());
			});
		},
		
		showLobby: function(about, watch){
			var layout = this;
			require(['views/lobbyview', 'views/footerview','views/headerview'], function(LobbyView, FooterView, HeaderView) {
				if (!(layout.header.currentView instanceof HeaderView)){
					layout.header.show(new HeaderView({model : about}))
				}
				if (!(layout.footer.currentView instanceof FooterView)){
					layout.footer.show(new FooterView({model : about}))
				}
				if( watch) {
					layout.content.show(new LobbyView({name: watch.getName(), displayName : watch.getDisplayName()}));
				}
			});
		},
		
		showWatches: function( about){
			var layout = this;
			require(['views/watchview', 'views/footerview','views/headerview'], function(WatchView, FooterView, HeaderView) {
				if (!(layout.header.currentView instanceof HeaderView)){
					layout.header.show(new HeaderView({model : about}))
				}
				if (!(layout.footer.currentView instanceof FooterView)){
					layout.footer.show(new FooterView({model : about}))
				}
				layout.content.show(new WatchView());
			});
		},
		
		showHistory: function(about){
			var layout = this;
			require(['views/historyview', 'views/footerview','views/headerview'], function(HistoryView, FooterView, HeaderView) {
				if (!(layout.header.currentView instanceof HeaderView)){
					layout.header.show(new HeaderView({model : about}))
				}
				if (!(layout.footer.currentView instanceof FooterView)){
					layout.footer.show(new FooterView({model : about}))
				}
				layout.content.show(new HistoryView());
			});
		}
	
	});
	
	
	return ObixView;
});
