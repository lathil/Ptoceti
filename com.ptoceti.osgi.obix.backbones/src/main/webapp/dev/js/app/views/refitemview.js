define([ 'backbone', 'marionette', 'underscore', 'jquery', 'models/obix', 'eventaggr', 'modelbinder', 'courier', 'bootstrap' ], function(Backbone, Marionette, _, $, Obix, ventAggr, ModelBinder, Courier) {
	
	var RefItemView = Backbone.Marionette.ItemView.extend({
		tagName:"div",
		template: "refitem",
		className: "item",
	
		// setup lister for pur DOM event handling
		events : {
			"click [name='listItem']" : "itemSelected",
			"click [name='reflink']" : "onRefLinkNavigate"
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
			this.on('itemUnselected', this.itemUnselected, this);
			// initialize Backbone.ModelBinder for dual binding
			this.modelbinder = new ModelBinder();
			// add this view to Backbone.Courier
			Courier.add(this);

		},
		
		// event handler called after the view has been closed
		onClose : function() {
			this.off('itemUnselected', this.itemUnselected, this);
			this.modelbinder.unbind();
		},
		
		// event handler call after the view has been rendered
		onRender : function(){
			this.modelbinder.bind(this.model, this.el, {
				name: {selector: '[name=reflink]', converter: this.nameConverter},
			} );
		},
		
		onRefLinkNavigate : function(event){
			this.spawn("refLinkNavigate", {point: this.model});
			event.stopImmediatePropagation();
		},
		
		nameConverter : function(direction, value, attributeName, model){
			if( direction =='ModelToView') {
				if(model.getDisplayName() && model.getDisplayName().length > 0) return model.getDisplayName();
				else if(model.getDisplay() && model.getDisplay().length > 0) return model.getDisplay();
				else if(value && value.length > 0) return value;
			}
		},
		
		itemUnselected : function(){
			if( this.$el.hasClass("active")){
				this.$el.removeClass("active");
			}
		},
		
		itemSelected : function(){
			if( this.$el.hasClass("active")){
				this.$el.removeClass("active");
				// setup view event to clear selection
				this.spawn("listItemSelected", {point: null});
			}
			else {
				//$(".listItem ").removeClass("active");
				this.trigger("siblingItem:Unselect", '', this);
				this.$el.addClass("active");
				// setup view event to indicate selection
				this.spawn("listItemSelected", {point: this.model});
			}
		},
		
		/**
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
		**/
	
	});
	
	return RefItemView;
});