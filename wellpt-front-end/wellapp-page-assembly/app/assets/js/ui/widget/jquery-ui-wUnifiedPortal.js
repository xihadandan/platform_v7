(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define([
      'jquery',
      'commons',
      'server',
      'constant',
      'appModal',
      'dataStoreBase',
      'css!/static/css/widget/jquery-ui-wUnifiedPortal'
    ], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function (jquery, commons, server, constant, appModal, DataStore) {
  'use strict';
  var $ = jquery;
  var StringUtils = commons.StringUtils;
  var UUID = commons.UUID;
  var StringBuilder = commons.StringBuilder;
  var BootstrapTableViewGetCount = 'BootstrapTableViewGetCount';
  $.widget('ui.wUnifiedPortal', $.ui.wWidget, {
    options: {
      // 组件定义
      widgetDefinition: {},
      // 上级容器定义
      containerDefinition: {}
    },
    /**
     * 创建组件
     */
    _createView: function () {
      // 创建js数据源对象
      var _self = this;
      _self.$element = $(_self.element);
      _self.$element.parent().css({
        height: '100%'
      });
      _self._beforeRenderView();
      this.navMap = {};
      var options = $.extend({}, _self.options.widgetDefinition, _self.options.widgetDefinition.configuration);
      var initOption = options;
      var customOptions = {};
      $.ajax({
        url: ctx + '/api/user/preferences/get',
        type: 'get',
        data: {
          dataKey: options.id,
          moduleId: 'UNIFIEDPORTAL'
        },
        success: function (result) {
          if (result.code === 0) {
            customOptions = result.data && result.data.dataValue ? JSON.parse(result.data.dataValue).options : initOption;
            var newOption = _self._compareOptions(initOption, customOptions);

            _self._renderView(newOption);
            _self._setEvent(newOption);
          }
        }
      });
    },

    _beforeRenderView: function () {
      var _self = this;
      _self.invokeDevelopmentMethod('beforeRender', [_self.options, _self.getConfiguration()]);
    },

    _afterRenderView: function () {
      var _self = this;
      _self.invokeDevelopmentMethod('afterRender', [_self.options, _self.getConfiguration()]);
    },

    _renderView: function (options) {
      var html = '';
      var _self = this;
      if (options.customClass) {
        _self.$element.addClass(options.customClass);
      }

      var userInfo = server.SpringSecurityUtils.getUserDetails();
      var textAlign = options.defaultLayout == 'center' ? 'titleCenter' : '';
      html +=
        "<div class='portal-wrap'>" +
        "<div class='portal-header clearfix'>" +
        "<div class='portal-header-left'>" +
        "<img src='" +
        options.logoFilePath +
        "'>" +
        "<div class='portal-header-title'>" +
        options.name +
        '</div>' +
        '</div>' +
        "<div class='portal-header-right'>" +
        "<div class='portal-user-avatar iconfont icon-ptkj-morentouxiang'></div>" +
        "<div class='portal-user-name'>" +
        userInfo.userName +
        '</div>' +
        "<div class='portal-sign-out iconfont icon-ptkj-tuichudenglu'></div>" +
        '</div>' +
        '</div>' +
        "<div class='portal-body' style='background-image:url(" +
        (options.defaultThemes && options.defaultThemes.themeImg) +
        ")'><div>";

      for (var i = 0; i < options.nav.length; i++) {
        var nav = options.nav[i];
        if (nav.children && nav.children.length > 0) {
          if (options.showCategory) {
            html +=
              "<div class='portal-list-title " +
              textAlign +
              "'>" +
              "<img class='leftImg' src='" +
              ctx +
              "/static/js/pt/img/left.png' alt='' />" +
              "<img class='centerImg' src='" +
              ctx +
              "/static/js/pt/img/centerLeft.png' alt='' />" +
              "<div class='portal-list-title-text'>" +
              nav.name +
              '</div>' +
              "<img class='centerImg' src='" +
              ctx +
              "/static/js/pt/img/centerRight.png' alt='' />" +
              '</div>';
          }

          html += "<div class='portal-item clearfix " + textAlign + "' data-type='business' data-index='" + i + "'>";
          if (nav.children.length > 0) {
            for (var j = 0; j < nav.children.length; j++) {
              var navChild = nav.children[j].data;
              _self.navMap[navChild.uuid] = navChild;
              if (navChild.defaultShow == true) {
                html +=
                  "<div class='portal-item-box business-link' data-id='" +
                  navChild.uuid +
                  "' data-index='" +
                  i +
                  "' data-childindex='" +
                  j +
                  "'>" +
                  "<div class='portal-item-icon' style='background:" +
                  (navChild.iconBg || '#0089ff') +
                  "'><i class='" +
                  navChild.icon +
                  "'></i></div>" +
                  "<div class='portal-item-title'>" +
                  navChild.name +
                  '</div>' +
                  '</div>';
              }
            }
          }
          html += '</div>';
        }
      }

      var reg = /(\.((2(5[0-5]|[0-4]\d))|[0-1]?\d{1,2})){3}/;

      for (var k = 0; k < options.linkNavTree.length; k++) {
        var linkNav = options.linkNavTree[k];
        if (options.showCategory) {
          html +=
            "<div class='portal-list-title " +
            textAlign +
            "'>" +
            "<img class='leftImg' src='" +
            ctx +
            "/static/js/pt/img/left.png' alt='' />" +
            "<img class='centerImg' src='" +
            ctx +
            "/static/js/pt/img/centerLeft.png' alt='' />" +
            "<div class='portal-list-title-text'>" +
            linkNav.name +
            '</div>' +
            "<img class='centerImg' src='" +
            ctx +
            "/static/js/pt/img/centerRight.png' alt='' /></div>";
        }
        html += "<div class='portal-item clearfix " + textAlign + "' data-type='common' data-index='" + k + "'>";
        if (linkNav.children.length > 0) {
          for (var h = 0; h < linkNav.children.length; h++) {
            var linkNavChild = linkNav.children[h].data;
            var addr = linkNavChild.linkAddress;
            if (reg.test(addr) && addr.indexOf('://') < 0) {
              addr = 'http://' + addr;
            }
            html +=
              "<div class='portal-item-box common-links' data-url='" +
              addr +
              "' data-index='" +
              k +
              "' data-childIndex='" +
              h +
              "'>" +
              "<div class='portal-item-icon'>";
            html += "<img src='" + addr + "/favicon.ico' />";
            html += "<i class='portal-icon-ip'>IP</i>";
            html += "<i class='portal-icon-w'>W</i>";
            html += '</div>' + "<div class='portal-item-title'>" + linkNavChild.linkName + '</div>';
            if (_self.options.widgetDefinition.configuration.linkCustomDefined) {
              html +=
                "<div class='portal-operate'>" +
                "<i class='portal-operate-icon iconfont icon-ptkj-gengduocaozuo'></i>" +
                "<div class='portal-operate-box'>" +
                "<div class='portal-operate-btn' id='editPortal'><i class='iconfont icon-ptkj-bianji'></i>编辑</div>" +
                "<div class='portal-operate-btn' id='deletePortal'><i class='iconfont icon-ptkj-shanchu'></i>删除</div>" +
                '</div>' +
                '</div>';
            }

            html += '</div>';
          }
        }
        if (_self.options.widgetDefinition.configuration.linkCustomDefined) {
          html +=
            "<div class='portal-item-box addLink' data-index='" +
            k +
            "'>" +
            "<div class='portal-item-icon'><i class='iconfont icon-ptkj-jiahao'></i></div>" +
            "<div class='portal-item-title'>添加</div>" +
            '</div>';
        }

        html += '</div>';
      }
      html += '</div></div>' + "<div class='portal-set'>";
      if (_self.options.widgetDefinition.configuration.customDefined) {
        html += "<div id='portalSetBtn' class='portal-set-btn'><i class='iconfont icon-ptkj-shezhi'></i></div>";
      }
      html += "<div id='portalStyleBtn' class='portal-set-btn'><i class='iconfont icon-ptkj-zhutifengge'></i></div>";

      html += '</div>' + '</div>';

      _self.$element.html(html);
      _self.getStyleHtml(options);
      _self.getSetHtml(options);

      _self.refreshBadge();

      _self._afterRenderView();
    },
    getStyleHtml: function (options) {
      var html = '';
      html +=
        "<div class='portal-style'>" +
        "<div class='portal-style-title'>门户主题</div>" +
        "<div class='portal-style-theme clearfix'>" +
        "<div class='style-theme-prev iconfont icon-ptkj-xianmiaojiantou-zuo'></div>" +
        "<div class='portal-theme-wrap'>" +
        "<div class='portal-theme-list'>";
      for (var i = 0; i < options.themesObj.length; i++) {
        var theme = options.themesObj[i];
        html +=
          "<div class='portal-theme-item' title='" +
          theme.themeName +
          "'><img data-src='" +
          theme.themeImg +
          "' src='" +
          theme.themeImg +
          "' alt=''><i class='iconfont icon-ptkj-dagou'></i></div>";
      }
      html +=
        '</div>' +
        '</div>' +
        "<div class='style-theme-next iconfont icon-ptkj-xianmiaojiantou-you'></div>" +
        '</div>' +
        "<div class='portal-style-title'>布局设置<div class='portal-style-category'><input id='category' name='category' type='checkbox'/><label for='category'>显示分类</label></div></div>" +
        "<div class='portal-style-layout'>" +
        "<div class='portal-layout-item' id='layoutLeft'><i class='iconfont icon-ptkj-juzuopailie'></i></div>" +
        "<div class='portal-layout-item' id='layoutCenter'><i class='iconfont icon-ptkj-juzhongpailie'></i></div>" +
        '</div>' +
        '</div>';
      this.$element.append(html);
    },
    getSetHtml: function (options) {
      var _self = this;
      var chooseWrap = _self._renderChooseWrap(options);
      var modal = new StringBuilder();

      modal.appendFormat(
        '<div id="{0}" class="nav-block-modal modal" tabindex="-1" role="dialog">' +
          '  <div class="modal-dialog" role="document">' +
          '    <div class="modal-content">' +
          '      <div class="modal-header">' +
          '        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true"><i class="iconfont icon-ptkj-dacha"></i></span></button>' +
          '        <span class="tit">{1}</span>' +
          '      </div>' +
          '      <div class="modal-body">' +
          '         <p class="tip"><span><i class="iconfont icon-ptkj-weixianjinggaotishiyuqi"></i>温馨提示：</span>单击标签可添加或去除业务应用入口！<i class="tips-close iconfont icon-ptkj-dacha-xiao"></i></p>' +
          '       <div class="clearfix">' +
          '         <div class="choose-wrap">' +
          '             <div class="choose-tit">' +
          '               <div class="text">选择应用</div>' +
          '               <div class="search-wrap">' +
          '                   <input class="form-control" name="searchKeyword" placeholder="搜索关键字"/>' +
          '                   <div class="delete-icon"><i class="iconfont icon-ptkj-dacha-xiao"></i></div>' +
          '                   <div class="search-icon"><i class="iconfont icon-ptkj-sousuochaxun"></i></div>' +
          '               </div>' +
          '             </div>' +
          '             <div class="choose-content">' +
          chooseWrap +
          '             </div>' +
          '             <div class="choose-control clearfix"><div class="choose-control-item choose-open"><i class="iconfont icon-ptkj-zhankai"></i>展开</div><div class="choose-control-item choose-close"><i class="iconfont icon-ptkj-zhedie"></i>折叠</div></div>' +
          '         </div>' +
          '         </div>' +
          '      </div>' +
          '      <div class="modal-footer">' +
          '        <button type="button" class="well-btn w-btn-primary portal-submit">保存</button>' +
          '        <button type="button" class="well-btn w-btn-default portal-reset">恢复默认</button>' +
          '        <button type="button" class="well-btn w-btn-default" data-dismiss="modal">取消</button>' +
          '      </div>' +
          '    </div>' +
          '  </div>' +
          '</div>',
        'modal_' + options.id,
        options.customTitle || '设置'
      );
      _self.$element.append(modal._buffers[0]);
    },
    _renderChooseWrap: function (options) {
      var chooseWrap = new StringBuilder();
      $.each(options.nav, function (i, v) {
        var chooseItem = new StringBuilder();
        $.each(v.children, function (child_i, child_v) {
          chooseItem.appendFormat(
            '<li data-id="{0}" class="{1}" data-navIndex="{2}" data-childIndex="{3}"><div class="has-choose-icon"><i class="iconfont icon-ptkj-youshangjiao-xuanzhong"></i></div><div class="icon-wrap"><i class="{4}"></i></div>{5}</li>',
            child_v.id,
            Boolean(child_v.data.defaultShow) ? 'has-choose' : '',
            i,
            child_i,
            child_v.data.icon,
            child_v.name
          );
        });
        chooseWrap.appendFormat(
          '<div class="choose-item">' +
            '<div class="divider">' +
            '<div class="text"><i class="iconfont icon-ptkj-shixinjiantou-you icon-ptkj-shixinjiantou-xia"></i> {0}</div>' +
            '</div>' +
            '<ul class="clearfix">' +
            chooseItem +
            '</ul>' +
            '</div>',
          v.name
        );
      });
      return chooseWrap;
    },
    _getAddLinkHtml: function () {
      var html = '';
      html +=
        "<div class='form'>" +
        "<div class='form-group clearfix'>" +
        "<label class='col-sm-2 control-label'>名称</label>" +
        "<div class='col-sm-10'>" +
        "<input name='linkName' class='form-control' id='linkName' type='text'/>" +
        '</div>' +
        '</div>' +
        "<div class='form-group clearfix'>" +
        "<label class='col-sm-2 control-label'>网址</label>" +
        "<div class='col-sm-10'>" +
        "<input name='linkAddress' class='form-control' id='linkAddress' type='text'/>" +
        '</div>' +
        '</div>' +
        '</div>';
      return html;
    },
    _setEvent: function (options) {
      var _self = this;

      if (options.defaultLayout == 'center') {
        $('#layoutCenter', _self.$element).addClass('layoutActive').siblings().removeClass('layoutActive');
      } else {
        $('#layoutLeft', _self.$element).addClass('layoutActive').siblings().removeClass('layoutActive');
      }

      if (options.showCategory) {
        $('#category', _self.$element).prop('checked', true);
      } else {
        $('#category', _self.$element).prop('checked', false);
      }

      // 搜索框删除
      $("input[name='searchKeyword']", _self.$element).keyup(function () {
        $('.delete-icon', _self.$element).show();
      });
      $('.delete-icon', _self.$element).on('click', function () {
        $("input[name='searchKeyword']", _self.$element).val('');
        $(this).hide(300);
      });

      // 退出登录
      $('.portal-sign-out', _self.$element).on('click', function () {
        window.open(ctx + '/j_spring_security_logout', '_self');
      });

      // 显示设置弹窗
      $('#portalSetBtn', _self.$element).click(function () {
        $('.nav-block-modal', _self.$element).show();
        $(this).parents('.portal-style').siblings('.nav-block-modal').modal('show');
        setTimeout(function () {
          $('.choose-item ul', _self.$element).each(function () {
            var $this = $(this);
            if (!$this.data('height')) {
              $this.data('height', $this[0].offsetHeight);
            }
          });
        }, 200);
      });

      _self.$element.find('.nav-block-modal').draggable({
        handle: '.modal-header',
        cursor: 'move',
        refreshPositions: false
      });

      // 显示风格弹窗
      $('#portalStyleBtn', _self.$element).click(function (e) {
        e.stopPropagation();
        e.preventDefault();
        $('.portal-style', _self.$element).show();
        var items = $('.portal-theme-list .portal-theme-item', _self.$element);
        $.each(options.themesObj, function (index, item) {
          if (item.themeImg == options.defaultThemes.themeImg) {
            items.eq(index).addClass('isActive').siblings().removeClass('isActive');
          }
        });
      });

      // 关闭风格弹窗
      $(document).click(function (e) {
        if (!$(e.target).hasClass('portal-style') && !$(e.target).parents().hasClass('portal-style')) {
          $('.portal-style', _self.$element).hide();
        }
      });

      // 切换风格
      $('.portal-layout-item', _self.$element).click(function () {
        $(this).addClass('layoutActive').siblings('').removeClass('layoutActive');
        if ($(this).attr('id') == 'layoutLeft') {
          $('.portal-list-title', _self.$element).removeClass('titleCenter');
          $('.portal-item', _self.$element).removeClass('titleCenter');
          options.defaultLayout = 'left';
        } else {
          $('.portal-list-title', _self.$element).addClass('titleCenter');
          $('.portal-item', _self.$element).addClass('titleCenter');
          options.defaultLayout = 'center';
        }

        _self._saveConfigurer(options);
      });

      // 替换页面背景
      $('.portal-theme-item', '.portal-theme-list').on('click', function () {
        var img = $(this).find('img').data('src');
        $('.portal-wrap', _self.$element).css('background-image', "url('" + img + "')");
        $(this).addClass('isActive').siblings().removeClass('isActive');
        options.defaultThemes = {
          themeName: $(this).attr('title'),
          themeImg: img
        };
        _self._saveConfigurer(options);
      });

      //搜索
      $('.search-wrap .form-control', _self.$element)
        .on('focus', function () {
          $(this).removeClass('blur');
        })
        .on('blur', function () {
          $(this).addClass('blur');
        });

      $('.search-icon', _self.$element).on('click', function () {
        var keyword = $("input[name='searchKeyword']", _self.$element).val();
        $('.choose-item ul li', _self.$element).each(function () {
          var $this = $(this);
          $this.show();
          if ($this.text().indexOf(keyword) < 0 && keyword) {
            $this.hide();
          }
        });
      });

      $('input[name="searchKeyword"]', _self.$element).on('keyup', function (e) {
        if (e.keyCode == 13) {
          $('.search-icon', _self.$element).trigger('click');
        }
      });

      // 关闭提示
      $('.tip .tips-close', _self.$element).on('click', function () {
        var $this = $(this);
        var $parent = $this.parent();
        $parent.remove();
      });

      // 展开/折叠
      $('.choose-content .choose-item .divider .text', _self.$element).on('click', function (e) {
        e.stopPropagation();
        var $this = $(this);
        var _ul = $this.parent().siblings('ul');
        var _i = $this.find('i');
        _i.toggleClass('icon-ptkj-shixinjiantou-xia');
        if (_i.hasClass('icon-ptkj-shixinjiantou-xia')) {
          _ul.animate(
            {
              height: _ul.data('height') + 'px'
            },
            100
          );
        } else {
          _ul.animate(
            {
              height: 0
            },
            100
          );
        }
      });

      // 弹窗设置-是否显示
      $('.choose-item ul li', _self.$element).on('click', function (e) {
        e.stopPropagation();
        var $this = $(this);
        var _navIndex = $this.data('navindex');
        var _childIndex = $this.data('childindex');

        $this.toggleClass('has-choose');
        if ($this.hasClass('has-choose')) {
          options.nav[_navIndex].children[_childIndex].data.defaultShow = true;
        } else {
          options.nav[_navIndex].children[_childIndex].data.defaultShow = false;
        }
      });

      $('.business-link', _self.$element).on('click', function (event) {
        var $this = $(this);
        var menuId = $this.data('id');

        var navItem = _self.navMap[menuId];

        var appType = navItem.eventHanlderType || navItem.type || navItem.appType;
        var appPath = navItem.eventHanlderPath || navItem.path || navItem.appPath;
        var targetPosition = navItem.targetPosition;
        var targetWidgetId = navItem.targetWidgetId;
        var refreshIfExists = navItem.refreshIfExists;
        var eventParams = $.extend({}, navItem.eventParams);

        var opt = {
          menuId: menuId,
          menuItem: navItem,
          target: targetPosition || options.targetPosition,
          targetWidgetId: targetWidgetId || options.targetWidgetId,
          refreshIfExists: refreshIfExists,
          appType: appType,
          appPath: appPath,
          params: eventParams,
          event: event,
          onPrepare: {}
        };

        _self.startApp(opt);
      });

      // 展开/折叠
      $('.choose-control-item', _self.$element).on('click', function () {
        var $this = $(this);
        var _chooseItem = $this.parent().siblings('.choose-content').find('.choose-item');
        var _ul = _chooseItem.find('ul');
        var _i = _chooseItem.find('.divider i');
        if ($this.hasClass('choose-open')) {
          _ul.each(function () {
            $(this).animate(
              {
                height: $(this).data('height') + 'px'
              },
              300
            );
          });
          _i.addClass('icon-ptkj-shixinjiantou-xia');
        } else {
          _ul.animate(
            {
              height: 0
            },
            300
          );
          _i.removeClass('icon-ptkj-shixinjiantou-xia');
        }
      });

      // 保存
      $('.portal-submit', _self.$element).on('click', function () {
        // 保存用户的组件配置信息

        $.ajax({
          url: ctx + '/api/user/preferences/save',
          type: 'POST',
          data: {
            dataKey: options.id,
            dataValue: JSON.stringify({
              options: options
            }),
            moduleId: 'UNIFIEDPORTAL',
            remark: '统一门户'
          },
          dataType: 'json',
          success: function (result) {
            if (result.code == 0) {
              _self._renderView(options);
              _self._setEvent(options);

              $('#modal_' + options.id).modal('hide');
            }
          }
        });
      });

      // 重置
      $('.portal-reset', _self.$element).on('click', function () {
        // 保存用户的组件配置信息
        $.ajax({
          url: ctx + '/api/user/preferences/save',
          type: 'POST',
          data: {
            dataKey: options.id,
            dataValue: '',
            moduleId: 'UNIFIEDPORTAL',
            remark: '统一门户'
          },
          dataType: 'json',
          success: function (result) {
            if (result.code == 0) {
              _self._createView();
            }
          }
        });
      });

      // 关闭设置弹窗
      $("button[data-dismiss='modal']", _self.$element).on('click', function () {
        $('#modal_' + options.id).hide();
      });

      // 常用链接跳转
      $('.common-links', _self.$element)
        .off()
        .on('click', function () {
          var url = $(this).data('url');
          window.open(url, '_blank');
        });

      // 添加常用链接
      $('.addLink', _self.$element)
        .off()
        .on('click', function () {
          var linkIndex = $(this).data('index');
          _self._editLinks(options, linkIndex, -1);
        });
      // 主题选择
      $('.style-theme-next', _self.$element).on('click', function () {
        var themeList = $(this).siblings('.portal-theme-wrap').find('.portal-theme-list');
        var left = themeList.position().left;
        var listWidth = $(this).siblings('.portal-theme-wrap').width();
        var itemWidth = themeList.width();
        if (listWidth - left <= itemWidth) {
          $(this).removeClass('disabled');
          themeList.animate(
            {
              left: left - 90 + 'px'
            },
            300
          );
        } else {
          $(this).addClass('disabled');
        }
      });
      $('.style-theme-prev', _self.$element).on('click', function () {
        var themeList = $(this).siblings('.portal-theme-wrap').find('.portal-theme-list');
        var left = themeList.position().left;
        if (left <= -90) {
          $(this).removeClass('disabled');
          themeList.animate(
            {
              left: left + 90 + 'px'
            },
            300
          );
        } else {
          $(this).addClass('disabled');
        }
      });

      // 常用链接删除/编辑
      $('.portal-operate-box', _self.$element).on('click', '.portal-operate-btn', function (e) {
        e.stopPropagation();
        e.preventDefault();
        var linkIndex = $(this).parents('.portal-item-box').data('index');
        var linkChildIndex = $(this).parents('.portal-item-box').data('childindex');

        if ($(this).attr('id') == 'editPortal') {
          _self._editLinks(options, linkIndex, linkChildIndex);
        } else {
          appModal.confirm('确定删除该链接？', function (res) {
            if (res) {
              options.linkNavTree[linkIndex].children.splice(linkChildIndex, 1);
              _self._saveConfigurer(options);
            }
          });
        }
      });

      // 拖拽
      $('.portal-item', _self.$element).sortable({
        revert: false,
        cursor: 'move',
        items: '.portal-item-box',
        // scroll:false,
        tolerance: 'pointer',
        update: function () {
          var type = $(this).data('type');
          var items = $(this).find('.portal-item-box');
          var nav = type == 'business' ? options.nav : options.linkNavTree;
          var newType = type == 'business' ? 'nav' : 'linkNavTree';
          var newNav = $.extend(true, [], nav);
          var outIndex = $(this).data('index');
          newNav[outIndex].children = [];
          $.each(items, function (index, item) {
            var navIndex = $(item).data('index');
            var navChildIndex = $(item).data('childindex');
            if (navChildIndex != undefined) {
              newNav[outIndex].children.push(nav[navIndex].children[navChildIndex]);
            }
          });

          if (type == 'business') {
            var newOption = $.extend(true, {}, options);
            newOption.nav = newNav;
            options[newType] = _self._compareOptions(options, newOption).nav;
          } else {
            options[newType] = newNav;
          }
          _self._saveConfigurer(options);
        }
      });

      // 显示分类标题
      $('#category', _self.$element).on('change', function () {
        if ($(this).prop('checked') == true) {
          options.showCategory = true;
        } else {
          options.showCategory = false;
        }
        console.log(options);
        setTimeout(function () {
          _self._saveConfigurer(options);
        }, 100);
      });

      // 图片加载错误， ip显示ip,网址显示w
      $('img', '.portal-item-icon').on('error', function (e) {
        //加入相应的图片类名
        var url = $(this).parents('.common-links').data('url');
        var reg = /(\.((2(5[0-5]|[0-4]\d))|[0-1]?\d{1,2})){3}/;
        var iconShow = reg.test(url) ? 'ipIcon' : 'addrIcon';
        $(this).parents('.common-links').addClass(iconShow);
      });

      $('.portal-item-box.common-links', _self.$element)
        .mouseenter(function () {
          var $that = $(this);
          _self.timer = setTimeout(function () {
            $that.find('.portal-operate-icon').css({
              display: 'block'
            });
          }, 800);
        })
        .mouseleave(function () {
          clearTimeout(_self.timer);
          $(this).find('.portal-operate-icon').css({
            display: 'none'
          });
        });

      // 显示微章数量
      // 监听容器创建完成事件
      var pageContainer = _self.pageContainer;
      pageContainer.on(constant.WIDGET_EVENT.Change, function (e, ui) {
        if (appContext.isWidgetExists(_self)) {
          $.each(_self.navMap, function () {
            var navData = this;
            if (navData == null || (navData.showBadgeCoun && navData.showBadgeCount[0] !== 'true')) {
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
        $.each(_self.navMap, function () {
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
    },
    _editLinks: function (options, index, childIndex) {
      var _self = this;
      var message = _self._getAddLinkHtml();
      appModal.dialog({
        message: message,
        title: options.linkCustomTitle || '添加常用链接',
        size: 'middle',
        zIndex: 1000,
        buttons: {
          confirm: {
            label: '保存',
            className: 'btn btn-primary',
            callback: function () {
              var name = $('#linkName').val();
              var addr = $('#linkAddress').val();
              if (name == '') {
                appModal.error('名称不能为空');
                return false;
              }
              if (addr == '') {
                appModal.error('网址不能为空');
                return false;
              }

              if (childIndex != -1) {
                var children = options.linkNavTree[index].children[childIndex];
                children.data.linkName = name;
                children.data.linkAddress = addr;
              } else {
                var uuid = UUID.createUUID();
                options.linkNavTree[index].children.push({
                  id: uuid,
                  name: name,
                  isParent: false,
                  data: {
                    linkName: name,
                    linkUuid: uuid,
                    linkAddress: addr,
                    isParent: false
                  }
                });
              }

              _self._saveConfigurer(options);
            }
          },
          cancel: {
            label: '取消',
            className: 'btn btn-default'
          }
        },
        shown: function () {
          if (childIndex != -1) {
            var linkNav = options.linkNavTree[index];
            var name = linkNav.children[childIndex].data.linkName;
            var addr = linkNav.children[childIndex].data.linkAddress;
            $('#linkName').val(name);
            $('#linkAddress').val(addr);
          }
        }
      });
    },
    _saveConfigurer: function (options) {
      var _self = this;

      $.ajax({
        url: ctx + '/api/user/preferences/save',
        type: 'POST',
        data: {
          dataKey: options.id,
          dataValue: JSON.stringify({
            options: options
          }),
          moduleId: 'UNIFIEDPORTAL',
          remark: '统一门户'
        },
        dataType: 'json',
        success: function (result) {
          if (result.code == 0) {
            _self._createView();
          }
        }
      });
    },
    _compareOptions: function (options, customerOptions) {
      var newOption = $.extend(true, {}, customerOptions);
      var defaultThem = customerOptions.defaultThemes;

      // 比较主题备选项和默认主题
      if (!this._deepEqual(options.themesObj, customerOptions.themesObj)) {
        newOption.themesObj = options.themesObj;
        newOption.defaultThemes = newOption.themesObj[0];
        $.each(newOption.themesObj, function (index, item) {
          if (defaultThem.themeImg == item.themeImg) {
            newOption.defaultThemes = defaultThem;
            return true;
          }
        });
      }

      // 比较业务应用备选项
      if (!this._deepEqual(options.nav, customerOptions.nav)) {
        var nav = options.nav;
        var cusNav = customerOptions.nav;
        var newNav = [];
        var thirdNav = $.extend(true, [], options.nav);
        for (var j = 0; j < cusNav.length; j++) {
          for (var i = 0; i < nav.length; i++) {
            if (cusNav[j].id == nav[i].id) {
              newNav.push($.extend(true, {}, nav[i]));
              if (nav[i].children && cusNav[j].children) {
                var lastNav = newNav[newNav.length - 1];
                lastNav.children = [];
                var children = nav[i].children;
                var cusChildren = cusNav[j].children;
                for (var k = 0; k < cusChildren.length; k++) {
                  for (var h = 0; h < children.length; h++) {
                    if (children[h].id == cusChildren[k].id) {
                      lastNav.children.push($.extend(true, {}, children[h]));
                      lastNav.children[lastNav.children.length - 1].data.defaultShow = cusChildren[k].data.defaultShow;
                      thirdNav[i].children.splice(h, 1, undefined);
                    }
                  }
                }
              }
            }
          }
        }

        for (var s = 0; s < thirdNav.length; s++) {
          if (thirdNav[s].children) {
            for (var q = 0; q < thirdNav[s].children.length; q++) {
              if (thirdNav[s].children[q] != undefined && newNav[s].children) {
                newNav[s].children.splice(q, 0, thirdNav[s].children[q]);
              }
            }
          }
        }
        $.extend(true, newOption.nav, newNav);
      }

      return newOption;
    },
    _deepEqual: function (x, y) {
      if (x === y) {
        return true;
      }
      if (!(typeof x == 'object' && x != null) || !(typeof y == 'object' && y != null)) {
        return false;
      }
      //比较对象内部
      if (Object.keys(x).length != Object.keys(y).length) {
        return false;
      }
      for (var prop in x) {
        if (y.hasOwnProperty(prop)) {
          if (!this._deepEqual(x[prop], y[prop])) {
            return false;
          }
        } else {
          return false;
        }
      }
      return true;
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
      var selector = 'div.portal-item-box[data-id="' + nav.uuid + '"]';
      var $nav = $(selector, _self.element).children('.portal-item-icon');
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
      var navMap = _self.navMap;
      $.each(navMap, function () {
        // _self._getBadgetCount(this);
        var navData = this;
        _self.refreshSingleBadge(navData);
      });
    },
    refreshSingleBadge: function (navData) {
      var _self = this;
      if (navData == null || !navData.showBadgeCount) {
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
    }
  });
});
