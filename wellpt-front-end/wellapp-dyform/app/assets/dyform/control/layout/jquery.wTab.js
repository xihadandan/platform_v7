/**
 * 页签布局控件
 */
(function ($) {
  /*
   * Tab CLASS DEFINITION ======================
   */
  var Tab = function ($placeholder, options) {
    this.options = $.extend({}, $.fn['wTab'].defaults, options);
    var $newPlaceHolder = $('[layoutName="' + this.options.name + '"]');
    this.$placeholder = $placeholder.is($newPlaceHolder) ? $placeholder : $newPlaceHolder;
    this.$placeholder.removeClass('layout').addClass('container');
    this.layouts = {};
    this.layoutsCount = 0;
  };

  Tab.prototype = {
    constructor: Tab
  };

  $.Tab = {
    initSelf: function () {
      var _this = this;

      //设置上下文
      this.setContext();

      // 配置属性
      var tabStyle = this.options.tabStyle;
      var tabBorder = this.options.tabBorder;
      var tabWidth = this.options.tabWidth;
      var contentWidth = this.options.contentWidth;
      var newTabWidth = tabWidth != '' ? 'width:' + tabWidth + ';' : '';
      var newContentWidth = contentWidth != '' ? 'width:' + contentWidth + ';' : '';

      var $subtabElems = this.$placeholder.children('.subtab-design');

      this.$placeholder.removeAttr('title');

      if (tabStyle == 'left') {
        // 页签风格 - 左侧风格
        var $tabs = $('<div>', { class: 'row row-tabs row-tabs-left' });
        // Nav tabs
        var $navTabs = $('<div>', { class: 'col-xs-3', style: newTabWidth });
        var $navUlTabs = $('<ul>', { class: 'nav nav-tabs tabs-left js-nav-tabs', role: 'tablist' });
        $navTabs.append($navUlTabs);
        $subtabElems.each(function () {
          var $subtab = $(this);
          $subtab.addClass('tab-pane').removeAttr('title');
          var subtblName = $subtab.attr('name');
          var subtblDef = _this.getSubTblDef(subtblName);
          var $li = $('<li>').append($('<a>', { href: '#' + subtblName, 'data-toggle': 'tab' }).text(subtblDef.displayName));
          $li.attr('subtabName', subtblName);
          $navUlTabs.append($li);

          $subtab.attr('id', subtblName);
        });
        $tabs.append($navTabs);

        // Tab panes
        var $tabPanes = $('<div>', { class: 'col-xs-9', style: newContentWidth });
        var $tabContent = $('<div>', { class: 'tab-content js-tab-content' });
        var innerPlaceHolderHtml = this.$placeholder.html(); //内部占位符
        $tabContent.append(innerPlaceHolderHtml);
        $tabPanes.append($tabContent);
        $tabs.append($tabPanes);

        this.$placeholder.empty();
        this.$placeholder.find('.tab-content').css('height', this.options.height);
        this.$placeholder.append($tabs[0].outerHTML);
        this.$placeholder.removeClass('tab-design').addClass('tab');
        this.$placeholder.find('.subtab-design').each(function () {
          var $this = $(this);
          if ($this.parent().hasClass('tab-design')) {
            return;
          }
          $this.removeClass('subtab-design').addClass('subtab');
        });

        setTimeout(function () {
          $('.tabs-left').height($('.row-tabs-left').height() + 'px');
        }, 1000);
      } else if (tabStyle == 'right') {
        // 页签风格 - 右侧风格
        var $tabs = $('<div>', { class: 'row row-tabs row-tabs-right' });

        // Nav tabs
        var $navTabs = $('<div>', { class: 'col-xs-3', style: newTabWidth });
        var $navUlTabs = $('<ul>', { class: 'nav nav-tabs tabs-right js-nav-tabs', role: 'tablist' });
        $navTabs.append($navUlTabs);
        $subtabElems.each(function () {
          var $subtab = $(this);

          $subtab.addClass('tab-pane').removeAttr('title');
          var subtblName = $subtab.attr('name');
          var subtblDef = _this.getSubTblDef(subtblName);
          var $li = $('<li>').append($('<a>', { href: '#' + subtblName, 'data-toggle': 'tab' }).text(subtblDef.displayName));
          $li.attr('subtabName', subtblName);
          $navUlTabs.append($li);
          $subtab.attr('id', subtblName);
        });

        // Tab panes
        var $tabPanes = $('<div>', { class: 'col-xs-9', style: newContentWidth });
        var $tabContent = $('<div>', { class: 'tab-content js-tab-content' });
        var innerPlaceHolderHtml = this.$placeholder.html(); //内部占位符
        $tabContent.append(innerPlaceHolderHtml);
        $tabPanes.append($tabContent);
        $tabs.append($tabPanes);
        $tabs.append($navTabs);
        this.$placeholder.empty();
        this.$placeholder.append($tabs[0].outerHTML);
        this.$placeholder.find('.tab-content').css('height', this.options.height);
        this.$placeholder.removeClass('tab-design').addClass('tab');
        this.$placeholder.find('.subtab-design').each(function () {
          var $this = $(this);
          if ($this.parent().hasClass('tab-design')) {
            return;
          }
          $this.removeClass('subtab-design').addClass('subtab');
        });
      } else {
        // 页签风格 - 顶部风格
        var tabBorder = tabBorder == 'noborder' ? 'tab-noborder' : 'tab-border';
        var $navUlTabs = $('<ul>', { class: 'nav nav-tabs', role: 'tablist' });
        $subtabElems.each(function () {
          var $subtab = $(this);

          $subtab.addClass('tab-pane').removeClass('subtab-design').addClass('subtab').removeAttr('title');
          var subtblName = $subtab.attr('name');
          var subtblDef = _this.getSubTblDef(subtblName);
          var $li = $('<li class="' + tabBorder + '">').append(
            $('<a>', { href: '#' + subtblName, 'data-toggle': 'tab' }).text(subtblDef.displayName)
          );
          $li.attr('subtabName', subtblName);
          $navUlTabs.append($li);
          $subtab.attr('id', subtblName);
        });
        this.$placeholder.removeClass('tab-design').addClass('tab');
        var innerPlaceHolderHtml = this.$placeholder.html(); //内部占位符
        this.$placeholder.empty();
        var $tabContent = $('<div>', { class: 'tab-content' });
        $tabContent.append(innerPlaceHolderHtml);
        $tabContent.css('height', this.options.height);
        this.$placeholder.append($navUlTabs[0].outerHTML);
        this.$placeholder.append($tabContent);
        this.$placeholder.find('.tab-content').css('height', this.options.height);
      }

      // 生成子页签对象
      $subtabElems.each(function () {
        var $subtab = $(this);
        var subtblName = $subtab.attr('name');
        var subtblDef = _this.getSubTblDef(subtblName);

        var $li = _this.$('.nav li[subtabName=' + subtblName + ']');
        _this.createSubtabLayout(subtblName, $subtab, $li, subtblDef);
      });

      // 激活子页签
      for (var name in _this.options.subtabs) {
        var subtblDef = _this.options.subtabs[name];
        if (subtblDef.isActive) {
          this.active(name);
        }
      }

      // 子页签头点击事件
      this.$placeholder.find('.nav-tabs > li > a').click(function () {
        // 激活子页签
        var subtblName = $(this).attr('href').substring(1);
        _this.getSubTblDef(subtblName).isActive == true;

        // 重置页签布局的滚动条
        if ($('#wf_form').length > 0 && $('#wf_form').parent('.widget-main').length > 0) {
          _this._resetLayoutScrollBar();
        }
      });
    },

    /** 创建子页签布局对象 */
    createSubtabLayout: function (subtabName, $placeholder, $anchor, subTabDefinition) {
      var otherOptions = { $placeholder: $placeholder, $anchor: $anchor, $container: this, definition: subTabDefinition };
      var options = {};
      $.extend(true, options, subTabDefinition, otherOptions);

      $placeholder.wTabLayout(options);
      var layoutObj = $placeholder.wTabLayout('getObject');
      var obj = { $placeholder: $placeholder, $anchor: $anchor, layoutObj: layoutObj, order: ++this.layoutsCount };
      this.layouts[subtabName] = obj;
      $.ContainerManager.layouts[subtabName] = obj;
      return layoutObj;
    },

    /** 设置上下文 */
    setContext: function () {
      this.$context = this.$placeholder;
    },

    /** 获得上下文 */
    getContext: function () {
      return this.$context;
    },

    /** 判断子页签是否隐藏, **平台目前只支持子页签布局级别的隐藏** */
    isHide: function (subtabName) {
      if (typeof subtabName === 'string') {
        var $subtab = this.getSubTabLayout(subtabName).layoutObj;
        return $subtab.isHide();
      }

      return false;
    },

    /** 获得外层布局元素 */
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
            if (parent.nodeType === 'tab' && parent.tabName === _this.options.name) {
              return parent;
            }
            parent = parent.parent;
          }
          return null;
        }
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
        this.options.$layout = $.ContainerManager.getContainer(layoutNode.symbol);
      } else {
        this.options.$layout = null;
      }

      return this.options.$layout;
    },

    /** 获得子页签布局对象 */
    getSubTabLayout: function (subtabName) {
      return this.layouts[subtabName];
    },

    /** 隐藏子页签 */
    hide: function (subtabName) {
      var $subtab = this.getSubTabLayout(subtabName).layoutObj;
      $subtab.hide();
    },

    /** 取得第一个不隐藏的子页签 */
    getFirstSubTblName: function () {
      return this.$('.nav-tabs > li > a').not(':hidden').attr('href').substring(1);
    },

    /** 激活第一个子页签 */
    activeFirstSubTab: function () {
      var firstSubTabName = this.getFirstSubTblName();
      var $subtab = this.getSubTabLayout(firstSubTabName).layoutObj;
      $subtab.active();
    },

    /** 显示子页签 */
    show: function (subtabName) {
      var $subtab = this.getSubTabLayout(subtabName).layoutObj;
      $subtab.show();
    },

    /** 取得子页签对应的title `li` */
    getTitleLi: function (subtabName) {
      var $subtab = this.getSubTabLayout(subtabName).layoutObj;
      return $subtab.getTitleLi();
    },

    /** 取得子页签中 title `li` 对应的 `a` 标签 */
    getTitleLiA: function (subtabName) {
      var $subtab = this.getSubTabLayout(subtabName).layoutObj;
      return $subtab.getTitleLiA();
    },

    /** 激活子页签 */
    active: function (subtabName) {
      var $subtab = this.getSubTabLayout(subtabName).layoutObj;
      $subtab.active();

      if ($('#wf_form').length > 0 && $('#wf_form').parent('.widget-main').length > 0) {
        this._resetLayoutScrollBar();
      }
    },

    /** 重置滚动条 */
    _resetLayoutScrollBar: function () {
      setTimeout(function () {
        $('#wf_form').parent('.widget-main').getNiceScroll().resize();
      }, 300);
    },

    /** 获取子页签的定义 */
    getSubTblDef: function (subtabName) {
      return this.options.subtabs[subtabName];
    },

    /** 子页签是否激活 */
    isActive: function (subtabName) {
      var $subtab = this.getSubTabLayout(subtabName).layoutObj;
      return $subtab.isActive();
    },

    /** 激活页签名称 */
    getName: function () {
      return this.options.name;
    },

    /** 激活控件所在的子页签 */
    activeSubTab: function (control) {
      var subtblDefs = this.options.subtabs;
      for (var i in subtblDefs) {
        var $placeHolder = control.$placeHolder || control.$placeholder;
        if ($placeHolder && $placeHolder.parents('#' + i).size() > 0) {
          this.active(i);
          break;
        }
      }
    },

    /** 是否为自定义的标签 */
    isCustom: function (subtabName) {
      var $subtab = this.getSubTabLayout(subtabName).layoutObj;
      return $subtab.isCustom();
    },

    /** 获得子页签内容区域 */
    getSubTabContentElement: function (subtabName) {
      var $subtab = this.getSubTabLayout(subtabName).layoutObj;
      return $subtab.getSubTabContentElement();
    },

    /** 删除自定义子页签 */
    removeCustomSubTab: function (subtabName) {
      if (!this.isCustom(subtabName)) {
        throw new Error('非自定义子页签，不可删除');
      }

      var isActive = this.isActive(subtabName);

      // 删除子页签标题
      this.removeTitleLi(subtabName);
      // 删除子页签定义
      this.removeSubTabDef(subtabName);

      if (isActive) {
        // 激活第一个页签
        this.activeFirstSubTab();
      }

      // 删除子页签内容区域
      this.removeSubTabContent(subtabName);

      // 删除子页签布局对象
      delete this.layouts[subtabName];
    },

    /** 删除子页签内容区域 */
    removeSubTabContent: function (subtabName) {
      this.getSubTabContentElement(subtabName).remove();
    },

    /** 删除子页签定义 */
    removeSubTabDef: function (subtabName) {
      delete this.options.subtabs[subtabName];
    },

    /** 删除子页签标题元素 */
    removeTitleLi: function (subtabName) {
      this.getTitleLi(subtabName).remove();
    },

    /** 创建自定义子页签 */
    createCustomSubTab: function (subtblCode, displayName, html) {
      var definition = this.addSubTabDef(subtblCode, displayName, true);
      var $anchor = this.addTitleLi(subtblCode, displayName);
      var $placeholder = this.addSubTabContent(subtblCode, displayName, html);

      this.createSubtabLayout(subtblCode, $placeholder, $anchor, definition);
    },

    /**
     * 添加子页签定义
     * @private
     */
    addSubTabDef: function (name, displayName, isCustom) {
      var definition = { name: name, displayName: displayName, isActive: false, isCustom: isCustom };
      this.options.subtabs[name] = definition;
      return definition;
    },

    /**
     * 添加子页签标题区域
     * @private
     */
    addTitleLi: function (name, displayName) {
      var className = this.options.tabBorder == 'noborder' ? 'tab-noborder' : 'tab-border';
      var $anchor = $('<li class="' + className + '"><a href="#' + name + '" data-toggle="tab">' + displayName + ' </a></li>').appendTo(
        this.$('.nav-tabs')
      );
      return $anchor;
    },

    /**
     * 添加子页签内容区域
     * @private
     */
    addSubTabContent: function (name, displayName, html) {
      var $content = $('<div class="tab-pane subtab layout" name="' + name + '" id="' + name + '">' + html + '</div>');
      $content.appendTo(this.$('.tab-content'));
      return $content;
    },

    /** 设置自定义子页签 */
    setCustomSubTabContent: function (name, html) {
      if (!this.isCustom(name)) {
        throw new Error('非自定义子页签，不可设置内容');
      }
      this.getSubTabContentElement(name).html(html);
    },

    $: function (selector) {
      return $(selector, this.getContext()[0]);
    }
  };

  /*
   * Tab PLUGIN DEFINITION =========================
   */
  $.fn.wTab = function (option) {
    var method = false;
    var args = null;
    if (arguments.length == 2) {
      method = true;
      args = arguments[1];
    }

    if (option === 'getObject') {
      //通过getObject来获取实例
      var $this = $(this);
      data = $this.data('wTab');

      if (data) {
        return data; //返回实例对象
      } else {
        throw new Error('This object is not available');
      }
    }

    return this.each(function () {
      var $this = $(this);
      var data = $this.data('wTab');
      var options = typeof option == 'object' && option;

      if (!data) {
        data = new Tab($(this), options);
        $.extend(data, $.wLayoutInterface);
        $.extend(data, $.Tab);
        data.init();
        $this.data('wTab', data);
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

  $.fn.wTab.Constructor = Tab;

  $.fn.wTab.defaults = {
    name: '',
    displayName: '',
    subTabs: {}
  };
})(jQuery);
