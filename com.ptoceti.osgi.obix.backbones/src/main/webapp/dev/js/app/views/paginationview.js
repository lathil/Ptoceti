/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : Ptoceti
 * PROJECT : Obix Backbones
 * FILENAME : paginationview.js
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
define([ 'backbone', 'marionette', 'underscore', 'jquery', 'courier', 'mediaenquire', 'models/pageableobjs', "i18n!nls/paginationtext"],
		function(Backbone, Marionette, _, $, Courier, mediaEnquire, PageableObj, localizedPaginationText) {
	
	var PaginView = Marionette.CompositeView.extend({
		template:"pagination",
		//itemView: PointItemView,
		itemViewContainer: "tbody",
		
		tagName:"div",
		className: "col-md-12",
		
		ui : {
			nextPageButton : "#nextPage",
			previousPageButton : "#previousPage",
		},
		
		events : {
			"click #nextPage" : "goToNextPage",
			"click #previousPage" : "goToPreviousPage",
			"click [id|='pagenav']" : "goToPage",
			"scroll #paginationTable" : "onScroll"
		},
		
		getItemView: function(item){
			
			if( item.hasContract('obix:Watch')) {
				return require('views/watchitemview');
			}
			if( item.hasContract('obix:Point')) {
				return require('views/pointitemview');
			}
			if( item.hasContract('ptoceti:MonitoredPoint')) {
				return require('views/monitoreditemview');
			}
		},
		
		// pass all messages from subview to parent view
		passMessages : {
			"*" : '.'
		},
		
		templateHelpers : function() {
			return {
				hasNext: this.collection.hasNext(),
				hasPrevious : this.collection.hasPrevious(),
				totalPages : this.collection.state.totalPages,
				currentPage : this.collection.state.currentPage,
				paginationtext : localizedPaginationText.paginationtext,
				context: this.context
			};
		},
		
		initialize: function(options){
			// add this view to Backbone.Courier
			Courier.add(this);
			
			this.context = options.context;
			
			//this.hasTouchEvent = Modernizr.touch;
			
			_.bindAll(this, 'enterXs','quitXs');

			this.onXsMedia = false;
			this.xsQueryHandler = {match : this.enterXs, unmatch: this.quitXs};
			mediaEnquire.registerXs(this.xsQueryHandler);
			
			if( this.onXsMedia ) {
				this.collection = new PageableObj(this.collection.models, {
					mode: 'client',
					state: {
					    firstPage: 1,
					    currentPage: 1
					  },
				});
			} else {
				this.collection = new PageableObj(this.collection.models, {
					mode: 'client',
					state: {
					    firstPage: 1,
					    currentPage: 1,
					    pageSize: 5
					  },
				});
			}
			
		},
		
		/**
		 * Do extra cleaning here on view closing. Marionnette manage already most of it.
		 */
		onClose : function() {
			mediaEnquire.unregisterXs(this.xsQueryHandler);
		},
		
		enterXs : function(){
			this.onXsMedia = true;
		},
		
		quitXs : function(){
			this.onXsMedia = false;
		},
		
		updateItemList : function(updatedCollection) {
			this.collection.set(updatedCollection.models,{add: true, remove: true, merge : true});
		},
		
		updateItemValues : function(updatedCollection) {
			this.collection.set(updatedCollection.models,{add: false, remove: false, merge : true});
		},
		
		goToNextPage : function(){
			this.collection.getNextPage();
			this.render();
		},
		
		goToPreviousPage : function(){
			this.collection.getPreviousPage();
			this.render();
		},
		
		goToPage : function(event){
			var pageNumber = event.currentTarget.text;
			this.collection.getPage(parseInt(pageNumber));
			this.render();
		},
		
		onScroll : function(event) {
			if( $(windows).scrollTop() == $(document).height() - $(window).height()){
				
			}
		}
	});
	
	return PaginView;
});
