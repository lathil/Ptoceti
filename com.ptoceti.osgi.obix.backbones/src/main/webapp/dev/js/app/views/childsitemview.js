define([ 'backbone', 'marionette', 'underscore', 'jquery', 'courier', 'mediaenquire', 'modernizr', 'models/pageableobjs', "i18n!nls/paginationtext"],
		function(Backbone, Marionette, _, $, Courier, mediaEnquire, Modernizr, PageableObj, localizedPaginationText) {
	
	var ChildsItemView = Marionette.CollectionView.extend({
		template:"childsitem",
		
		tagName:"div",
		
		ui : {
		
		},
		
		events : {

		},
		
	
		
		// pass all messages from subview to parent view
		passMessages : {
			"*" : '.'
		},
		
		
		templateHelpers : function() {
			return {

			};
		},
		
		initialize: function(options){
			// add this view to Backbone.Courier
			Courier.add(this);
			
			this.context = options.context;
			

		},
		
		/**
		 * Do extra cleaning here on view closing. Marionnette manage already most of it.
		 */
		onClose : function() {
			
		},
		
		getItemView: function(item){
	
			if( item.getIs() != undefined){
				if( item.hasContract('obix:Point')) {
					return require('views/pointitemview');
				}
				if( item.hasContract('ptoceti:MeasurePoint')) {
					return require('views/pointitemview');
				}
				if( item.hasContract('ptoceti:MonitoredPoint')) {
					return require('views/monitoreditemview');
				}
				if( item.hasContract('ptoceti:ReferencePoint')) {
					return require('views/referenceitemview');
				}
				if( item.hasContract('ptoceti:DigitPoint')) {
					return require('views/stateitemview');
				}
				if( item.hasContract('ptoceti:SwitchPoint')) {
					return require('views/switchitemview');
				}
				
			} 
			
			if( item.attributes.type && item.attributes.type == 'ref' ) {
				return require('views/refitemview');
			} 
			
			return require('views/blankitemview');
		}
		
	});
	
	return ChildsItemView;
});