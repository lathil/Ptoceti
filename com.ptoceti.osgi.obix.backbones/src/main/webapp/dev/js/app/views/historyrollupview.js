define([ 'backbone', 'marionette', 'underscore', 'jquery', 'models/obix', 'models/historyrollupviewmodel', 'mediaenquire', 'modelbinder', 'courier', 'moment' , 'numeral', 'modernizr',  "i18n!nls/unittext", "i18n!nls/rolluptext", 'bootstrap' ], function(Backbone, Marionette, _, $, Obix, HistoryRollupViewModel, mediaEnquire, ModelBinder, Courier, Moment , Numeral, Modernizr, unitText, localizedRollupText) {
	
	var HistoryRollUpView = Backbone.Marionette.Layout.extend({
		tagName: "div",
		template: "historyrollup",
		className: "historyrollup",
		
		model : new HistoryRollupViewModel(),
		
		regions : {
			historyRegion : '[name=\'history\']'
		},
		
		initialize : function() {
			// add this view to Backbone.Courier
			Courier.add(this);
			
			this.collection.on('change', this.updateModelFromCollection, this);
			this.collection.on('add remove', this.updateCollection, this);
			// initialize Backbone.ModelBinder for dual binding
			this.modelbinder = new ModelBinder();
			
			this.onXsMedia = false;
			this.xsQueryHandler = {match : this.enterXs, unmatched: this.quitXs};
			mediaEnquire.registerXs(this.xsQueryHandler);
		},
		
		enterXs : function(){
			this.onXsMedia = true;
		},
		
		quitXs : function(){
			this.onXsMedia = false;
		},
		
		templateHelpers : function() {
			return {
				rolluptext : localizedRollupText.rolluptext,
			};
		},
		
		// event handler called after the view has been closed
		onClose : function() {
			
			this.collection.off('update change', this.updateModelFromCollection, this);
			this.collection.off('add remove', this.updateCollection, this);
			this.modelbinder.unbind();
			mediaEnquire.unregisterXs(this.xsQueryHandler);
		},
		
		updateItemValues : function(historyRollupColl) {
			this.collection.set( historyRollupColl.models, {add: true, remove: true, merge : true});
			this.updateRollUpChart(this.collection);
		},
		
		// event handler call after the view has been rendered
		onRender : function(){
			if( this.collection.length > 0) {
				
				this.modelbinder.bind(this.model, this.el, {
					min: {selector: '[name=min]', converter: this.valConverter},
					max: {selector: '[name=max]', converter: this.valConverter},
					unit: [{selector: '[name=maxunit]', converter: this.unitConverter}, {selector: '[name=minunit]', converter: this.unitConverter}],
					date: {selector: '[name=date]', converter: this.dateConverter}
				});
			}
		},
		
		onShow : function(){
			this.updateRollUpChart(this.collection);
			this.updateModelFromCollection();
		},
		
		updateCollection : function(model, collection, option){
			this.updateModelFromCollection();
		},
		
		updateModelFromCollection : function(model, options){
			
			var max;
			var date;
			
			_.each(this.collection.models, function(model){
				if(max == null){
					max = new Number(model.getMax().getVal());
				} else {
					temp = new Number(model.getMax().getVal());
					if( temp > max){
						max = temp;
					}
				}
				var tempdate = Moment(model.getStart().getVal());
				if( tempdate.hours() == 0){
					//were at midnight, keep the date
					date = tempdate;
				}
			});
			
			if( date == null){
				if( this.collection.length > 0){
					date = Moment(this.collection.at(0).getStart().getVal());
				} else {
					date = new Moment();
				}
			}
			
			this.model.set('date', date);
			this.model.set('max', max);
			
			var min;
			_.each(this.collection.models, function(model){
				if(min == null){
					min = new Number(model.getMin().getVal());
				} else {
					temp = new Number(model.getMin().getVal());
					if( temp < min){
						min = temp;
					}
				}
			});
			this.model.set('min', min);
			
			if( this.collection.length > 0){
				var unit = this.collection.at(0).getMin().getUnit().getVal();
				this.model.set('unit', unit);
			}
			
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
		
		dateConverter : function(direction, value, attributeName, model) {
			if(direction == 'ModelToView'){
				return (this.onXsMedia ?  Moment(value).format("ddd, H:mm") : Moment(value).format("dddd, MMM, H:mm"))
			}
		},
		
		valConverter : function(direction, value, attributeName, model) {
			if(direction == 'ModelToView'){
				return Numeral(value).format('0.[00]a');
			}
		},
		
		unitConverter : function(direction, value){
			if(direction == 'ModelToView'){
				var unitContract = value;
				if( unitContract.lastIndexOf("obix:Unit/") > -1){
					return unitText[unitContract.substr(unitContract.lastIndexOf('/') + 1)];
				}
			}
		},
		
	});
	
	return HistoryRollUpView;
});