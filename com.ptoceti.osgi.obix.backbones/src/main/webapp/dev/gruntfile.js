module.exports = function(grunt) {
	grunt.initConfig({
		pkg : grunt.file.readJSON('package.json'),
		bower: {
		    install: {
		    	options: {
		            targetDir: './js/lib',
		            layout: 'byComponent',
		            install: true,
		            verbose: true,
		            cleanTargetDir: true,
		            cleanBowerDir: false
		          }
		    }
		},
		
		modernizr: {
			 dist: {
			        // [REQUIRED] Path to the build you're using for development.
			        "devFile" : "bower_components/modernizer/modernizr.js",

			        // [REQUIRED] Path to save out the built file.
			        "outputFile" : "js/lib/modernizr/modernizr-custom.js",

			        // Based on default settings on http://modernizr.com/download/
			        "extra" : {
			            "shiv" : true,
			            "printshiv" : false,
			            "load" : true,
			            "mq" : false,
			            "cssclasses" : true
			        },

			        // Based on default settings on http://modernizr.com/download/
			        "extensibility" : {
			            "addtest" : false,
			            "prefixed" : false,
			            "teststyles" : false,
			            "testprops" : false,
			            "testallprops" : true,
			            "hasevents" : false,
			            "prefixes" : false,
			            "domprefixes" : false
			        },

			        // By default, source is uglified before saving
			        "uglify" : false,

			        // Define any tests you want to implicitly include.
			        "tests" : ["indexeddb", "mq", "svg", "touch", "websockets", "contenteditable"],

			        // By default, this task will crawl your project for references to Modernizr tests.
			        // Set to false to disable.
			        "parseFiles" : false,

			        // When parseFiles = true, this task will crawl all *.js, *.css, *.scss files, except files that are in node_modules/.
			        // You can override this by defining a "files" array below.
			        // "files" : {
			            // "src": []
			        // },

			        // When parseFiles = true, matchCommunityTests = true will attempt to
			        // match user-contributed tests.
			        "matchCommunityTests" : false,

			        // Have custom Modernizr tests? Add paths to their location here.
			        "customTests" : []
			    }
		},
		
		requirejs: {
			compile_build: {
			    options: {
			      appDir: "js",
			      baseUrl: "app",
			      mainConfigFile: "js/config.js",
			      dir: "js_optimized/",
			      // skip files or directories that start with '.'
			      fileExclusionRegExp: /^\./,
			      logLevel: 0,
			      optimizeCss : "none",
			      // skip optimizing any js file on out dir
			      skipDirOptimize: false,
			      // remove working files
			      removeCombined: true,
			      //
			      keepBuildDir: false,
			      // switch below to none for deguging optimizer output
			      //optimize:"none",
			      optimize:"uglify",
			      preserveLicenseComments:false,
			      optimizeCss: "standard",
			      
			      modules : [
			          {
			        	  name: 'main',
			        	  include: ["i18n", "text",'courier','moment','mediaenquire','jquery.enterkeyevent']
			          },
			          {
			        	  name:'views/progressview',
			        	  exclude: ['backbone', 'marionette', 'underscore', 'jquery']
			          },
			          {
			        	  name:'views/headerview',
			        	  exclude: ['backbone', 'marionette', 'underscore', 'jquery', 'bootstrap','moment',"i18n", "text"]
			          },
			          {
			        	  name:'views/footerview',
			        	  exclude: ['backbone', 'marionette', 'underscore', 'jquery','bootstrap','moment',"i18n", "text"]
			          },
			          {
			        	  name:'views/paginationview',
			        	  exclude: ['backbone', 'marionette', 'underscore', 'jquery', 'bootstrap', 'models/obix', 'modelbinder','mediaenquire','moment','courier','modernizr','jquery.enterkeyevent', "i18n", "text"]
			          },
			          {
			        	  name:'views/lobbyview',
			        	  exclude: ['backbone', 'marionette', 'underscore', 'jquery', 'bootstrap', 'models/obix', 'modelbinder','mediaenquire','moment','courier',"i18n", "text"]
			          },
			          {
			        	  name:'views/watchview',
			        	  exclude: ['backbone', 'marionette', 'underscore', 'jquery', 'bootstrap', 'models/obix', 'modelbinder','mediaenquire','moment','courier','modernizr','jquery.enterkeyevent',"i18n", "text"]
			          },
			          {
			        	  name :'views/watchitemview',
			        	  exclude : ['backbone', 'marionette', 'underscore', 'jquery', 'bootstrap', 'models/obix', 'modelbinder','moment','courier','modernizr', 'jquery.enterkeyevent',"i18n", "text"]
			          },
			          {
			        	  name: 'views/monitoreditemview',
			        	  exclude : ['backbone', 'marionette', 'underscore', 'jquery', 'bootstrap', 'models/obix', 'modelbinder', 'mediaenquire', 'moment','courier','modernizr', 'jquery.enterkeyevent',"i18n", "text"]
			          },
			          {
			        	  name:'views/historyrollupview',
			        	  exclude: ['backbone', 'marionette', 'underscore', 'jquery', 'bootstrap', 'models/obix', 'modelbinder', 'moment','courier','modernizr', "i18n", "text"] 
			          },
			          {
			        	  name:'views/historyrollupchartview',
			        	  exclude: ['backbone', 'marionette', 'underscore', 'jquery', 'bootstrap', 'models/obix', 'modelbinder', 'mediaenquire', 'moment','courier', 'd3', 'normalize', "i18n", "text"] 
			          }
			          
			      ]
			      
			    }
			}
		},
		jshint : {
			// define the files to lint
			files : [ 'gruntfile.js', './js/app/**/*.js' ],
			// configure JSHint (documented at
			// http://www.jshint.com/docs/)
			options : {
				// more options here if you want to override JSHint
				// defaults
				globals : {
					jQuery : true,
					console : true,
					module : true
				},
				ignores : [ './js/lib/**/*.js', './js/nls/**/*.js' ],
			}

		}
	});

	// Load libs
	grunt.loadNpmTasks('grunt-contrib-jshint');
	grunt.loadNpmTasks('grunt-bower-task');
	grunt.loadNpmTasks('grunt-contrib-requirejs');
	grunt.loadNpmTasks('grunt-modernizr');
	
	
	// Register building task
	grunt.registerTask('install', [ 'bower' , 'modernizr']);

	// Register default task
	grunt.registerTask('require', [ 'requirejs' ]);
	
	// Register default task
	grunt.registerTask('build', [ 'bower', 'modernizr', 'requirejs' ]);


};