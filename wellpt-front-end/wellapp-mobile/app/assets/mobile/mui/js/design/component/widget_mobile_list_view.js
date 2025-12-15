define([
  'ui_component',
  'constant',
  'commons',
  'server',
  'design_commons',
  'formBuilder',
  'appModal',
  'design_commons',
  'select2',
  'wSelect2',
  'summernote'
], function (ui, constant, commons, server, DesignUtils, formBuilder, appModal, designCommons) {
  var component = $.ui.component.BaseComponent();
  var collectClass = 'w-configurer-option';
  var nameSource = null;
  var StringUtils = commons.StringUtils;
  var onEditHidden = function (field, row, $el, reason) {
    $el.closest('table').bootstrapTable('resetView');
  };
  var clearChecked = function (row) {
    row.checked = false;
    return row;
  };
  var checkRequire = function (propertyNames, options, $container) {
    for (var i = 0; i < propertyNames.length - 1; i++) {
      var propertyName = propertyNames[i];
      if (StringUtils.isBlank(options[propertyName])) {
        var title = $("label[for='" + propertyName + "']", $container).text();
        appModal.error(title + '不允许为空！');
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
    if (alwaysLoad || !nameSource) {
      var source = [];
      var dataStoreId = $('#dataStoreId').val();
      server.JDS.call({
        service: 'viewComponentService.getColumnsById',
        data: [dataStoreId],
        async: false,
        success: function (result) {
          if (result.msg == 'success') {
            nameSource = $.map(result.data, function (data) {
              //							return {
              //								text : data.columnIndex,
              //								value : data.columnIndex
              //							};
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
  var setElementValue = function (key, value) {
    if (typeof key == 'string') {
      $("input[name='" + key + "']").each(function () {
        $element = $(this);
        var type = $element.attr('type');
        if (type == 'text' || type == 'hidden') {
          $element.val(value);
        } else if (type == 'checkbox') {
          $element.prop('checked', Boolean(value));
        } else if (type == 'radio') {
          var checked = $element.val() == value;
          $element.prop('checked', checked);
        }
      });
    }
  };
  component.prototype.create = function () {};
  // 使用属性配置器
  component.prototype.usePropertyConfigurer = function () {
    return true;
  };
  // 返回属性配置器
  component.prototype.getPropertyConfigurer = function () {
    var configurer = $.ui.component.BaseComponentConfigurer();
    configurer.prototype.changeTemplateProperties = function ($propertiesInfoTable) {
      $('#templateHtml').val('');
      $propertiesInfoTable.bootstrapTable('removeAll');
      var template = $('#template').val();
      if (StringUtils.isNotBlank(template)) {
        server.JDS.call({
          service: 'mobileListService.getMobileListTemplateByBeanName',
          data: [template],
          success: function (result) {
            if (result.msg == 'success') {
              var newColumnNames = loadColumnNames(true);
              $('#templateHtml').val(result.data.html);
              $('#templateHtmlCode').text(result.data.html);
              var columns = [];
              $.each(result.data.properties, function (key, title) {
                columns.push({
                  checked: false,
                  uuid: commons.UUID.createUUID(),
                  attrTitle: title,
                  title: title,
                  mapColumn: '',
                  name: key,
                  renderer: {},
                  sortOrder: 'none'
                });
              });
              $propertiesInfoTable.bootstrapTable('removeAll').bootstrapTable('load', columns);
            }
          }
        });
      }
    };
    configurer.prototype.onLoad = function ($container, options) {
      // 初始化页签项
      $('#widget_mobile_list_view_tabs ul a', $container).on('click', function (e) {
        e.preventDefault();
        $(this).tab('show');
      });
      var self = this;
      var $propertiesInfoTable = $('#table_properties_info', $container);
      var configuration = $.extend(true, {}, options.configuration);
      $.each(configuration, function (key, value) {
        if (key != 'templateProperties') {
          setElementValue(key, value);
        }
      });
      var $definitionDive = $('#div_template_properties_definition', $container);
      // 数据源
      $('#dataStoreId', $container)
        .wSelect2({
          serviceName: 'viewComponentService',
          queryMethod: 'loadSelectData',
          labelField: 'dataStoreName',
          valueField: 'dataStoreId',
          remoteSearch: false
        })
        .on('change', function () {
          var newColumnNames = loadColumnNames(true);
          $('#readMarkerField').val('').wSelect2({
            data: newColumnNames
          });
          $('#primaryField').val('').wSelect2({
            data: newColumnNames
          });
          self.changeTemplateProperties($propertiesInfoTable);
        });
      // 启用已阅未阅
      var newColumnNames = loadColumnNames();
      var $readMarkerDefinition = $('#div_readMarker_definition', $container);
      $('#readMarker', $container)
        .on('change', function () {
          if ($(this).is(':checked')) {
            $readMarkerDefinition.show();
          } else {
            $readMarkerDefinition.hide();
            clearInputValue($readMarkerDefinition);
          }
        })
        .trigger('change');
      $('#readMarkerField').wSelect2({
        data: newColumnNames
      });
      $('#primaryField').wSelect2({
        data: newColumnNames
      });
      // 加载的JS模块
      $('#jsModule', $container).wSelect2({
        serviceName: 'appJavaScriptModuleMgr',
        params: {
          dependencyFilter: 'MobileListDevelopmentBase'
        },
        remoteSearch: false,
        multiple: true
      });
      // 模板
      $('#template', $container)
        .wSelect2({
          serviceName: 'mobileListService',
          queryMethod: 'getMobileListTemplate',
          remoteSearch: false
        })
        .on('change', function () {
          self.changeTemplateProperties($propertiesInfoTable);
        });
      $('#templateHtmlCode').text(configuration.templateHtml);
      self.initButtonInfo(configuration.buttons, $container);
      self.initProperties(configuration.templateProperties, $container);
      self.initQueryInfo(configuration.query, $container);
      self.initItemContentInfo(configuration, $container);
    };
    configurer.prototype.initButtonInfo = function (buttons, $container) {
      var buttonData = buttons || [];
      var piUuid = this.component.pageDesigner.getPiUuid();
      var system = appContext.getCurrentUserAppData().getSystem();
      var productUuid = system.productUuid;
      if (StringUtils.isNotBlank(system.piUuid)) {
        piUuid = system.piUuid;
      }
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
          position: ['2'],
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
          if (item.eventHandler && item.eventHandler.eventHandler) {
            item.eventManger = item.eventHandler;
          } else {
            item.eventManger = {};
            item.eventManger.eventHandler = item.eventHandler;
          }
        }
      });
      $buttonInfoTable.bootstrapTable('destroy').bootstrapTable({
        data: buttonData,
        idField: 'uuid',
        showColumns: true,
        striped: true,
        width: 600,
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
                // var regu = "^[a-zA-Z\_][0-9a-zA-Z]*$";
                // var re = new RegExp(regu);
                // if (!re.test(value)) {
                // return '请输入数字、字母、下划线且不以数字开头!';
                // }
              }
            }
          },
          {
            field: 'position',
            title: '位置',
            width: 80,
            editable: {
              onblur: 'save',
              type: 'wCustomForm',
              renderParams: {
                columnTableData: []
              },
              placement: 'right',
              savenochange: true,
              source: [
                {
                  id: 'top',
                  value: '1',
                  text: '头部'
                },
                {
                  id: 'bottom',
                  value: '2',
                  text: '底部'
                },
                {
                  id: 'right',
                  value: '3',
                  text: '行末'
                }
              ],
              validate: function (value) {
                if (value.length == 0) {
                  return '请选择按钮位置!';
                }
              },
              value2input: designCommons.bootstrapTable.btnPosition.value2input,
              input2value: designCommons.bootstrapTable.btnPosition.input2value,
              value2display: designCommons.bootstrapTable.btnPosition.value2display,
              value2html: designCommons.bootstrapTable.btnPosition.value2html
            }
          },
          {
            field: 'group',
            title: '组别',
            width: 60,
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
            width: 60,
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
              mode: 'modal',
              showbuttons: false,
              onblur: 'submit',
              placement: 'bottom',
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
            field: 'eventManger',
            title: '事件管理',
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
            //   field: 'eventHandler',
            //   title: '事件逻辑',
            //   width: 150,
            //   editable: {
            //     mode: 'modal',
            //     type: 'wCommonComboTree',
            //     showbuttons: false,
            //     onblur: 'submit',
            //     placement: 'bottom',
            //     otherProperties: {
            //       type: 'data.type',
            //       path: 'data.path'
            //     },
            //     otherPropertyPath: 'data',
            //     wCommonComboTree: {
            //       inlineView: true,
            //       service: 'appProductIntegrationMgr.getTree',
            //       serviceParams: [productUuid],
            //       multiSelect: false, // 是否多选
            //       parentSelect: true
            //       // 父节点选择有效，默认无效
            //     }
            //   }
            // }, {
            //   field: 'eventParams',
            //   title: '事件参数',
            //   width: 100,
            //   editable: {
            //     mode: 'modal',
            //     onblur: 'cancel',
            //     type: 'wCustomForm',
            //     placement: 'left',
            //     savenochange: true,
            //     value2input: designCommons.bootstrapTable.eventParams.value2input,
            //     input2value: designCommons.bootstrapTable.eventParams.input2value,
            //     value2display: designCommons.bootstrapTable.eventParams.value2display
            //   }
          }
        ]
      });
    };
    configurer.prototype.initQueryInfo = function (queryConfiguration, $container) {
      var fieldData = [];
      if (queryConfiguration) {
        if (queryConfiguration.fields) {
          fieldData = queryConfiguration.fields;
        }
        //				$.each(queryConfiguration, function(key, value) {
        //					setElementValue(key, value);
        //				});
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

      // 字段查询定义上移事件
      formBuilder.bootstrapTable.addRowUpButtonClickEvent({
        tableElement: $fieldSearchInfoTable,
        button: $('#btn_row_up_field', $container)
      });
      // 字段查询定义下移事件
      formBuilder.bootstrapTable.addRowDownButtonClickEvent({
        tableElement: $fieldSearchInfoTable,
        button: $('#btn_row_down_field', $container)
      });
      // 字段查询定义添加一行事件
      formBuilder.bootstrapTable.addAddRowButtonClickEvent({
        tableElement: $fieldSearchInfoTable,
        button: $('#btn_add_field', $container),
        bean: {
          checked: false,
          uuid: '',
          name: '',
          label: '',
          defaultValue: '',
          queryOptions: {
            queryTypeLabel: '文本框',
            queryType: 'text'
          },
          operator: 'like'
        }
      });
      // 字段查询定义删除一行事件
      formBuilder.bootstrapTable.addDeleteRowButtonClickEvent({
        tableElement: $fieldSearchInfoTable,
        button: $('#btn_delete_field', $container)
      });

      var $columnInfoTable = $('#table_column_info', $container);
      var operatorSource = loadOperator();
      $fieldSearchInfoTable.bootstrapTable('destroy').bootstrapTable({
        data: fieldData,
        idField: 'uuid',
        showColumns: true,
        striped: true,
        width: 500,
        onEditableHidden: onEditHidden,
        onEditableSave: function (field, row, oldValue, $el) {
          // 选择名称时，标题为空，设置标题为选择的名称
          if (field == 'name' && StringUtils.isBlank(row.label)) {
            var columns = $columnInfoTable.bootstrapTable('getData');
            $.each(columns, function (index, column) {
              if (column.name == row.name) {
                row.label = column.header;
              }
            });
            var data = $fieldSearchInfoTable.bootstrapTable('getData');
            $.each(data, function (index, rowData) {
              if (row == rowData) {
                $fieldSearchInfoTable.bootstrapTable('updateRow', index, rowData);
              }
            });
          }
          // 选择查询类型为时间时，如果操作符为包含，设置为区间
          if (field == 'queryOptions' && row.queryOptions && row.queryOptions.queryType == 'date' && row.operator == 'like') {
            row.operator = 'in';
            var data = $fieldSearchInfoTable.bootstrapTable('getData');
            $.each(data, function (index, rowData) {
              if (row == rowData) {
                $fieldSearchInfoTable.bootstrapTable('updateRow', index, rowData);
              }
            });
          }
        },
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
              source: loadColumnNames
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
    configurer.prototype.initProperties = function (properties, $container) {
      var properties = properties || [];
      // 旧数据处理
      $.each(properties, function (i, property) {
        if (!property.hasOwnProperty('attrTitle')) {
          property.attrTitle = property.title;
        }
      });
      var $propertiesInfoTable = $('#table_properties_info', $container);
      var buttonRowBean = {
        checked: false,
        uuid: '',
        attrTitle: '',
        title: '',
        name: '',
        mapColumn: '',
        dataType: '',
        renderer: '',
        sortOrder: ''
      };
      // 定义添加，删除，上移，下移4按钮事件
      formBuilder.bootstrapTable.initTableTopButtonToolbar('table_properties_info', 'property', '', buttonRowBean);
      $propertiesInfoTable.bootstrapTable('destroy').bootstrapTable({
        data: properties,
        idField: 'uuid',
        showColumns: true,
        striped: true,
        width: 500,
        onEditableHidden: onEditHidden,
        onEditableSave: function (field, row, oldValue, $el) {
          if (field == 'mapColumn') {
            var rowDatas = $propertiesInfoTable.bootstrapTable('getData');
            $.each(rowDatas, function (index, rowData) {
              if (row == rowData) {
                $.each(loadColumnNames(), function (index, val) {
                  if (val.value == row.mapColumn) {
                    rowData.dataType = val.dataType;
                  }
                });
                $propertiesInfoTable.bootstrapTable('updateRow', index, rowData);
              }
            });
          }
        },
        toolbar: $('#div_properties_info_toolbar', $container),
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
            field: 'attrTitle',
            title: '属性标题'
          },
          {
            field: 'title',
            title: '显示标题',
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              validate: function (value) {
                if (StringUtils.isBlank(value)) {
                  return '请输入标题!';
                }
              }
            }
          },
          {
            field: 'name',
            title: '属性',
            visible: false
          },
          {
            field: 'mapColumn',
            title: '映射字段',
            editable: {
              type: 'select',
              mode: 'inline',
              showbuttons: false,
              source: loadColumnNames
            }
          },
          {
            field: 'dataType',
            title: '数据类型',
            visible: false
          },
          {
            field: 'renderer',
            title: '渲染器',
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'bottom',
              savenochange: true,
              value2html: designCommons.bootstrapTable.renderOption.value2html,
              input2value: designCommons.bootstrapTable.renderOption.input2value, //value2input,
              value2input: designCommons.bootstrapTable.renderOption.value2input, //value2input,
              value2display: designCommons.bootstrapTable.renderOption.value2display //value2display
            }
          },
          {
            field: 'sortOrder',
            title: '排序',
            editable: {
              type: 'select',
              mode: 'inline',
              showbuttons: false,
              source: [
                {
                  value: 'none',
                  text: '无排序'
                },
                {
                  value: 'asc',
                  text: '升序'
                },
                {
                  value: 'desc',
                  text: '降序'
                }
              ]
            }
          }
        ]
      });
    };
    configurer.prototype.initItemContentInfo = function (configuration, $container) {
      var customTemplateHtml = configuration.customTemplateHtml || '';
      $('#custom_template_html', $container).summernote({
        height: '300px'
      });
      $('#custom_template_html', $container).summernote('code', customTemplateHtml);

      $('input[name="itemContentType"]', $container).on('change', function () {
        var itemContentType = $(this).val();
        if (itemContentType == '2') {
          $('#custom_template_html', $container).parent().show();
          $('#templateHtmlCode', $container).closest('.form-group').hide();
          // 设置默认值
          var templateHtml = $('#custom_template_html', $container).summernote('code');
          if (StringUtils.isBlank(templateHtml)) {
            templateHtml = $('#templateHtml', $container).val();
            $('#custom_template_html', $container).summernote('code', templateHtml);
          }
        } else {
          $('#custom_template_html', $container).parent().hide();
          $('#templateHtmlCode', $container).closest('.form-group').show();
        }
      });
      $('input[name="itemContentType"]:checked', $container).trigger('change');
    };
    configurer.prototype.onOk = function ($container) {
      if (this.component.isReferenceWidget()) {
        return;
      }
      var $buttonInfoTable = $('#table_button_info', $container);
      var $propertiesInfoTable = $('#table_properties_info', $container);
      var opt = DesignUtils.collectConfigurerData($('#widget_mobile_list_view', $container), collectClass);
      opt.hasSearch = Boolean(opt.hasSearch);
      opt.editable = Boolean(opt.editable);
      opt.readMarker = Boolean(opt.readMarker);
      if (opt.readMarker === true && StringUtils.isBlank(opt.readMarkerField)) {
        appModal.error('标记字段必填');
        return false;
      }
      opt.hideChecked = Boolean(opt.hideChecked);
      var buttons = $buttonInfoTable.bootstrapTable('getData');
      for (var i = 0; i < buttons.length; i++) {
        var button = buttons[i];
        if (button.eventManger) {
          button.eventHandler = button.eventManger.eventHandler;
          button.eventParams = button.eventManger.eventParams;
        }
        if (StringUtils.isBlank(button.text)) {
          appModal.error('按钮的名称不允许为空！');
          return false;
        }
        if (StringUtils.isBlank(button.code)) {
          appModal.error('按钮的编码不允许为空！');
          return false;
        }
        if (StringUtils.isBlank(button.position)) {
          appModal.error('按钮的位置不允许为空！');
          return false;
        }
      }
      opt.buttons = $.map(buttons, clearChecked);

      // 查询
      var query = DesignUtils.collectConfigurerData($('#widget_bootstrap_table_tabs_query_info', $container), collectClass);
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
      opt.query = query;
      opt.query.fields = fields;
      var properties = $propertiesInfoTable.bootstrapTable('getData');
      opt.templateProperties = $.map(properties, clearChecked);

      // 数据项内容
      var customTemplateHtml = $('#custom_template_html', $container).summernote('code');
      opt.customTemplateHtml = customTemplateHtml;

      // console.log(opt);
      this.component.options.configuration = $.extend({}, opt);
    };
    return configurer;
  };
  // 返回组件定义
  component.prototype.getDefinitionJson = function ($element) {
    var definitionJson = this.options;
    definitionJson.id = this.getId();
    return definitionJson;
  };
  return component;
});
