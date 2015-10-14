/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : Ptoceti
 * PROJECT : Obix Backbones
 * FILENAME : router.js
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
define([
//librairies
'backbone', 'marionette', ], function(Backbone, Marionette) {

	/**
	 * Main application level Router that handles view swicthing from url changes events in the browser.
	 * 
	 */
	
	var AppRouter = Backbone.Marionette.AppRouter.extend({

		/**
		 *  Map routes to methods that must exists in coontroller injected at creation time.
		 */
		appRoutes : {
			"intro" : "goToIntro",
			"lobby" : "goToLobby",
			"lobby/:watchUri" : "goToLobbyWithWatch",
			"watches" : "goToWatches",
			"history" : "goToHistory"
		},

		// standard routes can bind to AppRouter
		routes : {},

	});

	return AppRouter;

});
