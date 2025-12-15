(function(factory) {
    "use strict";
    if (typeof define === 'function' && define.amd) {
        // AMD. Register as an anonymous module.
        define([ 'jquery', 'appContext','perfectScrollbar'], factory);
    } else {
        // Browser globals
        factory(jQuery);
    }
}(function($, appContext,PerfectScrollbar) {
    "use strict";
    $.widget("ui.wLayoutCustomPage", $.ui.wWidget, {
        options : {
            // 组件定义
            widgetDefinition : {},
            // 上级容器定义
            containerDefinition : {}
        },
        _createView : function() {
            // 初始化基本信息
            this.element.addClass("ui-wBootgrid");
            // "生成布局"（flow）和"绘制"（paint）这两步，合称为"渲染"（render）
            this._renderView();
            this._initEvent();
        },
        _renderView : function() {
            var _self = this;
            // 生成布局
            _self._createLayout();
            // 生成页面组件
            _self._createWidgets();

            //添加监听重置尺寸
            _self.element.off("resize").on("resize",function(e){
                e.stopPropagation();
                var $this = $(this);
                var $navToggleBtn = $this.find('.nav-toggle-btn');
                if($navToggleBtn.hasClass('close-nav')) {
                    $this.removeClass('show-float-menu');
                } else {
                    $this.addClass('show-float-menu');
                }
            })
        },
        _createLayout : function() {
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
        },
        _initEvent: function () {
            var _self = this;
            var $ps = _self.element.find('.well-left-nav')[0];
            var ps = new PerfectScrollbar($ps);
            _self.ps = window.ps = ps;

            _self.element.find('.well-left-nav').on('niceScrollResize',function () {
                _self.niceScrollResize(ps);
            })
            // _self.element.find('.well-left-nav').niceScroll();
        },
        niceScrollResize: function (PS) {
            var _self = this;
            _self.ps.update();
            _self.element.find('.well-left-nav').scrollTop(0)
        }
    });
}));