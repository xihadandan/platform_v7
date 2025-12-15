/**
 *  事件处理选择页面
 */
(function (root, factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    define(['jquery', 'appContext', 'formBuilder', 'appModal'], factory);
  } else if (typeof exports === 'object') {
    module.exports = factory(require('jquery', 'appContext', 'formBuilder', 'appModal'));
  } else {
    factory(root.jQuery, root.appContext, root.formBuilder, root.appModal);
  }
})(this, function ($, appContext, formBuilder, appModal) {
  var types = ['all', 'page', 'appWidgetDefinition', 'jsModuleFunction', 'DmsAction', 'flowDefinition', 'url', 'webController'];
  var typeNames = ['所有', '页面', '组件定义功能', 'JS脚本', '数据管理操作', '流程', 'URL功能', 'Web控制器功能'];
  var typeDatas = {};

  var AppEvent = function (options, $el) {
    this.options = options;
    this.$el = $el;
  };

  AppEvent.prototype.init = function () {
    var _self = this;
    this.$el.prop('readonly', true);
    this.$el.wrap($('<div>', { class: 'input-group' }));
    var $span = $('<span>', { class: 'input-group-btn' }).append(
      $('<button>', { class: 'btn btn-success', type: 'button' }).text('选择事件'),
      $('<button>', { class: 'btn btn-danger', type: 'button' }).append($('<span>', { class: 'glyphicon glyphicon-remove' }))
    );
    $span.insertAfter(this.$el);
    this.$eventButton = this.$el.next().find('button:eq(0)');
    this.$clearEventButton = this.$el.next().find('button:eq(1)');
    this.$eventButton.on('click', function (e) {
      //点击展示tab项
      _self.showEventTagDialog();
    });
    this.$clearEventButton.on('click', function (e) {
      _self.$el.val('');
      _self.selectEventData(null);
      _self.setValue('');
      if ($.isFunction(_self.options.clearCallback)) {
        _self.options.clearCallback(_self.$el);
      }
    });

    return this;
  };

  AppEvent.prototype.showEventTagDialog = function () {
    //弹窗
    var _self = this;
    typeDatas = {};
    var $dialog;
    var dialogOpts = {
      title: '选择事件',
      message: _self.createTabs(),
      buttons: {
        confirm: {
          label: '确定',
          className: 'btn-primary',
          callback: function (result) {
            if (_self.getData()) {
              _self.$el.val(_self.getData().name);
            }
            if (_self.options.okCallback && $.isFunction(_self.options.okCallback)) {
              _self.options.okCallback(_self.$el, _self.getData());
            }
          }
        },

        cancel: {
          label: '取消',
          className: 'btn-default',
          callback: function (result) {
            if (_self.options.cancelCallback && $.isFunction(_self.options.cancelCallback)) {
              _self.options.cancelCallback(_self.$el, _self.getData());
            }
          }
        }
      },
      shown: function () {
        //第一个tab页：产品集成树展示
        _self.productIntegrationTreeInit();
      },
      size: 'large'
    };
    _self.$dialog = top.appModal.dialog(dialogOpts);
  };

  AppEvent.prototype.productIntegrationTreeInit = function () {
    var _self = this;
    var treeSetting = {
      check: {
        enable: true,
        chkStyle: 'radio',
        chkboxType: {
          Y: '',
          N: ''
        },
        radioType: 'all'
      },
      async: {
        enable: true,
        contentType: 'application/json',
        url: ctx + '/json/data/services',
        otherParam: {
          serviceName: _self.options.ztree.serviceName || 'appProductIntegrationMgr',
          methodName: _self.options.ztree.methodName || 'getTreeWithPtProduct',
          data: _self.options.ztree.params,
          version: ''
        },
        type: 'POST'
      },
      callback: {
        onCheck: function (event, treeId, treeNode) {
          _self.selectEventData(treeNode);
        },
        onClick: function (event, treeId, treeNode) {},
        onAsyncSuccess: function (event, treeId, treeNode, dataList) {
          var id =  _self.options.idValue;
          if (id) {
            //初始值展开树
            //展开树
            var nodes = _self.$piZtree.getNodesByParam('id', id, null);
            if (nodes.length > 0) {
              var n = nodes[0];
              var expandNode = function (n0) {
                _self.$piZtree.expandNode(n0, true, false, n0.id == n.id);
                var parentN = n0.getParentNode();
                if (parentN != null) {
                  expandNode(parentN);
                }
              };
              expandNode(n);
              _self.$piZtree.selectNode(n);
              _self.$piZtree.checkNode(n);
              /*setTimeout(function () {
                                _self.$dialog.find('.container-fluid').scrollTop($("#" + n.tId).offset().top);
                            }, 200);*/
            }
          }
          _self.typeDatas(dataList);
          for (var i = 1; i < types.length; i++) {
            _self.initFunctionTable(types[i]);
          }
        }
      }
    };

    this.$piZtree = $.fn.zTree.init(top.$('#all_ztree'), treeSetting);
    $('#ztreeSearch', _self.$dialog).on('keyup', function (e) {
      if (e.keyCode == 13) {
        $('#ztreeSearchBtn', _self.$dialog).trigger('click');
      }
    });

    $('#ztreeSearchBtn', _self.$dialog).on('click', function () {
      var nodeName = $('#ztreeSearch', _self.$dialog).val();
      //global.js
      searchZtreeNode(0, 'all_ztree', nodeName, function (uuid) {});
      _self.$dialog.find('.container-fluid').scrollTop(0);
      return false;
    });
  };

  AppEvent.prototype.selectEventData = function (data) {
    this.$el.data('eventData', data);
  };

  AppEvent.prototype.typeDatas = function (dataList) {
    typeDatas.all = dataList;
    var cascadeClassifyData = function (node) {
      var list = node.children;
      for (var i = 0; i < list.length; i++) {
        if (list[i].data.type == 4) {
          var name = list[i].data.name;
          var n = $.extend(true, {}, list[i]);
          //n._name = n.name;
          if (name === '默认页面容器' || list[i].name.indexOf('页面:') == 0) {
            if (!typeDatas.page) {
              typeDatas.page = [];
            }
            n._name = '[' + node.name + '] ' + n.name;
            typeDatas.page.push(n);
          } else if (name.indexOf('菜单/URL资源') == 0) {
            if (!typeDatas.url) {
              typeDatas.url = [];
            }
            n._name = '[' + node.name + '] ' + n.name;
            typeDatas.url.push(n);
          } else if (name.indexOf('Web控制器功能') == 0) {
            if (!typeDatas.webController) {
              typeDatas.webController = [];
            }
            n._name = '[' + node.name + '] ' + n.name;
            typeDatas.webController.push(n);
          } else if (name.indexOf('组件定义') == 0) {
            if (!typeDatas.appWidgetDefinition) {
              typeDatas.appWidgetDefinition = [];
            }
            n._name = '[' + node.name + '] ' + n.name;
            typeDatas.appWidgetDefinition.push(n);
            if (name.indexOf('组件定义_默认页面容器') == 0) {
              if (!typeDatas.page) {
                typeDatas.page = [];
              }
              typeDatas.page.push(n);
            }
          } else if (name.indexOf('流程定义') == 0) {
            if (!typeDatas.flowDefinition) {
              typeDatas.flowDefinition = [];
            }
            n._name = '[' + node.name + '] ' + n.name;
            typeDatas.flowDefinition.push(n);
          } else if (name.indexOf('数据管理') == 0) {
            if (!typeDatas.DmsAction) {
              typeDatas.DmsAction = [];
            }
            n._name = '[' + node.name + '] ' + n.name;
            typeDatas.DmsAction.push(n);
          } else if (name.indexOf('JavaScript模块功能') == 0) {
            if (!typeDatas.jsModuleFunction) {
              typeDatas.jsModuleFunction = [];
            }
            n._name = '[' + node.name + '] ' + n.name;
            typeDatas.jsModuleFunction.push(n);
          }

          if (list[i].children) {
            cascadeClassifyData(list[i]);
          }
        } else {
          cascadeClassifyData(list[i]);
        }
      }
    };

    for (var i = 0; i < dataList.length; i++) {
      var node = dataList[i];
      cascadeClassifyData(node);
    }

    console.log(typeDatas);
  };

  AppEvent.prototype.initFunctionTable = function (type) {
    var _self = this;
    var $table = $('#' + type, _self.$dialog).find('table');
    $table.bootstrapTable('destroy').bootstrapTable({
      data: typeDatas[type],
      idField: 'id',
      striped: false,
      showColumns: false,
      search: true,
      visibleSearch: true,
      showSearchButton: true,
      width: 500,
      pagination: true,
      singleSelect: true,
      // clickToSelect: true,
      pageSize: 10,
      pageList: [10, 50, 100, 200],
      columns: [
        {
          field: 'checked',
          checkbox: true
        },
        {
          field: 'id',
          title: 'id',
          visible: false,
          searchable: false
        },
        {
          field: '_name',
          title: '名称',
          align: 'left'
        }
      ],
      onClickCell: function (field, value, row, $element) {
        if (field != 'checked') {
          $($element.parents('tr').find('input').trigger('click'));
        }
        _self.selectEventData(row);
        return true;
      },
      onCheck: function (row, $element) {
        _self.selectEventData(row);
      }
    });

    $('tr:gt(0)', $table.find('tbody')).on('hover', function () {});
  };

  AppEvent.prototype.setValue = function (v) {
    this.options.idValue = v;
  };

  AppEvent.prototype.getData = function () {
    return this.$el.data('eventData');
  };

  AppEvent.prototype.createTabs = function () {
    var $div = $('<div>', {
      class: 'container-fluid',
      style: 'overflow-y:auto;height:600px;'
    });
    var $ul = $('<ul>', { class: 'nav nav-tabs', role: 'tablist' });
    var $liItems = [],
      $tabItems = [];
    var $tabContent = $('<div>', { class: 'tab-content' });
    for (var i = 0; i < types.length; i++) {
      $liItems.push(
        $('<li>', {
          role: 'presentation',
          class: i == 0 ? 'active' : ''
        }).append(
          $('<a>', {
            href: '#' + types[i],
            'aria-controls': types[i],
            role: 'tab',
            'data-toggle': 'tab',
            'aria-expanded': 'true'
          }).text(typeNames[i])
        )
      );

      $tabItems.push(
        $('<div>', {
          role: 'tabpanel',
          class: i == 0 ? 'tab-pane active' : 'tab-pane',
          id: types[i]
        }).append(
          i == 0
            ? $('<div>', { class: 'form-group' }).append(
                $('<div>', { class: 'col-sm-6' }).append(
                  $('<div>', { class: 'input-group' }).append(
                    $('<input>', {
                      type: 'text',
                      class: 'form-control',
                      id: 'ztreeSearch',
                      placeholder: '搜索...'
                    }),
                    $('<span>', { class: 'input-group-btn' }).append(
                      $('<button>', {
                        class: 'btn btn-success',
                        type: 'button',
                        id: 'ztreeSearchBtn'
                      }).text('搜索')
                    )
                  )
                )
              )
            : '',
          $('<div>', { class: 'form-group' }).append(
            $('<div>', { class: 'col-sm-12' }).append(
              i == 0 ? $('<ul>', { class: 'ztree', id: types[i] + '_ztree' }) : $('<table>', { class: 'table' })
            )
          )
        )
      );
    }
    $ul.append($liItems);
    $tabContent.append($tabItems);
    $div.append($ul, $tabContent);
    return $div[0].outerHTML;
  };

  $.fn.AppEvent = function () {
    if (arguments.length == 1 && typeof arguments[0] != 'string') {
      var options = arguments[0];
      var appEvent = new AppEvent(
        $.extend(
          true,
          {
            idValue: '',
            ztree: { params: [] }, //ztree的相关配置
            okCallback: $.noop, //确定后的回调函数
            cancelCallback: $.noop, //取消后的回调函数
            clearCallback: $.noop //清除事件的回调函数
          },
          options
        ),
        $(this)
      ).init();
      $(this).data('AppEvent', appEvent);
      return appEvent;
    } else {
      var appEvent = $(this).data('AppEvent');
      var method = appEvent[arguments[0]];
      if (method && $.isFunction(method)) {
        var args = Array.prototype.slice.call(arguments);
        args.splice(0, 1);
        return method.apply(appEvent, args);
      }
    }
  };
});
