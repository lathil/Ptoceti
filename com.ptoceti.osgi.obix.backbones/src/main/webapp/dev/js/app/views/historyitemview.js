define([ 'backbone', 'marionette', 'underscore', 'jquery', 'models/obix', 'models/historyrollupoutlocal', 'eventaggr','oauth2', 'modelbinder', 'courier', 'numeral', 'moment', 'bootstrap' ], function(Backbone, Marionette, _, $, Obix, HistoryRollupOutLocal, ventAggr, oauth2, ModelBinder, Courier, Numeral, Moment) {
	
	var HistoryItemView = Backbone.Marionette.Layout.extend({
		tagName: "div",
		template: "historyitem",
		className: "item",
	
		templateHelpers :  {
			elemId : function() {
				return this.history.getHref().getVal().replace(/\//gi,"-");
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
			nextPeriodItem : "[name=\"nextPeriod\"]",
			previousPeriodItem : "[name=\"previousPeriod\"]",
			spin : "[name=\"spin\"]"
		},
		
		// setup lister for pur DOM event handling
		events : {
			"click [name='listItem']" : "itemSelected",
			"click [name='deleteItem']" : "onItemDelete",
			"hidden.bs.collapse [name='childPanel']" : "onChildCollapsed",
			"show.bs.collapse [name='childPanel']" : "onChildShow",
			"shown.bs.collapse [name='childPanel']" : "onChildShown",
			"click [name='nextPeriod']" : "onNextPeriod",
			"click [name='previousPeriod']" : "onPreviousPeriod",
		},
		
		regions: {
			rollUpRegion : "[name=\"rollupPanel\"]"
		},
		
		initialize : function() {
			// add this view to Backbone.Courier
			Courier.add(this);
			// initialize Backbone.ModelBinder for dual binding
			this.historybinder = new ModelBinder();
			// indicate that the contents must be shown once the rollup has been retrieved.
			this.mustShowContents = false;
			
			_.bindAll(this, 'showContents','onChildCollapsed','onChildShown', 'onChildShow');
			
			this.on('itemUnselected', this.itemUnselected, this);
			this.model.getHistory().on('change:displayName', this.saveChanges, this);
			
			//this.model.getHistory().getRollupOp()
			
			var localHistoryRollup = HistoryRollupOutLocal.get(this.model.getHistory().getRollupOp().getHref().getVal());
			if( localHistoryRollup ){
				this.model.setRollUp(localHistoryRollup);
			}
			
			ventAggr.on(this.model.getHistory().getHref().getVal().replace(/\//gi,"-") + ":updateRollup", this.loadLast1HRollUp, this);

		},
		
		// event handler called after the view has been closed
		onClose : function() {
			this.model.getRollUp().save();
			ventAggr.off(this.model.getHistory().getHref().getVal().replace(/\//gi,"-") + ":updateRollup", this.loadLast1HRollUp, this);
			this.off('itemUnselected', this.itemUnselected, this);
			this.historybinder.unbind();
			
		},
		
		/**
		 * Detect changes in the model from view, save them back to server
		 */
		saveChanges : function(model, value, options){
			// model bindel tag its change from the view by setting options.changeSource
			if( options.changeSource == 'ModelBinder'){
				console.log("saveChanges");
				model.getHistory().save();
			}
		},
		
		// event handler call after the view has been rendered
		onRender : function(){
			this.historybinder.bind(this.model.getHistory(), this.el, {
				displayName: {selector: '[name=displayName]', converter: this.nameConverter}
			}, {'changeTriggers': {'': 'change', '[contenteditable]': 'enterpress'}}  );
			
			// if rollup was not retrieved from local storage
			if( !this.model.getRollUp()){
				//... go get it
				this.loadLast24HRollUp();
			} else {
				// rollup end there
				var endRecord = Moment(this.model.getRollUp().getEnd().getVal());
				var now = Moment();
				
				var duration = Moment.duration(now.diff(endRecord));
				var hours = duration.asHours();
				
				if(hours < 1){
					this.loadLast1HRollUp();
				} else if( hours > 24){
					this.loadLast24HRollUp();
				} else {
					this.loadRollUp(endRecord, now, 24);
				}
			}
		},
		
		onItemDelete : function(event){
			this.spawn("historyItemDelete", {history: this.model.getHistory()});
			event.stopImmediatePropagation();
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
				this.spawn("historyItemSelected", {history: null});
			}
			else {
				//$(".watchItem ").removeClass("active");
				this.trigger("siblingItem:Unselect", '', this);
				this.$el.addClass("bg-selected");
				this.ui.infosCollapsePanel.collapse('show');
				// setup view event to indicate selection
				this.spawn("historyItemSelected", {history: this.model});
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
			this.updateRollUpView();
		},
		
		onNextPeriod : function(event){
			if( this.model.getRollUp()){
				
				var endRecord = Moment(this.model.getRollUp().getEnd().getVal());
				if( endRecord.isSame(this.endChartDate)) {
					// get newer history from rest service
					var start = endRecord.clone();
					var end =  Moment(start).add(24,'hours');
					// update displayable periode.
					//this.endChartDate = Moment(end);
					//this.startChartDate = Moment(start);
					// Get missing data
					this.loadRollUp(start, end, 24);
				} else if( endRecord.isAfter( this.endChartDate)) {
					// end of history is after end of display chart, we can go forward a bit
					var nextEndChartDate  = Moment(this.endChartDate).add(24,'hours');
					if( endRecord.isAfter(nextEndChartDate)){
						this.endChartDate = nextEndChartDate;
					} else {
						this.endChartDate = endRecord;
					}
					
					this.startChartDate = Moment(this.endChartDate).subtract(24,'hours');
					this.chartDateChanged = true;
					this.updateRollUpView();
				}
			}
			event.stopImmediatePropagation();
		},
		
		onPreviousPeriod : function(event){
			if( this.model.getRollUp()){
				
				var startRecord = Moment(this.model.getRollUp().getStart().getVal());
				if( startRecord.isSame(this.startChartDate)) {
					// get older history from rest service
					 var end = startRecord.clone();
					 var start = Moment(end).subtract(24,'hours');
					 // update displayable periode.
					 //this.endChartDate = Moment(end);
					 //this.startChartDate = Moment(start);
					// Get missing data
					 this.loadRollUp(start, end, 24);
					 
				} else if ( startRecord.isBefore( this.startChartDate)){
					// start of history is before start of display chart, we can go back a bit
					var previousStartChartDate = Moment(this.startChartDate).subtract(24,'hours');
					if( startRecord.isBefore (previousStartChartDate)){
						this.startChartDate = previousStartChartDate;
					} else {
						this.startChartDate = startRecord;
					}
					
					this.endChartDate = Moment(this.startChartDate).add(24,'hours');
					this.chartDateChanged = true;
					this.updateRollUpView();
				}
			}
			event.stopImmediatePropagation();
		},
		
		/**
		 * Load a rollup of the last 24 Hour
		 * 
		 */
		loadLast24HRollUp : function () {
			
			var now = Moment();
			now.millisecond(0);
			
			// keep track of current hours
			var end =  Moment(now);
			end.add(1,'hours');
			end.minute(0);
			end.second(0);
			end.millisecond(0);
			
			
			// fisrt resquest 24 hours, after only 1 hours
			var start = Moment(end).subtract(24,'hours');
			// ask for plain hours
			start.minute(0);
			start.second(0);
			start.millisecond(0);
			
			this.loadRollUp(start, end, 24);
		},
		
		/**
		 * Load a rollup of the last 1 Hour
		 * 
		 */
		loadLast1HRollUp: function(){
			
			var now = Moment();
			now.millisecond(0);
			
			// keep track of current hours
			var end = Moment(now);
			end.add(1,'hours');
			end.minute(0);
			end.second(0);
			end.millisecond(0);
			
			
			// request only last hour
			var start = Moment(end).subtract(1,'hours');
			// ask for plain hours
			start.minute(0);
			start.second(0);
			start.millisecond(0);
			
			this.loadRollUp(start, end, 1);
		},
		
		/**
		 * Make a request to history object link to this monitoredPoint to get a rollup of data for the last 24 hours. On success
		 * triger a update of the rollup data view.
		 */
		loadRollUp : function(start, end, count) {
			
			if( this.model){
				var rollUpIn = new Obix.historyRollupIn();
				rollUpIn.getLimit().setVal(count);
				
				rollUpIn.getStart().setVal(start.toISOString());
				rollUpIn.getEnd().setVal(end.toISOString());
				rollUpIn.getInterval().setVal( Moment.duration(1,'hours').toIsoString());
				
				console.log('request history rollup');
				
				var historyrollup  = new Obix.historyRollupOut({}, {
					urlRoot : this.model.getHistory().urlRoot
				});
				
				this.model.getHistory().getRollupOp().invoke(rollUpIn, historyrollup, {
					headers: oauth2.getAuthorizationHeader(),
					success : _.bind( this.historyRollUpUpdate, this )
				});
			}
		},
		
	
		/**
		 * Update internal list of rollup records with new one. Insert new records at the right place depending of their start en and time stamps.
		 * 
		 */
		historyRollUpUpdate : function(historyRollupOut, response, options) {
			var region = this.rollUpRegion;
			
			if( historyRollupOut && historyRollupOut.getData().getChildrens().length > 0) {
				
				var rollUpStart = Moment( historyRollupOut.getStart().getVal());
				var rollUpEnd = Moment( historyRollupOut.getEnd().getVal());
				
				if( !this.model.getRollUp()){
					
					HistoryRollupOutLocal.create( historyRollupOut);
					this.model.setRollUp(historyRollupOut);
					
					this.startChartDate = Moment( rollUpStart);
					this.endChartDate = Moment( rollUpEnd);
					
				} else {
					
					// iterate over each rollup record from the resource response ...
					_.each(historyRollupOut.getData().getChildrens().models, function(updatedrecord, updatedindex, updatedlist) {
						
						var updatedstart =  Moment(updatedrecord.getStart().getVal());
						var updatedend = Moment(updatedrecord.getEnd().getVal())
						
						var afterIndex = -1;
						var beforeIndex = -1;
						
						// ... and find where it fits in the model existing list of rollups
						_.each(this.model.getRollUp().getData().getChildrens().models, function(currentrecord, currentindex, currentlist) {
							
							var currentstart =  Moment(currentrecord.getStart().getVal())
							var currentend = Moment(currentrecord.getEnd().getVal())
							
							// updated sample is after current one
							if( updatedstart.isSame(currentend) || updatedstart.isAfter(currentend)) {
								if( afterIndex < currentindex) {
									// find the last record after
									afterIndex = currentindex;
								} 
							}
							// updates sample is before current one
							if( updatedend.isSame(currentstart) || updatedend.isBefore(currentstart)) {
								if( beforeIndex == -1 ) {
									// find the first record before
									beforeIndex = currentindex;
								} 
							}
							// updated sample is same as current
							if( updatedstart.isSame(currentstart) && updatedend.isSame(currentend)) {
								afterIndex = currentindex;
								beforeIndex = currentindex;
							}
							
				    	},this);
						
						if( afterIndex == beforeIndex){
							// replace in place
							this.model.getRollUp().getData().getChildrens().add(updatedrecord, {at:afterIndex, merge:true});
						} else if( beforeIndex == -1 && afterIndex > 0) {
							// insert at end of collection
							this.model.getRollUp().getData().getChildrens().add(updatedrecord, {at:this.model.getRollUp().getData().getChildrens().length, merge:false});
							this.model.getRollUp().getEnd().setVal(updatedrecord.getEnd().getVal());
						} else if( beforeIndex == 0 && afterIndex == -1) {
							// insert at start of collection
							this.model.getRollUp().getData().getChildrens().add(updatedrecord, {at:0, merge:true});
							this.model.getRollUp().getStart().setVal(updatedrecord.getStart().getVal());
						} else {
							// insert in collection
							this.model.getRollUp().getData().getChildrens().add(updatedrecord, {at:afterIndex + 1 });
						}
						
			    	},this);
					
					// update displayable area.
					if( rollUpStart.isBefore(this.startChartDate)) {
						this.startChartDate = Moment(this.model.getRollUp().getStart().getVal());
						this.endChartDate = Moment(this.startChartDate).add(24,'hours');
						this.chartDateChanged = true;
					} else if ( rollUpEnd.isAfter(this.endChartDate)){
						this.endChartDate = Moment(this.model.getRollUp().getEnd().getVal());
						this.startChartDate = Moment(this.endChartDate).subtract(24,'hours');
						this.chartDateChanged = true;
					} else {
						this.chartDateChanged = false;
					}
				}
			}
			
			// if the chevron to show the rollup panel is hidden
			if(this.ui.childCollapseItem.hasClass('hidden')){
				//.. make it visible  so that user can unfold it the rollup panel
				this.ui.childCollapseItem.removeClass('hidden');
				
				
				if( this.mustShowContents ){
					//this.ui.bootstrapCollapseItem.collapse('show');
					this.ui.bootstrapCollapseItem.click();
					this.mustShowContents = false;
				}
				
				if(!this.ui.spin.hasClass('hidden')){
					this.ui.spin.addClass('hidden');
				}
				
				
				
			} else {
				// else the panel is visible, update the rollup panel with fresh data
				this.updateRollUpView();
			}
			
		},
		
		/**
		 * Setup the rollup region if it has not yet been done. Push to collection of rollup records.
		 * Setup timer to create update event.
		 * 
		 */
		updateRollUpView : function(){
			var region = this.rollUpRegion;
			
			
			var rollupRecords = this.model.getRollUp().getData().getChildrens().filter(function(rollupRecord){
				var recordStart = Moment(rollupRecord.getStart().getVal());
				var recordEnd = Moment(rollupRecord.getEnd().getVal());
				
				if((recordStart.isSame(this.startChartDate) || recordStart.isAfter(this.startChartDate) && recordStart.isBefore( this.endChartDate))
					 && (recordEnd.isSame(this.endChartDate) || recordEnd.isBefore(this.endChartDate) && recordEnd.isAfter( this.startChartDate))){
					return true;
				}
				
				return false;
			}, this);
			
			
			require(['views/historyrollupview'], function(HistoryRollUpView){
				if( region.currentView == null){
					region.show(new HistoryRollUpView({collection: new Backbone.Collection(rollupRecords)}));
				} else {
					region.currentView.updateItemValues(new Backbone.Collection(rollupRecords));
				} 
			});
			
			// ensure we do not have another timeout already set
			if( this.updateRollupTimer != null ) clearTimeout(this.updateRollupTimer);
			this.updateRollupTimer = setTimeout( _.bind(function(){
				ventAggr.trigger( this.model.getHistory().getHref().getVal().replace(/\//gi,"-") + ":updateRollup");
			}, this), 30000);
		},
		
		
	});
	
	return HistoryItemView;
});