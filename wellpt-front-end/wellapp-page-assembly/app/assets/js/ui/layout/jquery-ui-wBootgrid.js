(function(factory) {
	"use strict";
	if (typeof define === 'function' && define.amd) {
		// AMD. Register as an anonymous module.
		define([ 'jquery', 'appContext' ], factory);
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function($, appContext) {
	"use strict";
	$.widget("ui.wBootgrid", $.ui.wWidget, {
		options : {
			// 组件定义
			widgetDefinition : {},
			// 上级容器定义
			containerDefinition : {}
		},
		_createView : function() {
			// 初始化基本信息
			this.element.addClass("ui-wBootgrid ui-custom-layout");
			// "生成布局"（flow）和"绘制"（paint）这两步，合称为"渲染"（render）
			this._renderView();
		},
		_renderView : function() {
			// 生成布局
			this._createLayout();
			// 生成页面组件
			this._createWidgets();
			
			var _self = this;
			
			//添加监听重置尺寸
			this.element.off("resize").on("resize",function(e){
				e.stopPropagation();
				var columns = $(this).children();
				var columnNum = 0;
				var leftSidebarIndex;
				var averageWidth;
				$.each(columns,function(index,item){
					if($(item).html() != ""){
						var itemChild = $(item).children();
						var maxWidthItem;
						$(itemChild).each(function(i,n){
							if(maxWidthItem == undefined){
								maxWidthItem = n;
							}else{
								if($(maxWidthItem).width()<$(n).width()){
									maxWidthItem = n;
								}
							}
						})
						var maxWidthItemPercent = ($(maxWidthItem).width()+30)/_self.element.width()*100;
						maxWidthItemPercent = parseFloat(maxWidthItemPercent.toFixed(1));
						if($(".nav-toggle-btn").hasClass("close-nav")){
							$(item).css("width",_self._columns["column-"+index]+"%");
						}else{
							if(maxWidthItemPercent<_self._columns["column-"+index]){
								$(item).css("width",maxWidthItemPercent+"%");
								averageWidth = (_self._columns["column-"+index] - maxWidthItemPercent)/(columns.length-1);
								averageWidth = parseFloat(averageWidth.toFixed(1));
							}else{
								$(item).css("width",maxWidthItemPercent+averageWidth+"%");
							}
						}
					}
				})
			})
		},
		_createLayout : function() {
		},
		_createWidgets : function() {
			var _self = this;
			var configuration = _self.getConfiguration();
			_self._columns = new Object();
			var columns = $(this.element).children();
			var parentWidth = $(this.element).innerWidth();
			$.each(columns,function(index,item){
				var _column = configuration.columns[index];
				var itemWidth = $(item).innerWidth()/parentWidth*100;
				itemWidth = parseFloat(itemWidth.toFixed(1));
				if(_column.colspan != 12) {
                    $(item).addClass('custom-column')
					if(_column.width) {
                        $(item).css('width', _column.width - 20 + 'px');
					} else if(_column.renderWidth) {
                        var renderWidth = _column.renderWidth.slice(4);
                        var _w = 'calc(' + renderWidth + ' - 20px)';
                        if(_column.renderWidthArg) {
                            _w = 'calc((100% - ' + (parseInt(_column.renderWidthArg.split('|')[0]) + 20) + 'px)/' + _column.renderWidthArg.split('|')[1];
                        }
                        $(item).css('width',_w);
					} else {
                        $(item).css('width','calc(' + _column.colspan / 12*100 + '% - 20px)');
					}
                }
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