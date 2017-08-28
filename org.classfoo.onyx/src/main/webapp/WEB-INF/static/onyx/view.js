/**
 * Onyx View
 */
define("onyx/view", [ "jquery", "require", "css!./view.css", "onyx/utils",
		"onyx/ui" ], function($, require) {

	var Utils = require("onyx/utils");

	var UI = require("onyx/ui");

	function View(options) {
		this.options = options;
		this.build(this.options);
	}

	View.prototype.build = function(options) {
		this.dom = $("<div class='onyx-view'></div>");
		this.dom.appendTo(this.options.pdom);
		this.multipage = UI.createMultiPage({
			clazz : "onyx-view-multipage",
			pdom : this.dom
		});
		return this.dom;
	}

	View.prototype.refresh = function(options) {
		return $.dfd(this);
	}

	View.prototype.showPage = function(options) {
		return this.multipage.showPage(options);
	}
	return View;
});

/**
 * Onyx Space Enter
 */
define("onyx/view/entity", [ "jquery", "require", "onyx/utils", "onyx/ui" ],
		function($, require) {

			var Utils = require("onyx/utils");

			var UI = require("onyx/ui");

			function Entity(options) {
				this.options = options;
				this.resource = this.options.resource;
				this.kid = this.resource.kid;
				this.eid = this.resource.eid;
				this.build(this.options.pdom);
			}

			Entity.prototype.build = function(pdom) {
				this.dom = $("<div class='onyx-view-entity'></div>");
				this.dom.appendTo(pdom);
				this.buildHeader();
				this.buildBody();
				this.buildTools();
				this.buildFooter();
				return this.dom;
			}

			Entity.prototype.buildHeader = function() {
				this.header = $("<div class='onyx-view-entity-header'></div>");
				this.header.appendTo(this.dom);
			}

			Entity.prototype.buildBody = function() {
				this.body = $("<div class='onyx-view-entity-body'></div>");
				this.body.appendTo(this.dom);
				this.buildPage();
			}

			Entity.prototype.buildPage = function() {
				this.page = $("<div class='onyx-view-entity-page'/>");
				this.page.appendTo(this.body);
				var paragraph = $("<div class='onyx-view-entity-paragraph'/>");
				paragraph.appendTo(this.page);
				paragraph.text(this.resource.name);

			}

			Entity.prototype.buildFooter = function() {
				this.footer = $("<div class='onyx-view-entity-footer'></div>");
				this.footer.appendTo(this.dom);
			}

			Entity.prototype.buildTools = function() {
				this.tools = $("<div class='onyx-view-entity-tools'/>");
				this.tools.appendTo(this.dom);
				this.editTool = UI.createButton({
					clazz : "onyx-view-entity-tool",
					theme : "blue",
					icon : "icon-edit",
					uri : "/edit/entity/" + this.eid,
					pdom : this.tools
				});
				this.shareTool = UI.createButton({
					clazz : "onyx-view-entity-tool",
					theme : "blue",
					icon : "icon-relation",
					pdom : this.tools
				});
				this.commentTool = UI.createButton({
					clazz : "onyx-view-entity-tool",
					theme : "blue",
					icon : "icon-bell",
					pdom : this.tools
				});
			}

			Entity.prototype.refresh = function(options) {
				return $.dfd(this);
			}

			Entity.prototype.getPathes = function() {
				return [ {
					id : "home",
					caption : "虾掰",
					uri : "/"
				}, {
					id : "base",
					caption : "知识库",
					uri : "/space/base"
				}, {
					id : this.kid,
					name : this.kid,
					uri : "/base/knowledges/" + this.kid
				}, {
					id : this.eid,
					name : this.resource.name,
					uri : "/view/entity/" + this.eid
				} ];
			}

			return Entity;
		});

/**
 * Onyx Space Label
 */
define("onyx/view/label", [ "jquery", "require", "onyx/utils", "onyx/ui" ],
		function($, require) {

			var Utils = require("onyx/utils");

			var UI = require("onyx/ui");

			function Label(options) {
				this.options = options;
				this.resource = this.options.resource;
				this.kid = this.resource.kid;
				this.lid = this.resource.lid;
				this.build(this.options.pdom);
			}

			Label.prototype.build = function(pdom) {
				this.dom = $("<div class='onyx-view-label'></div>");
				this.dom.appendTo(pdom);
				this.buildHeader();
				this.buildBody();
				this.buildTools();
				this.buildFooter();
				return this.dom;
			}

			Label.prototype.buildHeader = function() {
				this.header = $("<div class='onyx-view-label-header'></div>");
				this.header.appendTo(this.dom);
			}

			Label.prototype.buildBody = function() {
				this.body = $("<div class='onyx-view-label-body'></div>");
				this.body.appendTo(this.dom);
				this.buildPage();
			}

			Label.prototype.buildPage = function() {
				this.page = $("<div class='onyx-view-label-page'/>");
				this.page.appendTo(this.body);
				var paragraph = $("<div class='onyx-view-label-paragraph'/>");
				paragraph.appendTo(this.page);
				paragraph.text(this.resource.name);

			}

			Label.prototype.buildFooter = function() {
				this.footer = $("<div class='onyx-view-label-footer'></div>");
				this.footer.appendTo(this.dom);
			}

			Label.prototype.buildTools = function() {
				this.tools = $("<div class='onyx-view-label-tools'/>");
				this.tools.appendTo(this.dom);
				this.editTool = UI.createButton({
					clazz : "onyx-view-label-tool",
					theme : "blue",
					icon : "icon-edit",
					uri : "/edit/label/" + this.lid,
					pdom : this.tools
				});
				this.shareTool = UI.createButton({
					clazz : "onyx-view-label-tool",
					theme : "blue",
					icon : "icon-relation",
					pdom : this.tools
				});
				this.commentTool = UI.createButton({
					clazz : "onyx-view-label-tool",
					theme : "blue",
					icon : "icon-bell",
					pdom : this.tools
				});
			}

			Label.prototype.refresh = function(options) {
				return $.dfd(this);
			}

			Label.prototype.getPathes = function() {
				return [ {
					id : "home",
					caption : "虾掰",
					uri : "/"
				}, {
					id : "base",
					caption : "知识库",
					uri : "/space/base"
				}, {
					id : this.kid,
					name : this.kid,
					uri : "/base/knowledges/" + this.kid
				}, {
					id : this.eid,
					name : this.resource.name,
					uri : "/view/label/" + this.lid
				} ];
			}

			return Label;
		});