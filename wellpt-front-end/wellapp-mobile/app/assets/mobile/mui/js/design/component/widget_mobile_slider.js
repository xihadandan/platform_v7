define(['ui_component', 'design_commons', 'constant', 'commons', 'formBuilder', 'summernote', 'wSelect2'], function (
  ui_component,
  designCommons,
  constant,
  commons,
  formBuilder
) {
  var component = $.ui.component.BaseComponent();
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;

  function buildMenuItemBootstrapTable($element, name, menuItems, productUuid, navType) {
    var value2html = function (value, $el) {
      if ($.isEmptyObject(value) || !value) {
        $el.innerHTML = 'Empty';
      } else {
        $el.innerHTML = '<img src=' + value.iconPath + ' width="100" class="editable-container">';
      }
    };
    formBuilder.bootstrapTable.build({
      container: $element,
      name: name,
      ediableNest: true,
      table: {
        data: menuItems,
        striped: true,
        idField: 'uuid',
        onEditableSave: function (field, row, oldValue, $el) {
          if (navType !== '2') {
            return;
          }
          var $tableSubNavMenuItemsInfo = $('#table_subNav-menuItems_info', $element);
          if (field == 'defaultSelected' && row[field] == '1') {
            var data = $tableSubNavMenuItemsInfo.bootstrapTable('getData');
            $.each(data, function (index, rowData) {
              if (row != rowData) {
                rowData.defaultSelected = 0;
                $tableSubNavMenuItemsInfo.bootstrapTable('updateRow', index, rowData);
              }
            });
          }
        },
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
            field: 'text',
            title: '标题',
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit'
            }
          },
          {
            field: 'icon',
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
              value2html: value2html
            }
          },
          {
            field: 'hidden',
            title: '是否隐藏',
            editable: {
              type: 'select',
              mode: 'inline',
              onblur: 'submit',
              showbuttons: false,
              source: [
                {
                  value: '-1',
                  text: ''
                },
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
            field: 'imgURL',
            title: 'URL',
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit'
            }
          }
        ]
      }
    });
  }
  component.prototype.create = function () {
    $(this.element).css('overflow', 'auto');
  };
  component.prototype.usePropertyConfigurer = function () {
    return true;
  };
  component.prototype.getPropertyConfigurer = function () {
    var collectClass = 'w-configurer-option';
    var configurer = $.ui.component.BaseComponentConfigurer();
    configurer.prototype.onLoad = function ($container, options) {
      // 初始化页签项
      $('#widget_html_tabs ul a', $container).on('click', function (e) {
        e.preventDefault();
        $(this).tab('show');
      });
      $('#isLoop', $container).on('change', function () {
        if (this.checked) {
          $('#loop-time').css('display', 'block');
        } else {
          $('#loop-time').css('display', 'none');
        }
      });
      // $("#sliderHeight", $container).on("input", function () {
      //   if (Number($(this).val())) {
      //     $("#dot_style").show();
      //   } else {
      //     $("#dot_style").hide();
      //   }
      // })
      var configuration = $.extend(true, {}, options.configuration);
      this.initConfiguration(configuration, $container);
    };
    // 初始化配置信息
    configurer.prototype.initConfiguration = function (configuration, $container) {
      // 基本信息
      this.initBaseInfo(configuration, $container);
      this.initContentInfo(configuration, $container);
    };
    // 初始化配置信息
    configurer.prototype.initBaseInfo = function (configuration, $container) {
      // 设置值
      designCommons.setElementValue(configuration, $container);
      if ($('#isLoop').is(':checked')) {
        $('#loop-time').css('display', 'block');
      }
      // if (Number($("#sliderHeight", $container).val())) {
      //   $("#dot_style").show();
      // }
      var dotMode = [
        {
          id: 'default',
          text: 'default'
        },
        {
          id: 'dot',
          text: 'dot'
        },
        {
          id: 'round',
          text: 'round'
        },
        {
          id: 'nav',
          text: 'nav'
        },
        {
          id: 'indexes',
          text: 'indexes'
        }
      ];
      $('#dotMode').wSelect2({
        data: dotMode,
        valueField: 'dotMode',
        remoteSearch: false
      });
      this.buildImageContentByColors($container, configuration);
      this.buildSelContentByColors($container, configuration);
    };
    configurer.prototype.buildImageContentByColors = function ($container, options) {
      formBuilder.buildColorsInput({
        container: $container.find('#dotColorContainer'),
        label: '',
        name: 'dotColor',
        value: options.dotColor,
        inputClass: 'w-configurer-option',
        // labelColSpan: "1",
        controlColSpan: '12'
      });
    };
    configurer.prototype.buildSelContentByColors = function ($container, options) {
      formBuilder.buildColorsInput({
        container: $container.find('#dotSelColorContainer'),
        label: '',
        name: 'dotSelColor',
        value: options.dotSelColor,
        inputClass: 'w-configurer-option',
        // labelColSpan: "1",
        controlColSpan: '12'
      });
    };
    configurer.prototype.initContentInfo = function (configuration, $container) {
      var _self = this;
      var sliderImg = configuration.sliderImg || {};
      // 设置值
      designCommons.setElementValue(sliderImg, $container);
      var appPageUuid = _self.component.pageDesigner.getPageUuid();
      var system = appContext.getCurrentUserAppData().getSystem();
      var productUuid = system.productUuid;
      var $element = $('.slider-sliderItems', $container);
      var sliderItems = sliderImg.sliderItems || [];
      buildMenuItemBootstrapTable($element, 'slider-sliderItems', sliderItems, productUuid, '1');
    };
    configurer.prototype.onOk = function ($container) {
      this.component.options.configuration = this.collectConfiguration($container);
    };
    // 收集配置信息
    configurer.prototype.collectConfiguration = function ($container) {
      var configuration = {};
      // 基本信息
      this.collectBaseInfo(configuration, $container);
      // 内容信息
      this.collectContentInfo(configuration, $container);
      return $.extend({}, configuration);
    };
    configurer.prototype.collectBaseInfo = function (configuration, $container) {
      var $form = $('#widget_html_tabs_base_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      $.extend(configuration, opt);
    };
    configurer.prototype.collectContentInfo = function (configuration, $container) {
      var $form = $('#widget_html_tabs_content_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      var $tableSliderItemsInfo = $('#table_slider-sliderItems_info', $container);
      var sliderItems = $tableSliderItemsInfo.bootstrapTable('getData');
      opt.sliderItems = sliderItems;
      configuration.sliderImg = configuration.sliderImg || {};
      $.extend(configuration.sliderImg, opt);
    };
    return configurer;
  };
  component.prototype.toHtml = function () {
    var _self = this;
    var options = _self.options;
    var configuration = options.configuration || {};
    //var content = configuration.content || options.content;
    var id = _self.getId();
    //var html = '<div id="' + id + '" class="ui-wSlider">' + content + '</div>';
    var html = '<div id="' + id + '" class="ui-wSlider"></div>';
    return html;
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
