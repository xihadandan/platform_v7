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
      $('#widget_mobile_tiles_tabs ul a', $container).on('click', function (e) {
        e.preventDefault();
        $(this).tab('show');
      });
      var _self = this.component;
      var configuration = $.extend(true, {}, options.configuration);
      // 设置值
      designCommons.setElementValue(configuration, $container);

      // 加载的JS模块
      $('#jsModule', $container).val(configuration.jsModule);
      $('#jsModule', $container).wSelect2({
        serviceName: 'appJavaScriptModuleMgr',
        labelField: 'jsModuleName',
        valueField: 'jsModule',
        params: {
          dependencyFilter: 'mui-MobileTilesDevelopmentBase'
        },
        remoteSearch: false,
        multiple: false
      });

      this.initTilesInfo(configuration, $container);
    };
    configurer.prototype.onOk = function ($container) {
      var _self = this.component;
      var options = _self.options;
      // 按钮定义
      var $tableButtonInfo = $('#table_tile_info', $container);
      var tileItems = $tableButtonInfo.bootstrapTable('getData');
      tileItems = $.map(tileItems, clearChecked);
      var opt = {};
      opt.tiles = $.map(tileItems, function (item) {
        if (item.eventManger) {
          item.eventHandler = item.eventManger.eventHandler;
          item.eventParams = item.eventManger.eventParams;
        }
        return clearChecked(item);
      });
      opt.name = $('#name', $container).val();
      opt.iconClassName = $('#iconClassName', $container).val();
      opt.mode = $('#mode', $container).val();
      opt.jsModule = $('#jsModule', $container).val();
      opt.jsModuleName = $('#jsModuleName', $container).val();
      opt.uniJsModule = $('#uniJsModule', $container).val();
      opt.customClass = $('#customClass', $container).val();
      this.component.options.configuration = $.extend({}, opt);
    };
    configurer.prototype.initTilesInfo = function (configuration, $container) {
      if (StringUtils.isNotBlank(configuration.iconClassName)) {
        $('#iconClassNameSpan', $container).show();
        $('#iconClassNameSpan', $container).attr('iconClass', configuration.iconClassName);
        $('#iconClassNameSpan', $container).addClass(configuration.iconClassName);
        $('#iconClassName', $container).val(configuration.iconClassName);
      }

      // 初始化基本信息
      $('#logoFileImageSelectBtn', $container).on('click', function () {
        $.WCommonPictureLib.show({
          selectTypes: [3],
          initPrevImg: $('#iconClassName', $container).val(),
          confirm: function (data) {
            var pictureFilePath = data.filePaths;
            if (StringUtils.isBlank(pictureFilePath)) {
              return;
            }
            $('#iconClassName', $container).val(pictureFilePath);
            $('#iconClassNameSpan', $container).show();
            $('#iconClassNameSpan', $container).removeClass($('#iconClassNameSpan', $container).attr('iconClass'));
            $('#iconClassNameSpan', $container).attr('iconClass', pictureFilePath);
            $('#iconClassNameSpan', $container).addClass(pictureFilePath);
          }
        });
      });
      $('#logoFileImageRemoveBtn', $container).on('click', function () {
        $('#iconClassName', $container).val('');
        $('#iconClassNameSpan', $container).removeClass($('#iconClassNameSpan', $container).attr('iconClass'));
      });
      var tiles = configuration.tiles ? configuration.tiles : [];
      var piUuid = this.component.pageDesigner.getPiUuid();
      var system = appContext.getCurrentUserAppData().getSystem();
      if (system != null && system.piUuid != null) {
        piUuid = system.piUuid;
      }

      // 按钮定义
      var $buttonInfoTable = $('#table_tile_info', $container);

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
          title: '',
          subtitle: '',
          hidden: '0',
          backgroundColor: { iconColor: '#fadfdf' },
          foregroundColor: { iconColor: '#000' }
        }
      });
      // 按钮定义删除一行事件
      formBuilder.bootstrapTable.addDeleteRowButtonClickEvent({
        tableElement: $buttonInfoTable,
        button: $('#btn_delete_button', $container)
      });

      $.each(tiles, function (i, item) {
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
        data: tiles,
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
            field: 'title',
            title: '大标题',
            width: 130,
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit'
            }
          },
          {
            field: 'subtitle',
            title: '小标题',
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
              iconSelectTypes: [1, 2],
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
            field: 'backgroundColor',
            title: '背景色',
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
            field: 'foregroundColor',
            title: '前景色',
            visible: false,
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
