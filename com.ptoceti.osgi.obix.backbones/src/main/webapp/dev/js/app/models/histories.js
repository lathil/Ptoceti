define([ 'backbone', 'underscore', 'models/compositehistory' ], function(Backbone, _,  CompositeHistory ) {
	
	/**
	 * A list of histories
	 */
	var Histories = Backbone.Collection.extend({
		
		model: CompositeHistory,
		
	
	});
	
	return Histories;
	
});