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
    var options = this.options;
    var children = this.getChildren();
    var id = this.getId();
    var html = "<div id='" + id + "' class='ui-wMobileDataManagementViewer'>";
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
    };
    configurer.prototype.initBaseInfo = function (configuration, $container) {
      // 设置值
      designCommons.setElementValue(configuration, $container, 'query');

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
    };
    configurer.prototype.initDataSourceInfo = function (configuration, $container) {
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
      var bindDyformColumns = function () {
        // 启用置顶
        var $stickDefinition = $('#div_stick_definition', $container);
        $('#stick', $container)
          .on('change', function () {
            if ($(this).is(':checked')) {
              $stickDefinition.show();
            } else {
              $stickDefinition.hide();
              clearInputValue($stickDefinition);
            }
          })
          .trigger('change');
        $('#stickStatusField', $container).wSelect2({
          serviceName: 'dataManagementViewerComponentService',
          queryMethod: 'getColumnsOfDyFormSelectData',
          labelField: 'stickStatusFieldName',
          valueField: 'stickStatusField',
          defaultBlank: true,
          remoteSearch: false,
          width: '100%',
          height: 250,
          params: {
            formUuid: $('#formUuid').val()
          }
        });
        $('#stickTimeField', $container).wSelect2({
          serviceName: 'dataManagementViewerComponentService',
          queryMethod: 'getColumnsOfDyFormSelectData',
          labelField: 'stickTimeFieldName',
          valueField: 'stickTimeField',
          defaultBlank: true,
          remoteSearch: false,
          width: '100%',
          height: 250,
          params: {
            formUuid: $('#formUuid').val()
          }
        });

        // 记录阅读记录
        var $readRecordDefinition = $('#div_readRecord_definition', $container);
        $('#readRecord', $container)
          .on('change', function () {
            if ($(this).is(':checked')) {
              $readRecordDefinition.show();
            } else {
              $readRecordDefinition.hide();
              clearInputValue($readRecordDefinition);
            }
          })
          .trigger('change');
        $('#readRecordField', $container).wSelect2({
          serviceName: 'dataManagementViewerComponentService',
          queryMethod: 'getColumnsOfDyFormSelectData',
          labelField: 'readRecordFieldName',
          valueField: 'readRecordField',
          defaultBlank: true,
          remoteSearch: false,
          width: '100%',
          height: 250,
          params: {
            formUuid: $('#formUuid').val()
          }
        });
      };

      // 显示表单
      var resetDisplayFormSelect2Data = function () {
        $('#mFormName', $container).wSelect2({
          serviceName: 'dataManagementViewerComponentService',
          queryMethod: 'getDataTypeOfDyFormSelectData',
          selectionMethod: 'getDataTypeOfDyFormSelectDataByIds',
          labelField: 'mFormName',
          valueField: 'mFormUuid',
          defaultBlank: true,
          remoteSearch: false,
          width: '100%',
          height: 250,
          params: {
            pformUuid: $('#formUuid').val(),
            formType: 'M'
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
        height: 250
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
        remoteSearch: false
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
          cssClass: 'btn-default'
        }
      });
      // 按钮定义删除一行事件
      formBuilder.bootstrapTable.addDeleteRowButtonClickEvent({
        tableElement: $buttonInfoTable,
        button: $('#btn_delete_button', $container)
      });

      $.each(buttonData, function (i, item) {
        if (!item.eventManger) {
          item.eventManger = {
            eventHandler: item.eventHandler,
            eventParams: item.eventParams,
            parameters: item.eventParams ? item.eventParams.parameters : null,
            params: item.eventParams ? item.eventParams.params : null
          };
        }
      });
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
            // width : 100,
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
            // width : 80,
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
            field: 'associatedDocEditModel',
            title: '关联单据状态',
            // width: 80,
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
            field: 'group',
            title: '组别',
            // width : 80,
            visible: false,
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
            width: 80,
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
            // width : 80,
            visible: true,
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
            title: '操作处理',
            width: 150,
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
            // }, {
            //   field: "eventHandler",
            //   title: "操作处理",
            //   // width : 150,
            //   editable: {
            //     mode: "modal",
            //     type: "wCommonComboTree",
            //     showbuttons: false,
            //     onblur: "submit",
            //     placement: 'left',
            //     otherProperties: {
            //       'type': 'data.type',
            //       'path': 'data.path'
            //     },
            //     otherPropertyPath: "data",
            //     wCommonComboTree: {
            //       inlineView: true,
            //       service: "appProductIntegrationMgr.getTree",
            //       serviceParams: [productUuid],
            //       multiSelect: false, // 是否多选
            //       parentSelect: true
            //     }
            //   }
          }
        ]
      });
    };
    configurer.prototype.initVisualizationInfo = function (configuration, $container) {
      var _self = this;
      var view = configuration.view || {};
      // 设置值
      designCommons.setElementValue(view, $container, 'query');
      var appPageUuid = _self.component.pageDesigner.getPageUuid();

      // 数据展示
      var dataViewTypes = [
        {
          id: 'mobileListDataView',
          text: 'MobileList视图展示'
        }
      ];
      $('#dataViewType', $container).wellSelect({
        labelField: 'dataViewName',
        valueField: 'dataViewType',
        data: dataViewTypes
      });
      $('#dataViewType', $container)
        .on('change', function () {
          var value = $(this).val();
          $('.dataView').hide();
          $('.' + value).show();
        })
        .trigger('change');
      // 视图组件
      $('#listViewId', $container).wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        labelField: 'listViewName',
        valueField: 'listViewId',
        params: {
          wtype: 'wMobileListView',
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
      opt.stick = Boolean(opt.stick);
      opt.readRecord = Boolean(opt.readRecord);
      configuration.store = configuration.store || {};
      $.extend(configuration.store, opt);
    };
    configurer.prototype.collectActionInfo = function (configuration, $container) {
      var $form = $('#widget_data_management_viewer_tabs_action_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      opt.enableVersioning = Boolean(opt.enableVersioning);
      var $tableButtonInfo = $('#table_button_info', $container);
      var buttons = $tableButtonInfo.bootstrapTable('getData');
      opt.buttons = $.map(buttons, function (item) {
        if (item.eventManger) {
          item.eventHandler = item.eventManger.eventHandler;
          item.eventParams = item.eventManger.eventParams;
        }
        return clearChecked(item);
      });
      configuration.document = configuration.document || {};
      $.extend(configuration.document, opt);
    };
    configurer.prototype.collectVisualizationInfo = function (configuration, $container) {
      var $form = $('#widget_data_management_viewer_tabs_visualization_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      opt.relationshipQuery = $container.find('#table_relationshipQuery_info').bootstrapTable('getData');
      configuration.view = configuration.view || {};
      $.extend(configuration.view, opt);
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
