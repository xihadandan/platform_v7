(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['mui', 'commons', 'server', 'constant', 'mui-wWidget', 'appContext', 'formBuilder', 'moment', 'appModal'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($, commons, server, constant, widget, appContext, formBuilder, moment, appModal) {
  'use strict';
  var UUID = commons.UUID;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var SpringSecurityUtils = server.SpringSecurityUtils;

  var _viewStart = moment().startOf('month').format('YYYY-MM-DD HH:mm:ss');
  var _viewEnd = moment().endOf('month').format('YYYY-MM-DD HH:mm:ss');

  var _minTime = '08:00';
  var _maxTime = '18:00';
  var calendarBean = {
    id: null,
    title: null,
    allDay: null,
    start: null,
    end: null,
    url: null,
    className: null,
    color: null,
    backgroundColor: null,
    borderColor: null,
    textColor: null,
    uuid: null,
    recVer: null,
    creator: null,
    createTime: null,
    modifier: null,
    modifyTime: null,
    attach: null,
    startTime: null,
    endTime: null,
    eventContent: null,
    belongObjId: null,
    address: null,
    fileUuids: null,
    joinUsers: null,
    publicRange: null,
    noticeTypes: null,
    noticeObjs: null,
    repeatMarkId: null,
    isAll: null,
    isRemind: null,
    isRepeat: null,
    repeatConf: null,
    remindConf: null,
    isFinish: null,
    repeatPeriodStartTime: null,
    repeatPeriodEndTime: null,
    calendarCreator: null,
    calendarCreatorName: null,
    isCanView: null,
    remindTime: null,
    remindStatus: null,
    partUsers: null
  };
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
    eventLimit: true, // 限制显示在一天的事件数,当事件太多时，会显示一个看起来像“+ 2更多”,
    views: {
      month: {
        titleFormat: 'YYYY年MM月'
      },
      week: {
        titleFormat: 'YYYY年MM月DD日',
        columnFormat: 'M.D dddd'
      },
      day: {
        titleFormat: 'YYYY年MM月DD日 dddd',
        columnFormat: 'M/D dddd'
      }
    },
    allDaySlot: true,
    allDayDefault: false,
    slotEventOverlap: true, // 设置日程视图中的事件是否可以重叠，值为布尔类型，默认值为true，事件会相互重叠，最多一半会被遮住。
    minTime: _minTime, // 设置日程开始最小时间
    maxTime: _maxTime, // 设置日程最大时间
    lazyFetching: false, // 不需要缓存，每次切换视图都从新获取数据
    timeFormat: 'HH:mm',
    displayEventTime: true, // 显示事件时间
    displayEventEnd: true
    // 并且显示事件的结束时间
  };
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

  $.widget('mui.wMobileCalendar', $.ui.wWidget, {
    $calendarElement: null, // 日历面板
    $resourceElement: null, // 资源面板
    groupDataMap: {}, // 分组的数据，用来实现资源视图分组的时候ID转换成name， 格式必须为{ key:name }
    _dataSource: [], // 存放当前正在使用的数据源，供给资源视图使用，这样就不需要每次切换视图都需要重新获取数据
    ajaxParams: {}, // 加载数据时需要传递的其他参数
    isValidePass: false, // 每次保存事项的时候，根据这个值来确定是否数据验证通过
    _validator: null, // 表单验证器
    options: {
      // 重复的类型
      repeatTypeOptions: {
        day: '每天',
        week: '每周',
        month: '每月',
        year: '每年'
      },
      // 每天重复的选项
      repeatDayOptions: {
        work: '工作日',
        sat: '星期六',
        sun: '星期日'
      },
      // 每周重复的选项
      repeatWeekOptions: {
        all: '全部',
        w1: '周一',
        w2: '周二',
        w3: '周三',
        w4: '周四',
        w5: '周五',
        w6: '周六',
        w7: '周日'
      },

      // 提醒的间隔时间
      intervalOptions: new Array(1, 2, 3, 4, 5, 10, 15, 20, 30),
      // 提醒的间隔时间单位
      unitOptions: {
        min: '分钟',
        hour: '小时',
        day: '天',
        week: '周',
        month: '月'
      },
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

      // _self._beforeRenderView();
      _self.invokeDevelopmentMethod('beforeRender', [_self.options, _self.getConfiguration()]);
      _self._renderView();
      _self.invokeDevelopmentMethod('afterRender', [this.options, this.getConfiguration()]);
      // _self._afterRenderView();
      // _self._setButtonEvent();
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
        // $element.width(configuration.width);
      }
      if (StringUtils.isNotBlank(configuration.height)) {
        // $element.height(configuration.height);
      }
      // 渲染头部按钮
      // this._renderButton($element);
      // 渲染头部搜索条件
      // this._renderSearchBar($element);
      var content = new StringBuilder();
      // content.appendFormat('<link
      // href="{0}/mobile/mui/css/calendar.css" rel="stylesheet" />',
      // ctx);
      content.append('<div class="calendar-box" id="calendar-box"></div>');
      content.append('<h4 class="calendar-day" id="calendar-day"></h4>');
      content.append('<ul class="mui-table-view mui-table-view-striped mui-table-view-condensed calendar-list"  id="calendar-list"></ul>');
      $element[0].innerHTML = content.toString();
      var curr_time = new Date();
      var year = curr_time.getFullYear();
      var month = curr_time.getMonth() + 1;
      // 日历插件
      _self.calendar = mui('.calendar-box').calendar({
        clickCallback: function (changeGroup, calendar) {
          _self._renderListView(changeGroup, calendar);
        }, // 点击回调
        dragCallback: function () {}, // 滑动回调
        time: {
          // 初始化传递时间
          year: year,
          month: month
        },
        year_unit: '年',
        month_unit: '月',
        day_unit: '日',
        showdate: 'this-time' // 时间显示面板
      });

      mui('.calendar-list').on('tap', 'a[name=ope-item-del]', function (event) {
        var viewCell = $(this).closest('.mui-table-view-cell[id]');
        var cellId = viewCell.getAttribute('id');
        event.preventDefault();
        event.stopPropagation();
        var dataItem = _self.getDataById(cellId);
        _self.invokeDevelopmentMethod('deleteEventClick', [dataItem, _self.options, _self.getConfiguration()]);
        $.swipeoutClose(viewCell);
      });
      mui('.calendar-list').on('tap', 'a[name=ope-item-finish]', function (event) {
        var viewCell = $(this).closest('.mui-table-view-cell[id]');
        var cellId = viewCell.getAttribute('id');
        event.preventDefault();
        event.stopPropagation();
        var dataItem = _self.getDataById(cellId);
        var isFinish = dataItem.isFinish == 1 ? 0 : 1;
        server.JDS.call({
          service: 'calendarFacade.updateEventStatus',
          data: [cellId, isFinish],
          success: function (result) {
            if (result.success) {
              $.toast('标记成功');
              _self.refresh();
            }
          }
        });
        $.swipeoutClose(viewCell);
      });
      mui('.calendar-list').on('tap', '.mui-table-view-cell[id]', function (event) {
        var viewCell = this;
        var cellId = viewCell.getAttribute('id');
        event.preventDefault();
        event.stopPropagation();
        var dataItem = _self.getDataById(cellId);
        _self._renderViewForm(dataItem);
      });

      var navMenuTemplate = '<button class="mui-btn mui-btn-link">新建</button>';
      mui.ui.showAndAddRightNavEventListener(
        'tap',
        function (event) {
          event.preventDefault();
          event.stopPropagation();
          var now = new Date();
          var end = new Date(now.getTime() + 1000 * 60 * 30);
          _self._renderItemForm({
            repeatPeriodStartTime: now.format('yyyy-MM-dd HH:mm'),
            repeatPeriodEndTime: end.format('yyyy-MM-dd HH:mm')
          });
        },
        navMenuTemplate,
        $element.closest('.panel')
      );
      // 渲染日历面板
      if (Boolean(configuration.enableCalendarView)) {
        // this.$calendarElement = this._renderCalendarView($panelDiv);
      }
      // 渲染资源面板
      if (Boolean(configuration.enableResourceView)) {
        // 资源视图，需要分组信息，获取资源分组的MAP数据
        _self.invokeDevelopmentMethod('getGroupDataMap', [_self.options, _self.getConfiguration()]);
        // this.$resourceElement = this._renderResourceView($panelDiv);
      }
      if (Boolean(configuration.enableCalendarView) && Boolean(configuration.enableResourceView)) {
        // _self.$resourceElement.hide();
      }
      // 渲染视图切换选择器
      // this._renderSelectView($element);
    },

    _renderListView: function (changeGroup, calendar) {
      var _self = this;
      var activeDay = calendar.getActiveDay();
      if (StringUtils.isNotBlank(activeDay)) {
        var _viewStart = activeDay + ' 00:00:00';
        var _viewEnd = activeDay + ' 23:59:59';

        var dataSource = _self.getDataSource({
          startTime: _viewStart,
          endTime: _viewEnd
        });
        var content = new StringBuilder();
        $.each(dataSource || [], function (idx, item) {
          if (item.isCanView === false) {
            // 权限过滤
            return;
          }
          content.appendFormat('<li class="mui-table-view-cell" id="{0}">', item.id || item.uuid);
          content.append('<div class="mui-slider-right">');
          content.appendFormat(
            '<a class="mui-btn mui-icon mui-btn-default" name="ope-item-finish">{0}</a>',
            item.isFinish ? '未完成' : '完成'
          );
          content.append('<a class="mui-btn mui-icon mui-btn-danger" name="ope-item-del">删除</a>');
          content.append('</div>');
          content.appendFormat(
            '<div class="{0} mui-table">',
            item.calendarCreator === SpringSecurityUtils.getCurrentUserId() ? 'mui-slider-handle' : ''
          );
          content.append('<div class="mui-table-cell mui-col-xs-12">');
          content.appendFormat(
            '<h4 class="mui-ellipsis" style="{1}">标题：{0}</h4>',
            item.title || '',
            item.isFinish ? '' : 'color:green;'
          );
          content.appendFormat('<h5>开始：{0}</h5>', item.startTime || '');
          content.appendFormat('<h5>结束：{0}</h5>', item.endTime || '');
          content.appendFormat('<p class="mui-h6 mui-ellipsis">所属：{0}</p>', item.calendarCreatorName || '');
          content.append('</div>');
          content.append('</div>');
          content.append('</li>');
        });
        mui('.calendar-day')[0].innerHTML = activeDay;
        mui('.calendar-list')[0].innerHTML = content.toString();
      }
    },

    _renderSelectView: function ($element) {
      var _self = this;
      var configuration = this.getConfiguration();
      var $leftBtnGroup = $('.fc-left .btn-group');
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

      return $calendarElement;
    },

    getDataById: function (id) {
      var self = this;
      var dataSource = self._dataSource;
      for (var i = 0; i < dataSource.length; i++) {
        var item = dataSource[i];
        if (item.id === id || item.uuid === id) {
          return item;
        }
      }
    },

    /**
     * 获取日历视图数据源对象
     */
    getDataSource: function (params) {
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
            _self._dataSource = []; // 获取到新数据，需要先清空下
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
                  // allDay要生效，data.end必须为null, calendar组件的BUG
                  data.end = null;
                }
              }
              _self._dataSource.push(data);
            });
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
        params: $.extend(_self.ajaxParams, {
          // from : "attention"
        }),
        criterions: _self._collectCriterion() || [],
        orders: [],
        startTime: params.startTime,
        endTime: params.endTime
      };

      options.criterions = options.criterions.concat(_self.otherConditions);
      options.criterions = options.criterions.concat(_self.getDefaultConditions());

      return options;
    },

    // 将日历资源数据转换成资源视图数据
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
     * 渲染字段查询
     */
    _renderFieldSearch: function () {
      var _self = this;
      var $element = $(_self.element);
      var configuration = _self.getConfiguration();
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
    },
    /**
     * 渲染查询按钮/搜索
     */
    _renderSearchBar: function ($element) {
      var _self = this;
      var options = this.options;
      var configuration = this.getConfiguration();
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
      return criterions;
    },
    /**
     * 校验提交的数据
     */
    validateEventData: function (data) {
      var _self = this;
      var errorMsg = null;
      if (StringUtils.isBlank(data.title)) {
        errorMsg = '标题不能为空';
      } else if (StringUtils.isBlank(data.belongObjId)) {
        errorMsg = '日程归属必选';
      } else if (StringUtils.isBlank(data['repeatPeriodStartTime'])) {
        errorMsg = '开始时间不能为空';
      } else if (data['repeatPeriodEndTime'] && data['repeatPeriodStartTime'] > data['repeatPeriodEndTime']) {
        errorMsg = '开始时间不能大于结束时间';
      }
      if (StringUtils.isNotBlank(errorMsg)) {
        $.toast(errorMsg);
        return false;
      }
      if (_self._executeJsModuleWithResult('otherValidateRules', [data]) === false) {
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
      _self._renderListView([], _self.calendar);
    },
    /**
     * 查询数据
     */
    query: function () {
      this.$calendarElement.fullCalendar(
        'refetchEventSources',
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
    },
    /**
     * 需要查询时调用
     */
    _onSearch: function () {
      this.refresh();
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
    _renderViewForm: function (dataItem) {
      var _self = this;
      var currentUserId = SpringSecurityUtils.getCurrentUserId();
      var configuration = _self.getConfiguration();
      if (configuration.columns) {
        var $itemForm = '#div_calendar_view_form';
        var editFieldOptions = {
          container: $itemForm + ' .mui-content',
          inputClass: 'w-itemform-option',
          rowClass: 'js-item-row',
          labelColSpan: '4',
          controlColSpan: '8',
          multiLine: false, // 一行一列/一行两列
          contentItems: []
        };
        $.each(configuration.columns, function (i, field) {
          if (field.hidden == '0') {
            var defaultValue;
            if (typeof event != 'undefined') {
              defaultValue = dataItem[field.name];
            }
            if (field.name === 'belongObjId' && dataItem.calendarCreator !== currentUserId) {
              return;
            }
            var controlOption = field.controlOptions;
            var controlType = controlOption.queryType;
            var editItemOptions = {
              outline: true,
              readonly: true,
              label: field.title,
              name: field.name, // 加前缀区分字段，否则造成formBuilder渲染表单控件冲突
              type: controlType,
              value: defaultValue || field.defaultValue,
              controlOption: controlOption
            };
            editFieldOptions.contentItems.push(editItemOptions);
            if (StringUtils.contains(field.name, 'address')) {
              editFieldOptions.contentItems.push({
                type: 'seperate'
              });
            } else if (StringUtils.contains(field.name, 'title')) {
              editFieldOptions.contentItems.push({
                readonly: true,
                label: '全天',
                value: dataItem['allDay'],
                type: 'switch',
                name: 'isAll'
              });
            }
          }
        });
        var optButton = null;
        if (dataItem.calendarCreator === currentUserId || dataItem.creator === currentUserId) {
          optButton = {
            label: '<i class="mui-icon mui-icon-more" style="padding: 0px 12px;"></i>',
            actionSheet: [
              {
                id: 'editCalender',
                name: 'editCalender',
                text: '编辑',
                callback: function (event, panel) {
                  $.ui.goBack();
                  _self._renderItemForm(dataItem, '日程详情');
                }
              },
              {
                id: 'deleteCalender',
                name: 'deleteCalender',
                cssClass: 'btn-danger',
                text: '删除',
                callback: function (event, panel) {
                  _self.invokeDevelopmentMethod('deleteEventClick', [
                    dataItem,
                    _self.options,
                    _self.getConfiguration(),
                    function () {
                      // 删除成功后返回,确认框消失后重新入列
                      setTimeout(function () {
                        $.ui.goBack();
                      }, 0);
                    }
                  ]);
                }
              }
            ],
            callback: function (event, panel) {
              return false;
            }
          };
        }
        var panel = formBuilder.showPanel({
          title: '日程详情',
          actionBack: {
            showNavTitle: true
          },
          optButton: optButton,
          content: '',
          container: $itemForm,
          shown: function (options) {}
        });

        $.extend(_self.options, {
          event: dataItem
        });
        formBuilder.buildContent(editFieldOptions);
        _self.invokeDevelopmentMethod('afterEventDialogRender', [panel, _self.options, _self.getConfiguration()]);
        return $itemForm;
      }
      return '';
    },
    /**
     * 渲染新建事项表单
     */
    _renderItemForm: function (dataItem, title) {
      var _self = this;
      var configuration = _self.getConfiguration();
      if (configuration.columns) {
        var $itemForm = '#div_calendar_item_form';
        var editFieldOptions = {
          container: $itemForm + ' .mui-content',
          inputClass: 'w-itemform-option',
          rowClass: 'js-item-row',
          labelColSpan: '4',
          controlColSpan: '8',
          multiLine: false, // 一行一列/一行两列
          contentItems: []
        };
        $.each(configuration.columns, function (i, field) {
          if (field.hidden == '0') {
            var defaultValue;
            if (typeof event != 'undefined') {
              defaultValue = dataItem[field.name];
            }

            var controlOption = field.controlOptions;
            var controlType = controlOption.queryType;
            var editItemOptions = {
              outline: true,
              label: field.title,
              name: field.name, // 加前缀区分字段，否则造成formBuilder渲染表单控件冲突
              type: controlType,
              value: defaultValue || field.defaultValue,
              controlOption: controlOption
            };
            editFieldOptions.contentItems.push(editItemOptions);
            if (StringUtils.contains(field.name, 'address')) {
              editFieldOptions.contentItems.push({
                type: 'seperate'
              });
            } else if (StringUtils.contains(field.name, 'title')) {
              editFieldOptions.contentItems.push({
                label: '全天',
                value: dataItem['allDay'],
                type: 'switch',
                name: 'isAll'
              });
            }
          }
        });
        var panel = formBuilder.showPanel({
          title: title || '新建日程',
          actionBack: {
            label: '取消'
          },
          optButton: {
            label: '完成',
            callback: function (event, panel) {
              var options = {
                event: dataItem
              };
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
          },
          content: '',
          container: $itemForm,
          shown: function (options) {}
        });

        $.extend(_self.options, {
          event: dataItem
        });
        formBuilder.buildContent(editFieldOptions);
        _self.invokeDevelopmentMethod('afterEventDialogRender', [panel, _self.options, _self.getConfiguration()]);
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
        title: options.event.uuid ? '编辑事项' : '新建事项',
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

          // 添加表单的校验规则
          _self.initFormValideRule();
        },
        buttons: _thisButtons
      });
      return $dialog;
    },

    // 定义表单的校验规则
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
        }
      };
      var configuration = this.getConfiguration();
      var columnRules = [];
      $.each(configuration.columns, function (i, field) {
        if (field.hidden == '0') {
          // 有设置校验规则，则添加校验器
          var regex = field.validateRules.validateRegex;
          if (StringUtils.isNotBlank(regex)) {
            var method = 'check' + field.name + 'Format';
            var errMsg = field.title + '格式不对。';
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

      // 开放添加校验器的路口
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
        title: '详情',
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
    },
    /**
     * 保存事项
     */
    _saveEvent: function (event, fnCallback) {
      var success = false;
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
          success = result.success;
          if (result.success) {
            if (typeof fnCallback == 'function') {
              fnCallback.call(this, result);
            }
          } else {
            appModal.error(result.msg);
          }
        },
        error: function (error) {
          appModal.error('保存失败:' + error.statusText);
        }
      });
      return success;
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
          if ($.isFunction(fnCallback) && fnCallback.apply(this, arguments) === false) {
            return;
          }
          appModal.toast('删除成功');
        },
        error: function (error) {
          appModal.error(error);
        }
      });
    },

    // 收集事项表单数据
    _collectEventData: function (options) {
      var _self = this;
      var formData = $.extend(
        {
          repeatDay: null,
          repeatWeek: null,
          repeatMonth: null,
          repeatYear: null,
          interval: null,
          intervalUnit: null,
          remind: 0,
          repeat: 0
        },
        calendarBean
      );
      var formData = $(options.$el).form2json(formData);
      formData['uuid'] = options.event.uuid;
      // 标准化开始时间和结束时间格式
      if (formData.repeatPeriodStartTime) {
        formData['repeatPeriodStartTime'] = formData['startTime'] = commons.DateUtils.format(
          new Date(formData['repeatPeriodStartTime']),
          'yyyy-MM-dd HH:mm:ss'
        );
      }
      if (formData.repeatPeriodEndTime) {
        formData['repeatPeriodEndTime'] = formData['endTime'] = commons.DateUtils.format(
          new Date(formData['repeatPeriodEndTime']),
          'yyyy-MM-dd HH:mm:ss'
        );
      }
      formData.isRepeat = StringUtils.isBlank(formData.repeat) ? 0 : 1;
      formData.isRemind = StringUtils.isBlank(formData.remind) ? 0 : 1;

      return formData;
    },

    /**
     * 弹窗表单"确认"
     */
    _formOkClick: function (options) {
      var _self = this;
      var newFormData = _self._collectEventData(options);
      _self.invokeDevelopmentMethod('afterCollectEventData', [options, _self.getConfiguration(), newFormData]);
      // 数据校验
      if (_self.validateEventData(newFormData)) {
        // 数据保存
        return _self._saveEvent(newFormData, function (result) {
          // 因为允许批量添加重复的事项，所以每次保存完后，就直接刷新数据，
          _self.invokeDevelopmentMethod('afterSaveEvent', [_self.options, _self.getConfiguration(), options]);
          // 重新刷新数据
          appModal.success('保存成功！');
          _self.refresh();
        });
        // return false;
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

    // 更新事项数据
    _updateEvent: function (event) {
      var _self = this;
      if (StringUtils.isNotBlank(event.uuid)) {
        _self.$calendarElement.fullCalendar('updateEvent', event);
      } else {
        _self.$calendarElement.fullCalendar('renderEvent', event);
      }
    },

    // 默认的删除事项事件
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
      // console.log(events);
      var $allEventBox = $('<div id="div_event_list"/>');
    }
  });
});
