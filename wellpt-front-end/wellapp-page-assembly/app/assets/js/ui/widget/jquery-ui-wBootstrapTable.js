(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // prettier-ignore
    // AMD. Register as an anonymous module.
    define(['jquery', 'commons', 'server', 'constant', 'dataStoreBase', 'formBuilder', 'appModal', 'api-wBootstrapTable', 'select2', 'wSelect2', 'bootstrap-table-label-mark'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function (jquery, commons, server, constant, DataStore, formBuilder, appModal, Api) {
  'use strict';
  // moment.
  var $ = jquery;
  var UUID = commons.UUID;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var rowCheckItem = 'rowCheckItem';
  var lineEnderToolbar = 'lineEnderToolbar';

  /**
   * 插入排序
   * #48826: 使用火狐浏览器，表格组件自定义列设置后不生效
   * @param {any[]} array
   * @param {(a:any, b:any)=> number} compareFn
   */

  function insertionSort(arr, compareFn) {
    for (var i = 1; i < arr.length; i++) {
      for (var j = i; j > 0; j--) {
        if (compareFn(arr[j], arr[j - 1]) >= 0) {
          break;
        }

        var temp = arr[j];
        arr[j] = arr[j - 1];
        arr[j - 1] = temp;
      }
    }
  }

  if (!window.insertionSort) {
    window.insertionSort = insertionSort;
  }

  $.widget('ui.wBootstrapTable', $.ui.wWidget, {
    options: {
      // 组件定义
      widgetDefinition: {},
      // 上级容器定义
      containerDefinition: {},
      // 当前查询的类型
      searchType: '',
      jsModules: {},
      otherConditions: []
    },

    // region: 用于设置多行
    timer: null,
    _debounce: function (fn, delay) {
      var ctx = this;
      var args = arguments;

      if (this.timer) {
        clearTimeout(this.timer);
      }

      this.timer = setTimeout(function () {
        fn.apply(ctx, args);
      }, delay);
    },

    _calculateHtmlWidth: function (text) {
      $('body').append(
        $('<span>', {
          id: 'testFont',
          style: 'opacity:0;'
        }).text(text)
      );
      var width = $('#testFont').width();
      $('#testFont').remove();
      return width != 0 ? width + 38 : 0;
    },
    // end region

    /**
     * 获取数据源对象
     */
    getDataProvider: function () {
      return this._dataProvider;
    },
    /**
     * 获取配置对象
     */
    getConfiguration: function () {
      var configuration = this.options.widgetDefinition.configuration;

      // 固定列开启时默认开启自定义列
      // if (configuration.fixedColumns) {
      //   configuration.showColumns = true;
      // }
      return configuration;
    },
    closeCustomColumnPopup: function () {
      $('.bt-custom-columns-wrapper').removeClass('menu-open');
    },
    getCustomConfiguration: function (update) {
      var customConfiguration = null;
      var self = this;
      var tableConfiguration = this.getConfiguration();

      if (tableConfiguration.customConfiguration && update === true) {
        // 获取自定义列的字段
        if (
          tableConfiguration.hasExport &&
          (tableConfiguration.exportTypes == 'excel_column' || tableConfiguration.exportTypes == 'excel_ooxml_column')
        ) {
          var exportColumns = [];
          if (customConfiguration) {
            $.each(customConfiguration.order, function (index, item) {
              if (customConfiguration.forcedVisibility[item]) {
                var cIndex = _.findIndex(tableConfiguration.columns, function (o) {
                  return o.uuid == item;
                });
                if (cIndex > -1) {
                  var column = tableConfiguration.columns[cIndex];
                  exportColumns.push({
                    columnIndex: column.name,
                    title: column.header,
                    renderer: column.renderer
                  });
                }
              }
            });
          }

          // 检查自定义列之后是否有修改表格列定义的字段显示隐藏
          $.each(tableConfiguration.columns, function (cIndex, cItem) {
            if (cItem.hidden == '0' && (!customConfiguration || customConfiguration.order.indexOf(cItem.uuid) == -1)) {
              exportColumns.push({
                columnIndex: cItem.name,
                title: cItem.header,
                renderer: cItem.renderer
              });
            } else if (cItem.hidden == '1') {
              // 剔除先显示后再隐藏的字段
              var tIndex = _.findIndex(exportColumns, function (o) {
                return o.columnIndex == cItem.name;
              });
              if (tIndex > -1) {
                exportColumns.splice(tIndex, 1);
              }
            }
          });

          self.getDataProvider().options.exportColumns = exportColumns;
        }
      }

      return tableConfiguration.customConfiguration || {};
    },
    saveCustomConfiguration: function (widgetId, configuration) {
      var self = this;

      $.ajax({
        url: ctx + '/api/user/preferences/save',
        type: 'POST',
        data: {
          dataKey: widgetId,
          dataValue: JSON.stringify(configuration),
          moduleId: 'BOOTSTRAPTABLE',
          remark: '表格自定义列'
        },
        dataType: 'json',
        success: function (result) {
          self.getConfiguration().customConfiguration = configuration;
          self.getCustomConfiguration(true);
          appModal.success('自定义列已生效，并保存成功!');
        }
      });
      // $.ajax({
      //   url: '/api/user/widgetDef/save',
      //   type: 'POST',
      //   data: { widgetId: widgetId, configuration: configuration },
      //   dataType: 'json',
      //   success: function (result) {
      //     appModal.info('自定义列已生效，并保存成功!');
      //   }
      // });
    },

    /**
     * 获取数据源ID
     */
    getDataStoreId: function () {
      return this.getConfiguration().dataStoreId;
    },

    preventLoadData: false,
    initialized: false,
    /**
     * 通知数据源回调根据条件加载数据
     */
    load: function (request) {
      if (this.preventLoadData) {
        this.preventLoadData = false;
        this.element.find('.fixed-table-loading').addClass('display-none hide-no-records-picture');
        this.element.addClass('hide-no-records-picture');
        return;
      } else {
        this.element.removeClass('hide-no-records-picture');
      }
      // appModal.showMask("数据加载中...", $(this.element));
      var _self = this;
      _self.invokeDevelopmentMethod('beforeLoadData', [_self.options, _self.getConfiguration(), request]);
      _self.loadSuccess = request.success;
      var notifyChange = request.data.notifyChange;
      delete request.data.notifyChange;

      var otherConditions = _self.otherConditions || [];
      if (otherConditions.length) {
        $.each(otherConditions, function (i, cond) {
          request.data.criterions.push(cond);
        });
      }

      var otherOrders = _self.otherOrders || {};
      request.data.orders = request.data.orders || [];
      $.each(otherOrders, function (sortName, sortOrder) {
        var orders = request.data.orders;
        for (var i = 0; i < orders.length; i++) {
          if (orders[i]['sortName'] === sortName) {
            return; //orders[i]["sortOrder"] = sortOrder;
          }
        }
        request.data.orders.push({
          sortName: sortName,
          sortOrder: sortOrder
        });
      });

      _self.getDataProvider().load(request.data, {
        loadSuccess: request.success,
        notifyChange: notifyChange
      });
      _self.initialized = true;
    },

    loadFieldRenderData: function (renderer, data) {
      var _self = this;
      var datas = _self.getDataProvider().getData();

      var renderers = [];
      var index = 1;
      if (_self.getConfiguration().hideChecked) {
        //列数据在隐藏checkboc情况下，索引从0开始
        index = 0;
      }
      $.each(_self.getConfiguration().columns, function (i, column) {
        if (column.renderer && StringUtils.isNotBlank(column.renderer.rendererType) && column.renderer.loadType == '1') {
          renderers.push({
            index: index,
            columnIndex: column.name,
            param: column.renderer
          });
        }
        if (column['hidden'] == '0') {
          index = index + 1;
        }
      });
      var $table = $('#' + _self.getId() + '_table');
      for (var i = 0; i < renderers.length; i++) {
        for (var j = 0; j < datas.length; j++) {
          var data = datas[j];
          var renderer = renderers[i];
          server.JDS.call({
            async: true,
            service: 'cdDataStoreService.loadFieldRenderData',
            data: [
              renderer,
              data,
              {
                trIndex: j,
                tdIndex: renderer['index']
              }
            ],
            version: '',
            success: function (result) {
              if (result.msg == 'success') {
                var trIndex = null;
                var tdIndex = null;
                var field = null;
                var value = null;
                for (var k in result.data) {
                  if (k == 'indexMap') {
                    trIndex = result.data[k]['trIndex'];
                    tdIndex = result.data[k]['tdIndex'];
                  } else {
                    field = k;
                    value = result.data[k];
                  }
                }
                if (field && value) {
                  datas[trIndex][field] = value;
                }
                var $td = $table.find('tbody tr:eq(' + trIndex + ') td:eq(' + tdIndex + ')');
                $td.html(value);
                $td.attr('title', $td.text());
              }
            }
          });
        }
      }
    },

    /**
     * 通知数据变更
     */
    onDataChange: function (params) {
      var _self = this;
      var data = _self.getDataProvider().getData();
      var total = _self.getDataProvider().getCount();
      // _self.triggerOnDataChange = true;
      params.loadSuccess({
        rows: data,
        total: total
      });
      _self.trigger(constant.WIDGET_EVENT.Refresh, {
        viewData: {
          rows: data,
          total: total
        },
        rows: data,
        total: total
      });
      if (params.notifyChange) {
        _self.trigger(constant.WIDGET_EVENT.Change, {
          viewData: {
            rows: data,
            total: total
          },
          rows: data,
          total: total
        });
      }

      if (total > 0 && data.length == 0) {
        _self.selectPage(1);
      }
      _self.loadFieldRenderData();
    },
    /**
     * 创建组件
     */
    _createView: function () {
      // 创建js数据源对象
      var _self = this;
      var configuration = _self.getConfiguration();
      _self.otherOrders = _self.otherOrders || {};
      _self.otherConditions = _self.otherConditions || [];
      _self._dataProvider = new DataStore({
        dataStoreId: _self.getDataStoreId(),
        onDataChange: function (data, count, params) {
          _self.onDataChange(params);
        },
        receiver: _self,
        exportColumns: _self.getExportColumns(),
        renderers: _self.getRenderers(),
        defaultOrders: _self.getDefaultSort(),
        defaultCriterions: _self.getDefaultConditions(),
        pageSize: configuration.pageSize || 20
      });
      if (configuration.lazyData) {
        _self.preventLoadData = true;
      }

      _self._beforeRenderView();
      _self._renderView();
      _self._afterRenderView();
      _self._setEvent();
      _self.Api = new Api(this); //绑定Api，提供给其他组件代码获取到表格Api

      //监听刷新数据的请求
      _self.pageContainer.on(constant.WIDGET_EVENT.BootstrapTableRefresh, function () {
        _self.refresh();
      });
    },

    /**
     * 渲染视图前
     */
    _beforeRenderView: function () {
      var _self = this;
      _self.invokeDevelopmentMethod('beforeRender', [_self.options, _self.getConfiguration()]);
      var configuration = this.getConfiguration();

      if (configuration.defineEventJs && configuration.defineEventJs['beforeRenderCode']) {
        var defineFunc = appContext.eval(configuration.defineEventJs['beforeRenderCode'], $(this), {
          $this: _self,
          configuration: configuration,
          commons: commons,
          server: server,
          Api: new Api(_self)
        });
        console.log('在表格组件渲染之前触发自定义代码块', defineFunc);
      }
    },
    /**
     * 渲染字段查询
     */
    _renderFieldSearch: function () {
      var _self = this;
      var $element = $(_self.element);
      var configuration = _self.getConfiguration();
      if (configuration.query.fields) {
        $element.find('.div_search_toolbar').remove();
        var $fieldSearchElement = $("<div class='div_search_toolbar' style='padding-top:10px;'></div>");
        $element.prepend($fieldSearchElement);
        var fieldOptions = {
          container: $fieldSearchElement,
          inputClass: 'w-search-option',
          labelColSpan: '3',
          controlColSpan: '9',
          multiLine: configuration.query.fieldRowColumns,
          contentItems: []
        };
        $.each(configuration.query.fields, function (i, field) {
          var queryOption = field.queryOptions;
          var queryType = queryOption.queryType;
          var itemOptions = {
            label: field.label,
            name: field.name,
            type: queryType,
            value: field.defaultValue,
            controlOption: queryOption
          };
          if (queryType === 'date') {
            if (field.operator === 'between') {
              if (queryOption.format.indexOf('|') > -1) {
                var formatArr = queryOption.format.split('|');
                if (formatArr[0].indexOf('range') < 0) {
                  queryOption.format = formatArr[0] + '-range|' + formatArr[1];
                }
                if (formatArr[2]) {
                  queryOption.includeEnd = true;
                }
              } else {
                var type = 'date';
                if (
                  queryOption.format.indexOf('HH') > -1 ||
                  queryOption.format.indexOf('mm') > -1 ||
                  queryOption.format.indexOf('ss') > -1
                ) {
                  type = 'datetime';
                }
                queryOption.format = type + '-range|' + queryOption.format.replace(/Y/g, 'y').replace(/D/g, 'd');

                // itemOptions.beginName = field.name + "_Begin";
                // itemOptions.endName = field.name + "_End";
                // itemOptions.beginValue = field.defaultValue;
                // itemOptions.endValue = field.defaultValue;
                // itemOptions.type = 'timeInterval';
              }
            }
            itemOptions.timePicker = {
              format: queryOption.format
            };
          } else if (queryType == 'select2') {
            itemOptions.select2 = _self.getSelect2OptionValue(queryOption);
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
          fieldOptions.contentItems.push(itemOptions);
        });
        formBuilder.buildContent(fieldOptions);
        return $fieldSearchElement;
      }
      return '';
    },

    /**
     * 执行属性搜索
     */
    _executePropertySearch: function () {
      var _self = this;
      var $content = _self.bindings;
      var $element = $(_self.element);
      var $property_search_result = $content.find('.property_search_result');
      _self.searchType = 'fieldSearch';
      _self._onSearch();
    },

    /**
     * 渲染属性搜索
     */
    _renderPropertySearch: function () {
      var _self = this;
      var $content = _self.bindings;
      var $element = $(_self.element);
      var configuration = _self.getConfiguration();
      var propertyQueryData = $.map(configuration.query.propertyData, function (n) {
        return {
          init: n,
          optionValue: formBuilder._getFieldOptionValue(n.queryOptions)
        };
      });

      var property_search_toolbar = new StringBuilder();
      var property_search_result =
        '<div class="property_search_result clearfix"><div class="tit">全部 > </div><ul class="clearfix"></ul></div>';
      var property_choose_list = new StringBuilder();
      var property_more = '';
      var property_more_text = [];
      var show_property_li_num = 2;
      $.each(propertyQueryData, function (i, v) {
        var options = new StringBuilder();
        var showMulti = Boolean(v.init.queryOptions.multiple);

        if (i + 1 > show_property_li_num) {
          property_more_text.push(v.init.name);
        }

        $.each(v.optionValue, function (v_i, v_v) {
          options.appendFormat(
            '<li class="pull-left property-item" data-id="{0}" data-type="{1}">{2}<div class="close-icon"><i class="iconfont icon-ptkj-dacha"></i></div></li>',
            v_v.id,
            v.init.operator,
            v_v.text
          );
        });
        var btns = '';
        var multibtns = '';
        if (showMulti) {
          btns += '<div class="property-choose-btns"><div class="multi-btn">+ 多选</div></div>';
          multibtns +=
            '<div class="property-choose-multi-btns"><button type="button" class="multi-cancel btn btn-default">取消</button><button type="button" class="multi-submit btn btn-primary">确定</button></div>';
        }
        property_choose_list.appendFormat(
          '<li class="property-choose-li clearfix {0}" data-id="{1}"><div class="property-name" data-name="{2}">{2}</div><div class="property-item-wrap"><ul class="clearfix">' +
            options +
            '</ul>' +
            multibtns +
            '</div>' +
            btns +
            '</li>',
          i + 1 > show_property_li_num ? 'hidden' : '',
          v.init.name,
          v.init.label
        );
      });

      if (property_more_text.length) {
        var _show_text = [];
        $.each(property_more_text, function (i, v) {
          if (i < show_property_li_num) {
            _show_text.push(v);
          }
        });
        property_more =
          '<div class="property_li_more">' +
          '<span data-text="' +
          _show_text.join('、') +
          '" data-more="' +
          (property_more_text.length > 2 ? '等' : '') +
          '">' +
          '更多选项（' +
          _show_text.join('、') +
          (property_more_text.length > 2 ? '等' : '') +
          '） ' +
          '<i class="iconfont icon-ptkj-xianmiaoshuangjiantou-xia"></i>' +
          '</span></div>';
      }

      property_search_toolbar.appendFormat(
        '<div class="property_search_toolbar">' +
          property_search_result +
          '<ul class="property_choose_list">' +
          property_choose_list +
          '</ul>' +
          property_more +
          '</div>'
      );
      $element.prepend(property_search_toolbar.toString());

      var $property_search_result = $content.find('.property_search_result');
      var $property_choose_list = $content.find('.property_choose_list');
      var $property_choose_li = $content.find('.property-choose-li');

      //判断是否显示属性选择列表
      function propertyChooseLiShow() {
        var is_show = false;
        $property_choose_li.each(function () {
          if (!$(this).hasClass('hidden')) {
            is_show = true;
          }
        });
        if (is_show) {
          $property_choose_list.show();
        } else {
          $property_choose_list.hide();
        }
        renderPropertyMore();
        _self._executePropertySearch();
      }

      //渲染更多选项及属性列表
      function renderPropertyMore() {
        var _index = 0; //剩余属性列表显示数量
        var _show_text = []; //更多选项显示文字
        $property_choose_li.each(function () {
          var $this = $(this);
          if (_index < show_property_li_num) {
            if (!$this.hasClass('hidden')) {
              //不是隐藏的
              _index++;
            } else if (!$this.hasClass('has-choose')) {
              //不是选中的隐藏属性列表改为显示
              $this.removeClass('hidden');
              _index++;
            }
          } else if (!$this.hasClass('has-choose') && $this.hasClass('hidden')) {
            //隐藏未选的全部加入更多
            _show_text.push($this.data('id'));
          }
        });
        if (_show_text.length) {
          var _data_text = _show_text.splice(0, 2).join('、');
          var _data_more = _show_text.length > 2 ? '等' : '';
          $property_choose_list
            .siblings('.property_li_more')
            .removeClass('hidden')
            .html(
              '<span data-text="' +
                _data_text +
                '" data-more="' +
                _data_more +
                '">' +
                '更多选项（' +
                _data_text +
                _data_more +
                '） ' +
                '<i class="iconfont icon-ptkj-xianmiaoshuangjiantou-xia"></i></span>'
            );
        } else {
          $property_choose_list.siblings('.property_li_more').addClass('hidden');
        }
      }

      $element.on('click', '.property-item', function () {
        var $this = $(this);
        var $propertyChooseLi = $this.parents('.property-choose-li');

        var is_multi = $propertyChooseLi.hasClass('open-multi');
        if (is_multi) {
          $this.toggleClass('active');
        } else {
          var _type = 'eq'; //单选默认 等于
          var propertyId = $propertyChooseLi.data('id');
          var propertyName = $propertyChooseLi.find('.property-name').data('name');
          $property_search_result
            .find('ul')
            .append(
              '<li class="pull-left" data-id="' +
                propertyId +
                '" data-value="' +
                $this.data('id') +
                '" data-type="' +
                _type +
                '">' +
                '<span>' +
                propertyName +
                ' ：</span><span class="text-primary">' +
                $this.text() +
                '</span>' +
                '<div class="close-icon"><i class="iconfont icon-ptkj-dacha"></i></div></li>'
            );
          $propertyChooseLi.addClass('has-choose hidden');
          propertyChooseLiShow();
        }
      });

      $element.on('click', '.property_search_result ul li .close-icon', function (e) {
        e.stopPropagation();
        var $this = $(this);
        var _li = $this.parent();
        var propertyName = _li.data('id');
        var _show_num = 0;
        _li.remove();

        $property_choose_list.find('.property-choose-li').each(function (i) {
          var $this = $(this);
          if ($this.data('id') === propertyName) {
            $this.removeClass('has-choose');
          }

          if (_show_num < show_property_li_num) {
            if (!$this.hasClass('hidden')) {
              _show_num++;
            } else if (!$this.hasClass('has-choose')) {
              $this.removeClass('hidden');
              _show_num++;
            }
          } else {
            $this.addClass('hidden');
          }
        });
        propertyChooseLiShow();
      });
      $element.on('click', '.multi-btn', function () {
        var $this = $(this);
        $this.parents('.property-choose-li').addClass('open-multi');
      });

      $element.on('click', '.multi-cancel', function () {
        var $this = $(this);
        var $propertyChooseLi = $this.parents('.property-choose-li');
        $propertyChooseLi.removeClass('open-multi').find('.property-item').removeClass('active');
      });

      $element.on('click', '.multi-submit', function () {
        var $this = $(this);
        var $propertyChooseLi = $this.parents('.property-choose-li');
        var propertyId = $propertyChooseLi.data('id');
        var propertyName = $propertyChooseLi.find('.property-name').data('name');
        var $propertyActiveItem = $propertyChooseLi.find('.property-item.active');
        var _dataValue = '',
          _dataType = '',
          _dataText = '';

        if (!$propertyActiveItem.length) {
          return appModal.alert('请选择搜索内容!');
        }

        $propertyActiveItem.each(function () {
          $(this).removeClass('active');
          _dataType = 'in'; //多选的固定查询条件是in
          if (_dataValue === '') {
            _dataValue += $(this).data('id');
            _dataText += $(this).text();
          } else {
            _dataValue += ';' + $(this).data('id');
            _dataText += '、' + $(this).text();
          }
        });
        $property_search_result
          .find('ul')
          .append(
            '<li class="pull-left" data-id="' +
              propertyId +
              '" data-value="' +
              _dataValue +
              '" data-type="' +
              _dataType +
              '">' +
              '<span>' +
              propertyName +
              ' ：</span><span class="text-primary">' +
              _dataText +
              '</span>' +
              '<div class="close-icon"><i class="iconfont icon-ptkj-dacha"></i></div></li>'
          );
        $propertyChooseLi.removeClass('open-multi').addClass('has-choose hidden');
        propertyChooseLiShow();
      });

      $element.on('click', '.property_li_more span', function () {
        var $this = $(this);
        var $property_choose_li = $this.parent().prev().find('.property-choose-li');
        var _text = $this.text();
        var _more_text = $this.data('text').split('、');
        if (_text.substr(0, 4) === '更多选项') {
          $this.html('收起选项' + _text.slice(4) + '<i class="iconfont icon-ptkj-xianmiaoshuangjiantou-shang"></i>');
          $property_choose_li.not('.has-choose').removeClass('hidden');
        } else {
          $this.html('更多选项' + _text.slice(4) + '<i class="iconfont icon-ptkj-xianmiaoshuangjiantou-xia"></i>');
          $property_choose_li.each(function () {
            var _$this = $(this);
            if ($.inArray(_$this.data('id'), _more_text) > -1) {
              _$this.addClass('hidden');
            }
          });
        }
      });
    },

    /**
     * 渲染关键字搜索
     */
    _renderKeywordSearch: function () {
      var _self = this;
      var $content = _self.bindings;
      var $element = $(_self.element);
      var configuration = _self.getConfiguration();
      if (!configuration.query.keyword) return;

      $element.find('.keyword_search_toolbar').remove();
      var $fieldSearchElement = $("<div class='keyword_search_toolbar clearfix' style='padding-top:10px;'></div>");
      $element.prepend($fieldSearchElement);

      var searchBtn =
        '<div class="pull-left"><button class="well-btn w-btn-primary btn-query" type="button" name="query" title="查询">查询</button></div>';

      if (configuration.query.fieldSearch) {
        searchBtn += '<div class="more-search pull-left">更多 <i class="iconfont icon-ptkj-xianmiaoshuangjiantou-xia"></i></div>';
      }

      var input =
        '<div class="keyword-search-wrap pull-left">' +
        '<span class="search-icon"><i class="iconfont icon-ptkj-sousuochaxun"></i></span>' +
        '<input class="form-control" type="text" placeholder="关键字">' +
        '<div class="close-icon" style="display: none"><i class="iconfont icon-ptkj-dacha"></i></div>' +
        '</div>';

      if (configuration.query.multiKeyword) {
        var multiKeywordData = configuration.query.multiKeywordData;
        var dropdownItem = new StringBuilder();
        var dropdown = '';
        dropdownItem.appendFormat('<li data-id="">全部</li>');
        $.each(multiKeywordData, function (i, v) {
          dropdownItem.appendFormat('<li data-id="{0}">{1}</li>', i, v.label);
        });
        dropdown +=
          '<div class="dropdown pull-left">' +
          '  <button class="btn btn-default dropdown-toggle" type="button" id="multiKeywordDropdownMenu" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">' +
          '    <span class="dropdown-value">全部</span>' +
          '    <span class="fa fa-angle-down"></span>' +
          '  </button>' +
          '  <ul class="dropdown-menu" aria-labelledby="multiKeywordDropdownMenu">' +
          dropdownItem +
          '</ul>' +
          '</div>';

        $('.keyword_search_toolbar', $content)
          .addClass('multiKeyword_search')
          .append(dropdown + input + searchBtn);

        $element.on('click', '.dropdown-menu li', function () {
          var $this = $(this);
          var _id = $this.data('id');
          $this.parent().siblings('button').find('.dropdown-value').text($this.text()).data('id', _id);
        });
      } else {
        $('.keyword_search_toolbar', $content).append(input + searchBtn);
      }

      $element.on('input propertychange', '.keyword-search-wrap input', function () {
        var $this = $(this);
        if ($this.val()) {
          $this.siblings('.close-icon').show();
        } else {
          $this.siblings('.close-icon').hide();
        }
      });

      $element.on('click', '.close-icon', function (e) {
        e.stopPropagation();
        $(this).hide().siblings('input').val('');
        _self._onSearch();
      });

      $fieldSearchElement.find("button[name='query']").on('click', function () {
        _self._onSearch();
      });

      $fieldSearchElement.find('input').on('keypress', function (event) {
        if (event.keyCode === 13) {
          _self._onSearch();
        }
      });
      $element.on('click', '.more-search', function () {
        var $this = $(this);
        var $search_toolbar = $element.siblings('.div_search_toolbar');
        if ($search_toolbar) {
          _self._renderFieldSearch($element);
        } else {
          $search_toolbar.show();
        }
        $this.parent().hide();
        $element.find('.fixed-table-search-toolbar').show();
        $element.find('.search-tool-bar').show();
        _self.searchType = 'fieldSearch';
        _self.invokeDevelopmentMethod('changeToFieldSearch');
      });
    },

    /**
     * 拼接按钮工具栏
     */
    _renderButton: function (isCardView, shouldInvokeSecondMethod) {
      var _self = this;
      var configuration = this.getConfiguration();
      var hasCardGroup = configuration.cardGrid && configuration.cardGrid.usedIndividualCardGroup;
      var $headerToolbar = $("<div class='div_header_toolbar '></div>");
      var $footerToolbar = $("<div class='div_footer_toolbar'></div>");
      var buttonGroup = {};

      if (shouldInvokeSecondMethod) {
        _self.invokeDevelopmentMethod('beforeRenderButtons', [configuration.buttons || []]);
        if (configuration.defineEventJs && configuration.defineEventJs['onPreButtonCode']) {
          var defineFunc = appContext.eval(configuration.defineEventJs['onPreButtonCode'], $(this), {
            $this: _self,
            buttons: configuration.buttons || [],
            commons: commons,
            server: server,
            Api: new Api(_self)
          });
          console.log('在按钮渲染之前触发自定义代码块', defineFunc);
        }
      }

      if (configuration.buttons) {
        var $headerToolbarHtmls = [];
        var $footerToolbarHtmls = [];
        var $lineEnderButtonHtmls = [];
        var $floatButtonHtmls = [];
        var $lineUnderButtonHtmls = [];
        $.each(configuration.buttons, function (index, button) {
          var positions = button.position.btnPositionArr || button.position;
          var group = isCardView && hasCardGroup ? button.cardGroup : button.group;
          if (StringUtils.isBlank(group)) {
            var buttonHtml = new commons.StringBuilder();
            var btnClass = StringUtils.isBlank(button.cssClass) ? 'btn-default' : button.cssClass + ' ' + button.cssStr;
            if (button.icon && button.icon.className) {
              btnClass += ' ' + button.icon.className;
            }
            if (button.btnLib) {
              if ($.inArray(button.btnLib.btnInfo.type, ['primary', 'minor', 'line', 'noLine']) > -1) {
                if (button.btnLib.iconInfo) {
                  buttonHtml.appendFormat(
                    '<button type="button" class="well-btn {0} {1} {2} btn_class_{3}" id="{6}"><i class="{4}"></i>{5}</button>',
                    button.btnLib.btnColor,
                    button.btnLib.btnInfo['class'],
                    button.btnLib.btnSize,
                    button.code,
                    button.btnLib.iconInfo.fileIDs,
                    button.text,
                    button.uuid
                  );
                } else {
                  buttonHtml.appendFormat(
                    '<button type="button" class="well-btn {0} {1} {2} btn_class_{3}" id="{5}">{4}</button>',
                    button.btnLib.btnColor,
                    button.btnLib.btnInfo['class'],
                    button.btnLib.btnSize,
                    button.code,
                    button.text,
                    button.uuid
                  );
                }
              } else {
                if (button.btnLib.btnInfo.icon) {
                  buttonHtml.appendFormat(
                    '<button type="button" class="well-btn {0} {1} btn_class_{2}" id="{5}"><i class="{3}"></i>{4}</button>',
                    button.btnLib.btnInfo['class'],
                    button.btnLib.btnSize,
                    button.code,
                    button.btnLib.btnInfo.icon,
                    button.btnLib.btnInfo.text,
                    button.uuid
                  );
                } else {
                  buttonHtml.appendFormat(
                    '<button type="button" class="well-btn {0} {1} btn_class_{2}" id="{4}">{3}</button>',
                    button.btnLib.btnInfo['class'],
                    button.btnLib.btnSize,
                    button.code,
                    button.btnLib.btnInfo.text,
                    button.uuid
                  );
                }
              }
            } else {
              buttonHtml.appendFormat(
                "<button type='button' class='well-btn w-btn-primary w-line-btn  {0} btn_class_{1}' title='{2}' id='{4}'>{3}</button>",
                btnClass,
                button.code,
                button.text,
                button.text,
                button.uuid
              );
            }

            $.each(positions, function (i, item) {
              var _position = item.split('-')[0];
              var float_btn_column_value = item.split('-')[1];
              switch (_position) {
                case '1':
                  $headerToolbarHtmls.push(buttonHtml.toString());
                  break;
                case '2':
                  $footerToolbarHtmls.push(buttonHtml.toString());
                  break;
                case '3':
                  $lineEnderButtonHtmls.push(buttonHtml.toString());
                  break;
                case '4':
                  $floatButtonHtmls.push({
                    buttonHtml: buttonHtml.toString(),
                    column: float_btn_column_value
                  });
                  break;
                case '5':
                  $lineUnderButtonHtmls.push({
                    buttonHtml: buttonHtml.toString(),
                    column: float_btn_column_value
                  });
                  break;
              }
            });
          } else {
            if (!buttonGroup[group]) {
              buttonGroup[group] = {};
              if ($.inArray('1', positions) > -1) {
                $headerToolbarHtmls.push({
                  group: group
                }); //放一个占位
              } else if ($.inArray('3', positions) > -1) {
                $lineEnderButtonHtmls.push({
                  group: group
                }); //放一个占位
              }
            }
            $.each(positions, function (index, position) {
              if (!buttonGroup[group][position]) {
                buttonGroup[group][position] = [];
              }
              buttonGroup[group][position].push(button);
            });
          }
        });
      }
      var replaceToolbarBtnGroupHtml = function (group, htmlArray, buttonHtml) {
        var i = _.findIndex(htmlArray, {
          group: group
        });
        if (i !== -1) {
          //替换当前的按钮组对象为HTML
          htmlArray[i] = buttonHtml.toString();
        } else {
          htmlArray.push(buttonHtml.toString());
        }
      };
      $.each(buttonGroup, function (group, positions) {
        $.each(positions, function (position, buttons) {
          var buttonHtml = new commons.StringBuilder();
          buttonHtml.append("<div class='btn-group' style='overflow: visible'>");
          var btnClass = StringUtils.isBlank(buttons[0].cssClass) ? 'btn-default' : buttons[0].cssClass;
          if (buttons[0].isHide) {
            btnClass += ' hide';
          }
          if (buttons[0].icon && buttons[0].icon.className) {
            btnClass += ' ' + buttons[0].icon.className;
          }
          buttonHtml.appendFormat("<button type='button' class='well-btn w-btn-primary w-line-btn dropdown-toggle'");
          buttonHtml.append("	data-toggle='dropdown'>");
          buttonHtml.append('<span>' + group + '</span>');
          buttonHtml.append("		<i class='iconfont icon-ptkj-xianmiaojiantou-xia'></i>");
          buttonHtml.append('	</button>');
          buttonHtml.append("	<ul class='dropdown-menu w-btn-dropMenu' role='menu'>");
          $.each(buttons, function (index, button) {
            var liClass = 'li_class_' + button.code;
            buttonHtml.append('	<li class=' + liClass + ' id="' + button.uuid + '"><a>' + button.text + '</a></li>');
          });
          buttonHtml.append('	</ul>');
          buttonHtml.append('</div>');
          if (position == '1') {
            replaceToolbarBtnGroupHtml(group, $headerToolbarHtmls, buttonHtml);
          } else if (position == '2') {
            replaceToolbarBtnGroupHtml(group, $footerToolbarHtmls, buttonHtml);
          } else if (position == '3') {
            replaceToolbarBtnGroupHtml(group, $lineEnderButtonHtmls, buttonHtml);
          } else if (position == '4') {
            replaceToolbarBtnGroupHtml(group, $floatButtonHtmls, buttonHtml);
          } else if (position == '5') {
            replaceToolbarBtnGroupHtml(group, $lineUnderButtonHtmls, buttonHtml);
          }
        });
      });

      function fixedDropDown($this) {
        var top = $this[0].getBoundingClientRect().top;
        var left = $this[0].getBoundingClientRect().left;
        var outHeight = $this.outerHeight();
        if (!$this.hasClass('open')) {
          var $list = $this.find('.dropdown-menu');
          $list.css({
            position: 'fixed',
            top: top + outHeight + 'px',
            left: left + 'px'
          });

          var listHeight = $list.outerHeight();
          var listBottom = top + outHeight + listHeight;
          var $table = $this.closest('table');
          var tableBottom = $table[0].getBoundingClientRect().bottom;

          if (listBottom > tableBottom) {
            $list.css({
              position: 'fixed',
              top: top - listHeight + 'px',
              left: left + 'px'
            });
          }
        }
      }

      _self.element.off('click', '.div_lineEnd_toolbar .btn-group').on('click', '.div_lineEnd_toolbar .btn-group', function (e) {
        if (_self.getConfiguration().clickToSelect) {
          var $tr = $(this).parents('tr').first();
          $tr.find("td:eq('0')").trigger('click');
        }
        fixedDropDown($(this));
      });

      _self.element.off('click', '.table-handle .btn-group').on('click', '.table-handle .btn-group', function (e) {
        fixedDropDown($(this));
      });

      $(document)
        .off('scroll')
        .on('scroll', function () {
          _self.element.find('.btn-group.open').trigger('click');
        });

      var buttonElement = {};
      if ($headerToolbarHtmls.length > 0) {
        $headerToolbar.append($headerToolbarHtmls);
        buttonElement.headerToolbar = $headerToolbar;
      } else {
        // setTimeout(function () {
        //     _self.element.find('.fixed-table-toolbar .btn-group').hide();
        // },0)
      }
      if ($footerToolbarHtmls.length > 0) {
        $footerToolbar.append($footerToolbarHtmls);
        buttonElement.footerToolbar = $footerToolbar;
      }
      if ($lineEnderButtonHtmls.length > 0) {
        buttonElement.lineEnderButtonHtml = $lineEnderButtonHtmls.join('');
      }
      if ($floatButtonHtmls.length > 0) {
        buttonElement.floatButtonHtml = $floatButtonHtmls;
      }
      if ($lineUnderButtonHtmls.length > 0) {
        buttonElement.lineUnderButtonHtmls = $lineUnderButtonHtmls;
      }

      return buttonElement;
    },

    _getElementWidth: function ($el) {
      $(document.body).append(
        $('<div></div>', {
          id: 'testElementWidth',
          style: 'display:inline-block; padding-bottom:1px; opacity:0;'
        }).append($el)
      );
      var width = $('#testElementWidth').width();
      $('#testElementWidth').remove();
      return width;
    },

    _getUuidByTitle: function (title, uuidCache, defaultConfig) {
      if (!title) {
        return null;
      }

      if (uuidCache[title]) {
        return uuidCache[title];
      }

      // IE11 Array.prototype.find polyfill
      if (!Array.prototype.find) {
        Array.prototype.find = function (callback) {
          return callback && (this.filter(callback) || [])[0];
        };
      }

      var column = defaultConfig.columns.find(function (col) {
        return col.header === title;
      });
      uuidCache[title] = column && column.uuid;
      return uuidCache[title];
    },

    _setFixedColDirections: function () {
      var configuration = this.getConfiguration();
      var customConfig = this.getCustomConfiguration(true);
      var cnt = 0;
      if (customConfig && customConfig.fixedNumber != '0') {
        cnt++;
      }
      if (customConfig && customConfig.fixedRightNumber != '0') {
        cnt++;
      }
      this.options.fixedColDirections = cnt;
      this.options.successfixedColDirections = 0;
    },

    _resetTableOptions: function (defaultConfig, customConfig) {
      var _self = this;
      var originalOption = _self.$tableElement.bootstrapTable('getOptions');
      var refreshOption = $.extend({}, originalOption);
      refreshOption.customConfig = customConfig;
      refreshOption.widgetConfiguration = defaultConfig;
      // 固定列 -- 不都为0时，固定
      if (customConfig && customConfig.fixedNumber && customConfig.fixedRightNumber) {
        refreshOption.fixedColumns = true;
        $('.fixed-table-toolbar .js-fixed-columns-num #fixedNumber', _self.$element).val(customConfig.fixedNumber);
        $('.fixed-table-toolbar .js-fixed-columns-num #fixedRightNumber', _self.$element).val(customConfig.fixedRightNumber);
      }
      // 都为0时 不固定了
      if (customConfig.fixedNumber == '0' && customConfig.fixedRightNumber == '0') {
        refreshOption.fixedColumns = false;
      }

      refreshOption.fixedNumber = +customConfig.fixedNumber;
      refreshOption.fixedRightNumber = +customConfig.fixedRightNumber;
      _self._setFixedColDirections();
      var btColumns = refreshOption.columns[0];

      // 自定义列显示
      if (defaultConfig.showColumns && customConfig && customConfig.forcedVisibility) {
        for (var columnUUid in customConfig.forcedVisibility) {
          var column = defaultConfig.columns.find(function (col) {
            return col.uuid === columnUUid;
          });
          var btColumn = btColumns.find(function (col) {
            return col.title === column.header;
          });

          btColumn.visible = _self._isColumnVisible(column, customConfig);
        }
      }

      // 自定义列排序
      if (defaultConfig.showColumns && customConfig && customConfig.order) {
        var uuidCache = {};

        insertionSort(btColumns, function (col1, col2) {
          if (!customConfig || !customConfig.order) {
            return 0;
          }

          // 操作列在最后
          if (col1.field === 'lineEnderToolbar') {
            return 1;
          }

          if (col2.field === 'lineEnderToolbar') {
            return -1;
          }

          // 序号列和选择列在最前面
          if (col1.field === 'rowCheckItem' && col2.field === 'sequenceIndex') {
            return -1;
          }

          if (col2.field === 'rowCheckItem' && col1.field === 'sequenceIndex') {
            return 1;
          }

          if (col1.field === 'rowCheckItem' || col1.field === 'sequenceIndex') {
            return -1;
          }

          if (col2.field === 'rowCheckItem' || col2.field === 'sequenceIndex') {
            return 1;
          }

          var idx1 = $.inArray(_self._getUuidByTitle(col1.title, uuidCache, defaultConfig), customConfig.order);
          var idx2 = $.inArray(_self._getUuidByTitle(col2.title, uuidCache, defaultConfig), customConfig.order);

          if (idx1 === -1 && idx2 === -1) {
            return 0;
          } else if (idx1 === -1) {
            return 1;
          } else if (idx2 === -1) {
            return -1;
          } else {
            return idx1 - idx2;
          }
        });
      }

      refreshOption.columns = [btColumns];
      var orderColumns = {};
      btColumns.map(function (col, idx) {
        if (col.visible) {
          orderColumns[col.field] = idx;
        }
      });
      var $toolbar = $('.fixed-table-toolbar', _self.element).detach(); // 删除文本和节点，但是事件绑定未删除
      var $searchToolbar = $('.fixed-table-search-toolbar', _self.element).detach();
      $toolbar.find('.div_header_toolbar').closest('.bs-bars').addClass('bs-bars-wrapper');
      if (_self.isFixedColumns()) {
        _self.$tableElement.css('opacity', 0);
      }
      _self.$tableElement.bootstrapTable('destroy').bootstrapTable(refreshOption);
      var $buttons = _self.element.find('.fixed-table-toolbar .div_header_toolbar').detach();
      $toolbar.find('.bs-bars-wrapper').empty().append($buttons);

      _self.element.find('.fixed-table-toolbar').empty().append($toolbar).before($searchToolbar);

      // 重载表格
      // _self.$tableElement.bootstrapTable('fixedColumns');
    },
    _isColumnVisible: function (column, customConfig) {
      if (column.hidden === '1') {
        return false;
      }

      if (column.alwaysDisplay === '1') {
        return true;
      }

      if (customConfig && customConfig.forcedVisibility && column.uuid in customConfig.forcedVisibility) {
        return customConfig.forcedVisibility[column.uuid] === 'true' || customConfig.forcedVisibility[column.uuid] === true;
      }

      if (column.defaultDisplay === '0') {
        return false;
      }

      return true;
    },

    /**
     * 渲染BootstrapTable控件和相关查询条件，按钮等
     */
    _renderTable: function ($element, buttonElement, isCardView) {
      var _self = this;
      var options = this.options;
      var configuration = this.getConfiguration();
      // 获得自定义列配置
      var customConfig = _self.getCustomConfiguration(true);

      var tableId = options.widgetDefinition.id + '_table';
      var $tableElement = $("<table id='" + tableId + "' ></table>");
      $element.append($tableElement);
      var bootstrapTableOptions = {};

      if ($.type(configuration.clickToSelect) !== 'boolean') {
        bootstrapTableOptions.clickToSelect = true; // 默认
      } else {
        bootstrapTableOptions.clickToSelect = configuration.clickToSelect;
      }
      bootstrapTableOptions.singleSelect = !configuration.multiSelect;
      bootstrapTableOptions.showHeader = !configuration.hideColumnHeader;
      if (StringUtils.isNotBlank(configuration.pageList) && configuration.pagination) {
        bootstrapTableOptions.pageList = configuration.pageList.split(constant.Separator.Comma);
      }
      bootstrapTableOptions.columns = [];
      if (!configuration.hideChecked) {
        if (bootstrapTableOptions.singleSelect) {
          bootstrapTableOptions.columns.push({
            field: rowCheckItem,
            radio: true
          });
        } else {
          bootstrapTableOptions.columns.push({
            field: rowCheckItem,
            checkbox: true
          });
        }
      }

      if (configuration.hideTdBorder) {
        $element.addClass('hideTdBorder');
      }

      var rendererFormat = function (value, row, index) {
        return row[this.field + 'RenderValue'];
      };
      var idField = '';

      $.each(configuration.columns, function (index, column) {
        var visible = column.hidden != '1';
        var sortable = column.sortable == '1';
        if (column.idField == '1') {
          idField = column.name;
        }

        if (configuration.showColumns && customConfig && customConfig.order) {
          visible = _self._isColumnVisible(column, customConfig);
        }

        var columnOptions = {
          field: column.name,
          title: column.header,
          width: column.width,
          className: column.className,
          sortable: sortable,
          visible: visible
        };
        if (column.renderer && StringUtils.isNotBlank(column.renderer.rendererType)) {
          columnOptions.formatter = rendererFormat;
        }
        bootstrapTableOptions.columns.push(columnOptions);
      });

      // 序号列, 提取为变量,后面更新frozen属性（有固定列,则序号也固定）
      if (configuration.hideColumnIndex === false) {
        var sequenceColumn = {
          field: 'sequenceIndex',
          index: 'sequenceIndex',
          title: '序号',
          width: '45px',
          sortable: false,
          fixed: true,
          align: 'center',
          valign: 'middle',
          class: 'bs-seqno',
          editable: false,
          visible: true,
          formatter: function (value, row, index) {
            if (configuration.paginationStackColumnIndex === false || configuration.pageStyle === 'waterfall') {
              return index + 1;
            } else {
              var options = $tableElement.bootstrapTable('getOptions');
              var pageSize = options.pageSize;
              var pageNumber = options.pageNumber - 1;
              return pageNumber * pageSize + index + 1;
            }
          }
        };
        var insertIndex = bootstrapTableOptions.columns[0].field === 'rowCheckItem' ? 1 : 0;
        bootstrapTableOptions.columns.splice(insertIndex, 0, sequenceColumn);
      }

      // 按钮相关
      if (buttonElement.headerToolbar) {
        bootstrapTableOptions.toolbar = buttonElement.headerToolbar;
      }
      if (buttonElement.lineEnderButtonHtml) {
        //获取行按钮的整体宽度
        var toolbarWidth = _self._getElementWidth($(buttonElement.lineEnderButtonHtml.toString())) + 50;
        bootstrapTableOptions.columns.push({
          field: lineEnderToolbar,
          width: toolbarWidth,
          class: 'table-handle',
          title: '操作',
          sortable: false,
          formatter: function (value, row, index) {
            var buttonToolbar = new commons.StringBuilder();
            buttonToolbar.appendFormat(
              "<div class='div_lineEnd_toolbar' style='display:inline-block; padding-bottom:1px; overflow: visible' index='{0}'>{1}</div>",
              index,
              buttonElement.lineEnderButtonHtml
            );
            //行按钮格式化
            var format = {
              before: buttonToolbar.toString(),
              after: null
            };
            var btns = _self.getConfiguration().buttons;
            $.each(btns, function (i, item) {
              var $html = $(format.before);
              if (item.position.indexOf('3') > -1 && item.btnConstraint && item.btnConstraint.template) {
                var condition = item.btnConstraint.template;
                if (!eval(condition)) {
                  $html.find('.btn_class_' + item.code).remove();
                  format.before = $html[0].outerHTML;
                }
              }
            });

            _self.invokeDevelopmentMethod('lineEnderButtonHtmlFormat', [format, row, index]);
            if (format.after) {
              return format.after;
            }
            return format.before.toString();
          }
        });
      }

      var treeGrid = configuration.treeGrid || {};
      var cardGrid = configuration.cardGrid || {};
      var currentSelectedPage = 1;
      _self.invokeDevelopmentMethod('getTableOptions', [bootstrapTableOptions]);

      // 自定义列排序
      var uuidCache = {};

      if (configuration.showColumns && customConfig && customConfig.order) {
        insertionSort(bootstrapTableOptions.columns, function (col1, col2) {
          if (!customConfig || !customConfig.order) {
            return 0;
          }

          // 操作列在最后
          if (col1.field === 'lineEnderToolbar') {
            return 1;
          }

          if (col2.field === 'lineEnderToolbar') {
            return -1;
          }

          // 序号列和选择列在最前面
          if (col1.field === 'rowCheckItem' && col2.field === 'sequenceIndex') {
            return -1;
          }

          if (col2.field === 'rowCheckItem' && col1.field === 'sequenceIndex') {
            return 1;
          }

          if (col1.field === 'rowCheckItem' || col1.field === 'sequenceIndex') {
            return -1;
          }

          if (col2.field === 'rowCheckItem' || col2.field === 'sequenceIndex') {
            return 1;
          }

          var idx1 = $.inArray(_self._getUuidByTitle(col1.title, uuidCache, configuration), customConfig.order);
          var idx2 = $.inArray(_self._getUuidByTitle(col2.title, uuidCache, configuration), customConfig.order);

          if (idx1 === -1 && idx2 === -1) {
            return 0;
          } else if (idx1 === -1) {
            return 1;
          } else if (idx2 === -1) {
            return -1;
          } else {
            return idx1 - idx2;
          }
        });
      }

      var cardView = false;
      // var ifFixedColumns = false;
      // debugger;
      // if (configuration.fixedColumns == true) {
      //   ifFixedColumns = true;
      // } else {
      //   if (customConfig.fixedNumber || customConfig.fixedRightNumber) {
      //     if (customConfig.fixedNumber == '0' && customConfig.fixedRightNumber == '0') {
      //       ifFixedColumns = false;
      //     } else {
      //       ifFixedColumns = true;
      //     }
      //   }
      // }
      // 初始化表格

      if (this.isFixedColumns()) {
        this._setFixedColDirections();
        $tableElement.on('fixedTableColumnsSuccess', function (e, data) {
          if (data == 'fixedOnLoadSuccess') {
            //表数据加载完成
            $tableElement.data('fixedOnLoadSuccess', 1);
          } else {
            _self.options.successfixedColDirections++;
          }
          if ($tableElement.data('fixedOnLoadSuccess') == 1 && _self.options.successfixedColDirections >= 0) {
            $('#' + $tableElement.attr('id') + '_fixed_left').css('opacity', 1);
            $('#' + $tableElement.attr('id') + '_fixed_right').css('opacity', 1);
            $tableElement.css('opacity', 1);
            $tableElement.data('fixedOnLoadSuccess', 0);
            _self.options.successfixedColDirections = 0;
            $tableElement.bootstrapTable('resetView');
          }
        });
      }

      var ifFixedColumn = this.isFixedColumns();

      $tableElement.bootstrapTable('destroy').bootstrapTable(
        $.extend(
          {
            widgetConfiguration: configuration,
            customConfig: customConfig,
            sidePagination: 'server',
            parentIdField: treeGrid.parentIdField,
            treeView: treeGrid.treeView,
            treeNodeFields: treeGrid.treeNodes,
            allPageDataTreeFormate: treeGrid.allPageDataTreeFormate,
            treeNodeDisplayField: treeGrid.treeNodeDisplayField,
            treeBuildType: treeGrid.treeBuildType,
            collapseIcon: treeGrid.collapseIcon,
            expandIcon: treeGrid.expandIcon,
            initTreeShowStyle: treeGrid.initTreeShowStyle,
            treeCascadeCheck: treeGrid.treeCascadeCheck,
            cardView: false,
            // cardView: isCardView || (cardGrid.enableCardgrid && cardGrid.cardView),
            cardGridEnable: cardGrid.enableCardgrid,
            cardGridColumn: cardGrid.gridColumn,
            height: configuration.height,
            striped: true,
            escape: true,
            idField: idField,
            resizable: configuration.columnDnd,
            searchText: configuration.initSearchText,
            fixedColumns: ifFixedColumn,
            // fixedColumns: customConfig && customConfig.fixedColumns ? customConfig.fixedColumns : configuration.fixedColumns,
            fixedNumber:
              customConfig && customConfig.fixedNumber && configuration.showColumns
                ? +customConfig.fixedNumber
                : +configuration.fixedNumber || '1',
            fixedRightNumber:
              customConfig && customConfig.fixedRightNumber && configuration.showColumns
                ? +customConfig.fixedRightNumber
                : configuration.fixedRightNumber || '0',
            showColumns: configuration.showColumns,
            hidePageList: configuration.hidePageList || false,
            hideTotalPageText: configuration.hideTotalPageText || false,
            formatNoMatches: function () {
              return configuration.formatNoMatchText ? configuration.formatNoMatchText : '没有找到匹配的记录';
            },
            ajax: function (request) {
              _self.load(request);
            },
            onClickRow: function (row, $element, field, e) {
              //选中按钮，不处理
              if (e) {
                var $target = $(e.target);
                if ($target.closest('.well-btn').length || $target.closest('button').length || $target.closest('.dropdown-menu').length) {
                  return;
                }
              }

              var rowNum = $element.attr('data-index');
              if (field !== lineEnderToolbar && field !== rowCheckItem) {
                _self.invokeDevelopmentMethod('onClickRow', [rowNum, row, $element, field, e]);
                _self.readMarkerAdd(e);

                _self.trigger('ClickRow', {
                  rowNum: rowNum,
                  row: row,
                  element: $element,
                  field: field
                });
              }
            },
            onDblClickRow: function (row, $element, field) {
              //                    if ($(window.event.target).is('.tree-icon')) {//树形折叠/展开按钮的点击
              //                        return;
              //                    }
              var rowNum = $element.attr('data-index');
              if (field !== lineEnderToolbar && field !== rowCheckItem) {
                _self.invokeDevelopmentMethod('onDblClickRow', [rowNum, row, $element, field]);
              }
            },
            onClickCell: function () {
              //                    if ($(window.event.target).is('.tree-icon')) {//树形折叠/展开按钮的点击
              //                        return;
              //                    }
              _self.invokeDevelopmentMethod('onClickCell', arguments);
            },
            onSaveConfig: function (config) {
              _self.saveCustomConfiguration(_self.options.widgetDefinition.id, config); // 调后端接口保存用户自定义设置
              _self.closeCustomColumnPopup(); // 关闭自定义框
              _self._resetTableOptions(_self.getConfiguration(), config);
            },
            onPageChange: function (number) {
              currentSelectedPage = number;
            },
            onToggle: function (isCardView) {
              var _this = this;
              var $container = $tableElement.closest('.ui-wBootstrapTable');

              cardView = isCardView;
              if (isCardView) {
                $container.addClass('card-view');
              } else {
                $container.removeClass('card-view');
                $tableElement.bootstrapTable('selectPage', currentSelectedPage);
              }

              buttonElement = _self._renderButton(cardView, false);

              // 重新渲染表头按钮
              var toolbarButtons = $('.fixed-table-toolbar .bs-bars .div_header_toolbar', $container);
              toolbarButtons.replaceWith(buttonElement.headerToolbar);

              if (isCardView) {
                $container.find('.card-view .title').width('');
                $container.find('.fixed-table-body table').width('');
              }

              attachLineUnderButton(isCardView);
            },
            onSort: function (name, order) {
              _self.invokeDevelopmentMethod('onSort', [name, order]);
            },
            onPostBody: function (data) {
              // 重新设置行内按钮列宽
              var $inlineButton = $tableElement.find('tbody tr td.table-handle');
              if ($inlineButton.length) {
                var maxButtonWidth = 0;
                $.each($inlineButton, function (idx, el) {
                  var $button = $(el).find('.div_lineEnd_toolbar:first');
                  var width = _self._getElementWidth($button.clone());
                  if (width > maxButtonWidth) {
                    maxButtonWidth = width;
                  }
                });

                $tableElement.find('tr > .table-handle').width(maxButtonWidth + 50);
                $tableElement.find('tr > .table-handle .th-inner').width('');
              } else {
                $tableElement.find('tr > th.table-handle .th-inner').width('');
              }

              // 设置提示
              $tableElement.find('tr>td').each(function () {
                var $cell = $(this);
                if ($cell.find('.div_lineEnd_toolbar').length > 0) {
                  //如果是操作按钮组，不需要设置title
                  $cell.attr('title', '');
                  return true;
                }
                var options = $tableElement.bootstrapTable('getOptions');
                if (options.cardView !== true) {
                  $cell.attr('title', $cell.text());
                }
              });

              if (_self.isFixedColumns()) {
                $tableElement.css('opacity', 0);
              }

              // _self._debounce(function () {
              var $container = $tableElement.closest('.ui-wBootstrapTable');
              var options = $tableElement.bootstrapTable('getOptions');
              if (!options.columns) {
                return;
              }
              var widthSetColumns = [];
              $.each(options.columns[0], function (idx, col) {
                if (!options.fixedColumns || col.width) {
                  widthSetColumns.push(col.field);
                }
              });

              // 设置最多行数
              if (configuration.maxRows && configuration.maxRows > 1) {
                var isIEBrowser = !!(navigator.userAgent.indexOf('MSIE ') > 0 || !!navigator.userAgent.match(/Trident.*rv\:11\./));

                // 计算行高
                if (options.cardView) {
                  $container.find('.card-view > .title').css('width', '');

                  $container.find('.card-view > .title, .card-view > .value').each(function () {
                    var $cell = $(this);
                    $cell.wrapInner('<span class="multiple-rows-wrapper" style="-webkit-line-clamp: ' + configuration.maxRows + ';">');
                  });
                } else {
                  var height = configuration.maxRows * 24 + 8;

                  $container.find('tr').css('max-height', height);
                  $container.find('tr > td').each(function () {
                    var $cell = $(this);
                    var field = $cell.attr('data-field');
                    if ($.inArray(field, widthSetColumns) > -1 && $cell.children('.multiple-rows-wrapper').length === 0) {
                      $cell.wrapInner('<span class="multiple-rows-wrapper" style="-webkit-line-clamp: ' + configuration.maxRows + ';">');
                    }
                  });
                  $container.find('tr > th > .th-inner').each(function () {
                    var $cell = $(this);
                    var field = $($cell.closest('th')).attr('data-field');
                    if ($.inArray(field, widthSetColumns) > -1 && $cell.children('.multiple-rows-wrapper').length === 0) {
                      $cell.css({
                        '-webkit-line-clamp': configuration.maxRows,
                        'max-height': height
                      });
                      $cell.addClass('multiple-rows-wrapper');
                    }
                  });

                  if (isIEBrowser) {
                    $container.find('.multiple-rows-wrapper').css({
                      display: 'inline-block',
                      'max-height': height + 3
                    });
                  }
                }
              }
              $tableElement.bootstrapTable('resetView');
              var height = $container.find('.fixed-table-body:first').height();
              var scrollWidth = $container.find('.fixed-table-body:first').get(0).scrollWidth;
              var clientWidth = $container.find('.fixed-table-body:first').get(0).clientWidth;
              var hasScrollBar = scrollWidth > clientWidth;
              console.log('hasScrollbar', hasScrollBar);

              // if(_self.triggerOnDataChange){
              //   debugger;
              //   _self.triggerOnDataChange = false;
              //   _self.fixedColumns();
              // }
              // }, 50);

              // $('.fixed-table-body-columns').html('');

              _self.invokeDevelopmentMethod('onPostBody', arguments);

              //$tableElement.bootstrapTable('toggleTreeView');
            },
            rowStyle: function (row, index) {
              var style = {};
              style.classes = (configuration.rowClass || []).join(' ');
              return style;
            },
            queryParamsType: '',
            queryParams: function (params) {
              var newParams = {
                pagination: {
                  pageSize: params.pageSize,
                  currentPage: params.pageNumber
                },
                criterions: _self._collectCriterion()
              };
              _self.saveSearchSnapshot(newParams.criterions);

              if (StringUtils.isNotBlank(params.sortName)) {
                newParams.orders = [
                  {
                    sortName: params.sortName,
                    sortOrder: params.sortOrder
                  }
                ];
              }

              return newParams;
            },
            undefinedText: '',
            pagination: configuration.pagination,
            pageStyleOption: {
              pageStyle: configuration.pageStyle,
              waterfallPageLabel: configuration.waterfallPageLabel,
              prePageLabel: configuration.prePageLabel,
              nextPageLabel: configuration.nextPageLabel,
              jumpPage: configuration.jumpPage
            },
            pageSize: configuration.pageSize || 20,
            searchOnEnterKey: true,
            //数据加载完后触发
            onLoadSuccess: function (data) {
              console.log('表格数据加载完毕,触发onLoadSuccess方法');
              customConfig = _self.getCustomConfiguration(false);
              _self.markReadStatus(data.rows, $tableElement);

              if (configuration.fixedColumns || (customConfig && customConfig.fixedColumns)) {
                // $tableElement.css('table-layout', 'auto'); //如果是固定列，重置表格table-layout样式

                // 重新设置th的宽度
                var $header = $tableElement.find('thead');
                $header.find('>tr>th').each(function () {
                  var $this = $(this);
                  var field = $this.attr('data-field');
                  if (field == 'lineEnderToolbar') {
                    //操作栏宽度不需要重新计算
                    return true;
                  }
                  var thInnerWidth = $this.find('.th-inner').css('width');
                  var fontWidth = _self._calculateHtmlWidth($this.text()); // $this.text().length * _self._fontSize();
                  var thWidth = fontWidth || 80;
                  var index = $this.find('.th-inner').attr('style').indexOf('width');
                  if (index == -1) {
                    getTdTextWidth($this, field, thWidth);
                  } else {
                    $this.css({
                      width: thInnerWidth
                    });
                  }
                });
                $tableElement.bootstrapTable('resetView');
                //$tableElement.bootstrapTable('fixedColumns');
              } else {
                $tableElement.css('table-layout', 'fixed');
                // $tableElement.css('width', '100%');
              }

              // 获取表体文本宽度
              function getTdTextWidth(thObject, field, thWidth) {
                // console.log(thObject, field);
                var $tbodys = $tableElement.find('tbody');
                var tds = $tbodys.find('>tr>td[data-field="' + field + '"]');
                var arr = [];
                var newArr = [];
                tds.each(function () {
                  var fontWidth = _self._calculateHtmlWidth($(this).text()); // _self._fontSize()*$(this).text().length;
                  arr.push(fontWidth || thWidth);
                });
                newArr = arr.sort(function (a, b) {
                  return b - a;
                });
                if (newArr[0] > thWidth) {
                  thObject.css('width', newArr[0] + 'px');
                } else if (newArr[newArr.length - 1] > thWidth) {
                  thObject.css('width', newArr[newArr.length - 1] + 'px');
                } else {
                  thObject.css('width', thWidth);
                }
              }

              attachLineUnderButton(cardView);

              _self.invokeDevelopmentMethod('onLoadSuccess', arguments);

              showOrHidePageDetail();
              // debugger;
              if (data.total) {
                _self.element.find('.pagination-info').find('.page-more').show();
              } else {
                _self.element.find('.pagination-info').find('.page-more').hide();
              }
              if (_self.isFixedColumns()) {
                window.setTimeout(function () {
                  _self.fixedColumns();
                  $tableElement.trigger('fixedTableColumnsSuccess', ['fixedOnLoadSuccess']);
                }, 10);
              }
              _self._createAutoRefreshInterval(); //数据加载后，启用刷新定时器
            }
          },
          bootstrapTableOptions
        )
      );

      function showOrHidePageDetail() {
        var paginationWrapW = _self.element.find('.fixed-table-pagination').width();
        var paginationW = _self.element.find('.pagination').outerWidth();
        var paginationDetailW = _self.element.find('.pagination-detail').outerWidth();
        if (paginationW + paginationDetailW > paginationWrapW) {
          _self.element.find('.pagination-detail').hide();
        } else {
          _self.element.find('.pagination-detail').show();
        }
      }

      $(window).resize(function () {
        showOrHidePageDetail();
        // 固定列重新固定
        var customConfig = _self.getCustomConfiguration(false);
        if (configuration.fixedColumns || (customConfig && customConfig.fixedColumns)) {
          $tableElement.css('table-layout', 'fixed');
          $tableElement.css('width', '100%');
          //			$tableElement.bootstrapTable('resetView');
          $tableElement.css('table-layout', 'auto');
          $tableElement.trigger('fixedTableColumnsSuccess', ['fixedOnLoadSuccess']);
          $tableElement.bootstrapTable('fixedColumns');
        }
        $tableElement.bootstrapTable('resetView');
      });

      function attachLineUnderButton(cardView) {
        if (_self.buttonElement.lineUnderButtonHtmls && $('.no-records-found', $element).size() <= 0) {
          var $trs = cardView ? $('.card-view-table tr', _self.$tableElement) : $('tr:gt(0)', _self.$tableElement);

          var lineUnderBtn = _self.buttonElement.lineUnderButtonHtmls;
          var newHtml = {};
          $.each(lineUnderBtn, function (indexBtn, itemBtn) {
            if (!newHtml[itemBtn.column]) {
              newHtml[itemBtn.column] = itemBtn.buttonHtml;
            } else {
              newHtml[itemBtn.column] += itemBtn.buttonHtml;
            }
          });
          $.each($trs, function (index, item) {
            for (var i in newHtml) {
              var formatHtml = {
                before: $("<div class='underline-btn' index='" + index + "'>").append(newHtml[i]),
                after: null
              };
              var row = _self.getData()[index];
              var btns = _self.getConfiguration().buttons;
              $.each(btns, function (j, btn) {
                var $btnHtml = formatHtml.before;
                if (btn.btnConstraint && btn.btnConstraint.template) {
                  var condition = btn.btnConstraint.template;
                  if (!eval(condition)) {
                    $btnHtml.find('.btn_class_' + btn.code).remove();
                    formatHtml.before = $btnHtml;
                  }
                }
              });
              var $item = $(item)
                .find(cardView ? '.card-view' : 'td')
                .eq(configuration.hideChecked ? i - 1 : i);

              if (!cardView) {
                $item.addClass('underPadding').append(formatHtml.before);
              } else {
                $item.find('.value').addClass('underPadding').append(formatHtml.before);
              }
            }
          });
        }
      }

      if (!configuration.clickToSelect && (!configuration.defineEventJs || !configuration.defineEventJs['on-mouse-click-code'])) {
        $tableElement.addClass('disable-pointer-cursor');
      }

      // 如果为卡片视图，延迟切换为卡片视图 （由于固定列插件和卡片视图插件冲突）
      if (isCardView || (cardGrid.enableCardgrid && cardGrid.cardView)) {
        appModal.showMask();
        setTimeout(function () {
          $tableElement.bootstrapTable('toggleCardView');
          appModal.hideMask();
        }, 300);
      }

      //获取行按钮的整体宽度
      var $container = _self.pageContainer.element ? _self.pageContainer.element : _self.pageContainer;

      $tableElement
        .on('mouseenter', 'tr', function (e) {
          if (cardView) {
            if (!$(this).closest('.card-view-table').length) {
              return;
            }

            var $this = $(e.target).closest('tr');
            var row = _self.getData()[parseInt($(this).attr('data-index'))];

            $.each(buttonElement.floatButtonHtml, function (i, item) {
              var renderTd = $this
                .find('.card-view .value')
                .css('position', 'relative')
                .eq(item.column - 1);

              if (!renderTd.find('.table-float-btn').length) {
                renderTd.append('<div class="table-float-btn"></div>');
              }

              var formatHtml = item.buttonHtml;
              var btns = _self.getConfiguration().buttons;

              $.each(btns, function (j, btn) {
                if (btn.btnConstraint && btn.btnConstraint.template) {
                  var condition = btn.btnConstraint.template;
                  if (!eval(condition)) {
                    if ($(formatHtml).hasClass('btn_class_' + btn.code)) {
                      formatHtml = '';
                    }
                  }
                }
              });
              renderTd.find('.table-float-btn').append(formatHtml);
            });
          } else {
            var $this = $(this);
            if (!$this.is(':gt(0)')) {
              return;
            }
            var row = _self.getData()[parseInt($(this).attr('data-index'))];
            $.each(buttonElement.floatButtonHtml, function (i, item) {
              var renderTd = $this
                .find('td')
                .css('position', 'relative')
                .eq(configuration.hideChecked ? item.column - 1 : item.column);
              if (!renderTd.find('.table-float-btn').length) {
                renderTd.append('<div class="table-float-btn"></div>');
              }
              var formatHtml = item.buttonHtml;
              var btns = _self.getConfiguration().buttons;
              $.each(btns, function (j, btn) {
                if (btn.btnConstraint && btn.btnConstraint.template) {
                  var condition = btn.btnConstraint.template;
                  if (!eval(condition)) {
                    if ($(formatHtml).hasClass('btn_class_' + btn.code)) {
                      formatHtml = '';
                    }
                  }
                }
              });
              renderTd.find('.table-float-btn').append(formatHtml);
            });
          }
        })
        .on('mouseleave', 'tr:gt(0)', function (e) {
          _self.element.find('.table-float-btn').remove();
        });
      // 监听鼠标移到组件
      $element
        .on('mouseover', function (e) {
          _self._distoryAutoRefreshInterval();
        })
        .on('mouseout', function (e) {
          _self._createAutoRefreshInterval(); //启用循环
        });

      if (configuration.query.keyword) {
        _self.searchType = 'keyword';
      } else if (configuration.query.fieldSearch) {
        _self.searchType = 'fieldSearch';
        _self.$fieldSearchElement = _self._renderFieldSearch($element);
      }

      this._setTableSelfDefineCodeEvent($tableElement);

      if (
        StringUtils.isNotBlank(_self.searchType) ||
        StringUtils.isNotBlank(configuration.exportTypes) ||
        treeGrid.enableTreegrid ||
        cardGrid.enableCardgrid
      ) {
        var toolbar = new commons.StringBuilder();
        var searchToolbar = new commons.StringBuilder();
        toolbar.append("<div class='columns columns-right btn-group pull-right'>");
        searchToolbar.append("<div class='btn-group pull-right search-tool-bar'>");
        if (StringUtils.isNotBlank(_self.searchType) && configuration.query.fieldSearch) {
          searchToolbar.append(
            "<button class='well-btn w-btn-primary btn-query' type='button' name='query' title='查询'>查询</button>" +
              "<button class='well-btn w-btn-light w-line-btn btn-reset' type='button' name='reset' title='重置'>重置</button>"
          );
        }
        // 导出按钮
        if (configuration.hasExport) {
          var exportFileTypes = configuration.exportTypes.split(constant.Separator.Semicolon);
          var exportDataTypes = configuration.exportDataType.split(constant.Separator.Semicolon);
          if (exportFileTypes.length == 1 && exportDataTypes.length == 1) {
            if (!configuration.exportBtn) {
              toolbar.append("<button class='btn btn-default' name='export'  type='button' title='导出'>");
              toolbar.append("	<i class='glyphicon glyphicon-export'></i>");
              toolbar.append('</button>');
            } else {
              var $exportBtn = $('#' + configuration.exportBtn, _self.widget.element);
              var btnclass = $exportBtn.attr('class');
              btnclass = btnclass.replace(/btn_class_.+|li_class_.+/, '');
              $exportBtn.attr('class', btnclass);
              $exportBtn.attr('name', 'export');
              $exportBtn.attr('file-type', exportFileTypes);
              $exportBtn.attr('data-type', exportDataTypes);
            }
          } else {
            var $ul = new commons.StringBuilder();
            $ul.append("<ul class='dropdown-menu w-btn-dropMenu' role='menu'>");
            if (exportFileTypes.length > 1 && exportDataTypes.length == 1) {
              $.each(exportFileTypes, function (index, fileType) {
                $ul.appendFormat(
                  "	<li name='export' file-type='{0}' data-type='{1}'><a href='javascript:void(0)'>{2}</a></li>",
                  fileType,
                  exportDataTypes[0],
                  exportTypeShows[index]
                );
              });
            } else if (exportFileTypes.length == 1 && exportDataTypes.length > 1) {
              $.each(exportDataTypes, function (index, dataType) {
                var dataTypeName = dataType == '1' ? '全部' : dataType == '2' ? '当前页' : '当前选中项';
                $ul.appendFormat(
                  "	<li name='export' file-type='{0}' data-type='{1}'><a href='javascript:void(0)'>{2}</a></li>",
                  exportFileTypes[0],
                  dataType,
                  dataTypeName
                );
              });
            } else {
              $.each(exportFileTypes, function (fileIndex, fileType) {
                $.each(exportDataTypes, function (dataIndex, dataType) {
                  var dataTypeName = dataType == '1' ? '全部' : dataType == '2' ? '当前页' : '当前选中项';
                  var btnName = dataTypeName + '[' + exportTypeShows[fileIndex] + ']';
                  $ul.appendFormat(
                    "	<li name='export' file-type='{0}' data-type='{1}'><a href='javascript:void(0)'>{2}</a></li>",
                    exportFileTypes[fileIndex],
                    dataType,
                    btnName
                  );
                });
              });
            }
            $ul.append('</ul>');
            if (!configuration.exportBtn) {
              var exportTypeShows = configuration.exportTypeShows.split(constant.Separator.Semicolon);
              toolbar.append("<div class='export btn-group'>");
              toolbar.append("<button class='btn btn-default dropdown-toggle' data-toggle='dropdown' title='导出' type='button'>");
              toolbar.append("	<i class='glyphicon glyphicon-export'></i>");
              toolbar.append("	<span class='fa fa-angle-down'></span>");
              toolbar.append('</button>');
              toolbar.append($ul.toString());
              toolbar.append('</div>');
            } else {
              var $exportBtn = $('#' + configuration.exportBtn, _self.element);
              if ($exportBtn.length > 0) {
                var btnclass = $exportBtn.attr('class');
                btnclass = btnclass.replace(/btn_class_.+|li_class_.+/, '');
                $exportBtn.attr('class', btnclass);
                $exportBtn.html('<span>' + $exportBtn.text() + '</span><i class="iconfont icon-ptkj-xianmiaojiantou-xia"></i>');
                $exportBtn.addClass('dropdown-toggle').attr('data-toggle', 'dropdown').attr('aria-expanded', 'false');
                $exportBtn.wrap(
                  $('<div>', {
                    class: 'export btn-group'
                  })
                );
                var $div = $exportBtn.parent();
                $div.append($ul.toString());
              }
            }
          }
        }
        // 树形/卡片列表切换
        if (treeGrid.enableTreegrid || cardGrid.enableCardgrid) {
          toolbar.append('<div class="btn-group split-btn-group">');
          if (cardGrid.enableCardgrid) {
            toolbar.append(
              '<button type="button" class="btn btn-success" name="toggleCardView" title="切换卡片列表"><i class="glyphicon glyphicon-th-large"></i></button>'
            );
            var gridColumns = (cardGrid.gridColumn + '').split(',');
            if (gridColumns.length > 1) {
              toolbar.append(
                '<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">'
              );
              toolbar.append('<span class="fa fa-angle-down"></span>');
              toolbar.append('<span class="sr-only">Toggle Dropdown</span>');
              toolbar.append('</button>');
              toolbar.append('<ul class="dropdown-menu card-grid-menu">');
              gridColumns.sort();
              for (var i = 0; i < gridColumns.length; i++) {
                toolbar.appendFormat(
                  "<li class='card-grid-option card-grid-{0}'><a href=\"#\" grid-column='{0}'>{0}列</a></li>",
                  gridColumns[i]
                );
              }
              toolbar.append('</ul>');
            }
          }
          if (treeGrid.enableTreegrid) {
            toolbar.append(
              '<button type="button" class="btn btn-success" name="toggleTreeView" title="切换树形列表"><i class="iconfont icon-ptkj-liebiaoshitu" ></i></button>'
            );
            toolbar.append(
              '<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">'
            );
            toolbar.append('<span class="fa fa-angle-down"></span>');
            toolbar.append('<span class="sr-only">Toggle Dropdown</span>');
            toolbar.append('</button>');
            toolbar.append('<ul class="dropdown-menu">');
            toolbar.append('<li class="allTreeNodeExpand"><a href="#">全部展开</a></li>');
            toolbar.append('<li class="allTreeNodeCollapse"><a href="#">全部折叠</a></li>');
            toolbar.append('</ul>');
          }
          toolbar.append('</div>');
        }

        // 条件查询和关键字查询切换按钮
        if (configuration.query.fieldSearch && configuration.query.keyword) {
          searchToolbar.append('<div class="hide-query-fields">收起 <i class="iconfont icon-ptkj-xianmiaoshuangjiantou-shang"></i></div>');
        }
        toolbar.append('</div>');
        searchToolbar.append('</div>');
        var $tableToolbar = $element.find('.fixed-table-toolbar');
        $tableToolbar.prepend(toolbar.toString());
        var $searchToolbar = $('<div class="fixed-table-search-toolbar">' + searchToolbar.toString() + '</div>');
        $tableToolbar.before($searchToolbar);
        if (StringUtils.isNotBlank(_self.searchType)) {
          // 查询事件
          $searchToolbar.find("button[name='query']").on('click', function () {
            _self._onSearch();
          });
          // 重置事件
          $searchToolbar.find("button[name='reset']").on('click', function () {
            _self.resetQueryFields();
            _self._onSearch();
          });
          $searchToolbar.find('.hide-query-fields').on('click', function () {
            $(this).parent().hide();
            $element.find('.div_search_toolbar').hide();
            $element.find('.keyword_search_toolbar').show();
            _self.searchType = 'keyword';
          });
          $tableToolbar.find("button[name='showQueryField']").on('click', function () {
            var $this = $(this);
            if (_self.searchType == 'keyword') {
              if (_self.$fieldSearchElement) {
                _self.$fieldSearchElement.show();
              } else {
                _self.$fieldSearchElement = _self._renderFieldSearch($element);
              }
              $element.find('.fixed-table-toolbar .search').hide();
              $this.find('i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
              $this.attr('title', '收起字段查询');
              _self.searchType = 'fieldSearch';
            } else {
              _self.$fieldSearchElement.hide();
              $element.find('.fixed-table-toolbar .search').show();
              $this.find('i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
              $this.attr('title', '展开字段查询');
              _self.searchType = 'keyword';
            }
            _self.resetHeight();
          });

          // 默认展开字段查询
          if (configuration.query.fieldSearch && configuration.query.expandFieldSearch) {
            $element.find('.more-search').trigger('click');
          } else {
            $element.find('.search-tool-bar').hide();
            $element.find('.div_search_toolbar').hide();
          }

          $element.on('keyup', '.w-search-option', function (event) {
            if (event.keyCode == 13) {
              _self._onSearch();
            }
          });
        }
        if (configuration.hasExport) {
          $tableToolbar.find("*[name='export']").on('click', function () {
            var exportTypes = configuration.exportTypes;
            var params = {
              fileName: configuration.fileName,
              type: exportTypes.indexOf('_column') > -1 ? exportTypes.split('exportTypes')[0] : exportTypes,
              exportTemplateId: configuration.exportTemplateFileId,
              searchSnapshot: _self.getSearchSnapshot(),
              beforeExportCode: configuration.beforeExportCode,
              Api: new Api(_self)
            };
            var dataType = configuration.exportDataType;
            var $element = $(this);
            if ($element.is('li')) {
              params.type = $element.attr('file-type');
              dataType = $element.attr('data-type');
            }
            if (dataType == '1') {
              //导出全部
              params.pagination = {
                pageSize: 100000000,
                currentPage: 1
              };
            } else if (dataType == '3') {
              //导出选中项
              var selected = _self.getSelections();
              if (selected && selected.length > 0) {
                var uuids = [];
                $.each(selected, function (i, item) {
                  uuids.push(item.uuid || item.UUID);
                });
                var filterCriterion = {
                  columnIndex: selected[0]['uuid'] ? 'uuid' : 'UUID',
                  value: uuids,
                  type: 'in'
                };
                params.criterions = [filterCriterion];
                // 导出选中项，currentPage 必须重置为 1，不然会出现，翻页后导出选择项，数据出不来的问题。
                params.pagination = {
                  pageSize: 100000,
                  currentPage: 1
                };
              } else {
                appModal.error('请先选择要导出的记录');
                return false;
              }
            }
            var exportCallback = configuration.exportCallback;
            _self.getDataProvider().exportData(
              params,
              function () {
                appModal.error('导出异常');
              },
              exportCallback
            );
          });
        }
        if (treeGrid.enableTreegrid) {
          var $treeIcon = $tableToolbar.find("button[name='toggleTreeView']");
          //监听表格渲染完成事件，如果树形列表启用状态，则需要进行树形展示
          $tableElement.on('post-body.bs.table', function () {
            var options = $tableElement.bootstrapTable('getOptions');
            if (options.treeView && !options.postBody2TreeView) {
              $treeIcon.trigger('treeIconChange');
              options.treeView = false;
              options.postBody2TreeView = true; //表示是有表格渲染事件触发的树形结构转换
              $tableElement.bootstrapTable('toggleTreeView');
            }
            // delete options.postBody2TreeView;//转换完，删除该属性，避免死循环
          });

          $treeIcon.on('click', function () {
            $tableElement.bootstrapTable('getOptions').postBody2TreeView = true; //在post-body.bs.table事件处理时候，避免重复加载树形
            $tableElement.bootstrapTable('toggleTreeView'); //切换树形列表方法，bootstrap-table-treegrid.js

            $(this).trigger('treeIconChange');
          });
          $tableElement.on('post-body.bs.table toggle.bs.table', function () {
            var options = $tableElement.bootstrapTable('getOptions');
            if (options.treeView) {
              $treeIcon.attr('title', '恢复初始列表');
              $treeIcon.find('i').removeClass('icon-ptkj-liebiaoshitu');
              $treeIcon.find('i').addClass('icon-wsbs-liebiaoshitu');
            } else {
              $treeIcon.attr('title', '切换树形列表');
              $treeIcon.find('i').removeClass('icon-wsbs-liebiaoshitu');
              $treeIcon.find('i').addClass('icon-ptkj-liebiaoshitu');
            }
          });
        }
        if (cardGrid.enableCardgrid) {
          var $cardIcon = $tableToolbar.find("button[name='toggleCardView']");
          $tableElement.on('post-body.bs.table toggle.bs.table', function () {
            var options = $tableElement.bootstrapTable('getOptions');
            if (options.cardView) {
              $cardIcon.attr('title', '恢复初始列表');
              $cardIcon.find('i').removeClass('glyphicon');
              $cardIcon.find('i').removeClass('glyphicon-th-large');
              $cardIcon.find('i').addClass('iconfont');
              $cardIcon.find('i').addClass('icon-wsbs-liebiaoshitu');
            } else {
              $cardIcon.attr('title', '切换卡片列表');
              $cardIcon.find('i').addClass('glyphicon');
              $cardIcon.find('i').addClass('glyphicon-th-large');
              $cardIcon.find('i').removeClass('iconfont');
              $cardIcon.find('i').removeClass('icon-wsbs-liebiaoshitu');
            }
          });
          $cardIcon.on('click', function (e) {
            $tableElement.bootstrapTable('toggleCardView');
          });
          // 卡片列数切换
          $tableToolbar.find('.card-grid-menu').on('click', '.card-grid-option>a', function (e) {
            var gridColumn = $(this).attr('grid-column');
            $tableElement.bootstrapTable('switchToCardView', gridColumn);
          });
        }
      }
      if (buttonElement.footerToolbar) {
        $element.append(buttonElement.footerToolbar);
      }

      return $tableElement;
    },

    clearSearchSnapshot: function () {
      this.searchSnapshot = {};
    },

    saveSearchSnapshot: function (criterions) {
      if (criterions && criterions.length === 0) {
        return;
      }
      var _self = this;
      var configuration = _self.getConfiguration();
      var searchSnapshot = {};
      if (configuration.query.multiKeyword) {
        console.log(criterions);
      } else {
        var $fieldSearchElement = _self.element.find('.div_search_toolbar');
        $.each(configuration.query.fields, function (i, field) {
          var fieldSearch = false;
          for (var i = 0, len = criterions.length; i < len; i++) {
            if (field.name === criterions[i].columnIndex) {
              fieldSearch = true;
              break;
            }
          }
          if (fieldSearch) {
            var queryOption = field.queryOptions;
            var queryType = queryOption.queryType;
            if (queryType == 'date') {
              if (field.operator == 'between') {
                searchSnapshot[field.name + '_Begin'] = $fieldSearchElement.find('#' + field.name + '_Begin').val();
                searchSnapshot[field.name + '_End'] = $fieldSearchElement.find('#' + field.name + '_End').val();
              }
            } else {
              searchSnapshot[field.name] = $fieldSearchElement.find('#' + field.name).val();
            }
          }
        });
      }
      _self.searchSnapshot = searchSnapshot;
    },

    /**
     * 获取查询快照
     */
    getSearchSnapshot: function () {
      return this.searchSnapshot;
    },

    _setTableSelfDefineCodeEvent: function ($tableElement) {
      var _self = this;
      var configuration = this.getConfiguration();
      if (configuration.defineEventJs && configuration.defineEventJs['onPreBodyCode']) {
        $tableElement.on('pre-body.bs.table', function (e, data) {
          var defineFunc = appContext.eval(configuration.defineEventJs['onPreBodyCode'], $(this), {
            $this: $tableElement,
            event: e,
            commons: commons,
            server: server,
            Api: new Api(_self),
            data: data
          });
          console.log('在表格body渲染之前触发自定义代码块', defineFunc);
        });
      }

      if (configuration.defineEventJs && configuration.defineEventJs['onPostBodyCode']) {
        $tableElement.on('post-body.bs.table', function (e, data) {
          var defineFunc = appContext.eval(configuration.defineEventJs['onPostBodyCode'], $(this), {
            $this: $tableElement,
            event: e,
            commons: commons,
            server: server,
            Api: new Api(_self),
            data: data
          });
          console.log('在表格body渲染之后触发自定义代码块', defineFunc);
        });
      }

      if (configuration.defineEventJs && configuration.defineEventJs['onMouseOverCode']) {
        $tableElement.on('mouseover', 'tr:gt(0)', function (e) {
          var rowData = _self.getData()[parseInt($(this).attr('data-index'))];
          var defineFunc = appContext.eval(configuration.defineEventJs['onMouseOverCode'], $(this), {
            $this: $(this),
            event: e,
            commons: commons,
            row: rowData,
            server: server,
            Api: new Api(_self)
          });
          console.log('鼠标移入表格行触发自定义代码块', defineFunc);
        });
      }

      if (configuration.defineEventJs && configuration.defineEventJs['onMouseOutCode']) {
        $tableElement.on('mouseout ', 'tr:gt(0)', function (e) {
          var defineFunc = appContext.eval(configuration.defineEventJs['onMouseOutCode'], $(this), {
            $this: $(this),
            event: e,
            commons: commons,
            server: server,
            Api: new Api(_self)
          });
          console.log('鼠标移出表格行触发自定义代码块', defineFunc);
        });
      }

      if (configuration.defineEventJs && configuration.defineEventJs['onMouseClickCode']) {
        $tableElement.on('click', 'tr:gt(0)', function (e) {
          if ($(e.target).hasClass('well-btn') || $(e.target)[0].tagName === 'BUTTON') {
            return;
          }
          var rowData = _self.getData()[parseInt($(this).attr('data-index'))];
          var defineFunc = appContext.eval(configuration.defineEventJs['onMouseClickCode'], $(this), {
            $this: $(this),
            event: e,
            commons: commons,
            row: rowData,
            server: server,
            Api: new Api(_self)
          });
          console.log('鼠标点击表格行触发自定义代码块', defineFunc);
        });
      }
    },
    /**
     * 视图组件渲染主体方法
     */
    _renderView: function () {
      var _self = this;
      var $element = $(this.element);
      var configuration = this.getConfiguration();

      if (StringUtils.isNotBlank(configuration.width)) {
        $element.width(configuration.width);
      }
      if (StringUtils.isNotBlank(configuration.height)) {
        $element.height(configuration.height);
      }
      console.log('width:' + $element.width());

      switch (configuration.query.queryType) {
        case 'keywordFieldQueryWrap':
          _self._renderKeywordSearch();
          break;
        case 'propertyQueryWrap':
          _self._renderPropertySearch();
          break;
        default:
          _self._renderKeywordSearch();
          break;
      }

      this.buttonElement = this._renderButton(configuration.cardGrid && configuration.cardGrid.cardView, true);
      this.$tableElement = this._renderTable($element, this.buttonElement);
      // bootstrap-table-resizable中initResizable延后100秒执行，需要跟着延后，确保是初始化完列拖动后才处理高度。

      setTimeout(function () {
        _self.resetHeight();
      }, 150);
    },

    /**
     * 视图组件渲染结束调用
     */
    _afterRenderView: function () {
      var _self = this;
      _self.invokeDevelopmentMethod('afterRender', [this.options, this.getConfiguration()]);
      var configuration = this.getConfiguration();
      if (configuration.defineEventJs && configuration.defineEventJs['afterRenderCode']) {
        var defineFunc = appContext.eval(configuration.defineEventJs['afterRenderCode'], $(this), {
          $this: _self,
          configuration: configuration,
          commons: commons,
          server: server,
          Api: new Api(_self)
        });
        console.log('在表格组件渲染之后触发自定义代码块', defineFunc);
      }
    },
    /**
     * 收集查询条件
     */
    _collectCriterion: function () {
      var _self = this;
      var options = _self.options;
      var configuration = _self.getConfiguration();
      var $element = $(_self.element);
      var criterions = [];
      // if (StringUtils.isNotBlank(_self.queryText)) {
      //     var orcriterion = {
      //         conditions: [],
      //         type: 'or'
      //     };
      //     $.each(configuration.columns, function (index, column) {
      //         if (column.hidden == '0' && column.keywordQuery == '1') {
      //             orcriterion.conditions.push({
      //                 columnIndex: column.name,
      //                 value: _self.queryText,
      //                 type: 'like'
      //             });
      //         }
      //     });
      //     criterions.push(orcriterion);
      // }
      // 初始化表格时，由外部传入的初始化查询文本
      var initSearchText = configuration.initSearchText;
      // 清空keyword
      _self.getDataProvider().setKeyword('');
      if (_self.searchType === 'keyword' || (StringUtils.isBlank(_self.searchType) && StringUtils.isNotBlank(initSearchText))) {
        var text = initSearchText;
        if (_self.searchType === 'keyword') {
          text = _self.getKeyword();
        }
        if (StringUtils.isNotBlank(text)) {
          // 特殊字符%模糊查询转义
          if (StringUtils.contains(text, '%')) {
            text = text.replace(new RegExp(/%/g), '/%');
          }
          if (configuration.query.multiKeyword) {
            _self.getDataProvider().setKeyword(text);
            var _$dropdownValueEle = $element.find('.dropdown-value');
            var _currMultiKeyword = configuration.query.multiKeywordData[_$dropdownValueEle.data('id')];

            if (_currMultiKeyword) {
              var criterion = {
                columnIndex: _currMultiKeyword.name,
                value: text,
                type: _currMultiKeyword.operator
              };
              criterions.push(criterion);
            } else {
              var orcriterion = {
                conditions: [],
                type: 'or'
              };
              $.each(configuration.query.multiKeywordData, function (index, column) {
                orcriterion.conditions.push({
                  columnIndex: column.name,
                  value: text,
                  type: column.operator
                });
              });

              criterions.push(orcriterion);
            }
          } else {
            _self.getDataProvider().setKeyword(text);
            var orcriterion = {
              conditions: [],
              type: 'or'
            };
            $.each(configuration.columns, function (index, column) {
              if (column.keywordQuery == '1') {
                orcriterion.conditions.push({
                  columnIndex: column.name,
                  value: text,
                  type: 'like'
                });
              }
            });

            criterions.push(orcriterion);
          }
        }
      } else if (_self.searchType === 'fieldSearch') {
        if (configuration.query.queryType === 'propertyQueryWrap') {
          var $property_search_result = $element.find('.property_search_result');
          var orcriterion = [];
          $property_search_result.find('li').each(function () {
            var $this = $(this);
            orcriterion.push({
              columnIndex: $this.data('id'),
              value: $this.data('value'),
              type: $this.data('type')
            });
            criterions = orcriterion;
          });
        } else {
          criterions = $.map(configuration.query.fields, function (field) {
            var queryType = field.queryOptions.queryType;
            if (field.queryOptions.queryType === 'date' && field.queryOptions.format.indexOf('range') > -1) {
              field.operator = 'between';
            }
            if (field.operator !== 'between' && field.operator !== 'exists') {
              var value = $('#' + field.name, $element).val();
              // select2数据字典的默认空值映射
              if (_self.fieldSearchEmptyValueMap && _self.fieldSearchEmptyValueMap[value]) {
                value = '';
              }
              // 单选框的值
              if (queryType == 'radio') {
                var $radio = $('input[name=' + field.name + ']:checked', $element);
                value = $radio.val();
              }
              // 复选框的值
              if (queryType == 'checkbox') {
                var $checkbox = $('input[name=' + field.name + ']:checked', $element);
                var checkboxValues = [];
                $.each($checkbox, function () {
                  checkboxValues.push($(this).val());
                });
                value = checkboxValues.join(';');
                if (StringUtils.isBlank(value) && StringUtils.isNotBlank(field.defaultValue)) {
                  value = field.defaultValue;
                }
              }
              //下拉树的值
              if (queryType == 'treeSelect') {
                var treeNodes = $('input[name=' + field.name + ']', $element).data('value');
                if (treeNodes && treeNodes.length > 0) {
                  var treeVals = [];
                  for (var i = 0, len = treeNodes.length; i < len; i++) {
                    treeVals.push(treeNodes[i].data[field.queryOptions.valueColumn]);
                  }
                  value = treeVals.join(';');
                }
              }
              if (StringUtils.isNotBlank(value)) {
                var criterion = {};
                criterion.columnIndex = field.name;
                criterion.value = value;
                criterion.type = field.operator;
                var queryOption = field.queryOptions;

                return criterion;
              }
            } else if (field.operator === 'exists') {
              console.log(configuration, field);
              var criterion = {};
              var custom_sql = field.custom_sql;
              var val = $('#' + field.name, $element).val();
              if (typeof val === 'string' && val.indexOf(',') > 0) {
                val = val.split(',');
              } else if (typeof val === 'string' && val.indexOf(';') > 0) {
                val = val.split(';');
              } else if (false === $.isArray(val)) {
                val = [val];
              }
              var data = {};
              data.values = val;
              var exists_sql = appContext.resolveParams(
                {
                  resolveHtml: custom_sql
                },
                data
              ).resolveHtml;
              criterion.sql = exists_sql;
              criterion.type = 'exists';
              return criterion;
            } else {
              var value1, value2;
              if ($('#' + field.name + '_Begin', $element).length && $('#' + field.name + '_End', $element).length) {
                value1 = $('#' + field.name + '_Begin', $element).val();
                value2 = $('#' + field.name + '_End', $element).val();
                if (field.queryOptions.queryType === 'text') {
                  //文本区间时把值转化为数值
                  value1 = value1 === '' ? '' : parseFloat(value1);
                  value2 = value2 === '' ? '' : parseFloat(value2);
                }
              } else {
                var valueBetween = $('#' + field.name, $element).val();
                value1 = valueBetween.split(' 至 ')[0];
                value2 = valueBetween.split(' 至 ')[1];
                if (value2 && field.queryOptions.includeEnd) {
                  value2 += ' 23:59:59';
                }
              }

              if (StringUtils.isNotBlank(value1) || StringUtils.isNotBlank(value2)) {
                var criterion = {};
                criterion.columnIndex = field.name;
                criterion.value = [value1, value2];
                criterion.type = 'between';
                var queryOption = field.queryOptions;
                if (queryType == 'date') {
                  if (_self._getFieldDataType(criterion.columnIndex) == 'Date') {
                    if (StringUtils.isNotBlank(criterion.value[0])) {
                      // criterion.value[0] = moment(criterion.value[0], field.queryOptions.format).format('YYYY-MM-DD HH:mm:ss');
                    }
                    if (StringUtils.isNotBlank(criterion.value[1])) {
                      // criterion.value[1] = moment(criterion.value[1], field.queryOptions.format).format('YYYY-MM-DD HH:mm:ss');
                    }
                  }
                }
                return criterion;
              }
            }
            return null;
          });
        }
      }
      return criterions;
    },
    /**
     * 获取默认条件查询
     */
    getDefaultConditions: function () {
      var defaultConditions = [];
      var configuration = this.getConfiguration();
      if (StringUtils.isNotBlank(configuration.defaultCondition)) {
        var criterion = {};
        configuration.defaultCondition = appContext.resolveParamsNoConflics(
          {
            sql: configuration.defaultCondition
          },
          {
            location: location
          }
        ).sql;
        criterion.sql = configuration.defaultCondition;
        defaultConditions.push(criterion);
      }
      return defaultConditions;
    },
    /**
     * 获取默认排序信息
     */
    getDefaultSort: function () {
      var defaultSort = [];
      var configuration = this.getConfiguration();
      if (configuration.defaultSorts) {
        defaultSort = $.map(configuration.defaultSorts, function (value) {
          return {
            sortName: value.sortName,
            sortOrder: value.sortOrder
          };
        });
      }
      return defaultSort;
    },
    /**
     * 获取列渲染器
     */
    getRenderers: function () {
      var renderers = [];
      var configuration = this.getConfiguration();
      $.each(configuration.columns, function (index, column) {
        if (column.renderer && StringUtils.isNotBlank(column.renderer.rendererType)) {
          renderers.push({
            columnIndex: column.name,
            param: column.renderer
          });
        }
      });
      return renderers;
    },
    /**
     * 清除视图参数
     */
    clearParams: function () {
      this.getDataProvider().clearParams();
    },
    /**
     * 添加参数
     */
    addParam: function (key, value) {
      this.getDataProvider().addParam(key, value);
    },
    /**
     * 移除参数
     */
    removeParam: function (key) {
      return this.getDataProvider().removeParam(key);
    },
    /**
     * 获取参数
     */
    getParam: function (key) {
      return this.getDataProvider().getParam(key);
    },
    /**
     * 获取选中列数据集合
     */
    getSelections: function () {
      return this.$tableElement.bootstrapTable('getSelections');
    },
    /**
     * 获取选中行索引集合
     */
    getSelectionIndexes: function () {
      return $.map(this.getData(), function (row, index) {
        if (row.rowCheckItem) {
          return index;
        }
      });
    },

    /**
     * 获取选中的主键值
     */
    getSelectionPrimaryKeys: function () {
      var primaryColumnName = this.getPrimaryColumnName();
      if (!primaryColumnName) {
        return [];
      }
      return $.map(this.getData(), function (row, index) {
        if (row.rowCheckItem) {
          return row[primaryColumnName];
        }
      });
    },

    /**
     * 获取数据集合的指定列值
     */
    getDataColumnValues: function (columnName) {
      if (columnName) {
        return $.map(this.getData(), function (row, index) {
          return row[columnName];
        });
      }
      return [];
    },

    /**
     * 获取列定义的主键列名称
     * @returns {*}
     */
    getPrimaryColumnName: function () {
      var columns = this.getConfiguration().columns;
      for (var i = 0; i < columns.length; i++) {
        if (columns[i]['idField'] == '1') {
          return columns[i]['name'];
        }
      }
      return null;
    },

    /**
     * 获取视图数据集合
     */
    getData: function () {
      return this.$tableElement.bootstrapTable('getData');
    },
    /**
     * 根据唯一键获取列数据
     */
    getRowByUniqueId: function (id) {
      return this.$tableElement.bootstrapTable('getRowByUniqueId', id);
    },
    /**
     * 刷新数据
     */
    refresh: function (notifyChange) {
      if (!this.$tableElement) {
        return;
      }

      this.element.find('.keyword-search-wrap input').val('');
      this.element.find('.close-icon').hide();
      this.$tableElement.bootstrapTable('refresh', {
        query: {
          notifyChange: notifyChange
        }
      });

      //如果表格组件在面板tab里，刷新面板tab徽章数
      if (this.element.parents('[role="tabpanel"]').hasClass('panel-tab-content')) {
        var _panelId = this.element.closest('.ui-wPanel').attr('id');
        appContext.getWidgetById(_panelId).refreshBadge();
      } else if (this.element.parents('[role="tabpanel"]')[0]) {
        //如果表格组件在标签页tab里，刷新tab徽章数
        var tabid = this.element.parents('[role="tabpanel"]').attr('id');
        $("a[href='#" + tabid + "']")
          .parent()
          .trigger(constant.WIDGET_EVENT.BadgeRefresh);
      }
      if (this.element.parents('.web-app-container').find('.menu-sidebar')[0]) {
        //如果表格组件有关联的左导航，刷新左导航徽章数
        this.element.parents('.web-app-container').trigger(constant.WIDGET_EVENT.BadgeRefresh);
      }
    },
    /**
     * 查询数据数据
     */
    query: function (queryText, type) {
      if (type === 'keyword') {
        this.searchType = 'keyword';
        this.panelKeyword = queryText;
      }
      this.queryText = queryText;
      this.$tableElement.bootstrapTable('refresh', {
        query: {
          notifyChange: true
        }
      });
    },
    setKeyword: function (keyword) {
      $(this.element).find('.keyword_search_toolbar .keyword-search-wrap input').val(keyword);
    },
    getKeyword: function () {
      return this.panelKeyword || $.trim($(this.element).find('.keyword_search_toolbar .keyword-search-wrap input').val());
    },
    ascOtherOrders: function (sortNames) {
      var _self = this;
      $.each(arguments, function (idx, sortName) {
        return _self.addOtherOrder(sortName, 'asc');
      });
    },
    descOtherOrders: function (sortNames) {
      var _self = this;
      $.each(arguments, function (idx, sortName) {
        return _self.addOtherOrder(sortName, 'desc');
      });
    },
    /**
     * 添加额外的查询排序
     */
    addOtherOrder: function (sortName, sortOrder) {
      if ($.type(sortName) != 'string') {
        return false;
      } else if (sortOrder != 'asc' && sortOrder != 'desc') {
        return false;
      }
      var _self = this;
      var otherOrders = _self.otherOrders || {};
      _self.otherOrders[sortName] = sortOrder;
    },
    /**
     * 情况额外查询排序,sortName为空是清楚全部，否则清楚等于sortName的一条额外查询排序
     */
    clearOtherOrders: function (sortName) {
      var _self = this;
      var otherOrders = _self.otherOrders;
      if (sortName && otherOrders) {
        delete otherOrders[sortName];
      } else {
        _self.otherOrders = {};
      }
    },
    /**
     * 添加额外的查询条件
     */
    addOtherConditions: function (conditions) {
      var _self = this;
      var otherConditions = _self.otherConditions || [];
      $.each(conditions, function (i, cond) {
        var index = -1;
        $.each(otherConditions, function (i, condition) {
          if (JSON.stringify(condition) == JSON.stringify(cond)) {
            index = i;
            return false;
          }
        });
        if (index == -1) {
          otherConditions.push(cond);
        }
      });
      _self.otherConditions = otherConditions;
    },
    /**
     * 情况额外查询条件,condition为空是清楚全部，否则清楚等于condition的一条额外查询条件
     */
    clearOtherConditions: function (condition) {
      var _self = this;
      var otherConditions = _self.otherConditions;
      if (condition) {
        _self.otherConditions = $.map(otherConditions, function (cond) {
          if (JSON.stringify(condition) != JSON.stringify(cond)) {
            return cond;
          }
        });
      } else {
        _self.otherConditions = [];
      }
    },
    /**
     * 页码跳转
     */
    selectPage: function (pageNum) {
      this.$tableElement.bootstrapTable('selectPage', parseInt(pageNum));
    },
    readMarkerAdd: function (event) {
      var _self = this;
      var getConfiguration = this.getConfiguration();
      if (getConfiguration.readStatus == '1') {
        if ($(event.target).parents('td').length > 0) {
          // 行末按钮或行内渲染器
          var index = $(event.target).parents('td').first().parent().data('index');
        } else if ($(event.target)[0].nodeName == 'TD') {
          // 行点击不包括渲染器
          var index = $(event.target).parent().data('index');
        } else {
          return false;
        }
        // var index = $(event.target).parents('td').parent().data('index');
        var data = this.getData()[index];
        var tagDataKey = getConfiguration['tagDataKey'];
        var uuid = data[tagDataKey];
        $.ajax({
          type: 'POST',
          url: ctx + '/api/readMarker/add',
          dataType: 'json',
          data: {
            tagDataKeys: uuid
          },
          success: function (result) {
            _self.$tableElement
              .find('tbody tr:eq(' + index + ')')
              .find('td')
              .css({
                fontWeight: 'normal'
              });
          }
        });
      }
    },
    /**
     * 重新计算（设置）视图高度
     */
    resetHeight: function (height) {
      var _self = this;
      if (!_self.$tableElement) {
        return;
      }
      var configuration = _self.getConfiguration();
      if (StringUtils.isNotBlank(height)) {
        configuration.height = height;
        var $element = $(_self.element);
        $element.height(configuration.height);
      }
      if (StringUtils.isBlank(configuration.height)) {
        return;
      }
      var fieldSearchHeight = _self._getRealHeight(_self.$fieldSearchElement);
      var footerToolbarHeight = _self._getRealHeight(_self.buttonElement.footerToolbar);
      var newHeight = configuration.height - fieldSearchHeight - footerToolbarHeight;
      console.log('new height==' + newHeight);
      _self.$tableElement.bootstrapTable('resetView', {
        height: newHeight
      });
    },

    /**
     * 需要查询时调用
     */
    _onSearch: function (params) {
      var _self = this;
      var configuration = _self.getConfiguration();
      // this.selectPage(1);
      // bootstrapTable 的刷新只有当url变更才会跳转到第一页。
      // 避免关键字查询相同时重复查询
      if (_self.alwaysRemoteQuery != true && _self.searchType === 'keyword') {
        var storeKeyword = _self.getDataProvider().getKeyword();
        var keyword = _self.getKeyword();
        if (storeKeyword === keyword && !configuration.query.multiKeyword && _self.initialized) {
          //组合关键字搜索时不作处理
          return;
        }
      }
      _self.$tableElement.bootstrapTable(
        'refresh',
        $.extend(
          {
            url: 'blank'
          },
          params
        )
      );
    },
    /**
     * 是否展开查询条件
     */
    isShowQueryFields: function () {
      return this.searchType === 'fieldSearch';
    },
    /**
     * 展开查询条件
     */
    showQueryFields: function () {
      var _self = this;
      if (!_self.isShowQueryFields()) {
        var $tableToolbar = $(_self.element).find('.fixed-table-toolbar');
        var $showQueryField = $tableToolbar.find("button[name='showQueryField']");
        $showQueryField.trigger('click');
      }
    },
    /**
     * 重置查询条件
     */
    resetQueryFields: function () {
      var _self = this;
      if (_self.searchType == 'keyword') {
        _self.setKeyword('');
      } else {
        _self.$fieldSearchElement = _self._renderFieldSearch(_self.element);
      }
      _self.invokeDevelopmentMethod('onResetQueryFields');
    },
    /**
     * 导出列信息
     */
    getExportColumns: function () {
      var exportColumns = [];
      var configuration = this.getConfiguration();
      if (configuration.hasExport) {
        //如果是按模板Excel导出，则把列定义的使用渲染器渲染的字段也包含进来
        if (configuration.exportTypes == 'templateExcel') {
          $.each(configuration.columns, function (index, column) {
            var columnIndex = column.name;
            if (column.renderer && StringUtils.isNotBlank(column.renderer.rendererType)) {
              columnIndex = columnIndex; // + "RenderValue";
              exportColumns.push({
                columnIndex: columnIndex,
                title: column.header,
                renderer: column.renderer
              });
            }
          });
        }

        if (configuration.exportTypes.indexOf('_column') > -1) {
          $.each(configuration.columns, function (index, column) {
            if (column.hidden == '0') {
              exportColumns.push({
                columnIndex: column.name,
                title: column.header,
                renderer: column.renderer
              });
            }
          });
        } else {
          $.each(configuration.exportColumns, function (index, column) {
            var columnIndex = column.name;
            var hasRender = false;
            if (column.renderer && StringUtils.isNotBlank(column.renderer.rendererType)) {
              columnIndex = columnIndex; // + "RenderValue";
              hasRender = true;
            }
            var pushCol = true;
            if (configuration.exportTypes == 'templateExcel' && hasRender) {
              //如果是按模板excel导出，则导出字段定义的渲染器可以覆盖列定义的字段渲染器
              for (var i = 0, len = exportColumns.length; i < len; i++) {
                if (exportColumns[i].columnIndex == columnIndex && column.renderer) {
                  exportColumns[i].renderer = column.renderer;
                  exportColumns[i].title = column.header;
                  pushCol = false;
                  break;
                }
              }
            }
            if (pushCol) {
              exportColumns.push({
                columnIndex: columnIndex,
                title: column.header,
                renderer: column.renderer
              });
            }
          });
        }
      }
      return exportColumns;
    },
    /**
     * 注册按钮事件，调用相关二开接口
     */
    _setEvent: function () {
      var _self = this;
      var options = _self.options;
      var $contextElement = $(_self.element);
      var $table = $('#' + options.widgetDefinition.id);
      var configuration = _self.getConfiguration();
      $('.div_lineEnd_toolbar').parent().css('overflow', 'visible');
      $('tr').hover(function () {
        var $this = $(this);
        var _index = $this.data('index');
        $this
          .parents('.bootstrap-table')
          .find('tr')
          .each(function () {
            if ($(this).data('index') === _index) {
              $(this).addClass('hover');
            } else {
              $(this).removeClass('hover');
            }
          });
      });

      $.each(configuration.buttons, function (index, button) {
        var buttonSeletor = (StringUtils.isBlank(button.group) ? '.btn_class_' : '.li_class_') + button.code;
        //自定义脚本函数处理
        if (!$.isEmptyObject(button.defineEventJs)) {
          for (var k in button.defineEventJs) {
            var bindDefineEvent = function (eventName) {
              $table.on(eventName, buttonSeletor, function (event) {
                var $toolbarDiv = $(event.target).closest('div');
                var rowData = [];
                if ($toolbarDiv.is('.div_lineEnd_toolbar') || $toolbarDiv.is('.underline-btn')) {
                  var index = $toolbarDiv.attr('index');
                  var allData = _self.$tableElement.bootstrapTable('getData');
                  rowData = [allData[index]];
                } else if ($toolbarDiv.is('.table-float-btn')) {
                  var index = $toolbarDiv.closest('tr').data('index');
                  var allData = _self.$tableElement.bootstrapTable('getData');
                  rowData = [allData[index]];
                } else {
                  rowData = _self.getSelections();
                }
                var defineFunc = appContext.eval(
                  button.defineEventJs[eventName],
                  $(this),
                  {
                    $this: _self,
                    event: event,
                    commons: commons,
                    server: server,
                    Api: new Api(_self),
                    rowData: rowData
                  },
                  function (v) {
                    if (v === false) {
                      event.preventDefault();
                      event.stopImmediatePropagation();
                    }
                  }
                );
                console.log('执行按钮脚本：', defineFunc);
              });
              if (eventName === 'init') {
                //立即触发初始化事件
                $(buttonSeletor, $contextElement).trigger(eventName);
              }
            };

            bindDefineEvent(k);
          }
        }

        if (button.eventHandler && button.eventHandler.id && button.eventHandler.path) {
          var eventHandler = button.eventHandler;
          var eventParams = button.eventParams || {};
          var target = button.target || {};
          $contextElement.on('click', buttonSeletor, function (event) {
            event.stopPropagation();
            _self.readMarkerAdd(event);

            if (_self.getConfiguration().clickToSelect) {
              var $tr = $(this).parents('tr').first();
              $tr.find("td:eq('0')").trigger('click');
            }

            //按钮事件显示位置为导航tab时获取导航tab的widgetId
            function getNavTabWidget(appContext, curWindow) {
              var widgetMap = appContext.widgetMap;
              var hasWidget = false;
              $.each(widgetMap, function (i) {
                if (i.indexOf('wNavTab_') > -1) {
                  opt.targetWidgetId = i;
                  opt.renderNavTab = true;
                  opt.appContext = appContext;
                  hasWidget = true;
                  return false;
                }
              });
              if (!hasWidget) {
                getNavTabWidget(curWindow.parent.appContext, curWindow.parent);
              }
            }

            var opt = {
              target: target.position,
              targetWidgetId: target.widgetId,
              refreshIfExists: target.refreshIfExists,
              eventTarget: target,
              event: event,
              appId: eventHandler.id,
              appType: eventHandler.type,
              appPath: eventHandler.path,
              params: $.extend({}, eventParams.params, appContext.parseEventHashParams(eventHandler, 'menuid')),
              button: $.extend({}, button),
              view: _self,
              appData: appContext.getCurrentUserAppData().appData,
              viewOptions: options
            };
            var $toolbarDiv = $(event.target).closest('div');
            if ($toolbarDiv.is('.div_lineEnd_toolbar') || $toolbarDiv.is('.underline-btn')) {
              var index = $toolbarDiv.attr('index');
              var allData = _self.$tableElement.bootstrapTable('getData');
              opt.rowData = [allData[index]];
            } else if ($toolbarDiv.is('.btn-group') && $toolbarDiv.parent().is('.div_lineEnd_toolbar')) {
              // 行未更多按钮
              var index = $toolbarDiv.parent().attr('index');
              var allData = _self.$tableElement.bootstrapTable('getData');
              opt.rowData = [allData[index]];
            } else {
              opt.rowData = _self.getSelections();
            }
            if (opt.target === '_navTab') {
              getNavTabWidget(appContext, window);
              opt.buttonEvent = true;
              if (window !== top) {
                var allIframe = $(top.document).find('iframe');
                allIframe.each(function () {
                  var $this = $(this);
                  if ($this[0].src === location.href) {
                    opt.pNavTabId = $this.parent().attr('id');
                    return false;
                  }
                });
              } else {
                opt.pNavTabId = _self.element.closest('.well-nav-tab').attr('id');
              }

              opt.buttonEventId = 'buttonEventId_' + _self.options.widgetDefinition.id;
              opt.text = _self.getConfiguration().name + '-' + button.text;
            }
            if (eventHandler && eventHandler.id == '462c8476b78918fc342c8b087b925c1f') {
              MarkerRead.defaultEvent.apply(_self);
            } else if (eventHandler && eventHandler.id == '400305480a9b70401461b9c5096889d6') {
              MarkerUnRead.defaultEvent.apply(_self);
            } else {
              // if (configuration.readStatus && configuration.readStatus == '1') {
              //   opt.extra = {
              //     tagDataKey: configuration.tagDataKey
              //   };
              // }
              if (opt.appPath.indexOf('formDesigner') > -1 && opt.params && !opt.params.tableBtnEditTag) {
                // 针对表单新增
                opt.useUniqueName = false;
              }
              _self.startApp(opt);
            }
          });
        } else {
          $contextElement.on('click', buttonSeletor, function (event) {
            event.stopPropagation();
            _self.readMarkerAdd(event);

            var $toolbarDiv = $(event.target).closest('div');
            // 更多下拉选项中的按钮点击事件
            if ($(event.currentTarget).is('li')) {
              $toolbarDiv = $(event.currentTarget).closest('.div_lineEnd_toolbar');
              if ($toolbarDiv.length == 0) {
                $toolbarDiv = $(event.currentTarget).closest('.underline-btn');
              }
            }
            var eventParams = button.eventParams || {};
            var opt = {
              ui: _self,
              view: _self,
              viewOptions: options
            };
            opt.params = appContext.resolveParams(eventParams.params, opt);
            var args = [event, opt];
            if ($toolbarDiv.is('.div_lineEnd_toolbar') || $toolbarDiv.is('.underline-btn')) {
              var index = $toolbarDiv.attr('index');
              var allData = _self.$tableElement.bootstrapTable('getData');
              var originalRowCheckItem = !!allData[index][rowCheckItem];
              allData[index][rowCheckItem] = true; // 标记数据为选中状态，兼容旧的二开脚本会获取选中行数据
              args.push(allData[index]);
              window.setTimeout(function () {
                allData[index][rowCheckItem] = originalRowCheckItem;
              }, 100);
            }
            if (_self.getConfiguration().clickToSelect) {
              var $tr = $toolbarDiv.parents('tr').first();
              $tr.find("td:eq('0')").trigger('click');
            }

            _self.invokeDevelopmentMethod(button.code, args);
          });
        }
      });

      _self.on('dmsEventHanlderActionDone', function (e) {
        var detail = e.detail;
        // 处理数据管理操作完成的代码块
        if (detail && detail.invokeResult && detail.invokeResult.success && detail.options.ui) {
          if (detail.options.data.action) {
            $.each(configuration.buttons, function (index, button) {
              if (button.eventHandler && button.eventHandler.id) {
                var paths = button.eventHandler.path.split('/');
                if (paths[paths.length - 1] == detail.options.data.action.id) {
                  appContext.eval(button.eventHandler['afterEventHandlerCodes'], $(_self), {
                    $this: _self,
                    commons: commons,
                    server: server,
                    Api: new Api($(_self))
                  });

                  return false;
                }
              }
            });
          }
        }
      });

      //监听表格刷新动作：触发执行二开的刷新事件
      _self.on(constant.WIDGET_EVENT.Refresh, function (e) {
        _self.invokeDevelopmentMethod('onRefresh', [e.details]);
      });

      _self.element.off('click', '.bs-checkbox input + label').on('click', '.bs-checkbox input + label', function (e) {
        e.stopPropagation();
        var $this = $(this);
        var isFixedColumn = $this.closest('.fixed-table-body-columns.fixed-table-body').length > 0;
        if (!isFixedColumn) {
          return;
        }

        var checked = $(e.target).prev().prop('checked');
        if ($this.closest('th').length) {
          // 点击th里的复选框
          _self.$tableElement.bootstrapTable('checkAll');

          if (checked) {
            _self.element.find('.fixed-table-body-columns.fixed-table-body .bs-checkbox input').removeProp('checked');
          } else {
            _self.element.find('.fixed-table-body-columns.fixed-table-body .bs-checkbox input').prop('checked', 'checked');
          }
        } else {
          // 点击td里的复选框
          var checkedItemsCount = _self.element.find('.fixed-table-body-columns.fixed-table-body td.bs-checkbox input:checked').length;
          var uncheckedItemsCount = _self.element.find(
            '.fixed-table-body-columns.fixed-table-body td.bs-checkbox input:not(:checked)'
          ).length;

          if (checkedItemsCount > 0 && uncheckedItemsCount === 0) {
            _self.element.find('.fixed-table-body-columns.fixed-table-body th.bs-checkbox input').prop('checked', 'checked');
          } else {
            _self.element.find('.fixed-table-body-columns.fixed-table-body th.bs-checkbox input').removeProp('checked');
          }
          // 解决固定列开启选中无效问题
          // var trIndex = $(e.target).parents('tr').data('index');
          // _self.element
          //   .find('.fixed-table-body')
          //   .first()
          //   .find('tbody tr:eq(' + trIndex + ') td.bs-checkbox input + label')
          //   .trigger('click');
        }
      });

      $(document).on('click', function (e) {
        if (!$(e.target).closest('.bt-custom-columns-wrapper').length) {
          _self.closeCustomColumnPopup();
        }
      });
    },
    /**
     * 获取JS模块
     */
    getDevelopmentModules: function () {
      var _self = this;
      var jsModule = _self.getConfiguration().jsModule;
      if (StringUtils.isBlank(jsModule)) {
        _self.devJsModules = [];
      } else {
        _self.devJsModules = jsModule.split(constant.Separator.Semicolon);
      }
      return _self.devJsModules;
    },

    getSelect2OptionValue: function (options) {
      var select2 = {};
      if (options.optionType != 3) {
        select2.defaultBlank = false;
        select2.data = this._getFieldOptionValue(options);
      } else {
        select2.serviceName = 'select2DataStoreQueryService';
        select2.queryMethod = 'loadSelectData';
        select2.selectionMethod = 'loadSelectDataByIds';
        select2.remoteSearch = true;
        select2.defaultBlank = true;
        select2.defaultBlankText = '';
        select2.multiple = true;
        select2.params = {
          dataStoreId: options.dataStore,
          idColumnIndex: options.valueColumn,
          textColumnIndex: options.textColumn
        };
      }
      return select2;
    },
    /**
     * 解析条件查询中配置下拉框数据来源
     */
    _getFieldOptionValue: function (options) {
      var optionValue = [];
      switch (options.optionType) {
        case '1':
          return options.optionValue;
        case '2':
          server.JDS.call({
            service: 'dataDictionaryMaintain.getDataDictionariesByParentUuid',
            data: [options.dataDic],
            async: false,
            success: function (result) {
              if (result.msg == 'success') {
                optionValue = $.map(result.data, function (data) {
                  return {
                    id: data.code,
                    text: data.name
                  };
                });
              }
            }
          });
          return optionValue;
        case '3':
          server.JDS.call({
            service: 'viewComponentService.loadAllData',
            data: [options.dataStore],
            async: false,
            success: function (result) {
              if (result.msg == 'success') {
                optionValue = $.map(result.data.data, function (data) {
                  return {
                    id: data[options.valueColumn],
                    text: data[options.textColumn]
                  };
                });
              }
            }
          });
          return optionValue;
        case '4':
          return optionValue;
        default:
          return optionValue;
      }
    },
    /**
     * 获取控件真实高度
     */
    _getRealHeight: function ($el) {
      var height = 0;
      if ($el && $el.is(':visible ')) {
        height = $el.outerHeight(true);
        $el.children().each(function () {
          if (height < $(this).outerHeight(true)) {
            height = $(this).outerHeight(true);
          }
        });
      }
      return height;
    },
    /**
     * 获取某个列的字段类型
     */
    _getFieldDataType: function (columnIndex) {
      var configuration = this.getConfiguration();
      var dataType = '';
      $.each(configuration.columns, function (index, column) {
        if (columnIndex == column.name) {
          dataType = column.dataType;
          return false;
        }
      });
      return dataType;
    },

    /**
     * 固定列方法
     */
    fixedColumns: function () {
      this.$tableElement.bootstrapTable('fixedColumns'); //固定列方法，bootstrap-table-fixed-columns.js
    },

    isFixedColumns: function () {
      var flag = true;
      var configuration = this.getConfiguration();
      //var customConfig = this.getCustomConfiguration(this.options.widgetDefinition.id,false);

      if (!configuration.fixedColumns && !configuration.showColumns) {
        flag = false;
      }
      return flag;
      // return configuration.fixedColumns || (customConfig && customConfig.fixedColumns);
    },

    /**
     * 树形列表切换
     */
    toggleTreeView: function () {
      this.$tableElement.bootstrapTable('getOptions').postBody2TreeView = true; //在post-body.bs.table事件处理时候，避免重复加载树形
      this.$tableElement.bootstrapTable('toggleTreeView'); //切换树形列表方法，bootstrap-table-treegrid.js
      var $treeIcon = $(this.element).find('.fixed-table-toolbar').find("button[name='toggleTreeView']");
      if ($treeIcon.length > 0) {
        $treeIcon.trigger('treeIconChange');
      }
    },

    /**
     * 展开树节点
     * @param dataIndex null或者undefined 则展开所有树节点
     */
    expandTreeView: function (dataIndex) {
      this.$tableElement.bootstrapTable('expandTreeView', dataIndex);
    },
    /**
     * 折叠树节点
     * @param dataIndex null或者undefined 则折叠所有树节点
     */
    collapseTreeView: function (dataIndex) {
      this.$tableElement.bootstrapTable('collapseTreeView', dataIndex);
    },

    markReadStatus: function (rows, $tableElement) {
      var configuration = this.getConfiguration();
      if (configuration.readStatus == '1') {
        var uuids = [];
        $.each(rows, function (index, item) {
          uuids.push(item[configuration.tagDataKey]);
        });
        $.ajax({
          type: 'POST',
          url: ctx + '/api/readMarker/unReadList',
          dataType: 'json',
          data: {
            tagDataKeys: uuids.join(',')
          },
          success: function (result) {
            if (result.msg == '成功') {
              $.each(rows, function (i, row) {
                if (result.data.indexOf(row[configuration.tagDataKey]) > -1) {
                  $tableElement
                    .find('tbody tr:eq(' + i + ')')
                    .find('td')
                    .css({
                      fontWeight: 'bold'
                    });
                }
              });
            }
          }
        });
      }
    },
    /**
     * 自动刷新功能
     * autoRefresh true：自动刷新 false：不刷新
     * autoRefreshTime 刷新频率（单位：秒）
     */
    _createAutoRefreshInterval: function () {
      var _self = this;
      var configuration = _self.getConfiguration();
      var intervalName = _self.widget().attr('id') + '_' + 'autoRefreshInterval'; //循环名称 = 表单id + autoRefreshInterval
      if (configuration.autoRefresh) {
        //启用列表自动刷新
        if (_self[intervalName]) {
          _self._distoryAutoRefreshInterval();
        }
        // console.error("启用循环刷新:" + intervalName + "__" + _self.widget().height());
        //组件高度为0时，组件被隐藏，不再循环刷新
        if (_self.widget().height()) {
          var autoRefreshTime = parseInt(configuration.autoRefreshTime) * 1000; //循环时间
          _self[intervalName] = setInterval(_self._autoRrfreshFunction, autoRefreshTime, _self); //启用循环
        }
      }
    },
    /**
     * 销毁定时循环器
     */
    _distoryAutoRefreshInterval: function () {
      var _self = this;
      var intervalName = _self.widget().attr('id') + '_' + 'autoRefreshInterval'; //循环名称 = 表单id + autoRefreshInterval
      if (_self[intervalName]) {
        clearInterval(_self[intervalName]); //停用循环
        _self[intervalName] = null;
      }
    },
    /**
     * 自动刷新内容
     */
    _autoRrfreshFunction: function (_self) {
      _self._distoryAutoRefreshInterval();
      // console.error(intervalName + "自动刷新内容");
      _self.refresh();
    }
  });
});
