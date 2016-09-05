define([ 'backbone', 'underscore' ], function(Backbone, _) {
	
	/**
	 * A list of watches
	 */
	var AlarmViewModel = Backbone.Model.extend({
		
		defaults : {
			count : 0,
			alarmUriToLoad : ''
		},
		
		
	
	});
	
	return AlarmViewModel;
	
});