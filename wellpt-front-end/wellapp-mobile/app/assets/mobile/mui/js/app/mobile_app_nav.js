define([ "mui", "commons", "server", "appContext", "appModal", "formBuilder" ], function($, commons, server,
		appContext, appModal, formBuilder) {
	var StringUtils = commons.StringUtils;
	var StringBuilder = commons.StringBuilder;
	var actionBackTemplate = '<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>';

	// 工作导航
	var appNavMap = {};
	var appNavs = [];

	// 解析URI地址参数
	// 格式startApp?key=value&...
	// 例如startApp?isJsModule=true&jsModule=mobile_new_work&action=newWorkById&flowDefId=L_RSZYGL_JBSQ(ZB)
	function parseUri(uriString) {
		if (StringUtils.isBlank(uriString)) {
			return null;
		}
		var uri = {};
		var paramString = uriString.substring("startApp?".length);
		var parmas = paramString.split("&");
		$.each(parmas, function(i, param) {
			var keyValue = param.split("=");
			if (keyValue.length == 2) {
				var key = keyValue[0];
				var value = keyValue[1];
				if (value === "true") {
					value = Boolean(value);
				}
				uri[key] = value;
			}
		});
		return uri;
	}

	// 绑定事件
	function bindEvent(wrapper, ui) {
		$(".mui-app-nav").on('tap', '.mui-menu-item', function(event) {
			var menuId = this.getAttribute("menuId");
			if (StringUtils.isBlank(menuId)) {
				return;
			}
			var appNav = appNavMap[menuId];
			var uri = parseUri(appNav.data.uri);
			if (uri == null) {
				return;
			}
			var options = $.extend({
				event : event
			}, uri);
			appContext.startApp(appContext.getPageContainer(), options);
		});
	}

	var startNewWork = function(options) {
		var JDS = server.JDS;

		var data = {
			navContentOnly : false
		};
		JDS.call({
			service : "mobileAppNavService.getCmsCategoryByParentId",
			async : false,
			data : [ "ldx_index_nav" ],
			success : function(result) {
				data.mainNavs = result.data.children;
				appNavs = data.mainNavs;
				$.each(data.mainNavs, function() {
					var mainNav = this;
					$.each(mainNav.children, function() {
						var subNav = this;
						subNav.parent = mainNav;
						$.each(subNav.children, function() {
							this.parent = subNav;
							appNavMap[this.id] = this;
						})
					});
				});
			}
		})

		var templateEngine = appContext.getJavaScriptTemplateEngine();
		var content = templateEngine.renderById("mui-app-nav", data);

		// 创建发起工作面板
		var selectorId = "ldx_mobile_app_nav";
		var wrapper = document.createElement("div");
		wrapper.classList.add("app-nav");
		wrapper.id = selectorId;
		var pageContainer = appContext.getPageContainer();
		var renderPlaceholder = pageContainer.getRenderPlaceholder();
		renderPlaceholder[0].classList.add("app-nav-page");
		renderPlaceholder[0].appendChild(wrapper);
		formBuilder.buildPanel({
			title : "工作导航",
			content : content,
			contentClass : "mui-app-nav-panel mui-fullscreen mui-content",
			container : "#" + selectorId
		})

		bindEvent(wrapper, options.ui);
		$.ui.loadContent("#" + selectorId);

		var $searchInput = $("#" + selectorId + ' .mui-search input');
		$searchInput.input();
		// 搜索按钮点击搜索
		if($.os && $.os.ios) {
			setTimeout(function() {
				$.trigger($searchInput[0], "click");
			}, 100);
		}
		$searchInput[0].addEventListener("search", function(event) {
			var searchText = this.value;
			var treeNodes = searchTreeNode(searchText);
			var searchData = {
				mainNavs : treeNodes,
				navContentOnly : true
			};
			var searchContent = templateEngine.renderById("mui-app-nav", searchData);
			$("#" + selectorId + ' .mui-app-nav-content')[0].innerHTML = searchContent;
			showFirst();
		});

		// 导航点击
		$("#" + selectorId + ' .mui-app-nav').on("tap", ".mui-control-item", function() {
			var dataIndex = this.getAttribute("data-index");
			var $contents = $("#appNavSegmentedControlContents .mui-control-content");
			$.each($contents, function() {
				this.style.display = "none";
			});
			$("#appNavSegmentedControlContents #appNavContent" + dataIndex)[0].style.display = "block";
		});
		showFirst();
		var fitHeight = 50;
		var headerBar = $(".app-nav>header.mui-bar-nav")[0];
		if(headerBar){
			var headerHeight = parseInt(headerBar.offsetHeight) || 0;
			fitHeight = fitHeight + headerHeight
		}
		// 限制高度
		$("#" + selectorId + ' .mui-app-nav-content')[0].style.height = ($.screen.availHeight - fitHeight) + "px";
		//$(".mui-scroll-wrapper").scroll();
	}
	function showFirst() {
		var firstNav = $("#appNavSegmentedControls .mui-control-item");
		if (firstNav && firstNav.length > 0) {
			firstNav[0].classList.add("mui-active");
			$.trigger(firstNav[0], "tap");
		}
	}
	var copyNodeMap = {};
	function searchTreeNode(searchText) {
		if (StringUtils.isBlank(searchText)) {
			return appNavs;
		}
		copyNodeMap = {};
		var treeNodes = [];
		for ( var p in appNavMap) {
			var appNav = appNavMap[p];
			var name = appNav.name
			if (StringUtils.contains(name, searchText)) {
				if (appNav.parent && appNav.parent.parent) {
					var node = copyNode(appNav);
					var parent = copyNode(appNav.parent);
					var root = copyNode(appNav.parent.parent);

					parent.children.push(node);

					if (!contains(root.children, parent)) {
						root.children.push(parent);
					}

					if (!contains(treeNodes, root)) {
						treeNodes.push(root);
					}
				}
			}
		}

		return treeNodes;
	}
	function contains(nodes, node) {
		for (var i = 0; i < nodes.length; i++) {
			if (nodes[i].id === node.id) {
				return true;
			}
		}
		return false;
	}
	function copyNode(node) {
		if (copyNodeMap[node.id]) {
			return copyNodeMap[node.id];
		}
		var returnNode = {};
		returnNode.id = node.id;
		returnNode.name = node.name;
		returnNode.children = [];
		copyNodeMap[returnNode.id] = returnNode;
		return returnNode;
	}
	return startNewWork;
});