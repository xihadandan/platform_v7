(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'constant', 'appContext'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($, constant, appContext) {
  'use strict';
  $.widget('ui.wSecondPageLayout', $.ui.wWidget, {
    options: {
      // 组件定义
      widgetDefinition: {},
      // 上级容器定义
      containerDefinition: {}
    },
    _createView: function () {
      // 初始化基本信息
      this.element.addClass('ui-wBootgrid');
      // "生成布局"（flow）和"绘制"（paint）这两步，合称为"渲染"（render）
      this._renderView();
    },
    _renderView: function () {
      var _self = this;
      // 生成布局
      this._createLayout();
      // 生成页面组件
      this._createWidgets();
      this._secondPageLayoutResize();
      window.addEventListener('resize', function () {
        _self._secondPageLayoutResize();
      });
      //添加监听重置尺寸
      this.element.off('resize').on('resize', function (e) {
        e.stopPropagation();
        var $this = $(this);
        var $navToggleBtn = $this.find('.nav-toggle-btn');
        var $menuSidebar = $this.find('.menu-sidebar');
        var $content = $this.find('.content');
        if ($navToggleBtn.hasClass('close-nav')) {
          $this.removeClass('show-float-menu');
        } else {
          $this.addClass('show-float-menu');
        }
      });
    },

    _secondPageLayoutResize: function () {
      var _self = this;
      var $element = _self.element;

      //兄弟节点的高度
      var pageContainer = appContext.getPageContainer().element;
      var height = document.body.clientHeight; //浏览器高度
      var panelBody0 = $element.parents('.panel-body');
      if (panelBody0[0] && panelBody0[0].style.height) {
        height = panelBody0.height();
      }
      var scrollHeight = document.documentElement.scrollHeight; //滚动的高度

      var siblingsHeight = 0;
      var hasFooter = false;
      $element.siblings().each(function () {
        siblingsHeight += $(this).outerHeight();
        if ($(this).is('.ui-wFooter')) {
          //有底部控件的时候，底部控件会占据容器高度
          hasFooter = true;
        }
      });

      if (!hasFooter) {
        //没有底部控件，则需要扣掉容器的底部padding
        siblingsHeight += parseInt($(pageContainer).css('padding-bottom'));
      }

      //设置二级页面布局的最小可视高度
      $element.css({
        height: height - siblingsHeight,
        overflow: 'auto'
      });

      if (height == scrollHeight || height >= $(pageContainer).height()) {
        $element.css({
          height: height - siblingsHeight
        });
      }
    },
    _createLayout: function () {},
    _createWidgets: function () {
      var bootgridDefinition = this.options.widgetDefinition;
      $.each(bootgridDefinition.items, function (index, childWidgetDefinition) {
        appContext.createWidget(childWidgetDefinition, bootgridDefinition);
      });
    },
    getRenderPlaceholder: function () {
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
});
