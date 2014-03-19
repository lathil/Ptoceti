/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : Ptoceti
 * PROJECT : Obix Backbones
 * FILENAME : watchview.js
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
define([ 'backbone', 'marionette', 'underscore', 'jquery', 'eventaggr', 'mediaenquire', 'modelbinder', 'courier', 'models/watchviewmodel','views/watchitemview', "i18n!nls/watchtext", 'bootstrap' ],
		function(Backbone, Marionette, _, $, ventAggr, mediaEnquire, ModelBinder, Courier, WatchViewModel, WatchItemView, localizedWatchText ) {
	
	var WatchView = Marionette.Layout.extend({
		template : 'watches',  

		ui : {
			reloadButton : "#reloadButton",
			removeButton : "#removeButton",
			createButton : "#createButton",
		},

		events : {
			"click #reloadButton" : "reloadList",
			"click #removeButton" : "removeFromList",
			"click #createButton" : "createWatch",
		},
		
		regions: {
			watchListRegion : "#watchList"
		},

		initialize : function() {
			
			// add this view to Backbone.Courier
			Courier.add(this);
			// initialize Backbone.ModelBinder for dual binding
			this.modelbinder = new ModelBinder();
			
			this.model = new WatchViewModel();
			
			ventAggr.on("controller:updatedWatchList", this.onUpdatedWatchesList, this);
			ventAggr.on("controller:updatedWatchValues", this.onUpdatedWatchesValues, this);
			
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
				watchtext : this.onXsMedia ? localizedWatchText.watchtextxs : localizedWatchText.watchtext,
				onXsMedia : this.onXsMedia
			};
		},
		
		/**
		 * Do extra cleaning here on view closing. Marionnette manage already most of it.
		 */
		onClose : function() {
			this.modelbinder.unbind();
			ventAggr.off("controller:updatedWatchList", this.onUpdatedWatchesList, this);
			ventAggr.off("controller:updatedWatchValues", this.onUpdatedWatchesValues, this);
			mediaEnquire.unregisterXs(this.xsQueryHandler);
		},
		
		 /**
	     * Called after the view has been rendered. Trigger a event to receive a updated list of values.
	     * 
	     */    
	    onRender : function() {
	    	this.modelbinder.bind(this.model, this.el, {
				count: {selector: '[name=count]'},
			});
	    	ventAggr.trigger("watchlist:updateList");
	    },
	    
		// listener for view events coming from a subview
		onMessages : {
	        "watchItemSelected" : "onWatchSelected"
	    },
		
		onWatchSelected : function( message ) {
			
			if( message.data.watch != null) {
				this.watchSelected = message.data.watch;
				this.ui.removeButton.removeAttr("disabled");
			} else  {
				this.watchSelected = null;
				this.ui.removeButton.attr("disabled", "disabled");
			}
		},
		
		onUpdatedWatchesValues: function(updatedCollection) {
			var region = this.watchListRegion;
			this.model.set("count", updatedCollection.length);
			require(['views/paginationview','views/watchitemview' ], function(PaginView){
				if( region.currentView == null){
					region.show(new PaginView({template: "pagination", itemView: WatchItemView, collection: updatedCollection, context: 'watch'}));
				} else {
					region.currentView.updateItemValues(updatedCollection);
				}
			}); 
		},
		
		onUpdatedWatchesList: function(updatedCollection) {
			var region = this.watchListRegion;
			this.model.set("count", updatedCollection.length);
			require(['views/paginationview','views/watchitemview'], function(PaginView){
				if( region.currentView == null){
					region.show(new PaginView({template: "pagination", itemView: WatchItemView,  collection: updatedCollection, context: 'watch'}));
				} else {
					region.currentView.updateItemList(updatedCollection);
				}
			});
		},
		
		reloadList : function() {
			
		},

		removeFromList : function() {
			ventAggr.trigger("watch:removeWatch", this.watchSelected);
		},
		
		createWatch : function() {
			ventAggr.trigger("watch:createWatch");
		},
		
	});

	return WatchView;
	
});
