define(['constant', 'commons', 'server', 'ListViewWidgetDevelopment', 'AppDataImportExportCommon'], function (
  constant,
  commons,
  server,
  ListViewWidgetDevelopment,
  appCommonJS
) {
  var AppDataImportTaskListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  //平台应用_数据导入导出管理_数据导入任务_列表二开
  commons.inherit(AppDataImportTaskListDevelopment, ListViewWidgetDevelopment, {
    // 取消
    btn_cancel: function (events, options, rowData) {
      var _self = this;
      if (rowData.TASK_STATUS == 2 || rowData.TASK_STATUS == 3) {
        //“导入中”、“异常终止”时，按钮可用
        var option = {
          title: '确认提示',
          size: 'small',
          message: '是否确定取消导入数据？',
          callback: function (callue) {
            if (callue) {
              _self.cancelReq(rowData.UUID);
            }
          }
        };
        appModal.confirm(option);
      }
    },
    //取消请求
    cancelReq: function (uuid) {
      var self = this;
      var type = 'import'; //类型：导入（import）、导出（export）
      var data = {
        url: '/api/dataIO/cancelTask/' + type + '/' + uuid,
        params: {},
        type: 'GET'
      };
      appCommonJS
        .ajaxFunction(data)
        .then(function (res) {
          if (res.code == 0) {
            appModal.success('取消成功');
            self.getWidget().refresh();
          } else {
            appModal.error(res.msg || '取消失败');
          }
        })
        .catch(function (err) {
          // console.error(err.msg);
        });
    },
    // 重新导入
    btn_reimport: function (events, options, rowData) {
      var self = this;
      if (rowData.TASK_STATUS == 3) {
        //“异常终止”时，按钮可用
        var type = 'import'; //类型：导入（import）、导出（export）
        var data = {
          url: '/api/dataIO/restartTask/' + type + '/' + rowData.UUID,
          params: {},
          type: 'GET'
        };
        appCommonJS
          .ajaxFunction(data)
          .then(function (res) {
            if (res.code == 0) {
              appModal.success(res.msg || '操作成功');
              self.getWidget().refresh();
            } else {
              appModal.error(res.msg || '操作失败');
            }
          })
          .catch(function (err) {
            // console.error(err.msg);
          });
      }
    },
    //格式化行级按钮
    lineEnderButtonHtmlFormat: function (format, row, index) {
      var before = format.before;
      //取消按钮，“导入中”、“异常终止”时，按钮可用，否则禁用
      //重新导入（行），“异常终止”时，按钮可用，否则禁用
      if (row.TASK_STATUS == 1 || row.TASK_STATUS == 0) {
        //状态为完成与取消状态
        before = before.replace('btn-default btn-bg-color btn_class_btn_cancel', 'w-disable-btn btn_class_btn_cancel');
        before = before.replace('btn-default btn-bg-color btn_class_btn_reimport', 'w-disable-btn btn_class_btn_reimport');
      } else if (row.TASK_STATUS == 2) {
        //状态为导入中状态
        before = before.replace('btn-default btn-bg-color btn_class_btn_reimport', 'w-disable-btn btn_class_btn_reimport');
      }
      format.after = before;
      return format;
    },
    onLoadSuccess: function () {
      var id = this.widget.element.parents('.tab-pane').first().attr('id');
      $("a[href='#" + id + "']")
        .parent()
        .trigger(constant.WIDGET_EVENT.BadgeRefresh);
    }
  });
  return AppDataImportTaskListDevelopment;
});
