define(['jquery', 'jquery-ui', 'commons', 'design_commons', 'formBuilder', 'jsoneditor', 'minicolors'], function (
  $,
  jqueryui,
  commons,
  designCommons,
  formBuilder,
  JSONEditor
) {
  $.ui.chartComponent = $.extend({}, $.ui.component);

  var ChartBaseComponentConfigurer = $.ui.component.BaseComponentConfigurer();
  var collectClass = 'w-configurer-option';

  var fontWeight = [
    { id: 'normal', text: '正常字体' },
    { id: 'bold', text: '粗字体' },
    { id: 'bolder', text: '特粗字体' },
    { id: 'lighter', text: '细字体' }
  ];

  ChartBaseComponentConfigurer.prototype.initBaseDefineInfo = function (configuration, $container) {
    var _self = this;
    $container = $('#widget_chart_base_info', $container);
    configuration.backgroundColor = configuration.backgroundColorHex;
    designCommons.setElementValue(configuration, $container);
    configuration.textStyle = configuration.textStyle || {};
    designCommons.setElementValue(configuration.textStyle, $container);
    this._elementJsModuleSelectInitial($container, configuration);
    this._elementColorInitial(
      $container,
      'backgroundColor',
      configuration.backgroundColorHex,
      true,
      'top left',
      configuration.backgroundColorOpacity
    );
    this._elementFontStyleSelectInitial($container, 'textStyle_fontStyle');
    this._elementFontWeightSelectInitial($container, 'textStyle_fontWeight');
    this._elementColorInitial(
      $container,
      'textStyle_color',
      configuration.textStyle ? configuration.textStyle.color : null,
      false,
      'top left'
    );

    var colorNum = 0;
    //调色盘
    $('#addColor', $container).on('click', function (e, initColor) {
      var _id = 'color_' + colorNum++;
      initColor = initColor || {};
      var $input = $('<input>', {
        type: 'text',
        class: 'form-control add-color',
        id: _id,
        name: 'color',
        style: 'width:120px'
      });
      var $div = $('<div>', { style: 'float:left' }).append($input);
      $div.insertBefore($(this));
      _self._elementColorInitial($container, 'color_' + (colorNum - 1), initColor.colorHex, true, 'top left', initColor.colorOpacity);
      $input.focus();
    });
    if (configuration.colorObjects) {
      for (var i = 0, len = configuration.colorObjects.length; i < len; i++) {
        (function (index) {
          $('#addColor', $container).trigger('click', [configuration.colorObjects[index]]);
        })(i);
      }
    }
    $('#minusColor', $container).on('click', function () {
      $('.add-color:last').parent().remove();
    });
  };

  ChartBaseComponentConfigurer.prototype.initChartDataInfo = function (configuration, $container) {
    var _self = this;
    $container = $('#widget_chart_data_info', $container);
    var chartDataInfo = configuration.chartDataInfo || {};
    designCommons.setElementValue(chartDataInfo, $container);

    $('#dataSourceType', $container).wSelect2({
      labelField: 'dataSourceTypeName',
      valueField: 'dataSourceType',
      remoteSearch: false,
      data: [
        { id: 'json', text: '自定义Json数据集' },
        { id: 'interface', text: '数据集接口' }
      ]
    });

    $('#dataSourceType', $container)
      .on('change', function () {
        $('.dataSourceType', $container).hide();
        if ($(this).val()) {
          $('#dataSourceType_' + $(this).val()).show();
        }
      })
      .trigger('change');

    // 加载的数据集
    $('#dataInterface', $container).wSelect2({
      serviceName: 'reportFacadeService',
      queryMethod: 'loadEchartDatasetServiceData',
      labelField: 'dataInterfaceName',
      valueField: 'dataInterface',
      remoteSearch: false
    });

    //json编辑器化
    this.initJsoneditor('defineJsonDataEditor', 'defineJsonData', $container, chartDataInfo.defineJsonData || [], {
      templates: [
        //定义json编辑器的相关属性模板
        {
          text: '数据集',
          title: '插入数据集',
          className: 'jsoneditor-type-object',
          field: '',
          value: {
            dimensions: ['月份', '数量'],
            source: [
              { 月份: '一月份', 数量: 100 },
              { 月份: '二月份', 数量: 200 }
            ]
          }
        },
        {
          text: '维度信息',
          title: '插入维度信息',
          className: 'jsoneditor-type-array',
          field: 'dimensions',
          value: ['维度一', '维度二']
        },
        {
          text: '维度数据',
          title: '插入维度数据',
          className: 'jsoneditor-type-array',
          field: 'source',
          value: [
            {
              维度一: '维度一的值',
              维度二: 0
            }
          ]
        }
      ]
    });
  };

  //jsoneditor编辑器
  ChartBaseComponentConfigurer.prototype.initJsoneditor = function (containerId, valueId, $pageContainer, defaultValue, opt) {
    //json编辑器化
    var jsonContainer = document.getElementById(containerId);
    var options = {
      modes: ['code', 'tree'],
      onChange: function () {
        var $jsoneditor = $('#' + containerId, $pageContainer).data('jsoneditor');
        var json;
        try {
          json = $jsoneditor.getText();
        } catch (e) {}
        if (json) {
          $('#' + valueId, $pageContainer).val(json);
        }
      }
    };

    var jsoneditor = new JSONEditor(jsonContainer, $.extend(options, opt));
    if (defaultValue) {
      jsoneditor.set(_.isString(defaultValue) ? JSON.parse(defaultValue) : defaultValue);
    }
    $('#' + containerId, $pageContainer).data('jsoneditor', jsoneditor);
  };

  // 初始化图表标题定义信息
  ChartBaseComponentConfigurer.prototype.initTitleDefineInfo = function (configuration, $container) {
    var titleDefineInfo = configuration.titleDefineInfo || {};
    titleDefineInfo.backgroundColor = titleDefineInfo.backgroundColorHex;
    titleDefineInfo.borderColor = titleDefineInfo.borderColorHex;
    $container = $('#widget_chart_title_info', $container);

    if ($.isArray(titleDefineInfo.padding)) {
      titleDefineInfo.padding = titleDefineInfo.padding.join(','); //转字符串
    }
    if ($.isArray(titleDefineInfo.borderRadius)) {
      titleDefineInfo.borderRadius = titleDefineInfo.borderRadius.join(','); //转字符串
    }

    designCommons.setElementValue(titleDefineInfo, $container);

    this._elementShowCheckboxEvent($container);

    this._elementTargetWindowModeSelectInitial($container, 'target');
    this._elementTargetWindowModeSelectInitial($container, 'subtarget');
    this._elementFontStyleSelectInitial($container, 'title_textStyle_fontStyle', function (val) {
      $('#text', $container).css('font-style', val);
    });
    this._elementFontStyleSelectInitial($container, 'subtextStyle_fontStyle', function (val) {
      $('#subtext', $container).css('font-style', val);
    });
    this._elementFontWeightSelectInitial($container, 'title_textStyle_fontWeight');
    this._elementFontWeightSelectInitial($container, 'subtextStyle_fontWeight');

    $('#textStyle_fontFamily,#subtextStyle_fontFamily', $container).on('blur', function () {
      $('#' + $(this).attr('name').replace('Style_fontFamily', '')).css('font-family', $(this).val());
    });

    $('#title_textStyle_fontWeight,#subtextStyle_fontWeight', $container).on('change', function () {
      $('#' + $(this).attr('name').replace('Style_fontWeight', '')).css('font-weight', $(this).val());
    });

    // FIXME:对齐方式与偏移量冲突，存在样式问题
    /* //主/副标题水平对齐方式
             $("#textAlign", $container).wSelect2({
                 labelField: "textAlignName",
                 valueField: "textAlign",
                 remoteSearch: false,
                 data: [
                     {id: 'auto', text: '自动'},
                     {id: 'left', text: '左对齐'},
                     {id: 'right', text: '右对齐'},
                     {id: 'center', text: '居中对齐'}
                 ]
             });
             //主/副标题垂直对齐方式
             $("#textVerticalAlign", $container).wSelect2({
                 labelField: "textVerticalAlignName",
                 valueField: "textVerticalAlign",
                 remoteSearch: false,
                 data: [
                     {id: 'auto', text: '自动'},
                     {id: 'top', text: '顶部对齐'},
                     {id: 'bottom', text: '底部对齐'},
                     {id: 'middle', text: '中部对齐'}
                 ]
             });*/

    this._elementColorInitial(
      $container,
      'backgroundColor',
      titleDefineInfo.backgroundColorHex,
      true,
      'top left',
      titleDefineInfo.backgroundColorOpacity
    );
    this._elementColorInitial(
      $container,
      'borderColor',
      titleDefineInfo.borderColorHex,
      true,
      'top left',
      titleDefineInfo.borderColorOpacity
    );
    this._elementColorInitial(
      $container,
      'title_textStyle_color',
      titleDefineInfo.textStyle_color,
      false,
      'bottom left',
      null,
      function (_id, val) {
        $('#text', $container).css('color', val);
      }
    );
    this._elementColorInitial(
      $container,
      'subtextStyle_color',
      titleDefineInfo.subtextStyle_color,
      false,
      'bottom left',
      null,
      function (_id, val) {
        $('#subtext', $container).css('color', val);
      }
    );
  };

  ChartBaseComponentConfigurer.prototype.initLegendDefineInfo = function (configuration, $container) {
    var _self = this;
    var legendDefineInfo = configuration.legendDefineInfo || {};
    // legendDefineInfo.backgroundColor = legendDefineInfo.backgroundColorHex;
    // legendDefineInfo.borderColor = legendDefineInfo.borderColorHex;
    // legendDefineInfo.textStyle_borderColor = legendDefineInfo.textStyle_borderColorHex;
    // legendDefineInfo.textStyle_backgroundColor = legendDefineInfo.textStyle_backgroundColorHex;

    $container = $('#widget_chart_legend_info', $container);

    if ($.isArray(legendDefineInfo.padding)) {
      legendDefineInfo.padding = legendDefineInfo.padding.join(','); //转字符串
    }
    if ($.isArray(legendDefineInfo.borderRadius)) {
      legendDefineInfo.borderRadius = legendDefineInfo.borderRadius.join(','); //转字符串
    }

    designCommons.setElementValue(legendDefineInfo, $container);

    $('#legendType', $container).wSelect2({
      valueField: 'legendType',
      remoteSearch: false,
      data: [
        { id: 'plain', text: '普通图例' },
        { id: 'scroll', text: '可滚动翻页的图例' }
      ]
    });

    $('#icon', $container).wSelect2({
      valueField: 'icon',
      remoteSearch: false,
      data: [
        { id: 'circle', text: '圆形' },
        { id: 'rect', text: '矩形' },
        { id: 'roundRect', text: '圆角矩形' },
        { id: 'triangle', text: '三角形' },
        { id: 'diamond', text: '菱形' },
        { id: 'arrow', text: '箭号' },
        { id: 'none', text: '无图形' }
      ]
    });

    $('#selectedMode', $container).wSelect2({
      valueField: 'selectedMode',
      remoteSearch: false,
      data: [
        { id: 'true', text: '开启图例选择' },
        { id: 'false', text: '关闭图例选择' },
        { id: 'single', text: '单选图例选择' },
        { id: 'multiple', text: '多选图例选择' }
      ]
    });

    this._elementShowCheckboxEvent($container, 'showIcon');

    this._elementOrientSelectInitial('orient', $container);
    this._elementAlignSelectInitial($container, 'align');
    this._elementColorInitial($container, 'backgroundColor', legendDefineInfo.legend_backgroundColorHex, true, 'top left');
    this._elementColorInitial($container, 'borderColor', legendDefineInfo.legend_borderColorHex, true, 'top left');
    this._elementFontStyleSelectInitial($container, 'legend_textStyle_fontStyle');
    this._elementFontWeightSelectInitial($container, 'legend_textStyle_fontWeight');
    this._elementColorInitial(
      $container,
      'textStyle_backgroundColor',
      legendDefineInfo.textStyle_backgroundColorHex,
      true,
      'top left',
      legendDefineInfo.textStyle_backgroundColorOpacity
    );
    this._elementColorInitial($container, 'textStyle_borderColor', legendDefineInfo.textStyle_borderColorHex, true, 'top left');
  };

  ChartBaseComponentConfigurer.prototype._elementJsModuleSelectInitial = function ($container, configuration) {
    // 加载的JS模块
    $('#jsModule', $container).wSelect2({
      serviceName: 'appJavaScriptModuleMgr',
      params: {
        dependencyFilter: configuration.jsDependencyFilter || 'ChartWidgetDevelopment'
      },
      labelField: 'jsModuleName',
      valueField: 'jsModule',
      remoteSearch: false,
      multiple: true
    });
  };

  ChartBaseComponentConfigurer.prototype._elementAlignSelectInitial = function ($container, id) {
    $('#' + id, $container).wSelect2({
      valueField: id,
      remoteSearch: false,
      data: [
        { id: 'auto', text: '自动对齐' },
        { id: 'left', text: '左对齐' },
        { id: 'right', text: '右对齐' }
      ]
    });
  };

  ChartBaseComponentConfigurer.prototype._elementVerticalAlignSelectInitial = function ($container, id) {
    $('#' + id, $container).wSelect2({
      valueField: id,
      remoteSearch: false,
      data: [
        { id: 'top', text: '顶部对齐' },
        { id: 'middle', text: '居中对齐' },
        { id: 'bottom', text: '底部对齐' }
      ]
    });
  };

  ChartBaseComponentConfigurer.prototype._elementTargetWindowModeSelectInitial = function ($container, id) {
    // 副标题超链接跳转方式
    $('#' + id, $container).wSelect2({
      valueField: id,
      remoteSearch: false,
      data: [
        { id: 'blank', text: '新窗口打开' },
        { id: 'self', text: '当前窗口打开' }
      ]
    });
  };

  /**
   * 元素字体样式select2初始化
   * @param $container
   * @param id
   * @private
   */
  ChartBaseComponentConfigurer.prototype._elementFontStyleSelectInitial = function ($container, id) {
    $('#' + id, $container).each(function () {
      var _id = $(this).attr('id');
      $(this).wSelect2({
        valueField: _id,
        remoteSearch: false,
        data: [
          { id: 'normal', text: '常规文字' },
          { id: 'italic', text: '斜体文字' },
          { id: 'oblique', text: '倾斜文字' }
        ]
      });
    });
  };

  /**
   * 元素字体大小select2初始化
   * @param $container
   * @param id
   * @private
   */
  ChartBaseComponentConfigurer.prototype._elementFontWeightSelectInitial = function ($container, id) {
    $('#' + id, $container).each(function () {
      var _id = $(this).attr('id');
      $(this).wSelect2({
        valueField: _id,
        remoteSearch: false,
        data: [].concat(fontWeight)
      });
    });
  };

  /**
   * 元素朝向布局select2插件初始化
   * @param id
   * @private
   */
  ChartBaseComponentConfigurer.prototype._elementOrientSelectInitial = function (id, $container) {
    $('#' + id, $container).wSelect2({
      valueField: id,
      remoteSearch: false,
      data: [
        { id: 'horizontal', text: '水平' },
        { id: 'vertical', text: '垂直' }
      ]
    });
  };

  /**
   * 元素颜色插件初始化
   * @param $container
   * @param id
   * @param defaultValue
   * @param opacity
   * @param position
   * @private
   */
  ChartBaseComponentConfigurer.prototype._elementColorInitial = function (
    $container,
    id,
    defaultValue,
    opacity,
    position,
    opacityValue,
    changeCallback
  ) {
    $('#' + id, $container).minicolors({
      control: 'hue',
      format: 'hex',
      opacity: opacity,
      position: position || 'top left',
      letterCase: 'lowercase',
      change: function (value, opacity) {
        var _this = $('#' + id, $container);
        _this.data('rgbObject', _this.minicolors('rgbObject'));
        _this.data('rgbaString', _this.minicolors('rgbaString'));

        if ($.isFunction(changeCallback)) {
          changeCallback(id, value);
        }
      },
      hide: function () {},
      theme: 'bootstrap'
    });
    if (defaultValue != null) {
      $('#' + id, $container).minicolors('value', defaultValue); //设置值触发change事件
    }

    if (opacity === true) {
      //设置透明度
      if (opacityValue != undefined) {
        $('#' + id, $container).minicolors('opacity', parseFloat(opacityValue));
      }
    }
  };

  ChartBaseComponentConfigurer.prototype.initTooltipDefineInfo = function (configuration, $container) {
    var _self = this;
    var tooltipDefineInfo = configuration.tooltipDefineInfo || {};
    $container = $('#widget_chart_tooltip_info', $container);
    designCommons.setElementValue(tooltipDefineInfo, $container);

    _self._elementShowCheckboxEvent($container, 'showTooltip');

    //触发类型
    $('#trigger', $container).wSelect2({
      valueField: 'trigger',
      remoteSearch: false,
      data: [
        { id: 'item', text: '数据项图形触发' },
        { id: 'axis', text: '坐标轴触发' },
        { id: 'none', text: '不触发' }
      ]
    });
    $('#triggerOn', $container).wSelect2({
      valueField: 'triggerOn',
      remoteSearch: false,
      data: [
        { id: 'mousemove', text: '鼠标移动时触发' },
        { id: 'click', text: '鼠标点击时触发' },
        { id: 'mousemove|click', text: '同时鼠标移动和点击时触发' },
        { id: 'none', text: '不触发' }
      ]
    });
    $('#formatterType', $container)
      .on('change', function () {
        if ($(this).val() == 'function') {
          $('#formatter', $container).attr(
            'placeholder',
            '回调函数代码支持参数params，返回HTML字符串。例如：return params.name+"<br>"+params.seriesName; '
          );
          $('#formatterFunctionDescription', $container).show();
          $('#stringTemplateDescription', $container).hide();
        } else if ($(this).val() == 'stringTemplate') {
          $('#stringTemplateDescription', $container).show();
          $('#formatterFunctionDescription', $container).hide();
        } else {
          $('#formatter', $container).attr('placeholder', '');
          $('#stringTemplateDescription', $container).hide();
          $('#formatterFunctionDescription', $container).hide();
        }
      })
      .trigger('change');

    _self._elementColorInitial(
      $container,
      'backgroundColor',
      tooltipDefineInfo.backgroundColorHex ? tooltipDefineInfo.backgroundColorHex : null,
      true,
      'top left',
      tooltipDefineInfo.backgroundColorOpacity ? tooltipDefineInfo.backgroundColorOpacity : null
    );
    _self._elementColorInitial(
      $container,
      'borderColor',
      tooltipDefineInfo.borderColor ? tooltipDefineInfo.borderColor : null,
      false,
      'top left'
    );
    _self._elementColorInitial(
      $container,
      'tooltip_textStyle_color',
      tooltipDefineInfo.textStyle_color ? tooltipDefineInfo.textStyle_color : null,
      false,
      'top left'
    );
    _self._elementFontStyleSelectInitial($container, 'tooltip_textStyle_fontStyle');
    _self._elementFontWeightSelectInitial($container, 'tooltip_textStyle_fontWeight');
  };

  ChartBaseComponentConfigurer.prototype.initToolboxDefineInfo = function (configuration, $container) {
    var _self = this;
    var toolboxDefineInfo = configuration.toolboxDefineInfo || {};
    $container = $('#widget_chart_toolbox_info', $container);

    if (toolboxDefineInfo.feature_magicType_type && toolboxDefineInfo.feature_magicType_type.length != 0) {
      toolboxDefineInfo.feature_magicType_type = toolboxDefineInfo.feature_magicType_type.join(';');
    }
    designCommons.setElementValue(toolboxDefineInfo, $container);
    this._elementOrientSelectInitial('toolbox_orient', $container);
    $('#feature_saveAsImage_show')
      .on('change', function () {
        if ($(this).prop('checked')) {
          $(this).parents('fieldset').find('.form-group:gt(0)').show();
        } else {
          $(this).parents('fieldset').find('.form-group:gt(0)').hide();
        }
      })
      .trigger('change');

    $('#feature_saveAsImage_type').wSelect2({
      valueField: 'feature_magicType_type',
      remoteSearch: false,
      data: [
        { id: 'png', text: 'png图片格式' },
        { id: 'jpeg', text: 'jpeg图片格式' }
      ]
    });

    //以下类型允许图表切换
    if ($.inArray(configuration.chartType, ['line', 'bar', 'stack', 'tiled']) != -1) {
      $('#switch_chart').show();

      $('#feature_magicType_type', $container).wSelect2({
        valueField: 'feature_magicType_type',
        remoteSearch: false,
        data: [
          { id: 'line', text: '折线图' },
          { id: 'bar', text: '柱状图' },
          { id: 'stack', text: '堆叠模式' },
          { id: 'tiled', text: '平铺模式' }
        ],
        multiple: true
      });

      $('#feature_magicType_show', $container)
        .on('change', function () {
          $('#switch_chart > .form-group:gt(0)').hide();
          if ($(this).prop('checked')) {
            $('#switch_chart > .form-group:gt(0)').show();
          }
        })
        .trigger('change');
    }

    _self._elementShowCheckboxEvent($container, 'showToolbox', function (checked) {
      if (checked && $.inArray(configuration.chartType, ['line', 'bar', 'stack', 'tiled']) == -1) {
        $('#switch_chart').hide();
      }
    });
  };
  ChartBaseComponentConfigurer.prototype._elementShowCheckboxEvent = function ($container, id, callback) {
    $('#' + (id ? id : 'show'), $container)
      .on('change', function () {
        var $form = $container.find('> form');
        if ($form.length == 1) {
          $container = $form;
        }
        $container.find('> .form-group:gt(0)').hide();
        $container.find('> fieldset').hide();
        if ($(this).prop('checked')) {
          $container.find('> .form-group:gt(0)').show();
          $container.find('> fieldset').show();
        }
        if ($.isFunction(callback)) {
          callback($(this).prop('checked'));
        }
      })
      .trigger('change');
  };

  ChartBaseComponentConfigurer.prototype.initSeriesDefineInfo = function (configuration, $container) {
    var _self = this;
    var seriesDefineInfo = configuration.seriesDefineInfo || {};
    $container = $('#widget_chart_series_info', $container);

    var $seriesTable = $('#table_series_info', $container);
    //系列数据表格初始化
    var seriesRowBean = {
      type: null,
      name: null,
      datasetIndex: null, //对应行标
      series: null
    };

    formBuilder.bootstrapTable.initTableTopButtonToolbar('table_series_info', 'series', $container, seriesRowBean);

    var $seriesTable = $('#table_series_info', $container);
    $seriesTable.bootstrapTable('destroy').bootstrapTable({
      data: seriesDefineInfo.seriesRow || [],
      idField: 'uuid',
      striped: true,
      showColumns: true,
      toolbar: $('#div_series_info_toolbar', $container),
      onEditableHidden: function (field, row, $el, reason) {
        $el.closest('table').bootstrapTable('resetView');
      },
      width: 500,
      columns: [
        {
          field: 'checked',
          formatter: function (value, row, index) {
            if (value) {
              return true;
            }
            return false;
          },
          checkbox: true
        },
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'name',
          title: '系列名称',
          editable: {
            type: 'text',
            showbuttons: false,
            onblur: 'submit',
            mode: 'inline'
          }
        },
        {
          field: 'type',
          title: '系列类型',
          width: 400,
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            onblur: 'submit',
            emptytext: '请选择',
            source: [
              {
                value: 'pie',
                text: '饼图'
              },
              {
                value: 'line',
                text: '折线图'
              },
              {
                value: 'bar',
                text: '柱状图'
              }
            ]
          }
        },
        {
          field: 'operation',
          title: '操作',
          width: 100,
          formatter: function (value, row, index) {
            return '<button type="button" data-index=\'' + index + '\' class="btn btn-primary btn-series-define">定义</button>';
          }
        },
        {
          field: 'series',
          title: 'series',
          visible: false
        }
      ]
    });

    var seriesHtml = {};
    $('#series_define_detail div[type]', $container).each(function (i, item) {
      seriesHtml[$(this).attr('type')] = $(this).html();
    });
    $('#series_define_detail', $container).data('seriesHtml', seriesHtml);
    $('#series_define_detail', $container).html('');

    //定义按钮
    $seriesTable.on('click', '.btn-series-define', function () {
      var row = $seriesTable.bootstrapTable('getData')[parseInt($(this).attr('data-index'))];
      if (!row.type) {
        appModal.info('请先系列类型');
        return false;
      }
      _self.showSeriesDetailDialog(row, $container);
    });
  };

  ChartBaseComponentConfigurer.prototype.showSeriesDetailDialog = function (row, $container) {
    var seriesTemplateContainer =
      "<div id='series_detail_container' style='max-height: 600px;overflow-y: auto;  overflow-x: hidden;'></div>";
    var _self = this;
    var $dialog = appModal.dialog({
      title: '系列定义',
      size: 'large',
      message: seriesTemplateContainer,
      buttons: {
        confirm: {
          label: '确定',
          className: 'btn-primary',
          callback: function () {
            //收集数据
            _self.collectSeriesDetailDialog(row);
          }
        },
        cancel: {
          label: '关闭',
          className: 'btn-default',
          callback: function () {}
        }
      },
      shown: function () {
        var $detailContainer = $('#series_detail_container');
        $detailContainer.html('');
        var html = $('#series_define_detail', $container).data('seriesHtml')['series_' + row.type];
        $detailContainer.html('<form class="form-horizontal widget-template-container">' + html + '</form>');
        _self.initSeriesDetailDialog(row);
      }
    });
  };

  ChartBaseComponentConfigurer.prototype.collectSeriesDetailDialog = function (row) {
    var $container = $('#series_detail_container');
    var collect = designCommons.collectConfigurerData($container, collectClass);
    var _self = this;
    if (row.type === 'pie') {
      if (collect.label_show.length == 1) {
        collect.label_show = Boolean(collect.label_show[0]);
      } else {
        collect.label_show = false;
      }

      //半径
      if (collect['radius-in'] != '' || collect['radius-out'] != '') {
        collect.radius = [
          collect['radius-in'] == '' ? 0 : collect['radius-in'],
          collect['radius-out'] == '' ? '75%' : collect['radius-out']
        ];
      }
      //坐标
      if (collect['center-position-x'] != '' || collect['center-position-y'] != '') {
        collect['position'] = [collect['center-position-x'], collect['center-position-y']];
      }
      if (collect['roseType'] == '' || collect['roseType'] == 'false') {
        collect['roseType'] = false; //不展示南丁格尔图
      }
      var itemStyleColor = this.getColorData($('#itemStyle_color', $container));
      $.extend(collect, itemStyleColor);
      var itemStyleBorderColor = this.getColorData($('#itemStyle_borderColor', $container));
      $.extend(collect, itemStyleBorderColor);
    }

    if (row.type == 'line') {
      if (collect.smooth.length == 1) {
        collect.smooth = Boolean(collect.smooth[0]);
      }
      var areaStyleColor = this.getColorData($('#areaStyle_color', $container));
      if (!$.isEmptyObject(areaStyleColor)) {
        $.extend(collect, areaStyleColor);
      } else {
        //移除areaStyle的属性
        delete collect.areaStyle_color;
        delete collect.areaStyle_origin;
      }
    }

    if (row.type == 'bar') {
      var itemStyleColor = this.getColorData($('#itemStyle_color', $container));
      $.extend(collect, itemStyleColor);
      var itemStyleBorderColor = this.getColorData($('#itemStyle_borderColor', $container));
      $.extend(collect, itemStyleBorderColor);
    }

    if (collect.datasetIndex !== '') {
      collect.datasetIndex = Number(collect.datasetIndex);
    }

    row.seriesConfig = collect;

    var series = {
      type: row.type,
      name: row.name
    };

    for (var k in collect) {
      this.nestedJsonObject(series, k, collect[k], $container);
    }
    if ($.isEmptyObject(series.itemStyle)) {
      delete series.itemStyle;
    }
    if ($.isEmptyObject(series.tooltip)) {
      delete series.tooltip;
    }
    if ($.isEmptyObject(series.label)) {
      delete series.label;
    }
    row.series = series;
  };

  ChartBaseComponentConfigurer.prototype.initSeriesDetailDialog = function (row) {
    var _self = this;
    var $container = $('#series_detail_container');
    var type = row.type;
    if (row.seriesConfig) {
      row.seriesConfig.itemStyle_color = row.seriesConfig.itemStyle_colorHex;
      row.seriesConfig.itemStyle_borderColor = row.seriesConfig.itemStyle_borderColorHex;
      row.seriesConfig.areaStyle_color = row.seriesConfig.areaStyle_colorHex;
    }
    designCommons.setElementValue(row.seriesConfig || {}, $container);
    if (type == 'pie') {
      _self._elementShowCheckboxEvent($('#label_show').parents('fieldset'), 'label_show');

      $('#label_position', $container).wSelect2({
        valueField: 'label_position',
        remoteSearch: false,
        data: [
          { id: 'outside', text: '外侧' },
          { id: 'inside', text: '内部' },
          { id: 'center', text: '中心' }
        ]
      });

      $('#roseType', $container).wSelect2({
        valueField: 'roseType',
        remoteSearch: false,
        data: [
          { id: 'radius', text: '圆心角区分百分比/半径区分数据大小' },
          { id: 'area', text: '仅半径区分数据大小' }
        ]
      });
    }

    if (type == 'line') {
      $('#coordinateSystem', $container).wSelect2({
        valueField: 'coordinateSystem',
        remoteSearch: false,
        data: [
          { id: 'cartesian2d', text: '笛卡尔坐标系' },
          { id: 'polar', text: '极坐标系' }
        ]
      });
      $('#step', $container).wSelect2({
        valueField: 'step',
        remoteSearch: false,
        data: [
          { id: 'start', text: '起点拐弯' },
          { id: 'middle', text: '中间拐弯' },
          { id: 'end', text: '结点拐弯' }
        ]
      });
      _self._elementColorInitial(
        $container,
        'areaStyle_color',
        row.seriesConfig ? row.seriesConfig.areaStyle_color : null,
        true,
        'top left',
        row.seriesConfig ? row.seriesConfig.areaStyle_colorOpacity : null
      );

      $('#areaStyle_origin', $container).wSelect2({
        valueField: 'areaStyle_origin',
        remoteSearch: false,
        data: [
          { id: 'auto', text: '填充坐标轴轴线到数据间的区域' },
          { id: 'start', text: '填充坐标轴底部（非 inverse 情况是最小值）到数据间的区域' },
          { id: 'end', text: '填充坐标轴顶部（非 inverse 情况是最大值）到数据间的区域' }
        ]
      });
    }

    if (type == 'line' || type == 'bar') {
      //坐标轴选择
      var axisSelection = function (axis) {
        $('#' + axis + 'Index', $container).wSelect2({
          valueField: axis + 'Index',
          remoteSearch: false,
          data: (function () {
            var selection = [];
            var axisRows = $('#table_' + axis + '_info').bootstrapTable('getData');
            for (var a = 0, alen = axisRows.length; a < alen; a++) {
              selection.push({
                id: a,
                text: axisRows[a].name
              });
            }
            return selection;
          })()
        });
      };

      axisSelection('xAxis');
      axisSelection('yAxis');
    }

    $('#tooltip_formatterType', $container)
      .on('change', function () {
        $('#tooltip_formatter', $container).attr('placeholder', '');
        $('#formatterFunctionDescription', $container).hide();
        if ($(this).val() == 'function') {
          $('#formatterFunctionDescription', $container).show();
          $('#tooltip_formatter', $container).attr(
            'placeholder',
            '回调函数代码支持参数params，返回HTML字符串。例如：return params.name+"<br>"+params.seriesName; '
          );
        }
      })
      .trigger('change');

    //数据使用
    $('#datafromCheck', $container).wSelect2({
      valueField: 'datafromCheck',
      remoteSearch: false,
      data: [
        { id: 'dataset', text: '使用数据集' },
        { id: 'seriesdata', text: '使用系列数据' }
      ]
    });
    $('#datafromCheck', $container)
      .on('change', function () {
        var v = $(this).val();
        $('.datafrom', $container).hide();
        if (v) {
          $('.datafrom-' + v, $container).show();
        }
      })
      .trigger('change');
    $('#seriesDataType', $container).wSelect2({
      valueField: 'seriesDataType',
      remoteSearch: false,
      data: [
        { id: 'json', text: '自定义JSON数据内容' },
        { id: 'dataInterface', text: '数据内容接口' }
      ]
    });

    $('#seriesDataType', $container)
      .on('change', function () {
        var v = $(this).val();
        $('.seriesDataType', $container).hide();
        if (v) {
          $('.seriesDataType-' + v, $container).show();
        }
      })
      .trigger('change');

    $('#seriesDataInterface', $container).wSelect2({
      serviceName: 'reportFacadeService',
      queryMethod: 'loadEchartSeriesDataServiceData',
      valueField: 'seriesDataInterface',
      remoteSearch: false
    });

    //数据集选择下标
    var datasourceType = $('#dataSourceType', '#widget_chart_data_info').val();
    if (datasourceType !== 'interface') {
      $('#datasetIndex', $container).wSelect2({
        valueField: 'datasetIndex',
        remoteSearch: false,
        data: (function () {
          var selections = [];
          var datasetJson = $('#defineJsonData', '#widget_chart_data_info').val();
          if (datasetJson) {
            try {
              var json = JSON.parse(datasetJson);
              var len = 1;
              if ($.isArray(json)) {
                len = json.length;
              }
              for (var i = 0; i < len; i++) {
                selections.push({
                  id: i,
                  text: '第' + (i + 1) + '个数据集'
                });
              }
            } catch (e) {
              console.error('自定义JSON数据格式错误');
            }
          }
          return selections;
        })()
      });
    }

    _self._elementAlignSelectInitial($container, 'label_align');
    _self._elementVerticalAlignSelectInitial($container, 'label_verticalAlign');
    _self._elementFontStyleSelectInitial($container, 'label_fontStyle');
    _self._elementFontWeightSelectInitial($container, 'label_fontWeight');
    _self._elementColorInitial($container, 'label_color', row.seriesConfig ? row.seriesConfig.label_color : null, false, 'top left');

    $('#seriesLayoutBy', $container).wSelect2({
      valueField: 'seriesLayoutBy',
      remoteSearch: false,
      data: [
        { id: 'column', text: '数据集的列对应于系列' },
        { id: 'row', text: '数据集的行对应于系列' }
      ]
    });

    _self._elementColorInitial(
      $container,
      'itemStyle_color',
      row.seriesConfig ? row.seriesConfig.itemStyle_colorHex : null,
      true,
      'top left',
      row.seriesConfig ? row.seriesConfig.itemStyle_colorOpacity : null
    );
    _self._elementColorInitial(
      $container,
      'itemStyle_borderColor',
      row.seriesConfig ? row.seriesConfig.itemStyle_borderColorHex : null,
      true,
      'top left',
      row.seriesConfig ? row.seriesConfig.itemStyle_borderColorOpacity : null
    );

    //json编辑器化
    this.initJsoneditor('dataJsonEditor', 'defineJsonData', $container, row.seriesConfig ? row.seriesConfig.dataJson : [], {
      templates: [
        {
          text: '系列数据',
          title: '插入系列数据',
          className: 'jsoneditor-type-object',
          field: '',
          value: {
            name: '',
            value: 0,
            itemStyle: {
              color: '',
              borderColor: '#000',
              borderWidth: 0,
              borderType: 'solid'
            },
            label: {
              show: false,
              position: 'top',
              color: '#fff',
              fontStyle: 'normal',
              fontWeight: 'normal',
              fontFamily: 'sans-serif',
              fontSize: 12,
              borderWidth: 0,
              borderRadius: 0,
              padding: 0
            }
          }
        }
      ]
    });
  };

  ChartBaseComponentConfigurer.prototype.getColorData = function ($colorEle) {
    var elementId = $colorEle.attr('name');
    var color = {};
    if ($colorEle.val() !== '' && $colorEle.data('rgbaString')) {
      color[elementId + 'Hex'] = $colorEle.val();
      color[elementId] = $colorEle.data('rgbaString');
      color[elementId + 'Opacity'] = $colorEle.data('rgbObject').a; //透明度
    }
    return color;
  };

  ChartBaseComponentConfigurer.prototype.nestedJsonObject = function (object, jsonKey, jsonValue, $container) {
    if (jsonKey.indexOf('_') != -1) {
      //通过下划线解析嵌套的json对象
      var parts = jsonKey.split('_');
      var key = parts[0];
      if (object[key] == undefined) {
        object[key] = {};
      }
      var keyObj = object[key];

      if (parts.length == 2) {
        if (jsonValue === '') {
          return;
        }
        //数值属性
        if ($.inArray(parts[1], ['fontSize', 'lineHeight', 'itemGap', 'borderWidth', 'itemHeight', 'itemWidth']) != -1) {
          jsonValue = Number(jsonValue);
        }
        //颜色取值
        if (parts[1] === 'color') {
          jsonValue = this.getColorData($(":input[name='" + jsonKey + "']", $container))[jsonKey];
        }
        if (parts[1] === 'borderRadius') {
          //圆角
          jsonValue = jsonValue.split(',');
          if (jsonValue.length == 1) {
            jsonValue = Number(jsonValue);
          }
        }

        keyObj[parts[1]] = jsonValue;
      } else {
        //继续拆解
        var remains = jsonKey.substr(jsonKey.indexOf('_') + 1); //剩余部分进行解析嵌套的json对象
        this.nestedJsonObject(keyObj, remains, jsonValue);
      }
    } else if (jsonValue !== '' || jsonValue === false) {
      object[jsonKey] = jsonValue;
      if (jsonKey === 'show' && $.isArray(jsonValue)) {
        object[jsonKey] = Boolean(jsonValue[0]);
      }
    }
  };

  ChartBaseComponentConfigurer.prototype.collectBasicDefineInfo = function ($container) {
    var _self = this;
    $container = $('#widget_chart_base_info', $container);
    var collect = designCommons.collectConfigurerData($container, collectClass);
    $.extend(collect, this.getColorData($('#backgroundColor', $container)));

    var chartOptions = designCommons.collectConfigurerData($container, 'w-chart-option');

    var chartOption = {};
    for (var k in chartOptions) {
      this.nestedJsonObject(chartOption, k, collect[k], $container);
    }
    //调色板数据收集
    collect.colorObjects = [];
    chartOption.color = [];
    $('.add-color', $container).each(function (i, item) {
      var color = _self.getColorData($(item));
      if (!$.isEmptyObject(color)) {
        chartOption.color.push(color[$(item).attr('name')]);
        collect.colorObjects.push(color);
      }
    });
    if (collect.colorObjects.length == 0) {
      delete chartOption.color;
    }

    collect.chartOption = chartOption;

    return collect;
  };

  ChartBaseComponentConfigurer.prototype.collectLegendDefineInfo = function ($container) {
    $container = $('#widget_chart_legend_info', $container);
    var legendDefineInfo = designCommons.collectConfigurerData($container, collectClass);
    $.extend(legendDefineInfo, this.getColorData($('#backgroundColor', $container)));
    $.extend(legendDefineInfo, this.getColorData($('#borderColor', $container)));
    $.extend(legendDefineInfo, this.getColorData($('#textStyle_borderColor', $container)));
    $.extend(legendDefineInfo, this.getColorData($('#textStyle_backgroundColor', $container)));

    if (legendDefineInfo.padding != '') {
      var paddings = legendDefineInfo.padding.split(','); //设置的是左、上、右、下内边距
      var len = paddings.length;
      if (len == 1) {
        legendDefineInfo.padding = Number(legendDefineInfo.padding);
      } else {
        legendDefineInfo.padding = [];
        for (var i = 0; i < paddings.length; i++) {
          legendDefineInfo.padding.push(Number(paddings[i]));
        }
      }
    }
    legendDefineInfo.show = Boolean(legendDefineInfo.show[0]);
    if (legendDefineInfo.selectedMode === 'true') {
      //selectedMode：支持boolean值，也支持字符串值：single/multiple
      legendDefineInfo.selectedMode = true;
    } else if (legendDefineInfo.selectedMode === 'false') {
      legendDefineInfo.selectedMode = false;
    }
    if (legendDefineInfo.itemGap === '') {
      delete legendDefineInfo.itemGap;
    } else {
      legendDefineInfo.itemGap = Number(legendDefineInfo.itemGap);
    }
    if (legendDefineInfo.borderWidth === '') {
      delete legendDefineInfo.borderWidth;
    } else {
      legendDefineInfo.borderWidth = Number(legendDefineInfo.borderWidth);
    }
    if (legendDefineInfo.borderRadius != '') {
      var borderRadiuss = legendDefineInfo.borderRadius.split(','); //设置的是左、上、右、下圆角
      var len = borderRadiuss.length;
      if (len == 1) {
        legendDefineInfo.borderRadius = Number(legendDefineInfo.borderRadius);
      } else {
        legendDefineInfo.borderRadius = [];
        for (var i = 0; i < borderRadiuss.length; i++) {
          legendDefineInfo.borderRadius.push(Number(borderRadiuss[i]));
        }
      }
    }

    var legend = {}; //legend
    for (var k in legendDefineInfo) {
      this.nestedJsonObject(legend, k, legendDefineInfo[k], $container);
    }

    legendDefineInfo.legend = legend; //echart选项
    return legendDefineInfo;
  };

  ChartBaseComponentConfigurer.prototype.explainPaddingOptions = function (option) {
    if (option.padding != '') {
      var paddings = option.padding.split(','); //设置的是左、上、右、下内边距
      var len = paddings.length;
      if (len == 1) {
        option.padding = Number(option.padding);
      } else {
        option.padding = [];
        for (var i = 0; i < paddings.length; i++) {
          option.padding.push(Number(paddings[i]));
        }
      }
    }
  };

  ChartBaseComponentConfigurer.prototype.collectTitleDefineInfo = function ($container) {
    $container = $('#widget_chart_title_info', $container);
    //收集图表标题定义信息
    var titleDefineInfo = designCommons.collectConfigurerData($container, collectClass);
    titleDefineInfo.show = Boolean(titleDefineInfo.show[0]);
    $.extend(titleDefineInfo, this.getColorData($('#backgroundColor', $container)));
    $.extend(titleDefineInfo, this.getColorData($('#borderColor', $container)));
    this.explainPaddingOptions(titleDefineInfo);

    if (titleDefineInfo.itemGap === '') {
      delete titleDefineInfo.itemGap;
    } else {
      titleDefineInfo.itemGap = Number(titleDefineInfo.itemGap);
    }
    if (titleDefineInfo.borderWidth === '') {
      delete titleDefineInfo.borderWidth;
    } else {
      titleDefineInfo.borderWidth = Number(titleDefineInfo.borderWidth);
    }
    if (titleDefineInfo.borderRadius != '') {
      var borderRadiuss = titleDefineInfo.borderRadius.split(','); //设置的是左、上、右、下圆角
      var len = borderRadiuss.length;
      if (len == 1) {
        titleDefineInfo.borderRadius = Number(titleDefineInfo.borderRadius);
      } else {
        titleDefineInfo.borderRadius = [];
        for (var i = 0; i < borderRadiuss.length; i++) {
          titleDefineInfo.borderRadius.push(Number(borderRadiuss[i]));
        }
      }
    }
    var title = {}; //下沉参数到title
    for (var k in titleDefineInfo) {
      this.nestedJsonObject(title, k, titleDefineInfo[k], $container);
    }
    titleDefineInfo.title = title; //echart选项
    return titleDefineInfo;
  };

  ChartBaseComponentConfigurer.prototype.collectToolboxDefineInfo = function ($container) {
    $container = $('#widget_chart_toolbox_info', $container);
    //收集图表工具框定义信息
    var toolboxDefineInfo = designCommons.collectConfigurerData($container, collectClass);
    toolboxDefineInfo.show = Boolean(toolboxDefineInfo.show[0]);
    toolboxDefineInfo.feature_saveAsImage_show = Boolean(toolboxDefineInfo.feature_saveAsImage_show[0]);
    toolboxDefineInfo.feature_magicType_show = Boolean(toolboxDefineInfo.feature_magicType_show[0]);
    toolboxDefineInfo.feature_magicType_type = toolboxDefineInfo.feature_magicType_type.split(';');
    var toolbox = {}; //下沉参数到toolbox
    for (var k in toolboxDefineInfo) {
      this.nestedJsonObject(toolbox, k, toolboxDefineInfo[k], $container);
    }
    toolboxDefineInfo.toolbox = toolbox; //echart选项
    return toolboxDefineInfo;
  };

  ChartBaseComponentConfigurer.prototype.collectTooltipDefineInfo = function ($container) {
    $container = $('#widget_chart_tooltip_info', $container);
    //收集图表标题定义信息
    var tooltipDefineInfo = designCommons.collectConfigurerData($container, collectClass);
    if (tooltipDefineInfo.show.length == 1) {
      tooltipDefineInfo.show = Boolean(tooltipDefineInfo.show[0]);
    }
    if (tooltipDefineInfo.showContent.length == 1) {
      tooltipDefineInfo.showContent = Boolean(tooltipDefineInfo.showContent[0]);
    }
    if (tooltipDefineInfo.alwaysShowContent.length == 1) {
      tooltipDefineInfo.alwaysShowContent = Boolean(tooltipDefineInfo.alwaysShowContent[0]);
    }
    if (tooltipDefineInfo.enterable.length == 1) {
      tooltipDefineInfo.enterable = Boolean(tooltipDefineInfo.enterable[0]);
    }
    if (tooltipDefineInfo.confine.length == 1) {
      tooltipDefineInfo.confine = Boolean(tooltipDefineInfo.confine[0]);
    }
    $.extend(tooltipDefineInfo, this.getColorData($('#backgroundColor', $container)));
    $.extend(tooltipDefineInfo, this.getColorData($('#borderColor', $container)));
    $.extend(tooltipDefineInfo, this.getColorData($('#tooltip_textStyle_color', $container)));
    this.explainPaddingOptions(tooltipDefineInfo);

    var tooltip = {}; //tooltip
    for (var k in tooltipDefineInfo) {
      this.nestedJsonObject(tooltip, k, tooltipDefineInfo[k], $container);
    }
    tooltipDefineInfo.tooltip = tooltip; //echart选项
    return tooltipDefineInfo;
  };

  ChartBaseComponentConfigurer.prototype.collectSeriesDefineInfo = function ($container) {
    $container = $('#widget_chart_series_info', $container);
    var seriesDefineInfo = {
      series: []
    };
    var $table = $('#table_series_info', $container);
    var tableRow = $table.bootstrapTable('getData');
    if (tableRow.length == 0) {
      appModal.alert('系列列表定义无相关数据');
      throw new Error('系列列表定义无相关数据，图表将无法展示！');
    }
    for (var i = 0, len = tableRow.length; i < len; i++) {
      //tableRow[i].series.datasetIndex = i;//行标对应数据集下标
      seriesDefineInfo.series.push(tableRow[i].series);
    }

    seriesDefineInfo.seriesRow = tableRow;
    return seriesDefineInfo;
  };

  ChartBaseComponentConfigurer.prototype.initGridDefineInfo = function (configuration, $container) {
    var _self = this;
    $container = $('#widget_chart_grid_info', $container);
    var gridDefineInfo = configuration.gridDefineInfo || {};
    var $gridTable = $('#table_grid_info', $container);
    //坐标系表格初始化
    var gridRowBean = {
      name: null,
      grid: null
    };

    formBuilder.bootstrapTable.initTableTopButtonToolbar('table_grid_info', 'grid', $container, gridRowBean);

    $gridTable.bootstrapTable('destroy').bootstrapTable({
      data: gridDefineInfo.gridRow || [],
      idField: 'uuid',
      striped: true,
      showColumns: true,
      toolbar: $('#div_grid_info_toolbar', $container),
      onEditableHidden: function (field, row, $el, reason) {
        $el.closest('table').bootstrapTable('resetView');
      },
      width: 500,
      columns: [
        {
          field: 'checked',
          formatter: function (value, row, index) {
            if (value) {
              return true;
            }
            return false;
          },
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
            showbuttons: false,
            onblur: 'submit',
            mode: 'inline'
          }
        },
        {
          field: 'operation',
          title: '操作',
          width: 100,
          formatter: function (value, row, index) {
            return '<button type="button" data-index=\'' + index + '\' class="btn btn-primary btn-grid-define">定义</button>';
          }
        },
        {
          field: 'grid',
          title: 'grid',
          visible: false
        }
      ]
    });

    $('#grid_define_detail_template', $container).data('gridHtml', $('#grid_define_detail_template #template', $container).html());
    $('#grid_define_detail_template', $container).html('');

    //定义按钮
    $gridTable.on('click', '.btn-grid-define', function () {
      var row = $gridTable.bootstrapTable('getData')[parseInt($(this).attr('data-index'))];
      _self.showGridDetailDialog(row, $container);
    });
  };

  ChartBaseComponentConfigurer.prototype.showGridDetailDialog = function (row, $container) {
    var gridTemplateContainer = "<div id='grid_detail_container' style='max-height: 600px;overflow-y: auto;  overflow-x: hidden;'></div>";
    var _self = this;
    var $dialog = appModal.dialog({
      title: '网格定义',
      size: 'large',
      message: gridTemplateContainer,
      buttons: {
        confirm: {
          label: '确定',
          className: 'btn-primary',
          callback: function () {
            //收集数据
            _self.collectGridDetailDialog(row);
          }
        },
        cancel: {
          label: '关闭',
          className: 'btn-default',
          callback: function () {}
        }
      },
      shown: function () {
        var $detailContainer = $('#grid_detail_container');
        $detailContainer.html('');
        var html = $('#grid_define_detail_template', $container).data('gridHtml');
        $detailContainer.html('<form class="form-horizontal widget-template-container">' + html + '</form>');
        _self.initGridDetailDialog(row);
      }
    });
  };

  ChartBaseComponentConfigurer.prototype.collectGridDetailDialog = function (row) {
    var $container = $('#grid_detail_container');
    var collect = designCommons.collectConfigurerData($container, collectClass);
    var _self = this;
    if (collect.show.length == 1) {
      collect.show = Boolean(collect.show[0]);
    }
    if (collect.containLabel.length == 1) {
      collect.containLabel = Boolean(collect.containLabel[0]);
    }

    var backgroundColor = this.getColorData($('#backgroundColor', $container));
    $.extend(collect, backgroundColor);
    var borderColor = this.getColorData($('#borderColor', $container));
    $.extend(collect, borderColor);

    row.gridConfig = collect;

    var grid = {};
    for (var k in collect) {
      this.nestedJsonObject(grid, k, collect[k], $container);
    }
    row.grid = grid;
  };

  ChartBaseComponentConfigurer.prototype.initGridDetailDialog = function (row) {
    var $container = $('#grid_detail_container');
    var _self = this;
    if (row.gridConfig) {
      row.gridConfig.backgroundColor = row.gridConfig.backgroundColorHex;
      row.gridConfig.borderColor = row.gridConfig.borderColorHex;
    }
    designCommons.setElementValue(row.gridConfig || {}, $container);

    _self._elementColorInitial(
      $container,
      'backgroundColor',
      row.gridConfig ? row.gridConfig.backgroundColorHex : null,
      true,
      'top left',
      row.gridConfig ? row.gridConfig.backgroundColorOpacity : null
    );
    _self._elementColorInitial(
      $container,
      'borderColor',
      row.gridConfig ? row.gridConfig.borderColorHex : null,
      true,
      'top left',
      row.gridConfig ? row.gridConfig.borderColorOpacity : null
    );
  };

  ChartBaseComponentConfigurer.prototype.initAxisDefineInfo = function (configuration, $container, axis) {
    var _self = this;
    $container = $('#widget_chart_' + axis + '_info', $container);
    var axisDefineInfo = configuration[axis + 'DefineInfo'] || {};
    var $axisTable = $('#table_' + axis + '_info', $container);
    //坐标系表格初始化
    var axisRowBean = {
      name: null
    };

    formBuilder.bootstrapTable.initTableTopButtonToolbar('table_' + axis + '_info', axis, $container, axisRowBean);

    $axisTable.bootstrapTable('destroy').bootstrapTable({
      data: axisDefineInfo[axis + 'Row'] || [],
      idField: 'uuid',
      striped: true,
      showColumns: true,
      toolbar: $('#div_' + axis + '_info_toolbar', $container),
      onEditableHidden: function (field, row, $el, reason) {
        $el.closest('table').bootstrapTable('resetView');
      },
      width: 500,
      columns: [
        {
          field: 'checked',
          formatter: function (value, row, index) {
            if (value) {
              return true;
            }
            return false;
          },
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
            showbuttons: false,
            onblur: 'submit',
            mode: 'inline'
          }
        },
        {
          field: 'operation',
          title: '操作',
          width: 100,
          formatter: function (value, row, index) {
            return '<button type="button" data-index=\'' + index + '\' class="btn btn-primary btn-axis-define">定义</button>';
          }
        },
        {
          field: axis,
          title: axis,
          visible: false
        }
      ]
    });

    $('#' + axis + '_define_detail_template', $container).data(
      'axisHtml',
      $('#' + axis + '_define_detail_template #template', $container).html()
    );
    $('#' + axis + '_define_detail_template', $container).html('');

    //定义按钮
    $axisTable.on('click', '.btn-axis-define', function () {
      var row = $axisTable.bootstrapTable('getData')[parseInt($(this).attr('data-index'))];
      _self.showAxisDetailDialog(row, $container, axis);
    });
  };

  ChartBaseComponentConfigurer.prototype.showAxisDetailDialog = function (row, $container, axis) {
    var axisTemplateContainer = "<div id='axis_detail_container' style='max-height: 600px;overflow-y: auto;  overflow-x: hidden;'></div>";
    var _self = this;
    var $dialog = appModal.dialog({
      title: axis + '定义',
      size: 'large',
      message: axisTemplateContainer,
      buttons: {
        confirm: {
          label: '确定',
          className: 'btn-primary',
          callback: function () {
            //收集数据
            _self.collectAxisDetailDialog(row, axis);
          }
        },
        cancel: {
          label: '关闭',
          className: 'btn-default',
          callback: function () {}
        }
      },
      shown: function () {
        var $detailContainer = $('#axis_detail_container');
        $detailContainer.html('');
        var html = $('#' + axis + '_define_detail_template', $container).data('axisHtml');
        $detailContainer.html('<form class="form-horizontal widget-template-container">' + html + '</form>');
        _self.initAxisDetailDialog(row, axis);
      }
    });
  };

  ChartBaseComponentConfigurer.prototype.collectAxisDetailDialog = function (row, axis) {
    var $container = $('#axis_detail_container');
    var collect = designCommons.collectConfigurerData($container, collectClass);
    var _self = this;
    if (collect.show.length == 1) {
      collect.show = Boolean(collect.show[0]);
    } else {
      collect.show = false;
    }

    if (collect.axisTick_show.length == 1) {
      collect.axisTick_show = Boolean(collect.axisTick_show[0]);
    } else {
      collect.axisTick_show = false;
    }

    if (collect.axisLine_show.length == 1) {
      collect.axisLine_show = Boolean(collect.axisLine_show[0]);
    } else {
      collect.axisLine_show = false;
    }

    //收集类目数据
    if (collect.type === 'category') {
      var categoryDataRows = $('#table_category_data_info').bootstrapTable('getData');
      if (categoryDataRows.length != 0) {
        collect.dataRow = [];
        collect.data = [];
      }
      for (var i = 0, len = categoryDataRows.length; i < len; i++) {
        collect.data.push({
          value: categoryDataRows[i].value
        });
        collect.dataRow.push(categoryDataRows[i]);
      }
    }

    if (collect.interval != '') {
      collect.interval = Number(collect.interval);
    }
    collect.gridIndex = 0; //默认第一个grid
    if (collect.gridIndex != '') {
      collect.gridIndex = Number(collect.gridIndex);
    }

    row[axis + 'Config'] = collect;

    var axisData = {};
    for (var k in collect) {
      this.nestedJsonObject(axisData, k, collect[k], $container);
    }

    row[axis] = axisData;
  };

  ChartBaseComponentConfigurer.prototype.initAxisDetailDialog = function (row, axis) {
    var $container = $('#axis_detail_container');
    var _self = this;
    var rowData = row[axis + 'Config'] || {};

    designCommons.setElementValue(rowData, $container);

    _self._elementShowCheckboxEvent($container, 'show_x', function (v) {
      if (v) {
        $('#' + axis + 'Type', $container).trigger('change');
      }
    });
    _self._elementShowCheckboxEvent($container, 'show_y', function (v) {
      if (v) {
        $('#' + axis + 'Type', $container).trigger('change');
      }
    });

    $('#' + axis + 'Position', $container).wSelect2({
      valueField: axis + 'Position',
      remoteSearch: false,
      data: [
        { id: 'bottom', text: '网格底部' },
        { id: 'top', text: '网格顶部' }
      ]
    });

    $('#' + axis + 'Type', $container).wSelect2({
      valueField: axis + 'Type',
      remoteSearch: false,
      data: [
        { id: 'category', text: '类目轴' },
        { id: 'value', text: '数值轴' },
        { id: 'time', text: '时间轴' },
        { id: 'log', text: '对数轴' }
      ]
    });

    $('#' + axis + 'Type', $container)
      .on('change', function () {
        $('#category_data', $container).hide();
        if ($(this).val() == 'category') {
          $('#category_data', $container).show();
        }
      })
      .trigger('change');

    $('#nameLocation', $container).wSelect2({
      valueField: 'nameLocation',
      remoteSearch: false,
      data: [
        { id: 'start', text: '开始位置' },
        { id: 'center', text: '中间位置' },
        { id: 'end', text: '结束位置' }
      ]
    });

    _self._elementFontWeightSelectInitial($container, 'nameTextStyle_fontWeight');
    _self._elementFontStyleSelectInitial($container, 'nameTextStyle_fontStyle');
    _self._elementColorInitial(
      $container,
      'nameTextStyle_backgroundColor',
      rowData ? rowData.nameTextStyle_backgroundColorHex : null,
      true,
      'top left',
      rowData ? rowData.nameTextStyle_backgroundColorOpacity : null
    );
    _self._elementColorInitial(
      $container,
      'nameTextStyle_borderColor',
      rowData ? rowData.nameTextStyle_borderColorHex : null,
      true,
      'top left',
      rowData ? rowData.nameTextStyle_borderColorOpacity : null
    );
    _self._elementColorInitial($container, 'nameTextStyle_color', rowData ? rowData.nameTextStyle_colorHex : null, false, 'top left');

    //类目数据表格初始化
    var $categoryDataTable = $('#table_category_data_info', $container);
    //系列数据表格初始化
    var categoryDataRowBean = {
      value: null
    };
    formBuilder.bootstrapTable.initTableTopButtonToolbar('table_category_data_info', 'category_data', $container, categoryDataRowBean);
    $categoryDataTable.bootstrapTable('destroy').bootstrapTable({
      data: rowData.dataRow || [],
      idField: 'uuid',
      striped: true,
      showColumns: true,
      toolbar: $('#div_category_data_info_toolbar', $container),
      onEditableHidden: function (field, row, $el, reason) {
        $el.closest('table').bootstrapTable('resetView');
      },
      width: 500,
      columns: [
        {
          field: 'checked',
          formatter: function (value, row, index) {
            if (value) {
              return true;
            }
            return false;
          },
          checkbox: true
        },
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'value',
          title: '类目名称',
          editable: {
            type: 'text',
            showbuttons: false,
            onblur: 'submit',
            mode: 'inline'
          }
        }
      ]
    });

    //网格选择
    var gridData = $('#table_grid_info', '#widget_chart_grid_info').bootstrapTable('getData');
    $('#gridIndex', $container).wSelect2({
      valueField: 'gridIndex',
      remoteSearch: false,
      data: (function () {
        var selections = [];
        for (var s = 0, slen = gridData.length; s < slen; s++) {
          selections.push({
            id: s,
            text: gridData[s].name
          });
        }
        return selections;
      })()
    });
  };

  ChartBaseComponentConfigurer.prototype.collectGridDefineInfo = function ($container) {
    $container = $('#widget_chart_grid_info', $container);
    var gridDefineInfo = {
      grid: []
    };
    var $table = $('#table_grid_info', $container);
    var tableRow = $table.bootstrapTable('getData');
    if (tableRow.length == 0) {
      gridDefineInfo.grid = {};
      return gridDefineInfo;
    }
    for (var i = 0, len = tableRow.length; i < len; i++) {
      gridDefineInfo.grid.push(tableRow[i].grid);
    }
    gridDefineInfo.gridRow = tableRow;
    return gridDefineInfo;
  };

  ChartBaseComponentConfigurer.prototype.collectAxisDefineInfo = function ($container, axis) {
    $container = $('#widget_chart_' + axis + '_info', $container);
    var axisDefineInfo = {};
    axisDefineInfo[axis] = [];
    var $table = $('#table_' + axis + '_info', $container);
    var tableRow = $table.bootstrapTable('getData');
    if (tableRow.length == 0) {
      appModal.alert((axis == 'xAxis' ? 'x' : 'y') + '轴定义无相关数据');
      throw new Error((axis == 'xAxis' ? 'x' : 'y') + '网格定义无相关数据，图表将无法展示！');
    }
    for (var i = 0, len = tableRow.length; i < len; i++) {
      tableRow[i].name = tableRow[i][axis].name || tableRow[i].name; //坐标轴名称
      axisDefineInfo[axis].push(tableRow[i][axis]);
    }
    axisDefineInfo[axis + 'Row'] = tableRow;
    return axisDefineInfo;
  };

  // 基本图表组件对象
  $.ui.chartComponent.ChartBaseComponentConfigurer = function (prototype) {
    var configurer = function (component) {
      ChartBaseComponentConfigurer.call(this, component);
    };
    commons.inherit(configurer, ChartBaseComponentConfigurer, prototype);
    return configurer;
  };

  return $.ui.chartComponent;
});
