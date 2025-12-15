/**
 * 组织同步设置 二开
 */
define(['constant', 'commons', 'server', 'appContext', 'appModal', 'wSelect2', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  wSelect2,
  HtmlWidgetDevelopment
) {
  var AppDingtalkOrgSyncSetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppDingtalkOrgSyncSetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var that = this;
      var $widgetElement = that.getWidgetElement();
      // 默认设置
      var oldBean = {
        org_sync_dept: true,
        org_sync_user_photo: true,
        org_sync_user_mobile: true,
        org_sync_user_telephone: true,
        org_sync_user_email: true,
        org_sync_user_jobno: true,
        org_sync_user_remark: true,
        org_sync_workinfo: true,
        org_sync_user_idnumber: true
      };
      var saveFirst = 0;

      getSavedData();
      // 获取已保存数据
      function getSavedData() {
        var that = this;
        $.ajax({
          url: ctx + '/pt/dingtalk/getConfig',
          type: 'GET',
          async: false,
          data: {},
          dataType: 'json',
          success: function (result) {
            if (result.success) {
              json2form(result.data);
            }
          },
          error: function () {
            appModal.error('获取同步设置信息失败！');

            for (var key in oldBean) {
              $('#' + key, $widgetElement).prop('checked', oldBean[key]);
            }
          }
        });
      }
      // json2form
      function json2form(jsonData) {
        if (jsonData && typeof jsonData == 'object') {
          for (var key in oldBean) {
            var newKey = key.replaceAll('_', '.');
            oldBean[key] = jsonData[newKey] == '1' ? true : false;
            $('#' + key, $widgetElement).prop('checked', oldBean[key]);
          }
        }
      }
      // form2json
      function form2json(formData) {
        var data = new Object();
        for (var key in formData) {
          var newKey = key.replaceAll('_', '.');
          data[newKey] = $('#' + key, $widgetElement).prop('checked') ? '1' : '0';
        }
        return data;
      }
      // 保存
      $('#dingtalk_sync_set_btn_save', $widgetElement)
        .off()
        .on('click', function (e, isSync) {
          var orgSetValue = $('#org_sync_dept').prop('checked');
          var orgSetValue9 = $('#org_sync_workinfo').prop('checked');

          if (!orgSetValue && orgSetValue9) {
            appModal.confirm(
              '确定部门不同步，但人员和职位的关系同步吗？<br />如果职位所属的部门不存在，则人员和职位的关系将无法同步，报错同步异常！',
              function (result) {
                if (result) {
                  ajax_save(isSync);
                }
              }
            );
          } else {
            ajax_save(isSync);
          }
        });
      // 保存请求
      function ajax_save(isSync) {
        var that = this;
        $.ajax({
          url: ctx + '/pt/dingtalk/saveConfig',
          type: 'POST',
          data: { jsonParams: JSON.stringify(form2json(oldBean)) },
          dataType: 'json',
          success: function (result) {
            if (result.success) {
              appModal.success('保存成功');
              getSavedData();
              saveFirst = 0;

              if (isSync) {
                ajax_sync();
              }
            }
          },
          error: function () {
            appModal.error('保存失败');
          }
        });
      }
      // 同步状态
      function syncStatusFn() {
        const that = this;
        appModal.showMask('正在同步中...', null, 10 * 60 * 1000);

        var timer = setInterval(function () {
          $.ajax({
            url: ctx + '/pt/dingtalk/syncOrgStatue',
            type: 'GET',
            dataType: 'json',
            async: true,
            success: function (result) {
              var data = JSON.parse(result.data);
              if (data.code == 1) {
                appModal.hideMask();
                appModal.success('同步成功！');
                clearInterval(timer);
                appContext.refreshWidgetById('page_20200604161753', true);
              } else if (data.code == 2) {
                clearInterval(timer);

                syncStatusFn();
              } else if (data.code == 0) {
                appModal.hideMask();
                appModal.error('同步异常！');
                clearInterval(timer);
              }
            }
          });
        }, 5000);
      }
      // 同步请求
      function ajax_sync() {
        var that = this;
        $.ajax({
          url: ctx + '/pt/dingtalk/syncOrg',
          type: 'POST',
          dataType: 'json',
          data: {},
          success: function (result) {
            // appModal.hideMask();
            // if (result.success) {
            //   appModal.success('同步成功！');
            // } else {
            //   appModal.error(result.msg);
            // }
          },
          error: function (err) {
            // appModal.error('同步异常');
          }
        });
        syncStatusFn();
      }
      // 同步
      $('#dingtalk_sync_set_btn_sync', $widgetElement)
        .off()
        .on('click', function () {
          var formData = new Object();
          for (var key in oldBean) {
            formData[key] = $('#' + key, $widgetElement).prop('checked');
          }
          var flag = false;
          for (var key in formData) {
            if (formData[key] != oldBean[key]) {
              flag = true;
              break;
            }
          }
          // 没保存
          if (flag) {
            saveFirst++;
          }
          if (saveFirst) {
            // appModal.error('内容已修改，请先保存后再同步，否则修改内容将丢失！');
            // return false;

            appModal.confirm({
              title: '组织同步',
              message: '内容已修改，请先保存后再同步，否则修改内容将丢失！',
              callback: function (result) {
                if (result) {
                  $('#dingtalk_sync_set_btn_save', $widgetElement).trigger('click', true);
                }
              }
            });
          } else {
            // 已经保存   可以同步
            appModal.confirm({
              title: '组织同步确认',
              message: '确定根据当前设置同步？',
              callback: function (result) {
                if (result) {
                  // 调接口
                  ajax_sync();
                }
              }
            });
          }
        });
    },
    refresh: function () {
      var that = this;
      that.init();
    }
  });
  return AppDingtalkOrgSyncSetDevelopment;
});
