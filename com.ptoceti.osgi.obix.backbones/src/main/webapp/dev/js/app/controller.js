/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : Ptoceti
 * PROJECT : Obix Backbones
 * FILENAME : controller.js
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
 * main application controller. Make initial call to server, load resources and initialize views.
 * 
 * 
 */
define(['backbone', 'marionette', 'underscore', 'eventaggr','views/obixview','models/obix', 'models/watcheslocal', 'models/watches'], function(
		Backbone, Marionette, _, ventAggr, ObixView, Obix, WatchesLocal, Watches) {

	var AppController = Marionette.Controller.extend({

		initialize : function(options) {
			this.restRoot = options.restRoot;
			
			this.lobby = new Obix.lobby({}, {
				urlRoot : this.restRoot
			});
			

			// load watches configuration from local storage
			WatchesLocal.fetch();
			// cerate a collection that will hold all watches
			this.watches = new Watches();
			
			this.obixView = new ObixView();
			this.rootRegion = options.rootRegion;
			this.rootRegion.show(this.obixView);
			
			ventAggr.on("watch:removePoint", this.onWatchDeletePoint, this);
			ventAggr.on("watch:updateListValues", this.onUpdateWatchListItemValues,this);
			ventAggr.on("watch:updateList", this.onUpdateWatchListItem,this);
			ventAggr.on("watchlist:updateList", this.onUpdateWatchList, this); 
			ventAggr.on("watch:removeWatch", this.onRemoveWatch, this);
			ventAggr.on("watch:createWatch", this.onCreateWatch, this);
			ventAggr.on("history:update", this.onUpdateHistory,this);

			_.bindAll(this, 'lobbyLoaded', 'aboutLoaded', 'watchServiceLoaded', 'watchCreated', 'watchUpdated', 'watchDeleted', 'handleWatchListItemValueError', 'handleWatchListItemError');
			
		},

		/**
		 * Start application by retreiving lobby from sever. Looby is main entry point and will give directions to reach About resource, Batch operation,
		 * WatchService entry point and any other additional resources we can navigate to.
		 */
		start : function() {
			ventAggr.trigger("controller:startApp");
			
			this.lobby.fetch({
				success : this.lobbyLoaded,
				error : _.bind(function(model, response){
					console.log('Error loading lobby');
				}, this)
			});
			
		},

		/**
		 * Handler called when lobby resource has been retrieved. When arriving we can assume we got connection with the server. Continue with loding succesive part
		 * of the application. Get hold of About and WatchService. Part of the UI will start to be showed from there on.
		 * 
		 * @param model
		 * @param response
		 */
		lobbyLoaded : function(model, response) {
			console.log('lobby fetched');
			ventAggr.trigger("controller:lobbyLoaded");

			// Lobby indicate uri to use to get About
			var aboutRef = this.lobby.getAbout();

			this.about = new Obix.about({
				href : aboutRef.getHref()
			}, {
				urlRoot : this.restRoot
			});
			this.about.fetch({
				// async load
				success : this.aboutLoaded
			});
			
			// idem for uri of WatchService
			var watchServiceRef = this.lobby.getWatchService();

			this.watchService = new Obix.watchService({
				href : watchServiceRef.getHref()
			}, {
				urlRoot : this.restRoot
			});
			this.watchService.fetch({
				// assync load
				success : this.watchServiceLoaded
			});
			// ..and wait for responses.
		},
		
		/**
		 * About resource handler. Called once About is loaded. Initialize view + controller ( header + footer ) and assign model.
		 * 
		 * 
		 */
		aboutLoaded : function(model, response) {
			console.log('about loaded');
			this.about = model;
			ventAggr.trigger("controller:updatedAbout", model);
		},

		/**
		 *  WatchService resource handler. Called once resource is loaded. Get hold of watch.make resource and initiate creation of a watch
		 * 
		 * 
		 */
		watchServiceLoaded : function(model, response) {
			
			console.log('watchService loaded');
			ventAggr.trigger("controller:watchServiceLoaded");
			
			// if details of watches are stored locally
			if( WatchesLocal.length > 0 ){
				_.each(WatchesLocal.models, function(element,index){
					// parse each and recreate watch
					var watch = new Obix.watch({ href: element.getHref().toJSON() }, {urlRoot : this.restRoot});
					
					watch.fetch({
						//async: false,
						async: true,
						success: _.bind(function(model, response) {
							this.watches.add(model);
							// if first watch reloaded successfully,
							if(typeof this.lobbyWatch == 'undefined' ||  this.lobbyWatch == null) {
								// .. save as lobby watch and tell app
								this.lobbyWatch = model ;
								ventAggr.trigger("controller:watchListLoaded"); 
								ventAggr.trigger("controller:rootLoaded"); 
							}
						}, this),
						error : _.bind(function(model, response){
							// remove element from localstorage
							element.destroy();
						}, this)
					}, this);
					
				},this);
				
				if( !this.lobbyWatch){
					// if none of the watches local matched, create a new one.
					ventAggr.trigger("watch:createWatch"); 
				}
				
			} else {
				// create initial lobby watch
				ventAggr.trigger("watch:createWatch"); 
			}
		},
		
		/**
		 * Lobby watch creation handler. Once the watch is created, give it references to all object of type obix:Point or obix:WritablePoint
		 * and ask the watch to monitor them.
		 * 
		 * @param model
		 * @param response
		 */
		watchCreated : function(model, response) {
			console.log('watch created');

			var watchIn = new Obix.watchIn();
			var watchedPoints = _.union(this.lobby.getChildrens().getByContract('ptoceti:MonitoredPoint'),
					this.lobby.getChildrens().getByContract('ptoceti:SwitchPoint'),
					this.lobby.getChildrens().getByContract('ptoceti:DigitPoint'),
					this.lobby.getChildrens().getByContract('ptoceti:ReferencePoint'),
					this.lobby.getChildrens().getByContract('obix:writablePoint'));
			for ( var i = 0; i < watchedPoints.length; i++) {
				watchIn.getHrefList().add(watchedPoints[i].getHref());
			}

			var watchOut  = new Obix.watchOut({}, {
				urlRoot : this.restRoot
			});
			
			model.getAddOp().invoke(watchIn, watchOut, {
				success : this.watchUpdated,
				updateValues : false
				//createUpdateTask : true,
			});
			
			// if looby not yet initialised
			if(!this.lobbyWatch || typeof this.lobbyWatch == 'undefined' ||  this.lobbyWatch == null) {
				// means it is the first watch
				this.lobbyWatch = model;
				ventAggr.trigger("controller:rootLoaded");
			}
			
			WatchesLocal.create( model);
			this.watches.add(model);
			
			ventAggr.trigger("controller:updatedWatchList", this.watches);
		},

		/**
		 * Call back looby watch with updated values
		 * 
		 * @param model
		 * @param response
		 */
		watchUpdated : function(watchout, response, options) {
			console.log('watch updated response');
			
			if( options.updateValues !== 'undefined' && options.updateValues == true) {
				ventAggr.trigger("controller:updatedWatchPointValues", watchout.getValueList().getChildrens());
			}
			else ventAggr.trigger("controller:updatedWatchPointList", watchout.getValueList().getChildrens());
			
			/**
			if( options.createUpdateTask !== 'undefined' &&  options.createUpdateTask == true) {
				this.schedulesUpdateWatch = setTimeout(_.bind(this.updateWatch, this), 5000);
			}
			**/
		},
		
		watchDeleted : function(watchout, response, option) {
			console.log('watch deleted response');
			// supress the watch from local storage
			_.each(WatchesLocal.models, function(element,index){
				if( element.getName() == option.deletedWatch.getName()) {
					element.destroy();
					this.watches.remove(option.deletedWatch);
				}
			}, this)
			
			ventAggr.trigger("controller:updatedWatchList", this.watches); 
			
		},
 
		/**
		 * Invoke the watch to get an list of element that have changed since last invocation, with theirs new values
		 * 
		 */
		onUpdateWatchListItemValues : function() {
			console.log('update watch request');
			var getPoolChangesOp = this.lobbyWatch.getPoolChangesOp().invoke(new Obix.nil(), new Obix.watchOut({}, {
				urlRoot : this.restRoot
			}), {
				success : this.watchUpdated,
				error : this.handleWatchListItemValueError,
				updateValues : true
			});
		},
		
		handleWatchListItemValueError : function() {
			ventAggr.trigger("watch:updateListValues");
		},
		
		/**
		 * Invoke the watch to get a full list of element with theirs last values
		 * 
		 */
		onUpdateWatchListItem : function() {
			console.log('update watch request');
			var getPoolChangesOp = this.lobbyWatch.getPoolRefreshOp().invoke(new Obix.nil(), new Obix.watchOut({}, {
				urlRoot : this.restRoot
			}), {
				success : this.watchUpdated,
				error : this.handleWatchListItemError,
				updateValues : false
			});
		},
		
		handleWatchListItemError : function(){
			ventAggr.trigger("watch:updateList");
		},
		
		
		/**
		 * Invoked to get updated list of all watches
		 */
		onUpdateWatchList : function() {
			ventAggr.trigger("controller:updatedWatchList", this.watches);
		}, 
		
		/**
		 * Remove a point from the list of watched points of the lobby watch.
		 * 
		 */
		onWatchDeletePoint : function( point) {
			if( this.lobbyWatch != null ) {
				
				var watchIn = new Obix.watchIn();
				watchIn.getHrefList().add(point.getHref());
				
				this.lobbyWatch.getRemoveOp().invoke(watchIn, new Obix.nil(), {
					success : _.bind(function(model, response) {
						ventAggr.trigger("watch:updateList")
					}, this),
				});
			}
		},
		
		/**
		 * Event handler for application level event type watch:removeWatch. Remove watch loccaly and on server
		 * 
		 * 
		 * @param watch the select watch to delete
		 */
		onRemoveWatch : function( watch) {
			
			watch.getDeleteOp().invoke( new Obix.nil(), new Obix.watchOut(), {
				success: this.watchDeleted,
				deletedWatch : watch
			});
		},
		
		/**
		 * Event handler for application level event type watch:createWatch. Create a new watch on server and update local list
		 * 
		 */
		onCreateWatch : function() {
			
			var makeWatchOp = this.watchService.getMakeOp(); 

			var watch  = new Obix.watch({}, {
				urlRoot : this.restRoot
			});
			makeWatchOp.invoke(new Obix.nil(), watch, {
				success : this.watchCreated
			});
		},
		
		onUpdateHistory : function() {
			ventAggr.trigger("controller:updatedHistory");
		},

		goToIntro : function(){
			this.obixView.showIntro();
		},
		
		goToLobby : function() {
			this.obixView.showLobby(this.about, this.lobbyWatch);
		},
		
		goToLobbyWithWatch : function ( watchUri ) {
			
			var selectedWatch = _.find(this.watches.models, function( element){
				if( element.getHref().getVal() == watchUri){
					return true;
				}
			}, this);
			
			if( !!selectedWatch){
				this.lobbyWatch = selectedWatch; 
				var controller = this;
				this.obixView.showLobby(this.about, this.lobbyWatch);
			}
			
		},

		goToWatches : function() {
			this.obixView.showWatches(this.about);
		},
		
		goToHistory : function() {
			this.obixView.showHistory(this.about);
		}

	});

	return AppController;
});
