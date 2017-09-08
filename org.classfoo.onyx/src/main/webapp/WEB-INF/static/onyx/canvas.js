/**
 * Onyx Canvas Class
 */
define(
		"onyx/canvas",
		[ "jquery", "require", "css!./canvas.css", "d3/d3", ,
				"onyx/canvas/graph", "onyx/canvas/compass",
				"onyx/canvas/searchpanel", "onyx/canvas/cornerbutton" ],
		function($, require) {

			var d3 = require("d3/d3");

			var Graph = require("onyx/canvas/graph");

			var Compass = require("onyx/canvas/compass");

			var SearchPanel = require("onyx/canvas/searchpanel");

			var CornerButton = require("onyx/canvas/cornerbutton");

			function Canvas(resource) {
				this.resource = resource;
				this.kid = this.resource.id;
			}

			Canvas.prototype.build = function(pdom) {
				// init dom
				this.width = pdom.innerWidth();
				this.height = pdom.innerHeight();
				this.dom = $("<div class='onyx-canvas'></div>");
				this.dom.appendTo(pdom);
				this.canvasDom = $("<canvas class='onyx-canvas-canvas'></canvas>");
				this.canvasDom.attr("width", this.width);
				this.canvasDom.attr("height", this.height);
				this.canvasDom.appendTo(this.dom);
				this.canvasDom.on("dblclick", this.onDblClick.bind(this));
				this.canvasDom.on("click", this.onClick.bind(this));
				this.canvasDom.on("mousemove", this.onMouseMove.bind(this));
				this.canvasDom.on("clicknode", this.onClickNode.bind(this));
				this.canvasDom.on("dblclicknode", this.onDblClickNode
						.bind(this));
				this.canvasDom.on("clickgraph", this.onClickGraph.bind(this));
				this.canvasDom.on("dblclickgraph", this.onDblClickGraph
						.bind(this));
				this.canvasDom.on("clickmenu", this.onClickMenu.bind(this));
				this.canvasDom.on("contextmenu", this.onContextMenu.bind(this));
				// init canvas
				this.canvas = document.querySelector(".onyx-canvas-canvas");
				this.context = this.canvas.getContext("2d");
				this.graph = new Graph(this);
				this.compass = new Compass(this, this.graph);
				this.searchPanel = new SearchPanel(this, this.graph);
				this.connerButton = new CornerButton(this, this.graph,
						this.searchPanel);
				this.render();
			}

			Canvas.prototype.getCanvas = function() {
				return this.canvas;
			}

			Canvas.prototype.getContext = function() {
				return this.context;
			}

			Canvas.prototype.onDblClick = function(event) {
				if (this.compass.onDblClick(event)) {
					this.render();
					return;
				}
				if (this.graph.onDblClick(event)) {
					this.render();
					return;
				}
			}

			Canvas.prototype.onClick = function(event) {
				event.stopPropagation();
				if (this.compass.onClick(event)) {
					this.render();
					return;
				}
				if (this.graph.onClick(event)) {
					this.render();
					return;
				}
			}

			Canvas.prototype.onMouseMove = function(event) {
				event.stopPropagation();
				if (this.compass.onMouseMove(event)) {
					this.render();
					return;
				}
				if (this.graph.onMouseMove(event)) {
					this.render();
					return;
				}
			}

			Canvas.prototype.onClickMenu = function(event, menu) {
				this.compass.hide();
				if (menu.button.id == "add") {
					this.searchPanel.show(menu.node);
				}
			}

			Canvas.prototype.onClickNode = function(event, node) {
				this.compass.show(node);
			}

			Canvas.prototype.onDblClickNode = function(event, node) {
			}

			Canvas.prototype.onClickGraph = function(event, pos) {
				this.compass.hide();
			}

			Canvas.prototype.onDblClickGraph = function(event, pos) {
				this.searchPanel.show(pos);
			}

			Canvas.prototype.onContextMenu = function(event) {
				return false;
			}

			Canvas.prototype.fire = function(event, options) {
				return this.canvasDom.trigger(event, options);
			}

			Canvas.prototype.render = function() {
				this.context.clearRect(0, 0, this.width, this.height);
				this.graph.render();
				this.compass.render();
				//console.log("render");
			}

			return Canvas;
		});

