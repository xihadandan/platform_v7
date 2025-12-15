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
  var BizProcessNodeView = function () {
    BizDataView.apply(this, arguments);
  };
  commons.inherit(BizProcessNodeView, BizDataView, {
    // 初始化
    init: function (options) {
      var _self = this;
      options.extraParams = options.extraParams || {};
      _self.options = options;
      _self.processNodeInstUuid = options.processNodeInstUuid;
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
        url: '/proxy/api/biz/process/node/instance/get',
        data: {
          processNodeInstUuid: _self.processNodeInstUuid,
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

      // 加载业务事项办件实例数据
      _self.loadProcessItemInstances();
    },
    // 加载业务事项办件实例数据
    loadProcessItemInstances: function () {
      var _self = this;
      var $container = _self.$element;
      var itemPlaceHolder = _self.bizData.itemPlaceHolder;
      var tableId = UUID.createUUID();
      var $processItemContainer =
        $("th[blockcode='" + itemPlaceHolder + "']", $container).length > 0
          ? $("th[blockcode='" + itemPlaceHolder + "']", $container)
          : $("td[blockcode='" + itemPlaceHolder + "']", $container);
      $processItemTable = $processItemContainer.closest('table');
      $processItemTable.after($(`<div id='${tableId}'></div>`));
      appContext.renderWidget({
        renderTo: '#' + tableId,
        widgetDefId: 'wBootstrapTable_CA053B0228100001616C1340FED0E270',
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
          $processItemTable.css({ marginBottom: '10px' });
        },
        onPrepare: {
          wBootstrapTable_CA053B0228100001616C1340FED0E270: function () {
            listViewWidget = this;
            listViewWidget.develops.push({
              beforeLoadData: function () {
                listViewWidget.addParam('processNodeInstUuid', _self.processNodeInstUuid);
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
    reload: function (processNodeInstUuid) {
      if (StringUtils.isNotBlank(processNodeInstUuid)) {
        var url = `/biz/process/node/instance/view?processNodeInstUuid=${processNodeInstUuid}`;
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
        url: '/proxy/api/biz/process/node/instance/save',
        data: bizData,
        success: function (result) {
          var processNodeInstUuid = result.data;
          appModal.success('保存成功！', function () {
            _self.reload(processNodeInstUuid);
          });
        }
      });
    }
  });
  return BizProcessNodeView;
});
