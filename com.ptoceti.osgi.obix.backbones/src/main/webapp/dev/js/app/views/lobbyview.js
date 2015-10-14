/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : Ptoceti
 * PROJECT : Obix Backbones
 * FILENAME : lobbyview.js
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
define([ 'backbone', 'marionette', 'underscore', 'jquery', 'eventaggr', 'mediaenquire','modelbinder', 'courier', 'models/lobbyviewmodel', "i18n!nls/lobbytext", 'bootstrap' ],
		function(Backbone, Marionette, _, $, ventAggr, mediaEnquire, ModelBinder, Courier, LobbyViewModel, localizedLobbyText ) {

	var LobbyView = Marionette.Layout.extend({
		template : 'lobby',  

		ui : {
			reloadButton : "#reloadButton",
			removeButton : "#removeButton"
		},

		events : {
			"click #reloadButton" : "reloadWatch",
			"click #removeButton" : "removeFromWatch"
		},
		
		regions: {
			pointsListRegion : "#pointsList"
		},

		initialize : function(options) {
			
			// add this view to Backbone.Courier
			Courier.add(this);
			// initialize Backbone.ModelBinder for dual binding
			this.modelbinder = new ModelBinder();
			
			this.model = new LobbyViewModel();
			this.model.set("name", options.name);
			this.model.set("displayName", options.displayName);
			
			ventAggr.on("controller:updatedWatchPointList", this.onUpdatedPointsList, this);
			ventAggr.on("controller:updatedWatchPointValues", this.onUpdatedPointsValues, this);
			

			_.bindAll(this, 'enterXs','quitXs');

			this.onXsMedia = false;
			this.xsQueryHandler = {match : this.enterXs, unmatched: this.quitXs};
			mediaEnquire.registerXs(this.xsQueryHandler);
			
		},
		
		enterXs : function(){
			this.onXsMedia = true;
		},
		
		quitXs : function(){
			this.onXsMedia = false;
		},
		
		templateHelpers : function() {
			return {
				lobbytext : this.onXsMedia ? localizedLobbyText.lobbytextxs : localizedLobbyText.lobbytext,
				onXsMedia : this.onXsMedia
			};
		},
		
		/**
		 * Do extra cleaning here on view closing. Marionnette manage already most of it.
		 */
		onClose : function() {
			this.modelbinder.unbind();
			ventAggr.off("controller:updatedWatchPointList", this.onUpdatedPointsList, this);
			ventAggr.off("controller:updatedWatchPointValues", this.onUpdatedPointsValues, this);
			mediaEnquire.unregisterXs(this.xsQueryHandler);
		},
		
		// listener for view events coming from a subview
		onMessages : {
	        "listItemSelected" : "onItemSelected",
	        "itemDelete" : "onItemDelete"	
	    },
		
	    /**
	     * Called after the view has been rendered. Trigger a event to receive a updated list of values.
	     * 
	     */    
	    onRender : function() {
	    	ventAggr.trigger("watch:updateList");
	    	this.modelbinder.bind(this.model, this.el, {
	    		name: {selector: '[name=displayName]', converter: this.nameConverter},
				count: {selector: '[name=count]'},
			});
	    },
	    
	    nameConverter : function(direction, value, attributeName, model){
			if( direction =='ModelToView') {
				if(!model.get('displayName')) return value;
				else return model.get('displayName');
			}
		},
		
		onItemSelected : function( message ) {
			
			if( message.data.point != null) {
				this.pointSelected = message.data.point;
				this.ui.removeButton.removeAttr("disabled");
			} else  {
				this.pointSelected = null;
				this.ui.removeButton.attr("disabled", "disabled");
			}
		},
		
		onItemDelete : function( message ){
			ventAggr.trigger("watch:removePoint", message.data.point);
		},
		
		onUpdatedPointsValues: function(updatedCollection) {
			var region = this.pointsListRegion;
			
			_.each(updatedCollection.models, function(element,index) {
				element.set({updateTimeStamp: new Date()});
			});
			
			this.model.set("count", updatedCollection.length);
			
			var requiredModules = ['views/paginationview'];
			_.each(updatedCollection.models, function(element,index) {
				var nextView;
				if( element.hasContract('obix:Point')) {
					nextView = 'views/pointitemview';
				}
				if( element.hasContract('ptoceti:MonitoredPoint')) {
					nextView = 'views/monitoreditemview';
				}
				if( element.hasContract('ptoceti:ReferencePoint')) {
					nextView = 'views/referenceitemview';
				}
				if( element.hasContract('ptoceti:DigitPoint')) {
					nextView = 'views/stateitemview';
				}
				if( element.hasContract('ptoceti:SwitchPoint')) {
					nextView = 'views/switchitemview';
				}
				
				if( requiredModules.indexOf(nextView) < 0){
					requiredModules.push(nextView);
				}
			});
			
			require(requiredModules, function(PaginView){
				if( region.currentView == null){
					region.show(new PaginView({template:"pagination", collection: updatedCollection, context: 'lobby'}));
				} else {
					region.currentView.updateItemValues(updatedCollection);
				}
				
				// ensure we do not have another timeout already set
				if( this.schedulesUpdateWatch != null ) clearTimeout(this.schedulesUpdateWatch);
				// triger event in futur to get new updated list
				this.schedulesUpdateWatch = setTimeout(function(){
					ventAggr.trigger("watch:updateListValues");
				}, 30000);
			});
		},
		
		onUpdatedPointsList: function(updatedCollection) {
			var region = this.pointsListRegion;
			
			_.each(updatedCollection.models, function(element,index) {
				element.set({updateTimeStamp: new Date()});
			});
			
			this.model.set("count", updatedCollection.length);
			
			var requiredModules = ['views/paginationview'];
			_.each(updatedCollection.models, function(element,index) {
				var nextView;
				if( element.hasContract('obix:Point')) {
					nextView = 'views/pointitemview';
				}
				if( element.hasContract('ptoceti:MonitoredPoint')) {
					nextView = 'views/monitoreditemview';
				}
				if( element.hasContract('ptoceti:ReferencePoint')) {
					nextView = 'views/referenceitemview';
				}
				if( element.hasContract('ptoceti:DigitPoint')) {
					nextView = 'views/stateitemview';
				}
				if( element.hasContract('ptoceti:SwitchPoint')) {
					nextView = 'views/switchitemview';
				}
				
				if( requiredModules.indexOf(nextView) < 0){
					requiredModules.push(nextView);
				}
			});
			
			require(requiredModules, function(PaginView){
				if( region.currentView == null){
					region.show(new PaginView({template:"pagination", collection: updatedCollection, context: 'lobby'}));
				} else {
					region.currentView.updateItemList(updatedCollection);
				}
				
				// ensure we do not have another timeout already set
				if( this.schedulesUpdateWatch != null ) clearTimeout(this.schedulesUpdateWatch);
				// triger event in futur to get new updated list
				this.schedulesUpdateWatch = setTimeout(function(){
					ventAggr.trigger("watch:updateListValues");
				}, 30000);
			});
		},
		
		/**
		 * Binded to refresh watch button.Send event to mediator to refresh watch values
		 */
		reloadWatch : function() {
			ventAggr.trigger("watch:updateList");
		},

		/**
		 * Binded to delete watch button. Send event to mediator to remove watch
		 */
		removeFromWatch : function() {
			ventAggr.trigger("watch:removePoint", this.pointSelected);
		}
		
	});

	return LobbyView;
});
