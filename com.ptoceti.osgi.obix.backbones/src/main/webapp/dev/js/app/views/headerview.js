/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : Ptoceti
 * PROJECT : Obix Backbones
 * FILENAME : headerview.js
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
define([ 'backbone', 'marionette', 'underscore', 'jquery', 'eventaggr', 'moment', 'mediaenquire', "i18n!nls/lobbytext", 'bootstrap' ], function(Backbone, Marionette, _, $, ventAggr, Moment,
		mediaEnquire, localizedLobbyText) {

	var HeaderView = Marionette.ItemView.extend({

		template : 'header',
		// el: $("#header"),

		ui : {
			//serverTimeElem : "#serverTime",
			//localTimeElem : "#localTime",
			lobbyMenu : "#lobbyMenu",
			watchesMenu : "#watchesMenu",
			historyMenu : "#historyMenu"
		},

		
		events : {
			"click #lobbyMenu" : "lobbyMenuClicked",
			"click #watchesMenu" : "watchesMenuClicked",
			"click #historyMenu" : "historyMenuClicked",
			"click #logout" : "goToIntro"
			//"click .navbar-collapse.in" : function(e) {if($(e.target).is('a')){$(this).collapse('hide');}}
		},

		initialize : function() {
			this.schedulesUpdateTimes = setInterval(_.bind(this.updateTimes, this), 1000);
			
			this.serverTimeOffsetMillis = Moment().diff(Moment(this.model.getServerTime().getVal()));
			
			_.bindAll(this, 'enterXs','quitXs');
			
			this.onXsMedia = false;
			this.xsQueryHandler = {match : this.enterXs, unmatch: this.quitXs};
			mediaEnquire.registerXs(this.xsQueryHandler);
		},

		templateHelpers : function() {
			return {
				//localTime :  this.onXsMedia ?  Moment().format("ddd, H:mm") : Moment().format("dddd, MMM, H:mm"),
				//serverTime : Moment().add('ms',this.serverTimeOffsetMillis).format("H:mm:s"),
				lobbytext : localizedLobbyText.lobbytext
			};
		},

		onRender : function() {
			this.shiftSeconds = 1;
		},

		onclose : function() {
			clearinterval(this.schedulesUpdateTimes);
			mediaEnquire.unregisterXs(this.xsQueryHandler);
		},
		
		enterXs : function(){
			this.onXsMedia = true;
		},
		
		quitXs : function(){
			this.onXsMedia = false;
		},

		updateTimes : function() {
			if (typeof this.ui.localTimeElem !== 'undefined' && typeof this.ui.serverTimeElem !== 'undefined') {
				this.ui.localTimeElem.text(this.onXsMedia ?  Moment().format("ddd, H:mm") : Moment().format("dddd, MMM, H:mm"));
				this.ui.serverTimeElem.text( Moment().add('ms',this.serverTimeOffsetMillis).format("H:mm:s"));
			}
		},

		goToIntro : function(event){
			ventAggr.trigger("app:goToIntro");
		},
		
		lobbyMenuClicked : function() {
			this.ui.lobbyMenu.parent().addClass('active');
			this.ui.watchesMenu.parent().removeClass('active');
			this.ui.historyMenu.parent().removeClass('active');
			
			ventAggr.trigger("app:goToLobby");
			
		},

		watchesMenuClicked : function() {
			this.ui.lobbyMenu.parent().removeClass('active');
			this.ui.watchesMenu.parent().addClass('active');
			this.ui.historyMenu.parent().removeClass('active');
			ventAggr.trigger("app:goToWatches");
		},
		
		historyMenuClicked : function() {
			this.ui.lobbyMenu.parent().removeClass('active');
			this.ui.watchesMenu.parent().removeClass('active');
			this.ui.historyMenu.parent().addClass('active');
			ventAggr.trigger("app:goToHistories");
		}

	});

	return HeaderView;
});
