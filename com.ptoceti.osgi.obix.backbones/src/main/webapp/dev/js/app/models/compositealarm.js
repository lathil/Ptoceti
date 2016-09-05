define([ 'backbone', 'underscore', 'models/obix' ], function(Backbone, _,  Obix ) {
	
	/**
	 * A list of histories
	 */
	var CompositeAlarm = Backbone.Model.extend({
		
		defaults : {
			alarm : null,
			rollup : null,
		},
	
		setAlarm : function(inAlarm) {
			this.set({
				alarm : inAlarm
			});
		},
		getAlarm : function() {
			return this.get('alarm');
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
			if( contract == "ptoceti:compositealarm") return true;
			else return false;
		},
		
	});
	
	return CompositeAlarm;
	
});