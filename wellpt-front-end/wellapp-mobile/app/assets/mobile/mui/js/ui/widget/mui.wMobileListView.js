(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define([
      'mui',
      'commons',
      'constant',
      'mui-wWidget',
      'dataStoreBase',
      'MobileListDevelopmentBase',
      'appModal',
      'formBuilder',
      'server',
      'templateEngine'
    ], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($, commons, constant, widget, DataStore, DevelopmentBase, appModal, formBuilder, server, templateEngine) {
  'use strict';
  var StringUtils = commons.StringUtils;
  var customEvent = function (data) {
    this.isPreventDefault = false;
    this.data = data;
  };
  customEvent.prototype.preventDefault = function () {
    this.isPreventDefault = true;
  };
  $.widget('mui.wMobileListView', $.ui.wWidget, {
    options: {
      // 组件定义
      widgetDefinition: {
        configuration: {}
      },
      // 上级容器定义
      containerDefinition: {},
      jsModules: [],
      data: [],
      otherOrders: {},
      otherConditions: []
    },
    /**
     * 获取配置对象
     */
    getConfiguration: function () {
      return this.options.widgetDefinition.configuration;
    },
    /**
     * 获取配置对象
     */
    getId: function () {
      return this.options.widgetDefinition.id;
    },
    /**
     * 获取数据源ID
     */
    getDataStoreId: function () {
      return this.getConfiguration().dataStoreId;
    },
    _createView: function () {
      // 生成页面组件
      var _self = this;
      var options = _self.options;
      var configuration = _self.getConfiguration();
      var multiSelect = configuration.multiSelect;
      _self._dataProvider = new DataStore({
        type: configuration.type,
        dataStoreId: _self.getDataStoreId(),
        onDataChange: function (data, count, params) {
          _self.onDataChange(params);
        },
        renderers: _self.getRenderers(),
        defaultOrders: _self.getDefaultSort(),
        defaultCriterions: _self.getDefaultConditions(),
        receiver: _self,
        autoCount: false,
        pageSize: configuration.pageSize || 20
      });

      _self._executeJsModule('beforeRender', [_self.options, _self.getConfiguration()]);
      _self._renderView();
      _self._executeJsModule('afterRender', [_self.options, _self.getConfiguration()]);
    },
    /**
     * 获取数据源对象
     */
    getDataProvider: function () {
      return this._dataProvider;
    },
    /**
     * 通知数据源回调根据条件加载数据
     */
    load: function (data) {
      var self = this;
      var criterions = self._collectCriterion();
      var keyword = self._collectSearchWord();

      var options = self.options;
      var otherConditions = options.otherConditions || [];
      if (otherConditions.length) {
        $.each(otherConditions, function (i, cond) {
          criterions.push(cond);
        });
      }
      var orders = [];
      var otherOrders = options.otherOrders || {};
      $.each(otherOrders, function (sortName, sortOrder) {
        orders.push({
          sortName: sortName,
          sortOrder: sortOrder
        });
      });

      self.getDataProvider().load(
        {
          pagination: {
            currentPage: 1
          },
          orders: orders,
          keyword: keyword,
          criterions: criterions
        },
        data
      );
    },
    /**
     * 刷新
     */
    refresh: function () {
      this.load(true);
    },
    /**
     * 通知数据变更
     */
    onDataChange: function (refresh) {
      var self = this;
      var configuration = self.getConfiguration();
      var pageSize = configuration.pageSize || 20;
      var data = self.getDataProvider().getData();
      var StringBuilder = commons.StringBuilder;
      var sb = new StringBuilder();
      var table = self.element[0].querySelector('.mui-table-view');
      self.updateUnreadMarker(data);
      var buttons = configuration.buttons || [];
      var liButtons = buttons.filter(function (n) {
        return $.inArray('3', n.position) > -1;
      });
      if (refresh) {
        table.innerHTML = '';
        this.options.data = [];
      }
      for (var i in data) {
        var index = self.options.data.length;
        var d = data[i];
        self.options.data.push(d);
        var itemHtml = configuration.templateHtml;
        $.each(configuration.templateProperties, function (index, property) {
          var re = eval('/\\{' + property.name + '\\}/mg;');
          var value = d[property.mapColumn];
          if (property.renderer && StringUtils.isNotBlank(property.renderer.rendererType)) {
            value = d[property.mapColumn + 'RenderValue'];
          }
          itemHtml = itemHtml.replace(re, value || '');
        });

        itemHtml = templateEngine(itemHtml, d);

        var li = document.createElement('li');
        if (self.isUnread(d)) {
          li.classList.add('unread');
        }
        if (liButtons.length !== 0) {
          li.classList.add('mui-table-view-cell');
          li.classList.add('mui-transitioning');
          li.setAttribute('data-index', index);
          var btns = document.createElement('div');
          btns.className = 'mui-slider-right mui-disabled';
          for (var j = 0; j < liButtons.length; j++) {
            var btn = document.createElement('a');
            if (!liButtons[j].cssClass) {
              liButtons[j].cssClass = 'btn-grey';
            }
            btn.className = 'mui-btn mui-icon mui-' + liButtons[j].cssClass;
            btn.innerHTML = liButtons[j].text;
            btn.setAttribute('name', liButtons[j].code);
            btns.appendChild(btn);
          }
          var contentWrapper = document.createElement('div');
          contentWrapper.className = 'mui-slider-handle';
          contentWrapper.innerHTML = itemHtml;
          var checkbox = self._createCheckbox(index);
          contentWrapper.insertBefore(checkbox, contentWrapper.childNodes[0]);
          li.appendChild(btns);
          li.appendChild(contentWrapper);
          $(li).on('tap', '.mui-btn', function (event) {
            var name = this.getAttribute('name');
            event.stopPropagation();
            self.buttonAction(name, event, '3');
            setTimeout(function () {
              $.swipeoutClose($(event.target).closest('li.mui-table-view-cell'));
            }, 0);
          });
        } else {
          li.classList.add('mui-table-view-cell');
          li.setAttribute('data-index', index);
          var contentWrapper = document.createElement('div');
          contentWrapper.className = 'mui-slider-handle';
          contentWrapper.innerHTML = itemHtml;
          var checkbox = self._createCheckbox(index);
          contentWrapper.insertBefore(checkbox, contentWrapper.childNodes[0]);
          li.appendChild(contentWrapper);
        }
        table.appendChild(li);
        self._executeJsModule('onAddLiElement', [index, d, li]);
      }
      if (table.childElementCount == 0) {
        var contentHeight = $('.mui-content.mui-scroll-wrapper.table-view-container')[0].offsetHeight;
        var emptyPaddingHeight = (contentHeight - 120) / 2;
        mui('.mui-content.mui-scroll-wrapper.table-view-container')[0].style.background = '#fff';
        document.styleSheets[0].addRule('.mui-table-view:before', 'background-color: #fff');
        document.styleSheets[0].addRule('.mui-table-view:after', 'background-color: #fff');
        var $empty =
          '<li><div style="text-align: center;padding-top:' +
          emptyPaddingHeight +
          'px;"><img src="/static/mobile/mui/images/empty.png" style="margin: auto;"><div style="color:#666;">无匹配数据!</div></div></li>';
        $(table).append($empty);
      }
      if (refresh) {
        self.refreshContainer.pullRefresh().endPulldownToRefresh();
      } else {
        self.refreshContainer.pullRefresh().endPullupToRefresh(false);
      }
      appModal.hideMask();
    },

    _createCheckbox: function (index) {
      var checkboxWrapper = document.createElement('div');
      checkboxWrapper.className = 'mui-checkbox';
      var checkbox = document.createElement('input');
      checkbox.setAttribute('name', 'itemCheckbox');
      checkbox.setAttribute('type', 'checkbox');
      checkbox.setAttribute('data-index', index);
      checkbox.classList.add('input-check-item');
      checkboxWrapper.appendChild(checkbox);
      return checkboxWrapper;
    },
    _createRadio: function (index) {
      var radioWrapper = document.createElement('dv');
      radioWrapper.className = 'mui-radio';
      var radio = document.createElement('input');
      radio.setAttribute('name', 'itemRadio');
      radio.setAttribute('type', 'radio');
      radio.setAttribute('data-index', index);
      radio.classList.add('input-check-item');
      radioWrapper.appendChild(checkbox);
      return radioWrapper;
    },

    _classSelector: function (className) {
      var id = this.getId();
      return '#' + id + ' ' + className;
    },
    _mui: function (className) {
      var self = this;
      return mui(className, self.element[0]);
    },
    _renderView: function () {
      // 生成页面组件
      var _self = this;
      var options = this.options;
      var configuration = this.getConfiguration();
      var multiSelect = configuration.multiSelect;
      /*
       * this._dataProvider = new DataStore({ type : configuration.type,
       * dataStoreId : this.getDataStoreId(), onDataChange :
       * function(data, count, params) { _self.onDataChange(params); },
       * renderers : _self.getRenderers(), defaultOrders :
       * _self.getDefaultSort(), defaultCriterions :
       * _self.getDefaultConditions(), receiver : this, autoCount : false,
       * pageSize : configuration.pageSize || 20 });
       */

      var StringBuilder = commons.StringBuilder;
      var sb = new StringBuilder();
      if (configuration.hasSearch) {
        // 添加编辑按钮
        if (configuration.editable) {
          sb.append('<form class="mui-input-row mui-search list-editor" action="" onsubmit="return false;">');
          sb.append(" <button type='button' class='mui-btn mui-list-editable'>编辑</button>");
          sb.append('	<input type="search" class="mui-input-clear " placeholder="搜索">');
        } else {
          sb.append('<form class="mui-input-row mui-search" action="" onsubmit="return false;">');
          sb.append('	<input type="search" class="mui-input-clear " placeholder="搜索">');
        }
        sb.append('</form>');
      }
      sb.append('<div  class="mui-content mui-scroll-wrapper table-view-container">');
      sb.append('	<div class="mui-scroll">');
      sb.append('	 <ul class="mui-table-view mui-table-view-striped mui-table-view-condensed table-view-content" ');
      sb.append('	 </ul>');
      sb.append('	</div>');
      sb.append('</div>');
      _self.element[0].innerHTML = sb.toString();
      _self._mui('.mui-input-row').on('tap', '.mui-list-editable', function (event) {
        _self._editList(event);
        // _self._renderFieldSearch();
      });
      if (_self.isShowActionSheel()) {
        _self._createActionSheel();
      }
      _self.refreshContainer = _self._mui('.table-view-container');
      _self.searchInput = _self._mui('.mui-input-row input');
      _self.searchInput.input();

      _self.refreshContainer
        .pullRefresh({
          down: {
            callback: function () {
              var event = new customEvent();
              _self._executeJsModule('onPulldown', [event]);
              if (event.isPreventDefault) {
                _self.refreshContainer.pullRefresh().endPulldownToRefresh();
                return;
              }
              appModal.showMask('');
              _self.load(true);
            }
          },
          up: {
            contentrefresh: '正在加载...',
            callback: function () {
              var event = new customEvent();
              _self._executeJsModule('onPullup', [event]);
              if (event.isPreventDefault) {
                _self.refreshContainer.pullRefresh().endPullupToRefresh();
                return;
              }
              appModal.showMask('');
              _self.getDataProvider().nextPage(false);
            }
          }
        })
        .pulldownLoading();

      var header = document.querySelector('.panel:not(.mui-hidden)>header.mui-bar');
      var headerHeight = header.offsetHeight;
      var offsetParent = _self.element.offsetParent();
      if (offsetParent && offsetParent.contains && offsetParent.contains(header) === false) {
        headerHeight = 0;
      }
      if (configuration.hasSearch) {
        var searchDiv = _self.element[0].querySelector('.mui-input-row');
        var height = headerHeight + searchDiv.offsetHeight + 1;
        _self.refreshContainer[0].style.top = height + 'px';
        var searchFn = function (event) {
          var text = this.value || '';
          _self.searchWork = _self.searchWork || '';
          if (_self.searchWork != text) {
            _self.searchWork = text;
            _self.refreshContainer.pullRefresh().pulldownLoading();
          }
        };
        if ($.os && $.os.ios) {
          setTimeout(function () {
            $.trigger(_self.searchInput[0], 'click');
          }, 100);
        }
        _self.searchInput[0].addEventListener('blur', searchFn, false);
        _self.searchInput[0].addEventListener('search', searchFn, false);
        _self._mui('.mui-search').on('tap', '.mui-icon-clear', searchFn, false);
      } else {
        _self.refreshContainer[0].style.top = headerHeight + 'px';
      }

      _self.refreshContainer.on(
        'tap',
        '.mui-table-view-cell',
        function (event) {
          if (_self.editable && event.target) {
            var target = event.target;
            if (target.classList.contains('input-check-item')) {
              return; // target.checked
            }
            var tableCell = $(target).closest('.mui-table-view-cell[data-index]');
            // var dataIndex = tableCell.getAttribute("data-index");
            // var rowData = _self.getData(dataIndex);
            var checkItem = $('input.input-check-item', tableCell)[0];
            return (checkItem.checked = !checkItem.checked);
          } else {
            var index = this.getAttribute('data-index');
            var data = _self.getData(index);
            _self._executeJsModule('onClickRow', [index, data, this, event]);
          }
        },
        false
      );
    },
    /**
     * 获取列渲染器
     */
    getRenderers: function () {
      var renderers = [];
      var configuration = this.getConfiguration();
      $.each(configuration.templateProperties, function (index, property) {
        if (property.renderer && StringUtils.isNotBlank(property.renderer.rendererType)) {
          renderers.push({
            columnIndex: property.mapColumn,
            param: property.renderer
          });
        }
      });
      return renderers;
    },
    /**
     * 获取默认排序信息
     */
    getDefaultSort: function () {
      var defaultSort = [];
      var configuration = this.getConfiguration();
      $.each(configuration.templateProperties, function (index, property) {
        if (property.sortOrder == 'asc' || property.sortOrder == 'desc') {
          defaultSort.push({
            sortName: property.mapColumn,
            sortOrder: property.sortOrder
          });
        }
      });
      return defaultSort;
    },
    /**
     * 获取默认条件查询
     */
    getDefaultConditions: function () {
      var defaultConditions = [];
      var configuration = this.getConfiguration();
      if (StringUtils.isNotBlank(configuration.defaultCondition)) {
        var criterion = {};
        criterion.sql = configuration.defaultCondition;
        defaultConditions.push(criterion);
      }
      return defaultConditions;
    },
    /**
     * 收集查询条件
     */
    _collectCriterion: function () {
      var options = this.options;
      var _self = this;
      var configuration = this.getConfiguration();
      var criterions = [];
      if (configuration.hasSearch && _self.searchWork) {
        var orcriterion = {
          conditions: [],
          type: 'or'
        };
        $.each(configuration.templateProperties, function (index, property) {
          orcriterion.conditions.push({
            columnIndex: property.mapColumn,
            value: _self.searchWork,
            type: 'like'
          });
        });
        criterions.push(orcriterion);
      }
      return criterions;
    },
    _collectSearchWord: function () {
      return this.searchWork;
    },
    /**
     * 将JS模块从集合移除
     */
    _removeJsModlue: function (jsModule) {
      var devJsModules = this._getJsModules();
      var index = devJsModules.indexOf(jsModule);
      if (index > -1) {
        devJsModules.splice(index, 1);
      }
    },
    /**
     * 判断当前行数据已读未读状态
     */
    isUnread: function (row) {
      var self = this;
      var configuration = self.getConfiguration();
      if (configuration.readMarker) {
        if ($.inArray(row[configuration.readMarkerField], self.unreadUuids || []) != -1) {
          return true;
        }
      }
      return false;
    },
    /**
     * 根据当前页的数据刷新当前页的未读信息
     */
    updateUnreadMarker: function (data) {
      var _self = this;
      var configuration = _self.getConfiguration();
      if (configuration.readMarker && data.length && StringUtils.isNotBlank(configuration.readMarkerField)) {
        var uuids = data.map(function (row) {
          return row[configuration.readMarkerField];
        });
        server.JDS.call({
          service: 'viewComponentService.filterUnread',
          data: [uuids],
          async: false,
          success: function (result) {
            if (result.msg == 'success') {
              _self.unreadUuids = result.data;
            }
          }
        });
      }
    },
    /**
     * 根据索引获取索引位置的列数据
     */
    getData: function (index) {
      var datas = this.options.data;
      if (index === null || typeof index === 'undefined') {
        return datas;
      }
      return datas[index];
    },
    /**
     * 根据索引获取索引位置的列数据
     */
    getDatas: function (indexs) {
      var datas = this.options.data;
      if ($.isArray(indexs)) {
        var selected = [];
        $(indexs).each(function (i, idx) {
          selected.push(datas[idx]);
        });
        return selected;
      }
      return datas;
    },
    /**
     * 获取选中列数据集合
     */
    getSelections: function () {
      var self = this;
      var selectionIndexes = self.getSelectionIndexes();
      return self.getDatas(selectionIndexes);
    },
    /**
     * 获取选中列索引集合
     */
    getSelectionIndexes: function () {
      var self = this;
      var checked = [];
      var checkedSelector = '.table-view-content input.input-check-item:checked';
      var checkItems = $(checkedSelector, self.refreshContainer[0]) || [];
      checkItems.each(function (index, elem) {
        checked.push(elem.getAttribute('data-index'));
      });
      return checked;
    },
    /**
     * 根据列li对象返回索引属性
     */
    getIndexByLi: function (li) {
      return li.getAttribute('data-index');
    },
    /**
     * 根据索引获取索引位置的li对象
     */
    getLiElement: function (index) {
      var liList = this._mui('.mui-table-view .mui-table-view-cell');
      if (index === null || typeof index === 'undefined') {
        return liList;
      }
      return liList[index];
    },
    /**
     * 根据过滤函数获取列表数据 filter参数 li对象 data列数据 index列索引 返回数据格式 { li:li对象,
     * data:列数据,index:列索引} }
     */
    getRowDatas: function (filter) {
      var liList = this._mui('.mui-table-view .mui-table-view-cell');
      var result = [];
      for (var i = 0; i < liList.length; i++) {
        var li = liList[i];
        var index = li.getAttribute('data-index');
        var data = this.options.data[index];
        if (filter) {
          if (!filter(li, data, index)) {
            continue;
          }
        }
        result.push({
          li: li,
          index: index,
          data: data
        });
      }
      return result;
    },
    /**
     * 设置上拉加载的状态 type = done设置上拉不再加载（没有更多数据） type=reset 将上拉状态重新设置为可以加载
     */
    setPullupStatus: function (type) {
      if (type == 'reset') {
        this.refreshContainer.pullRefresh().refresh(true);
      } else if ((type = 'done')) {
        this.refreshContainer.pullRefresh().endPullupToRefresh(true);
      }
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
      var options = _self.options;
      var otherOrders = options.otherOrders;
      otherOrders[sortName] = sortOrder;
    },
    /**
     * 情况额外查询排序,sortName为空是清楚全部，否则清楚等于sortName的一条额外查询排序
     */
    clearOtherOrders: function (sortName) {
      var _self = this;
      var options = _self.options;
      var otherOrders = options.otherOrders;
      if (sortName && otherOrders) {
        delete otherOrders[sortName];
      } else {
        _self.otherOrders = {};
      }
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
     * 添加额外的查询条件
     */
    addOtherConditions: function (conditions) {
      var _self = this;
      var options = _self.options;
      var otherConditions = options.otherConditions;
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
                optionValue = result.data.map(function (data) {
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
                optionValue = result.data.data.map(function (data) {
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
     * 高级查询
     */
    _renderFieldSearch: function () {
      var _self = this;
      // TODO remove offsetParent
      var $element = _self.element[0].offsetParent;
      var searchPanel = document.createElement('div');
      searchPanel.className = 'mui-search-panel';
      $element.appendChild(searchPanel);
      var configuration = _self.getConfiguration();
      if (configuration.query.fields) {
        var $fieldSearchElement = document.createElement('div');
        $fieldSearchElement.classList.add('div_search_panel');
        var fieldOptios = {
          container: $fieldSearchElement,
          inputClass: 'w-search-option',
          labelColSpan: '3',
          controlColSpan: '9',
          contentItems: []
        };
        $.each(configuration.query.fields, function (i, field) {
          var queryOption = field.queryOptions;
          var queryType = queryOption.queryType;
          var itemOptions = {
            label: field.label,
            name: field.name,
            type: queryType,
            value: field.defaultValue
          };
          if (queryType == 'date') {
            if (field.operator == 'between') {
              itemOptions.beginName = field.name + '_Begin';
              itemOptions.endName = field.name + '_End';
              itemOptions.beginValue = field.defaultValue;
              itemOptions.endValue = field.defaultValue;
              itemOptions.type = 'timeInterval';
            }
            itemOptions.timePicker = {
              format: queryOption.format
            };
          } else if (queryType == 'select2') {
            itemOptions.select2 = _self.getSelect2OptionValue(queryOption);
            // 是否多选
            itemOptions.select2.multiple = queryOption.multiple === '1';
            // 常量默认值
            if (queryOption.optionType == '1') {
              $.each(itemOptions.select2.data, function () {
                if (StringUtils.isBlank(this.id)) {
                  var emptyId = commons.UUID.createUUID();
                  this.id = emptyId;
                  itemOptions.value = emptyId;
                  // 缓存空值
                  _self.fieldSearchEmptyValueMap = _self.fieldSearchEmptyValueMap || {};
                  _self.fieldSearchEmptyValueMap[this.id] = this.id;
                } else {
                  if (_self.fieldSearchEmptyValueMap && _self.fieldSearchEmptyValueMap[this.id]) {
                    itemOptions.value = this.id;
                  }
                }
              });
            } // 数据字典默认值
            else if (queryOption.optionType == '2') {
              if (StringUtils.isNotBlank(queryOption.defaultBlankId)) {
                itemOptions.select2.defaultBlank = false;
                var emptyId = commons.UUID.createUUID();
                var defaultOption = [
                  {
                    id: emptyId,
                    text: queryOption.defaultBlankText
                  }
                ];
                itemOptions.select2.data = defaultOption.concat(itemOptions.select2.data);
                itemOptions.value = emptyId;
                // 缓存空值
                _self.fieldSearchEmptyValueMap = _self.fieldSearchEmptyValueMap || {};
                _self.fieldSearchEmptyValueMap[emptyId] = emptyId;
              }
            }
          } else if (queryType == 'radio' || queryType == 'select' || queryType == 'checkbox') {
            itemOptions.items = _self._getFieldOptionValue(queryOption);
          }
          fieldOptios.contentItems.push(itemOptions);
        });
        formBuilder.buildContent(fieldOptios);
        formBuilder.showPanel({
          actionBack: {
            showNavTitle: true
          },
          optButton: {
            label: '完成',
            callback: function (event, panel) {
              return false;
            }
          },
          title: '高级搜索',
          content: '',
          container: searchPanel
        });
        searchPanel.appendChild($fieldSearchElement);
        // $.ui.loadContent(searchPanel)
        // return $fieldSearchElement;
      }
    },
    /**
     * 情况额外查询条件,condition为空是清楚全部，否则清楚等于condition的一条额外查询条件
     */
    clearOtherConditions: function (condition) {
      var _self = this;
      var options = _self.options;
      var otherConditions = options.otherConditions;
      if (condition) {
        $.each(otherConditions, function (idx, cond) {
          if (JSON.stringify(condition) != JSON.stringify(cond)) {
            return;
          }
          otherConditionssplice(idx, 1);
        });
      } else {
        _self.otherConditions = [];
      }
    },

    // 创建内容操作按钮
    _createActionSheel: function (result) {
      var _self = this;
      var menuItems = _self.menuItems;
      var rightMap = _self.options.rightMap || {};
      var configuration = _self.getConfiguration();
      var actionSheetData = [];
      var actionData = [];
      var dmsMoreId = 'list-more' + mui.uuid++;
      $.each(menuItems, function () {
        var self = this;
        // 按钮名称
        var name = self.text;
        // 按钮编号
        var code = self.code;
        // 按钮ID
        var btnId = self.uuid;
        var cssClass = self.cssClass;
        // 操作名称
        if ($.inArray('2', self.position) > -1 && (self.eventHandler || _self.hasDevelopmentMethod(code))) {
          if (actionData.length === 3) {
            actionData.push({
              id: dmsMoreId,
              name: dmsMoreId,
              text: '更多',
              icon: 'mui-icon mui-icon-more'
            });
          }
          if (actionData.length < 4) {
            actionData.push({
              id: btnId,
              name: code,
              text: name,
              cssClass: cssClass
            });
          } else {
            actionSheetData.push({
              id: btnId,
              name: code,
              text: name,
              cssClass: cssClass
            });
          }
        }
      });
      // 操作按钮
      var placeholder = _self.element[0];
      var wrapper = placeholder.querySelector('#list_tab_bar_mobile_view');
      if (wrapper != null) {
        wrapper.parentNode.removeChild(wrapper);
      }
      wrapper = document.createElement('div');
      wrapper.id = 'list_tab_bar_mobile_view';
      wrapper.classList.add('dms-tab-bar');
      if (configuration.editable) {
        // 隐藏按钮
        wrapper.classList.add('mui-hidden');
      }
      placeholder.appendChild(wrapper);
      // 更多操作按钮
      var acWrapper = placeholder.querySelector('#list_action_sheet_mobile_view');
      if (acWrapper != null) {
        acWrapper.parentNode.removeChild(acWrapper);
      }
      acWrapper = document.createElement('div');
      acWrapper.id = 'list_action_sheet_mobile_view';
      placeholder.appendChild(acWrapper);
      if (actionData.length > 0) {
        formBuilder.buildTabBar({
          data: actionData,
          container: wrapper
        });
        formBuilder.buildActionSheet({
          sheetId: dmsMoreId,
          data: actionSheetData,
          container: acWrapper
        });
      }
      // 绑定TabBar事件
      $(placeholder.querySelector('#list_tab_bar_mobile_view')).on('tap', '.mui-tab-item', function (event) {
        var name = this.getAttribute('name');
        if (name === dmsMoreId) {
          _self.toggleMoreActionSheel(name);
        } else {
          // 绑定当前事件
          _self.buttonAction(name, event, '2');
        }
        return true;
      });
      // 绑定ActionSheet事件
      $(placeholder.querySelector('#list_action_sheet_mobile_view')).on('tap', '.mui-table-view-cell', function (event) {
        var name = this.getAttribute('name');
        if (StringUtils.isNotBlank(name)) {
          // 隐藏ActionSheet操作按钮
          _self.toggleMoreActionSheel(dmsMoreId);
          // 绑定当前事件
          _self.buttonAction(name, event, '2');
        }
        return true;
      });
    },
    _editList: function (event) {
      var _self = this;
      var configuration = _self.getConfiguration();
      var viewContent = _self._mui('.table-view-content');
      if (_self.editable) {
        _self.editable = false;
        event.target.innerHTML = '编辑';
        viewContent[0].classList.remove('show-checkbox');
      } else {
        _self.editable = true;
        event.target.innerHTML = '完成';
        //_self._mui('.table-view-content .mui-checkbox input').each(function(i, n) {
        //n.checked = false;
        //})；
        viewContent[0].classList.add('show-checkbox');
      }
      _self.showOperations(_self.editable === true);
    },
    showOperations: function (show) {
      var self = this;
      var placeholder = self.element[0];
      var tabBar = placeholder.querySelector('#list_tab_bar_mobile_view');
      var viewContainer = placeholder.querySelector('.table-view-container');
      if (tabBar && viewContainer) {
        tabBar.classList[show ? 'remove' : 'add']('mui-hidden');
        if (StringUtils.isNotBlank(tabBar.innerHTML)) {
          // 添加底部偏移
          viewContainer.classList[show ? 'add' : 'remove']('wui-scroll-bottom');
        }
      }
    },
    toggleMoreActionSheel: function (name) {
      $(this.element[0].querySelector('#action_sheet_' + name)).popover('toggle');
    },
    isShowActionSheel: function () {
      var _self = this;
      var configuration = _self.getConfiguration();
      var menuItems = configuration.buttons;
      if (menuItems == null || menuItems.length == 0) {
        return false;
      }
      _self.menuItems = menuItems;
      var buttons = (_self.buttons = {});
      $.each(menuItems, function (i, button) {
        if ($.inArray('2', button.position) > -1) {
          _self.isShowActionSheel = true;
        }
        buttons[button.code] = button;
      });
      return _self.isShowActionSheel;
    },
    buttonAction: function (name, event, position) {
      var self = this;
      var options = self.options;
      var button = self.buttons[name];
      if ($.isEmptyObject(button.eventHandler)) {
        var args = [event, options];
        if (position === '3' && event.target) {
          var cell = $(event.target).closest('li.mui-table-view-cell');
          args.push(self.getData(cell.getAttribute('data-index')));
        }
        self.invokeDevelopmentMethod(button.code, args);
      } else {
        var eventHandler = button.eventHandler;
        var target = button.target || {};
        var eventParams = button.eventParams || {};
        var selectedRow = null;
        if (position === '3' && event.target) {
          var cell = $(event.target).closest('li.mui-table-view-cell');
          selectedRow = self.getData(cell.getAttribute('data-index'));
        }
        var opt = {
          view: self,
          action: button.code,
          target: target.position,
          targetWidgetId: target.widgetId,
          refreshIfExists: target.refreshIfExists,
          appType: eventHandler.type,
          appPath: eventHandler.path,
          params: eventParams.params,
          viewOptions: options,
          selectedRow: selectedRow
        };
        opt.event = event;
        self.startApp(opt);
      }
    }
  });
});
