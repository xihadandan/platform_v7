define(['constant', 'commons', 'server', 'ListViewWidgetDevelopment'], function (constant, commons, server, ListViewWidgetDevelopment) {
  // 平台应用_基础数据管理_计时服务_列表二开
  var AppWorkTimeVersionListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppWorkTimeVersionListDevelopment, ListViewWidgetDevelopment, {
    beforeLoadData: function (options, configuration) {
      var $element = this.widget.element;
      parent.$('iframe.embed-responsive-item').parent().css({
        paddingBottom: '52%'
      });
      $element.parents('.container-fluid').css({
        background: '#fff'
      });
      var id = GetRequestParam().id || this.getWidgetParams().id;
      this.clearOtherConditions();
      this.addOtherConditions([
        {
          columnIndex: 'id',
          value: id,
          type: 'eq'
        }
      ]);
    },
    lineEnderButtonHtmlFormat: function (format, row, index) {
      var $html = $(format.before);
      $html.find(row.status == '0' ? '' : '.btn_class_btn_edit').remove();
      format.after = $html[0].outerHTML;
    }
  });

  return AppWorkTimeVersionListDevelopment;
});
