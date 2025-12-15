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

  $.widget('ui.wNavBlock', $.ui.wWidget, {
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

      $.ajax({
        url: ctx + '/api/user/preferences/get',
        type: 'get',
        data: {
          dataKey: options.id,
          moduleId: 'NAVBLOCK'
        },
        success: function (result) {
          if (result.code === 0) {
            customOptions = options;
            _sorts = result.data && result.data.dataValue ? JSON.parse(result.data.dataValue).sorts : [];
            // 渲染组件内容
            _self._renderView(customOptions, _sorts);
            // 绑定组件事件
            _self._bindEvents(customOptions, options, _sorts);
            _self._setEvent(customOptions);
          }
        }
      });

      // //获取用户的组件配置信息
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
      //       _self._bindEvents(customOptions, options, _sorts);
      //       _self._setEvent(customOptions);
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
     * 渲染可展示关注项
     */
    _renderNavBlock: function (options, sorts, type) {
      var _self = this;
      var html = new StringBuilder();
      var edit = Boolean(options.isEditable)
        ? '<div class="nav-block-item nav-block-edit"><i class="iconfont icon-ptkj-jiahao"></i></div>'
        : '';
      var show_num = 0;
      var contentMaxNum = parseInt(options.contentMaxNum);
      if (sorts.length) {
        $.each(sorts, function (index, data) {
          var _child = options.nav[data.navIndex].children[data.childIndex];
          _self.menuItemMap[_child.data.uuid] = _child.data;
          html.appendFormat(
            '<div class="nav-block-item" id="{0}" data-id="{0}"><div class="icon-wrap"><i class="{1}"></i></div><span class="text">{2}</span><span class="nav-block-badge"></span></div>',
            _child.id,
            _child.data.icon,
            _child.name
          );
        });
      } else {
        $.each(options.nav, function (i, v) {
          $.each(v.children, function (child_i, child_v) {
            _self.menuItemMap[child_v.data.uuid] = child_v.data;
            if (show_num >= contentMaxNum || !Boolean(child_v.data.isShow)) {
              return;
            }
            html.appendFormat(
              '<div class="nav-block-item" id="{0}" data-id="{0}"><div class="icon-wrap"><i class="{1}"></i></div><span class="text">{2}</span><span class="nav-block-badge"></span></div>',
              child_v.id,
              child_v.data.icon,
              child_v.name
            );
            show_num++;
          });
        });
      }
      if (type) {
        $('.nav-block-wrap')
          .html(html + edit)
          .find('.nav-block-edit')
          .data('data', {
            options: options,
            sorts: sorts
          });
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
              Boolean(child_v.data.isShow) ? 'has-choose' : '',
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
            if (show_num >= contentMaxNum || !Boolean(child_v.data.isShow)) {
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
      var navBlock = _self._renderNavBlock(options, sorts);

      _self.$element.html('<div class="nav-block-wrap clearfix">' + navBlock + '</div>');
      if (contentWidth) {
        $('.nav-block-wrap', _self.$element).css('width', contentWidth + 'px');
      }
      // 锚点选择
      if (options.params && options.params.hash) {
        var hashInfo = appContext.parseHashInfo(options.params.hash);
        if (hashInfo && hashInfo.widgetId == _self.getId()) {
          _self.onHashSelection(hashInfo);
        }
      }
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
        $("div[id='" + menuId + "']", _self.element).data('selectionParams', params);
        $("div[id='" + menuId + "']", _self.element).trigger('click');
        $("div[id='" + menuId + "']", _self.element).data('selectionParams', null);
      });
    },
    _bindEvents: function (options, initOptions, sorts) {
      var _self = this;
      var customerOptions = _.cloneDeep(options);
      var pageContainer = _self.getPageContainer();
      $(document)
        .off('click', '.nav-block-item')
        .on('click', '.nav-block-item', function (event) {
          var $this = $(this);
          var menuId = $this.data('id');
          if ($this.hasClass('nav-block-edit')) {
            return;
          }
          var menuItem = _self.menuItemMap[menuId];

          var appType = menuItem.eventHanlderType || menuItem.type || menuItem.appType;
          var appPath = menuItem.eventHanlderPath || menuItem.path || menuItem.appPath;
          var targetPosition = menuItem.targetPosition;
          var targetWidgetId = menuItem.targetWidgetId;
          var refreshIfExists = menuItem.refreshIfExists;
          var selectionParams = $this.data('selectionParams') || {};
          var eventParams = $.extend({}, menuItem.eventParams, appContext.parseEventHashParams(menuItem, 'menuid'), selectionParams);

          var opt = {
            menuId: menuId,
            menuItem: menuItem,
            target: targetPosition || options.targetPosition,
            targetWidgetId: targetWidgetId || options.targetWidgetId,
            refreshIfExists: refreshIfExists,
            appType: appType,
            appPath: appPath,
            params: eventParams,
            event: event,
            onPrepare: {}
          };
          if (eventParams && eventParams.menuid) {
            pageContainer.trigger(constant.WIDGET_EVENT.MenuItemSelect, eventParams.menuid);
          }
          _self.startApp(opt);
        });

      $(document)
        .off('click', '.nav-block-edit')
        .on('click', '.nav-block-edit', function () {
          var data = $(this).data('data');
          if (data) {
            options = data.options;
            sorts = data.sorts;
          }
          var _html =
            '<div class="nav-block-modal"><p class="tip"><span><i class="iconfont icon-ptkj-weixianjinggaotishiyuqi"></i>温馨提示：</span>单击标签可添加或去除关注项；拖拽已选标签可更改关注项的排列顺序！<i class="tips-close iconfont icon-ptkj-dacha-xiao"></i></p>' +
            '<div class="clearfix">' +
            '<div class="choose-wrap">' +
            '<div class="choose-tit">' +
            '<div class="text">选择关注项</div>' +
            '<div class="search-wrap">' +
            '<input class="form-control blur" name="navBlockKeyword" placeholder="搜索关键字"/>' +
            '<div class="nav-block-search-icon"><i class="iconfont icon-ptkj-sousuochaxun"></i></div>' +
            '</div>' +
            '</div>' +
            '<div class="choose-content">' +
            '</div>' +
            '<div class="choose-control clearfix"><div class="choose-control-item choose-open"><i class="iconfont icon-ptkj-zhankai"></i>展开</div><div class="choose-control-item choose-close"><i class="iconfont icon-ptkj-zhedie"></i>折叠</div></div>' +
            '</div>' +
            '<div class="drag-content">' +
            '<div class="choose-tit"><div class="text">已选关注项</div></div>' +
            '<div class="drag-wrap"></div>' +
            '</div>' +
            '</div></div>';

          var $dialog = appModal.dialog({
            title: options.popupTitle,
            message: _html,
            height: '700px',
            width: '1055px',
            size: 'large',
            shown: function () {
              var chooseWrap = _self._renderChooseWrap(options, sorts || []);
              var dragBox = _self._renderDragWrap(options, sorts || []);
              $dialog.find('.choose-content').append(chooseWrap.toString());
              $dialog.find('.drag-wrap').append(dragBox);
              var chooseItemInfoList = [];
              $.each(options.nav, function (i, v) {
                $.each(v.children, function (child_i, child_v) {
                  chooseItemInfoList.push(child_v.data);
                });
              });
              $dialog.find('.choose-item ul li').each(function () {
                for (var i in chooseItemInfoList) {
                  if (chooseItemInfoList[i].uuid === $(this).data('id')) {
                    $(this).data('info', chooseItemInfoList[i]);
                  }
                }
              });

              $dialog
                .on('focus', '.search-wrap .form-control', function () {
                  $(this).removeClass('blur');
                })
                .on('blur', '.search-wrap .form-control', function () {
                  $(this).addClass('blur');
                })
                .on('keydown', function (e) {
                  if (e.keyCode === 13) {
                    $dialog.find('.nav-block-search-icon').trigger('click');
                  }
                });

              $dialog.on('click', '.nav-block-search-icon', function () {
                var keyword = $("input[name='navBlockKeyword']", $dialog).val();
                $('.choose-item', $dialog).each(function () {
                  var _num = 0;
                  $(this)
                    .find('ul li')
                    .each(function () {
                      var $this = $(this);
                      if ($this.text().indexOf(keyword) < 0 && keyword) {
                        $this.hide();
                      } else {
                        $this.show();
                        _num++;
                      }
                    });
                  if (_num > 0) {
                    $(this).show();
                  } else {
                    $(this).hide();
                  }
                });
              });

              $dialog.find('#dragBox_' + customerOptions.id).sortable({
                revert: true
              });

              // 绑定组件事件
              $dialog.off('click', '.choose-item ul li').on('click', '.choose-item ul li', function (e) {
                e.stopPropagation();
                var $this = $(this);
                var _info = $this.data('info');
                var _id = $this.data('id');
                var _navIndex = $this.data('navindex');
                var _childIndex = $this.data('childindex');
                var _all_choose_li_len = $dialog.find('.choose-item ul li.has-choose').length;
                if (_all_choose_li_len >= customerOptions.contentMaxNum && !$this.hasClass('has-choose')) {
                  appModal.warning('最多只能显示' + customerOptions.contentMaxNum + '个关注项！');
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
                  $('.drag-box').append(_dragItem + '');
                  customerOptions.nav[_navIndex].children[_childIndex].data.isShow = ['true'];
                } else {
                  $('.drag-box li').each(function () {
                    var _$this = $(this);
                    if (_$this.data('id') === _id) {
                      _$this.remove();
                    }
                  });
                  customerOptions.nav[_navIndex].children[_childIndex].data.isShow = '';
                }
              });

              $dialog.off('click', '.drag-box li .close').on('click', '.drag-box li .close', function () {
                var $this = $(this);
                var $parent = $this.parent();
                var _id = $parent.data('id');
                $parent.remove();
                $('.choose-item ul li').each(function () {
                  var _$this = $(this);
                  if (_$this.data('id') === _id) {
                    _$this.removeClass('has-choose');
                  }
                });
              });

              $dialog.off('click', '.tip .tips-close').on('click', '.tip .tips-close', function () {
                var $this = $(this);
                var $parent = $this.parent();
                $parent.remove();
              });

              $dialog.on('click', '.choose-control-item', function () {
                var $this = $(this);
                var _chooseItem = $this.parent().siblings('.choose-content').find('.choose-item');
                var _ul = _chooseItem.find('ul');
                var _i = _chooseItem.find('.divider i');
                if ($this.hasClass('choose-open')) {
                  _ul.slideDown();
                  _i.addClass('icon-ptkj-shixinjiantou-xia');
                } else {
                  _ul.slideUp();
                  _i.removeClass('icon-ptkj-shixinjiantou-xia');
                }
              });

              $dialog.on('click', '.choose-content .choose-item .divider .text', function (e) {
                e.stopPropagation();
                var $this = $(this);
                var _ul = $this.parent().siblings('ul');
                var _i = $this.find('i');
                _i.toggleClass('icon-ptkj-shixinjiantou-xia');
                _ul.slideToggle('fast');
              });

              if (sorts && sorts.length === 0) {
                var dragBoxId = 'dragBox_' + customerOptions.id;
                $dialog.find('#' + dragBoxId + ' li').each(function (i, v) {
                  var $this = $(this);
                  sorts.push({ navIndex: $this.data('navindex'), childIndex: $this.data('childindex') });
                });
              }
            },
            buttons: {
              save: {
                label: '保存',
                className: 'well-btn w-btn-primary',
                callback: function () {
                  var _sort = [];
                  var dragBoxId = 'dragBox_' + customerOptions.id;
                  $dialog.find('#' + dragBoxId + ' li').each(function (i, v) {
                    var $this = $(this);
                    _sort.push({ navIndex: $this.data('navindex'), childIndex: $this.data('childindex') });
                  });

                  if (!_sort.length) {
                    appModal.warning('至少要显示一个关注项！');
                    return false;
                  } else {
                    appModal.success('保存成功！');
                  }

                  // 保存用户的组件配置信息

                  $.ajax({
                    url: ctx + '/api/user/preferences/save',
                    type: 'POST',
                    data: {
                      dataKey: customerOptions.id,
                      dataValue: JSON.stringify({ options: customerOptions, sorts: _sort }),
                      moduleId: 'NAVBLOCK',
                      remark: '工作台关注项'
                    },
                    dataType: 'json',
                    success: function (result) {
                      _self._renderNavBlock(customerOptions, _sort, 'edit');
                      _self._setEvent(customerOptions, 'fresh');
                    }
                  });

                  // server.JDS.call({
                  //   async: false,
                  //   service: 'appUserWidgetDefFacadeService.saveCurrentUserWidgetDefinition',
                  //   data: [customerOptions.id, JSON.stringify({ options: customerOptions, sorts: _sort })],
                  //   version: '',
                  //   success: function (result) {
                  //     _self._renderNavBlock(customerOptions, _sort, 'edit');
                  //     _self._setEvent(customerOptions, 'fresh');
                  //   }
                  // });
                }
              },
              reset: {
                label: '恢复默认',
                className: 'well-btn w-btn-primary',
                callback: function () {
                  // 保存用户的组件配置信息
                  $.ajax({
                    url: ctx + '/api/user/preferences/save',
                    type: 'POST',
                    data: {
                      dataKey: customerOptions.id,
                      dataValue: '',
                      moduleId: 'NAVBLOCK',
                      remark: '工作台关注项'
                    },
                    dataType: 'json',
                    success: function (result) {
                      if (result.code == 0) {
                        _self._renderView(initOptions, []);
                        // 绑定组件事件
                        _self._bindEvents(initOptions, initOptions);
                        _self._setEvent(initOptions, 'fresh');
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
                  //       _self._setEvent(initOptions, 'fresh');
                  //     }
                  //   }
                  // });
                }
              },
              close: {
                label: '取消',
                className: 'btn-default',
                callback: function () {
                  var _sort = [];
                  var dragBoxId = 'dragBox_' + customerOptions.id;
                  $dialog.find('#' + dragBoxId + ' li').each(function (i, v) {
                    var $this = $(this);
                    _sort.push({ navIndex: $this.data('navindex'), childIndex: $this.data('childindex') });
                  });

                  if (sorts && sorts.length && JSON.stringify(_sort) !== JSON.stringify(sorts)) {
                    appModal.confirm('您修改的数据未保存,您确定要取消吗？', function (result) {
                      if (result) {
                        $dialog.modal('hide');
                      }
                    });
                    return false;
                  }
                }
              }
            }
          });
        });
    },
    _setEvent: function (options, type) {
      var _self = this;
      var $element = $(_self.element);
      var pageContainer = _self.getPageContainer();
      // 页面容器创建完成，加载徽章数量
      pageContainer.on(constant.WIDGET_EVENT.PageContainerCreationComplete, function (e) {
        _self._getTabBadgetCount(options, e);
      });
      pageContainer.on(constant.WIDGET_EVENT.BadgeRefresh, function (e, param) {
        _self._getTabBadgetCount(options, e, param);
      });
      if (type) {
        _self._getTabBadgetCount(options);
      }
    },
    _getTabBadgetCount: function (options, e, params) {
      var _self = this;
      $.each(options.nav, function (i, v) {
        $.each(v.children, function (child_i, child_v) {
          if (!Boolean(child_v.data.isShowBadge)) {
            return;
          }
          if (e && e.detail && e.detail.targetTabName) {
            //由于trigger事件中，将参数转到e.detail里了
            params.targetTabName = e.detail.targetTabName;
          }
          if (child_v.data.badgeType === 'datastore') {
            //按数据仓库统计
            //按数据仓库的数据量计算徽章数量
            _self._badgeCountDataProvider = new DataStore({
              dataStoreId: child_v.data.badgeTypeCountDs,
              params: options.params || {},
              onDataChange: function (data, count) {
                if (count !== -1) {
                  _self._setBadgetCount(child_v.data.uuid, count);
                }
              }
            });
            _self._badgeCountDataProvider.getCount(true);
          } else if (child_v.data.badgeType === 'countJs') {
            //执行统计js脚本
            // 获取导航徽章数量
            var badge = {
              countJsModule: child_v.data.badgeTypeCountJs,
              widgetDefId: child_v.data.badgeTypeCountJsWBootstrapTable,
              tab: child_v.data
            };
            _self.startApp({
              isJsModule: true,
              jsModule: child_v.data.badgeTypeCountJs,
              action: 'getCount',
              data: badge,
              ui: _self,
              callback: function (count, realCount, options) {
                _self._setBadgetCount(child_v.data.uuid, count);
              }
            });
          }
        });
      });
    },

    _setBadgetCount: function (uuid, count) {
      var selector = '.nav-block-item[id="' + uuid + '"]';
      var $badge = $(selector).find('.nav-block-badge');
      count = count > 99 ? '99+' : count;
      $badge.html(count);
    }
  });
});
