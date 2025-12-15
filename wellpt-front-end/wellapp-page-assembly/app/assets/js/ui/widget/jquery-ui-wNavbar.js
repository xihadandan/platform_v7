(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'constant', 'commons', 'server', 'appContext', 'appModal'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($, constant, commons, server, appContext, appModal) {
  'use strict';
  var Browser = commons.Browser;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var SpringSecurityUtils = server.SpringSecurityUtils;
  // 组装当前LI项
  var navTpl = '<li class="nav-menu-item" menuId="{1}">';
  navTpl += '<a><span class="ui-wIcon {2}"></span>{3}</a></li>';

  (function ($) {
    $.fn.addBack = $.fn.addBack || $.fn.andSelf;
    $.fn.extend({
      actual: function (method, options) {
        // check if the jQuery method exist
        if (!this[method]) {
          throw '$.actual => The jQuery method "' + method + '" you called does not exist';
        }
        var defaults = {
          absolute: false,
          clone: false,
          includeMargin: false
        };
        var configs = $.extend(defaults, options);
        var $target = this.eq(0);
        var fix, restore;
        if (configs.clone === true) {
          fix = function () {
            var style = 'position: absolute !important; top: -1000 !important; ';
            // this is useful with css3pie
            $target = $target.clone().attr('style', style).appendTo('body');
          };
          restore = function () {
            // remove DOM element after getting the width
            $target.remove();
          };
        } else {
          var tmp = [];
          var style = '';
          var $hidden;
          fix = function () {
            // get all hidden parents
            $hidden = $target.parents().addBack().filter(':hidden');
            style += 'visibility: hidden !important; display: block !important; ';

            if (configs.absolute === true) style += 'position: absolute !important; ';

            // save the origin style props
            // set the hidden el css to be got the actual value later
            $hidden.each(function () {
              var $this = $(this);

              // Save original style. If no style was set, attr() returns undefined
              tmp.push($this.attr('style'));
              $this.attr('style', style);
            });
          };
          restore = function () {
            // restore origin style values
            $hidden.each(function (i) {
              var $this = $(this);
              var _tmp = tmp[i];

              if (_tmp === undefined) {
                $this.removeAttr('style');
              } else {
                $this.attr('style', _tmp);
              }
            });
          };
        }

        fix();
        // get the actual value with user specific methed
        // it can be 'width', 'height', 'outerWidth', 'innerWidth'... etc
        // configs.includeMargin only works for 'outerWidth' and 'outerHeight'
        var actual = /(outer)/.test(method) ? $target[method](configs.includeMargin) : $target[method]();

        restore();
        // IMPORTANT, this plugin only return the value of the first element
        return actual;
      }
    });
  })(jQuery);

  $.widget('ui.wNavbar', $.ui.wWidget, {
    options: {
      // 组件定义
      widgetDefinition: {},
      // 上级容器定义
      containerDefinition: {}
    },
    constant: {
      NAV_TYPE: {
        SUB_NAV: 'subNav',
        TOOL_BAR: 'toolBar'
      }
    },
    _createView: function () {
      var _self = this;
      var options = $.extend({}, _self.options.widgetDefinition, _self.options.widgetDefinition.configuration);
      _self.$element = $(_self.element);
      _self._renderView(options);
      _self._setEvent(options);
    },
    // 生成页面组件
    _renderView: function (options) {
      var _self = this;
      _self.menuItemMap = {};
      if (options.contentWidth) {
        $('.navbar-content', _self.element).css({ width: options.contentWidth + 'px', margin: '0 auto' });
      }
      _self.selectmenuid = commons.Browser.getQueryString('menuid'); //通过浏览器地址参数指定要选中的导航菜单
      if (_self.selectmenuid) {
        _self.selectmenuid = _self.selectmenuid.split(',');
      }
      if (options.params && options.params.menuid) {
        // 通过事件参数传进来的
        _self.selectmenuid = options.params.menuid;
      }

      // 渲染导航条
      if (options.isShowCustomNavbar == 'true' || options.isShowCustomNavbar[0] == 'true') {
        _self.renderCustomNavBar(options);
      } else {
        _self.renderNavBar(options);
      }

      if (options.fixedTop !== '0') {
        $(document.body).addClass('fixed-navbar');
        if ($('.ui-wNewHeader').length) $(document.body).addClass('fixed-header');
      }

      // 渲染导航工具
      // _self.renderToolBar(options);
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
      });
    },
    renderNavBar: function (options) {
      var _self = this;
      var subNav = options.subNav;
      if (options.subNavAndToolBarHidden === true || !subNav) {
        return;
      }
      var navbarItems = subNav.menuItems;
      //对导航项进行分组
      var menuItems = this._getNavbarItem(navbarItems);

      //添加更多模块导航
      var showMenuItems = menuItems.length > 10 ? menuItems.slice(0, 9) : menuItems;
      var moreMenuItems = menuItems.length > 10 ? menuItems.slice(9, menuItems.length) : [];
      var moreObject = {
        text: '更多模块',
        eventType: 'click',
        isMore: true,
        hidden: '',
        iconClass: 'caret',
        uuid: ''
      };
      if (menuItems.length > 10) {
        showMenuItems.push(moreObject);
      }
      if (!menuItems || menuItems.length == 0) {
        return;
      }
      var navStyle = subNav.navStyle;
      var tabContent = null;
      // 标签点位内容
      if (StringUtils.isNotBlank(navStyle)) {
        tabContent = new StringBuilder();
        tabContent.append('<div class="tab-content">');
      }
      var sb = new StringBuilder();
      $.each(showMenuItems, function (i, nav) {
        if (nav.hidden === '1') {
          return;
        }
        if (tabContent != null) {
          tabContent.appendFormat('<div class="tab-pane fade" id="{0}"></div>', nav.uuid);
        }
        if (nav.icon) {
          nav.iconClass = nav.icon.className;
        }
        //二级子菜单
        if (nav[nav.text]) {
          var dropdownItem = '<li class="nav-menu-item nav-group-item" >';
          dropdownItem += '<a class="dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">';
          dropdownItem += nav.text;
          dropdownItem += '<ul class="dropdown-menu-items dropdown-menu clearfix">';
          sb.append(dropdownItem);
          for (var i = 0; i < nav[nav.text].length; i++) {
            _self.menuItemMap[nav[nav.text][i].uuid] = nav[nav.text][i];
            sb.appendFormat(navTpl, i, nav[nav.text][i].uuid, nav[nav.text][i].icon.className, nav[nav.text][i].text);
          }
          sb.append('</ul></li>');
        } else {
          //更多菜单下拉项
          if (nav.isMore) {
            var liTpl = '<li class="nav-menu-item item-{0} more-menu-item" >';
            liTpl +=
              '<a class="dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">{2}<span class="ui-wIcon {1}"></span></a></li>';
            sb.appendFormat(liTpl, i, nav.iconClass, nav.text);
          } else {
            _self.menuItemMap[nav.uuid] = nav;
            sb.appendFormat(navTpl, i, nav.uuid, nav.iconClass, nav.text);
          }
        }
      });
      $('.ui-navbar-nav', _self.element).html(sb.toString());
      if (tabContent != null) {
        tabContent.append('</div>');
        $(tabContent.toString()).insertAfter($('.ui-navbar-nav', _self.element));
        // 初始化页签项
        $('.ui-navbar-nav a', _self.element).on('click', function (e) {
          e.preventDefault();
          $(this).tab('show');
          $(this).blur();
        });
      }
      if (moreMenuItems.length != 0) {
        var sb1 = new StringBuilder();
        sb1.append("<ul class='more-menu-items dropdown-menu clearfix'>");
        $.each(moreMenuItems, function (i, nav) {
          if (nav.hidden === '1') {
            return;
          }
          if (tabContent != null) {
            tabContent.appendFormat('<div class="tab-pane fade" id="{0}"></div>', nav.uuid);
          }
          _self.menuItemMap[nav.uuid] = nav;
          if (nav.icon) {
            nav.iconClass = nav.icon.className;
          }
          sb1.appendFormat(navTpl, i, nav.uuid, nav.iconClass, nav.text);
        });
        sb1.append('</ul>');
        $('.ui-navbar-nav .more-menu-item', _self.element).append(sb1.toString());
      }
      $('.ui-navbar-nav li', _self.element).on('click', function (e) {
        e.preventDefault();
        $('.ui-navbar-nav li').removeClass('active');
        $(this).addClass('active');
      });
    },
    renderCustomNavBar: function (options) {
      var sb = new StringBuilder();
      var navData = options.nav;
      var _self = this;
      var getNavbarHtml = function (navData, navLevel) {
        $.each(navData || [], function (i, item) {
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
          //	                item.data.navEvent = item.isParent ? null : navEvent;
          _self.menuItemMap[menuId] = item.data;

          var isParent = _self._hasChildren(item.isParent || item.data.isParent, item.children);

          if (isParent) {
            var dropdownItem = '<li class="nav-menu-item nav-group-item" >';
            dropdownItem +=
              '<a class="dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">' +
              item.data.name +
              '<span class="ui-wIcon caret"></span></a>';
            sb.append(dropdownItem);
          } else {
            sb.appendFormat(navTpl, i, item.data.uuid, item.data.iconClass, item.data.name);
          }

          if (isParent) {
            if (navLevel == 1) {
              sb.append('<ul class="dropdown-menu">');
            } else {
              sb.append('<ul class="dropdown-menu nav-dropdown">');
            }
            getNavbarHtml(item.children, navLevel + 1);
            sb.append('</ul></li>');
          }
        });
      };
      _self.renderMainNavBarHtml(navData);
      // getNavbarHtml(navData,1);
      // $(".ui-navbar-nav", _self.element).html(sb.toString());
    },

    //渲染自定义主导航
    renderMainNavBarHtml: function (navData) {
      var _self = this;
      var configuration = _self.getConfiguration();
      var moreNavArr = [];
      var hasShowNav = [];
      var currentWidth = 0;
      var navBarMaxWidth = _self.element.width() - 500;
      if (!navData.length) {
        return;
      }
      $.each(navData, function (i, item) {
        if (item.data && item.data.hidden) {
          return true;
        }
        // menuId优先取data的数据
        var menuId = item.data ? item.data.uuid || item.data.UUID || item.data.id || item.data.ID || item.id : item.id;

        // UUID为空，随机生成
        if (StringUtils.isBlank(menuId)) {
          menuId = commons.UUID.createUUID();
          item.data.uuid = menuId;
        }
        _self.menuItemMap[menuId] = item.data;
        var isParent = _self._hasChildren(item.isParent || item.data.isParent, item.children);
        var $navLi = $('<li>', {
          class: 'nav-menu-item',
          menuid: menuId
        });
        var _li_a = new StringBuilder();
        if (item.data.icon) {
          _li_a.appendFormat('<a><span class="ui-wIcon {1}"></span>{2}</a>', menuId, item.data.icon, item.data.name);
        } else {
          _li_a.appendFormat('<a>{1}</a>', menuId, item.data.name);
        }
        $navLi.append(_li_a.toString());
        if (_self.activeItem == undefined && _self.selectmenuid && _self.selectmenuid.indexOf(menuId) != -1) {
          _self.activeItem = menuId;
        }
        if (currentWidth < navBarMaxWidth) {
          $('.ui-navbar-nav', _self.element).append($navLi);
          var _width = parseInt($navLi.find('a').outerWidth());
          currentWidth += _width;
          hasShowNav.push({
            menuId: menuId,
            item: item,
            width: _width
          });
        } else {
          moreNavArr.push({
            menuId: menuId,
            item: item
          });
        }
      });
      var more_menuId = commons.UUID.createUUID();
      if (moreNavArr.length) {
        $('.ui-navbar-nav', _self.element).append(
          '<li class="nav-menu-item more-nav-bar" menuid="' + more_menuId + '"><i class="iconfont icon-ptkj-gengduocaidan"></i></li>'
        );
      }
      if (configuration.navBarStyle === 'single') {
        _self.renderSingleSubNavBarHtml(hasShowNav, moreNavArr, more_menuId);
      } else {
        _self.renderAllSubNavBarHtml(hasShowNav, moreNavArr, more_menuId);
      }
    },
    //渲染单个自定义导航二级导航
    renderSingleSubNavBarHtml: function (hasShowNav, moreNavArr, more_menuId) {
      var _self = this;
      $.each(hasShowNav, function (index, nav) {
        var $subWrap = $('<div>', {
          class: 'single-sub-nav-wrap'
        });
        var $subUl = $('<ul>', {
          class: 'single-sub-nav-list sub-nav-list',
          menuId: nav.menuId
        });
        $subUl.css('min-width', nav.width);
        if (nav.item.children && nav.item.children.length) {
          var hasArrow = false; //用于对齐
          $.each(nav.item.children, function (i, _child) {
            if (_child.data.hidden) {
              return true;
            }
            var $subLi = $('<li class="sub-nav-li" menuId="' + _child.data.uuid + '" title="' + _child.data.name + '"></li>');
            if (_child.data.icon) {
              $subLi.append('<a><span class="ui-wIcon ' + _child.data.icon + '"></span>' + _child.data.name + '</a>');
            } else {
              $subLi.append('<a>' + _child.data.name + '</a>');
            }
            if (_child.children && _child.children.length > 0) {
              hasArrow = true;
              $subLi.data('children', _child.children).append('<i class="sub-nav-arrow iconfont icon-ptkj-xianmiaojiantou-you"></i>');
              _self.renderDynamicNav($subLi, _child.children, nav.width);
            }
            _self.menuItemMap[_child.data.uuid] = _child.data;
            $subUl.append($subLi);
            if (_self.activeItem == undefined && _self.selectmenuid && _self.selectmenuid.indexOf(_child.data.uuid) != -1) {
              _self.activeItem = _child.data.uuid;
            }
          });
          if (hasArrow) {
            $subUl.children('.sub-nav-li').css('padding-right', '48px');
          }

          if (nav.item.children.length > _self._getOverflowItemsCount()) {
            var $btnLi = $(
              '<li class="btn-li"><button class="move-up disabled"><span class="icon iconfont icon-ptkj-shixinjiantou-shang"></span></button><button class="move-down"><span class="icon iconfont icon-ptkj-shixinjiantou-xia"></span></button></li>'
            );
            $subUl.append($btnLi);
            $subUl.addClass('sub-nav-scrollable');
          }

          $subWrap.append($subUl);
          _self.element.find('.nav-menu-item[menuid="' + nav.menuId + '"]').append($subWrap);
        }
      });

      var $moreSubWrap = $('<div>', {
        class: 'single-sub-nav-wrap'
      });
      var $moreUl = $('<ul>', {
        class: 'single-sub-nav-list sub-more-nav-list sub-nav-list',
        menuId: more_menuId
      });
      $moreUl.css('min-width', '120px'); //给更多菜单首级列表默认宽度120px
      var hasArrow = false; //用于对齐
      $.each(moreNavArr, function (index, nav) {
        if (nav.item.data.hidden) {
          return true;
        }
        var $subLi = $('<li class="sub-nav-li" menuId="' + nav.item.data.uuid + '" title="' + nav.item.data.name + '"></li>');
        if (nav.item.data.icon) {
          $subLi.append('<a><span class="ui-wIcon ' + nav.item.data.icon + '"></span>' + nav.item.data.name + '</a>');
        } else {
          $subLi.append('<a>' + nav.item.data.name + '</a>');
        }
        var isEmpty = false;
        if (nav.item.children && nav.item.children.length > 0) {
          hasArrow = true;
          $subLi.data('children', nav.item.children).append('<i class="sub-nav-arrow iconfont icon-ptkj-xianmiaojiantou-you"></i>');
          _self.renderDynamicNav($subLi, nav.item.children, nav.width || 0);
        }
        _self.menuItemMap[nav.item.data.uuid] = nav.item.data;
        $moreUl.append($subLi);
      });
      if (hasArrow) {
        $moreUl.children('.sub-nav-li').css('padding-right', '48px');
      }
      if (moreNavArr.length) {
        $moreSubWrap.append($moreUl);
        _self.element.find('.more-nav-bar').append($moreSubWrap);
      }
    },
    //渲染全部自定义导航二级导航
    renderAllSubNavBarHtml: function (hasShowNav, moreNavArr, more_menuId) {
      var _self = this;
      var _html = $('<div>', {
        class: 'sub-nav-wrap clearfix'
      });
      var isEmpty = true;
      _self.subNavWrap = _html;
      $.each(hasShowNav, function (index, nav) {
        if (nav.item.data.hidden) {
          return true;
        }
        var $subUl = $('<ul>', {
          class: 'sub-nav-list',
          menuId: nav.menuId
        });
        $subUl.css('width', nav.width);
        if (nav.item.children && nav.item.children.length) {
          isEmpty = false;
          $.each(nav.item.children, function (i, _child) {
            if (_child.data.hidden) {
              return true;
            }
            var $subLi = $('<li class="sub-nav-li" menuId="' + _child.data.uuid + '" title="' + _child.data.name + '"></li>');
            if (_child.data.icon) {
              $subLi.append('<a><span class="ui-wIcon ' + _child.data.icon + '"></span>' + _child.data.name + '</a>');
            } else {
              $subLi.append('<a>' + _child.data.name + '</a>');
            }
            if (_child.children && _child.children.length > 0) {
              $subLi.data('children', _child.children).append('<i class="sub-nav-arrow iconfont icon-ptkj-xianmiaojiantou-you"></i>');
              _self.renderDynamicNav($subLi, _child.children, nav.width || 0);
            }
            _self.menuItemMap[_child.data.uuid] = _child.data;
            $subUl.append($subLi);
          });

          if (nav.item.children.length > _self._getOverflowItemsCount()) {
            var $btnLi = $(
              '<li class="btn-li"><button class="move-up disabled"><span class="icon iconfont icon-ptkj-shixinjiantou-shang"></span></button><button class="move-down"><span class="icon iconfont icon-ptkj-shixinjiantou-xia"></span></button></li>'
            );
            $subUl.append($btnLi);
            $subUl.addClass('sub-nav-scrollable');
          }
          _html.append($subUl);
        } else {
          _html.append($subUl);
        }
      });

      var $moreUl = $('<ul>', {
        class: 'sub-more-nav-list sub-nav-list',
        menuId: more_menuId
      });
      $moreUl.css('width', '120px'); //给更多菜单首级列表默认宽度120px
      $.each(moreNavArr, function (index, nav) {
        if (nav.item.data.hidden) {
          return true;
        }
        var $subLi = $('<li class="sub-nav-li" menuId="' + nav.item.data.uuid + '" title="' + nav.item.data.name + '"></li>');
        if (nav.item.data.icon) {
          $subLi.append('<a><span class="ui-wIcon ' + nav.item.data.icon + '"></span>' + nav.item.data.name + '</a>');
        } else {
          $subLi.append('<a>' + nav.item.data.name + '</a>');
        }
        isEmpty = false;
        if (nav.item.children && nav.item.children.length > 0) {
          $subLi.data('children', nav.item.children).append('<i class="sub-nav-arrow iconfont icon-ptkj-xianmiaojiantou-you"></i>');
          _self.renderDynamicNav($subLi, nav.item.children, nav.width || 0);
        }
        _self.menuItemMap[nav.item.data.uuid] = nav.item.data;
        $moreUl.append($subLi);
      });
      if (moreNavArr.length) {
        _html.append($moreUl);
      }
      if (isEmpty) {
        return;
      }
      $('.ui-navbar-nav', _self.element).after(_html);
      _self.subNavWrap = _html;
    },

    setSubNavHeight: function (subNavScrollHeight) {
      var _self = this;
      var subNavWrapHeight = _self.subNavWrap.outerHeight();
      var bodyHeight = _self._getBodyHeight();
      if (subNavWrapHeight && subNavWrapHeight > bodyHeight) subNavWrapHeight = bodyHeight;
      var maxHeight;
      if (subNavScrollHeight) {
        if (bodyHeight < subNavScrollHeight) {
          maxHeight = bodyHeight;
        } else {
          maxHeight = subNavScrollHeight;
        }
      }
      maxHeight = maxHeight ? (subNavWrapHeight >= maxHeight ? subNavWrapHeight : maxHeight) : subNavWrapHeight;
      _self.subNavWrap.css('height', maxHeight + 'px');
      _self.$element.find('.sub-nav-list').css({ height: maxHeight + 'px', 'overflow-y': 'auto' });
    },

    navHover: function (menuId) {
      var _self = this;
      var $navMenuItem = _self.$element.find('.nav-menu-item[menuId="' + menuId + '"]');
      var $subMenuItem = _self.$element.find('.sub-nav-list[menuId="' + menuId + '"]');
      $navMenuItem.addClass('hover').siblings().removeClass('hover');
      if ($subMenuItem.html() === '') {
        $subMenuItem.siblings().removeClass('hover');
      } else {
        $subMenuItem.addClass('hover').siblings().removeClass('hover');
      }
    },

    getMaxHeight: function (h1, h2) {
      return h1 > h2 ? h1 : h2;
    },

    setSingleNavListHeight: function ($dynamicNav, $dynamicNavHeight) {
      var _self = this;
      var _parentList = $dynamicNav.parent().closest('ul');
      var _parentListHeight = _parentList.outerHeight();
      var maxHeight = _self.getMaxHeight($dynamicNavHeight, _parentListHeight);
      var clientHeight = _self._getBodyHeight();
      var warpHeight = maxHeight > clientHeight ? clientHeight : maxHeight;
      $dynamicNav.css('height', warpHeight + 'px');
      _parentList.css('height', warpHeight + 'px');
      if (!_parentList.hasClass('single-sub-nav-list')) {
        _self.setSingleNavListHeight(_parentList, warpHeight);
      }
    },
    _setNavListHeight: function () {},

    setSingleNavListWidth: function ($ele, type) {
      var $singleNavWrap = $ele.closest('.single-sub-nav-wrap');
      var $singleNavList = $singleNavWrap.find('.single-sub-nav-list');
      var singleNavListWidth = $singleNavList.outerWidth();
      var singleNavWrapMinWidth = $singleNavWrap.data('min-width') || $singleNavList.data('width');
      if (type && type === 'noList') {
        var minWidth = 0;
        $singleNavList.find('.dynamic-nav').each(function () {
          var $this = $(this);
          if ($this.css('display') === 'block') {
            $this.css('width', $this.outerWidth());
            minWidth += $this.outerWidth();
          }
        });
        $singleNavWrap.data('min-width', singleNavListWidth + minWidth).css('min-width', singleNavListWidth + minWidth + 'px');
      } else if (type && type === 'minus') {
        var minusList = $ele.children('.dynamic-nav');
        var minusListW = minusList.outerWidth();
        $singleNavWrap.data('min-width', singleNavWrapMinWidth - minusListW).css('min-width', singleNavWrapMinWidth - minusListW + 'px');
      } else if (type && type === 'minusDep') {
        var minusList = $ele;
        var minusListW = minusList.outerWidth();
        var minusDepList = minusList.children('.dynamic-nav');
        var minusDepListW = minusDepList.outerWidth();
        $singleNavWrap
          .data('min-width', singleNavWrapMinWidth - minusListW - minusDepListW)
          .css('min-width', singleNavWrapMinWidth - minusListW - minusDepListW + 'px');
      } else {
        var dynamicNavWidth = $ele.outerWidth();
        $singleNavWrap
          .data('min-width', singleNavWrapMinWidth + dynamicNavWidth)
          .css('min-width', singleNavWrapMinWidth + dynamicNavWidth + 'px');
      }
    },

    setSubNavPosition: function (_mount, $dynamicNav) {
      var _self = this;
      var _mountP = _mount.parent();
      var direction = _mountP.attr('direction');
      var mountOffset = _mount.offset();
      var mountDataWidth = _mount.data('width');
      var $dynamicNavHeight = $dynamicNav.outerHeight();
      var windowWidth = window.innerWidth;
      var mountOutWidth;
      var $singleWrap = _mount.closest('.single-sub-nav-wrap');

      if (!mountDataWidth) {
        _mount.data('width', _mount.outerWidth());
        mountDataWidth = _mount.outerWidth();
      }
      mountOutWidth = mountDataWidth;
      if (_mountP.hasClass('single-sub-nav-list') && !_mountP.data('width')) {
        _mountP.data('width', mountOutWidth);
      }

      if (!_mountP.hasClass('dynamic-nav')) {
        var _dynamicNavWidth = _self.getSubNavMaxWidth($dynamicNav);
        // $dynamicNav.css('width', mountOutWidth);
        direction = windowWidth - mountOffset.left - mountOutWidth - _dynamicNavWidth > 30 ? 'left' : 'right';
      }
      $dynamicNav.closest('li').attr('direction', direction);
      $dynamicNav.attr('direction', direction);
      if (_self.subNavWrap) {
        if (direction === 'left') {
          $dynamicNav.css({
            left: mountOffset.left + mountOutWidth + 'px'
            // borderLeft: '1px solid rgba(80, 130, 180, 0.75)',
            // borderRight: 'none'
          });
          $dynamicNav.addClass('border-left').removeClass('border-right');
        } else {
          $dynamicNav.css({
            right: windowWidth - mountOffset.left + 'px'
            // borderLeft: 'none',
            // borderRight: '1px solid rgba(80, 130, 180, 0.75)'
          });

          $dynamicNav.addClass('border-right').removeClass('border-left');
        }
        // console.log('b',$dynamicNav, _self.subNavWrap.offset().top)
        // $dynamicNav.css('top', _self.subNavWrap.offset().top + 'px');

        $dynamicNav.css('top', '131px');
      } else {
        if (direction === 'left') {
          $singleWrap.removeClass('direction-right');
          $dynamicNav.css({ left: mountOffset.left + mountOutWidth + 'px' });
        } else {
          $singleWrap.addClass('direction-right');
          var w1 = $singleWrap.find('.single-sub-nav-list').width();
          var w2 = $singleWrap.closest('li').width();
          $singleWrap.css({ right: w2 - w1, height: $singleWrap.find('.single-sub-nav-list').outerHeight() + 'px' });
          $dynamicNav.css({ right: windowWidth - mountOffset.left + 'px' });
        }

        // console.log('a', $dynamicNav, _mount.closest('.single-sub-nav-list').offset().top, _self.subNavWrap.offset().top);
        // $dynamicNav.css('top', _mount.closest('.single-sub-nav-list').offset().top + 'px');

        $dynamicNav.css('top', '131px');
        _self.setSingleNavListHeight($dynamicNav, $dynamicNavHeight);
      }
    },

    getSubNavMaxWidth: function ($dynamicNav) {
      var _self = this;
      // var _width = $dynamicNav.outerWidth();
      var _width = $dynamicNav.actual('outerWidth', { includeMargin: true });
      var _maxWidth = 0;
      $dynamicNav.find('.sub-nav-li').each(function () {
        var $this = $(this);
        var _child = $this.children('.dynamic-nav');
        if (_child.length) {
          _maxWidth = _maxWidth > _self.getSubNavMaxWidth(_child) ? _maxWidth : _self.getSubNavMaxWidth(_child);
        }
      });

      $dynamicNav.css('width', _width + 2);
      return _width + _maxWidth;
    },

    renderDynamicNav: function (_mount, _childrenData, width) {
      var _self = this;
      var $list = $('<ul>', {
        class: 'sub-nav-list dynamic-nav'
      });
      // if (width) $list.css('width', width);
      var hasArrow = false; //用于对齐
      $.each(_childrenData, function (i, _child) {
        var $subLi = $('<li class="sub-nav-li" menuId="' + _child.data.uuid + '" title="' + _child.data.name + '"></li>');
        if (_child.data.icon) {
          $subLi.append('<a><span class="ui-wIcon ' + _child.data.icon + '"></span>' + _child.data.name + '</a>');
        } else {
          $subLi.append('<a>' + _child.data.name + '</a>');
        }
        if (_child.children && _child.children.length > 0) {
          hasArrow = true;
          $subLi.data('children', _child.children).append('<i class="sub-nav-arrow iconfont icon-ptkj-xianmiaojiantou-you"></i>');
          _self.renderDynamicNav($subLi, _child.children, width);
        }
        _self.menuItemMap[_child.data.uuid] = _child.data;
        $list.append($subLi);
      });
      if (hasArrow && !_self.subNavWrap) {
        $list.children('.sub-nav-li').css('padding-right', '48px');
      }
      if (_childrenData.length > _self._getOverflowItemsCount()) {
        var $btnLi = $(
          '<li class="btn-li"><button class="move-up disabled"><span class="icon iconfont icon-ptkj-shixinjiantou-shang"></span></button><button class="move-down"><span class="icon iconfont icon-ptkj-shixinjiantou-xia"></span></button></li>'
        );
        $list.append($btnLi);
        $list.addClass('sub-nav-scrollable');
      }
      _mount.append($list);
    },

    renderToolBar: function (options) {
      var _self = this;
      var toolBar = options.toolBar;
      if (options.subNavAndToolBarHidden === true || !toolBar) {
        return;
      }
      var menuItems = this._getNavbarItem(toolBar.menuItems);
      if (!menuItems || menuItems.length == 0) {
        return;
      }
      var sb = new StringBuilder();
      $.each(menuItems, function (i, nav) {
        if (nav.hidden === '1') {
          return;
        }
        _self.menuItemMap[nav.uuid] = nav;
        if (nav.icon) {
          nav.iconClass = nav.icon.className;
        }
        if (nav[nav.text]) {
          var dropdownItem = '<li class="nav-menu-item nav-group-item">';
          dropdownItem +=
            '<a class="dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">' +
            nav.text +
            '<span class="ui-wIcon caret"></span></a>';
          dropdownItem += '<ul class="dropdown-menu-items dropdown-menu clearfix">';
          sb.append(dropdownItem);
          for (var i = 0; i < nav[nav.text].length; i++) {
            _self.menuItemMap[nav[nav.text][i].uuid] = nav[nav.text][i];
            sb.appendFormat(navTpl, i, nav[nav.text][i].uuid, nav[nav.text][i].iconClass, nav[nav.text][i].text);
          }
          sb.append('</ul></li>');
        } else {
          _self.menuItemMap[nav.uuid] = nav;
          sb.appendFormat(navTpl, i, nav.uuid, nav.iconClass, nav.text);
        }
      });
      $('.ui-navbar-toolBar', _self.element).html(sb.toString());
    },
    _hasChildren: function (isParent, children) {
      return children && children.length > 0 && isParent === true;
    },
    _getNavbarItem: function (itemArr) {
      var menuItems = [];
      itemArr.forEach(function (item) {
        if (item.group) {
          if (menuItems.length == 0) {
            var obj = new Object();
            obj['text'] = item.group;
            obj[item.group] = [];
            obj[item.group].push(item);
            menuItems[0] = obj;
          } else {
            for (var i = 0; i < menuItems.length; i++) {
              if (menuItems[i][item.group]) {
                menuItems[i][item.group].push(item);
                break;
              } else {
                var itemObj = new Object();
                itemObj['text'] = item.group;
                itemObj[item.group] = [];
                if (!menuItems[i + 1]) {
                  menuItems[i + 1] = itemObj;
                }
              }
            }
          }
        } else {
          menuItems.push(item);
        }
      });
      return menuItems;
    },
    _setEvent: function (options) {
      var _self = this;
      var configuration = _self.getConfiguration();
      var eventCallback = function (event) {
        // 避免触发父级导航的事件
        event.stopPropagation();

        $(this).addClass('active').siblings().removeClass('active');
        if ($(this).hasClass('more-nav-bar')) {
          return;
        }
        var menuId = $(this).attr('menuId');
        if (StringUtils.isBlank(menuId)) {
          return;
        }
        var menuItem = _self.menuItemMap[menuId];
        if (menuItem == null) {
          // 主题判断处理
          var theme = _self.themeMap[menuId];
          if (theme != null) {
            _self.setTheme(theme.id);
          }
          return;
        }
        var opt = {};
        var selectionParams = $(this).data('selectionParams') || {};
        if (menuItem.eventType) {
          if (menuItem.eventType !== event.type) {
            return;
          }
          var handler = menuItem.eventHandler;
          var eventParams = menuItem.eventParams || {};
          var target = menuItem.target;
          opt = {
            target: target.position,
            targetWidgetId: target.widgetId,
            refreshIfExists: target.refreshIfExists,
            appType: handler.type,
            appPath: handler.path,
            params: $.extend({}, eventParams.params, appContext.parseEventHashParams(handler, 'menuid'), selectionParams),
            event: event,
            selection: menuId
          };
        } else {
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
          opt = {
            menuId: menuId,
            menuItem: menuItem,
            target: options.targetPosition,
            targetWidgetId: options.targetWidgetId,
            refreshIfExists: refreshIfExists,
            appType: appType,
            appPath: appPath,
            params: $.extend(eventParams, appContext.parseEventHashParams(menuItem, 'menuid'), selectionParams),
            event: event,
            onPrepare: {}
          };
          if (targetPosition) {
            opt.target = targetPosition;
          }
          if (targetWidgetId) {
            opt.targetWidgetId = targetWidgetId;
          }
        }
        _self.startApp(opt);
        $('body, html').animate({ scrollTop: 0 }, 500);

        // if(_self.subNavWrap) {
        //    _self.subNavWrap.hide();
        // } else {
        // 	_self.element.find('.single-sub-nav-wrap').hide();
        // }
      };
      // 点击事件
      $(_self.element).on('click', 'li.nav-menu-item,li.sub-nav-li', eventCallback);
      if (_self.activeItem) {
        $("[menuid='" + _self.activeItem + "']", $(_self.element)).trigger('click');
      }

      // 滚动事件
      $('ul.sub-nav-scrollable', _self.element).on('scroll', function () {
        var $this = $(this);
        _self._setScrollButtonsStatus($this);
      });

      var navTimeOut;
      $('.ui-navbar-nav li.nav-menu-item', _self.element).hover(
        function () {
          var $this = $(this);
          if (_self.subNavWrap) {
            navTimeOut = setTimeout(function () {
              _self.setSubNavHeight();

              // var _list = $('.navbar-nav .dynamic-nav');
              // if (!_list.data('width')) {
              //     _list.data('width', _list.outerWidth())
              // }
              // _list.css('width', _list.data('width') + 'px');
              _self.subNavWrap.finish().slideDown();
            }, 500);
            _self.navHover($this.attr('menuid'));
          } else {
            navTimeOut = setTimeout(function () {
              $this.find('.single-sub-nav-wrap').css('min-width', 0).finish().fadeIn();
              var _list = $this.find('.single-sub-nav-list');
              if (!_list.data('width')) {
                _list.data('width', _list.outerWidth());
              }
              _list.css('width', _list.data('width') + 'px');
            }, 250);
          }
        },
        function (e) {
          var $this = $(this);
          if (_self.subNavWrap) {
            if (
              ($(e.relatedTarget).hasClass('nav-menu-item') && $(e.relatedTarget).parent().is($('.ui-navbar-nav', _self.element))) ||
              (e.relatedTarget.offsetParent &&
                $(e.relatedTarget.offsetParent).hasClass('nav-menu-item') &&
                $(e.relatedTarget.offsetParent).parent().is($('.ui-navbar-nav', _self.element)))
            ) {
              return;
            }
            $this.removeClass('hover').siblings().removeClass('hover');
            _self.subNavWrap.finish().slideUp();
            clearTimeout(navTimeOut);
          } else {
            $this
              .find('.single-sub-nav-wrap')
              .removeClass('direction-right')
              .css({ right: 'auto', height: 'auto' })
              .finish()
              .fadeOut('fast', function () {
                $this.find('.single-sub-nav-wrap').hide();
              });
            $this.find('.single-sub-nav-list').css('height', 'auto');
            $this.find('.single-sub-nav-list ul').css('height', 'auto');
            clearTimeout(navTimeOut);
          }
        }
      );

      if (configuration.navBarStyle !== 'single') {
        if (_self.subNavWrap) {
          _self.subNavWrap.find('.sub-nav-list').hover(function (e) {
            _self.navHover($(this).attr('menuid'));
          });

          _self.subNavWrap
            .mouseenter(function () {
              $(this).stop();
            })
            .mouseleave(function (e) {
              //todo
              if (
                $(e.relatedTarget).hasClass('nav-menu-item') ||
                (e.relatedTarget && e.relatedTarget.offsetParent && $(e.relatedTarget.offsetParent).hasClass('nav-menu-item'))
              ) {
                return;
              }
              $(this).css('height', 'auto').finish().slideUp().find('.sub-nav-list').css('height', 'auto');
              $(this).prev().find('.nav-menu-item').removeClass('hover');
            });
        }
      }
      _self.$element
        .on('mouseenter', '.sub-nav-li', function (e) {
          e.stopPropagation();
          var $this = $(this);
          var $dynamicNav = $this.children('.dynamic-nav');
          if ($dynamicNav.length) {
            $dynamicNav.css('height', 'auto');
            if (_self.subNavWrap) {
              _self.setSubNavHeight($dynamicNav.outerHeight());
            }
            if ($(e.fromElement).hasClass('dynamic-nav')) {
              _self.setSingleNavListWidth($(e.fromElement).parent(), 'minus');
            }
            if ($(e.fromElement).children('.dynamic-nav').length > 0) {
              _self.setSingleNavListWidth($(e.fromElement).parent(), 'minusDep');
            }
            _self.setSubNavPosition($this, $dynamicNav);
            _self.setSingleNavListWidth($this, 'noList');
          } else {
            _self.setSingleNavListWidth($this, 'noList');
          }
        })
        .on('mouseleave', '.sub-nav-li', function (e) {
          e.stopPropagation();
          var $this = $(this);
          if (!_self.subNavWrap) {
            if ($(e.toElement).parent().is($(e.fromElement).parent())) {
              if ($(e.fromElement).find('.dynamic-nav').length > 0) {
                _self.setSingleNavListWidth($(e.fromElement), 'minus');
              }
              return;
            }
            _self.setSingleNavListWidth($(e.toElement), 'noList');
          }
        });

      var scrollAction;
      // hover action
      _self.$element
        .on('mouseenter', '.btn-li .move-up', function (e) {
          e.stopPropagation();
          var $this = $(this);
          scrollAction = _self._scrollSubNav($this.closest('.sub-nav-list'), 'up');
        })
        .on('mouseleave', '.btn-li .move-up', function (e) {
          e.stopPropagation();
          if (scrollAction) clearInterval(scrollAction);
        });

      _self.$element
        .on('mouseenter', '.btn-li .move-down', function (e) {
          e.stopPropagation();
          var $this = $(this);
          scrollAction = _self._scrollSubNav($this.closest('.sub-nav-list'), 'down');
        })
        .on('mouseleave', '.btn-li .move-down', function (e) {
          e.stopPropagation();
          if (scrollAction) clearInterval(scrollAction);
        });

      // 鼠标移动
      var mouseover = constant.getValueByKey(constant.EVENT_TYPE, 'MOUSE_OVER');
      var defaultSelectedItems = [];
      var menuItems = [];
      menuItems = menuItems.concat(options.subNav.menuItems);
      menuItems = menuItems.concat(options.toolBar.menuItems);
      $.each(menuItems, function (i, menu) {
        if (menu.defaultSelected === '1') {
          defaultSelectedItems.push(menu);
        }
        if (menu.eventType !== mouseover) {
          return;
        }
        var menuId = menu.uuid;
        var selector = 'li.nav-menu-item[menuId="' + menuId + '"]';
        $(selector, _self.element).hoverDelay({
          hoverEvent: eventCallback
        });
      });

      // 选中元素，menuid或menu对应的eventHandler的id
      var selection = Browser.getQueryString('selection');
      if (StringUtils.isNotBlank(selection)) {
        // 1、menuid
        var selector = 'li.nav-menu-item[menuId="' + selection + '"]';
        if ($(selector, _self.element).length > 0) {
          $(selector, _self.element).addClass('active');
        } else {
          // 2、menu对应的eventHandler的id
          for (var key in _self.menuItemMap) {
            var menuItem = _self.menuItemMap[key];
            var eh = menuItem.eventHandler;
            if (menuItem && eh && eh.id === selection) {
              var miSelector = 'li.nav-menu-item[menuId="' + menuItem.uuid + '"]';
              $(miSelector, _self.element).addClass('active');
            }
          }
        }
      } else {
        // 默认选中
        $.each(defaultSelectedItems, function (i, menu) {
          var selector = 'li.nav-menu-item[menuId="' + menu.uuid + '"]';
          $(selector, _self.element).addClass('active');
        });
      }

      // 页面加载完成后，调用获取数量的模块取数量
      var pageContainer = _self.pageContainer;
      pageContainer.on(constant.WIDGET_EVENT.PageContainerCreationComplete, function (e, ui) {
        if (_self.pageContainer !== ui) {
          return;
        }
        $.each(menuItems, function (i, nav) {
          // 获取导航徽章数量
          _self._getBadgetCount(nav);

          // 监听组件变化
          if ($.isEmptyObject(nav.badge)) {
            return;
          }
          var listViewId = nav.badge.widgetDefId;
          if (StringUtils.isBlank(listViewId)) {
            return;
          }
          var listViewWidget = appContext.getWidgetById(listViewId);
          if (listViewWidget != null) {
            listViewWidget.on(constant.WIDGET_EVENT.Change, function (e, ui) {
              var totalCount = ui.getDataProvider().getCount();
              _self._setBadgetCount(nav, totalCount);
            });
          }
        });
      });
      // 微章刷新事件
      pageContainer.on(constant.WIDGET_EVENT.BadgeRefresh, function () {
        _self.refreshBadge();
      });
      pageContainer.on(constant.WIDGET_EVENT.MenuItemSelect, function (e) {
        if (e.detail) {
          var ids = typeof e.detail == 'string' ? e.detail.split(/,|;/) : e.detail;
          for (var i = 0; i < ids.length; i++) {
            var $m = $(_self.element).find("[menuid='" + ids[i] + "']");
            if ($m.length) {
              $(_self.element).find('[menuid].active').removeClass('active');
              if ($m.is('.sub-nav-li')) {
                $m.parents('.nav-menu-item').addClass('active');
              } else {
                $m.addClass('active');
              }
              break;
            }
          }
        }
      });
    },
    // 获取导航徽章数量
    _getBadgetCount: function (nav) {
      var _self = this;
      if (!$.isEmptyObject(nav.badge) && nav.badge !== '') {
        var countJsModule = nav.badge.countJsModule;
        if (nav.badge.countWay === 'countJs') {
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
        } else {
          _self._badgeCountDataProvider = new DataStore({
            dataStoreId: nav.badge.dataStoreCounter,
            params: {},
            onDataChange: function (data, count) {
              if (count != -1) {
                _self._setBadgetCount(nav, count);
              }
            }
          });
          _self._badgeCountDataProvider.getCount(true);
        }
      }
    },
    _setBadgetCount: function (nav, count, realCount) {
      var _self = this;
      var selector = 'li.nav-menu-item[menuId="' + nav.uuid + '"]';
      var $nav = $(selector, _self.element).children('a');
      var $badge = $nav.children('span.badge');
      if ($badge.length == 0) {
        $badge = $('<span class="badge">' + count + '</span>');
        $nav.append($badge);
      } else {
        $badge.html(count);
      }
    },
    _getBodyHeight: function () {
      // var bodyHeight = $(document.body).outerHeight();
      var bodyHeight = document.body.clientHeight;
      // var fixedHeaderHeight =  $('.ui-navbar-nav').offset().top + $('.ui-navbar-nav').height();
      var fixedHeaderHeight = 131;
      return bodyHeight - fixedHeaderHeight;
    },
    _scrollSubNav: function ($subNav, direction) {
      var _self = this;
      var scrolling = setInterval(function () {
        var scrollTop = $subNav.scrollTop();
        $subNav.scrollTop(scrollTop + (direction === 'down' ? 3 : -3));
        _self._setScrollButtonsStatus($subNav);
      }, 10);
      return scrolling;
    },
    _setScrollButtonsStatus: function ($subNav) {
      var scrollTop = $subNav.scrollTop();
      var navbarInnerHeight = $subNav.innerHeight();
      var navbarScrollHeight = $subNav.prop('scrollHeight');

      // 控制是否允许向下滚动
      if (scrollTop + navbarInnerHeight < navbarScrollHeight - 1) {
        $subNav.find('.btn-li .move-down').removeClass('disabled');
      } else {
        $subNav.find('.btn-li .move-down').addClass('disabled');
      }

      // 控制是否允许向上滚动
      if (scrollTop > 0) {
        $subNav.find('.btn-li .move-up').removeClass('disabled');
      } else {
        $subNav.find('.btn-li .move-up').addClass('disabled');
      }
    },
    _getOverflowItemsCount: function () {
      var bodyHeight = document.body.clientHeight;
      // var fixedHeaderHeight = $('.ui-navbar-nav').offset().top + $('.ui-navbar-nav').height();
      var fixedHeaderHeight = 131;
      var navWrapHeight = bodyHeight - fixedHeaderHeight;

      var navItemHeight = this.options.widgetDefinition.configuration.navBarStyle === 'single' ? 40 : 30;
      var _count = Math.ceil((navWrapHeight - 20) / navItemHeight);
      return _count;
    },
    // 刷新徽章
    refreshBadge: function () {
      var _self = this;
      var menuItemMap = _self.menuItemMap;
      $.each(menuItemMap, function () {
        _self._getBadgetCount(this);
      });
    }
  });
});
