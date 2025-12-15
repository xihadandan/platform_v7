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
      'dataStoreBase',
      'layDate',
      'select2',
      'wSelect2'
    ], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function (
  $,
  commons,
  server,
  constant,
  appContext,
  DesignUtils,
  formBuilder,
  moment,
  appModal,
  CalendarDevelopment,
  WellCalendar,
  DataStore,
  layDate
) {
  'use strict';
  var UUID = commons.UUID;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;

  function prefixZero(num) {
    return ('0' + num).slice(-2);
  }

  function hexToRgb(color) {
    if (/#.{6}/.test(color)) {
      var matched = color.match(/#(.{2})(.{2})(.{2})/);

      return [parseInt(matched[1], 16), parseInt(matched[2], 16), parseInt(matched[3], 16)];
    }
  }

  function toRgba(color, alpha) {
    var rgba = [].concat(color);
    if (typeof color === 'string') {
      rgba = hexToRgb(color);
    }

    if (typeof alpha !== 'number') {
      alpha = 1.0;
    }

    rgba.push(alpha);

    return 'rgba(' + rgba.map(String).join(', ') + ')';
  }

  function hexify(color) {
    var values = color
      .replace(/rgba?\(/, '')
      .replace(/\)/, '')
      .replace(/[\s+]/g, '')
      .split(',');
    var a = parseFloat(values[3] || 1),
      r = Math.floor(a * parseInt(values[0]) + (1 - a) * 255),
      g = Math.floor(a * parseInt(values[1]) + (1 - a) * 255),
      b = Math.floor(a * parseInt(values[2]) + (1 - a) * 255);
    return '#' + ('0' + r.toString(16)).slice(-2) + ('0' + g.toString(16)).slice(-2) + ('0' + b.toString(16)).slice(-2);
  }

  var _viewStart = moment().startOf('month').format('YYYY-MM-DD HH:mm:ss');
  var _viewEnd = moment().endOf('month').format('YYYY-MM-DD HH:mm:ss');
  var _minTime = '08:00';
  var _maxTime = '18:00';

  var defaultOptions = {
    themeSystem: 'bootstrap3',
    customButtons: {
      viewsFilter: {
        text: ' ', // '视图切换'
        bootstrapGlyphicon: ''
      },
      statusFilter: {
        text: '状态',
        bootstrapGlyphicon: 'iconfont icon-ptkj-shaixuan'
      },
      timeSetting: {
        text: '设置',
        bootstrapGlyphicon: 'iconfont icon-ptkj-shezhi'
      }
    },
    header: {
      left: 'agendaDay,agendaWeek,month today',
      center: 'prev title next',
      right: 'statusFilter timeSetting viewsFilter'
    },
    bootstrapGlyphicons: {
      close: 'iconfont icon-ptkj-dacha-xiao',
      prev: 'iconfont icon-ptkj-xianmiaojiantou-zuo',
      next: 'iconfont icon-ptkj-xianmiaojiantou-you',
      prevYear: 'iconfont icon-ptkj-xianmiaoshuangjiantou-zuo',
      nextYear: 'iconfont icon-ptkj-xianmiaoshuangjiantou-you'
    },
    firstDay: 1, // 0~6 (周日、周一~~周六)， 一设置每周的第一天为 周
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
        titleFormat: 'YYYY年MM月DD日 W周',
        columnFormat: 'M.D dddd'
      },
      day: {
        titleFormat: 'YYYY年MM月DD日 ddd',
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
    contentHeight: 'auto'
  };

  var eventObject = {
    id: '',
    title: '',
    allDay: false,
    start: '', // 必填
    end: '',
    url: '',
    className: null,
    color: '',
    backgroundColor: getUserTheme().colorValue ? getUserTheme().colorValue + ' !important' : '#a3c5f6 !important', //默认主题色，没有的话用#a3c5f6
    borderColor: '',
    textColor: ''
  };

  // 默认弹窗按钮
  var dialogButtons = {
    cancel: {
      label: '关闭',
      className: 'well-btn w-btn-primary w-line-btn no-order',
      callback: function () {
        $('.layui-laydate').remove(); //时间控件弹出窗
      }
    }
  };

  $.widget('ui.wMyCalendar', $.ui.wWidget, {
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

    _preConfiguration: function (configuration) {
      $.each(['calendar', 'resource'], function (idx, type) {
        var conf = configuration[type];
        for (var key in conf) {
          var finalKey = key.replace(/calendar|resource/gi, '');
          if (finalKey === key) {
            continue;
          }
          if (null == conf[finalKey]) {
            conf[finalKey] = conf[key];
          }
        }
      });

      // 设置状态颜色配置
      var beanValueList = [];
      $.ajax({
        type: 'GET',
        url: '/api/webapp/color/setting/getAllBean',
        dataType: 'json',
        async: false,
        success: function (result) {
          beanValueList = result.data;
        }
      });

      var calendarColorVals = [];
      $.each(beanValueList, function (i, datum) {
        if (configuration.calendar.calendarStatusKey === datum.moduleCode + '_' + datum.type) {
          calendarColorVals = datum.valueList;
        }
      });
      var calendarStatus = $.map(calendarColorVals, function (elem, idx) {
        return {
          code: elem.id,
          text: elem.name,
          color: {
            iconColor: elem.color,
            className: ''
          }
        };
      });

      if (configuration.calendar && configuration.calendar.calendarStatusKey) {
        configuration.calendar.status = calendarStatus;
      }

      var resourceColorVals = [];
      $.each(beanValueList, function (i, datum) {
        if (configuration.resource.resourceStatusKey === datum.moduleCode + '_' + datum.type) {
          resourceColorVals = datum.valueList;
        }
      });
      var resourceStatus = $.map(resourceColorVals, function (elem, idx) {
        return {
          code: elem.id,
          text: elem.name,
          color: {
            iconColor: elem.color,
            className: ''
          }
        };
      });

      if (configuration.resource && configuration.resource.resourceStatusKey) {
        configuration.resource.status = resourceStatus;
      }
    },

    /** 创建组件 */
    _createView: function () {
      // 创建js数据源对象
      var _self = this;
      var configuration = _self.getConfiguration();
      _self._preConfiguration(configuration);
      _self.otherOrders = _self.otherOrders || {};
      _self.otherConditions = _self.otherConditions || [];

      _self._beforeRenderView();
      _self._renderView();
      _self._afterRenderView();
      _self._setButtonEvent();

      // 页面容器监听左导航事件
      _self.pageContainer.on(constant.WIDGET_EVENT.LeftSidebarItemClick, function (e, ui) {
        _self.invokeDevelopmentMethod('onLeftSidebarItemClick', [e, _self.options, configuration]);
      });

      // 页面容器监听日历需要刷新事件
      _self.pageContainer.on('onWidgetToRefreshCalendar', function (e, ui, arg) {
        _self.invokeDevelopmentMethod('onWidgetToRefreshCalendar', [e, arg, _self.options, configuration]);
      });
    },

    /** 渲染视图前 */
    _beforeRenderView: function () {
      var _self = this;
      _self.invokeDevelopmentMethod('beforeRender', [_self.options, _self.getConfiguration()]);
    },

    /** 视图组件渲染结束调用 */
    _afterRenderView: function () {
      var _self = this;
      _self.addCurrentTimeLine();
      _self.invokeDevelopmentMethod('afterRender', [_self.options, _self.getConfiguration()]);
    },

    /** 图组件渲染主体方法 */
    _renderView: function () {
      var _self = this;
      var $element = $(_self.element);
      var configuration = _self.getConfiguration();

      // 定义默认日历title
      configuration.titleJson = {
        title: '',
        eventTitle: '查看详情',
        blankTitle: '新建'
      };

      _self.invokeDevelopmentMethod('getTitle', [configuration]);

      if (StringUtils.isNotBlank(configuration.width)) {
        $element.width(configuration.width).css({
          'overflow-x': 'auto'
        });
      }

      if (StringUtils.isNotBlank(configuration.height)) {
        $element.height(configuration.height).css({
          'overflow-y': 'auto'
        });
      }

      if (configuration.calendarType === 'simple' && configuration.calendar) {
        // 简版视图
        _self._renderSimpleView(configuration);
      } else {
        // 标准视图 + 面板视图
        _self._renderStandardView(configuration);
      }

      setTimeout(function () {
        _self.resetHeight();
      }, 150);
    },

    /** 渲染简版视图 */
    _renderSimpleView: function (configuration) {
      var _self = this;
      var $element = $(_self.element);

      var calendarConf = configuration.calendar;
      // WellCalendar没有源码，改不动，在这里改，依赖jQuery
      (function ($, WellCalendar) {
        var render = WellCalendar.prototype.render,
          switcherYM = WellCalendar.prototype.switcherYM,
          updateView = WellCalendar.prototype.updateView,
          initialize = WellCalendar.prototype.initialize;
        WellCalendar.prototype.initialize = function (options) {
          var calendar = this;
          var ret = initialize.apply(calendar, arguments);
          var onDatePick = calendar.attributes.onDatePick;
          calendar.attributes.onDatePick = function hookOnDatePick(time, $el, WellCalendar) {
            onDatePick && onDatePick.apply(this, arguments);
            calendar.gotoDate('current'); // 设置标题
          };
          return ret;
        };

        function beforeRender() {
          // console.log('beforeRender');
          var calendar = this;
          var elements = calendar.elements;
          var options = calendar.attributes;
          if ('agendaDay' === options.defaultView) {
            //
          } else if ('agendaWeek' === options.defaultView) {
            //
          } else if ('month' === options.defaultView) {
            //
          }
        }

        function afterRender() {
          // console.log('afterRender');
          var calendar = this;
          var elements = calendar.elements;
          var options = calendar.attributes;
          var defaultIcon = '';
          if ('agendaDay' === options.defaultView) {
          } else if ('agendaWeek' === options.defaultView) {
            defaultIcon = 'icon-ptkj-xianmiaojiantou-xia';
          } else if ('month' === options.defaultView) {
            defaultIcon = 'icon-ptkj-xianmiaojiantou-shang';
          }
          //
          var $title2 = $('<div class="well-calendar-cur-year-month-week">1周</div>');
          var $title3 = $('<div class="well-calendar-cur-year-month-date">1日</div>');
          var $switcher2 = $('<div class="well-calendar-date-switcher2"></div>')
            .append('<span class="prev"><i class="iconfont icon-ptkj-xianmiaojiantou-zuo"></i></span>')
            .append('<span class="toggle-view"><i class="iconfont calender-view-switcher"></i></span>')
            .append('<span class="next"><i class="iconfont icon-ptkj-xianmiaojiantou-you"></i></span>');

          $switcher2.find('.icon-ptkj-xianmiaojiantou-zuo').on('click', function (event) {
            calendar.switcherYM('prevMonth');
          });
          $switcher2.find('.icon-ptkj-xianmiaojiantou-you').on('click', function (event) {
            calendar.switcherYM('nextMonth');
          });
          $switcher2.find('.calender-view-switcher').addClass(defaultIcon);
          $switcher2.find('.calender-view-switcher').on('click', function (event) {
            var $target = $(event.target),
              defaultView;
            if ($target.hasClass('icon-ptkj-xianmiaojiantou-shang')) {
              $target.removeClass('icon-ptkj-xianmiaojiantou-shang');
              $target.addClass('icon-ptkj-xianmiaojiantou-xia');
              defaultView = 'agendaWeek';
            } else if ($target.hasClass('icon-ptkj-xianmiaojiantou-xia')) {
              $target.removeClass('icon-ptkj-xianmiaojiantou-xia');
              $target.addClass('icon-ptkj-xianmiaojiantou-shang');
              defaultView = 'month';
            }
            calendar.switchView(defaultView);
          });
          if (!elements.$title2) {
            $(elements.title).after((elements.$title2 = $title2));
            $(elements.title).after((elements.$title3 = $title3));
          } else {
            elements.$title2 = $title2;
            elements.$title3 = $title3;
          }

          $(elements.body).append((elements.$switcher2 = $switcher2));
          calendar.gotoDate('current'); // 设置标题
          calendar.switchView(options.defaultView || '');
          calendar.renderDurations();
        }

        function setStartAndEndTimeToOption(options) {
          var minTimeArr = options.minTime.split(':');
          var maxTimeArr = options.maxTime.split(':');

          var startHour = +minTimeArr[0];
          var startMinute = +minTimeArr[1] % 31 ? 30 : 0;

          var endHour = +maxTimeArr[0];
          var endMinute = +maxTimeArr[1];

          if (endMinute % 31) {
            endHour++;
            endMinute = 0;
          } else if (endMinute) {
            endMinute = 30;
          }

          options.startHour = startHour;
          options.startMinute = startMinute;
          options.startTime = startHour;
          options.endHour = endHour;
          options.endMinute = endMinute;
          options.startTime = [prefixZero(startHour), prefixZero(startMinute)].join(':');
          options.endTime = [prefixZero(endHour), prefixZero(endMinute)].join(':');
        }

        WellCalendar.prototype._renderFooterList = function () {
          var calendar = this;
          var options = calendar.attributes;
          var createElement = WellCalendar.DOM.createElement;
          var list = this.dataList;
          var fragment = document.createDocumentFragment();
          var $ul = this.getEls().events;
          setStartAndEndTimeToOption(options);

          if (!$ul) {
            $ul = createElement('ul', {
              class: 'view-events scrollbar scrollbar-white-bg'
            });
            this.getEls().events = $ul;
          }

          var currentData = this.getDate();
          var currentYear = currentData.year;
          var currentMonth = prefixZero(currentData.month);
          var currentDay = prefixZero(currentData.date);
          var currentDateText = [currentYear, currentMonth, currentDay].join('-');

          var currentDateEvents = list[currentDateText];

          $ul.innerHTML = '';
          if (currentDateEvents) {
            if (null != window._ && _.sortByOrder) {
              currentDateEvents = _.sortByOrder(currentDateEvents, ['start'], ['asc']);
            }

            var startTime;
            var endTime;
            if (options.defaultView === 'agendaDay') {
              startTime = moment(currentDateText + ' ' + options.startTime, 'YYYY-MM-DD HH:mm');
              endTime = moment(currentDateText + ' ' + options.endTime, 'YYYY-MM-DD HH:mm');

              var newEvents = [];
              _.each(currentDateEvents, function (item, index) {
                var _startDate = moment(item['startTime']);
                var _endDate = moment(item['endTime']);
                var ismove = false;
                // 判断该项是否为全天
                if (_startDate.isSameOrBefore(startTime) && _endDate.isSameOrAfter(endTime)) {
                  //当前事项开始时间在搜索时间之前，结束时间在搜索时间之后
                  item.allDay = true;
                } else if (_endDate.isBefore(startTime) || _startDate.isAfter(endTime)) {
                  //移除当前事项结束时间在搜索时间之前，开始时间在搜索时间之后，即不在日历时间段内数据
                  ismove = true;
                }
                if (!ismove) {
                  newEvents.push(item);
                }
              });
              currentDateEvents = newEvents;
            }
            currentDateEvents.forEach(function (item, i) {
              var fullDay = item.allDay || null == item.end;
              var start = moment(item.start, 'YYYY-MM-DD HH:mm:ss');

              var $li = createElement('li', {
                class: 'viewEvent' + (fullDay ? ' full-day' : ''),
                'data-id': item.id,
                'data-item': JSON.stringify(item)
              });
              var end = fullDay ? null : moment(item.end, 'YYYY-MM-DD HH:mm:ss');
              $li.innerHTML = '<div class="time">'
                .concat(null == end ? '' : start.format('HH:mm'), '</div><div class="time-end">')
                .concat(null == end ? '' : end.format('HH:mm'), '</div><div class="text">')
                .concat(item.customTitle || item.title, '</div>');

              if (startTime && endTime) {
                // 每个小时为 高度50px + 边框 1px
                var heightPerHour = 50 + 1;
                var _eventHeight = heightPerHour / 2;
                if (fullDay) {
                  currentDateEvents[i].top = 1;
                  currentDateEvents[i].bottom = _eventHeight;
                  $li.style.top = 0 + 'px';
                  $li.style.height = _eventHeight + 'px';
                  $li.style.lineHeight = _eventHeight + 'px';
                } else {
                  var eventHeight = (end.diff(start, 'minutes') / 60) * heightPerHour;

                  if (eventHeight < _eventHeight) {
                    eventHeight = _eventHeight;
                  }

                  var eventTop = (start.diff(startTime, 'minutes') / 60) * heightPerHour;

                  if (eventTop < 0) {
                    eventHeight = eventHeight + eventTop;
                    eventTop = 0;
                  }

                  $li.style.top = eventTop + heightPerHour + 'px';
                  $li.style.height = eventHeight + 'px';
                  $li.style.lineHeight = _eventHeight + 'px';
                }

                if ('agendaDay' === options.defaultView && item.backgroundColor) {
                  var bg = item.backgroundColor.replace(/ !important/, '');
                  // $li.style.backgroundColor = toRgba(bg, 0.2);
                  $li.style.backgroundColor = hexify(toRgba(bg, 0.2));
                  $li.style.setProperty('--bg', bg);
                  // $li.style.setProperty('--bgrgb', toRgba(bg, 0.3));
                  $li.style.setProperty('--bgrgb', hexify(toRgba(bg, 0.3)));
                }
              }

              fragment.appendChild($li);
            });
            $ul.appendChild(fragment);
            this.getEls().footer.appendChild($ul);
            this.setFullDateEventItem($ul);
            this.setOverlapEvents();
          } else {
            var noData = createElement('li', {
              class: 'no-data'
            });
            noData.innerHTML = '当日暂无日程安排!';
            // 仅日视图时动态处理高度
            if ($(this.getEls().footer).parents('.calendar-view-agendaDay')[0]) {
              var $fullday = $(this.getEls().footer).find('.view-duration .full-day')[0];
              $fullday.style.height = '';
            }
            $ul.appendChild(noData);
          }
          $(this.getEls().footer).animate(
            {
              scrollTop: 0
            },
            100
          );
          return this;
        };

        WellCalendar.prototype.setOverlapEvents = function () {
          var thisself = this;
          var _top = 0;
          var $lis = $(this.getEls().footer).find('.view-events li');
          var diffHeight = 0;
          if ($(this.getEls().footer).find('.view-duration .full-day')[0]) {
            var $fullday = $(this.getEls().footer).find('.view-duration .full-day')[0];
            var _height = $fullday.style.height;
            if (_height) {
              diffHeight = parseFloat(_height) - 50;
            }
          }
          // 位置从上到下排序
          $lis.each(function (index, item) {
            if (!$(item).hasClass('full-day')) {
              var top = parseFloat($(item)[0].style.top) + diffHeight;
              $(item)[0].style.top = top + 'px';
              if (top < _top && index > 0) {
                $lis[index - 1].prepend($(item));
                $(item).remove();
              }
            }
          });
          var _overlap = {};
          $lis.each(function (index, item) {
            var top = parseFloat($(item)[0].style.top);
            var height = parseFloat($(item)[0].style.height);
            var bottom = top + height;
            // 全天数据不做处理
            if (!$(item).hasClass('full-day')) {
              _overlap[index] = [];
              $lis.each(function (cindex, citem) {
                var ctop = parseFloat($(citem)[0].style.top);
                var cheight = parseFloat($(citem)[0].style.height);
                var cbottom = ctop + cheight;
                // 全天数据不做处理(计算冲突数)
                if (!$(citem).hasClass('full-day')) {
                  if (bottom > ctop && top < cbottom) {
                    _overlap[index].push(cindex);
                  }
                }
              });
            }
          });
          this.setEventItem(_overlap);
        };

        //处理冲突展示
        WellCalendar.prototype.setEventItem = function (_overlap) {
          var thisself = this;
          var $lis = $(this.getEls().footer).find('.view-events li');
          $lis.each(function (index, item) {
            thisself.setEventItemIndex($(item), index, _overlap[index]);
          });
          $lis.each(function (index, item) {
            thisself.setEventItemWidth($(item), index, _overlap[index]);
          });
          $lis.each(function (index, item) {
            thisself.setEventItemLeft($(item), index, _overlap[index]);
          });
        };

        // 设置所有事项的层级
        WellCalendar.prototype.setFullDateEventItem = function ($ul) {
          var thisself = this;
          var $lis = $(this.getEls().footer).find('.view-events li.full-day');
          var _eventHeight = 25.5;
          var allDayHeight = 0;
          _.each($lis, function (item, index) {
            var top = index ? _eventHeight * index + 5 * index : 0;
            $(item)[0].style.top = top + 'px';
            allDayHeight = top + _eventHeight;
          });
          // 仅日视图时动态处理高度
          if ($(this.getEls().footer).parents('.calendar-view-agendaDay')[0]) {
            var $fullday = $(this.getEls().footer).parents('.calendar-view-agendaDay').find('.view-duration .full-day')[0];
            if (allDayHeight > 50) {
              $fullday.style.height = allDayHeight + 'px';
            } else {
              $fullday.style.height = '';
            }
            $ul.style.height = $(this.getEls().footer).find('.view-durations').height() + 'px';
            thisself.addCurrentTimeLine();
          }
        };

        // 设置所有事项的层级
        WellCalendar.prototype.setEventItemIndex = function ($li, index, current) {
          var thisself = this;
          var $lis = $(this.getEls().footer).find('.view-events li');
          if (current && current.length > 1) {
            var idx = current.indexOf(index);
            if (idx == 0) {
              //第一个，只设置宽度
              $li.attr('data-zindex', 0);
            } else {
              var c_left = [];
              for (var i = 0; i < idx; i++) {
                var $prev = $($lis[current[i]]);
                var _zIndex = $prev.data('zindex');
                c_left.push({
                  'z-index': _zIndex
                });
              }
              var c_left = _.sortBy(c_left, 'z-index');
              _.each(c_left, function (item, i) {
                if (i == 0) {
                  if (item['z-index'] > 0) {
                    //如果第一个起始位置大于0
                    $li.attr('data-zindex', 0);
                    return false;
                  } else if (i == c_left.length - 1) {
                    $li.attr('data-zindex', 1);
                  }
                } else if (i == c_left.length - 1) {
                  $li.attr('data-zindex', item['z-index'] + 1);
                } else {
                  if (c_left[i - 1]['z-index'] + 1 != item['z-index']) {
                    //前一个的结束与当前的开始不一样
                    $li.attr('data-zindex', c_left[i - 1]['z-index'] + 1);
                    return false;
                  }
                }
              });
            }
          }
        };

        // 通过冲突项的最大层级，设置对应项的默认宽度
        WellCalendar.prototype.setEventItemWidth = function ($li, index, current) {
          var thisself = this;
          var $lis = $(this.getEls().footer).find('.view-events li');
          if (current && current.length > 1) {
            var maxIndex = _.max(
              _.map(current, function (item, index) {
                return $($lis[item]).data('zindex');
              })
            );
            var width = (1 / (maxIndex + 1)) * 100;
            _.each(current, function (item, i) {
              var _width = $lis[item].style.width || 100;
              $lis[item].style.width = thisself.setPrcent(Math.min(_width, width));
            });
          }
        };

        // 通过冲突项的宽度，设置对应项的默认宽度
        WellCalendar.prototype.setEventItemLeft = function ($li, index, current, all) {
          var thisself = this;
          var $lis = $(this.getEls().footer).find('.view-events li');
          if (current && current.length > 1) {
            var width = parseFloat($li[0].style.width) || 100; //当前item宽度;
            var idx = current.indexOf(index);
            if (idx == 0) {
              //第一个，只设置宽度
            } else {
              var c_left = [];
              var allWidth = 0;
              for (var i = 0; i < idx; i++) {
                var $prev = $lis[current[i]];
                var _width = parseFloat($prev.style.width) || 100;
                allWidth += _width;
                var _left = parseFloat($prev.style.left || 0);
                c_left.push({
                  start: _left,
                  end: _left + _width
                });
              }
              var c_left = _.sortBy(c_left, 'start');
              _.each(c_left, function (item, i) {
                if (i == 0) {
                  if (item.start > 0) {
                    //如果第一个起始位置大于0
                    $li[0].style.left = thisself.setPrcent(0);
                    if (c_left.length + 1 == current.length) {
                      $li[0].style.width = thisself.setPrcent(item.start);
                    }
                    return false;
                  } else if (i == c_left.length - 1) {
                    $li[0].style.left = thisself.setPrcent(item.end);
                    if (c_left.length + 1 == current.length) {
                      //无其他项
                      $li[0].style.width = thisself.setPrcent(100 - item.end);
                    }
                  }
                } else if (c_left[i - 1].end != item.start) {
                  //前一个的结束与当前的开始不一样
                  $li[0].style.left = thisself.setPrcent(c_left[i - 1].end); //$li左偏移为前一个的结束
                  if (c_left.length + 1 == current.length) {
                    $li[0].style.width = thisself.setPrcent(item.start - c_left[i - 1].end);
                  }
                  return false;
                } else {
                  $li[0].style.left = thisself.setPrcent(item.end);
                  if (c_left.length + 1 == current.length) {
                    $li[0].style.width = thisself.setPrcent(100 - item.end);
                  }
                }
              });
            }
          }
        };

        WellCalendar.prototype.setPrcent = function (val) {
          return StringUtils.format('${percent}%', {
            percent: val
          });
        };

        WellCalendar.prototype.renderDurations = function () {
          var calendar = this;
          var elements = calendar.elements;
          var options = calendar.attributes;
          if (!elements.footer) {
            return;
          }

          var slotDuration = options.slotDuration.replace(/:/gi, '');
          var $durationUl = $('<ul>', {
            class: 'view-durations duration - ' + slotDuration
          });

          var $rowLi = $('<li>', {
            class: 'view-duration'
          });

          // 全天
          $rowLi.clone().append('<div class="duration-label full-day">全天</div><div class="duration-time"></div>').appendTo($durationUl);

          function createTimeRow($hourRow, timeText, splitRows, halfHour, position) {
            var rowHeightClass = '';
            if ((splitRows === 1 && halfHour) || splitRows === 2) {
              rowHeightClass = 'half-height';
            } else if (splitRows === 4) {
              rowHeightClass = 'quarter-height';
            }

            var rows = halfHour ? (splitRows === 1 ? 1 : splitRows / 2) : splitRows;

            var $ul = $('<ul>').appendTo($hourRow);
            var startRow = true;
            for (var i = 0; i < rows; i++) {
              var labelStr = StringUtils.format('<div class="duration-label">${timeText}</div><div class="duration-time"></div>', {
                timeText: startRow ? timeText : ''
              });

              $ul.append(
                $('<li>', {
                  class: rowHeightClass
                }).append(labelStr)
              );
              startRow = false;
            }

            return $hourRow;
          }

          var startHour = options.startHour;
          var startMinute = options.startMinute;
          var endHour = options.endHour;
          var endMinute = options.endMinute;

          for (var curHour = startHour; curHour < endHour || (curHour === endHour && endMinute === 30); curHour++) {
            // 小时
            var $hourRow = $rowLi.clone();
            var timeText;
            var halfHour = false;
            var splitRows = 1;
            var position = 'middle';

            if (curHour === startHour) {
              position = 'start';
            }

            if (curHour === endHour || (curHour < endHour && endMinute === 0)) {
              position = 'end';
            }

            if (curHour === startHour && startMinute === 30) {
              halfHour = true;
              timeText = [prefixZero(startHour), prefixZero(startMinute)].join(':');
            } else if (curHour === endHour && endMinute === 30) {
              halfHour = true;
              timeText = [prefixZero(endHour), prefixZero(0)].join(':');
            } else {
              timeText = [prefixZero(curHour), prefixZero(0)].join(':');
            }

            switch (options.slotDuration) {
              case '00:30:00':
                splitRows = 2;
                break;
              case '00:15:00':
                splitRows = 4;
                break;
              default:
                splitRows = 1;
                break;
            }

            $durationUl.append(createTimeRow($hourRow, timeText, splitRows, halfHour, position));
          }
          $(elements.footer).prepend($durationUl);
          calendar._renderFooterList();
        };

        WellCalendar.prototype.switchView = function (defaultView) {
          var calendar = this;
          var elements = calendar.elements;
          var options = calendar.attributes;
          $.each(['agendaDay', 'agendaWeek', 'month'], function (idx, view) {
            $(elements.wrap).removeClass('calendar-view-' + view);
          });
          calendar.gotoWeek('current');
          options.defaultView = defaultView;
          $(elements.wrap).addClass('calendar-view-' + options.defaultView);
          calendar.addCurrentTimeLine();
        };
        WellCalendar.prototype.gotoDate = function (currentDate) {
          var calendar = this;
          var elements = calendar.elements;
          var year = calendar.getYear();
          var month = calendar.getMonth();
          var calendarDate = calendar.data.date;
          var oneDaySeconds = 24 * 60 * 60 * 1000;
          var ccDate = new Date(calendarDate.year, calendarDate.month - 1, calendarDate.date);
          // console.log('beforeGotoDate:' + ccDate.format('yyyy-MM-dd'));
          if ('prev' === currentDate) {
            currentDate = new Date(ccDate.getTime() - oneDaySeconds);
            if (ccDate.getMonth() === currentDate.getMonth()) {
              currentDate = currentDate.format('yyyy-MM-dd');
            } else {
              return false; // 跨月
            }
          } else if ('next' === currentDate) {
            currentDate = new Date(ccDate.getTime() + oneDaySeconds);
            if (ccDate.getMonth() === currentDate.getMonth()) {
              currentDate = currentDate.format('yyyy-MM-dd');
            } else {
              return false; // 跨月
            }
          } else if ('first' === currentDate) {
            currentDate = year + '-' + (month < 10 ? '0' + month : month) + '-01';
          } else if ('last' === currentDate) {
            var DATES = calendar.get('DATES');
            var isLeapYear = WellCalendar.isLeapYear;
            var days = isLeapYear(year) && month === 2 ? 29 : DATES[month - 1];
            currentDate = year + '-' + (month < 10 ? '0' + month : month) + '-' + days;
          } else if ('current' === currentDate) {
            var ffDate = calendarDate.date < 10 ? '0' + calendarDate.date : calendarDate.date;
            currentDate = year + '-' + (month < 10 ? '0' + month : month) + '-' + ffDate;
          }
          // console.log('afterGotoDate:' + currentDate);
          var $currentDate = $('.well-calendar-body-date[data-date="' + currentDate + '"]', elements.dates);
          if ($currentDate.length <= 0) {
            return false;
          } else if (false == $currentDate.hasClass('picked')) {
            // 不重复触发选中事件
            $currentDate.trigger('click');
          }
          elements.$title3.text(currentDate.split('-')[2] + '日');
          return true;
        };
        WellCalendar.prototype.gotoWeek = function (currentWeek) {
          var calendar = this,
            weeks = [];
          var year = calendar.getYear();
          var month = calendar.getMonth();
          var elements = calendar.elements;
          // 收集当前月的周分组
          var ym = year + '-' + (month < 10 ? '0' + month : month);
          $('.well-calendar-body-date', elements.dates).each(function (idx, elem) {
            var $elem = $(elem);
            var elemDate = $elem.data('date');
            var kk = Math.floor(idx / 7);
            if (null == weeks[kk] && (kk === 0 || elemDate.indexOf(ym) === 0)) {
              weeks[kk] = [];
            }
            weeks[kk] && weeks[kk].push(elem);
            $elem.removeClass('current-week');
          });
          if ('prev' === currentWeek) {
            currentWeek = calendar.data.week - 1;
          } else if ('next' === currentWeek) {
            currentWeek = calendar.data.week + 1;
          } else if ('first' === currentWeek) {
            currentWeek = 1;
          } else if ('last' === currentWeek) {
            currentWeek = weeks.length;
          } else if ('current' === currentWeek) {
            var calendarDate = calendar.data.date;
            currentWeek = Math.ceil((calendarDate.date + 6 - calendarDate.day) / 7);
            // 默认第一周
            currentWeek = null == weeks[currentWeek - 1] ? 1 : currentWeek;
          }
          // 判断是否溢出（周）
          if (null == weeks[currentWeek - 1]) {
            return false;
          }
          // 显示当前周
          var pickd = null;
          $.each(weeks[currentWeek - 1], function (idx, elem) {
            var $elem = $(elem);
            $elem.addClass('current-week');
            if ($elem.hasClass('picked')) {
              pickd = elem;
            } else if (null == pickd && $elem.attr('data-date').indexOf(ym) === 0) {
              pickd = elem;
            }
          });
          calendar.data.week = currentWeek;
          elements.$title2.text(currentWeek + '周');
          pickd && $(pickd).trigger('click');
          return true;
        };
        WellCalendar.prototype.render = function () {
          beforeRender.apply(this, arguments);
          var ret = render.apply(this, arguments);
          afterRender.apply(this, arguments);
          return ret;
        };
        WellCalendar.prototype.updateView = function () {
          var calendar = this;
          var elements = calendar.elements;
          var options = calendar.attributes;
          var ret = updateView.apply(calendar, arguments);
          calendar.gotoDate('current'); // 设置标题
          calendar.switchView(options.defaultView || '');
          return ret;
        };
        WellCalendar.prototype.addCurrentTimeLine = function () {
          var calendar = this;
          var _now = moment();
          var $wrap = calendar.elements.wrap;
          if ($wrap) {
            $($wrap).find('.view-durations .current-line').remove();
          }
          if ($wrap && $($wrap).hasClass('calendar-view-agendaDay')) {
            var day = moment(calendar.data.date.text).format('YYYY-MM-DD');
            if (day == _now.format('YYYY-MM-DD')) {
              $($wrap).find('.view-durations').append('<div class="current-line">');
              var firstheight = $($wrap).find('.view-durations>li').height();
              var allheight = $($wrap).find('.view-durations').height() - firstheight;
              var alldiff = moment(day + ' ' + calendar.attributes.maxTime).diff(
                moment(day + ' ' + calendar.attributes.minTime),
                'minutes'
              );
              var diff = _now.diff(moment(day + ' ' + calendar.attributes.minTime), 'minutes');
              if (diff > alldiff) {
                //超过当天可显示时间
                $($wrap).find('.view-durations .current-line').remove();
              } else {
                var top = (diff * allheight) / alldiff + firstheight - 5;
                $($wrap)
                  .find('.view-durations .current-line')
                  .css('top', top + 'px');
              }
            }
          }
        };
      })($, WellCalendar);
      if (typeof calendarConf.DayTimeArray === 'string') {
        var dayTimeArray = calendarConf.DayTimeArray.split(' - ');
        // 6:00转06:00
        _minTime = moment(dayTimeArray[0], 'mm:ss').format('mm:ss');
        _maxTime = moment(dayTimeArray[1], 'mm:ss').format('mm:ss');
      }
      _self.wellCalendar = new WellCalendar({
        // viewMode: 0,
        // pickMode: 'week',
        minTime: _minTime,
        maxTime: _maxTime,
        slotDuration: _self.getSlotDuration(calendarConf.Gradu),
        defaultView: calendarConf.defaultView,
        parent: $element.attr('id'),
        parentWidth: configuration.simpleViewWidth || '',
        caller: _self,
        getDataSource: _self.getDataSource,
        scheduleEvent: function (date) {
          _self.invokeDevelopmentMethod('beforeDayClick', [date, null, null]);
          _self.invokeEventHandler('dayClick', [date, null, null]);
        },
        viewEvent: function (item) {
          _self.invokeEventHandler('eventClick', [item, null, null]);
        }
      });
    },

    _renderStandardView: function (configuration) {
      var _self = this;
      var $element = $(_self.element);

      // 渲染头部按钮
      _self._renderButton($element);

      // 渲染搜索
      _self._renderKeywordSearch();

      var $panelDiv = $("<div class='calendar-panel' style=''>");
      $element.append($panelDiv);

      $element.addClass('gradu-' + configuration.calendar.Gradu);

      // 渲染日历面板
      _self.$calendarElement = _self._renderCalendarView($panelDiv);

      // 渲染资源面板
      // 资源视图，需要分组信息，获取资源分组的MAP数据
      _self.invokeDevelopmentMethod('getGroupDataMap', [_self.options, _self.getConfiguration()]);

      _self.$resourceElement = _self._renderResourceView($panelDiv);
      _self.selectCalendarView = configuration.defaultEnableView === 'enableResourceView' ? 'resource' : 'calendar'; //当前日历视图

      // 渲染视图切换选择器
      _self._renderStandardViewSelect($element, _self.selectCalendarView);
    },

    /** 标准视图 - 头部按钮工具栏 */
    _renderButton: function ($element) {
      var _self = this;
      var options = _self.options;
      var buttonElement = _self.buttonElement;
      var configuration = _self.getConfiguration();

      var $toolbarElement = $("<div class='fixed-calendar-toolbar'></div>");
      $element.prepend($toolbarElement);
      var $buttonDiv = $("<div class='clearfix bs-bars'></div>");
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
        _self.buttonElement = buttonElement;
      }
    },

    /** 标准视图 - 视图他切换下拉框 */
    _renderStandardViewSelect: function ($element, defaultValue) {
      var _self = this;
      var $element = $(_self.element);
      var configuration = _self.getConfiguration();
      // var $toolbar = $('.fixed-calendar-toolbar>.bs-bars', _self.element);
      var $viewsFilter = $('.fc-viewsFilter-button', _self.element);
      var $selectView = $viewsFilter.find("select[name='selectView']");

      if ($selectView.length <= 0) {
        var selectView = new commons.StringBuilder();
        selectView.append("<select name='selectView'> ");
        selectView.append("	<option value='calendar'>日历视图</option>");
        selectView.append("	<option value='resource'>资源视图</option>");
        selectView.append('</select>');

        $selectView = $(selectView.toString());
        $viewsFilter.append($selectView);
        $selectView.val(defaultValue);

        $selectView
          .wSelect2({
            width: '100px',
            searchable: false
          })
          .on('change', function (event) {
            var $eventContext = this;
            var eventArgs = arguments;

            function viewChangeCallBack() {
              var view = $(this).val();
              _self.defaultView = view;
              var conf = configuration[view] || {};
              _self.selectCalendarView = view; //当前日历视图

              if (view == 'calendar') {
                //不能用show()和hide()来隐藏，因为会导致从资源视图切换回日历视图的时候布局变形，数据展示不出来
                _self.$calendarElement.find('.fc-view-container').css('visibility', 'visible');
                _self.$resourceElement.hide();
                _self.element.find('.no-records-found').remove();
              } else if (view == 'resource') {
                _self.$calendarElement.find('.fc-view-container').css('visibility', 'hidden');
                _self.$resourceElement.show();

                $('.table-wrapper', _self.$resourceElement).niceScroll().resize();
              }

              // 每日显示时间区间
              if (typeof conf.DayTimeArray === 'string') {
                var dayTimeArray = conf.DayTimeArray.split(' - ');
                // 6:00转06:00
                dayTimeArray[0] = moment(dayTimeArray[0], 'mm:ss').format('mm:ss');
                dayTimeArray[1] = moment(dayTimeArray[1], 'mm:ss').format('mm:ss');

                if (_minTime !== dayTimeArray[0] || _maxTime !== dayTimeArray[1]) {
                  _minTime = dayTimeArray[0];
                  _maxTime = dayTimeArray[1];

                  if (view == 'calendar') {
                    _self.$calendarElement.fullCalendar('option', {
                      minTime: _minTime,
                      maxTime: _maxTime
                    });
                  } else if (view == 'resource') {
                    _self.$resourceElement.view('setDayHour', {
                      min: _minTime,
                      max: _maxTime
                    });
                  }
                }
              }

              // 状态过滤
              _self._renderStatusFilter(view);

              // 允许用户自定义时区
              if (conf.allowCustomDayTime || conf.allowCustomGradu) {
                $('.fc-timeSetting-button', _self.element).show();
              } else {
                $('.fc-timeSetting-button', _self.element).hide();
              }

              // 启用视图
              var $calendarViews = $('.fc-agendaDay-button, .fc-agendaWeek-button, .fc-month-button', _self.element);
              $calendarViews
                .each(function (idx, elem) {
                  var $elem = $(elem);
                  var viewType = $elem.attr('class').match(/fc-(agendaDay|agendaWeek|month)-button/)[1];
                  $elem[$.inArray(viewType, conf.enable) > -1 ? 'show' : 'hide']();
                })
                .click(function (event) {
                  var $this = $(this);
                  var text = $this.text();
                  if (text === '周' || text === '月') {
                    text = '本' + text;
                  } else {
                    text = '今天';
                  }

                  var viewType = $this.attr('class').match(/fc-(agendaDay|agendaWeek|month)-button/)[1];
                  conf.defaultView = viewType;
                  configuration.calendar.defaultView = viewType;
                  configuration.resource.defaultView = viewType;

                  _self._renderStatusFilter(view);

                  $('.fc-today-button', _self.element).text(text);
                });

              if (!$('.fc-viewsFilter-button', _self.element).html().trim()) {
                _self._renderStandardViewSelect($element, view);
              }

              _self.addCurrentTimeLine(); //添加当前时间线

              //切换视图后操作
              _self.invokeDevelopmentMethod('afterChangeViewRender', [$element, _self.options, _self.getConfiguration()]);

              // 默认视图
              var $defaultView = $calendarViews.filter('.fc-' + conf.defaultView + '-button');
              if ($defaultView.is(':visible') && false == $defaultView.hasClass('active')) {
                $defaultView.trigger('click');
              }
            }

            setTimeout(function () {
              // 异步执行
              viewChangeCallBack.apply($eventContext, eventArgs);
            }, 0);
          });

        $selectView.prev('div.well-select').addClass('pull-right');
      }
      //视图切换
      var enableCalendarView = $.inArray('enableCalendarView', configuration.enableView) > -1,
        enableResourceView = $.inArray('enableResourceView', configuration.enableView) > -1;
      if (enableCalendarView && enableResourceView) {
        $selectView.wSelect2('show');
      } else {
        $selectView.wSelect2('hide');
      }
      $selectView.val(defaultValue).trigger('change');
    },

    _renderStatusFilter: function (view) {
      view = 'calendar'; // 都是事项状态
      var _self = this;
      var $element = $(_self.element);
      var configuration = _self.getConfiguration();

      // if (configuration.calendarType === 'panel') {
      //   $element.find('.fc-statusFilter-button').hide();
      //   return;
      // }

      var viewConf = configuration[view];
      if (!viewConf) {
        return false;
      }
      var eventStatusFieldName = configuration.eventStatusFieldName || 'isFinish';

      var $statusFilterBtn = $element.find('.fc-statusFilter-button');
      if (!$statusFilterBtn.find('.filter-text').length) {
        $statusFilterBtn.append('<span class="filter-text">筛选</span>');
      }

      // 状态对应数量不显示
      var $popup;
      if ($statusFilterBtn.find('.calendar-filter-popup').length > 0) {
        // $statusFilterBtn.find('.calendar-filter-popup').remove();
        return false;
      }

      $popup = $('<div class="calendar-filter-popup">');
      // 状态过滤
      var $statusFilterArea = $('<div class="calendar-status-filter">');
      $popup.append($statusFilterArea);
      $statusFilterArea.append('<h3 class="title">状态</h3>');
      var $statusFilterItems = $('<div class="calendar-status-items">');
      $statusFilterArea.append($statusFilterItems);

      for (var i = 0; i < viewConf.status.length; i++) {
        var status = viewConf.status[i];
        var color = status.color.iconColor;

        // 状态对应数量不显示
        // var vals = $.map(_self._dataSource, function (item) {
        //   return status.code === item[eventStatusFieldName]+"" ? item : null;
        // });

        // var count = vals.length; //status.code === 'done' ? _self.finishCount : _self.ongoingCount;

        var $statusFilterItem = $('<div class="calendar-status-item selected">')
          .css({
            color: toRgba(color, 1),
            borderColor: toRgba(color, 1),
            backgroundColor: toRgba(color, 0.1)
          })
          .data('color', color);
        $statusFilterItem.append('<span class="status-text" code="' + status.code + '">' + status.text + '</span>');
        // $statusFilterItem.append('<span class="status-count">' + count + '</span>');
        $statusFilterItem.append('<span class="check fr"><i class="iconfont icon-ptkj-dagou"></i></span>');
        $statusFilterItem.find('.iconfont').css({
          backgroundColor: color
        });

        $statusFilterItem.click(function (e) {
          var $this = $(this);
          e.stopPropagation();
          var color = $this.data('color');
          if ($this.hasClass('selected')) {
            $this.removeClass('selected');
            $this.css({
              borderColor: toRgba(color, 0.4)
            });
          } else {
            $this.addClass('selected');
            $this.css({
              borderColor: toRgba(color, 1)
            });
          }
        });

        $statusFilterItems.append($statusFilterItem);
      }

      // 时间过滤
      var $timeFilterArea = $('<div class="calendar-time-filter">');
      $popup.append($timeFilterArea);
      $timeFilterArea.append('<h3 class="title">时间</h3>');

      var $timeRange = $('<div class="calendar-time-range">').appendTo($timeFilterArea);
      $timeRange.append('<div>从 <input type="text" id="calendar-time-range-start" placeholder="请选择开始时间"></div>');
      $timeRange.append('<div>至 <input type="text" id="calendar-time-range-end" placeholder="请选择结束时间"></div>');

      // 底部
      $('<div class="popup-footer">')
        .append('<button class="well-btn w-btn-primary filter-popup-confirm">确定</button>')
        .append('<button class="well-btn w-btn-primary w-line-btn like-primary filter-popup-cancel">取消</button>')
        .append('<button class="well-btn w-btn-primary w-line-btn like-primary filter-popup-reset">重置</button>')
        .appendTo($popup);

      $statusFilterBtn.append($popup);

      layDate.render({
        elem: '#calendar-time-range-start',
        ready: function (date) {
          $statusFilterBtn.addClass('show-popup');
        }
      });

      layDate.render({
        elem: '#calendar-time-range-end',
        ready: function (date) {
          $statusFilterBtn.addClass('show-popup');
        }
      });

      $statusFilterBtn.click(function (e) {
        // 状态对应数量不显示
        // 重新计算状态对应事项数
        // for (var i = 0; i < viewConf.status.length; i++) {
        //   var status = viewConf.status[i];

        //   var vals = $.map(_self._dataSource, function (item) {
        //     return status.code === item[eventStatusFieldName]+"" ? item : null;
        //   });

        //   var count = vals.length;
        //   $statusFilterItems.find(`span[code="${status.code}"]`).next().html(count);
        // }

        // 展开
        if ($(e.target).closest('.calendar-filter-popup').length === 0) {
          $(this).addClass('show-popup');
        }
      });

      $(document).mousedown(function (e) {
        if (
          $statusFilterBtn.hasClass('show-popup') &&
          $('.layui-laydate').length === 0 &&
          $(e.target).closest('.fc-statusFilter-button').length === 0
        ) {
          $statusFilterBtn.removeClass('show-popup');
        }
      });

      $statusFilterBtn.find('.filter-popup-confirm').on('click', function () {
        var status = [];
        $.each($('.calendar-status-item.selected', $statusFilterBtn), function (idx, item) {
          var $item = $(item);
          status.push($item.find('.status-text').attr('code'));
        });

        var dateRange = [$('#calendar-time-range-start').val(), $('#calendar-time-range-end').val()];
        _self.invokeDevelopmentMethod('statusFilter', [status, dateRange]);
      });

      $statusFilterBtn.find('.filter-popup-reset').on('click', function () {
        $('.calendar-status-item', $statusFilterBtn).addClass('selected');
        $('.calendar-time-range input', $statusFilterBtn).val('');
      });

      $statusFilterBtn.find('.filter-popup-cancel').on('click', function (e) {
        e.stopPropagation();
        $statusFilterBtn.removeClass('show-popup');
      });
    },

    getSlotDuration: function (duration) {
      if (duration == '15') {
        return '00:15:00';
      } else if (duration == '30') {
        return '00:30:00';
      } else if (duration == '60') {
        return '01:00:00';
      }
    },

    /** 渲染FullCalendar控件和相关查询条件，按钮等 */
    _renderCalendarView: function ($element) {
      var _self = this;
      var options = _self.options;
      var configuration = _self.getConfiguration();
      var calendarConfig = configuration.calendar;
      if ($.isEmptyObject(configuration) || $.isEmptyObject(calendarConfig)) {
        appModal.warning('未定义资源日历组件');
        return;
      }

      var calendarId = options.widgetDefinition.id + '_calendar';
      var $calendarElement = $("<div id='" + calendarId + "' ></div>");
      $element.append($calendarElement);

      var fullCalendarOptions = {};
      _self.invokeDevelopmentMethod('getTitle', [configuration]);

      fullCalendarOptions.configuration = configuration;

      defaultOptions.customButtons.timeSetting.click = function () {
        _self._setMinToMaxTime($element);
      };
      defaultOptions.customButtons.statusFilter.click = function () {
        // _self._setMinToMaxTime($element);
      };
      $.extend(true, fullCalendarOptions, defaultOptions);
      // 单日日程最大显示数量
      if (configuration.eventLimitNum && configuration.eventLimitNum > 0) {
        fullCalendarOptions.eventLimit = parseInt(configuration.eventLimitNum);
      }
      // 可切换视图
      var defaultCalendarView = calendarConfig.defaultCalendarView || configuration.defaultView;
      // 时间刻度
      fullCalendarOptions.slotDuration = _self.getSlotDuration(calendarConfig.Gradu);
      // 高级设置
      var customSetting = {};
      if (StringUtils.isNotBlank(configuration.seniorConfiguration.customSetting)) {
        customSetting = JSON.parse(configuration.seniorConfiguration.customSetting);
      }
      if (typeof calendarConfig.DayTimeArray === 'string') {
        var dayTimeArray = calendarConfig.DayTimeArray.split(' - ');
        // 6:00转06:00
        _minTime = moment(dayTimeArray[0], 'mm:ss').format('mm:ss');
        _maxTime = moment(dayTimeArray[1], 'mm:ss').format('mm:ss');
      }
      fullCalendarOptions.minTime = _minTime;
      fullCalendarOptions.maxTime = _maxTime;
      $.extend(true, fullCalendarOptions, customSetting);

      /** FullCalendar初始化 * */
      $calendarElement.fullCalendar('destroy').fullCalendar(
        $.extend(
          {
            defaultView: defaultCalendarView,
            weekends: true, // configuration.weekends
            //点击日，月，周，上一个，下一个，今天按钮都会触发该方法
            events: function (start, end, timezone, callback) {
              _viewStart = start.format('YYYY-MM-DD HH:mm:ss');
              _viewEnd = end.format('YYYY-MM-DD HH:mm:ss');

              var dataSource = _self.getDataSource({
                startTime: _viewStart,
                endTime: _viewEnd,
                $calendarElement: $calendarElement
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
              _self.invokeEventHandler('dayClick', [date, jsEvent, view]);
            },
            // 点击事项事件
            eventClick: function (event, jsEvent, view) {
              _self.invokeEventHandler('eventClick', [event, jsEvent, view]);
            },
            eventAfterRender: function (event, element, view) {
              _self._resetAllDayRender(event, element, view);
              _self.invokeDevelopmentMethod('eventAfterRender', [event, element, view]);
            },
            eventAfterAllRender: function (view) {
              _self.addCurrentTimeLine(); //添加当前时间轴
              // 状态过滤
              _self._renderStatusFilter(_self.defaultView);
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

    _resetAllDayRender: function (event, element, view) {
      var _self = this;
      // 仅对标准视图--周视图--全天事件做处理
      if (event.allDay && view.type == 'agendaWeek' && _self.defaultView == 'calendar') {
        var allWidth = _self.element.find('.fc-day-header').width();
        if (event.marginLeft_diff) {
          element[0].style.marginLeft = event.marginLeft_diff * allWidth + 'px';
        }
        if (event.marginRight_diff) {
          element[0].style.marginRight = event.marginRight_diff * allWidth + 'px';
        }
      }
    },

    // 添加当前时间线（日视图）
    addCurrentTimeLine: function () {
      var _self = this;
      var _now = moment();
      var options = _self.options;
      var configuration = _self.getConfiguration();
      // 简版仅日视图添加
      if (configuration.calendarType === 'simple' && configuration.calendar) {
        var $wrap = _self.wellCalendar && _self.wellCalendar.elements.wrap;
        if ($wrap) {
          $($wrap).find('.view-durations .current-line').remove();
        }
        if ($wrap && $($wrap).hasClass('calendar-view-agendaDay')) {
          var day = moment(_self.wellCalendar.data.date.text).format('YYYY-MM-DD');
          if (day == _now.format('YYYY-MM-DD')) {
            $($wrap).find('.view-durations').append('<div class="current-line">');
            var firstheight = $($wrap).find('.view-durations>li').height();
            var allheight = $($wrap).find('.view-durations').height() - firstheight;
            var alldiff = moment(day + ' ' + _self.wellCalendar.attributes.maxTime).diff(
              moment(day + ' ' + _self.wellCalendar.attributes.minTime),
              'minutes'
            );
            var diff = _now.diff(moment(day + ' ' + _self.wellCalendar.attributes.minTime), 'minutes');
            if (diff > alldiff) {
              //超过当天可显示时间
              $($wrap).find('.view-durations .current-line').remove();
            } else {
              var top = (diff * allheight) / alldiff + firstheight - 5;
              $($wrap)
                .find('.view-durations .current-line')
                .css('top', top + 'px');
            }
          }
        }
      } else {
        if (_self.$calendarElement) {
          _self.$calendarElement.find('.view-table .current-line').remove();
          _self.$calendarElement.find('.fc-time-grid-container .current-line').remove();
          var type = _self.$calendarElement.fullCalendar('getView').type; //当前是日/周/月
          if (_self.selectCalendarView == 'resource') {
            //资源视图
            if (type == 'agendaDay') {
              //日视图
              var day = _self.$calendarElement.fullCalendar('getDate').format('YYYY-MM-DD');
              var getDayHour = _self.$resourceElement.view('getDayHour');
              if (day == _now.format('YYYY-MM-DD')) {
                _self.$resourceElement.find('.view-table').append('<div class="current-line">');
                var firstwidth = _self.$resourceElement.find('.view-table .resourse-type').outerWidth();
                var allwidth = _self.$resourceElement.find('.view-table').outerWidth() - firstwidth;
                var finalMaxTime = _self.$resourceElement.view('finalMaxTime');
                var alldiff = moment(day + ' ' + finalMaxTime).diff(moment(day + ' ' + getDayHour.min), 'minutes');
                var diff = _now.diff(moment(day + ' ' + getDayHour.min), 'minutes');
                if (diff > alldiff) {
                  //超过当天可显示时间
                  _self.$resourceElement.find('.view-table .current-line').remove();
                } else {
                  var left = (diff * allwidth) / alldiff + firstwidth - 1;
                  _self.$resourceElement.find('.view-table .current-line').css('left', left + 'px');
                }
              }
            }
          } else {
            //标准视图
            if (type == 'agendaDay') {
              //日视图
              var day = _self.$calendarElement.fullCalendar('getDate').format('YYYY-MM-DD');
              if (day == _now.format('YYYY-MM-DD')) {
                _self.$calendarElement.find('.fc-time-grid-container').append('<div class="current-line">');
                var allheight = _self.$calendarElement.find('.fc-time-grid-container').height();
                var alldiff = moment(day + ' ' + _maxTime).diff(moment(day + ' ' + _minTime), 'minutes');
                var diff = _now.diff(moment(day + ' ' + _minTime), 'minutes');
                if (diff > alldiff) {
                  //超过当天可显示时间
                  _self.$calendarElement.find('.fc-time-grid-container .current-line').remove();
                } else {
                  var top = (diff * allheight) / alldiff - 5;
                  _self.$calendarElement.find('.fc-time-grid-container .current-line').css('top', top + 'px');
                }
              }
            }
          }
        }
      }
    },

    // 获取处理事项展示的搜索及其他相关数据
    _getQueryData: function (params) {
      var _self = this;
      var configuration = _self.getConfiguration();
      var calendarConf = configuration.calendar || {};
      var startTime = params.startTime,
        endTime = params.endTime;
      if (params.$calendarElement) {
        var dateRange = params.$calendarElement.fullCalendar('getView')._props.dateProfile.currentUnzonedRange;
        startTime = moment(dateRange.startMs).format('YYYY-MM-DD HH:mm');
        endTime = moment(dateRange.endMs).format('YYYY-MM-DD HH:mm');
      }
      var DayTimeArray = calendarConf.DayTimeArray.split(' - ');
      var _queryData = {
        diffDays: moment(endTime).diff(moment(startTime), 'days'), //搜索时间间隔天数
        startDate: moment(moment(startTime).format('YYYY-MM-DD') + ' ' + (DayTimeArray[0] || '00:00')), //搜索开始时间
        endDate: moment(moment(endTime).subtract(1, 'days').format('YYYY-MM-DD') + ' ' + (DayTimeArray[1] || '23:59')), //搜索结束时间
        _startDate: moment(moment(startTime).format('YYYY-MM-DD') + ' 00:00:00'), //搜索开始时间
        _endDate: moment(moment(endTime).subtract(1, 'days').format('YYYY-MM-DD') + ' 23:59:59'), //搜索结束时间
        startMin: DayTimeArray[0] || '00:00',
        endMin: DayTimeArray[1] || '23:59'
      };
      var nextDay = moment(_queryData.startDate).add(1, 'days');
      _queryData.onedayDiff = nextDay.diff(_queryData.startDate, 'minutes'); //间隔1天的分钟数
      _queryData.allDayDiff = moment(moment(startTime).format('YYYY-MM-DD') + ' ' + (DayTimeArray[1] || '23:59')).diff(
        _queryData.startDate,
        'minutes'
      );
      return _queryData;
    },

    /** 获取日历视图数据源对象 */
    getDataSource: function (params, callback) {
      var _self = this;
      var queryData = _self.getDataParams(params);
      var configuration = _self.getConfiguration();
      var calendarConf = configuration.calendar || {};
      var titleTemplate = _self.getTitleExpression(calendarConf.Title);
      var templateEngine = appContext.getJavaScriptTemplateEngine();
      var eventStatus = calendarConf.status || [];
      var eventStatusFieldName = configuration.eventStatusFieldName || 'isFinish';

      var _queryData = _self._getQueryData(params);

      $.ajax({
        url: ctx + '/basicdata/calendarcomponent/loadEvents',
        type: 'POST',
        data: JSON.stringify(queryData),
        dataType: 'json',
        async: false,
        contentType: 'application/json',
        success: function (result) {
          if (result.success) {
            // var finishCount = 0;
            // var ongoingCount = 0;

            _self._dataSource = []; //获取到新数据，需要先清空下
            $.each(result.data, function (index, event) {
              var data = $.extend(false, eventObject, event);

              // data.isFinish ? finishCount++ : ongoingCount++;

              // 设置块的颜色
              var iconColor = _self.getEventStatus(data[eventStatusFieldName] + '');
              iconColor = iconColor || {
                color: {
                  iconColor: getUserTheme().colorValue
                }
              };
              data.backgroundColor = iconColor.color.iconColor + ' !important';

              data.id = data.uuid;
              data.start = data['startTime'];
              data.end = data['endTime'];
              var startDate = moment(data.start);
              var endDate = moment(data.end);

              if (startDate.isSameOrBefore(_queryData._startDate) && endDate.isSameOrAfter(_queryData._endDate)) {
                //当前事项开始时间在搜索时间之前，结束时间在搜索时间之后
                data.allDay = true;
                var dataDiff = endDate.diff(_queryData._startDate, 'minutes'); //搜索开始时间到事项结束时间距离
                if (dataDiff < _queryData.onedayDiff) {
                  data.end = _queryData._endDate.add(1, 'day');
                }
              } else if (endDate.isBefore(_queryData._startDate) || startDate.isAfter(_queryData._endDate)) {
                //移除当前事项结束时间在搜索时间之前，开始时间在搜索时间之后，即不在日历时间段内数据
                data.ismove = true;
              } else if (_queryData.diffDays == 1) {
                if (startDate.isSameOrBefore(_queryData.startDate) && endDate.isSameOrAfter(_queryData.endDate)) {
                  //当前事项开始时间在搜索时间之前，结束时间在搜索时间之后
                  data.allDay = true;
                  var dataDiff = endDate.diff(_queryData._startDate, 'minutes'); //搜索开始时间到事项结束时间距离
                  if (dataDiff < _queryData.onedayDiff) {
                    data.end = _queryData._endDate.add(1, 'day');
                  }
                }
              } else if (_queryData.diffDays == 7) {
                //周
                if (startDate.isSameOrAfter(_queryData.startDate) && endDate.isSameOrAfter(_queryData.endDate)) {
                  // 搜索结束那天的开始时间
                  var _qEndStart = moment(moment(params.endTime).subtract(1, 'days').format('YYYY-MM-DD') + ' ' + _queryData.startMin);
                  if (startDate.isSameOrBefore(_qEndStart)) {
                    //事项开始时间在搜索结束时间在一天前
                    data.allDay = true;
                    if (startDate.format('HH:mm') > _queryData.startMin && startDate.format('HH:mm') <= _queryData.endMin) {
                      //事项开始时间小时分钟大于开始时间，则不占满格
                      var dataDiff = startDate.diff(moment(moment(startDate).format('YYYY-MM-DD') + ' ' + _queryData.startMin), 'minutes');
                      // if (dataDiff > 0) { //事件开始事件大于开始当天开始时间，全天不占全部
                      data.marginLeft_diff = dataDiff / _queryData.allDayDiff; //开始时间不满一天时，左侧不占当天格子大小百分比
                      // }
                    }
                  }
                } else if (startDate.isSameOrBefore(_queryData.startDate) && endDate.isSameOrBefore(_queryData.endDate)) {
                  // 搜索开始那天的结束时间
                  var _qStartEnd = moment(moment(_queryData.startDate).format('YYYY-MM-DD') + ' ' + _queryData.endMin);
                  if (endDate.isSameOrAfter(_qStartEnd)) {
                    //事项结束时间在搜索开始时间的结束时间之后
                    data.allDay = true;
                    if (endDate.format('HH:mm') >= _queryData.startMin) {
                      //事项结束时间小时分钟大于开始时间，说明当天有记录
                      data.end = moment(moment(endDate).add(1, 'day').format('YYYY-MM-DD') + ' ' + _queryData.startMin); //结束时间应设为第二天开始时间
                      var dataDiff = moment(moment(endDate).format('YYYY-MM-DD') + ' ' + _queryData.endMin).diff(endDate, 'minutes');
                      if (dataDiff > 0) {
                        //当天结束时间大于事件结束事件，全天不占全部
                        data.marginRight_diff = dataDiff / _queryData.allDayDiff; //结束时间不满一天时，右侧不占当天格子大小百分比
                      }
                    }
                  }
                } else {
                  //事项时间在搜索时间中间
                  if (startDate.format('HH:mm') <= _queryData.startMin || startDate.format('HH:mm') >= _queryData.endMin) {
                    if (startDate.format('HH:mm') >= _queryData.endMin) {
                      //事项开始时间小时分钟大于结束时间,事项开始时间设置为第二天开始时间
                      startDate = moment(moment(startDate).add(1, 'day').format('YYYY-MM-DD') + ' ' + _queryData.startMin);
                    }
                  }
                  if (endDate.format('HH:mm') <= _queryData.startMin || endDate.format('HH:mm') >= _queryData.endMin) {
                    if (endDate.format('HH:mm') >= _queryData.endMin) {
                      //事项结束时间小时分钟大于开始时间，说明当天有记录，结束时间应设为第二天开始时间
                      endDate = moment(moment(endDate).add(1, 'days').format('YYYY-MM-DD') + ' ' + _queryData.startMin);
                    }
                  }
                  var dataDiff = endDate.diff(startDate, 'minutes'); //搜索开始时间到事项结束时间距离
                  if (dataDiff >= _queryData.onedayDiff) {
                    data.allDay = true;
                    data.end = endDate;
                    if (startDate.format('HH:mm') > _queryData.startMin && startDate.format('HH:mm') <= _queryData.endMin) {
                      //事项开始时间小时分钟大于开始时间，则不占满格
                      var dataDiff = startDate.diff(moment(moment(startDate).format('YYYY-MM-DD') + ' ' + _queryData.startMin), 'minutes');
                      data.marginLeft_diff = dataDiff / _queryData.allDayDiff; //开始时间不满一天时，左侧不占当天格子大小百分比
                    }
                    if (endDate.format('HH:mm') > _queryData.startMin) {
                      //事项结束时间小时分钟大于开始时间，说明当天有记录
                      var dataDiff = moment(moment(endDate).format('YYYY-MM-DD') + ' ' + _queryData.endMin).diff(endDate, 'minutes');
                      if (dataDiff > 0) {
                        //当天结束时间大于事件结束事件，全天不占全部
                        data.marginRight_diff = -(1 - dataDiff / _queryData.allDayDiff); //结束时间不满一天时，右侧不占当天格子大小百分比
                      }
                    }
                  }
                }
              }

              if (startDate.format('YYYY-MM-DD') != endDate.format('YYYY-MM-DD')) {
                data.formatStr = 'YYYY-MM-DD HH:mm';
              }

              if ($.trim(titleTemplate).length > 0) {
                // data.origTitle = data.title;
                data.customTitle = templateEngine.render(titleTemplate, data);
              }

              if (!data.ismove) {
                _self._dataSource.push(data);
              }
            });

            // _self.ongoingCount = ongoingCount;
            // _self.finishCount = finishCount;

            // if (
            //   (_self.queryStartDate && _self.queryStartDate !== data.startDate) ||
            //   (_self.queryEndDate && _self.queryEndDate !== data.endDate)
            // ) {

            // }

            _self.queryStartDate = queryData.startDate;
            _self.queryEndDate = queryData.endDate;

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
      _self.invokeDevelopmentMethod('dataSourceProcessing', [_self._dataSource, eventStatus, eventStatusFieldName]);
      return _self._dataSource;
    },

    getDataResource: function (dataSourceEvents) {
      var _self = this;
      var configuration = _self.getConfiguration();
      var eventResourceFieldName = configuration.eventResourceFieldName;
      var otherConditions = _self.otherConditions;

      if (configuration.resProviderId) {
        var params = {};

        var eventResourceValArr = [];
        for (let i = 0; i < dataSourceEvents.length; i++) {
          var dataSourceEvent = dataSourceEvents[i];
          if (eventResourceValArr.indexOf(dataSourceEvent[eventResourceFieldName]) < 0) {
            eventResourceValArr.push(dataSourceEvent[eventResourceFieldName]);
          }
        }

        for (var i = 0; i < otherConditions.length; i++) {
          var otherCondition = otherConditions[i];
          params[otherCondition.columnIndex] = otherCondition.value;
          // if (otherCondition.columnIndex === 'belongObjId') {
          //   belongObjId = otherCondition.value;
          // }
        }
        params['whereSql'] = configuration.resDefaultCondition;
        params[eventResourceFieldName] = eventResourceValArr;

        var opts = {};
        if (configuration.resDefaultCondition && configuration.resDefaultCondition.trim()) {
          opts.defaultCriterions = [
            {
              sql: configuration.resDefaultCondition
            }
          ];
        }
        _self.dataResourceStore = new DataStore(
          $.extend(
            {
              async: false,
              pageSize: 1024,
              dataStoreId: configuration.resProviderId,
              params: params,
              onDataChange: function (data, totalCount, params, definitionJson) {
                //
                _self.dataResource = data;
              }
            },
            opts
          )
        );
        // var criterions = _self._collectCriterion({});
        _self.dataResourceStore.load(null, {});
        _self.dataResource = _self.dataResourceStore.getData() || [];
      }
      return _self.dataResource;
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

      options.criterions = options.criterions.concat(_self.otherConditions);
      options.criterions = options.criterions.concat(_self.getDefaultConditions());

      return options;
    },

    /** 未用到方法 */
    _renderCalendarCategory: function () {
      var _self = this;
      var categoryWith = 230;
      var widgetDefinition = {
        wtype: 'wHtml',
        title: '搜索栏',
        configuration: {
          customClass: '',
          jsModule: 'MyCalendarCategoryWidgetDevelopment',
          jsModuleName: '日程管理_我的日历分类',
          htmlSourceType: 'html_from_project_code_page',
          selectPage: '/calendar/calendar_category.html'
        },
        id: 'wHtml_CalendarCategory'
      };
      var $element = $(_self.element);
      var $wHtml = $('<div id="' + widgetDefinition.id + '" class="ui-wHtml col-xs-3"></div>');
      var $element = $(_self.element).addClass('col-xs-9').before($wHtml);
      $wHtml.css({
        width: categoryWith + 'px'
      });
      $element.attr({
        style: 'width: calc(100% - ' + categoryWith + 'px);'
      });
      appContext.require(['wHtml'], function callback() {
        appContext.createWidget(widgetDefinition, _self.options.containerDefinition);
      });
    },

    /** 将日历资源数据转换成资源视图数据 */
    _calendarData2ResourceData: function (dataSource) {
      var _self = this;
      dataSource = dataSource || [];
      var configuration = _self.getConfiguration();
      var resourceConf = configuration['resource'];
      var titleTemplate = _self.getTitleExpression(resourceConf.Title);
      var templateEngine = appContext.getJavaScriptTemplateEngine();
      var dataResource = _self.getDataResource(dataSource);
      var eventResourceFieldName = _self.getEventResourceFieldName();
      var resourceGroupId = configuration.resourceGroupId;
      var resourceGroupName = configuration.resourceGroupName;
      var objs = []; //commons.Lists.toGroupList(dataSource, groupField);
      $.each(dataResource, function (idx, res) {
        var groupId = res[resourceGroupId];
        var data = dataSource.filter(function (eventItem) {
          return eventItem[eventResourceFieldName] === groupId;
        });
        var obj = {
          res: res,
          data: data,
          groupId: groupId,
          groupName: res[resourceGroupName]
        };
        if ($.trim(titleTemplate).length > 0) {
          // obj['origGroupName'] = obj.groupName;
          obj.customGroupName = templateEngine.render(titleTemplate, res);
        }
        objs.push(obj);
      });
      // console.log(objs)
      return objs;
    },

    /** 获取资源来源ID */
    getResProviderId: function () {
      return this.getConfiguration().resProviderId;
    },

    /** 获取数据来源ID */
    getDataProviderId: function () {
      return this.getConfiguration().dataProviderId;
    },

    /** 渲染 标准视图 - 关键字查询 */
    _renderKeywordSearch: function () {
      var _self = this;
      var $content = _self.bindings;
      var $element = $(_self.element);
      var configuration = _self.getConfiguration();
      if (!configuration.query.keyword) return;
      var $fieldSearchElement = $("<div class='keyword_search_toolbar clearfix' style='padding-top:10px;'></div>");
      $element.prepend($fieldSearchElement);
      _self.options.searchType = 'keyword';

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

    /** 渲染 标准视图 - 字段查询 */
    _renderFieldSearch: function () {
      var _self = this;
      var $element = $(_self.element);
      var configuration = _self.getConfiguration();
      if (configuration.query.fields) {
        var searchToolbar = $('<div class="btn-group pull-right search-tool-bar">');
        searchToolbar.append(
          "<button class='well-btn w-btn-primary btn-query' type='button' name='query' title='查询'>查询</button>" +
            "<button class='well-btn w-btn-primary w-line-btn btn-reset' type='button' name='reset' title='重置'>重置</button>"
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

    /** 解析条件查询中配置下拉框数据来源 */
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
      var options = _self.options;
      var configuration = _self.getConfiguration();
      var resourceConfig = configuration.resource;
      if ($.isEmptyObject(configuration) || $.isEmptyObject(resourceConfig)) {
        appModal.warning('未定义资源日历组件');
        return;
      }

      var resourceId = options.widgetDefinition.id + '_resource';
      var $resourceElement = $("<div id='" + resourceId + "'></div>");
      $resourceElement.css({
        width: _self.$calendarElement.css('width')
      });
      $element.append($resourceElement);
      var dayLength = {
        min: _minTime,
        max: _maxTime,
        slotDuration: _self.getSlotDuration(resourceConfig.Gradu)
      };
      if (typeof resourceConfig.DayTimeArray === 'string') {
        var dayTimeArray = resourceConfig.DayTimeArray.split(' - ');
        // 6:00转06:00
        dayLength.min = moment(dayTimeArray[0], 'mm:ss').format('mm:ss');
        dayLength.max = moment(dayTimeArray[1], 'mm:ss').format('mm:ss');
      }
      // 选择面板选中事件
      var onSelectCallback = undefined;
      if (configuration.calendarType === 'panel') {
        onSelectCallback = _self.onSelectCallback;
      }

      /** 资源日历视图 初始化 * */
      var viewInstance = $resourceElement.view(
        $.extend(true, {
          titleJson: configuration.titleJson, //用于弹框标题
          calendarType: configuration.calendarType,
          eventStatusFieldName: configuration.eventStatusFieldName,
          status: resourceConfig.status, //状态及其颜色
          occupyStyle: resourceConfig.TimeType || '1',
          details: resourceConfig.detail,
          defaultDate: _self.$calendarElement.fullCalendar('getDate').format('YYYY-MM-DD'),
          activeView: resourceConfig.defaultResourceView,
          // defaultView: resourceConfig.defaultResourceView,
          setDayLength: dayLength,
          $calendarWidget: _self,
          eventData: _self._calendarData2ResourceData(_self._dataSource),
          getEventData: function () {
            return _self._calendarData2ResourceData(_self._dataSource);
          },
          onSelectCallback: onSelectCallback,
          clickCallback: function (date, e, viewType) {
            if (configuration.calendarType === 'panel') {
              return;
            }
            var _view = _self.$calendarElement.fullCalendar('getCalendar').view;
            //console.log('$resourceElement.clickCallback=' + date + ',viewType=' + viewType);
            _self.invokeEventHandler('dayClick', [date, e, _view]);
          },
          eventClickCallback: function (events) {
            if (configuration.calendarType === 'panel') {
              // return;
            }
            var _view = _self.$calendarElement.fullCalendar('getCalendar').view;
            //console.log('$resourceElement.eventClickCallback' + events);
            _self.invokeEventHandler('resouceEventListClick', [events, _view]);
            //_self._showEventList(events);
          }
        })
      );
      return $resourceElement;
    },

    // 选择面板选中事件
    onSelectCallback: function (valueObj) {
      var _self = this.$calendarWidget;
      if (valueObj) {
        var options = {
          event: {
            endTime: valueObj.end,
            startTime: valueObj.start
          }
        };
        // 选择时间后触发
        _self.invokeDevelopmentMethod('afterSelectResourseRender', [valueObj, options, _self.options, _self.getConfiguration()]);
        _self.openEventDialog(options, _self.getConfiguration());
      }
    },

    /** 收集查询条件 */
    _collectCriterion: function (obj) {
      var _self = this;
      var options = _self.options;
      var configuration = _self.getConfiguration();
      var $element = $(_self.element);
      var criterions = [];
      if (StringUtils.isNotBlank(_self.queryText)) {
        var orCriterion = {
          conditions: [],
          type: 'or'
        };
        $.each(configuration.columns, function (index, column) {
          if (column.hidden == '0') {
            orCriterion.conditions.push({
              columnIndex: column.name,
              value: _self.queryText,
              type: 'like'
            });
          }
        });
        criterions.push(orCriterion);
        if (obj) {
          obj.criterions = criterions;
        }
      }
      if (_self.options.searchType == 'keyword') {
        var text = $element.find('.keyword_search_toolbar .keyword-search-wrap input').val();
        if (StringUtils.isNotBlank(text)) {
          var orCriterion = {
            conditions: [],
            type: 'or'
          };
          $.each(configuration.columns, function (index, column) {
            if (column.keywordQuery != '0') {
              orCriterion.conditions.push({
                columnIndex: column.name,
                value: text,
                type: 'like'
              });
            }
          });
          criterions.push(orCriterion);
          if (obj) {
            obj.criterions = criterions;
            obj.keyword = text;
          }
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
                  }
                  if (StringUtils.isNotBlank(criterion.value[1])) {
                  }
                }
              }
              return criterion;
            }
          }
          return null;
        });
        if (obj) {
          obj.criterions = criterions;
        }
      }
      return obj ? obj : criterions;
    },

    /** 校验提交的数据 */
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
      var self = this;
      var configuration = self.getConfiguration();
      return configuration.resourceGroupId || '';
    },

    getEventStatus: function (code) {
      var self = this;
      var configuration = self.getConfiguration();
      var calendarConf = configuration.calendar || {};
      var eventStatus = calendarConf.status || [];
      return eventStatus.filter(function (v) {
        return v.code === code;
      })[0];
    },

    getEventStatusFieldName: function (code) {
      var self = this;
      var configuration = self.getConfiguration();
      return configuration.eventStatusFieldName || '';
    },

    getEventResourceFieldName: function () {
      var self = this;
      var configuration = self.getConfiguration();
      return configuration.eventResourceFieldName || '';
    },

    /** 获取参数 */
    getParam: function (key) {
      return this.ajaxParams[key];
    },

    /** 获取视图数据集合 */
    getData: function () {
      return this.$calendarElement.fullCalendar('getEventSources');
    },

    /** 根据唯一键获取列数据 */
    getEventByUniqueId: function (id) {
      return this.$calendarElement.fullCalendar('getEventSourceById', id);
    },

    /** 刷新数据 */
    refresh: function (params) {
      var _self = this;
      if (_self.wellCalendar) {
        _self.wellCalendar.refresh();
      } else {
        //重新触发events function
        _self.$calendarElement.fullCalendar('refetchEvents');
        // 刷新资源视图
        var resourceDatas = _self._calendarData2ResourceData(_self._dataSource);
        if (_self.$resourceElement) {
          _self.$resourceElement.view('refresh', resourceDatas);
        }
      }
    },

    /** 查询数据 */
    query: function (params) {
      var _self = this;
      _self.$calendarElement.fullCalendar('removeEvents');

      var events = _self.getDataSource({
        startTime: params && params.start ? params.start : _viewStart,
        endTime: params && params.end ? params.end : _viewEnd,
        $calendarElement: _self.$calendarElement
      });

      if (_self.$resourceElement) {
        _self.$resourceElement.view('refresh', _self._calendarData2ResourceData(events));
      }

      _self.$calendarElement.fullCalendar('renderEvents', events);
      _self.$calendarElement.find('.fc-statusFilter-button').removeClass('show-popup');
    },

    /** 添加额外的查询条件 */
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

    /** 清空额外查询条件,condition为空是清除全部，否则清除等于condition的一条额外查询条件 */
    /**
     *
     * @param {*} condition 可传空或者数组,数组项只要查询条件的columnIndex值
     */
    clearOtherConditions: function (condition) {
      var _self = this;
      var otherConditions = _self.otherConditions;
      if (condition && condition.length) {
        _.each(condition, function (item) {
          _self.otherConditions = $.map(otherConditions, function (cond) {
            if (JSON.stringify(item) != JSON.stringify(cond.columnIndex)) {
              return cond;
            }
          });
        });
      } else {
        _self.otherConditions = [];
      }
    },

    /** 重新计算（设置）视图高度 */
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

    /** 需要查询时调用 */
    _onSearch: function () {
      this.query();
    },

    /** 重置查询条件 */
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

    _startApp: function (opt) {
      var _self = this;
      _self.startApp(opt);
      if (opt.eventHandler && $.trim(opt.eventHandler['afterEventHandlerCodes']).length > 0) {
        // 事件处理后
        appContext.eval(opt.eventHandler['afterEventHandlerCodes'], _self, {
          opt: opt
        });
      }
    },

    /** 注册按钮事件，调用相关二开接口 */
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
            view: _self,
            target: target.position,
            appType: eventHandler.type,
            appPath: eventHandler.path,
            targetWidgetId: target.widgetId,
            refreshIfExists: target.refreshIfExists,
            eventHandler: button.eventHandler.eventHandler,
            params: $.extend({}, params, appContext.parseEventHashParams(eventHandler, 'menuid')),
            viewOptions: options
          };
          $contextElement.on('click', buttonSelector, function (event) {
            opt.event = event;
            _self._startApp(opt);
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

    /** 获取JS模块 */
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

    /** 获取控件真实高度 */
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

    /** 渲染事项查看表单 */
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

    /** 渲染新建事项表单 */
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

    /** TODO 渲染自定义事项表单 */
    _renderCustomItemForm: function (event, exist) {
      return '';
    },

    /** 打开事项弹出框 */
    openEventDialog: function (options) {
      var _self = this;
      var configuration = _self.getConfiguration();
      var _thisButtons = _self._viewEventButtonsSetting(options, configuration.customOptions, {
        ok: {
          label: '保存', // 查看事项->删除
          className: 'well-btn w-btn-primary js-btn-save',
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
        }
      });

      // 默认都有取消按钮
      $.extend(true, _thisButtons, dialogButtons);

      var title = options.title || (_self.getConfiguration().titleJson && _self.getConfiguration().titleJson.title) || '事项';

      //清除所有弹出的编辑框
      _.each($('.bootbox'), function (item) {
        if ($(item).find('#form_calendar_event')[0]) {
          $(item).remove();
        }
      });

      var $dialog = appModal.dialog({
        title: options.event.uuid ? '编辑' + title : '新建' + title,
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
          _self.initFormValidateRule();
        },
        onEscape: function () {
          $('.layui-laydate').remove(); //时间控件弹出窗
        },
        buttons: _thisButtons
      });
      return $dialog;
    },

    /** 定义表单的校验规则 */
    initFormValidateRule: function () {
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
      var configuration = _self.getConfiguration();
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

      // 结束时间不能早于开始时间
      validateOptions.rules['itemform_endTime'] = {
        checkLtEndTime: true
      };
      $.validator.addMethod(
        'checkLtEndTime',
        function (value, element) {
          //有设置重复进行检查
          var start = moment($('#itemform_startTime').val());
          var end = moment(value);
          return start.isBefore(end);
        },
        '结束时间不能早于开始时间。'
      );

      //开放添加校验器的路口
      _self.invokeDevelopmentMethod('addOtherValidateRules', [validateOptions, _self.getConfiguration()]);

      _self._validator = $('#form_calendar_event').validate(validateOptions);
    },

    /** 查看/编辑事项 */
    _viewEvent: function (options) {
      var _self = this;
      var _thisButtons = _self._viewEventButtonsSetting(options, options.customOptions, {
        edit: {
          label: '编辑', // 查看事项->编辑
          className: 'well-btn w-btn-primary no-order js-btn-edit',
          callback: function () {
            _self.openEventDialog(options);
          }
        },
        del: {
          label: '删除', // 查看事项->删除
          className: 'well-btn w-btn-danger js-btn-del',
          callback: function () {
            _self._formDeleteClick(options);
          }
        }
      });
      $.extend(true, _thisButtons, dialogButtons);

      var title = options.title || (_self.getConfiguration().titleJson && _self.getConfiguration().titleJson.title) || '事项';

      var $dialog = appModal.dialog({
        size: 'middle',
        title: title + '详情' || '详情',
        message: $("<div id='div_calendar_view_form'></div>"),
        shown: function (_$dialog) {
          _self._renderViewForm(options.event);
          _self.invokeDevelopmentMethod('afterViewEventDialogRender', [_$dialog, options, _self.getConfiguration()]);
        },
        buttons: _thisButtons
      });

      return $dialog;
    },

    _viewEventButtonsSetting: function (options, customOptions, initBtns) {
      var _self = this;
      var _thisButtons = {};
      //如果btns无设置，则默认组件的编辑、删除和关闭按钮，如果有值，则显示该数组按钮
      if (customOptions && customOptions.btns) {
        _.each(customOptions.btns, function (item) {
          _thisButtons[item.code] = item;
        });
      } else {
        _.each(initBtns, function (item, index) {
          _thisButtons[index] = item;
        });
      }
      return _thisButtons;
    },

    /** 设置时间段 */
    _setMinToMaxTime: function ($element) {
      var _self = this;
      var options = _self.options;
      var calendarId = options.widgetDefinition.id;
      var oldView = $("select[name='selectView']").val();
      var configuration = _self.getConfiguration();
      var viewConfiguration = configuration[oldView] || {};
      var $timeQuantum = $("<div id='" + calendarId + "_setTimeQuantum' class='taxt' style='margin: 20px 40px;'>");

      function getTimeTextBySliderValue(val) {
        if (val % 2) {
          return [prefixZero((val - 1) / 2), 30].join(':');
        } else {
          return [prefixZero(val / 2), prefixZero(0)].join(':');
        }
      }

      appModal.dialog({
        title: '时间设置',
        size: 'middle',
        message: $timeQuantum,
        shown: function () {
          $('.modal-content').css('width', '560px');
          if (viewConfiguration.allowCustomDayTime) {
            var timeQuantumBar = new commons.StringBuilder();
            timeQuantumBar.append("	<label class='custom-daytime'>显示时间区间</label>");
            timeQuantumBar.append("	<div id='" + calendarId + "_slider' class='calendar-timeQuantum-silder'></div>");
            timeQuantumBar.append('	</div>');
            $timeQuantum.append(timeQuantumBar.toString());

            var $calendarSet = $('#' + calendarId + '_setTimeQuantum');
            var $calendarSlider = $('#' + calendarId + '_slider');

            var beginHour = moment(_minTime, 'mm:ss').format('m');
            var beginMinute = moment(_minTime, 'mm:ss').format('s');
            var endHour = moment(_maxTime, 'mm:ss').format('m');
            var endMinute = moment(_minTime, 'mm:ss').format('s');

            var startTime = [prefixZero(beginHour), prefixZero(beginMinute % 29 ? 30 : 0)].join(':');
            var endTime = [prefixZero(endHour), prefixZero(endMinute % 29 ? 30 : 0)].join(':');
            var $amount0 = $('<span class="sildertext">').html(startTime);
            var $amount1 = $('<span class="sildertext">').html(endTime);

            $calendarSlider.slider({
              range: true,
              min: 0,
              max: 48,
              values: [beginHour * 2 + (beginMinute % 29), endHour * 2 + (endMinute % 29)],
              slide: function (event, ui) {
                if (ui.values[0] == ui.values[1]) {
                  return false;
                }

                $amount0.html(getTimeTextBySliderValue(ui.values[0]));
                $amount1.html(getTimeTextBySliderValue(ui.values[1]));
              },
              create: function () {
                $($(this).find('.ui-slider-handle')[0]).append($amount0);
                $($(this).find('.ui-slider-handle')[1]).append($amount1);
              }
            });
          }
          if (viewConfiguration.allowCustomGradu) {
            var timeGradu = new commons.StringBuilder();
            timeGradu.append("<div class=''>");
            timeGradu.append("<label class='custom-gradu'>时间刻度</label>");
            timeGradu.append('<select name="customGradu" class="mt10 mb20">');
            timeGradu.append('	<option value="15">15分钟</option>');
            timeGradu.append('	<option value="30">30分钟</option>');
            timeGradu.append('	<option value="60">1小时</option>');
            timeGradu.append('</select>');
            timeGradu.append('</div>');
            var $timeGradu = $(timeGradu.toString());
            $timeQuantum.append($timeGradu);
            $timeGradu.find('select[name="customGradu"]').val(viewConfiguration.Gradu);
          }
        },
        buttons: {
          ok: {
            label: '确定',
            className: 'well-btn w-btn-primary',
            callback: function () {
              // 保持原来的视图，所以需要先记录下
              var settingOptions = {};
              if (viewConfiguration.allowCustomDayTime) {
                var $calendarSlider = $('#' + calendarId + '_slider');

                _minTime = getTimeTextBySliderValue($calendarSlider.slider('values', 0));
                _maxTime = getTimeTextBySliderValue($calendarSlider.slider('values', 1));

                viewConfiguration.DayTimeArray = _minTime + ' - ' + _maxTime;
                $.extend(settingOptions, {
                  minTime: _minTime,
                  maxTime: _maxTime,
                  min: _minTime,
                  max: _maxTime
                });
                //重新设置了时间段，日历头部会被重新渲染，所以这里需要重新渲染下视图切换下拉框
              }
              if (viewConfiguration.allowCustomGradu) {
                var slotDuration = $('select[name=customGradu]').val();
                viewConfiguration.Gradu = slotDuration;

                // $element.removeClass('gradu-15, gradu-30, gradu-60').addClass('gradu-' + slotDuration);
                // $element.removeClass('gradu-15').removeClass('gradu-30').removeClass('gradu-60').addClass('gradu-' + slotDuration);

                settingOptions.slotDuration = _self.getSlotDuration(slotDuration);
              }
              if (oldView === 'calendar' && _self.$calendarElement) {
                _self.$calendarElement.fullCalendar('option', settingOptions);
              } else if (oldView === 'resource' && _self.$resourceElement) {
                _self.$resourceElement.view('setDayHour', settingOptions);
              }
              _self._renderStandardViewSelect($element, oldView);
            }
          },
          cancel: {
            label: '关闭',
            className: 'well-btn w-btn-primary w-line-btn',
            callback: function () {}
          }
        }
      });
    },

    /** 保存事项 */
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

    /**  删除事项 */
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

    /** 收集事项表单数据 */
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

    /** 弹窗表单"确认" */
    _formOkClick: function (options) {
      var _self = this;
      appModal.showMask();
      $('.layui-laydate').remove(); //时间控件弹出窗
      var newFormData = _self._collectEventData(options);
      _self.invokeDevelopmentMethod('afterCollectEventData', [options, _self.getConfiguration(), newFormData]);

      var _callback = _self.getAfterCollectEventData('getAfterCollectEventDataCB', [options, _self.getConfiguration(), newFormData]);
      if (_callback === false) {
        appModal.hideMask();
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
          //刷新简版日历
          if (_self.wellCalendar && _self.wellCalendar.refresh) {
            _self.wellCalendar.refresh();
          }
          appModal.hideMask();
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
        appModal.hideMask();
      }

      return false;
    },

    /** 弹窗表单"删除" */
    _formDeleteClick: function (options, fn) {
      var _self = this;
      _self.invokeDevelopmentMethod('deleteEventClick', [options.event, _self.options, _self.getConfiguration()]);
    },

    /** 更新事项数据 */
    _updateEvent: function (event) {
      var _self = this;
      var configuration = _self.getConfiguration();
      if (configuration.calendarType === 'simple') {
        var $li = $('li[data-id=' + event.uuid + ']');
        $li.attr('data-item', JSON.stringify(event));
        if (configuration.calendar && configuration.calendar.defaultView === 'agendaDay') {
          var bg = event.backgroundColor.replace(/ !important/, '');
          $li[0].style.backgroundColor = hexify(toRgba(bg, 0.2));
          $li[0].style.setProperty('--bg', bg);
          $li[0].style.setProperty('--bgrgb', hexify(toRgba(bg, 0.3)));
        }
        return;
      }
      if (event) {
        //单独更新
        _.each(_self._dataSource, function (item, index) {
          if (event && event.uuid == item.uuid) {
            _self._dataSource[index] = event;
          }
        });
      }
      if (StringUtils.isNotBlank(event.uuid)) {
        _self.$calendarElement.fullCalendar('updateEvent', event);
      } else {
        _self.$calendarElement.fullCalendar('renderEvent', event);
      }
      _self.reRenderResourceElement(); //重构资源视图
    },

    //重构资源视图
    reRenderResourceElement: function () {
      var _self = this;
      //刚开始初始化的时候$resourceElement 没有值，所以加一个判断这里
      if (_self.$resourceElement) {
        // 刷新资源视图
        var currView = _self.$calendarElement.fullCalendar('getView');
        var resourceDatas = _self._calendarData2ResourceData(_self._dataSource);
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

    /** 默认的删除事项事件 */
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

    // 打开资源视图列表页（已无效）
    _showEventList: function (events) {
      var _self = this;
      var configuration = _self.getConfiguration();
      var $allEventBox = $('<div id="div_event_list"/>');
      $.each(events, function (i) {
        var e = events[i];
        var $eventDiv = $("<div class='event-box'/>");
        var $table = $('<table class="table"/>');
        var title = e.title;
        if (!e.isCanView && e.calendarCreatorName) {
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
          var $more = $('<div class="more-div text-right"><button class="more well-btn w-btn-primary w-line-btn">更多详情</button></div>');
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
          $.each(_$dialog.find('button.more'), function () {
            $(this).click(function () {
              var uuid = $(this).parents('div').data('event');
              _self._viewEvent({
                event: e
              });
              $dialog.find('.bootbox-close-button').trigger('click');
            });
          });
          _self.invokeDevelopmentMethod('afterEventListDialogRender', [_$dialog, events, _self.options, _self.getConfiguration()]);
        }
      });
    },

    invokeEventHandler: function (eventName, eventArgs) {
      var _self = this;
      var configuration = _self.getConfiguration(),
        viewConfig;
      var defaultView = _self.defaultView || 'calendar';
      if (eventName === 'dayClick' || eventName === 'eventClick' || eventName === 'resouceEventListClick') {
        var handlerName = null;
        if (eventName === 'dayClick') {
          handlerName = 'panel.click';
        } else if (eventName === 'eventClick' || eventName === 'resouceEventListClick') {
          handlerName = 'event.click';
        }
        if (handlerName && (viewConfig = configuration[defaultView])) {
          for (var i = 0; i < viewConfig.events.length; i++) {
            var ee = viewConfig.events[i];
            if (ee.code === handlerName && ee.eventHandler) {
              // console.log(ee);
              var eventHandler = ee.eventHandler || {};
              var target = ee.target || {};
              var params = eventHandler.eventParams ? eventHandler.eventParams.params : {};
              eventHandler = eventHandler.eventHandler;
              if (eventHandler.path || $.trim(eventHandler.afterEventHandlerCodes).length > 0) {
                var opt = {
                  view: _self,
                  eventName: eventName,
                  target: target.position,
                  targetWidgetId: target.widgetId,
                  refreshIfExists: target.refreshIfExists,
                  eventArgs: eventArgs,
                  appType: eventHandler.type,
                  appPath: eventHandler.path,
                  eventHandler: eventHandler,
                  params: $.extend({}, params),
                  viewOptions: _self.options
                };
                return _self._startApp(opt);
              }
            }
          }
        }
      }
      _self.invokeDevelopmentMethod.apply(_self, arguments);
    },

    getTitleExpression: function (origTitle) {
      if ($.trim(origTitle).length) {
        var juicerSchema = 'juicer://';
        if (origTitle.indexOf(juicerSchema) === 0) {
          return origTitle.substr(juicerSchema.length);
        }
        return origTitle.replace(/\$\{([^(]*?)\(([^)]+?)\)\}/g, function (matched, g1, g2, index, originalText) {
          return '{@if null != ' + g2 + '}${' + g2 + '}{@/if}';
        });
      }
    }
  });
});
