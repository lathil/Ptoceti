define('modernizr',[],Modernizr);


Modernizr.load({
	  test: Modernizr.mq(''),
	  nope: 'lib/media-match/media.match.js'
	});


 require.config({

	deps : [ 'main' ],

	baseUrl: 'app/',
	
	paths : {
		// libraries
		jquery :  '../lib/jquery/jquery',
		underscore : '../lib/underscore/underscore',
		backbone : '../lib/backbone/backbone',
		"backbone.wreqr" : '../lib/backbone.wreqr/lib/amd/backbone.wreqr',
		"backbone.babysitter" : '../lib/backbone.babysitter/lib/amd/backbone.babysitter',
		marionette : '../lib/backbone.marionette/lib/backbone.marionette',
		handlebars : '../lib/handlebars/handlebars',
		modelbinder : '../lib/Backbone.ModelBinder/Backbone.ModelBinder',
		collectionbinder : '../lib/Backbone.ModelBinder/Backbone.CollectionBinder',
		pageable : '../lib/backbone-pageable/lib/backbone-pageable',
		localstorage : "../lib/backbone.localstorage/backbone.localStorage",
		moment : "../lib/momentjs/moment",
		d3 : "../lib/d3/d3",
		css : "../lib/require-css/css",
		"css-builder" : "../lib/require-css/css-builder",
		normalize : "../lib/require-css/normalize",
		bootstrap : '../lib/bootstrap/dist/js/bootstrap',
		enquire : '../lib/enquire/enquire',

		modernizr : '../lib/modernizr/modernizr-custom',
		// pluggins
		text : '../lib/text/text',
		i18n : '../lib/requirejs-i18n/i18n',
		'marionette.handlebars' : 'pluggins/marionette.handlebars',
		"jquery.enterkeyevent" : 'pluggins/jquery.enterkeyevent',
		"d3.global" : 'pluggins/d3.global',
		"oauth2" : 'pluggins/backbones.oauth2',
			
		// helpers
		'handlebars.helpers' : 'helpers/handlebars.helpers',
		
		//assets
		'courier' : 'assets/backbone.courier/backbone.courier',
		xchart : 'assets/xcharts/xcharts',
		powerange : '../lib/powerange/powerange',
		numeral : "../lib/numeral/numeral",
		jscookie : "../lib/js-cookie/js.cookie",
		md5 : "../lib/js-md5/md5"
	},
	
	
	map: {
		  '*': {
			//Backbone.CollectionBinder require Backbone.Modelbinder, not modelbinder
		    'Backbone.ModelBinder': 'modelbinder'
		  }
	},
	

	shim : {
		underscore : {
			exports : '_'
		},
		backbone : {
			deps : [ 'underscore', 'jquery' ],
			exports : 'Backbone'
		},
		marionette: {
			deps : ['jquery', 'underscore', 'backbone'],
            exports: 'Marionette'
        },
		handlebars : {
			exports : 'Handlebars'
		},
		bootstrap : {
			deps : [ 'jquery' ]
		},
		xchart : {
			deps : ['d3.global']
		}
	},
	
});
 
