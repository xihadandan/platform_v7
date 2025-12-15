(function(factory) {
	"use strict";
	if (typeof define === 'function' && define.amd) {
		// AMD. Register as an anonymous module.
		define([ 'jquery', 'jquery-ui', 'constant', 'commons', 'appContext' ], factory);
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function($, ui, constant, commons, appContext) {
	"use strict";
	var StringUtils = commons.StringUtils;
	$.widget("ui.wWidget", {
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
			return {};
		},
		registerAppDispatcher : function(callback) {
			// 注册应用派发器
			appContext.registerAppDispatcher.call(appContext, callback);
		},
		// 获取组件发起者
		getInitiator : function() {
			var _self = this;
			if(_self.options.initiator) {
				return _self.options.initiator;
			}
			var pageContainer = _self.getPageContainer();
			return pageContainer == this ? {} : (pageContainer.getInitiator ? pageContainer.getInitiator() : {});
		},
		startApp : function(options) {
			// 启动应用
			if(options.target === '_navTab') {
                appContext.startApp.call(options.appContext, this, options);
			} else {
                appContext.startApp.call(appContext, this, options);
			}
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
			appContext.dispatchEvent(type, eventData, this);
		},
		on : function(type, selector, data, eventHandle) {
			// 添加事件监听
			appContext.addEventListener(type, selector, data, eventHandle, this);
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
		// 锚点选择回调处理
		onHashSelection : function(options) {
		},
		off : function(type, selector, eventHandle) {
			// 删除事件监听
			appContext.removeEventListener(type, selector, eventHandle, this);
		},
		// 返回二开的JS模块
		getDevelopmentModules : function() {
			var _self = this;
			var jsModule = _self.getConfiguration().jsModule;
			var devJsModules = [];
			if (StringUtils.isNotBlank(jsModule)) {
				devJsModules = jsModule.split(constant.Separator.Semicolon);
			}
			return devJsModules;
		},
		// 调用JS二开模块的回调接口
		invokeDevelopmentMethod : function(method, args) {
			var _self = this;
			var develops = _self.develops;
			var jq = $;
			if (jq.isArray(develops)) {
				var methodArgs = args;
				if (!methodArgs) {
					methodArgs = [];
				}
				jq.each(develops, function(i, develop) {
					if (jq.isFunction(develop[method])) {
						try {
							develop[method].apply(develop, methodArgs);
						} catch (e) {
							console.error(e);
						}
					} else {
						if (jq.isFunction(develop.onUnkonwnDevelopmentMethodInvoked)) {
							develop.onUnkonwnDevelopmentMethodInvoked.call(develop, method, methodArgs);
						}
						console.log("cann't invoke method[" + method + "],method not find.");
					}
				});
			}
		},
		_create : function() {
			var _self = this;
			_self.pageContainer = appContext.getPageContainer();
			_self.element.addClass(_self.widgetFullName);
			_self.element.data("widgetFullName", _self.widgetFullName);
			var configuration = _self.getConfiguration();
			if (configuration && configuration.customClass) {
				_self.element.addClass(configuration.customClass);
			}
			if(configuration&&configuration.name){
				_self.element.attr('w-name',configuration.name);
			}
			var callback = function() {
				try {
					// 初始化二开模块
					var develops = _self.develops || [];
					for (var i = 0; i < arguments.length; i++) {
						var developsModule = arguments[i];
						if (developsModule) {
							var develop = new developsModule(_self);
							develops.push(develop);
						}
					}
					_self.develops = develops;
					// 准备创建回调
					_self.invokeDevelopmentMethod("prepare");
					if (_self.options.onPrepare) {
						_self.options.onPrepare.call(_self, _self);
					}

					// 创建页面组件
					_self._createView();

					// 创建后回调
					_self.invokeDevelopmentMethod("create");
					if (_self.options.onCreate) {
						_self.options.onCreate.call(_self, _self);
					}

					// 初始化回调
					_self.invokeDevelopmentMethod("init");
					if (_self.options.onInit) {
						_self.options.onInit.call(_self, _self);
					}
				} catch (e) {
					appContext.onWidgetCreateError(_self);
					console.error(e);
				}
			};
			var devJsModules = _self.getDevelopmentModules();
			try {
				if (devJsModules && devJsModules.length > 0) {
					appContext.require(devJsModules, callback);
				} else {
					callback();
				}
			} catch (e) {
				appContext.onWidgetCreateError(_self);
				console.error(e);
			}
		},
		_createView : $.noop,
		// 获取页面容器
		getPageContainer : function(getRoot) {
			var _self = this;
			// 获取最顶层的页面容器
			if (getRoot || WebApp.pageDefinition == null) {
				_self.pageContainer = appContext.getPageContainer();
			} else {
				// 获取最近的页面容器
				var uiCssClass = "ui-" + WebApp.pageDefinition.wtype;
				var $container = $(_self.element).closest("." + uiCssClass);
				if ($container.length > 0) {
					var container = $($container[0]).data(uiCssClass);
					_self.pageContainer = container;
				}
				if (_self.pageContainer == null) {
					_self.pageContainer = appContext.getPageContainer();
				}
			}
			return _self.pageContainer;
		},
		destroy:function(){
			this.element.remove();
		},
		getRenderPlaceholder : function() {
			// 获取渲染组件的占位符
			return this.element;
		},
		// 隐藏
		hide : function() {
			$(this.element).hide();
		},
		// 显示
		show : function() {
			$(this.element).show();
		},
		// 刷新
		refresh : function() {
			var widgetDefinition = this.options.widgetDefinition;
			var _arguments = arguments;
			$.each(widgetDefinition.items, function(index, childWidgetDefinition) {
				var childWidget = appContext.getWidgetById(childWidgetDefinition.id);
				if (childWidget) {
					childWidget.refresh.apply(childWidget, _arguments);
				}
			});
		}
	});
}));