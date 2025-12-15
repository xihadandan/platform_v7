define(['constant', 'commons', 'server', 'appContext', 'appModal', 'ListViewWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  ListViewWidgetDevelopment
) {
  var JDS = server.JDS;

  var AppOrgVerListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppOrgVerListDevelopment, ListViewWidgetDevelopment, {
    onLoadSuccess: function (data) {
      var $element = this.widget.element;
      var $index = $element.find("th[data-field='fullName']").index();
      $element
        .find('tbody')
        .find('tr')
        .find('td:eq(' + $index + ')')
        .css({
          position: 'relative'
        });
    },
    btn_update_ver: function () {
      var rowId = this.getSelectionUuids()[0];
      var self = this;
      if (rowId) {
        var rowData = this.getSelections()[0];
        var newUnitName = rowData.name;
        var initUnitName = rowData.initSystemUnitName;
        var msg = '确定升级版本？';
        var buttons = {};

        if (newUnitName == initUnitName) {
          buttons = {
            confirm: {
              label: '升级',
              className: 'btn-primary',
              callback: function () {
                self.submitUpgrade(rowId, false);
              }
            },
            cancel: {
              label: '取消',
              className: 'btn-default'
            }
          };
        } else {
          msg = '单位名称已经改名，是否同步升级？';
          buttons = {
            confirm: {
              label: '升级并改名',
              className: 'btn-primary',
              callback: function () {
                self.submitUpgrade(rowId, true, rowData.id);
              }
            },
            update: {
              label: '升级不改名',
              className: 'btn-primary',
              callback: function () {
                self.submitUpgrade(rowId, false, rowData.id);
              }
            },
            cancel: {
              label: '取消',
              className: 'btn-default'
            }
          };
        }
        var html = "<div id='dialog_upgrade'><p>" + msg + '</p></div>';
        appModal.dialog({
          title: '版本升级提示',
          message: html,
          buttons: buttons
        });
      } else {
        appModal.error('请先选中一行');
      }
    },
    btn_start: function () {
      var rowId = this.getSelectionUuids()[0];
      if (rowId) {
        this.activeOrgVersion(rowId);
      } else {
        appModal.error('请先选中一行');
      }
    },
    btn_stop: function () {
      var data = this.getSelections()[0];
      var rowId = this.getSelectionUuids()[0];
      if (rowId) {
        this.unactiveOrgVersion(rowId, data);
      } else {
        appModal.error('请先选中一行');
      }
    },
    submitUpgrade: function (rowId, isSyncName, fromVersionId) {
      var self = this;
      $.ajax({
        url: ctx + '/api/org/version/addNewOrgVersionForUpgrade',
        type: 'POST',
        data: {
          sourceVersionUuid: rowId,
          isSyncName: isSyncName
        },
        dataType: 'json',
        async: false,
        success: function (result) {
          if (isSyncName) {
            // 重新计算用户的工作信息
            $.ajax({
              url: ctx + '/api/org/user/recomputeUserWorkInfoByVersions',
              type: 'POST',
              data: {
                fromVersionId: fromVersionId,
                toVersionId: result.data.id
              },
              dataType: 'json',
              async: false,
              success: function (result) {}
            });
          }
          appModal.success('升级成功，如需启用，需要手动发布！', function () {
            self.refresh();
          });
        }
      });
    },
    activeOrgVersion: function (orgVersionUuid) {
      var self = this;
      appModal.confirm('确定使用该版本，则会停用其他的同类型版本？', function (res) {
        if (res) {
          $.ajax({
            url: ctx + '/api/org/version/activeOrgVersion',
            type: 'POST',
            data: {
              orgVersionUuid: orgVersionUuid
            },
            dataType: 'json',
            async: false,
            success: function (result) {
              appModal.success('启用成功', function () {
                self.refresh();
              });
            }
          });
        }
      });
      return false;
    },
    unactiveOrgVersion: function (orgVersionUuid, data) {
      var self = this;
      var title = '确定停用该版本？';
      if (data.isDefault) {
        title = data.fullName + '是默认组织版本，停用后将取消默认，是否确认停用？';
      }
      appModal.confirm(title, function (res) {
        if (res) {
          $.ajax({
            url: ctx + '/api/org/version/unactiveOrgVersion',
            type: 'POST',
            data: {
              orgVersionUuid: orgVersionUuid
            },
            dataType: 'json',
            async: false,
            success: function (result) {
              appModal.success('停用成功', function () {
                self.refresh();
              });
            }
          });
        }
      });

      return false;
    }
  });
  return AppOrgVerListDevelopment;
});
