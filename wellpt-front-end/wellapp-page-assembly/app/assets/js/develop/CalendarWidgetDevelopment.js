define(['constant', 'commons', 'server', 'WidgetDevelopment', 'moment'], function (constant, commons, server, WidgetDevelopment, moment) {
  // 日历组件二开基础
  var CalendarWidgetDevelopment = function () {
    WidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(CalendarWidgetDevelopment, WidgetDevelopment, {});
  //用来获取资源视图那边需要的分组信息，如果分组的字段是ID的话，这里就需要实现这个接口，获取对应的分组数据
  CalendarWidgetDevelopment.prototype.getGroupDataMap = function (options, configuration) {
    //获取完数据后，需要执行下面这句话，来设置数据
    //this.getWidget().groupDataMap = 获取到的map数据
  };
  /** 日历组件渲染前回调方法 */
  CalendarWidgetDevelopment.prototype.beforeRender = function (options, configuration) {};
  /** 日历组件渲染完成回调方法 */
  CalendarWidgetDevelopment.prototype.afterRender = function (options, configuration) {};
  /** 新建和编辑日历事项弹窗渲染完后的回调方法 */
  CalendarWidgetDevelopment.prototype.afterEventDialogRender = function ($dialog, options, configuration) {};
  /** 查看日历事项弹窗渲染完后的回调方法 */
  CalendarWidgetDevelopment.prototype.afterViewEventDialogRender = function ($dialog, options, configuration) {};
  /** 查看日历事项列表弹窗设置前的回调方法 */
  CalendarWidgetDevelopment.prototype.beforeEventListDialogRender = function (events, options, configuration) {};
  /** 查看日历事项列表弹窗渲染完后的回调方法 */
  CalendarWidgetDevelopment.prototype.afterEventListDialogRender = function ($dialog, events, options, configuration) {};
  /** 新增，编辑日程弹出的表单，添加表单校验器 */
  CalendarWidgetDevelopment.prototype.addOtherValidateRules = function (validateOptions, configuration) {};

  /** 日历事项表单数据收集完后的回调方法，用来收集其他隐藏字段的数据，以及调整数据格式 */
  CalendarWidgetDevelopment.prototype.afterCollectEventData = function (options, configuration, newFormData) {};
  /** 获取日历事项表单数据收集完后的回调方法的返回值 */
  CalendarWidgetDevelopment.prototype.getAfterCollectEventDataCB = function (options, configuration, newFormData) {};
  /** 监听左导航点击事项的处理,用来配合左导航做过滤 */
  CalendarWidgetDevelopment.prototype.onLeftSidebarItemClick = function (menuItemEvent, options, configuration) {
    var belongObjId = menuItemEvent.detail.selectedItem.value;
    if (commons.StringUtils.isNotBlank(belongObjId)) {
      // 清空其他的搜索添加
      this.clearOtherConditions();
      var filterCriterion = {
        columnIndex: 'belongObjId',
        value: belongObjId,
        type: 'eq'
      };

      this.addOtherConditions([filterCriterion]);
      //刷新页面
      this.getWidget().refresh();
    }
  };
  /** 日历事项单击事件 */
  CalendarWidgetDevelopment.prototype.dayClick = function (date, jsEvent, view) {
    var startTime = date.format('YYYY-MM-DD HH:mm');
    var endTime = date.add(30, 'minutes').format('YYYY-MM-DD HH:mm');
    // date.startTime = startTime;
    // date.endTime = endTime;
    var event = {
      startTime: startTime,
      endTime: endTime
    };
    var $dialog = this.getWidget().openEventDialog({
      title: this.getTitle(this.getWidget().getConfiguration(), event, jsEvent, view),
      date: date,
      jsEvent: jsEvent,
      view: view,
      event: event
    });
    return $dialog;
  };

  /** 获取title 主要用在详情等弹框标题使用*/
  CalendarWidgetDevelopment.prototype.getTitle = function (configuration, event, jsEvent, view) {};

  /** 日历事项查看事件 */
  CalendarWidgetDevelopment.prototype.eventClick = function (event, jsEvent, view) {
    var $dialog = this.getWidget()._viewEvent({
      title: this.getTitle(this.getWidget().getConfiguration(), event, jsEvent, view),
      event: event,
      jsEvent: jsEvent,
      view: view
    });
    return $dialog;
  };

  /** 资源面板的已排事项的点击事件 */
  CalendarWidgetDevelopment.prototype.resouceEventListClick = function (events, view) {
    // this.getWidget()._showEventList(events);
  };

  /** 删除按钮点击事件定义 * */
  CalendarWidgetDevelopment.prototype.deleteEventClick = function (event, configuration, options) {
    this.getWidget()._defaultDeleteEvent(event);
  };

  /** 获取字段校验规则 */
  CalendarWidgetDevelopment.prototype.getColumnValidataRule = function () {
    return this.getWidget().getColumnValidataRule();
  };
  /** 获取视图参数 */
  CalendarWidgetDevelopment.prototype.getParam = function (key) {
    return this.getWidget().getParam(key);
  };
  /** 获取视图所有参数 */
  CalendarWidgetDevelopment.prototype.getDataParams = function () {
    return this.getWidget().getDataParams();
  };
  /** 根据唯一键获取事项数据 */
  CalendarWidgetDevelopment.prototype.getEventByUniqueId = function (id) {
    return this.getWidget().getEventByUniqueId(id);
  };
  /** 刷新数据 */
  CalendarWidgetDevelopment.prototype.refresh = function () {
    return this.getWidget().refresh();
  };
  /** 重新计算（设置）视图高度 */
  CalendarWidgetDevelopment.prototype.resetHeight = function (height) {
    return this.getWidget().resetHeight(height);
  };
  /**
   * 获取视图定义信息
   */
  CalendarWidgetDevelopment.prototype.getViewConfiguration = function () {
    return this.getWidget().getConfiguration();
  };
  /**
   * 获取数据源集合
   */
  CalendarWidgetDevelopment.prototype.getDataSource = function () {
    return this.getWidget().getDataSource();
  };
  /**
   * 获取视图数据集合
   */
  CalendarWidgetDevelopment.prototype.getData = function () {
    return this.getWidget().getData();
  };

  /**
   * 添加额外的查询条件, conditions 是个数组
   */
  CalendarWidgetDevelopment.prototype.addOtherConditions = function (conditions) {
    this.getWidget().addOtherConditions(conditions);
  };
  /** 情况额外查询条件,condition为空是清除全部，否则清楚等于condition的一条额外查询条件 */
  CalendarWidgetDevelopment.prototype.clearOtherConditions = function (condition) {
    this.getWidget().clearOtherConditions(condition);
  };
  /** 获取日历组件对象 */
  CalendarWidgetDevelopment.prototype.getWidget = function (condition) {
    return this.widget;
  };
  /** 当日程事件被渲染到日程表后触发 */
  CalendarWidgetDevelopment.prototype.eventAfterRender = function (event, element, view) {};
  /** 当所有事件完成渲染后触发 */
  CalendarWidgetDevelopment.prototype.eventAfterAllRender = function (view) {};
  /** 当事件元素从DOM中删除之前触发 */
  CalendarWidgetDevelopment.prototype.eventDestroy = function (event, element, view) {};
  /** 当事件开始拖动时触发 */
  CalendarWidgetDevelopment.prototype.eventDragStart = function (event, jsEvent, ui, view) {};
  /** 当事件拖动停止后触发 */
  CalendarWidgetDevelopment.prototype.eventDragStop = function (event, jsEvent, ui, view) {};
  /** 当拖动结束且日程移动另一个时间时触发 */
  CalendarWidgetDevelopment.prototype.eventDrop = function (event, jsEvent, ui, view) {};
  /** 新增事项前事件触发 */
  CalendarWidgetDevelopment.prototype.beforeDayClick = function (date, jsEvent, view, configuration) {};
  /** 事项保存(确定)后触发 */
  CalendarWidgetDevelopment.prototype.afterSaveEvent = function (options, configuration, eventOptions) {};
  /** 事项删除后触发 */
  CalendarWidgetDevelopment.prototype.afterDeleteEvent = function (event, options, configuration) {};
  /** 获取数据源后,对数据源进行加工 */
  CalendarWidgetDevelopment.prototype.dataSourceProcessing = function (data) {};

  /** 切换视图后触发 */
  (CalendarWidgetDevelopment.prototype.afterChangeViewRender = function (event, options, configuration) {}),
    /** 监听其他组件触发日历组件变化事件,可以用来配合做过滤 */
    (CalendarWidgetDevelopment.prototype.onWidgetToRefreshCalendar = function (event, arg, options, configuration) {});
  /** 选择面板，选定时间后事件-新增事项 */
  CalendarWidgetDevelopment.prototype.afterSelectResourseRender = function (data, options, configuration) {};
  /** 选择面板，自定义选定时间后事件 */
  CalendarWidgetDevelopment.prototype.onSelectCallback = function (data, options, configuration) {};
  /** 资源-展示数据*/
  CalendarWidgetDevelopment.prototype.loadResourceDataSuccess = function (options, data) {};
  /** 资源显示实际时间-展示数据-合并事项且设置样式配置前数据 */
  CalendarWidgetDevelopment.prototype.loadResourceDataBefore = function (options, data, mergedData) {};
  /** 资源显示实际时间-展示数据-合并事项且设置样式配置后数据 */
  CalendarWidgetDevelopment.prototype.loadResourceDataAfter = function (options, data, mergedData) {};
  /** 资源视图，已排事件详情弹出框前处理 */
  CalendarWidgetDevelopment.prototype.beforeResourceEventsPopoverRender = function (options, data, mergedData) {};
  /** 资源视图，左侧资源标题详情弹出框前处理 */
  CalendarWidgetDevelopment.prototype.beforeResourceGroupPopoverRender = function (options, data, mergedData) {};

  /** 状态过滤弹窗，点击确定事件 */
  CalendarWidgetDevelopment.prototype.statusFilter = function (status, dateRange) {
    var _self = this;
    var widget = this.getWidget();
    var configuration = widget.getConfiguration();
    var eventStatusFieldName = configuration.eventStatusFieldName || 'isFinish';
    _self.clearOtherConditions([eventStatusFieldName]);

    if (status && status.length) {
      var filterCriterion = {
        columnIndex: eventStatusFieldName,
        value: status,
        type: 'in'
      };
      _self.addOtherConditions([filterCriterion]);
    }

    var _viewStart, _viewEnd;
    if (dateRange && dateRange[0]) {
      _viewStart = moment(dateRange[0] + ' 00:00:00').format('YYYY-MM-DD HH:mm:ss');
    }

    if (dateRange && dateRange[1]) {
      _viewEnd = moment(dateRange[1] + ' 23:59:59').format('YYYY-MM-DD HH:mm:ss');
    }

    _self.getWidget().query({
      start: _viewStart,
      end: _viewEnd
    });
  };

  return CalendarWidgetDevelopment;
});
