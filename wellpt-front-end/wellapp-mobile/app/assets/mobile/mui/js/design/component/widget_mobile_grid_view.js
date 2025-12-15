define(['ui_component', 'constant', 'formBuilder', 'commons', 'appContext', 'design_commons'], function (
  ui,
  constant,
  formBuilder,
  commons,
  appContext,
  designCommons
) {
  var component = $.ui.component.BaseComponent();
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  component.prototype.create = function () {};
  // 使用属性配置器
  component.prototype.usePropertyConfigurer = function () {
    return true;
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
  // 返回属性配置器
  component.prototype.getPropertyConfigurer = function () {
    var configurer = $.ui.component.BaseComponentConfigurer();
    configurer.prototype.onLoad = function ($container, options) {
      // 初始化页签项
      $('#widget_mobile_grid_view_tabs ul a', $container).on('click', function (e) {
        e.preventDefault();
        $(this).tab('show');
      });
      var _self = this.component;
      var configuration = $.extend(true, {}, options.configuration);
      // 设置值
      designCommons.setElementValue(configuration, $container);
      $('#numColumns', $container).val(configuration.numColumns);

      // 加载的JS模块
      $('#jsModule', $container).val(configuration.jsModule);
      $('#jsModule', $container).wSelect2({
        serviceName: 'appJavaScriptModuleMgr',
        labelField: 'jsModuleName',
        valueField: 'jsModule',
        params: {
          dependencyFilter: 'mui-MobileGridDevelopmentBase'
        },
        remoteSearch: false,
        multiple: false
      });

      this.initGridCellInfo(configuration, $container);
    };
    configurer.prototype.onOk = function ($container) {
      var _self = this.component;
      var options = _self.options;
      // 按钮定义
      var $tableButtonInfo = $('#table_grid_cell_info', $container);
      var gridCells = $tableButtonInfo.bootstrapTable('getData');
      gridCells = $.map(gridCells, clearChecked);
      var opt = {};
      opt.columns = $.map(gridCells, function (item) {
        if (item.eventManger) {
          item.eventHandler = item.eventManger.eventHandler;
          item.eventParams = item.eventManger.eventParams;
        }
        return clearChecked(item);
      });
      opt.name = $('#name', $container).val();
      opt.numColumns = $('#numColumns', $container).val();
      opt.logoFilePath = $('#logoFilePath', $container).val();
      opt.showAsSingle = $('#showSingle').is(':checked') ? '1' : '0';
      opt.jsModule = $('#jsModule', $container).val();
      opt.jsModuleName = $('#jsModuleName', $container).val();
      opt.uniJsModule = $('#uniJsModule', $container).val();
      opt.customClass = $('#customClass', $container).val();
      this.component.options.configuration = $.extend({}, opt);
    };
    configurer.prototype.initGridCellInfo = function (configuration, $container) {
      if (StringUtils.isNotBlank(configuration.logoFilePath)) {
        $('#logoFileImage', $container).show();
        $('#logoFileImage', $container).attr('src', ctx + configuration.logoFilePath);
        $('#logoFilePath', $container).val(configuration.logoFilePath);
      }
      if (configuration.showAsSingle == '1') {
        $('#showSingle').prop('checked', true);
      }

      // 初始化基本信息
      $('#logoFileImageSelectBtn', $container).on('click', function () {
        $.WCommonPictureLib.show({
          selectTypes: [1, 2],
          initPrevImg: $('#logoFilePath', $container).val(),
          confirm: function (data) {
            var pictureFilePath = data.filePaths;
            if (StringUtils.isBlank(pictureFilePath)) {
              return;
            }
            $('#logoFilePath', $container).val(pictureFilePath);
            $('#logoFileImage', $container).show();
            $('#logoFileImage', $container).attr('src', ctx + pictureFilePath);
          }
        });
      });
      $('#logoFileImageRemoveBtn', $container).on('click', function () {
        $('#logoFilePath', $container).val('');
        $('#logoFileImage', $container).removeAttr('src');
      });
      var columns = configuration.columns ? configuration.columns : [];
      var piUuid = this.component.pageDesigner.getPiUuid();
      var system = appContext.getCurrentUserAppData().getSystem();
      if (system != null && system.piUuid != null) {
        piUuid = system.piUuid;
      }

      // 按钮定义
      var $buttonInfoTable = $('#table_grid_cell_info', $container);

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
          text: '',
          hidden: '0',
          iconClass: 'mui-icon',
          iconBackgroundColor: { iconColor: '#fff' }
        }
      });
      // 按钮定义删除一行事件
      formBuilder.bootstrapTable.addDeleteRowButtonClickEvent({
        tableElement: $buttonInfoTable,
        button: $('#btn_delete_button', $container)
      });

      $.each(columns, function (i, item) {
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
        data: columns,
        idField: 'uuid',
        showColumns: true,
        striped: true,
        width: 500,
        onEditableHidden: function (field, row, $el, reason) {
          $buttonInfoTable.bootstrapTable('resetView');
        },
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
            width: 130,
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit'
            }
          },
          {
            field: 'image',
            title: '图片',
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'bottom',
              savenochange: true,
              iconSelectTypes: [1, 2, 3],
              value2input: designCommons.bootstrapTable.icon.value2input,
              input2value: designCommons.bootstrapTable.icon.input2value,
              value2display: designCommons.bootstrapTable.icon.value2display,
              value2html: designCommons.bootstrapTable.icon.value2html
            }
          },
          {
            field: 'icon',
            title: '图标',
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'bottom',
              savenochange: true,
              iconSelectTypes: [3],
              iconEditable: true,
              value2input: designCommons.bootstrapTable.icon.value2input,
              input2value: designCommons.bootstrapTable.icon.input2value,
              value2display: designCommons.bootstrapTable.icon.value2display,
              value2html: designCommons.bootstrapTable.icon.value2html
            }
          },
          {
            field: 'iconBackgroundColor',
            title: '图标背景色',
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'left',
              savenochange: true,
              value2input: designCommons.bootstrapTable.color.value2inputFactory([
                '#488cee',
                '#bfe7f5',
                '#e33033',
                '#ff6000',
                '#e99f00',
                '#3aa322'
              ]),
              input2value: designCommons.bootstrapTable.color.input2value,
              value2display: designCommons.bootstrapTable.color.value2display,
              value2html: designCommons.bootstrapTable.color.value2html
            }
          },
          {
            field: 'iconBgStyle',
            title: '图标背景风格',
            width: 100,
            editable: {
              type: 'select',
              mode: 'inline',
              onblur: 'submit',
              showbuttons: false,
              source: [
                {
                  value: 'square',
                  text: '方形'
                },
                {
                  value: 'corner',
                  text: '圆角'
                },
                {
                  value: 'circular',
                  text: '圆形'
                }
              ]
            }
          },
          {
            field: 'badge',
            title: '徽章',
            visible: true,
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'bottom',
              savenochange: true,
              value2input: designCommons.bootstrapTable.badge.value2input('wMobileListView'),
              value2display: designCommons.bootstrapTable.badge.value2display
            }
          },
          {
            field: 'hidden',
            title: '是否隐藏',
            width: 80,
            editable: {
              type: 'select',
              mode: 'inline',
              onblur: 'submit',
              showbuttons: false,
              source: [
                {
                  value: '0',
                  text: '显示'
                },
                {
                  value: '1',
                  text: '隐藏'
                }
              ]
            }
          },
          {
            field: 'target',
            title: '目标位置',
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
          }
          // {
          //   field: 'eventHandler',
          //   title: '事件逻辑',
          //   width: 150,
          //   editable: {
          //     mode: 'modal',
          //     type: 'wCommonComboTree',
          //     showbuttons: false,
          //     placement: 'bottom',
          //     onblur: 'submit',
          //     otherProperties: {
          //       type: 'data.type',
          //       path: 'data.path'
          //     },
          //     otherPropertyPath: 'data',
          //     wCommonComboTree: {
          //       inlineView: true,
          //       service: 'appProductIntegrationMgr.getTreeNodeByUuid',
          //       serviceParams: [piUuid, [], []],
          //       multiSelect: false, // 是否多选
          //       parentSelect: true
          //       // 父节点选择有效，默认无效
          //     }
          //   }
          // },
          // {
          //   field: 'eventParams',
          //   title: '事件参数',
          //   editable: {
          //     mode: 'modal',
          //     onblur: 'ignore',
          //     type: 'wCustomForm',
          //     placement: 'left',
          //     savenochange: true,
          //     value2input: designCommons.bootstrapTable.eventParams.value2input,
          //     input2value: designCommons.bootstrapTable.eventParams.input2value,
          //     value2display: designCommons.bootstrapTable.eventParams.value2display
          //   }
          // }
        ]
      });
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
