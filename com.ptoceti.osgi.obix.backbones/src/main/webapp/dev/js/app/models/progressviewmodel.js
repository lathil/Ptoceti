define([ 'backbone', 'underscore' ], function(Backbone, _) {
	
	/**
	 * A list of watches
	 */
	var ProgressViewModel = Backbone.Model.extend({
		
		defaults : {
			progressMessage : '',
			progress : 30
		},
	
	});
	
	return ProgressViewModel;
	
});