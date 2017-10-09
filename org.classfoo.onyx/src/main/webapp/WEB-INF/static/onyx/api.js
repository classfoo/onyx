/**
 * Onyx Api
 */
define("onyx/api", [ "jquery", "require", "onyx/api/label", "onyx/api/entity",
		"onyx/api/recommend", "onyx/api/base", "onyx/api/timeline",
		"onyx/api/material", "onyx/api/file", "onyx/api/image",
		"onyx/api/link", "onyx/api/linknames", "onyx/api/linknodes" ],
		function($, require) {

			var labels = {};

			var label;

			var entities = {};

			var entity;

			var links = {};

			var link;

			var linknames = {};

			var linkname;

			var timelines = {};

			var timeline;

			var materials = {};

			var material;

			var base;

			var recommend;

			function Api() {
				window.Api = Api;
			}

			Api.recommend = function() {
				var Recommend = require("onyx/api/recommend");
				return new Recommend();
			}

			Api.base = function() {
				var Base = require("onyx/api/base");
				return new Base();
			}

			Api.label = function(kid) {
				if (!kid) {
					if (label) {
						return label;
					}
					var Label = require("onyx/api/label");
					label = new Label();
					return label;
				}
				if (labels[kid]) {
					return labels[kid];
				}
				var Label = require("onyx/api/label");
				labels[kid] = new Label(kid);
				return labels[kid];
			}

			Api.entity = function(kid) {
				if (!kid) {
					if (entity) {
						return entity;
					}
					var Entity = require("onyx/api/entity");
					entity = new Entity();
					return entity;
				}
				if (entities[kid]) {
					return entities[kid];
				}
				var Entity = require("onyx/api/entity");
				entities[kid] = new Entity(kid);
				return entities[kid];
			}

			Api.link = function(kid) {
				if (!kid) {
					if (link) {
						return link;
					}
					var Link = require("onyx/api/link");
					link = new Link();
					return link;
				}
				if (links[kid]) {
					return links[kid];
				}
				var Link = require("onyx/api/link");
				links[kid] = new Link(kid);
				return links[kid];
			}

			Api.linknames = function(kid) {
				if (!kid) {
					if (linkname) {
						return linkname;
					}
					var LinkName = require("onyx/api/linknames");
					linkname = new LinkName();
					return linkname;
				}
				if (linknames[kid]) {
					return linknames[kid];
				}
				var LinkName = require("onyx/api/linknames");
				linknames[kid] = new LinkName(kid);
				return linknames[kid];
			}

			Api.linknodes = function() {
				var LinkNodes = require("onyx/api/linknodes");
				return new LinkNodes();
			}

			Api.timeline = function(kid) {
				if (!kid) {
					if (timeline) {
						return timeline;
					}
					var TimeLine = require("onyx/api/timeline");
					timeline = new TimeLine();
					return timeline;
				}
				if (timelines[kid]) {
					return timelines[kid];
				}
				var TimeLine = require("onyx/api/timeline");
				timelines[kid] = new TimeLine(kid);
				return timelines[kid];
			}

			Api.material = function(kid) {
				if (!kid) {
					if (material) {
						return material;
					}
					var Material = require("onyx/api/material");
					material = new Material();
					return material;
				}
				if (materials[kid]) {
					return materials[kid];
				}
				var Material = require("onyx/api/material");
				materials[kid] = new Material(kid);
				return materials[kid];
			}

			/**
			 * Api File for uploading and visit files
			 */
			Api.file = function() {
				var File = require("onyx/api/file");
				return new File();
			}

			/**
			 * Api Image for canvas drawing
			 */
			Api.image = function() {
				if (this.imageApi) {
					return this.imageApi;
				}
				var OnyxImage = require("onyx/api/image");
				this.imageApi = new OnyxImage();
				return this.imageApi;
			}

			Api.getResource = function(resource) {
				if (resource == null || typeof (resource) == "undefined") {
					return $.dfd({});
				}
				var type = resource.substring(0, 1);
				switch (type) {
				case 'k': {
					return Api.base().single(resource);
				}
				case 'u': {
					return $.dfd();// Api.user().single(resource);
				}
				case 'e': {
					return Api.entity().single(resource);
				}
				case 'm': {
					return Api.material().single(resource);
				}
				case 'l': {
					return Api.label().single(resource);
				}
				}
				return $.dfd({
					id : resource,
					caption : "知识库1"
				});
			}

			Api.get = function(api, options) {
				return this.ajax("get", api, options);
			}

			Api.post = function(api, options) {
				return this.ajax("post", api, options);
			}

			Api.upload = function(api, options) {
				return this.ajaxform("post", api, options);
			}

			Api.put = function(api, options) {
				return this.ajax("put", api, options);
			}

			Api.remove = function(api, options) {
				return this.ajax("delete", api, options);
			}

			Api.ajax = function(method, api, options) {
				var dfd = $.Deferred();
				var data = Api.stringifyData(options);
				$.ajax({
					method : method,
					url : "/onyxapi/v1/" + api,
					data : data,
					success : function(result) {
						dfd.resolve(result);
					},
					error : function(error) {
						dfd.resolve({});
					}
				});
				return dfd.promise();
			}

			Api.ajaxform = function(method, api, formdata) {
				var dfd = $.Deferred();
				$.ajax({
					method : method,
					url : "/onyxapi/v1/" + api,
					data : formdata,
					enctype : 'multipart/form-data',
					processData : false,
					contentType : false,
					success : function(result) {
						dfd.resolve(result);
					},
					error : function(error) {
						dfd.resolve({});
					}
				});
				return dfd.promise();
			}

			Api.stringifyData = function(options) {
				if (!options) {
					return null;
				}
				var data = {};
				$.each(options, function(k, v) {
					if (typeof (v) === "string") {
						data[k] = v;
						return;
					}
					data[k] = JSON.stringify(v);
				});
				return data;
			}
			return Api;
		});

