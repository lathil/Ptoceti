define([ 'backbone', 'marionette', 'underscore', 'jquery', 'models/obix', 'modelbinder', 'courier', 'd3', "i18n!nls/unittext","i18n!nls/statustext", 'bootstrap' ], function(Backbone, Marionette, _,$, Obix, ModelBinder, Courier, d3, unitText, statusText) {

	var ReferenceItemSvgView = Backbone.Marionette.ItemView.extend({
		tagName : "div",
		template : "referenceitemsvg",
		className: "item",

		templateHelpers : {
			contentEditable : function() {
				return Modernizr.contenteditable;
			}
		},

		ui : {
			roundslider : "[class=\"roundslider\"]",
			infosCollapsePanel : "[name=\"infoPanel\"]"
		},

		// setup lister for pur DOM event handling
		events : {
			"click [name='listItem']" : "itemSelected",
			"click [name='deleteItem']" : "onItemDelete"
		},

		initialize : function() {
			// add this view to Backbone.Courier
			Courier.add(this);
			this.on('itemUnselected', this.itemUnselected, this);
			this.model.on('change:displayName', this.saveChanges, this);
			// initialize Backbone.ModelBinder for dual binding
			this.modelbinder = new ModelBinder();

		},

		// event handler called after the view has been closed
		onClose : function() {
			this.off('itemUnselected', this.itemUnselected, this);
			this.modelbinder.unbind();
		},

		/**
		 * Detect changes in the model from view, save them back to server
		 */
		saveChanges : function(model, value, options) {
			// model bindel tag its change from the view by setting
			// options.changeSource
			if (options.changeSource == 'ModelBinder') {
				model.save();
			}
		},

		// event handler call after the view has been rendered
		onRender : function() {
			
			this.modelbinder.bind(this.model, this.el, {
				//unit : {selector : '[name=unit]',converter : this.unitConverter},
				//val : [ {selector : '[name=val]',  converter: this.valConverter} ],
				name : {selector : '[name=displayName]',converter : this.nameConverter},
				status : [ {selector : '[name=status]',elAttribute : 'class',converter : this.statusClassConverter}, {selector : '[name=statusText]',converter : this.statusConverter} ]
			});
		},

		/**
		 * Called when the view has been rendered and display, eg when placed
		 * inside a region
		 */
		onShow : function() {
			this.makeSlider();
			
		},

		makeSlider : function() {
			this.roundslidersize = this.ui.roundslider.width();
			this.angle = 0;
			this.pie = d3.layout.pie().sort(null);
			this.roundslidercolors = ['#428bca', '#CCCCCC'];
			
			this.arc = d3.svg.arc().innerRadius((this.roundslidersize / 2) - 30).outerRadius(this.roundslidersize / 2);
			this.group = d3.select(this.ui.roundslider[0]).append('svg').attr('width', this.roundslidersize).attr('height', this.roundslidersize).append('g').attr('transform',
					'translate(' + this.roundslidersize / 2 + ',' + this.roundslidersize / 2 + ')');
			// d3 enter mode
			var ratio = 100 * ( parseFloat(this.model.getVal()) - this.model.getMin() ) / (this.model.getMax() - this.model.getMin());
			this.path = this.group.selectAll('path').data(this.pie([ ratio, 100 - ratio ])).enter().append('path').attr('fill', _.bind(function(d, i) {
						return this.roundslidercolors[i];
					}, this)).attr('d', this.arc);
			
			this.circle = this.group.append("circle").attr("r", 15).attr("fill", this.roundslidercolors[0]).attr("cursor", "move")
			  .call( d3.behavior.drag().on('drag', _.bind(function(){
				    var a = this.findAngle(d3.event.x, d3.event.y);
				    this.setAngle(a);
				  },this)).on('dragend', _.bind(function(){
						 this.model.save();
				  }, this))
			  );
			
			this.text = this.group.append("text").attr("class", "h3").attr("text-anchor", "middle");
			
			
			this.setAngle( 360 *  ( parseFloat(this.model.getVal()) - this.model.getMin() ) / (this.model.getMax() - this.model.getMin()));
		},
		
		/**
		 * angle: degree
		 */
		updateSliderValue : function(angle){
			// d3 update mode
			this.path.data(this.pie([ (angle / 360) * 100, ((360 - angle) /360) * 100 ])).attr('d', this.arc);  
			this.model.setVal( (((( this.model.getMax() - this.model.getMin()) * angle ) / 360) + this.model.getMin()).toString() );
			this.text.text(_.bind(function(d){ 
				var value = new Number(this.model.getVal()).toLocaleString( undefined,{minimumFractionDigits: 0, maximumFractionDigits: 2});
				
				var unitContract = this.model.getUnit().getVal();
				var unit;
				if (unitContract.lastIndexOf("obix:Unit/") > -1) {
					unit = unitText[unitContract.substr(unitContract
							.lastIndexOf('/') + 1)];
				}
				
				return value + " " + unit;
			}, this));
		},
		
		setAngle : function(angle) {
			this.path.transition().duration(50).ease("linear").call(
		      this.arcTween,
		      angle,
		      this
		    );
		},
		
		arcTween: function (transition, newAngle, itemView ) {
			transition.attrTween("d", (function(d) {
			    return function(t) {
			      itemView.moveCircle(newAngle);
			      itemView.updateSliderValue(newAngle);
			      return itemView.arc(d);
			    };
			  }));
		},
		
		/** return angle in degres **/
		findAngle : function(x, y) {
			var addAngle = x < 0 ? 270 : 90;
			return (Math.atan(y/x) * 180 / Math.PI) + addAngle; 
		},
		
		
		/** angle in degres **/
		moveCircle: function(angle) {
			var r = (this.roundslidersize)/2 - 15;
			var x = r * Math.sin(angle * Math.PI / 180);
			var y = -r * Math.cos(angle * Math.PI / 180);
			this.circle.attr("cx", x).attr("cy", y);
		},

		onItemDelete : function(event){
			this.spawn("itemDelete", {point: this.model});
			event.stopImmediatePropagation();
		},
		
		itemUnselected : function(){
			if( this.$el.hasClass("active")){
				this.$el.removeClass("active");
				this.ui.infosCollapsePanel.collapse('hide');
			}
		},
		
		itemSelected : function() {
			if (this.$el.hasClass("active")) {
				this.ui.infosCollapsePanel.collapse('hide');
				this.$el.removeClass("active");
				// setup view event to clear selection
				this.spawn("listItemSelected", {
					point : null
				});
			} else {
				//$(".listItem ").removeClass("active");
				this.trigger("siblingItem:Unselect", '', this);
				this.$el.addClass("active");
				this.ui.infosCollapsePanel.collapse('show');
				// setup view event to indicate selection
				this.spawn("listItemSelected", {
					point : this.model
				});
			}
		},

		valConverter : function(direction, value, attributeName, model) {
			if(direction == 'ModelToView'){
				return ( new Number(value)).toLocaleString( undefined,{minimumFractionDigits: 0, maximumFractionDigits: 2});
			}
		},
		
		unitConverter : function(direction, value) {
			if (direction == 'ModelToView') {
				var unitContract = value.getVal();
				if (unitContract.lastIndexOf("obix:Unit/") > -1) {
					return unitText[unitContract.substr(unitContract
							.lastIndexOf('/') + 1)];
				}
			}
		},

		nameConverter : function(direction, value, attributeName, model) {
			if (direction == 'ModelToView') {
				if (model.getDisplayName() == '')
					return value;
				else
					return model.getDisplayName();
			}
		},

		statusClassConverter : function(direction, value) {
			if (direction == "ModelToView") {
				if (value != null) {
					var statustoLower = value.toLowerCase();
					if( statustoLower == Obix.status.DISABLED) return "glyphicon glyphicon-ban-circle";
					if( statustoLower == Obix.status.FAULT) return "glyphicon glyphicon-alert";
					if( statustoLower == Obix.status.DOWN) return "glyphicon glyphicon-warning-sign";
					if( statustoLower == Obix.status.UNAKEDALARM) return "glyphicon glyphicon-exclamation-sign";
					if( statustoLower == Obix.status.ALARM) return "glyphicon glyphicon-bell";
					if( statustoLower == Obix.status.UNACKED) return "glyphicon glyphicon-exclamation-sign";
					if( statustoLower == Obix.status.OVERRIDEN) return "glyphicon glyphicon-remove-circle";
					if( statustoLower == Obix.status.OK) return "glyphicon glyphicon-ok-circle";
				}
			}
		},

		statusConverter : function(direction, value) {
			if (direction == "ModelToView") {
				if (value != null) {
					var statustoLower = value.toLowerCase();
					if (statustoLower == Obix.status.DISABLED) return statusText[statustoLower];
					if (statustoLower == Obix.status.FAULT) return statusText[statustoLower];
					if (statustoLower == Obix.status.DOWN) return statusText[statustoLower];
					if (statustoLower == Obix.status.UNAKEDALARM) return statusText[statustoLower];
					if (statustoLower == Obix.status.ALARM) return statusText[statustoLower];
					if (statustoLower == Obix.status.UNACKED) return statusText[statustoLower];
					if (statustoLower == Obix.status.OVERRIDEN) return statusText[statustoLower];
					if (statustoLower == Obix.status.OK) return statusText[statustoLower];
				}
			}
		},

	});

	return ReferenceItemSvgView;
});
