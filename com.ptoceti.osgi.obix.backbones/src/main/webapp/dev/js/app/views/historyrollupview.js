define([ 'backbone', 'marionette', 'underscore', 'jquery', 'models/obix', 'modelbinder', 'courier', 'moment' , 'numeral', 'modernizr',  "i18n!nls/unittext", "i18n!nls/rolluptext", 'bootstrap' ], function(Backbone, Marionette, _, $, Obix, ModelBinder, Courier, Moment , Numeral, Modernizr, unitText, localizedRollupText) {
	
	var HistoryRollUpView = Backbone.Marionette.Layout.extend({
		tagName: "div",
		template: "historyrollup",
		className: "historyrollup",
		
		regions : {
			historyRegion : '[name=\'history\']'
		},
		
		initialize : function() {
			// add this view to Backbone.Courier
			Courier.add(this);
			// initialize Backbone.ModelBinder for dual binding
			this.modelbinder = new ModelBinder();
			this.maxModelbinder = new ModelBinder();
			this.minModelbinder = new ModelBinder();
		},
		
		templateHelpers : function() {
			return {
				rolluptext : localizedRollupText.rolluptext,
			};
		},
		
		// event handler called after the view has been closed
		onClose : function() {
			this.modelbinder.unbind();
			this.maxModelbinder.unbind();
			this.minModelbinder.unbind();
		},
		
		updateItemValues : function(historyRollupColl) {
			this.collection.set( historyRollupColl.models, {add: true, remove: false, merge : true});
			this.updateRollUpChart(this.collection);
		},
		
		// event handler call after the view has been rendered
		onRender : function(){
			this.maxModelbinder.bind(this.collection.at(0).getMax(), this.el, {
				val: {selector: '[name=max]', converter: this.valConverter},
				unit: {selector: '[name=maxunit]', converter: this.unitConverter},
			});
			this.minModelbinder.bind(this.collection.at(0).getMin(), this.el, {
				val: {selector: '[name=min]', converter: this.valConverter},
				unit: {selector: '[name=minunit]', converter: this.unitConverter},
			});
		},
		
		onShow : function(){
			this.updateRollUpChart(this.collection);
		},
		
		updateRollUpChart : function( historyRollupColl) {
			if( Modernizr.svg) {
				var region = this.historyRegion;
				require(['views/historyrollupchartview'], function(HistoryRollupChartView){
					if( region.currentView == null){
						region.show(new HistoryRollupChartView({collection: historyRollupColl}));
					} else {
						region.currentView.updateItemValues(historyRollupColl);
					} 
				});
			}
		},
		
		valConverter : function(direction, value, attributeName, model) {
			if(direction == 'ModelToView'){
				return Numeral( new Number(value)).format('0.[00]a');
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
		
	});
	
	return HistoryRollUpView;
});