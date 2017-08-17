/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : Ptoceti
 * PROJECT : Obix Backbones
 * FILENAME : progressview.js
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
define([ 'backbone', 'marionette', 'underscore', 'jquery', 'eventaggr', 'modelbinder', "i18n!nls/progresstext" ], function(Backbone, Marionette, _, $, ventAggr, ModelBinder, progressText) {
	
	var ProgressView = Backbone.Marionette.ItemView.extend({
		template: 'progress',
		tagName:"div",
		
		initialize : function() {
			
			ventAggr.on("controller:lobbyLoaded", this.onLobbyLoaded, this);
			ventAggr.on("controller:updatedAbout", this.onUpdatedAbout, this);
			ventAggr.on("controller:watchServiceLoaded", this.onWatchServiceLoaded, this);
			ventAggr.on("controller:watchListLoaded", this.onWatchListLoaded, this);
			
			
			// initialize Backbone.ModelBinder for dual binding
			this.modelbinder = new ModelBinder();
		},
		
		// event handler called after the view has been closed
		onClose : function() {
			ventAggr.off("controller:lobbyLoaded", this.onLobbyLoaded, this);
			ventAggr.off("controller:updatedAbout", this.onUpdatedAbout, this);
			ventAggr.off("controller:watchServiceLoaded", this.onWatchServiceLoaded, this);
			ventAggr.off("controller:watchListLoaded", this.onWatchListLoaded, this);
			
			this.off('itemUnselected', this.itemUnselected, this);
			this.modelbinder.unbind();
		},
		
		// event handler call after the view has been rendered
		onRender : function(){
			this.modelbinder.bind(this.model, this.el, {
				//progress: {selector: '[name=progressBar]', elAttribute:'style', converter: this.progressConverter},
				progressMessage: {selector: '[name=progressMessage]', converter: this.messageConverter}
			});
		},
		
		onLobbyLoaded : function(){
			this.model.set('progressMessage', 'lobbyloaded');
			//this.model.set('progress', 40);
		},
		
		onUpdatedAbout : function(){
			this.model.set('progressMessage', 'aboutloaded');
			//this.model.set('progress', 50);
		},
		
		onWatchServiceLoaded : function(){
			this.model.set('progressMessage', 'watchserviceloaded');
			//this.model.set('progress', 60);
		},
		
		onWatchListLoaded : function(){
			this.model.set('progressMessage', 'watchlistloaded');
			//this.model.set('progress', 80);
		},
		
		progressConverter : function( direction, value, attributeName, model) {
			if( direction == "ModelToView") {
				if( value != null) {
					return 'width: ' + model.get('progress') + '%;';
				}
			}
		},
		
		messageConverter : function(direction, value, attributeName, model ) {
			if( direction == "ModelToView") {
				var message = model.get('progressMessage');
				if( message != null)
					return progressText[message];
				
			}
		}
		
	});
	
	return ProgressView;
});
