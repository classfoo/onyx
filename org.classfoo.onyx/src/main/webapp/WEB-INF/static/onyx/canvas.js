/**
 * Onyx Canvas Class
 */
define(
		"onyx/canvas",
		[ "jquery", "require", "css!./canvas.css", "d3/d3", ,
				"onyx/canvas/graph", "onyx/canvas/searchpanel",
				"onyx/canvas/cornerbutton" ],
		function($, require) {

			var d3 = require("d3/d3");

			var Graph = require("onyx/canvas/graph");

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
				this.dom.on("dblclickgraph", this.onDblClickGraph.bind(this));
				this.dom.on("contextmenu", this.onContextMenu.bind(this));
				// init canvas
				this.canvas = document.querySelector(".onyx-canvas-canvas");
				this.context = this.canvas.getContext("2d");
				this.graph = new Graph(this);
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

			Canvas.prototype.onDblClickGraph = function(event, pos) {
				this.searchPanel.show(pos);
			}

			Canvas.prototype.onContextMenu = function(event) {
				return false;
			}

			Canvas.prototype.fire = function(event, options) {
				return this.canvasDom.trigger(event, options);
			}

			Canvas.prototype.on = function(event, func) {
				this.canvasDom.on(event, func);
			}

			Canvas.prototype.render = function() {
				this.context.clearRect(0, 0, this.width, this.height);
				this.graph.render();
				console.log("render");
			}

			return Canvas;
		});

/**
 * Onyx Canvas Graph
 */
