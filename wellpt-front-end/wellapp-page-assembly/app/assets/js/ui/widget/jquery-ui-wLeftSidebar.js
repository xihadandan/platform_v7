(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'constant', 'commons', 'server', 'appContext', 'dataStoreBase', 'niceScroll', 'perfectScrollbar'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($, constant, commons, server, appContext, DataStore, niceScroll, PerfectScrollbar) {
  'use strict';
  var BootstrapTableViewGetCount = 'BootstrapTableViewGetCount';
  var StringUtils = commons.StringUtils;

  // IE11 Array.prototype.find polyfill
  if (!Array.prototype.find) {
    Array.prototype.find = function (callback) {
      return callback && (this.filter(callback) || [])[0];
    };
  }

  $.widget('ui.wLeftSidebar', $.ui.wWidget, {
    options: {
      // 组件定义
      widgetDefinition: {},
      // 上级容器定义
      containerDefinition: {}
    },
    _createView: function () {
      var options = $.extend({}, this.options.widgetDefinition, this.options.widgetDefinition.configuration, {
        defaultMenuLevel: 1
      });
      //this.options = options;
      this._beforeRenderView();
      this._renderView(options);
      this._setEvent(options);
      // 因为 3,和 4 是异步加载数据，所以 _afterRenderView 方法应该放在异步数据加载完后执行
      if (options.navType == 3 || options.navType == 4) {
      } else {
        this._afterRenderView(options);
      }

      var $activeItem = this.element.find('.nav-menu-active');

      function openMenu($activeItem) {
        if ($activeItem.parent().hasClass('collapse')) {
          $activeItem.parent().addClass('open').show().parent().addClass('menu-open');
          openMenu($activeItem.parent());
        }
      }
      if ($activeItem.length) {
        openMenu($activeItem);
        // 处理锚点选择的子结点信息
        var menuId = $activeItem.attr('menuId');
        if (options.params && options.params.hash && StringUtils.contains(options.params.hash, menuId)) {
          var hashInfo = appContext.parseHashInfo(options.params.hash);
          if (hashInfo && hashInfo.widgetId == this.getId()) {
            this.onHashSelection(hashInfo);
          } else {
            $activeItem.trigger('click');
          }
        } else {
          $activeItem.trigger('click');
        }
        if ($activeItem.children('.collapse').length) {
          $activeItem.children('.collapse').addClass('open');
        }
      }
      if (options.initExpend) {
        this.element.find('.collapse').addClass('open').show().parent().addClass('menu-open');
      }
      // 默认图标式收起
      // if(options.thumbnailNav){
      //     this.element.find(".nav-toggle-btn").trigger("click");
      //     var _self = this;
      //     setTimeout(function(){
      //         _self.element.parent().removeClass("ps");
      //     })
      // }
    },

    _setOption: function (key, value) {
      // 设置参数
      this.options[key] = value;
      return this;
    },

    _beforeRenderView: function () {
      var _self = this;
      var configuration = this.getConfiguration();
      if (configuration.defineEventJs && configuration.defineEventJs['beforeRenderCode']) {
        var defineFunc = appContext.eval(configuration.defineEventJs['beforeRenderCode'], $(this), {
          $this: _self,
          configuration: configuration,
          commons: commons,
          server: server
        });
        console.log('在导航组件渲染之后触发自定义代码块', defineFunc);
      }
    },

    _renderView: function (options) {
      // 生成页面组件
      var _self = this;
      // 菜单映射
      _self.menuItemMap = {};
      _self.element.empty();

      _self.selectmenuid = commons.Browser.getQueryString('menuid'); //通过浏览器地址参数指定要选中的导航菜单
      // 选中的导航菜单与锚点同时存在时，以锚点为准
      if (window.location.hash && StringUtils.contains(window.location.hash, _self.selectmenuid)) {
        _self.selectmenuid = '';
      }
      if (_self.selectmenuid) {
        _self.selectmenuid = _self.selectmenuid.split(',');
      }
      if (options.params && options.params.menuid) {
        // 通过事件参数传进来的
        _self.selectmenuid = options.params.menuid;
      }

      // NAV数据
      var nav = [];
      var navEvent; //导航事件统一配置，用于动态生成的导航节点事件处理
      if (options.navType == 1) {
        if (options.navResource) {
          server.JDS.call({
            async: false,
            service: 'appProductIntegrationMgr.getTreeByUuid',
            data: [options.navResource, ['1', '2', '3']],
            success: function (result) {
              if (options.isShowRoot) {
                nav.push(result.data);
              } else {
                nav = result.data.children;
              }
            }
          });
        } else {
          var userAppData = appContext.getCurrentUserAppData();
          var moduleApps = userAppData.getModuleApps();
          for (var i = 0; i < moduleApps.length; i++) {
            var moduleApp = moduleApps[i];
            var mApp = {};
            mApp.id = moduleApp.piUuid;
            mApp.data.name = moduleApp.name;
            mApp.data = {};
            mApp.data.path = moduleApp.path;
            mApp.data.type = 3;
            nav.push(mApp);
          }
        }
        _self._initMetismenu(options, nav);
      } else if (options.navType == 2) {
        if (options.isShowRoot === true) {
          nav = options.nav;
        } else {
          $.each(options.nav, function (i, navData) {
            nav = nav.concat(navData.children);
          });
        }
        _self._initMetismenu(options, nav);
      } else if (options.navType == 3 || options.navType == 4) {
        _self._loadTree(null, null, options.isShowRoot, options.navType, function (data) {
          _self._initMetismenu(options, data);
          _self._afterRenderView(options);
        });
      }
      _self.perfectScrollbarInitEvent();
    },

    _initMetismenu: function (options, nav) {
      var _self = this;
      //导航模块名称
      if (options.moduleText) {
        var $moduleText = $('<div>', {
          class: 'module-text'
        });
        $moduleText.text(options.moduleText);
        this.element.prepend($moduleText);
      }

      // 渲染NAV
      var navHtml =
        '<div class="metismenu">' +
        '<aside class="metismenu sidebar">' +
        '<nav class="metismenu sidebar-nav">' +
        '<ul class="metismenuul" navtype="' +
        options.navType +
        '"  loadintf="' +
        options.navInterface +
        '" datastore="' +
        options.navDataStoreId +
        '" >';

      if (!nav || !nav.length) {
        return;
      }

      var firstLevelIcon = false;
      $.each(nav, function (i, item) {
        if (item && ((item.data && item.data.icon) || item.iconSkin)) {
          firstLevelIcon = true;
          return false;
        }
      });

      if (!firstLevelIcon) {
        _self.element.addClass('first-level-no-icon');
      }

      var navObj = this._getNavItemHtml(
        nav,
        options.navType,
        options.navType == 3 || options.navType == 4
          ? {
              targetWidgetId: options.configuration.targetWidgetId,
              targetPosition: options.configuration.targetPosition,
              refreshIfExists: options.configuration.refreshIfExists,
              eventHanlderPath: options.configuration.eventHanlderPath,
              eventHanlderType: options.configuration.eventHanlderType,
              eventParams: options.configuration.eventParams
            }
          : null,
        options.defaultMenuLevel
      );

      navHtml += navObj.mainNav;

      navHtml += '</ul></nav></aside></div>';

      //添加折叠导航按钮
      if (options.isTelescopicNav) {
        var toggleBtn = $('<button/>').addClass('nav-toggle-btn close-nav');
        toggleBtn.css({
          display: 'none'
        });
        toggleBtn.on('click', function () {
          _self._navToggle.call(this, _self, options);
        });

        var iconNav;

        var allLeftSidebar = _self.element.parent().find('.ui-wLeftSidebar');

        if (options.thumbnailNav) {
          iconNav = $('<ul/>').addClass('icon-nav');
          _self.element.append(iconNav);
        }

        allLeftSidebar.find('.nav-toggle-btn').remove();

        var firstLeftSidebar = allLeftSidebar.eq(0);

        firstLeftSidebar.append(toggleBtn);
        if (iconNav) {
          $.each(navObj.iconNav, function (i, n) {
            iconNav.append(n);
          });
        }
      }

      _self.element.append(navHtml);
      if (options.navInterface) {
        _self.element.attr('loadintf', options.navInterface);
      }
      if (options.navDataStoreId) {
        _self.element.attr('datastore', options.navDataStoreId);
      }

      _self.element.off('click', '.metismenuul .nav-menu-item').on('click', '.metismenuul .nav-menu-item', function () {
        var $this = $(this);

        var _collapse = $this.children('ul.collapse');
        if ($this.children('ul.collapse').length) {
          if (_collapse.hasClass('open')) {
            $this.removeClass('menu-open').children('ul.collapse').removeClass('open').slideUp();
          } else {
            $this.addClass('menu-open').children('ul.collapse').addClass('open').slideDown();
          }
        }

        if (options.autoToggle) {
          _self.element
            .parent()
            .find('.nav-menu-item')
            .each(function () {
              var _$this = $(this);
              if (!_$this.is($this) && !_$this.is($this.parents('.nav-menu-item'))) {
                _$this.removeClass('menu-open').children('ul.collapse').removeClass('open').slideUp();
              }
            });
        }

        // 如果父级关闭，则打开父级
        var $parent = $this.parents('ul.collapse');
        if ($parent.length && !$parent.hasClass('open')) {
          $parent.addClass('open').slideDown(function () {
            $this.get(0).scrollIntoView();
          });
        }
      });

      _self.element.find('.nav-menu-item').each(function () {
        var $this = $(this);
        if ($this.hasClass('third-nav-menu-item')) {
          $this.closest('.main-nav-menu-item').addClass('has-third-nav');
        }
      });
      setTimeout(function () {
        var allLeftSidebar = _self.element.parent().find('.ui-wLeftSidebar');
        var firstLeftSidebar = allLeftSidebar.eq(0);
        var firstLeftSidebarOffset = firstLeftSidebar.parent().offset();
        var firstLeftSidebarWidth = firstLeftSidebar.parent().width();

        toggleBtn &&
          toggleBtn.css({
            top: firstLeftSidebarOffset.top - 0 + 50 + 'px',
            left: firstLeftSidebarOffset.left - 0 + firstLeftSidebarWidth + 'px',
            display: 'block'
          });
      }, 500);
    },
    //导航折叠事件
    _navToggle: function (_self, options) {
      var allLeftSidebar = _self.element.parent().find('.ui-wLeftSidebar');
      var firstLeftSidebar = allLeftSidebar.eq(0);
      var firstLeftSidebarOffset = firstLeftSidebar.parent().offset();
      if ($(this).hasClass('close-nav')) {
        if (options.thumbnailNav) {
          allLeftSidebar.find('.icon-nav').show();
          allLeftSidebar.find('.metismenu').hide();
          allLeftSidebar.css('width', '50px');
        } else {
          allLeftSidebar.find('.metismenu').hide();
          allLeftSidebar.css('width', 0);
          allLeftSidebar.closest('.ui-wSecondPageLayout').addClass('no-thumbnailNav');
        }
        $(this).removeClass('close-nav');
        $(this).css({
          left: firstLeftSidebarOffset.left - 0 + 50 + 'px'
        });
      } else {
        if (options.thumbnailNav) {
          allLeftSidebar.find('.icon-nav').hide();
          allLeftSidebar.find('.metismenu').show();
          allLeftSidebar.css('width', 'auto');
        } else {
          allLeftSidebar.find('.metismenu').show();
          allLeftSidebar.css('width', 'auto');
          allLeftSidebar.closest('.ui-wSecondPageLayout').removeClass('no-thumbnailNav');
        }
        $(this).addClass('close-nav');
        $(this).css({
          left: firstLeftSidebarOffset.left - 0 + 230 + 'px'
        });
      }
      var parentWidget = _self._getParentWidget();
      parentWidget.trigger('resize');
    },
    _getParentWidget: function () {
      var parentWidget,
        _self = this;
      var $parent = _self.element.parent();
      while (true) {
        var parentWidgetId = $parent.attr('id');
        if (parentWidgetId != undefined && parentWidgetId != null) {
          parentWidget = appContext.getWidgetById(parentWidgetId);
          if (parentWidget != null) {
            break;
          }
        } else {
          $parent = $parent.parent();
        }
      }
      return parentWidget;
    },
    _afterRenderView: function (options) {
      var _self = this;
      var configuration = this.getConfiguration();
      _self.invokeDevelopmentMethod('afterRenderView', [options]);
      if (configuration.defineEventJs && configuration.defineEventJs['afterRenderCode']) {
        var defineFunc = appContext.eval(options.configuration.defineEventJs['afterRenderCode'], $(this), {
          $this: _self,
          options: options,
          commons: commons,
          server: server
        });
        console.log('在导航组件渲染之后触发自定义代码块', defineFunc);
      }
    },
    _getNavItemHtml: function (items, navType, navEvent, navLevel) {
      var _self = this;
      var navHtml = '';
      //生成缩略图导航项
      var iconNav = [];
      var navObj = new Object();
      $.each(items || [], function (i, item) {
        if (!item) {
          return;
        }
        if (item.data && item.data.hidden) {
          return;
        }
        //menuId优先取data的数据
        var menuId = item.data ? item.data.uuid || item.data.UUID || item.data.id || item.data.ID || item.id : item.id;
        // UUID为空，随机生成
        if (StringUtils.isBlank(menuId)) {
          menuId = commons.UUID.createUUID();
          item.data.uuid = menuId;
        }
        item.data.navEvent = item.isParent ? null : navEvent;
        item.data.navType = navType;
        _self.menuItemMap[menuId] = item.data;

        var isParent = _self._hasChidren(item.isParent || item.data.isParent, item.children);

        var iconSkin = item.iconSkin || item.data.icon;

        var itemClass = 'nav-menu-item';
        //区分一、二、三级菜单

        if (navLevel == 1) {
          itemClass += ' main-nav-menu-item';
          //生成缩略图导航项
          var iconNavItem = $('<li/>').addClass('icon-nav-item').attr({
            menuId: menuId,
            title: item.name
          });
          var _div = $('<div>').addClass('icon-nav-wrap');
          var icon = $('<i/>').addClass(iconSkin || 'iconfont icon-ptkj-liebiaoshitu');

          iconNavItem.append(_div.append(icon));
          iconNavItem
            .on('mouseenter', function () {
              _self._iconNavEvent.call(this, _self);
            })
            .on('mouseleave', function () {
              $('body')
                .find('.left-slide-float-menu[datamenuid="' + menuId + '"]')
                .hide();
            });

          iconNav.push(iconNavItem);
        }

        if (navLevel == 2) {
          itemClass += ' sub-nav-menu-item';
        }

        if (navLevel == 3) {
          itemClass += ' third-nav-menu-item';
        }

        if (_self.selectmenuid && _self.selectmenuid.indexOf(menuId) != -1) {
          _self.activeItem = true;
          itemClass += ' nav-menu-active';
        }

        if (item.data && item.data.active && !_self.selectmenuid) {
          if (!_self.activeItem) {
            _self.activeItem = true;
            itemClass += ' nav-menu-active';
          }
        }

        if (_self.options.widgetDefinition.configuration.initExpend) {
          itemClass += ' active';
        }

        if (navType == 1) {
          navHtml +=
            '<li class="' +
            itemClass +
            '" menuId="' +
            menuId +
            '" isParent=' +
            (isParent ? 1 : 0) +
            '>' +
            '		<a href="javascript:void(0);" title="' +
            item.data.name +
            '">' +
            ' 		<span class="sidebar-nav-item">' +
            item.data.name +
            '</span>';
        } else if (navType == 2) {
          navHtml +=
            '<li class="' +
            itemClass +
            '" menuId="' +
            menuId +
            '" isParent=' +
            (isParent ? 1 : 0) +
            '>' +
            '		<a href="javascript:void(0);" title="' +
            item.name +
            '">' +
            '			<span class="sidebar-nav-item-icon ' +
            iconSkin +
            '"></span>' +
            ' 		<span class="sidebar-nav-item">' +
            item.name +
            '</span>';
        } else if (navType == 3 || navType == 4) {
          var iconStyle = item.iconStyle || item.data.iconStyle;
          navHtml +=
            '<li class="' +
            itemClass +
            '" menuId="' +
            menuId +
            '" isParent=' +
            (isParent ? 1 : 0) +
            '>' +
            '		<a href="javascript:void(0);" title="' +
            item.name +
            '">' +
            '			<span class="sidebar-nav-item-icon ' +
            iconSkin +
            '" style="' +
            iconStyle +
            '"></span>' +
            ' 		<span class="sidebar-nav-item">' +
            item.name +
            '</span>';
        }

        if (isParent) {
          navHtml += '<span class="arrow iconfont icon-ptkj-xianmiaojiantou-xia"></span>';
        }
        navHtml += '</a>';

        if (isParent) {
          if (_self.options.widgetDefinition.configuration.initExpend) {
            navHtml += '<ul class="collapse">';
          } else {
            navHtml += '<ul class="collapse">';
          }
          navHtml += _self._getNavItemHtml(item.children, navType, navEvent, navLevel + 1);
          navHtml += '</ul>';
        } else {
          if (!_self.firstNavTarget && item.data.eventHanlderId) {
            _self.firstNavTarget = {
              menuId: menuId,
              targetWidgetId: item.data.targetWidgetId
            };
          }
        }
        navHtml += '</li>';
      });

      if (navLevel == 1) {
        navObj.mainNav = navHtml;
        navObj.iconNav = iconNav;
        return navObj;
      } else {
        return navHtml;
      }
    },
    //缩略导航项点击事件
    _iconNavEvent: function (_self) {
      var _this = $(this);
      var menuid = $(this).attr('menuid');
      var menuArr = [];

      if ($('body').find('.left-slide-float-menu[datamenuid="' + menuid + '"]').length) {
        $('body')
          .find('.left-slide-float-menu[datamenuid="' + menuid + '"]')
          .show();
        return;
      }
      _self.element.find('.nav-menu-item').each(function () {
        var $this = $(this);
        if ($this.attr('menuid') === menuid) {
          if ($this.find('.sub-nav-menu-item').length) {
            $this.find('.sub-nav-menu-item').each(function () {
              var _$this = $(this);
              var subMenu = {
                menuId: _$this.attr('menuid'),
                name: _$this.find('a').attr('title')
              };
              if (_$this.find('.third-nav-menu-item').length) {
                subMenu.child = [];
                _$this.find('.third-nav-menu-item').each(function () {
                  var __$this = $(this);
                  subMenu.child.push({
                    menuId: __$this.attr('menuid'),
                    name: __$this.find('a').attr('title')
                  });
                });
              }
              menuArr.push(subMenu);
            });
          } else {
            menuArr.push({
              menuId: $this.attr('menuid'),
              name: $this.find('a').attr('title')
            });
          }
          return false;
        }
      });
      var $floatMenu = $('<div>', {
        class: 'left-slide-float-menu',
        datamenuid: menuid
      });
      var _wrap = $('<div>', {
        class: 'left-slide-float-menu-wrap'
      });
      $.each(menuArr, function (i, v) {
        if (v.child) {
          var subMenu = $('<div class="float-menu-item" menuid="' + v.menuId + '">' + v.name + '</div>');
          var thirdMenu = $('<div class="third-float-menu">');
          var thirdMenuWrap = $('<div class="third-float-menu-wrap">');
          $.each(v.child, function (index, item) {
            thirdMenuWrap.append(
              '<div class="float-menu-item third-float-menu-item" menuid="' + item.menuId + '" isparent="0">' + item.name + '</div>'
            );
          });
          subMenu.append('<div class="arrow"><i class="iconfont icon-ptkj-xianmiaojiantou-you"></div>');
          _wrap.append(subMenu);
          _wrap.append(thirdMenu.append(thirdMenuWrap));
        } else {
          _wrap.append('<div class="float-menu-item" menuid="' + v.menuId + '"  isparent="0">' + v.name + '</div>');
        }
      });
      $('body').append($floatMenu.append(_wrap));
      var top = $('.icon-nav-item[menuid="' + menuid + '"]').offset().top;
      var height = $('.left-slide-float-menu[datamenuid="' + menuid + '"]').height();
      var dHeight = parent.document.body.offsetHeight;
      console.log(top, height, dHeight);
      var diffHeight = top + height - dHeight;
      if (diffHeight > 0 && top + height > dHeight - 220) {
        if (height >= top) {
          $('.left-slide-float-menu[datamenuid="' + menuid + '"]').css({
            maxHeight: height / 2 + 'px',
            overflow: 'auto',
            bottom: dHeight - top - 47 + 'px',
            top: 'initial'
          });
        } else {
          $('.left-slide-float-menu[datamenuid="' + menuid + '"]').css({
            // maxHeight: height / 2 + 'px',
            overflow: 'auto',
            top: top - height + 47 + 'px'
          });
        }

        $.fn.niceScroll &&
          $('.left-slide-float-menu[datamenuid="' + menuid + '"]').niceScroll({
            cursorcolor: '#ccc'
          });
      } else if (diffHeight > 0) {
        $('.left-slide-float-menu[datamenuid="' + menuid + '"]').css({
          top: top - height + 47 + 'px'
        });
      } else {
        $('.left-slide-float-menu[datamenuid="' + menuid + '"]').css({
          top: top + 'px'
        });
      }
      $floatMenu
        .on('mouseenter', function () {
          $(this).show();
        })
        .on('mouseleave', function () {
          $(this).hide();
        });

      $floatMenu
        .find('.float-menu-item')
        .on('mouseenter', function () {
          var $this = $(this);
          var thirdMenu = $this.next('.third-float-menu');
          if (thirdMenu.length) {
            thirdMenu.show();
          }
        })
        .on('mouseleave', function () {
          var $this = $(this);
          var thirdMenu = $this.next('.third-float-menu');
          if (thirdMenu.length) {
            thirdMenu.hide();
          }
        });

      $floatMenu
        .find('.third-float-menu')
        .on('mouseenter', function () {
          $(this).show();
        })
        .on('mouseleave', function () {
          $(this).hide();
        });

      $floatMenu.find('.float-menu-item').on('click', function () {
        var $this = $(this);
        var thirdMenu = $this.children('.third-float-menu');
        if (thirdMenu.length) {
          return;
        } else {
          $('.icon-nav .selected').removeClass('selected');
          _this.addClass('selected');
          $this.addClass('selected').parent().closest('.float-menu-item').addClass('selected');
          $('li[menuid = ' + $(this).attr('menuid') + ']>a', _self.element).trigger('click');
          $floatMenu.hide();
        }
      });
    },
    _hasChidren: function (isParent, children) {
      return children && children.length > 0 && isParent === true;
    },
    _loadTree: function (parentId, searchText, isShowRoot, navType, successCallback) {
      var nav = [];
      var self = this;
      var options = self.getConfiguration();
      if (navType == undefined || navType == 3) {
        //按来源接口加载
        var params = {};
        if (StringUtils.isNotBlank(parentId)) {
          params.parentId = parentId;
        }
        params.dataProvider = options.navInterface;
        params.nodeTypeInfo = JSON.stringify(options.nodeTypeInfo);
        params.intfParams = options.srcServerParams;
        $.ajax({
          type: 'POST',
          url: ctx + '/basicdata/treecomponent/loadTree',
          data: params,
          async: true,
          dataType: 'json',
          success: function (data) {
            if (isShowRoot === false) {
              $.each(data, function (idx, item) {
                nav = nav.concat(item.children);
              });
            } else {
              nav = data;
            }
            if ($.isFunction(successCallback)) {
              successCallback(nav);
            }
          }
        });
      } else if (navType == 4) {
        //按数据仓库加载
        if (options.navDsDefaultCondition) {
          options.navDsDefaultCondition = appContext.resolveParamsNoConflics(
            {
              sql: options.navDsDefaultCondition
            },
            {
              location: location
            }
          ).sql;
        }
        server.JDS.call({
          async: true,
          service: 'cdDataStoreService.loadTreeNodes',
          data: [
            {
              dataStoreId: options.navDataStoreId,
              uniqueColumn: options.navUniqueColumn,
              parentColumn: options.navParentColumn,
              displayColumn: options.navDisplayColumn,
              treeRootName: options.navRootName,
              defaultCondition: options.navDsDefaultCondition
            }
          ],
          success: function (result) {
            if (isShowRoot === false) {
              $.each(result.data, function (idx, item) {
                nav = nav.concat(item.children);
              });
            } else {
              nav = result.data;
            }
            if ($.isFunction(successCallback)) {
              successCallback(nav);
            }
          }
        });
      }
    },
    setSelectedItem: function (item) {
      var _self = this;
      var _menuId = item.menuId;
      var $li = $('.nav-menu-item[menuId="' + _menuId + '"]');
      if ($li.attr('isparent') === '0') {
        $('.nav-menu-item.nav-menu-active', '.ui-wLeftSidebar').removeClass('nav-menu-active');
        $li.addClass('nav-menu-active');
        _self.selectedItem = item;
        if ($li.closest('.menu-sidebar')) {
          $li.closest('.menu-sidebar').attr('_menuid', _menuId);
        }
      } else {
        var _item = _self.selectedItem;
        var __menuId = item.menuId;
        if ($li.closest('.menu-sidebar')) {
          __menuId = $li.closest('.menu-sidebar').attr('_menuid');
        }
        if (__menuId) {
          var _$li = $('.nav-menu-item[menuId="' + __menuId + '"]');
          $('.nav-menu-item.nav-menu-active', '.ui-wLeftSidebar').removeClass('nav-menu-active');
          _$li.addClass('nav-menu-active');
        }
      }
    },

    getSelectedItem: function () {
      return this.selectedItem;
    },
    // 锚点选择回调处理
    onHashSelection: function (options) {
      var _self = this;
      // 调用父类方法
      _self._superApply(arguments);
      var selection = options.selection || [];
      $.each(selection, function (i, menuId) {
        var params = {};
        if (options.subHash) {
          params.menuid = options.subHash.selection.join(',');
          params.hash = appContext.hashInfoToString(options.subHash);
        }
        $("li[menuId='" + menuId + "']", _self.element).data('selectionParams', params);
        $("li[menuId='" + menuId + "']", _self.element).trigger('click');
        $("li[menuId='" + menuId + "']", _self.element).data('selectionParams', null);
        // 导航隐藏且存在上级导航时展开上级导航
        if ($("li[menuid='" + menuId + "']", _self.element).is(':hidden')) {
          $("li[menuid='" + menuId + "']", _self.element)
            .closest("li[isparent='1']")
            .trigger('click');
        }
      });
    },
    /**
     * 设置事件，注意：这里的元素事件必须是动态绑定，因为加载动态左导航是异步的，可能元素还没渲染出来
     * @param options
     * @private
     */
    _setEvent: function (options) {
      var _self = this;
      var navType = options.navType;
      if (navType == '3' || navType == '4') {
        _self.element.on('show.metisMenu', '.metismenuul', function (event) {
          var $target = $(event.target);
          if (!$target.is(':empty') || $target.is('[data-load=loaded]')) {
            return;
          }
          event.preventDefault();
          event.stopPropagation();
          $target.attr('data-load', 'loaded');
          var menuId = $target.closest('.nav-menu-item[menuid]').attr('menuId');
          var menuItem = _self.menuItemMap[menuId];
          if (menuItem == null || !menuItem.id) {
            return;
          }
          _self._loadTree(menuItem.id, function (nav) {
            var navEvent = {
              targetWidgetId: options.configuration.targetWidgetId,
              targetPosition: options.configuration.targetPosition,
              refreshIfExists: options.configuration.refreshIfExists,
              eventHanlderPath: options.configuration.eventHanlderPath,
              eventHanlderType: options.configuration.eventHanlderType,
              eventParams: options.configuration.eventParams
            };
            var navHtml = _self._getNavItemHtml(nav, navType, navEvent, options.defaultMenuLevel);
            $target.html(navHtml.mainNav);
            // 重新初始化展开
            $('.metismenuul', _self.element).metisMenu('init');
            $target.addClass('in');
          });
        });

        //动态导航的重载节点侦听
        _self.element.on('reloadMenuItem', 'ul[loadintf],ul[datastore]', function (event, param) {
          var $metismenuul = $('.metismenuul', _self.element);
          _self._loadTree(null, null, options.isShowRoot, $(this).attr('navtype'), function (nav) {
            var navEvent = {
              targetWidgetId: options.configuration.targetWidgetId,
              targetPosition: options.configuration.targetPosition,
              refreshIfExists: options.configuration.refreshIfExists,
              eventHanlderPath: options.configuration.eventHanlderPath,
              eventHanlderType: options.configuration.eventHanlderType,
              eventParams: options.configuration.eventParams
            };
            var navHtml = _self._getNavItemHtml(nav, navType, navEvent, options.defaultMenuLevel);
            $metismenuul.html(navHtml.mainNav);
            $metismenuul.metisMenu('init');
            if (param && param.expand) {
              var $a = $metismenuul.find('a:first');
              $metismenuul.find('a:first').trigger('click'); //触发自动展开
            }
            _self.invokeDevelopmentMethod('afterReloadMenuItem', [event, param]);
          });
        });
      }
      // 绑定导航点击事件
      _self.element.off('click', 'li.nav-menu-item').on('click', 'li.nav-menu-item', function (event, triggerData) {
        event.preventDefault();
        event.stopPropagation();

        // 防止重复点击
        var _$this = $(this);
        var isClick = _$this.data('isClick');
        if (isClick === true) {
          return;
        } else {
          _$this.data('isClick', true);
        }
        //根据左导航的高度设置内容最小高度
        var menuSideBar = $(this).closest('.menu-sidebar');
        setTimeout(function () {
          _$this.data('isClick', false);
          if (menuSideBar.length) {
            var menuSideBarH = menuSideBar.height();
            menuSideBar.next('.content').css('min-height', menuSideBarH);
          }
        }, 500);

        // _self.element.closest('.well-left-nav').trigger('niceScrollResize');
        var $self = $(event.target).closest('.nav-menu-item[menuid]');
        var menuId = $self.attr('menuId');
        var value = $self.attr('data-value') || menuId;
        var menuItem = _self.menuItemMap[menuId];
        if (menuItem == null) {
          return;
        }
        var appType = menuItem.eventHanlderType || menuItem.type || menuItem.appType;
        var appPath = menuItem.eventHanlderPath || menuItem.path || menuItem.appPath;
        var targetPosition = menuItem.targetPosition;
        var targetWidgetId = menuItem.targetWidgetId;
        var refreshIfExists = menuItem.refreshIfExists;
        var eventParams = $.extend({}, menuItem.eventParams);
        //使用动态导航的事件配置
        if (!appType && menuItem.navEvent) {
          appType = menuItem.navEvent.eventHanlderType;
        }
        if (!appPath && menuItem.navEvent) {
          appPath = menuItem.navEvent.eventHanlderPath;
        }
        if (!targetPosition && menuItem.navEvent) {
          targetPosition = menuItem.navEvent.targetPosition;
        }
        if (!targetWidgetId && menuItem.navEvent) {
          targetWidgetId = menuItem.navEvent.targetWidgetId;
        }
        if (!refreshIfExists && menuItem.navEvent) {
          refreshIfExists = menuItem.navEvent.refreshIfExists;
        }
        if ($.isEmptyObject(eventParams) && menuItem.navEvent) {
          eventParams = $.extend({}, menuItem.navEvent.eventParams);
        }
        // 锚点选择传入的参数
        var selectionParams = $self.data('selectionParams') || {};
        eventParams = $.extend(eventParams, appContext.parseEventHashParams(menuItem, 'menuid'), selectionParams);
        var opt = {
          value: value,
          text: $.trim($self.find('.sidebar-nav-item').text()),
          menuId: menuId,
          menuItem: menuItem,
          target: options.targetPosition,
          targetWidgetId: options.targetWidgetId,
          refreshIfExists: refreshIfExists,
          appType: appType,
          appPath: appPath,
          params: eventParams,
          event: event,
          onPrepare: {}
        };
        if (eventParams.navAddTag) {
          opt.useUniqueName = false;
        }
        if (targetPosition) {
          opt.target = targetPosition;
        }
        if (targetWidgetId) {
          opt.targetWidgetId = targetWidgetId;
          if (targetWidgetId.indexOf('wNavTab_') > -1) {
            opt.renderNavTab = true;
          }
        }

        opt.eventTarget = {
          position: menuItem.targetPosition,
          widgetSelectorTypeName: menuItem.targetWidgetSelectorTypeName,
          widgetSelectorType: menuItem.targetWidgetSelectorType || '1',
          widgetName: menuItem.targetWidgetName,
          widgetId: opt.targetWidgetId,
          widgetCssClass: menuItem.targetWidgetCssClass,
          refreshIfExists: opt.refreshIfExists
        };

        _self._relationshipQuery(opt);

        var item = {};
        item.name = $self.text();
        $.extend(item, opt);
        _self.setSelectedItem(item);
        delete item.event;

        // startApp需要放在 _self.setSelectedItem 方法之后，startApp出来的页面，如果来获取该导航的选中项时
        // 会是原来的选中结果而不是当前的选中值
        if ((navType == '1' || navType == '2' || navType == '3' || navType == '4') && $self.is('[isparent=0]')) {
          $(this).data('menuitem', menuItem);
          if (opt.renderNavTab) {
            var isExit = false;
            var navTabWidget = appContext.getWidgetByCssSelector('#' + targetWidgetId);
            navTabWidget.$element.find('.tag-tab-wrapper .scroll-body .nav-tag').each(function () {
              var $this = $(this);
              if ($this.data('id') === 'nav_tab_' + opt.menuId) {
                isExit = true;
                if (!$this.hasClass('active')) {
                  $this.trigger('click');
                }
                if (menuItem.refreshIfExists) {
                  //点击tab导航触发不重新刷新tab
                  if (!triggerData || (triggerData && triggerData !== 'navTab')) {
                    navTabWidget.refreshTab($this);
                  }
                }
                appContext.updateCurrentHash({
                  ui: _self,
                  selection: menuItem.uuid
                });
              }
            });
            if (!isExit) {
              _self.startApp(opt);
            }
          } else {
            _self.startApp(opt);
          }
        }

        var eventData = {
          selectedItem: item
        };
        _self.trigger(constant.WIDGET_EVENT.LeftSidebarItemClick, eventData);
        _self.pageContainer.trigger(constant.WIDGET_EVENT.MenuItemSelect, eventParams ? eventParams.menuid : '');

        if (
          item.menuItem.name &&
          $self.attr('isparent') !== '1' &&
          !(item.menuItem.targetPosition === '_blank' && item.menuItem.eventHanlderId.split('_')[0] === 'page')
        ) {
          top.document.title = item.menuItem.name;
        }
      });

      // 显示微章数量
      var nav = options.nav;
      // 监听容器创建完成事件
      var pageContainer = _self.pageContainer;
      pageContainer.on(constant.WIDGET_EVENT.Change, function (e, ui) {
        if (appContext.isWidgetExists(_self)) {
          $.each(_self.menuItemMap, function () {
            var navData = this;
            if (navData == null || navData.showBadgeCount !== true) {
              return;
            }
            if (navData.getBadgeCountWay === 'tableWidgetCount' || (!navData.getBadgeCountWay && navData.getBadgeCountListViewId)) {
              //表格视图的数据量计算徽章数量
              var getBadgeCountListViewId = navData.getBadgeCountListViewId;
              if (getBadgeCountListViewId !== ui.getId()) {
                return;
              }
              var totalCount = ui.getDataProvider().getCount();
              if (totalCount != 0) {
                _self._setBadgetCount(navData, totalCount, totalCount);
              }
            } else if (navData.getBadgeCountWay === 'countJs' && navData.getBadgeCountJs) {
              //表格视图的数据量计算徽章数量
              var getBadgeCountListViewId = navData.getBadgeCountJsTable;
              if (getBadgeCountListViewId !== ui.getId()) {
                return;
              }
              var totalCount = ui.getDataProvider().getCount();
              if (totalCount != 0) {
                _self._setBadgetCount(navData, totalCount, totalCount);
              }
            } else if (navData.getBadgeCountWay === 'dataStoreCount' && navData.getBadgeCountDataStore) {
              _self.refreshSingleBadge(navData);
            }
          });
        }
      });
      // 页面加载完成后，如果页面不存在组件，调用获取数量的模块取数量
      _self.getPageContainer().on(constant.WIDGET_EVENT.PageContainerCreationComplete, function () {
        $.each(_self.menuItemMap, function () {
          var navData = this;
          _self.refreshSingleBadge(navData);
        });
      });

      // 微章刷新事件
      pageContainer.on(constant.WIDGET_EVENT.BadgeRefresh, function () {
        if (appContext.isWidgetExists(_self)) {
          _self.refreshBadge();
        }
      });

      // 仅刷新动态的左导航
      pageContainer.on(constant.WIDGET_EVENT.LeftSideBarRefreshDynamicItem, function () {
        if (appContext.isWidgetExists(_self)) {
          _self._refreshDynamicItem();
        }
      });

      // 刷新事件:重新初始化导航dom元素
      pageContainer.on(constant.WIDGET_EVENT.LeftSideBarRefresh, function () {
        if (appContext.isWidgetExists(_self)) {
          _self.refresh();
        }
      });

      _self.element.on(constant.WIDGET_EVENT.WidgetCreated, function () {
        //默认展开第一个有效的导航
        if (_self.firstNavTarget && _self.getConfiguration().firstNavView) {
          $("[menuid='" + _self.firstNavTarget.menuId + "']", _self.widget.element).trigger('click');
        }
      });
    },

    //刷新动态的左导航项
    _refreshDynamicItem: function () {
      var _self = this;
      $('ul[loadintf],ul[datastore]', _self.element).each(function () {
        $(this).trigger('reloadMenuItem');
      });
    },

    //关联查询
    _relationshipQuery: function (opt) {
      var configuration = this.getConfiguration();
      if (configuration.relationshipQuery && configuration.relationshipQuery.length > 0) {
        var otherConditions = [];
        $.each(configuration.relationshipQuery, function (i) {
          var _query = this;
          // 忽略空值处理
          if (StringUtils.isBlank(opt.value) && _query.ignoreEmptyValue !== '0') {
            return;
          }
          var condition = {
            columnIndex: _query.columnIndex,
            value: opt.value,
            type: _query.operator
          };
          otherConditions.push(condition);
        });
        //关联查询的表格组件创建时候的prepare动作：增加查询条件
        opt.onPrepare[configuration.widgetViewId] = function () {
          this.otherConditions = otherConditions; // this-> 视图组件的条件，目前只与表格组件相关
          console.log('左导航关联表格组件查询条件', otherConditions);
        };
      }
    },
    // 获取导航徽章数量
    _getBadgetCount: function (nav) {
      var _self = this;
      if (!$.isEmptyObject(nav.badge)) {
        var countJsModule = nav.badge.countJsModule;
        if (StringUtils.isNotBlank(countJsModule)) {
          _self.startApp({
            isJsModule: true,
            jsModule: countJsModule,
            action: 'getCount',
            data: nav.badge,
            callback: function (count, realCount, options) {
              _self._setBadgetCount(nav, count, realCount);
            }
          });
        }
      }
    },
    _setBadgetCount: function (nav, count, realCount) {
      var _self = this;
      var selector = 'li.nav-menu-item[menuId="' + nav.uuid + '"]';
      var $nav = $(selector, _self.element).children('a');
      var $badge = $nav.children('span.badge');
      var showCount = _self._setShowCount1(nav, realCount);
      if ($badge.length == 0) {
        $badge = $('<span class="badge">' + showCount + '</span>');
        $nav.append($badge);
      } else {
        $badge.html(showCount);
      }
    },
    // 设置徽章数量判断
    _setShowCount1: function (nav, count) {
      var _self = this;
      if (!nav.getBadgeDigit || nav.getBadgeDigit == 'default') {
        //使用系统默认
        var defaultVal = _self.getBadgeNumberdisplayBit();
        return _self._setShowCount2(nav, defaultVal, count);
      } else {
        var defaultVal = nav.getBadgeDigitNumber;
        return _self._setShowCount2(nav, defaultVal, count);
      }
    },
    // 设置徽章数量判断
    _setShowCount2: function (nav, defaultVal, count) {
      if (count) {
        if (defaultVal == 'off') {
          //不控制显示位数
          return count;
        } else {
          var badgeNo = '';
          for (var i = 0; i < parseInt(defaultVal); i++) {
            badgeNo += '9';
          }
          if (count > parseInt(badgeNo)) {
            return badgeNo + '+';
          } else {
            return count;
          }
        }
      } else {
        // 0的显示问题
        if (!nav.getBadgeZero || nav.getBadgeZero == 'default') {
          var defaultZero = SystemParams.getValue('badge.number.zero.show.switch');
          if (defaultZero == '1') {
            return count;
          } else {
            return '';
          }
        } else if (nav.getBadgeZero == '1') {
          return count;
        } else if (nav.getBadgeZero == '0') {
          return '';
        }
      }
    },
    // 获取系统默认徽章显示
    getBadgeNumberdisplayBit: function () {
      var _self = this;
      //徽章数量显示位数默认值（全局）：2
      var defaultVal = SystemParams.getValue('badge.number.display.bit.default');
      if (defaultVal && parseInt(defaultVal)) {
        defaultVal = parseInt(defaultVal);
        if (defaultVal < 1) {
          defaultVal = 'off';
        }
      } else {
        defaultVal = 'off';
      }
      //徽章数量显示位数开关（全局）：0 为关，表示不做处理；1 为开，表示使用默认值。
      var switchVal = SystemParams.getValue('badge.number.display.bit.switch');
      if (switchVal == '1') {
        return defaultVal;
      }
      return 'off';
    },
    // 刷新徽章
    refreshBadge: function () {
      var _self = this;
      var menuItemMap = _self.menuItemMap;
      $.each(menuItemMap, function () {
        // _self._getBadgetCount(this);
        var navData = this;
        _self.refreshSingleBadge(navData);
      });
    },
    refreshSingleBadge: function (navData) {
      var _self = this;
      if (navData == null || navData.showBadgeCount !== true) {
        return;
      }

      if (navData.getBadgeCountWay === 'tableWidgetCount' || (!navData.getBadgeCountWay && navData.getBadgeCountListViewId)) {
        //表格视图的数据量计算徽章数量
        var getBadgeCountListViewId = navData.getBadgeCountListViewId;
        // 获取导航徽章数量
        navData.badge = {
          countJsModule: BootstrapTableViewGetCount,
          widgetDefId: getBadgeCountListViewId
        };
        _self._getBadgetCount(navData);
      } else if (navData.getBadgeCountWay === 'dataStoreCount' && navData.getBadgeCountDataStore) {
        //按数据仓库的数据量计算徽章数量
        _self._badgeCountDataProvider = new DataStore({
          dataStoreId: navData.getBadgeCountDataStore,
          onDataChange: function (data, count) {
            _self._setBadgetCount(navData, count, count);
          }
        });
        _self._badgeCountDataProvider.getCount(true);
      } else if (navData.getBadgeCountWay === 'countJs' && navData.getBadgeCountJs) {
        //执行统计js脚本
        // 获取导航徽章数量
        navData.badge = {
          countJsModule: navData.getBadgeCountJs,
          widgetDefId: navData.getBadgeCountJsTable
        };
        _self._getBadgetCount(navData);
      }
    },
    refresh: function () {
      var options = $.extend({}, this.options.widgetDefinition, this.options.widgetDefinition.configuration, {
        defaultMenuLevel: 1
      });
      this._renderView(options);
      this.refreshBadge();
    },
    // 生成左导航滚动条
    perfectScrollbarInitEvent: function () {
      var _self = this;
      var $ps = _self.element.parents('.menu-sidebar')[0];
      if ($ps) {
        var ps = new PerfectScrollbar($ps);
        _self.ps = window.ps = ps;

        _self.element.parents('.menu-sidebar').on('niceScrollResize', function () {
          _self.niceScrollResize(ps);
        });
      }
    },
    niceScrollResize: function (PS) {
      var _self = this;
      _self.ps.update();
      _self.element.parents('.menu-sidebar').scrollTop(0);
    }
  });
});
