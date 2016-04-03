/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : Ptoceti
 * PROJECT : Obix Backbones
 * FILENAME : historyview.js
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
define([ 'backbone', 'marionette', 'underscore', 'jquery', 'eventaggr', 'mediaenquire', 'modelbinder', 'courier', 'models/historyviewmodel','views/historyitemview', "i18n!nls/historytext", 'bootstrap' ],
		function(Backbone, Marionette, _, $, ventAggr, mediaEnquire, ModelBinder, Courier, HistoryViewModel, HistoryItemView, localizedHistoryText ) {
	
	var HistoryView = Marionette.Layout.extend({
		template : 'history',
		
		ui : {
			reloadButton : "#reloadButton",
			removeButton : "#removeButton",
		},
		
		events : {
			"click #reloadButton" : "reloadList",
			"click #removeButton" : "removeFromList",
		},
		
		regions: {
			historyListRegion : "#historyList"
		},
		
		initialize : function(options) {
			
			// add this view to Backbone.Courier
			Courier.add(this);
			// initialize Backbone.ModelBinder for dual binding
			this.modelbinder = new ModelBinder();
			
			this.model = new HistoryViewModel();
			this.model.set("historyUriToLoad", options.historyUri);
			
			ventAggr.on("controller:updatedHistoryList", this.onUpdatedHistoryList, this);
			
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
				historytext : this.onXsMedia ? localizedHistoryText.historytextxs : localizedHistoryText.historytext,
				onXsMedia : this.onXsMedia
			};
		},
		
		/**
		 * Do extra cleaning here on view closing. Marionnette manage already most of it.
		 */
		onClose : function() {
			this.modelbinder.unbind();
			ventAggr.off("controller:updatedHistoryList", this.onUpdatedHistoryList, this);
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
	    	
	    	ventAggr.trigger("controller:loadHistory", this.model.get('historyUriToLoad'));
	    
	    },
		
	 // listener for view events coming from a subview
		onMessages : {
			"historyItemDelete" : "onHistoryDelete",
	        "historyItemSelected" : "onHistorySelected"
	    },
		
		onHistorySelected : function( message ) {
			
			if( message.data.history != null) {
				this.historySelected = message.data.history;
				this.ui.removeButton.removeAttr("disabled");
			} else  {
				this.historySelected = null;
				this.ui.removeButton.attr("disabled", "disabled");
			}
		},
		
		
		onUpdatedHistoryList: function(updatedCollection) {
			var region = this.historyListRegion;
			this.model.set("count", updatedCollection.length);
			var modelCid = this.model.get("historyUriToLoad");
			require(['views/paginationview','views/historyitemview'], function(PaginView){
				if( region.currentView == null){
					region.show(new PaginView({template: "pagination", itemView: HistoryItemView,  collection: updatedCollection, context: 'history', modelToOpenCid : modelCid}));
				} else {
					region.currentView.updateItemList(updatedCollection);
				}
			});
		},
		
		reloadList : function() {
			
		},
		
		removeFromList : function() {
			ventAggr.trigger("history:removeHistory", this.historySelected);
		},
		
		onHistoryDelete : function( message ){
			ventAggr.trigger("history:removeHistory", message.data.history);
		}
		
	});
		
	
	return HistoryView;
});
