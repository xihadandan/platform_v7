define(['constant', 'commons', 'server', 'ListViewWidgetDevelopment'], function (constant, commons, server, ListViewWidgetDevelopment) {
  // 平台应用_基础数据管理_计时服务_列表二开
  var AppWorkTimeHistoryListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppWorkTimeHistoryListDevelopment, ListViewWidgetDevelopment, {
    beforeLoadData: function (options, configuration) {
      var $element = this.widget.element;
      parent.$('iframe.embed-responsive-item').parent().css({
        paddingBottom: '52%'
      });
      $element.parents('.container-fluid').css({
        background: '#fff'
      });
      var id = $element.attr('id');
      $('#' + id + '_table', $element).css({
        'table-layout': 'initial'
      });
      var uuid = GetRequestParam().uuid || this.getWidgetParams().uuid;
      this.clearOtherConditions();
      this.addOtherConditions([
        {
          columnIndex: 'workTimePlanUuid',
          value: uuid,
          type: 'eq'
        }
      ]);
    }
  });

  return AppWorkTimeHistoryListDevelopment;
});
