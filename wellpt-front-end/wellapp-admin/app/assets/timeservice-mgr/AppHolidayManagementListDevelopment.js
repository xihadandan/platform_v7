define(['constant', 'commons', 'server', 'ListViewWidgetDevelopment'], function (constant, commons, server, ListViewWidgetDevelopment) {
  // 平台应用_基础数据管理_节假日管理_列表二开
  var AppHolidayManagementListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppHolidayManagementListDevelopment, ListViewWidgetDevelopment, {
    onClickRow: function (rowNum, row, $element, field) {
      var url =
        ctx +
        '/web/app/pt-mgr/pt-basicdata-mgr/app_20210531101600.html?pageUuid=0c641e1e-b5d4-483e-805f-f119804cadb6&uuid=' +
        row.uuid +
        '&tableBtnEditTag=' +
        row.uuid;
      appContext.getNavTabWidget().createTab('holiday_management', '节假日管理-编辑', 'iframe', url);
    },
    btn_del_all: function (e) {
      var datas = this.getSelections();
      if (datas.length == 0) {
        appModal.error('请先选择记录！');
      } else {
        var uuid = [];
        $.each(datas, function (index, item) {
          uuid.push(item.uuid);
        });
        this.deleteTimeService(uuid);
      }
    },
    btn_del: function (e) {
      var index = $(e.target).parents('tr').first().data('index');
      var datas = this.getData();
      var uuid = datas[index].uuid;
      this.deleteTimeService([uuid]);
    },
    deleteTimeService: function (uuid) {
      var self = this;
      $.ajax({
        // 先判断是否有被引用
        url: ctx + '/api/ts/holiday/isUsed',
        type: 'post',
        data: { uuids: uuid },
        success: function (res) {
          if (res.data) {
            appModal.error('节假日正在使用，无法删除！');
          } else {
            appModal.confirm('确定删除节假日？', function (result) {
              if (result) {
                $.ajax({
                  // 删除计时服务
                  url: ctx + '/api/ts/holiday/deleteAll',
                  type: 'post',
                  data: { uuids: uuid },
                  success: function (res) {
                    if (res.code == 0) {
                      appModal.success('删除成功');
                      self.refresh();
                    } else {
                      appModal.error(res.msg);
                    }
                  }
                });
              }
            });
          }
        }
      });
    }
  });

  return AppHolidayManagementListDevelopment;
});
