(function(factory) {
	"use strict";
	if (typeof define === 'function' && define.amd) {
		// AMD. Register as an anonymous module.
		define([ 'mui', 'commons', 'constant', 'mui-wWidget','server', 'appModal', "formBuilder"], factory);
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function($, commons, constant, widget,server,appModal, formBuilder) {
	"use strict";
	var MobileNav = {};
	var StringUtils = commons.StringUtils;
	var ObjectUtils = commons.ObjectUtils;
	var bootstrapTableViewGetCount = "BootstrapTableViewGetCount";
	$.widget("mui.wMobileNav", $.ui.wWidget, {
		options : {
			// 组件定义
			widgetDefinition : {
				configuration : {}
			},
			// 上级容器定义
			containerDefinition : {},
			data : [],
		},
		/**
		 * 获取配置对象
		 */
		getConfiguration : function() {
			return this.options.widgetDefinition.configuration;
		},
		/**
		 * 获取配置对象
		 */
		getId : function() {
			return this.options.widgetDefinition.id;
		},
		_createView : function() {
			var self = this;
			var options = $.extend({}, self.options.widgetDefinition, self.options.widgetDefinition.configuration,{defaultMenuLevel:1});
			self._executeJsModule('beforeRender', [ self.options, self.getConfiguration() ]);
			self._renderView(options);
			self._executeJsModule('afterRender', [ self.options, self.getConfiguration() ]);
		},	
		_classSelector : function(className) {
			var id = this.getId();
			return '#' + id + ' ' + className;
		},
		_mui : function(className) {
			var self = this;
			return mui(className, self.element[0]);
		},
		_loadTree : function(parentId, searchText, isShowRoot){
			var nav = [];
			var params = {};
			var self = this;
			var options = self.getConfiguration();
			if(StringUtils.isNotBlank(parentId)){
				params.parentId = parentId;
			}
			params.dataProvider = options.navInterface,
			params.nodeTypeInfo = JSON.stringify(options.nodeTypeInfo)
			$.ajax({
				type : "POST",
				url : ctx + "/basicdata/treecomponent/loadTree",
				data : params,
				async : false,
				dataType : "json",
				success : function(data) {
					if (isShowRoot === false) {
						$.each(data, function(idx, item){
							nav = nav.concat(item.children);
						});
					} else {
						nav = data;
					}
				}
			});
			return nav;
		},
		_renderView : function(options) {
			var _self = this;
			_self.menuItemMap = {};
			_self.menuItemArray = [];
			// NAV数据
			var nav = [];
			var navEvent;//导航事件统一配置，用于动态生成的导航节点事件处理
			if (options.navType == 1) {
				if (options.navResource) {
					server.JDS.call({
						async : false,
						service : "appProductIntegrationMgr.getTreeByUuid",
						data : [ options.navResource, [ "1", "2", "3" ] ],
						success : function(result) {
							if (options.isShowRoot) {
								nav.push(result.data);
							} else {
								nav = result.data.children;
							}
						}
					});
				} else {
					var userAppData = appContext.getCurrentUserAppData();
					var moduleApps = userAppData.getModuleApps();
					for(var i = 0; i < moduleApps.length; i++) {
						var moduleApp = moduleApps[i];
						var mApp = {};
						mApp.id = moduleApp.piUuid;
						mApp.data = {};
						mApp.data.name = moduleApp.name;
						mApp.data.path = moduleApp.path;
						mApp.data.type = 3;
						nav.push(mApp);
					}
				}
			} else if(options.navType == 2){
				if (options.isShowRoot === true) {
					nav = options.nav;
				} else {
					$.each(options.nav, function(i, navData){
						nav = nav.concat(navData.children);
					})
				}
			}  else if(options.navType == 3){
				nav = _self._loadTree(null, null, options.isShowRoot);
				navEvent={
					targetWidgetId:options.configuration.targetWidgetId,
					targetPosition:options.configuration.targetPosition,
					refreshIfExists:options.configuration.refreshIfExists,
					eventHanlderPath:options.configuration.eventHanlderPath,
					eventHanlderType:options.configuration.eventHanlderType,
					eventParams:options.configuration.eventParams
				};
			}
			var navArr = nav;
			// 生成页面组件			
			var configuration = _self.getConfiguration();
			var StringBuilder = commons.StringBuilder;
			var sb = new StringBuilder();
			sb.append('<ul class="mui-nav mui-table-view mui-table-view-striped mui-table-view-condensed table-view-content">');
			_self._renderItems(options, navArr, navEvent, options.defaultMenuLevel, sb);
			sb.append('	</ul>');
			_self.element[0].innerHTML = sb.toString();
			_self._setEvent();
		},
		/**
		 * 渲染列表item
		 */
		_renderItems : function(options, nav, navEvent, navLevel, sb){
			var _self = this;
			$.each(nav || [], function(idx, item){
				if(typeof item === "undefined" || item== null) {
					return;
				}
				if (item.data && item.data.hidden) {
					return;
				}
				var navData = item.data;
				var menuId = navData.uuid || navData.id;
				// UUID为空，随机生成
				if (StringUtils.isBlank(menuId)) {
					menuId = commons.UUID.createUUID();
					navData.uuid = menuId;
				}
				navData.menuId = menuId;// 统一ID
				navData.menuName = options.navType == 1 ? navData.name : item.name; // 统一名称
				navData.navType = options.navType;
				navData.navEvent = item.isParent ? null: navEvent;
				_self.menuItemMap[menuId] = navData;
				_self.menuItemArray.push(navData);
				
				// panelNavArr.push(navData);				
								
				var isParent = _self._hasChidren(item.isParent || navData.isParent, item.children);
				var itemClass = "mui-table-view-cell";
				if(isParent === true) {
					itemClass += " mui-collapse";
					if(options.autoToggle == '1'){
					}else {
						itemClass += " mui-active";
					}
				}
				// 区分一、二、三级菜单
				itemClass += " nav-menu-item menu-item-level-" + navLevel;
				sb.appendFormat('<li class="{0}" menuid="{1}" data-menu-level="{2}" style="{3}">', itemClass, menuId, navLevel, navData.iconStyle || "");
				sb.appendFormat('<a class="mui-navigate-right" href="#">');
				if(navData && navData.icon){
					sb.appendFormat('<i class="{0}"></i>', navData.icon);
				}else if(isParent === true){
					sb.appendFormat('<i class="{0}"></i>', "fa fa-folder-o");
				}else {
					sb.appendFormat('<i class="{0}"></i>', "fa fa-list-ul");
				}
				if(navData.showBadgeCount && navData.getBadgeCountListViewId){
					sb.appendFormat('<span class="mui-badge mui-badge-purple mui-hidden"></span>');
					var getBadgeCountListViewId = navData.getBadgeCountListViewId;
					navData.badge = {
						countJsModule : bootstrapTableViewGetCount,
						widgetDefId : getBadgeCountListViewId
					};
					// 异步处理
					setTimeout(function() {
						_self._getBadgetCount(navData);
					}, 0);
				}
				sb.appendFormat('<span class="nva-menu-text">{0}</span></a>', navData.menuName);
				sb.appendFormat("</a>");
				
				if (isParent === true) {
					if(item.data && item.data.active){
					}else{
					}
					sb.appendFormat('<div class="mui-collapse-content">');
					sb.appendFormat('<ul class="mui-table-view">');
					_self._renderItems(options, item.children, navEvent, navLevel + 1, sb);
					sb.appendFormat('</ul>');
					sb.appendFormat('</div>');
				}
				sb.appendFormat('</li>');
			})

		},
		_hasChidren : function(isParent, children) {
			return ((children && children.length > 0) || isParent === true);
		},
		getMenuArray : function(){
			var self = this;
			return self.menuItemArray || [];
		},
		getMenuItems : function(menuId){
			var self = this;
			var menuItemMap = self.menuItemMap;
			return menuId ? menuItemMap[menuId] : menuItemMap;
		},
		// 绑定导航点击事件
		_setEvent:function(){
			var _self = this;
			var configuration = _self.getConfiguration();
			_self.element.on("tap", "li.mui-table-view-cell[menuid]", function(event){
				var $self = $(event.target).closest(".mui-table-view-cell[menuid]");
				if($self.classList.contains("mui-collapse")){
					return;// 隐藏子项
				}
				var menuId = $self.getAttribute("menuid");
				var menuLevel = $self.getAttribute("data-menu-level");
				var menuItem = _self.menuItemMap[menuId];
				if (menuItem == null || typeof menuItem === "undefined") {
					return;
				}
				event.preventDefault();
				event.stopPropagation();
				var appType = menuItem.eventHanlderType || menuItem.type || menuItem.appType;
				var appPath = menuItem.eventHanlderPath || menuItem.path || menuItem.appPath;
				var targetPosition = menuItem.targetPosition;
				var targetWidgetId = menuItem.targetWidgetId;
				var refreshIfExists = menuItem.refreshIfExists;
				var eventParams = menuItem.eventParams;//|| {}
				//使用动态导航的事件配置
				if(!appType&&menuItem.navEvent){
					appType=menuItem.navEvent.eventHanlderType;
				}
				if(!appPath&&menuItem.navEvent){
					appPath=menuItem.navEvent.eventHanlderPath;
				}
				if(!targetPosition&&menuItem.navEvent){
					targetPosition=menuItem.navEvent.targetPosition;
				}
				if(!targetWidgetId&&menuItem.navEvent){
					targetWidgetId=menuItem.navEvent.targetWidgetId;
				}
				if(!refreshIfExists&&menuItem.navEvent){
					refreshIfExists=menuItem.navEvent.refreshIfExists;
				}
				if(!eventParams&&menuItem.navEvent){
					eventParams=menuItem.navEvent.eventParams;
				}
				eventParams = eventParams || {}
				var opt = {
					event : event,
					value : menuId,
					menuId : menuId,
					menuItem : menuItem,
					name : menuItem.menuName,//$self.innerText,
					target : targetPosition || "",
					targetWidgetId : targetWidgetId || "",
					refreshIfExists : refreshIfExists,
					appType : appType,
					appPath : appPath,
					params : eventParams,
				};
				for(var pKey in eventParams) {
					if(pKey.indexOf("opt_") === 0) {
						var pValue = eventParams[pKey];
						delete eventParams[pKey]; // 删除取值表达式
						pKey = pKey.replace("opt_", "");
						pValue = ObjectUtils.expValue(opt, pValue) || "";
						eventParams[pKey] = pValue;
					}
				}
				// 同步生成panel下拉导航菜单
				configuration.syncMenu = configuration.syncMenu || true;
				if(configuration && configuration.syncMenu) {
					// 使用后及时删除
					var _requestMenuId = eventParams["_requestMenuId"] = opt.menuId;
					var _requestMenuName = eventParams["_requestMenuName"] = opt.name;
					var _requestNavCode = eventParams["_requestNavCode"] = _self.getId();
					var _requestNavLevel = eventParams["_requestNavLevel"] = menuLevel || "1";
				}
				_self.startApp(opt);
				
				var item = {};
				$.extend(item, opt);
				delete item.event;
				var eventData = {
					selectedItem : item
				};
				_self.trigger("tap", eventData);
			})
		},
		// 获取导航徽章数量
		_getBadgetCount : function(nav) {
			var _self = this;
			if ($.isEmptyObject(nav.badge) === false) {
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
			var selector = '.mui-table-view-cell[menuid=' + nav.menuId + ']>a.mui-navigate-right';
			var $nav = $(selector, _self.element[0]);
			if (!$nav || $nav.length <= 0 ){
				return false;
			}
			var $badge = $nav[0].querySelector("span.mui-badge");
			if (typeof $badge === "undefined" || $badge == null) {
				$nav[0].appendChild($.dom('<span class="mui-badge mui-badge-purple">' + count + '</span>')[0]);
			} else {
				$badge.innerHTML = count;
				$badge.classList.remove("mui-hidden");
			}
		},
	});
	return MobileNav;
}));