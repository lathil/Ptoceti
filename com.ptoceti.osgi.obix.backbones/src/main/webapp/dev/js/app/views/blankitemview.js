define([ 'backbone', 'marionette', 'underscore', 'jquery', 'models/obix', 'modelbinder', 'courier', 'bootstrap' ], 
		function(Backbone, Marionette, _, $, Obix, ModelBinder, Courier) {
	
	var BlankItemView = Backbone.Marionette.ItemView.extend({
		tagName: "div",
		template: "blankitem",
		className: "item",
	
		templateHelpers :  {
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
	
	});
	
	return BlankItemView;
});