/**
 * Onyx Canvas Graph
 */
define("onyx/canvas/graph", [ "jquery", "require", "d3/d3" ],
		function($, require) {

			var d3 = require("d3/d3");

			var radius = 20;

			var Graph = function(canvas) {
				this.canvas = canvas;
				this.context = canvas.getContext();
				this.nodes = [];
				this.links = [];
				this.width = this.canvas.width;
				this.height = this.canvas.height;
				this.graph = {
					id : "graph",
					x : 0,
					y : 0,
					width : this.width,
					height : this.height
				};
				this.index = this.nodes.length;
				this.bindEvents();
			}

			Graph.prototype.bindEvents = function() {
				var width = this.canvas.width;
				var height = this.canvas.height;
				this.simulation = d3.forceSimulation(this.nodes);
				this.simulation.force("collide", d3.forceCollide(this.nodes)
						.radius(function(d) {
							return radius + 12;
						}).iterations(4).strength(1));
//				this.simulation.force("link", d3.forceLink(this.links).id(
//						function(d) {
//							return d.id;
//						}).iterations(4).distance(100).strength(1));
				this.simulation.on("tick", this.onTick.bind(this));
				this.simulation.on("end",this.onTickEnd.bind(this));
				this.simulation.restart();
				var d3Canvas = d3.select(this.canvas.getCanvas());
				d3Canvas.call(d3.drag().container(this.canvas.getCanvas())
						.subject(this.dragsubject.bind(this)).on("start",
								this.dragstarted.bind(this)).on("drag",
								this.dragging.bind(this)).on("end",
								this.dragended.bind(this)));
			}

			Graph.prototype.dragsubject = function() {
				var node = this.simulation.find(d3.event.x - this.graph.x,
						d3.event.y - this.graph.y, radius);
				if (node) {
					return node;
				}
				return this.graph;
			}

			Graph.prototype.dragstarted = function() {
				var subject = d3.event.subject;
				if (subject.id == "graph") {
					subject.sx = d3.event.x;
					subject.sy = d3.event.y;
				} else {
					if (!d3.event.active)
						this.simulation.alphaTarget(0.3).restart();
					subject.fx = subject.x;
					subject.fy = subject.y;
					this.dragged = subject.id;
				}
			}

			Graph.prototype.dragging = function() {
				var subject = d3.event.subject;
				if (subject.id == "graph") {
					subject.x += d3.event.x - subject.sx;
					subject.y += d3.event.y - subject.sy;
					subject.sx = d3.event.x;
					subject.sy = d3.event.y;
					this.canvas.render();
				} else {
					subject.fx = d3.event.x;
					subject.fy = d3.event.y;
				}
			}

			Graph.prototype.dragended = function() {
				var subject = d3.event.subject;
				if (subject.id == "graph") {
					this.render();
				} else {
					if (!d3.event.active)
						this.simulation.alphaTarget(0);
					subject.fx = null;
					subject.fy = null;
					this.dragged = null;
				}
			}

			Graph.prototype.isMouseOvered = function(node) {
				return node.id === this.mouseovered;
			}

			Graph.prototype.isSelected = function(node) {
				return this.selected ? (this.selected[node.id] ? true : false)
						: false;
			}

			Graph.prototype.isDragged = function(node) {
				return this.dragged ? node.id === this.dragged : false;
			}

			Graph.prototype.onTick = function(event) {
				this.canvas.render();
			}

			Graph.prototype.onTickEnd = function(event) {
				this.canvas.render();
			}
			
			Graph.prototype.onClick = function(event) {
				var item = this.simulation.find(event.offsetX - this.graph.x,
						event.offsetY - this.graph.y, radius);
				if (item == null) {
					if (this.selected || this.lastselected) {
						this.lastselected = null;
						this.selected = null;
						this.canvas.fire("clickgraph");
						return true;
					}
					this.canvas.fire("clickgraph");
					return false;
				}
				if (this.selected) {
					var sel = this.selected[item.id];
					if (sel) {
						this.selected[item.id] = false;
						this.lastselected = null;
						this.canvas.fire("clickgraph");
						return true;
					} else {
						this.selected[item.id] = true;
						this.lastselected = item.id;
						this.canvas.fire("clicknode", item);
						return true;
					}
				} else {
					this.selected = {};
					this.selected[item.id] = true;
					this.lastselected = item.id;
					this.canvas.fire("clicknode", item);
					return true;
				}
			}

			Graph.prototype.onDblClick = function(event) {
				var item = this.simulation.find(event.offsetX - this.graph.x,
						event.offsetY - this.graph.y, radius);
				if (item == null) {
					this.canvas.fire("dblclickgraph", {
						x : event.offsetX - this.graph.x,
						y : event.offsetY - this.graph.y
					});
					return true;
				}
				this.canvas.fire("dblclicknode", item);
				return true;
			}

			Graph.prototype.onMouseMove = function(event) {
				var item = this.simulation.find(event.offsetX - this.graph.x,
						event.offsetY - this.graph.y, radius);
				if (item == null) {
					if (this.mouseovered) {
						this.mouseovered = null;
						return true;
					}
					return false;
				}
				if (this.mouseovered == item.id) {
					return false;
				}
				this.mouseovered = item.id;
				return true;
			}

			Graph.prototype.render = function() {
				for (var i = 0, link, n = this.links.length; i < n; i++) {
					link = this.links[i];
					this.renderLink(link);
				}
				for (var i = 0, node, n = this.nodes.length; i < n; ++i) {
					node = this.nodes[i];
					this.renderNode(node);
				}
			}

			Graph.prototype.renderNode = function(node) {
				var dragged = this.isDragged(node);
				var selected = this.isSelected(node);
				var mouseovered = this.isMouseOvered(node);
				var nodex = node.x + this.graph.x;
				var nodey = node.y + this.graph.y;
				// draw node
				this.context.save();
				this.context.beginPath();
				this.context.moveTo(nodex + radius, nodey);
				this.context.arc(nodex, nodey, radius, 0, 2 * Math.PI);
				if (mouseovered) {
					this.context.fillStyle = "#000000";
				} else {
					this.context.fillStyle = "#FFFFFF";
				}
				this.context.fill();
				this.context.restore();
				// draw circle
				this.context.save();
				this.context.beginPath();
				this.context.arc(nodex, nodey, radius + 5, 0, 2 * Math.PI);
				this.context.strokeStyle = "#C5DBF0";
				this.context.lineWidth = 3;
				this.context.stroke();
				this.context.restore();
				// draw border
				this.context.save();
				if (dragged) {
					this.context.setLineDash([ 3, 3 ]);
					this.context.lineWidth = 3;
					this.context.stroke();
				} else if (selected) {
					this.context.lineWidth = 3;
					this.context.strokeStyle = "#C5DBF0";
					this.context.stroke();
				} else {
					// this.context.lineWidth = 1;
					// this.context.stroke();
				}
				this.context.restore();
				// draw icon
				this.context.save();
				this.context.font = "26px iconfont";
				this.context.textAlign = "center"
				if (mouseovered) {
					this.context.fillStyle = "#FFFFFF";
				} else {
					this.context.fillStyle = "#000000";
				}
				this.context.fillText("\ue60a", nodex, nodey + radius / 2);
				this.context.restore();
				// draw label
				this.context.save();
				this.context.font = "14px 微软雅黑";
				this.context.textAlign = "center"
				this.context.fillStyle = "#FFFFFF";
				this.context.fillText(node.name || node.id, nodex, nodey
						+ radius + 20);
				this.context.restore();
			}

			Graph.prototype.renderLink = function(link) {
				this.context.save();
				this.context.beginPath();
				this.context.moveTo(link.source.x + this.graph.x, link.source.y
						+ this.graph.y);
				this.context.lineTo(link.target.x + this.graph.x, link.target.y
						+ this.graph.y);
				this.context.strokeStyle = "#FFFFFF";
				this.context.stroke();
				this.context.restore();
			}

			Graph.prototype.addNode = function(node) {
				this.nodes.push(node);
				this.bindEvents();
			}

			Graph.prototype.addLine = function(options) {
				this.lines.push(node);
			}

			Graph.prototype.getX = function() {
				return this.graph.x;
			}

			Graph.prototype.getY = function() {
				return this.graph.y;
			}

			Graph.prototype.getWidth = function() {
				return this.graph.width;
			}

			Graph.prototype.getHeight = function() {
				return this.graph.height;
			}
			return Graph;
		});

