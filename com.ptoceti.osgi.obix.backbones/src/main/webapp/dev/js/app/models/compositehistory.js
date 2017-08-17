define([ 'backbone', 'underscore', 'models/obix' ], function(Backbone, _,  Obix ) {
	
	/**
	 * A list of histories
	 */
	var CompositeHistory = Backbone.Model.extend({
		
		defaults : {
			history : null,
			rollup : null,
		},
	
		setHistory : function(inHistory) {
			this.set({
				history : inHistory
			});
		},
		getHistory : function() {
			return this.get('history');
		},
		
		setRollUp : function(inRollUp) {
			this.set({
				rollup : inRollUp
			});
		},
		getRollUp : function() {
			return this.get('rollup');
		},
		
		hasContract : function(contract) {
			if( contract == "ptoceti:compositehistory") return true;
			else return false;
		},
		
	});
	
	return CompositeHistory;
	
});