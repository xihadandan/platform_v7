/**
 * 区块布局控件
 */
(function ($) {
  var BlockLayout = function ($placeholder, options) {
    this.options = $.extend({}, options);
    this.$placeholder = $placeholder;
    this.$container = options.$container;

    this.layouts = {};
    this.layoutsCount = 0;
  };

  BlockLayout.prototype = {
    constructor: BlockLayout
  };

  $.BlockLayout = {
    initSelf: function () {
      // 设置上下文
      this.setContext();
    },

    /** 设置上下文 */
    setContext: function () {
      this.$context = this.$placeholder;
    },

    /** 获得上下文 */
    getContext: function () {
      return this.$context;
    },

    /** 判断区块是否隐藏 **目前平台支持区块容器级别的隐藏** */
    isHide: function () {
      return this.get$Container().isHide();
    },

    /** 获得外层布局对象 **页签容器的布局对象** */
    get$Layout: function () {
      return this.get$Container().get$Layout();
    },

    /** 获得外层页签容器对象 */
    get$Container: function () {
      return this.$container;
    },

    /** 获得区块名称 */
    getName: function () {
      return this.options.name;
    },

    $: function (selector) {
      return $(selector, this.getContext()[0]);
    }
  };

  /*
   * Block PLUGIN DEFINITION =========================
   */
  $.fn.wBlockLayout = function (option) {
    var method = false;
    var args = null;
    if (arguments.length == 2) {
      method = true;
      args = arguments[1];
    }

    if (typeof option == 'string') {
      if (option === 'getObject') {
        //通过getObject来获取实例
        var $this = $(this);
        data = $this.data('wBlockLayout');
        if (data) {
          return data; //返回实例对象
        } else {
          throw new Error('This object is not available');
        }
      }
    }

    return this.each(function () {
      var $this = $(this);
      var data = $this.data('wBlockLayout');
      var options = typeof option == 'object' && option;

      if (!data) {
        data = new BlockLayout($(this), options);

        $.extend(data, $.BlockLayout);
        $this.data('wBlockLayout', data);
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

  $.fn.wBlockLayout.Constructor = BlockLayout;
})(jQuery);
