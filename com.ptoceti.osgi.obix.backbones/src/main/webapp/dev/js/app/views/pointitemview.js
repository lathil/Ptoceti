define([ 'backbone', 'marionette', 'underscore', 'jquery', 'models/obix', 'mediaenquire', 'modelbinder', 'courier', 'numeral', 'moment', 'views/baseitemview', "i18n!nls/unittext", "i18n!nls/statustext","i18n!nls/itemtext", 'bootstrap', 'jquery.enterkeyevent' ],
		function(Backbone, Marionette, _, $, Obix, mediaEnquire, ModelBinder, Courier, Numeral, Moment, BaseItemView, unitText, statusText, itemText) {
	
	var PointItemView = BaseItemView.extend({
		tagName: "div",
		template: "pointitem",
		className: "item",
		
		initialize : function() {
			// add this view to Backbone.Courier
			Courier.add(this);
			this.on('itemUnselected', this.itemUnselected, this);
			this.model.on('change:displayName', this.saveChanges, this);
			// initialize Backbone.ModelBinder for dual binding
			this.modelbinder = new ModelBinder();
			
			_.bindAll(this, 'onChildCollapsed','onChildShown', 'onChildShow', 'enterXs','quitXs', 'childSizeChanged');
			
			this.onXsMedia = false;
			this.xsQueryHandler = {match : this.enterXs, unmatched: this.quitXs};
			mediaEnquire.registerXs(this.xsQueryHandler);
			
			this.collection = this.model.getChildrens();
			
			this.collection.on('add remove', function(model, collection, options){
				this.childSizeChanged(model, collection, options);
				this.recordItemListener(model, collection, options);
				this.alarmItemListener(model, collection, options);
			}, this);
		},
		
		// event handler called after the view has been closed
		onClose : function() {
			this.modelbinder.unbind();
			this.collection.off('add remove');
			this.off('itemUnselected', this.itemUnselected, this);
			this.stopListening(this.model, "change:val", this.onChangeValHistory);
			mediaEnquire.unregisterXs(this.xsQueryHandler);
			
		},
		
		
		// event handler call after the view has been rendered
		onRender : function(){
			this.modelbinder.bind(this.model, this.el, {
				unit: {selector: '[name=unit]', converter: this.unitConverter},
				val: [{selector: '[name=val]', converter: this.valConverter}, {selector: '[name=variance]', elAttribute:'class', converter: this.varianceConverter}],
				name: {selector: '[name=displayName]', converter: this.nameConverter},
				status: [{selector: '[name=status]',  elAttribute: 'class', converter: this.statusClassConverter}],
				updateTimeStamp: {selector:'[name=timeStamp]', converter: this.lastTimeStamp},
				childrens: [{selector: '[name=childCollapseItem]', elAttribute:'class', converter: this.collapseItemConverter}, {selector: '[name=recordItem]', elAttribute:'class', converter: this.recordItemConverter}, {selector: '[name=alarmItem]', elAttribute:'class', converter: this.alarmItemConverter}]
			},{'changeTriggers': {'': 'change', '[contenteditable]': 'enterpress'}} );
		},
		
		onItemRecord : function(){
			this.spawn("itemRecord", {point: this.model});
			event.stopImmediatePropagation();
		},
		
		valConverter : function(direction, value, attributeName, model) {
			if(direction == 'ModelToView'){
				return Numeral( new Number(value)).format('0.[00]a');
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
	
	return PointItemView;
});
