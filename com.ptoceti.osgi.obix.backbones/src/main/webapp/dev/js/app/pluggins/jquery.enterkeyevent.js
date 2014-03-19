(function(factory){
	if( typeof define =='function' && define.amd){
		define(['jquery'], factory);
	} else {
		factory(jQuery);
	}
}( function($) {
	
	$.event.special.enterpress = {

			setup: function( data, namespaces, eventhandle){
				var elem = this, $elem = $(elem);
				$elem.bind('keypress', {oldhandler: eventhandle}, $.event.special.enterpress.handler);
			},
			
			teardown : function( namespaces){
				var elem = this, $elem = $(elem);
				$elem.unbind('keypress', $.event.special.enterpress.handler);
			},
			
			add: function( handleObj){ 
				
			},
			
			remove: function( handleObj){
				
			},

			 
			handler : function(event){
				var handleObj = event.handleObj;
				var targetData = $.data(event.target);
				var ret = null;
				
				var keycode = (event.keyCode ? event.keyCode : event.which);
				if( keycode == '13'){
					event.type = "enterpress";
					ret = event.data.oldhandler.apply(this, arguments);
					return false;
				}
			}
	}
}));