define([
  'appContext',
  'design_commons',
  'constant',
  'commons',
  'server',
  'bootbox',
  'formBuilder',
  'appModal',
  'design_commons',
  'select2',
  'wSelect2'
], function (appContext, DesignUtils, constant, commons, server, bootbox, formBuilder, appModal, designCommons) {
  var component = $.ui.component.BaseComponent();
  var StringUtils = commons.StringUtils;
  var collectClass = 'w-configurer-option';
  var clearChecked = function (row) {
    row.checked = false;
    return row;
  };
  var isJSON = function (str) {
    try {
      var obj = JSON.parse(str);
      if (typeof obj == 'object') {
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  };
  var checkRequire = function (propertyNames, options, $container) {
    for (var i = 0; i < propertyNames.length; i++) {
      var propertyName = propertyNames[i];
      if (StringUtils.isBlank(options[propertyName])) {
        var title = $("label[for='" + propertyName + "']", $container).text();
        appModal.error('基础信息的' + '' + title.replace('*', '') + '不允许为空！');
        return false;
      }
    }
    return true;
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
  // 列定义bean
  var columnRowBean = {
    checked: false,
    uuid: '',
    title: '',
    name: '',
    dataType: '',
    width: '',
    hidden: '1',
    sortable: '0',
    keywordQuery: '1',
    editable: '0',
    defaultValue: '',
    controlOptions: {
      queryTypeLabel: '文本框',
      queryType: 'text'
    },
    validateRules: {
      validateRuleLabel: '',
      validateRegex: ''
    }
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

  var loadColumnNames = function (alwaysLoad) {
    var dataProviderId = $('#dataProviderId').val();
    var dataProviderIdKey = 'data-' + dataProviderId;
    var columnNames = $('#dataProviderId').data(dataProviderIdKey);
    if (columnNames == null || typeof columnNames === 'undefined') {
      server.JDS.call({
        service: 'calendarComponentService.loadColumnsSelectData',
        data: [dataProviderId],
        async: false,
        success: function (result) {
          if (result.msg == 'success') {
            columnNames = $.map(result.data.results, function (data) {
              return {
                value: data.id,
                text: data.id,
                title: data.text,
                id: data.id,
                dataType: '' // 预留
              };
            });
          }
        }
      });
      $('#dataProviderId').data(dataProviderIdKey, columnNames);
    }
    // 重复加载数据来源字段时会取到空字段,在这里过滤掉
    var newColumnNames = [];
    $.each(columnNames, function (i, v) {
      if (StringUtils.isNotBlank(v.id)) {
        newColumnNames.push(columnNames[i]);
      }
    });
    return newColumnNames;
  };

  var loadOperator = function () {
    var operatorSource = [];
    server.JDS.call({
      service: 'viewComponentService.getQueryOperators',
      async: false,
      success: function (result) {
        if (result.msg == 'success') {
          operatorSource = result.data;
        }
      }
    });
    return operatorSource;
  };

  var configurer = $.ui.component.BaseComponentConfigurer();

  // 初始化'基础信息'页签
  configurer.prototype.initBaseInfo = function (configuration, $container) {
    var _self = this;
    designCommons.setElementValue(configuration, $container, ['query']);
    // 分类
    $('#categoryCode', $container).wSelect2({
      serviceName: 'dataDictionaryService',
      params: {
        type: 'MODULE_CATEGORY'
      },
      labelField: 'categoryName',
      valueField: 'categoryCode',
      remoteSearch: false
    });
    // 数据来源
    $('#dataProviderId', $container).wSelect2({
      serviceName: 'calendarComponentService',
      queryMethod: 'loadCalendarComponent',
      remoteSearch: false
    });
    $('#dataProviderId', $container).change(function () {
      var newColumnNames = loadColumnNames(true);
      var columns = $.map(newColumnNames, function (column) {
        var bean = $.extend({}, columnRowBean);
        bean.uuid = commons.UUID.createUUID();
        bean.title = column.title;
        bean.name = column.id;
        return bean;
      });
      var $columnInfoTable = $('#table_column_info', $container);
      $columnInfoTable.bootstrapTable('removeAll').bootstrapTable('load', columns);
      clearInputValue($('#widget_fullcalendar_tabs_query_info', $container));
      var $sortInfoTable = $('#table_sort_info', $container);
      $sortInfoTable.bootstrapTable('removeAll');
      // 资源分组字段
      $('#resourceGroupId', $container).wSelect2({
        data: newColumnNames
      });
    });

    // 加载的JS模块
    $('#jsModule', $container).wSelect2({
      serviceName: 'appJavaScriptModuleMgr',
      params: {
        dependencyFilter: 'CalendarWidgetDevelopment'
      },
      labelField: 'jsModuleName',
      valueField: 'jsModule',
      remoteSearch: false,
      multiple: true
    });

    // 可切换视图
    $('#switchView', $container).wSelect2({});

    // 启用/禁用日历视图
    var $enableCalendarView = $('#div_enableCalendarView', $container);
    $('#enableCalendarView', $container)
      .on('change', function () {
        if ($(this).is(':checked')) {
          $enableCalendarView.show();
        } else {
          $enableCalendarView.hide();
          clearInputValue($enableCalendarView);
        }
      })
      .trigger('change');

    // 启用/禁用资源视图
    var $enableResourceView = $('#div_enableResourceView', $container);
    $('#enableResourceView', $container)
      .on('change', function () {
        if ($(this).is(':checked')) {
          $enableResourceView.show();
          $('#resourceGroupId', $container).wSelect2({
            data: loadColumnNames(true)
          });
        } else {
          $enableResourceView.hide();
          clearInputValue($enableResourceView);
        }
      })
      .trigger('change');
  };

  // 初始化'列定义'页签数据
  configurer.prototype.initColumnTable = function (columns, $container) {
    var columnsData = columns ? columns : [];
    var $columnInfoTable = $('#table_column_info', $container);

    $columnInfoTable.bootstrapTable('destroy').bootstrapTable({
      data: columnsData,
      idField: 'uuid',
      showColumns: true,
      striped: true,
      width: 500,
      onEditableHidden: onEditHidden,
      onEditableSave: function (field, row, oldValue, $el) {
        if (field == 'idField' && row[field] == '1') {
          var data = $columnInfoTable.bootstrapTable('getData');
          $.each(data, function (index, rowData) {
            if (row != rowData) {
              rowData.idField = 0;
              $columnInfoTable.bootstrapTable('updateRow', index, rowData);
            }
          });
        }
        if (field == 'name') {
          var rowDatas = $columnInfoTable.bootstrapTable('getData');
          $.each(rowDatas, function (index, rowData) {
            if (row == rowData) {
              $.each(loadColumnNames(), function (index, val) {
                if (val.value == row.name) {
                  rowData.title = val.title;
                  rowData.dataType = val.dataType;
                }
              });
              $columnInfoTable.bootstrapTable('updateRow', index, rowData);
            }
          });
        }
      },
      toolbar: $('#div_column_toolbar', $container),
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
          field: 'title',
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
          title: '字段名',
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            source: loadColumnNames
          }
        },
        {
          field: 'width',
          title: '列宽',
          editable: {
            type: 'text',
            showbuttons: false,
            onblur: 'submit',
            mode: 'inline',
            validate: function (value) {
              if (StringUtils.isNotBlank(value)) {
                var regu = '^(([1-9][0-9]*)|([1-9][0-9]*.[0-9]+)|([0].[0-9]+))$';
                var re = new RegExp(regu);
                if (!re.test(value)) {
                  return '请输入正确的数字!';
                }
              }
            }
          }
        },
        {
          field: 'controlOptions',
          title: '控件类型',
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
          field: 'validateRules',
          title: '校验规则',
          editable: {
            onblur: 'cancel',
            type: 'wCustomForm',
            placement: 'bottom',
            savenochange: true,
            value2input: designCommons.bootstrapTable.validateRule.value2input,
            input2value: designCommons.bootstrapTable.validateRule.input2value,
            value2display: designCommons.bootstrapTable.validateRule.value2display,
            validate: function (value) {}
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
        },
        {
          field: 'hidden',
          title: '隐藏',
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            source: [
              {
                value: '1',
                text: '是'
              },
              {
                value: '0',
                text: '否'
              }
            ]
          }
        },
        {
          field: 'keywordQuery',
          title: '参与关键字查询',
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            source: [
              {
                value: '1',
                text: '是'
              },
              {
                value: '0',
                text: '否'
              }
            ]
          }
        }
      ]
    });
  };

  // 初始化列定义页签
  configurer.prototype.initColumnInfo = function (columns, $container) {
    //定义添加，删除，上移，下移4按钮事件
    formBuilder.bootstrapTable.initTableTopButtonToolbar('table_column_info', 'column', $container, columnRowBean);
    //初始化列定义表格
    this.initColumnTable(columns);
  };

  // 初始化'查询定义'页签
  configurer.prototype.initQueryInfo = function (queryConfiguration, $container) {
    var fieldData = [];
    if (queryConfiguration) {
      if (queryConfiguration.fields) {
        fieldData = queryConfiguration.fields;
      }
      designCommons.setElementValue(queryConfiguration, $container);
    }
    // 查询
    // 按字段查询
    var $fieldSearchInfo = $('#div_field_search_info', $container);
    var $fieldSearchInfoTable = $('#table_field_search_info', $container);
    $('#fieldSearch', $container)
      .on('change', function () {
        if ($(this).is(':checked')) {
          $fieldSearchInfo.show();
        } else {
          $fieldSearchInfo.hide();
          $fieldSearchInfoTable.bootstrapTable('removeAll');
        }
      })
      .trigger('change');

    var columnRowBean = {
      checked: false,
      uuid: '',
      name: '',
      label: '',
      defaultValue: '',
      queryOptions: {
        queryTypeLabel: '文本框',
        queryType: 'text'
      },
      operator: 'eq'
    };
    //定义添加，删除，上移，下移4按钮事件
    formBuilder.bootstrapTable.initTableTopButtonToolbar('table_field_search_info', 'field', $container, columnRowBean);

    var $columnInfoTable = $('#table_column_info', $container);
    var operatorSource = loadOperator();
    $fieldSearchInfoTable.bootstrapTable('destroy').bootstrapTable({
      data: fieldData,
      idField: 'uuid',
      showColumns: true,
      striped: true,
      width: 500,
      onEditableHidden: onEditHidden,
      toolbar: $('#div_field_search_info_toolbar', $container),
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
          title: '字段名',
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            emptytext: '请选择',
            source: function () {
              var columns = $columnInfoTable.bootstrapTable('getData');
              return $.map(columns, function (column) {
                return {
                  value: column.name,
                  text: column.title
                };
              });
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
                return '请选择查询类型!';
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
        },
        {
          field: 'operator',
          title: '操作符',
          editable: {
            type: 'select',
            mode: 'inline',
            onblur: 'submit',
            showbuttons: false,
            source: operatorSource
          }
        }
      ]
    });
  };

  // 初始化'按钮定义'页签
  configurer.prototype.initButtonInfo = function (configuration, $container) {
    var buttonData = configuration.buttons ? configuration.buttons : [];
    var piUuid = this.component.pageDesigner.getPiUuid();
    var system = appContext.getCurrentUserAppData().getSystem();
    var productUuid = system.productUuid;
    if (StringUtils.isNotBlank(system.piUuid)) {
      piUuid = system.piUuid;
    }
    // 按钮定义
    var $buttonInfoTable = $('#table_button_info', $container);
    var columnRowBean = {
      checked: false,
      uuid: '',
      code: '',
      text: '',
      //position : [ '1' ],
      group: '',
      cssClass: 'btn-default'
    };
    //添加，删除，上移，下移4个按钮
    formBuilder.bootstrapTable.initTableTopButtonToolbar('table_button_info', 'button', $container, columnRowBean);

    $buttonInfoTable.bootstrapTable('destroy').bootstrapTable({
      data: buttonData,
      idField: 'uuid',
      showColumns: true,
      striped: true,
      width: 500,
      onEditableHidden: onEditHidden,
      toolbar: $('#div_button_info_toolbar', $container),
      columns: [
        {
          field: 'checked',
          checkbox: true,
          formatter: checkedFormat
        },
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'text',
          title: '名称',
          width: 100,
          editable: {
            type: 'text',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            validate: function (value) {
              if (StringUtils.isBlank(value)) {
                return '请输入名称!';
              }
            }
          }
        },
        {
          field: 'code',
          title: '编码',
          width: 100,
          editable: {
            type: 'text',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            savenochange: true,
            validate: function (value) {
              if (StringUtils.isBlank(value)) {
                return '请输入编码!';
              }
            }
          }
        },
        {
          field: 'group',
          title: '组别',
          width: 100,
          editable: {
            type: 'text',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit'
          }
        },
        {
          field: 'icon',
          title: '图标',
          width: 100,
          editable: {
            onblur: 'cancel',
            type: 'wCustomForm',
            placement: 'bottom',
            savenochange: true,
            iconSelectTypes: [3],
            value2input: designCommons.bootstrapTable.icon.value2input,
            input2value: designCommons.bootstrapTable.icon.input2value,
            value2display: designCommons.bootstrapTable.icon.value2display,
            value2html: designCommons.bootstrapTable.icon.value2html
          }
        },
        {
          field: 'cssClass',
          title: '样式',
          width: 80,
          editable: {
            type: 'select',
            mode: 'inline',
            onblur: 'submit',
            showbuttons: false,
            source: function () {
              return $.map(constant.WIDGET_COLOR, function (val) {
                return {
                  value: 'btn-' + val.value,
                  text: val.name
                };
              });
            }
          }
        },
        {
          field: 'resource',
          title: '资源',
          width: 100,
          editable: {
            type: 'wCommonComboTree',
            placement: 'bottom',
            mode: 'modal',
            showbuttons: false,
            onblur: 'submit',
            wCommonComboTree: {
              inlineView: true,
              service: 'appProductIntegrationMgr.getTreeNodeByUuid',
              serviceParams: [piUuid, [], ['BUTTON']],
              multiSelect: false
              // 是否多选
            }
          }
        },
        {
          field: 'target',
          title: '目标位置',
          width: 100,
          editable: {
            onblur: 'cancel',
            type: 'wCustomForm',
            placement: 'bottom',
            savenochange: true,
            value2input: designCommons.bootstrapTable.targePosition.value2input,
            value2display: designCommons.bootstrapTable.targePosition.value2display,
            inputCompleted: designCommons.bootstrapTable.targePosition.inputCompleted
          }
        },
        {
          field: 'eventHandler',
          title: '事件逻辑',
          width: 100,
          editable: {
            mode: 'modal',
            onblur: 'ignore',
            type: 'wCustomForm',
            placement: 'left',
            savenochange: true,
            value2input: designCommons.bootstrapTable.eventManager.value2input,
            input2value: designCommons.bootstrapTable.eventManager.input2value,
            validate: designCommons.bootstrapTable.eventManager.validate,
            value2display: designCommons.bootstrapTable.eventManager.value2display
          }
        }
      ]
    });
  };

  // 初始化'高级配置'页签
  configurer.prototype.initSeniorInfo = function (seniorConfiguration, $container) {
    var seniorConfiguration = seniorConfiguration ? seniorConfiguration : [];
    designCommons.setElementValue(seniorConfiguration, $container);
  };

  // 初始化'事项表单模板'页签
  configurer.prototype.initContentInfo = function (formModuleConfiguration, $container) {
    var formModuleConfiguration = formModuleConfiguration ? formModuleConfiguration : [];
    var component = this.component;
    var options = component.options || {};
    designCommons.setElementValue(formModuleConfiguration, $container);

    var content = formModuleConfiguration.formHtmlContent ? formModuleConfiguration.formHtmlContent : StringUtils.EMPTY;

    $('#wHtml_content').summernote({
      height: '300px',
      dialogsInBody: true
    });

    $('#wHtml_content').summernote('code', content);

    var $customHtml = $('#div_wHtml_content', $container);
    $('#enableCustomForm', $container)
      .on('change', function () {
        if ($(this).is(':checked')) {
          $customHtml.show();
        } else {
          $customHtml.hide();
          clearInputValue($customHtml);
        }
      })
      .trigger('change');
  };

  // 组件定义加载
  configurer.prototype.onLoad = function ($container, options) {
    // 初始化页签项
    $('#widget_fullcalendar_tabs ul a', $container).on('click', function (e) {
      e.preventDefault();
      $(this).tab('show');
      var panelId = $(this).attr('href');
      $(panelId + ' .definition_info').bootstrapTable('resetView');
    });
    console.log('初始化数据');
    console.log(options.configuration);
    var configuration = $.extend(true, {}, options.configuration);
    this.initBaseInfo(configuration, $container);
    this.initColumnInfo(configuration.columns, $container);
    this.initQueryInfo(configuration.query, $container);
    this.initButtonInfo(configuration, $container);
    this.initSeniorInfo(configuration.seniorConfiguration, $container);
    this.initContentInfo(configuration.formModuleConfig, $container);
  };

  // 组件定义确认
  configurer.prototype.onOk = function ($container) {
    if (this.component.isReferenceWidget()) {
      return;
    }
    var $columnInfoTable = $('#table_column_info', $container);

    var opt = DesignUtils.collectConfigurerData($('#widget_fullcalendar_tabs_base_info', $container), collectClass);

    // 名称，分类，数据来源
    var requeryFields = ['name', 'categoryCode', 'dataProviderId', 'eventLimitNum'];

    /* ------------------ 插件相关参数 start --------------- */
    // 启用日历/资源视图
    opt.enableCalendarView = Boolean(opt.enableCalendarView);
    opt.enableResourceView = Boolean(opt.enableResourceView);
    if (!opt.enableCalendarView && !opt.enableResourceView) {
      appModal.error('至少启用一种类型的视图！');
      return false;
    }
    if (opt.enableResourceView) {
      // 资源分组字段
      requeryFields.push('resourceGroupId');
    }

    // 默认视图
    opt.defaultView = opt.defaultView;
    // 是否显示周末
    opt.weekends = Boolean(opt.weekends);
    // 其他选项
    opt.multiSelect = Boolean(opt.multiSelect);

    if (!checkRequire(requeryFields, opt, $container)) {
      return false;
    }
    /* ------------------ 插件相关参数 end --------------- */

    // '宽度' 校验
    if (StringUtils.isNotBlank(opt.width)) {
      var regu = '^(([1-9][0-9]*)|([1-9][0-9]*.[0-9]+)|([0].[0-9]+))$';
      var re = new RegExp(regu);
      if (!re.test(opt.width)) {
        appModal.error('宽度必须为正浮点数!');
        return false;
      }
    }
    // '高度' 校验
    if (StringUtils.isNotBlank(opt.height)) {
      var regu = '^(([1-9][0-9]*)|([1-9][0-9]*.[0-9]+)|([0].[0-9]+))$';
      var re = new RegExp(regu);
      if (!re.test(opt.height)) {
        appModal.error('高度必须为正浮点数!');
        return false;
      }
    }
    // '展示数量' 校验
    if (StringUtils.isNotBlank(opt.eventLimitNum)) {
      var regu = '^[0-9]*$';
      var re = new RegExp(regu);
      if (!re.test(opt.eventLimitNum)) {
        appModal.error('展示数量必须为正整数!');
        return false;
      }
    }

    var columns = $columnInfoTable.bootstrapTable('getData');
    columns = $.map(columns, clearChecked);
    if (columns.length == 0) {
      appModal.error('请配置数据列！');
      return false;
    }
    for (var i = 0; i < columns.length; i++) {
      var column = columns[i];
      if (StringUtils.isBlank(column.title) && column.hidden != '1') {
        appModal.error('展示字段标题不允许为空！');
        return false;
      }
    }
    // '查询定义'页签
    var query = DesignUtils.collectConfigurerData($('#widget_fullcalendar_tabs_query_info', $container), collectClass);
    var $fieldSearchInfoTable = $('#table_field_search_info', $container);
    var fields = $fieldSearchInfoTable.bootstrapTable('getData');
    query.keyword = Boolean(query.keyword);
    query.fieldSearch = Boolean(query.fieldSearch);
    query.expandFieldSearch = Boolean(query.expandFieldSearch);
    query.allowSaveTemplate = Boolean(query.allowSaveTemplate);
    fields = $.map(fields, clearChecked);
    if (query.fieldSearch && fields.length == 0) {
      appModal.error('请配置字段查询内容！');
      return false;
    }
    for (var i = 0; i < fields.length; i++) {
      var field = fields[i];
      if (StringUtils.isBlank(field.name)) {
        appModal.error('字段查询的字段名不允许为空！');
        return false;
      }
      if (StringUtils.isBlank(field.label)) {
        appModal.error('字段查询的标题不允许为空！');
        return false;
      }
      if (field.operator == 'between' && field.queryOptions.queryType != 'date') {
        appModal.error('字段查询中只有日期才允许配置区间！');
        return false;
      }
    }
    // '按钮定义'页签
    var $tableButtonInfo = $('#table_button_info', $container);
    var buttons = $tableButtonInfo.bootstrapTable('getData');
    buttons = $.map(buttons, clearChecked);
    for (var i = 0; i < buttons.length; i++) {
      var button = buttons[i];
      if (StringUtils.isBlank(button.text)) {
        appModal.error('按钮的名称不允许为空！');
        return false;
      }
      if (StringUtils.isBlank(button.code)) {
        appModal.error('按钮的编码不允许为空！');
        return false;
      }
      /*
       * if (StringUtils.isBlank(button.position)) {
       *	appModal.error("按钮的位置不允许为空！");
       *	return false;
       *}
       **/
    }
    // '高级配置'页签
    var seniorSetting = DesignUtils.collectConfigurerData($('#widget_fullcalendar_tabs_senior_info', $container), collectClass);
    if (StringUtils.isNotBlank(seniorSetting.customSetting)) {
      if (!isJSON(seniorSetting.customSetting)) {
        appModal.error('高级配置>自定义参数非有效JSON格式！');
        return false;
      }
    }

    // '事项表单模板'页签
    var formModuleConfig = DesignUtils.collectConfigurerData($('#widget_fullcalendar_tabs_formhtml_info', $container), collectClass);
    //formModuleConfig.enableCustomForm = Boolean(formModuleConfig.enableCustomForm);
    //opt.enableCustomForm = formModuleConfig.enableCustomForm;
    if (formModuleConfig.enableCustomForm) {
      var content = $('#wHtml_content').summernote('code');
      formModuleConfig.formHtmlContent = content;
    }

    requeryFields = []; // 格式化

    opt.columns = columns; // 列定义配置
    opt.query = query; // 查询定义配置
    opt.query.fields = fields; // 查询字段配置
    opt.buttons = buttons; // 按钮定义配置
    opt.seniorConfiguration = seniorSetting; // 高级配置
    opt.formModuleConfig = formModuleConfig; // 表单模板

    this.component.options.configuration = $.extend({}, opt);
  };

  //
  component.prototype.usePropertyConfigurer = function () {
    return true;
  };

  // 返回属性配置器
  component.prototype.getPropertyConfigurer = function () {
    return configurer;
  };

  //
  component.prototype.create = function () {
    $(this.element).find('.widget-body').html(this.options.content);
  };

  //
  component.prototype.getDefinitionJson = function () {
    var options = this.options;
    var id = this.getId();
    options.id = id;
    return options;
  };

  return component;
});
