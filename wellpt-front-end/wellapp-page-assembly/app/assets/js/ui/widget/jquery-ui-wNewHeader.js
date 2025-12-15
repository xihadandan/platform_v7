(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'constant', 'commons', 'server', 'appContext', 'appModal', 'dataStoreBase', 'layDate'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($, constant, commons, server, appContext, appModal, DataStore, layDate) {
  'use strict';
  var Browser = commons.Browser;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var SpringSecurityUtils = server.SpringSecurityUtils;
  var dateUtils = commons.DateUtils;

  var presentOnlinePersonNumber = 0;

  var sTimer = null;
  var timer = null;

  $.widget('ui.wNewHeader', $.ui.wWidget, {
    options: {
      // 组件定义
      widgetDefinition: {},
      // 上级容器定义
      containerDefinition: {}
    },
    constant: {
      NAV_TYPE: {
        MAIN_NAV: 'mainNav',
        RIGHT_NAV: 'rightNav'
      }
    },
    _createNavItem: function (index, uuid, iconClass, text, defaultSelected) {
      // 组装当前LI项
      var navTpl = '<li class="nav-menu-item item-' + index + '" menuId="' + uuid + '" title="' + text + '" >';
      if (defaultSelected) {
        navTpl = '<li class="nav-menu-item active item-' + index + '" menuId="' + uuid + '">';
      }
      if (iconClass) {
        if (uuid === 'onlinePerson') {
          navTpl += '<a><i class="ui-wIcon ' + iconClass + '" ></i><span class="nav-text" >' + text + '</span></a></li>';
        } else if (text || uuid === 'personInfo') {
          navTpl += '<a><i class="ui-wIcon ' + iconClass + '"></i><span class="nav-text">' + text + '</span></a></li>';
        } else {
          navTpl += '<a><i class="ui-wIcon ' + iconClass + '"></i></a></li>';
        }
      } else {
        if (uuid === 'onlinePerson') {
          navTpl += '<a><span class="nav-text" id="online-person-number">' + text + '</span></a></li>';
        } else if (text || uuid === 'personInfo') {
          navTpl += '<a><span class="nav-text">' + text + '</span></a></li>';
        } else {
          navTpl += '<a></a></li>';
        }
      }
      return navTpl;
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
      // 渲染导航区块一（基本信息、一级导航）及导航区块二（二级导航、导航工具栏）包装层
      _self.$elementContainer = $('.ui-wHeader-mainNavbar .mainnav-content', _self.$element);
      // 渲染基本信息
      _self.renderBaseInfo(options);
      // 渲染主导航
      _self.renderMainNav(options);
      // 渲染右侧导航
      _self.renderRightNav(options);
    },
    // 渲染基本信息
    renderBaseInfo: function (options) {
      var _self = this;
      if (options.logoFilePath) {
        var logoSrc = (options.logoFilePath || '') + '';
        if (options.logoFilePath.substr(0, 4) !== 'data') {
          // 非base64格式图片
          var _auth = getCookie('_auth');
          var token = getCookie(_auth);
          logoSrc += '&' + _auth + '=' + token;
        }

        var image = ' <img src="' + logoSrc + '" alt=""  />';
        $('.ui-wHeader-logo', _self.element).append(image);
      }
      if (options.systemTitle) {
        var systemTitle = '<b>' + (options.systemTitle ? options.systemTitle : '') + '</b>';
        $('.ui-wHeader-title', _self.element).append(systemTitle);
      }
      if (options.contentWidth) {
        $('.header-content', _self.element).css({
          width: options.contentWidth + 'px',
          margin: '0 auto'
        });
      }
      if (options.navType != 0) {
        $(_self.element).addClass('wheader-type' + options.navType);
      } else {
        $(_self.element).addClass('wHeader-default');
      }
      if (options.fixedTop !== '0') {
        $(document.body).addClass('fixed-header');
      }
    },
    //渲染主导航
    renderMainNav: function (options) {
      var _self = this;
      var mainNav = options.mainNav;
      if (!mainNav) {
        return;
      }
      var menuItems = mainNav.menuItems;
      if (!menuItems || menuItems.length == 0) {
        return;
      }
      var sb = new StringBuilder();
      // options.mainNavShowCount = 3;
      // 处理LI存放位置
      var navShowCount = parseInt(options.mainNavShowCount) ? parseInt(options.mainNavShowCount) : 5;
      var navItemCount = 0;
      var navHasShowCount = 0;
      var moreLi = null;
      var moreNavs = [];
      _self.selectmenuid = commons.Browser.getQueryString('menuid'); //通过浏览器地址参数指定要选中的导航菜单
      if (_self.selectmenuid) {
        _self.selectmenuid = _self.selectmenuid.split(',');
      }
      if (options.params && options.params.menuid) {
        // 通过事件参数传进来的
        _self.selectmenuid = options.params.menuid;
      }
      for (var i = 0; i < menuItems.length; i++) {
        var defaultSelected = false;
        var nav = menuItems[i];
        _self.menuItemMap[nav.uuid] = nav;
        if (nav.hidden === '1') {
          continue;
        }
        if (nav.defaultSelected === '1' || (_self.selectmenuid && _self.selectmenuid.indexOf(nav.uuid) != -1)) {
          defaultSelected = true;
          _self._MainNavDefaultEvent(nav);
        }

        var text = nav.text;
        var iconClass = nav.iconClass;
        if (navHasShowCount < navShowCount || _self.isDefaultNav(nav)) {
          if (nav.icon) {
            iconClass = nav.icon.className;
          }
          // sb.appendFormat(navTpl, navHasShowCount, nav.uuid, nav.iconClass, nav.text);
          var navItem = _self._createNavItem(navItemCount++, nav.uuid, iconClass, text, defaultSelected);
          sb.append(navItem);
        } else {
          if (moreLi == null) {
            var moreNav = {};
            moreNav.text = '更多';
            moreNav.iconClass = 'iconfont icon-ptkj-gengduocaidan';
            moreLi = _self.getDropdown(moreNav, [], 'wHeaderNavMore1', 'wHeaderNavMoreUL');
            sb.append(moreLi);
          }
          moreNavs.push(nav);
        }
        navHasShowCount++;
      }

      $('.ui-wHeader-mainNav', _self.element).append(sb.toString());
      sb = new StringBuilder();
      $.each(moreNavs, function (i, nav) {
        if (nav.icon) {
          nav.iconClass = nav.icon.className;
        }
        var navItem = _self._createNavItem(navItemCount++ + ' item-more', nav.uuid, nav.iconClass, nav.text);
        sb.append(navItem);
      });
      $('.wHeaderNavMoreUL', '.ui-wHeader-mainNav').append(sb.toString());
    },

    _MainNavDefaultEvent: function (menuItem) {
      var _self = this;
      var handler = menuItem.eventHandler;
      var eventParams = menuItem.eventParams || {};
      var target = menuItem.target;
      var opt = {
        target: target.position,
        targetWidgetId: target.widgetId,
        eventTarget: target,
        refreshIfExists: target.refreshIfExists,
        appType: handler.type,
        appPath: handler.path,
        appName: handler.name,
        params: $.extend({}, eventParams.params, appContext.parseEventHashParams(handler, 'menuid')),
        event: event,
        selection: menuItem.uuid
      };
      _self.startApp(opt);
      $('body, html').animate(
        {
          scrollTop: 0
        },
        500
      );
    },

    // 渲染右侧导航
    renderRightNav: function (options) {
      var _self = this;
      var rightNav = options.rightNav;
      if (!rightNav) {
        return;
      }
      var menuItems = rightNav.menuItems;
      if (!menuItems || menuItems.length == 0) {
        return;
      }
      var sb = new StringBuilder();

      // 处理LI存放位置
      var navShowCount = parseInt(options.rightNavShowCount) ? parseInt(options.rightNavShowCount) : 3;
      var navItemCount = 0;
      var navHasShowCount = 0;
      var moreLi = null;
      var moreNavs = [];
      for (var i = 0; i < menuItems.length; i++) {
        var nav = menuItems[i];
        _self.menuItemMap[nav.uuid] = nav;
        if (nav.hidden === '1') {
          continue;
        }
        var text = nav.hiddenName === '0' ? nav.text : '';
        var iconClass = nav.iconClass;
        // 显示主题设置
        // if (nav.uuid === 'theme') {
        //   var allThemes = appContext.getAllThemes();
        //   _self.themeMap = {};
        //   $.each(allThemes, function (i, theme) {
        //     _self.themeMap[theme.id] = theme;
        //   });
        //   var themeSwitcher = 'wHeaderNav-Theme-Switcher';
        //   var themeThemes = 'wHeaderNav-Themes';
        //   var themeLi = _self.getDropdown(nav, allThemes, themeSwitcher, themeThemes);
        //   sb.appendFormat(themeLi);
        //   continue;
        // } else
        if (nav.uuid === 'personInfo') {
          text = '';
          setTimeout(function () {
            $("li[menuid='personInfo'] .ui-wIcon", _self.element).hide();
            $("li[menuid='personInfo'] .ui-wIcon", _self.element).after(
              "<img src='" +
                '/proxy/org/user/view/photo/user/' +
                getCookie('cookie.current.userId') +
                "' class='nav-avatar' onerror='javascript:this.style.display=\"none\";this.previousElementSibling.style.display=\"inline-block\";'/>"
            );
            $("li[menuid='personInfo'] .nav-text", _self.element).append(getCookie('cookie.current.userName'));
            HeaderDefaultEvent.personInfoEvent();
          }, 100);
        } else if (nav.uuid === 'switchAccount') {
          setTimeout(function () {
            HeaderDefaultEvent.switchAccountEvent($("li[menuid='switchAccount']", _self.element));
          }, 100);
        } else if (nav.uuid === 'onlinePerson') {
          var _html =
            '<div class="logining-list-contain"><div class="searchable-block">' +
            '<span class="search-icon"><i class="iconfont icon-ptkj-sousuochaxun"></i></span><input placeholder="输入关键字" id="input_value" /><button class="well-btn w-btn-primary" id="search_login">查询</button>' +
            '</div><table id="logining_table" data-toggle="table"></table></div>';

          var pageIndex = 1;
          var pageSize = 100;
          setTimeout(function () {
            // 在线人数绑定事件
            $("li[menuid='onlinePerson'] .nav-text", _self.element)
              .off('click')
              .on('click', function () {
                var title = `在线人员列表<span style="font-size:14px;">（在线人数<span style="" class="onlinePersonN"></span>/总人数<span class="personTotalN"></span>人）</span><i class="iconfont icon-ptkj-tishishuoming" id="tishi"></i>`;
                var $dialog = top.appModal.dialog({
                  title: title,
                  message: _html,
                  width: '725px',
                  height: '828px',
                  size: 'large',
                  shown: function () {
                    _self.getLoginingListData('', 1, pageSize, undefined, function (tableData, total) {
                      _self.initLoginingTable(tableData, $dialog, 1);
                      presentOnlinePersonNumber = total;
                      $('.onlinePersonN', $dialog).html(presentOnlinePersonNumber);
                    });

                    // 获取总数
                    _self.getSystemUserTotal($dialog);

                    // 去掉选中状态
                    $("li[menuid='onlinePerson']", _self.element).removeClass('active');

                    // 绑定事件
                    $('#search_login', $dialog)
                      .off('click')
                      .on('click', function () {
                        pageIndex = 1;
                        var value = $('#input_value', $dialog).val() || '';
                        _self.getLoginingListData(value, pageIndex, pageSize, undefined, function (tableData) {
                          _self.initLoginingTable(tableData, $dialog, 1);
                        });

                        // $('#logining_table', $dialog).bootstrapTable('refreshOptions', { data: tableData });
                      });
                    $('#input_value', $dialog)
                      .off()
                      .on('keypress', function (e) {
                        if (e.keyCode == 13) {
                          pageIndex = 1;
                          var value = $(this).val() || '';
                          _self.getLoginingListData(value, pageIndex, pageSize, undefined, function (tableData) {
                            _self.initLoginingTable(tableData, $dialog, 1);
                          });
                        }
                      });
                    $('#tishi', $dialog).popover({
                      container: false,
                      content: '总人数：账号状态为非禁用状态的用户总数',
                      placement: 'bottom',
                      trigger: 'hover',
                      template:
                        '<div class="popover" role="tooltip"><div class="arrow"></div><div class="popover-content" style="color:#999;"></div></div>'
                    });
                  },
                  buttons: {
                    refresh: {
                      label: '刷新',
                      className: 'well-btn w-btn-primary',
                      callback: function () {
                        pageIndex = 1;
                        presentOnlinePersonNumber = 0;
                        // console.log('点击刷新', presentOnlinePersonNumber);
                        $('#input_value', $dialog).val('');

                        $('#logining_table', $dialog).bootstrapTable('removeAll');
                        // 清空延时器
                        clearTimeout(sTimer);
                        clearTimeout(timer);

                        _self.getLoginingListData('', pageIndex, pageSize, undefined, function (tableData) {
                          _self.initLoginingTable(tableData, $dialog, pageIndex, true);

                          _self.$element
                            .find('.navbar-right li[menuid=onlinePerson]')
                            .find('a .nav-text')
                            .html(presentOnlinePersonNumber + '人在线');

                          // 总人数刷新
                          // _self.getSystemUserTotal();
                        });

                        return false;
                      }
                    },
                    close: {
                      label: '关闭',
                      className: 'well-btn w-btn-default',
                      callback: function () {}
                    }
                  }
                });
              });
            // 图标绑定刷新
            $("li[menuid='onlinePerson'] i", _self.element)
              .off('click')
              .on('click', function () {
                _self.getLoginingListData('', 1, 100, true);

                // 去掉选中状态
                $("li[menuid='onlinePerson']", _self.element).removeClass('active');
              });

            // 得到在线总人数
            _self.getLoginingListData('', 1, 100, true);
          }, 100);
        }

        if (navHasShowCount < navShowCount || _self.isDefaultNav(nav)) {
          if (nav.icon) {
            iconClass = nav.icon.className;
          }
          // sb.appendFormat(navTpl, navHasShowCount, nav.uuid, nav.iconClass, nav.text);
          var navItem = _self._createNavItem(navItemCount++, nav.uuid, iconClass, text);
          sb.append(navItem);
        } else {
          if (moreLi == null) {
            var moreNav = {};
            moreNav.text = '更多';
            moreNav.iconClass = 'iconfont icon-ptkj-gengduocaidan';
            moreLi = _self.getDropdown(moreNav, [], 'wHeaderNavMore', 'wHeaderNavMoreUL');
            sb.append(moreLi);
          }
          moreNavs.push(nav);
        }

        // 主题设置，个人信息，我的消息，退出登录不参与显示限制
        if (!_self.isDefaultNav(nav)) {
          navHasShowCount++;
        }
      }

      $('.ui-wHeader-rightNav', _self.element).append(sb.toString());
      sb = new StringBuilder();

      var maxLen = 0;
      $.each(moreNavs, function (i, nav) {
        if (nav.icon) {
          nav.iconClass = nav.icon.className;
        }
        var text = nav.hiddenName === '0' ? nav.text : '';
        var navItem = _self._createNavItem(navItemCount++ + ' item-more', nav.uuid, nav.iconClass, text);
        sb.append(navItem);

        var _len = text.length * 14 + (nav.icon ? 18 : 0) + 40;
        maxLen = maxLen > _len ? maxLen : _len;
      });
      $('.wHeaderNavMoreUL', '.ui-wHeader-rightNav')
        .css('min-width', maxLen + 'px')
        .append(sb.toString());

      // 搜索框
      if (options.userSearch && options.userSearch == '1') {
        var html =
          "<li class='nav-menu-item' menuid='globalSearch' id='globalSearch'><a><i class='ui-wIcon iconfont icon-ptkj-sousuochaxun'></i></a></li>";
        $('.ui-wHeader-rightNav', _self.element).prepend(html);

        _self.renderSearchContainer(options);
      }
    },
    // 获取系统用户总数
    getSystemUserTotal: function ($dialog) {
      var _this = this;
      $.ajax({
        url: '/proxy/api/org/user/account/countUnforbiddenAccount',
        type: 'get',
        data: {
          systemUnitId: SpringSecurityUtils.getCurrentUserUnitId() == 'S0000000000' ? '' : SpringSecurityUtils.getCurrentUserUnitId()
        },
        success: function (result) {
          if (result.code == 0) {
            $('.personTotalN', $dialog).html(result.data);
          }
        },
        error: function (err) {}
      });
    },

    // 初始化在线人员表格
    initLoginingTable: function (data, $dialog, pageNumber, isRefresh) {
      var _data = data || [];
      var _self = this;
      $('#logining_table', $dialog)
        .bootstrapTable('destroy')
        .bootstrapTable({
          data: _data,
          striped: true,
          width: 683,
          height: 596,
          undefinedText: null,
          theadClasses: 'thead-dark',
          sortOrder: 'desc',
          sortName: 'loginTime',
          // height: 300,
          columns: [
            {
              field: 'userName',
              title: '姓名',
              align: 'center'
            },
            {
              field: 'loginName',
              title: '账号名',
              align: 'center'
            },
            {
              field: 'mainJobName',
              title: '职位'
            },
            {
              field: 'mainDeptName',
              title: '部门'
            },
            {
              field: 'loginTime',
              title: '登录时间',
              width: '168'
            }
          ]
        });
      if (_data.length == 0) {
        return;
      }
      // 绑定滚动事件
      var pageNumber = pageNumber || 1;
      var value = $('#input_value', $dialog).val();
      var newData = [];

      var scrollFn = null;

      var pageTotal = Math.ceil(presentOnlinePersonNumber / 100);

      var isTrue = true;

      var oldPageIndex = 1;

      // console.log('目前在线总人数', presentOnlinePersonNumber);
      // console.log('总页数', pageTotal);

      if (sTimer) {
        clearTimeout(sTimer);
      }
      sTimer = setTimeout(function () {
        scrollFn = function () {
          $('.fixed-table-body', $dialog)
            .unbind('scroll')
            .bind(
              'scroll',

              _.debounce(
                function (e) {
                  var nScrollHeight = 0; // 滚动条距离总长
                  var nScrollTop = 0; // 滚动的距离
                  var nDivHeight = $('.fixed-table-body', $dialog).height();
                  nScrollHeight = $(this)[0].scrollHeight;
                  nScrollTop = Math.ceil($(this)[0].scrollTop);
                  var flagHeight = Math.ceil(nDivHeight + nScrollTop);

                  // console.log('pageNumber===', pageNumber);

                  // console.log(nScrollHeight, nScrollTop, nDivHeight);

                  if (pageNumber < pageTotal) {
                    if (nScrollHeight - nScrollTop <= nDivHeight && isTrue) {
                      console.log('到底了');
                      pageNumber++;
                      isTrue = false;
                      _self.getLoginingListData(value || '', pageNumber, 100, undefined, function (list) {
                        newData = list;
                        if (newData.length > 0) {
                          isTrue = true;
                          $('#logining_table', $dialog).bootstrapTable('append', newData);
                          $('#logining_table', $dialog).bootstrapTable('resetView');
                          if (timer) {
                            clearTimeout(timer);
                          }
                          timer = setTimeout(function () {
                            scrollFn();
                          }, 300);
                        }
                      });
                    }
                  }
                },
                500,
                {
                  leading: false,
                  trailing: true
                }
              )
            );
        };
        scrollFn();
      }, 200);

      // $('#logining_table', $dialog)
      //   .off()
      //   .on('post-body.bs.table', function (data) {
      //     if (timer) {
      //       clearTimeout(timer);
      //     }
      //     timer = setTimeout(function () {
      //       console.log(pageNumber);
      //       scrollFn ? scrollFn() : null;
      //       // scrollFn();
      //     }, 500);
      //   });
    },

    // 获取在线人员列表数据
    getLoginingListData: function (keyword, pageIndex, pageSize, isHome, asyncCallback) {
      var list = [];
      $.ajax({
        url: ctx + '/getLoginingUser',
        type: 'post',
        dataType: 'json',
        contentType: 'application/json',
        data: JSON.stringify({
          keyword: keyword,
          page: {
            pageIndex: pageIndex,
            pageSize: pageSize
          }
        }),
        async: true,
        success: function (result) {
          list = result.data.map(function (item) {
            item.loginTime = dateUtils.getDateStrByDateAndFormat(new Date(item.loginTime), 'yyyy-MM-dd HH:mm:ss');
            return item;
          });

          if (isHome) {
            $('.navbar-right li[menuid=onlinePerson] a .nav-text').html(result.total + '人在线');
          } else {
            presentOnlinePersonNumber = result.total;
            $('.onlinePersonN').html(presentOnlinePersonNumber);
          }
          if (asyncCallback != undefined) {
            asyncCallback(list, result.total);
          }
        },
        error: function (err) {}
      });
      //return list;
    },
    // 渲染全文检索内容
    renderSearchContainer: function (options) {
      var html = '';
      html +=
        "<div class='ui-wHeader-searchNav'>" +
        "<div class='ui-wHeader-search'>" +
        "<input type='text' id='searchKeyword' placeholder='" +
        (options.searchPlaceholder || '') +
        "'>" +
        "<i class='iconfont icon-ptkj-dacha-xiao'></i>" +
        "<i class='iconfont icon-ptkj-sousuochaxun'></i>" +
        '</div>' +
        '</div>';

      $('.header-content', this.element).append(html);
      setTimeout(function () {
        var baseWidth = $('.ui-wHeader-baseInfo', this.element).width();
        var width = $('.header-content', this.element).width() - baseWidth - $('.ui-wHeader-rightNav', this.element).width() - 60;
        if (options.userSearchShow == '1') {
          //搜索框默认显示
          width = width - $('.ui-wHeader-mainNav', this.element).width(); //输入框大小应再减去主导航宽度
          baseWidth = baseWidth + $('.ui-wHeader-mainNav', this.element).width(); //输入框左位移应再加上主导航宽度
          $('.ui-wHeader-searchNav', this.element).show();
          $('#globalSearch', this.element).hide();
        }
        $('.ui-wHeader-searchNav', this.element)
          .css({
            left: baseWidth + 'px'
          })
          .width(width);
      }, 100);
    },

    isDefaultNav: function (nav) {
      return (
        // nav.uuid === 'theme' ||
        nav.uuid === 'personInfo' || nav.uuid === 'myMsg' || nav.uuid === 'loginOut' || nav.uuid === 'logOut' || nav.uuid === 'workbench'
      );
    },
    getDropdown: function (nav, menus, navClass, menuClass) {
      var sb = new StringBuilder();
      if (nav.icon) {
        nav.iconClass = nav.icon.className;
      }
      var text = nav.hiddenName === '0' ? nav.text : '';
      sb.appendFormat('<li class="dropdown-wrap nav-menu-item {0}">', navClass);
      sb.appendFormat('<a><i class="ui-wIcon {0}"></i>', nav.iconClass, text);
      if (text) {
        sb.appendFormat('<span class="nav-text">{0}</span>', text);
      }
      sb.appendFormat('</a><ul class="dropdown-ul {0}">', menuClass);
      $.each(menus, function (i, menu) {
        var li = '<li class="nav-menu-item" menuId="{0}"><span class="nav-text">{1}</span></li>';
        sb.appendFormat(li, menu.id, menu.name);
      });
      // 滚动按钮
      sb.append('<li class="nav-scroll-btn-li">');
      sb.append('<button class="move-up disabled"><span class="icon iconfont icon-ptkj-shixinjiantou-shang"></span></button>');
      sb.append('<button class="move-down"><span class="icon iconfont icon-ptkj-shixinjiantou-xia"></span></button>');
      sb.append('</li>');

      sb.append('</ul>');
      sb.append('</li>');
      return sb.toString();
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
    _setEvent: function (options) {
      var _self = this;
      var eventCallback = function (event) {
        var $this = $(this);
        $('li.nav-menu-item', _self.element).removeClass('active');
        $this.addClass('active');
        var menuId = $this.attr('menuId');
        if (StringUtils.isBlank(menuId) || $.inArray(menuId, ['personInfo', 'myMsg', 'logOut', 'loginOut']) > -1) {
          return;
        }
        var menuItem = _self.menuItemMap[menuId];
        if (menuItem == null) {
          // 主题判断处理
          // var theme = _self.themeMap[menuId];
          // if (theme != null) {
          //   _self.setTheme(theme.id);
          // }
          return;
        }
        if (menuItem.eventType !== event.type) {
          return;
        }
        // 锚点选择传入的参数
        var selectionParams = $this.data('selectionParams') || {};
        var handler = menuItem.eventHandler;
        var eventParams = menuItem.eventParams || {};
        var target = menuItem.target;
        var opt = {
          target: target.position,
          targetWidgetId: target.widgetId,
          eventTarget: target,
          refreshIfExists: target.refreshIfExists,
          appType: handler.type,
          appPath: handler.path,
          appName: menuItem.text,
          params: $.extend({}, eventParams.params, appContext.parseEventHashParams(handler, 'menuid'), selectionParams),
          event: event,
          selection: menuId
        };
        _self.startApp(opt);
        $('body, html').animate(
          {
            scrollTop: 0
          },
          500
        );
      };

      HeaderDefaultEvent.defaultEvent.apply(_self);

      if (_self.menuItemMap['myMsg']) {
        if (_self.menuItemMap['myMsg'].eventHandler && _self.menuItemMap['myMsg'].eventHandler.id == '246174de446c31abe74fb2285e1ffcd9') {
          UserMsgEvent.defaultEvent.apply(_self);
        } else {
          DefaultMsgEvent.defaultEvent.apply(_self);
        }
      }

      // 点击事件
      $(_self.element).on('click', 'li.nav-menu-item', eventCallback);

      // 鼠标移动
      var mouseover = constant.getValueByKey(constant.EVENT_TYPE, 'MOUSE_OVER');
      var defaultSelectedItems = [];
      var menuItems = [];

      menuItems = menuItems
        .concat(options && options.mainNav && options.mainNav.menuItems ? options.mainNav.menuItems : [])
        .concat(options && options.rightNav && options.rightNav.menuItems ? options.rightNav.menuItems : []);
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

      $(_self.element)
        .on('mouseover', '.dropdown-wrap', function () {
          var $this = $(this);
          var $dropdown = $this.find('.dropdown-ul');
          $dropdown.css('width', $dropdown.width());
          var dropdownScrollHeight = $dropdown.prop('scrollHeight');
          var dropdownHeight = $dropdown.height();
          if (dropdownScrollHeight && dropdownScrollHeight > dropdownHeight) {
            $dropdown.addClass('scrollable');
          }
          $dropdown.show();

          // 滚动事件
          $dropdown.off('scroll').on('scroll', function () {
            var $this = $(this);
            _self._setScrollButtonsStatus($this);
          });
        })
        .on('mouseleave', '.dropdown-wrap', function () {
          var $this = $(this);
          $this.find('.dropdown-ul').hide();
        });

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
          var ids = typeof e.detail == 'string' ? e.detail.split(',|;') : e.detail;
          for (var i = 0; i < ids.length; i++) {
            var $m = $(_self.element).find("[menuid='" + ids[i] + "']");
            if ($m.length) {
              $(_self.element).find('[menuid].active').removeClass('active');
              $m.addClass('active');
              break;
            }
          }
        }
      });

      $('#globalSearch', _self.element).on('click', function (e) {
        e.stopPropagation();
        e.preventDefault();
        $(this).fadeOut(400);
        $(_self.element).find('.ui-wHeader-searchNav').fadeIn(400).find('#searchKeyword').focus();
      });

      $('.ui-wHeader-search i.icon-ptkj-dacha-xiao', _self.element).on('click', function (e) {
        e.stopPropagation();
        e.preventDefault();
        $(this).removeClass('showIcon').siblings('#searchKeyword').val('');
      });

      $('.ui-wHeader-search i.icon-ptkj-sousuochaxun', _self.element).on('click', function (e) {
        e.stopPropagation();
        e.preventDefault();
        var val = $(this).siblings('#searchKeyword').val();
        $(this).parents('.ui-wHeader-searchNav').fadeOut();
        $(_self.element).find('#globalSearch').fadeIn(400).removeClass('active');
        $('.ui-wSearchContainer').remove();
        top.$('body').addClass('fullFixedBody');
        FullTextEvent.renderSearchResult(val);
        $(this).siblings('#searchKeyword').val('');
      });

      $('#searchKeyword', _self.element).on('keyup', function (e) {
        if (e.keyCode == 13) {
          $(this).parents('.ui-wHeader-searchNav').fadeOut();
          $(_self.element).find('#globalSearch').fadeIn(400).removeClass('active');
          $('.ui-wSearchContainer').remove();
          top.$('body').addClass('fullFixedBody');
          FullTextEvent.renderSearchResult($(this).val());
          $(this).val('');
        }
        if ($(this).val() != '') {
          $(this).siblings('.icon-ptkj-dacha-xiao').addClass('showIcon');
        } else {
          $(this).siblings('.icon-ptkj-dacha-xiao').removeClass('showIcon');
        }
      });

      $(document).on('click', function (e) {
        if ($(e.target).parents('.ui-wHeader-search').length == 0 && $(e.target).parents('#globalSearch').length == 0) {
          $(_self.element).find('.ui-wHeader-searchNav').fadeOut();
          $(_self.element).find('#globalSearch').fadeIn().removeClass('active');
        }
      });

      var scrollAction;
      // hover action
      _self.$element
        .on('mouseenter', '.nav-scroll-btn-li .move-up', function (e) {
          e.stopPropagation();
          var $this = $(this);
          scrollAction = _self._scrollDropdown($this.closest('.dropdown-ul'), 'up');
        })
        .on('mouseleave', '.nav-scroll-btn-li .move-up', function (e) {
          e.stopPropagation();
          if (scrollAction) clearInterval(scrollAction);
        });

      _self.$element
        .on('mouseenter', '.nav-scroll-btn-li .move-down', function (e) {
          e.stopPropagation();
          var $this = $(this);
          scrollAction = _self._scrollDropdown($this.closest('.dropdown-ul'), 'down');
        })
        .on('mouseleave', '.nav-scroll-btn-li .move-down', function (e) {
          e.stopPropagation();
          if (scrollAction) clearInterval(scrollAction);
        });

      // 滚动事件
      _self.element.on('scroll', '.dropdown-ul', function () {
        var $this = $(this);
        _self._setScrollButtonsStatus($this);
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
          var dataStoreCounter = nav.badge.dataStoreCounter;
          if (StringUtils.isNotBlank(dataStoreCounter)) {
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
      }
    },
    _setBadgetCount: function (nav, count, realCount) {
      var _self = this;
      var selector = 'li.nav-menu-item[menuId="' + nav.uuid + '"]';
      var $nav = $(selector, _self.element).children('a');
      var $badge = $nav.children('sup.badge');
      count = realCount > 99 ? '99+' : count;
      if ($badge.length == 0) {
        var display = realCount == 0 ? 'none' : 'inline-block';
        $badge = $('<sup class="badge" style="display: ' + display + '">' + count + '</sup>');
        $nav.append($badge);
      } else {
        if (realCount == 0) {
          $badge.html(count).hide();
        } else {
          $badge.html(count).show();
        }
      }
    },
    // 刷新徽章
    refreshBadge: function () {
      var _self = this;
      var menuItemMap = _self.menuItemMap;
      $.each(menuItemMap, function () {
        _self._getBadgetCount(this);
      });
    },

    _scrollDropdown: function ($dropdown, direction) {
      var _self = this;
      var scrolling = setInterval(function () {
        var scrollTop = $dropdown.scrollTop();
        $dropdown.scrollTop(scrollTop + (direction === 'down' ? 3 : -3));
        _self._setScrollButtonsStatus($dropdown);
      }, 10);
      return scrolling;
    },
    _setScrollButtonsStatus: function ($dropdown) {
      var scrollTop = $dropdown.scrollTop();
      var navbarInnerHeight = $dropdown.innerHeight();
      var navbarScrollHeight = $dropdown.prop('scrollHeight');

      // 控制是否允许向下滚动
      if (scrollTop + navbarInnerHeight < navbarScrollHeight - 1) {
        $dropdown.find('.nav-scroll-btn-li .move-down').removeClass('disabled');
      } else {
        $dropdown.find('.nav-scroll-btn-li .move-down').addClass('disabled');
      }

      // 控制是否允许向上滚动
      if (scrollTop > 0) {
        $dropdown.find('.nav-scroll-btn-li .move-up').removeClass('disabled');
      } else {
        $dropdown.find('.nav-scroll-btn-li .move-up').addClass('disabled');
      }
    }
  });
});
