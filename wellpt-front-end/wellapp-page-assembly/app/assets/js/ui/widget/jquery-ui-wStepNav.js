(function(factory) {
	"use strict";
	if (typeof define === 'function' && define.amd) {
		// AMD. Register as an anonymous module.
		define([ 'jquery', 'appContext','server','commons',"constant" ], factory);
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function($, appContext,server,commons,constant) {
	"use strict";
	var StringUtils = commons.StringUtils;
	$.widget("ui.wStepNav", $.ui.wWidget, {
		options : {
			// 组件定义
			widgetDefinition : {},
			// 上级容器定义
			containerDefinition : {}
		},
		_createView : function() {
			// 初始化基本信息
			this.element.addClass("ui-wstep-nav");
			// "生成布局"（flow）和"绘制"（paint）这两步，合称为"渲染"（render）
			this._beforeRenderView();
			this._renderView();
			this._afterRenderView();
		},
		_renderView : function() {
			// 生成布局
			this._createLayout();
			//绑定事件
			this._setEvent();
			// 生成页面组件
			this._createWidgets();
		},
		_createLayout : function() {
			//渲染步骤头部
			var configuration = this.options.widgetDefinition.configuration;
			var steps = configuration.steps;
			var stepNavs = configuration.stepsData;
			var stepBtns = configuration.buttons;
			var _self = this;
			for(var i=0;i<steps.length;i++){
				var navContainer = $("<div class='step-nav-container'/>");
				var navContent = $("<ul class='step-nav-content clearfix'/>");
				for(var j=0;j<stepNavs.length;j++){
					var navItem;
					if(i == j){
						navItem = $("<li class='step-nav-item active'/>");
					}else if(i < j){
						navItem = $("<li class='step-nav-item next'/>");
					}else{
						navItem = $("<li class='step-nav-item'/>");
					}
					var navNum = $("<p class='step-nav-num'/>").html(j+1);
					var stepName = stepNavs[j].name == "" ? stepNavs[j].text : stepNavs[j].name;
					var navText = $("<p class='step-nav-text'/>").html(stepName);
					navItem.append(navNum).append(navText);
					navContent.append(navItem);
				}
				navContainer.append(navContent);
				$(".step-nav-"+i ,this.element).append(navContainer);
				var btnToolbar = $("<div class='step-btn-toolbar'/>");
				$(".step-btns-"+i ,this.element).append(btnToolbar);
			}
			for(var j=0;j<stepBtns.length;j++){				
				var btn = $("<button class='btn "+stepBtns[j].cssClass+"' id='"+stepBtns[j].uuid+"'/>");
				if(stepBtns[j].icon){
					var btnText = "<i class="+stepBtns[j].icon.className+"></i>"+stepBtns[j].text;
				}else{
					var btnText = stepBtns[j].text;
				}
				btn.html(btnText);
				var btnPosition = stepBtns[j].position;
				var events = stepBtns[j].eventManger;
				var positionNum;
				if(typeof(stepBtns[j].position) == "object"){
					for(var index=0;index<btnPosition.length;index++){
						positionNum = btnPosition[index]-1;
						$(".step-btn-toolbar",".step-btns-"+ positionNum).append(btn);
					}
				}else{
					positionNum = btnPosition-1;
					$(".step-btn-toolbar",".step-btns-"+ positionNum).append(btn);
				}
			}
		},
		_beforeRenderView : function() {
            var _self = this;
            _self.invokeDevelopmentMethod('beforeRender', [ _self.options, _self.getConfiguration() ]);
	    },
	    _afterRenderView : function() {
            var _self = this;
            _self.invokeDevelopmentMethod('afterRender', [ _self.options, _self.getConfiguration() ]);
        },      
		_setEvent:function(){
			var _self = this;
			var btnDevelopArr = _self.invokeDevelopmentMethod('setBtnDefaultEvent', [ _self.options, _self.getConfiguration()]);
			btnDevelopArr = btnDevelopArr ? btnDevelopArr : [];
			var configuration = _self.getConfiguration();
			var buttons = configuration.buttons;
			for(var i=0;i<buttons.length;i++){
				if(btnDevelopArr.indexOf(buttons[i].uuid) == -1){
					if(buttons[i].text == "下一步"){
						$("#"+buttons[i].uuid,_self.element).on("click",function(){
							$(this).parents(".active").next().addClass("active").removeClass("next");
							$(this).parents(".active").addClass("passed").removeClass("active");
						})				
					}else if(buttons[i].text == "上一步"){
						$("#"+buttons[i].uuid,_self.element).on("click",function(){
							$(this).parents(".active").prev().addClass("active").removeClass("passed");
							$(this).parents(".active").addClass("next").removeClass("active");
						})
					}
				}
				if(!$.isEmptyObject(buttons[i].eventManger)){
					_self._setCustomEvent($("#"+buttons[i].uuid,_self.element),buttons[i].eventManger)
				}
			}
		},
		/**
         * 获取JS模块
         */
        getDevelopmentModules: function () {
            var _self = this;
            var jsModule = _self.getConfiguration().jsModule;
            if (StringUtils.isBlank(jsModule)) {
                _self.devJsModules = [];
            } else {
                _self.devJsModules = jsModule.split(constant.Separator.Semicolon);
            }
            return _self.devJsModules;
        },
		_setCustomEvent:function(btn,config){
			var _self = this;
			if (!$.isEmptyObject(config.defineEventJs)) {
                for (var k in config.defineEventJs) {
                    var bindDefineEvent = function (eventName) {
                    	btn.on(eventName, function (event) {
                            var defineFunc = appContext.eval(config.defineEventJs[eventName], $(this), {
                                '$this': _self, 'event': event, 'commons': commons,'server': server
                            }, function (v) {
                                if (v === false) {
                                    event.preventDefault();
                                    event.stopImmediatePropagation();
                                }
                            });
                            console.log('执行按钮脚本：', defineFunc);
                        });
                        if (eventName === 'init') {//立即触发初始化事件
                            btn.trigger(eventName);
                        }
                    };
                    bindDefineEvent(k);
                }
            }
		},
		_createWidgets : function() {
			var _self = this;
			_self._columns = new Object();
			var columns = $(this.element).children();
			var parentWidth = $(this.element).innerWidth();
			$.each(columns,function(index,item){
				var itemWidth = $(item).innerWidth()/parentWidth*100;
				itemWidth = parseFloat(itemWidth.toFixed(1));
				//除去空的容器占位
				if($(item).html() != ""){
					_self._columns["column-"+index] = itemWidth;
				}
			})
			var bootgridDefinition = this.options.widgetDefinition;
			$.each(bootgridDefinition.items, function(index, childWidgetDefinition) {
				appContext.createWidget(childWidgetDefinition, bootgridDefinition);				
			});
		},
		getRenderPlaceholder : function() {
			var _self = this;
			if (_self.renderPlaceholder == null) {
				var wtype = _self.getWtype();
				// 获取渲染组件的占位符，只有一列布局返回该列元素
				var $children = _self.element.children();
				if ($children.length == 1) {
					_self.renderPlaceholder = $children[0];
				} else {
					// 调用父类提交方法
					_self.renderPlaceholder = _self._superApply(arguments);
				}
			}
			return _self.renderPlaceholder;
		}
	});
}));