define("onyx/api/recommend", [ "jquery", "require" ], function($, require) {

	function Recommend() {
	}

	/**
	 * get Recommond list
	 * 
	 * @param offset
	 * @param limit
	 */
	Recommend.prototype.list = function(offset, limit) {
		return Api.get("timeline");
	}

	return Recommend;
});

define("onyx/api/base", [ "jquery", "require" ], function($, require) {

	function Base() {
	}

	/**
	 * get Base list
	 * 
	 * @param offset
	 * @param limit
	 */
	Base.prototype.list = function(offset, limit) {
		return Api.get("base");
	}

	/**
	 * get Base resource
	 */
	Base.prototype.single = function(kid) {
		return Api.get("base/" + kid);
	}

	return Base;
});

define("onyx/api/label", [ "jquery", "require" ], function($, require) {

	function Label(kid) {
		this.kid = kid;
		this.labelNames = [];
		this.labelMap = {};
	}

	/**
	 * get label list
	 * 
	 * @param offset
	 * @param limit
	 */
	Label.prototype.list = function(offset, limit) {
		return Api.get("label", {
			kid : this.kid
		});
	}

	/**
	 * get label resource
	 * 
	 * @param lid
	 */
	Label.prototype.get = function(lid) {
		return Api.get("label/" + lid);
	}

	/**
	 * get label image
	 * 
	 * @param name
	 */
	Label.prototype.image = function(name) {
		var dfd = $.Deferred();
		Api.get("label/" + name, {
			kid : this.kid
		}).done(function(label) {
			var icon = (label.properties && label.properties.icon) || "\ue66f";
			var background = (label.properties && label.properties.background)||"#C5DBF0";
			var color = (label.properties && label.properties.color)||"white";
			var canvasOffscreen = document.createElement('canvas');
			canvasOffscreen.width = 64;
			canvasOffscreen.height = 64;
			var context = canvasOffscreen.getContext('2d');
			context.save();
			context.beginPath();
			context.moveTo(0,0);
			context.rect(0, 0, 64,64);
			context.font = "32px iconfont";
			context.textAlign = "center";
			context.fillStyle = background;
			context.fill();
			context.fillStyle = color;
			context.fillText(icon, 32, 48);
			context.closePath();
			context.restore();
			dfd.resolve(canvasOffscreen);
		});
		return dfd.promise();
	}

	/**
	 * get label properties by name
	 */
	Label.prototype.properties = function(name) {
		return $.dfd([ {}, {}, {}, {} ]);
	}

	/**
	 * get label parents by name
	 */
	Label.prototype.parents = function(name) {
		return $.dfd([ {}, {}, {}, {} ]);
	}

	/**
	 * save label modifies
	 * 
	 * @param kid
	 * @param lid
	 * @param modifies
	 */
	Label.prototype.save = function(options) {
		return Api.post("label", options);
	}
	return Label;
});

