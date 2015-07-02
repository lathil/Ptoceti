define([ 'backbone', 'marionette', 'underscore', 'jquery', 'models/obix', 'modelbinder', 'courier', 'i18n!nls/statetext', "i18n!nls/statustext", 'bootstrap' ], function(Backbone, Marionette, _, $, Obix, ModelBinder, Courier, stateText, statusText) {
	
	var SwitchItemView = Backbone.Marionette.ItemView.extend({
		tagName: "tr",
		template: "switchitem",
		className: "switchItem",
		
		templateHelpers :  {
			contentEditable : function() {
				return Modernizr.contenteditable;
			}
		},
		
		// setup lister for pur DOM event handling
		events : {
			"click td" : "itemSelected",
			"click [name='switch']" : "switchClicked"
		},
		
		initialize : function() {
			// add this view to Backbone.Courier
			Courier.add(this);
			this.model.on('change:displayName', this.saveChanges, this);
			// initialize Backbone.ModelBinder for dual binding
			this.modelbinder = new ModelBinder();
		},
		
		// event handler called after the view has been closed
		onClose : function() {
			this.modelbinder.unbind();
		},
		
		/**
		 * Detect changes in the model from view, save them back to server
		 */
		saveChanges : function(model, value, options){
			// model bindel tag its change from the view by setting options.changeSource
			if( options.changeSource == 'ModelBinder'){
				model.save();
			}
		},
		
		// event handler call after the view has been rendered
		onRender : function(){
			this.modelbinder.bind(this.model, this.el, {
				val: [{selector: '[name=switch]',  converter: this.valueConverter}, {selector: '[name=switch]',  elAttribute: 'class', converter: this.valueClassConverter} ],
				name: {selector: '[name=displayName]', converter: this.nameConverter},
				status: [{selector: '[name=status]',  elAttribute: 'class', converter: this.statusClassConverter},{selector: '[name=status]', converter: this.statusConverter}],
				updateTimeStamp: {selector:'[name=timeStamp]', converter: this.lastTimeStamp}
			});
		},
		
		switchClicked : function(){
			this.model.save({val: this.model.get("val") == "true" ? "false" : "true" }, {
				success : this.lobbyLoaded,
				error : _.bind(function(model, response){
					console.log('Error loading lobby');
				}, this)
			})
		},
		
		itemSelected : function(){
			if( this.$el.hasClass("active")){
				this.$el.removeClass("active");
				// setup view event to clear selection
				this.spawn("switchItemSelected", {point: null});
			}
			else {
				$(".switchItem ").removeClass("active");
				this.$el.addClass("active");
				// setup view event to indicate selection
				this.spawn("switchItemSelected", {point: this.model});
			}
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
						return "btn btn-danger"
					} else return "btn btn-primary"
				}
			}
		},
		
		
		statusClassConverter : function( direction, value) {
			if( direction == "ModelToView") {
				if( value != null) {
					var statustoLower = value.toLowerCase();
					if( statustoLower == Obix.status.DISABLED) return "label label-default";
					if( statustoLower == Obix.status.FAULT) return "label label-danger";
					if( statustoLower == Obix.status.DOWN) return "label label-default";
					if( statustoLower == Obix.status.UNAKEDALARM) return "label label-info";
					if( statustoLower == Obix.status.ALARM) return "label label-warning";
					if( statustoLower == Obix.status.UNACKED) return "label label-info";
					if( statustoLower == Obix.status.OVERRIDEN) return "label label-primary";
					if( statustoLower == Obix.status.OK) return "label label-success";
				}
			}
		},
		
		statusConverter : function( direction, value) {
			if( direction == "ModelToView") {
				if( value != null) {
					var statustoLower = value.toLowerCase();
					if( statustoLower == Obix.status.DISABLED) return statusText[statustoLower];
					if( statustoLower == Obix.status.FAULT) return statusText[statustoLower];
					if( statustoLower == Obix.status.DOWN) return statusText[statustoLower];
					if( statustoLower == Obix.status.UNAKEDALARM) return statusText[statustoLower];
					if( statustoLower == Obix.status.ALARM) return statusText[statustoLower];
					if( statustoLower == Obix.status.UNACKED) return statusText[statustoLower];
					if( statustoLower == Obix.status.OVERRIDEN) return statusText[statustoLower];
					if( statustoLower == Obix.status.OK) return statusText[statustoLower];
				}
			}
		},
		
		lastTimeStamp : function(direction, value, attributeName, model ) {
			if( direction == "ModelToView") {
				var lastTimeStamp = model.get('updateTimeStamp');
				if( lastTimeStamp != null)
					return lastTimeStamp.toLocaleTimeString();
				
			}
		}
	});
	
	return SwitchItemView;
});