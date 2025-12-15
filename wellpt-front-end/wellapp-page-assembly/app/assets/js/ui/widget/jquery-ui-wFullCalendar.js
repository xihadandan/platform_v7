(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define([
      'jquery',
      'commons',
      'server',
      'constant',
      'appContext',
      'design_commons',
      'formBuilder',
      'moment',
      'appModal',
      'CalendarWidgetDevelopment',
      'well_calendar',
      'select2',
      'wSelect2'
    ], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($, commons, server, constant, appContext, DesignUtils, formBuilder, moment, appModal, CalendarDevelopment, WellCalendar) {
  'use strict';
  var UUID = commons.UUID;
  var StringUtils = commons.StringUtils;

  var _viewStart = moment().startOf('month').format('YYYY-MM-DD HH:mm:ss');
  var _viewEnd = moment().endOf('month').format('YYYY-MM-DD HH:mm:ss');

  var _minTime = '08:00';
  var _maxTime = '18:00';

  // Reference https://www.helloweba.net/javascript/445.html
  // fullcalendar defaultOptions
  var defaultOptions = {
    themeSystem: 'bootstrap3',
    customButtons: {
      timeSetting: {
        text: '设置'
      }
    },
    header: {
      left: 'today,timeSetting',
      center: 'prev title next',
      right: ''
    },
    firstDay: 1, // 0~6 (周日、周一~~周六)， 设置每周的第一天为 周一
    fixedWeekCount: false, // 设置每月根据实际周数显示
    slotLabelFormat: 'H:mm', // 设置日历y轴上显示的时间文本格式
    slotDuration: '00:30:00', // 显示时间间隔，默认'00:30:00'
    aspectRatio: '1.8', // 日历单元格宽高比例,浮点型：1~2
    eventLimit: 4, // 限制显示在一天的事件数,当事件太多时，会显示一个看起来像“+ 2更多”,
    views: {
      month: {
        titleFormat: 'YYYY年MM月'
      },
      week: {
        titleFormat: 'YYYY年MM月DD日',
        columnFormat: 'M.D dddd'
      },
      day: {
        titleFormat: 'YYYY年MM月DD日',
        columnFormat: 'M/D dddd'
      }
    },
    allDaySlot: true,
    allDayDefault: false,
    slotEventOverlap: false, // 设置日程视图中的事件是否可以重叠，值为布尔类型，默认值为true，事件会相互重叠，最多一半会被遮住。
    minTime: _minTime, // 设置日程开始最小时间
    maxTime: _maxTime, // 设置日程最大时间
    lazyFetching: false, // 不需要缓存，每次切换视图都从新获取数据
    timeFormat: 'HH:mm',
    displayEventTime: true, //显示事件时间
    displayEventEnd: true, //并且显示事件的结束时间

    // height: '200px'
    contentHeight: 'auto'
  };
  // Reference
  // https://www.helloweba.net/javascript/454.html#fc-EventObject
  // eventObject
  var eventObject = {
    id: '',
    title: '',
    allDay: false,
    start: '', // 必填
    end: '',
    url: '',
    className: null,
    color: '',
    backgroundColor: '',
    borderColor: '',
    textColor: ''
  };
  // 默认弹窗按钮
  var dialogButtons = {
    cancel: {
      label: '关闭',
      className: 'btn-default',
      callback: function () {}
    }
  };

  $.widget('ui.wFullCalendar', $.ui.wWidget, {
    $calendarElement: null, // 日历面板
    $resourceElement: null, // 资源面板
    groupDataMap: {}, //分组的数据，用来实现资源视图分组的时候ID转换成name， 格式必须为{ key:name }
    _dataSource: [], //存放当前正在使用的数据源，供给资源视图使用，这样就不需要每次切换视图都需要重新获取数据
    ajaxParams: {}, //加载数据时需要传递的其他参数
    isValidePass: false, //每次保存事项的时候，根据这个值来确定是否数据验证通过
    _validator: null, //表单验证器
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

    /**
     * 创建组件
     */
    _createView: function () {
      // 创建js数据源对象
      var _self = this;
      var configuration = _self.getConfiguration();
      _self.otherOrders = _self.otherOrders || {};
      _self.otherConditions = _self.otherConditions || [];

      _self._beforeRenderView();
      _self._renderView();
      _self._afterRenderView();
      _self._setButtonEvent();

      // 页面容器监听左导航事件
      _self.pageContainer.on(constant.WIDGET_EVENT.LeftSidebarItemClick, function (e, ui) {
        console.log('-------------收到左导航事件点击事件 ----------');
        _self.invokeDevelopmentMethod('onLeftSidebarItemClick', [e, _self.options, _self.getConfiguration()]);
      });
    },
    /**
     * 渲染视图前
     */
    _beforeRenderView: function () {
      var _self = this;
      _self.invokeDevelopmentMethod('beforeRender', [_self.options, _self.getConfiguration()]);
    },

    /**
     * 视图组件渲染结束调用
     */
    _afterRenderView: function () {
      var _self = this;
      _self.invokeDevelopmentMethod('afterRender', [this.options, this.getConfiguration()]);
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
        $element.css({
          'overflow-x': 'auto'
        });
      }
      if (StringUtils.isNotBlank(configuration.height)) {
        $element.height(configuration.height);
        $element.css({
          'overflow-y': 'auto'
        });
      }

      if (configuration.isSimple) {
        _self.wellCalendar = new WellCalendar({
          parent: $element.attr('id'),
          parentWidth: configuration.simpleViewWidth || '',
          caller: _self,
          getDataSource: _self.getDataSource,
          scheduleEvent: function (date) {
            _self.invokeDevelopmentMethod('beforeDayClick', [date, null, null]);
            _self.invokeDevelopmentMethod('dayClick', [date, null, null]);
          },
          viewEvent: function (item) {
            _self.invokeDevelopmentMethod('eventClick', [item, null, null]);
          }
        });
      } else {
        // 渲染头部按钮
        this._renderButton($element);
        // 渲染头部搜索条件
        // this._renderSearchBar($element);
        this._renderKeywordSearch();
        var $panelDiv = $("<div class='calendar-panel'>");
        $element.append($panelDiv);
        // 渲染日历面板
        if (Boolean(configuration.enableCalendarView)) {
          this.$calendarElement = this._renderCalendarView($panelDiv);
        }
        // 渲染资源面板
        if (Boolean(configuration.enableResourceView)) {
          //资源视图，需要分组信息，获取资源分组的MAP数据
          _self.invokeDevelopmentMethod('getGroupDataMap', [_self.options, _self.getConfiguration()]);
          this.$resourceElement = this._renderResourceView($panelDiv);
        }
        if (Boolean(configuration.enableCalendarView) && Boolean(configuration.enableResourceView)) {
          _self.$resourceElement.hide();
        }
        //渲染视图切换选择器
        this._renderSelectView($element, 'calendar');
      }

      setTimeout(function () {
        _self.resetHeight();
      }, 150);
    },

    _renderSelectView: function ($element, defaultValue) {
      var _self = this;
      var configuration = this.getConfiguration();
      var $leftBtnGroup = $('.fc-right');
      //视图切换
      if (Boolean(configuration.enableCalendarView) && Boolean(configuration.enableResourceView)) {
        var selectView = new commons.StringBuilder();
        selectView.append("<select name='selectView'> ");
        selectView.append("	<option value='calendar'>日历视图</option>");
        selectView.append("	<option value='resources'>资源视图</option>");
        selectView.append('</select>');
        $leftBtnGroup.append(selectView.toString());
        $leftBtnGroup.find("select[name='selectView']").wSelect2({
          width: '100px',
          searchable: false
        });
        $leftBtnGroup.find('.select2-choice').css('height', '35px').find('span:first-child').css({ 'line-height': '35px', width: 'auto' });
        $leftBtnGroup.find('.btn-group').css({ float: 'right', marginBottom: '5px' });
      }

      $("select[name='selectView']").on('change', function () {
        var view = $(this).val();
        if (view == 'calendar') {
          //不能用show()和hide()来隐藏，因为会导致从资源视图切换回日历视图的时候布局变形，数据展示不出来
          _self.$calendarElement.find('.fc-view-container').css('visibility', 'visible');
          _self.$resourceElement.hide();
        } else if (view == 'resources') {
          _self.$calendarElement.find('.fc-view-container').css('visibility', 'hidden');
          _self.$resourceElement.show();
        }
      });
      $("select[name='selectView']").val(defaultValue).trigger('change');
    },

    /**
     * 渲染FullCalendar控件和相关查询条件，按钮等
     */
    _renderCalendarView: function ($element) {
      var _self = this;
      var options = this.options;
      var configuration = this.getConfiguration();
      if ($.isEmptyObject(configuration)) {
        appModal.warning('未定义资源日历组件');
        return;
      }

      var calendarId = options.widgetDefinition.id + '_calendar';
      var $calendarElement = $("<div id='" + calendarId + "' ></div>");
      $element.append($calendarElement);

      var fullCalendarOptions = {};
      defaultOptions.customButtons.timeSetting.click = function () {
        _self._setMinToMaxTime($element);
      };
      $.extend(true, fullCalendarOptions, defaultOptions);
      // 单日日程最大显示数量
      if (configuration.eventLimitNum && configuration.eventLimitNum > 0) {
        fullCalendarOptions.eventLimit = parseInt(configuration.eventLimitNum);
      }

      // 可切换视图
      var switchView = StringUtils.EMPTY;
      // 有设置切换视图
      if (configuration.switchView) {
        fullCalendarOptions.header.right = configuration.switchView.join(',');
      }

      // 高级设置
      var customSetting = {};
      if (StringUtils.isNotBlank(configuration.seniorConfiguration.customSetting)) {
        customSetting = JSON.parse(configuration.seniorConfiguration.customSetting);
      }
      $.extend(true, fullCalendarOptions, customSetting);

      /** FullCalendar初始化 * */
      $calendarElement.fullCalendar('destroy').fullCalendar(
        $.extend(
          {
            defaultView: configuration.defaultView,
            weekends: configuration.weekends,
            //点击日，月，周，上一个，下一个，今天按钮都会触发该方法
            events: function (start, end, timezone, callback) {
              _viewStart = start.format('YYYY-MM-DD HH:mm:ss');
              _viewEnd = end.format('YYYY-MM-DD HH:mm:ss');

              var dataSource = _self.getDataSource({
                startTime: _viewStart,
                endTime: _viewEnd
              });

              if (dataSource.length >= 0) {
                callback(dataSource);
              }

              //刚开始初始化的时候$resourceElement 没有值，所以加一个判断这里
              if (_self.$resourceElement) {
                // 刷新资源视图
                var currView = $calendarElement.fullCalendar('getView');
                var resourceDatas = _self._calendarData2ResourceData(dataSource);
                //月视图会有跨月的问题，所以需要加一周时间
                var defaultDate = _viewStart;
                if ('month' == currView.name) {
                  defaultDate = moment(_viewStart).add(7, 'days').format('YYYY-MM-DD HH:mm:ss');
                }
                _self.$resourceElement.view('changeView', {
                  eventData: resourceDatas,
                  activeView: currView.name,
                  defaultDate: defaultDate
                });
              }
            },
            // 日点击事件
            dayClick: function (date, jsEvent, view) {
              _self.invokeDevelopmentMethod('beforeDayClick', [date, jsEvent, view]);
              _self.invokeDevelopmentMethod('dayClick', [date, jsEvent, view]);
            },
            // 点击事项事件
            eventClick: function (event, jsEvent, view) {
              _self.invokeDevelopmentMethod('eventClick', [event, jsEvent, view]);
            },
            eventAfterRender: function (event, element, view) {
              _self.invokeDevelopmentMethod('eventAfterRender', [event, element, view]);
            },
            eventAfterAllRender: function (view) {
              _self.invokeDevelopmentMethod('eventAfterAllRender', [view]);
            },
            eventDestroy: function (event, element, view) {
              _self.invokeDevelopmentMethod('eventDestroy', [event, element, view]);
            },
            eventDragStart: function (event, jsEvent, ui, view) {
              _self.invokeDevelopmentMethod('eventDragStart', [event, jsEvent, ui, view]);
            },
            eventDragStop: function (event, jsEvent, ui, view) {
              _self.invokeDevelopmentMethod('eventDragStop', [event, jsEvent, ui, view]);
            },
            eventDrop: function (event, jsEvent, ui, view) {
              _self.invokeDevelopmentMethod('eventDrop', [event, jsEvent, ui, view]);
            }
          },
          fullCalendarOptions
        )
      );

      return $calendarElement;
    },

    /**
     * 获取日历视图数据源对象
     */
    getDataSource: function (params, callback) {
      var _self = this;
      var data = _self.getDataParams(params);
      $.ajax({
        url: ctx + '/basicdata/calendarcomponent/loadEvents',
        type: 'POST',
        data: JSON.stringify(data),
        dataType: 'json',
        async: false,
        contentType: 'application/json',
        success: function (result) {
          if (result.success) {
            _self._dataSource = []; //获取到新数据，需要先清空下
            $.each(result.data, function (index, event) {
              var data = $.extend(false, eventObject, event);
              data.id = data.uuid;
              data.start = data['startTime'];
              data.end = data['endTime'];
              var startMin = moment(data.start).format('HH:mm:ss');
              var endMin = moment(data.end).format('HH:mm:ss');
              var startDate = moment(data.start).format('YYYY-MM-DD');
              var endDate = moment(data.end).format('YYYY-MM-DD');
              if (startDate == endDate) {
                if (startMin == '00:00:00' && endMin == '23:59:59') {
                  data.allDay = true;
                  //allDay要生效，data.end必须为null, calendar组件的BUG
                  data.end = null;
                }
              }
              _self._dataSource.push(data);
            });
            callback && callback(_self._dataSource);
          } else {
            appModal.error(result.msg);
          }
        },
        error: function (error) {
          appModal.error(error);
        }
      });
      // 对数据源加工
      _self.invokeDevelopmentMethod('dataSourceProcessing', [_self._dataSource]);
      return _self._dataSource;
    },

    getDataParams: function (params) {
      var _self = this;
      var options = {
        dataProviderId: _self.getDataProviderId(),
        params: _self.ajaxParams,
        criterions: _self._collectCriterion() || [],
        orders: [],
        startTime: params.startTime,
        endTime: params.endTime
      };

      //									if (typeof params != "undefined") {
      //										$.extend(true, options.params, params);
      //									}

      options.criterions = options.criterions.concat(_self.otherConditions);
      options.criterions = options.criterions.concat(_self.getDefaultConditions());

      return options;
    },

    //将日历资源数据转换成资源视图数据
    _calendarData2ResourceData: function (dataSource) {
      var _self = this;
      var groupField = _self.getResourceGroupField();
      var objs = commons.Lists.toGroupList(dataSource, groupField);
      return objs;
    },
    /**
     * 获取数据来源ID
     */
    getDataProviderId: function () {
      return this.getConfiguration().dataProviderId;
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
      _self.options.searchType = 'keyword';
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
          $this.parent().siblings('button').find('.dropdown-value').text($this.text()).data('id', $this.data('id'));
        });

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
        });
      } else {
        $('.keyword_search_toolbar', $content).append(input + searchBtn);
      }

      $fieldSearchElement.find("button[name='query']").on('click', function () {
        _self._onSearch();
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
        _self.options.searchType = 'fieldSearch';
      });

      // 默认展开字段查询
      if (configuration.query.fieldSearch && configuration.query.expandFieldSearch) {
        $element.find('.more-search').trigger('click');
      }
    },
    _renderFieldSearch: function () {
      var _self = this;
      var $element = $(_self.element);
      var configuration = _self.getConfiguration();
      if (configuration.query.fields) {
        var searchToolbar = $('<div class="btn-group pull-right search-tool-bar">');
        searchToolbar.append(
          "<button class='well-btn w-btn-primary btn-query' type='button' name='query' title='查询'>查询</button>" +
            "<button class='well-btn w-btn-light w-line-btn btn-reset' type='button' name='reset' title='重置'>重置</button>"
        );
        // 条件查询和关键字查询切换按钮
        if (configuration.query.fieldSearch && configuration.query.keyword) {
          searchToolbar.append('<div class="hide-query-fields">收起 <i class="iconfont icon-ptkj-xianmiaoshuangjiantou-shang"></i></div>');
        }
        var $searchToolbar = $('<div class="fixed-table-search-toolbar"></div>');
        $searchToolbar.append(searchToolbar);

        $element.find('.div_search_toolbar').remove();
        $element.find('.fixed-table-search-toolbar').remove();
        var $fieldSearchElement = $("<div class='div_search_toolbar' style='padding-top:10px;'></div>");
        $element.prepend($fieldSearchElement);
        $fieldSearchElement.after($searchToolbar);

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
          _self.options.searchType = 'keyword';
        });

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

        $fieldSearchElement;
        return $fieldSearchElement;
      }
      return '';
    },
    /**
     * 拼接按钮工具栏
     */
    _renderButton: function ($element) {
      var _self = this;
      var options = this.options;
      var buttonElement = this.buttonElement;
      var configuration = this.getConfiguration();

      var $toolbarElement = $("<div class='fixed-calendar-toolbar'></div>");
      $element.prepend($toolbarElement);

      var $buttonDiv = $("<div class='pull-left bs-bars'></div>");
      var $calendarToolbar = $element.find('.fixed-calendar-toolbar');
      $calendarToolbar.append($buttonDiv);

      // 按钮定义
      var $headerToolbar = $("<div class='div_header_toolbar btn-group'></div>");
      var buttonGroup = {};
      if (configuration.buttons) {
        $.each(configuration.buttons, function (index, button) {
          if (StringUtils.isBlank(button.group)) {
            var buttonHtml = new commons.StringBuilder();
            var btnClass = StringUtils.isBlank(button.cssClass) ? 'btn-default' : button.cssClass;
            if (button.icon && button.icon.className) {
              btnClass += ' ' + button.icon.className;
            }
            if (button.btnLib && button.btnLib.iconInfo && button.btnLib.iconInfo.fileIDs) {
              btnClass += ' ' + button.btnLib.iconInfo.fileIDs;
            }
            if (button.btnLib && button.btnLib.btnColor) {
              btnClass += ' well-btn ' + button.btnLib.btnColor;
            }
            if (button.btnLib && button.btnLib.btnSize) {
              btnClass += ' ' + button.btnLib.btnSize;
            }
            buttonHtml.appendFormat("<button type='button' class='btn {0} btn_class_{1}'>{2}</button>", btnClass, button.code, button.text);
            $headerToolbar.append(buttonHtml.toString());
          } else {
            if (!buttonGroup[button.group]) {
              buttonGroup[button.group] = [];
            }
            buttonGroup[button.group].push(button);
          }
        });

        $.each(buttonGroup, function (group, buttons) {
          var firstBtn = buttons[0];
          var buttonHtml = new commons.StringBuilder();
          buttonHtml.append("<div class='btn-group'>");
          var btnClass = StringUtils.isBlank(firstBtn.cssClass) ? 'btn-default' : firstBtn.cssClass;
          if (firstBtn.icon && firstBtn.icon.className) {
            btnClass += ' ' + firstBtn.icon.className;
          }
          buttonHtml.appendFormat("	<button type='button' class='btn {0} dropdown-toggle' ", btnClass);
          buttonHtml.append("	data-toggle='dropdown'>");
          buttonHtml.append(group);
          buttonHtml.append("		<span class='caret'></span>");
          buttonHtml.append('	</button>');
          buttonHtml.append("	<ul class='dropdown-menu' role='menu'>");
          $.each(buttons, function (index, button) {
            var liClass = 'li_class_' + button.code;
            buttonHtml.append('	<li class=' + liClass + "><a href='#'>" + button.text + '</a></li>');
          });
          buttonHtml.append('	</ul>');
          buttonHtml.append('</div>');
          $headerToolbar.append(buttonHtml.toString());
        });
        $buttonDiv.append($headerToolbar);

        var buttonElement = {
          headerToolbar: $headerToolbar
        };
        this.buttonElement = buttonElement;
      }
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

    /** 渲染资源日历视图 */
    _renderResourceView: function ($element) {
      var _self = this;
      var options = this.options;
      var configuration = this.getConfiguration();
      if ($.isEmptyObject(configuration)) {
        appModal.warning('未定义资源日历组件');
        return;
      }

      var resourceId = options.widgetDefinition.id + '_resource';
      var $resourceElement = $("<div id='" + resourceId + "' ></div>");
      $resourceElement.css('width', _self.$calendarElement.css('width'));
      $element.append($resourceElement);

      /** 资源日历视图 初始化 * */
      $resourceElement.view(
        $.extend(true, {
          defaultView: configuration.defaultView,
          setDayLength: {
            min: _minTime,
            max: _maxTime,
            slotDuration: '01:00:00' //旧版资源日历不支持时间刻度，则使用默认一个小时时间刻度
          },
          $calendarWidget: _self,
          eventData: _self._calendarData2ResourceData(_self._dataSource),
          clickCallback: function (date, viewType) {
            console.log('$resourceElement.clickCallback=' + date + ',viewType=' + viewType);
            _self.invokeDevelopmentMethod('dayClick', [date, null, null]);
          },
          eventClickCallback: function (events) {
            console.log('$resourceElement.eventClickCallback' + events);
            _self.invokeDevelopmentMethod('resouceEventListClick', [events]);
            //_self._showEventList(events);
          }
        })
      );
      return $resourceElement;
    },
    /**
     * 渲染查询按钮/搜索
     */
    _renderSearchBar: function ($element) {
      var _self = this;
      var options = this.options;
      var configuration = this.getConfiguration();
      // 工具栏
      if (configuration.query.keyword) {
        options.searchType = 'keyword';
      } else if (configuration.query.fieldSearch) {
        options.searchType = 'fieldSearch';
        _self.$fieldSearchElement = _self._renderFieldSearch($element);
      }
      if (StringUtils.isNotBlank(options.searchType)) {
        var toolbar = new commons.StringBuilder();
        toolbar.append("<div class='columns columns-right btn-group pull-left'>");
        toolbar.append(
          "<button class='well-btn w-btn-primary btn-query' type='button' name='query' title='查询'>查询</button>" +
            "<button class='well-btn w-btn-light w-line-btn btn-reset' type='button' name='reset' title='重置'>重置</button>"
        );

        // 条件查询和关键字查询切换按钮
        if (configuration.query.fieldSearch && configuration.query.keyword) {
          toolbar.append("	<button class='btn btn-default' type='button' name='showQueryField' title='展开字段查询'>");
          toolbar.append("		<i class='glyphicon glyphicon-chevron-down' >");
          toolbar.append('		</i>');
          toolbar.append('	</button>');
          toolbar.append('<div class="more-search pull-left">更多 <i class="iconfont icon-ptkj-xianmiaoshuangjiantou-xia"></i></div>');
        }
        toolbar.append('</div>');
        var $calendarToolbar = $element.find('.fixed-calendar-toolbar');

        var $searchDiv = $("<div class='pull-left search'></div>");
        $searchDiv.append("<input class='form-control' type='search' placeholder='搜索'>");
        $calendarToolbar.append($searchDiv);
        $calendarToolbar.append(toolbar.toString());
        // 查询事件
        $calendarToolbar.find("button[name='query']").on('click', function () {
          _self._onSearch();
        });
        // 重置事件
        $calendarToolbar.find("button[name='reset']").on('click', function () {
          _self.resetQueryFields();
        });
        var $showQueryField = $calendarToolbar.find("button[name='showQueryField']").on('click', function () {
          var $this = $(this);
          if (options.searchType == 'keyword') {
            if (_self.$fieldSearchElement) {
              _self.$fieldSearchElement.show();
            } else {
              _self.$fieldSearchElement = _self._renderFieldSearch($element);
            }
            $element.find('.fixed-calendar-toolbar .search').hide();
            $this.find('i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
            $this.attr('title', '收起字段查询');
            options.searchType = 'fieldSearch';
          } else {
            _self.$fieldSearchElement.hide();
            $element.find('.fixed-calendar-toolbar .search').show();
            $this.find('i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
            $this.attr('title', '展开字段查询');
            options.searchType = 'keyword';
          }
          _self.resetHeight();
        });

        // 默认展开字段查询
        if (configuration.query.fieldSearch && configuration.query.expandFieldSearch) {
          $showQueryField.trigger('click');
        }

        $element.on('keyup', '.w-search-option', function (event) {
          if (event.keyCode == 13) {
            _self._onSearch();
          }
        });
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
      if (StringUtils.isNotBlank(_self.queryText)) {
        var orcriterion = {
          conditions: [],
          type: 'or'
        };
        $.each(configuration.columns, function (index, column) {
          if (column.hidden == '0') {
            orcriterion.conditions.push({
              columnIndex: column.name,
              value: _self.queryText,
              type: 'like'
            });
          }
        });
        criterions.push(orcriterion);
      }
      if (_self.options.searchType == 'keyword') {
        var text = $element.find('.keyword_search_toolbar .keyword-search-wrap input').val();
        if (StringUtils.isNotBlank(text)) {
          var orcriterion = {
            conditions: [],
            type: 'or'
          };
          $.each(configuration.columns, function (index, column) {
            if (column.keywordQuery != '0') {
              orcriterion.conditions.push({
                columnIndex: column.name,
                value: text,
                type: 'like'
              });
            }
          });
          criterions.push(orcriterion);
        }
      } else if (_self.options.searchType == 'fieldSearch') {
        criterions = $.map(configuration.query.fields, function (field) {
          var queryType = field.queryOptions.queryType;
          if (field.operator != 'between') {
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
            }
            if (StringUtils.isNotBlank(value)) {
              var criterion = {};
              criterion.columnIndex = field.name;
              criterion.value = value;
              criterion.type = field.operator;
              var queryOption = field.queryOptions;
              if (queryType == 'date' && _self._getFieldDataType(criterion.columnIndex) == 'Date') {
                criterion.value = moment(criterion.value, field.queryOptions.format).format('YYYY-MM-DD HH:mm:ss');
              }
              return criterion;
            }
          } else {
            var value = $('#' + field.name + '_Begin', $element).val();
            var value2 = $('#' + field.name + '_End', $element).val();
            if (StringUtils.isNotBlank(value) || StringUtils.isNotBlank(value2)) {
              var criterion = {};
              criterion.columnIndex = field.name;
              criterion.value = [value, value2];
              criterion.type = field.operator;
              var queryOption = field.queryOptions;
              if (queryType == 'date') {
                if (_self._getFieldDataType(criterion.columnIndex) == 'Date') {
                  if (StringUtils.isNotBlank(criterion.value[0])) {
                    // criterion.value[0]
                    // =
                    // moment(criterion.value[0],
                    // field.queryOptions.format).format('YYYY-MM-DD
                    // HH:mm:ss');
                  }
                  if (StringUtils.isNotBlank(criterion.value[1])) {
                    // criterion.value[1]
                    // =
                    // moment(criterion.value[1],
                    // field.queryOptions.format).format('YYYY-MM-DD
                    // HH:mm:ss');
                  }
                }
              }
              return criterion;
            }
          }
          return null;
        });
      }
      return criterions;
    },
    /**
     * 校验提交的数据
     */
    validateEventData: function (data) {
      var _self = this;
      if (!_self._validator.form()) {
        return false;
      }
      return true;
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
     * 获取资源分组字段名
     */
    getResourceGroupField: function () {
      return this.getConfiguration().resourceGroupId || '';
    },
    /**
     * 获取参数
     */
    getParam: function (key) {
      return this.ajaxParams[key];
    },
    /**
     * 获取视图数据集合
     */
    getData: function () {
      return this.$calendarElement.fullCalendar('getEventSources');
    },
    /**
     * 根据唯一键获取列数据
     */
    getEventByUniqueId: function (id) {
      return this.$calendarElement.fullCalendar('getEventSourceById', id);
    },
    /**
     * 刷新数据
     */
    refresh: function () {
      var _self = this;
      if (_self.wellCalendar) {
        _self.wellCalendar.refresh();
      } else {
        //重新触发events function
        this.$calendarElement.fullCalendar('refetchEvents');
        // 刷新资源视图
        var resourceDatas = _self._calendarData2ResourceData(_self._dataSource);
        if (this.$resourceElement) {
          this.$resourceElement.view('refresh', resourceDatas);
        }
      }
    },
    /**
     * 查询数据
     */
    query: function () {
      this.$calendarElement.fullCalendar(
        'refetchEvents',
        this.getDataSource({
          startTime: _viewStart,
          endTime: _viewEnd
        })
      );
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
    },
    /**
     * 清空额外查询条件,condition为空是清除全部，否则清除等于condition的一条额外查询条件
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
     * 重新计算（设置）视图高度
     */
    resetHeight: function (height) {
      var _self = this;
      if (!_self.$calendarElement) {
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
      var newHeight = configuration.height - fieldSearchHeight;
      _self.$calendarElement.fullCalendar({
        height: newHeight
      });
    },
    /**
     * 需要查询时调用
     */
    _onSearch: function () {
      this.query();
    },
    /**
     * 重置查询条件
     */
    resetQueryFields: function () {
      var _self = this;
      var options = _self.options;
      if (options.searchType == 'keyword') {
        $(_self.element).find('.fixed-calendar-toolbar .search>input').val('');
      } else {
        _self.$fieldSearchElement = _self._renderFieldSearch(_self.element);
      }
      _self.invokeDevelopmentMethod('onResetQueryFields');
    },
    /**
     * 注册按钮事件，调用相关二开接口
     */
    _setButtonEvent: function () {
      var _self = this;
      var options = _self.options;
      var $contextElement = $(_self.element);
      var configuration = _self.getConfiguration();
      $.each(configuration.buttons, function (index, button) {
        var buttonSelector = (StringUtils.isBlank(button.group) ? '.btn_class_' : '.li_class_') + button.code;
        if (!$.isEmptyObject(button.eventHandler)) {
          var eventHandler = button.eventHandler.eventHandler || button.eventHandler;
          var target = button.target || {};
          var params = button.eventHandler.eventParams ? button.eventHandler.eventParams.params : {};
          var opt = {
            target: target.position,
            targetWidgetId: target.widgetId,
            refreshIfExists: target.refreshIfExists,
            appType: eventHandler.type,
            appPath: eventHandler.path,
            view: _self,
            params: $.extend({}, params, appContext.parseEventHashParams(eventHandler, 'menuid')),
            viewOptions: options
          };
          $contextElement.on('click', buttonSelector, function (event) {
            opt.event = event;
            _self.startApp(opt);
          });
        } else {
          $contextElement.on('click', buttonSelector, function (event) {
            var $toolbarDiv = $(event.target).closest('div');
            var args = [event, options];
            if ($toolbarDiv.is('.div_lineEnd_toolbar')) {
              var index = $toolbarDiv.attr('index');
              var allData = _self.$calendarElement.fullCalendar('getEventSources');
              args.push(allData[index]);
            }
            _self.invokeDevelopmentMethod(button.code, args);
          });
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
     * 渲染事项查看表单
     */
    _renderViewForm: function (event) {
      var _self = this;
      var configuration = _self.getConfiguration();
      if (configuration.columns) {
        var $viewForm = $('#div_calendar_view_form');
        var viewFieldOptions = {
          container: $viewForm,
          inputClass: 'w-viewform-option',
          rowClass: 'js-item-row',
          labelColSpan: '3',
          controlColSpan: '9',
          multiLine: false, // 一行一列/一行两列
          contentItems: []
        };
        $.each(configuration.columns, function (i, field) {
          if (field.hidden == '0') {
            // 处理'状态'的值
            var defaultValue = event[field.name];
            //
            var controlOption = field.controlOptions;
            var controlType = controlOption.queryType;
            var viewItemOptions = {
              label: field.title,
              name: 'viewform_' + field.name, // 加前缀区分字段，否则造成formBuilder渲染表单控件冲突
              type: controlType,
              value: defaultValue,
              controlOption: controlOption
            };
            viewFieldOptions.contentItems.push(viewItemOptions);
          }
        });
        formBuilder.buildContentAsLabel(viewFieldOptions);
        return $viewForm;
      }
      return '';
    },
    /**
     * 渲染新建事项表单
     */
    _renderItemForm: function (event) {
      var _self = this;
      var configuration = _self.getConfiguration();
      if (configuration.columns) {
        var $itemForm = $('#div_calendar_item_form');
        var editFieldOptions = {
          container: $itemForm,
          inputClass: 'w-itemform-option',
          rowClass: 'js-item-row',
          labelColSpan: '3',
          controlColSpan: '9',
          multiLine: false, // 一行一列/一行两列
          contentItems: [],
          isRequired: false
        };

        $.each(configuration.columns, function (i, field) {
          if (field.hidden == '0') {
            var defaultValue;
            if (typeof event != 'undefined') {
              defaultValue = event[field.name];
            }

            var controlOption = field.controlOptions;
            var controlType = controlOption.queryType;
            var isRequired = false;
            if (field.validateRules && field.validateRules.validateRegex == 'notNull') {
              isRequired = true;
            }
            var editItemOptions = {
              label: field.title,
              name: 'itemform_' + field.name, // 加前缀区分字段，否则造成formBuilder渲染表单控件冲突
              type: controlType,
              value: defaultValue || field.defaultValue,
              controlOption: controlOption,
              labelClass: 'label-filed',
              isRequired: isRequired
            };
            editFieldOptions.contentItems.push(editItemOptions);
          }
        });
        formBuilder.buildContent(editFieldOptions);
        return $itemForm;
      }
      return '';
    },
    /**
     * TODO 渲染自定义事项表单
     */
    _renderCustomItemForm: function (event, exist) {
      return '';
    },
    /**
     * 打开事项弹出框
     */
    openEventDialog: function (options) {
      var _self = this;
      var _thisButtons = {};

      _thisButtons['ok'] = {
        label: '保存', // 新增，编辑事项 -> 确定
        className: 'btn-primary js-btn-save',
        callback: function () {
          return _self._formOkClick(
            $.extend(
              true,
              {
                $el: $('#div_calendar_item_form'),
                collectClass: 'w-itemform-option'
              },
              options
            )
          );
        }
      };

      // 默认都有取消按钮
      $.extend(true, _thisButtons, dialogButtons);

      var $dialog = appModal.dialog({
        title: options.event.uuid ? '编辑日程' : '新建日程',
        size: 'large', // large
        message: $("<form id='form_calendar_event'><div id='div_calendar_item_form'></div></form>"), // $itemForm,
        shown: function (_$dialog) {
          // 启用自定义表单
          if (_self.getConfiguration().formModuleConfig.enableCustomForm) {
            _self._renderCustomItemForm(options.event, false);
            $('.modal-content').css('width', '750px');
          } else {
            // 渲染事项表单
            _self._renderItemForm(options.event);
          }
          _self.invokeDevelopmentMethod('afterEventDialogRender', [_$dialog, options, _self.getConfiguration()]);

          //添加表单的校验规则
          _self.initFormValideRule();
        },
        buttons: _thisButtons
      });
      return $dialog;
    },

    //定义表单的校验规则
    initFormValideRule: function () {
      var _self = this;
      var validateOptions = {
        errorElement: 'div',
        ignore: '',
        rules: {},
        messages: {},
        errorPlacement: function (error, element) {
          error.css('color', 'red');
          error.appendTo(element.parents('.controls').last());
          //											if( element.next().length == 0 ){
          //												error.insertAfter(element);
          //											}else{
          //
          //											}
        }
      };
      var configuration = this.getConfiguration();
      var columnRules = [];
      $.each(configuration.columns, function (i, field) {
        if (field.hidden == '0') {
          //有设置校验规则，则添加校验器
          var regex = field.validateRules.validateRegex;
          if (StringUtils.isNotBlank(regex)) {
            var method = 'check' + field.name + 'Format';
            var errMsg = field.title + '只能输入' + field.validateRules.validateRuleLabel;
            if (regex == 'notNull') {
              method = 'required';
              errMsg = field.title + '不能为空。';
            }
            validateOptions.rules['itemform_' + field.name] = {};
            validateOptions.rules['itemform_' + field.name][method] = true;
            validateOptions.messages['itemform_' + field.name] = {};
            validateOptions.messages['itemform_' + field.name][method] = errMsg;

            if (regex != 'notNull') {
              $.validator.addMethod(method, function (value, element) {
                var reg = new RegExp(regex);
                if (reg.test(value)) {
                  return true;
                }
                return false;
              });
            }
          }
        }
      });

      //开放添加校验器的路口
      _self.invokeDevelopmentMethod('addOtherValidateRules', [validateOptions, _self.getConfiguration()]);

      _self._validator = $('#form_calendar_event').validate(validateOptions);
    },

    /**
     * 查看/编辑事项
     */
    _viewEvent: function (options) {
      var _self = this;
      var _thisButtons = {};
      if (true) {
        // TODO 添加编辑权限
        _thisButtons['del'] = {
          label: '删除', // 查看事项->删除
          className: 'btn-danger js-btn-del',
          callback: function () {
            _self._formDeleteClick(options);
          }
        };
        _thisButtons['edit'] = {
          label: '编辑', // 查看事项->编辑
          className: 'btn-info js-btn-edit',
          callback: function () {
            _self.openEventDialog(options);
          }
        };
      }
      // 取消按钮放最下面
      $.extend(true, _thisButtons, dialogButtons);

      var $dialog = appModal.dialog({
        size: 'middle',
        title: '日程详情',
        message: $("<div id='div_calendar_view_form'></div>"),
        shown: function (_$dialog) {
          _self._renderViewForm(options.event);
          _self.invokeDevelopmentMethod('afterViewEventDialogRender', [_$dialog, options, _self.getConfiguration()]);
        },
        buttons: _thisButtons
      });

      return $dialog;
    },
    /**
     * 设置时间段
     */
    _setMinToMaxTime: function ($element) {
      var _self = this;
      var options = _self.options;
      var calendarId = options.widgetDefinition.id;

      var timeQuantumBar = new commons.StringBuilder();
      timeQuantumBar.append("	<div id='" + calendarId + "_slider'></div>");
      timeQuantumBar.append("	<div style='text-align: center' class='slidertext'>");
      timeQuantumBar.append(
        "		<input type='text' id='" +
          calendarId +
          "_amount' style='border:0; color:#f6931f; font-weight:bold; text-align: center; font-size: 16px;'>"
      );
      timeQuantumBar.append('	</div>');

      var $timeQuantum = $("<div id='" + calendarId + "_setTimeQuantum' class='taxt'>");
      appModal.dialog({
        title: '设置时间段',
        size: 'middle',
        message: $timeQuantum,
        shown: function () {
          $('.modal-content').css('width', '500px');

          $timeQuantum.append(timeQuantumBar.toString());
          $timeQuantum.css('margin-top', '20px');

          var $calendarSet = $('#' + calendarId + '_setTimeQuantum');
          var $calendarAmount = $('#' + calendarId + '_amount');
          var $calendarSlider = $('#' + calendarId + '_slider');
          var beginHour = moment(_minTime, 'mm:ss').format('m');
          var endHour = moment(_maxTime, 'mm:ss').format('m');
          $calendarSlider.slider({
            range: true,
            min: 0,
            max: 24,
            values: [beginHour, endHour],
            slide: function (event, ui) {
              if (ui.values[0] == ui.values[1]) {
                return false;
              }
              $calendarAmount.val(ui.values[0] + ':00 - ' + ui.values[1] + ':00');
            }
          });
          $calendarAmount.val($calendarSlider.slider('values', 0) + ':00 - ' + $calendarSlider.slider('values', 1) + ':00');
        },
        buttons: {
          cancel: {
            label: '关闭',
            className: 'btn-default',
            callback: function () {}
          },
          ok: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              // 保持原来的视图，所以需要先记录下
              var oldView = $("select[name='selectView']").val();

              var $calendarSlider = $('#' + calendarId + '_slider');
              _minTime =
                ($calendarSlider.slider('values', 0) < 10
                  ? '0' + $calendarSlider.slider('values', 0)
                  : $calendarSlider.slider('values', 0)) + ':00';
              _maxTime = $calendarSlider.slider('values', 1) + ':00';
              if (_self.$calendarElement) {
                _self.$calendarElement.fullCalendar('option', 'minTime', _minTime);
                _self.$calendarElement.fullCalendar('option', 'maxTime', _maxTime);
              }

              if (_self.$resourceElement.length > 0) {
                _self.$resourceElement.view('setDayHour', {
                  min: _minTime,
                  max: _maxTime
                });
              }

              //重新设置了时间段，日历头部会被重新渲染，所以这里需要重新渲染下视图切换下拉框
              _self._renderSelectView($element, oldView);
            }
          }
        }
      });
    },
    /**
     * 保存事项
     */
    _saveEvent: function (event, fnCallback) {
      var json = {
        dataProviderId: this.getDataProviderId(),
        data: event
      };
      $.ajax({
        url: ctx + '/basicdata/calendarcomponent/save',
        type: 'POST',
        data: JSON.stringify(json),
        dataType: 'json',
        async: false,
        contentType: 'application/json',
        success: function (result) {
          if (result.success) {
            if (typeof fnCallback == 'function') {
              fnCallback.call(this, result);
            }
          } else {
            appModal.error(result.msg);
            return false;
          }
        },
        error: function (error) {
          appModal.error(error);
          return false;
        }
      });
    },
    /**
     * 删除事项
     */
    _deleteEvent: function (id, fnCallback) {
      $.ajax({
        url: ctx + '/basicdata/calendarcomponent/delete',
        type: 'POST',
        data: {
          dataProviderId: this.getDataProviderId(),
          uuid: id
        },
        dataType: 'json',
        async: false,
        success: function (result) {
          if (result.success && typeof fnCallback == 'function') {
            fnCallback.call(this);
          }
        },
        error: function (error) {
          appModal.error(error);
        }
      });
    },

    // 收集事项表单数据
    _collectEventData: function (options) {
      var _self = this;
      var formData = DesignUtils.collectConfigurerData(options.$el, options.collectClass);
      var newFormData = {};
      newFormData['uuid'] = options.event.uuid;
      // 去除字段中的 "itemform_"\"viewform_" 前缀
      $.each(formData, function (key, value) {
        if (key.indexOf('itemform_') == 0) {
          newFormData[key.replace('itemform_', '')] = value;
        }
      });
      // 标准化开始时间和结束时间格式
      if (newFormData.startTime) {
        newFormData['startTime'] = commons.DateUtils.format(new Date(newFormData['startTime']), 'yyyy-MM-dd HH:mm:ss');
      }
      if (newFormData.endTime) {
        newFormData['endTime'] = commons.DateUtils.format(new Date(newFormData['endTime']), 'yyyy-MM-dd HH:mm:ss');
      }

      return newFormData;
    },

    getAfterCollectEventData: function (method, args) {
      var _self = this;
      var develops = _self.develops;
      var _develop = null;
      var methodArgs = args;
      if (!methodArgs) {
        methodArgs = [];
      }
      if ($.isArray(develops)) {
        $.each(develops, function (i, develop) {
          if (develop instanceof CalendarDevelopment) {
            _develop = develop;
          }
        });
      }
      if (_develop[method]) {
        return _develop[method].apply(_develop, methodArgs);
      }
    },

    /**
     * 弹窗表单"确认"
     */
    _formOkClick: function (options) {
      var _self = this;
      var newFormData = _self._collectEventData(options);
      _self.invokeDevelopmentMethod('afterCollectEventData', [options, _self.getConfiguration(), newFormData]);

      var _callback = _self.getAfterCollectEventData('getAfterCollectEventDataCB', [options, _self.getConfiguration(), newFormData]);
      if (_callback === false) {
        return false;
      }
      // 数据校验
      if (_self.validateEventData(newFormData)) {
        // 数据保存
        _self._saveEvent(newFormData, function (result) {
          // 因为允许批量添加重复的事项，所以每次保存完后，就直接刷新数据，
          _self.invokeDevelopmentMethod('afterSaveEvent', [_self.options, _self.getConfiguration(), options]);
          // 重新刷新数据
          appModal.success('保存成功！');
          appModal.hide();
          _self.refresh();
        });
        return true;
      } else {
        $(document).on('change', function (e) {
          if ($(e.target).attr('type') != 'file') {
            _self._validator.element($(e.target));
          }
        });

        $(document).on('changeFiles', function (e) {
          setTimeout(function () {
            _self._validator.element($(e.target));
          }, 100);
        });

        $(document).on('onTreeSelectChange', function (e) {
          setTimeout(function () {
            _self._validator.element($(e.target));
          }, 100);
        });
      }

      return false;
    },

    /**
     * 弹窗表单"删除"
     */
    _formDeleteClick: function (options, fn) {
      var _self = this;
      _self.invokeDevelopmentMethod('deleteEventClick', [options.event, _self.options, _self.getConfiguration()]);
    },

    //更新事项数据
    _updateEvent: function (event) {
      var _self = this;
      if (StringUtils.isNotBlank(event.uuid)) {
        _self.$calendarElement.fullCalendar('updateEvent', event);
      } else {
        _self.$calendarElement.fullCalendar('renderEvent', event);
      }
    },

    //默认的删除事项事件
    _defaultDeleteEvent: function (event) {
      var _self = this;
      var _thisUuid = event.uuid;
      appModal.confirm('确认删除？', function (result) {
        if (result) {
          _self._deleteEvent(_thisUuid, function () {
            if (typeof fn == 'function') {
              fn();
            }
            _self.invokeDevelopmentMethod('afterDeleteEvent', [event, _self.options, _self.getConfiguration()]);

            _self.$calendarElement.fullCalendar('removeEvents', event.id);
            appModal.success('删除成功!');
          });
        }
      });
    },

    // 打开资源视图列表页
    _showEventList: function (events) {
      var _self = this;
      console.log(events);
      var $allEventBox = $('<div id="div_event_list"/>');
      $.each(events, function (i) {
        var e = events[i];
        var $eventDiv = $("<div class='event-box'/>");
        var $table = $('<table class="table"/>');
        var title = e.title;
        if (!e.isCanView) {
          title = '[' + e.calendarCreatorName + ']已安排';
        }
        var $titleTr = $('<tr><td>标题</td><td>' + title + '</td></tr>');
        var $timeTr = $("<tr><td>时间</td><td class='js-eventTime'></td></tr>");
        var startTimeStr = moment(e.startTime).format('YYYY-MM-DD HH:mm');
        var endTimeStr = moment(e.endTime).format('YYYY-MM-DD HH:mm');
        $timeTr.find('td.js-eventTime').html(startTimeStr + ' 至 ' + endTimeStr);
        $table.append($titleTr, $timeTr);
        $eventDiv.append($table);
        if (e.isCanView) {
          var $more = $('<div class="more-div text-center"><a class="more pull-right">更多详情</a></div>');
          $more.data('event', e);
          $eventDiv.append($more);
        }
        $allEventBox.append($eventDiv);
      });
      var $dialog = appModal.dialog({
        title: '日程详情',
        size: 'middle', // large
        message: $allEventBox, // $itemForm,
        shown: function (_$dialog) {
          //定义更多详情的点击事件
          $.each(_$dialog.find('a.more'), function () {
            $(this).click(function () {
              var e = $(this).parents('div').data('event');
              _self._viewEvent({ event: e });
              $dialog.find('.bootbox-close-button').trigger('click');
            });
          });
          _self.invokeDevelopmentMethod('afterEventListDialogRender', [_$dialog, events, _self.options, _self.getConfiguration()]);
        }
      });
    }
  });
});
