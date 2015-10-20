module.exports = function(grunt) {
	grunt
			.initConfig({
				pkg : grunt.file.readJSON('package.json'),
				bower : {
					install : {
						options : {
							targetDir : './js/lib',
							layout : 'byComponent',
							install : true,
							verbose : true,
							cleanTargetDir : true,
							cleanBowerDir : false
						}
					}
				},

				less : {
					compileCore: {
				        options: {
				          strictMath: true,
				          sourceMap: true,
				          outputSourceFiles: true,
				          sourceMapURL: 'bootstrap.css.map',
				          sourceMapFilename: 'dist/css/bootstrap.css.map',
				          paths : [ 'less' ],
				        },
				        
						files : {
							'js/lib/bootstrap/dist/css/bootstrap.css' : 'less/manifest.less'
						}
					}

				},

				modernizr_builder: {
				    build: {
				        options: {
				            features: 'indexeddb, svg, touch, websockets, contenteditable',
				            options: 'mq',
				            dest: 'js/lib/modernizr/modernizr-custom.js'
				        }
				    }
				},
				
				modernizr : {
					dist : {
						// Avoid unnecessary builds (see Caching section below)
						"cache" : false,
						// [REQUIRED] Path to the build you're using for
						// development.
						"devFile" : "bower_components/modernizr/modernizr.js",


						// [REQUIRED] Path to save out the built file.
						"outputFile" : "js/lib/modernizr/modernizr-custom.js",

						// Based on default settings on
						// http://modernizr.com/download/
						"extra" : {
							"shiv" : true,
							"printshiv" : false,
							"load" : true,
							"mq" : false,
							"cssclasses" : true
						},

						// Based on default settings on
						// http://modernizr.com/download/
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
						"tests" : [ "indexeddb", "mq", "svg", "touch",
								"websockets", "contenteditable" ],

						// By default, this task will crawl your project for
						// references to Modernizr tests.
						// Set to false to disable.
						"parseFiles" : false,

						// When parseFiles = true, this task will crawl all
						// *.js, *.css, *.scss files, except files that are in
						// node_modules/.
						// You can override this by defining a "files" array
						// below.
						// "files" : {
						// "src": []
						// },

						// When parseFiles = true, matchCommunityTests = true
						// will attempt to
						// match user-contributed tests.
						"matchCommunityTests" : false,

						// Have custom Modernizr tests? Add paths to their
						// location here.
						"customTests" : []
					}
				},
				compress : {
					main : {
						options : {
							mode : 'gzip'
						},
						files : [
						// Each of the files in the src/ folder will be output
						// to
						// the dist/ folder each with the extension .gz.js
						{
							expand : true,
							src : [ './js_optimized/**/*.js' ],
							dest : '',
							ext : '.js.gz'
						} ]
					}
				},
				requirejs : {
					compile_build : {
						options : {
							appDir : "js",
							baseUrl : "app",
							mainConfigFile : "js/config.js",
							dir : "js_optimized/",
							// skip files or directories that start with '.'
							fileExclusionRegExp : /^\./,
							logLevel : 0,
							optimizeCss : "none",
							// skip optimizing any js file on out dir
							skipDirOptimize : false,
							// remove working files
							removeCombined : true,
							//
							keepBuildDir : false,
							// switch below to none for deguging optimizer
							// output
							//optimize:"none",
							optimize : "uglify",
							preserveLicenseComments : false,
							optimizeCss : "standard",

							modules : [
									{
										name : 'views/footerview',
										exclude : [ 'backbone', 'marionette','underscore', 'jquery','bootstrap', 'moment', "i18n","text",'modernizr' ]
									},
									{
										name : 'views/headerview',
										exclude : [ 'backbone', 'marionette','underscore', 'jquery','bootstrap', 'moment', "i18n","text",'modernizr' ]
									},
									{
										name : 'views/historyrollupchartview',
										exclude : [ 'backbone', 'marionette','underscore', 'jquery','bootstrap', 'models/obix','modelbinder', 'mediaenquire','moment', 'courier', 'd3', 'd3.global', 'xchart', 'normalize', "i18n", "text", 'modernizr' ]
									},
									{
										name : 'views/historyrollupview',
										exclude : [ 'backbone', 'marionette','underscore', 'jquery','bootstrap', 'models/obix','modelbinder', 'moment','courier', 'modernizr', "i18n","text" ]
									},
									{
										name : 'views/historyview',
										exclude : [ 'backbone', 'marionette','underscore', 'jquery','bootstrap', 'courier', "i18n","text",'modernizr' ]
									},
									{
										name : 'views/introitemview',
										exclude : [ 'backbone', 'marionette', 'underscore', 'jquery', 'moment', 'models/obix', 'modelbinder', 'courier', 'bootstrap','modernizr' ]
									},
									{
										name : 'views/introview',
										exclude : [ 'backbone', 'marionette', 'underscore', 'jquery', 'eventaggr','models/obix','modernizr']
									},
									{
										name : 'views/landingview',
										exclude : [ 'backbone', 'marionette', 'underscore', 'jquery', 'eventaggr', 'modelbinder', 'moment', 'mediaenquire', 'bootstrap','modernizr']
									},
									{
										name : 'views/lobbyview',
										exclude : [ 'backbone', 'marionette', 'underscore', 'jquery', 'eventaggr', 'mediaenquire','modelbinder', 'courier', 'bootstrap','modernizr']
									},
									{
										name : 'views/monitoreditemview',
										exclude : [ 'backbone', 'marionette','underscore', 'jquery','bootstrap', 'models/obix','modelbinder', 'mediaenquire','moment', 'courier','modernizr','jquery.enterkeyevent', "i18n","text" ]
									},
									{
										name : 'views/obixview',
										exclude : [ 'backbone', 'marionette', 'underscore', 'jquery', 'eventaggr', 'modelbinder', 'bootstrap', 'modernizr']
									},
									{
										name : 'views/paginationview',
										exclude : [ 'backbone', 'marionette', 'underscore', 'jquery', 'courier', 'mediaenquire', 'modernizr' ]
									},
									{
										name : 'views/pointitemview',
										exclude : [ 'backbone', 'marionette', 'underscore', 'jquery', 'models/obix', 'modelbinder', 'courier', 'bootstrap','modernizr']
									},
									{
										name : 'views/progressview',
										exclude : [ 'backbone', 'marionette', 'underscore', 'jquery', 'eventaggr', 'modelbinder','modernizr' ]
									},
									{
										name : 'views/referenceitemsvgview',
										exclude : [ 'backbone', 'marionette', 'underscore', 'jquery', 'models/obix', 'modelbinder', 'courier', 'bootstrap','modernizr','d3', 'd3.global', ]
									},
									{
										name : 'views/referenceitemview',
										exclude : [ 'backbone', 'marionette', 'underscore', 'jquery', 'models/obix', 'modelbinder', 'courier', 'bootstrap','modernizr' ]
									},
									{
										name : 'views/stateitemview',
										exclude : [ 'backbone', 'marionette', 'underscore', 'jquery', 'models/obix', 'modelbinder', 'courier', 'bootstrap','modernizr'  ]
									},
									{
										name : 'views/switchitemview',
										exclude : [ 'backbone', 'marionette', 'underscore', 'jquery', 'models/obix', 'modelbinder', 'courier', 'bootstrap','modernizr' ]
									},
									{
										name : 'views/watchitemview',
										exclude : [ 'backbone', 'marionette', 'underscore', 'jquery', 'models/obix', 'eventaggr', 'modelbinder', 'courier', 'moment', 'modernizr', 'bootstrap', 'jquery.enterkeyevent','modernizr' ]
									},
									{
										name : 'views/watchview',
										exclude : [ 'backbone', 'marionette', 'underscore', 'jquery', 'eventaggr', 'mediaenquire', 'modelbinder', 'courier', 'bootstrap','modernizr' ]
									},
									
									{
										name : 'main',
										include : [ "i18n", "text", 'courier','moment', 'mediaenquire','jquery.enterkeyevent' ]
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
	grunt.loadNpmTasks('grunt-contrib-less');
	grunt.loadNpmTasks('grunt-contrib-requirejs');
	grunt.loadNpmTasks('grunt-modernizr');
	//grunt.loadNpmTasks('grunt-modernizr-builder');
	grunt.loadNpmTasks('grunt-contrib-compress');

	// Register building task
	grunt.registerTask('install', [ 'bower', 'modernizr', 'less:compileCore' ]);

	// Register default task
	grunt.registerTask('require', [ 'requirejs' ]);

	// Register default task
	grunt.registerTask('build', [ 'bower', 'less:compileCore', 'modernizr', 'requirejs', 'compress' ]);

};