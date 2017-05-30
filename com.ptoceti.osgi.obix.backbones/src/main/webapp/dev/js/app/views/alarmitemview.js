define([ 'backbone', 'marionette', 'underscore', 'jquery', 'models/obix', 'eventaggr', 'oauth2', 'modelbinder', 'courier', 'moment', 'mediaenquire', 'modernizr',  "i18n!nls/alarmtext", "i18n!nls/statustext", 'bootstrap', 'jquery.enterkeyevent' ],
		function(Backbone, Marionette, _, $, Obix, ventAggr, oauth2, ModelBinder, Courier, Moment, mediaEnquire, Modernizr,  alarmText, statusText) {
	
	var AlarmItemView = Backbone.Marionette.Layout.extend({
		tagName: "div",
		template: "alarmitem",
		className: "item",
	
		templateHelpers : {
			alarmtext : alarmText.alarmtext,
			elemId : function() {
				return this.alarm.getHref().getVal().replace(/\//gi,"-");
			},
			contentEditable : function() {
				return Modernizr.contenteditable;
			}
		},
		
		ui : {
			infosCollapsePanel : "[name=\"infoPanel\"]",
			childCollapseControlElem : "[name=\"childCollapseControl\"]",
			childCollapseItem : "[name=\"childCollapseItem\"]",
			bootstrapCollapseItem : "[name=\"bootstrapCollapseItem\"]",
			maxPanel : "[name=\"maxPanel\"]",
			minPanel : "[name=\"minPanel\"]",
			timeStampPanel : "[name=\"timeStampPanel\"]",
			ackTimeStampPanel : "[name=\"ackTimeStampPanel\"]",
			normalTimeStampPanel : "[name=\"normalTimeStampPanel\"]",
			ackButtonPanel : "[name=\"ackButtonPanel\"]",
			ackButton : "[name=\"ackButton\"]"
		},
		
		// setup lister for pur DOM event handling
		events : {
			"click [name='listItem']" : "itemSelected",
			"click [name='maxval']" : "onMaxValClick",
			"click [name='minval']" : "onMinValClick",
			"click [name='deleteItem']" : "onItemDelete",
			"click [name='deleteItem']" : "onItemDelete",
			"click [name='ackButton']" : "onAcknowledge",
			"hidden.bs.collapse [name='childPanel']" : "onChildCollapsed",
			"show.bs.collapse [name='childPanel']" : "onChildShow",
			"shown.bs.collapse [name='childPanel']" : "onChildShown",
		},
		
		regions: {
			rollUpRegion : "[name=\"rollupPanel\"]"
		},
		
		initialize : function() {
			// add this view to Backbone.Courier
			Courier.add(this);
			// initialize Backbone.ModelBinder for dual binding
			this.alarmbinder = new ModelBinder();
			
			this.timestampbinder = new ModelBinder();
			this.acktimestampbinder = new ModelBinder();
			this.normaltimestampbinder = new ModelBinder();
			this.minmodelbinder = new ModelBinder();
			this.maxmodelbinder = new ModelBinder();
			// indicate that the contents must be shown once the rollup has been retrieved.
			this.mustShowContents = false;
			
			_.bindAll(this, 'showContents','onChildCollapsed','onChildShown', 'onChildShow', 'alarmAcked');
			
			this.onXsMedia = false;
			this.xsQueryHandler = {match : this.enterXs, unmatch: this.quitXs};
			mediaEnquire.registerXs(this.xsQueryHandler);
			
			this.on('itemUnselected', this.itemUnselected, this);
			this.model.getAlarm().on('change:displayName', this.saveChanges, this);
			
			this.model.getAlarm().getChildrens().on('add', this.onChildrenAdd, this);
			this.model.getAlarm().getChildrens().on('remove', this.onChildrenRemove, this);
			
			ventAggr.on(this.model.getAlarm().getHref().getVal().replace(/\//gi,"-") + ":updateAlarm", this.updateAlarmView, this);
		},
		
		// event handler called after the view has been closed
		onClose : function() {
			ventAggr.off(this.model.getAlarm().getHref().getVal().replace(/\//gi,"-") + ":updateAlarm", this.updateAlarmView, this);
			
			this.model.getAlarm().getChildrens().off('add', this.onChildrenAdd, this);
			this.model.getAlarm().getChildrens().off('remove', this.onChildrenRemove, this);
			
			this.off('itemUnselected', this.itemUnselected, this);
			this.alarmbinder.unbind();
			this.timestampbinder.unbind();
			this.acktimestampbinder.unbind();
			this.normaltimestampbinder.unbind();
			this.minmodelbinder.unbind();
			this.maxmodelbinder.unbind();
			
			mediaEnquire.unregisterXs(this.xsQueryHandler);
		},
		
		enterXs : function(){
			this.onXsMedia = true;
		},
		
		quitXs : function(){
			this.onXsMedia = false;
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
		
		// event handler call after the view has been rendered
		onRender : function(){
			this.alarmbinder.bind(this.model.getAlarm(), this.el, {
				displayName: {selector: '[name=displayName]', converter: this.nameConverter},
				status: [{selector: '[name=status]',  elAttribute: 'class', converter: this.statusClassConverter},
				         {selector: '[name=statusText]', converter: this.statusConverter},
				         {selector: '[name=ackTimeStampPanel]', elAttribute: 'class', converter: this.ackTimeStampClassConverter},
				         {selector: '[name=ackButtonPanel]',  elAttribute: 'class', converter: this.buttonPanelClassConverter},
				         {selector: '[name=timestamp]',  elAttribute: 'style', converter: this.timeStampStyleConverter}]
			}, {'changeTriggers': {'': 'change', '[contenteditable]': 'enterpress'}}  );
			
			this.timestampmodel = this.model.getAlarm().getChildrens().getByName('timestamp');
			if( this.timestampmodel){
				this.timestampbinder.bind(this.timestampmodel, this.el, {
					val: {selector:'[name=timestamp]', converter: this.timeStampConverter}
				});
				
				this.ui.timeStampPanel.removeClass("hidden");
			}
			
			this.normaltimestampmodel = this.model.getAlarm().getChildrens().getByName('normalTimestamp');
			if( this.normaltimestampmodel){
				this.normaltimestampbinder.bind(this.normaltimestampmodel, this.el, {
					val: {selector:'[name=normaltimestamp]', converter: this.timeStampConverter}
				});
				this.ui.normalTimeStampPanel.removeClass("hidden");
			}
			
			this.acktimestampmodel = this.model.getAlarm().getChildrens().getByName('ackTimestamp');
			if( this.acktimestampmodel){
				this.acktimestampbinder.bind(this.acktimestampmodel, this.el, {
					val: {selector:'[name=acktimestamp]', converter: this.timeStampConverter}
				});
				
			}
		
			
			this.minmodel = this.model.getAlarm().getChildrens().getByName('minValue');
			if( this.minmodel){
				this.minmodelbinder.bind(this.minmodel, this.el, {
					val: [{selector: '[name=minval]'} ],
				}, {'changeTriggers': {'': 'enterpress'}} );
							
				this.minmodel.on('change:val', this.saveChanges, this);
				this.ui.minPanel.removeClass("hidden");
			}
			
			this.maxmodel = this.model.getAlarm().getChildrens().getByName('maxValue');
			if( this.maxmodel){
				this.maxmodelbinder.bind(this.maxmodel, this.el, {
					val: [{selector: '[name=maxval]'} ],
				}, {'changeTriggers': {'': 'enterpress'}} );
				
				this.maxmodel.on('change:val', this.saveChanges, this);
				this.ui.maxPanel.removeClass("hidden");
			}
			
		},
		
		onChildrenAdd: function(model, collection, option){
			
			if( model.getName() == 'timestamp'){
				if( !this.timestampmodel){
					this.timestampmodel = model;
					this.timestampbinder.bind(this.timestampmodel, this.el, {
						val: {selector:'[name=timestamp]', converter: this.timeStampConverter}
					});
					
					this.ui.timeStampPanel.removeClass("hidden");
				}
				
			} else if( model.getName() == 'normalTimestamp'){
				if( !this.normaltimestampmodel){
					this.normaltimestampmodel = model;
					this.normaltimestampbinder.bind(this.normaltimestampmodel, this.el, {
						val: {selector:'[name=normaltimestamp]', converter: this.timeStampConverter}
					});
					this.ui.normalTimeStampPanel.removeClass("hidden");
				}
				
			} else if( model.getName() == 'ackTimestamp'){
				if( !this.acktimestampmodel){
					this.acktimestampmodel = model;
					this.acktimestampbinder.bind(this.acktimestampmodel, this.el, {
						val: {selector:'[name=acktimestamp]', converter: this.timeStampConverter}
					});
					
				}
			} 
			
		},
		
		onChildrenRemove: function(model, collection, option){
			if( model.getName() == 'timestamp'){
				if( this.timestampmodel){
					this.timestampbinder.unbind();
					this.ui.timeStampPanel.addClass("hidden");
					this.timestampmodel = null;
				}
				
			} else if( model.getName() == 'normalTimestamp'){
				if( this.normaltimestampmodel){
					this.normaltimestampbinder.unbind();
					this.ui.normalTimeStampPanel.addClass("hidden");
					this.normaltimestampmodel = null;
				}
				
			} else if( model.getName() == 'ackTimestamp'){
				if( this.acktimestampmodel){
					this.acktimestampbinder.unbind();
					this.acktimestampmodel = null;
				}
			} 
		},
		
		onAcknowledge : function(e){
			
			var alarmackin = new Obix.alarmackin();
			var alarmackout = new Obix.alarmackout({}, {
				urlRoot : this.model.getAlarm().urlRoot
			});
			
			
			this.model.getAlarm().getAckOp().invoke(alarmackin, alarmackout, {
				headers: oauth2.getAuthorizationHeader(),
				success : this.alarmAcked,
				error : _.bind(function(model, response, option){
					if (response == 'error' && (( model.status == 403 ) || (model.status == 401))) { // Not authorized
						ventAggr.trigger("oauth2:error");
		            }
				}, this)
			});
			
			e.stopImmediatePropagation(); 
		},
		
		alarmAcked: function(alarmackout, response){
			var ackAlarm = alarmackout.getAlarm();
			this.model.getAlarm().getChildrens().set( ackAlarm.getChildrens().models, {add: true, remove: true, merge : true});
			this.model.getAlarm().setStatus(ackAlarm.getStatus());
			
		},
		
		onItemDelete : function(e){
			this.spawn("alarmItemDelete", {alarm: this.model});
			e.stopImmediatePropagation(); 
		},
		
		onMaxValClick :function(e){
			e.stopImmediatePropagation(); 
		},
		
		onMinValClick :function(e){
			e.stopImmediatePropagation(); 
		},
		
		
		itemUnselected : function(){
			if( this.$el.hasClass("bg-selected")){
				this.$el.removeClass("bg-selected");
				this.ui.infosCollapsePanel.collapse('hide');
			}
		},
		
		itemSelected : function(){
			if( this.$el.hasClass("bg-selected")){
				this.ui.infosCollapsePanel.collapse('hide');
				this.$el.removeClass("bg-selected");
				// setup view event to clear selection
				this.spawn("alarmItemSelected", {alarm: null});
			}
			else {
				
				this.trigger("siblingItem:Unselect", '', this);
				this.$el.addClass("bg-selected");
				this.ui.infosCollapsePanel.collapse('show');
				// setup view event to indicate selection
				this.spawn("alarmItemSelected", {alarm: this.model});
				this.updateAlarmView();
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
		
		buttonPanelClassConverter : function(direction, value) {
			if( direction == "ModelToView") {
				if( value != null) {
					var statustoLower = value.toLowerCase();
					if( statustoLower == Obix.status.DISABLED) return "hidden";
					if( statustoLower == Obix.status.FAULT) return "hidden";
					if( statustoLower == Obix.status.DOWN) return "hidden";
					if( statustoLower == Obix.status.UNACKEDALARM) return "show";
					if( statustoLower == Obix.status.ALARM) return "hidden";
					if( statustoLower == Obix.status.UNACKED) return "show";
					if( statustoLower == Obix.status.OVERRIDEN) return "hidden";
					if( statustoLower == Obix.status.OK) return "hidden";
				}
			}
		},
		
		ackTimeStampClassConverter : function(direction, value, attributeName, model) {
			if( direction == "ModelToView") {
				if( model.getChildrens().getByName('ackTimestamp')){
					if( value != null) {
						var statustoLower = value.toLowerCase();
						if( statustoLower == Obix.status.DISABLED) return "hidden";
						if( statustoLower == Obix.status.FAULT) return "hidden";
						if( statustoLower == Obix.status.DOWN) return "hidden";
						if( statustoLower == Obix.status.UNACKEDALARM) return "hidden";
						if( statustoLower == Obix.status.ALARM) return "show";
						if( statustoLower == Obix.status.UNACKED) return "hidden";
						if( statustoLower == Obix.status.OVERRIDEN) return "hidden";
						if( statustoLower == Obix.status.OK) return "hidden";
					}
				} else {
					return "hidden";
				}
			}
		},
		
		statusClassConverter : function( direction, value) {
			if( direction == "ModelToView") {
				if( value != null) {
					var statustoLower = value.toLowerCase();
					if( statustoLower == Obix.status.DISABLED) return "glyphicon glyphicon-ban-circle";
					if( statustoLower == Obix.status.FAULT) return "glyphicon glyphicon-alert";
					if( statustoLower == Obix.status.DOWN) return "glyphicon glyphicon-warning-sign";
					if( statustoLower == Obix.status.UNACKEDALARM) return "glyphicon glyphicon-exclamation-sign";
					if( statustoLower == Obix.status.ALARM) return "glyphicon glyphicon-bell";
					if( statustoLower == Obix.status.UNACKED) return "glyphicon glyphicon-exclamation-sign";
					if( statustoLower == Obix.status.OVERRIDEN) return "glyphicon glyphicon-remove-circle";
					if( statustoLower == Obix.status.OK) return "glyphicon glyphicon-ok-circle";
				}
			}
		},
		
		statusConverter : function( direction, value) {
			if( direction == "ModelToView") {
				if( value != null) {
					var statustoLower = value.toLowerCase();
					if( statustoLower == Obix.status.DISABLED) return statusText[statustoLower];
					if( statustoLower == Obix.status.FAULT) return statusText[statustoLower];
					if( statustoLower == Obix.status.DOWN) return statusText[statustoLower];
					if( statustoLower == Obix.status.UNACKEDALARM) return statusText[statustoLower];
					if( statustoLower == Obix.status.ALARM) return statusText[statustoLower];
					if( statustoLower == Obix.status.UNACKED) return statusText[statustoLower];
					if( statustoLower == Obix.status.OVERRIDEN) return statusText[statustoLower];
					if( statustoLower == Obix.status.OK) return statusText[statustoLower];
				}
			}
		},
		
		timeStampConverter : function(direction, value, attributeName, model ) {
			if( direction == "ModelToView") {
				return Moment(value).format(this.onXsMedia ? "ddd, H:mm" : "dddd, MMM, H:mm");
				
			}
		},
		
		timeStampStyleConverter : function(direction, value, attributeName, model ) {
			if( direction == "ModelToView") {
				if( value != null) {
					var statustoLower = value.toLowerCase();
					if( statustoLower == Obix.status.DISABLED) return  "text-decoration:line-through";
					if( statustoLower == Obix.status.FAULT) return "text-decoration:line-through";
					if( statustoLower == Obix.status.DOWN) return "text-decoration:line-through";
					if( statustoLower == Obix.status.UNACKEDALARM) return "";
					if( statustoLower == Obix.status.ALARM) return "";
					if( statustoLower == Obix.status.UNACKED) return "";
					if( statustoLower == Obix.status.OVERRIDEN) return "text-decoration:line-through";
					if( statustoLower == Obix.status.OK) return "text-decoration:line-through";
				}
			}
		},
		
		showContents : function(){
			this.mustShowContents = true;
		},
		
		/**
		 * Event handler called once the detail panel has fully collapse
		 */
		onChildCollapsed : function() {
			this.ui.childCollapseControlElem.removeClass('glyphicon-menu-up');
			this.ui.childCollapseControlElem.addClass('glyphicon-menu-down');
		},
		
		/**
		 * Event handler called once the details panel is fully shown
		 */
		onChildShown : function() {
			this.ui.childCollapseControlElem.removeClass('glyphicon-menu-down');
			this.ui.childCollapseControlElem.addClass('glyphicon-menu-up');
		},
		
		/**
		 * Event handler called immediately after the show method on the details panel has been called.
		 */
		onChildShow : function() {
			
		},
		
		
		/**
		 * Refresh periodically the alarm view
		 * 
		 */
		updateAlarmView : function(){
			
			this.model.getAlarm().fetch({
				headers: oauth2.getAuthorizationHeader(),
			
				error : _.bind(function(model, response, option){
					if (response == 'error' && (( model.status == 403 ) || (model.status == 401))) { // Not authorized
						ventAggr.trigger("oauth2:error");
		            }
					console.log('Error loading alarm');
				}, this)
			});
			
			// ensure we do not have another timeout already set
			if( this.updateAlarmTimer != null ) clearTimeout(this.updateAlarmTimer);
			this.updateAlarmTimer = setTimeout( _.bind(function(){
				ventAggr.trigger( this.model.getAlarm().getHref().getVal().replace(/\//gi,"-") + ":updateAlarm");
			}, this), 30000);
		}
	
	});
	
	return AlarmItemView;
});
