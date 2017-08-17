define([ 'backbone', 'underscore', 'models/compositehistory' ], function(Backbone, _,  CompositeAlarm ) {
	
	/**
	 * A list of alarms
	 */
	var Alarms = Backbone.Collection.extend({
		
		model: CompositeAlarm,
		
	
	});
	
	return Alarms;
	
});