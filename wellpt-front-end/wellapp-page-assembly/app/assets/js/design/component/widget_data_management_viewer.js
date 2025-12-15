define(['ui_component', 'constant', 'commons', 'server', 'formBuilder', 'appContext', 'design_commons'], function (
  ui_component,
  constant,
  commons,
  server,
  formBuilder,
  appContext,
  designCommons
) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var collectClass = 'w-configurer-option';
  var component = $.ui.component.BaseComponent();
  component.prototype.create = function () {
    var _self = this;
    var options = _self.options;
    var $element = $(_self.element);
    var $dmsContainer = $element.find('.ui-sortable');
    _self.pageDesigner.sortable(_self, $dmsContainer, $dmsContainer);

    // 初始化容器结点
    if (options.items != null) {
      $.each(options.items, function (index, item) {
        var $draggable = _self.pageDesigner.createDraggableByDefinitionJson(item);
        _self.pageDesigner.drop(_self, $dmsContainer, $draggable, item);
      });
    }
  };

  // 返回定义的HTML
  component.prototype.toHtml = function () {
    var _self = this;
    var options = _self.options;
    var children = _self.getChildren();
    var id = _self.getId();
    var html = '<div id="' + id + '" class="ui-wDataManagementViewer">';
    if (children != null) {
      $.each(children, function (i) {
        var child = this;
        var childHtml = child.toHtml.call(child);
        html += childHtml;
      });
    }
    html += '</div>';
    return html;
  };

  // 使用属性配置器
  component.prototype.usePropertyConfigurer = function () {
    return true;
  };
  var onEditHidden = function (field, row, $el, reason) {
    $el.closest('table').bootstrapTable('resetView');
  };
  var clearChecked = function (row) {
    row.checked = false;
    return row;
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
  var nameSource = null;
  var loadDyformFields = function () {
    if (!nameSource) {
      var source = [];
      var formUuid = $('#formUuid').val();
      server.JDS.call({
        service: 'dataManagementViewerComponentService.getDyformFieldDefinitionsByUuid',
        data: [formUuid],
        async: false,
        success: function (result) {
          nameSource = $.map(result.data, function (data) {
            return {
              value: data.displayName,
              text: data.displayName
            };
          });
        }
      });
    }
    return nameSource;
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
  // 验证配置信息的合法性
  var validateConfiguration = function (configuration) {
    // 单据管理按钮名称、编号不能为空
    if (configuration.document && configuration.document.buttons) {
      var buttons = configuration.document.buttons;
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
      }
    }
    return true;
  };
  // 返回属性配置器
  component.prototype.getPropertyConfigurer = function () {
    var configurer = $.ui.component.BaseComponentConfigurer();
    configurer.prototype.onLoad = function ($container, options) {
      // 初始化页签项
      $('#widget_data_management_viewer_tabs ul a', $container).on('click', function (e) {
        e.preventDefault();
        $(this).tab('show');
      });
      var _self = this.component;
      var configuration = $.extend(true, {}, options.configuration);
      this.initConfiguration(configuration, $container);
    };
    configurer.prototype.onOk = function ($container) {
      var _self = this;
      var component = _self.component;
      component.options.configuration = _self.collectConfiguration($container);
      return validateConfiguration(component.options.configuration);
    };

    configurer.prototype.initConfiguration = function (configuration, $container) {
      // 基本信息
      this.initBaseInfo(configuration, $container);
      // 数据源
      this.initDataSourceInfo(configuration, $container);
      // 权限操作
      this.initActionInfo(configuration, $container);
      // 可视化
      this.initVisualizationInfo(configuration, $container);
      //数据标记
      this.initDataMarkDefine(configuration, $container);
      //数据活动记录
      this.initDataActiveDefineInfo(configuration, $container);
    };
    configurer.prototype.initBaseInfo = function (configuration, $container) {
      // 设置值
      designCommons.setElementValue(configuration, $container, 'query');

      // 分类
      // $("#categoryCode", $container).wSelect2({
      //     serviceName: "dataDictionaryService",
      //     params: {
      //         type: "MODULE_CATEGORY"
      //     },
      //     labelField: "categoryName",
      //     valueField: "categoryCode",
      //     remoteSearch: false
      // });
    };
    configurer.prototype.initDataSourceInfo = function (configuration, $container) {
      var _self = this;
      var store = configuration.store || {};
      // 设置值
      designCommons.setElementValue(store, $container, 'query');

      // 类型
      $('#dataType', $container).wSelect2({
        serviceName: 'dataManagementViewerComponentService',
        queryMethod: 'getDataTypeSelectData',
        valueField: 'dataType',
        remoteSearch: false
      });
      $('.data-type', $container).hide();
      $('#dataType', $container)
        .on('change', function () {
          $('.data-type', $container).hide();
          var val = $(this).val();
          $('.data-type-' + val, $container).show();
        })
        .trigger('change');

      // 绑定表单选择的列信息
      var bindDyformColumns = function () {};
      // 显示表单
      var resetDisplayFormSelect2Data = function () {
        $('#displayFormName', $container).wSelect2({
          serviceName: 'dataManagementViewerComponentService',
          queryMethod: 'getDataTypeOfDyFormSelectData',
          selectionMethod: 'getDataTypeOfDyFormSelectDataByIds',
          labelField: 'displayFormName',
          valueField: 'displayFormUuid',
          defaultBlank: true,
          remoteSearch: false,
          width: '100%',
          height: 250,
          params: {
            pformUuid: $('#formUuid').val()
          }
        });
      };

      // 动态表单
      $('#formName', $container).wSelect2({
        serviceName: 'dataManagementViewerComponentService',
        queryMethod: 'getDataTypeOfDyFormSelectData',
        selectionMethod: 'getDataTypeOfDyFormSelectDataByIds',
        labelField: 'formName',
        valueField: 'formUuid',
        defaultBlank: true,
        width: '100%',
        height: 250,
        params: {
          piUuid: _self.component.pageDesigner.getPiUuid()
        }
      });
      $('#formName', $container).on('change', function () {
        $('#stickStatusField', $container).val('').trigger('change');
        $('#stickTimeField', $container).val('').trigger('change');
        $('#readRecordField', $container).val('').trigger('change');
        // 重新绑定表单选择的列信息
        bindDyformColumns();
        // 显示表单
        resetDisplayFormSelect2Data();
      });
      // 显示表单
      resetDisplayFormSelect2Data();

      // 数据源
      $('#dataStoreId', $container).wSelect2({
        serviceName: 'viewComponentService',
        queryMethod: 'loadSelectData',
        labelField: 'dataStoreName',
        valueField: 'dataStoreId',
        remoteSearch: false,
        params: {
          piUuid: _self.component.pageDesigner.getPiUuid()
        }
      });

      // 绑定表单选择的列信息
      bindDyformColumns();
    };
    configurer.prototype.initActionInfo = function (configuration, $container) {
      var document = configuration.document || {};
      // 设置值
      designCommons.setElementValue(document, $container, 'query');

      var buttonData = document.buttons ? document.buttons : [];
      var piUuid = this.component.pageDesigner.getPiUuid();
      var system = appContext.getCurrentUserAppData().getSystem();
      var productUuid = system.productUuid;
      if (system != null && system.piUuid != null) {
        piUuid = system.piUuid;
      }

      // 操作处理拦截器
      $('#interceptors', $container).wSelect2({
        serviceName: 'dataManagementViewerComponentService',
        queryMethod: 'getInterceptorSelectData',
        labelField: 'interceptorNames',
        valueField: 'interceptors',
        remoteSearch: false,
        multiple: true
      });

      // 操作处理拦截器
      $('#jsModule', $container).wSelect2({
        serviceName: 'appJavaScriptModuleMgr',
        params: {
          dependencyFilter: 'DmsDocumentViewDevelopment'
        },
        labelField: 'jsModuleName',
        valueField: 'jsModule',
        defaultBlank: true,
        remoteSearch: false
      });

      // 按钮定义
      var $buttonInfoTable = $('#table_button_info', $container);
      // 按钮定义上移事件
      formBuilder.bootstrapTable.addRowUpButtonClickEvent({
        tableElement: $buttonInfoTable,
        button: $('#btn_row_up_button', $container)
      });
      // 按钮定义下移事件
      formBuilder.bootstrapTable.addRowDownButtonClickEvent({
        tableElement: $buttonInfoTable,
        button: $('#btn_row_down_button', $container)
      });
      // 按钮定义添加一行事件
      formBuilder.bootstrapTable.addAddRowButtonClickEvent({
        tableElement: $buttonInfoTable,
        button: $('#btn_add_button', $container),
        bean: {
          checked: false,
          uuid: '',
          code: '',
          text: '',
          group: '',
          associatedDocEditModel: '2',
          cssClass: 'well-btn w-btn-primary'
        }
      });
      // 按钮定义删除一行事件
      formBuilder.bootstrapTable.addDeleteRowButtonClickEvent({
        tableElement: $buttonInfoTable,
        button: $('#btn_delete_button', $container)
      });

      //兼容旧的事件处理配置
      for (var i = 0, len = buttonData.length; i < len; i++) {
        if (!buttonData[i].eventManger) {
          buttonData[i].eventManger = {
            eventHandler: buttonData[i].eventHandler
          };
          if (buttonData[i].eventParams) {
            $.extend(buttonData[i].eventManger, buttonData[i].eventParams);
          }
        }
      }

      $buttonInfoTable.bootstrapTable('destroy').bootstrapTable({
        data: buttonData,
        idField: 'uuid',
        showColumns: true,
        striped: true,
        width: 500,
        height: 400,
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
            width: 80,
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
            width: 80,
            visible: false,
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit'
            }
          },
          {
            field: 'associatedDocEditModel',
            title: '关联单据状态',
            width: 80,
            visible: true,
            editable: {
              type: 'select',
              mode: 'inline',
              onblur: 'submit',
              showbuttons: false,
              source: [
                {
                  value: '2',
                  text: '全部'
                },
                {
                  value: '1',
                  text: '编辑模式'
                },
                {
                  value: '0',
                  text: '显示为文本'
                }
              ]
            }
          },
          {
            field: 'btnLib',
            title: '按钮库',
            width: 100,
            editable: {
              onblur: 'save',
              type: 'wCustomForm',
              placement: 'bottom',
              savenochange: true,
              value2input: designCommons.bootstrapTable.btnLib.value2input,
              input2value: designCommons.bootstrapTable.btnLib.input2value,
              value2display: designCommons.bootstrapTable.btnLib.value2display,
              value2html: designCommons.bootstrapTable.btnLib.value2html
            }
          },
          {
            field: 'hidden',
            title: '隐藏',
            width: 80,
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
            field: 'eventManger',
            title: '事件管理',
            width: 100,
            editable: {
              onblur: 'ignore',
              type: 'wCustomForm',
              placement: 'left',
              savenochange: true,
              renderParams: {
                defineJs: false,
                eventUseFor: 'dms'
              },
              value2input: designCommons.bootstrapTable.eventManager.value2input,
              input2value: designCommons.bootstrapTable.eventManager.input2value,
              validate: designCommons.bootstrapTable.eventManager.validate,
              value2display: designCommons.bootstrapTable.eventManager.value2display
            }
          }
        ]
      });
      $buttonInfoTable.parent('.fixed-table-body').height(300);
      this.initRightSidebarTabInfo(configuration, $container);
    };
    configurer.prototype.initVisualizationInfo = function (configuration, $container) {
      var _self = this;
      var view = configuration.view || {};
      // 设置值
      designCommons.setElementValue(view, $container, 'query');
      var appPageUuid = _self.component.pageDesigner.getPageUuid();

      var dataViewTypes = [
        {
          id: 'bootstrapTableDataView',
          text: 'BootstrapTable视图展示'
        },
        {
          id: 'tagTreeWithBootstrapTableDataView',
          text: '标签树+BootstrapTable视图展示'
        },
        {
          id: 'unitTreeWithBootstrapTableDataView',
          text: '通用树+BootstrapTable视图展示'
        },
        {
          id: 'leftSidebarWithBootstrapTableDataView',
          text: '左导航+BootstrapTable视图展示'
        },
        {
          id: 'dyformDataView',
          text: '表单视图展示'
        }
      ];
      // 数据展示
      $('#dataViewType', $container)
        .wellSelect({
          labelField: 'dataViewName',
          valueField: 'dataViewType',
          data: dataViewTypes
        })
        .on('change', function () {
          var value = $(this).val();
          $('.dataView').hide();
          if (StringUtils.isNotBlank(value)) {
            $('.' + value).show();
          }
        })
        .trigger('change');
      // 视图组件
      $('#listViewId', $container).wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        labelField: 'listViewName',
        valueField: 'listViewId',
        params: {
          wtype: 'wBootstrapTable',
          appPageUuid: appPageUuid,
          uniqueKey: 'id',
          includeWidgetRef: 'true'
        },
        remoteSearch: false
      });

      // 标签树组件
      $('#tagTreeId', $container).wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        queryMethod: 'loadSelectData',
        labelField: 'tagTreeName',
        valueField: 'tagTreeId',
        params: {
          wtype: 'wTagTree',
          appPageUuid: appPageUuid,
          uniqueKey: 'id'
        },
        remoteSearch: false
      });
      // 组织树组件
      $('#unitTreeId', $container).wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        queryMethod: 'loadSelectData',
        labelField: 'unitTreeName',
        valueField: 'unitTreeId',
        params: {
          wtype: 'wUnitTree',
          appPageUuid: appPageUuid,
          uniqueKey: 'id'
        },
        remoteSearch: false
      });
      // 左导航组件
      $('#leftSidebarTreeId', $container).wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        queryMethod: 'loadSelectData',
        labelField: 'leftSidebarTreeName',
        valueField: 'leftSidebarTreeId',
        params: {
          wtype: 'wLeftSidebar',
          appPageUuid: appPageUuid,
          uniqueKey: 'id'
        },
        remoteSearch: false
      });
      // 常量
      var operatorSource = loadOperator();
      var $element = $('.relationship-query-info', $container);
      var optionValue = view.relationshipQuery;
      formBuilder.bootstrapTable.build({
        container: $element,
        name: 'relationshipQuery',
        ediableNest: true,
        table: {
          data: optionValue,
          striped: true,
          idField: 'value',
          columns: [
            {
              field: 'checked',
              formatter: checkedFormat,
              checkbox: true
            },
            {
              field: 'uuid',
              title: 'UUID',
              visible: false,
              editable: {
                type: 'text',
                showbuttons: false,
                onblur: 'submit',
                mode: 'inline'
              }
            },
            {
              field: 'columnIndex',
              title: '字段名',
              editable: {
                type: 'select',
                mode: 'inline',
                showbuttons: false,
                onblur: 'submit',
                emptytext: '请选择',
                source: function () {
                  var listViewId = $('#listViewId', $container).val();
                  if (StringUtils.isBlank(listViewId)) {
                    return [];
                  }
                  var appWidgetDefinition = appContext.getWidgetDefinition(listViewId);
                  if (appWidgetDefinition == null) {
                    return [];
                  }
                  var widgetDefinition = JSON.parse(appWidgetDefinition.definitionJson);
                  var columns = widgetDefinition.configuration.columns;
                  return $.map(columns, function (column) {
                    return {
                      value: column.name,
                      text: column.header
                    };
                  });
                }
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
            },
            {
              field: 'ignoreEmptyValue',
              title: '忽略真实值为空的查询条件',
              editable: {
                type: 'select',
                mode: 'inline',
                onblur: 'submit',
                showbuttons: false,
                source: [
                  {
                    value: '0',
                    text: '不忽略'
                  },
                  {
                    value: '1',
                    text: '忽略'
                  }
                ]
              }
            }
          ]
        }
      });

      // 数据预览
      $('#previewId', $container).wSelect2({
        serviceName: 'dataManagementViewerComponentService',
        queryMethod: 'getDataPreviewTemplateSelectData',
        labelField: 'previewName',
        valueField: 'previewId',
        remoteSearch: false
      });

      $('.dyformDataSelectCk', $container).on('change', function () {
        if ($(this).attr('id') === 'enableDefaultConditionSql') {
          $('.enableDefaultConditionSql').show();
        } else {
          $('.enableDefaultConditionSql').val('');
          $('.enableDefaultConditionSql').hide();
        }
      });

      if (view.dyfromDataType == undefined || view.dyfromDataType == '1') {
        //默认
        $('#editUnitOneFormData').prop('checked', true).trigger('change');
      } else {
        $('#enableDefaultConditionSql').prop('checked', true).trigger('change');
      }

      var editor = ace.edit('defaultConditionSql');
      editor.setTheme('ace/theme/clouds');
      editor.session.setMode('ace/mode/sql');
      //启用提示菜单
      ace.require('ace/ext/language_tools');
      editor.setOptions({
        enableBasicAutocompletion: true,
        enableSnippets: true,
        enableLiveAutocompletion: true,
        showPrintMargin: false,
        enableVarSnippets: {
          value: 'wComponentDataStoreCondition',
          showSnippetsTabs: ['内置变量', '常用代码逻辑'],
          scope: ['sql']
        },
        enableCodeHis: {
          relaBusizUuid: this.component.options.id,
          codeType: 'wDataManagementViewer.defaultConditionSql',
          enable: true
        }
      });
      if (configuration.view && configuration.view.defaultConditionSql) {
        editor.setValue(configuration.view.defaultConditionSql);
      }
      $('#defaultConditionSql').data('codeEditor', editor);
    };

    //初始化状态标记
    configurer.prototype.initStatusMarkDefine = function (configuration, $container) {
      var _self = this;
      // 状态标记表格
      var $statusMarkTableInfo = $('#table_status_mark_info', $container);

      var statusMarkRowBean = {
        checked: false,
        uuid: '',
        text: '',
        markDataType: '', //标记数据类型
        markStyle: '', //标记样式
        unmarkStyle: '', //取消标记样式
        markButton: '', //标记事件按钮
        unmarkButton: '', //取消标记事件按钮
        markBtnTrigger: '', //其他触发
        unmarkBtnTrigger: ''
      };
      //定义添加，删除，上移，下移4按钮事件
      formBuilder.bootstrapTable.initTableTopButtonToolbar('table_status_mark_info', 'status_mark', $container, statusMarkRowBean);
      var statusMarks =
        configuration.markDataDefine && configuration.markDataDefine.statusMarks ? configuration.markDataDefine.statusMarks : [];
      $statusMarkTableInfo.bootstrapTable('destroy').bootstrapTable({
        data: statusMarks,
        idField: 'uuid',
        showColumns: true,
        striped: true,
        width: 500,
        onEditableHidden: onEditHidden,
        toolbar: $('#div_status_mark_toolbar', $container),
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
            width: 50,
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
            field: 'markDataType',
            title: '标记数据类型',
            width: 100,
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'right',
              renderParams: {
                superEntityClass: 'com.wellsoft.pt.dms.entity.DataMarkEntity'
              },
              savenochange: true,
              value2input: designCommons.bootstrapTable.markTableOrEntity.value2input,
              input2value: designCommons.bootstrapTable.markTableOrEntity.input2value,
              value2display: designCommons.bootstrapTable.markTableOrEntity.value2display,
              value2html: designCommons.bootstrapTable.markTableOrEntity.value2html
            }
          },
          {
            field: 'markStyle',
            title: '标记样式',
            width: 100,
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'right',
              savenochange: true,
              iconSelectTypes: [3],
              value2input: designCommons.bootstrapTable.tableRowStyle.value2input,
              input2value: designCommons.bootstrapTable.tableRowStyle.input2value,
              value2display: designCommons.bootstrapTable.tableRowStyle.value2display,
              value2html: designCommons.bootstrapTable.tableRowStyle.value2html
            }
          },
          {
            field: 'unmarkStyle',
            title: '取消标记样式',
            width: 100,
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'bottom',
              savenochange: true,
              iconSelectTypes: [3],
              value2input: designCommons.bootstrapTable.tableRowStyle.value2input,
              input2value: designCommons.bootstrapTable.tableRowStyle.input2value,
              value2display: designCommons.bootstrapTable.tableRowStyle.value2display,
              value2html: designCommons.bootstrapTable.tableRowStyle.value2html
            }
          },
          {
            field: 'markButton',
            title: '标记事件处理',
            width: 50,
            editable: {
              type: 'select',
              mode: 'inline',
              onblur: 'submit',
              showbuttons: false,
              source: _self.getListViewTableButtonData()
            }
          },
          {
            field: 'unmarkButton',
            title: '取消标记事件处理',
            width: 50,
            editable: {
              type: 'select',
              mode: 'inline',
              onblur: 'submit',
              showbuttons: false,
              source: _self.getListViewTableButtonData()
            }
          },
          {
            field: 'markBtnTrigger',
            title: '关联标记事件处理',
            width: 100,
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'left',
              savenochange: true,
              renderParams: {
                source: _self.getListViewTableButtonData()
              },
              value2input: designCommons.bootstrapTable.multiSelectButtons.value2input,
              input2value: designCommons.bootstrapTable.multiSelectButtons.input2value,
              value2display: designCommons.bootstrapTable.multiSelectButtons.value2display,
              value2html: designCommons.bootstrapTable.multiSelectButtons.value2html
            }
          },
          {
            field: 'unmarkBtnTrigger',
            title: '关联取消标记事件处理',
            width: 100,
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'left',
              savenochange: true,
              renderParams: {
                source: _self.getListViewTableButtonData()
              },
              value2input: designCommons.bootstrapTable.multiSelectButtons.value2input,
              input2value: designCommons.bootstrapTable.multiSelectButtons.input2value,
              value2display: designCommons.bootstrapTable.multiSelectButtons.value2display,
              value2html: designCommons.bootstrapTable.multiSelectButtons.value2html
            }
          }
        ]
      });
      $statusMarkTableInfo.parent().height(300);
    };

    configurer.prototype.getListViewTableButtonData = function () {
      var btnDatas = [];
      if ($('#listViewId').val() && appPageDesigner[$('#listViewId').val()]) {
        var buttonColumnData = appPageDesigner[$('#listViewId').val()].options.configuration.buttons;
        for (var i = 0; i < buttonColumnData.length; i++) {
          btnDatas.push({
            value: buttonColumnData[i].code,
            id: buttonColumnData[i].code,
            text: buttonColumnData[i].text
          });
        }
      }
      return btnDatas;
    };

    configurer.prototype.getListViewTableColumnData = function () {
      var colDatas = [];
      if ($('#listViewId').val() && appPageDesigner[$('#listViewId').val()]) {
        var columnTableData = appPageDesigner[$('#listViewId').val()].options.configuration.columns;
        var j = 1;
        for (var i = 0; i < columnTableData.length; i++) {
          if (columnTableData[i].hidden == 0) {
            colDatas.push({
              value: j + '',
              id: j + '',
              text: '[第' + j++ + '列] ' + columnTableData[i].header
            });
          }
        }
      }
      return colDatas;
    };

    configurer.prototype.getListViewDataStoreColumnData = function () {
      return (function () {
        var columnSource = [];
        if ($('#listViewId').val()) {
          server.JDS.call({
            service: 'viewComponentService.getColumnsById',
            data: [appPageDesigner[$('#listViewId').val()].options.configuration.dataStoreId],
            async: false,
            success: function (result) {
              if (result.msg == 'success') {
                columnSource = $.map(result.data, function (data) {
                  return {
                    value: data.columnIndex,
                    text: data.columnIndex,
                    dataType: data.dataType,
                    title: data.title,
                    id: data.columnIndex
                  };
                });
              }
            }
          });
        }
        return columnSource;
      })();
    };

    //初始化状态标记
    configurer.prototype.initClassifyMarkDefine = function (configuration, $container) {
      var _self = this;

      // 状态标记表格
      var $classifyMarkTableInfo = $('#table_classify_mark_info', $container);

      //分类按钮
      var classifyButton = {
        value2input: function (value) {
          var $input = this.$input;
          var _this = this;
          var renderParams = this.options.renderParams;
          value = value || {};

          var $div = $('<div>');
          $input.append(
            $('<div>', {
              class: 'div_event_param_option hide'
            }),
            $div
          );
          designCommons.bootstrapTable.eventParams.value2input.call(this, value);
          $input.find('.div_event_param_option').find('.form-group').removeClass('form-group');
          $div.insertBefore($input.find('.div_event_param_option'));
          formBuilder.buildSelect2({
            container: $div,
            label: '按钮',
            labelClass: 'required',
            name: 'buttonCode',
            value: value.buttonCode,
            display: 'buttonName',
            displayValue: value.buttonName,
            inputClass: 'w-custom-collect',
            labelColSpan: '3',
            controlColSpan: '9',
            select2: {
              data: _self.getListViewTableButtonData(),
              remoteSearch: false
            }
          });
          formBuilder.buildSelect2({
            container: $div,
            label: '展示样式',
            labelClass: 'required',
            name: 'menuStyle',
            value: value.menuStyle,
            display: 'menuStyleName',
            displayValue: value.menuStyleName,
            inputClass: 'w-custom-collect',
            labelColSpan: '3',
            controlColSpan: '9',
            select2: {
              data: (function () {
                var colDatas = [];
                colDatas.push({
                  id: 'horizontal_tree',
                  text: '水平树状'
                });
                colDatas.push({
                  id: 'vertical_tree',
                  text: '垂直树状'
                });
                return colDatas;
              })(),
              remoteSearch: false
            }
          });

          formBuilder.buildSelect2({
            container: $div,
            label: '来源类型',
            name: 'sourceType',
            labelClass: 'required',
            value: value.sourceType,
            display: 'sourceTypeName',
            displayValue: value.sourceTypeName,
            inputClass: 'w-custom-collect',
            labelColSpan: '3',
            controlColSpan: '9',
            select2: {
              data: (function () {
                var colDatas = [];
                colDatas.push({
                  id: 'classifyTableConfig',
                  text: '分类表配置'
                });
                colDatas.push({
                  id: 'interface',
                  text: '数据接口'
                });
                colDatas.push({
                  id: 'datastore',
                  text: '数据仓库'
                });
                return colDatas;
              })(),
              remoteSearch: false
            },
            events: {
              change: function () {
                $('.div_source_type_div').empty();
                if ($(this).val() == 'interface') {
                  formBuilder.buildSelect2({
                    container: $('.div_source_type_div'),
                    label: '按钮数据来源',
                    labelClass: 'required',
                    name: 'buttonProvider',
                    value: value.buttonProvider,
                    display: 'buttonProviderName',
                    displayValue: value.buttonProviderName,
                    inputClass: 'w-custom-collect',
                    labelColSpan: '3',
                    controlColSpan: '9',
                    select2: {
                      serviceName: 'treeComponentService',
                      queryMethod: 'loadTreeComponent',
                      params: {
                        //superComponentClass: 'com.wellsoft.pt.dms.support.AbstractButtonTreeDataProvider'
                      },
                      remoteSearch: false,
                      defaultBlank: true
                    }
                  });
                  _this.$input.find('.div_event_param_option').removeClass('hide');
                } else {
                  _this.$input.find('.div_event_param_option table').bootstrapTable('removeAll');
                  _this.$input.find('.div_event_param_option').addClass('hide');
                }
                if ($(this).val() == 'datastore') {
                  formBuilder.buildSelect2({
                    container: $('.div_source_type_div'),
                    label: '数据仓库',
                    labelClass: 'required',
                    name: 'datastoreId',
                    value: value.datastoreId,
                    display: 'datastoreName',
                    displayValue: value.datastoreName,
                    inputClass: 'w-custom-collect',
                    labelColSpan: '3',
                    controlColSpan: '9',
                    select2: {
                      serviceName: 'viewComponentService',
                      queryMethod: 'loadSelectData',
                      remoteSearch: false,
                      defaultBlank: true
                    }
                  });

                  formBuilder.buildInput({
                    container: $('.div_source_type_div'),
                    label: '唯一标识字段',
                    labelClass: 'required',
                    name: 'uniqueKeyColumn',
                    value: value.uniqueKeyColumn,
                    // display: 'buttonProviderName',
                    // displayValue: value.buttonProviderName,
                    inputClass: 'w-custom-collect datastoreColSelect',
                    labelColSpan: '3',
                    controlColSpan: '9'
                  });
                  formBuilder.buildInput({
                    container: $('.div_source_type_div'),
                    label: '展示字段',
                    labelClass: 'required',
                    name: 'displayColumn',
                    value: value.displayColumn,
                    // display: 'buttonProviderName',
                    // displayValue: value.buttonProviderName,
                    inputClass: 'w-custom-collect datastoreColSelect',
                    labelColSpan: '3',
                    controlColSpan: '9'
                  });
                  formBuilder.buildInput({
                    container: $('.div_source_type_div'),
                    label: '父级标识字段',
                    labelClass: '',
                    name: 'parentKeyColumn',
                    value: value.parentKeyColumn,
                    // display: 'buttonProviderName',
                    // displayValue: value.buttonProviderName,
                    inputClass: 'w-custom-collect datastoreColSelect',
                    labelColSpan: '3',
                    controlColSpan: '9'
                  });

                  formBuilder.buildInput({
                    container: $('.div_source_type_div'),
                    label: '默认查询条件',
                    labelClass: '',
                    name: 'defaultCondition',
                    value: value.defaultCondition,
                    inputClass: 'w-custom-collect',
                    labelColSpan: '3',
                    controlColSpan: '9'
                  });

                  $('.datastoreColSelect', $container).each(function (i) {
                    var columnDatas = function () {
                      var colDatas = [];
                      server.JDS.call({
                        service: 'viewComponentService.getColumnsById',
                        data: [$('#datastoreId', $('.div_source_type_div')).val()],
                        async: false,
                        success: function (result) {
                          if (result.msg == 'success') {
                            colDatas = $.map(result.data, function (data) {
                              return {
                                value: data.columnIndex,
                                text: data.columnIndex,
                                title: data.title,
                                id: data.columnIndex
                              };
                            });
                          }
                        }
                      });
                      return colDatas;
                    };
                    $(this)
                      .on('reloadColumn', function () {
                        //展示列
                        $(this).wSelect2({
                          data: columnDatas(),
                          defaultBlank: true,
                          remoteSearch: false
                        });
                      })
                      .trigger('reloadColumn');
                  });

                  $('#datastoreId', $('.div_source_type_div')).on('change', function () {
                    $('.datastoreColSelect', $('.div_source_type_div')).trigger('reloadColumn');
                  });
                }
              }
            }
          });

          var $sourceTypeDiv = $('<div>', {
            class: 'div_source_type_div'
          });
          $div.append($sourceTypeDiv);
          if (!$.isEmptyObject(value)) {
            $('#sourceType', $input).trigger('change');
          }
        },
        input2value: function (value) {
          var $input = this.$input;
          var buttonName = $('#buttonName', $input).val();
          if (buttonName) {
            value.buttonName = buttonName;
          }
          var buttonCode = $('#buttonCode', $input).val();
          if (buttonCode) {
            value.buttonCode = buttonCode;
          }
          var buttonProvider = $('#buttonProvider', $input).val();
          if (buttonProvider) {
            value.buttonProvider = buttonProvider;
          }
          var menuStyle = $('#menuStyle', $input).val();
          if (menuStyle) {
            value.menuStyle = menuStyle;
          }
          var menuStyleName = $('#menuStyleName', $input).val();
          if (menuStyleName) {
            value.menuStyleName = menuStyleName;
          }
          var sourceType = $('#sourceType', $input).val();
          if (sourceType) {
            value.sourceType = sourceType;
          }

          var params = {};
          value.parameters = this.$input.find('#table_parameters_info').bootstrapTable('getData');
          $.map(value.parameters, function (option) {
            params[option.name] = option.value;
          });
          value.params = params;
          return value;
        },
        value2display: function (value) {
          return value.buttonName;
        },
        value2html: function (value, element) {
          if (!$.isEmptyObject(value)) {
            return $(element).text(value.buttonName);
          }
        }
      };

      //分类数据库表选择
      var classifyDatabaseTable = {
        value2input: function (value) {
          var $input = this.$input;
          $input.closest('form').removeClass('form-inline');
          $input.css('width', '430');
          $input.empty();
          value = value || {};
          var $columnSelectDiv = $('<div>', {
            class: 'tableColumnSelectDiv'
          });
          formBuilder.buildSelect2({
            container: $input,
            label: '数据库表',
            labelClass: 'required',
            name: 'classifyDbTable',
            value: value.classifyDbTable,
            inputClass: 'w-custom-collect',
            labelColSpan: '3',
            controlColSpan: '9',
            select2: {
              serviceName: 'cdDataStoreDefinitionService',
              queryMethod: 'loadSelectDataByTable',
              remoteSearch: false
            },
            events: {
              change: function () {
                $columnSelectDiv.empty();
                var columnDatas = (function () {
                  var columnDatas = [];
                  var tableName = $('#classifyDbTable', $container).val();
                  if (!tableName) {
                    return columnDatas;
                  }
                  // server.JDS.call({
                  //   service: 'cdDataStoreDefinitionService.loadTableColumns',
                  //   data: [tableName],
                  //   version: '',
                  //   success: function (res) {
                  //     if (res.success && res.data) {
                  server.JDS.restfulGet({
                    url: `/proxy/api/datastore/loadTableColumns/${tableName}`,
                    success: function (res) {
                      if (res.code == 0 && res.data) {
                        for (var d = 0, dlen = res.data.length; d < dlen; d++) {
                          columnDatas.push({
                            id: res.data[d].columnIndex,
                            text: res.data[d].columnName
                          });
                        }
                      }
                    },
                    async: false
                  });
                  return columnDatas;
                })();
                formBuilder.buildSelect2({
                  container: $columnSelectDiv,
                  label: '唯一标识字段',
                  labelClass: 'required',
                  name: 'unqiueColumn',
                  value: value.unqiueColumn,
                  inputClass: 'w-custom-collect',
                  divClass: 'tableColumnSelectDiv',
                  labelColSpan: '3',
                  controlColSpan: '9',
                  select2: {
                    data: columnDatas
                  }
                });
                formBuilder.buildSelect2({
                  container: $columnSelectDiv,
                  label: '父级标识字段',
                  name: 'parentColumn',
                  value: value.parentColumn,
                  inputClass: 'w-custom-collect',
                  divClass: 'tableColumnSelectDiv',
                  labelColSpan: '3',
                  controlColSpan: '9',
                  select2: {
                    data: columnDatas,
                    defaultBlank: true
                  }
                });
                formBuilder.buildSelect2({
                  container: $columnSelectDiv,
                  label: '分类展示字段',
                  labelClass: 'required',
                  name: 'displayColumn',
                  value: value.displayColumn,
                  inputClass: 'w-custom-collect',
                  divClass: 'tableColumnSelectDiv',
                  labelColSpan: '3',
                  controlColSpan: '9',
                  select2: {
                    data: columnDatas
                  }
                });
              }
            }
          });
          $input.append($columnSelectDiv);
          if (!$.isEmptyObject(value)) {
            $('#classifyDbTable', $input).trigger('change');
          }
        },
        input2value: function (value) {
          var $input = this.$input;
          var classifyDbTable = $('#classifyDbTable', $input).val();
          if (classifyDbTable) {
            value.classifyDbTable = classifyDbTable;
          }
          var displayColumn = $('#displayColumn', $input).val();
          if (displayColumn) {
            value.displayColumn = displayColumn;
          }
          var parentColumn = $('#parentColumn', $input).val();
          if (parentColumn) {
            value.parentColumn = parentColumn;
          }
          var unqiueColumn = $('#unqiueColumn', $input).val();
          if (unqiueColumn) {
            value.unqiueColumn = unqiueColumn;
          }
          return value;
        },
        value2display: function (value) {
          return value.classifyDbTable;
        },
        value2html: function (value, element) {
          if (!$.isEmptyObject(value)) {
            return $(element).text(value.classifyDbTable);
          }
        }
      };

      //分类关系的数据库表选择
      var classifyRelaDatabaseTable = {
        value2input: function (value) {
          var $input = this.$input;
          $input.closest('form').removeClass('form-inline');
          $input.css('width', '430');
          $input.empty();
          value = value || {};
          var $columnSelectDiv = $('<div>', {
            class: 'tableColumnSelectDiv'
          });
          formBuilder.buildSelect2({
            container: $input,
            label: '数据库表',
            labelClass: 'required',
            name: 'relaTable',
            value: value.relaTable,
            inputClass: 'w-custom-collect',
            labelColSpan: '3',
            controlColSpan: '9',
            select2: {
              serviceName: 'cdDataStoreDefinitionService',
              queryMethod: 'loadSelectDataByTable',
              remoteSearch: false
            },
            events: {
              change: function () {
                $columnSelectDiv.empty();
                var columnDatas = (function () {
                  var columnDatas = [];
                  var tableName = $('#relaTable', $container).val();
                  if (!tableName) {
                    return columnDatas;
                  }
                  // server.JDS.call({
                  //   service: 'cdDataStoreDefinitionService.loadTableColumns',
                  //   data: [tableName],
                  //   version: '',
                  //   success: function (res) {
                  //     if (res.success && res.data) {
                  server.JDS.restfulGet({
                    url: `/proxy/api/datastore/loadTableColumns/${tableName}`,
                    success: function (res) {
                      if (res.code == 0 && res.data) {
                        for (var d = 0, dlen = res.data.length; d < dlen; d++) {
                          columnDatas.push({
                            id: res.data[d].columnIndex,
                            text: res.data[d].columnName
                          });
                        }
                      }
                    },
                    async: false
                  });
                  return columnDatas;
                })();
                formBuilder.buildSelect2({
                  container: $columnSelectDiv,
                  label: '数据真实值字段',
                  labelClass: 'required',
                  name: 'dataValueColumn',
                  value: value.dataValueColumn,
                  inputClass: 'w-custom-collect',
                  divClass: 'tableColumnSelectDiv',
                  labelColSpan: '3',
                  controlColSpan: '9',
                  select2: {
                    data: columnDatas
                  }
                });

                formBuilder.buildSelect2({
                  container: $columnSelectDiv,
                  label: '分类真实值字段',
                  labelClass: 'required',
                  name: 'classifyValueColumn',
                  value: value.classifyValueColumn,
                  inputClass: 'w-custom-collect',
                  divClass: 'tableColumnSelectDiv',
                  labelColSpan: '3',
                  controlColSpan: '9',
                  select2: {
                    data: columnDatas
                  }
                });
              }
            }
          });
          $input.append($columnSelectDiv);
          if (!$.isEmptyObject(value)) {
            $('#relaTable', $input).trigger('change');
          }
        },
        input2value: function (value) {
          var $input = this.$input;
          var relaTable = $('#relaTable', $input).val();
          if (relaTable) {
            value.relaTable = relaTable;
          }
          var dataValueColumn = $('#dataValueColumn', $input).val();
          if (dataValueColumn) {
            value.dataValueColumn = dataValueColumn;
          }
          var classifyValueColumn = $('#classifyValueColumn', $input).val();
          if (classifyValueColumn) {
            value.classifyValueColumn = classifyValueColumn;
          }
          return value;
        },
        value2display: function (value) {
          return value.relaTable;
        },
        value2html: function (value, element) {
          if (!$.isEmptyObject(value)) {
            return $(element).text(value.relaTable);
          }
        }
      };

      var classifyMarkRowBean = {
        checked: false,
        uuid: '',
        text: '',
        classifyTable: '', //分类表
        // classifyMarkEntity: '',//分类标记的存储实体类
        classifyMarkTable: '',
        targetButton: '', //宿主按钮
        //classifyMarkStyle: ''//分类标记后的样式展示
        dataRelaType: '' //数据与关系的类型
      };

      //定义添加，删除，上移，下移4按钮事件
      formBuilder.bootstrapTable.initTableTopButtonToolbar('table_classify_mark_info', 'classify_mark', $container, classifyMarkRowBean);
      var classifyMarkData =
        configuration.markDataDefine && configuration.markDataDefine.classifyMarks ? configuration.markDataDefine.classifyMarks : [];
      $classifyMarkTableInfo.bootstrapTable('destroy').bootstrapTable({
        data: classifyMarkData,
        idField: 'uuid',
        showColumns: true,
        striped: true,
        width: 500,
        onEditableHidden: onEditHidden,
        toolbar: $('#div_classify_mark_toolbar', $container),
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
            width: 50,
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
            field: 'classifyTable',
            title: '分类表配置',
            width: 100,
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'right',
              savenochange: true,
              value2input: classifyDatabaseTable.value2input,
              input2value: classifyDatabaseTable.input2value,
              value2display: classifyDatabaseTable.value2display,
              value2html: classifyDatabaseTable.value2html
            }
          },
          /*{
                    field: "classifyMarkEntity",
                    title: "分类关系实体",
                    width: 100,
                    editable: {
                        onblur: "cancel",
                        type: "wCustomForm",
                        placement: "bottom",
                        savenochange: true,
                        renderParams: {superEntityClass: "com.wellsoft.pt.dms.entity.DataClassifyRelaEntity"},
                        value2input: designCommons.bootstrapTable.entityClass.value2input,
                        input2value: designCommons.bootstrapTable.entityClass.input2value,
                        value2display: designCommons.bootstrapTable.entityClass.value2display,
                        value2html: designCommons.bootstrapTable.entityClass.value2html
                    }
                },*/
          {
            field: 'classifyMarkTable',
            title: '分类关系表',
            width: 100,
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'bottom',
              savenochange: true,
              value2input: classifyRelaDatabaseTable.value2input,
              input2value: classifyRelaDatabaseTable.input2value,
              value2display: classifyRelaDatabaseTable.value2display,
              value2html: classifyRelaDatabaseTable.value2html
            }
          },
          {
            field: 'targetButton',
            title: '分类按钮组',
            width: 60,
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'left',
              savenochange: true,
              value2input: classifyButton.value2input,
              input2value: classifyButton.input2value,
              value2display: classifyButton.value2display,
              value2html: classifyButton.value2html
            }
          },
          {
            field: 'dataRelaType',
            title: '数据分类关系',
            width: 100,
            editable: {
              type: 'select',
              mode: 'inline',
              showbuttons: false,
              source: [
                {
                  value: '1',
                  text: '数据有且仅存在一个分类'
                },
                {
                  value: 'n',
                  text: '数据有且存在多个分类'
                }
              ]
            }
          }
        ]
      });

      $classifyMarkTableInfo.parent().height(300);
    };

    configurer.prototype.initDataMarkDefine = function (configuration, $container) {
      var markDataDefine = configuration.markDataDefine;
      var _self = this;
      designCommons.setElementValue(markDataDefine, $container);
      $('.enableDataMark', $container)
        .on('change', function () {
          if ($(this).is(':checked')) {
            $('.' + $(this).attr('id'))
              .filter(':not(.mark_data_type)')
              .show();
          } else {
            $('.' + $(this).attr('id')).hide();
            clearInputValue($('.' + $(this).attr('id')));
            $('.' + $(this).attr('id')).css('display', 'none');
            $('.' + $(this).attr('id'))
              .find('.iconDelete')
              .trigger('click');
            $('.' + $(this).attr('id'))
              .find('.markDataType')
              .trigger('change');
          }
        })
        .trigger('change');
      $('#labelModuleId')
        .on('change', function () {
          var moduleId = appContext.getCurrentUserAppData().appData.module ? appContext.getCurrentUserAppData().appData.module.id : null;
          if (!moduleId && appContext.getCurrentUserAppData().appData.appPath) {
            moduleId = appContext.getCurrentUserAppData().appData.appPath.split('/')[2];
          }
          $('#labelModuleId').val(moduleId);
        })
        .trigger('change');

      //可视化视图变更，触发标签展示列、按钮的变更
      $('#listViewId', $container).on('change', function () {
        //展示列
        $('#columnNameForShowLabel', $container).wSelect2({
          data: _self.getListViewTableColumnData(),
          labelField: 'columnNameForShowLabel',
          valueField: 'columnIndexForShowLabel',
          remoteSearch: false
        });

        $('.renderMarkBtn').each(function () {
          var btnLabelField = $(this).attr('id');
          var btnValueField = $(this).next().attr('id');
          $(this).wSelect2({
            data: _self.getListViewTableButtonData(),
            labelField: btnLabelField,
            valueField: btnValueField,
            remoteSearch: false
          });
        });
      });
      $('#listViewId', $container).trigger('change');

      //标签关联实体类
      $('#lableRelaEntity', $container).wSelect2({
        serviceName: 'dmsDataLabelFacadeService',
        queryMethod: 'loadDataLabelRelaEntityData',
        labelField: 'lableRelaEntity',
        valueField: 'lableRelaEntityName',
        remoteSearch: false
      });

      _self.initClassifyMarkDefine(configuration, $container);
      _self.initStatusMarkDefine(configuration, $container);
    };

    configurer.prototype.initDataActiveDefineInfo = function (configuration, $container) {
      var _self = this;
      var dataActiveDefine = configuration.dataActiveDefine || [];
      designCommons.setElementValue(dataActiveDefine, $container);

      // 数据活动定义表格
      var $dataActiveDefineTable = $('#table_data_active_info', $container);

      var dataActiveRowBean = {
        checked: false,
        uuid: '',
        text: '',
        saveConfig: '', //保存配置
        buttonTrigger: '' //触发按钮
      };

      // 3、标记据库表字段与实体类选择
      var dataActiveSaveConfig = {
        fields: {
          entity: [
            //实体类的字段选择
            {
              name: 'dataValue',
              required: true,
              label: '真实值',
              tip: '来源可视化的视图配置的列定义，标识活动数据的唯一性'
            },
            {
              name: 'dataText',
              required: false,
              label: '显示值',
              tip: '来源可视化的视图配置的列定义，展示活动数据的文本内容'
            }
          ],

          dyform: [
            //表单类的字段选择
            {
              name: 'dataValue',
              required: true,
              label: '真实值',
              tip: '来源可视化的视图配置的列定义，标识活动数据的唯一性'
            },
            {
              name: 'dataText',
              required: false,
              label: '显示值',
              tip: '来源可视化的视图配置的列定义，展示活动数据的文本内容'
            },
            {
              name: 'dataValueColumn',
              required: true,
              label: '真实值保存字段',
              tip: '真实值写入表单的对应字段'
            },
            {
              name: 'dataTextColumn',
              required: false,
              label: '显示值保存字段',
              tip: '显示值写入表单的对应字段'
            },
            {
              name: 'lastActiveTimeColumn',
              required: true,
              label: '最近操作时间保存字段',
              tip: '数据活动的当前时间写入表单对应字段'
            },
            {
              name: 'activeCntColumn',
              required: true,
              label: '操作次数保存字段',
              tip: '数据活动的次数写入表单对应字段'
            }
          ]
        },

        renderDataTypeOptions: function ($container, params) {
          var valueDislpalySelect = function (selectParams) {
            $('.fieldSelectDiv', $container).remove();
            var fieldsData = dataActiveSaveConfig.fields[selectParams.dataType];
            for (var i = 0; i < fieldsData.length; i++) {
              formBuilder.buildSelect2({
                container: $container,
                label: fieldsData[i].label,
                labelClass: fieldsData[i].required ? 'required' : '',
                name: fieldsData[i].name,
                value: selectParams.value[fieldsData[i].name],
                inputClass: 'w-custom-collect',
                divClass: 'fieldSelectDiv',
                labelColSpan: '3',
                controlColSpan: '9',
                help: fieldsData[i].tip,
                select2:
                  fieldsData[i].name == 'dataValue' || fieldsData[i].name == 'dataText'
                    ? {
                        data: _self.getListViewDataStoreColumnData(),
                        remoteSearch: false
                      }
                    : {
                        serviceName: 'dataManagementViewerComponentService',
                        queryMethod: 'getColumnsOfDyFormSelectData',
                        selectionMethod: 'getDataTypeOfDyFormSelectDataByIds',
                        valueField: fieldsData[i].name,
                        //labelField: fieldsData[i].name + 'Name',
                        remoteSearch: false,
                        params: {
                          formUuid: $('#dyformUuid', $container).val()
                        },
                        defaultBlank: true
                      }
              });
            }
          };

          if (params.dataType == 'entity') {
            //实体类选择
            formBuilder.buildSelect2({
              container: $container,
              label: '实体类',
              name: 'entityClasspath',
              value: params.value.entityClasspath,
              display: 'entityClassName',
              displayValue: params.value.entityClassName,
              inputClass: 'w-custom-collect',
              labelColSpan: '3',
              controlColSpan: '9',
              help: '用于保存数据活动的实体类，必须继承com.wellsoft.pt.dms.entity.DataActiveReportEntity',
              select2: {
                serviceName: 'select2DataStoreQueryService',
                queryMethod: 'loadEntityData',
                params: {
                  superEntityClass: 'com.wellsoft.pt.dms.entity.DataActiveReportEntity'
                },
                remoteSearch: false
              }
            });
            valueDislpalySelect(params);
          } else if (params.dataType == 'dyform') {
            //表单选择
            formBuilder.buildSelect2({
              container: $container,
              label: '动态表单',
              labelClass: 'required',
              name: 'dyformUuid',
              value: params.value.dyformUuid,
              display: 'dyformName',
              displayValue: params.value.dyformName,
              inputClass: 'w-custom-collect',
              labelColSpan: '3',
              help: '用于保存数据活动的表单',
              controlColSpan: '9',
              select2: {
                serviceName: 'dataManagementViewerComponentService',
                queryMethod: 'getDataTypeOfDyFormSelectData',
                selectionMethod: 'getDataTypeOfDyFormSelectDataByIds',
                defaultBlank: true
              },
              events: {
                change: function () {
                  $('.tip', $container).remove();
                  valueDislpalySelect(params);
                }
              }
            });

            if (!$.isEmptyObject(params.value)) {
              $('#dyformUuid', $container).trigger('change');
            }
          }
        },

        value2input: function (value) {
          var $input = this.$input;
          $input.closest('form').removeClass('form-inline');
          $input.css('width', '450');
          $input.empty();
          value = value || {};
          var $dataTypeOptionContainer = $('<div>', {
            id: 'dataTypeOptionContainer'
          });
          var $operationTipContainer = $('<div>', {
            id: 'operationTipContainer'
          });
          formBuilder.buildSelect2({
            container: $input,
            label: '数据类型',
            labelClass: 'required',
            name: 'activeDataType',
            value: value.activeDataType,
            display: 'activeDataTypeName',
            displayValue: value.activeDataTypeName,
            inputClass: 'w-custom-collect',
            labelColSpan: '3',
            controlColSpan: '9',
            select2: {
              data: (function () {
                var colDatas = [];
                colDatas.push({
                  id: 'entity',
                  text: '实体类'
                });
                colDatas.push({
                  id: 'dyform',
                  text: '动态表单'
                });
                return colDatas;
              })(),
              remoteSearch: false
            },
            events: {
              change: function () {
                $dataTypeOptionContainer.empty();
                $operationTipContainer.empty();
                dataActiveSaveConfig.renderDataTypeOptions($dataTypeOptionContainer, {
                  dataType: $(this).val(),
                  value: value
                });

                formBuilder.buildLabel({
                  divClass: 'operationTip',
                  container: $operationTipContainer,
                  text: '',
                  name: 'tip',
                  inputClass: 'text-info',
                  labelColSpan: '0',
                  controlColSpan: '12'
                });
              }
            }
          });
          $input.append($dataTypeOptionContainer, $operationTipContainer);
          if (!$.isEmptyObject(value)) {
            $('#activeDataType', $input).trigger('change');
          }
        },
        input2value: function (value) {
          var $input = this.$input;
          return designCommons.collectConfigurerData($input, 'w-custom-collect');
        },
        value2display: function (value) {
          return value.activeDataType;
        },

        value2html: function (value, element) {
          return $(element).html('<code>' + (value.entityClassName ? value.entityClassName : value.dyformName) + '</code>');
        }
      };

      //定义添加，删除，上移，下移4按钮事件
      formBuilder.bootstrapTable.initTableTopButtonToolbar('table_data_active_info', 'data_active', $container, dataActiveRowBean);
      $dataActiveDefineTable.bootstrapTable('destroy').bootstrapTable({
        data: dataActiveDefine,
        idField: 'uuid',
        showColumns: true,
        striped: true,
        width: 500,
        onEditableHidden: onEditHidden,
        toolbar: $('#div_data_active_toolbar', $container),
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
            width: 50,
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
            field: 'saveConfig',
            title: '活动数据保存配置',
            width: 100,
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'bottom',
              savenochange: true,
              value2input: dataActiveSaveConfig.value2input,
              input2value: dataActiveSaveConfig.input2value,
              value2display: dataActiveSaveConfig.value2display,
              value2html: dataActiveSaveConfig.value2html
            }
          },
          {
            field: 'buttonTrigger',
            title: '触发按钮',
            width: 100,
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'left',
              savenochange: true,
              renderParams: {
                source: _self.getListViewTableButtonData()
              },
              value2input: designCommons.bootstrapTable.multiSelectButtons.value2input,
              input2value: designCommons.bootstrapTable.multiSelectButtons.input2value,
              value2display: designCommons.bootstrapTable.multiSelectButtons.value2display,
              value2html: designCommons.bootstrapTable.multiSelectButtons.value2html
            }
          }
        ]
      });
    };

    configurer.prototype.initRightSidebarTabInfo = function (configuration, $container) {
      var _self = this;
      var $tabsForm = $('#widget_bootstap_tabs_form');
      $('#navTree_uuid', $tabsForm).val(commons.UUID.createUUID());
      var tabs = configuration.document ? (configuration.document.rightSlidebarTabs ? configuration.document.rightSlidebarTabs : []) : [];
      var system = appContext.getCurrentUserAppData().getSystem();
      var productUuid = system.productUuid;
      var navTreeId = 'widget_bootstrap_tabs_nav_tree';
      var $navTree = $('#' + navTreeId);
      var treeSetting = {
        edit: {
          drag: {
            autoExpandTrigger: true,
            isCopy: false,
            isMove: true,
            prev: true,
            inner: false,
            next: true
          },
          enable: true,
          showRemoveBtn: false,
          showRenameBtn: false
        },
        view: {
          dblClickExpand: false,
          selectedMulti: false
        },
        data: {
          simpleData: {
            enable: false
          }
        },
        callback: {
          onClick: function (event, treeId, treeNode) {
            var data = treeNode.data;
            clearNavTree();
            for (var i in data) {
              var value = data[i];
              if (typeof value === 'object') {
                for (var j in value) {
                  $('#navTree_' + i + '_' + j, $tabsForm).val(value[j]);
                }
              } else {
                $('#navTree_' + i, $tabsForm).val(value);
              }
            }
          }
        }
      };

      function clearNavTree() {
        $('input[type="text"]', $tabsForm).each(function () {
          $(this).val('');
        });
        $('input[type="hidden"]', $tabsForm).each(function () {
          $(this).val('');
        });

        $('input[type="checkbox"]', $tabsForm).each(function () {
          $(this).prop('checked', false);
        });
      }

      var zNodes = [];
      for (var i = 0; i < tabs.length; i++) {
        var zNode = {
          id: tabs[i].uuid,
          name: tabs[i].name,
          data: tabs[i]
        };
        zNodes.push(zNode);
      }

      var zTree = $.fn.zTree.init($navTree, treeSetting, zNodes);
      //_self.navTree = zTree;
      // 添加同级
      $('#widget_bootstrap_tabs_nav_add').on('click', function () {
        clearNavTree();
        $('#navTree_uuid', $tabsForm).val(commons.UUID.createUUID());
      });

      // 删除
      $('#widget_bootstrap_tabs_nav_remove').on('click', function () {
        appModal.confirm('确认要删除吗?', function (result) {
          if (result) {
            clearNavTree();
            var selectedNode = getSelectedNode();
            var data = selectedNode.data;
            zTree.removeNode(selectedNode);

            for (var i = 0; i < tabs.length; i++) {
              if (tabs[i].uuid == data.uuid) {
                tabs.splice(i, 1);
                $('#defaultShowTab', $container).trigger('initSelections');
                return;
              }
            }
            $('#navTree_uuid', $tabsForm).val(commons.UUID.createUUID());
          }
        });
      });

      var getSelectedNode = function () {
        var selectedNodes = zTree.getSelectedNodes();
        if (selectedNodes.length == 0) {
          return null;
        }
        return selectedNodes[0];
      };

      // 事件处理
      $('#navTree_eventHandler_name', $tabsForm).wCommonComboTree({
        service: 'appProductIntegrationMgr.getTreeWithPtProduct',
        serviceParams: [productUuid],
        width: '330px',
        height: '280px',
        multiSelect: false, // 是否多选
        parentSelect: true, // 父节点选择有效，默认无效
        onAfterSetValue: function (event, self, value) {
          var valueNodes = self.options.valueNodes;
          if (valueNodes && valueNodes.length > 0) {
            $('#navTree_eventHandler_id', $tabsForm).val(valueNodes[0].id);
            $('#navTree_eventHandler_type', $tabsForm).val(valueNodes[0].data.type);
            $('#navTree_eventHandler_path', $tabsForm).val(valueNodes[0].data.path);
          }
        }
      });

      $('#navTree_save', $tabsForm).on('click', function () {
        var name = $('#navTree_name', $tabsForm).val();
        if (StringUtils.isBlank(name)) {
          appModal.warning('名称不能为空！');
          return;
        }
        var data = {};
        $('input[type="text"]', $tabsForm).each(function () {
          var id = $(this).attr('id');
          var fields = id.split('_');
          if (fields.length == 2) {
            data[fields[1]] = $(this).val();
          } else if (fields.length == 3) {
            if (!data[fields[1]]) {
              data[fields[1]] = {};
            }
            data[fields[1]][fields[2]] = $(this).val();
          }
        });
        $('input[type="hidden"]', $tabsForm).each(function () {
          var id = $(this).attr('id');
          var fields = id.split('_');
          if (fields.length == 2) {
            data[fields[1]] = $(this).val();
          } else if (fields.length == 3) {
            if (!data[fields[1]]) {
              data[fields[1]] = {};
            }
            data[fields[1]][fields[2]] = $(this).val();
          }
        });

        for (var i = 0; i < tabs.length; i++) {
          if (tabs[i].uuid == $('#navTree_uuid').val()) {
            tabs[i] = data;
            var node = zTree.getNodesByParam('id', data.uuid, null)[0];
            node.name = data.name;
            node.data = data;
            zTree.updateNode(node);
            $.WCommonAlert('保存成功！');
            return;
          }
        }

        var zNode = {
          id: data.uuid,
          name: data.name,
          data: data
        };
        tabs.push(data);
        zTree.addNodes(null, zNode);
        zTree.selectNode(zTree.getNodesByParam('id', data.uuid, null)[0]);
        $.WCommonAlert('保存成功！');

        $('#defaultShowTab', $container).trigger('initSelections');
      });

      $('#tabShowType', $container).wSelect2({
        valueField: 'tabShowType',
        remoteSearch: false,
        data: [
          {
            id: 'float',
            text: '浮层展现'
          },
          {
            id: 'extrusion',
            text: '挤压展现'
          }
        ]
      });

      $('#defaultShowTab', $container)
        .on('initSelections', function (e) {
          var selections = [];
          for (var i = 0, len = tabs.length; i < len; i++) {
            selections.push({
              id: tabs[i].name,
              text: tabs[i].name
            });
          }
          $('#defaultShowTab', $container).wSelect2({
            valueField: 'defaultShowTab',
            remoteSearch: false,
            data: selections
          });
          $('#defaultShowTab', $container).trigger('change');
        })
        .trigger('initSelections');
    };

    configurer.prototype.collectConfiguration = function ($container) {
      var configuration = {};
      // 基本信息
      this.collectBaseInfo(configuration, $container);
      // 数据源
      this.collectDataSourceInfo(configuration, $container);
      // 权限操作
      this.collectActionInfo(configuration, $container);
      // 可视化
      this.collectVisualizationInfo(configuration, $container);
      // 数据标记
      this.collectMarkDefineInfo(configuration, $container);
      //数据活动配置
      this.collectDataActiveDefineInfo(configuration, $container);

      return $.extend({}, configuration);
    };
    configurer.prototype.collectBaseInfo = function (configuration, $container) {
      var $form = $('#widget_data_management_viewer_tabs_base_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      $.extend(configuration, opt);
    };
    configurer.prototype.collectDataSourceInfo = function (configuration, $container) {
      var $form = $('#widget_data_management_viewer_tabs_data_source_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      if (opt.useDyFormLatestVersion.length == 1) {
        opt.useDyFormLatestVersion = Boolean(opt.useDyFormLatestVersion[0]);
      }
      if (opt.useCForm.length == 1) {
        opt.useCForm = Boolean(opt.useCForm[0]);
      }
      configuration.store = configuration.store || {};
      $.extend(configuration.store, opt);
    };
    configurer.prototype.collectActionInfo = function (configuration, $container) {
      var $form = $('#widget_data_management_viewer_tabs_action_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      if (opt.enableVersioning.length == 1) {
        opt.enableVersioning = Boolean(opt.enableVersioning[0]);
      }
      var $tableButtonInfo = $('#table_button_info', $container);
      var buttons = $tableButtonInfo.bootstrapTable('getData');
      opt.buttons = $.map(buttons, clearChecked);
      for (var i = 0; i < opt.buttons.length; i++) {
        //事件管理的各参数值提到上一层，兼容老代码
        if (!$.isEmptyObject(opt.buttons[i].eventManger)) {
          for (var ek in opt.buttons[i].eventManger) {
            opt.buttons[i][ek] = opt.buttons[i].eventManger[ek];
          }
        }
      }
      //右侧导航菜单节点
      var nodes = $.fn.zTree.getZTreeObj('widget_bootstrap_tabs_nav_tree').getNodes();
      opt.rightSlidebarTabs = [];
      for (var i = 0; i < nodes.length; i++) {
        opt.rightSlidebarTabs.push(nodes[i].data);
      }

      opt.tabShowType = $('#tabShowType', $container).val();
      opt.defaultShowTab = $('#defaultShowTab', $container).val();
      opt.dragableTabWidth = $('#dragableTabWidth', $container).prop('checked');

      configuration.document = configuration.document || {};
      $.extend(configuration.document, opt);
    };
    configurer.prototype.collectVisualizationInfo = function (configuration, $container) {
      var $form = $('#widget_data_management_viewer_tabs_visualization_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      opt.relationshipQuery = $container.find('#table_relationshipQuery_info').bootstrapTable('getData');
      configuration.view = configuration.view || {};
      if (opt.dyfromDataType == 1) {
        opt.editUnitOneFormData = true;
      } else if (opt.dyfromDataType == 2) {
        opt.enableDefaultConditionSql = true;
      }
      opt.defaultConditionSql = $('#defaultConditionSql').data('codeEditor').getValue();
      $.extend(configuration.view, opt);
    };

    configurer.prototype.collectMarkDefineInfo = function (configuration, $container) {
      var markDataDefine = designCommons.collectConfigurerData(
        $('#widget_data_management_viewer_tabs_mark_info', $container),
        collectClass
      );
      markDataDefine.enableLabel = Boolean(markDataDefine.enableLabel);
      configuration.markDataDefine = configuration.markDataDefine || {};
      // 状态标记配置定义
      var $tableClassifyMarkInfo = $('#table_classify_mark_info', $container),
        classifyMarks = [];
      if ($tableClassifyMarkInfo.find('tr').length > 0) {
        classifyMarks = $.map($tableClassifyMarkInfo.bootstrapTable('getData'), clearChecked);
        for (var i = 0; i < classifyMarks.length; i++) {
          var mk = classifyMarks[i];
          if (StringUtils.isBlank(mk.text)) {
            appModal.error('名称不允许为空！');
            return false;
          }
        }
      }

      // 状态标记配置定义
      var $tableStatusMarkInfo = $('#table_status_mark_info', $container),
        statusMarks = [];
      if ($tableStatusMarkInfo.find('tr').length > 0) {
        statusMarks = $.map($tableStatusMarkInfo.bootstrapTable('getData'), clearChecked);
        for (var i = 0; i < statusMarks.length; i++) {
          var mk = statusMarks[i];
          if (StringUtils.isBlank(mk.text)) {
            appModal.error('名称不允许为空！');
            return false;
          }
          if (StringUtils.isBlank(mk.markDataType)) {
            appModal.error('标记数据类型不允许为空！');
            return false;
          }
        }
      }
      markDataDefine.statusMarks = statusMarks;
      markDataDefine.classifyMarks = classifyMarks;
      $.extend(configuration.markDataDefine, markDataDefine);
    };

    configurer.prototype.collectDataActiveDefineInfo = function (configuration, $container) {
      // 状态标记配置定义
      var $tableDataActiveInfo = $('#table_data_active_info', $container),
        dataActive = [];
      if ($tableDataActiveInfo.find('tr').length > 0) {
        dataActive = $.map($tableDataActiveInfo.bootstrapTable('getData'), clearChecked);
        for (var i = 0; i < dataActive.length; i++) {
          var da = dataActive[i];
          if (StringUtils.isBlank(da.text)) {
            appModal.error('名称不允许为空！');
            return false;
          }
        }
      }
      configuration.dataActiveDefine = dataActive;
    };

    return configurer;
  };

  // 返回组件定义
  component.prototype.getDefinitionJson = function () {
    var _self = this;
    var definitionJson = _self.options;
    definitionJson.id = _self.getId();
    definitionJson.items = [];
    var children = _self.getChildren();
    $.each(children, function (i) {
      var child = this;
      definitionJson.items.push(child.getDefinitionJson());
    });
    return definitionJson;
  };

  return component;
});
