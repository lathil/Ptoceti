define([ 'backbone', 'underscore', 'localstorage', 'models/obix' ], function(Backbone, _, LocalStorage, Obix ) {
	
	/**
	 * Create a backup of history rollups in browser local storage space.
	 */
	var HistoryRollupOutLocal = Backbone.Collection.extend({
		
		model: Obix.historyRollupOut,
		
		localStorage: new LocalStorage("historyrollupout")
	
	});
	
	return new HistoryRollupOutLocal();
	
});