/**
 * Onyx Canvas Compass Controller Class
 */
define("onyx/canvas/compass", [ "jquery", "require", "d3/d3" ], function($,
		require) {

	var d3 = require("d3/d3");

	var OUTERRADIUS = 100;

	var INNERRADIUS = 40;

	var Compass = function(canvas, graph) {
		this.graph = graph;
		this.canvas = canvas;
		this.context = canvas.getContext();
		this.active = -1;
		this.buttons = [ {
			id : "raida",
			icon : '\ue6b4',
			length : 1,
			name : "雷达"
		}, {
			id : "links",
			icon : '\ue6b6',
			length : 1,
			name : "关系"
		}, {
			id : "properties",
			icon : '\ue6ae',
			length : 1,
			name : "查看"
		}, {
			id : "search",
			icon : '\ue6b3',
			length : 1,
			name : "搜索",
			children : [ {
				id : "all",
				length : 1,
				name : "所有"
			}, {
				id : "entities",
				length : 1,
				name : "实体"
			}, {
				id : "properties",
				length : 1,
				name : "属性"
			}, {
				id : "labels",
				length : 1,
				name : "标签"
			}, {
				id : "sources",
				length : 1,
				name : "来源"
			}, {
				id : "reference",
				length : 1,
				name : "引用"
			} ]
		}, {
			id : "add",
			icon : '\ue6b5',
			length : 1,
			name : "添加"
		}, {
			id : "remove",
			icon : '\ue6b1',
			length : 1,
			name : "移除"
		}, {
			id : "select",
			icon : '\ue6b2',
			length : 1,
			name : "选择",
		}, {
			id : "expand",
			icon : '\ue6b0',
			length : 1,
			name : "展开"
		} ];
	}

	Compass.prototype.show = function(node) {
		this.node = node;
		this.arcs = null;
	}

	Compass.prototype.hide = function() {
		this.node = null;
		this.arcs = null;
	}

	Compass.prototype.render = function() {
		if (!this.node) {
			return;
		}
		this.x = this.node.x + this.graph.getX();
		this.y = this.node.y + this.graph.getY();
		this.renderInner();
		if (this.active) {
			this.renderOutter();
		}
	}
	Compass.prototype.renderInner = function() {
		this.context.save();
		var arc = d3.arc().outerRadius(OUTERRADIUS).innerRadius(INNERRADIUS)
				.padAngle(0.01).context(this.context);
		this.arcs = d3.pie().startAngle(-Math.PI / 8).endAngle(
				Math.PI * 2 - Math.PI / 8).value(function(d) {
			return d.length;
		})(this.buttons);
		this.context.translate(this.x, this.y);
		this.context.globalAlpha = 0.8;
		this.arcs.forEach(this.renderArc.bind(this, arc));
		this.context.restore();
	}

	Compass.prototype.renderOutter = function() {
		if (!this.active) {
			return;
		}
		var button = this.getButton(this.active);
		;
		if (!button) {
			return;
		}
		var children = button.children;
		if (!children) {
			return;
		}
		this.context.save();
		var arc = d3.arc().outerRadius(OUTERRADIUS + 3).innerRadius(
				OUTERRADIUS + 63).padAngle(0.01).context(this.context);
		var arcs = d3.pie().startAngle(-Math.PI / 8).endAngle(
				Math.PI - Math.PI / 8).value(function(d) {
			return d.length;
		})(children);
		this.context.translate(this.x, this.y);
		this.context.globalAlpha = 0.8;
		arcs.forEach(this.renderArc.bind(this, arc));
		this.context.restore();
	}

	Compass.prototype.renderArc = function(arc, d, i) {
		var button = d.data;
		this.context.beginPath();
		arc.cornerRadius(button.id === this.active ? 2 : 4);
		arc(d);
		this.context.fillStyle = button.id === this.active ? "#FFFFFF"
				: "#000000";
		this.context.fill();
		var c = arc.centroid(d);
		var angle = (d.startAngle + d.endAngle) / 2;
		if (angle > Math.PI / 2 && angle < Math.PI * 3 / 2) {// downside
			this.context.save();
			this.context.translate(c[0], c[1]);
			this.context.rotate(angle + Math.PI);
			// draw text
			this.context.font = "12px iconfont";
			this.context.textAlign = "center"
			this.context.fillStyle = button.id === this.active ? "#000000"
					: "#FFFFFF";
			this.context.fillText(button.icon, 0, -4);
			this.context.font = "16px 微软雅黑";
			this.context.textAlign = "center"
			this.context.fillText(button.name, 0, 18);
			this.context.restore();
		} else {// upside
			this.context.save();
			this.context.translate(c[0], c[1]);
			this.context.rotate(angle);
			this.context.font = "12px iconfont";
			this.context.textAlign = "center"
			this.context.fillStyle = button.id === this.active ? "#000000"
					: "#FFFFFF";
			this.context.fillText(button.icon, 0, 10);
			// draw text
			this.context.font = "16px 微软雅黑";
			this.context.textAlign = "center"
			this.context.fillText(button.name, 0, -10);
			this.context.restore();
		}

	}

	Compass.prototype.onDblClick = function(event) {
		return false;
	}

	Compass.prototype.onClick = function(event) {
		if (!this.node) {
			return false;
		}
		if (!this.active) {
			return false;
		}
		var button = this.getButton(this.active);
		if (button.children) {
			this.showOutterMenu(button);
			return true;
		}
		this.canvas.fire("clickmenu", {
			button : button,
			node : this.node
		});
		return true;
	}

	Compass.prototype.onMouseMove = function(event) {
		if (!this.arcs) {
			if (this.active) {
				this.active = null;
				this.canvas.render();
			}
			return;
		}
		var x = event.offsetX - this.x;
		var y = event.offsetY - this.y;
		var radius = Math.sqrt(x * x + y * y);
		if (radius < INNERRADIUS || radius > OUTERRADIUS) {
			if (this.active != null) {
				this.active = null;
				this.canvas.render();
			}
			return;
		}
		var angle = this.getAngle(x, y);
		var self = this;
		$.each(this.arcs, function(index, arc) {
			var startAngle = arc.startAngle;
			var endAngle = arc.endAngle;
			if (startAngle < 0) {
				if (angle < Math.PI * 2 + startAngle && angle > endAngle) {
					return;
				}
			} else {
				if (angle < startAngle || angle > endAngle) {
					return;
				}
			}
			var button = self.buttons[index];
			if (self.active == button.id) {
				return;
			}
			self.active = button.id;
			self.canvas.render();
		})
	}

	Compass.prototype.getButton = function(id) {
		for (var i = 0; i < this.buttons.length; i++) {
			if (this.buttons[i].id == id) {
				return this.buttons[i];
			}
		}
		return null;
	}

	Compass.prototype.getAngle = function(x, y) {
		if (x == 0 && y <= 0) {
			return 0;
		}
		if (x == 0 && y > 0) {
			return Math.PI;
		}
		if (y == 0 && x > 0) {
			return Math.PI / 2;
		}
		if (y == 0 && x < 0) {
			return Math.PI * 3 / 2;
		}
		if (x > 0 && y < 0) {
			return Math.atan(x / -y);
		}
		if (x > 0 && y > 0) {
			return Math.PI - Math.atan(x / y);
		}
		if (x < 0 && y > 0) {
			return Math.PI + Math.atan(-x / y);
		}
		if (x < 0 && y < 0) {
			return Math.PI * 2 - Math.atan(x / y);
		}
	}
	return Compass;
});

