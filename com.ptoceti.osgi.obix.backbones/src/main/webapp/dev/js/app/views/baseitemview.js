define([ 'backbone', 'marionette', 'underscore', 'jquery', 'models/obix', 'mediaenquire', 'modelbinder', 'courier', 'numeral', 'moment', "i18n!nls/unittext", "i18n!nls/statustext", "i18n!nls/itemtext",'bootstrap', 'jquery.enterkeyevent' ], function(Backbone, Marionette, _, $, Obix, mediaEnquire, ModelBinder, Courier, Numeral, Moment, unitText, statusText,itemText) {
	
	var BaseItemView = Backbone.Marionette.Layout.extend({
	
		templateHelpers :  {
			elemId : function() {
				return this.href.getVal().replace(/\//gi,"-");
			},
			contentEditable : function() {
				return Modernizr.contenteditable;
			},
			onXsMedia : function() {
				return this.onXsMedia;
			},
			itemtext : this.onXsMedia ? itemText.itemtextxs : itemText.itemtext
		},
		
		regions: {
			childRegion : "[name=\"childPanel\"]"
		},
		
		ui : {
			infosCollapsePanel : "[name=\"infoPanel\"]",
			childCollapsePanel : "[name=\"childPanel\"]",
			childCollapseControlElem : "[name=\"childCollapseControl\"]",
			recordItem : "[name=\"recordItem\"]",
			alarmItem : "[name=\"alarmItem\"]",
			childCollapseItem : "[name=\"childCollapseItem\"]"
		},
		
		// setup lister for pur DOM event handling
		events : {
			"click [name='listItem']" : "itemSelected",
			"click [name='deleteItem']" : "onItemDelete",
			"click [name='recordItem']" : "onItemRecord",
			"click [name='alarmItem']" : "onItemAlarm",
			"click [name='childCollapseItem']" : "onChildCollapse",
			"hidden.bs.collapse [name='childPanel']" : "onChildCollapsed",
			"show.bs.collapse [name='childPanel']" : "onChildShow",
			"shown.bs.collapse [name='childPanel']" : "onChildShown"
		},
		
		// pass all messages from subview to parent view
		passMessages : {
			"*" : '.'
		},
		
		initialize : function() {
			// add this view to Backbone.Courier
			Courier.add(this);
			this.on('itemUnselected', this.itemUnselected, this);
			this.model.on('change:displayName', this.saveChanges, this);
			// initialize Backbone.ModelBinder for dual binding
			this.modelbinder = new ModelBinder();
			
			_.bindAll(this, 'onChildCollapsed','onChildShown', 'onChildShow', 'enterXs','quitXs');
			
			this.onXsMedia = false;
			this.xsQueryHandler = {match : this.enterXs, unmatched: this.quitXs};
			mediaEnquire.registerXs(this.xsQueryHandler);
			
		},
		
		// event handler called after the view has been closed
		onClose : function() {
			this.modelbinder.unbind();
			this.off('itemUnselected', this.itemUnselected, this);
			this.stopListening(this.model, "change:val", this.onChangeValHistory);
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
				model.save();
			}
		},
		
		
		onItemRecord : function(event){
			this.spawn("itemRecord", {point: this.model});
			event.stopImmediatePropagation();
		},
		
		onItemAlarm : function(event){
			this.spawn("itemAlarm", {point: this.model});
			event.stopImmediatePropagation();
		},
		
		onItemDelete : function(event){
			this.spawn("itemDelete", {point: this.model});
			event.stopImmediatePropagation();
		},
		
		onChildCollapse : function(event){
			
		},
		
		itemUnselected : function(){
			if( this.$el.hasClass("bg-selected")){
				this.$el.removeClass("bg-selected");
				this.ui.infosCollapsePanel.collapse('hide');
				this.ui.childCollapsePanel.collapse('hide');
			}
		},
		
		itemSelected : function(event){
			if( this.$el.hasClass("bg-selected")){
				//this.ui.infosCollapsePanel.collapse('hide');
				//this.ui.childCollapsePanel.collapse('hide');
				
				//this.$el.removeClass("bg-selected");
				// setup view event to clear selection
				//this.spawn("listItemSelected", {point: null});
			}
			else {
				//$(".listItem ").removeClass("active");
				this.trigger("siblingItem:Unselect", '', this);
				this.$el.addClass("bg-selected");
				this.ui.infosCollapsePanel.collapse('show');
				//this.ui.childCollapsePanel.collapse('show');
				// setup view event to indicate selection
				this.spawn("listItemSelected", {point: this.model});
			}
		},
		
		childSizeChanged : function (model, collection, options){
			if( collection.length > 0){
				this.ui.childCollapseItem.removeClass('hidden');
			} else {
				this.ui.childCollapseItem.addClass('hidden');
			}
		},
		
		recordItemListener: function (model, collection, options){
			if( collection.length > 0){
				if( collection.getByName("history")){
					this.ui.recordItem.addClass('hidden');
				} else {
					this.ui.recordItem.removeClass('hidden');
				}
			} else {
				this.ui.recordItem.removeClass('hidden');
			}
		},
		
		alarmItemListener: function (model, collection, options){
			if( collection.length > 0){
				if( collection.getByName("alarm")){
					this.ui.alarmItem.addClass('hidden');
				} else {
					this.ui.alarmItem.removeClass('hidden');
				}
			} else {
				this.ui.alarmItem.removeClass('hidden');
			}
		},
		
		collapseItemConverter : function(direction, value){
			if(direction == 'ModelToView'){
				if(value && value.length > 0 ){
					return "h4";
				} else {
					return "h4 hidden";
				}
			}
		},
		
		recordItemConverter : function(direction, value){
			if(direction == 'ModelToView'){
				if(value && value.length > 0 ){
					if( value.getByName("history")){
						return "hidden";
					} else {
						return "";
					}
				} else {
					return "";
				}
			}
		},
		
		alarmItemConverter : function(direction, value){
			if(direction == 'ModelToView'){
				if(value && value.length > 0 ){
					if( value.getByName("alarm")){
						return "hidden";
					} else {
						return "";
					}
				} else {
					return "";
				}
			}
		},
		
		unitConverter : function(direction, value){
			if(direction == 'ModelToView'){
				var unitContract = value.getVal();
				if( unitContract.lastIndexOf("obix:Unit/") > -1){
					return unitText[unitContract.substr(unitContract.lastIndexOf('/') + 1)];
				}
			}
		},
		
		nameConverter : function(direction, value, attributeName, model){
			if( direction =='ModelToView') {
				if(model.getDisplayName() == '') return value;
				else return model.getDisplayName();
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
		
		varianceConverter : function(direction, value, attributeName, model) {
			if( direction == 'ModelToView'){
				if( model.previousAttributes().hasOwnProperty('val')){
					var previousVal = model.previousAttributes().val;
					if( previousVal > value) return "glyphicon glyphicon-arrow-up";
					if( previousVal < value) return "glyphicon glyphicon-arrow-down";
					else return "{display: none}";
				} else
					return "{display: none}";
			}
		},
		
		/**
		 * Event handler called once the detail panel has fully collapse
		 */
		onChildCollapsed : function() {
			this.ui.childCollapseControlElem.removeClass('glyphicon-menu-up');
			this.ui.childCollapseControlElem.addClass('glyphicon-menu-down');
		},
		
		/**
		 * Event handler called immediately after the show method on the details panel has been called.
		 */
		onChildShow : function() {
			// triggger initial loading of rollup history
			//this.onChangeValHistory();
			var region = this.childRegion;
			var childCollection = this.model.getChildrens();
			
			var requiredModules = ['views/childsitemview'];
			_.each(childCollection.models, function(element,index) {
				var nextView;
				var found = false;
				if( element.getIs() != undefined){
					if( element.hasContract('obix:Point')) {
						nextView = 'views/pointitemview';
						found = true;
					}
					if( element.hasContract('ptoceti:MeasurePoint')) {
						nextView = 'views/pointitemview';
						found = true;
					}
					if( element.hasContract('ptoceti:MonitoredPoint')) {
						nextView = 'views/monitoreditemview';
						found = true;
					}
					if( element.hasContract('ptoceti:ReferencePoint')) {
						nextView = 'views/referenceitemview';
						found = true;
					}
					if( element.hasContract('ptoceti:DigitPoint')) {
						nextView = 'views/stateitemview';
						found = true;
					}
					if( element.hasContract('ptoceti:SwitchPoint')) {
						nextView = 'views/switchitemview';
						found = true;
					}
					
				} 
				
				if( !found && element.attributes.type && element.attributes.type == 'ref' ) {
					nextView = 'views/refitemview';
					found = true;
				} 
				
				if( !found){
					nextView = 'views/blankitemview';
				}
				if( requiredModules.indexOf(nextView) < 0){
					requiredModules.push(nextView);
				}
			});
			
			require(requiredModules, function(ChildsItemView){
				if( region.currentView == null){
					region.show(new ChildsItemView({collection: childCollection}));
				} 
			});
		},
		
		/**
		 * Event handler called once the details panel is fully shown
		 */
		onChildShown : function() {
			this.ui.childCollapseControlElem.removeClass('glyphicon-menu-down');
			this.ui.childCollapseControlElem.addClass('glyphicon-menu-up');
			
		},
		
		
		lastTimeStamp : function(direction, value, attributeName, model ) {
			if( direction == "ModelToView") {
				var lastTimeStamp = model.get('updateTimeStamp');
				if( lastTimeStamp != null)
					return lastTimeStamp.toLocaleTimeString();
				
			}
		}
	});
	
	return BaseItemView;
});
