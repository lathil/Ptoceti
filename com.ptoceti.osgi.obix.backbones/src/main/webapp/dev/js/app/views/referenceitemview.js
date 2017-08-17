
define([ 'backbone', 'marionette', 'underscore', 'jquery', 'models/obix', 'modelbinder', 'courier', 'views/baseitemview', 'powerange', "i18n!nls/unittext", "i18n!nls/statustext", 'bootstrap' ], 
		function(Backbone, Marionette, _, $, Obix, ModelBinder, Courier, BaseItemView, Powerange, unitText, statusText) {
	
	var ReferenceItemView = BaseItemView.extend({
		tagName: "div",
		template: "referenceitem",
		className: "item",
	
		ui : {
			infosCollapsePanel : "[name=\"infoPanel\"]",
			childCollapsePanel : "[name=\"childPanel\"]",
			childCollapseControlElem : "[name=\"childCollapseControl\"]",
			childCollapseItem : "[name=\"childCollapseItem\"]",
			rangeSlider :  "[name=\"range\"]"
		},
		
		
		// setup lister for pur DOM event handling
		events : {
			"click [name='listItem']" : "itemSelected",
			"click [name='deleteItem']" : "onItemDelete",
			"click [name='childCollapseItem']" : "onChildCollapse",
			"hidden.bs.collapse [name='childPanel']" : "onChildCollapsed",
			"show.bs.collapse [name='childPanel']" : "onChildShow",
			"shown.bs.collapse [name='childPanel']" : "onChildShown",
			"touchend .range-handle" : "onRangeClick",
			"mouseup .range-handle" : "onRangeClick"
		},
		
	
		onRangeClick : function(event){
			if( this.model.hasChanged("val")){
				this.model.save();
			}
			//event.stopImmediatePropagation();
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
		
		
		// event handler call after the view has been rendered
		onRender : function(){
			this.range = new Powerange(this.ui.rangeSlider[0], {
				min: this.model.getMin(), max: this.model.getMax(), start: this.model.getVal(), callback: _.bind(this.onRangeChange, this), hideRange: true});
			
			this.modelbinder.bind(this.model, this.el, {
				unit: {selector: '[name=unit]', converter: this.unitConverter},
				val: [{selector: '[name=val]'} ],
				name: {selector: '[name=displayName]', converter: this.nameConverter},
				status: [{selector: '[name=status]',  elAttribute: 'class', converter: this.statusClassConverter},{selector: '[name=statusText]', converter: this.statusConverter}]
			});
		},
		
		onRangeChange : function(){
			if( this.range){
				this.model.setVal(( parseFloat(this.range.element.value)).toString());
			}
		},
		

	});
	
	return ReferenceItemView;
});
