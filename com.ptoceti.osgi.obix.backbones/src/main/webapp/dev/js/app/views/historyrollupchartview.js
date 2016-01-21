define([ 'backbone', 'marionette', 'underscore', 'jquery', 'courier', 'moment', 'mediaenquire', 'd3', 'xchart' ,  'bootstrap', 'css!../assets/xcharts/xcharts.css' ],
		function(Backbone, Marionette, _, $, Courier, Moment, mediaEnquire, d3, xChart ) {

	var HistoryRollupChartView = Marionette.Layout.extend({
		template : 'historyrollupchart',
		tagName: 'div',
		id: function(){
			return this.cid;
		},
		
		ui : {
			chartfigure: "[name=\"chartfigure\"]",
		},
		

		templateHelpers :  {
			onXsMedia : this.onXsMedia
		},
		
		initialize : function(options) {
			// add this view to Backbone.Courier
			Courier.add(this);
			
			_.bindAll(this, 'enterXs','quitXs');

			this.onXsMedia = false;
			this.xsQueryHandler = {match : this.enterXs, unmatched: this.quitXs};
			mediaEnquire.registerXs(this.xsQueryHandler);
		},
		
		/**
		 * Do extra cleaning here on view closing. Marionnette manage already most of it.
		 */
		onClose : function() {
			mediaEnquire.unregisterXs(this.xsQueryHandler);
		},
		
		enterXs : function(){
			this.onXsMedia = true;
		},
		
		quitXs : function(){
			this.onXsMedia = false;
		},
		
		 /**
	     * Called after the view has been rendered. Trigger a event to receive a updated list of values.
	     * 
	     */    
	    onRender : function() {
	    	
	    	
	    },
	    
	    /**
	     * Called when the view has been rendered and display, eg when placed inside a region
	     */
	    onShow : function() {
	    	
	    	var width = this.ui.chartfigure.width();
	    	this.ui.chartfigure.height( width / ( this.onXsMedia ? 16/9 : 32/9));
	    	 
	    	var opts = {
    			dataFormatX : function(x) {
    	            return new Date(x);
    	        },

    	        tickFormatX : function(x) {
    	            return  Moment(x).format('HH');
    	        },
    	        
    	        paddingLeft : 20,
    	        paddingTop : 10,
    	        paddingRight : 0,
    	        paddingBottom : 20,
    	        
    	        axisPaddingLeft : 10,
    	        
    	        tickHintY : 5
	    	};
	    	 
	    	this.historyChart = new xChart('line', this.getChartData(), '#' + this.cid +' > [name=\'chartfigure\']', opts);
	    },
	    
	    updateItemValues : function(historyRollupColl) {
	    	
	    	
	    	this.collection.set( historyRollupColl.models, {add: true, remove: false, merge : true});
	    	this.historyChart.setData(this.getChartData());
	    },
	    
	    
	    /**
	     * Calculate array of x, y point to be display in xchart
	     */
	    getChartData : function() {
	    	
	    	var set = [];
	    	var max =  Number.MIN_VALUE;
	    	var min =  Number.MAX_VALUE;
	    	
	    	_.each(this.collection.models, function(historyRecord, index, list) {
	    		var valueMin = parseInt(historyRecord.getMin().getVal());
	    		var valueMax = parseInt(historyRecord.getMax().getVal());
	    		
	    		if( valueMax > max ) max = valueMax;
	    		if( valueMin < min ) min = valueMin;
	    		
	    		set.push({ x : new Date(historyRecord.getStart().getVal()), y : historyRecord.getAvg().getVal()})
	    	},this);
	    	
	    	var data = {
				    "xScale" : "time",
				    "yScale" : "linear",
				    "yMin" : min,
				    "yMax" : max,
				    "main" : [{
				        className : "." + this.cid,
				        "data" : set
				    }]
				};
	    	
	    	return data;
	    }
	});

	return HistoryRollupChartView;
});
