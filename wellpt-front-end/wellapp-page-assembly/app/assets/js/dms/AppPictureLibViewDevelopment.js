define(['constant', 'commons', 'server', 'ListViewWidgetDevelopment'], function (constant, commons, server, ListViewWidgetDevelopment) {
  // 视图组件二开基础
  var AppPictureLibViewDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppPictureLibViewDevelopment, ListViewWidgetDevelopment, {
    btn_del: function () {
      var self = this;

      var selectedRows = this.getSelections();
      if (!selectedRows.length) {
        appModal.error('请选择记录!');
        return;
      }

      $.ajax({
        type: 'GET',
        url: '/basicdata/img/category/queryAllCategory',
        dataType: 'json',
        async: false,
        success: function (result) {
          var data = result.data;

          var hasPicturesCategories = [];
          for (var i = 0; i < selectedRows.length; i++) {
            var category = selectedRows[i];
            var uuid = category.UUID;
            var name = category.NAME;
            for (var j = 0; j < data.length; j++) {
              if (data[j].uuid === uuid && data[j].fileIDs.length > 0) {
                hasPicturesCategories.push(name);
              }
            }
          }

          if (hasPicturesCategories.length) {
            appModal.error('以下分类已存在图片：' + hasPicturesCategories.join('、') + '，不允许删除，请重新选择!');
            return;
          }

          appModal.confirm('确定要删除所选记录吗？', function (res) {
            if (res) {
              for (var i = 0; i < selectedRows.length; i++) {
                var uuid = selectedRows[i].UUID;
                $.ajax({
                  url: '/basicdata/img/category/' + uuid,
                  type: 'DELETE',
                  async: false,
                  dataType: 'json',
                  success: function (result) {}
                });
              }
              appModal.success('删除成功!');
              self.refresh();
            }
          });
        }
      });
    }
  });

  return AppPictureLibViewDevelopment;
});
