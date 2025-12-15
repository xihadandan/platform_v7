(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    define(['jquery', 'layDate'], factory);
  } else {
    factory(jQuery);
  }
})(function ($, laydate) {
  var columnProperty = {
    // 控件字段属性
    applyTo: null, // 应用于
    columnName: null, // 字段定义 fieldname
    displayName: null, // 描述名称 descname
    dbDataType: '', // 字段类型 datatype type
    indexed: null, // 是否索引
    showed: null, // 是否界面表格显示
    sorted: null, // 是否排序
    sysType: null, // 系统定义类型，包括三种（0：系统默认，1：管理员常量定义，2：表单添加后自定义）
    length: null, // 长度
    showType: '1', // 显示类型 1,2,3,4 datashow
    defaultValue: null, // 默认值
    valueCreateMethod: '1', // 默认值创建方式 1用户输入
    onlyreadUrl: null, // 只读状态下设置跳转的url
    dpStyle: null, // 控件风格
    minDate: null, //最小值
    maxDate: null //最大值
  };

  // 控件公共属性
  var commonProperty = {
    inputMode: null, // 输入样式 控件类型 inputDataType
    fieldCheckRules: null,
    fontSize: null, // 字段的大小
    fontColor: null, // 字段的颜色
    ctlWidth: null, // 宽度
    ctlHight: null, // 高度
    textAlign: null // 对齐方式
  };

  /*
   * DATEPICKER CLASS DEFINITION ======================
   */
  var DatePicker = function ($placeHolder, options) {
    this.options = $.extend({}, $.fn['wdatePicker'].defaults, options);
    this.value = '';
    this.$editableElem = null;
    this.$labelElem = null;
    this.$placeHolder = $placeHolder;
  };

  DatePicker.prototype = {
    constructor: DatePicker
  };

  $.DatePicker = {
    createEditableElem: function () {
      if (this.$editableElem != null) {
        // 创建可编辑框
        return;
      }
      var options = this.options;
      var ctlName = this.getCtlName();
      var editableElem = document.createElement('input');
      editableElem.setAttribute('class', this.editableClass);
      editableElem.setAttribute('name', ctlName);
      editableElem.setAttribute('type', 'text');
      editableElem.setAttribute('autocomplete', 'off');
      var _css = this.getTextInputCss();
      _css.color = '#666';
      $(editableElem).css(_css);

      this.$placeHolder.after($(editableElem));
      this.$editableElem = this.$placeHolder.next('.' + this.editableClass);
      /*
       * var _this = this; this.$editableElem.change(function(){ alert(1);
       * _this.value = _this.$editableElem.val(); });
       */

      var format = '';
      var fmt = options.contentFormat;

      format = this.getFormat(fmt);

      if (this.options.showIcon) {
        this.$editableElem.addClass('Wdate');
      }
      var _this_ = this.$editableElem;
      var _this = this;
      var _option = this.options;
      _this.initDatePicker({
        minDate: options.columnProperty.minDate,
        maxDate: options.columnProperty.maxDate
      });
      _this_.bind('paste', function () {
        window.setTimeout(function () {
          _this.setValue(_this_.val(), false);
        }, 100);
      });
      _this_.keyup(function () {
        if (!_this.isInSubform()) {
          _this.setValue(_this_.val(), false);
        }
      });
      _this_.keydown(function () {
        if (_this.isInSubform()) {
          // 从表不允许手动输入
          return false;
        } else {
          return true;
        }
      });

      /*
       * _this_.bind('change',function(){ _this.setValue( _this_.val());
       * });
       */
    },
    initDatePicker: function (param) {
      var options = $.extend(this.options, param);
      var format = '';
      var fmt = options.contentFormat;
      var nowDate = new Date();
      var customFmt = options.customFmt;
      format = this.getFormat(fmt);
      var newDate = DyformFacade.getDateStrByDateAndFormat(nowDate, format);
      var _this_ = this.$editableElem;
      var _this = this;
      var _option = this.options;
      if (param.minDate == '') {
        param.minDate = undefined;
      } else if (param.minDate == 'sysdate') {
        param.minDate = newDate;
      }
      if (param.maxDate == '') {
        param.maxDate = undefined;
      } else if (param.maxDate == 'sysdate') {
        param.maxDate = newDate;
      }
      var dpStyle = options.columnProperty.dpStyle || dyDpStyle.BootstrapDatePicker;
      if (dpStyle === dyDpStyle.BootstrapDatePicker) {
        if (_this.options.readOnly) {
          _this_.datetimepicker('destory');
        } else {
          var defaultDatetimepickerOptions = {
            widgetParent: 'body',
            widgetPositioning: {
              vertical: 'bottom',
              horizontal: 'left'
            },
            showClose: true,
            showClear: true,
            showTodayButton: true,
            locale: 'zh-cn' // 本地化
          };
          // YYYY-MM-DD HH:mm:ss
          format = format.replace(/d/g, 'D').replace(/y/g, 'Y');
          var timePicker = $.extend({}, defaultDatetimepickerOptions, {
            useCurrent: false,
            format: format,
            minDate: param.minDate,
            maxDate: param.maxDate
          });
          var fnValue = function (event) {
            if (event.date && event.date.format) {
              var value = event.date.format(timePicker.format);
              if (value == _this.getValue()) {
                return false;
              }
              _this.setValue(value, true);
              $(_this_).attr('title', value);
            }
            if (event.date === false) {
              _this.setValue('', true);
            }
          };
          //					$(_this_).parent().css("position", "relative");
          //					_this_.on("dp.change", fnValue).on("dp.show", function(){
          //						 $(this).datetimepicker("date", _this_.val());
          //					}).datetimepicker(timePicker);
          _this_.on('dp.change', fnValue).datetimepicker(timePicker);
        }
      } else if (dpStyle === dyDpStyle.My97DatePicker) {
        _this_.unbind('click').bind('click', function (event) {
          if (!_this.options.readOnly) {
            _this_.unbind('focus').bind('focus', function (event) {
              setTimeout(function () {
                if (_this_.val() == _this.getValue()) {
                  return false;
                }
                // 再设置一次
                _this.setValue(_this_.val(), false);
              }, 500);
            });
            WdatePicker({
              srcEl: event.target,
              dateFmt: format,
              customFmt: customFmt,
              alwaysUseStartDate: options.alwaysUseStartDate,
              onpicked: function () {
                _this.setValue(_this_.val(), false);
                _this_.focus();
              },
              // add by wujx 20160708 begin
              // 处理年和月控件，点击顶部的年、月选择后设置
              ychanged: function () {
                var c = $dp.cal;
                // 日控件，点击顶部的左右选择，不会改变结果输入框，不该触发设置
                if (c.dateFmt.indexOf('d') > -1) {
                  return;
                }
                _this.setValue(c.getNewDateStr(), false);
              },
              Mchanged: function () {
                var c = $dp.cal;
                // 日控件，点击顶部的左右选择，不会改变结果输入框，不该触发设置
                if (c.dateFmt.indexOf('d') > -1) {
                  return;
                }
                _this.setValue(c.getNewDateStr(), false);
              },
              // add by wujx 20160708 begin
              isShowOthers: false,
              startDate: options.startDate,
              minDate: param.minDate,
              maxDate: param.maxDate,
              oncleared: function () {
                _this.setValue('');
              }
            });
          }
        });
      } else if (dpStyle === dyDpStyle.LayDate) {
        //添加独一的class
        var _class = 'laydate-' + options.columnProperty.columnName;
        _this_.addClass(_class);
        var laydateParams = {
          elem: _this_[0]
        };

        var _layDateFormat = '';
        console.log('options.contentFormat', options.contentFormat);
        switch (options.contentFormat) {
          case '1':
            _layDateFormat = 'date|yyyy-MM-dd';
            break;
          case '2':
            _layDateFormat = 'date|yyyy年M月d日';
            break;
          case '7':
            _layDateFormat = 'year|yyyy';
            laydateParams.quickConfirm = true;
            break;
          case '3':
            _layDateFormat = 'year|yyyy年';
            laydateParams.quickConfirm = true;
            break;
          case '14':
            _layDateFormat = 'month|yyyy-MM';
            laydateParams.quickConfirm = true;
            break;
          case '4':
            _layDateFormat = 'month|yyyy年MM月';
            laydateParams.quickConfirm = true;
            break;
          case '5':
            _layDateFormat = 'date|MM月dd日';
            break;
          case '8':
            _layDateFormat = 'time|HH';
            break;
          case '9':
            _layDateFormat = 'time|HH:mm';
            break;
          case '10':
            _layDateFormat = 'time|HH:mm:ss';
            break;
          case '11':
            _layDateFormat = 'datetime|yyyy-MM-dd HH';
            break;
          case '12':
            _layDateFormat = 'datetime|yyyy-MM-dd HH:mm';
            break;
          case '13':
            _layDateFormat = 'datetime|yyyy-MM-dd HH:mm:ss';
            break;
          case '15':
            _layDateFormat = 'date-range|yyyy-MM-dd';
            laydateParams.autoWeek = true;
            laydateParams.control = this;
            laydateParams.quickConfirm = true;
            break;
          case '16':
            _layDateFormat = 'date|yyyy-M-d';
            break;
          case '17':
            _layDateFormat = 'date|yyyy年MM月dd日';
            break;
        }

        if (_layDateFormat) {
          var _layDateFormatArr = _layDateFormat.split('|');
          var _typeArr = _layDateFormatArr[0].split('-');

          laydateParams.type = _typeArr[0];
          if (_typeArr[1] || (options.columnProperty.setTimeStatus === 'startTime' && options.columnProperty.relevanceEndTime)) {
            laydateParams.range = '至';
          }
          laydateParams.format = _layDateFormatArr[1];
        }

        if (param.minDate) {
          laydateParams.min = _this.complementDate(_this.replaceTimeFormat(param.minDate), 'start');
        }
        if (param.maxDate) {
          laydateParams.max = _this.complementDate(_this.replaceTimeFormat(param.maxDate), 'end');
        }
        if (options.columnProperty.defaultTime) {
          laydateParams.value = new Date();
          if (options.contentFormat === '15') {
            //周时默认时间改为默认时间所在周的区间
            var startDate = new Date(laydateParams.value.getTime() - laydateParams.value.getDay() * 86400000).format(laydateParams.format);
            var endDate = new Date(laydateParams.value.getTime() + (6 - laydateParams.value.getDay()) * 86400000).format(
              laydateParams.format
            );
            // debugger;
            _this.setValue(startDate, true);
          } else {
            _this.setValue(laydateParams.value.format(laydateParams.format), true);
          }
        }
        laydateParams.trigger = 'click';

        laydateParams.done = function (value) {
          $('.layui-laydate').remove();
          if (options.contentFormat === '15' && typeof value === 'string') {
            // debugger;
            value = $.trim(value.split(laydateParams.range)[0]);
          }
          _this.setValue(value, true);
        };
        this.laydateParams = laydateParams;
        laydate.render(laydateParams);
      }
    },

    //补充为完整格式yyyy-MM-dd hh:mm:ss
    complementDate: function (date, type) {
      var dateArr = date.split(' ');
      var YMD = dateArr[0];
      var YMDArr = YMD.split('-');
      if (!YMDArr[YMDArr.length - 1]) {
        YMDArr.splice(YMDArr.length - 1);
      }
      var HMS = dateArr[1] || '';
      var HMSArr = HMS ? HMS.split(':') : [];
      if (HMSArr && !HMSArr[HMSArr.length - 1]) {
        HMSArr.splice(HMSArr.length - 1);
      }
      if (type === 'start') {
        switch (YMDArr.length) {
          case 1:
            YMDArr = YMDArr.concat(['01', '01']);
            break;
          case 2:
            if (YMDArr[0].length > 2) {
              YMDArr.push('01');
            } else {
              YMDArr.unshift(new Date().getFullYear());
            }
            break;
        }
        switch (HMSArr && HMSArr.length) {
          case 1:
            HMSArr = HMSArr.concat(['00', '00']);
            break;
          case 2:
            HMSArr.push('00');
            break;
        }
      } else {
        switch (YMDArr.length) {
          case 1:
            YMDArr = YMDArr.concat(['12', '31']);
            break;
          case 2:
            if (YMDArr[0].length > 2) {
              YMDArr.push(new Date(YMDArr[0], YMDArr[1] + 1, 0).getDate());
            } else {
              YMDArr.unshift(new Date().getFullYear());
            }
            break;
        }
        switch (HMSArr && HMSArr.length) {
          case 1:
            HMSArr = HMSArr.concat(['59', '59']);
            break;
          case 2:
            HMSArr.push('59');
            break;
        }
      }
      return YMDArr.join('-') + (HMSArr.length ? ' ' + HMSArr.join(':') : '');
    },
    //把中文格式转化为符号
    replaceTimeFormat: function (time) {
      if (time.indexOf('年')) {
        time = time.replace('年', '-');
      }
      if (time.indexOf('月')) {
        time = time.replace('月', '-');
      }
      if (time.indexOf('日')) {
        time = time.replace('日', '');
      }
      if (time.indexOf('时')) {
        time = time.replace('时', ':');
      }
      if (time.indexOf('分')) {
        time = time.replace('分', ':');
      }
      if (time.indexOf('秒')) {
        time = time.replace('秒', '');
      }
      return time;
    },

    setMinDate: function (minDate) {
      var _this_ = this.$editableElem;
      var dpStyle = this.options.columnProperty.dpStyle;
      if (dpStyle === dyDpStyle.BootstrapDatePicker) {
        _this_.datetimepicker('minDate', minDate);
      } else if (dpStyle === dyDpStyle.My97DatePicker || dpStyle === dyDpStyle.LayDate) {
        this.initDatePicker({
          minDate: minDate
        });
      }
    },

    setMaxDate: function (maxDate) {
      var _this_ = this.$editableElem;
      var dpStyle = this.options.columnProperty.dpStyle;
      if (dpStyle === dyDpStyle.BootstrapDatePicker) {
        _this_.datetimepicker('maxDate', maxDate);
      } else if (dpStyle === dyDpStyle.My97DatePicker || dpStyle === dyDpStyle.LayDate) {
        this.initDatePicker({
          maxDate: maxDate
        });
      }
    },

    setDateScope: function (param) {
      this.initDatePicker(param);
    },

    getWeek: function (date) {
      var time,
        week,
        checkDate = new Date(date.split(' 至 ')[0]);
      console.log(checkDate.getDate() + 4 - (checkDate.getDay() || 6));
      checkDate.setDate(checkDate.getDate() + 4 - (checkDate.getDay() || 6));
      time = checkDate.getTime();
      checkDate.setMonth(0);
      checkDate.setDate(1);
      week = Math.floor(Math.round((time - checkDate) / 86400000) / 7) + 1;
      return new Date(checkDate).getFullYear() + '年第' + (week < 10 ? '0' : '') + week + '周';
    },
    getValue: function () {
      var self = this;
      var value = self.value;
      var options = self.options;
      if (value == null || $.trim(value).length === 0) {
        return '';
      }

      if (options.columnProperty.setTimeStatus && options.columnProperty.setTimeStatus === 'startTime') {
        var endTimeValue = self.getEndTimeValue(options.columnProperty.relevanceEndTime);
        var endTimeCtl = $.ControlManager.getCtl(self.getContronId(options.columnProperty.relevanceEndTime));
        endTimeValue = endTimeCtl ? endTimeCtl.getValue() : endTimeValue;
        value = [value, endTimeValue].join(' 至 ');
      }
      return value;
    },

    setValue: function (value, isRender, formData) {
      if (
        value == null ||
        value.indexOf('undefined') > -1 ||
        value.indexOf('aN') > -1 ||
        value.indexOf('1970-01-01') > -1 ||
        value.indexOf('1970年01月01日') > -1
      ) {
        return;
      }
      var columnProperty = this.options.columnProperty;
      if (columnProperty.setTimeStatus) {
        if (columnProperty.setTimeStatus === 'startTime') {
          var relevantEndTimeCtrlId = this.getContronId(columnProperty.relevanceEndTime);
          var relevantEndTimeCtrlIdRemoveNs = relevantEndTimeCtrlId.replace(/^.+__/, '');
          var relevantEndTimeCtrl = $.ControlManager.getCtl(relevantEndTimeCtrlId);
          var valueArr = value.split(' 至 ');
          var startTime = valueArr[0];
          var endTime = valueArr[1];
          if (!endTime && formData && formData[relevantEndTimeCtrlIdRemoveNs]) {
            endTime = formData[relevantEndTimeCtrlIdRemoveNs];
          }

          this.value = startTime;

          endTime && relevantEndTimeCtrl.setValue(endTime);
        } else if (columnProperty.setTimeStatus === 'endTime') {
          this.value = value;
        } else {
          this.value = value;
        }
      } else if (this.options.contentFormat === '15') {
        //选择周格式
        var valueArr = value.split(' 至 ');
        this.value = valueArr[0];
      } else {
        this.value = value;
      }

      if (this.isRender()) {
        // 该控件被隐藏时则不进行渲染
        this.render(isRender); // 将值渲染到页面元素上
      }
      if (this.culateByFormula) this.culateByFormula(); // 根据运算公式计算
      this.invoke('afterSetValue', this.value);
    },

    getControl: function (fieldName) {
      var ns = this.getNamespace();
      if (ns && fieldName.indexOf(ns) < 0) {
        fieldName = ns + '___' + fieldName;
      }
      return $.ControlManager.getCtl(fieldName);
    },

    /* 设值到标签中 */
    setValue2LabelElem: function () {
      var self = this;
      if (this.$labelElem == null) {
        return;
      }
      if (this.value == null || $.trim(this.value).length == 0) {
        this.$labelElem.html('');
        return;
      }
      var value = self.value;
      var options = this.options;
      var format = '';
      var fmt = options.contentFormat;
      format = this.getFormat(fmt);

      if (options.columnProperty.setTimeStatus && options.columnProperty.setTimeStatus === 'startTime') {
        var endTimeValue = self.getEndTimeValue(options.columnProperty.relevanceEndTime);
        var endTimeCtl = $.ControlManager.getCtl(self.getContronId(options.columnProperty.relevanceEndTime));
        endTimeValue = endTimeCtl ? endTimeCtl.getValue() : endTimeValue;
      }

      var date = this.parseDate(value);
      if (date != null) {
        var _date = date.format(format);
        value = _date;
      }
      if (endTimeValue) {
        value = [value, endTimeValue].join(' 至 ');
      }
      if (options.contentFormat === '15') {
        value = this.getWeek(value);
      }
      this.$labelElem.html(value).attr('title', value);
    },
    getEndTimeValue: function (relevanceEndTime) {
      var self = this;
      var endTimeValue = '';
      var formData = DyformFacade.get$dyform().data();
      var formDatas = formData.options.formData.formDatas;
      $.each(formDatas, function (i, _formData) {
        $.each(_formData, function (j, item) {
          var dataUuid = self.getDataUuid();
          if (item.uuid !== dataUuid) {
            return true;
          }
          endTimeValue = item[relevanceEndTime];
        });
      });
      return endTimeValue;
    },

    /* 设置到可编辑元素中 */
    setValue2EditableElem: function () {
      var self = this;
      var $editableElem = self.$editableElem;
      if ($editableElem == null) {
        return;
      }
      var value = self.value;

      if ($.trim(value).length == 0) {
        return $editableElem.val('');
      }
      var options = self.options;
      var format = '';
      var fmt = options.contentFormat;
      format = self.getFormat(fmt);

      if (options.columnProperty.setTimeStatus && options.columnProperty.setTimeStatus === 'startTime') {
        var endTimeValue = self.getEndTimeValue(options.columnProperty.relevanceEndTime);
        var endTimeCtl = $.ControlManager.getCtl(self.getContronId(options.columnProperty.relevanceEndTime));
        endTimeValue = endTimeCtl ? endTimeCtl.getValue() : endTimeValue;
      }

      var date = self.parseDate(value);
      if (date != null) {
        var _date = date.format(format);
        value = _date;
      }
      if (endTimeValue) {
        value = [value, endTimeValue].join(' 至 ');
      }
      if (options.contentFormat === '15') {
        value = this.getWeek(value);
      }
      $editableElem.val(value).attr('title', value);
    },
    parseDate: function (str) {
      if (typeof str == 'string') {
        var results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) *$/);
        if (results && results.length > 3)
          return new Date(parseInt(results[1], 10), parseInt(results[2], 10) - 1, parseInt(results[3], 10));
        results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}) *$/);
        if (results && results.length > 5)
          return new Date(
            parseInt(results[1], 10),
            parseInt(results[2], 10) - 1,
            parseInt(results[3], 10),
            parseInt(results[4], 10),
            parseInt(results[5], 10)
          );
        results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}):(\d{1,2}) *$/);
        if (results && results.length > 6)
          return new Date(
            parseInt(results[1], 10),
            parseInt(results[2], 10) - 1,
            parseInt(results[3], 10),
            parseInt(results[4], 10),
            parseInt(results[5], 10),
            parseInt(results[6], 10)
          );
        results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}):(\d{1,2})[\.| ](\d{1,9}) *$/);
        if (results && results.length > 7)
          return new Date(
            parseInt(results[1], 10),
            parseInt(results[2], 10) - 1,
            parseInt(results[3], 10),
            parseInt(results[4], 10),
            parseInt(results[5], 10),
            parseInt(results[6], 10),
            parseInt(results[7], 10)
          );
      }
      return null;
    },
    getFormat: function (contentFormat) {
      var format = 'yyyy-MM-dd HH:mm:ss';
      var fmt = contentFormat;
      if (fmt == dyDateFmt.yearMonthDate || fmt == dyDateFmt.week) {
        format = 'yyyy-MM-dd';
      } else if (fmt == dyDateFmt.dateTimeHour) {
        format = 'yyyy-MM-dd HH';
      } else if (fmt == dyDateFmt.dateTimeMin) {
        format = 'yyyy-MM-dd HH:mm';
      } else if (fmt == dyDateFmt.dateTimeSec) {
        format = 'yyyy-MM-dd HH:mm:ss';
      } else if (fmt == dyDateFmt.timeHour) {
        format = 'HH';
      } else if (fmt == dyDateFmt.timeMin) {
        format = 'HH:mm';
      } else if (fmt == dyDateFmt.timeSec) {
        format = 'HH:mm:ss';
      } else if (fmt == dyDateFmt.yearMonthDateCn) {
        format = 'yyyy年M月d日';
      } else if (fmt == dyDateFmt.yearCn) {
        format = 'yyyy年';
      } else if (fmt == dyDateFmt.yearMonthCn) {
        format = 'yyyy年MM月';
      } else if (fmt == dyDateFmt.yearMonth) {
        format = 'yyyy-MM';
      } else if (fmt == dyDateFmt.monthDateCn) {
        format = 'MM月dd日';
      } else if (fmt == dyDateFmt.year) {
        format = 'yyyy';
      } else if (fmt == dyDateFmt.yearMonthDate2) {
        format = 'yyyy-M-d';
      } else if (fmt == dyDateFmt.yearMonthDateCn2) {
        format = 'yyyy年MM月dd日';
      }
      return format;
    }
  };

  /*
   * DATEPICKER PLUGIN DEFINITION =========================
   */
  $.fn.wdatePicker = function (option) {
    var method = false;
    var args = null;
    if (arguments.length == 2) {
      method = true;
      args = arguments[1];
    }

    if (typeof option == 'string') {
      if (option === 'getObject') {
        // 通过getObject来获取实例
        var $this = $(this);
        data = $this.data('wdatePicker');
        if (data) {
          return data; // 返回实例对象
        } else {
          throw new Error('This object is not available');
        }
      }
    }

    var $this = $(this),
      data = $this.data('wdatePicker'),
      options = typeof option == 'object' && option;
    if (!data) {
      data = new DatePicker($(this), options);
      $.extend(data, $.wControlInterface);
      $.extend(data, $.wTextCommonMethod);
      $.extend(data, $.DatePicker);
      data.init();
      $this.data('wdatePicker', data);
    }
    if (typeof option == 'string') {
      if (method == true && args != null) {
        return data[option](args);
      } else {
        return data[option]();
      }
    } else {
      return data;
    }
  };

  $.fn.wdatePicker.Constructor = DatePicker;

  $.fn.wdatePicker.defaults = {
    columnProperty: columnProperty, // 字段属性
    commonProperty: commonProperty, // 公共属性

    // 控件私有属性
    disabled: false,
    readOnly: false,
    isShowAsLabel: false,
    isHide: false, // 是否隐藏
    alwaysUseStartDate: false,
    startDate: '%y-%M-%d %H:%m:%s',
    showIcon: true,
    contentFormat: ''
  };
});
