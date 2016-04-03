define([ 'backbone', 'marionette', 'underscore', 'jquery', 'models/obix', 'modelbinder', 'courier', 'powerange', "i18n!nls/unittext", "i18n!nls/statustext", 'bootstrap' ], function(Backbone, Marionette, _, $, Obix, ModelBinder, Courier, Powerange, unitText, statusText) {
	
	var BlankItemView = Backbone.Marionette.ItemView.extend({
		tagName: "tr",
		template: "blankitem",
	
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