/**
 * Onyx Canvas Search Bar
 */
define("onyx/canvas/searchpanel", [ "jquery", "require", "d3/d3" ], function($,
		require) {

	var d3 = require("d3/d3");

	var SearchPanel = function(canvas, graph) {
		this.canvas = canvas;
		this.graph = graph;
		this.build(canvas.dom);
	}

	SearchPanel.prototype.build = function(pdom) {
		this.dom = $("<div class='onyx-canvas-searchpanel'></div>");
		this.dom.appendTo(pdom);
		this.layout = UI.createLayout({
			clazz : "onyx-canvas-searchpanel-layout",
			header : {
				height : 46
			},
			body : {

			},
			pdom : this.dom
		});
		this.layout.on("click", this.onClickBlank.bind(this));
		this.searchbox = UI.createSearchBox({
			clazz : "onyx-canvas-searchpanel-search",
			pdom : this.layout.getHeader()
		});
		this.slideboard = UI.createSlideBoard({
			type : "entity",
			datas : this.queryEntities.bind(this),
			pdom : this.layout.getBody()
		});
		this.slideboard.on("clickblank", this.onClickBlank.bind(this));
		this.slideboard.on("clickitem", this.onClickItem.bind(this));
	}

	SearchPanel.prototype.onClickBlank = function(event) {
		this.hide();
	}

	SearchPanel.prototype.onClickItem = function(event, item) {
		var node = $(item).data();
		var nodex = (this.pos && this.pos.x) || this.graph.getWidth() / 2
				- this.graph.getX();
		var nodey = (this.pos && this.pos.y) || this.graph.getHeight() / 2
				- this.graph.getY();
		this.graph.addNode($.extend({
			x : nodex,
			y : nodey
		}, node));
		this.hide();
	}

	SearchPanel.prototype.queryEntities = function() {
		return Api.entity(this.canvas.kid).list();
	}

	SearchPanel.prototype.show = function(pos) {
		this.pos = pos;
		this.dom.css("display", "block");
	}

	SearchPanel.prototype.hide = function() {
		this.dom.css("display", "none");
	}

	SearchPanel.prototype.render = function() {

	}
	return SearchPanel;
});