define("onyx/api/entity", [ "jquery", "require" ], function($, require) {

	function Entity(kid) {
		this.kid = kid;
		this.entityNames = [];
		this.entityMap = {};
	}

	/**
	 * get entity list
	 * 
	 * @param offset
	 * @param limit
	 */
	Entity.prototype.list = function(offset, limit) {
		return Api.get("entity", {
			kid : this.kid
		});
	}

	/**
	 * get entity resource
	 * 
	 * @param eid
	 */
	Entity.prototype.get = function(eid) {
		return Api.get("entity/" + eid);
	}

	/**
	 * save entity modifies
	 * 
	 * @param kid
	 * @param lid
	 * @param modifies
	 */
	Entity.prototype.save = function(options) {
		return Api.post("entity", options);
	}
	return Entity;
});

define("onyx/api/link", [ "jquery", "require" ], function($, require) {

	function Link(kid) {
		this.kid = kid;
	}

	/**
	 * get link names by source
	 * 
	 * @param eid
	 * @param offset
	 * @param limit
	 */
	Link.prototype.names = function(eid, offset, limit) {
		return Api.get("link", {
			kid : this.kid,
			eid : eid
		});
	}

	return Link;
});

define("onyx/api/linknames", [ "jquery", "require" ], function($, require) {

	function LinkNames(kid) {
		this.kid = kid;
	}

	/**
	 * get link names by source
	 * 
	 * @param eid
	 * @param offset
	 * @param limit
	 */
	LinkNames.prototype.list = function(eid, offset, limit) {
		return Api.get("linknames/" + eid);
	}

	return LinkNames;
});

/**
 * query links and nodes by one node
 */
define("onyx/api/linknodes", [ "jquery", "require" ], function($, require) {

	function LinkNodes() {
	}

	/**
	 * get links and nodes by one node
	 * 
	 * @param eid
	 * @param offset
	 * @param limit
	 */
	LinkNodes.prototype.list = function(options) {
		return Api.get("linknodes/" + options.id, options);
	}

	return LinkNodes;
});

define("onyx/api/timeline", [ "jquery", "require" ], function($, require) {

	function TimeLine(kid) {
		this.kid = kid;
	}

	/**
	 * get TimeLine list
	 * 
	 * @param offset
	 * @param limit
	 */
	TimeLine.prototype.list = function(offset, limit) {
		return Api.get("timeline");
	}

	return TimeLine;
});

define("onyx/api/material", [ "jquery", "require" ], function($, require) {

	function Material(kid) {
		this.kid = kid;
	}

	/**
	 * get Material resource
	 */
	Material.prototype.single = function(mid) {
		return Api.get("material/" + mid);
	}

	/**
	 * get material list
	 * 
	 * @param offset
	 * @param limit
	 */
	Material.prototype.list = function(offset, limit) {
		return Api.get("material");
	}

	/**
	 * create a new material
	 * 
	 * @param data
	 *            formdata for new contribute
	 */
	Material.prototype.create = function(data) {
		data.append("kid", this.kid);
		return Api.upload("material", data);
	}

	return Material;
});

/**
 * API File
 */
define("onyx/api/file", [ "jquery", "require" ], function($, require) {

	function File() {
	}

	/**
	 * upload a file to server
	 */
	File.prototype.upload = function(file) {
		var data = new FormData();
		data.append("file", file);
		return Api.upload("file", data);
	}

	return File;
});

/**
 * API Image
 */
define("onyx/api/image", [ "jquery", "require" ], function($, require) {

	function ImageApi() {
		this.images = {};
	}

	/**
	 * upload a file to server
	 */
	ImageApi.prototype.get = function(id) {
		var image = this.images[id];
		if (image) {
			return $.dfd(image);
		}
		var dfd = $.Deferred();
		var image = new Image(); // 创建img元素
		var self = this;
		image.onload = function() {
			self.images[id] = image;
			dfd.resolve(image);
		}
		image.onerror = function() {
			// 通过node.id获取类型信息，将icon绘制到canvas，然后取出image
			Api.entity().get(id).done(function(entity) {
				var kid = entity.kid;
				var name = entity.labels[0];
				Api.label(kid).image(name).done(function(image) {
					dfd.resolve(image);
				});
			});
		}
		image.src = "/onyxapi/v1/image/" + id;
		return dfd.promise();
	}

	return ImageApi;
});
