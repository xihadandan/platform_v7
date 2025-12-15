/**
 * 表格组件的api，主要提供表格组件的二开脚本代码调用
 */
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
      'appContext',
      'DmsListViewActionBase',
      'DmsDataServices',
      'api-commons'
    ], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function (jquery, commons, server, constant, appModal, appContext, DmsListViewActionBase, DmsDataServices, apiCommons) {
  'use strict';

  var wBootstrapTableApi = function ($widget) {
    this.$widget = $widget; //表格组件
    this.$table = $widget.$tableElement; //表格元素
  };

  /**
   * 获取主键字段
   * @param $tableElement
   * @returns {*}
   */
  wBootstrapTableApi.prototype.getPrimaryColumnName = function ($tableElement) {
    $tableElement = $tableElement || this.$table;
    if ($tableElement) {
      var columns = this.getWidget($tableElement).getConfiguration().columns;
      for (var i = 0, len = columns.length; i < len; i++) {
        if (columns[i].idField == '1') {
          return columns[i].name;
        }
      }
    }
    return null;
  };

  /**
   * 获取表格的选中行数据
   * @param $tableElement
   */
  wBootstrapTableApi.prototype.getSelectedData = function ($tableElement) {
    $tableElement = $tableElement || this.$table;
    if ($tableElement) {
      return $tableElement.bootstrapTable('getSelections');
    }
  };

  /**
   * 获取表格数据
   * @param dataIndex 下标
   * @param $tableElement
   * @returns {*}
   */
  wBootstrapTableApi.prototype.getData = function (dataIndex, $tableElement) {
    $tableElement = $tableElement || this.$table;
    if ($tableElement && dataIndex != undefined) {
      return $tableElement.bootstrapTable('getData')[dataIndex];
    }
    if ($tableElement) {
      return $tableElement.bootstrapTable('getData');
    }
  };

  /**
   * 获取表格的选中主键id值集合
   * @param $tableElement
   */
  wBootstrapTableApi.prototype.getSelectedIds = function ($tableElement) {
    $tableElement = $tableElement || this.$table;
    var ids = [];
    if ($tableElement) {
      var primaryColumn = this.getPrimaryColumnName($tableElement);
      if (primaryColumn) {
        var data = $tableElement.bootstrapTable('getSelections');
        for (var i = 0, len = data.length; i < len; i++) {
          ids.push(data[i][primaryColumn]);
        }
        return ids;
      }
      console.error('表格无配置主键字段');
    }
    return ids;
  };

  /**
   * 判断是否存在选中的一行的某个字段值在指定值范围内
   * @param $tableElement 表格
   * @param field  字段
   * @param valueInclude  值范围数组
   * @returns {boolean}
   */
  wBootstrapTableApi.prototype.existOneSelRowFieldInValues = function (field, valueInclude, $tableElement) {
    $tableElement = $tableElement || this.$table;
    if (Object.prototype.toString.call(valueInclude) === '[object String]' && valueInclude.length > 0) {
      valueInclude = valueInclude.split(',');
    }
    if (
      $tableElement != undefined &&
      field != undefined &&
      valueInclude != undefined &&
      Object.prototype.toString.call(valueInclude) === '[object Array]'
    ) {
      var isLineEndBtnClick = $(window.event.target).parent().is('.div_lineEnd_toolbar');
      var data = isLineEndBtnClick
        ? (function () {
            var i = parseInt($(window.event.target).parents('tr').attr('data-index'));
            return [$tableElement.bootstrapTable('getData')[i]];
          })()
        : $tableElement.bootstrapTable('getSelections');
      for (var i = 0, len = data.length; i < len; i++) {
        if (valueInclude.indexOf(data[i][field]) != -1) {
          return true;
        }
      }
    }
    return false;
  };

  /**
   * 判断是否所有选中行的某个字段值在指定值范围内
   * @param $tableElement
   * @param field 字段
   * @param valueInclude 值范围数组
   * @returns {boolean}
   */
  wBootstrapTableApi.prototype.isAllSelRowFieldInValues = function (field, valueInclude, $tableElement) {
    $tableElement = $tableElement || this.$table;
    if (Object.prototype.toString.call(valueInclude) === '[object String]' && valueInclude.length > 0) {
      valueInclude = valueInclude.split(',');
    }
    if (
      $tableElement != undefined &&
      field != undefined &&
      valueInclude != undefined &&
      Object.prototype.toString.call(valueInclude) === '[object Array]'
    ) {
      var isLineEndBtnClick = $(window.event.target).parent().is('.div_lineEnd_toolbar');
      var data = isLineEndBtnClick
        ? (function () {
            var i = parseInt($(window.event.target).parents('tr').attr('data-index'));
            return [$tableElement.bootstrapTable('getData')[i]];
          })()
        : $tableElement.bootstrapTable('getSelections');
      for (var i = 0, len = data.length; i < len; i++) {
        if (valueInclude.indexOf(data[i][field]) == -1) {
          return false;
        }
      }
    }
    return true;
  };

  /**
   * 刷新表格数据
   * @param $tableElement
   */
  wBootstrapTableApi.prototype.refresh = function ($tableElement) {
    $tableElement = $tableElement || this.$table;
    $tableElement.bootstrapTable('refresh');
  };

  /**
   * 隐藏表格的字段列
   * @param $tableElement
   * @param fields
   */
  wBootstrapTableApi.prototype.hideColumns = function (fields, $tableElement) {
    $tableElement = $tableElement || this.$table;
    if (Object.prototype.toString.call(fields) === '[object Array]' && fields.length > 0) {
      for (var i = 0, len = fields.length; i < len; i++) {
        $tableElement.bootstrapTable('hideColumn', fields[i]);
      }
    }
  };

  /**
   * 显示表格的字段列
   * @param $tableElement
   * @param fields
   */
  wBootstrapTableApi.prototype.showColumns = function (fields, $tableElement) {
    $tableElement = $tableElement || this.$table;
    if (Object.prototype.toString.call(fields) === '[object Array]' && fields.length > 0) {
      for (var i = 0, len = fields.length; i < len; i++) {
        $tableElement.bootstrapTable('showColumn', fields[i]);
      }
    }
  };

  /**
   * 隐藏按钮的配置项
   * @param buttonCodes
   * @param $tableElement
   */
  wBootstrapTableApi.prototype.hideButtionConf = function (buttonCodes, $tableElement) {
    $tableElement = $tableElement || this.$table;
    var $widget = this.getWidget($tableElement);
    var configuration = $widget.getConfiguration();
    if (Object.prototype.toString.call(buttonCodes) === '[object String]' && buttonCodes.length > 0) {
      buttonCodes = buttonCodes.split(',');
    }

    for (var i = 0; i < configuration.buttons.length; i++) {
      if (buttonCodes.indexOf(configuration.buttons[i].code) != -1) {
        configuration.buttons[i].isHide = true;
      }
    }
  };

  /**
   * 移除按钮的配置
   * @param buttonCodes
   * @param $tableElement
   */
  wBootstrapTableApi.prototype.removeButtionConf = function (buttonCodes, $tableElement) {
    $tableElement = $tableElement || this.$table;
    var $widget = this.getWidget($tableElement);
    var configuration = $widget.getConfiguration();
    if (Object.prototype.toString.call(buttonCodes) === '[object String]' && buttonCodes.length > 0) {
      buttonCodes = buttonCodes.split(',');
    }

    for (var i = 0; i < configuration.buttons.length; i++) {
      if (buttonCodes.indexOf(configuration.buttons[i].code) != -1) {
        configuration.buttons.splice(i, 1);
      }
    }
  };

  wBootstrapTableApi.prototype.getWidget = function ($tableElement) {
    return $tableElement != undefined
      ? (function () {
          return $('#' + $tableElement.attr('id').replace('_table', '')).data('uiWBootstrapTable');
        })()
      : this.$widget;
  };

  wBootstrapTableApi.prototype.toggleButtons = function (buttonCodes, state, $tableElement) {
    $tableElement = $tableElement || this.$table;
    if (Object.prototype.toString.call(buttonCodes) === '[object String]' && buttonCodes.length > 0) {
      buttonCodes = buttonCodes.split(',');
    }

    if (Object.prototype.toString.call(buttonCodes) === '[object Array]' && buttonCodes.length > 0) {
      for (var i = 0, len = buttonCodes.length; i < len; i++) {
        var wBootstrapDivId = $tableElement.attr('id').replace('_table', '');
        var $wBootstrapDiv = $('#' + wBootstrapDivId);
        for (var i = 0, len = buttonCodes.length; i < len; i++) {
          if (state === 'show') {
            $wBootstrapDiv.find('.btn_class_' + buttonCodes[i] + ',.li_class_' + buttonCodes[i]).show();
          } else if (state === 'hide') {
            $wBootstrapDiv.find('.btn_class_' + buttonCodes[i] + ',.li_class_' + buttonCodes[i]).hide();
          } else if (state === 'remove') {
            $wBootstrapDiv.find('.btn_class_' + buttonCodes[i] + ',.li_class_' + buttonCodes[i]).remove();
          }
        }
      }
    }
  };

  /**
   * 隐藏表格的按钮
   * @param $tableElement
   * @param buttonCodes 按钮编码数组
   */
  wBootstrapTableApi.prototype.hideButtons = function (buttonCodes, $tableElement) {
    $tableElement = $tableElement || this.$table;
    this.toggleButtons(buttonCodes, 'hide', $tableElement);
  };

  /**
   * 显示表格的按钮
   * @param $tableElement
   * @param buttonCodes
   */
  wBootstrapTableApi.prototype.showButtons = function (buttonCodes, $tableElement) {
    $tableElement = $tableElement || this.$table;
    this.toggleButtons(buttonCodes, 'show', $tableElement);
  };

  /**
   * 删除按钮
   * @param $tableElement
   * @param buttonCodes
   */
  wBootstrapTableApi.prototype.removeButtons = function (buttonCodes, $tableElement) {
    $tableElement = $tableElement || this.$table;
    this.toggleButtons(buttonCodes, 'remove', $tableElement);
  };

  /**
   * 新窗口开单据
   * @param dataIndex
   */
  wBootstrapTableApi.prototype.openDocumentWindow = function (dataIndex) {
    this.openDocument(dataIndex, '_blank');
  };

  /**
   * 弹窗开单据
   * @param dataIndex
   */
  wBootstrapTableApi.prototype.openDocumentDialog = function (dataIndex) {
    this.openDocument(dataIndex, '_dialog');
  };

  /**
   * 打开单据
   * @param winType       窗口类型：_blank 新开窗口  _dialog 弹窗
   * @param edit          是否编辑
   * @param dataIndex     基于第几行打开单据，可为空，则打开行点击的那一行
   *
   */
  wBootstrapTableApi.prototype.openDocument = function (winType, edit, dataIndex) {
    var dmsListViewActionBase = new DmsListViewActionBase({
      ui: this.$widget
    });
    var dmsDataServices = new DmsDataServices();
    var dataIndex = dataIndex != undefined ? dataIndex : $(window.event.target).parents('tr').attr('data-index');
    // 功能ID参数
    var paramOptions = {
      appFunction: {
        id: 'open_view'
      },
      // 行数据
      rowdata: this.getData(dataIndex)
    };
    if (!paramOptions.rowdata) {
      console.warn('无行数据选择');
      return false;
    }
    var urlParams = dmsListViewActionBase.getUrlParams(paramOptions);
    //urlParams.ep_displayAsLabel = edit !== true;
    urlParams.ep_view_mode = edit ? 1 : 0;

    if (winType == undefined || winType == '_blank') {
      dmsDataServices.openWindow({
        urlParams: urlParams,
        ui: this.$widget
      });
    } else if (winType == '_dialog') {
      dmsDataServices.openDialog({
        urlParams: urlParams,
        ui: this.$widget
      });
    }
  };

  /**
   * 打开流程单据
   * @param customUrlParams 自定义url的参数
   * @param dataIndex
   */
  wBootstrapTableApi.prototype.openWorkflowDocument = function (customUrlParams, dataIndex) {
    var dataIndex = dataIndex != undefined ? dataIndex : $(window.event.target).parents('tr').attr('data-index');
    var row = this.getData(dataIndex);
    var url = ctx + '/workflow/work/v53/view/work?taskInstUuid={0}&flowInstUuid={1}';
    var taskInstUuid = row.task_inst_uuid || row.TASK_INST_UUID || row.taskInstUuid || '';
    var flowInstUuid = row.flow_inst_uuid || row.FLOW_INST_UUID || row.flowInstUuid || '';
    var sb = new commons.StringBuilder();
    sb.appendFormat(url, taskInstUuid, flowInstUuid);
    url = sb.toString();
    url = commons.UrlUtils.appendUrlParams(url, customUrlParams);
    var options = {};
    options.url = url;
    options.ui = this.getWidget();
    options.size = 'large';
    appContext.openWindow(options);
  };

  /**
   * 设置查无记录的表格提示语
   * @param winType
   * @param edit
   * @param dataIndex
   */
  wBootstrapTableApi.prototype.setNoRecordTip = function (tip, $tableElement) {
    $tableElement = $tableElement || this.$table;
    var $widget = this.getWidget($tableElement);
    var configuration = $widget.getConfiguration();
    configuration.formatNoMatchText = tip;
  };

  /**
   * 添加额外的查询条件
   */
  wBootstrapTableApi.prototype.addOtherConditions = function (conditions, $tableElement) {
    var widget = this.getWidget($tableElement);
    var otherConditions = widget.otherConditions || [];
    $.each(conditions, function (i, cond) {
      var index = -1;
      $.each(otherConditions, function (i, condition) {
        if (JSON.stringify(condition) == JSON.stringify(cond)) {
          index = i;
          return false;
        }
      });
      if (index == -1) {
        otherConditions.push(cond);
      }
    });
    widget.otherConditions = otherConditions;
  };

  /**
   * 情况额外查询条件,condition为空是清楚全部，否则清楚等于condition的一条额外查询条件
   */
  wBootstrapTableApi.prototype.clearOtherConditions = function (condition, $tableElement) {
    var widget = this.getWidget($tableElement);
    var otherConditions = widget.otherConditions;
    if (condition) {
      widget.otherConditions = $.map(otherConditions, function (cond) {
        if (JSON.stringify(condition) != JSON.stringify(cond)) {
          return cond;
        }
      });
    } else {
      widget.otherConditions = [];
    }
  };

  /**
   * 状态标记
   * @param uuids
   * @param markType {isDeleteMark: ,entityClassName:'',tableName:'',statusColumn:'',updateTimeColumn:''}
   * @param callback
   */
  wBootstrapTableApi.prototype.statusMarkRow = function (uuids, markType, callback) {
    this.$table.bootstrapTable('dmsMarkRows', uuids, markType, callback);
  };

  //公用api集成一起
  commons.inherit(wBootstrapTableApi, apiCommons, wBootstrapTableApi.prototype);

  return wBootstrapTableApi;
});