/**
 * Onyx Canvas Search Bar
 */
define(
		"onyx/canvas/cornerbutton",
		[ "jquery", "require" ],
		function($, require) {

			var CornerButton = function(canvas, graph, searchpanel) {
				this.canvas = canvas;
				this.graph = graph;
				this.searchpanel = searchpanel;
				this.build(canvas.dom);
			}

			CornerButton.prototype.build = function(pdom) {
				this.dom = $("<div class='onyx-canvas-cornerbutton'></div>");
				this.dom.appendTo(pdom);
				this.icon = $("<span class='onyx-canvas-cornerbutton-icon iconfont icon-search'></span>");
				this.icon.appendTo(this.dom);
				// this.dom.on("mouseover", this.onMouseOver.bind(this));
				this.dom.on("click", this.onClick.bind(this));
				this.dom.on("mouseover", this.onMouseOver.bind(this));
				this.dom.on("mouseout", this.onMouseOut.bind(this));

			}

			CornerButton.prototype.onClick = function(event) {
				this.searchpanel.show();
			}

			CornerButton.prototype.onMouseOver = function(event) {
				this.dom.addClass("onyx-canvas-cornerbutton-over");
			}

			CornerButton.prototype.onMouseOut = function(event) {
				this.dom.removeClass("onyx-canvas-cornerbutton-over");
			}

			return CornerButton;
		});

