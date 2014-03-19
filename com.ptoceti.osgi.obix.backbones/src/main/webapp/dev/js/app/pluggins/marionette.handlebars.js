/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : Ptoceti
 * PROJECT : Obix Backbones
 * FILENAME : marionette.handlebars.js
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
 * Usage: Just include this script after Marionette and Handlebars loading IF
 * you use require.js add script to shim and describe it in the requirements
 */
define(['handlebars', 'marionette'], function(Handlebars, Marionette) {

	Marionette.Handlebars = {
		path : 'app/templates/',
		extension : '.handlebars'
	};

	Marionette.TemplateCache.prototype.load = function() {
		if (this.compiledTemplate) {
			return this.compiledTemplate;
		}
		if (Handlebars.templates && Handlebars.templates[this.templateId]) {
			this.compiledTemplate = Handlebars.templates[this.templateId];
		} else {
			var template = this.loadTemplate(this.templateId);
			this.compiledTemplate = this.compileTemplate(template);
		}
		return this.compiledTemplate;
	};

	Marionette.TemplateCache.prototype.loadTemplate = function(templateId) {
		var template, templateUrl;
		try {
			template = Marionette.$(templateId).html();
		} catch (e) {
		}
		if (!template || template.length === 0) {
			templateUrl = Marionette.Handlebars.path + templateId
					+ Marionette.Handlebars.extension;
			Marionette.$.ajax({
				url : templateUrl,
				success : function(data) {
					template = data;
				},
				async : false
			});
			if (!template || template.length === 0) {
				throw "NoTemplateError - Could not find template: '"
						+ templateUrl + "'";
			}
		}
		return template;
	};

	Marionette.TemplateCache.prototype.compileTemplate = function(rawTemplate) {
		return Handlebars.compile(rawTemplate);
	};
	
	 /*
	* Use this to turn on logging: (in your local extensions file)
	*/
	Handlebars.logger.log = function(level) {
		if(level >= Handlebars.logger.level) {
		console.log.apply(console, [].concat(["Handlebars: "], _.toArray(arguments)));
		}
	};
	// DEBUG: 0, INFO: 1, WARN: 2, ERROR: 3,
	Handlebars.registerHelper('log', Handlebars.logger.log);
	// Std level is 3, when set to 0, handlebars will log all compilation results
	Handlebars.logger.level = 2;
	 
	/*
	* Log can also be used in templates: '{{log 0 this "myString" accountName}}'
	* Logs all the passed data when logger.level = 0
	*/

});
