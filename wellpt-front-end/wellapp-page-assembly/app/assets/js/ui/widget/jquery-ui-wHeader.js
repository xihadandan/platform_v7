(function(factory) {
	"use strict";
	if (typeof define === 'function' && define.amd) {
		// AMD. Register as an anonymous module.
		define([ 'jquery', 'constant', 'commons', "server",'appContext', "appModal" ], factory);
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function($, constant, commons, server, appContext, appModal) {
	"use strict";
	var Browser = commons.Browser;
	var StringUtils = commons.StringUtils;
	var StringBuilder = commons.StringBuilder;
	var SpringSecurityUtils = server.SpringSecurityUtils;
	// 组装当前LI项
	var navTpl = '<li class="nav-menu-item item-{0}" menuId="{1}">';
	navTpl += '<a href="#{1}"><span class="ui-wIcon {2}"></span>{3}</a></li>';
	var subNavTpl = '<li class="nav-menu-item" menuId="{1}">';
	subNavTpl += '<a href="#{1}"><span class="ui-wIcon {2}"></span>{3}</a></li>';
	$.widget("ui.wHeader", $.ui.wWidget, {
		options : {
			// 组件定义
			widgetDefinition : {},
			// 上级容器定义
			containerDefinition : {}
		},
		constant : {
			NAV_TYPE : {
				MAIN_NAV : "mainNav",
				SUB_NAV : "subNav",
				TOOL_BAR : "toolBar"
			}
		},
		_createView : function() {
			var _self = this;
			var options = $.extend({}, _self.options.widgetDefinition, _self.options.widgetDefinition.configuration);
			_self.$element = $(_self.element);
			_self._renderView(options);
			_self._setEvent(options);
		},
		// 生成页面组件
		_renderView : function(options) {
			var _self = this;
			// 渲染导航区块一（基本信息、一级导航）及导航区块二（二级导航、导航工具栏）包装层
			_self.$elementContainer = $(".ui-wHeader-mainNavbar .mainnav-content", _self.$element);
			// 渲染基本信息
			_self.renderBaseInfo(options);

			_self.menuItemMap = {};
			// 渲染一级导航
			_self.renderMainNav(options);

			// 渲染二级导航
			_self.renderSubNav(options);

			// 渲染导航工具
			_self.renderToolBar(options);
		},
		// 渲染基本信息
		renderBaseInfo : function(options) {
			var _self = this;
			if (options.logoFilePath) {
				var logoSrc = options.logoFilePath;
				var image = ' <img src="' + logoSrc + '" alt=""  />';
				$(".ui-wHeader-logo", _self.element).append(image);
			}
			if (options.systemTitle) {
				var systemTitle = '<b>' + (options.systemTitle ? options.systemTitle : "") + '</b>';
				$(".ui-wHeader-title", _self.element).append(systemTitle);
			}
		},
		// 渲染导航
		renderMainNav : function(options) {
			var _self = this;
			var mainNav = options.mainNav;
			if (!mainNav) {
				return;
			}
			var menuItems = mainNav.menuItems;
			if (!menuItems || menuItems.length == 0) {
				return;
			}
			var sb = new StringBuilder();

			// 处理LI存放位置
			var navShowCount = parseInt(options.mainNavShowCount) ? parseInt(options.mainNavShowCount) : 3;
			var navItemCount = 0;
			var navHasShowCount = 0;
			var moreLi = null;
			var moreNavs = [];
			for(var i = 0; i < menuItems.length; i++) {
				var nav = menuItems[i];
				_self.menuItemMap[nav.uuid] = nav;
				if (nav.hidden === "1") {
					continue;
				}
				var text = nav.text;
				var iconClass = nav.iconClass;
				// 显示主题设置
				if (nav.uuid === "theme") {
					var allThemes = appContext.getAllThemes();
					_self.themeMap = {};
					$.each(allThemes, function(i, theme) {
						_self.themeMap[theme.id] = theme;
					});
					var themeSwitcher = ".wHeaderNav-Theme-Switcher";
					var themeThemes = ".wHeaderNav-Themes";
					var themeLi = _self.getDropdown(nav, allThemes, themeSwitcher, themeThemes);
					sb.appendFormat(themeLi);
					continue;
				}else if(nav.uuid === "personInfo"){
					text = "";
					setTimeout(function(){
						var details = SpringSecurityUtils.getUserDetails();
						if(details && details.userName) {
							$("li[menuid='personInfo']>a", _self.element).append(details.userName);
						}
					}, 100);
				}
				if (navHasShowCount < navShowCount || _self.isDefaultNav(nav)) {
					if (nav.icon) {
						iconClass = nav.icon.className;
					}
					// sb.appendFormat(navTpl, navHasShowCount, nav.uuid, nav.iconClass, nav.text);
					sb.appendFormat(navTpl, navItemCount++, nav.uuid, iconClass, text);
				} else {
					if (moreLi == null) {
						var moreNav = {};
						moreNav.text = "更多";
						moreNav.iconClass = "glyphicon glyphicon-option-horizontal";
						moreLi = _self.getDropdown(moreNav, [], "wHeaderNavMore", "wHeaderNavMoreUL");
						sb.append(moreLi);
					}
					moreNavs.push(nav);
				}

				// 主题设置，个人信息，我的消息不参与显示限制
				if (!_self.isDefaultNav(nav)) {
					navHasShowCount++;
				}
			}

			$(".ui-wHeader-mainNav", _self.element).append(sb.toString());
			sb = new StringBuilder();
			$.each(moreNavs, function(i, nav) {
				if (nav.icon) {
					nav.iconClass = nav.icon.className;
				}
				// sb.appendFormat(navTpl, (navShowCount + i), nav.uuid, nav.iconClass, nav.text);
				sb.appendFormat(navTpl, (navItemCount++) + " item-more", nav.uuid, nav.iconClass, nav.text);
			});
			$(".wHeaderNavMoreUL", ".ui-wHeader-mainNav").append(sb.toString());
		},
		isDefaultNav : function(nav) {
			return nav.uuid === "theme" || nav.uuid === "personInfo" || nav.uuid === "myMsg";
		},
		getDropdown : function(nav, menus, navClass, menuClass) {
			var sb = new StringBuilder();
			if (nav.icon) {
				nav.iconClass = nav.icon.className;
			}
			sb.appendFormat('<li class="dropdown {0}">', navClass);
			sb.append('	<a href="#" class="dropdown-toggle" data-toggle="dropdown"');
			sb.append('	 role="button" aria-haspopup="true" aria-expanded="false">');
			sb.appendFormat('		<span class="ui-wIcon {0}"></span>{1}', nav.iconClass, nav.text);
			sb.append('		<span class="caret"></span>');
			sb.append('	</a>');
			sb.appendFormat('	<ul class="dropdown-menu {0}">', menuClass);
			$.each(menus, function(i, menu) {
				var li = '<li class="nav-menu-item" menuId="{0}"><a href="#">{1}</a></li>';
				sb.appendFormat(li, menu.id, menu.name);
			});
			sb.append('	</ul>');
			sb.append('	</li>');
			return sb.toString();
		},
		renderSubNav : function(options) {
			var _self = this;
			var subNav = options.subNav;
			if (options.subNavAndToolBarHidden === true || !subNav.menuItems.length) {
				return;
			}
			var menuItems = subNav.menuItems;
			//添加更多模块导航
			var showMenuItems = menuItems.length > 10 ? menuItems.slice(0,9): menuItems;
			var moreMenuItems = menuItems.length > 10 ? menuItems.slice(9,menuItems.length):[];
			var moreObject = {
				text:"更多模块",
				eventType:"click",
				isMore:true,
				hidden:"",
				iconClass:"caret",
				uuid:""
			}
			if(menuItems.length > 10){
				showMenuItems.push(moreObject);
			}
			if (!menuItems || menuItems.length == 0) {
				return;
			}
			var navStyle = subNav.navStyle;
			var tabContent = null;
			// 标签点位内容
			if (StringUtils.isNotBlank(navStyle)) {
				tabContent = new StringBuilder();
				tabContent.append('<div class="tab-content">');
			}
			var sb = new StringBuilder();
			$.each(showMenuItems, function(i, nav) {
				if (nav.hidden === "1") {
					return;
				}
				if (tabContent != null) {
					tabContent.appendFormat('<div class="tab-pane fade" id="{0}"></div>', nav.uuid);
				}				
				if (nav.icon) {
					nav.iconClass = nav.icon.className;
				}
				if(nav.isMore){
					var liTpl= '<li class="nav-menu-item item-{0} more-menu-item" >';
					liTpl += '<a class="dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">{2}<span class="ui-wIcon {1}"></span></a></li>';
					sb.appendFormat(liTpl,i,nav.iconClass, nav.text);
				}else{
					_self.menuItemMap[nav.uuid] = nav;
					sb.appendFormat(subNavTpl, i, nav.uuid, nav.iconClass, nav.text);
				}				
			});
			$(".ui-wHeader-subNav", _self.element).html(sb.toString());
			if (tabContent != null) {
				tabContent.append('</div>');
				$(tabContent.toString()).insertAfter($(".ui-wHeader-subNav", _self.element));
				// 初始化页签项
				$(".ui-wHeader-subNav a", _self.element).on("click", function(e) {
					e.preventDefault();
					$(this).tab("show");
					$(this).blur();
				})
			}
			if(moreMenuItems.length !=0 ){
				var sb1 = new StringBuilder();
				sb1.append("<ul class='more-menu-items dropdown-menu clearfix'>");
				$.each(moreMenuItems,function(i,nav){
					if (nav.hidden === "1") {
						return;
					}
					if (tabContent != null) {
						tabContent.appendFormat('<div class="tab-pane fade" id="{0}"></div>', nav.uuid);
					}
					_self.menuItemMap[nav.uuid] = nav;
					if (nav.icon) {
						nav.iconClass = nav.icon.className;
					}
					sb1.appendFormat(subNavTpl, i, nav.uuid, nav.iconClass, nav.text);
				})
				sb1.append('</ul>');
				$(".ui-wHeader-subNav .more-menu-item", _self.element).append(sb1.toString());
			}			
			$(".ui-wHeader-subNav li", _self.element).on("click", function(e) {
				e.preventDefault();
				$(".ui-wHeader-subNav li").removeClass("active");
				$(this).addClass("active");
			})
			var renderHeaderShortcut = function (){
				var preferences = appContext.getPreferences();
				var shortcutPref = preferences.get("indx/header/shortcut") || {};
				var sb = new StringBuilder();
				sb.append('<ul class="ui-wHeader-shortcut">');
				$.each(menuItems, function(i, nav) {
					if (nav.hidden === "1" || nav.hidden === 1) {
						return;
					}
					var pref = shortcutPref[nav.uuid];
					if(!pref || pref.hidden === true){
						return;
					}
					_self.menuItemMap[nav.uuid] = nav;
					if (nav.icon) {
						nav.iconClass = nav.icon.className;
					}
					sb.appendFormat('<li class="nav-menu-item shortcut-idx-{0}" menuId="{1}" title="{2}"><a href="#{1}" class="{3}"></a></li>', i, nav.uuid, nav.text, nav.iconClass);
				});
				sb.append('<li class="nav-menu-item custom-shortcut" title="添加"><a class="glyphicon glyphicon-plus" href="javascript:void(0);"></a></li></ul>');
				$(".ui-wHeader-shortcut", _self.element).remove();
				$(".ui-wHeader-subNavbar", _self.element).append(sb.toString());
				$(".ui-wHeader-shortcut>.custom-shortcut").click(function(evnet){
					var shortcutPref = preferences.get("indx/header/shortcut") || {};
					var sb = new StringBuilder();
					sb.append('<ul class="ui-wHeader-custom">');
					for(var i = 0; i < menuItems.length; i++){
						var nav = menuItems[i];
						if (nav.hidden === "1" || nav.hidden === 1) {
							continue;
						}
						var pref = shortcutPref[nav.uuid];
						var hidden = (!pref || pref.hidden === true);
						if (nav.icon) {
							nav.iconClass = nav.icon.className;
						}
						var checked = hidden ? "" : "checked=\"checked\""; 
						sb.appendFormat('<li class="nav-menu-item shortcut-idx-{0}"><input type="checkbox" menuId="{1}" id="D{1}" title="{2}" class="" {4}><label class="{3}" for="D{1}">{2}</li>', i, nav.uuid, nav.text, nav.iconClass, checked);
					}
					sb.append("</ul>");
					var $dialog = appModal.dialog({
						title : "快捷操作管理",
						size : "middle",
						message : sb.toString(),
						buttons : {
							ok : {
								label : "<i class='glyphicon glyphicon-ok'></i>确认",
								className : "btn-sm btn-primary",
								callback : function() {
									var shortcutPref = {};
									var $selected = $(".ui-wHeader-custom input[type=checkbox]:checked");
									$selected.each(function(i, elem){
										var $this = $(this);
										var title = $this.attr("title");
										var menuId = $this.attr("menuId");
										shortcutPref[menuId] = {
												"title" : title,
												"hidden" : false
										}
									});
									preferences.put("indx/header/shortcut", shortcutPref);
									preferences.flush();
									renderHeaderShortcut();
								}
							}
						}
					});
				});
			}
			if(options.shortcut === "1" || ($.isArray(options.shortcut) && options.shortcut[0] === "1")){
				renderHeaderShortcut();
			}else {
				setTimeout(function(){
					$(".jquery-ui-tips>.panel-body").each(function(){
						var $this = $(this);
						$this.css("margin-right", $this.css("margin-left"));
					})
					$(".ui-wPanel.index").each(function(){
						var $this = $(this);
						$this.css("margin-right", $this.css("margin-left"));
					})
				}, 0);
			}
		},
		renderToolBar : function(options) {
			var _self = this;
			var toolBar = options.toolBar;
			if (options.subNavAndToolBarHidden === true || !toolBar) {
				return;
			}
			var menuItems = toolBar.menuItems;
			if (!menuItems || menuItems.length == 0) {
				return;
			}
			var sb = new StringBuilder();
			$.each(menuItems, function(i, nav) {
				if (nav.hidden === "1") {
					return;
				}
				_self.menuItemMap[nav.uuid] = nav;
				if (nav.icon) {
					nav.iconClass = nav.icon.className;
				}
				sb.appendFormat(subNavTpl, i, nav.uuid, nav.iconClass, nav.text);
			});
			$(".ui-wHeader-toolBar", _self.element).html(sb.toString());
		},
		_setEvent : function(options) {
			var _self = this;
			var eventCallback = function(event) {
				var menuId = $(this).attr("menuId");
				if (StringUtils.isBlank(menuId)) {
					return;
				}
				var menuItem = _self.menuItemMap[menuId];
				if (menuItem == null) {
					// 主题判断处理
					var theme = _self.themeMap[menuId];
					if (theme != null) {
						_self.setTheme(theme.id);
					}
					return;
				}
				if (menuItem.eventType !== event.type) {
					return;
				}
				var handler = menuItem.eventHandler;
				var eventParams = menuItem.eventParams || {};
				var target = menuItem.target;
				var opt = {
					target : target.position,
					targetWidgetId : target.widgetId,
					refreshIfExists : target.refreshIfExists,
					appType : handler.type,
					appPath : handler.path,
					params : eventParams.params,
					event : event,
					selection : menuId
				};
				_self.startApp(opt);
			}
			// 点击事件
			$(_self.element).on("click", "li.nav-menu-item", eventCallback);

			// 鼠标移动
			var mouseover = constant.getValueByKey(constant.EVENT_TYPE, "MOUSE_OVER");
			var defaultSelectedItems = [];
			var menuItems = [];
			menuItems = menuItems.concat(options.mainNav.menuItems);
			menuItems = menuItems.concat(options.subNav.menuItems);
			menuItems = menuItems.concat(options.toolBar.menuItems);
			$.each(menuItems, function(i, menu) {
				if (menu.defaultSelected === "1") {
					defaultSelectedItems.push(menu);
				}
				if (menu.eventType !== mouseover) {
					return;
				}
				var menuId = menu.uuid;
				var selector = 'li.nav-menu-item[menuId="' + menuId + '"]';
				$(selector, _self.element).hoverDelay({
					hoverEvent : eventCallback
				});
			});

			// 选中元素，menuid或menu对应的eventHandler的id
			var selection = Browser.getQueryString("selection");
			if (StringUtils.isNotBlank(selection)) {
				// 1、menuid
				var selector = 'li.nav-menu-item[menuId="' + selection + '"]';
				if($(selector, _self.element).length > 0) {
					$(selector, _self.element).addClass("active");
				} else {
					// 2、menu对应的eventHandler的id
					for(var key in _self.menuItemMap) {
						var menuItem = _self.menuItemMap[key];
						var eh = menuItem.eventHandler;
						if(menuItem && eh && eh.id === selection) {
							var miSelector = 'li.nav-menu-item[menuId="' + menuItem.uuid + '"]';
							$(miSelector, _self.element).addClass("active");
						}
					}
				}
			} else {
				// 默认选中
				$.each(defaultSelectedItems, function(i, menu) {
					var selector = 'li.nav-menu-item[menuId="' + menu.uuid + '"]';
					$(selector, _self.element).addClass("active");
				});
			}

			// 页面加载完成后，调用获取数量的模块取数量
			var pageContainer = _self.pageContainer;
			pageContainer.on(constant.WIDGET_EVENT.PageContainerCreationComplete, function(e, ui) {
				if (_self.pageContainer !== ui) {
					return;
				}
				$.each(menuItems, function(i, nav) {
					// 获取导航徽章数量
					_self._getBadgetCount(nav);

					// 监听组件变化
					if ($.isEmptyObject(nav.badge)) {
						return;
					}
					var listViewId = nav.badge.widgetDefId;
					if (StringUtils.isBlank(listViewId)) {
						return;
					}
					var listViewWidget = appContext.getWidgetById(listViewId);
					if (listViewWidget != null) {
						listViewWidget.on(constant.WIDGET_EVENT.Change, function(e, ui) {
							var totalCount = ui.getDataProvider().getCount();
							_self._setBadgetCount(nav, totalCount);
						});
					}
				});
			});
			// 微章刷新事件
			pageContainer.on(constant.WIDGET_EVENT.BadgeRefresh, function() {
				_self.refreshBadge();
			});
		},
		// 获取导航徽章数量
		_getBadgetCount : function(nav) {
			var _self = this;
			if (!$.isEmptyObject(nav.badge)) {
				var countJsModule = nav.badge.countJsModule;
				if (StringUtils.isNotBlank(countJsModule)) {
					_self.startApp({
						isJsModule : true,
						jsModule : countJsModule,
						action : 'getCount',
						data : nav.badge,
						callback : function(count, realCount, options) {
							_self._setBadgetCount(nav, count, realCount);
						}
					});
				}
			}
		},
		_setBadgetCount : function(nav, count, realCount) {
			var _self = this;
			var selector = 'li.nav-menu-item[menuId="' + nav.uuid + '"]';
			var $nav = $(selector, _self.element).children("a");
			var $badge = $nav.children("span.badge");
			if ($badge.length == 0) {
				$badge = $('<span class="badge">' + count + '</span>');
				$nav.append($badge)
			} else {
				$badge.html(count);
			}
		},
		// 刷新徽章
		refreshBadge : function() {
			var _self = this;
			var menuItemMap = _self.menuItemMap;
			$.each(menuItemMap, function() {
				_self._getBadgetCount(this);
			});
		}
	});
}));