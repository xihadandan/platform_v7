define(["constant", "commons", "server", "appContext", "appModal", "ListViewWidgetDevelopment"],
  function (constant, commons, server, appContext, appModal, ListViewWidgetDevelopment) {
    var JDS = server.JDS;

    var AppOrgGroupListDevelopment = function () {
      ListViewWidgetDevelopment.apply(this, arguments);
    };
    // 接口方法
    commons.inherit(AppOrgGroupListDevelopment, ListViewWidgetDevelopment, {
      getTableOptions: function (bootstrapTableOptions) {
        this.addOtherConditions([{
          columnIndex: 'systemUnitId',
          value: SpringSecurityUtils.getCurrentUserUnitId(),
          type: 'eq'
        }])
      },
      btn_delAll: function () {
        var self = this;
        var data = this.getSelection();
        if (data.length == 0) {
          appModal.error("请选择记录！");
          return;
        }
        appModal.confirm("确定要删除记录吗？", function (res) {
          if (res) {
            var ids = [];
            for (var i = 0; i < data.length; i++) {
              ids.push(data[i].id);
            }
            $.ajax({
              url: ctx + '/api/org/group/deleteGroups',
              type: 'POST',
              data: {
                groupIds: ids
              },
              dataType: 'json',
              success: function (result) {
                if (result.code === 0) {
                  appModal.success("删除成功！", function () {
                    self.refresh()
                  });
                } else {
                  appModal.error(result.msg);
                }
              }
            });
          }
        })
      },
      btn_export: function () {
        this.onExport("group")
      },
      btn_import: function () {
        this.onImport()
      }
    });
    return AppOrgGroupListDevelopment;
  });
