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
define(['jquery', 'backbone', 'marionette', 'underscore', 'eventaggr', 'oauth2', 'views/obixview','models/obix', 'models/watcheslocal', 'models/watches', 'models/histories', 'models/historyrollupoutlocal','models/compositehistory','models/alarms', 'models/compositealarm', 'jscookie'], function(
		$, Backbone, Marionette, _, ventAggr, oauth2, ObixView, Obix, WatchesLocal, Watches, Histories, HistoryRollupOutLocal, CompositeHistory, Alarms, CompositeAlarm, Cookie ) {

	var AppController = Marionette.Controller.extend({

		initialize : function(options) {
			this.restRoot = options.restRoot;
			
			this.lobby = new Obix.lobby({}, {
				urlRoot : this.restRoot
			});
			
			window.location.protocol + '//' +  window.location.hostname + ':8080/obix/rest/';
			
			var clientid = Cookie.get('clientid');
			oauth2.clientId = clientid;
			oauth2.clientSecret = '';
			
			// load watches configuration from local storage
			WatchesLocal.fetch();
			// create a collection that will hold all watches
			this.watches = new Watches();
			// create a collection that will hold all histories
			this.histories = new Histories();
			// load previously saves history rollups
			HistoryRollupOutLocal.fetch();
			// create a collection that will hold all alarms
			this.alarms = new Alarms();
			
			this.obixView = new ObixView();
			this.rootRegion = options.rootRegion;
			this.rootRegion.show(this.obixView);
			
			ventAggr.on("watch:removePoint", this.onDeleteItemFromWatch, this);
			ventAggr.on("watch:updateListValues", this.onUpdateWatchListItemValues,this);
			ventAggr.on("watch:updateList", this.onUpdateWatchListItem,this);
			ventAggr.on("watchlist:updateList", this.onUpdateWatchList, this); 
			ventAggr.on("watch:removeWatch", this.onRemoveWatch, this);
			ventAggr.on("watch:createWatch", this.onCreateWatch, this);
			ventAggr.on("history:createHistory", this.onCreateHistory,this);
			ventAggr.on("history:removeHistory", this.onDeleteHistory,this);
			
			ventAggr.on("history:update", this.onUpdateHistory,this);
			ventAggr.on("history:showHistory", this.onShowHistory,this);
			ventAggr.on("controller:loadHistory", this.onLoadHistory, this);
			ventAggr.on("controller:loadAlarm", this.onLoadAlarm, this);
			ventAggr.on("controller:loadApp", this.loadApp, this);
			ventAggr.on("controller:doSearch", this.onDoSearch, this);
			ventAggr.on("controller:doAdditemToWatch", this.onAddItemToWatch, this);
			
			ventAggr.on("alarm:createAlarm", this.onCreateAlarm,this);
			
			ventAggr.on("oauth2:access", this.oauth2Access, this);
			ventAggr.on("oauth2:error", this.oauth2Error, this);
			ventAggr.on("controler:doLogin", this.doLogin, this);

			_.bindAll(this, 'lobbyLoaded', 'aboutLoaded', 'watchServiceLoaded', 'watchCreated', 'watchUpdated', 'watchDeleted', 'historyLoaded', 'historyDeleted', 'historyCreated', 'alarmLoaded', 'searchDone');
			
		},

		/**
		 * Start application by retreiving lobby from sever. Looby is main entry point and will give directions to reach About resource, Batch operation,
		 * WatchService entry point and any other additional resources we can navigate to.
		 */
		start : function() {
			ventAggr.trigger("controller:startApp");
			
			if(oauth2.isAuthenticated() || !oauth2.clientId){
				// if already authenticated
				ventAggr.trigger("controller:loadApp");
			} else {
				// else authenticate
				ventAggr.trigger("app:goToLogin");
				
			}	
			
		},
		
		doLogin : function(event){
			oauth2.clear();
			oauth2.access(event.name, event.password);
		},
		
		/**
		 * oauth access to get a token ok
		 * 
		 */
		oauth2Access : function(){
			ventAggr.trigger("controller:loadApp");
		},
		/**
		 * error occured while trying to get toauth2 token
		 */
		oauth2Error : function(response){
			window.location.reload();
		},
		
		loadApp : function(){
			this.lobby.fetch({
				headers: oauth2.getAuthorizationHeader(),
				success : this.lobbyLoaded,
				error : _.bind(function(model, response, option){
					if (response == 'error' && (( model.status == 403 ) || (model.status == 401))) { // Not authorized
						ventAggr.trigger("oauth2:error");
		            }
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
				headers: oauth2.getAuthorizationHeader(),
				// async load
				success : this.aboutLoaded,
				error : _.bind(function(model, response, option){
					if (response == 'error' && (( model.status == 403 ) || (model.status == 401))) { // Not authorized
						ventAggr.trigger("oauth2:error");
		            }
				}, this)
			});
			
			// idem for uri of WatchService
			var watchServiceRef = this.lobby.getWatchService();

			this.watchService = new Obix.watchService({
				href : watchServiceRef.getHref()
			}, {
				urlRoot : this.restRoot
			});
			this.watchService.fetch({
				headers: oauth2.getAuthorizationHeader(),
				// assync load
				success : this.watchServiceLoaded,
				error : _.bind(function(model, response, option){
					if (response == 'error' && (( model.status == 403 ) || (model.status == 401))) { // Not authorized
						ventAggr.trigger("oauth2:error");
		            }
				}, this)
			});
			
			// idem for uri of HistoryService
			var historyServiceRef = this.lobby.getHistoryService();

			this.historyService = new Obix.historyService({
				href : historyServiceRef.getHref()
			}, {
				urlRoot : this.restRoot
			});
			
			this.historyService.fetch({
				headers: oauth2.getAuthorizationHeader(),
				// assync load
				success : this.historyServiceLoaded,
				error : _.bind(function(model, response, option){
					if (response == 'error' && (( model.status == 403 ) || (model.status == 401))) { // Not authorized
						ventAggr.trigger("oauth2:error");
		            }
				}, this)
			});
			
			// idem for alarmService
			var alarmServiceRef = this.lobby.getAlarmService();
			
			this.alarmService = new Obix.alarmService({
				href: alarmServiceRef.getHref()
			}, {
				urlRoot : this.restRoot
			});
			
			this.alarmService.fetch({
				headers : oauth2.getAuthorizationHeader(),
				//assync load
				success : this.alarmServiceLoaded,
				error : _.bind(function(model,response, option){
					if (response == 'error' && (( model.status == 403 ) || (model.status == 401))) { // Not authorized
						ventAggr.trigger("oauth2:error");
		            }					
				},this)
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
		 *  HistoryService resource handler. Called once resource is loaded. Get hold of history.make resource 
		 * 
		 * 
		 */
		historyServiceLoaded : function(model, response) {
			
		},
		
		/**
		 *  AlarmService resource handler. Called once resource is loaded. Get hold of alarm.make resource 
		 * 
		 * 
		 */
		alarmServiceLoaded : function(model, response) {
			
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
				var deferreds = _.map(WatchesLocal.models, function(element,index){
					// parse each and recreate watch
					var watch = new Obix.watch({ href: element.getHref().toJSON() }, {urlRoot : this.restRoot});
					
					return watch.fetch({
						async: true,
						headers: oauth2.getAuthorizationHeader(),
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
							if (response == 'error' && (( model.status == 403 ) || (model.status == 401))) { // Not authorized
								ventAggr.trigger("oauth2:error");
				            }
							// remove element from localstorage
							element.destroy();
						}, this)
					}, this);
					
				},this);
				
				$.when.apply($,deferreds).then().always( _.bind(function(){
						if( !this.lobbyWatch){
							// if none of the watches local matched, create a new one.
							ventAggr.trigger("watch:createWatch"); 
						}
				},this));
				
				
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
		 * Add an item to the current lobby watch
		 */
		onAddItemToWatch : function( href ){
			
			var watchIn = new Obix.watchIn();
			watchIn.getHrefList().add(href.getHref());
			
			var watchOut  = new Obix.watchOut({}, {
				urlRoot : this.restRoot
			});
			
			this.lobbyWatch.getAddOp().invoke(watchIn, watchOut, {
				headers: oauth2.getAuthorizationHeader(),
				success : _.bind(function(watchOut, response, options){
					this.watchUpdated(watchOut, response, options);
					// add uri to watch list so that it won't be re-displayed
					this.lobbyWatch.getChildrens().add(href.getHref());
					
				},this),
				updateValues : false,
				error : _.bind(function(model, response, option){
					if (response == 'error' && (( model.status == 403 ) || (model.status == 401))) { // Not authorized
						ventAggr.trigger("oauth2:error");
		            }
				}, this)
			});
		},
		
		/**
		 * Remove a point from the list of watched points of the lobby watch.
		 * 
		 */
		onDeleteItemFromWatch : function( item) {
			if( this.lobbyWatch != null ) {
				
				var watchIn = new Obix.watchIn();
				watchIn.getHrefList().add(item.getHref());
				
				this.lobbyWatch.getRemoveOp().invoke(watchIn, new Obix.nil({}, {
					urlRoot : this.restRoot
				}), {
					headers: oauth2.getAuthorizationHeader(),
					success : _.bind(function(model, response) {
						// removed item from local watch so that it may be redisplayed in search list
						var removedItem = _.find(this.lobbyWatch.getChildrens().models, function( nextItem){
							if( nextItem.get('type') == "uri" && nextItem.getVal() == item.getHref().getVal()){
								return true;
							}
						}, this);
						if( removedItem){
							this.lobbyWatch.getChildrens().remove(removedItem);
						}
						
						ventAggr.trigger("watch:updateList")
					}, this),
					error : _.bind(function(model, response, option){
						if (response == 'error' && (( model.status == 403 ) || (model.status == 401))) { // Not authorized
							ventAggr.trigger("oauth2:error");
			            }
					}, this)
				});
			}
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
				ventAggr.trigger("controller:updatedWatchItemsValues", watchout.getValueList().getChildrens());
			}
			else ventAggr.trigger("controller:updatedWatchItemsList", watchout.getValueList().getChildrens());

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
				headers: oauth2.getAuthorizationHeader(),
				success : this.watchUpdated,
				error : _.bind(function(model, response, option){
					if (response == 'error' && (( model.status == 403 ) || (model.status == 401))) { // Not authorized
						ventAggr.trigger("oauth2:error");
		            }
				}, this),
				updateValues : true
			});
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
				headers: oauth2.getAuthorizationHeader(),
				success : this.watchUpdated,
				error : _.bind(function(model, response, option){
					if (response == 'error' && (( model.status == 403 ) || (model.status == 401))) { // Not authorized
						ventAggr.trigger("oauth2:error");
		            }
				}, this),
				updateValues : false
			});
		},
	
		
		
		/**
		 * Invoked to get updated list of all watches
		 */
		onUpdateWatchList : function() {
			ventAggr.trigger("controller:updatedWatchList", this.watches);
		}, 
		
		
		
		/**
		 * Event handler for application level event type watch:removeWatch. Remove watch loccaly and on server
		 * 
		 * 
		 * @param watch the select watch to delete
		 */
		onRemoveWatch : function( watch) {
			
			watch.getDeleteOp().invoke( new Obix.nil(), new Obix.watchOut({}, {
				urlRoot : this.restRoot
			}), {
				headers: oauth2.getAuthorizationHeader(),
				success: this.watchDeleted,
				error : _.bind(function(model, response, option){
					if (response == 'error' && (( model.status == 403 ) || (model.status == 401))) { // Not authorized
						ventAggr.trigger("oauth2:error");
		            }
				}, this),
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
				headers: oauth2.getAuthorizationHeader(),
				success : this.watchCreated,
				error : _.bind(function(model, response, option){
					if (response == 'error' && (( model.status == 403 ) || (model.status == 401))) { // Not authorized
						ventAggr.trigger("oauth2:error");
		            }
				}, this)
			});
		},
		
		/**
		 * Load an History object from rest server. The uri of the resource is passed on as parameter
		 * 
		 */
		onLoadHistory : function(historyRef) {
			
			var history;
			if( !!historyRef && this.histories.length > 0 ){
				history = this.histories.find(function(element){
					if( element.getHistory()){
						return element.getHistory().getHref().getVal() == historyRef;
					} else {
						return false;
					}
				})
			}
			
			if( !history && historyRef) {
				var historyHref = new Obix.uri();
				historyHref.setVal(historyRef);
				var history = new Obix.history({href : historyHref}, {urlRoot : this.restRoot});
				history.fetch({
					headers: oauth2.getAuthorizationHeader(),
					success: this.historyLoaded
				},this);
			} else {
				ventAggr.trigger("controller:updatedHistoryList", this.histories);
			}
		},
		
		historyLoaded : function(history, response) {
			console.log('history loaded response');
			var compositeHistory = new CompositeHistory();
			compositeHistory.set({id: history.getHref().getVal()})
			compositeHistory.setHistory(history)
			this.histories.add(compositeHistory);
			ventAggr.trigger("controller:updatedHistoryList", this.histories);
		},
		
		/**
		 * Delete an history on rest server. The uri of the resource is passed on as parameter
		 * 
		 */
		onDeleteHistory : function(history) {
			
			var compositeHistory;
			if( !!history && this.histories.length > 0 ){
				compositeHistory = this.histories.find(function(element){
					if( element.getHistory()){
						return element.getHistory().getHref().getVal() == history.getHref().getVal();
					} else {
						return false;
					}
				},this)
			}
			
			if( history && compositeHistory) {
				compositeHistory.getHistory().set('id',compositeHistory.getHistory().getHref().getVal() );
				compositeHistory.getHistory().destroy({
					headers: oauth2.getAuthorizationHeader(),
					success: this.historyDeleted,
					wait: true,
					compositeHistory: compositeHistory
				},this)
			}
		},
		
		historyDeleted : function(history, response, options){
			this.histories.remove(options.compositeHistory)
			console.log('history deleted response');
			ventAggr.trigger("controller:updatedHistoryList", this.histories);
		},
		
		/**
		 * Send a request to the rest server to create a history object for a point.
		 * The point is passed on as parameter
		 * 
		 */
		onCreateHistory : function(point) {
			
			var ref = new Obix.ref();
			ref.setHref(point.getHref())
			
			var history  = new Obix.history({}, {
				urlRoot : this.restRoot
			});
			
			this.historyService.getMakeOp().invoke(ref, history, {
				headers: oauth2.getAuthorizationHeader(),
				success : this.historyCreated,
				error : _.bind(function(model, response, option){
					if (response == 'error' && (( model.status == 403 ) || (model.status == 401))) { // Not authorized
						ventAggr.trigger("oauth2:error");
		            }
				}, this)
			});
			
		},
		
		historyCreated : function(){
			ventAggr.trigger("watch:updateList");
		},
		
		onUpdateHistory : function() {
			ventAggr.trigger("controller:updatedHistory");
		},
		
		
		onShowHistory : function(historyRef){
			this.goToHistoriesWithHistory(historyRef);
		},
		
		/**
		 * Load an Alarm object from rest server. The uri of the resource is passed on as parameter
		 * 
		 */
		onLoadAlarm : function(alarmRef) {
			
			var alarm;
			if( !!alarmRef && this.alarms.length > 0 ){
				alarm = this.alarms.find(function(element){
					if( element.getAlarm()){
						return element.getAlarm().getHref().getVal() == alarmRef;
					} else {
						return false;
					}
				})
			}
			
			if( !alarm && alarmRef) {
				var alarmHref = new Obix.uri();
				alarmHref.setVal(alarmRef);
				var alarm = new Obix.alarm({href : alarmHref}, {urlRoot : this.restRoot});
				alarm.fetch({
					headers: oauth2.getAuthorizationHeader(),
					success: this.alarmLoaded
				},this);
			} else {
				ventAggr.trigger("controller:updatedAlarmList", this.alarms);
			}
		},
		
		alarmLoaded : function(alarm, response) {
			console.log('alarm loaded response');
			var compositeAlarm = new CompositeAlarm();
			compositeAlarm.set({id: alarm.getHref().getVal()})
			compositeAlarm.setAlarm(alarm)
			this.alarms.add(compositeAlarm);
			ventAggr.trigger("controller:updatedAlarmList", this.alarms);
		},
		
		/**
		 * Send a request to the rest server to create a alarm object for a point.
		 * The point is passed on as parameter
		 * 
		 */
		onCreateAlarm : function(point) {
			
			var ref = new Obix.ref();
			ref.setHref(point.getHref())
			
			var alarm  = new Obix.alarm({}, {
				urlRoot : this.restRoot
			});
			
			this.alarmService.getMakeOp().invoke(ref, alarm, {
				headers: oauth2.getAuthorizationHeader(),
				success : this.alarmCreated,
				error : _.bind(function(model, response, option){
					if (response == 'error' && (( model.status == 403 ) || (model.status == 401))) { // Not authorized
						ventAggr.trigger("oauth2:error");
		            }
				}, this)
			});
			
		},
		
		alarmCreated : function(){
			ventAggr.trigger("watch:updateList");
		},
		
		
		/**
		 * execute a search on server for a list of items
		 */
		onDoSearch : function(searchValue) {
			
			var ref = new Obix.ref();
			ref.setDisplayName(searchValue);
			
			var searchOut  = new Obix.searchOut({}, {
				urlRoot : this.restRoot
			});
			
			this.lobby.getSearchOp().invoke(ref, searchOut, {
				headers: oauth2.getAuthorizationHeader(),
				success : this.searchDone,
				updateValues : false,
				error : _.bind(function(model, response, option){
					if (response == 'error' && (( model.status == 403 ) || (model.status == 401))) { // Not authorized
						ventAggr.trigger("oauth2:error");
		            }
				}, this)
			});
			
		},
		
		/**
		 * Publish event when search is done with results
		 */
		searchDone : function(searchout, response, options) {
			console.log('search done');
			
			var filteredResults = new Backbone.Collection(null,{model: Obix.ref});
			
			_.each(searchout.getValueList().getChildrens().models, function(item){
				
				var found = false;
				var searchitem = item
				
				_.each(this.lobbyWatch.getChildrens().models, function(watchitem){
					if( watchitem.get('type') == "uri" && watchitem.get('val') && watchitem.get('val') == searchitem.get('href').get('val')){
						found = true;
					}
					
				}, this)
				
				if( !found){
					filteredResults.push(searchitem);
				}
				
			}, this)
			
			ventAggr.trigger("controller:searchResultValues", filteredResults);
		},

		goToIntro : function(){
			this.obixView.showIntro();
		},
		
		goToLogin : function(){
			this.obixView.showLogin();
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
		
		goToHistories : function() {
			this.obixView.showHistory(this.about);
		},
		
		goToHistoriesWithHistory : function(historyUri){
			this.obixView.showHistory(this.about, historyUri);
		},
		
		goToAlarms : function() {
			this.obixView.showAlarms(this.about);
		},
		
		goToAlarmsWithAlarm : function(alarmUri){
			this.obixView.showAlarms(this.about, alarmUri);
		},
		
		goToAddItemToWatch : function(){
			this.obixView.showSearch(this.about);
		}

	});
	

	return AppController;
});
