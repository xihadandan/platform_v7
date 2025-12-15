define([
  'jquery',
  'server',
  'commons',
  'constant',
  'appContext',
  'appModal',
  'DmsDataServices',
  'BizDataView',
  'DmsActionDispatcher',
  'DyformExplain'
], function ($, server, commons, constant, appContext, appModal, DmsDataServices, BizDataView, DmsActionDispatcher, DyformExplain) {
  var JDS = server.JDS;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var UUID = commons.UUID;
  // 数据管理单据开发
  var BizProcessView = function () {
    BizDataView.apply(this, arguments);
  };
  commons.inherit(BizProcessView, BizDataView, {
    // 初始化
    init: function (options) {
      var _self = this;
      options.extraParams = options.extraParams || {};
      _self.options = options;
      _self.processInstUuid = options.processInstUuid;
      _self.formUuid = options.formUuid;
      _self.dataUuid = options.dataUuid;
      _self.dyformSelector = options.dyformSelector;
    },
    // 加载数据
    load: function () {
      var _self = this;
      var success = function () {
        _self.onLoadSuccess.apply(_self, arguments);
        _self.initLoadedData.apply(_self, arguments);
      };
      var failure = function () {
        _self.onLoadFailure.apply(_self, arguments);
      };

      JDS.restfulPost({
        url: '/proxy/api/biz/process/instance/get',
        data: {
          processInstUuid: _self.processInstUuid,
          formUuid: _self.formUuid,
          dataUuid: _self.dataUuid
        },
        success: function (result) {
          success.apply(_self, arguments);
        },
        error: function (error) {
          failure.apply(_self, arguments);
        }
      });
    },
    // 表单初始化成功回调
    onDyformInitSuccess: function () {
      var _self = this;
      _self._superApply(arguments);

      // 加载过程节点实例数据
      _self.loadProcessNodeInstances();
    },
    // 加载过程节点实例数据
    loadProcessNodeInstances: function () {
      var _self = this;
      var $container = _self.$element;
      var processNodePlaceHolder = _self.bizData.processNodePlaceHolder;
      var tableId = UUID.createUUID();
      var $processNodeContainer =
        $("th[blockcode='" + processNodePlaceHolder + "']", $container).length > 0
          ? $("th[blockcode='" + processNodePlaceHolder + "']", $container)
          : $("td[blockcode='" + processNodePlaceHolder + "']", $container);
      $processNodeTable = $processNodeContainer.closest('table');
      $processNodeTable.after($(`<div id='${tableId}'></div>`));
      appContext.renderWidget({
        renderTo: '#' + tableId,
        widgetDefId: 'wBootstrapTable_CA053ADFABF00001BCC910B01F4D1CA6',
        forceRenderIfConflict: true,
        callback: function () {
          $('#' + tableId + ' > .ui-wBootstrapTable')
            .css({
              overflow: 'auto'
            })
            .children('.bootstrap-table')
            .css({
              marginTop: '0px'
            });
          $processNodeTable.css({ marginBottom: '10px' });
        },
        onPrepare: {
          wBootstrapTable_CA053ADFABF00001BCC910B01F4D1CA6: function () {
            listViewWidget = this;
            listViewWidget.develops.push({
              beforeLoadData: function () {
                listViewWidget.addParam('processInstUuid', _self.processInstUuid);
                listViewWidget.addParam('loadItemInstCount', true);
              }
            });
          }
        }
      });
    },
    getActions: function () {
      var _self = this;
      // 草稿
      return [
        { id: 'save', name: '保存', validate: false },
        { id: 'printForm', name: '打印表单', validate: false }
      ];
    },
    // 重新加载单据
    reload: function (processInstUuid) {
      if (StringUtils.isNotBlank(processInstUuid)) {
        var url = `/biz/process/instance/view?processInstUuid=${processInstUuid}`;
        appContext.getWindowManager().refresh(url);
      } else {
        appContext.getWindowManager().refresh();
      }
    },
    // 保存
    save: function (options) {
      var _self = this;
      var bizData = _self.bizData;
      bizData.dyFormData = options.data.dyFormData;
      JDS.restfulPost({
        url: '/proxy/api/biz/process/instance/save',
        data: bizData,
        success: function (result) {
          var processInstUuid = result.data;
          appModal.success('保存成功！', function () {
            _self.reload(processInstUuid);
          });
        }
      });
    }
  });
  return BizProcessView;
});