define(
		"onyx/canvas/graph",
		[ "jquery", "require", "d3/d3", "onyx/canvas/compass",
				"onyx/canvas/node", "onyx/canvas/relationnode" ],
		function($, require) {

			var d3 = require("d3/d3");

			var Compass = require("onyx/canvas/compass");

			var RelationNode = require("onyx/canvas/relationnode");

			var RADIUS = 20;

			var Graph = function(canvas) {
				this.canvas = canvas;
				this.context = canvas.getContext();
				this.compass = new Compass(this.canvas, this);
				this.relationNode = new RelationNode(this.canvas, this);
				this.canvas.on("dblclick", this.onDblClick.bind(this));
				this.canvas.on("click", this.onClick.bind(this));
				this.canvas.on("clickmenu", this.onClickMenu.bind(this));
				this.canvas
						.on("clickrelation", this.onClickRelation.bind(this));
				this.canvas.on("mousemove", this.onMouseMove.bind(this));
				this.nodes = [];
				this.nodeMap = {};
				this.links = [];
				this.linkSourceMap = {};
				this.linkTargetMap = {};
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
				// this.initSimulations();
				this.initDrag();
			}

			Graph.prototype.initDrag = function() {
				var d3Canvas = d3.select(this.canvas.getCanvas());
				d3Canvas.call(d3.drag().container(this.canvas.getCanvas())
						.subject(this.dragsubject.bind(this)).on("start",
								this.dragstarted.bind(this)).on("drag",
								this.dragging.bind(this)).on("end",
								this.dragended.bind(this)));
			}

			Graph.prototype.dragsubject = function() {
				var node = this.findNode(d3.event.x, d3.event.y);
				if (!node) {
					return this.graph;
				}
				if (this.selects) {
					if (this.selects.includes(node)) {
						return {
							id : "selects",
							selects : this.selects
						};
					}
				}
				node.dragged = true;
				return node;
			}

			Graph.prototype.dragstarted = function() {
				var subject = d3.event.subject;
				if (subject.id == "graph") {
					subject.sx = d3.event.x;
					subject.sy = d3.event.y;
				} else if (subject.id == "selects") {
					var selects = subject.selects;
					selects.sx = d3.event.x;
					selects.sy = d3.event.y;
					for (var i = 0; i < selects.length; i++) {
						var node = selects[i];
						node.fx = node.x;
						node.fy = node.y;
					}
				} else {
					subject.fx = subject.x;
					subject.fy = subject.y;
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
				} else if (subject.id == "selects") {
					var selects = subject.selects;
					var offsetX = d3.event.x - selects.sx;
					var offsetY = d3.event.y - selects.sy;
					for (var i = 0; i < selects.length; i++) {
						var node = selects[i];
						node.fx += offsetX;
						node.fy += offsetY;
					}
					selects.sx = d3.event.x;
					selects.sy = d3.event.y;
					this.simulation.alpha(0);
					this.simulation.tick();
					this.canvas.render();
				} else {
					subject.fx = d3.event.x;
					subject.fy = d3.event.y;
					this.simulation.alpha(0);
					this.simulation.tick();
					this.canvas.render();
				}
			}

			Graph.prototype.dragended = function() {
				var subject = d3.event.subject;
				if (subject.id == "graph") {
					this.canvas.render();
				} else {
					subject.fx = null;
					subject.fy = null;
					subject.dragged = false;
				}
			}

			Graph.prototype.initSimulations = function(links) {
				var width = this.canvas.width;
				var height = this.canvas.height;
				this.simulationNodes = this.initSimulationNodes();
				this.simulation = d3.forceSimulation(this.simulationNodes);
				this.simulation.force("collide", d3.forceCollide(
						this.simulationNodes).radius(function(d) {
					return (d.radius || RADIUS) + 18;
				}).iterations(1).strength(1));
				// this.simulation.on("tick", this.onTick.bind(this));
				var simulationLinks = this.initSimulationLinks(links);
				if (simulationLinks && simulationLinks.length > 0) {
					this.simulation.force("link", d3.forceLink(simulationLinks)
							.id(function(d) {
								return d.id;
							}).iterations(1).strength(1).distance(
									function(link) {
										return link.distance;
									}));
				}
				this.simulation.stop();
				this.doTicks();
			}

			Graph.prototype.initSimulationNodes = function() {
				var nodes = [];
				for (var i = 0; i < this.nodes.length; i++) {
					var node = this.nodes[i];
					nodes.push(node);
				}
				var node = this.relationNode.initSimulationNode();
				if (node) {
					nodes.push(node);
				}
				return nodes;
			}

			Graph.prototype.initSimulationLinks = function(links) {
				var result = [];
				var link = this.relationNode.initSimulationLink();
				if (link) {
					result.push(link);
				}
				if (!links) {
					return result;
				}
				for (var i = 0; i < links.length; i++) {
					var link = links[i];
					var source = this.getNode(link.source);
					var target = this.getNode(link.target);
					var distance = Math.sqrt((source.x - target.x)
							* (source.x - target.x) + (source.y - target.y)
							* (source.y - target.y));
					result.push($.extend({
						distance : distance
					}, link));
				}
				return result;
			}

			Graph.prototype.onTick = function() {
				this.canvas.render();
			}

			Graph.prototype.findNode = function(eventX, eventY) {
				var x = eventX - this.graph.x;
				var y = eventY - this.graph.y;
				var nodes = this.simulationNodes ? this.simulationNodes
						: this.nodes;
				for (var i = 0; i < nodes.length; i++) {
					var node = nodes[i];
					var nodex = node.x;
					var nodey = node.y;
					var screenX = this.toScreenX(nodex);
					var radius = node.radius || RADIUS;
					if (screenX <= -radius || screenX >= this.width + radius) {
						continue;
					}
					var screenY = this.toScreenY(nodey);
					if (screenY <= -radius || screenY >= this.height + radius) {
						continue;
					}
					var distance = Math.sqrt((x - nodex) * (x - nodex)
							+ (y - nodey) * (y - nodey));
					if (distance <= radius) {
						return node;
					}
				}
			}

			Graph.prototype.doTicks = function() {
				var self = this;
				function drawFrame() {
					if (self.simulation.alpha() < 0.05) {
						return;
					}
					self.simulation.tick();
					self.canvas.render();
					requestAnimFrame(drawFrame);
				}
				requestAnimFrame(drawFrame);
			}

			Graph.prototype.onClick = function(event) {
				clearTimeout(this.intervalTimer);
				var self = this;
				this.intervalTimer = setTimeout(function() {
					self._onClick(event);
				}, 100);
			}

			Graph.prototype._onClick = function(event) {
				event.stopPropagation();
				if (this.compass.onClick(event)) {
					this.canvas.render();
					return;
				}
				if (this.relationNode.onClick(event)) {
					this.canvas.render();
					return;
				}
				var item = this.findNode(event.offsetX, event.offsetY);
				if (item == null) {
					if (this.selected || this.lastselected) {
						this.lastselected = null;
						this.selected = null;
						this.onClickGraph(event);
						this.canvas.fire("clickgraph");
						this.canvas.render();
						return;
					}
					this.canvas.fire("clickgraph");
					this.canvas.render();
					return;
				}
				if (this.selected) {
					var sel = this.selected[item.id];
					if (sel) {
						this.selected[item.id] = false;
						this.lastselected = null;
						this.onClickGraph(event);
						this.canvas.fire("clickgraph");
						this.canvas.render();
						return;
					} else {
						this.selected[item.id] = true;
						this.lastselected = item.id;
						this.onClickNode(event, item);
						this.canvas.fire("clicknode", item);
						this.canvas.render();
						return;
					}
				} else {
					this.selected = {};
					this.selected[item.id] = true;
					this.lastselected = item.id;
					this.onClickNode(event, item);
					this.canvas.fire("clicknode", item);
					this.canvas.render();
					return;
				}
			}

			Graph.prototype.onClickNode = function(event, node) {
				this.compass.show(node);
			}

			Graph.prototype.onClickGraph = function(event, node) {
				this.hideTools();
			}

			Graph.prototype.onClickMenu = function(event, menu) {
				this.hideTools();
				if (menu.button.id == "add") {
					this.searchPanel.show(menu.node);
					return;
				}
				if (menu.button.id == "links") {
					this.showRelationNode(menu.node);
					return;
				}
			}

			Graph.prototype.onClickRelation = function(event, options) {
				this.hideTools();
				var relation = options.relation;
				var node = options.node;
				var distance = Math.sqrt(128 * 128 + 128 * 128);
				var nodex = node.x + 128;
				var nodey = node.y - 128;
				var nodes = [ {
					id : Utils.guid(),
					name : "节点1",
					x : nodex,
					y : nodey
				}, {
					id : Utils.guid(),
					name : "节点2",
					x : nodex,
					y : nodey
				}, {
					id : Utils.guid(),
					name : "节点3",
					x : nodex,
					y : nodey
				}, {
					id : Utils.guid(),
					name : "节点4",
					x : nodex,
					y : nodey
				}, {
					id : Utils.guid(),
					name : "节点5",
					x : nodex,
					y : nodey
				} ];
				var links = [ {
					target : nodes[0].id,
					source : node.id,
					distance : distance
				}, {
					target : nodes[1].id,
					source : node.id,
					distance : distance
				}, {
					target : nodes[2].id,
					source : node.id,
					distance : distance
				}, {
					target : nodes[3].id,
					source : node.id,
					distance : distance
				}, {
					target : nodes[4].id,
					source : node.id,
					distance : distance
				} ];
				this.addNodesAndLinks(nodes, links);
			}

			Graph.prototype.onDblClick = function(event) {
				clearTimeout(this.intervalTimer);
				event.stopPropagation();
				if (this.compass.onDblClick(event)) {
					this.canvas.render();
					return;
				}
				var item = this.findNode(event.offsetX, event.offsetY);
				if (item == null) {
					this.canvas.fire("dblclickgraph", {
						x : event.offsetX - this.graph.x,
						y : event.offsetY - this.graph.y
					});
					this.canvas.render();
					return;
				}
				this.onDblClickNode(event, item);
				this.canvas.fire("dblclicknode", item);
				this.canvas.render();
				return;
			}

			Graph.prototype.onDblClickNode = function(event, node) {
				var selects = [ node ];
				var targets = this.getTargetNodes(node.id, true);
				this.selectNodes(selects.concat(targets));
			}

			Graph.prototype.onMouseMove = function(event) {
				event.stopPropagation();
				if (this.compass.onMouseMove(event)) {
					this.canvas.render();
					return;
				}
				if (this.relationNode.onMouseMove(event)) {
					this.canvas.render();
					return;
				}
				var item = this.findNode(event.offsetX, event.offsetY);
				if (!item) {
					if (this.currentNode) {
						this.currentNode.mouseovered = false;
						this.currentNode = null;
						this.canvas.render();
						return;
					}
					return;
				}
				if (item.mouseovered) {
					return;
				}
				item.mouseovered = true;
				this.currentNode = item;
				this.canvas.render();
				return;
			}

			Graph.prototype.render = function() {
				this.renderLinks();
				this.renderNodes();
				this.compass.render();
				this.relationNode.render();
			}

			Graph.prototype.renderLinks = function() {
				for (var i = 0, link, n = this.links.length; i < n; i++) {
					link = this.links[i];
					this.renderLink(link);
				}
			}

			Graph.prototype.renderLink = function(link) {
				this.context.save();
				// render line;
				this.context.beginPath();
				this.context.globalAlpha = 0.5;
				var source = this.getNode(link.source);
				var target = this.getNode(link.target);
				this.context.moveTo(this.toScreenX(source.x), this
						.toScreenY(source.y));
				this.context.lineTo(this.toScreenX(target.x), this
						.toScreenY(target.y));
				this.context.lineWidth = 3;
				this.context.strokeStyle = "pink";
				this.context.stroke();
				// render text rectangle
				var middlex = this.toScreenX((target.x + source.x) / 2);
				var middley = this.toScreenY((target.y + source.y) / 2);
				var angle = Math
						.atan2(target.y - source.y, target.x - source.x);
				this.context.translate(middlex, middley);
				this.context.rotate(angle);
				this.context.globalAlpha = 1;
				this.context.fillStyle = "pink";
				this.context.fillRect(-16, -8, 32, 16);

				// render text
				this.context.font = "12px 微软雅黑";
				this.context.textAlign = "center"
				this.context.fillStyle = "white";
				this.context.fillText("关系", 0, 4);
				this.context.restore();
			}

			Graph.prototype.renderNodes = function() {
				for (var i = 0, node, n = this.nodes.length; i < n; ++i) {
					node = this.nodes[i];
					var type = node.type || 'node';
					switch (type) {
					case 'node': {
						this.renderNode(node);
						continue;
					}
					}
				}
			}

			Graph.prototype.renderNode = function(node) {
				var dragged = node.dragged;
				var selected = node.selected;
				var mouseovered = node.mouseovered;
				var nodex = node.x + this.graph.x;
				var nodey = node.y + this.graph.y;
				var radius = node.radius || RADIUS;
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
				// draw circle
				this.context.beginPath();
				this.context.arc(nodex, nodey, radius + 5, 0, 2 * Math.PI);
				if (dragged) {
					this.context.strokeStyle = "#C5DBF0";
					this.context.setLineDash([ 3, 3 ]);
				} else if (selected) {
					this.context.setLineDash([ 3, 3 ]);
					this.context.strokeStyle = "orange";
				} else {
					this.context.strokeStyle = "#C5DBF0";
				}
				this.context.lineWidth = 3;
				this.context.stroke();
				// draw icon
				this.context.font = "26px iconfont";
				this.context.textAlign = "center"
				this.context.fillStyle = mouseovered ? "#FFFFFF" : "#000000";
				this.context.fillText("\ue60a", nodex, nodey + radius / 2);
				// draw label
				this.context.font = "14px 微软雅黑";
				this.context.textAlign = "center";
				this.context.fillStyle = "#FFFFFF";
				this.context.fillText(node.name || node.id, nodex, nodey
						+ radius + 20);
				this.context.restore();
			}

			/**
			 * get node by id
			 * 
			 * @param nodeid
			 */
			Graph.prototype.getNode = function(nodeid) {
				return this.nodeMap[nodeid];
			}

			/**
			 * get target node by links
			 * 
			 * @param sourceid
			 * @param recurse
			 */
			Graph.prototype.getTargetNodes = function(sourceid, recurse) {
				if (!recurse) {
					return this.getTargetNodesDirect(sourceid);
				}
				return this.getTargetNodesRecurse(sourceid);
			}

			/**
			 * get direct target nodes
			 * 
			 * @param sourceid
			 */
			Graph.prototype.getTargetNodesDirect = function(sourceid) {
				var links = this.getLinksBySource(sourceid);
				if (!links) {
					return [];
				}
				var result = [];
				for (var i = 0; i < links.length; i++) {
					var link = links[i];
					var target = this.getNode(link.target);
					result.push(target);
				}
				return result;
			}

			/**
			 * get target nodes recurse
			 * 
			 * @param soruceid
			 */
			Graph.prototype.getTargetNodesRecurse = function(sourceid) {
				var result = [];
				this._getTargetNodesRecurse(sourceid, result);
				return result;
			}

			Graph.prototype._getTargetNodesRecurse = function(sourceid, result) {
				var targets = this.getTargetNodesDirect(sourceid);
				for (var i = 0; i < targets.length; i++) {
					var target = targets[i];
					result.push(target);
					this._getTargetNodesRecurse(target.id, result);
				}
			}

			/**
			 * get source nodes by links
			 * 
			 * @param targetid
			 * @param recurse
			 */
			Graph.prototype.getSourceNodes = function(targetid, recurse) {
				if (!recurse) {
					return this.getSourceNodesDirect(targetid);
				}
				return this.getSourceNodesRecurse(targetid);
			}

			/**
			 * get direct source nodes
			 * 
			 * @param targetid
			 */
			Graph.prototype.getSourceNodesDirect = function(targetid) {
				var links = this.getLinksByTarget(targetid);
				if (!links) {
					return [];
				}
				var result = [];
				for (var i = 0; i < links.length; i++) {
					var link = links[i];
					var source = this.getNode(link.source);
					result.push(source);
				}
				return result;
			}

			/**
			 * get source nodes recurse
			 * 
			 * @param targetid
			 */
			Graph.prototype.getSourceNodesRecurse = function(targetid) {
				var result = [];
				this._getSourceNodesRecurse(targetid, result);
				return result;
			}

			Graph.prototype._getSourceNodesRecurse = function(targetid, result) {
				var sources = this.getSourceNodesDirect(targetid);
				for (var i = 0; i < sources.length; i++) {
					var source = sources[i];
					result.push(source);
					this._getSourceNodesRecurse(source.id, result);
				}
			}

			Graph.prototype.getLink = function(sourceid, targetid) {

			}

			Graph.prototype.getLinksBySource = function(sourceid) {
				return this.linkSourceMap[sourceid];
			}

			Graph.prototype.getLinksByTarget = function(targetid) {
				return this.linkTargetMap[targetid];
			}

			Graph.prototype.addNodesAndLinks = function(nodes, links) {
				if (nodes) {
					for (var i = 0; i < nodes.length; i++) {
						this._addNode(nodes[i]);
					}
				}
				if (links) {
					for (var i = 0; i < links.length; i++) {
						this._addLink(links[i]);
					}
				}
				this.initSimulations(links);
			}

			Graph.prototype.addNode = function(node) {
				this._addNode(node);
				this.initSimulations();
			}

			Graph.prototype._addNode = function(node) {
				this.nodes.push(node);
				this.nodeMap[node.id] = node;
			}

			Graph.prototype.addLink = function(link) {
				this._addLink(link);
				this.initSimulations();
			}

			Graph.prototype._addLink = function(link) {
				this.links.push(link);
				var sources = this.linkSourceMap[link.source];
				if (sources) {
					sources.push(link);
				} else {
					this.linkSourceMap[link.source] = [ link ];
				}
				var targets = this.linkTargetMap[link.target];
				if (targets) {
					targets.push(link);
				} else {
					this.linkTargetMap[link.target] = [ link ];
				}
				this.initSimulations();
			}

			Graph.prototype.selectNodes = function(nodes) {
				this.unselectNodes();
				this.selects = nodes;
				for (var i = 0; i < this.selects.length; i++) {
					this.selects[i].selected = true;
				}
			}

			Graph.prototype.unselectNodes = function() {
				if (!this.selects) {
					return;
				}
				for (var i = 0; i < this.selects.length; i++) {
					this.selects[i].selected = false;
				}
				this.selects = null;
			}

			Graph.prototype.showRelationNode = function(node) {
				this.relationNode.show(node);
				this.initSimulations();
			}

			Graph.prototype.getCompass = function() {
				return this.compass;
			}

			Graph.prototype.showCompass = function(node) {
				this.hideTools();
				this.compass.show(node);
			}

			Graph.prototype.hideTools = function() {
				this.compass.hide();
				if (this.relationNode.hide()) {
					this.initSimulations();
				}
				if (this.selects) {
					this.unselectNodes();
				}
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

			Graph.prototype.toScreenX = function(x) {
				return x + this.graph.x;
			}

			Graph.prototype.toScreenY = function(y) {
				return y + this.graph.y;
			}

			Graph.prototype.toScreenWidth = function(width) {
				return width;
			}

			Graph.prototype.toScreenHeight = function(height) {
				return height;
			}

			Graph.prototype.toStandardX = function(x) {
				return x - this.graph.x;
			}

			Graph.prototype.toStandardY = function(y) {
				return y - this.graph.y;
			}
			return Graph;
		});

/**
 * Onyx Canvas Relation Dialog
 */
define("onyx/canvas/node", [ "jquery", "require" ],
		function($, require) {

			var Node = function(canvas, node) {
				this.canvas = canvas;
				this.context = canvas.getContext();
				this.node = node;
				this.x = node.x;
				this.y = node.y;
				this.radius = 20;
			}

			Node.prototype.render = function() {
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
				// draw circle
				this.context.beginPath();
				this.context.arc(nodex, nodey, radius + 5, 0, 2 * Math.PI);
				if (dragged) {
					this.context.strokeStyle = "#C5DBF0";
					this.context.setLineDash([ 3, 3 ]);
				} else if (selected) {
					this.context.strokeStyle = "#C5DBF0";
				} else {
					this.context.strokeStyle = "#C5DBF0";
				}
				this.context.lineWidth = 3;
				this.context.stroke();
				// draw icon
				this.context.font = "26px iconfont";
				this.context.textAlign = "center"
				if (mouseovered) {
					this.context.fillStyle = "#FFFFFF";
				} else {
					this.context.fillStyle = "#000000";
				}
				this.context.fillText("\ue60a", nodex, nodey + radius / 2);
				// draw label
				this.context.font = "14px 微软雅黑";
				this.context.textAlign = "center"
				this.context.fillStyle = "#FFFFFF";
				this.context.fillText(node.name || node.id, nodex, nodey
						+ radius + 20);
				this.context.restore();
			}

			Node.prototype.setX = function(x) {
				this.x = x;
				return this;
			}

			Node.prototype.getX = function() {
				return this.x;
			}

			Node.prototype.setY = function(y) {
				this.y = y;
				return this;
			}

			Node.prototype.getY = function() {
				return this.y;
			}

			Node.prototype.setRadius = function(radius) {
				this.radius = radius;
				return this;
			}

			Node.prototype.getRadius = function() {
				return this.radius;
			}

			return Node;
		});
/**
 * Onyx Canvas Relation Dialog
 */
define("onyx/canvas/relationnode", [ "jquery", "require", "d3/d3" ], function(
		$, require) {

	var d3 = require("d3/d3");

	var RelationNode = function(canvas, graph) {
		this.canvas = canvas;
		this.context = canvas.getContext();
		this.graph = graph;
	}

	RelationNode.prototype.initSimulationNode = function(node) {
		if (!this.node) {
			return null;
		}
		this.simulationNode = {
			id : "relation",
			x : this.node.x + 128,
			y : this.node.y - 128,
			radius : -18
		};
		return this.simulationNode;
	}

	RelationNode.prototype.initSimulationLink = function() {
		if (!this.node) {
			return null;
		}
		this.simulationLink = {
			source : this.node.id,
			target : "relation",
			distance : Math.sqrt(128 * 128 + 128 * 128)
		};
		return this.simulationLink;
	}

	RelationNode.prototype.getX = function() {
		return this.node.x + 128;
	}

	RelationNode.prototype.getY = function() {
		return this.node.y - 128;
	}

	RelationNode.prototype.show = function(node) {
		this.node = node;
		var pack = d3.pack().size([ 256, 256 ]).padding(3);
		var data = {
			name : "root",
			children : [ {
				name : "电影"
			}, {
				name : "导演"
			}, {
				name : "父母"
			}, {
				name : "子女"
			}, {
				name : "亲戚"
			}, {
				name : "公司"
			}, {
				name : "广告"
			}, {
				name : "综艺节目"
			}, {
				name : "房产"
			}, {
				name : "兄弟"
			}, {
				name : "朋友"
			}, {
				name : "情敌"
			}, {
				name : "丈夫"
			}, {
				name : "绯闻"
			}, {
				name : "酒庄"
			} ]
		};
		this.root = d3.hierarchy(data).sum(function(d) {
			return d.name.length;
		}).sort(function(a, b) {
			return 1;
		});
		pack(this.root);
	}

	RelationNode.prototype.hide = function() {
		var hasNode = this.node != null;
		this.node = null;
		this.simulationNode = null;
		this.simulationLink = null;
		return hasNode;
	}

	RelationNode.prototype.render = function() {
		if (!this.node) {
			return;
		}
		this.renderLink();
		var node = this.simulationNode;
		this.renderNode(node);
		this.renderRelations(node);
	}

	RelationNode.prototype.renderLink = function(link) {
		this.context.save();
		this.context.beginPath();
		var source = this.node;
		var target = this.simulationNode;
		this.context.moveTo(this.graph.toScreenX(source.x), this.graph
				.toScreenY(source.y));
		this.context.lineTo(this.graph.toScreenX(target.x), this.graph
				.toScreenY(target.y));
		this.context.globalAlpha = 0.2;
		this.context.setLineDash([ 3, 3 ]);
		this.context.lineWidth = 3;
		this.context.strokeStyle = "#FFFFFF";
		this.context.stroke();
		this.context.restore();
	}

	RelationNode.prototype.renderNode = function(node) {
		var nodex = this.graph.toScreenX(node.x);
		var nodey = this.graph.toScreenY(node.y);
		// draw node
		this.context.save();
		this.context.globalAlpha = 0.2;
		this.context.beginPath();
		this.context.moveTo(nodex, nodey);
		this.context.arc(nodex, nodey, 128, 0, 2 * Math.PI);
		this.context.fillStyle = this.active ? "#FFFFFF" : "#5381B2";
		this.context.fill();
		// draw circle
		this.context.beginPath();
		this.context.arc(nodex, nodey, 128 + 5, 0, 2 * Math.PI);
		this.context.strokeStyle = "#FFFFFF";
		this.context.setLineDash([ 3, 3 ]);
		this.context.lineWidth = 3;
		this.context.stroke();
		this.context.restore();
	}

	RelationNode.prototype.renderRelations = function(node) {
		var self = this;
		$.each(this.root.children, function(index, item) {
			self.renderRelation(node, item);
		});
	}

	RelationNode.prototype.renderRelation = function(node, item) {
		// draw node
		var nodex = this.graph.toScreenX(node.x + item.x - 128);
		var nodey = this.graph.toScreenY(node.y + item.y - 128);
		var noder = item.r;
		this.context.save();
		this.context.globalAlpha = 0.8;
		this.context.beginPath();
		this.context.moveTo(nodex, nodey);
		this.context.arc(nodex, nodey, noder, 0, 2 * Math.PI);
		this.context.fillStyle = item.active ? "orange" : "red";
		this.context.fill();

		this.context.font = "12px 微软雅黑";
		this.context.textAlign = "center"
		this.context.fillStyle = "#FFFFFF";
		this.context.fillText(item.data.name, nodex, nodey + 4);
		this.context.restore();
	}

	RelationNode.prototype.onClick = function(event) {
		if (this.current) {
			this.canvas.fire("clickrelation", {
				relation : this.current,
				node : this.node
			});
			return true;
		}
		if (this.active) {
			return true;
		}
		return false;
	}

	RelationNode.prototype.onMouseMove = function(event) {
		if (!this.node) {
			this.clearMouseMove();
			return false;
		}
		var x = this.graph.toStandardX(event.offsetX);
		var y = this.graph.toStandardY(event.offsetY);
		var nodex = this.getX();
		var nodey = this.getY();
		// check if inside the node
		var distance = Math.sqrt((nodex - x) * (nodex - x) + (nodey - y)
				* (nodey - y));
		var active = distance <= 128;
		var changed = active === this.active;
		if (!active) {
			this.clearMouseMove();
			return changed;
		}
		this.active = true;
		// check if inside the relation bubble
		var children = this.root.children;
		var current = null;
		for (var i = 0; i < children.length; i++) {
			var child = children[i];
			var childx = child.x + this.simulationNode.x - 128;
			var childy = child.y + this.simulationNode.y - 128;
			var childdistance = Math.sqrt((childx - x) * (childx - x)
					+ (childy - y) * (childy - y));
			var childactive = childdistance <= child.r;
			child.active = childactive;
			if (childactive) {
				current = child;
			}
		}
		this.current = current;
		return changed;
	}

	RelationNode.prototype.clearMouseMove = function(event) {
		if (this.current) {
			this.current.active = false;
		}
		this.current = null;
		this.active = false;
	}
	return RelationNode;
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
			children : [ {
				id : "all",
				length : 1,
				name : "全选"
			}, {
				id : "others",
				length : 1,
				name : "反选"
			}, {
				id : "sources",
				length : 1,
				name : "连入"
			}, {
				id : "targets",
				length : 1,
				name : "连出"
			} ]
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
