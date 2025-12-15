define(['constant', 'commons', 'server', 'appContext', 'appModal', 'AppPtMgrListViewWidgetDevelopment', 'AppPtMgrCommons'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  AppPtMgrListViewWidgetDevelopment,
  AppPtMgrCommons
) {
  var StringUtils = commons.StringUtils;
  var UUID = commons.UUID;
  var JDS = server.JDS;

  // 平台管理_业务流程管理_业务流程定义列表_视图组件二开
  var AppBizProcessDefinitionListViewWidgetDevelopment = function () {
    AppPtMgrListViewWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppBizProcessDefinitionListViewWidgetDevelopment, AppPtMgrListViewWidgetDevelopment, {
    // 组件准备
    prepare: function () {},

    beforeRender: function (options, configuration) {
      // 归属模块ID
      this.widget.addParam('moduleId', this._moduleId());
    },

    _moduleId: function () {
      return this.getWidgetParams().moduleId;
    },

    getSelectRowData: function (event) {
      var _self = this;
      var $toolbarDiv = $(event.target).closest('div');
      var rowData = [];
      if ($toolbarDiv.is('.div_lineEnd_toolbar')) {
        //行级点击操作
        var index = $toolbarDiv.attr('index');
        var allData = _self.getData();
        rowData = [allData[index]];
      } else {
        if (_self.getSelectionIndexes().length == 0) {
          return [];
        }
        rowData = _self.getSelections();
      }
      return rowData;
    },

    // 新增
    btn_add: function (_event, ui) {
      this.showProcessDefinitionDialog({});
    },

    // 编辑
    btn_edit: function (event, ui) {
      var _self = this;
      var selection = _self.getSelectRowData(event);
      if (selection.length != 1) {
        appModal.error('请选择一条记录！');
      }
      var rowData = selection[0];
      JDS.restfulGet({
        url: ctx + `/proxy/api/biz/process/definition/get/${rowData.uuid}`,
        success: function (result) {
          var processDefinition = result.data;
          _self.showProcessDefinitionDialog(processDefinition);
        }
      });
    },

    // 复制
    btn_copy: function (event, ui) {
      var _self = this;
      var selection = _self.getSelectRowData(event);
      if (selection.length != 1) {
        appModal.error('请选择一条记录！');
        return;
      }
      var rowData = selection[0];
      _self.showCopyProcessDefinitionDialog(rowData);
    },

    showCopyProcessDefinitionDialog: function (rowData) {
      var _self = this;
      // 验证输入
      var checkInputValues = function () {
        var $container = $('.dlg_copy_definition');
        var newProcessDefName = $.trim($("input[name='new_process_def_name']", $container).val());
        var newProcessDefId = $.trim($("input[name='new_process_def_id']", $container).val());
        if (StringUtils.isBlank(newProcessDefName)) {
          appModal.error('业务流程名称不能为空！');
          return false;
        }
        if (StringUtils.isBlank(newProcessDefId)) {
          appModal.error('业务流程ID不能为空！');
          return false;
        }
        return true;
      };
      // 复制处理
      var copyFlowDefinition = function (successCallback) {
        var $container = $('.dlg_copy_definition');
        var newProcessDefName = $.trim($("input[name='new_process_def_name']", $container).val());
        var newProcessDefId = $.trim($("input[name='new_process_def_id']", $container).val());
        var copyResult = false;
        JDS.call({
          service: 'bizProcessDefinitionFacadeService.copy',
          data: [rowData.uuid, newProcessDefName, newProcessDefId],
          async: false,
          success: function () {
            if ($.isFunction(successCallback)) {
              successCallback.apply(this, arguments);
            } else {
              appModal.success('复制成功！');
            }
            copyResult = true;
            _self.refresh();
          }
        });
        return copyResult;
      };
      appModal.dialog({
        title: '复制业务流程定义',
        message: "<div class='dlg_copy_definition'></div>",
        shown: function () {
          var $container = $('.dlg_copy_definition');
          formBuilder.buildLabel({
            container: $container,
            labelColSpan: '12',
            label: '请输入新业务流程的名称和ID：'
          });
          formBuilder.buildInput({
            container: $container,
            labelColSpan: '3',
            controlColSpan: '9',
            isRequired: true,
            label: '业务流程名称',
            name: 'new_process_def_name',
            value: rowData.name + ' - 副本'
          });
          formBuilder.buildInput({
            container: $container,
            labelColSpan: '3',
            controlColSpan: '9',
            isRequired: true,
            label: '业务流程ID',
            name: 'new_process_def_id',
            value: rowData.id
          });
        },
        buttons: {
          confirm: {
            label: '保存',
            className: 'well-btn w-btn-primary',
            callback: function () {
              if (!checkInputValues()) {
                return false;
              }
              return copyFlowDefinition();
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default',
            callback: function () {}
          }
        }
      });
    },

    // 删除
    btn_delete: function (event, ui) {
      var _self = this;
      var rowData = _self.getSelectRowData(event);
      if (rowData.length == 1) {
        var name = rowData[0].name;
        appModal.confirm(`确认要删除业务流程定义[${name}]吗?`, function (result) {
          if (result) {
            server.JDS.restfulPost({
              url: ctx + '/proxy/api/biz/process/definition/deleteAll',
              traditional: true,
              data: {
                uuids: [rowData[0].uuid]
              },
              contentType: 'application/x-www-form-urlencoded',
              success: function (result) {
                appModal.info('刪除成功');
                _self.refresh(); //刷新表格
              }
            });
          }
        });
      } else {
        appModal.error('请选择一条记录！');
        return false;
      }
    },

    // 行点击查看详情
    onClickRow: function (rowNum, rowData, $element, field) {
      // 触发应用列表行点击事件
      this.widget.trigger('AppBizProcessDefinitionListView.editRow', {
        rowData: rowData,
        ui: this.widget
      });
    },

    // 行双击的时候回调方法，子类可覆盖
    onDblClickRow: function (rowNum, rowData, $element, field) {
      var _self = this;
      JDS.restfulGet({
        url: ctx + `/proxy/api/biz/process/definition/get/${rowData.uuid}`,
        success: function (result) {
          var processDefinition = result.data;
          _self.showProcessDefinitionDialog(processDefinition);
        }
      });
    },

    definition_import: function () {
      var _self = this;
      // 定义导入
      $.iexportData['import']({
        callback: function () {
          _self.refresh();
        }
      });
    },

    definition_export: function (event, ui) {
      // 定义导出
      var rowData = this.getSelectRowData(event);
      if (rowData.length > 0) {
        $.iexportData['export']({
          uuid: rowData[0].uuid,
          type: 'bizProcessDefinition'
        });
      } else {
        appModal.alert('请选择导出的业务流程定义！');
      }
    },

    afterRender: function (options, configuration) {
      var _self = this;
      this.widget.on('AppBizProcessDefinitionListView.refresh', function () {
        _self.refresh();
      });
    },

    refresh: function (options) {
      return this.getWidget().refresh(options);
    },

    // 业务流程基本信息弹出框
    showProcessDefinitionDialog: function (processDefinition) {
      var _self = this;
      var processDefinitionBean = processDefinition || {};
      var dlgId = UUID.createUUID();
      var dlgSelector = '#' + dlgId;
      var message = "<div id='" + dlgId + "'></div>";
      var options = {
        title: StringUtils.isBlank(processDefinitionBean.uuid) ? '新增' : '编辑' + '业务流程',
        size: 'large',
        message: message,
        shown: function () {
          formBuilder.buildInput({
            label: '名称',
            name: 'name',
            value: processDefinitionBean.name || '',
            labelClass: 'required',
            placeholder: '',
            container: dlgSelector
          });
          formBuilder.buildInput({
            label: 'ID',
            name: 'id',
            value: processDefinitionBean.id || '',
            labelClass: 'required',
            placeholder: '',
            container: dlgSelector
          });
          formBuilder.buildInput({
            label: '编号',
            name: 'code',
            value: processDefinitionBean.code || '',
            placeholder: '',
            container: dlgSelector
          });
          var enabled = processDefinitionBean.enabled == null ? true : processDefinitionBean.enabled;
          formBuilder.buildRadio({
            label: '状态',
            name: 'enabled',
            value: enabled ? '1' : '0',
            placeholder: '',
            container: dlgSelector,
            items: [
              {
                id: '1',
                text: '启用'
              },
              {
                id: '0',
                text: '禁用'
              }
            ]
          });
          $(dlgSelector).append("<input id='businessId' name='businessId' type='hidden'></input>");
          formBuilder.buildWCommonComboTree({
            container: dlgSelector,
            label: '所属业务',
            name: 'businessName',
            value: processDefinitionBean.businessId || '',
            inputClass: 'w-custom-collect',
            wCommonComboTree: {
              service: 'bizBusinessFacadeService.getBusinessTree',
              multiSelect: false, // 是否多选
              parentSelect: false, // 父节点选择有效，默认无效
              onAfterSetValue: function (event, self, value) {
                $('#businessId', dlgSelector).val(value);
              },
              beforeTreeExpand: function (treeId, treeNode) {
                // 非业务节点，不可选择
                $.each(treeNode.children, function (i, child) {
                  if (child.type != 'business') {
                    child.nocheck = true;
                  }
                });
              }
            }
          });
          formBuilder.buildSelect2({
            select2: {
              serviceName: 'bizTagFacadeService',
              defaultBlank: true,
              remoteSearch: false
            },
            container: dlgSelector,
            name: 'tagId',
            value: processDefinitionBean.tagId || '',
            diaplay: 'tagName',
            label: '业务标签'
          });
          formBuilder.buildTextarea({
            label: '备注',
            name: 'remark',
            value: processDefinitionBean.remark || '',
            placeholder: '',
            rows: 6,
            container: dlgSelector
          });
        },
        buttons: {
          canfirm: {
            label: '确定',
            className: 'well-btn w-btn-primary',
            callback: function () {
              var name = $('#name', dlgSelector).val();
              var id = $('#id', dlgSelector).val();
              var code = $('#code', dlgSelector).val();
              var enabled = $('input[name="enabled"]:checked', dlgSelector).val();
              var businessId = $('#businessId', dlgSelector).val();
              var tagId = $('#tagId', dlgSelector).val();
              var remark = $('#remark', dlgSelector).val();
              if (StringUtils.isBlank(name)) {
                appModal.error('名称不能为空！');
                return false;
              }
              if (StringUtils.isBlank(id)) {
                appModal.error('ID不能为空！');
                return false;
              }
              processDefinitionBean.name = name;
              processDefinitionBean.id = id;
              processDefinitionBean.code = code;
              processDefinitionBean.enabled = enabled == '1';
              processDefinitionBean.businessId = businessId;
              processDefinitionBean.tagId = tagId;
              processDefinitionBean.remark = remark;
              JDS.restfulPost({
                url: '/proxy/api/biz/process/definition/save',
                data: processDefinitionBean,
                success: function (result) {
                  appModal.success('保存成功！');
                  _self.refresh();
                }
              });
            }
          },
          cancel: {
            label: '取消',
            className: 'btn btn-default'
          }
        }
      };
      appModal.dialog(options);
    }
  });
  return AppBizProcessDefinitionListViewWidgetDevelopment;
});
