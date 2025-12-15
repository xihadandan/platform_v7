define(['ui_component', 'constant', 'commons', 'formBuilder', 'appContext', 'design_commons'], function (
  ui_component,
  constant,
  commons,
  formBuilder,
  appContext,
  designCommons
) {
  var component = $.ui.component.BaseComponent();
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  component.prototype.create = function () {};
  component.prototype.usePropertyConfigurer = function () {
    return true;
  };
  component.prototype.getPropertyConfigurer = function () {
    var collectClass = 'w-configurer-option';
    var configurerPrototype = {};
    configurerPrototype.onLoad = function ($container, options) {
      // 初始化页签项
      $('#widget_button_tabs ul a', $container).on('click', function (e) {
        e.preventDefault();
        $(this).tab('show');
      });
      var configuration = $.extend(true, {}, options.configuration);
      this.initConfiguration(configuration, $container);
    };
    configurerPrototype.onOk = function ($container) {
      this.component.options.configuration = this.collectConfiguration($container);
    };
    // 初始化配置信息
    configurerPrototype.initConfiguration = function (configuration, $container) {
      // 基本信息
      this.initBaseInfo(configuration, $container);
    };
    configurerPrototype.initBaseInfo = function (configuration, $container) {
      var _self = this;
      // 外观
      var appearanceOptionSb = new StringBuilder();
      appearanceOptionSb.append('<option value="">默认</option>');
      $.each(constant.WIDGET_COLOR, function () {
        appearanceOptionSb.appendFormat('<option value="{0}">{1}</option>', this.value, this.name);
      });
      appearanceOptionSb.append('<option value="link">链接</option>');
      $('#appearance', $container).append(appearanceOptionSb.toString());

      // 大小
      var sizeOptionSb = new StringBuilder();
      sizeOptionSb.append('<option value="">默认</option>');
      $.each(constant.WIDGET_SIZE, function () {
        sizeOptionSb.appendFormat('<option value="{0}">{1}</option>', this.value, this.name);
      });
      sizeOptionSb.append('<option value="block">块级</option>');
      $('#size', $container).append(sizeOptionSb.toString());

      // 设置值
      designCommons.setElementValue(configuration, $container);

      var appPageUuid = _self.component.pageDesigner.getPageUuid();
      var piUuid = _self.component.pageDesigner.getPiUuid();
      var system = appContext.getCurrentUserAppData().getSystem();
      var productUuid = system.productUuid;
      if (system != null && system.piUuid != null) {
        piUuid = system.piUuid;
      }

      // 头部事件
      var $element = $('.button-events', $container);
      var buttonEvents = configuration.events || [];
      var targetItems = [
        {
          text: '',
          id: ''
        },
        {
          text: '新窗口',
          id: '_blank'
        },
        {
          text: '当前窗口',
          id: '_self'
        },
        {
          text: '当前窗口组件',
          id: '_targetWidget'
        }
      ];
      var targetValue2Input = function (value) {
        var $input = this.$input;
        $input.closest('form').removeClass('form-inline');
        $input.css('width', '400');
        $input.empty();
        value = value || {};
        formBuilder.buildSelect({
          container: $input,
          label: '事件位置',
          name: 'position',
          value: value.position,
          display: 'positionName',
          displayValue: value.positionName,
          inputClass: 'w-custom-collect',
          labelColSpan: '3',
          controlColSpan: '9',
          items: targetItems,
          events: {
            change: function () {
              var position = $(this).find('option:checked').val();
              renderContentViewByPosition($input, {
                position: position
              });
            }
          }
        });
        renderContentViewByPosition($input, value);
      };
      var targetValue2Display = function (value) {
        for (var i = 0; i < targetItems.length; i++) {
          var target = targetItems[i];
          var position = target.id;
          var positionName = target.text;
          if (position === '_targetWidget' && position === value.position) {
            return positionName + '(' + value.widgetName + ')';
          } else if (position === value.position) {
            return positionName;
          }
        }
        return value.positionName;
      };
      var renderContentViewByPosition = function ($container, value) {
        var $containerDiv = $container.find('.div_content_option');
        if ($containerDiv[0]) {
          $containerDiv.empty();
        } else {
          $containerDiv = $("<div class='div_content_option'></div>");
          $container.append($containerDiv);
        }
        if (value.position == '_targetWidget') {
          formBuilder.buildSelect2({
            container: $containerDiv,
            label: '目标组件',
            name: 'widgetId',
            value: value.widgetId,
            display: 'widgetName',
            displayValue: value.widgetName,
            inputClass: 'w-custom-collect',
            labelColSpan: '3',
            controlColSpan: '9',
            select2: {
              serviceName: 'appWidgetDefinitionMgr',
              params: {
                appPageUuid: appPageUuid,
                uniqueKey: 'id'
              },
              remoteSearch: false
            }
          });
          formBuilder.buildCheckbox({
            container: $containerDiv,
            label: '组件内容',
            name: 'refreshIfExists',
            value: value.refreshIfExists,
            inputClass: 'w-custom-collect',
            labelColSpan: '3',
            controlColSpan: '9',
            items: [
              {
                id: true,
                text: '组件存在时刷新'
              }
            ]
          });
        }
      };

      formBuilder.bootstrapTable.build({
        container: $element,
        name: 'buttonEvents',
        ediableNest: true,
        table: {
          data: buttonEvents,
          striped: true,
          idField: 'uuid',
          columns: [
            {
              field: 'checked',
              formatter: designCommons.checkedFormat,
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
              field: 'source',
              title: '事件元素',
              editable: {
                type: 'select',
                mode: 'inline',
                showbuttons: false,
                onblur: 'submit',
                emptytext: '请选择',
                source: function () {
                  var sources = [];
                  sources.push({
                    value: '.btn',
                    text: '按钮'
                  });
                  return sources;
                }
              }
            },
            {
              field: 'type',
              title: '事件类型',
              editable: {
                type: 'select',
                mode: 'inline',
                onblur: 'submit',
                showbuttons: false,
                source: function () {
                  var eventTypes = [
                    {
                      key: 'CLICK',
                      value: 'click',
                      name: '单击'
                    }
                  ];
                  return $.map(eventTypes, function (eventType) {
                    return {
                      value: eventType.value,
                      text: eventType.name
                    };
                  });
                }
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
                value2input: targetValue2Input,
                value2display: targetValue2Display,
                inputCompleted: function (value) {
                  if (value) {
                    value.refreshIfExists = Boolean(value.refreshIfExists);
                  }
                }
              }
            },
            {
              field: 'handler',
              title: '事件处理',
              width: 150,
              editable: {
                placement: 'bottom',
                mode: 'modal',
                type: 'wCommonComboTree',
                showbuttons: false,
                onblur: 'submit',
                otherProperties: {
                  type: 'data.type',
                  path: 'data.path'
                },
                otherPropertyPath: 'data',
                wCommonComboTree: {
                  inlineView: true,
                  service: 'appProductIntegrationMgr.getTree',
                  serviceParams: [productUuid],
                  multiSelect: false, // 是否多选
                  parentSelect: true
                  // 父节点选择有效，默认无效
                }
              }
            },
            {
              field: 'params',
              title: '事件参数',
              editable: {
                mode: 'modal',
                onblur: 'cancel',
                type: 'wCustomForm',
                placement: 'left',
                savenochange: true,
                value2input: designCommons.bootstrapTable.eventParams.value2input,
                input2value: designCommons.bootstrapTable.eventParams.input2value,
                value2display: designCommons.bootstrapTable.eventParams.value2display
              }
            }
          ]
        }
      });
    };

    // 收集配置信息
    configurerPrototype.collectConfiguration = function ($container) {
      var configuration = {};
      // 基本信息
      this.collectBaseInfo(configuration, $container);
      return $.extend({}, configuration);
    };
    configurerPrototype.collectBaseInfo = function (configuration, $container) {
      var $form = $('#widget_button_tabs_base_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      // 头部事件
      var $tableheaderEventsInfo = $('#table_buttonEvents_info', $container);
      var events = $tableheaderEventsInfo.bootstrapTable('getData');
      opt.events = events;
      $.extend(configuration, opt);
    };
    var configurer = $.ui.component.BaseComponentConfigurer(configurerPrototype);
    return configurer;
  };
  component.prototype.toHtml = function () {
    var _self = this;
    var options = _self.options;
    var configuration = options.configuration || {};
    var id = _self.getId();
    var text = configuration.text;
    var appearance = configuration.appearance;
    var size = configuration.size;
    var html = new StringBuilder();
    // 外观
    debugger;
    if (StringUtils.isNotBlank(appearance)) {
      appearance = 'mui-btn-' + appearance;
    }
    // 大小
    if (StringUtils.isNotBlank(size)) {
      size = 'btn-' + size;
    }
    // 样式类
    var cssClass = configuration.cssClass;
    var btnTpl = '<div id="{0}" class="ui-wButton"><button class="mui-btn {2} {3} {4}">{1}</button></div>';
    html.appendFormat(btnTpl, id, text, appearance, size, cssClass);
    return html.toString();
  };
  component.prototype.getDefinitionJson = function () {
    var _self = this;
    var options = _self.options;
    var id = _self.getId();
    options.id = id;
    return options;
  };
  return component;
});
