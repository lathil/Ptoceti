define([ 'backbone', 'underscore' ], function(Backbone, _) {
	
	/**
	 * A list of watches
	 */
	var IntroViewModel = Backbone.Model.extend({
		
		defaults : {
			count : 0,
		},
	
	});
	
	return IntroViewModel;
	
});