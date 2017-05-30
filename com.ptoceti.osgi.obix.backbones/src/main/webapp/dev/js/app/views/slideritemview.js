define([ 'backbone', 'marionette', 'underscore', 'jquery', 'models/obix', 'eventaggr', 'modelbinder', 'courier', 'bootstrap' ],
		function(Backbone, Marionette, _, $, Obix, ventAggr, ModelBinder, Courier) {

	var SliderItemView = Backbone.Marionette.ItemView.extend({
		tagName:"div",
		template: "slideritem",
		className: "item",
	
		// setup lister for pur DOM event handling
		events : {
			
		},
		
		templateHelpers :  {
			elemId : function() {
				return this.href.getVal().replace(/\//gi,"-");
			}
		},
		
		initialize : function() {
			
			// initialize Backbone.ModelBinder for dual binding
			this.modelbinder = new ModelBinder();
			// add this view to Backbone.Courier
			Courier.add(this);

		},
		
		// event handler called after the view has been closed
		onClose : function() {
			
			this.modelbinder.unbind();
		},
		
		// event handler call after the view has been rendered
		onRender : function(){
		
		},
		

	});
	
	return SliderItemView;
	
}