define(['constant', 'commons', 'server', 'ListViewWidgetDevelopment', 'AppDataImportExportCommon'], function (
  constant,
  commons,
  server,
  ListViewWidgetDevelopment,
  appCommonJS
) {
  var AppDataTaskLogListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  //平台应用_数据导入导出管理_任务页查看日志列表_列表二开
  commons.inherit(AppDataTaskLogListDevelopment, ListViewWidgetDevelopment, {
    // 视图组件渲染前回调方法，子类可覆盖
    beforeRender: function (options, configuration) {
      var _self = this;
      var uuid = options.containerDefinition.params.uuid; //任务项uuid
      if (uuid) {
        _self.addOtherConditions([{ columnIndex: 'TASK_UUID', type: 'eq', value: uuid }]);
      }
      if (options.containerDefinition.params.repeat) {
        //1为重复数据
        _self.addOtherConditions([{ columnIndex: 'IS_REPEAT', type: 'eq', value: 1 }]); //1为重复数据
      }
      if (options.containerDefinition.params.error) {
        _self.addOtherConditions([{ columnIndex: 'IMPORT_STATUS', type: 'eq', value: 0 }]); //0为异常数据
      }

      //弹框底部按钮处理
      var $container = _self.getWidget().element.parents('.modal-content');
      $container.find('.modal-footer button[data-bb-handler="confirm"]').remove();
      $container.find('.modal-footer button[data-bb-handler="cancel"]').html('关闭');
    },
    // 视图组件渲染完成回调方法，子类可覆盖
    afterRender: function (options, configuration) {
      this.getWidget().refresh();
    }
  });
  return AppDataTaskLogListDevelopment;
});
