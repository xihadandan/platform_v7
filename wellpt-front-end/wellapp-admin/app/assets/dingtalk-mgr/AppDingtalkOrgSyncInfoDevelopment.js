/**
 * 组织同步日志详情 html容器二开
 */
define(['constant', 'commons', 'server', 'appContext', 'appModal', 'WidgetDevelopment', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  WidgetDevelopment,
  HtmlWidgetDevelopment
) {
  var AppDingtalkOrgSyncInfoDevelpment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };

  commons.inherit(AppDingtalkOrgSyncInfoDevelpment, HtmlWidgetDevelopment, {
    init: function () {
      var that = this;
      var params = this.getWidgetParams();
      var $container = this.widget.element;
      $('#p_uuid', $container).val(params.P_UUID);

      getDetail();

      // 详情
      function getDetail() {
        $.ajax({
          url: ctx + '/pt/dingtalk/getOrgSyncLogDetail',
          type: 'POST',
          data: {
            uuid: params.P_UUID
          },
          success: function (result) {
            var statusText = result.data.syncStatus == 1 ? '同步成功' : '同步异常';
            var statusIcon = result.data.syncStatus == 1 ? 'success' : 'error';
            $('.symcTime', $container).html('同步时间：' + result.data.syncTime);
            $('.syncContent', $container).html('同步内容：' + result.data.syncContent);
            $('.syncStatus', $container).html('状态：' + statusText);
            $('.' + statusIcon, $container).show();
            $('.operationMan', $container).html('操作人：' + result.data.operatorName);
          }
        });
      }
    },
    refresh: function () {
      this.init();
    }
  });

  return AppDingtalkOrgSyncInfoDevelpment;
});
