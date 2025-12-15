(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'appContext'], factory);
  } else {
    // Browser globals
    factory(jQuery, window.appContext);
  }
})(function ($, appContext) {
  'use strict';
  /**
   * <code>
     appContext.require(['MyCalendar'], function (MyCalendar) {
        MyCalendar.open({
          title: '资源名称',
          calendarWidgetId: 'wMyCalendar_C7FD69073640000159A61967D80545F0',
          callback: function (valueObj) {
            alert(JSON.stringify(valueObj));
          }
        });
      });
   * </code>
   */
  $.MyCalendar = {
    // 设置参数的默认值
    defaultOptions: {
      title: '选择资源', // 标题
      configuration: {
        calendarType: 'panel' // 组件应用：simple、stardard、panel。默认选择面板模式
      }, // 组件配置
      calendarWidgetId: null, // wMyCalendar_xxx
      valueField: null // 回写字段ID，可以为空字符串
    },
    options: {},
    open: function (params) {
      var _self = this;
      var ctx = window.ctx || '';
      // 合并参数到 this.options 中, 以options的配置为准, 供给所有方法共同使用
      var options = (_self.options = $.extend({}, _self.defaultOptions, params));
      if (null == options.calendarWidgetId) {
        alert('参数[calendarWidgetId]不允许为空');
        return null;
      }
      //检查参数是否正确
      var calendarDialogId = options.calendarWidgetId + '_dailog_' + $.guid++;
      var $iframeHtml = $('<div id="' + calendarDialogId + '">');
      $iframeHtml.append('<link href="' + ctx + '/static/js/select2/select2.css" rel="stylesheet">');
      $iframeHtml.append('<link href="' + ctx + '/static/js/bootstrap-datetimepicker/css/bootstrap-datetimepicker.css" rel="stylesheet">');
      $iframeHtml.append('<link href="' + ctx + '/static/js/fullcalendar/resourceview/css/resource_view.css" rel="stylesheet">');
      $iframeHtml.append('<link href="' + ctx + '/static/js/fullcalendar/3.9.0/css/fullcalendar.min.css" rel="stylesheet">');
      $iframeHtml.append('<link href="' + ctx + '/static/css/widget/well_calendar.css" rel="stylesheet">');
      $iframeHtml.append('<link href="' + ctx + '/static/css/widget/jquery-ui-wMyCalendar.css" rel="stylesheet">');
      $iframeHtml.append('<div class="ui-wMyCalendar"></div></div>');
      $iframeHtml.find('.ui-wMyCalendar').attr({
        id: options.calendarWidgetId
      });
      var calendarDialog = $iframeHtml.dialog({
        title: _self.options.title,
        bgiframe: true,
        autoOpen: true,
        resizable: true,
        stack: true,
        width: 980,
        height: 620,
        modal: true,
        overlay: {
          background: '#000',
          opacity: 0.5
        },
        buttons: {
          取消: function () {
            calendarDialog.dialog('close');
          },
          确定: function () {
            //将选中的值，赋值给对应labelField和valueField字段
            if (null == _self.valueObj) {
              appModal.warning('请选择');
              return false;
            }
            var valueObj = _self.valueObj;
            $('#' + _self.options.valueField).val(JSON.stringify(valueObj));
            if (_self.options.callback) {
              _self.options.callback.call(this, valueObj);
            }
            calendarDialog.dialog('close');
          }
        },
        open: function (event) {
          var widgetDefinition = appContext.getWidgetDefinition(options.calendarWidgetId);
          var requireJavaScriptModules = widgetDefinition.requireJavaScriptModules || [];
          appContext.require(requireJavaScriptModules, function () {
            var definitionJson = JSON.parse(widgetDefinition.definitionJson);
            definitionJson.configuration.onSelectCallback = function (valueObj) {
              _self.valueObj = valueObj;
            };
            $.extend(definitionJson.configuration, _self.options.configuration);
            appContext.createWidget(definitionJson, { id: calendarDialogId }, function (widget) {
              if (_self.options.open) {
                _self.options.open.apply(this, arguments);
              }
            });
          });
        },
        close: function (event) {
          if (_self.options.close) {
            _self.options.close.apply(this, arguments);
          }
          return true;
        }
      });
      return calendarDialog;
    }
  };
  return $.MyCalendar;
});
