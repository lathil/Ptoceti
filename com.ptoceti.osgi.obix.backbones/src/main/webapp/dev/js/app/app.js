/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : Ptoceti
 * PROJECT : Obix Backbones
 * FILENAME : app.js
 * 
 * This file is part of the Ptoceti project. More information about
 * this project can be found here: http://www.ptoceti.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2013 - 2014 Ptoceti
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * Main application booting module. 
 * 
 */
define([ 'jquery', 'underscore', 'backbone', 'marionette', 'controller', 'router', 'eventaggr',  ], function($, _, Backbone, Marionette, AppController, AppRouter, ventAggr) {

	var app = new Marionette.Application();

	app.root = '/';

	app.restRoot = window.location.protocol + '//' +  window.location.hostname + ':8081/obix/rest/';

	// only one main region in root template.
	app.addRegions({
		rootRegion : '#root'
	});

	// Add initializer to application
	app.addInitializer(function() {
		this.initApp();
	});
	
	// main initializer for the application. Create main application controller, router and root region.
	app.initApp = function() {

		app.controller = new AppController({
			rootRegion : app.rootRegion,
			restRoot : app.restRoot
		});
		app.router = new AppRouter({
			controller : app.controller
		});

	};

	// event handler called after initializer have finished their work. initialize backbone's history
	app.on('initialize:after', function() {
		app.controller.start();
	});

	// event handler called after initialisation phase has completed. Start application. Here setup history and go to lobby page.
	app.on('start', function() {

		// wait till controller has finished initializing views before trigerring redirection to lobby.
		ventAggr.on("controller:viewInitialized", function() {
			Backbone.history.start({
				pushState : false,
				root : app.root
			});
			
			ventAggr.trigger("app:goToLobby");
		});
		
	});

	

	/**
	 * Handles application level gnerated events that indicate to swicth to the lobby view. Check first that we are not on this same view
	 * from Backbones history.
	 * Delegate view switching to applcation controller
	 */
	ventAggr.on("app:goToLobby", function() {
		if( Backbone.history.fragment != "lobby" ) {
			Backbone.history.navigate("lobby", {});
			app.controller.goToLobby();
		}
	});

	/**
	 * Handles application level gnerated events that indicate to swicth to the watches view. Check first that we are not on this same view
	 * from Backbones history.
	 * Delegate view switching to applcation controller
	 */
	ventAggr.on("app.goToWatches", function() {
		if( Backbone.history.fragment != "watches" ) {
			Backbone.history.navigate("watches", {});
			app.controller.goToWatches();
		}
	});
	
	/**
	 * Handle application level generated  events that indicate to switch to the watches details view  ( lobby with selectedView )
	 */
	ventAggr.on("app:goToLobbyWithWatch", function( watchUri) {
		if( Backbone.history.fragment != "lobby" + watchUri ) {
			Backbone.history.navigate("lobby" + watchUri, {});
			app.controller.goToLobbyWithWatch( watchUri );
		}
	});
	
	return app;
});
