define([ 'backbone', 'marionette', 'underscore', 'jquery', 'models/obix', 'modelbinder', 'courier', 'views/baseitemview', 'i18n!nls/statetext', 'i18n!nls/statustext', 'bootstrap' ], function(Backbone, Marionette, _, $, Obix, ModelBinder, Courier, BaseItemView, stateText, statusText) {
	
	var StateItemView = BaseItemView.extend({
		tagName: "div",
		template: "stateitem",
		className: "item",
		
		
		// setup lister for pur DOM event handling
		events : {
			"click [name='listItem']" : "itemSelected",
			"click [name='deleteItem']" : "onItemDelete",
			"click [name='recordItem']" : "onItemRecord",
			"hidden.bs.collapse [name='childPanel']" : "onChildCollapsed",
			"show.bs.collapse [name='childPanel']" : "onChildShow",
			"shown.bs.collapse [name='childPanel']" : "onChildShown",
			"click [name='switch']" : "switchClicked"
		},
		
		// event handler call after the view has been rendered
		onRender : function(){
			this.modelbinder.bind(this.model, this.el, {
				val: [{selector: '[name=digit]',  converter: this.valueConverter}, {selector: '[name=digit]',  elAttribute: 'class', converter: this.valueClassConverter} ],
				name: {selector: '[name=displayName]', converter: this.nameConverter},
				status: [{selector: '[name=status]',  elAttribute: 'class', converter: this.statusClassConverter},{selector: '[name=statusText]', converter: this.statusConverter}],
				updateTimeStamp: {selector:'[name=timeStamp]', converter: this.lastTimeStamp}
			});
		},
		
		switchClicked : function(event){
			this.model.save({val: this.model.get("val") == "true" ? "false" : "true" }, {
				success : this.lobbyLoaded,
				error : _.bind(function(model, response){
					console.log('Error loading lobby');
				}, this)
			})
			event.stopImmediatePropagation();
		},
		
		valueConverter : function(direction, value, attributeName, model){
			if( direction =='ModelToView') {
				if(value == "true"){
					return stateText['on']
				} else return stateText['off']
			}
		},
		
		valueClassConverter : function( direction, value) {
			if( direction == "ModelToView") {
				if( value != null) {
					if(value == "true"){
						return "btn btn-danger btn-block"
					} else return "btn btn-primary btn-block"
				}
			}
		}
		
		
	});
	
	return StateItemView;
});