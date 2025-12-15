/**
 * 区块布局控件
 */
(function ($) {
  var Block = function ($placeholder, options) {
    this.options = $.extend({}, $.fn['wBlock'].defaults, options);
    this.$placeholder = $placeholder;
    this.layouts = {};
    this.layoutsCount = 0;
  };

  Block.prototype = {
    constructor: Block
  };

  $.Block = {
    initSelf: function () {
      // 设置上下文
      this.setContext();

      var $tbody = this.$placeholder.find('> tbody').addClass('layout');

      // 将旧版区块的标题调整到 `thead` 中
      if (!this.$placeholder.find('> thead').length) {
        var $titleRow = this.$placeholder.find('> tbody > tr:first');
        var $thead = $('<thead></thead>').append($titleRow);
        $thead.insertBefore($tbody);
      }

      // 设置区块布局对象
      this.createBlockLayout($tbody);
    },

    /** 设置上下文 */
    setContext: function () {
      this.$context = this.$placeholder;
    },

    /** 获得上下文 */
    getContext: function () {
      return this.$context;
    },

    /** 创建子页签布局对象 */
    createBlockLayout: function ($placeholder) {
      var options = { $placeholder: $placeholder, $container: this, definition: this.options };

      $placeholder.wBlockLayout(options);
      var layoutObj = $placeholder.wBlockLayout('getObject');
      var obj = { $placeholder: $placeholder, layoutObj: layoutObj, order: ++this.layoutsCount };
      this.layouts[this.options.blockCode] = { $placeholder: $placeholder, layoutObj: layoutObj, order: ++this.layoutsCount };
      $.ContainerManager.layouts[this.options.blockCode] = obj;
      return layoutObj;
    },

    /** 判断区块是否隐藏 **目前平台支持区块容器级别的隐藏** */
    isHide: function () {
      return this.options.hide === true;
    },

    /** 获得外层布局对象 */
    get$Layout: function () {
      if (typeof this.options.$layout === undefined) {
        return this.options.$layout;
      }

      var _this = this;
      var ns = this.options.namespace || this.options.formUuid;

      function findCurrentNode() {
        for (var fieldName in window.reverseFieldTree[ns]) {
          var field = window.reverseFieldTree[ns][fieldName];
          var parent = field.parent;
          while (parent) {
            if (parent.nodeType === 'block' && parent.blockCode === _this.options.blockCode) {
              return parent;
            }
            parent = parent.parent;
          }
        }
        return null;
      }
      var currentNode = findCurrentNode();
      if (!currentNode) {
        this.options.$layout = null;
        return this.options.$layout;
      }

      var layoutNode = currentNode.parent;
      while (layoutNode && !layoutNode.isLayout) {
        layoutNode = layoutNode.parent;
      }

      if (layoutNode) {
        this.options.$layout = $.ContainerManager.getLayout(layoutNode.symbol);
      } else {
        this.options.$layout = null;
      }
      return this.options.$layout;
    },

    /** 显示区块容器 */
    show: function () {
      this.options.hide = false;
      this.$placeholder.show();
    },

    /** 隐藏区块容器 */
    hide: function () {
      this.options.hide = true;
      this.$placeholder.hide();
    },

    /** 隐藏区块名称 */
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
  $.fn.wBlock = function (option) {
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
        data = $this.data('wBlock');
        if (data) {
          return data; //返回实例对象
        } else {
          throw new Error('This object is not available');
        }
      }
    }

    return this.each(function () {
      var $this = $(this);
      var data = $this.data('wBlock');
      var options = typeof option == 'object' && option;

      if (!data) {
        data = new Block($(this), options);
        $.extend(data, $.wLayoutInterface);
        $.extend(data, $.Block);
        data.init();
        $this.data('wBlock', data);
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

  $.fn.wBlock.Constructor = Block;

  $.fn.wBlock.defaults = {
    name: '', //页签组编码
    displayName: '' //
  };
})(jQuery);
