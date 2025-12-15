define(['constant', 'commons', 'server', 'HtmlWidgetDevelopment'], function (constant, commons, server, HtmlWidgetDevelopment) {
  'use strict';
  var AppDingtalkBusinessEventSyncInfoDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppDingtalkBusinessEventSyncInfoDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var that = this;
      var params = this.getWidgetParams();
      var $container = this.widget.element;
      $('#p_business_uuid', $container).val(params.P_UUID);
      getDetail();
      // 详情
      function getDetail() {
        $.ajax({
          url: ctx + '/pt/dingtalk/getEventCallBackDetail',
          type: 'POST',
          data: {
            uuid: params.P_UUID
          },
          success: function (result) {
            var statusText = result.data.status == 1 ? '同步成功' : result.data.status == 2 ? '同步异常' : '';
            var statusIcon = result.data.status == 1 ? 'success' : result.data.status == 2 ? 'error' : '';
            $('.eventName', $container).html('钉钉事件：' + result.data.eventName);
            $('.dingTimeStamp', $container).html('钉钉事件名称：' + result.data.dingTimeStamp);
            $('.syncContent', $container).html('同步内容：' + result.data.syncContent);
            $('.syncStatus', $container).html('状态：' + statusText);
            $('.' + statusIcon, $container).show();
            $('.optTime', $container).html('OA同步时间：' + result.data.optTime);
          }
        });
      }
    },
    refresh: function () {
      this.init();
    }
  });
  return AppDingtalkBusinessEventSyncInfoDevelopment;
});
