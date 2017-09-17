/**
 * Onyx Canvas Class
 */
define(
		"onyx/canvas",
		[ "jquery", "require", "css!./canvas.css", "d3/d3", ,
				"onyx/canvas/graph", "onyx/canvas/searchpanel",
				"onyx/canvas/rightpanel", "onyx/canvas/cornerbutton" ],
		function($, require) {

			var d3 = require("d3/d3");

			var Graph = require("onyx/canvas/graph");

			var SearchPanel = require("onyx/canvas/searchpanel");

			var CornerButton = require("onyx/canvas/cornerbutton");

			var RightPanel = require("onyx/canvas/rightpanel");

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
				this.progress = $("<div id='progress'></div>");
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
				this.rightPanel = new RightPanel(this);
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

			Canvas.prototype.docmd = function(cmd, options) {
				switch (cmd) {
				case "view": {
					return this.docmd_view(options);
				}
				}
				return null;
			}

			Canvas.prototype.docmd_view = function(options) {
				this.rightPanel.show(options);
			}

			return Canvas;
		});

/**
 * Onyx Canvas Graph
 */
define(
		"onyx/canvas/graph",
		[ "jquery", "require", "d3/d3", "onyx/canvas/compass",
				"onyx/canvas/relationnode" ],
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

			/**
			 * find node by event position
			 */
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
				}, 300);
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
					this.onClickGraph(event);
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
				this.compass.showNodeMenu(node);
			}

			Graph.prototype.onClickGraph = function(event, node) {
				this.hideTools();
			}

			Graph.prototype.onClickMenu = function(event, menu) {
				this.hideTools();
				if (menu.button.id === "add") {
					this.searchPanel.show(menu.node);
					return;
				}
				if (menu.button.id === "links") {
					this.showRelationNode(menu.node);
					return;
				}
				if (menu.button.id === "radar") {
					this.showRadar(menu.node);
					return;
				}
				if (menu.button.id === "remove") {
					this.removeNode(menu.node);
					return;
				}
				this.canvas.docmd(menu.button.id, menu.node);
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
				selects = selects.concat(targets)
				this.selectNodes(selects);
				this.compass.showSelectsMenu(node, selects);
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
				this.context.moveTo(-16, -8);
				this.context.lineTo(-16, 8);
				this.context.lineTo(16, 8);
				this.context.lineTo(20, 0);
				this.context.lineTo(16, -8);
				// this.context.fillRect(-16, -8, 32, 16);
				this.context.fill();
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
				radius = mouseovered || selected ? (radius + 5) : radius;
				// draw node
				// draw image or icon
				this.context.save();
				this.context.beginPath();
				this.context.moveTo(nodex + radius, nodey);
				this.context.arc(nodex, nodey, radius, 0, 2 * Math.PI);
				var image = this.getImage(node.id);
				if (image) {
					this.context.clip();
					this.context.drawImage(image, nodex - radius, nodey
							- radius, radius * 2, radius * 2);
					this.context.closePath();
				} else {
					this.context.font = "26px iconfont";
					this.context.textAlign = "center";
					this.context.fillStyle = "#C5DBF0";
					this.context.fillText("\ue60a", nodex, nodey + radius / 2);
					this.context.fill();
					this.context.closePath();
				}
				this.context.restore();
				this.context.save();
				// draw circle
				this.context.beginPath();
				this.context.arc(nodex, nodey, radius + 5, 0, 2 * Math.PI);
				if (dragged) {
					this.context.strokeStyle = "#C5DBF0";
					this.context.setLineDash([ 3, 3 ]);
				} else if (selected) {
					this.context.setLineDash([ 3, 3 ]);
					this.context.strokeStyle = "pink";
				} else if (mouseovered) {
					this.context.strokeStyle = "pink";
				} else {
					this.context.strokeStyle = "#C5DBF0";
				}
				this.context.lineWidth = 3;
				this.context.stroke();
				// draw label
				this.context.font = "14px 微软雅黑";
				this.context.textAlign = "center";
				this.context.fillStyle = "#FFFFFF";
				this.context.fillText(node.name || node.id, nodex, nodey
						+ radius + 20);
				this.context.restore();
			}

			Graph.prototype.getImage = function(id) {
				if (!this.images) {
					this.images = {};
				}
				var image = this.images[id];
				if (image) {
					return image;
				}
				if (!this.imageRequesting) {
					this.imageRequesting = {};
				}
				var request = this.imageRequesting[id];
				if (request) {// still in request
					return null;
				}
				this.imageRequesting[id] = true;
				// draw icon
				var self = this;
				Api.image().get(id).done(function(image) {
					self.images[id] = image;
					self.imageRequesting[id] = false;
				});
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
				this.canvas.docmd("view", node);
			}

			Graph.prototype._addNode = function(node) {
				this.nodes.push(node);
				this.nodeMap[node.id] = node;
			}

			/**
			 * remove node
			 */
			Graph.prototype.removeNode = function(node) {
				this._removeNode(node);
				this._removeLinksByNode(node);
				this.initSimulations();
			}

			Graph.prototype._removeNode = function(node) {
				var index = this.nodes.indexOf(node);
				if (index != -1) {
					this.nodes.splice(index, 1);
				}
				this.nodeMap[node.id] = null;
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
			}

			Graph.prototype.removeLink = function(link) {
				this._removeLink(link);
				this.initSimulations();
			}

			Graph.prototype._removeLink = function(link) {
				var index = this.links.indexOf(link);
				this._removeLinkByIndex(index);
			}

			Graph.prototype.removeLinksByNode = function(node) {
				this._removeLinkByNode(node);
				this.initSimulations();
			}

			Graph.prototype._removeLinksByNode = function(node) {
				for (var i = this.links.length - 1; i >= 0; i--) {
					var link = this.links[i];
					if (node.id != link.source && node.id != link.target) {
						continue;
					}
					this._removeLinksByIndex(i);
				}
			}

			Graph.prototype.removeLinksByIndex = function(index) {
				this.removeLinksByIndex(index);
				this.initSimulations();
			}

			Graph.prototype._removeLinksByIndex = function(index) {
				if (index == -1) {
					return;
				}
				var link = this.links.splice(index, 1);
				if (link && link.length == 1) {
					this.linkTargetMap[link[0].target] = null;
					this.linkSourceMap[link[0].source] = null;
				}
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
				this.compass.showNodeMenu(node);
			}

			Graph.prototype.showRadar = function(node) {
				var self = this;
				var count = 0;
				var t = d3.timer(function(gap) {
					self.context.save();
					self.context.beginPath();
					var radius = 40 + count++ * 20;
					self.context.arc(node.x, node.y, radius, 0, 2 * Math.PI);
					self.context.strokeStyle = "#C5DBF0";
					self.context.setLineDash([ 3, 3 ]);
					self.context.lineWidth = 1;
					self.context.stroke();
					self.context.restore();
					if (radius > self.width) {
						t.stop();
					}
				});
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

	var OUTERRADIUS = 160;

	var MIDDLERADIUS = 100;

	var INNERRADIUS = 40;

	var nodeMenu = [ {
		id : "radar",
		icon : '\ue6b4',
		length : 1,
		name : "雷达"
	}, {
		id : "links",
		icon : '\ue6b6',
		length : 1,
		name : "关系"
	}, {
		id : "view",
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

	var selectsMenu = [ {
		id : "raida",
		icon : '\ue6b4',
		length : 1,
		name : "多选"
	}, {
		id : "layout",
		icon : '\ue6b6',
		length : 1,
		name : "布局",
		children : [ {
			id : "layout-line",
			length : 1,
			name : "直线布局"
		}, {
			id : "layout-rectangle",
			length : 1,
			name : "矩阵布局"
		}, {
			id : "layout-round",
			length : 1,
			name : "圆形布局"
		}, {
			id : "layout-ball",
			length : 1,
			name : "球状布局"
		} ]
	}, {
		id : "analysis",
		icon : '\ue6ae',
		length : 1,
		name : "分析"
	}, {
		id : "mark",
		icon : '\ue6b3',
		length : 1,
		name : "标记"
	}, {
		id : "cancel",
		icon : '\ue6b5',
		length : 1,
		name : "取消"
	}, {
		id : "remove",
		icon : '\ue6b1',
		length : 1,
		name : "移除"
	}, {
		id : "group",
		icon : '\ue6b2',
		length : 1,
		name : "分组"
	}, {
		id : "expand",
		icon : '\ue6b0',
		length : 1,
		name : "展开"
	} ];

	var lineMenu = [];

	var Compass = function(canvas, graph) {
		this.graph = graph;
		this.canvas = canvas;
		this.context = canvas.getContext();
	}

	Compass.prototype.showNodeMenu = function(node) {
		this.hide();
		this.node = node;
		this.arcs = null;
		this.type = "node";
		this.center = null;
		this.show = true;
	}

	Compass.prototype.showLineMenu = function(line) {
		this.hide();
		this.node = line;
		this.arcs = null;
		this.type = "link";
		this.center = null;
		this.show = true;
	}

	Compass.prototype.showSelectsMenu = function(node, selects) {
		this.hide();
		this.node = node;
		this.selectNodes = selects;
		this.arcs = null;
		this.type = "selects";
		this.center = "选中" + selects.length + "项";
		this.show = true;
	}

	Compass.prototype.hide = function() {
		this.node = null;
		this.arcs = null;
		this.center = null;
		this.show = false;
		this.innerActive = null;
		this.outerActive = null;
		this.showOuter = false;
	}

	Compass.prototype.render = function() {
		if (!this.node) {
			return;
		}
		this.x = this.node.x + this.graph.getX();
		this.y = this.node.y + this.graph.getY();
		this.renderCenter();
		this.renderInner();
		this.renderOuter();
	}

	Compass.prototype.renderCenter = function() {
		if (!this.center) {
			return;
		}
		this.context.save();
		this.context.beginPath();
		this.context.globalAlpha = 0.8;
		// this.context.moveTo(this.x + 40, this.y);
		this.context.arc(this.x, this.y, 35, 0, 2 * Math.PI);
		var color = this.getColor();
		this.context.fillStyle = color;
		this.context.fill();

		this.context.font = "14px 微软雅黑";
		this.context.textAlign = "center"
		this.context.fillStyle = "#FFFFFF";
		this.context.fillText(this.center, this.x, this.y + 4);
		this.context.restore();
	}

	Compass.prototype.renderInner = function() {
		this.context.save();
		var arc = d3.arc().outerRadius(MIDDLERADIUS).innerRadius(INNERRADIUS)
				.padAngle(0.01).context(this.context);
		this.innerArcs = d3.pie().startAngle(-Math.PI / 8).endAngle(
				Math.PI * 2 - Math.PI / 8).value(function(d) {
			return d.length;
		})(this.getButtons());
		this.context.translate(this.x, this.y);
		this.context.globalAlpha = 0.8;
		this.innerArcs
				.forEach(this.renderArc.bind(this, this.innerActive, arc));
		this.context.restore();
	}

	Compass.prototype.renderOuter = function() {
		if (!this.innerActive) {
			this.showOuter = false;
			return;
		}
		var children = this.innerActive.button.children;
		if (!children) {
			this.showOuter = false;
			return;
		}
		this.showOuter = true;
		var arc = this.innerActive.arc;
		var middleAngle = (arc.startAngle + arc.endAngle) / 2;
		var halfAngle = (children.length * Math.PI / 6) / 2;
		this.context.save();
		var arc = d3.arc().outerRadius(OUTERRADIUS).innerRadius(
				MIDDLERADIUS + 3).padAngle(0.01).context(this.context);
		this.outerArcs = d3.pie().startAngle(middleAngle - halfAngle).endAngle(
				middleAngle + halfAngle).value(function(d) {
			return d.length;
		})(children);
		this.context.translate(this.x, this.y);
		this.context.globalAlpha = 0.8;
		this.outerArcs
				.forEach(this.renderArc.bind(this, this.outerActive, arc));
		this.context.restore();
	}

	Compass.prototype.renderArc = function(active, arc, d, i) {
		var button = d.data;
		this.context.beginPath();
		var isActive = active && button.id === active.id;
		arc.cornerRadius(isActive ? 2 : 4);
		arc(d);
		this.context.fillStyle = isActive ? "#FFFFFF" : this.getColor();
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
			this.context.fillStyle = isActive ? "#000000" : "#FFFFFF";
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
			this.context.fillStyle = isActive ? "#000000" : "#FFFFFF";
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
		if (!this.show) {
			return false;
		}
		var button = this.outerActive ? this.outerActive.button
				: (this.innerActive ? this.innerActive.button : null);
		if (!button) {
			return false;
		}
		this.canvas.fire("clickmenu", {
			button : button,
			node : this.node
		});
		return true;
	}

	Compass.prototype.onMouseMove = function(event) {
		if (!this.show) {
			this.onMouseMoveOutside(event);
			return;
		}
		var x = event.offsetX - this.x;
		var y = event.offsetY - this.y;
		var radius = Math.sqrt(x * x + y * y);
		if (this.showOuter && radius > MIDDLERADIUS && radius < OUTERRADIUS) {
			this.onMouseMoveOuter(x, y, event);
		} else if (radius > INNERRADIUS & radius < MIDDLERADIUS) {
			this.onMouseMoveInner(x, y, event);
		} else {
			this.onMouseMoveOutside(event);
		}
	}

	/**
	 * on mouse outside the compass
	 */
	Compass.prototype.onMouseMoveOutside = function(event) {
		if (this.innerActive || this.outerActive) {
			this.innerActive = null;
			this.outerActive = null;
			this.canvas.render();
		}
	}

	/**
	 * on mouse over the inner ring
	 */
	Compass.prototype.onMouseMoveInner = function(x, y, event) {
		var angle = this.getAngle(x, y);
		var buttons = this.getButtons();
		var self = this;
		this.outerActive = null;
		$.each(this.innerArcs, function(index, arc) {
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
			var button = buttons[index];
			if (self.innerActive && self.innerActive.id == button.id) {
				return;
			}
			self.innerActive = {
				id : button.id,
				button : button,
				arc : arc
			};
			self.canvas.render();
		})
	}

	/**
	 * on mouse over the outer ring
	 */
	Compass.prototype.onMouseMoveOuter = function(x, y, event) {
		var angle = this.getAngle(x, y);
		var buttons = this.getOuterButtons(this.innerActive.id);
		var self = this;
		$.each(this.outerArcs, function(index, arc) {
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
			var button = buttons[index];
			if (self.outerActive && self.outerActive.id == button.id) {
				return;
			}
			self.outerActive = {
				id : button.id,
				button : button,
				arc : arc
			};
			self.canvas.render();
		})
	}

	Compass.prototype.getButtons = function() {
		switch (this.type) {
		case "node": {
			return nodeMenu;
		}
		case "selects": {
			return selectsMenu;
		}
		case "line": {
			return lineMenu;
		}
		}
		return nodeMenu;
	}

	Compass.prototype.getOuterButtons = function(id) {
		var button = this.getButton(id);
		if (!button) {
			return null;
		}
		return button.children;
	}

	Compass.prototype.getButton = function(id) {
		var buttons = this.getButtons();
		for (var i = 0; i < buttons.length; i++) {
			if (buttons[i].id == id) {
				return buttons[i];
			}
		}
		return null;
	}

	Compass.prototype.getColor = function(id) {
		switch (this.type) {
		case "node": {
			return "#000000";
		}
		case "selects": {
			return "#162E3F";
		}
		case "link": {
			return "red";
		}
		}
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
define(
		"onyx/canvas/searchpanel",
		[ "jquery", "require", "d3/d3" ],
		function($, require) {

			var d3 = require("d3/d3");

			var SearchPanel = function(canvas, graph) {
				this.canvas = canvas;
				this.graph = graph;
				this.build(canvas.dom);
			}

			SearchPanel.prototype.build = function(pdom) {
				this.dom = $("<div class='onyx-canvas-searchpanel unselectable'></div>");
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
				var nodex = (this.pos && this.pos.x) || this.graph.getWidth()
						/ 2 - this.graph.getX();
				var nodey = (this.pos && this.pos.y) || this.graph.getHeight()
						/ 2 - this.graph.getY();
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

/**
 * Onyx Canvas Right Panel
 */
define(
		"onyx/canvas/rightpanel",
		[ "jquery", "require" ],
		function($, require) {

			var RightPanel = function(canvas) {
				this.canvas = canvas;
				this.build(canvas.dom);
			}

			RightPanel.prototype.build = function(pdom) {
				this.dom = $("<div class='onyx-canvas-rightpanel unselectable'></div>");
				this.dom.appendTo(pdom);
				this.handler = $("<div class='onyx-canvas-rightpanel-handler iconfont icon-close'></div>");
				this.handler.appendTo(this.dom);
				this.handler.on("click", this.onClick.bind(this));
				this.container = $("<div class='onyx-canvas-rightpanel-container'></div>");
				this.container.appendTo(this.dom);
			}

			RightPanel.prototype.buildNode = function(node) {
				this.container.children().remove();
				var layout = UI.createLayout({
					clazz : "onyx-canvas-rightpanel-layout",
					header : {
						height : 128
					},
					body : {

					},
					pdom : this.container
				});
				var icon = $("<img class='onyx-canvas-rightpanel-icon'></img>");
				icon.attr("src", "/onyxapi/v1/image/" + node.id);
				icon.appendTo(layout.getHeader());
				var details = $("<div class='onyx-canvas-rightpanel-details'></div>");
				details.appendTo(layout.getHeader());
				var title = $("<p class='onyx-canvas-rightpanel-title'></p>");
				title.text(node.name);
				title.appendTo(details);
			}

			RightPanel.prototype.show = function(node) {
				this.dom.css("display", "block");
				this.buildNode(node);
			}

			RightPanel.prototype.hide = function() {
				this.dom.css("display", "none");
			}

			RightPanel.prototype.onClick = function(event) {
				this.hide();
			}

			return RightPanel;
		});
