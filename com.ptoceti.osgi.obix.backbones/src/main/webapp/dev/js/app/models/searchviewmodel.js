define([ 'backbone', 'underscore' ], function(Backbone, _) {
	
	/**
	 * A list of watches
	 */
	var SearchViewModel = Backbone.Model.extend({
		
		defaults : {
			count : 0,
			searchvalue : ''
		},
	
	});
	
	return SearchViewModel;
	
});