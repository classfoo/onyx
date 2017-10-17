/**
 * Onyx App Enter
 */
define("onyx/web", [ "jquery", "require", "css!./web.css", "page/page",
		"onyx/utils", "onyx/ui", "onyx/web/topbar" ], function($, require) {

	var Utils = require("onyx/utils");

	var UI = require("onyx/ui");

	var Topbar = require("onyx/web/topbar");

	var Page = require("page/page");

	function Web(options) {
		this.options = options;
		var dom = this.build(this.options);
	}

	Web.prototype.build = function(options) {
		this.dom = $("<div class='onyx-web'></div>");
		this.dom.appendTo(options.pdom);
		this.layout = UI.createLayout({
			clazz : "onyx-web-layout",
			header : {
				height : 46
			},
			body : {},
			pdom : this.dom
		});
		this.topbar = new Topbar(this);
		this.topbar.build(this.layout.getHeader());
		this.multipage = UI.createMultiPage({
			pdom : this.layout.getBody()
		});
		return this.dom;
	}

	Web.prototype.showPage = function(options) {
		return this.multipage.showPage(options);
	}

	Web.prototype.refreshPage = function(options) {
		var dfd = $.Deferred();
		var self = this;
		this.topbar.refresh(options).done(function() {
			dfd.resolve(self);
		});
		return dfd.promise();
	}

	return Web;
});

/**
 * Onyx Topbar
 */
define("onyx/web/topbar", [ "jquery", "require", "onyx/ui", "page/page" ],
		function($, require) {

			var UI = require("onyx/ui");

			var Page = require("page/page");

			function Topbar(app) {
				this.app = app;
			}

			Topbar.prototype.build = function(pdom) {
				this.dom = $("<div class='onyx-web-topbar'></div>");
				this.dom.appendTo(pdom);
				this.layout = UI.createLayout({
					clazz : "onyx-web-topbar-layout",
					left : {
						width : 56
					},
					middle : {

					},
					right : {
						width : 152
					},
					pdom : this.dom
				});
				this.logo = UI.createImage({
					src : "/static/onyx/css/images/logo.png",
					pdom : this.layout.getLeft(),
					uri : "/",
					style : {
						width : 64,
						height : 64,
						margin : "8px"
					}
				});
				this.searchbox = UI.createSearchBox({
					placeholder : "search all...",
					style : {
						width : "100%",
						height : "60%",
						top : 2,
						"margin-top" : "8px"
					},
					pdom : this.layout.getMiddle(),
				});
				this.searchbox.on("change", function(){alert(1)});
				this.navbar = UI.createNavBar({
					theme : "topbar",
					active : "space",
					items : [ {
						id : "space",
						icon : "icon-space",
						uri : "/space"
					}, {
						id : "personal",
						icon : "icon-personal",
						uri : "/personal"
					}, {
						id : "alert",
						icon : "icon-alert",
						uri : "/settings"
					} ],
					pdom : this.layout.getRight()
				});
			}

			Topbar.prototype.refresh = function(options) {
				if (!options || !options.component
						|| !options.component.getPathes) {
					return $.dfd(this);
				}
				var pathes = options.component.getPathes();
				if (this.breadCrumb) {
					this.breadCrumb.setPathes(pathes);
					return $.dfd(this);
				}
				this.breadCrumb = UI.createBreadCrumb({
					pathes : pathes,
					pdom : this.searchbox.dom
				});
				return $.dfd(this);
			}

			return Topbar;
		});