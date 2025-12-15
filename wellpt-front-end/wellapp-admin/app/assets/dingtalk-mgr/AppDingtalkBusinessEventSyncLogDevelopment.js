define(['constant', 'commons', 'server', 'moment', 'ListViewWidgetDevelopment'], function (
  constant,
  commons,
  server,
  moment,
  ListViewWidgetDevelopment
) {
  'use strict';
  var AppDingtalkBusinessEventSyncLogDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  var DateUtils = commons.DateUtils;
  var endTime = moment().format('YYYY-MM-DD') + ' 23:59:59';
  var startTime = moment(endTime).subtract(1, 'months').format('YYYY-MM-DD') + ' 00:00:00';
  var defaultTime = startTime + ' 至 ' + endTime;
  commons.inherit(AppDingtalkBusinessEventSyncLogDevelopment, ListViewWidgetDevelopment, {
    afterRender: function () {},
    beforeRender: function (options, configuration) {
      configuration.query.fields[3].defaultValue = defaultTime;
      // 标识是不是第一次加载
      this.isFirst = true;
      // 是否跨年度查询校验
      $.each(configuration.query.fields, function (index, item) {
        if (item.name == 'OPT_TIME') {
          item.queryOptions.change = function (value) {
            var year = value.split(' 至 ');
            var last = new Date(year[0]).getFullYear();
            var curr = new Date(year[1]).getFullYear();
            if (last != curr) {
              setTimeout(function () {
                $('.laydate-btns-confirm').addClass('laydate-disabled').addClass('laydate-not-same');
                $('.layui-laydate').append('<div class="layui-laydate-hint">不支持跨年度的时间范围查询</div>').show();
              }, 100);
              setTimeout(function () {
                $('.layui-laydate').find('.layui-laydate-hint').remove();
              }, 3000);
            } else {
              $('.laydate-btns-confirm').removeClass('laydate-not-same');
            }
          };
        }
      });
    },
    beforeLoadData(options, configuration, request) {
      var reqData = request.data;
      var criterions = reqData.criterions;
      var index;
      for (var i = 0; i < criterions.length; i++) {
        if (criterions[i].columnIndex && criterions[i].columnIndex == 'STATUS' && criterions[i].value == "''") {
          index = i;
          break;
        }
      }
      if (index != undefined) {
        criterions.splice(index, 1);
      }
      if (this.isFirst) {
        this.addOtherConditions([
          {
            columnIndex: 'OPT_TIME',
            value: [startTime, endTime],
            type: 'between'
          }
        ]);
        this.isFirst = false;
      } else {
        this.clearOtherConditions();
      }
    },
    onLoadSuccess: function (data) {
      var $widget = this.getWidget().element;
      $('.form-tooltip', $widget).remove();
      var tooltipEle =
        '<div class="form-tooltip">' +
        '<i class="iconfont icon-ptkj-xinxiwenxintishi" style="font-size: 14px; vertical-align: middle"></i>' +
        '<div class="tooltip-body show-down"><span class="arrow"></span><div class="form-tooltip-content show-down"><p>1、默认显示最近一个月</p><p>2、不支持跨年度的时间范围查询</p></div></div>' +
        '</div>';
      var lastRow = $widget.find('.div_search_toolbar').find('.row').last();

      lastRow.find('label').eq(0).append(tooltipEle);
    }
  });
  return AppDingtalkBusinessEventSyncLogDevelopment;
});
