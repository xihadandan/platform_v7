define(['constant', 'commons', 'server', 'appModal', 'HtmlWidgetDevelopment', 'AppDataImportExportCommon'], function (
  constant,
  commons,
  server,
  appModal,
  HtmlWidgetDevelopment,
  appCommonJS
) {
  var $container = undefined;
  var _self = undefined;

  // 平台应用_数据导入导出管理_数据源详情页_HTML二开
  var AppExportLogSourceWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppExportLogSourceWidgetDevelopment, HtmlWidgetDevelopment, {
    // 组件初始化
    init: function (options) {
      _self = this;
      $container = this.widget.element;

      var pageUuid = this.widget.options.containerDefinition.params.uuid;
      $container.parents('.modal-content').find('.modal-title').html(pageUuid);
      _self.getBaseInfo(pageUuid);
      $container.parents('.modal-content').find('.modal-footer button[data-bb-handler="confirm"]').remove();
      $container.parents('.modal-content').find('.modal-footer button[data-bb-handler="cancel"]').html('关闭');
    },
    //获取数据
    getBaseInfo: function (uuid) {
      var data = {
        url: '/api/dataIO/getImportSourceData/' + uuid,
        params: {},
        type: 'GET'
      };
      appCommonJS
        .ajaxFunction(data)
        .then(function (res) {
          if (res.code == 0) {
            _self.initLogInfo(res.data); //渲染页面
          } else {
            appModal.error(res.msg || '操作失败');
          }
        })
        .catch(function (err) {
          // console.error(err.msg);
        });
    },
    //渲染日志数据
    initLogInfo: function (data) {
      var $logBody = $('.data-import-log', $container);
      $logBody.empty();
      var html = '';
      _.each(data, function (item) {
        var resource = JSON.stringify(JSON.parse(item.sourceData), null, 2);
        html += '<span style="white-space: pre-wrap;">' + resource + '</span><br/>';
      });
      $logBody.html(html);
    },
    refresh: function () {
      this.init();
    }
  });
  return AppExportLogSourceWidgetDevelopment;
});
