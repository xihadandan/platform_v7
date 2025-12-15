define([ "ui_component", "constant", "commons", "formBuilder", "appContext", "design_commons" ], function(ui_component,
		constant, commons, formBuilder, appContext, designCommons) {
	var component = $.ui.component.BaseComponent();

	var StringUtils = commons.StringUtils;
	var StringBuilder = commons.StringBuilder;
	component.prototype.create = function() {
	}
	// 使用属性配置器
	component.prototype.usePropertyConfigurer = function() {
		return true;
	}
	function buildMenuItemBootstrapTable($element, name, menuItems, productUuid, navType) {
		formBuilder.bootstrapTable.build({
			container : $element,
			name : name,
			ediableNest : true,
			table : {
				data : menuItems,
				striped : true,
				idField : "uuid",
				onEditableSave : function(field, row, oldValue, $el) {
					if (navType !== "2") {
						return;
					}
					var $tableSubNavMenuItemsInfo = $("#table_subNav-menuItems_info", $element);
					if (field == 'defaultSelected' && row[field] == '1') {
						var data = $tableSubNavMenuItemsInfo.bootstrapTable("getData");
						$.each(data, function(index, rowData) {
							if (row != rowData) {
								rowData.defaultSelected = 0;
								$tableSubNavMenuItemsInfo.bootstrapTable("updateRow", index, rowData);
							}
						});
					}
				},
				columns : [ {
					field : "checked",
					formatter : designCommons.checkedFormat,
					checkbox : true
				}, {
					field : "uuid",
					title : "UUID",
					visible : false,
					editable : {
						type : "text",
						showbuttons : false,
						onblur : "submit",
						mode : "inline"
					}
				}, {
					field : "text",
					title : "名称",
					editable : {
						type : "text",
						mode : "inline",
						showbuttons : false,
						onblur : "submit"
					}
				}, {
					field : "icon",
					title : "图标",
					editable : {
						onblur : "cancel",
						type : "wCustomForm",
						placement : "right",
						savenochange : true,
						iconSelectTypes : [ 3 ],
						value2input : designCommons.bootstrapTable.icon.value2input,
						input2value : designCommons.bootstrapTable.icon.input2value,
						value2display : designCommons.bootstrapTable.icon.value2display,
						value2html : designCommons.bootstrapTable.icon.value2html
					}
				}, {
					field : "hidden",
					title : "是否隐藏",
					editable : {
						type : "select",
						mode : "inline",
						onblur : "submit",
						showbuttons : false,
						source : [ {
							value : "-1",
							text : ""
						}, {
							value : "0",
							text : "显示"
						}, {
							value : "1",
							text : "隐藏"
						} ]
					}
				}, {
					field : "badge",
					title : "徽章",
					visible : (navType === "1"),
					editable : {
						onblur : "cancel",
						type : "wCustomForm",
						placement : "bottom",
						savenochange : true,
						value2input : designCommons.bootstrapTable.badge.value2input(),
						value2display : designCommons.bootstrapTable.badge.value2display
					}
				}, {
					field : "defaultSelected",
					title : "默认选中",
					visible : (navType === "2"),
					editable : {
						type : "select",
						mode : "inline",
						onblur : "submit",
						showbuttons : false,
						source : [ {
							value : "-1",
							text : ""
						}, {
							value : '1',
							text : "是"
						}, {
							value : '0',
							text : "否"
						} ]
					}
				}, {
					field : "eventType",
					title : "事件类型",
					editable : {
						type : "select",
						mode : "inline",
						onblur : "submit",
						showbuttons : false,
						source : function() {
							var eventTypes = constant.EVENT_TYPE
							var types = [ {
								value : "-1",
								text : ""
							} ];
							var etes = $.map(eventTypes, function(eventType) {
								return {
									value : eventType.value,
									text : eventType.name
								}
							});
							types = types.concat(etes);
							return types;
						}
					}
				}, {
					field : "target",
					title : "目标位置",
					editable : {
						onblur : "cancel",
						type : "wCustomForm",
						placement : "bottom",
						savenochange : true,
						value2input : designCommons.bootstrapTable.targePosition.value2input,
						value2display : designCommons.bootstrapTable.targePosition.value2display,
						inputCompleted : designCommons.bootstrapTable.targePosition.inputCompleted
					}
				}, {
					field : "eventHandler",
					title : "事件处理",
					width : 150,
					editable : {
                        onblur: "ignore",
                        type: "wCustomForm",
                        placement: "left",
                        savenochange: true,
                        value2input: designCommons.bootstrapTable.eventHandler.value2input,
                        input2value: designCommons.bootstrapTable.eventHandler.input2value,
                        value2display: designCommons.bootstrapTable.eventHandler.value2display
					}
				}, {
					field : "eventParams",
					title : "事件参数",
					editable : {
						onblur : "cancel",
						type : "wCustomForm",
						placement : "left",
						savenochange : true,
						value2input : designCommons.bootstrapTable.eventParams.value2input,
						input2value : designCommons.bootstrapTable.eventParams.input2value,
						value2display : designCommons.bootstrapTable.eventParams.value2display
					}
				} ]
			}
		});
	}
	// 返回属性配置器
	component.prototype.getPropertyConfigurer = function() {
		var collectClass = "w-configurer-option";
		var configurer = $.ui.component.BaseComponentConfigurer();
		configurer.prototype.onLoad = function($container, options) {
			// 初始化页签项
			$("#widget_header_tabs ul a", $container).on("click", function(e) {
				e.preventDefault();
				$(this).tab("show");
			})
			var configuration = $.extend(true, {}, options.configuration);
			this.initConfiguration(configuration, $container);
		};
		// 初始化配置信息
		configurer.prototype.initConfiguration = function(configuration, $container) {
			// 基本信息
			this.initBaseInfo(configuration, $container);
			// 一级导航信息
			this.initMainNavInfo(configuration, $container);
			// 二级导航信息
			this.initSubNavInfo(configuration, $container);
			// 工具栏信息
			this.initToolBarInfo(configuration, $container);
		};
		configurer.prototype.initBaseInfo = function(configuration, $container) {
			// 设置值
			designCommons.setElementValue(configuration, $container);

			if (StringUtils.isNotBlank(configuration.logoFilePath)) {
				$("#logoFileImage", $container).show();
				$("#logoFileImage", $container).attr("src", ctx + configuration.logoFilePath);
			}

			// 初始化基本信息
			$("#logoFileImageSelectBtn", $container).on("click", function() {
				$.WCommonPictureLib.show({
					selectTypes : [ 1, 2 ],
					initPrevImg : $("#logoFilePath", $container).val(),
					confirm : function(data) {
						var pictureFilePath = data.filePaths;
						if (StringUtils.isBlank(pictureFilePath)) {
							return;
						}
						$("#logoFilePath", $container).val(pictureFilePath);
						$("#logoFileImage", $container).show();
						$("#logoFileImage", $container).attr("src", ctx + pictureFilePath);
					}
				});
			});
			$("#logoFileImageRemoveBtn", $container).on("click", function() {
				$("#logoFilePath", $container).val("");
				$("#logoFileImage", $container).removeAttr("src");
			});

			var backgroupColorOption = "<option value='themeColor'>采用主题颜色</option>";
			for(var i = 0; i < constant.WIDGET_COLOR.length; i++) {
				var color = constant.WIDGET_COLOR[i];
				var selected = (color.value == configuration.backgroudColor ? "selected" : "");
				backgroupColorOption += "<option value='" + color.value + "'" + selected + ">" + color.name
						+ "</option>";
			}
			$("#backgroudColor", $container).append(backgroupColorOption);

			// 二开JS模块
			$("#jsModule", $container).wSelect2({
				serviceName : "appJavaScriptModuleMgr",
				params : {
					dependencyFilter : "HeaderWidgetDevelopment"
				},
				labelField : "jsModuleName",
				valueField : "jsModule",
				remoteSearch : false,
				multiple : true
			});
		};
		configurer.prototype.initMainNavInfo = function(configuration, $container) {
			var _self = this;
			var mainNav = configuration.mainNav || {};
			// 设置值
			designCommons.setElementValue(mainNav, $container);
			var appPageUuid = _self.component.pageDesigner.getPageUuid();
			var system = appContext.getCurrentUserAppData().getSystem();
			var productUuid = system.productUuid;

			// 一级导航
			var $element = $(".mainNav-menuItems", $container);
			var menuItems = mainNav.menuItems || [];
			// 填充默认导航
			fillDefaultNavs(menuItems);
			buildMenuItemBootstrapTable($element, "mainNav-menuItems", menuItems, productUuid, "1");
		};
		// 默认导航：主题设置，个人信息，我的消息
		function fillDefaultNavs(menuItems) {
			var hasPersonInfoNav = false;
			var hasMyMsgNav = false;
			var hasThemeNav = false;
			$.each(menuItems, function(i) {
				if (this.uuid === "personInfo") {
					hasPersonInfoNav = true;
				}
				if (this.uuid === "myMsg") {
					hasMyMsgNav = true;
				}
				if (this.uuid === "theme") {
					hasThemeNav = true;
				}
			});
			if (!hasPersonInfoNav) {
				var item = {
					uuid : "personInfo",
					text : "个人信息",
					icon : {}
				};
				item.icon.className = "glyphicon glyphicon-user";
				menuItems.push(item);
			}
			if (!hasMyMsgNav) {
				var item = {
					uuid : "myMsg",
					text : "我的消息",
					icon : {}
				};
				item.icon.className = "glyphicon glyphicon-comment";
				menuItems.push(item);
			}
			if (!hasThemeNav) {
				var item = {
					uuid : "theme",
					text : "主题设置",
					icon : {}
				};
				item.icon.className = "glyphicon glyphicon-text-size";
				menuItems.push(item);
			}
		}
		configurer.prototype.initSubNavInfo = function(configuration, $container) {
			var _self = this;
			var subNav = configuration.subNav || {};
			// 设置值
			designCommons.setElementValue(subNav, $container);
			var appPageUuid = _self.component.pageDesigner.getPageUuid();
			var system = appContext.getCurrentUserAppData().getSystem();
			var productUuid = system.productUuid;

			// 一级导航
			var $element = $(".subNav-menuItems", $container);
			var menuItems = subNav.menuItems || [];
			buildMenuItemBootstrapTable($element, "subNav-menuItems", menuItems, productUuid, "2");
		};
		configurer.prototype.initToolBarInfo = function(configuration, $container) {
			var _self = this;
			var toolBar = configuration.toolBar || {};
			// 设置值
			designCommons.setElementValue(toolBar, $container);
			var appPageUuid = _self.component.pageDesigner.getPageUuid();
			var system = appContext.getCurrentUserAppData().getSystem();
			var productUuid = system.productUuid;

			// 一级导航
			var $element = $(".toolBar-menuItems", $container);
			var menuItems = toolBar.menuItems || [];
			buildMenuItemBootstrapTable($element, "toolBar-menuItems", menuItems, productUuid);
		};
		configurer.prototype.onOk = function($container) {
			this.component.options.configuration = this.collectConfiguration($container);
		}
		// 收集配置信息
		configurer.prototype.collectConfiguration = function($container) {
			var configuration = {};
			// 基本信息
			this.collectBaseInfo(configuration, $container);
			// 一级导航信息
			this.collectMainNavInfo(configuration, $container);
			// 二级导航信息
			this.collectSubNavInfo(configuration, $container);
			// 工具栏信息
			this.collectToolBatInfo(configuration, $container);
			return $.extend({}, configuration);
		};
		configurer.prototype.collectBaseInfo = function(configuration, $container) {
			var $form = $("#widget_header_tabs_base_info", $container);
			var opt = designCommons.collectConfigurerData($form, collectClass);
			opt.subNavAndToolBarHidden = Boolean(opt.subNavAndToolBarHidden);
			$.extend(configuration, opt);
		};
		configurer.prototype.collectMainNavInfo = function(configuration, $container) {
			var $form = $("#widget_header_tabs_main_nav_info", $container);
			var opt = designCommons.collectConfigurerData($form, collectClass);
			// 一级导航
			var $tableMainNavMenuItemsInfo = $("#table_mainNav-menuItems_info", $container);
			var menuItems = $tableMainNavMenuItemsInfo.bootstrapTable("getData");
			opt.menuItems = menuItems;
			configuration.mainNav = configuration.mainNav || {};
			$.extend(configuration.mainNav, opt);
		};
		configurer.prototype.collectSubNavInfo = function(configuration, $container) {
			var $form = $("#widget_header_tabs_sub_nav_info", $container);
			var opt = designCommons.collectConfigurerData($form, collectClass);
			// 二级导航
			var $tableSubNavMenuItemsInfo = $("#table_subNav-menuItems_info", $container);
			var menuItems = $tableSubNavMenuItemsInfo.bootstrapTable("getData");
			opt.menuItems = menuItems;
			configuration.subNav = configuration.subNav || {};
			$.extend(configuration.subNav, opt);
		};
		configurer.prototype.collectToolBatInfo = function(configuration, $container) {
			var $form = $("#widget_header_tabs_tool_bar_info", $container);
			var opt = designCommons.collectConfigurerData($form, collectClass);
			// 工具栏
			var $tableToolBarMenuItemsInfo = $("#table_toolBar-menuItems_info", $container);
			var menuItems = $tableToolBarMenuItemsInfo.bootstrapTable("getData");
			opt.menuItems = menuItems;
			configuration.toolBar = configuration.toolBar || {};
			$.extend(configuration.toolBar, opt);
		};
		return configurer;
	}
	// 返回组件定义HTML
	component.prototype.toHtml = function() {
		var options = this.options;
		var configuration = options.configuration || {};
		var id = this.getId();
		var mainNavBarTpl = new StringBuilder();
		mainNavBarTpl.append('<div class="navbar navbar-{0} ui-wHeader-mainNavbar w-header-main-navbar" role="navigation">');
		mainNavBarTpl.append('	<div class="mainnav-content clearfix">');
		mainNavBarTpl.append('		<div class="navbar-header ui-wHeader-baseInfo">');
		mainNavBarTpl.append('			<a class="navbar-brand ui-wHeader-logo w-header-logo" href="#"></a>');
		mainNavBarTpl.append('			<a class="navbar-brand ui-wHeader-title" href="#"></a>');
		mainNavBarTpl.append('		</div>');
		//mainNavBarTpl.append('		<div>');
		mainNavBarTpl.append('			<ul class="nav navbar-nav navbar-right ui-wHeader-mainNav">');
		mainNavBarTpl.append('			</ul>');
		//mainNavBarTpl.append('		</div>');
		mainNavBarTpl.append('	</div>');
		mainNavBarTpl.append('</div>');
		var subNavBarTpl = new StringBuilder();
		// 隐藏二级导航判断处理
		if (configuration.subNavAndToolBarHidden !== true) {
			var navStyle = null;
			if (configuration.subNav) {
				navStyle = configuration.subNav.navStyle;
			}
			if (StringUtils.isNotBlank(navStyle)) {
				navStyle = "nav-" + navStyle;
			}
			subNavBarTpl.appendFormat('<div class="navbar navbar-{0} ui-wHeader-subNavbar">');
			subNavBarTpl.append('	<div class="subnav-content clearfix">');
			subNavBarTpl.appendFormat('		<ul class="nav navbar-nav {0} ui-wHeader-subNav">', navStyle);
			subNavBarTpl.append('		</ul>');
			subNavBarTpl.append('		<ul class="nav navbar-nav navbar-right ui-wHeader-toolBar">');
			subNavBarTpl.append('		</ul>');
			subNavBarTpl.append('	</div>');
			subNavBarTpl.append('</div>');
		}

		var sb = new StringBuilder();
		sb.appendFormat('<div id="{0}" class="ui-wHeader">', id);
		var backgroundColor = configuration.backgroudColor;
		sb.appendFormat(mainNavBarTpl.toString(), backgroundColor);
		sb.appendFormat(subNavBarTpl.toString(), backgroundColor);
		sb.appendFormat('</div>');
		return sb.toString();
	}

	// 返回组件定义JSON
	component.prototype.getDefinitionJson = function() {
		var options = this.options;
		var id = this.getId();
		options.id = id;
		return options;
	}
	return component;
});