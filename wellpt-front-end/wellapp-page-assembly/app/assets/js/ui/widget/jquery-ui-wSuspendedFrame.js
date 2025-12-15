(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'commons', 'server', 'constant', 'appContext', 'dataStoreBase'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($, commons, server, constant, appContext, DataStore) {
  'use strict';
  var Browser = commons.Browser;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var SpringSecurityUtils = server.SpringSecurityUtils;

  $.widget('ui.wSuspendedFrame', $.ui.wWidget, {
    options: {
      // 组件定义
      widgetDefinition: {},
      // 上级容器定义
      containerDefinition: {}
    },
    _createView: function () {
      var _self = this;
      var options = $.extend({}, _self.options.widgetDefinition, _self.options.widgetDefinition.configuration);
      _self.menuItemMap = {};
      var customOptions = {};
      var _sorts = [];
      _self.$element = $(_self.element);
      _self._beforeRenderView();
      //获取用户的组件配置信息
      $.ajax({
        url: ctx + '/api/user/preferences/get',
        type: 'get',
        data: {
          dataKey: options.id,
          moduleId: 'SUSPENDEDFRAME'
        },
        success: function (result) {
          if (result.code === 0) {
            customOptions = options;
            _sorts = result.data && result.data.dataValue ? JSON.parse(result.data.dataValue).sorts : [];
            _self._renderView(customOptions, _sorts);
            _self._bindEvents(customOptions, options);
          }
        }
      });

      // server.JDS.call({
      //   async: false,
      //   service: 'appUserWidgetDefFacadeService.getCurrentUserWidgetDefintion',
      //   data: [options.id],
      //   version: '',
      //   success: function (result) {
      //     if (result.msg === 'success') {
      //       customOptions = options;
      //       _sorts = result.data ? JSON.parse(result.data).sorts : [];
      //       // 渲染组件内容
      //       _self._renderView(customOptions, _sorts);
      //       // 绑定组件事件
      //       _self._bindEvents(customOptions, options);
      //     }
      //   }
      // });
    },
    /**
     * 渲染视图前
     */
    _beforeRenderView: function () {
      var _self = this;
      _self.invokeDevelopmentMethod('beforeRender', [_self.options, _self.getConfiguration()]);
    },
    /**
     * 渲染导航
     */
    _renderSuspended: function (options, sorts, type) {
      var _self = this;
      var html = new StringBuilder();
      var show_num = 0;
      var contentMaxNum = parseInt(options.contentMaxNum);
      if (sorts.length) {
        $.each(sorts, function (index, data) {
          var _child = options.nav[data.navIndex].children[data.childIndex];
          _self.menuItemMap[_child.data.uuid] = _child.data;
          html.appendFormat(
            '<div class="suspended-item clearfix" id="{0}" data-id="{0}"><div class="icon-wrap"><i class="{1}"></i></div><span class="text">{2}</span></div>',
            _child.id,
            _child.data.icon,
            _child.name
          );
        });
      } else {
        $.each(options.nav, function (i, v) {
          $.each(v.children, function (child_i, child_v) {
            _self.menuItemMap[child_v.data.uuid] = child_v.data;
            if (show_num >= contentMaxNum || !Boolean(child_v.data._show)) {
              return;
            }
            html.appendFormat(
              '<div class="suspended-item clearfix" id="{0}" data-id="{0}"><div class="icon-wrap"><i class="{1}"></i></div><span class="text">{2}</span></div>',
              child_v.id,
              child_v.data.icon,
              child_v.name
            );
            show_num++;
          });
        });
      }
      var edit = Boolean(options.isEditable)
        ? '<div class="suspended-item suspended-edit clearfix" id="" data-id=""><div class="icon-wrap"><i class="iconfont icon-ptkj-shezhi"></i></div><span class="text">自定义</span></div>'
        : '';
      if (type) {
        $('.suspended-wrap', _self.$element).html(html + edit);
      } else {
        return html + edit;
      }
    },
    /**
     * 渲染可选择关注项区域
     */
    _renderChooseWrap: function (options, sorts) {
      var chooseWrap = new StringBuilder();
      var chooseItemInfoList = [];
      var _sorts = sorts.map(function (i) {
        return i.navIndex + '-' + i.childIndex;
      });
      $.each(options.nav, function (i, v) {
        var chooseItem = new StringBuilder();
        $.each(v.children, function (child_i, child_v) {
          chooseItemInfoList.push(child_v.data);
          if (sorts.length) {
            if ($.inArray(i + '-' + child_i, _sorts) > -1) {
              chooseItem.appendFormat(
                '<li data-id="{0}" class="{1}" data-navIndex="{2}" data-childIndex="{3}"><div class="has-choose-icon"><i class="iconfont icon-ptkj-youshangjiao-xuanzhong"></i></div><div class="icon-wrap"><i class="{4}"></i></div>{5}</li>',
                child_v.id,
                'has-choose',
                i,
                child_i,
                child_v.data.icon,
                child_v.name
              );
            } else {
              chooseItem.appendFormat(
                '<li data-id="{0}" class="{1}" data-navIndex="{2}" data-childIndex="{3}"><div class="has-choose-icon"><i class="iconfont icon-ptkj-youshangjiao-xuanzhong"></i></div><div class="icon-wrap"><i class="{4}"></i></div>{5}</li>',
                child_v.id,
                '',
                i,
                child_i,
                child_v.data.icon,
                child_v.name
              );
            }
          } else {
            chooseItem.appendFormat(
              '<li data-id="{0}" class="{1}" data-navIndex="{2}" data-childIndex="{3}"><div class="has-choose-icon"><i class="iconfont icon-ptkj-youshangjiao-xuanzhong"></i></div><div class="icon-wrap"><i class="{4}"></i></div>{5}</li>',
              child_v.id,
              Boolean(child_v.data._show) ? 'has-choose' : '',
              i,
              child_i,
              child_v.data.icon,
              child_v.name
            );
          }
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
    /**
     * 渲染已选择关注项拖拽区域
     */
    _renderDragWrap: function (options, sorts) {
      var dragHtml = new StringBuilder();
      var show_num = 0;
      var contentMaxNum = parseInt(options.contentMaxNum);
      var dragBoxId = 'dragBox_' + options.id;
      var dragBox = '';
      if (sorts.length) {
        $.map(sorts, function (data) {
          var _child = options.nav[data.navIndex].children[data.childIndex];
          dragHtml.appendFormat(
            '<li data-id="{0}" data-navindex="{1}" data-childindex="{2}">' +
              '<div class="icon-wrap"><i class="{3}"></i></div>{4}' +
              '<div class="close"><i class="iconfont icon-ptkj-dacha"></i></div>' +
              '</li>',
            _child.data.uuid,
            data.navIndex,
            data.childIndex,
            _child.data.icon,
            _child.name
          );
        });
      } else {
        $.each(options.nav, function (i, v) {
          $.each(v.children, function (child_i, child_v) {
            if (show_num >= contentMaxNum || !Boolean(child_v.data._show)) {
              return;
            }
            dragHtml.appendFormat(
              '<li data-id="{0}" data-navindex="{1}" data-childindex="{2}">' +
                '<div class="icon-wrap"><i class="{3}"></i></div>{4}' +
                '<div class="close"><i class="iconfont icon-ptkj-dacha"></i></div>' +
                '</li>',
              child_v.data.uuid,
              i,
              child_i,
              child_v.data.icon,
              child_v.name
            );
            show_num++;
          });
        });
      }
      dragBox += '<ul id="' + dragBoxId + '" class="drag-box clearfix">' + dragHtml + '</ul>';
      return dragBox;
    },
    _renderView: function (options, sorts) {
      // 渲染组件内容
      var _self = this;
      _self.$element.addClass(options.customClass);
      var contentWidth = options.contentWidth;
      var navBlock = _self._renderSuspended(options, sorts);
      var chooseWrap = _self._renderChooseWrap(options, sorts);
      var dragBox = _self._renderDragWrap(options, sorts);
      var modal = new StringBuilder();
      var chooseItemInfoList = [];
      var menuStyle = options.menuStyle;
      $.each(options.nav, function (i, v) {
        $.each(v.children, function (child_i, child_v) {
          chooseItemInfoList.push(child_v.data);
        });
      });

      modal.appendFormat(
        '<div id="{0}" class="nav-block-modal modal fade" tabindex="-1" role="dialog">' +
          '  <div class="modal-dialog" role="document">' +
          '    <div class="modal-content">' +
          '      <div class="modal-header">' +
          '        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true"><i class="iconfont icon-ptkj-dacha"></i></span></button>' +
          '        <span class="tit">{1}</span>' +
          '      </div>' +
          '      <div class="modal-body">' +
          '         <p class="tip"><span><i class="iconfont icon-ptkj-weixianjinggaotishiyuqi"></i>温馨提示：</span>单击标签可添加或去除快捷菜单；拖拽已选标签可更改快捷菜单的排列顺序！<i class="tips-close iconfont icon-ptkj-dacha-xiao"></i></p>' +
          '       <div class="clearfix">' +
          '         <div class="choose-wrap">' +
          '             <div class="choose-tit">' +
          '               <div class="text">选择菜单项</div>' +
          '               <div class="search-wrap">' +
          '                   <input class="form-control" name="navBlockKeyword" placeholder="搜索关键字"/>' +
          '                   <div class="search-icon"><i class="iconfont icon-ptkj-sousuochaxun"></i></div>' +
          '               </div>' +
          '             </div>' +
          '             <div class="choose-content">' +
          chooseWrap +
          '             </div>' +
          '             <div class="choose-control clearfix"><div class="choose-control-item choose-open"><i class="iconfont icon-ptkj-zhankai"></i>展开</div><div class="choose-control-item choose-close"><i class="iconfont icon-ptkj-zhedie"></i>折叠</div></div>' +
          '         </div>' +
          '         <div class="drag-content">' +
          '         <div class="choose-tit"><div class="text">已选菜单项</div></div>' +
          '         <div class="drag-wrap">' +
          dragBox +
          '</div>' +
          '       </div>' +
          '         </div>' +
          '      </div>' +
          '      <div class="modal-footer">' +
          '        <button type="button" class="well-btn w-btn-primary suspended-submit">保存</button>' +
          '        <button type="button" class="well-btn w-btn-primary w-line-btn suspended-reset">恢复默认</button>' +
          '        <button type="button" class="well-btn w-btn-default" data-dismiss="modal">取消</button>' +
          '      </div>' +
          '    </div>' +
          '  </div>' +
          '</div>',
        'modal_' + options.id,
        options.popupTitle
      );

      _self.$element.html('<div class="suspended-wrap suspended-style-' + menuStyle + '">' + navBlock + '</div>' + modal);
      if (contentWidth) {
        $('.suspended-wrap', _self.$element).css('width', contentWidth + 'px');
      }
      $('.choose-item ul li', _self.$element).each(function () {
        for (var i in chooseItemInfoList) {
          if (chooseItemInfoList[i].uuid === $(this).data('id')) {
            $(this).data('info', chooseItemInfoList[i]);
          }
        }
      });
    },
    _bindEvents: function (options, initOptions) {
      var _self = this;
      var customerOptions = _.cloneDeep(options);

      $(_self.$element)
        .on('focus', '.search-wrap .form-control', function () {
          $(this).removeClass('blur');
        })
        .on('blur', '.search-wrap .form-control', function () {
          $(this).addClass('blur');
        });

      $(_self.$element).on('click', '.search-icon', function () {
        var keyword = $("input[name='navBlockKeyword']", _self.$element).val();
        $('.choose-item ul li', _self.$element).each(function () {
          var $this = $(this);
          $this.show();
          if ($this.text().indexOf(keyword) < 0 && keyword) {
            $this.hide();
          }
        });
      });

      $(_self.$element)
        .off('click', '.suspended-item')
        .on('click', '.suspended-item', function (event) {
          var $this = $(this);
          var menuId = $this.data('id');
          if ($this.hasClass('suspended-edit')) {
            return;
          }
          var menuItem = _self.menuItemMap[menuId];

          var appType = menuItem.eventHanlderType || menuItem.type || menuItem.appType;
          var appPath = menuItem.eventHanlderPath || menuItem.path || menuItem.appPath;
          var targetPosition = menuItem.targetPosition;
          var targetWidgetId = menuItem.targetWidgetId;
          var eventParams = $.extend({}, menuItem.eventParams, appContext.parseEventHashParams(menuItem, 'menuid'));

          var opt = {
            menuId: menuId,
            menuItem: menuItem,
            target: targetPosition || options.targetPosition,
            targetWidgetId: targetWidgetId || options.targetWidgetId,
            appType: appType,
            appPath: appPath,
            params: eventParams,
            event: event,
            onPrepare: {}
          };

          _self.startApp(opt);
        });

      $(_self.$element)
        .off('click', '.suspended-edit')
        .on('click', '.suspended-edit', function () {
          $(this).parents('.suspended-wrap').siblings('.nav-block-modal').modal('show');
          setTimeout(function () {
            $('.choose-item ul', _self.$element).each(function () {
              var $this = $(this);
              if (!$this.data('height')) {
                $this.data('height', $this[0].offsetHeight);
              }
            });
          }, 200);
        });
      // 绑定组件事件
      $(_self.$element)
        .off('click', '.choose-item ul li')
        .on('click', '.choose-item ul li', function (e) {
          e.stopPropagation();
          var $this = $(this);
          var _info = $this.data('info');
          var _id = $this.data('id');
          var _navIndex = $this.data('navindex');
          var _childIndex = $this.data('childindex');
          var _all_choose_li_len = $('.choose-item ul li.has-choose', _self.$element).length;
          if (_all_choose_li_len >= customerOptions.contentMaxNum && !$this.hasClass('has-choose')) {
            appModal.warning('最多只能显示' + customerOptions.contentMaxNum + '个菜单项！');
            return;
          }
          $this.toggleClass('has-choose');
          if ($this.hasClass('has-choose')) {
            var _dragItem = new StringBuilder();
            _dragItem.appendFormat(
              '<li data-id="{0}" data-navindex="{1}" data-childindex="{2}">' +
                '<div class="icon-wrap"><i class="{3}"></i></div>{4}' +
                '<div class="close"><i class="iconfont icon-ptkj-dacha"></i></div>' +
                '</li>',
              _info.uuid,
              _navIndex,
              _childIndex,
              _info.icon,
              _info.name
            );
            $('.drag-box', _self.$element).append(_dragItem + '');
            customerOptions.nav[_navIndex].children[_childIndex].data._show = ['true'];
          } else {
            $('.drag-box li', _self.$element).each(function () {
              var _$this = $(this);
              if (_$this.data('id') === _id) {
                _$this.remove();
              }
            });
            customerOptions.nav[_navIndex].children[_childIndex].data._show = '';
          }
        });

      $(_self.$element)
        .off('click', '.drag-box li .close')
        .on('click', '.drag-box li .close', function () {
          var $this = $(this);
          var $parent = $this.parent();
          var _id = $parent.data('id');
          $parent.remove();
          $('.choose-item ul li', _self.$element).each(function () {
            var _$this = $(this);
            if (_$this.data('id') === _id) {
              _$this.removeClass('has-choose');
            }
          });
        });

      $(_self.$element)
        .off('click', '.tip .tips-close')
        .on('click', '.tip .tips-close', function () {
          var $this = $(this);
          var $parent = $this.parent();
          $parent.remove();
        });

      $('.choose-control-item', _self.$element).on('click', function () {
        var $this = $(this);
        var _chooseItem = $this.parent().siblings('.choose-content').find('.choose-item');
        var _ul = _chooseItem.find('ul');
        var _i = _chooseItem.find('.divider i');
        if ($this.hasClass('choose-open')) {
          _ul.each(function () {
            $(this).animate({ height: $(this).data('height') + 'px' }, 300);
          });
          _i.addClass('icon-ptkj-shixinjiantou-xia');
        } else {
          _ul.animate({ height: 0 }, 300);
          _i.removeClass('icon-ptkj-shixinjiantou-xia');
        }
      });

      $('.choose-content .choose-item .divider .text', _self.$element).on('click', function (e) {
        e.stopPropagation();
        var $this = $(this);
        var _ul = $this.parent().siblings('ul');
        var _i = $this.find('i');
        _i.toggleClass('icon-ptkj-shixinjiantou-xia');
        if (_i.hasClass('icon-ptkj-shixinjiantou-xia')) {
          _ul.animate({ height: _ul.data('height') + 'px' }, 300);
        } else {
          _ul.animate({ height: 0 }, 300);
        }
      });

      $('.suspended-submit', _self.$element).on('click', function () {
        var _sort = [];
        var dragBoxId = 'dragBox_' + customerOptions.id;
        $('#' + dragBoxId + ' li', _self.$element).each(function (i, v) {
          var $this = $(this);
          _sort.push({ navIndex: $this.data('navindex'), childIndex: $this.data('childindex') });
        });

        if (!_sort.length) {
          appModal.warning('至少要显示一个菜单项！');
          return;
        }

        // 保存用户的组件配置信息
        $.ajax({
          url: ctx + '/api/user/preferences/save',
          type: 'POST',
          data: {
            dataKey: customerOptions.id,
            dataValue: JSON.stringify({ options: customerOptions, sorts: _sort }),
            moduleId: 'SUSPENDEDFRAME',
            remark: '悬浮框组件'
          },
          dataType: 'json',
          success: function (result) {
            if (result.code == 0) {
              _self._renderSuspended(customerOptions, _sort, 'edit');
              $('#modal_' + customerOptions.id).modal('hide');
            }
          }
        });
        // server.JDS.call({
        //   async: false,
        //   service: 'appUserWidgetDefFacadeService.saveCurrentUserWidgetDefinition',
        //   data: [customerOptions.id, JSON.stringify({ options: customerOptions, sorts: _sort })],
        //   version: '',
        //   success: function (result) {
        //     if (result.msg == 'success') {
        //       //TODO:保存成功
        //       _self._renderSuspended(customerOptions, _sort, 'edit');
        //       // _self._setEvent(customerOptions,'fresh');
        //       $('#modal_' + customerOptions.id).modal('hide');
        //     }
        //   }
        // });
      });
      $('.suspended-reset', _self.$element).on('click', function () {
        // 保存用户的组件配置信息
        $.ajax({
          url: ctx + '/api/user/preferences/save',
          type: 'POST',
          data: {
            dataKey: customerOptions.id,
            dataValue: '',
            moduleId: 'SUSPENDEDFRAME',
            remark: '悬浮框组件'
          },
          dataType: 'json',
          success: function (result) {
            if (result.code == 0) {
              _self._renderView(initOptions, []);
              _self._bindEvents(initOptions, initOptions);
              $('.modal-backdrop').remove();
            }
          }
        });

        // server.JDS.call({
        //   async: false,
        //   service: 'appUserWidgetDefFacadeService.saveCurrentUserWidgetDefinition',
        //   data: [customerOptions.id, ''],
        //   version: '',
        //   success: function (result) {
        //     if (result.msg === 'success') {
        //       // 渲染组件内容
        //       _self._renderView(initOptions, []);
        //       // 绑定组件事件
        //       _self._bindEvents(initOptions, initOptions);
        //       // _self._setEvent(initOptions,'fresh');
        //       $('.modal-backdrop').remove();
        //     }
        //   }
        // });
      });

      $('#dragBox_' + customerOptions.id, _self.$element).sortable({
        revert: true
      });
    }
  });
});
