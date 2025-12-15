(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'moment', 'lodash', 'commons'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function (jquery, moment, _, commons) {
  'use strict';
  var StringBuilder = commons.StringBuilder;
  // 基本配置
  var defaultConfig = {
    clickCallback: function (date, event, view) {
      //空白面板的点击回调
      /* alert(event) */
    },
    eventClickCallback: function (event) {
      //事项的点击回调函数
      // createModalTable(event);
    },
    setDayLength: {
      min: '00:00',
      max: '24:00'
    },
    defaultDate: moment().format('YYYY-MM-DD'),
    activeView: 'month', // month, agendaWeek, agendaDay 月，周，日三个视图
    eventData: [], // 事项数据，已经 是分过组的
    $calendarWidget: null, //日历面板的widget
    weekLang: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  };

  // 创建视图类
  var View = function (options) {
    this.options = options;
    this.activeView = options.activeView; // 当前展示的视图
    this.defaultDate = options.defaultDate; // 默认日期
    // this.dayLength = options.setDayLength;
    this.el = $(options.el);
    this.el.addClass('resource-view');
    this.eventData = options.eventData;
    this.$calendarWidget = options.$calendarWidget;
    this.weekLang = options.weekLang;
  };

  View.prototype = {
    // 获取传参日期对应月份的所有日期
    _computedDate: function (d) {
      var dateArr = moment(d, 'YYYY-MM-DD').toArray();
      var totalDays = moment(d).daysInMonth(); //获取该月的总天数
      var year = dateArr[0];
      var month = dateArr[1] + 1;
      var monthDateArr = [];
      for (var i = 1; i <= totalDays; i++) {
        var date = moment(year + '-' + month + '-' + i).format('YYYY-MM-DD');
        monthDateArr.push(date);
      }
      return monthDateArr;
    },

    // 根据参数日期，获取该日期所在的那一周，周一到周日的日期
    _computedWeek: function (d) {
      var date = moment(d, 'YYYY-MM-DD');
      var weekOfday = date.format('E'); // 计算今天是这周第几天
      var monday = date
        .clone()
        .subtract(weekOfday - 1, 'days')
        .format('YYYY-MM-DD'); // 周一日期
      // 返回这一周的日期
      var weekArr = [monday];
      for (var i = 1; i < 7; i++) {
        weekArr.push(moment(monday).add(i, 'days').format('YYYY-MM-DD'));
      }
      return weekArr;
    },
    // 判断参数日期是否今天，
    _isToday: function (date) {
      var className = 'today';
      var today = moment().format('YYYY-MM-DD');
      var flag = moment(today).isSame(moment(date));
      if (flag) {
        return className;
      }
      return '';
    },

    // 按月视图处理数据
    _dealMonthData: function (timePeriod, events) {
      var _self = this;
      var monthData = _self._groupEventByDate(events, timePeriod);
      return monthData;
    },

    // 将事件按天分组, 返回一个map
    _groupEventByDate: function (events, dateArray, dayTimePeriod) {
      var _self = this;
      var monthData = {};
      events.forEach(function (n) {
        var eventsOfGroup = n.data;
        var monthEventArr = [];
        for (var i = 0; i < dateArray.length; i++) {
          var date = moment(dateArray[i] + ' 00:00:00');
          var nextDate = moment(dateArray[i] + ' 23:59:59');
          var dayEvents = [];
          $.each(eventsOfGroup, function (j) {
            var event = eventsOfGroup[j];
            // 事件开始时间
            var eventStart = moment(event.startTime, 'YYYY-MM-DD HH:mm:ss');
            // 事件结束时间
            var eventEnd = moment(event.endTime, 'YYYY-MM-DD HH:mm:ss');
            // 事件包括的开始和结束时间，有经过该日期
            if (eventEnd.isBefore(date) || eventStart.isAfter(nextDate)) {
              //事件要么在今天之前就结束，要么在今天之后才开始，
            } else {
              dayEvents.push(event);
            }
          });
          var obj = {
            res: n.res,
            events: dayEvents,
            groupId: n.groupId,
            groupName: n.groupName,
            date: date.format('YYYY-MM-DD'),
            customGroupName: n.customGroupName
          };
          monthEventArr.push(obj);
        }
        monthData[n.groupId] = {
          'dateList': monthEventArr,
          'eventData': eventsOfGroup
        };
      });
      return monthData;
    },

    // 按周处理数据
    _dealWeekData: function (timePeriod, events) {
      var _self = this;
      var weekData = _self._groupEventByDate(events, timePeriod);
      return weekData;
    },

    // 按天处理数据
    _dealDayData: function (timePeriod, events) {
      var _self = this;
      var day = moment(_self.defaultDate).format('YYYY-MM-DD');
      //只过滤出指定日期那天的数据
      var dayData = _self._groupEventByDate(events, [day]);
      return dayData;
    },

    //判断两个时间段内是否与交集
    _isHasIntersection: function (start1, end1, start2, end2) {
      if (end1.isBefore(start2) || start1.isAfter(end2)) {
        return false;
      }
      return true;
    },

    _getGroupNameFromMap: function (key) {
      var _self = this;
      if (_self.$calendarWidget.groupDataMap) {
        var text = _self.$calendarWidget.groupDataMap[key];
        if (text) {
          return text;
        }
      }
      return key;
    },

    // 设置时间段的宽度及偏移量
    _renderEventData: function (data, viewType) {
      var _self = this;
      var min_maxT = _self._dealDateRange();
      var mergedData = _self._mergeEvent(data, min_maxT);
      _self.$calendarWidget.invokeDevelopmentMethod('loadResourceDataBefore', [_self.$calendarWidget.options, data, mergedData]);
      $.each(mergedData, function (groupId, mergeEventList) {
        $.each(mergeEventList, function (i, eventObj) {
          //判断如果时间段不在设置的最大最小时间段内，则不展示
          if (eventObj.start._i == eventObj.end._i || eventObj.start.isAfter(min_maxT.maxT) || eventObj.end.isBefore(min_maxT.minT)) {
            //结束，进行下一个
            return;
          }
          var len = _self._computedEventplacehoder(eventObj.start, eventObj.end, min_maxT);
          var eventTimeLength = len[1];
          var leftWidth = len[2];
          eventObj.length = eventObj.events.length;
          eventObj.width = Math.min(eventTimeLength, 100) + '%';
          eventObj.left = Math.min(leftWidth, 100) + '%';
        });
      });

      _self.$calendarWidget.invokeDevelopmentMethod('loadResourceDataAfter', [_self.$calendarWidget.options, data, mergedData]);
      _self._renderEventBox(data, mergedData);
    },

    //计算当前视图的时间范围
    _dealDateRange: function () {
      var _self = this;
      var minT = '',
        maxT = '',
        formatVal = '',
        minRange = '',
        _min = _self.options.setDayLength.min,
        _max = _self.options.setDayLength.max;
      var viewType = _self.options.activeView;
      if (viewType == 'agendaDay') {
        var eventStartDate = moment(_self.defaultDate).format("YYYY-MM-DD");
        minT = moment(eventStartDate + ' ' + _self.options.setDayLength.min, "YYYY-MM-DD HH:mm");
        maxT = moment(eventStartDate + ' ' + _self.finalMaxTime, "YYYY-MM-DD HH:mm");
        _max = _self.finalMaxTime;
        formatVal = 'HH:mm';
        minRange = moment(_self.options.setDayLength.slotDuration, 'HH:mm:ss').diff(moment("00:00:00", 'HH:mm:ss'), 'minutes'); //最小为1小时
      } else if (viewType == 'agendaWeek') {
        _min = '00:00';
        _max = '23:59';
        var weekOfday = moment(_self.defaultDate, "YYYY-MM-DD").format("E"); //计算今天是这周第几天
        var eventStartDate = moment(_self.defaultDate).subtract(weekOfday - 1, "days").format("YYYY-MM-DD"); //周一日期
        minT = moment(eventStartDate + ' ' + '00:00', "YYYY-MM-DD HH:mm");
        maxT = moment(moment(eventStartDate).add(6, "days").format("YYYY-MM-DD") + ' ' + '23:59', "YYYY-MM-DD HH:mm");
        formatVal = 'YYYY-MM-DD';
        //第二天开始时间
        var currDay = moment(eventStartDate + ' ' + '23:59', "YYYY-MM-DD HH:mm");
        minRange = currDay.diff(minT, 'minutes') / 2; //最小为半天-实际长度为早上开始时间到下午结束时间的一半
      } else if (viewType == 'month') {
        _min = '00:00';
        _max = '23:59';
        eventStartDate = moment(eventStartDate).format("YYYY-MM") + '-01';
        minT = moment(eventStartDate + ' ' + '00:00', "YYYY-MM-DD HH:mm");
        maxT = moment(moment(eventStartDate).add(1, "month").subtract(1, "days").format("YYYY-MM-DD") + ' ' + '23:59', "YYYY-MM-DD HH:mm");
        formatVal = 'YYYY-MM-DD';
        //第二天开始时间
        var currDay = moment(eventStartDate + ' ' + '23:59', "YYYY-MM-DD HH:mm");
        minRange = currDay.diff(minT, 'minutes'); //最小为1天-实际长度为早上开始时间到下午结束时间
      }
      //第二天开始时间
      var nextDay = moment(moment(maxT).add(1, "day").format("YYYY-MM-DD") + ' ' + _min, "YYYY-MM-DD HH:mm");
      //计算跨一天要省略多少分钟数
      var oneDayDiff = nextDay.diff(maxT, 'minutes');
      return {
        'minT': minT,
        'maxT': maxT,
        'minHM': _min,
        'maxHM': _max,
        'formatVal': formatVal,
        'oneDayDiff': oneDayDiff,
        'minRange': minRange //一个事项显示最小时间刻度
      }
    },

    //合并日资源事项
    _mergeEvent: function (events, min_maxT) {
      var _self = this;
      var dataMap = {};
      $.each(events, function (groupId, eventObjArray) {
        var eventList = eventObjArray.eventData
        //根据设置的时间点，过滤出有效的事项，并按开始时间升序排序
        var valideEventList = _self._filterValideEventsAndSort(eventList, min_maxT);
        //开始根据时间段，合并事项
        var mergeList = _self._mergeEventFun(valideEventList);
        _.each(mergeList, function (item, index) {
          //事项最小宽度设置
          var diffDays = item.end.diff(item.start, 'minutes'); //实际相隔分钟数
          if (diffDays < min_maxT.minRange) {
            // 设置结束时间等于开始时间+最小分钟数
            var end = moment(item.start.add(min_maxT.minRange, 'minute').format('YYYY-MM-DD HH:mm:ss'), 'YYYY-MM-DD HH:mm:ss');
            // 设置的结束时间分钟数在大于最大时间，小于最小时间，这段时间属于不计算长度时间段
            if (moment(end).format("HH:mm") > min_maxT.maxHM || moment(end).format("HH:mm") < min_maxT.minHM) {
              var currDate = end.format("YYYY-MM-DD");
              if (moment(end).format("HH:mm") <= "23:59" && moment(end).format("HH:mm") > min_maxT.maxHM) { //还在当天,则在第二天开始时间加上该时间段
                var diff = end.diff(moment(currDate + ' ' + maxHM), 'minute');
                var start = moment(moment(currDate).add(1, 'day').format("YYYY-MM-DD") + ' ' + min_maxT.minHM);
                item.end = moment(start.add(diff, 'minute').format('YYYY-MM-DD HH:mm:ss'), 'YYYY-MM-DD HH:mm:ss');
              } else {
                currDate = moment(currDate).subtract(1, 'day').format("YYYY-MM-DD");
                var diff = end.diff(moment(currDate + ' ' + min_maxT.maxHM), 'minute');
                var start = moment(end.format("YYYY-MM-DD") + ' ' + min_maxT.minHM);
                item.end = moment(start.add(diff, 'minute').format('YYYY-MM-DD HH:mm:ss'), 'YYYY-MM-DD HH:mm:ss');
              }
            } else {
              item.end = end;
            }
          }
        })
        // 有些块宽度太小，重新设置后再次合并
        mergeList = _self._mergeEventFun(mergeList);
        //填充其他状态色块
        mergeList = _self._filterOtherEvents(mergeList, min_maxT);

        dataMap[groupId] = mergeList;
      });
      return dataMap;
    },

    // 合并事项
    _mergeEventFun: function (valideEventList) {
      var _self = this;
      var mergeList = [];
      if (valideEventList && valideEventList.length > 0) {
        var prev = valideEventList[0];
        for (var i = 1; i < valideEventList.length; i++) {
          var curr = valideEventList[i];
          prev.end = moment(prev.end._i, prev.end._f);
          curr.start = moment(curr.start._i, curr.start._f);
          if (curr.events.length && prev.end >= curr.start) {
            //两个事项时间有交集，合并
            var es = [].concat(prev.events).concat(curr.events);
            var mergeEvent = {
              start: prev.start,
              end: prev.end > curr.end ? prev.end : curr.end,
              events: es
            };
            prev = mergeEvent;
          } else {
            prev.statusData = _self._getStatusData('occupied', prev.events[0]); //占用颜色
            prev.class = prev.class || 'occupied';
            //没有交集，独立时间段
            mergeList.push(prev);
            prev = curr;
          }
        }
        prev.statusData = _self._getStatusData('occupied', prev.events[0]); //占用颜色
        prev.class = prev.class || 'occupied';
        mergeList.push(prev);
      }
      return mergeList;
    },

    // 获取状态对应的颜色值
    _getStatusData: function (code, obj) {
      var _self = this,
        code1 = "",
        color = "";
      // if(obj){
      //   color = obj.backgroundColor && obj.backgroundColor.split(" ")[0]; //日历视图事件 状态颜色
      //   code1 = obj[_self.options.eventStatusFieldName]+''; // 获取当前事件状态
      // }
      var statusItem = _.find(_self.options.status || [], {
        'code': code1 || code
      }) || {};
      if (statusItem.color) {
        statusItem.bgcolor = (statusItem.color && statusItem.color.iconColor);
      } else {
        if (code == 'occupied') { //事件占用
          statusItem.bgcolor = color || getUserTheme().colorValue || '#a3c5f6';
          statusItem.text = "";
        } else {
          statusItem.bgcolor = "#ffffff";
          statusItem.text = "";
        }
      }
      return statusItem;
    },


    //过滤出有效时间段内的事项数据，并排序，返回一个LIST
    _filterValideEventsAndSort: function (eventList, min_maxT) {
      var _self = this;
      var valideEventList = [];
      var startHour = moment(min_maxT.minT._i, min_maxT.minT._f);
      var endHour = moment(min_maxT.maxT._i, min_maxT.maxT._f);
      $.each(eventList, function (i) {
        var event = eventList[i];
        var eventStart = moment(event.startTime, 'YYYY-MM-DD HH:mm:ss');
        var eventEnd = moment(event.endTime, 'YYYY-MM-DD HH:mm:ss');
        //只计算在过滤的时间范围内的事项
        if (eventEnd.isBefore(startHour) || eventStart.isAfter(endHour)) {
          console.log('事项不在过滤的时间段内，剔除掉=' + event);
        } else {
          //转化时间,不让时间超过设置的时间段内
          //开始时间比过滤的开始时间早，则用过滤开始时间
          var compareStart = eventStart.isBefore(startHour) ? startHour : eventStart;
          //结束时间比过滤的结束时间晚，则用过滤结束时间
          var compareEnd = eventStart.isAfter(endHour) ? endHour : eventEnd;
          var eventObj = {
            start: compareStart,
            end: compareEnd,
            events: [event],
          };
          valideEventList.push(eventObj);
        }
      });

      //排序
      var sortEvents = _.sortByOrder(valideEventList, ['start'], ['asc']);
      return sortEvents;
    },

    // 计算空余与过期时间
    _filterOtherEvents: function (mergeList, min_maxT) {
      var _self = this;
      var startNull = moment(min_maxT.minT._i, min_maxT.minT._f);
      var _otherList = []
      _.each(mergeList, function (item) {
        item.start = moment(item.start._i);
        item.end = moment(item.end._i);
        //非占用开始时间早于事项开始时间，则插入数据项
        if (startNull.isBefore(item.start)) {
          _otherList = _otherList.concat(_self._computerOtherEvents(startNull, _self._computedRangTime(item.start, 'end', min_maxT)));
        }
        startNull = _self._computedRangTime(item.end, 'start', min_maxT);
      })
      //非占用开始时间依旧等于开始时间，则插入数据项
      if (startNull.isSame(min_maxT.minT)) {
        _otherList = _otherList.concat(_self._computerOtherEvents(min_maxT.minT, min_maxT.maxT));
      } else if (startNull < min_maxT.maxT) { //非占用结束时间小于结束时间，则插入数据项
        _otherList = _otherList.concat(_self._computerOtherEvents(startNull, min_maxT.maxT));
      }
      mergeList = mergeList.concat(_otherList);
      return mergeList;
    },


    // 计算空余与过期时间
    _computerOtherEvents: function (start, end) {
      var _self = this;
      var data = [];
      start = moment(start.format(start._f), start._f);
      end = moment(end.format(end._f), end._f);
      var _now = moment(moment().format('YYYY-MM-DD HH:mm:ss'), 'YYYY-MM-DD HH:mm:ss');
      if (_now.isSameOrAfter(start) && _now.isSameOrBefore(end)) { //当前时间在开始结束时间之间
        data.push({
          start: start,
          end: _now,
          events: [],
          statusData: _self._getStatusData('outdate'), //过期颜色
          class: "outdate"
        })
        data.push({
          start: _now,
          end: end,
          events: [],
          statusData: _self._getStatusData('available'), //空闲颜色
          class: "available"
        })
      } else if (_now.isSameOrBefore(start)) { //当前时间在开始时间之前，为空闲
        // var start = _now.isAfter(start) ? _now : start;
        data.push({
          start: start,
          end: end,
          events: [],
          statusData: _self._getStatusData('available'), //空闲颜色
          class: "available"
        })
      } else if (_now.isSameOrAfter(end)) { //当前时间在结束时间之后，为过期
        // var end = _now.isBefore(end) ? _now : end;
        data.push({
          start: start,
          end: end,
          events: [],
          statusData: _self._getStatusData('outdate'), //过期颜色
          class: "outdate"
        })
      }
      return data;
    },

    //计算事件应该占领的长度
    _computedEventplacehoder: function (start, end, min_maxT) {
      var _self = this;
      if (min_maxT.minT.isAfter(start)) {
        start = moment(min_maxT.minT._i, min_maxT.minT._f);
      }
      if (end.isAfter(min_maxT.maxT)) {
        end = moment(min_maxT.maxT._i, min_maxT.maxT._f);
      }
      // 如果开始时间的分钟数小于设置的固定分钟开始值
      if (moment(start).format("HH:mm") < min_maxT.minHM) {
        var min = min_maxT.minHM.split(":");
        start.set({
          'hour': min[0],
          'minute': min[1]
        });
        start = moment(moment(start).format(start._f), start._f);
      }
      // 如果结束时间的分钟数大于设置的固定分钟结束值
      if (moment(end).format("HH:mm") > min_maxT.maxHM || moment(end).format("HH:mm") <= min_maxT.minHM) {
        var max = min_maxT.minHM.split(":");
        end.set({
          'hour': max[0],
          'minute': max[1]
        })
        end = moment(moment(end).format(end._f), start._f);
      }
      // 计算开始时间和结束时间偏差几个小时
      start = moment(start._i, start._f);
      end = moment(end._i, end._f);
      var eventTimeLength = end.diff(start, 'minutes');
      var widthDays = _self._getDiffDays(start, end); //实际天数间隔(跨夜晚)
      eventTimeLength = eventTimeLength - min_maxT.oneDayDiff * widthDays;
      var totalLength = min_maxT.maxT.diff(min_maxT.minT, 'minutes');
      var offsetTotalDay = _self._getDiffDays(min_maxT.minT, min_maxT.maxT); //全部天数间隔
      totalLength = totalLength - min_maxT.oneDayDiff * offsetTotalDay;
      var offsetLength = start.diff(min_maxT.minT, 'minutes');
      var offsetDays = _self._getDiffDays(min_maxT.minT, start); //与开始时天数间隔
      offsetLength = offsetLength - offsetDays * min_maxT.oneDayDiff; //总长度 + 中间不计算的时间
      // 获取每个TD的宽度
      var eventWidth = (eventTimeLength / totalLength) * 100;
      // 获取开始偏移
      var leftWidth = (offsetLength / totalLength) * 100;
      return new Array(eventTimeLength, eventWidth, leftWidth);
    },

    //根据最大最小时间值，调整开始结束时间
    _computedRangTime: function (_time, type) {
      var _self = this;
      if (type == 'start') {
        //如果结束时间大于最大时间和小于最小时间，属于无效过度时间内
        if (moment(_time).format("HH:mm") > _self.options.setDayLength.max || moment(_time).format("HH:mm") < _self.options.setDayLength.min) {
          var currDate = _time.format("YYYY-MM-DD");
          if (moment(_time).format("HH:mm") <= "23:59" && moment(_time).format("HH:mm") >= _self.options.setDayLength.max) { //还在当天,则在第二天开始时间加上该时间段
            currDate = moment(currDate).add(1, 'day').format("YYYY-MM-DD");
            _time = moment(currDate + ' ' + _self.options.setDayLength.min, 'YYYY-MM-DD HH:mm:ss');
          } else {
            _time = moment(currDate + ' ' + _self.options.setDayLength.min, 'YYYY-MM-DD HH:mm:ss');
          }
        }
      } else {
        if (moment(_time).format("HH:mm") >= _self.options.setDayLength.max || moment(_time).format("HH:mm") <= _self.options.setDayLength.min) {
          var currDate = _time.format("YYYY-MM-DD");
          if (moment(_time).format("HH:mm") <= "23:59" && moment(_time).format("HH:mm") > _self.options.setDayLength.max) { //还在当天,则在第二天开始时间加上该时间段
            _time = moment(currDate + ' ' + _self.options.setDayLength.max, 'YYYY-MM-DD HH:mm:ss');
          } else {
            currDate = moment(currDate).subtract(1, 'day').format("YYYY-MM-DD");
            _time = moment(currDate + ' ' + _self.options.setDayLength.max, 'YYYY-MM-DD HH:mm:ss');
          }
        }
      }
      return _time;
    },

    // 实际天数间隔(跨夜晚)
    _getDiffDays: function (start, end) {
      var diffDays = end.diff(start, 'day'); //实际天数间隔(跨夜晚)
      if (start.format("HH:mm:ss") > end.format("HH:mm:ss")) {
        diffDays = diffDays + 1
      }
      return diffDays
    },

    // 获取时间段
    _getDayLength: function (min, max, slotDuration) {
      var _self = this;
      // 计算步长
      var durationArr = [];
      var dataMonth = moment(_self.defaultDate).format('YYYY-MM-DD');
      var minTime = moment(dataMonth + ' ' + min, 'YYYY-MM-DD HH:mm');
      var maxTime = moment(dataMonth + ' ' + max, 'YYYY-MM-DD HH:mm');
      var sDuration = moment.duration(slotDuration);
      while (minTime < maxTime) {
        durationArr.push(minTime.format('HH:mm'));
        minTime.add(sDuration);
      }
      // if (minTime.format('HH:mm') != maxTime.format('HH:mm')) {
      // minTime高度是实际页面元素高度，比如17:30《18:00《18:30，18:30才是最终高度
      // 纠正刻度，方便根据maxTime计算高度和偏移：http://zen.well-soft.com:81/zentao/bug-view-55268.html
      _self.finalMaxTime = minTime.format('HH:mm');
      _self.options.finalMaxTime = _self.finalMaxTime;
      // }
      return durationArr;
    },
    _getEventData: function () {
      return this.eventData;
    }
  };

  // 创建表格视图类
  var TableView = function (options) {
    View.call(this, options);
    this._init();
  };
  TableView.prototype = Object.create(View.prototype, {
    _init: {
      value: function () {
        var dataArr = this._getEventData();
        if ('month' == this.activeView) {
          var timePeriod = this._computedDate(this.defaultDate);
          var data = this._dealMonthData(timePeriod, dataArr);
        } else if ('agendaWeek' == this.activeView) {
          var timePeriod = this._computedWeek(this.defaultDate);
          var data = this._dealWeekData(timePeriod, dataArr);
        } else if ('agendaDay' == this.activeView) {
          var timePeriod = this._getDayLength(
            this.options.setDayLength.min,
            this.options.setDayLength.max,
            this.options.setDayLength.slotDuration
          );
          var data = this._dealDayData(timePeriod, dataArr);
        }
        var viewTable = this._createDom(timePeriod, data);
        if (this.options.onSelectCallback) {
          this.filterKeySearch();
        }
        this._renderAfter();
      }
    },
    _createDom: {
      value: function (timePeriod, data) {
        var _self = this;
        var $tableWrapper = $('<div class="table-wrapper"/>');
        var $viewTable = $('<table>', {
          class: 'view-table table table-bordered'
        });
        var $thead = _self._createThead(timePeriod, _self.activeView);
        $viewTable.append($thead);
        _self.$calendarWidget.invokeDevelopmentMethod('loadResourceDataSuccess', [_self.$calendarWidget.options, data]);
        if (_self.options.occupyStyle == '1') {
          var $tbody = _self._renderTbody(timePeriod, data, _self.activeView);
        } else {
          var $tbody = _self._createTbody(timePeriod, data, _self.activeView);
        }
        $viewTable.append($tbody);
        var calendarWidgetHeight = _self.$calendarWidget.element.find(".fc-view-container").height();
        $tableWrapper.html($viewTable).css("max-height", calendarWidgetHeight + "px");
        this.el.append($tableWrapper);

        // if ($(".table-wrapper", this.el).width() > 0) {
        $(".table-wrapper", this.el).niceScroll({
          oneaxismousemode: false,
          cursorcolor: '#ccc',
          cursorwidth: '8px',
          autohidemode: false,
          zindex: 5
        });
        // }

        if (_self.options.occupyStyle == '2') {
          // 根据资源占用展示来渲染事项
          _self._renderEventData(data, _self.activeView);
        }

      }
    },
    // 创建表格头
    _createThead: {
      value: function (timePeriod, viewType) {
        var _self = this;
        var $thead = $('<thead/>');
        var $tr = $('<tr>');
        // 第一列分组名称列
        var $typeTD = $('<td>', {
          width: '120px',
          class: 'resourse-type'
        });
        $tr.append($typeTD);
        var len = timePeriod.length;
        for (var i = 0; i < len; i++) {
          var $td = $('<td/>');
          var $theadbox = $('<div>');
          if ('month' == viewType) {
            // 月视图
            var className = _self._isToday(timePeriod[i]);
            $td.addClass(className + ' month');
            $theadbox.addClass('month-date-num');
            $theadbox.html(moment(timePeriod[i]).format('MM/DD'));
          } else if ('agendaWeek' == viewType) {
            // 周
            var className = _self._isToday(timePeriod[i]);
            $td.addClass(className + ' week').attr("data-date", "");
            $theadbox.addClass('week-date-num');
            $theadbox.html(this.weekLang[i] + moment(timePeriod[i]).format('MM-DD'));
          } else if ('agendaDay' == viewType) {
            // 日
            $td.addClass('day');
            $theadbox.addClass('day-date-num');
            //$theadbox.html(timePeriod[i].substring(0, timePeriod[i].indexOf(':')));
            $theadbox.html(timePeriod[i]);
          }
          $td.html($theadbox);
          $tr.append($td);
        }
        return $thead.append($tr);
      }
    },
    // 创建表格内容
    _createTbody: {
      value: function (timePeriod, data, viewType) {
        var _self = this;
        var $tbody = $('<tbody/>');
        var nmoment = moment();
        var nmoment2 = nmoment.format('YYYY-MM-DD');
        var momentCache = _self.momentCache || {};
        var blankTitle = _self.options.titleJson && _self.options.titleJson.blankTitle;
        $.each(data, function (key, item) {
          var valueArray = item.dateList;
          var gg = valueArray[0] || {};
          var $tr = $('<tr>');
          // 资源TD
          var $resTD = $('<td>');
          var resText = gg.customGroupName || gg.groupName || _self._getGroupNameFromMap(key);
          $tr.attr({
            'data-id': key,
            'data-name': resText,
            'data-resourse': JSON.stringify(gg.res)
          });
          var $resDiv = $('<div class="resource-name"/>').html(resText).attr('title', resText);
          $resTD.html($resDiv);
          $tr.append($resTD);

          // 按天构建表格
          $.each(timePeriod, function (i, tp) {
            var valueObj = 'agendaDay' == viewType ? valueArray[0] : valueArray[i];
            var $td = $('<td>');
            var $countDiv = $('<div>');
            if ('month' == viewType) {
              var tpMoment = moment(tp);
              var className = _self._isToday(valueObj.date);
              $td.addClass(className);
              $td.attr('data-date', tpMoment.format('YYYY-MM-DD'));
            } else if ('agendaWeek' == viewType) {
              var tpMoment = moment(tp);
              var className = _self._isToday(valueObj.date);
              $td.addClass(className);
              $td.attr('data-date', tpMoment.format('YYYY-MM-DD'));
            } else if ('agendaDay' == viewType) {
              var dataMonth = moment(_self.defaultDate).format('YYYY-MM-DD');
              var tdDate = moment(dataMonth + ' ' + tp, 'YYYY-MM-DD HH:mm');
              $td.attr('data-date', tdDate.format('YYYY-MM-DD HH:mm'));
            }

            if (blankTitle) {
              $td.attr('title', blankTitle);
            }

            $td.html($countDiv);


            if (_self.options.onSelectCallback) {
              _self._selectTd($td, $tbody);
            } else {
              $td.click(function (e) {
                //有已排数据
                if ($td.find('.js-count').length > 0) {
                  if (_self.options.eventClickCallback) {
                    var resEvents = $countDiv.data('res-events') || valueObj.events;
                    _self.options.eventClickCallback.call(_self, resEvents);
                  }
                  return false;
                } else {
                  //没有数据触发
                  if (_self.options.clickCallback) {
                    if ('agendaDay' == viewType) {
                      var dataMonth = moment(_self.defaultDate).format('YYYY-MM-DD');
                      var tdDate = moment(dataMonth + ' ' + timePeriod[i], 'YYYY-MM-DD HH:mm');
                    } else {
                      var currTime = moment().format('HH:mm');
                      var tdDate = moment(timePeriod[i] + ' ' + currTime, 'YYYY-MM-DD HH:mm');
                    }
                    _self.options.clickCallback.call(_self, tdDate, e, viewType);
                  }
                }
              });
            }

            $tr.append($td);
          });

          $tbody.append($tr);

          if (_self.options.details && _self.options.details.length) {
            var content = _self.setEventTitleDetail(gg);
            _self.$calendarWidget.invokeDevelopmentMethod('beforeResourceGroupPopoverRender', [$resDiv, content, _self.$calendarWidget.options, _self.$calendarWidget.getConfiguration()]);
            _self.setPopover($resDiv, content);
          }
        });

        return $tbody;
      }
    },
    // 创建表格内容,按最小刻度显示
    _renderTbody: {
      value: function (timePeriod, data, viewType) {
        var _self = this;
        var $tbody = $('<tbody/>');
        var nmoment = moment();
        var nmoment2 = nmoment.format('YYYY-MM-DD');
        var momentCache = _self.momentCache || {};
        var blankTitle = _self.options.titleJson && _self.options.titleJson.blankTitle;
        $.each(data, function (key, valueArray) {
          var gg = valueArray.dateList[0] || {};
          var $tr = $('<tr>');
          // 资源TD
          var $resTD = $('<td>');
          var resText = gg.customGroupName || gg.groupName || _self._getGroupNameFromMap(key);
          $tr.attr({
            'data-id': key,
            'data-name': resText,
            'data-resourse': JSON.stringify(gg.res)
          });
          var $resDiv = $('<div class="resource-name"/>').html(resText).attr('title', resText);
          $resTD.html($resDiv);
          $tr.append($resTD);
          // 按天构建数据
          $.each(timePeriod, function (i, tp) {
            var valueObj = 'agendaDay' == viewType ? valueArray.dateList[0] : valueArray.dateList[i];
            var $td = $('<td>');
            var $countDiv = $('<div>');
            var ees = [];
            if ('month' == viewType) {
              var tpMoment = moment(tp);
              var className = _self._isToday(valueObj.date);
              $td.addClass(className + ' month');
              ees = valueObj.events;
              if (valueObj.events.length > 0) {
                $countDiv.data('res-events', valueObj.events);
                $countDiv.addClass('month-count js-count');
                var title = valueObj.title || '已排';
                $countDiv.html('<span>' + title + '：</span>' + valueObj.events.length);
                $td.css({
                  'background': _self._getStatusData('occupied', valueObj.events[0]).bgcolor
                });
              } else {
                if (tpMoment.isSameOrAfter(nmoment) || nmoment2 === tp) {
                  $td.css({
                    'background': _self._getStatusData('available').bgcolor
                  });
                } else {
                  $td.css({
                    'background': _self._getStatusData('outdate').bgcolor
                  });
                }
              }
              $td.attr('data-date', tpMoment.format('YYYY-MM-DD'));
            } else if ('agendaWeek' == viewType) {
              var className = _self._isToday(valueObj.date);
              $td.addClass(className + ' week');
              var tpMoment = moment(tp);
              ees = valueObj.events;
              if (valueObj.events.length > 0) {
                $countDiv.data('res-events', valueObj.events);
                $countDiv.addClass('week-count js-count');
                var title = valueObj.title || '已排';
                $countDiv.html('<span>' + title + '：</span>' + valueObj.events.length);
                $td.css({
                  'background': _self._getStatusData('occupied', valueObj.events[0]).bgcolor
                });
              } else {
                if (tpMoment.isSameOrAfter(nmoment) || nmoment2 === tp) {
                  $td.css({
                    'background': _self._getStatusData('available').bgcolor
                  });
                } else {
                  $td.css({
                    'background': _self._getStatusData('outdate').bgcolor
                  });
                }
              }
              $td.attr('data-date', tpMoment.format('YYYY-MM-DD'));
            } else if ('agendaDay' == viewType) {
              $td.addClass('day');
              var dataMonth = moment(_self.defaultDate).format('YYYY-MM-DD');
              var tdDate = moment(dataMonth + ' ' + tp, 'YYYY-MM-DD HH:mm');
              $td.attr('data-date', tdDate.format('YYYY-MM-DD HH:mm'));
              ees = valueObj.events.filter(function (item) {
                if (item.allDay || null == item.end) {
                  return true;
                }
                var startTime = tdDate;
                var tp2 = dataMonth + ' ' + (i < timePeriod.length - 1 ? timePeriod[i + 1] : '23:59');
                var eventStart = momentCache[item.startTime] || (momentCache[item.startTime] = moment(item.startTime, 'YYYY-MM-DD HH:mm'));
                var eventEnd = momentCache[item.endTime] || (momentCache[item.endTime] = moment(item.endTime, 'YYYY-MM-DD HH:mm'));
                var endTime = momentCache[tp2] || (momentCache[tp2] = moment(tp2, 'YYYY-MM-DD HH:mm'));
                if (eventStart.isSameOrAfter(startTime) && eventStart.isBefore(endTime)) {
                  return true;
                }
                if (eventEnd.isAfter(startTime) && eventEnd.isBefore(endTime)) {
                  return true;
                }
                if (startTime.isSameOrAfter(eventStart) && endTime.isSameOrBefore(eventEnd)) {
                  return true;
                }
                return false;
              });
              if (_self.options.occupyStyle === '1' && ees.length > 0) {
                $countDiv.data('res-events', ees);
                $countDiv.addClass('day-count js-count');
                var title = valueObj.title || '已排';
                $countDiv.html('<span>' + title + '：</span>' + ees.length);
                $td.css({
                  'background': _self._getStatusData('occupied', ees[0]).bgcolor
                });
              } else if (tdDate.isSameOrAfter(nmoment)) {
                $td.css({
                  'background': _self._getStatusData('available').bgcolor
                });
              } else {
                $td.css({
                  'background': _self._getStatusData('outdate').bgcolor
                });
              }
            }
            if (ees.length == 0 && blankTitle) {
              $td.attr('title', blankTitle);
            }

            $td.html($countDiv);

            if (_self.options.onSelectCallback) {
              _self._selectTd($td, $tbody);
            } else {
              $td.click(function (e) {
                //有已排数据
                if ($td.find('.js-count').length > 0) {
                  if (_self.options.eventClickCallback) {
                    var resEvents = $countDiv.data('res-events') || ees;
                    _self.options.eventClickCallback.call(_self, resEvents);
                  }
                  return false;
                } else {
                  //没有数据触发
                  if (_self.options.clickCallback) {
                    if ('agendaDay' == viewType) {
                      var dataMonth = moment(_self.defaultDate).format('YYYY-MM-DD');
                      var tdDate = moment(dataMonth + ' ' + timePeriod[i], 'YYYY-MM-DD HH:mm');
                    } else {
                      var currTime = moment().format('HH:mm');
                      var tdDate = moment(timePeriod[i] + ' ' + currTime, 'YYYY-MM-DD HH:mm');
                    }
                    _self.options.clickCallback.call(_self, tdDate, e, viewType);
                  }
                }
              });

              if ($td.find('.js-count').length > 0) {
                var eventObj = {
                  events: ees
                };
                var content = _self.setEventDetail(eventObj);
                _self.$calendarWidget.invokeDevelopmentMethod('beforeResourceEventsPopoverRender', [$td, content, eventObj, _self.$calendarWidget.options, _self.$calendarWidget.getConfiguration()]);
                _self.setPopover($td, content, eventObj);
              }
            }
            $tr.append($td);
          });

          $tbody.append($tr);
          if (_self.options.details && _self.options.details.length) {
            var content = _self.setEventTitleDetail(gg);
            _self.$calendarWidget.invokeDevelopmentMethod('beforeResourceGroupPopoverRender', [$resDiv, content, _self.$calendarWidget.options, _self.$calendarWidget.getConfiguration()]);
            _self.setPopover($resDiv, content);
          }
        });

        return $tbody;
      }
    },
    //渲染视图中的事项，按实际时间显示
    _renderEventBox: {
      value: function (initData, data) {
        var _self = this;
        var $eventBox = $('<div class="event-box" style="display:none;"/>');
        var $talbe = $('.resource-view .view-table');
        var colspanLength = $('.resource-view .view-table>thead td').length - 1;
        var $trTemplate = $("<tr class='resource-events'><td></td><td class='resource-event' colspan='" + colspanLength + "'></td></tr>");
        var min_maxT = _self._dealDateRange();
        $.each(data, function (groupId, mergeEventList) {
          var gg = initData[groupId].dateList[0] || {};
          var $tr = $trTemplate.clone();
          $talbe.find('tr[data-id="' + groupId + '"]').before($tr);
          var $resDiv = $tr.find('.resource-event');
          $.each(mergeEventList, function (i, eventObj) {
            //判断如果时间段不在设置的最大最小时间段内，则不展示
            if (eventObj.start._i == eventObj.end._i || eventObj.start.isAfter(min_maxT.maxT) || eventObj.end.isBefore(min_maxT.minT)) {
              //结束，进行下一个
              return;
            }
            var $acrossDayEvent = $('<div>', {
              title: (eventObj.title || _self.options.title || '') + '详情',
              class: 'across-day-event',
              'data-start': eventObj.start.format('YYYY-MM-DD HH:mm'),
              'data-end': eventObj.end.format('YYYY-MM-DD HH:mm'),
              'data-resourse': JSON.stringify(eventObj)
            });
            // 如果背景色为白色，不要默认为透明
            if (eventObj.statusData.bgcolor == "#ffffff") {
              $acrossDayEvent.removeClass('across-day-event');
            }
            var $eventTitle = $("<div class='event-title'>").append();
            var $eventText = $('<span>').html(eventObj.title);
            var $eventCount = $('<span>').html(eventObj.events.length ? eventObj.title ? eventObj.title + '：' + eventObj.events.length : '已排：' + eventObj.events.length : '');
            $eventTitle.append($eventText).append($eventCount);
            $acrossDayEvent.append($eventTitle);
            $acrossDayEvent.css({
              width: eventObj.width,
              left: eventObj.left,
              background: eventObj.statusData.bgcolor
            });
            $resDiv.append($acrossDayEvent);
            if (eventObj.events.length) {
              //定义事项的点击事件
              $acrossDayEvent.click(function () {
                if (_self.options.eventClickCallback) {
                  _self.options.eventClickCallback.call(_self, eventObj.events);
                }
                return false;
              });

              var content = _self.setEventDetail(eventObj);
              _self.$calendarWidget.invokeDevelopmentMethod('beforeResourceEventsPopoverRender', [$acrossDayEvent, content, eventObj, _self.$calendarWidget.options, _self.$calendarWidget.getConfiguration()]);
              _self.setPopover($acrossDayEvent, content, eventObj);
            }
          });

          //定义点击其他地方弹出新建日程弹出框
          $resDiv.click(function (e) {
            if (_self.options.clickCallback) {
              var currDate = moment(_self.defaultDate).format('YYYY-MM-DD');
              var currTime = moment().format('HH:mm');
              _self.options.clickCallback.call(_self, moment(currDate + ' ' + currTime), e, this.activeView);
            }
          });
        });
        return $eventBox;
      }
    },
    // 选择面板选择操作
    _selectTd: {
      value: function ($td, $tbody) {
        var _self = this;
        var viewType = _self.options.activeView;
        $td.css({
          'position': 'relative',
          'z-index': '1'
        });
        $td.on('mousedown', function (event) {
            _self.startElement = this;
            $tbody.find('td.selected').removeClass('selected');
            _self.options.onSelectCallback.call(_self, null);

            var $this = $(this);
            var $row = $this.closest('tr');
            var location = $row.find('.resource-name').text();
            var startTime = $row.children('.selected:first').attr('data-date');
            var endTime = $row.children('.selected:last').attr('data-date');

            $('body > .popover').remove();

            $this.popover({
              html: true,
              container: 'body',
              toggle: 'manual',
              selector: '.selected',
              title: location,
              placement: 'top',
              template: '<div class="popover" ><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
              content: '时间：<span id="startTime">xxx:xxxx</span> - <span id="endTime">xxx:xxxx</span>'
            });

            $this.popover('show');
          })
          .on('mousemove', function (event) {
            var $this = $(this);
            if (_self.startElement) {
              var resourceId = $this.closest('tr[data-id]').data('id');
              var resourceId2 = $(_self.startElement).closest('tr[data-id]').data('id');
              if (resourceId === resourceId2) {
                _self.bSelect = true;
                $this.addClass('selected');
              }
              var $row = $this.closest('tr');
              var startTime = $row.children('.selected:first').attr('data-date');
              var endTime = $row.children('.selected:last').attr('data-date');
              var isDayViewType = viewType === 'agendaDay';

              $('#startTime').text(isDayViewType ? moment(startTime).format('HH:mm') : moment(startTime).format('YYYY-MM-DD'));
              $('#endTime').text(isDayViewType ? moment(endTime).format('HH:mm') : moment(endTime).format('YYYY-MM-DD'));

              return;
            } else {
              _self.bSelect = null;
              _self.startElement = null;
            }
          })
          .on('mouseup', function (event) {
            if (true === _self.bSelect && _self.startElement) {
              event.preventDefault();
              event.stopPropagation();
              var $this = $(this);
              var resourceId = $this.closest('tr[data-id]').data('id');
              var resourceId2 = $(_self.startElement).closest('tr[data-id]').data('id');
              if (resourceId === resourceId2) {
                var resourceData = $this.closest('tr[data-name]').data('resourse');
                var resourceName = $this.closest('tr[data-name]').data('name');
                var end = $this.data('date');
                var start = $(_self.startElement).data('date');
                if (start <= end) {
                  if ('agendaDay' == viewType && _self.options.setDayLength.slotDuration) {
                    var slotDuration = moment.duration(_self.options.setDayLength.slotDuration);
                    var endMoment = moment(end, 'YYYY-MM-DD HH:mm').add(slotDuration);
                    end = endMoment.format('YYYY-MM-DD HH:mm');
                  } else {
                    var endMoment = moment(end, 'YYYY-MM-DD HH:mm').set('hour', 23).set('minute', 59);
                    end = endMoment.format('YYYY-MM-DD HH:mm');
                  }
                  start = moment(start).format('YYYY-MM-DD HH:mm');
                } else {
                  if ('agendaDay' == viewType && _self.options.setDayLength.slotDuration) {
                    var slotDuration = moment.duration(_self.options.setDayLength.slotDuration);
                    var endMoment = moment(end, 'YYYY-MM-DD HH:mm');
                    end = moment(start).add(slotDuration).format('YYYY-MM-DD HH:mm');
                    start = endMoment.format('YYYY-MM-DD HH:mm');
                  } else {
                    var endMoment = moment(end, 'YYYY-MM-DD HH:mm');
                    end = moment(start).format('YYYY-MM-DD HH:mm').set('hour', 23).set('minute', 59);
                    start = endMoment.format('YYYY-MM-DD HH:mm');
                  }
                }

                var retObj = {
                  end: end,
                  start: start,
                  viewType: viewType,
                  resourceId: resourceId,
                  resourceName: resourceName,
                  resourceData: resourceData
                };
                _self.$calendarWidget.invokeDevelopmentMethod('onSelectCallback', [retObj, _self.$calendarWidget.options, _self.$calendarWidget.getConfiguration()]);
                if (!retObj.toEnd) { // toEnd为false时，使用组件自带方法
                  _self.options.onSelectCallback.call(_self, retObj);
                }
              } else {
                $tbody.find('td.selected').removeClass('selected');
              }

              $this.closest('tr').popover('destroy');
            }
            _self.bSelect = null;
            _self.startElement = null;
            $('body > .popover').remove();
          }).on("mouseleave", function () {
            var $this = $(this);
            var $prev = $(this).prev();
            var $next = $(this).next();
            setTimeout(function () {
              if (_self.startElement) {
                if ($this.data('date') != $(_self.startElement).data('date') && $this.hasClass("selected")) {
                  if ($prev.hasClass("selected") && !$next.hasClass("selected")) {
                    $this.removeClass('selected');
                  } else if (!$prev.hasClass("selected") && $next.hasClass("selected")) {
                    $this.removeClass('selected');
                  }
                }
              }
            }, 100)
          })
      },
    },
    setPopover: {
      value: function ($el, content, eventObj) {
        var _self = this;
        var mouseLeft = 0;
        $el.popover({
          html: true,
          placement: 'auto right',
          container: 'body',
          trigger: 'manual',
          template: '<div class="popover res-detail" role="tooltip" style="z-index: 99999;"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
          content: function () {
            return (
              content
            );
          }
        }).on("mouseenter", function (event) {
          var _this = this;
          mouseLeft = event.offsetX;
          $(_this).popover("show"); // 滚动条
        }).on("mouseleave", function () {
          var _this = this;
          setTimeout(function () {
            if (!$(".res-detail:hover").length) {
              $(_this).popover("hide")
            } else {
              $(".res-detail").on("mouseleave", function () {
                $(_this).popover("hide")
              });
            }
          }, 100);
        });
        $el.on('shown.bs.popover', function () {
          var _this = this;
          var $popo = $('.res-detail', 'body');
          var top = $(this).offset().top - 20;
          $popo.css('top', top + 'px');
          var right = $(window).width() - $(this)[0].getBoundingClientRect().right; //距离屏幕右侧距离
          var left = $(this)[0].getBoundingClientRect().left; //距离屏幕左侧距离
          if (right < 360 && left < 360) {
            $popo.addClass("right").removeClass("left");
            $popo.css("left", left + mouseLeft + 'px')
          }
          $popo.css("visibility", "visible");
          $('.popover-content', '.res-detail', 'body').niceScroll({
            oneaxismousemode: false,
            cursorcolor: '#ccc',
            cursorwidth: '8px'
          });
          $popo.off().on("mouseleave", function () {
            $(_this).popover("hide")
          });
          if (eventObj) {
            $popo.on("click", ".more", function () {
              var uuid = $(this).parents(".res-detail-content").data('uuid');
              var event = _.find(eventObj.events, {
                "uuid": uuid
              });
              var title = eventObj.title || _self.options.title;
              var _options = {
                "event": event,
                "title": title
              }
              _self.$calendarWidget.invokeDevelopmentMethod('beforeEventListDialogRender', [_options, _self.$calendarWidget.options, _self.$calendarWidget.getConfiguration()]);
              _self.$calendarWidget._viewEvent(_options);
            })
          }
          _self.$calendarWidget.invokeDevelopmentMethod('afterEventListDialogRender', [eventObj, _self.$calendarWidget.options, _self.$calendarWidget.getConfiguration()]);
        })
        $el.on('hide.bs.popover', function () {
          var $popo = $('.res-detail', 'body');
          $popo.css("visibility", "hidden");
        }).on('show.bs.popover', function () {
          var $popo = $('.res-detail', 'body');
          if ($popo.length) {
            $popo.each(function () {
              $(this).remove();
            })
          }
        })
      },
    },
    // 资源名称详情
    setEventTitleDetail: {
      value: function (gg) {
        var _self = this;
        var res = gg.res,
          html = [];
        $.each(_self.options.details, function (idx, item) {
          var value = res[item.text];
          html.push("<div class='res-detail-item'>");
          if (item.icon && item.icon.className) {
            html.push("<i style='color:" + (item.icon.iconColor || '') + ";' class='" + item.icon.className + "'></i>");
          }
          html.push("<span class='res-detail-lable'>");
          html.push(item.title || item.text);
          html.push('</span>');
          html.push("<span class='res-detail-value'>");
          html.push(value || '');
          html.push('</span>');
          html.push('</div>');
        });
        return html.join('');
      }
    },
    // 资源事项详情
    setEventDetail: {
      value: function (data) {
        var _self = this;
        var columns = _.groupBy(_self.options.$calendarWidget.getConfiguration().columns, {
          resourceDetailHidden: "0"
        });
        columns = columns.true;
        var html = [];
        $.each(data.events, function (idx, item) {
          html.push("<div class='res-detail-content' data-uuid='" + item.uuid + "'>")
          if (columns && columns.length) {
            $.each(columns, function (cidx, citem) {
              var value = item[citem.name];
              html.push("<div class='res-detail-item'>");
              if (citem.icon && citem.icon.className) {
                html.push("<i style='color:" + (citem.icon.iconColor || '') + ";' class='" + citem.icon.className + "'></i>");
              }
              html.push("<div class='res-detail-lable'>");
              html.push(citem.title || citem.text);
              html.push('</div>');
              html.push("<div class='res-detail-value'>");
              html.push(value || '');
              html.push('</div>');
              html.push('</div>');
            });
          } else {
            html.push("<div class='res-detail-item'>无设置显示字段</div>");
          }
          html.push('<div style="text-align:right;" class="mt10"><div class="well-btn w-btn-primary w-line-btn btn-sm more">更多详情</div></div>')
          html.push('</div>');
        })
        return html.join('');
      }
    },
    filterKeySearch: {
      value: function () {
        var _self = this;
        var $element = _self.$calendarWidget.element.find(".fc-right");
        if (!$element.find(".search-wrapper")[0]) {
          var queryHint = '请输入资源关键字';
          var sb = new StringBuilder();
          sb.appendFormat(
            '<div class="search-wrapper">' +
            '<div class="input-group panel-heading-query animated">' +
            '<input type="search" class="form-control" placeholder="{0}">' +
            '<div class="search-clear-icon">' +
            '<i class="iconfont icon-ptkj-dacha-xiao"></i>' +
            '</div>' +
            '</div>' +
            '<div class="panel-search-icon">' +
            '<i class="iconfont icon-ptkj-sousuochaxun"></i>' +
            '</div>' +
            '</div>',
            queryHint
          );
          $element.prepend(sb.toString());

          // IE8的placeholder效果处理
          if ($.fn && $.fn.placeholder && Browser.isIE8OrLower()) {
            $('input', $panelHeader).placeholder();
            $('input', $tabHeader).placeholder();
          }

          /** 搜索框 */
          var searchWrap = '.search-wrapper:first';

          /** 包含删除按钮的搜索输入框 */
          var queryInputWrapSelector = '.panel-heading-query:first';

          /** 搜索输入框的input元素 */
          var queryInputSelector = '.panel-heading-query > input[type="search"]:first';

          /** 搜索按钮，鼠标进入搜索按钮时，显示搜索输入框 */
          var queryIconSelector = '.search-wrapper .panel-search-icon:first';

          function setQueryInputVisibility(visible) {
            var $searchWrap = $(searchWrap, $element);
            var $queryInputWrapper = $(queryInputWrapSelector, $element);
            var $queryInput = $(queryInputSelector, $element);

            // 当前搜索框是否显示
            var isCurrentVisible = $queryInputWrapper.hasClass('input-visible');

            // 当前搜索框输入值
            var queryValue = $queryInput.val();

            // 当前搜索框是否处于focus状态
            var isInputFocus = $searchWrap.hasClass('search-wrap-focus');

            if ((isCurrentVisible && visible) || (!isCurrentVisible && !visible)) {
              return;
            }

            if (!isCurrentVisible && visible) {
              $queryInputWrapper.show().removeClass('fadeOutRight').addClass('fadeInRight input-visible');
              return;
            }

            if (isCurrentVisible && !visible && !queryValue && !isInputFocus) {
              // input为focus状态或者有输入搜素字符时不关闭
              $queryInputWrapper.removeClass('fadeInRight input-visible').addClass('fadeOutRight');
            }
          }

          var queryCallback = function () {
            var queryValue = $(queryInputSelector, $element).val();
            if (queryValue.trim()) {
              _self.el.find("tbody").find("tr").each(function (index, item) {
                if ($(item).data('name').indexOf(queryValue) == -1) {
                  $(item).hide();
                  if ($(item).prev().hasClass(".resource-events")) {
                    $(item).prev().hide();
                  }
                } else {
                  $(item).show();
                  if ($(item).prev().hasClass(".resource-events")) {
                    $(item).prev().show();
                  }
                }
              })
            } else {
              _self.el.find("tbody").find("tr").each(function (index, item) {
                $(item).show();
                if ($(item).prev().hasClass(".resource-events")) {
                  $(item).prev().show();
                }
              })
            }
            _self.calendarShowFun();
          };
          $element
            .on('mouseenter', queryIconSelector, function () {
              setQueryInputVisibility(true);
            })
            .on('mouseleave', queryIconSelector, function (e) {
              var $target = $(e.relatedTarget);
              if ($target.closest('.panel-heading-query').length || $target.hasClass('panel-heading-query')) {
                return;
              }

              setQueryInputVisibility(false);
            })
            .on('click', queryIconSelector, function () {
              if ($element.find(queryInputWrapSelector).hasClass('fadeInRight')) {
                queryCallback();
              }
            });
          $element
            .on('keypress', queryInputSelector, function (event) {
              if (event.keyCode === 13) {
                queryCallback();
              }
            })
            .on('focus', queryInputSelector, function (event) {
              $element.find(queryInputWrapSelector).addClass('search-focus');
              $element.find(searchWrap).addClass('search-wrap-focus');
            })
            .on('blur', queryInputSelector, function (event) {
              $element.find(queryInputWrapSelector).removeClass('search-focus');
              $element.find(searchWrap).removeClass('search-wrap-focus');
            })
            .on('mouseenter', queryInputWrapSelector, function () {
              if (!$(this).hasClass('hover')) {
                $(this).addClass('hover');
                $('input:focus').blur();
              }
            })
            .on('mouseleave', queryInputWrapSelector, function (event) {
              setQueryInputVisibility(false);
            });

          $element.on('input propertychange', queryInputSelector, function () {
            var $this = $(this);
            var _val = $.trim($this.val());
            $('.panel-tab.active', _self.element).attr('searchValue', _val);
            if (_val) {
              $element.find('.search-clear-icon').show();
            } else {
              $element.find('.search-clear-icon').hide();
            }
          });

          $element.on('click', '.search-clear-icon', function (e) {
            $element.find(queryInputSelector).val('').focus();
            $(this).hide();
            queryCallback();
          });

          $(document).on('click', function (e) {
            if (!$(e.target).closest('.search-wrapper').length) {
              $element.find(searchWrap).removeClass('search-wrap-focus');
              $element.find(queryInputSelector).removeClass('search-focus');
              setQueryInputVisibility(false);
            }
          });
        }
      }

    },
    // 有无资源时，处理显示问题
    calendarShowFun: {
      value: function () {
        var _self = this;
        if (_self.$calendarWidget.selectCalendarView === "resource") {
          var isShow = false;
          var $tbody = _self.el.find("tbody");
          var $parent = _self.el.parent();
          $parent.find(".no-records-found").remove();
          var $noData = '<div class="no-records-found"><div colspan="4" title="没有找到匹配的记录"><div class="well-table-no-data"></div><div class="well-table-no-data-text">没有找到匹配的记录</div></div></div>'
          $tbody.find("tr").each(function (index, item) {
            isShow = isShow || $(item)[0].style.display !== "none";
          })
          if (isShow) {
            _self.el.show();
          } else {
            _self.el.hide();
            $parent.append($noData);
          }
        }
      }
    },
    _renderAfter: {
      value: function () {
        var _self = this;
        _self.calendarShowFun();
      }
    }
  });
  TableView.prototype.constructor = TableView;

  // 创建Resourse类
  var ResourseView = function (options) {
    this.options = $.extend(true, {}, defaultConfig, options);
    this._initView();
  };

  ResourseView.prototype = {
    _initView: function () {
      var _self = this;
      new TableView(this.options);

      _self._windowResize();
      $(window).resize(_self.windowResize);
    },

    _windowResize: function () {
      var _self = this;
      _self.windowResize = _.debounce(function () {
        _self.options.el.css("width", "100%");
        $(".table-wrapper", _self.options.el).niceScroll().resize();
        _self.options.$calendarWidget.addCurrentTimeLine && _self.options.$calendarWidget.addCurrentTimeLine();
      }, 100);
    },

    _setDayHour: function (obj) {
      // $.extend(this.options, obj);
      $.extend(this.options.setDayLength, obj.setDayLength);
      this.options.el.html('');
      new TableView(this.options);
    },

    _getDayHour: function () {
      return this.options.setDayLength;
    },

    _getFinalMaxTime: function () {
      return this.options.finalMaxTime;
    },


    refresh: function (obj) {
      $.extend(this.options, obj);
      this.options.el.html('');
      new TableView(this.options);
    },

    changeView: function (obj) {
      $.extend(this.options, obj);
      this.options.el.html('');
      new TableView(this.options);
    }
  };

  $.fn.view = function (options, key) {
    var _this = $(this);
    var data = _this.data('resource_view');
    if (!data && typeof options == 'function') {
      //初始化options是个json对象
      var el = this;
      var options = $.extend({}, {
          el: el
        },
        options
      );
      if (options.getEventData) {
        //#62688 全部用车中，切换到资源视图，面板占用位置显示错误；占用车辆信息与左边的车辆信息不一致；
        options.eventData = options.getEventData();
      }
      data = new ResourseView(options);
      _this.data('resource_view', data);
      return data;
    }

    if (data && options == 'setDayHour') {
      if (typeof key == 'object') {
        var obj = {
          setDayLength: key
        };
        return ResourseView.prototype._setDayHour.call(_this.data('resource_view'), obj);
      } else {
        alert("参数格式不正确!{'setDayHour', key, value}");
      }
    }
    if (data && options == 'finalMaxTime') {
      return ResourseView.prototype._getFinalMaxTime.call(_this.data('resource_view'))
    }
    if (data && options == 'getDayHour') {
      return ResourseView.prototype._getDayHour.call(_this.data('resource_view'))
    }
    if (data && options == 'refresh') {
      if (typeof key == 'object') {
        //				data.options.eventData = key;
        var obj = {
          eventData: key
        };
        return ResourseView.prototype.refresh.call(_this.data('resource_view'), obj);
      } else {
        alert("参数格式不正确!{'option', key}");
      }
    }
    if (data && options == 'changeView') {
      //
      if (typeof key == 'object') {
        // agendaDay, agendaWeek, month
        return ResourseView.prototype.changeView.call(_this.data('resource_view'), key);
      } else {
        alert("参数格式不正确!{'option', key}");
      }
    }
    if (data && options && ResourseView.prototype[options]) {
      return ResourseView.prototype[options].call(_this.data('resource_view'), key);
    }
  };
});
