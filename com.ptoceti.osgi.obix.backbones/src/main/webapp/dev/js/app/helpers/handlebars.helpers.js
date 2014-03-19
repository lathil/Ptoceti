/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : Ptoceti
 * PROJECT : Obix Backbones
 * FILENAME : handlebars.helpers.js
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
define(['handlebars'], function(Handlebars) {
	
	// both from http://jsfiddle.net/ambiguous/WNbrL/
	
	Handlebars.registerHelper('times', function(n, options) {
	    var accum = '';
	    for(var i = 1; i < n+1; ++i) {
	    	if (options.data) {
		          data = Handlebars.createFrame(options.data || {});
		          data.index = i;
	    	}
	        accum += options.fn(this,{ data: data });
	    }
	    return accum;
	    
	});
	
	Handlebars.registerHelper('for', function(from, to, incr, options) {
	    var accum = '';
	    for(var i = from; i < to; i += incr)
	        accum += options.fn(this);
	    return accum;
	});
	
	Handlebars.registerHelper('eq', function(a, b, options) {
		if( a == b) {
			return options.fn(this);
		} else {
			return options.inverse(this);
		}
	});
	
});