//
// Canvas.prototype.buildBackGround = function(dom, w, h) {
// var background = $("<canvas
// style='z-index:-10000;position:absolute;top:0;left:0;background-image:radial-gradient(circle
// at 50% 50%, #356E8E, #315B85);'></canvas>");
// background.appendTo(dom);
// var canvas = background[0];
// var ctx = canvas.getContext("2d");
// // 设置画布宽高与窗口宽高一样
// canvas.width = w;
// canvas.height = h;
// // 随机数函数
// function fnRandom(min, max) {
// return parseInt((max - min) * Math.random() + min + 1)
// }
// function Round() {
// this.r = fnRandom(10, 30);
// this.diam = this.r * 2;
// // 随机位置
// var x = fnRandom(0, canvas.width - this.r);
// this.x = x < this.r ? this.r : x;
// var y = fnRandom(0, canvas.height - this.r);
// this.y = y < this.r ? this.r : y
// // 随机速度
// var speed = fnRandom(2, 4) / 10;
// this.speedX = fnRandom(0, 4) > 2 ? speed : -speed;
// this.speedY = fnRandom(0, 4) > 2 ? speed : -speed;
// // 颜色
// this.color = "#F2F2F2";
// }
// Round.prototype.draw = function() {
// // 绘制函数
// ctx.fillStyle = this.color;
// ctx.beginPath();
// ctx.arc(this.x, this.y, this.r, 0, Math.PI * 2, true);
// ctx.closePath();
// ctx.fill();
// }
// Round.prototype.move = function() {
// this.x += this.speedX;
// if (this.x > canvas.width - this.r) {
// this.speedX *= -1;
// this.x = this.r2;
// } else if (this.x < this.r) {
// this.x = canvas.width - this.r;
// }
// this.y += this.speedY;
// if (this.y > canvas.height - this.r) {
// this.speedY *= -1;
// this.y = this.r;
// } else if (this.y < this.r) {
// this.y = canvas.height - this.r;
// }
// }
// // 使用Round
// var allRound = [];
// function initRound() {
// // 初始化30个圆形对象,放到数组中
// for (var i = 0; i < 10; i++) {
// var obj = new Round();
// obj.draw();
// obj.move();
// allRound.push(obj);
// }
// }
// initRound();
// var dxdy = [];
// function roundMove() {
// ctx.clearRect(0, 0, canvas.width, canvas.height);
// // 遍历所有的圆形对象,让对象自己重绘,移动
// for (var i = 0; i < allRound.length; i++) {
// var round = allRound[i];
// round.draw();
// round.move();
// dxdy[i] = {
// dx : round.x,
// dy : round.y
// };
// var dx = dxdy[i].dx;
// var dy = dxdy[i].dy;
// for (var j = 0; j < i; j++) {
// var sx = dxdy[j].dx;
// var sy = dxdy[j].dy;
// l = Math.sqrt((dx - sx) * (dx - sx) + (dy - sy)
// * (dy - sy));
// var C = 1 / l * 7 - 0.009;
// var o = C > 0.03 ? 0.03 : C;
// ctx.strokeStyle = 'rgba(255,255,255,' + o + ')';
// ctx.beginPath();
// ctx.lineWidth = 5;
// ctx.moveTo(dxdy[i].dx, dxdy[i].dy);
// ctx.lineTo(dxdy[j].dx, dxdy[j].dy);
// ctx.closePath();
// ctx.stroke();
// }
// }
// window.requestAnimationFrame(roundMove)
// }
//
// roundMove();
// }
