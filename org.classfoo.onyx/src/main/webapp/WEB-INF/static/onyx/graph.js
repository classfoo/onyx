/**
 * Onyx Space Enter
 */
define("onyx/graph", [ "jquery", "require", "css!./graph.css", "onyx/ui",
		"onyx/canvas" ], function($, require) {

	var UI = require("onyx/ui");
	var Canvas = require("onyx/canvas");
	function Graph(options) {
		this.options = options;
		this.build(options.pdom);
	}

	Graph.prototype.build = function(pdom) {
		this.dom = $("<div class='onyx-graph'></div>");
		this.dom.appendTo(pdom);
		this.canvas = new Canvas();
		this.canvas.build(this.dom);
		this.floatPanel = UI.createFloatPanel({
			pdom : this.dom,
			style : {
				position : "absolute",
				right : 5,
				top : 5,
				width : 100,
				height : 32,
				"border-radius" : 5,
				"background-color" : "rgba(100, 100, 100, 100)"
			}
		});
		this.closeIcon = UI.createIcon({
			icon : "icon-close",
			pdom : this.floatPanel.getDom(),
			style : {
				"font-size" : 12
			}
		});
		this.nextIcon = UI.createIcon({
			icon : "icon-next",
			pdom : this.floatPanel.getDom(),
			style : {
				"font-size" : 12
			}
		});
		this.preIcon = UI.createIcon({
			icon : "icon-pre",
			pdom : this.floatPanel.getDom(),
			style : {
				"font-size" : 12
			}
		});
		var self = this;
		return this.dom;
	}

	Graph.prototype.showPage = function(options) {
		return $.dfd();
	}

	return Graph;
});