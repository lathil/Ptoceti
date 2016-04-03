define([ 'backbone', 'marionette', 'underscore', 'jquery', 'models/obix', 'eventaggr', 'modelbinder', 'courier', 'bootstrap' ], function(Backbone, Marionette, _, $, Obix, ventAggr, ModelBinder, Courier) {
	
	var RefItemView = Backbone.Marionette.ItemView.extend({
		tagName:"div",
		template: "refitem",
	
		// setup lister for pur DOM event handling
		events : {
			"click [name='reflink']" : "onRefLinkNavigate",
		},
		
		templateHelpers :  {
			elemId : function() {
				return this.href.getVal().replace(/\//gi,"-");
			},
			contentEditable : function() {
				return Modernizr.contenteditable;
			}
		},
		
		initialize : function() {
			// add this view to Backbone.Courier
			Courier.add(this);

		},
		
		// event handler called after the view has been closed
		onClose : function() {
		
		},
		
		// event handler call after the view has been rendered
		onRender : function(){
			
		},
		
		onRefLinkNavigate : function(){
			if( this.model.hasContract('obix:History')){
				ventAggr.trigger("app:goToHistoriesWithHistory", this.model.getHref().getVal());
			}
		},
		
		fetchHistory : function( historyRef ){
			var region = this.refRegion;
			var refmodel = this.model;
			require(['views/historyitemview'], function(HistoryItemView){
				if( region.currentView == null){
					 var history = new Obix.history({href : historyRef}, {urlRoot : refmodel.urlRoot});
					 history.fetch({
						 headers: oauth2.getAuthorizationHeader(),
						 success: _.bind(function(historymodel, response) {
							 region.show(new HistoryItemView({model:historymodel}));
						 },this)
					 },this);
				} 
			});
		}
	
	});
	
	return RefItemView;
});