define(['ui_component', 'constant', 'commons', 'formBuilder', 'appContext', 'design_commons'], function (
  ui_component,
  constant,
  commons,
  formBuilder,
  appContext,
  designCommons
) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var component = $.ui.component.BaseComponent();
  var onEditHidden = function (field, row, $el, reason) {
    $el.closest('table').bootstrapTable('resetView');
  };
  component.prototype.create = function () {
    var $element = $(this.element);
    var options = this.options;
    var _self = this;

    function callback() {
      if (options.configuration && !options.configuration.steps) {
        var msg = "<input id='step_num' name='step_num' style='width:100%;'/>";
        msg += '输入步骤数量';
        return $.WCommonDialog({
          title: '步骤数量定义',
          message: msg,
          buttons: {
            confirm: {
              label: '确定',
              className: 'btn-primary',
              callback: $.proxy(function (e) {
                var html = '';
                var layoutString = $.trim($('#step_num').val());
                var stepNum = parseInt(layoutString);
                options.configuration = {};
                options.configuration.steps = [];
                for (var i = 1; i <= stepNum; i++) {
                  html += getColumnHtml(i);
                  options.configuration.steps.push(i);
                }
                $('.widget-body', $element).find('div').after(html).remove();
                _self.$columns = $element.find('div.column');
                _self.$columns.addClass('ui-sortable');
                _self.pageDesigner.sortable(_self, _self.$columns, $element);
              }, _self)
            },
            cancel: {
              label: '关闭',
              className: 'btn-default'
            }
          },
          shown: function () {}
        });
      } else {
        var steps = options.configuration.steps;
        var containerHtml = '';
        for (var i = 0; i < steps.length; i++) {
          containerHtml += getColumnHtml(steps[i]);
        }
        $('.widget-body', $element).find('div').after(containerHtml).remove();
        _self.$columns = $element.find('div.column');
        _self.$columns.addClass('ui-sortable');
        _self.pageDesigner.sortable(_self, _self.$columns, $element);
        if (options.items != null && options.items.length > 0) {
          $element.find('.ui-sortable').each(function (i) {
            var $placeHolder = $(this);
            $.each(options.items, function (j) {
              var item = this;
              if (item.columnIndex == i) {
                var $draggable = _self.pageDesigner.createDraggableByDefinitionJson(item);
                _self.pageDesigner.drop(_self, $placeHolder, $draggable, item);
              }
            });
          });
        }
      }
    }
    return callback();
  };

  // 生成随机颜色
  function randomColor() {
    return '#' + ('00000' + ((Math.random() * 16777215 + 0.5) >> 0).toString(16)).slice(-6);
  }

  // 获取列HTML
  function getColumnHtml(step) {
    var widget =
      "<div class='widget ui-draggable'><div class='widget-header'><div class='widget-handle'><span class='widget-title'>步骤" + step;
    widget += "</span></div></div><div class='widget-body'><div class='row'>";
    widget += '<div class="col-xs-12"><div class="column" colspan=12"></div></div>';
    widget += '</div></div></div>';
    return widget;
  }

  // 返回定义的HTML
  component.prototype.toHtml = function ($element) {
    var $element = $(this.element);
    var options = this.options;
    // 计算生成渲染的列宽度
    var columnMap = converColumnMap(options);
    var id = this.getId();
    var html = new StringBuilder();
    html.appendFormat('<div id="{0}" class="step-container clearfix">', id);
    var children = this.getChildren();
    var placeHolders = this.$columns;
    $.each(placeHolders, function (i) {
      var $holder = $(this);
      var colspan = $holder.attr('colspan');
      var holderChildren = [];
      for (var index = 0; index < children.length; index++) {
        var child = children[index];
        if ($holder.has(child.element).length > 0) {
          holderChildren.push(child);
        }
      }
      // 子结点HTML
      var childHtml = '';
      $.each(holderChildren, function (i, child) {
        childHtml += child.toHtml();
      });
      if (i == 0) {
        html.appendFormat('<div class="column active col-idx-{0} col-xs-{1}">', i, colspan);
      } else {
        html.appendFormat('<div class="column next col-idx-{0} col-xs-{1} ">', i, colspan);
      }
      html.appendFormat('<div class="step-nav-{0} step-header"></div><div class="step-content-{0} step-content">', i);
      html.appendFormat(childHtml);
      html.appendFormat('</div><div class="step-btns-{0} step-footer"></div></div>', i);
    });
    html.appendFormat('</div>');
    console.log(html.toString());
    return html.toString();
  };

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
    var collectClass = 'w-configurer-option';
    var configurerPrototype = {};
    configurerPrototype.getTemplateUrl = function () {
      // 调用父类提交方法
      var wtype = this._superApply(arguments);
      return wtype;
    };
    configurerPrototype.onLoad = function ($container, options) {
      // 初始化页签项
      $('#layout_bootgrid_custom_tabs ul a', $container).on('click', function (e) {
        e.preventDefault();
        $(this).tab('show');
      });
      var _self = this.component;
      var configuration = $.extend(true, {}, options.configuration);
      // 基本信息
      this.initBaseInfo(configuration, $container);
      //步骤信息
      this.initStepNameInfo(configuration, $container);
      // 列信息
      this.initGridCellInfo(configuration, $container);
    };
    configurerPrototype.onOk = function ($container) {
      var _self = this.component;
      var options = _self.options;

      var configuration = {};

      configuration.steps = options.configuration.steps;
      // 基本信息
      this.collectBaseInfo(configuration, $container);
      //步骤名称信息
      this.collectStepNameInfo(configuration, $container);
      // 列信息
      this.collectGridCellInfo(configuration, $container);

      this.component.options.configuration = configuration;
    };
    configurerPrototype.initBaseInfo = function (configuration, $container) {
      // 设置值
      designCommons.setElementValue(configuration, $container);
      // 加载的JS模块
      $('#jsModule', $container).wSelect2({
        serviceName: 'appJavaScriptModuleMgr',
        params: {
          dependencyFilter: 'ListViewWidgetDevelopment'
        },
        labelField: 'jsModuleName',
        valueField: 'jsModule',
        remoteSearch: false,
        multiple: true
      });
    };
    configurerPrototype.initStepNameInfo = function (configuration, $container) {
      stepNameData = configuration.stepsData ? configuration.stepsData : [];
      var steps = configuration.steps ? configuration.steps : [];
      if (stepNameData.length == 0) {
        for (var i = 0; i < steps.length; i++) {
          var stepObj = new Object();
          stepObj.uuid = 'step_' + steps[i];
          stepObj.text = '第' + steps[i] + '步骤';
          stepObj.name = '';
          stepNameData.push(stepObj);
        }
      }
      var $stepNameInfoTable = $('#table_step_name_info', $container);
      $stepNameInfoTable.bootstrapTable('destroy').bootstrapTable({
        data: stepNameData,
        idField: 'uuid',
        showColumns: true,
        striped: true,
        width: 500,
        onEditableHidden: onEditHidden,
        columns: [
          {
            field: 'uuid',
            title: 'UUID',
            visible: false
          },
          {
            field: 'text',
            title: '步骤',
            editable: false
          },
          {
            field: 'name',
            title: '步骤导航名称',
            width: 500,
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true,
              validate: function (value) {
                if (StringUtils.isBlank(value)) {
                  return '请输入步骤导航名称!';
                }
              }
            }
          }
        ]
      });
    };
    configurerPrototype.initGridCellInfo = function (configuration, $container) {
      var buttonData = configuration.buttons ? configuration.buttons : [];
      var steps = configuration.steps ? configuration.steps : [];
      var piUuid = this.component.pageDesigner.getPiUuid();
      var system = appContext.getCurrentUserAppData().getSystem();
      var productUuid = system.productUuid;
      if (StringUtils.isNotBlank(system.piUuid)) {
        piUuid = system.piUuid;
      }
      this.gridStepConfig = [];
      var defaultBtns = [];
      for (var i = 0; i < steps.length; i++) {
        var stepObj = new Object();
        stepObj.value = steps[i];
        stepObj.text = '第' + steps[i] + '步骤';
        this.gridStepConfig.push(stepObj);
        if (i == 0) {
          var firstStepBtn = new Object();
          firstStepBtn.uuid = 'first_step_btn_defalut';
          firstStepBtn.text = '下一步';
          firstStepBtn.code = 'next_' + steps[i];
          firstStepBtn.position = steps[i];
          defaultBtns.push(firstStepBtn);
        } else if (i == steps.length - 1) {
          var lastStepBtn = new Object();
          lastStepBtn.uuid = 'last_step_btn_defalut';
          lastStepBtn.text = '上一步';
          lastStepBtn.code = 'prev_' + steps[i];
          lastStepBtn.position = steps[i];
          defaultBtns.push(lastStepBtn);
        } else {
          var nextBtn = new Object();
          var prevBtn = new Object();
          nextBtn.uuid = 'next_step_btn_defalut' + i;
          nextBtn.text = '下一步';
          nextBtn.code = 'next_' + steps[i];
          nextBtn.position = steps[i];
          prevBtn.uuid = 'prev_step_btn_defalut' + i;
          prevBtn.text = '上一步';
          prevBtn.code = 'prev_' + steps[i];
          prevBtn.position = steps[i];
          defaultBtns.push(prevBtn);
          defaultBtns.push(nextBtn);
        }
      }
      buttonData = buttonData.length == 0 ? defaultBtns : buttonData;
      // 按钮定义
      var $buttonInfoTable = $('#table_button_info', $container);

      var buttonRowBean = {
        checked: false,
        uuid: '',
        code: '',
        text: '',
        position: ['1'],
        group: '',
        cssClass: 'btn-default'
      };
      // 定义添加，删除，上移，下移4按钮事件
      formBuilder.bootstrapTable.initTableTopButtonToolbar('table_button_info', 'button', $container, buttonRowBean);

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
            editable: {
              type: 'checklist',
              mode: 'inline',
              onblur: 'submit',
              showbuttons: false,
              source: this.gridStepConfig,
              validate: function (value) {
                if (value.length == 0) {
                  return '请选择按钮位置!';
                }
              }
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
              value2input: designCommons.bootstrapTable.icon.value2input,
              input2value: designCommons.bootstrapTable.icon.input2value,
              value2display: designCommons.bootstrapTable.icon.value2display,
              value2html: designCommons.bootstrapTable.icon.value2html
            }
          },
          {
            field: 'cssClass',
            title: '样式',
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
            field: 'eventManger',
            title: '事件管理',
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

    configurerPrototype.collectBaseInfo = function (configuration, $container) {
      var $form = $('#layout_bootgrid_custom_tabs_base_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      $.extend(configuration, opt);
    };
    configurerPrototype.collectStepNameInfo = function (configuration, $container) {
      var $tableStepNameInfo = $('#table_step_name_info', $container);
      var gridCells = $tableStepNameInfo.bootstrapTable('getData');
      gridCells = $.map(gridCells, clearChecked);
      var opt = {};
      opt.stepsData = gridCells;
      $.extend(configuration, opt);
    };
    configurerPrototype.collectGridCellInfo = function (configuration, $container) {
      var $tableButtonInfo = $('#table_button_info', $container);
      var gridCells = $tableButtonInfo.bootstrapTable('getData');
      gridCells = $.map(gridCells, clearChecked);
      var opt = {};
      opt.buttons = gridCells;
      $.extend(configuration, opt);
    };
    var configurer = $.ui.component.BaseComponentConfigurer(configurerPrototype);
    return configurer;
  };

  // 返回组件定义
  component.prototype.getDefinitionJson = function () {
    var _self = this;
    var $element = $(_self.element);
    var definitionJson = _self.options;
    var columnMap = converColumnMap(definitionJson);
    var id = _self.getId();
    definitionJson.id = id;
    definitionJson.items = [];
    definitionJson.configuration.columns = [];
    var children = this.getChildren();
    var placeHolders = this.$columns;
    $.each(placeHolders, function (i) {
      var $holder = $(this);
      var colspan = $holder.attr('colspan');
      var holderChildren = [];
      for (var index = 0; index < children.length; index++) {
        var child = children[index];
        if ($holder.has(child.element).length > 0) {
          holderChildren.push(child);
        }
      }
      // 收集子结点定义
      $.each(holderChildren, function (j, child) {
        var childJson = child.getDefinitionJson();
        childJson.columnIndex = i;
        definitionJson.items.push(childJson);
      });
      // 标记所在列
      var column = {};
      column.index = i;
      column.colspan = colspan;
      if (columnMap[i]) {
        column.width = columnMap[i].width;
      }
      definitionJson.configuration.columns.push(column);
    });
    return definitionJson;
  };

  function converColumnMap(definitionJson) {
    if (definitionJson == null || definitionJson.configuration == null || definitionJson.configuration.columns == null) {
      return {};
    }
    var map = {};
    var columns = definitionJson.configuration.columns;
    $.each(columns, function (i) {
      map[this.index] = this;
    });
    return map;
  }

  return component;
});
