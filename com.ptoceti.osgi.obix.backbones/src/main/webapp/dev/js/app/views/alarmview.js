define([ 'backbone', 'marionette', 'underscore', 'jquery', 'eventaggr', 'mediaenquire', 'modelbinder', 'courier', 'models/alarmviewmodel','views/alarmitemview', "i18n!nls/alarmtext", 'bootstrap' ],
		function(Backbone, Marionette, _, $, ventAggr, mediaEnquire, ModelBinder, Courier, AlarmViewModel, AlarmItemView, localizedAlarmText ) {
	
	var AlarmView = Marionette.Layout.extend({
		template : 'alarm',
		
		ui : {
			reloadButton : "#reloadButton",
			removeButton : "#removeButton",
		},
		
		events : {
			"click #reloadButton" : "reloadList",
			"click #removeButton" : "removeFromList",
		},
		
		regions: {
			AlarmListRegion : "#alarmList"
		},
		
		initialize : function(options) {
			
			// add this view to Backbone.Courier
			Courier.add(this);
			// initialize Backbone.ModelBinder for dual binding
			this.modelbinder = new ModelBinder();
			
			this.model = new AlarmViewModel();
			this.model.set("alarmUriToLoad", options.alarmUri);
			
			ventAggr.on("controller:updatedAlarmList", this.onUpdatedAlarmList, this);
			
			_.bindAll(this, 'enterXs','quitXs');

			this.onXsMedia = false;
			this.xsQueryHandler = {match : this.enterXs, unmatched: this.quitXs};
			mediaEnquire.registerXs(this.xsQueryHandler);
			
		},
		
		enterXs : function(){
			this.onXsMedia = true;
		},
		
		quitXs : function(){
			this.onXsMedia = false;
		},
		
		templateHelpers : function() {
			return {
				alarmtext : this.onXsMedia ? localizedAlarmText.alarmtextxs : localizedAlarmText.alarmtext,
				onXsMedia : this.onXsMedia
			};
		},
		
		/**
		 * Do extra cleaning here on view closing. Marionnette manage already most of it.
		 */
		onClose : function() {
			this.modelbinder.unbind();
			ventAggr.off("controller:updatedAlarmList", this.onUpdatedAlarmList, this);
			mediaEnquire.unregisterXs(this.xsQueryHandler);
		},
		
		 /**
	     * Called after the view has been rendered. Trigger a event to receive a updated list of values.
	     * 
	     */    
	    onRender : function() {
	    	this.modelbinder.bind(this.model, this.el, {
				count: {selector: '[name=count]'},
			});
	    	
	    	ventAggr.trigger("controller:loadAlarm", this.model.get('alarmUriToLoad'));
	    
	    },
		
	 // listener for view events coming from a subview
		onMessages : {
			"alarmItemDelete" : "onAlarmDelete",
	        "alarmItemSelected" : "onAlarmSelected"
	    },
		
		onAlarmSelected : function( message ) {
			
			if( message.data.Alarm != null) {
				this.AlarmSelected = message.data.alarm;
				this.ui.removeButton.removeAttr("disabled");
			} else  {
				this.AlarmSelected = null;
				this.ui.removeButton.attr("disabled", "disabled");
			}
		},
		
		
		onUpdatedAlarmList: function(updatedCollection) {
			var region = this.AlarmListRegion;
			this.model.set("count", updatedCollection.length);
			var modelCid = this.model.get("alarmUriToLoad");
			require(['views/paginationview','views/alarmitemview'], function(PaginView){
				if( region.currentView == null){
					region.show(new PaginView({template: "pagination", itemView: AlarmItemView,  collection: updatedCollection, context: 'alarm', modelToOpenCid : modelCid}));
				} else {
					region.currentView.updateItemList(updatedCollection);
				}
			});
		},
		
		reloadList : function() {
			
		},
		
		removeFromList : function() {
			ventAggr.trigger("alarm:removeAlarm", this.AlarmSelected);
		},
		
		onAlarmDelete : function( message ){
			ventAggr.trigger("alarm:removeAlarm", message.data.alarm.getAlarm());
		}
		
	});
		
	
	return AlarmView;
});