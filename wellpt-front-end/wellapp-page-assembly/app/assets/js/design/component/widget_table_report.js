/**
 * 表格报表组件定义js
 */
define([
  'appContext',
  'design_commons',
  'constant',
  'commons',
  'server',
  'bootbox',
  'formBuilder',
  'appModal',
  'select2',
  'wSelect2'
], function (appContext, designCommons, constant, commons, server, bootbox, formBuilder, appModal) {
  var component = $.ui.component.BaseComponent();
  var StringUtils = commons.StringUtils;
  var collectClass = 'w-configurer-option';
  var reportTableFieldParameters = [];
  var reportXmlConfCache = {};
  var clearChecked = function (row) {
    row.checked = false;
    return row;
  };

  var onEditHidden = function (field, row, $el, reason) {
    $el.closest('table').bootstrapTable('resetView');
  };
  var checkedFormat = function (value, row, index) {
    if (value) {
      return true;
    }
    return false;
  };

  var clearInputValue = function ($container) {
    $container.find('.w-configurer-option').each(function () {
      var $element = $(this);
      var type = $element.attr('type');
      if (type == 'text' || type == 'hidden') {
        $element.val('');
      } else if (type == 'checkbox' || type == 'radio') {
        $element.prop('checked', false);
      }
      $element.trigger('change');
    });
  };

  var configurer = $.ui.component.BaseComponentConfigurer();
  configurer.prototype.initBaseInfo = function (configuration, $container) {
    var _self = this;
    designCommons.setElementValue(configuration, $container);

    // 加载的JS模块
    $('#jsModule', $container).wSelect2({
      serviceName: 'appJavaScriptModuleMgr',
      params: {
        dependencyFilter: 'TableReportWidgetDevelopment'
      },
      labelField: 'jsModuleName',
      valueField: 'jsModule',
      remoteSearch: false,
      multiple: true
    });

    //报表操作
  };

  function getReportFileDetail(uuid) {
    var detail = null;
    server.JDS.call({
      service: 'rpFileRepositoryFacadeService.getRpFileDetail',
      data: [uuid],
      version: '',
      async: false,
      success: function (result) {
        if (result.success) {
          detail = result.data;
          reportXmlConfCache[uuid] = detail;
        }
      }
    });
    return detail;
  }

  function explainReportFileQueryFieldParameters(uuid) {
    var getParameters = function (detail) {
      var parameters = $($(detail.content)[1]).find('datasource parameter');
      var parameterData = [];
      parameters.each(function (i, item) {
        parameterData.push({
          value: $(item).attr('name'),
          text: $(item).attr('name'),
          type: $(item).attr('type'),
          defaultValue: $(item).attr('default-value')
        });
      });
      return parameterData;
    };
    if (reportXmlConfCache[uuid]) {
      return getParameters(reportXmlConfCache[uuid]);
    }
    return getParameters(getReportFileDetail(uuid));
  }

  configurer.prototype.initReportTableType = function (reportTableTypeConfig, $container) {
    var _self = this;
    reportTableTypeConfig = reportTableTypeConfig || {};
    designCommons.setElementValue(reportTableTypeConfig, $container);
    if (reportTableTypeConfig.tableAdjust == undefined) {
      $('#tableAdjust').prop('checked', true); //默认勾选表格宽度自适应
    }
    if (reportTableTypeConfig.fixRows == undefined) {
      $('#fixRows').val('1'); // 默认冻结一行
    }

    if (reportTableTypeConfig.reportFolder == '0') {
      $('#reportFolder').parent().removeClass('active');
      $('#reportFolder').parent().find('.switch-open').hide();
      $('#reportFolder').parent().find('.switch-close').show();
    } else {
      $('#reportFolder').parent().addClass('active');
      $('#reportFolder').parent().find('.switch-open').show();
      $('#reportFolder').parent().find('.switch-close').hide();
    }

    if (reportTableTypeConfig.lazyData) {
      $('#lazyData').prop('checked', 'checked');
    }

    $('#reportFileConfig', $container).wSelect2({
      serviceName: 'rpFileRepositoryFacadeService',
      queryMethod: 'loadSelectData',
      labelField: 'reportFileConfigName',
      valueField: 'reportFileConfig',
      remoteSearch: false
    });

    $('#reportFileConfig', $container)
      .on('change', function () {
        var reportUuid = $(this).val();
        $('#reportFileStorePrefix', $container).val('');
        if (reportUuid) {
          //解析报表配置的查询参数列表
          reportTableFieldParameters = explainReportFileQueryFieldParameters(reportUuid);
          $('#reportFileStorePrefix', $container).val(reportXmlConfCache[reportUuid].fileId ? 'mongo:' : 'db:');
          _self.initReportTableParameter(
            reportUuid === reportTableTypeConfig.reportFileConfig ? reportTableTypeConfig.parameters : [],
            $container
          );
        }
      })
      .trigger('change');

    //报表操作
    $('#reportOperation', $container).wSelect2({
      labelField: 'reportOperationName',
      valueField: 'reportOperation',
      remoteSearch: false,
      multiple: true,
      data: [
        {
          id: '1',
          text: '在线打印'
        },
        {
          id: '3',
          text: 'PDF在线预览打印'
        },
        {
          id: '4',
          text: '导出PDF文档'
        },
        {
          id: '5',
          text: '导出WORD文档'
        },
        {
          id: '6',
          text: '导出EXCEL文档'
        },
        {
          id: '9',
          text: '分页'
        }
      ]
    });
    $('#reportOperation', $container)
      .on('change', function () {
        var val = $(this).val();
        $('#exportFileName', $container).hide();
        if (val) {
          var vals = val.split(';');
          if (vals.indexOf('3') != -1 && vals.indexOf('4') != -1 && vals.indexOf('5') != -1) {
            $('#exportFileName', $container).show();
          }
        }
      })
      .trigger('change');

    // 报表内JS，以script方式引入报表
    $('#embedJS', $container).wSelect2({
      serviceName: 'appJavaScriptModuleMgr',
      params: {
        dependencyFilter: 'TableReportAdditionalJS'
      },
      labelField: 'embedJSName',
      valueField: 'embedJS',
      remoteSearch: false,
      multiple: true
    });

    $('#reportFolder')
      .parent()
      .off()
      .on('click', function () {
        if ($(this).hasClass('active')) {
          $(this).removeClass('active');
          $('#reportFolder').val('0');
          $(this).find('.switch-open').hide();
          $(this).find('.switch-close').show();
        } else {
          $(this).addClass('active');
          $('#reportFolder').val('1');
          $(this).find('.switch-open').show();
          $(this).find('.switch-close').hide();
        }
      });
  };

  configurer.prototype.initReportTableParameter = function (parameters, $container) {
    var parameterData = parameters || [];
    var $parameterSearchInfoTable = $('#table_parameter_search_info', $container);
    var parameterRowBean = {
      checked: false,
      uuid: '',
      name: '',
      label: '',
      defaultValue: '',
      queryOptions: {
        queryTypeLabel: '文本框',
        queryType: 'text'
      }
    };
    // 定义添加，删除，上移，下移4按钮事件
    formBuilder.bootstrapTable.initTableTopButtonToolbar('table_parameter_search_info', 'parameter', $container, parameterRowBean);
    $parameterSearchInfoTable.bootstrapTable('destroy').bootstrapTable({
      data: parameterData,
      idField: 'uuid',
      showColumns: true,
      striped: true,
      width: 500,
      onEditableHidden: onEditHidden,
      onEditableSave: function (field, row, oldValue, $el) {
        // 选择名称时，标题为空，设置标题为选择的名称
        var update = false;
        if (field == 'name' && StringUtils.isBlank(row.label)) {
          row.label = row.name;
          update = true;
        }

        // 设置默认值
        if (field == 'name') {
          $.each(reportTableFieldParameters, function (i, item) {
            if (item.value === row.name) {
              row.defaultValue = item.defaultValue;
              update = true;
              return false;
            }
          });
        }

        if (update) {
          var data = $parameterSearchInfoTable.bootstrapTable('getData');
          $.each(data, function (index, rowData) {
            if (row == rowData) {
              $parameterSearchInfoTable.bootstrapTable('updateRow', index, rowData);
            }
          });
        }
      },
      toolbar: $('#div_parameter_search_info_toolbar', $container),
      columns: [
        {
          field: 'checked',
          formatter: checkedFormat,
          checkbox: true
        },
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'label',
          title: '标题',
          editable: {
            type: 'text',
            showbuttons: false,
            onblur: 'submit',
            mode: 'inline'
          }
        },
        {
          field: 'name',
          title: '参数',
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            emptytext: '请选择',
            source: function () {
              return reportTableFieldParameters;
            }
          }
        },
        {
          field: 'queryOptions',
          title: '查询类型',
          editable: {
            onblur: 'cancel',
            type: 'wCustomForm',
            placement: 'bottom',
            savenochange: true,
            value2input: designCommons.bootstrapTable.queryFieldType.value2input,
            input2value: designCommons.bootstrapTable.queryFieldType.input2value,
            value2display: designCommons.bootstrapTable.queryFieldType.value2display,
            validate: function (value) {
              if (!value || !value.queryType) {
                return '请选择控件类型!';
              }
            }
          }
        },
        {
          field: 'defaultValue',
          title: '默认值',
          editable: {
            onblur: 'submit',
            type: 'text',
            mode: 'inline',
            showbuttons: false
          }
        }
      ]
    });
  };

  configurer.prototype.onLoad = function ($container, options) {
    // 初始化页签项
    $('#widget_bootstrap_table_tabs ul a', $container).on('click', function (e) {
      e.preventDefault();
      $(this).tab('show');
      var panelId = $(this).attr('href');
      $(panelId + ' .definition_info').bootstrapTable('resetView');
    });
    var configuration = $.extend(true, {}, options.configuration);
    this.initBaseInfo(configuration, $container);
    this.initReportTableType(configuration.reportTableTypeConfig, $container);
  };
  configurer.prototype.onOk = function ($container) {
    if (this.component.isReferenceWidget()) {
      return;
    }
    var opt = designCommons.collectConfigurerData($('#widget_report_base_info', $container), collectClass);
    opt.reportTableTypeConfig = {};
    opt.reportGraphTypeConfig = {};

    //表格式报表配置收集
    var reportFileConfigSelect = $('#reportFileConfig').select2('data');
    opt.reportTableTypeConfig.reportFileConfig = reportFileConfigSelect.id;
    opt.reportTableTypeConfig.reportFileConfigName = reportFileConfigSelect.text;
    //报表存储前缀
    opt.reportTableTypeConfig.reportFileStorePrefix = $('#reportFileStorePrefix', $container).val();
    opt.reportTableTypeConfig.reportFolder = $('#reportFolder').val();
    opt.reportTableTypeConfig.embedJS = $('#embedJS').val();
    opt.reportTableTypeConfig.embedJSName = $('#embedJSName').val();
    opt.reportTableTypeConfig.fixRows = $('#fixRows').val();

    var $parameterTable = $('#table_parameter_search_info', $container);
    var parameterData = $parameterTable.bootstrapTable('getData');
    parameterData = $.map(parameterData, clearChecked);
    for (var i = 0; i < parameterData.length; i++) {
      var node = parameterData[i];
      if (StringUtils.isBlank(node.name)) {
        appModal.error('参数不允许为空！');
        return false;
      }
    }
    opt.reportTableTypeConfig.parameters = parameterData;

    //报表操作
    var ropr = $('#reportOperation').select2('data');
    opt.reportTableTypeConfig.reportOperation = '';
    opt.reportTableTypeConfig.reportOperationName = '';
    opt.reportTableTypeConfig.exportFileName = $('#exportFileName').val();
    opt.reportTableTypeConfig.tableAdjust = $('#tableAdjust').prop('checked');
    opt.reportTableTypeConfig.lazyData = $('#lazyData').prop('checked');
    if (ropr) {
      for (var i = 0, len = ropr.length; i < len; i++) {
        opt.reportTableTypeConfig.reportOperation += ropr[i].id;
        opt.reportTableTypeConfig.reportOperationName += ropr[i].text;
        if (i < len - 1) {
          opt.reportTableTypeConfig.reportOperation += ';';
          opt.reportTableTypeConfig.reportOperationName += ';';
        }
      }
    }

    //图标式报表配置收集

    this.component.options.configuration = $.extend({}, opt);
  };

  // configurer.prototype.getTemplateUrl = function () {
  //     var wtype = this.component.options.wtype.replace(/([A-Z])/g, "_$1").toLowerCase();
  //     wtype = wtype.replace("__", "_");
  //     return ctx + "/web/app/page/configurer/report/widget" + wtype.substring(1);
  // };

  component.prototype.usePropertyConfigurer = function () {
    return true;
  };
  // 返回属性配置器
  component.prototype.getPropertyConfigurer = function () {
    return configurer;
  };
  component.prototype.create = function () {
    $(this.element).find('.widget-body').html(this.options.content);
  };
  component.prototype.getDefinitionJson = function () {
    var options = this.options;
    var id = this.getId();
    options.id = id;
    return options;
  };

  return component;
});
