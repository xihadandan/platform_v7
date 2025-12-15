define(['constant', 'commons', 'server', 'appContext', 'appModal', 'ListViewWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  ListViewWidgetDevelopment
) {
  var AppOrgRankListTableDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppOrgRankListTableDevelopment, ListViewWidgetDevelopment, {
    btn_add: function () {
      this.details();
    },
    btn_edit: function (e) {
      var index = $(e.target).parents('tr').data('index');
      var uuid = this.getData()[index].uuid;
      this.details(uuid);
    },
    btn_del: function (e) {
      var index = $(e.target).parents('tr').data('index');
      var uuid = this.getData()[index].uuid;
      this.delDuty(uuid);
    },
    btn_del_all: function () {
      var _self = this;
      // 1.获取选择的数据
      var selection = _self.getWidget().getSelections();
      if (selection.length == 0) {
        appModal.error('请选择要删除的职务！');
        return;
      }
      var uuids = _.map(selection, 'uuid');
      this.delDuty(uuids, true);
    },
    btn_import: function () {
      var self = this;
      var msg = getMessage();
      appModal.dialog({
        title: '导入职务',
        size: 'large',
        message: msg,
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              var file = $('#uploadfile').val();
              if (file == '') {
                appModal.error('请选择xls文件');
                return false;
              }
              if (file.indexOf('.') < 0) {
                return false;
              }
              var fileType = file.substring(file.lastIndexOf('.'), file.length);
              if (fileType == '.xls' || fileType == '.xlsx') {
                var url = getBackendUrl() + '/multi/org/duty/import';
                var _auth = getCookie('_auth');
                if (_auth) {
                  url += '?' + _auth + '=' + getCookie(_auth);
                }
                $.ajaxFileUpload({
                  url: url, // 链接到服务器的地址
                  secureuri: false,
                  fileElementId: 'uploadfile', // 文件选择框的ID属性
                  dataType: 'text', // 服务器返回的数据格式
                  success: function (data, status) {
                    // QQ浏览器会多返回莫名奇妙的额外数据，所以套个text()方法来过滤脏数据
                    data = $('<div>' + data + '</div>').text();
                    if (data.indexOf('成功') > 0) {
                      appModal.alert(data);
                      self.refresh();
                    } else {
                      appModal.alert(data);
                    }
                  },
                  error: function (data) {}
                });
              } else {
                appModal.error('请选择xls文件');
                return false;
              }
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default'
          }
        }
      });

      function getMessage() {
        var message = '';
        message +=
          '<form id="import_form" class="form-widget" name="import_form" tenctype="multipart/form-data" method="post">' +
          '<div class="well-form">' +
          '<div class="form-group">' +
          '<label for="sapCode" class="well-form-label control-label">选择XLS文件</label>' +
          '<div class="well-form-control">' +
          '<input id="uploadfile" name="upload" type="file">' +
          '<div class="content"><a href="' +
          ctx +
          '/static/resfacade/share/职务数据导入模板_6.0.xls">职务数据导入模板</a></div>' +
          '</div>' +
          '</div>' +
          '</div>' +
          '</form>';
        return message;
      }
    },
    btn_export: function () {
      var url = getBackendUrl() + '/multi/org/duty/export';
      var _auth = getCookie('_auth');
      if (_auth) {
        url += '?' + _auth + '=' + getCookie(_auth);
      }
      openWindowByPost(url, null, '_blank');
    },
    btn_import_def: function () {
      this.onImport();
    },
    btn_export_def: function () {
      this.onExport('duty');
    },
    details: function (uuid) {
      var html = getPostDialog();
      var _this = this;
      var formBean = {
        uuid: null,
        id: null,
        name: null,
        systemUnitId: null,
        code: null,
        sapCode: null,
        remark: null,
        dutySeqName: null,
        dutySeqUuid: null,
        jobRank: null
      };
      var $postDialog = appModal.dialog({
        message: html,
        title: '职务',
        size: 'large',
        width: 800,
        height: 600,
        shown: function () {
          function getJobRank(dutyUuid) {
            $.ajax({
              url: ctx + '/api/org/multi/listJobRankByDutySeqUuid/' + dutyUuid,
              type: 'get',
              dataType: 'json',
              success: function (result) {
                if (result.code === 0) {
                  var jobRankData = [];
                  $.each(result.data, function (index, item) {
                    jobRankData.push({
                      id: item.id,
                      text: item.jobRank,
                      desc: item.jobGradeName || item.jobGrade
                    });
                  });

                  $('#jobRank', $postDialog).wellSelect('reRenderOption', {
                    data: jobRankData
                  });
                }
              }
            });
          }

          $('#jobRank', $postDialog).wellSelect({
            data: [],
            valueField: 'jobRank',
            showEmpty: false,
            multiple: true,
            chooseAll: true,
            separator: ','
          });

          var setting = {
            check: {
              radioType: 'level',
              chkStyle: 'radio',
              enable: true
            },
            view: {
              autoCancelSelected: true
            },
            async: {
              otherParam: {
                serviceName: 'orgDutySeqService',
                methodName: 'queryDutySeqSelect',
                data: ['', '']
              }
            },
            callback: {
              onNodeCreated: function (event, treeId, treeNode) {},
              onClick: null
            }
          };

          $('#dutySeqName', $postDialog).comboTree({
            labelField: 'dutySeqName',
            valueField: 'dutySeqUuid',
            treeSetting: setting,
            width: 220,
            height: 220,
            multiple: true,
            autoInitValue: false,
            autoCheckByValue: true,
            labelBy: 'name',
            showParentCheck: false,
            selectParent: false,
            afterSetValue: function (value, label) {
              getJobRank(value);
              $('#wellSelect_jobRank').parents('tr').show();
            }
          });
          $('#wellSelect_jobRank').parents('tr').hide();
          if (uuid) {
            showDutyInfo(uuid, $postDialog);
          }
        },
        buttons: {
          save: {
            label: '保存',
            className: 'well-btn w-btn-primary',
            callback: function () {
              $('#post_form', $postDialog).form2json(formBean);

              formBean.systemUnitId = formBean.systemUnitId || SpringSecurityUtils.getCurrentUserUnitId();
              if (!formBean.name) {
                appModal.error('请填写名称！');
                return false;
              }
              if (!formBean.code) {
                appModal.error('请填写编号！');
                return false;
              }
              if (formBean.dutySeqUuid && !formBean.jobRank) {
                appModal.error('请选择职级！');
                return false;
              }

              var url = '/api/org/multi/' + (formBean.uuid ? 'modifyDuty' : 'addDuty');
              $.ajax({
                type: 'POST',
                url: url,
                dataType: 'json',
                data: formBean,
                success: function (result) {
                  if (result.code == 0) {
                    appModal.success('保存成功！');
                    _this.getWidget().refresh();
                    $postDialog.modal('hide');
                  } else {
                    appModal.error(result.msg);
                  }
                }
              });
              return false;
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default'
          }
        }
      });

      function showDutyInfo(uuid, $postDialog) {
        $.ajax({
          url: ctx + '/api/org/multi/getDuty',
          type: 'get',
          data: {
            uuid: uuid
          },
          success: function (result) {
            formBean = result.data;
            $('#post_form', $postDialog).json2form(formBean);
            $('#dutySeqName', $postDialog).comboTree('initValue', formBean.dutySeqUuid).trigger('change');
            if (formBean.dutySeqUuid) {
              $('#wellSelect_jobRank').parents('tr').show();
            }
          }
        });
      }

      function getPostDialog() {
        var html = '';
        html +=
          "<form id='post_form' class='dyform'>" +
          "<table class='well-form form-horizontal'>" +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'><font style='color:#f00;'>*</font>名称</td>" +
          "<td><input type='text' name='name' id='name' class='form-control' placeholder='名称'/></td>" +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'>ID</td>" +
          "<td><input type='text' name='id' id='id' class='form-control' placeholder='ID' readonly/></td>" +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'><font style='color:#f00;'>*</font>编号</td>" +
          "<td><input type='text' name='code' id='code' class='form-control' placeholder='编号'/></td>" +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'>所属职务序列</td>" +
          '<td>' +
          "<input name='dutySeqName' id='dutySeqName' class='form-control' placeholder='请选择'/>" +
          "<input name='dutySeqUuid' id='dutySeqUuid' type='hidden'/>" +
          '</td>' +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'><font style='color:#f00;'>*</font>职级</td>" +
          "<td><input name='jobRank' id='jobRank' class='form-control' placeholder='请选择'/></td>" +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'>描述</td>" +
          "<td><textarea name='remark' id='remark' cols='30' rows='5' class='form-control'></textarea></td>" +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'>SAP编号</td>" +
          "<td><input name='sapCode' id='sapCode' class='form-control' placeholder='SAP编号'/></td>" +
          '</tr>' +
          '</table>' +
          '</form>';

        return html;
      }
    },
    delDuty: function (uuid, isMulti) {
      var _self = this;
      appModal.confirm('确定删除职务？', function (result) {
        if (result) {
          if (!isMulti) {
            $.ajax({
              type: 'delete',
              url: ctx + '/api/org/multi/deleteDuty/' + uuid,
              dataType: 'json',
              success: function (res) {
                if (res.code == 0) {
                  appModal.success('删除成功！');
                  _self.refresh();
                } else {
                  appModal.error(res.msg);
                }
              }
            });
          } else {
            $.ajax({
              type: 'POST',
              url: ctx + '/api/org/multi/deleteDuty',
              dataType: 'json',
              data: {
                uuids: uuid
              },
              success: function (result) {
                if (result.code == 0) {
                  appModal.success('删除成功！');
                  _self.refresh();
                } else {
                  appModal.error(result.msg);
                }
              }
            });
          }
        }
      });
    }
  });
  return AppOrgRankListTableDevelopment;
});
