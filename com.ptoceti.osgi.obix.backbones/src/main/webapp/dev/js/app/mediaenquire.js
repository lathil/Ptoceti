
/**
 * Simple wrapper around enuire.js to set default breakpoints to match thoses of bootstrap. Guess I could have done with a mixin as well
 * 
 * 
 */
define([ 'underscore', 'enquire' ], function( _ , Enquire) {

	var MediaEnquire = function(enquirejs) {
		this.enquire = enquirejs;
	};
	
	_.extend(MediaEnquire.prototype, {
		
		/* extra small devices */
		registerXs : function(options, shouldDegrade) {
			this.enquire.register("screen and (max-device-width : 767px)", options, shouldDegrade);
		},
		
		unregisterXs : function(handler) {
			this.enquire.unregister("screen and (max-device-width : 767px)", handler);
		},
		
		/* small devices */
		registerSm : function(options, shouldDegrade) {
			this.enquire.register("screen and (min-device-width : 768px) and (max-device-width : 991px)", options, shouldDegrade);
		},
		
		unregisterSm : function(handler) {
			this.enquire.unregister("screen and (min-device-width : 768px) and (max-device-width : 991px)", handler);
		},
		
		/* medium devices */
		registerMd : function(options, shouldDegrade) {
			this.enquire.register("screen and (min-device-width : 992px) and (max-device-width : 1199px)", options, shouldDegrade);
		},
		
		unregisterMd : function(handler) {
			this.enquire.unregister("screen and (min-device-width : 992px) and (max-device-width : 1199px)", handler);
		},
		
		/* large devices */
		registerLd : function(options, shouldDegrade) {
			this.enquire.register("screen and (min-device-width : 1200px)", options, shouldDegrade);
		},
		
		unregisterLd : function(handler) {
			this.enquire.unregister("screen and (min-device-width : 1200px)", handler);
		}
		
		
	});

	
	return new MediaEnquire( Enquire);
});