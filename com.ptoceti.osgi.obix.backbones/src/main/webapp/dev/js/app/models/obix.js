/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : Ptoceti
 * PROJECT : Obix Backbones
 * FILENAME : obix.js
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
define([ 'backbone', 'underscore'], function(Backbone, _) {

	var Obj = Backbone.Model.extend({
		
		defaults : {
			type : 'obj',
			name : '',
			href : null,
			is : null,
			isNull : false,
			icon : null,
			displayName : '',
			display : '',
			writable : false,
			status : null,
			childrens : null,
		},

		initialize : function(attrs, options) {

			if (options != null && options.parent != null)
				this.parent = options.parent;

			/**
			 * With Obix, their are no id, resources are identified by theirs href. if this one is provided we can use it as an id
			 * internaly for beacknone use.
			 */
			if (this.get('href') !== null && !(this.get('href') instanceof Obj)) {
				this.set('href', new Uri(this.get('href')));
				this.id = this.getHref().getVal();
			}
			
			/**
			 * if href not provided obj not a resource, we can use the name attribute if provided
			 */
			if ((!this.id || 0 === this.id.length) && (this.get('name') && this.get('name').length > 0 )) {
				this.id = this.getName();
			}
			
			if (this.get('childrens') !== null && !(this.get('childrens') instanceof Objs))
				this.set('childrens', new Objs(this.get('childrens'), {
					urlRoot : _.result(this, 'urlRoot'),
					parent : this
				}));

			if (this.get('childrens') == null)
				this.set('childrens', new Objs([], {
					urlRoot : _.result(this, 'urlRoot'),
					parent : this
				}));
			
			if( this.get('is') != null )
				this.set('is', new Contract(this.get('is'), {
					urlRoot : _.result(this, 'urlRoot'),
					parent : this
				}));
			
			/**
			if( this.get('is') == null )
				this.set('is', new Contract([], {
					urlRoot : _.result(this, 'urlRoot'),
					parent : this
				}));
				**/
			
			//this.set({updateTimeStamp: new Date()});

		},

		parse : function(response, options) {
			if (!!response) {

				if (!response.href !== null) {
					response.href = new Uri(response.href);
				};
				if (response.childrens !== null)
					response.childrens = new Objs(response.childrens, {
						urlRoot : _.result(this, 'urlRoot'),
						parent : this
					});
				if( response.is != null)
					response.is = new Contract(response.is, {
						urlRoot : _.result(this,'urlRoot'),
						parent : this
					});
			}
			return response;
		},

		url : function() {
			var root = _.result(this, 'urlRoot') || _.result(this.collection, 'url') || urlError();
			var parent = _.result(this, 'parent');

			var href;
			// if we have an href, it must be part of effective url
			if ((this.getHref() != null) && ((href = this.getHref().getVal()) != null)) {
				// if href start by /, it is absolute href
				if (href.charAt(0) === '/') {
					// ... url = root + href
					return root.substr(0, root.charAt(root.length - 1) === '/' ? (root.length - 1) : (root.length)) + href;
				} else if (href.indexOf('../') == 0) {
					// if start by ../ it is relative to grand parent
				} else {
					// otherwise relative to parent
					if (parent == null) {
						// if there is no parent, take root
						if (root.charAt(root.length - 1) !== '/')
							root = root + '/';
						return root + href;
					} else {
						var parentUrl = parent.url();
						if (parentUrl.charAt(root.length - 1) !== '/')
							parentUrl = parentUrl + '/';
						return parentUrl + href;
					}
				}
			}

			// if there is no href, simply return parent'url
			if (parent != null)
				return parent.getUrl();
			else
				return root;
		},
		
		/**
		toJSON: function(options) {
			
		      if( options['noClientAttr'] != undefined && options.noClientAttr == true)
		    	  return _.pick( this.attributes, 'type', 'name', 'href', 'is','isNull','icon','displayName','display','writable','status','childrens' );
		      else
		    	  return _.clone(this.attributes);
		},
		**/
		
		/*
		 *  override set
		 */
	    set: function(key, val, options) {
	      var attr, attrs, unset, changes, silent, changing, prev, current;
	      if (key == null) return this;

	      // Handle both `"key", value` and `{key: value}` -style arguments.
	      if (typeof key === 'object') {
	        attrs = key;
	        options = val;
	      } else {
	        (attrs = {})[key] = val;
	      }

	      options || (options = {});

	      // Run validation.
	      if (!this._validate(attrs, options)) return false;

	      // Extract attributes and options.
	      unset           = options.unset;
	      silent          = options.silent;
	      changes         = [];
	      changing        = this._changing;
	      this._changing  = true;

	      if (!changing) {
	        this._previousAttributes = _.clone(this.attributes);
	        this.changed = {};
	      }
	      current = this.attributes, prev = this._previousAttributes;

	      // Check for changes of `id`.
	      if (this.idAttribute in attrs) this.id = attrs[this.idAttribute];

	      // For each `set` attribute, update or delete the current value.
	      for (attr in attrs) {
	        val = attrs[attr];
	        
	        var type = Object.prototype.toString.call(current[attr]);
	        if( current[attr] instanceof Objs && val instanceof Objs){
	        	current[attr].set(val.models,options);
	        } else {
		        if (!_.isEqual(current[attr], val)) changes.push(attr);
		        if (!_.isEqual(prev[attr], val)) {
		          this.changed[attr] = val;
		        } else {
		          delete this.changed[attr];
		        }
		        unset ? delete current[attr] : current[attr] = val;
	        }
	      }

	      // Trigger all relevant attribute changes.
	      if (!silent) {
	        if (changes.length) this._pending = true;
	        for (var i = 0, l = changes.length; i < l; i++) {
	          this.trigger('change:' + changes[i], this, current[changes[i]], options);
	        }
	      }

	      // You might be wondering why there's a `while` loop here. Changes can
	      // be recursively nested within `"change"` events.
	      if (changing) return this;
	      if (!silent) {
	        while (this._pending) {
	          this._pending = false;
	          this.trigger('change', this, options);
	        }
	      }
	      this._pending = false;
	      this._changing = false;
	      return this;
	    },
		
		hasContract : function(contract) {
			for ( var i = 0; i < this.getIs().get('uris').length; i++) {
				if (this.getIs().get('uris').at(i).get('val') == contract)
					return true;
			}
		},

		setName : function(inName) {
			this.set({
				name : inName
			});
		},
		getName : function() {
			return this.get('name');
		},
		setHref : function(inHref) {
			this.set({
				href : inHref
			});
		},
		getHref : function() {
			return this.get('href');
		},
		setIs : function(inIs) {
			this.set({
				is : inIs
			});
		},
		getIs : function() {
			return this.get('is');
		},
		
		setIsNull : function(inIsNull) {
			this.set({
				isnull : inIsNull
			});
		},
		getIsNull : function() {
			return this.get('isNull');
		},
		setIcon : function(inIcon) {
			this.set({
				icon : inIcon
			});
		},
		getIcon : function() {
			return this.get('icon');
		},
		setDisplayName : function(inDisplayName) {
			this.set({
				displayName : inDisplayName
			});
		},
		getDisplayName : function() {
			return this.get('displayName');
		},
		setDisplay : function(inDisplay) {
			this.set({
				display : inDisplay
			});
		},
		getDisplay : function() {
			return this.get('display');
		},
		setWritable : function(inWritable) {
			this.set({
				writable : inWritable
			});
		},
		getWritable : function() {
			return this.get('writable');
		},
		setStatus : function(inStatus) {
			this.set({
				status : inStatus
			});
		},
		getStatus : function() {
			return this.get('status');
		},
		setChildrens : function(inChildrens) {
			this.set({
				childrens : inChildrens
			});
		},
		getChildrens : function() {
			return this.get('childrens');
		},
		
		
		
	});

	var Contract = Backbone.Model.extend({

		defaults : {
			uris : null
		},
		
		initialize : function(attrs, options) {

			if (options != null && options.parent != null)
				this.parent = options.parent;

			if (this.get('uris') !== null)
				this.set('uris', new Objs(this.get('uris'), {
					urlRoot : _.result(this, 'urlRoot'),
					parent : this
				}));

		},
		
		parse : function(response, options) {
			if (response !== null && response != undefined) {
				if (response.uris !== null)
					response.childrens = new Objs(response.uris, {
						urlRoot : _.result(this, 'urlRoot'),
						parent : this
					});
			}
			return response;
		},
	});
	
	var Objs = Backbone.Collection.extend({
		model : function(attrs, option) {
			switch (attrs.type) {
			case "abstime":
				return new Abstime(attrs, option);
			case "bool":
				return new Bool(attrs, option);
			case "enum":
				return new Enum(attrs, option);
			case "err":
				return new Err(attrs, option);
			case "feed":
				return new Feed(attrs, option);
			case "int":
				return new Int(attrs, option);
			case "list":
				return new List(attrs, option);
			case "obj":
				return new Obj(attrs, option);
			case "op":
				return new Op(attrs, option);
			case "real":
				return new Real(attrs, option);
			case "ref":
				return new Ref(attrs, option);
			case "reltime":
				return new Reltime(attrs, option);
			case "str":
				return new Str(attrs, option);
			case "uri":
				return new Uri(attrs, option);
			case "watch":
				return new Watch(attrs, option);
			case "history":
				return new History(attrs, option);
			case "historyqueryout":
				return new HistoryQueryOut(attrs, option);
			case "historyrecord":
				return new HistoryRecord(attrs, option);
			case "historyrollupout":
				return new HistoryRollupOut(attrs, option);
			case "historyrolluprecord":
				return new HistoryRollupRecord(attrs,option);
			case "monitoredpoint":
				return new MonitoredPoint(attrs,option);
			default :
				return new Obj(attrs, option);
			}
		},

		getByName : function(name) {
			return this.find(function(model) {
				return model.getName() == name;
			});
		},

		getByContract : function(contract) {
			return this.filter(function(model) {
				if( model.getIs() !== null){
					for ( var i = 0; i < model.getIs().get('uris').length; i++) {
						if (model.getIs().get('uris').at(i).get('val') == contract)
							return true;
					}
				}
			});
		}
	});

	var Abstime = Obj.extend({

		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'abstime',
			val : null,
			max : null,
			min : null
		}),

		setVal : function(inVal) {
			this.set({
				val : inVal
			});
		},
		getVal : function() {
			return this.get('val');
		},
		setMax : function(inMax) {
			this.set({
				max : inMax
			});
		},
		getMax : function() {
			return this.get('max');
		},
		setMin : function(inMax) {
			this.set({
				min : inMin
			});
		},
		getMin : function() {
			return this.get('min');
		},

	});

	var Bool = Obj.extend({

		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'bool',
			val : null,
			range : null,
		}),

		setVal : function(inVal) {
			this.set({
				val : inVal
			});
		},
		getVal : function() {
			return this.get('val');
		},
		setRange : function(inRange) {
			this.set({
				range : inRange
			});
		},
		getRange : function() {
			return this.get('range');
		},

	});

	var Enum = Obj.extend({

		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'enum',
			val : null,
			range : null,
		}),

		setVal : function(inVal) {
			this.set({
				val : inVal
			});
		},
		getVal : function() {
			return this.get('val');
		},
		setRange : function(inRange) {
			this.set({
				range : inRange
			});
		},
		getRange : function() {
			return this.get('range');
		},

	});

	var Err = Obj.extend({

		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'err',
		}),
	});

	var Feed = Obj.extend({

		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'feed',
			'in' : null,
			of : null,
		}),

		setIn : function(inIn) {
			this.set({
				'in' : inIn
			});
		},
		getIn : function() {
			return this.get('in');
		},
		setOf : function(inOf) {
			this.set({
				of : inOf
			});
		},
		getOf : function() {
			return this.get('of');
		}
	});

	var Int = Obj.extend({

		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'int',
			val : null,
			max : null,
			min : null,
			unit : null,
		}),
		
		initialize : function(attrs, options) {
			this.constructor.__super__.initialize.apply(this, arguments);
			
			if (this.get('unit') !== null && !(this.get('unit') instanceof Obj))
				this.set('unit', new Uri(this.get('unit')));
		},
		
		parse : function(response, options) {
			this.constructor.__super__.parse.apply(this, arguments);
			if (response !== null) {

				if (response.unit !== null && response != undefined) {
					response.unit = new Uri(response.unit);
				};
			}
			return response;
		},

		setVal : function(inVal) {
			this.set({
				val : inVal
			});
		},
		getVal : function() {
			return this.get('val');
		},
		setMax : function(inMax) {
			this.set({
				max : inMax
			});
		},
		getMax : function() {
			return this.get('max');
		},
		setMin : function(inMax) {
			this.set({
				min : inMin
			});
		},
		getMin : function() {
			return this.get('min');
		},
		setUnit : function(inUnit) {
			this.set({
				unit : inUnit
			});
		},
		getUnit : function() {
			return this.get('unit');
		},

	});

	var List = Obj.extend({

		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'list',
			of : null,
			max : null,
			min : null,
		}),

		setOf : function(inOf) {
			this.set({
				of : inOf
			});
		},
		getOf : function() {
			return this.get('of');
		},
		setMax : function(inMax) {
			this.set({
				max : inMax
			});
		},
		getMax : function() {
			return this.get('max');
		},
		setMin : function(inMax) {
			this.set({
				min : inMin
			});
		},
		getMin : function() {
			return this.get('min');
		},

		add : function(obj) {
			this.getChildrens().add(obj);
		}
	});

	var Op = Obj.extend({

		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'op',
			'in' : null,
			out : null,
		}),

		setIn : function(inIn) {
			this.set({
				'in' : inIn
			});
		},
		getIn : function() {
			return this.get('in');
		},
		setOut : function(inOut) {
			this.set({
				out : inOut
			});
		},
		getOut : function() {
			return this.get('out');
		},

		invoke : function(inObj, outObj, options) {

			var method, xhr;

			// After a successful server-side save, the client is (optionally)
			// updated with the server-side state.
			if (options.parse === void 0)
				options.parse = true;
			var model = outObj;
			var success = options.success;
			options.success = function(resp) {
				model.set(model.parse(resp, options), options);
				if (success)
					success(model, resp, options);
				model.trigger('sync', model, resp, options);
			};
			// wrapError(this, options);
			method = 'create';

			// ensure we have an url
			if (!options.url)
				options.url = this.url();
			// indicate that we send no client side attributes
			options["noClientAttr"] = true;

			xhr = this.sync(method, inObj, options);

			return xhr;
		}
	});

	var Real = Obj.extend({

		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'real',
			val : null,
			max : null,
			min : null,
			unit : null,
			precision : null,
		}),

		initialize : function(attrs, options) {
			this.constructor.__super__.initialize.apply(this, arguments);
			
			if (this.get('unit') !== null && !(this.get('unit') instanceof Obj))
				this.set('unit', new Uri(this.get('unit')));
		},
		
		parse : function(response, options) {
			this.constructor.__super__.parse.apply(this, arguments);
			if (response !== null && response != undefined) {

				if (response.unit !== null) {
					response.unit = new Uri(response.unit);
				};
			}
			return response;
		},

		setVal : function(inVal) {
			this.set({
				val : inVal
			});
		},
		getVal : function() {
			return this.get('val');
		},
		setMax : function(inMax) {
			this.set({
				max : inMax
			});
		},
		getMax : function() {
			return this.get('max');
		},
		setMin : function(inMax) {
			this.set({
				min : inMin
			});
		},
		getMin : function() {
			return this.get('min');
		},
		setUnit : function(inUnit) {
			this.set({
				unit : inUnit
			});
		},
		getUnit : function() {
			return this.get('unit');
		},
		setPrecision : function(inPrecision) {
			this.set({
				precision : inPrecision
			});
		},
		getPrecision : function() {
			return this.get('precision');
		}

	});

	var Ref = Obj.extend({

		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'ref',
		}),

	});

	var Reltime = Obj.extend({

		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'reltime',
			val : null,
			max : null,
			min : null,
		}),

		setVal : function(inVal) {
			this.set({
				val : inVal
			});
		},
		getVal : function() {
			return this.get('val');
		},
		setMax : function(inMax) {
			this.set({
				max : inMax
			});
		},
		getMax : function() {
			return this.get('max');
		},
		setMin : function(inMax) {
			this.set({
				min : inMin
			});
		},
		getMin : function() {
			return this.get('min');
		}

	});

	var Str = Obj.extend({

		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'str',
			val : null,
			max : null,
			min : null,
		}),

		setVal : function(inVal) {
			this.set({
				val : inVal
			});
		},
		getVal : function() {
			return this.get('val');
		},
		setMax : function(inMax) {
			this.set({
				max : inMax
			});
		},
		getMax : function() {
			return this.get('max');
		},
		setMin : function(inMax) {
			this.set({
				min : inMin
			});
		},
		getMin : function() {
			return this.get('min');
		},

	});

	var Uri = Obj.extend({

		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'uri',
			val : null,
		}),

		setVal : function(inVal) {
			this.set({
				val : inVal
			});
		},
		getVal : function() {
			return this.get('val');
		}

	});
	
	var Status = {
			DISABLED : "disabled",
			FAULT : "fault",
			DOWN : "down",
			UNAKEDALARM : "unackedAlarm",
			ALARM :"alarm",
			UNACKED : "unacked",
			OVERRIDEN :"overridden",
			OK :"ok"
	};

	var About = Obj.extend({

		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'about',
			is : {uris: [{type: 'uri', val: 'obix:About'}]}
		}),

		getServerBootTime : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('serverBootTime');
		},

		getServerTime : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('serverTime');
		},
		getObixVersion : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('obixVersion');
		},
		getProductUrl : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('productUrl');
		},
		getProductName : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('productName');
		},
		getProductVersion : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('productVersion');
		},
		getVendorName : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('vendorName');
		},
		getVendorUrl : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('vendorUrl');
		},
		getServerName : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('serverName');
		}
	});

	var Lobby = Obj.extend({

		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'lobby',
			is : {uris: [{type: 'uri', val: 'obix:Lobby'}]}
		}),

		/*
		 * Return About 'Ref' type resource
		 */
		getAbout : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('about');
		},

		/*
		 * Return WatchService 'Ref' type resource
		 */
		getWatchService : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('watchService');
		},

		/*
		 * Return Batch 'op' type resource
		 */
		getBatchOp : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('batch');
		}
	});

	var Nil = Obj.extend({

		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'nil',
			is : {uris: [{type: 'uri', val: 'obix:Nil'}]}
		}),
		
	});

	var WatchService = Obj.extend({

		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'watchservice',
			is : {uris: [{type: 'uri', val: 'obix:WatchService'}]}
		}),

		/*
		 * Return Op 'make' operation for creating a new watch
		 */
		getMakeOp : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('make');
		},
	});

	var Watch = Obj.extend({

		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'watch',
			is : {uris: [{type: 'uri', val: 'obix:Watch'}]}
		}),

		/*
		 * Return Op 'make' operation for creating a new watch
		 */
		getAddOp : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('add');
		},

		getDeleteOp : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('delete');
		},

		getPoolChangesOp : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('poolChanges');
		},

		getPoolRefreshOp : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('poolRefresh');
		},

		getRemoveOp : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('remove');
		},

		getLease : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('lease');
		},
	});

	var WatchIn = Obj.extend({

		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'watchin',
			is : {uris: [{type: 'uri', val: 'obix:WatchIn'}]}
		}),

		initialize : function(attrs, options) {
			this.constructor.__super__.initialize.apply(this, arguments);

			
			this.getChildrens().add(new List({
				name : 'href',
				of : {uris: [{type: 'uri', val: 'obix:href'}]}
			}));
			
		},

		getHrefList : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('href');
		}
	});

	var WatchOut = Obj.extend({

		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'watchout',
			is : {uris: [{type: 'uri', val: 'obix:WatchOut'}]}
		}),
		
		getValueList : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('values');
		}
	});

	var WatchInItem = Obj.extend({

		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'watchinitem',
			is : {uris: [{type: 'uri', val: 'obix:WatchInItem'}]}
		}),

		getIn : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('in');
		}

	});

	var Point = Obj.extend({
		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'point',
			is : {uris: [{type: 'uri', val: 'obix:Point'}]}
		}),
	});

	var WritablePoint = Obj.extend({
		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'writablepoint',
			is : {uris: [{type: 'uri', val: 'obix:WritablePoint'}]}
		}),
	});
	
	var History = Obj.extend({
		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'history',
			is : {uris: [{type: 'uri', val: 'obix:History'}]}
		}),
		
		getQueryOp : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('query');
		},
		
		getRollupOp : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('rollup');
		},

	});
	
	var HistoryFilter = Obj.extend({
		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'historyfilter',
			is : {uris: [{type: 'uri', val: 'obix:HistoryFilter'}]}
		}),
		
		initialize : function(attrs, options) {
			this.constructor.__super__.initialize.apply(this, arguments);

			this.getChildrens().add(new Int({
				name : 'limit',
				isNull : "true"
			}));
			
			this.getChildrens().add(new Abstime({
				name : 'start',
				isNull : "true"
			}));
			
			this.getChildrens().add(new Abstime({
				name : 'end',
				isNull : "true"
			}));
			
		},
		
		getLimit : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('limit');
		},
		
		getStart : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('start');
		},
		
		getEnd : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('end');
		},
		
	});
	
	var HistoryRecord = Obj.extend({
		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'historyrecord',
			is : {uris: [{type: 'uri', val: 'obix:HistoryRecord'}]}
		}),
		
		getTimeStamp : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('timestamp');
		},
		
		
		getValue : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('value');
		},
	});
	
	var HistoryQueryOut = Obj.extend({
		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'historyqueryout',
			is : {uris: [{type: 'uri', val: 'obix:HistoryQueryOut'}]}
		}),
		
		getCount : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('count');
		},
		
		getStart : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('start');
		},
		
		getEnd : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('end');
		},
		
		getDataList : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('data');
		},
	});
	
	var HistoryRollupIn = Obj.extend({
		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'historyrollupin',
			is : {uris: [{type: 'uri', val: 'obix:HistoryRollupIn'}]}
		}),
		
		initialize : function(attrs, options) {
			this.constructor.__super__.initialize.apply(this, arguments);

			this.getChildrens().add(new Reltime({
				name : 'interval',
				isNull : "true"
			}));
			
			this.getChildrens().add(new Int({
				name : 'limit',
				isNull : "true"
			}));
			
			this.getChildrens().add(new Abstime({
				name : 'start',
				isNull : "true"
			}));
			
			this.getChildrens().add(new Abstime({
				name : 'end',
				isNull : "true"
			}));
			
		},
		
		getInterval : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('interval');
		},
		
		getLimit : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('limit');
		},
		
		getStart : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('start');
		},
		
		getEnd : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('end');
		} 
	});
	
	var HistoryRollupOut = Obj.extend({
		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'historyrollupout',
			is : {uris: [{type: 'uri', val: 'obix:HistoryRollupOut'}]}
		}),
		
		getData : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('data');
		},
		
		getCount : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('count');
		},
		
		getStart : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('start');
		},
		
		getEnd : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('end');
		}
	});
	
	var HistoryRollupRecord = Obj.extend({
		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'historyrolluprecord',
			is : {uris: [{type: 'uri', val: 'obix:HistoryRollupRecord'}]}
		}),
		
		getStart : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('start');
		},
		
		getEnd : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('end');
		},
		
		getMin : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('min');
		},
		
		getMax : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('max');
		},
		
		getAvg : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('avg');
		},
		
		getSum : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('sum');
		}
		
	});
	
	var MonitoredPoint = Obj.extend({

		defaults : _.extend({}, Obj.prototype.defaults, {
			type : 'monitoredpoint',
			is : {uris: [{type: 'uri', val: 'ptoceti:MonitoredPoint'}]}
		}),

		getHistoryRef : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('historyRef');
		},
		
		getPoint : function() {
			return this.constructor.__super__.getChildrens.apply(this).getByName('point');
		}

	});
	
	var MeasurePoint = Real.extend({

		defaults : _.extend({}, Real.prototype.defaults, {
			type : 'measurepoint',
			is : {uris: [{type: 'uri', val: 'ptoceti:MeasurePoint'}]}
		})

	});
	
	var ReferencePoint = Real.extend({

		defaults : _.extend({}, Real.prototype.defaults, {
			type : 'referencepoint',
			is : {uris: [{type: 'uri', val: 'ptoceti:ReferencePoint'}]}
		})

	});

	return {
		obj : Obj,
		objs : Objs,
		abstime : Abstime,
		bool : Bool,
		enum : Enum,
		err : Err,
		feed : Feed,
		int : Int,
		list : List,
		op : Op,
		real : Real,
		ref : Ref,
		reltime : Reltime,
		str : Str,
		uri : Uri,
		contract : Contract,
		status : Status,

		about : About,
		lobby : Lobby,
		nil : Nil,
		watchService : WatchService,
		watch : Watch,
		watchIn : WatchIn,
		watchOut : WatchOut,
		watchInItem : WatchInItem,
		point : Point,
		writablePoint : WritablePoint,
		history : History,
		historyFilter : HistoryFilter,
		historyRecord : HistoryRecord,
		historyQueryOut : HistoryQueryOut,
		historyRollupIn : HistoryRollupIn,
		historyRollupOut : HistoryRollupOut,
		historyRollupRecord : HistoryRollupRecord,
		
		monitoredPoint : MonitoredPoint,
		measurePoint : MeasurePoint,
		referencePoint : ReferencePoint

	};
});
