define([ 'backbone', 'underscore' ], function(Backbone, _) {
	
	/**
	 * A list of watches
	 */
	var HistoryViewModel = Backbone.Model.extend({
		
		defaults : {
			count : 0,
			historyUriToLoad : ''
		},
		
		
	
	});
	
	return HistoryViewModel;
	
});