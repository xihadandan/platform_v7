/**
 * 子页签布局
 */
(function ($) {
  /*
   * Tab CLASS DEFINITION ======================
   */
  var TabLayout = function ($placeholder, $anchor, $container, options) {
    this.options = $.extend({}, options);

    this.$placeholder = $placeholder;
    this.$anchor = $anchor;
    this.$container = $container;
  };

  TabLayout.prototype = {
    constructor: TabLayout
  };

  $.TabLayout = {
    initSelf: function () {
      this.setContext(); //设置上下文
    },

    setContext: function () {
      this.$context = this.$placeholder;
    },

    getContext: function () {
      return this.$context;
    },

    /** 获得标题锚点对象 */
    getAnchor: function () {
      return this.$anchor;
    },

    /** 子页签是否隐藏 */
    isHide: function () {
      return this.options.isHide === true;
    },

    /** 获得外层页签容器对象 */
    get$Container: function () {
      return this.$container;
    },

    /** 获得外层布局对象 **页签容器的布局对象** */
    get$Layout: function () {
      return this.get$Container().get$Layout();
    },

    /** 获得子页签是否隐藏 */
    isHide: function () {
      return this.options.isHide === true;
    },

    /** 隐藏子页签 */
    hide: function () {
      this.$anchor.hide();
      this.options.isHide = true;

      // 如果隐藏的子页签处于激活状态时,需要激活另一个子页签 (隐藏外的第一个子页签)
      if (this.isActive()) {
        var $container = this.get$Container();
        $container.activeFirstSubTab();
      }
    },

    /** 显示子页签 */
    show: function () {
      this.$anchor.show();
      this.options.isHide = false;
    },

    /** 取得子页签对应的title `li` */
    getTitleLi: function () {
      return this.$anchor;
    },

    /** 取得子页签中title `li` 对应的`a`标签 */
    getTitleLiA: function () {
      return this.$anchor.find('a');
    },

    /** 激活子页签 */
    active: function () {
      this.$anchor.children('a').trigger('click');
      this.options.isActive = true;
    },

    /** 获取子页签的定义 */
    getSubtabDefinition: function () {
      return this.options.definition;
    },

    /** 获取子页签是否激活 */
    isActive: function () {
      return this.$anchor.hasClass('active');
    },

    /** 获取子页签名称 */
    getSubtabName: function () {
      return this.options.name;
    },

    /** 获取子页签显示文本 */
    getSubtabDisplayName: function () {
      return this.options.displayName;
    },

    /** 是否为自定义的标签 */
    isCustom: function () {
      return this.options.definition.isCustom === true;
    },

    /** 获得子页签内容区域 */
    getSubTabContentElement: function () {
      return this.$placeholder;
    },

    $: function (selector) {
      return $(selector, this.getContext()[0]);
    }
  };

  /*
   * Tab PLUGIN DEFINITION =========================
   */
  $.fn.wTabLayout = function (option) {
    var method = false;
    var args = null;
    if (arguments.length == 2) {
      method = true;
      args = arguments[1];
    }

    if (option === 'getObject') {
      //通过getObject来获取实例
      var $this = $(this);
      data = $this.data('wTabLayout');
      if (data) {
        return data; //返回实例对象
      } else {
        throw new Error('This object is not available');
      }
    }

    return this.each(function () {
      var $this = $(this);
      var data = $this.data('wTabLayout');
      var options = typeof option == 'object' && option;
      if (!data) {
        data = new TabLayout($(this), options.$anchor, options.$container, options);
        $.extend(data, $.TabLayout);
        // data.init();
        $this.data('wTabLayout', data);
      }

      if (typeof option == 'string') {
        if (method == true && args != null) {
          return data[option](args);
        } else {
          return data[option]();
        }
      } else {
        return data;
      }
    });
  };

  $.fn.wTabLayout.Constructor = TabLayout;
})(jQuery);
