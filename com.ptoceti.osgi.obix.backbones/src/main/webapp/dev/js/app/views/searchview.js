define([ 'backbone', 'marionette', 'underscore', 'jquery', 'eventaggr', 'mediaenquire','modelbinder', 'courier',  'models/searchviewmodel', 'modernizr', "i18n!nls/searchtext", 'bootstrap' ],
		function(Backbone, Marionette, _, $, ventAggr, mediaEnquire, ModelBinder, Courier, SearchViewModel, Modernizr, localizedSearchText ) {

	var SearchView = Marionette.Layout.extend({
		template : 'search',  

		ui : {
		
		},

		events : {
			"click [name='searchsubmit']" : "onDoSearch"
		},
		
		regions: {
			searchListRegion : "#searchList"
		},

		initialize : function(options) {
			
			// add this view to Backbone.Courier
			Courier.add(this);
			// initialize Backbone.ModelBinder for dual binding
			this.modelbinder = new ModelBinder();
			
			this.model = new SearchViewModel();
			
			ventAggr.on("controller:searchResultValues", this.onUpdateSearchList, this);
			ventAggr.on("controller:updatedWatchItemsList", this.onUpdatedItemsList, this);
			
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
				searchtext : this.onXsMedia ? localizedSearchText.searchtextxs : localizedSearchText.searchtext,
				onXsMedia : this.onXsMedia
			};
		},
		
		 /**
	     * Called after the view has been rendered. Trigger a event to receive a updated list of values.
	     * 
	     */    
	    onRender : function() {
	    	this.modelbinder.bind(this.model, this.el, {
	    		searchvalue: {selector: '[name=searchfield]'},
	    		count: {selector: '[name=count]'}
			}, {'changeTriggers': {'': 'change', '[name=searchfield]': 'enterpress'}});
	    },
	    
		/**
		 * Do extra cleaning here on view closing. Marionnette manage already most of it.
		 */
		onClose : function() {
			ventAggr.off("controller:searchResultValues", this.onUpdateSearchList, this);
			ventAggr.off("controller:updatedWatchItemsList", this.onUpdatedItemsList, this);
			this.modelbinder.unbind();
			mediaEnquire.unregisterXs(this.xsQueryHandler);
		},
		
		// listener for view events coming from a subview
		onMessages : {
	        "listItemSelected" : "onItemSelected",
	        "refLinkNavigate" : "onRefLinkNavigated"
	    },
		
		
		onItemSelected : function( message ) {
			
			if( message.data.point != null) {
				this.pointSelected = message.data.point;
			} else  {
				this.pointSelected = null;
			}
		},
		
		onRefLinkNavigated : function( message) {
			ventAggr.trigger("controller:doAdditemToWatch", message.data.point);
		},
		
		/**
		 * Detect changes in the model from view, save them back to server
		 */
		searchChanged : function(model, value, options){
			// model bindel tag its change from the view by setting options.changeSource
			if( options.changeSource == 'ModelBinder'){
				ventAggr.trigger("controller:doSearch", value);
			}
		},
		
		/**
		 * User cliecked on search sublit button
		 */
		onDoSearch: function(){
			ventAggr.trigger("controller:doSearch", this.model.get('searchvalue'));
		},
		
		onUpdateSearchList :function(searchCollection){
			
			var region = this.searchListRegion;	
			this.model.set("count", searchCollection.length);
			
			var requiredModules = ['views/paginationview','views/blankitemview'];
			_.each(searchCollection.models, function(element,index) {
				var nextView;
				
				if( element.attributes.type && element.attributes.type == 'ref' ) {
					nextView = 'views/refitemview';
				} 
				
				if( requiredModules.indexOf(nextView) < 0){
					requiredModules.push(nextView);
				}
			});
			
			require(requiredModules, function(PaginView){
				if( region.currentView == null){
					region.show(new PaginView({template:"pagination", collection: searchCollection, context: 'search'}));
				} else {
					region.currentView.updateItemList(searchCollection);
				}
			});
			
		},
		
		onUpdatedItemsList: function(updatedCollection) {
		
			var region = this.searchListRegion;	
			_.each(updatedCollection.models, function(item,index) {
				region.currentView.removeItemFromList(item);
			});
			
		},
		
	});

	return SearchView;
});
