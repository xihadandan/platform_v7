define(['constant', 'commons', 'server', 'appModal', 'wSelect2', 'ListViewWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appModal,
  wSelect2,
  ListViewWidgetDevelopment
) {
  // 视图组件二开基础
  var AppFileLibraryListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppFileLibraryListDevelopment, ListViewWidgetDevelopment, {
    configuration: null,
    beforeRender: function (options, configuration) {
      console.log(1, options, configuration);
      this.configuration = configuration;
    },
    btn_add: function (e) {
      var indexes = this.getSelectionIndexes();
      var self = this;
      if (indexes.length > 0) {
        var uuids = this.getData()[indexes[0]].uuid;
        JDS.call({
          service: 'dmsFolderMgr.getBean',
          data: [uuids],
          version: '',
          success: function (result) {
            var data = result.data;
            if (data.parentUuid != null) {
              self.show_dialog(data.parentUuid, 'children', '新增' + self.configuration.name);
            } else {
              self.show_dialog('', '', '新增' + self.configuration.name);
            }
          }
        });
      } else {
        self.show_dialog('', '', '新增' + self.configuration.name);
      }
    },

    btn_add_children: function () {
      var index = this.getSelectionIndexes();
      if (index.length == 0) {
        appModal.error('请选择记录！');
        return;
      } else {
        var uuids = this.getData()[index[0]].uuid;
        this.show_dialog(uuids, 'children', '新增' + this.configuration.name + '子夹');
      }
    },

    btn_check: function (e) {
      var index = $(e.target).parents('tr').data('index');
      var uuid = this.getData()[index].uuid;
      this.show_dialog(uuid, '', '查看' + this.configuration.name);
    },

    btn_delete: function (e) {
      var self = this;
      var index = -1;
      if ($(e.target).parents('tr').data('index') !== '' && $(e.target).parents('tr').data('index') !== undefined) {
        index = $(e.target).parents('tr').data('index');
        var uuids = self.getData()[index].uuid;
      } else {
        var indexes = this.getSelectionIndexes();
        if (indexes.length == 0) {
          appModal.error('请选择记录！');
          return;
        }
        index = indexes[0];
        var uuids = this.getData()[index].uuid;
      }

      if (uuids == '') {
        appModal.error('请选择记录！');
        return;
      }
      JDS.call({
        service: 'dmsFolderService.existsFolerChildFile',
        data: [[uuids]],
        version: '',
        success: function (result) {
          if (!result.data.flg) {
            appModal.error(self.getData()[index].name + '或子夹中存在数据无法删除，请先移动或删除数据');
          } else {
            appModal.confirm('删除选中的文件夹？', function (result) {
              if (result) {
                self.delete_folder(uuids);
              }
            });
          }
        }
      });
    },
    delete_folder: function (uuids) {
      var self = this;
      JDS.call({
        service: 'dmsFolderService.removeAllUuids',
        data: [[uuids]],
        version: '',
        success: function (result) {
          appModal.success('删除成功！', function () {
            self.refresh();
          });
        }
      });
    },
    show_dialog: function (uuid, type, title) {
      var fieldsInfo = this.configuration.fieldsInfo;
      var rolesField = this.configuration.rolesField || [];
      var self = this;
      var bean = {
        uuid: null, // UUID
        recVer: null, // 版本号
        contentType: null, // CONTENT_TYPE
        name: null, // NAME
        code: null, // CODE
        parentUuid: null, // PARENT_UUID
        remark: null, // REMARK
        status: 1,
        systemUnitId: null,
        absolutePath: null,
        attach: null,
        createTime: null,
        creator: null,
        modifyTime: null,
        uuidPath: null
      };
      var folderConfiguration = {
        dataType: 'DYFORM', // 数据类型
        formUuid: null, // 表单定义UUID
        formName: null, // 表单定义名称
        displayFormUuid: null, // 展示表单定义UUID
        displayFormName: null, // 展示表单定义名称
        listViewId: null, // 展示视图ID
        listViewName: null, // 展示视图名称
        fileNameField: null, // 文档标题字段
        fileNameFieldName: null, // 文档标题字段名称
        fileStatusField: null, // 文档状态字段
        fileStatusFieldName: null, // 文档状态字段名称
        readFileField: null, // 阅读人员字段
        readFileFieldName: null, // 阅读人员字段名称
        editFileField: null, // 编辑人员字段
        editFileFieldName: null, // 编辑人员字段名称
        editFileField: null, // 编辑人员字段
        stick: null, // 置顶状态
        stickStatusField: null, // 置顶状态字段
        stickStatusFieldName: null, // 置顶状态字段名称
        stickTimeField: null, // 置顶时间字段
        stickTimeFieldName: null, // 置顶时间字段名称
        readRecord: null, // 启用阅读记录
        readRecordField: null, // 阅读人员字段
        readRecordFieldName: null, // 阅读人员字段名称
        isInheritFolderRole: '1', // 是否继承夹的权限配置，1是，0否
        isEnableMessageNotice: '0', // 是否继承夹的消息通知配置，1是，0否
        appId: null
      };
      var fieldsList = {
        formName: 'formUuid',
        fileNameFieldName: 'fileNameField',
        fileStatusFieldName: 'fileStatusField',
        readFileFieldName: 'readFileField',
        editFileFieldName: 'editFileField',
        stickStatusFieldName: 'stickStatusField',
        stickTimeFieldName: 'stickTimeField',
        readRecordFieldName: 'readRecordField',
        displayFormName: 'displayFormUuid',
        listViewName: 'listViewId'
      };
      var html =
        '<form action="" id="file_library_form" class="dyform" style="margin-top: 15px;">' +
        '<input type="hidden" id="uuid" name="uuid" />' +
        '<input type="hidden" id="recVer" name="recVer" />' +
        '<input type="hidden" id="parentUuid" name="parentUuid" />' +
        '<input type="hidden" id="appId" name="appId" />' +
        '<table class="well-form form-horizontal">';
      for (var i = 0; i < fieldsInfo.length; i++) {
        var field = fieldsInfo[i].field;
        var text = fieldsInfo[i].text;
        if (fieldsInfo[i].isChecked) {
          html += '<tr class="field">' + '<td class="label-td" style="width:20%;">';
          if (field == 'name') {
            html += "<font style='color:#f00;'>*</font>";
          }
          html += text + '</td>';

          if (field == 'name' || field == 'code' || field == 'contentType') {
            html +=
              '<td>' + '<input type="text" class="form-control" name="' + field + '" id="' + field + '" placeholder="请输入">' + '</td>';
          } else if (field == 'remark' || field == 'code') {
            html +=
              '<td>' + '<textarea type="text" class="form-control" id="remark" name="remark" placeholder="请输入"></textarea>' + '</td>';
          } else if (field == 'dataType') {
            html +=
              '<td>' +
              '<select id="dataType" name="dataType" type="text" class="full-width">' +
              '<option value="DYFORM" selected>动态表单</option>' +
              '</select>' +
              '</td>';
          } else if (
            field == 'formName' ||
            field == 'fileNameFieldName' ||
            field == 'fileStatusFieldName' ||
            field == 'readFileFieldName' ||
            field == 'editFileFieldName' ||
            field == 'stickStatusFieldName' ||
            field == 'stickTimeFieldName' ||
            field == 'readRecordFieldName' ||
            field == 'displayFormName' ||
            field == 'listViewName'
          ) {
            html +=
              '<td>' +
              '<input type="text" class="form-control" id="' +
              field +
              '" name="' +
              field +
              '" placeholder="请选择">' +
              '<input type="hidden" class="form-control" id="' +
              fieldsList[field] +
              '" name="' +
              fieldsList[field] +
              '" >' +
              '</td>';
          } else if (field == 'stick' || field == 'readRecord') {
            html +=
              '<td>' +
              '<input class="form-control" id="' +
              field +
              '" name="' +
              field +
              '" type="checkbox" value="true">' +
              '<label for="' +
              field +
              '"></label>' +
              '</td>';
          }
          html += '</tr>';
        }
      }
      html +=
        '<tr>' +
        "<td class='label-td' style='width:20%;'>权限配置</td>" +
        '<td>' +
        "<input type='radio' id='isInheritFolderRole_Y' name='isInheritFolderRole' value='1'><label for='isInheritFolderRole_Y'>继承上级夹</label>" +
        "<input type='radio' id='isInheritFolderRole_N' name='isInheritFolderRole' value='0' checked><label for='isInheritFolderRole_N'>自定义</label>" +
        '</td>' +
        '</tr>';

      for (var i = 0; i < rolesField.length; i++) {
        var role = rolesField[i];
        html +=
          '<tr class="field role-item">' +
          '<td class="label-td" style="width:20%;" data-id="' +
          role.roleUuid +
          '">' +
          role.roleName +
          '</td>';
        html +=
          '<td>' +
          '<input class="form-control" id="orgNames' +
          i +
          '" name="orgNames" type="text" placeholder="请输入">' +
          '<input class="form-control" id="orgIds' +
          i +
          '" name="orgIds" type="hidden">' +
          '</td>';
        html += '</tr>';
      }
      html += '</table>' + '</form>';
      appModal.dialog({
        message: html,
        size: 'large',
        title: '目录',
        buttons: {
          confirm: {
            label: '保存',
            className: 'well-btn w-btn-primary',
            callback: function () {
              $('#file_library_form').form2json(bean);
              $('#file_library_form').form2json(folderConfiguration);
              if (bean.name == '' || bean.name == null) {
                var label = $("input[name='name']").parents('tr').find('td.label-td').text().replace('*', '');
                appModal.error(label + '不能为空！');
                return false;
              }
              bean.systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
              bean.parentUuid = bean.parentUuid || null; //父级节点不能保存空字符串
              bean.configuration = $.extend(true, bean.configuration, folderConfiguration);

              var roles = $('.role-item');
              $.each(roles, function (index, item) {
                for (var i = 0; i < rolesField.length; i++) {
                  if ($(item).find('.label-td').data('id') === rolesField[i].roleUuid) {
                    rolesField[i].orgNames = $(item).find("input[name='orgNames']").val();
                    rolesField[i].orgIds = $(item).find("input[name='orgIds']").val();
                  }
                }
              });
              bean.configuration.assignRoles = rolesField;

              JDS.call({
                service: 'dmsFolderMgr.saveBean',
                data: bean,
                version: '',
                success: function (result) {
                  if (result.success) {
                    appModal.success('保存成功！', function () {
                      self.refresh();
                    });
                  } else {
                    appModal.error(result.msg);
                    return false;
                  }
                },
                error: function (err) {
                  appModal.error(err.msg);
                  return false;
                }
              });
            }
          },
          cancel: {
            label: '关闭',
            className: 'btn btn-default'
          }
        },
        shown: function () {
          $('#dataType').select2({});
          // 显示表单
          $('#displayFormName').wSelect2({
            serviceName: 'formDefinitionService',
            queryMethod: 'queryFormDefinitionSelect',
            labelField: 'displayFormName',
            valueField: 'displayFormUuid',
            defaultBlank: true,
            width: '100%',
            height: 250,
            params: {
              includeTypes: 'V'
            }
          });
          // 展示视图
          $('#listViewName').wSelect2({
            serviceName: 'appWidgetDefinitionMgr',
            labelField: 'listViewName',
            valueField: 'listViewId',
            params: {
              wtype: 'wBootstrapTable',
              uniqueKey: 'id',
              includeWidgetRef: 'false'
            },
            defaultBlank: true,
            width: '100%',
            remoteSearch: true
          });
          //动态表单
          $('#formName')
            .wSelect2({
              serviceName: 'formDefinitionService',
              queryMethod: 'queryFormDefinitionSelect',
              labelField: 'formName',
              valueField: 'formUuid',
              defaultBlank: true,
              remoteSearch: true,
              width: '100%',
              height: 250,
              params: {
                includeTypes: 'P;MST'
              }
            })
            .on('change', function () {
              for (var field in fieldsList) {
                if (field != 'formName' && field != 'displayFormName' && field != 'listViewName') {
                  $.each(fieldsInfo, function (index, item) {
                    if (item.isChecked && item.field == field) {
                      $('#' + field).wSelect2({
                        serviceName: 'dataManagementViewerComponentService',
                        queryMethod: 'getColumnsOfDyFormSelectData',
                        labelField: field,
                        valueField: fieldsList[field],
                        defaultBlank: true,
                        remoteSearch: false,
                        width: '100%',
                        height: 250,
                        params: {
                          formUuid: $('#formUuid').val()
                        }
                      });
                    }
                  });
                }
              }
            });

          for (var i = 0; i < rolesField.length; i++) {
            (function (i) {
              $('#orgNames' + i).orgSelect({
                trigger: 'click',
                orgStyle: 'org-style3',
                orgOptions: {
                  labelField: 'orgNames' + i,
                  valueField: 'orgIds' + i,
                  title: '选择人员',
                  selectTypes: 'all',
                  type: 'all',
                  valueFormat: 'justId'
                }
              });
            })(i);
          }

          $("input[name='isInheritFolderRole']").change(function () {
            if ($('tr.role-item').size() > 0) {
              var val = $("input[name='isInheritFolderRole']:checked").val();
              if (val == '1') {
                $('tr.role-item').hide();
              } else {
                $('tr.role-item').show();
              }
            }
          });

          if (uuid && type == 'children') {
            $('input[name="parentUuid"]').val(uuid);
          } else if (uuid && type != 'children') {
            self.getFileDetail(bean, uuid);
          } else {
            $('#isInheritFolderRole_Y').attr('disabled', true);
          }
          self.afterShowDialog(uuid, title);
        }
      });
    },

    getFileDetail: function (bean, uuid) {
      JDS.call({
        service: 'dmsFolderMgr.getBean',
        data: [uuid],
        version: '',
        success: function (result) {
          bean = result.data;
          $('#file_library_form').json2form(bean);
          $('#file_library_form').json2form(bean.configuration);
          $('#formName').trigger('change');
          $('#displayFormName').trigger('change');
          $('#listViewName').trigger('change');
          var assignRoles = bean.configuration.assignRoles;
          var roles = $('.role-item');

          $.each(roles, function (index, item) {
            for (var i = 0; i < assignRoles.length; i++) {
              if ($(item).find('.label-td').data('id') === assignRoles[i].roleUuid) {
                $('#orgIds' + i).val(assignRoles[i].orgIds);
                $('#orgNames' + i)
                  .val(assignRoles[i].orgNames)
                  .trigger('change.orgSelect');
              }
            }
          });

          $("input[name='isInheritFolderRole']").trigger('change');
          if (!bean.parentUuid) {
            $('#isInheritFolderRole_Y').attr('disabled', true);
          }
        }
      });
    },
    afterShowDialog: function (uuid) {}
  });

  return AppFileLibraryListDevelopment;
});
