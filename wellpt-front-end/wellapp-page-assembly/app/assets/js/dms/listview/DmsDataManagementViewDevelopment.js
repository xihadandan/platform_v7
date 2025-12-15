define([
  'jquery',
  'commons',
  'constant',
  'server',
  'appContext',
  'ListViewWidgetDevelopment',
  'DmsListViewActionBase',
  'DmsDataServices'
], function ($, commons, constant, server, appContext, ListViewWidgetDevelopment, DmsListViewActionBase, DmsDataServices) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var ACTION_OPEN_VIEW = 'open_view';
  var ROW_CHECK_ITEM = 'rowCheckItem';
  // 数据管理——视图二开
  var DmsDataManagementViewDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
    var options = {
      ui: this.getWidget()
    };
    this.dmsListViewActionBase = new DmsListViewActionBase(options);
    this.dmsDataServices = new DmsDataServices();
  };
  commons.inherit(DmsDataManagementViewDevelopment, ListViewWidgetDevelopment, {
    beforeRender: function () {
      var _self = this;
      var configuration = _self.getViewConfiguration();
      if (configuration.dmsWidgetDefinition) {
        var dataProvider = _self.getDataProvider();
        if (dataProvider.options.proxy == null || dataProvider.options.proxy.type == null) {
          dataProvider.options.proxy = dataProvider.options.proxy || {};
          dataProvider.options.proxy.type = 'com.wellsoft.pt.dms.core.criteria.DmsDyformCriteria';
          dataProvider.options.proxy.extras = {
            // dms_id : configuration.dmsWidgetDefinition.srcId || configuration.dmsWidgetDefinition.id
            // bug#46019
            dms_id: configuration.dmsWidgetDefinition.id
          };
        }
      }
    },
    onClickRow: function (rowNum, row, $element, field) {
      var _self = this;
      // 选中复选框，不处理
      if (ROW_CHECK_ITEM === field) {
        return;
      }
      var widget = _self.getWidget();
      // 功能ID参数
      var paramOptions = {
        appFunction: {
          id: ACTION_OPEN_VIEW
        },
        // 行数据
        rowdata: row
      };
      var urlParams = _self.dmsListViewActionBase.getUrlParams(paramOptions);
      // 树结点目录行点击直接返回
      if (!row.hasOwnProperty(urlParams.idKey)) {
        return;
      }

      // 行点击打开文档模式
      var documentOpenMode = _self.getDocumentOpenMode();
      if (documentOpenMode == '2') {
        urlParams.target = '_dialog';
        _self.dmsDataServices.openDialog({
          urlParams: urlParams,
          ui: widget
        });
      } else {
        _self.dmsDataServices.openWindow({
          urlParams: urlParams,
          ui: widget
        });
      }
    },
    getDocumentOpenMode: function () {
      var _self = this;
      if (_self.documentOpenMode) {
        return _self.documentOpenMode;
      }
      var configuration = _self.getViewConfiguration();
      var dmsWidgetDefinition = configuration.dmsWidgetDefinition;
      if (dmsWidgetDefinition && dmsWidgetDefinition.configuration && dmsWidgetDefinition.configuration.document) {
        _self.documentOpenMode = dmsWidgetDefinition.configuration.document.openMode;
      }
      return _self.documentOpenMode;
    }
  });

  return DmsDataManagementViewDevelopment;
});
