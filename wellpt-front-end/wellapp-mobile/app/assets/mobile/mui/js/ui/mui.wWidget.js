(function(factory) {
	"use strict";
	if (typeof define === 'function' && define.amd) {
		// AMD. Register as an anonymous module.
		define([ 'mui', 'constant', 'commons', 'appContext' ], factory);
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function($, constant, commons, appContext) {
	"use strict";
	var StringUtils = commons.StringUtils;
	$.widget("mui.wWidget", {
		version : constant.WIDGET_VERSION,
		getId : function() {
			// 返回组件ID
			return this.options.widgetDefinition.id;
		},
		getWtype : function() {
			// 返回组件类型
			return this.options.widgetDefinition.wtype;
		},
		getConfiguration : function() {
			// 返回配置信息
			return this.options.widgetDefinition.configuration || {};
		},
		getParent : function(){
			return this.options.containerInstance;
		},
		setTheme : function(theme) {
			// 设置主题
			appContext.setTheme(theme, this);
		},
		getCurrentUserAppData : function() {
			// 获取当前用户的应用数据
			return appContext.getCurrentUserAppData();
		},
		getDataProvider : function() {
			// 数据提供者，获取组件对应的数据
		},
		registerAppDispatcher : function(callback) {
			// 注册应用派发器
			appContext.registerAppDispatcher.call(appContext, callback);
		},
		startApp : function(options) {
			// 启动应用
			appContext.startApp.call(appContext, this, options);
		},
		getDispatcher : function(create) {
			// 获取应用派发器, create为true创建一个新的派发器，否则返回默认的应用派发器
			return appContext.getDispatcher.call(appContext, create);
		},
		getEventType : function(type) {
			// 返回组件事件类型
			return this.getWtype() + "." + type;
		},
		trigger : function(type, eventData) {
			// 触发事件
			var self = this;
			appContext.dispatchEvent(self.getEventType(type), eventData, self);
		},
		on : function(type, selector, data, eventHandle) {
			// 添加事件监听
			var self = this;
			appContext.addEventListener(self.getEventType(type), selector, data, eventHandle, self);
		},
		off : function(type, selector, eventHandle) {
			// 删除事件监听
			var self = this;
			appContext.removeEventListener(self.getEventType(type), selector, eventHandle, self);
		},
		onWidgetCreated : function(widgetId, data, eventHandle) {
			if (StringUtils.isBlank(widgetId)) {
				console.log("onWidgetCreated: parameters of widgetId is blank");
				return;
			}
			// 添加组件创建完成事件监听
			var selector = "#" + widgetId;
			var eventType = constant.WIDGET_EVENT.WidgetCreated;
			appContext.removeEventListener(eventType, selector, eventHandle, this);
			appContext.addEventListener(eventType, selector, data, eventHandle, this);
		},
		_create : function() {
			var _self = this;
			_self.pageContainer = appContext.getPageContainer();
			var callback = function() {
				// 初始化二开模块
				var develops = [];
				for (var i = 0; i < arguments.length; i++) {
					var developsModule = arguments[i];
					if (developsModule) {
						var develop = new developsModule(_self);
						develops.push(develop);
					}
				}
				_self.develops = develops;
				// 准备创建回调
				_self._executeJsModule("prepare");
				if (_self.options.onPrepare) {
					_self.options.onPrepare.call(_self, _self);
				}
				var configuration = _self.getConfiguration();
				if(configuration && configuration.customClass){
					_self.element[0].classList.add(configuration.customClass);
				}
				// 创建页面组件
				_self._createView();

				// 创建后回调
				_self._executeJsModule("create");
				if (_self.options.onCreate) {
					_self.options.onCreate.call(_self, _self);
				}

				// 初始化回调
				_self._executeJsModule("init");
				if (_self.options.onInit) {
					_self.options.onInit.call(_self, _self);
				}
			};
			var devJsModules = _self._getJsModules();
			try {
				if (devJsModules && devJsModules.length > 0) {
					appContext.require(devJsModules, callback);
				} else {
					callback();
				}
			} catch (e) {
				console.error(e);
			}
		},
		_createView : $.noop,
		getRenderPlaceholder : function() {
			// 获取渲染组件的占位符
			return this.element;
		},
		/**
		 * 获取JS模块
		 */
		_getJsModules : function() {
			var self = this;
			if (!self.devJsModules) {
				var jsModule = self.getConfiguration().jsModule;
				if (StringUtils.isBlank(jsModule)) {
					self.devJsModules = [];
				} else {
					self.devJsModules = jsModule.split(constant.Separator.Semicolon);
				}
			}
			return self.devJsModules;
		},
		/**
		 * 执行JS模块化方法
		 */
		_executeJsModule : function(methodName, args) {
			var _self = this;
			var develops = _self.develops;
			var jq = $;
			if (jq.isArray(develops)) {
				var methodArgs = args;
				if (!methodArgs) {
					methodArgs = [];
				}
				jq.each(develops, function(i, develop) {
					if (jq.isFunction(develop[methodName])) {
						try {
							develop[methodName].apply(develop, methodArgs);
						} catch (e) {
							console.error(e);
						}
					} else {
						console.log("cann't invoke method[" + methodName + "],method not find.");
					}
				});
			}
		},
		/**
		 * 执行JS模块化方法
		 */
		_executeJsModuleWithResult : function(methodName, args) {
			var _self = this;
			var develops = _self.develops;
			var jq = $, results = [];
			if (jq.isArray(develops)) {
				var methodArgs = args;
				if (!methodArgs) {
					methodArgs = [];
				}
				for(var i=0;i<develops.length; i++){
					var develop = develops[i];
					if (jq.isFunction(develop[methodName])) {
						try {
							var result = develop[methodName].apply(develop, methodArgs);
							if(result === false){
								return false;
							} else {
								results.push(result);
							}
						} catch (e) {
							console.error(e);
						}
					} else {
						console.log("cann't invoke method[" + methodName + "],method not find.");
					}
				}
			}
			return results;
		},
		hasDevelopmentMethod : function(methodName){
			var self = this;
			var develops = self.develops || [];
			for(var i = 0; i < develops.length; i++){
				var dev = develops[i];
				if($.isFunction(dev[methodName])){
					return true;
				}
			}
			return false;
		},
		invokeDevelopmentMethod : function(){
			var self = this;
			self._executeJsModule.apply(self, arguments);
		},
		// 刷新
		refresh : $.noop,
		getChildWidgetByType : function(wTypes) {
			var self = this;
			// String or Array
			wTypes = wTypes || [];
			var widgetDefinitions = [];
			var widgetDefinition = self.options.widgetDefinition;
			// 获取当前
			if(wTypes.length === 0 || wTypes.indexOf(widgetDefinition.wtype) >= 0){
				widgetDefinitions.push(self);
			}
			// 获取子节点
			$.each(widgetDefinition.items || [], function(idx, item){
				var childWidget = appContext.getWidgetById(item.id);
				if(childWidget && childWidget.getChildWidgetByType){
					widgetDefinitions = widgetDefinitions.concat(childWidget.getChildWidgetByType(wTypes));
				}
			});
			return widgetDefinitions;
		},
		getPageContainer : function(getRoot) {
			var _self = this;
			return appContext.getPageContainer();
		},
		// 隐藏
		hide : function() {
			$(this.element).hide();
		},
		// 显示
		show : function() {
			$(this.element).show();
		},
	});
}));