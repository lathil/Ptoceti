
define([ 'backbone', 'marionette', 'underscore', 'jquery', 'models/obix', 'modelbinder', 'courier', "i18n!nls/unittext", "i18n!nls/statustext", 'bootstrap' ], function(Backbone, Marionette, _, $, Obix, ModelBinder, Courier, unitText, statusText) {
	
	var ReferenceItemView = Backbone.Marionette.ItemView.extend({
		tagName: "tr",
		template: "referenceitem",
		className: "referenceItem",
	
		templateHelpers :  {
			contentEditable : function() {
				return Modernizr.contenteditable;
			}
		},
		
		// setup lister for pur DOM event handling
		events : {
			"click td" : "itemSelected",
			"mousedown [name='backward']" : "backwardDown",
			"click [name='backward']" : "backward",
			"mousedown [name='forward']" : "forwardDown",
			"click [name='forward']" : "forward"
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
		
		// event handlers for the forward / bacward controls
		backwardDown : function(){
			this.backwarddown = true;
			this.backwardDownInterval = setInterval(function(item ){
				
				if(item.backwarddown == false){
					if( item.backwardDownInterval != null){
						clearInterval(item.backwardDownInterval);
						item.backwardDownInterval = null;
					}
				} else {
					item.model.setVal(( parseFloat(item.model.getVal()) + 1 ).toString());
				}
			}, 200, this);
			
		},
		backward : function(){
			this.backwarddown = false;
			if( this.model.hasChanged("val")){
				this.model.save();
			}
		},
		forwardDown : function(){
			this.forwarddown = true;
			this.forwardDownInterval = setInterval(function(item ){
				
				if(item.forwarddown == false){
					if( item.forwardDownInterval != null){
						clearInterval(item.forwardDownInterval);
						item.forwardDownInterval = null;
					}
				} else {
					item.model.setVal(( parseFloat(item.model.getVal()) - 1 ).toString());
				}
			}, 200, this);
			
		},
		forward : function(){
			this.forwarddown = false;
			if( this.model.hasChanged("val")){
				this.model.save();
			}
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
				unit: {selector: '[name=unit]', converter: this.unitConverter},
				val: [{selector: '[name=val]'} ],
				name: {selector: '[name=displayName]', converter: this.nameConverter},
				status: [{selector: '[name=status]',  elAttribute: 'class', converter: this.statusClassConverter},{selector: '[name=status]', converter: this.statusConverter}],
				updateTimeStamp: {selector:'[name=timeStamp]', converter: this.lastTimeStamp}
			});
		},
		
		itemSelected : function(){
			if( this.$el.hasClass("active")){
				this.$el.removeClass("active");
				// setup view event to clear selection
				this.spawn("pointItemSelected", {point: null});
			}
			else {
				$(".pointItem ").removeClass("active");
				this.$el.addClass("active");
				// setup view event to indicate selection
				this.spawn("pointItemSelected", {point: this.model});
			}
		},
		
		unitConverter : function(direction, value){
			if(direction == 'ModelToView'){
				var unitContract = value.getVal();
				if( unitContract.lastIndexOf("obix:Unit/") > -1){
					return unitText[unitContract.substr(unitContract.lastIndexOf('/') + 1)];
				}
			}
		},
		
		nameConverter : function(direction, value, attributeName, model){
			if( direction =='ModelToView') {
				if(model.getDisplayName() == '') return value;
				else return model.getDisplayName();
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
	
	return ReferenceItemView;
});
