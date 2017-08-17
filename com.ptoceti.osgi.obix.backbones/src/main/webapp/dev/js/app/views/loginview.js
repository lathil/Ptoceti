define([ 'backbone', 'marionette', 'underscore', 'jquery', 'eventaggr', 'courier', 'md5', 'bootstrap' ], function(Backbone, Marionette, _, $, ventAggr, Courier, md5 ) {
	
	
	var LoginView = Backbone.Marionette.ItemView.extend({
		tagName: "div",
		template: "login",
	
		templateHelpers :  {
			contentEditable : function() {
				return Modernizr.contenteditable;
			}
		},
		
		ui : {
			username : "#username",
			password : "#secret",
			enter : "#enter",
			spin : "#spin"
		},
		
		// setup lister for pur DOM event handling
		events : {
			"click #enter" : "doEnter",
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
		
		doEnter : function(){
			this.ui.enter.addClass('disabled');
			if(this.ui.spin.hasClass('hidden')){
				this.ui.spin.removeClass('hidden');
			}
			this.ui.spin.addClass('show');
			ventAggr.trigger("controler:doLogin", {name: this.ui.username[0].value, password: md5(this.ui.password[0].value)});
		}
	});
	
	return LoginView;
});
