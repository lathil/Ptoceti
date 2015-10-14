define([ 'backbone', 'marionette', 'underscore', 'jquery', 'eventaggr', 'modelbinder', 'moment', 'mediaenquire', 'marionette.handlebars', 'handlebars.helpers','bootstrap' ],
		function(Backbone, Marionette, _, $, ventAggr, ModelBinder, Moment, mediaEnquire  ) {

	var LandingView = Marionette.Layout.extend({
		template : 'landing',

		ui : {
			//serverTimeElem : "#serverTime",
			localTimeElem : "#localTime",
		},
	
		templateHelpers : function() {
			return {
				localTime :  this.onXsMedia ?  Moment().format("ddd, H:mm") : Moment().format("dddd, MMM, H:mm"),
			};
		},
		
		initialize : function() {		
			
			this.schedulesUpdateTimes = setInterval(_.bind(this.updateTimes, this), 1000);
			
			_.bindAll(this, 'enterXs','quitXs');
			
			this.onXsMedia = false;
			this.xsQueryHandler = {match : this.enterXs, unmatch: this.quitXs};
			mediaEnquire.registerXs(this.xsQueryHandler);
			
		},
		
		onclose : function() {
			clearinterval(this.schedulesUpdateTimes);
			mediaEnquire.unregisterXs(this.xsQueryHandler);
		},
		
		enterXs : function(){
			this.onXsMedia = true;
		},
		
		quitXs : function(){
			this.onXsMedia = false;
		},
		
		updateTimes : function() {
			if (typeof this.ui.localTimeElem !== 'undefined' && typeof this.ui.serverTimeElem !== 'undefined') {
				this.ui.localTimeElem.text(this.onXsMedia ?  Moment().format("ddd, H:mm") : Moment().format("dddd, MMM, H:mm"));
			}
		},
	
	});
	
	
	return LandingView;
});
