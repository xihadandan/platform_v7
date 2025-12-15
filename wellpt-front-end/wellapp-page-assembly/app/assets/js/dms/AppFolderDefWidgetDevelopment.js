define([
  'constant',
  'commons',
  'server',
  'appContext',
  'appModal',
  'wSelect2',
  'formBuilder',
  'HtmlWidgetDevelopment',
  'bootstrapTable_editable'
], function (constant, commons, server, appContext, appModal, wSelect2, formBuilder, HtmlWidgetDevelopment) {
  var JDS = server.JDS;
  var StringUtils = commons.StringUtils;

  var AppFolderDefWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppFolderDefWidgetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var form_selector = '#dms_folder_form';
      var folder_assign_role_list_selector = '#dms_folder_assign_role_list';
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
      // 夹配置
      var folderConfiguration = {
        dataType: null, // 数据类型
        formUuid: null, // 表单定义UUID
        formName: null, // 表单定义名称
        displayFormUuid: null, // 展示表单定义UUID
        displayFormName: null, // 展示表单定义名称
        displayMFormUuid: null, // 展示手机表单定义UUID
        displayMFormName: null, // 展示手机表单定义名称
        listViewId: null, // 展示视图ID
        listViewName: null, // 展示视图名称
        printTemplateUuids: null, //打印模板ID
        printTemplateNames: null, //打印模板名称
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
        // 应用ID
      };
      var validator = $.common.validation.validate(form_selector, 'dmsFolderEntity');
      initTable();
      initControlsFun();
      var uuid = GetRequestParam().uuid;
      var type = GetRequestParam().type;
      if (uuid && type) {
        $('#parentUuid').val(uuid);
        $('#dataType').val('');
        $('#formUuid').val('');
        $('#displayFormUuid').val('');
        $('#displayMFormUuid').val('');
        $('#dataType').trigger('change');
        $('#formName').trigger('change');
        $('#displayFormName').trigger('change');
        $('#displayMFormName').trigger('change');
        $('#listViewName').trigger('change');
        $('#printTemplateNames').trigger('change');
        $('#div_isInheritFolderRole_Y').hide();
      } else if (uuid) {
        getDmsFolder(uuid);
      } else {
        var parentUuid = $('#parentUuid').val();
        if (StringUtils.isBlank(parentUuid)) {
          $("input[name='isInheritFolderRole'][value='0']")[0].checked = true;
          $("input[name='isInheritFolderRole'][value='0']").trigger('change');
          $("input[name='isInheritFolderRole'][value='1']").attr('disabled', 'disabled');
          $('#div_isInheritFolderRole_Y').show();
        }
      }

      function getDmsFolder(uuid) {
        appModal.showMask(null, $('#dms_folder_form'));
        JDS.call({
          service: 'dmsFolderMgr.getBean',
          data: [uuid],
          version: '',
          success: function (result) {
            bean = result.data;
            $(form_selector).json2form(bean);
            $(form_selector).json2form(bean.configuration);
            $('#dataType').trigger('change');
            $('#formName').trigger('change');
            $('#displayFormName').trigger('change');
            $('#displayMFormName').trigger('change');
            $('#listViewName').trigger('change');
            $('#printTemplateNames').comboTree('initValue', bean.configuration.printTemplateUuids).trigger('change');

            $('#fileNameFieldName').trigger('change');
            $('#fileStatusFieldName').trigger('change');
            $('#readFileFieldName').trigger('change');
            $('#editFileFieldName').trigger('change');
            $('#stickStatusFieldName').trigger('change');
            $('#stickTimeFieldName').trigger('change');
            $('#readRecordFieldName').trigger('change');
            if (bean.configuration) {
              $("input[name='isInheritFolderRole'][value=" + bean.configuration.isInheritFolderRole + ']').trigger('change');
            }
            // 设置到分配的权限列表
            toFolderAssignRoleList(bean.configuration);
            validator.form();
            appModal.hideMask($('#dms_folder_form'));
          }
        });
      }

      $('#dataType')
        .wSelect2({
          width: '100%',
          valueField: 'dataType',
          defaultBlank: true,
          data: [
            {
              id: 'DYFORM',
              text: '动态表单'
            },
            {
              id: 'FILE',
              text: '文件实体'
            },
            {
              id: 'MIXTURE',
              text: '混合模式'
            }
          ]
        })
        .trigger('change');

      $('#dataType').on('change', function () {
        var dataType = $(this).val();
        if (dataType == null) {
          dataType = '';
        }
        var dataTypeCls = dataType.toLowerCase();
        $('.data-type').hide();
        $('.data-type-' + dataTypeCls).show();
      });

      // 动态表单
      $('#formName').wSelect2({
        serviceName: 'dataManagementViewerComponentService',
        queryMethod: 'getDataTypeOfDyFormSelectData',
        selectionMethod: 'getDataTypeOfDyFormSelectDataByIds',
        labelField: 'formName',
        valueField: 'formUuid',
        defaultBlank: true,
        remoteSearch: false,
        width: '100%',
        height: 250
      });
      $('#formName').on('change', function () {
        // 选择表单字段
        resetDyFormSelect2Data('fileNameFieldName', 'fileNameField');
        resetDyFormSelect2Data('fileStatusFieldName', 'fileStatusField');
        resetDyFormSelect2Data('readFileFieldName', 'readFileField');
        resetDyFormSelect2Data('editFileFieldName', 'editFileField');
        resetDyFormSelect2Data('stickStatusFieldName', 'stickStatusField');
        resetDyFormSelect2Data('stickTimeFieldName', 'stickTimeField');
        resetDyFormSelect2Data('readRecordFieldName', 'readRecordField');
      });

      // 重新选择表单后，重置表单字段的下拉选择
      function resetDyFormSelect2Data(labelField, valueField) {
        $('#' + labelField).wSelect2({
          serviceName: 'dataManagementViewerComponentService',
          queryMethod: 'getColumnsOfDyFormSelectData',
          labelField: labelField,
          valueField: valueField,
          defaultBlank: true,
          remoteSearch: false,
          width: '100%',
          height: 250,
          params: {
            formUuid: $('#formUuid').val()
          }
        });
      }

      function initControlsFun() {
        // 显示表单
        $('#displayFormName').wSelect2({
          serviceName: 'dataManagementViewerComponentService',
          queryMethod: 'getDataTypeOfDyFormSelectData',
          selectionMethod: 'getDataTypeOfDyFormSelectDataByIds',
          labelField: 'displayFormName',
          valueField: 'displayFormUuid',
          defaultBlank: true,
          remoteSearch: false,
          width: '100%',
          height: 250
        });
        // 显示手机表单
        $('#displayMFormName').wSelect2({
          serviceName: 'dataManagementViewerComponentService',
          queryMethod: 'getDataTypeOfDyFormSelectData',
          selectionMethod: 'getDataTypeOfDyFormSelectDataByIds',
          labelField: 'displayMFormName',
          valueField: 'displayMFormUuid',
          params: {
            formType: 'M'
          },
          defaultBlank: true,
          remoteSearch: false,
          width: '100%',
          height: 250
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
          remoteSearch: false
        });

        var printTreeSetting = {
          view: {
            showIcon: true,
            showLine: false
          },
          check: {
            enable: true,
            chkStyle: 'checkbox',
            radioType: 'all'
          },
          async: {
            otherParam: {
              serviceName: 'dmsFolderConfigurationFacadeService',
              methodName: 'getPrintTemplateTreeByUser',
              data: []
            }
          },
          callback: {
            onNodeCreated: function (event, treeId, treeNode) {},
            onClick: null,
            beforeClick: function (treeId, treeNode) {}
          }
        };
        // 打印模板
        $('#printTemplateNames').comboTree({
          labelField: 'printTemplateNames',
          valueField: 'printTemplateUuids',
          treeSetting: printTreeSetting,
          width: 220,
          height: 220,
          mutiSelect: true,
          // autoInitValue: false,
          autoCheckByValue: true,
          showCheckAll: true,
          includeParentValue: true,
          showParentCheck: true,
          selectParent: true
        });
      }
      // 继承上级夹
      $("input[name='isInheritFolderRole']").on('change', function (e) {
        var isInheritFolderRole = $(this).val();
        if (isInheritFolderRole == '1') {
          $('#div_isInheritFolderRole_Y').hide();
        } else {
          $('#div_isInheritFolderRole_Y').show();
        }
      });

      $('#folder_btn_save').on('click', function () {
        if (!validator.form()) {
          return false;
        }
        $(form_selector).form2json(bean);
        $(form_selector).form2json(folderConfiguration);
        if (folderConfiguration.dataType == 'DYFORM' && StringUtils.isBlank(folderConfiguration.formUuid)) {
          appModal.error('请选择使用的表单！');
          return;
        }
        bean.systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        bean.parentUuid = bean.parentUuid || null; //父级节点不能保存空字符串
        bean.configuration = $.extend(true, bean.configuration, folderConfiguration);
        bean.configuration.assignRoles = collectFolderAssignRoleList();
        JDS.call({
          service: 'dmsFolderMgr.saveBean',
          data: bean,
          version: '',
          success: function (result) {
            if (result.success) {
              appModal.success('保存成功！');
              appContext.getNavTabWidget().closeTab();
            } else {
              appModal.error(result.msg);
            }
          },
          error: function (err) {}
        });
      });

      // 增加权限角色
      $('#btn_dms_role_add').on('click', function () {
        var mydata = {
          uuid: new UUID().createUUID(),
          roleName: null,
          roleUuid: null,
          orgNames: null,
          orgIds: null,
          denyName: null,
          deny: null,
          permit: 'Y',
          permitName: '是'
        };
        $(folder_assign_role_list_selector).bootstrapTable('insertRow', {
          index: 0,
          row: mydata
        });
      });

      // 删除权限角色
      $('#btn_dms_role_del').on('click', function () {
        var rowids = $(folder_assign_role_list_selector).bootstrapTable('getSelections');
        if (rowids.length == 0) {
          appModal.error('请选择记录！');
          return;
        }

        appModal.confirm('确定删除记录？', function (result) {
          if (result) {
            var fieldsVal = [];
            for (var i = 0; i < rowids.length; i++) {
              fieldsVal.push(rowids[i].uuid);
            }
            $(folder_assign_role_list_selector).bootstrapTable('remove', {
              field: 'uuid',
              values: fieldsVal
            });
          }
        });
      });

      // 收集权限角色
      function collectFolderAssignRoleList() {
        var assignRoles = $(folder_assign_role_list_selector).bootstrapTable('getData');
        $.each(assignRoles, function (i, assignRole) {
          assignRole.sortOrder = i;
          if (StringUtils.isBlank(assignRole.orgNames)) {
            assignRole.orgIds = '';
          }
        });
        return assignRoles;
      }

      // 设置到分配的权限列表
      function toFolderAssignRoleList(configuration) {
        var $folderAssignRoleList = $(folder_assign_role_list_selector);
        // 清空字典列表
        var assignRoles = configuration['assignRoles'] || [];
        for (var index = 0; index < assignRoles.length; index++) {
          $folderAssignRoleList.bootstrapTable('insertRow', {
            index: 0,
            row: assignRoles[index]
          });
        }
      }

      function initTable() {
        var _self = this;
        var roleEditOptionValues = [
          {
            value: '',
            text: ''
          }
        ];
        JDS.call({
          service: 'dmsRoleMgr.getAll',
          async: false,
          version: '',
          success: function (reuslt) {
            var data = reuslt.data;
            $.each(data, function (i, dmsRole) {
              roleEditOptionValues.push({
                value: dmsRole.uuid,
                text: dmsRole.name
              });
            });
          }
        });
        $('#dms_folder_assign_role_list').bootstrapTable({
          data: [],
          striped: false,
          idField: 'uuid',
          uniqueId: 'uuid',
          width: 500,
          undefinedText: 'empty',
          columns: [
            {
              checkbox: true
            },
            {
              field: 'uuid',
              title: 'uuid',
              visible: false
            },
            {
              field: 'roleUuid',
              title: '文件操作权限',
              editable: {
                type: 'select',
                showbuttons: true,
                onblur: 'submit',
                mode: 'inline',
                source: roleEditOptionValues
              }
            },
            {
              field: 'roleName',
              title: '文件操作权限UUID',
              visible: false
            },
            {
              field: 'orgNames',
              title: '人员',
              editable: {
                type: 'text',
                showbuttons: true,
                onblur: 'ignore',
                mode: 'inline'
              }
            },
            {
              field: 'orgIds',
              title: '人员ID',
              visible: false
            },
            {
              field: 'permit',
              title: '允许',
              editable: {
                onblur: 'submit',
                type: 'select',
                mode: 'inline',
                showbuttons: false,
                source: [
                  {
                    value: 'E',
                    text: ''
                  },
                  {
                    value: 'Y',
                    text: '是'
                  },
                  {
                    value: 'N',
                    text: '否'
                  }
                ]
              }
            },
            {
              field: 'permitName',
              title: '允许真实值',
              visible: false
            },
            {
              field: 'deny',
              title: '拒绝',
              editable: {
                onblur: 'submit',
                type: 'select',
                mode: 'inline',
                showbuttons: false,
                source: [
                  {
                    value: 'E',
                    text: ''
                  },
                  {
                    value: 'Y',
                    text: '是'
                  },
                  {
                    value: 'N',
                    text: '否'
                  }
                ]
              }
            },
            {
              field: 'denyName',
              title: '拒绝真实值',
              visible: false
            }
          ],
          onEditableInit: function () {},
          onClickCell: function (field, value, row, $element, e) {
            if (field == 'orgNames' && $(e.target)[0].tagName == 'A') {
              $('#userName').val(value);
              $('#userId').val(row.orgIds);
              $.unit2.open({
                labelField: 'userName',
                valueField: 'userId',
                title: '选择人员',
                selectTypes: 'all',
                type: 'all',
                valueFormat: 'justId',
                callback: function (ids, names) {
                  value = $('#userName').val();
                  $element.find('input').val($('#userName').val()).blur();
                  row.orgIds = $('#userId').val();
                  row.orgNames = $('#userName').val();
                }
              });
            } else if (field == 'permit') {
              setVals($element, row, 'permitName');
            } else if (field == 'deny') {
              setVals($element, row, 'denyName');
            } else if (field == 'roleUuid') {
              setVals($element, row, 'roleName');
            }
          }
        });
      }

      function setVals(ele, row, field1) {
        $('.input-sm', ele).on('change', function () {
          var val = $(this).val();
          row[field1] = $(this)
            .find("option[value='" + val + "']")
            .text();
        });
      }
    },
    refresh: function () {
      this.init();
    }
  });
  return AppFolderDefWidgetDevelopment;
});
