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
define(
		"onyx/view/entity",
		[ "jquery", "require", "onyx/utils", "onyx/ui" ],
		function($, require) {

			var Utils = require("onyx/utils");

			var UI = require("onyx/ui");

			function Entity(options) {
				this.options = options;
				this.resource = this.options.resource;
				this.kid = this.resource.kid;
				this.eid = this.resource.id;
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
				var container = $("<div class='onyx-view-entity-header-container'/>");
				container.appendTo(this.header);
				var left = $("<div class='onyx-view-entity-header-left'/>");
				left.appendTo(container);
				var right = $("<div class='onyx-view-entity-header-right'/>");
				right.appendTo(container);
				this.buildEntityLabels(left);
				this.buildEntityInfo(left);
			}

			Entity.prototype.buildEntityLabels = function(pdom) {
				var labels = $("<div class='onyx-view-entity-info-labels'/>");
				labels.appendTo(pdom);
				for (var i = 0; i < this.resource.labels.length; i++) {
					var label = this.resource.labels[i];
					var labelDom = $("<span class='onyx-view-entity-info-label'/>");
					labelDom.text(label);
					labelDom.appendTo(labels);
				}
			}

			Entity.prototype.buildEntityInfo = function(pdom) {
				var left = $("<div class='onyx-view-entity-info-left'/>");
				left.appendTo(pdom);
				var right = $("<div class='onyx-view-entity-info-right'/>");
				right.appendTo(pdom);
				var image = $("<span class='onyx-view-entity-info-image iconfont icon-man1'/>");
				image.appendTo(left);
				var name = $("<div class='onyx-view-entity-info-name'/>");
				name.text(this.resource.name);
				name.appendTo(right);
				if (this.resource.description) {
					var description = $("<div class='onyx-view-entity-info-description'/>");
					description.text(this.resource.description);
					description.appendTo(right);
				} else {
					var description = $("<div class='onyx-view-entity-info-description-empty'/>");
					description.text("Oops，" + this.resource.name
							+ "的概要描述不存在，我来添加一下...");
					description.appendTo(right);
				}
			}

			Entity.prototype.buildBody = function() {
				this.body = $("<div class='onyx-view-entity-body'></div>");
				this.body.appendTo(this.dom);
				var container = $("<div class='onyx-view-entity-body-container'></div>");
				container.appendTo(this.body);
				this.buildPage(container);
				this.buildPanels(container);
			}

			Entity.prototype.buildPanels = function(pdom) {
				this.buildGraphPanel(pdom);
				this.buildPanel(pdom);
			}

			Entity.prototype.buildGraphPanel = function(pdom) {
				var graph = $("<div class='onyx-view-entity-panel shadow'/>");
				graph.appendTo(pdom);
				var icon = $("<div class='onyx-view-entity-panel-graph-icon iconfont icon-graph'>");
				icon.appendTo(graph);
				var self = this;
				graph.on("click", function() {
					UI.redirect("/graph/entity/" + self.resource.id);
				});
			}

			Entity.prototype.buildPanel = function(pdom) {
				var panel = $("<div class='onyx-view-entity-panel shadow'/>");
				panel.appendTo(pdom);
			}

			Entity.prototype.buildPage = function(pdom) {
				var page = $("<div class='onyx-view-entity-page shadow'/>");
				page.appendTo(pdom);
				UI.createNavBar({
					clazz : "onyx-view-entity-navbar",
					theme : "horizon",
					active : "basic",
					items : [ {
						id : "basic",
						caption : "基本信息"
					}, {
						id : "basic",
						caption : "关联"
					}, {
						id : "basic",
						caption : "事件"
					}, {
						id : "basic",
						caption : "资料"
					} ],
					pdom : page
				});
				var properties = this.resource.properties;
				for ( var key in properties) {
					var item = $("<div class='onyx-view-entity-property'/>");
					item.appendTo(page);
					var keydom = $("<span class='onyx-view-entity-property-key'/>");
					keydom.text(key);
					keydom.appendTo(item);
					var value = properties[key];
					var valuedom = $("<span class='onyx-view-entity-property-value'/>");
					valuedom.text(value);
					valuedom.appendTo(item);
				}
			}

			Entity.prototype.buildFooter = function() {
				this.footer = $("<div class='onyx-view-entity-footer'></div>");
				this.footer.text("Copy Right @ XiaBai.com");
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
				var dfd = $.Deferred();
				var self = this;
				Api.base().get(this.kid).done(function(base) {
					dfd.resolve([ {
						id : "home",
						caption : "虾掰",
						uri : "/"
					}, {
						id : "base",
						caption : "知识库",
						uri : "/space/base"
					}, {
						id : self.kid,
						name : base.name,
						uri : "/base/knowledges/" + self.kid
					}, {
						id : self.eid,
						name : self.resource.name,
						uri : "/view/entity/" + self.eid
					} ]);
				});
				return dfd.promise();

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
				this.lid = this.resource.id;
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
				return $.dfd([ {
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
				} ]);
			}

			return Label;
		});