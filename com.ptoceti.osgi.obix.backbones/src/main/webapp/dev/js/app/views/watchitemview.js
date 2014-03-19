/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : Ptoceti
 * PROJECT : Obix Backbones
 * FILENAME : watchitemview.js
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
define([ 'backbone', 'marionette', 'underscore', 'jquery', 'models/obix', 'eventaggr', 'modelbinder', 'courier', 'moment', 'modernizr', "i18n!nls/watchtext", 'bootstrap', 'jquery.enterkeyevent' ], function(Backbone, Marionette, _, $, Obix, ventAggr, ModelBinder, Courier, Moment, Modernizr,localizedWatchText) {
	
	var WatchItemView = Backbone.Marionette.ItemView.extend({
		tagName: "tr",
		template: "watchitem",
		className: "watchItem",
	
		// setup lister for pur DOM event handling
		events : {
			"click td" : "itemSelected",
			"click [name=\'details\']" : "onDetailsClicked"
		},
		
		initialize : function() {
			// add this view to Backbone.Courier
			Courier.add(this);
			// initialize Backbone.ModelBinder for dual binding
			this.watchbinder = new ModelBinder();
			this.leaseBinder = new ModelBinder();
			
			this.model.on('change:displayName', this.saveChanges, this);
		},
		
		templateHelpers : {
			watchtext : localizedWatchText.watchtext,
			contentEditable : function() {
				return Modernizr.contenteditable;
			}
		},
		
		/**
		 * Detect changes in the model from view, save them back to server
		 */
		saveChanges : function(model, value, options){
			// model bindel tag its change from the view by setting options.changeSource
			if( options.changeSource == 'ModelBinder'){
				console.log("saveChanges");
				model.save();
			}
		},
		
		// event handler called after the view has been closed
		onClose : function() {
			this.watchbinder.unbind();
			this.leaseBinder.unbind();
		},
		
		// event handler call after the view has been rendered
		onRender : function(){
			this.watchbinder.bind(this.model, this.el, {
				displayName: {selector: '[name=displayName]', converter: this.nameConverter}
			}, {'changeTriggers': {'': 'change', '[contenteditable]': 'enterpress'}}  );
			
			this.leaseBinder.bind(this.model.getLease(), this.el, {
				val: {selector: '[name=lease]', converter: this.leaseConverter}
			});
		},
		
		/**
		 * The user has clicked on right arrow details, we redirect to lobby with this watch.
		 * 
		 */
		onDetailsClicked : function(){
			ventAggr.trigger("app:goToLobbyWithWatch", this.model.getHref().getVal());
		},
		
		itemSelected : function(){
			if( this.$el.hasClass("active")){
				this.$el.removeClass("active");
				// setup view event to clear selection
				this.spawn("watchItemSelected", {watch: null});
			}
			else {
				$(".watchItem ").removeClass("active");
				this.$el.addClass("active");
				// setup view event to indicate selection
				this.spawn("watchItemSelected", {watch: this.model});
			}
		},
		
		nameConverter : function(direction, value, attributeName, model){
			if( direction =='ModelToView') {
				if(value == '' || value == null ) return model.getName();
				else return value;
			} else {
				return value;
			}
		},
		
		leaseConverter : function(direction, value, attributeName, model) {
			if( direction =='ModelToView') {
				if( model.getVal() != null) {
					return Moment.duration(model.getVal()).humanize();
				}
			}
		}
		
	});
	
	return WatchItemView;
});
