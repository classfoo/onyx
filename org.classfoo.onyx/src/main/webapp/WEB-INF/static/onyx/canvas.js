/**
 * Onyx Canvas Class
 */
define("onyx/canvas", [ "jquery", "require", "css!./canvas.css", "d3/d3",
		"onyx/compass" ], function($, require) {

	var d3 = require("d3/d3");

	var Compass = require("onyx/compass");

	var index = 0;

	var radius = 20;

	function Canvas() {
		this.index = 4;
		this.nodes = [ {
			id : 1,
			r : 30,
			x : 100,
			y : 100
		}, {
			id : 2,
			r : 30,
			x : 100,
			y : 200
		}, {
			id : 3,
			r : 30,
			x : 100,
			y : 300
		}, {
			id : 4,
			r : 30,
			x : 500,
			y : 200
		} ];
		this.links = [ {
			source : 1,
			target : 2
		} ];
		var offset = 70;
		var r = 14;
		this.selected = null;// select nodes ids
		this.lastselected = null;// last selected node id
		this.dragged = null;// dragging node id
		this.mouseovered = null;// mouse move over node id
	}

	Canvas.prototype.build = function(dom) {
		// init dom
		this.width = dom.innerWidth();
		this.height = dom.innerHeight();
		// this.buildBackGround(dom, this.width, this.height);
		this.canvasDom = $("<canvas class='onyx-canvas'></canvas>");
		this.canvasDom.attr("width", this.width);
		this.canvasDom.attr("height", this.height);
		this.canvasDom.appendTo(dom);
		this.canvasDom.on("dblclick", this.onDblClick.bind(this));
		this.canvasDom.on("click", this.onClick.bind(this));
		this.canvasDom.on("mousemove", this.onMouseMove.bind(this));

		// init canvas
		this.canvas = document.querySelector(".onyx-canvas");
		this.context = this.canvas.getContext("2d");
		this.compass = new Compass(this.context);
		// bind events and render
		this.simulation = d3.forceSimulation();
		this.simulation.on("tick", this.render.bind(this));

		this.bindEvents();
		this.render();
	}
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

	Canvas.prototype.isMouseOvered = function(node) {
		return this.mouseovered ? node.id === this.mouseovered : false;
	}

	Canvas.prototype.isSelected = function(node) {
		return this.selected ? (this.selected[node.id] ? true : false) : false;
	}

	Canvas.prototype.isDragged = function(node) {
		return this.dragged ? node.id === this.dragged : false;
	}

	Canvas.prototype.bindEvents = function() {
		var width = this.width;
		var height = this.height;
		this.simulation.nodes(this.nodes);
		this.simulation.force("collide", d3.forceCollide(this.nodes).radius(
				function(d) {
					return radius + 6;
				}).iterations(4).strength(1));
		this.simulation.force("link", d3.forceLink(this.links).id(function(d) {
			return d.id;
		}).iterations(4).distance(100).strength(1));
		this.simulation.restart();
		d3.select(this.canvas).call(
				d3.drag().container(this.canvas).subject(
						this.dragsubject.bind(this)).on("start",
						this.dragstarted.bind(this)).on("drag",
						this.dragging.bind(this)).on("end",
						this.dragended.bind(this)));

		d3.select(this.canvas).call(
				d3.zoom().scaleExtent([ 1 / 2, 4 ]).on("start",
						this.zoomstart.bind(this)).on("zoom",
						this.zoomming.bind(this))).on("zoom.end",
				this.zoomend.bind(this));

	}

	Canvas.prototype.zoomstart = function() {
		this.zoom = {
			x : d3.event.transform.x,
			y : d3.event.transform.y,
			k : d3.event.transform.k
		};
	}

	Canvas.prototype.zoomming = function() {
		if (this.zoom == null) {
			return;
		}
		var dx = d3.event.transform.x - this.zoom.x;
		var dy = d3.event.transform.y - this.zoom.y;
		for (var i = 0; i < this.nodes.length; i++) {
			var node = this.nodes[i];
			node.x += dx;
			node.y += dy;
		}
		var ck = d3.event.transform.k;
		this.zoom.x = d3.event.transform.x;
		this.zoom.y = d3.event.transform.y;
		this.render();
	}

	Canvas.prototype.zoomend = function() {
		this.zoom = null;
	}

	Canvas.prototype.dragsubject = function() {
		return this.simulation.find(d3.event.x, d3.event.y, radius);
	}

	Canvas.prototype.dragstarted = function() {
		if (!d3.event.active)
			this.simulation.alphaTarget(0.3).restart();
		d3.event.subject.fx = d3.event.subject.x;
		d3.event.subject.fy = d3.event.subject.y;
		this.dragged = d3.event.subject.id;
	}

	Canvas.prototype.dragging = function() {
		d3.event.subject.fx = d3.event.x;
		d3.event.subject.fy = d3.event.y;
	}

	Canvas.prototype.dragended = function() {
		if (!d3.event.active)
			this.simulation.alphaTarget(0);
		d3.event.subject.fx = null;
		d3.event.subject.fy = null;
		this.dragged = null;
	}

	Canvas.prototype.onDblClick = function(event) {
		this.addNode({
			id : ++this.index,
			x : event.offsetX,
			y : event.offsetY
		});
		this.bindEvents();
		this.render();
	}

	Canvas.prototype.onClick = function(event) {
		var item = this.simulation.find(event.offsetX, event.offsetY, radius);
		if (item == null) {
			if (this.selected || this.lastselected) {
				this.lastselected = null;
				this.selected = null;
				this.render();
			}
			return;
		}
		if (this.selected) {
			var sel = this.selected[item.id];
			if (sel) {
				this.selected[item.id] = false;
				this.lastselected = null;
			} else {
				this.selected[item.id] = true;
				this.lastselected = item.id;
			}
		} else {
			this.selected = {};
			this.selected[item.id] = true;
			this.lastselected = item.id;
		}
		this.render();
	}

	Canvas.prototype.onMouseMove = function(event) {
		this.compass.onMouseMove(event);
		var item = this.simulation.find(event.offsetX, event.offsetY, radius);
		if (item == null) {
			if (this.mouseovered) {
				this.mouseovered = null;
				this.render();
			}
			return;
		}
		this.mouseovered = item.id;
		this.render();
	}

	Canvas.prototype.addNode = function(node) {
		this.nodes.push(node);
		this.bindEvents();
		this.render();
	}

	Canvas.prototype.addLine = function(options) {
		this.lines.push(node);
	}

	Canvas.prototype.render = function(options) {
		this.context.clearRect(0, 0, this.width, this.height);
		for (var i = 0, link, n = this.links.length; i < n; i++) {
			link = this.links[i];
			this.renderLink(link);
		}
		var lastselnode = null;
		for (var i = 0, node, n = this.nodes.length; i < n; ++i) {
			node = this.nodes[i];
			this.renderNode(node);
			// draw buttons
			if (this.lastselected && node.id === this.lastselected) {
				lastselnode = node;
			}
		}
		if (lastselnode != null && !this.isDragged(lastselnode)) {
			this.compass.render(lastselnode);
			// this.renderButtons(lastselnode);
			this.renderNode(lastselnode);
		} else {
			this.compass.hide();
		}
	}

	Canvas.prototype.renderNode = function(node) {
		var dragged = this.isDragged(node);
		var selected = this.isSelected(node);
		var mouseovered = this.isMouseOvered(node);
		// draw node
		this.context.save();
		this.context.beginPath();
		this.context.moveTo(node.x + radius, node.y);
		this.context.arc(node.x, node.y, radius, 0, 2 * Math.PI);
		if (mouseovered) {
			this.context.fillStyle = "#000000";
		} else {
			this.context.fillStyle = "#FFFFFF";
		}
		this.context.fill();
		this.context.restore();
		// draw border
		this.context.save();
		if (dragged) {
			this.context.setLineDash([ 3, 3 ]);
			this.context.lineWidth = 3;
			this.context.stroke();
		} else if (selected) {
			this.context.lineWidth = 3;
			this.context.stroke();
		} else {
			this.context.lineWidth = 1;
			this.context.stroke();
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
		this.context.fillText("\ue63a", node.x, node.y + radius / 2);
		this.context.restore();
		// draw label
		this.context.save();
		this.context.font = "12px Open Sans";
		this.context.textAlign = "center"
		this.context.fillStyle = "#000000";
		this.context.fillText(node.id, node.x, node.y + radius + 10);
		this.context.restore();
	}

	Canvas.prototype.renderLink = function(link) {
		this.context.save();
		this.context.beginPath();
		this.context.moveTo(link.source.x, link.source.y);
		this.context.lineTo(link.target.x, link.target.y);
		this.context.strokeStyle = "#FFFFFF";
		this.context.stroke();
		this.context.restore();
	}

	return Canvas;
});

/**
 * Onyx Canvas Compass Controller Class
 */
define("onyx/compass", [ "jquery", "require", "d3/d3" ], function($, require) {

	var d3 = require("d3/d3");

	var Compass = function(context) {
		this.context = context;
		this.buttons = [ {
			id : 0,
			icon : '\ue66e',
			length : 1
		}, {
			id : 1,
			icon : '\ue66e',
			length : 1
		}, {
			id : 2,
			icon : '\ue618',
			length : 1
		}, {
			id : 3,
			icon : '\ue61c',
			length : 1
		}, {
			id : 4,
			icon : '\ue61f',
			length : 1
		}, {
			id : 5,
			icon : '\ue624',
			length : 1
		}, {
			id : 6,
			icon : '\ue626',
			length : 1
		}, {
			id : 7,
			icon : '\u3438',
			length : 1
		} ];
	}
	Compass.prototype.hide = function() {
		this.node = null;
		this.arcs = null;
	}

	Compass.prototype.render = function(node) {
		this.node = node;
		this.x = node.x;
		this.y = node.y;
		this.context.save();
		var arc = d3.arc().outerRadius(100).innerRadius(40).padAngle(0.03)
				.context(this.context);
		this.arcs = d3.pie().startAngle(Math.PI / 8).endAngle(
				Math.PI * 2 + Math.PI / 8).value(function(d) {
			return d.length;
		})(this.buttons);
		this.context.translate(this.x, this.y);
		this.context.globalAlpha = 0.8;
		var self = this;
		this.arcs.forEach(this.renderArc.bind(this, arc));
		this.context.restore();
	}

	Compass.prototype.renderArc = function(arc, d, i) {
		var button = d.data;
		this.context.beginPath();
		arc.cornerRadius(10);
		arc(d);
		this.context.fillStyle = button.color || "#000000";// colors[i];
		this.context.fill();

		var c = arc.centroid(d);
		this.context.save();
		this.context.translate(c[0], c[1]);
		this.context.font = "12px iconfont";
		this.context.textAlign = "center"
		this.context.fillStyle = "#FFFFFF";
		this.context.fillText(button.icon, 0, 0);
		this.context.restore();
	}

	Compass.prototype.onMouseMove = function(event) {
		if (!this.arcs) {
			return;
		}
		var x = event.offsetX - this.x;
		var y = event.offsetY - this.y;
		var radius = Math.sqrt(x * x + y * y);
		if (radius < 40 || radius > 100) {
			return;
		}
		var angle = this.getAngle(x, y);
		console.log(angle);
		var self = this;
		$.each(this.arcs, function(index, arc) {
			if (angle < arc.startAngle || angle > arc.endAngle) {
				return;
			}
			self.buttons[index].color = "#FFFFFF";
		})
	}

	Compass.prototype.getAngle = function(x, y) {
		if (x >= 0 && y <= 0) {
			return Math.atan(Math.abs(y / x));
		}
		if (x <= 0 && y <= 0) {
			return Math.PI - Math.atan(Math.abs(y / x));
		}
		if (x <= 0 && y >= 0) {
			return Math.atan(Math.abs(y / x)) + Math.PI;
		}
		if (x >= 0 && y >= 0) {
			return Math.PI * 2 - Math.atan(Math.abs(y / x));
		}
	}

	return Compass;
});
