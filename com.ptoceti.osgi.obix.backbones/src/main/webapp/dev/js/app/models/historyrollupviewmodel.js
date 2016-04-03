
define([ 'backbone', 'underscore' ], function(Backbone, _) {
	
	/**
	 * A list of watches
	 */
	var HistoryRollupViewModel = Backbone.Model.extend({
		
		defaults : {
			min: 0,
			max: 0,
			unit: '',
			date : new Date()
		},
	
	});
	
	return HistoryRollupViewModel;
	
});
