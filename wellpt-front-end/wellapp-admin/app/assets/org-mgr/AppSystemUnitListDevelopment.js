define(['constant', 'commons', 'server', 'ListViewWidgetDevelopment'], function (constant, commons, server, ListViewWidgetDevelopment) {
  // 视图组件二开基础
  var AppSystemUnitListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppSystemUnitListDevelopment, ListViewWidgetDevelopment, {
    // 删除
    btn_del: function (e) {
      var index = $(e.target).parents('tr').data('index');
      var data = this.getData();
      var name = data[index].name;
      var self = this;
      appModal.confirm('确定要删除页面元素[' + name + ']吗？', function (res) {
        if (res) {
          JDS.call({
            service: 'exchangeDataTypeService.remove',
            data: [data[index].uuid],
            success: function (result) {
              self.refresh();
            }
          });
        }
      });
    },
    // 批量删除
    btn_delAll: function (e) {
      var self = this;
      var uuids = this.getSelectionPrimaryKeys();
      if (uuids.length == 0) {
        appModal.error('请选择记录！');
        return;
      }
      appModal.confirm('确定要删除所选记录吗？', function (res) {
        if (res) {
          JDS.call({
            service: 'exchangeDataTransformService.deleteByUuids',
            data: [uuids],
            success: function (result) {
              appModal.success('删除成功！');
              self.refresh();
            }
          });
        }
      });
    }
  });

  return AppSystemUnitListDevelopment;
});
