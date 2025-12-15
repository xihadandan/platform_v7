define(['jquery', 'commons', 'constant', 'server', 'appContext', 'moment', 'appModal', 'dataStoreBase'], function (
  $,
  commons,
  constant,
  server,
  appContext,
  moment,
  appModal,
  DataStore
) {
  var StringUtils = commons.StringUtils;
  return {
    createQueryToolbarHtml: function () {
      var queryToolbar =
        "<div class='table-toolbar-container'><div class='keyword_search_toolbar clearfix' style='padding:10px;'>" +
        '<div class="keyword-search-wrap pull-left">' +
        '<span class="search-icon"><i class="iconfont icon-ptkj-sousuochaxun"></i></span>' +
        '<input class="form-control" type="text" placeholder="关键字">' +
        '<div class="close-icon" style="display: none"><i class="iconfont icon-ptkj-dacha"></i></div>' +
        '</div>' +
        '<div class="pull-left"><button class="well-btn w-btn-primary btn-query" type="button" name="query" title="查询">查询</button></div>' +
        '<div class="more-search pull-left">更多 <i class="iconfont icon-ptkj-xianmiaoshuangjiantou-xia"></i></div></div>' +
        '<div class="fixed-table-search-toolbar" style="display:none;"><div class="div_search_toolbar" style="padding-top:10px;"></div>' +
        '<div class="btn-group pull-right search-tool-bar" style="display: block;padding:0 10px 10px 0;"><button class="well-btn w-btn-primary btn-query" type="button" name="query" title="查询">查询</button><button class="well-btn w-btn-light w-line-btn btn-reset" type="button" name="reset" title="重置">重置</button><div class="hide-query-fields">收起 <i class="iconfont icon-ptkj-xianmiaoshuangjiantou-shang"></i></div></div></div></div>';
      return queryToolbar;
    },
    bindQueryToolbarEvent: function ($tableElement, $container) {
      var _self = this;
      $("button[name='query']", $container).on('click', function () {
        $tableElement.bootstrapTable(
          'refresh',
          $.extend({
            url: 'blank'
          })
        );
      });
      $('.keyword_search_toolbar .keyword-search-wrap input', $container).on('keyup', function (event) {
        if (event.keyCode !== 13) {
          return;
        }
        $tableElement.bootstrapTable(
          'refresh',
          $.extend({
            url: 'blank'
          })
        );
      });
      // 更多
      $('.more-search', $container).on('click', function () {
        $container.data('searchType', 'queryFields');
        var $searchToolbar = $container.find('.keyword_search_toolbar');
        var $fixedTableSearchToolbar = $container.find('.fixed-table-search-toolbar');
        $searchToolbar.hide();
        $fixedTableSearchToolbar.show();
      });
      // 字段查询
      $container.on('keyup', '.w-search-option', function (event) {
        if (event.keyCode == 13) {
          $tableElement.bootstrapTable(
            'refresh',
            $.extend({
              url: 'blank'
            })
          );
        }
      });
      // 重置
      $("button[name='reset']", $container).on('click', function () {
        var $fieldSearchElement = $('.div_search_toolbar', $container);
        $fieldSearchElement.html('');
        _self.buildQueryFields({
          container: $container,
          queryFields: $container.data('queryFields')
        });
        $tableElement.bootstrapTable(
          'refresh',
          $.extend({
            url: 'blank'
          })
        );
      });
      // 收起
      $('.hide-query-fields', $container).on('click', function () {
        $container.data('searchType', 'keyword');
        var $searchToolbar = $container.find('.keyword_search_toolbar');
        var $fixedTableSearchToolbar = $container.find('.fixed-table-search-toolbar');
        $searchToolbar.show();
        $fixedTableSearchToolbar.hide();
      });
    },
    buildQueryFields: function (options) {
      var $container = options.container;
      var queryFields = options.queryFields || [];
      $container.data('queryFields', queryFields);
      // bug#59701
      $container.closest('.doc_exchange_container').addClass('ui-wBootstrapTable');
      var $fieldSearchElement = $('.div_search_toolbar', $container);
      var fieldOptions = {
        container: $fieldSearchElement,
        inputClass: 'w-search-option',
        labelColSpan: '3',
        controlColSpan: '9',
        multiLine: 3,
        contentItems: []
      };
      $.each(queryFields, function (i, field) {
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
              if (queryOption.format.indexOf('HH') > -1 || queryOption.format.indexOf('mm') > -1 || queryOption.format.indexOf('ss') > -1) {
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
    },
    createDataStoreTableOptions: function (options) {
      var _self = this;
      var dataStore = _self.createDataStore(options);
      return {
        pagination: true,
        pageSize: 10,
        pageStyleOption: {
          jumpPage: true,
          nextPageLabel: '',
          pageStyle: 'default',
          prePageLabel: '',
          waterfallPageLabel: ''
        },
        pageList: ['10', '20', '50', '100', '200'],
        sidePagination: 'server',
        queryParamsType: '',
        queryParams: function (params) {
          var newParams = {
            pagination: {
              pageSize: params.pageSize,
              currentPage: params.pageNumber
            },
            criterions: []
          };
          return newParams;
        },
        ajax: function (request) {
          dataStore.loadData(request);
        }
      };
    },
    createDataStore: function (options) {
      var dataStore = new DataStore({
        dataStoreId: 'local',
        onDataChange: $.noop,
        pageSize: 10
      });
      dataStore.data = options.data || [];
      dataStore.container = options.container;
      dataStore.indexes = options.indexes;
      dataStore.loadData = function (request) {
        var dataList = this.filterAndPaginationData(request.data.pagination);
        request.success({
          rows: dataList,
          total: this.total
        });
      };
      dataStore.getKeyword = function () {
        return this.container.find('.keyword_search_toolbar .keyword-search-wrap input').val();
      };
      dataStore.filterAndPaginationData = function (pagination) {
        var _self = this;
        var dataList = this.data;
        var $container = _self.container;
        var searchType = $container.data('searchType');
        // 字段查询
        if (searchType == 'queryFields') {
          dataList = _self.queryByFields(dataList);
        } else {
          // 模糊查询
          var keyword = _self.getKeyword();
          if (StringUtils.isNotBlank(keyword)) {
            dataList = _self.queryByKeyword(keyword, dataList);
          }
        }
        _self.total = dataList.length;
        var pageSize = pagination.pageSize;
        var currentPage = pagination.currentPage;
        var total = dataList.length;
        if (total <= pageSize) {
          return dataList;
        } else {
          var retData = [];
          var index = pageSize * (currentPage - 1);
          for (var i = 0; i < pageSize; i++) {
            if (total >= index + i + 1) {
              retData.push(dataList[index + i]);
            }
          }
          return retData;
        }
      };
      // 字段查询
      dataStore.queryByFields = function (dataList) {
        var _self = this;
        var retData = [];
        var criterions = _self.collectCriterions();
        $.each(dataList, function (i, rowData) {
          if (_self.isMatch(rowData, criterions)) {
            retData.push(rowData);
          }
        });
        return retData;
      };
      dataStore.isMatch = function (rowData, criterions) {
        if (criterions == null || criterions.length == 0) {
          return true;
        }
        var match = false;
        for (var i = 0; i < criterions.length; i++) {
          var criterion = criterions[i];
          if (criterion == null) {
            continue;
          }
          var fieldValue = rowData[criterion.columnIndex] || '';
          fieldValue = $.trim(fieldValue);
          var type = criterion.type;
          var value = criterion.value;
          if (type == 'like') {
            if (StringUtils.contains(fieldValue, value)) {
              match = true;
            } else {
              match = false;
              break;
            }
          } else if (type == 'between') {
            if (fieldValue == null || StringUtils.isBlank(fieldValue)) {
              match = false;
            } else {
              var fromTime = moment(value[0]);
              var toTime = moment(value[1]);
              match = moment(fieldValue).isBetween(fromTime, toTime, 'days', true);
            }
          } else if (type == 'in') {
            if (StringUtils.isBlank(value)) {
              match = false;
            } else {
              var values = value.split(';');
              var inMatch = false;
              for (var j = 0; j < values.length; j++) {
                if (StringUtils.contains(fieldValue, values[j])) {
                  inMatch = true;
                  break;
                }
              }
              match = inMatch;
            }
          }
          // 一个条件不匹配就返回false
          if(!match) {
			break;
          }
        }
        return match;
      };
      dataStore.collectCriterions = function () {
        var _self = this;
        var $container = _self.container;
        var $element = $container.find('.div_search_toolbar');
        var queryFields = $container.data('queryFields') || [];
        var criterions = $.map(queryFields, function (field) {
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
              //		                if (queryType == 'date') {
              //		                  if (_self._getFieldDataType(criterion.columnIndex) == 'Date') {
              //		                    if (StringUtils.isNotBlank(criterion.value[0])) {
              //		                      // criterion.value[0] = moment(criterion.value[0], field.queryOptions.format).format('YYYY-MM-DD HH:mm:ss');
              //		                    }
              //		                    if (StringUtils.isNotBlank(criterion.value[1])) {
              //		                      // criterion.value[1] = moment(criterion.value[1], field.queryOptions.format).format('YYYY-MM-DD HH:mm:ss');
              //		                    }
              //		                  }
              //		                }
              return criterion;
            }
          }
          return null;
        });
        return criterions;
      };
      // 模糊查询
      dataStore.queryByKeyword = function (keyword, dataList) {
        var retData = [];
        var indexes = this.indexes || [];
        $.each(dataList, function (i, rowData) {
          for (var j = 0; j < indexes.length; j++) {
            var val = rowData[indexes[j]];
            if (val && StringUtils.contains(val + '', keyword)) {
              retData.push(rowData);
              break;
            }
          }
        });
        return retData;
      };
      return dataStore;
    }
  };
});
