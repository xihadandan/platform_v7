(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'commons', 'server', 'constant', 'appContext', 'appDispatcher', 'dataStoreBase'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($, commons, server, constant, appContext, appDispatcher, DataStore) {
  'use strict';
  $.widget('ui.wNavTab', $.ui.wWidget, {
    options: {
      // 组件定义
      widgetDefinition: {},
      // 上级容器定义
      containerDefinition: {}
    },
    _createView: function () {
      var _self = this;
      var options = $.extend({}, _self.options.widgetDefinition, _self.options.widgetDefinition.configuration);
      _self.$element = $(_self.element);

      _self._beforeRenderView();
      // 渲染组件内容
      _self._renderView();
      // 绑定组件事件
      _self._bindEvents();

      // 展示默认页面
      if (options.defaultPage) {
        _self.startApp({
          renderNavTab: true,
          target: '_targetWidget',
          targetWidgetId: options.id,
          text: options.defaultPageName || '主页',
          appPath: options.eventHanlderPath,
          appType: '4',
          menuId: 'mainPage'
        });
      }
    },
    _beforeRenderView: function () {
      var _self = this;
      _self.invokeDevelopmentMethod('beforeRender', [_self.options, _self.getConfiguration()]);
    },
    _renderView: function () {
      var _self = this;
      // 渲染组件内容
      var $navTab = $('<div>', {
        class: 'tag-tab-section'
      });
      $navTab.html(
        '<div class="tag-tab-wrapper"><div class="tags-nav">' +
          // '    <div class="close-icon-wrap">' +
          // '       <div class="close-icon">' +
          // '           <i class="iconfont icon-ptkj-dacha"></i>' +
          // '       </div>' +
          // '    </div>' +
          '    <ul class="tags-nav-menu">' +
          '        <li class="close-other">关闭其他</li>' +
          '        <li class="close-all">关闭所有</li>' +
          '        <li class="nav-fresh">刷新</li>' +
          '    </ul>' +
          '    <div class="left-btn">' +
          '       <i class="iconfont icon-ptkj-xianmiaojiantou-zuo"></i>' +
          '    </div>' +
          '    <div class="right-btn">' +
          '       <i class="iconfont icon-ptkj-xianmiaojiantou-you"></i>' +
          '    </div>' +
          '    <div class="scroll-outer">' +
          '        <div class="scroll-body">' +
          '            <span class="scroll-span"></span>' +
          '        </div>' +
          '    </div>' +
          '</div></div><div class="tag-tab-content-wrapper"></div>'
      );
      _self.$element.append($navTab);
    },
    // 锚点选择回调处理
    onHashSelection: function (options) {
      var _self = this;
      // 调用父类方法
      _self._superApply(arguments);
      var selection = options.selection || [];
      var configuration = _self.getConfiguration();
      $.each(selection, function (i, tabId) {
        _self._activeTab(tabId);
      });
    },
    _bindEvents: function () {
      // 绑定组件事件
      var _self = this;
      var $tagsNav = _self.$element.find('.tags-nav');
      var $tagsNavMenu = $tagsNav.find('.tags-nav-menu');
      var $closeIcon = $tagsNav.find('.close-icon');
      var $leftBtn = $tagsNav.find('.left-btn');
      var $rightBtn = $tagsNav.find('.right-btn');
      var $scrollOuter = $tagsNav.find('.scroll-outer');
      var $scrollBody = $scrollOuter.find('.scroll-body');
      var $closeOther = $tagsNavMenu.find('.close-other');
      var $closeAll = $tagsNavMenu.find('.close-all');
      var $navFresh = $tagsNavMenu.find('.nav-fresh');
      var $document = $(document);
      var scrollOuterWidth = $scrollOuter.width();

      //当前多标签导航关闭
      $scrollBody.on('click', '.nav-tag .nav-tag-close', function (e) {
        e.stopPropagation();
        var $parent = $(this).parent();
        var _id = $parent.data('id');
        var _data = $parent.data();
        var w = $parent.width();
        appContext.removeWidgetById(_data.options.widgetDefId, true);
        $parent.remove();
        if ((_data.options.ui || _data.options.initiator) && (_data.options.menuId || _data.options.selection)) {
          var initiatorUi = _data.options.ui;
          if (!initiatorUi) {
            var initiatorUi = appContext.getWidgetById(_data.options.initiator.id);
          }
          if (initiatorUi) {
            appContext.removeFromCurrentHash({
              ui: initiatorUi,
              selection: _data.options.menuId || _data.options.selection
            });
          }
        }
        $('#' + _id).remove();
        if ($parent.hasClass('active')) {
          if (_data.options.pNavTabId) {
            var isRefreshParent = $parent.attr('isRefreshParent');
            _self._activeTab(_data.options.pNavTabId, null, isRefreshParent);
          } else {
            $scrollBody.find('.nav-tag').eq(0).trigger('click');
          }
        }

        var _scrollBodyLeft = parseInt($scrollBody.css('left'));
        $scrollBody.css('left', _scrollBodyLeft + w);
        _self.countScrollBodyStatus();
      });

      // 点击非导航菜单区域，则关闭导航菜单
      $document.on('click', function (e) {
        $tagsNavMenu.slideUp();
      });

      //多标签导航事件
      $scrollBody
        .on('click', '.nav-tag', function () {
          var $this = $(this);
          var _id = $this.data('id');
          // 切换到主页标签，设为主页锚点
          if (appContext.pageContainerCreationComplete == true) {
            if (_id == 'nav_tab_mainPage') {
              var appHash = appContext.parseCurrentHash();
              appHash.init('#/' + _self.getId() + '/mainPage');
            } else {
              var _data = $this.data();
              if (_data && (_data.options.ui || _data.options.initiator) && (_data.options.menuId || _data.options.selection)) {
                var initiatorUi = _data.options.ui;
                if (!initiatorUi) {
                  var initiatorUi = appContext.getWidgetById(_data.options.initiator.id);
                }
                if (initiatorUi) {
                  appContext.updateCurrentHash({
                    ui: initiatorUi,
                    selection: _data.options.menuId || _data.options.selection
                  });
                }
              }
            }
          }
          $this.addClass('active').siblings().removeClass('active');
          $('#' + _id)
            .show()
            .siblings()
            .hide();
          $tagsNavMenu.slideUp();
          _self.countScrollBodyStatus();
          //判断tab位置
          if ($this.closest('.tags-nav').hasClass('show-btn')) {
            var _left = $this.offset().left;
            var _width = $this.width();
            var _scrollBodyLeft = parseInt($scrollBody.css('left'));
            var leftBtnLeft = $leftBtn.offset().left;
            var rightBtnLeft = $rightBtn.offset().left;
            if (_left < leftBtnLeft) {
              $scrollBody.css('left', _scrollBodyLeft - _left + leftBtnLeft + 60);
            } else if (_left + _width > rightBtnLeft) {
              $scrollBody.css('left', _scrollBodyLeft - _left - _width + rightBtnLeft - 60);
            }
          }
          //判断左侧导航是否选中状态
          $('.ui-wLeftSidebar .nav-menu-item').each(function () {
            var _$this = $(this);
            if (_id.indexOf(_$this.attr('menuid')) > -1) {
              if (!_$this.hasClass('nav-menu-active')) {
                _$this.trigger('click', ['navTab']);
              }
            }
          });
        })
        .on('mousedown', '.nav-tag', function (e) {
          if (e.button === 2) {
            //右键点击
            var $this = $(this);
            var _left = $this.offset().left;
            var tagsNavLeft = $tagsNav.offset().left;
            $tagsNavMenu.data('id', $this.data('id'));
            $tagsNavMenu.data('options', $this.data('options'));
            $tagsNavMenu
              .css({ left: _left - tagsNavLeft, right: 'auto' })
              .slideDown()
              .find('.nav-fresh')
              .show();

            // 注册iframe点击事件
            var iframs = $('iframe');
            function registerIframeClickEvent(i) {
              try {
                iframs[i].contentWindow.document.body.addEventListener('click', function (e) {
                  iframs[i].contentWindow.parent.document.body.click();
                });
              } catch (error) {}
            }
            for (var i = 0; i < iframs.length; i++) {
              registerIframeClickEvent(i);
            }
          }
        })
        .on('contextmenu', '.nav-tag', function (e) {
          //阻止鼠标右键默认事件
          e.preventDefault();
        });

      //右侧关闭按钮
      $closeIcon
        .mouseenter(function () {
          var $this = $(this);
          $tagsNavMenu.stop().css({ left: 'auto', right: 0 }).slideDown().find('.nav-fresh').hide();
        })
        .mouseleave(function () {
          $tagsNavMenu
            .mouseenter(function () {
              $tagsNavMenu.stop().slideDown();
            })
            .mouseleave(function () {
              $tagsNavMenu.stop().slideUp();
            });
        });

      //左移
      $leftBtn.on('click', function () {
        var _scrollBodyLeft = parseInt($scrollBody.css('left'));
        if (_scrollBodyLeft + 120 < 60) {
          $scrollBody.css('left', _scrollBodyLeft + 120);
        } else {
          $scrollBody.css('left', 0);
        }
      });
      //右移
      $rightBtn.on('click', function () {
        var scrollBodyWidth = $scrollBody.width();
        var _scrollBodyLeft = parseInt($scrollBody.css('left'));
        if (scrollBodyWidth + _scrollBodyLeft > scrollOuterWidth) {
          $scrollBody.css('left', _scrollBodyLeft - 120);
        }
      });

      //关闭其他
      $closeOther.on('click', function () {
        var _id = $tagsNavMenu.data('id');
        $scrollBody.find('.nav-tag').each(function () {
          var $this = $(this);
          if (_id) {
            if ($this.data('id') !== _id) {
              $this.find('.nav-tag-close').trigger('click');
              if (!$this.hasClass('main-page-nav-tag')) {
                $this.remove();
              }
            } else {
              $this.trigger('click');
            }
          } else {
            if (!$this.hasClass('active')) {
              $this.find('.nav-tag-close').trigger('click');
              if (!$this.hasClass('main-page-nav-tag')) {
                $this.remove();
              }
            }
          }
        });
        $tagsNavMenu.hide();
        $scrollBody.css('left', 0);
      });

      //关闭全部
      $closeAll.on('click', function () {
        $scrollBody.find('.nav-tag').each(function () {
          var $this = $(this);
          $this.find('.nav-tag-close').trigger('click');
          if (!$this.hasClass('main-page-nav-tag')) {
            $this.remove();
          } else {
            $this.trigger('click');
          }
        });
        $tagsNavMenu.hide();
        $scrollBody.css('left', 0);
      });

      //刷新tab
      $navFresh.on('click', function () {
        var _id = $tagsNavMenu.data('id');
        $scrollBody.find('.nav-tag').each(function () {
          var $this = $(this);
          if ($this.data('id') === _id) {
            _self.refreshTab($this);
          }
        });
        $tagsNavMenu.hide();
      });
    },

    refreshTab: function ($this) {
      var _self = this;
      var _id = $this.data('id');
      var options = $this.data('options');
      var $tabContent = _self.$element.find('#' + _id).children()[0];
      if ($tabContent.nodeName === 'IFRAME' || $tabContent.id.indexOf('iframe_') > -1) {
        $tabContent.contentWindow.location.reload(true);
      } else {
        var widgetDefId = options.widgetDefId;
        // var renderWidget = appContext.getWidgetById(widgetDefId);
        // if (renderWidget != null) {
        //     renderWidget.refresh();
        //     return false;
        // }
        appContext.removeWidgetById(widgetDefId, true);
        var options = _self.$element.find('#' + _id).data('options');
        options.refresh = true;
        appContext.renderWidget(options);
      }
    },

    /**
     *
     * @param id tabId
     * @param name tabName
     * @param isRefreshTab 是否刷新tab
     * @private
     */
    _activeTab: function (id, name, isRefreshTab) {
      if (isRefreshTab === 'false' || isRefreshTab === false) {
        isRefreshTab = false;
      } else {
        isRefreshTab = true; // 默认刷新父级tab
      }

      var _self = this;
      var $tag = _self.$element.find('.scroll-body .nav-tag');
      $tag.each(function () {
        var $this = $(this);
        var _data = $this.data();
        if (_data.id.indexOf(id) > -1) {
          if (name) {
            _data.options.text = name;
            $this.data('options', _data.options).find('.nav-tag-text').text(name);
          }
          if (isRefreshTab) {
            _self.refreshTab($this);
          }
          $this.trigger('click');
        }
      });
    },

    closeTab: function (val) {
      var _self = this;
      var $tag = _self.$element.find('.scroll-body .nav-tag');
      var activeNavTab = _self.$element.find('.scroll-body .nav-tag.active');
      if (!val) {
        var _data = activeNavTab.data();
        activeNavTab.find('.nav-tag-close').trigger('click');
        if (_data.options.pNavTabId) {
          _self._activeTab(_data.options.pNavTabId);
        }
        return;
      }
      $tag.each(function () {
        var $this = $(this);
        var _data = $this.data();
        if (_data.id.indexOf(val) > -1 || _data.options.text.indexOf(val) > -1) {
          $this.find('.nav-tag-close').trigger('click');
          if (_data.options.pNavTabId) {
            _self._activeTab(_data.options.pNavTabId);
          }
          return false;
        }
      });
    },

    setCloseActiveNavNotRefreshParent: function () {
      var _self = this;
      var activeNavTab = _self.$element.find('.scroll-body .nav-tag.active');
      activeNavTab.attr('isRefreshParent', 'false');
    },

    closeTabAndRefreshTrData: function (rowData) {
      var _self = this;
      // var $tag = _self.$element.find('.scroll-body .nav-tag');
      var activeNavTab = _self.$element.find('.scroll-body .nav-tag.active');
      // if (!val) {
      var _data = activeNavTab.data();
      _self.setCloseActiveNavNotRefreshParent();
      activeNavTab.find('.nav-tag-close').trigger('click');
      if (_data.options.pNavTabId) {
        // refreshTrFuc.apply(_self,[_data.options.pNavTabId]);

        var $table = _self.$element.find('#' + _data.options.pNavTabId + ' .bootstrap-table .table-hover');
        var dataList = $table.bootstrapTable('getData', { useCurrentPage: false, includeHiddenRows: false });
        var rowIndex = -1;
        for (var i = 0; i < dataList.length; i++) {
          var dataListElement = dataList[i];
          if (dataListElement.uuid === rowData.uuid) {
            rowIndex = i;
          }
        }
        if (rowIndex !== -1) {
          $table.bootstrapTable('updateRow', {
            index: rowIndex,
            replace: true,
            row: rowData
          });
        }
        _self._activeTab(_data.options.pNavTabId, '', false);
      }
    },

    refreshParentTabAndCloseTab: function (val) {
      this.refreshParentTab(val, true);
    },
    refreshParentTab: function (val, close) {
      var _self = this;
      var $tag = _self.$element.find('.scroll-body .nav-tag');
      var activeNavTab = _self.$element.find('.scroll-body .nav-tag.active');
      if (!val) {
        var _data = activeNavTab.data();
        if (close) {
          _self.closeTab();
        }
        if (_data.options.pNavTabId) {
          _self._activeTab(_data.options.pNavTabId, null);
        }
        return;
      }
      $tag.each(function () {
        var $this = $(this);
        var _data = $this.data();
        if (_data.id.indexOf(val) > -1 || _data.options.text(val) > -1) {
          if (close) {
            _self.closeTab();
          }
          if (_data.options.pNavTabId) {
            _self._activeTab(_data.options.pNavTabId, null);
          }
          return false;
        }
      });
    },

    //获取当前tabId
    getActiveTabId: function () {
      var _self = this;
      var $activeTab = _self.$element.find('.tags-nav .nav-tag.active');
      return $activeTab.length ? $activeTab.data().id : '';
    },

    /*
     * 创建新tab
     * tabId: 导航tab的唯一ID
     * tabName: 导航tab显示的名称
     * tabType: 导航tab内容显示的类型 iframe || html
     * tabContent: 类型为iframe时传 url 为html时 传html内容
     * */
    createTab: function (tabId, tabName, tabType, tabContent, params) {
      var _self = this;
      _self._addTab({
        options: {
          buttonEvent: true,
          buttonEventId: tabId,
          text: tabName,
          pNavTabId: _self.getActiveTabId(),
          params: params || {}
        },
        type: tabType,
        warpperUrl: tabContent,
        html: tabContent
      });
    },

    countScrollBodyStatus: function () {
      var _self = this;
      var $element = _self.$element;
      var scrollBodyW = $element.find('.scroll-body').width();
      var scrollOutW = $element.find('.scroll-outer').width();
      if (scrollBodyW > scrollOutW) {
        $element.find('.tags-nav').addClass('show-btn');
      } else {
        $element.find('.tags-nav').removeClass('show-btn');
        $element.find('.scroll-body').css('left', 0);
      }
    },

    _addTab: function (params) {
      var _self = this;
      var options = params.options;
      var nav_tab_id = options.buttonEvent ? 'nav_tab_' + options.buttonEventId : 'nav_tab_' + options.menuId;
      $('#' + nav_tab_id).data('options', options);
      if (params.type === 'iframe' && !params.single) {
        //single： true只会打开一个标签
        if (options.buttonEventId && options.params && options.params.tableBtnEditTag) {
          nav_tab_id += '_' + options.params.tableBtnEditTag;
        } else {
          nav_tab_id += '_' + new Date().getTime();
        }
      }
      if (options.refresh) {
        _self.renderTabData(params, nav_tab_id);
        return;
      }
      if ($('#' + nav_tab_id).length) {
        _self._activeTab(nav_tab_id, options.text);
        _self.renderTabData(params, nav_tab_id);
        return;
      }
      _self.renderTabData(params, nav_tab_id, 'add');
    },
    renderTabData: function (params, nav_tab_id, type) {
      var _self = this;
      var options = params.options;
      if ($('#' + nav_tab_id).length) {
        $('#' + nav_tab_id).remove();
      }
      if (params.type === 'iframe') {
        var iframe =
          '<div id="' +
          nav_tab_id +
          '" class="well-nav-tab embed-responsive" style="width:100%;height:100%;"><iframe id="iframe_' +
          nav_tab_id +
          '" src="' +
          params.warpperUrl +
          '" class="embed-responsive-item"></iframe></div>';
        _self.$element.find('.tag-tab-content-wrapper').append(iframe);
      } else if (params.type === 'html') {
        var tabHtml = $('<div>', {
          id: nav_tab_id,
          class: 'well-nav-tab'
        });
        tabHtml.data('options', options);
        tabHtml.html(params.html);
        _self.$element.find('.tag-tab-content-wrapper').append(tabHtml[0]);
      }

      _self.$element
        .find('#' + nav_tab_id)
        .show()
        .siblings()
        .hide();

      if (type && type === 'add') {
        var $newNavTag = $('<div>', {
          class: 'nav-tag'
        });
        $newNavTag.data('options', options);
        $newNavTag.append(
          '<span class="nav-tag-text">' +
            options.text +
            '</span>' +
            '<span class="nav-tag-close"><i class="iconfont icon-ptkj-dacha-xiao"></i></span>'
        );
        if (options.menuId && options.menuId === 'mainPage') {
          $newNavTag.addClass('main-page-nav-tag').find('.nav-tag-close').remove();
        }
        $newNavTag.data('id', nav_tab_id);
        _self.$element.find('.scroll-span').append($newNavTag);
        $newNavTag.trigger('click');
      }
    }
  });
});
