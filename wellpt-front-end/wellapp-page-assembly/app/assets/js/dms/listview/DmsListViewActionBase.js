define(['jquery', 'commons', 'constant', 'server', 'DmsDataServices'], function ($, commons, constant, server, DmsDataServices) {
  var KEY_DMS_ID = 'dms_id';
  var StringUtils = commons.StringUtils;
  var DmsListViewActionBase = function (options) {
    var self = this;
    self.options = options;
    self.ui = options.ui;
    var listViewWidgets = null;
    if (options.ui.getWtype && (options.ui.getWtype() === 'wMobileListView' || options.ui.getWtype() === 'wBootstrapTable')) {
      // wMobileListView或者wBootstrapTable优先
    } else if (
      options.ui.getChildWidgetByType &&
      (listViewWidgets = options.ui.getChildWidgetByType(['wMobileListView', 'wBootstrapTable'])).length > 0
    ) {
      self.ui = listViewWidgets[0] || options.ui;
    }
    self.dmsDataServices = new DmsDataServices();
    self.dmsDataServices.eventOptions = options;
  };

  $.extend(DmsListViewActionBase.prototype, {
    checkSelection: function (checkOptions) {
      var _self = this;
      var options = checkOptions || _self.options;
      var ui = _self.ui || _self.options.ui;
      // 选中数据检验
      var selection = ui.getSelections();
      // 如果按钮是行末按钮，则采用的是options.rowData，代表当前按钮所在的行，
      selection = selection.length == 0 ? options.rowData : selection;
      var selectionLength = selection == null ? 0 : selection.length;
      var actionFunction = options.appFunction || _self.options.appFunction || {};
      var params = options.params || {};
      var promptMsg = params.promptMsg ? params.promptMsg : actionFunction.promptMsg;
      var singleSelect = actionFunction.singleSelect;
      if (selectionLength === 0 && StringUtils.isNotBlank(promptMsg)) {
        appModal.error(promptMsg);
        return false;
      } else if (selectionLength === 0) {
        ui.refresh(false);
        return false;
      } else if (singleSelect == true && selectionLength > 1) {
        // 单选提示
        var singleSelectPromptMsg = params.singleSelectPromptMsg ? params.singleSelectPromptMsg : actionFunction.singleSelectPromptMsg;
        appModal.error(singleSelectPromptMsg);
        return false;
      }
      return true;
    },
    getUrlParams: function (paramOptions) {
      var _self = this;
      var options = paramOptions || _self.options;
      var row = options.rowdata;
      var ui = _self.ui;

      // 视图相关参数
      var viewInfo = _self.getViewInfo(row);
      var idKey = viewInfo.idKey;
      var idValue = viewInfo.idValue;
      var dataStoreId = viewInfo.dataStoreId;

      // 数据管理参数
      var dmsId = $(ui.element).data(KEY_DMS_ID);
      if (!dmsId) {
        dmsId = $(ui.element).closest('.ui-wDataManagementViewer').attr('id');
      }

      var acId = options.appFunction && options.appFunction.id;
      var lvId = ui.getId();
      if (dmsId == null && ui.getParent) {
        dmsId = ui.getParent().getId();
      }
      var urlParams = {
        idKey: idKey,
        idValue: idValue,
        dataStoreId: dataStoreId,
        dms_id: dmsId,
        ac_id: acId,
        lv_id: lvId
      };

      // 表单定义UUID
      if (viewInfo.formUuid) {
        urlParams.formUuid = viewInfo.formUuid;
      }
      return urlParams;
    },
    getViewInfo: function (row) {
      var ui = this.ui;
      var configuration = ui.getConfiguration();
      var idKeys = [];
      var idValues = [];
      if (appContext.isMobileApp() && configuration.primaryField) {
        idKeys.push(configuration.primaryField);
      } else {
        $.each(configuration.columns, function () {
          if (this.idField === '1') {
            idKeys.push(this.name);
          }
        });
      }
      if (idKeys.length === 0) {
        if (row) {
          if (row.hasOwnProperty('uuid')) {
            idKeys.push('uuid');
          } else if (row.hasOwnProperty('UUID')) {
            idKeys.push('UUID');
          }
        }
        if (idKeys.length === 0) {
          idKeys.push('UUID');
        }
      }
      for (var i = 0; i < idKeys.length; i++) {
        if (row) {
          idValues.push(row[idKeys[i]]);
        } else {
          idValues.push('');
        }
      }
      var idKey = idKeys.join(';');
      var idValue = idValues.join(';');
      var dataStoreId = ui.getDataProvider().options.dataStoreId;
      var viewInfo = {
        idKey: idKey,
        idValue: idValue,
        dataStoreId: dataStoreId
      };
      if (row) {
        var formUuid = row.formUuid || row.FORM_UUID || '';
        if (StringUtils.isNotBlank(formUuid)) {
          viewInfo.formUuid = formUuid;
        }
      }
      return viewInfo;
    }
  });

  return DmsListViewActionBase;
});
