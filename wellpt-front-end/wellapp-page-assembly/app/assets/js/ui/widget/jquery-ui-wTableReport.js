/**
 * 表格报表组件：使用ureport2的报表配置进行渲染
 */
(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define([
      'jquery',
      'commons',
      'server',
      'constant',
      'dataStoreBase',
      'formBuilder',
      'moment',
      'appModal',
      'ViewDevelopmentBase',
      'select2',
      'wSelect2'
    ], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function (jquery, commons, server, constant, DataStore, formBuilder, moment, appModal, ViewDevelopmentBase) {
  'use strict';
  var $ = jquery;
  var UUID = commons.UUID;
  var StringUtils = commons.StringUtils;
  $.widget('ui.wTableReport', $.ui.wWidget, {
    options: {
      // 组件定义
      widgetDefinition: {},
      // 上级容器定义
      containerDefinition: {},
      // 当前查询的类型
      searchType: '',
      jsModules: {},
      searchParams: {}
    },

    /**
     * 获取配置对象
     */
    getConfiguration: function () {
      return this.options.widgetDefinition.configuration;
    },

    initialized: false,

    /**
     * 创建组件
     */
    _createView: function () {
      // 创建js数据源对象
      var _self = this;
      _self._beforeRenderView();
      _self._renderView();
      _self._afterRenderView();
      _self._setEvent();
      this.initialized = true;
    },

    _beforeRenderView: function () {
      var _self = this;
      _self.invokeDevelopmentMethod('beforeRender', [_self.options, _self.getConfiguration()]);
    },

    _renderView: function () {
      this._renderTableReportView();
    },

    /**
     * 表格式报表视图渲染
     * @private
     */
    _renderTableReportView: function () {
      var _self = this;
      var $element = $(_self.element);
      _self._renderParameterSearch();

      $element.find('.div_table_report_panel').remove();
      var $tableReportPanel = $("<div class='div_table_report_panel' style='padding-top:10px;width: 100%;height:100%;'></div>");
      $element.append($tableReportPanel);
      $tableReportPanel.empty();

      var $iframe = $('<iframe>');
      var iframeId = UUID.createUUID();
      var configuration = _self.getConfiguration();

      var lazyData = !!configuration.reportTableTypeConfig.lazyData;
      $iframe.attr('src', _self._generateTableReportUrl(iframeId, configuration.reportTableTypeConfig.tableAdjust, lazyData));
      $iframe.attr('style', 'width: 100%;border:0px;');
      $iframe.attr('id', iframeId);
      $tableReportPanel.append($iframe);
      $iframe.attr('scrolling', 'auto');
      _self.tableReport = {
        $tableReportPage: $iframe,
        tableReprotPageId: iframeId
      };
      window.addEventListener('message', function (e) {
        try {
          var _data = JSON.parse(e.data);
          if (_data.id === iframeId) {
            var wHeight = $(document).height();
            $iframe.height(wHeight - 70);
          }
          _self.invokeDevelopmentMethod('onLoadSuccess', [_self.options, _self.getConfiguration()]);
        } catch (error) {}
      });
    },

    _generateTableReportUrl: function (iframeId, tableAdjust, lazyData) {
      var _self = this;
      var configuration = this.getConfiguration();
      var baseUrl = backendUrl + '/ureport/preview?_u=';
      baseUrl += encodeURI(_self._getTableReportFileConfigName());
      var _auth = getCookie('_auth');
      baseUrl += '&' + _auth + '=' + getCookie(_auth);
      if (lazyData && !_self.initialized) {
        baseUrl += '&lazyData=1';
      }
      baseUrl += '&wiframe=' + iframeId;
      if (tableAdjust != undefined) {
        baseUrl + '&wtableAdjust=true';
      }
      if (configuration.reportTableTypeConfig.fixRows) {
        baseUrl += '&fixRows=' + configuration.reportTableTypeConfig.fixRows;
      }

      if (configuration.reportTableTypeConfig.embedJS) {
        $.ajax({
          url: '/jsexplainer/getJavaScriptPath?js=' + configuration.reportTableTypeConfig.embedJS,
          type: 'POST',
          async: false,
          contentType: 'json',
          dataType: 'json',
          success: function (result) {
            var jsPath = result.join('|');

            baseUrl += '&jsPath=' + jsPath;
            baseUrl += '&origin=' + location.origin;
          }
        });
      }

      _self._collectUrlParameters(); //收集表单提交参数
      if (this.options.searchParams) {
        for (var k in this.options.searchParams) {
          baseUrl += '&' + k + '=' + this.options.searchParams[k];
        }
      }
      return baseUrl + _self._tableReportOperationButtons();
    },

    _getTableReportFileConfigName: function () {
      var configuration = this.getConfiguration();
      return configuration.reportTableTypeConfig.reportFileStorePrefix + configuration.reportTableTypeConfig.reportFileConfigName;
    },

    /**
     * 表格式报表的操作按钮
     * @private
     */
    _tableReportOperationButtons: function () {
      var _self = this;
      var configuration = _self.getConfiguration();
      var reportOperation = configuration.reportTableTypeConfig.reportOperation;
      if (reportOperation) {
        return '&_t=' + reportOperation.replace(/;/g, ',') + '&_n=' + configuration.reportTableTypeConfig.exportFileName;
      }
      return '&_t=0'; //不展示操作按钮
    },

    _collectUrlParameters: function () {
      var _self = this;
      var configuration = _self.getConfiguration();
      var parameters = configuration.reportTableTypeConfig.parameters;
      var $element = $(_self.element);
      // var urlParams = '';
      if (parameters) {
        for (var i = 0, len = parameters.length; i < len; i++) {
          var p = parameters[i];
          var value = $('#' + p.name, $element).val();
          delete _self.options.searchParams[p.name];
          delete _self.options.searchParams[p.name + '_start'];
          delete _self.options.searchParams[p.name + '_end'];
          var queryType = p.queryOptions.queryType;
          // 单选框的值
          if (queryType == 'radio') {
            var $radio = $('input[name=' + p.name + ']:checked', $element);
            value = $radio.val();
          }

          // 复选框的值
          if (queryType == 'checkbox') {
            var $checkbox = $('input[name=' + p.name + ']:checked', $element);
            var checkboxValues = [];
            $.each($checkbox, function () {
              checkboxValues.push($(this).val());
            });
            value = checkboxValues.join(',');
            if (StringUtils.isBlank(value) && StringUtils.isNotBlank(p.defaultValue)) {
              value = p.defaultValue;
            }
          }
          //下拉树的值
          if (queryType == 'treeSelect') {
            var treeNodes = $('input[name=' + p.name + ']', $element).data('value');
            if (treeNodes && treeNodes.length > 0) {
              var treeVals = [];
              for (var idx = 0, count = treeNodes.length; idx < count; idx++) {
                treeVals.push(treeNodes[idx].data[p.queryOptions.valueColumn]);
              }
              value = treeVals.join(',');
            }
          }

          //select2的值
          if (queryType == 'select2') {
            var selects = $('input[name=' + p.name + ']', $element).select2('data');
            if (selects) {
              if (p.queryOptions.multiple == '1') {
                var selVals = [];
                for (var idx = 0, count = selects.length; idx < count; idx++) {
                  selVals.push(selects[idx].id);
                }
                value = selVals.join(',');
              } else {
                value = selects.id;
              }
            }
          }
          if (StringUtils.isNotBlank(value)) {
            if (queryType == 'date') {
              var ridx,
                format = p.queryOptions.format || '';
              if ((ridx = format.indexOf('-range|')) > 0) {
                var values = value.split('至');
                format = format.substr(ridx + 7);
                _self.options.searchParams[p.name + '_start'] = moment($.trim(values[0]), 'YYYY-MM-DD HH:mm:ss').format(
                  'YYYY-MM-DD HH:mm:ss'
                );
                _self.options.searchParams[p.name + '_end'] = moment($.trim(values[1]), 'YYYY-MM-DD HH:mm:ss').format(
                  'YYYY-MM-DD HH:mm:ss'
                );
              } else {
                value = moment(value, 'YYYY-MM-DD HH:mm:ss').format('YYYY-MM-DD HH:mm:ss');
              }
            }
            // urlParams += '&' + p.name + '=' + value;
            _self.options.searchParams[p.name] = value; //查询参数
          }
        }
        // return urlParams;
      }
    },

    /**
     * 渲染参数查询
     */
    _renderParameterSearch: function (clearAll) {
      var _self = this;
      var $element = $(_self.element);
      var configuration = _self.getConfiguration();
      var parameters = configuration.reportTableTypeConfig.parameters;
      if (parameters) {
        $element.find('.div_search_toolbar').remove();
        var $fieldSearchElement = $("<div class='div_search_toolbar' style='padding-top:10px;'></div>");
        $element.prepend($fieldSearchElement);
        var fieldOptios = {
          container: $fieldSearchElement,
          inputClass: 'w-search-option',
          labelColSpan: '3',
          controlColSpan: '9',
          multiLine: $element.width() > 400,
          contentItems: []
        };
        $.each(parameters, function (i, field) {
          var queryOption = field.queryOptions;
          var queryType = queryOption.queryType;
          var itemOptions = {
            label: field.label,
            name: field.name,
            type: queryType,
            controlOption: queryOption
          };
          if (!clearAll && field.defaultValue != undefined && field.defaultValue != '') {
            itemOptions.value = field.defaultValue;
            if (queryType == 'checkbox' || queryType == 'select2') {
              itemOptions.value = field.defaultValue.replace(/,/g, ';');
            }
          }
          if (queryType === 'date') {
            if (field.operator === 'between') {
              if (queryOption.format.indexOf('|') > -1) {
                var formatArr = queryOption.format.split('|');
                if (formatArr[0].indexOf('range') < 0) {
                  queryOption.format = formatArr[0] + '-range|' + formatArr[1];
                }
              } else {
                itemOptions.beginName = field.name + '_Begin';
                itemOptions.endName = field.name + '_End';
                itemOptions.beginValue = field.defaultValue;
                itemOptions.endValue = field.defaultValue;
                itemOptions.type = 'timeInterval';
              }
            }
            itemOptions.timePicker = {
              format: queryOption.format
            };
          } else if (queryType == 'select2') {
            itemOptions.select2 = formBuilder.getSelect2OptionValue(queryOption);
            // 是否多选
            itemOptions.select2.multiple = queryOption.multiple === '1';
            // 启用搜索
            itemOptions.select2.searchable = queryOption.searchable === '1';
            // 数据字典空值
            if (queryOption.optionType == '2') {
              if (StringUtils.isNotBlank(queryOption.defaultBlankId)) {
                var emptyId = UUID.createUUID();
                itemOptions.controlOption.defaultBlankId = emptyId;
                // 缓存空值
                _self.fieldSearchEmptyValueMap = _self.fieldSearchEmptyValueMap || {};
                _self.fieldSearchEmptyValueMap[emptyId] = emptyId;
              }
            }
          }
          if (queryType === 'text' && field.operator === 'between') {
            //新增文本区间
            itemOptions.beginName = field.name + '_Begin';
            itemOptions.endName = field.name + '_End';
            itemOptions.type = 'numberInterval';
          }

          fieldOptios.contentItems.push(itemOptions);
        });
        formBuilder.buildContent(fieldOptios);

        var toolbar = new commons.StringBuilder();
        toolbar.append("<div class='columns columns-right pull-right'>");
        toolbar.append("	<button class='well-btn w-btn-primary btn-query' type='button' name='query' title='查询'>");
        toolbar.append('		查询');
        toolbar.append('	</button>');
        toolbar.append("	<button class='well-btn w-btn-primary w-line-btn btn-reset' type='button' name='reset' title='重置'>");
        toolbar.append('		重置');
        toolbar.append('	</button>');
        toolbar.append("	<div class='hide-query-fields' style='display: inline-block;margin-right: 10px;'>");
        toolbar.append("		收起查询条件<i class='iconfont icon-ptkj-xianmiaoshuangjiantou-shang'></i>");
        toolbar.append('	</div>');
        toolbar.append('</div>');
        $fieldSearchElement.append(toolbar.toString());

        var unfolder = new commons.StringBuilder();
        unfolder.append("<div class='unfolder_serch_toolbal' style='padding: 0 10px;'>");
        unfolder.append("	<div class='show-query-fields' style='display: inline-block;'>");
        unfolder.append("		展开查询条件<i class='iconfont icon-ptkj-xianmiaoshuangjiantou-xia'></i>");
        unfolder.append('	</div>');
        unfolder.append('</div>');
        $fieldSearchElement.after(unfolder.toString());

        if (configuration.reportTableTypeConfig.reportFolder == '0') {
          $fieldSearchElement.hide();
          $fieldSearchElement.next().show();
        } else {
          $fieldSearchElement.next().hide();
        }
      }
    },

    _afterRenderView: function () {
      var _self = this;
      _self.invokeDevelopmentMethod('afterRender', [_self.options, _self.getConfiguration()]);
    },

    _setEvent: function () {
      var _self = this;
      var $element = $(_self.element);
      //查询按钮事件
      $('.btn-query', $element)
        .off('click')
        .on('click', function () {
          _self.invokeDevelopmentMethod('beforeSearch', [_self.options, _self.getConfiguration()]);
          _self.refresh();
        });

      //重置按钮事件
      $('.btn-reset', $element)
        .off('click')
        .on('click', function () {
          _self.resetQueryFields();
          $('.btn-query', $element).trigger('click');
        });

      //收起查询按钮事件
      $('.hide-query-fields', $element)
        .off('click')
        .on('click', function () {
          $('.div_search_toolbar', $element).hide();
          $('.div_search_toolbar', $element).next().show();
        });
      //展开查询按钮事件
      $('.show-query-fields', $element)
        .off('click')
        .on('click', function () {
          $('.div_search_toolbar', $element).show();
          $('.div_search_toolbar', $element).next().hide();
        });
    },

    refresh: function () {
      this.tableReport.$tableReportPage.attr('src', this._generateTableReportUrl());
      // this._adjustTableReportPage();
    },

    /**
     * 重置查询条件
     */
    resetQueryFields: function () {
      var _self = this;
      _self._renderParameterSearch(true);
      _self._setEvent();
      _self.invokeDevelopmentMethod('onResetQueryParameters');
    },

    // 清除表格式报表参数
    clearParams: function () {
      this.options.searchParams = {};
    },

    // 添加表格式报表参数
    addParam: function (key, value) {
      this.options.searchParams[key] = value;
    },
    // 移除表格式报表参数
    removeParam: function (key) {
      delete this.options.searchParams[key];
    },
    // 获取表格式报表参数
    getParam: function (key) {
      return this.options.searchParams[key];
    }
  });
});
