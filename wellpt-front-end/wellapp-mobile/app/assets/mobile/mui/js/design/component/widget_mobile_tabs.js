define(['ui_component', 'constant', 'commons', 'server', 'formBuilder', 'appContext', 'design_commons', 'wSelect2'], function (
  ui_component,
  constant,
  commons,
  server,
  formBuilder,
  appContext,
  designCommons
) {
  var collectClass = 'w-configurer-option';
  var StringUtils = commons.StringUtils;
  var component = $.ui.component.BaseComponent();
  var isJSON = function (str) {
    try {
      JSON.parse(str);
      return true;
    } catch (e) {
      return false;
    }
  };
  var checkedFormat = function (value, row, index) {
    if (value) {
      return true;
    }
    return false;
  };

  var checkRequire = function (propertyNames, options, $container) {
    for (var i = 0; i < propertyNames.length; i++) {
      var propertyName = propertyNames[i];
      if (StringUtils.isBlank(options[propertyName])) {
        var title = $("label[for='" + propertyName + "']", $container).text();
        appModal.error(title.replace('*', '') + '不允许为空！');
        return false;
      }
    }
    return true;
  };

  var onEditHidden = function (field, row, $el, reason) {
    $el.closest('table').bootstrapTable('resetView');
  };

  var configurer = $.ui.component.BaseComponentConfigurer();
  component.prototype.create = function () {
    //				$(this.element).find(".widget-body").html(this.options.content);

    var _self = this;
    var options = _self.options;
    var $element = $(_self.element);
    var $tabsContainer = $element.find('.ui-sortable');
    _self.pageDesigner.sortable(_self, $tabsContainer, $tabsContainer);

    // 初始化容器结点
    if (options.items != null) {
      $.each(options.items, function (j, item) {
        var $draggable = _self.pageDesigner.createDraggableByDefinitionJson(item);
        _self.pageDesigner.drop(_self, $tabsContainer, $draggable, item);
      });
    }
  };
  component.prototype.usePropertyConfigurer = function () {
    return true;
  };
  configurer.prototype.initBaseInfo = function (configuration, $container) {
    // 二开JS模块
    $('#jsModule', $container).wSelect2({
      serviceName: 'appJavaScriptModuleMgr',
      params: {
        dependencyFilter: 'BootstrapTabsWidgetDevelopment'
      },
      labelField: 'jsModuleName',
      valueField: 'jsModule',
      remoteSearch: false,
      multiple: true
    });

    // Tab模式
    $('#tabType', $container)
      .select2({
        width: '100%'
      })
      .on('change', function (event) {
        var selectType = $(this).val();
        // 隐藏全部
        $('.tab-type', $container).addClass('hidden');
        // 显示指定类型
        $('.tab-type-' + selectType, $container).removeClass('hidden');
      })
      .trigger('change');

    $('select.w-configurer-option', $container).select2({
      width: '100%'
    });
  };

  configurer.prototype.initTabsInfo = function (configuration, $container) {
    var _self = this;
    var tabRowBean = {
      checked: false,
      uuid: '',
      name: '',
      id: '',
      icon: '',
      eventHandler: ''
    };

    var $tabelInfo = $('#table_tabs_info');
    formBuilder.bootstrapTable.initTableTopButtonToolbar('table_tabs_info', 'tab', $container, tabRowBean);
    var system = appContext.getCurrentUserAppData().getSystem();
    var productUuid = system.productUuid;
    var tabs = configuration.tabs ? configuration.tabs : [];
    $.each(tabs, function (i, item) {
      if (!item.eventManger) {
        item.eventManger = {
          eventHandler: item.eventHandler,
          eventParams: item.eventParams,
          parameters: item.eventParams ? item.eventParams.parameters : null,
          params: item.eventParams ? item.eventParams.params : null
        };
      }
    });
    $tabelInfo.bootstrapTable('destroy').bootstrapTable({
      data: tabs,
      idField: 'uuid',
      striped: true,
      width: 500,
      onEditableHidden: onEditHidden,
      toolbar: $('#div_button_tab_toolbar', $container),
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
          field: 'name',
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
          field: 'id',
          title: 'Id',
          editable: {
            type: 'text',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            validate: function (value) {
              if (StringUtils.isBlank(value)) {
                return '请输入Id!';
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
          // }, {
          //   field: "eventHandler",
          //   title: "事件处理",
          //   width: 150,
          //   editable: {
          //     mode: "modal",
          //     type: "wCommonComboTree",
          //     showbuttons: false,
          //     onblur: "submit",
          //     placement: "bottom",
          //     otherProperties: {
          //       'type': 'data.type',
          //       'path': 'data.path'
          //     },
          //     otherPropertyPath: "data",
          //     wCommonComboTree: {
          //       inlineView: true,
          //       service: "appProductIntegrationMgr.getTreeWithPtProduct",
          //       serviceParams: [productUuid],
          //       multiSelect: false, // 是否多选
          //       parentSelect: true
          //       // 父节点选择有效，默认无效
          //     }
          //   }
          // }, {
          //   field: "eventParams",
          //   title: "事件参数",
          //   editable: {
          //     mode: "modal",
          //     onblur: "cancel",
          //     type: "wCustomForm",
          //     placement: "left",
          //     savenochange: true,
          //     value2input: designCommons.bootstrapTable.eventParams.value2input,
          //     input2value: designCommons.bootstrapTable.eventParams.input2value,
          //     value2display: designCommons.bootstrapTable.eventParams.value2display
          //   }
        }
      ]
    });
  };

  configurer.prototype.onLoad = function ($container, options) {
    var configuration = $.extend(true, {}, options.configuration);
    $('#widget_tree_tabs ul a', $container).on('click', function (e) {
      e.preventDefault();
      $(this).tab('show');
    });
    designCommons.setElementValue(configuration, $container, 'tabs');
    // 初始化基础信息tab
    this.initBaseInfo(configuration, $container);
    // 初始化页签tab
    this.initTabsInfo(configuration, $container);
  };

  configurer.prototype.onOk = function ($container) {
    if (this.component.isReferenceWidget()) {
      return;
    }
    var opt = designCommons.collectConfigurerData($('#widget_tree_tabs_base_info', $container), collectClass);
    opt.tabs = $('#table_tabs_info').bootstrapTable('getData');
    $.each(opt.tabs, function (i, item) {
      if (item.eventManger) {
        item.eventHandler = item.eventManger.eventHandler;
        item.eventParams = item.eventManger.eventParams;
      }
    });
    var requeryFields = ['name'];
    if (!checkRequire(requeryFields, opt, $container)) {
      return false;
    }
    this.component.options.configuration = $.extend({}, opt);
  };

  component.prototype.getPropertyConfigurer = function () {
    return configurer;
  };
  component.prototype.toHtml = function () {
    var _self = this;
    var options = _self.options;
    var children = _self.getChildren();
    var id = _self.getId();
    var html = "<div id='" + id + "' class='ui-wMobileTabs'>";
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
