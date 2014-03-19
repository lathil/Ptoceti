/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : Ptoceti
 * PROJECT : Obix Backbones
 * FILENAME : watcheslocal.js
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

define([ 'backbone', 'underscore', 'localstorage', 'models/obix' ], function(Backbone, _, LocalStorage, Obix ) {
	
	/**
	 * Create a backup of user created watches in browset local storage space.
	 */
	var WatchesLocal = Backbone.Collection.extend({
		
		model: Obix.watch,
		
		localStorage: new LocalStorage("watches")
	
	});
	
	return new WatchesLocal();
	
});
