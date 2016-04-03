define([ 'backbone', 'marionette', 'underscore', 'jquery', 'eventaggr','models/obix', 'models/pageableobjs', 'models/introviewmodel', 'views/introitemview' ], function(Backbone, Marionette, _, $, ventAggr, Obix, PageableObj, IntroViewModel) {
	
	var IntroView = Marionette.CompositeView.extend({
		template: 'intro',
		tagName:"div",
		
		model : new IntroViewModel(),
		
		//collection : new Obix.objs(),
		
		//itemView: PointItemView,
		//itemViewContainer: "itemContainer",
		
		ui : {
			nextPageButton : "#nextPage",
			previousPageButton : "#previousPage",
		},
		
		events : {
			"click #nextPage" : "goToNextPage",
			"click #previousPage" : "goToPreviousPage",
			"click #lobbyLink" : "goToLobby"
		},
		
		templateHelpers : function() {
			return {
				hasNext: this.collection.hasNext(),
				hasPrevious : this.collection.hasPrevious(),
				totalPages : this.collection.state.totalPages,
				currentPage : this.collection.state.currentPage,
				context: this.context
			};
		},
		
		modelEvents: {
		    "change": "render"
		},
		
		initialize : function() {
			
			this.collection = new PageableObj([],{
				mode: 'client',
				state: {
				    firstPage: 1,
				    currentPage: 1,
				    pageSize: 1
				  },
			});
			
			ventAggr.on("controller:updatedWatchPointList", this.onUpdatedPointsList, this);
			ventAggr.on("controller:updatedWatchPointValues", this.onUpdatedPointsValues, this);
			
			ventAggr.trigger("watch:updateList");
			
		},
		
		
		// event handler called after the view has been closed
		onClose : function() {
			ventAggr.off("controller:updatedWatchPointList", this.onUpdatedPointsList, this);
			ventAggr.off("controller:updatedWatchPointValues", this.onUpdatedPointsValues, this);
		},
		
		// event handler call after the view has been rendered
		onRender : function(){
			//ventAggr.trigger("watch:updateList");
		},
		
		
		getItemView: function(item){
			return require('views/introitemview');
		},
		
		itemViewContainer: function(){
		    return "#itemContainer";
		},
		
		goToLobby : function(event){
			ventAggr.trigger("app:goToLobby");
		},
		
		onUpdatedPointsList: function(updatedCollection) {
			
			var filteredCollection = new Obix.objs();
			
			_.each(updatedCollection.models, function(element,index) {
				if(element.hasContract('ptoceti:MeasurePoint')){
					element.set({updateTimeStamp: new Date()});
					filteredCollection.push(element);
				} 
			});
			
			
			this.collection.set(filteredCollection.models,{add: true, remove: false, merge : true});
			this.model.set('count', this.collection.length);
			this.checkNextPreviousControls();
			
			// ensure we do not have another timeout already set
			if( this.schedulesUpdateWatch != null ) clearTimeout(this.schedulesUpdateWatch);
			// triger event in futur to get new updated list
			this.schedulesUpdateWatch = setTimeout(function(){
				ventAggr.trigger("watch:updateListValues");
			}, 30000);
			
		},
		
		onUpdatedPointsValues: function(updatedCollection) {
			var region = this.pointsListRegion;
			
			var filteredCollection = new Obix.objs();
			
			_.each(updatedCollection.models, function(element,index) {
				if(element.hasContract('ptoceti:MeasurePoint')){
					element.set({updateTimeStamp: new Date()});
					filteredCollection.push(element);
				} 
			});
			
			this.collection.set(filteredCollection.models,{add: false, remove: false, merge : true});
			this.model.set('count', this.collection.length);
			this.checkNextPreviousControls();
			
			// ensure we do not have another timeout already set
			if( this.schedulesUpdateWatch != null ) clearTimeout(this.schedulesUpdateWatch);
			// triger event in futur to get new updated list
			this.schedulesUpdateWatch = setTimeout(function(){
				ventAggr.trigger("watch:updateListValues");
			}, 30000);
			
		},
		
		goToNextPage : function(){
			this.collection.getNextPage();
			this.checkNextPreviousControls();
		},
		
		goToPreviousPage : function(){
			this.collection.getPreviousPage();
			this.checkNextPreviousControls();
		},
		
		goToPage : function(event){
			var pageNumber = event.currentTarget.text;
			this.collection.getPage(parseInt(pageNumber));
			this.checkNextPreviousControls();
		},
		
		onScroll : function(event) {
			if( $(windows).scrollTop() == $(document).height() - $(window).height()){
				
			}
		},
		
		checkNextPreviousControls : function(){
			if(this.collection.hasNext()){
				if(this.ui.nextPageButton.hasClass('hidden')){
					this.ui.nextPageButton.removeClass('hidden');
				}
			} else {
				if(!this.ui.nextPageButton.hasClass('hidden')){
					this.ui.nextPageButton.addClass('hidden');
				}
			}
			
			if(this.collection.hasPrevious()){
				if(this.ui.previousPageButton.hasClass('hidden')){
					this.ui.previousPageButton.removeClass('hidden');
				}
			} else {
				if(!this.ui.previousPageButton.hasClass('hidden')){
					this.ui.previousPageButton.addClass('hidden');
				}
			}
		}
	});
	
	return IntroView;
});