define([
  'constant',
  'commons',
  'server',
  'appContext',
  'appModal',
  'AppPtMgrListViewWidgetDevelopment',
  'AppPtMgrCommons',
  'AppBizProcessDefinitionItemWorkflowBiMilestoneConfig'
], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  AppPtMgrListViewWidgetDevelopment,
  AppPtMgrCommons,
  workflowBiMilestoneConfig
) {
  var StringUtils = commons.StringUtils;
  var UUID = commons.UUID;
  var JDS = server.JDS;

  // 平台管理_业务流程管理_业务流程配置项模板_视图组件二开
  var AppBizDefinitionTemplateListViewWidgetDevelopment = function () {
    AppPtMgrListViewWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppBizDefinitionTemplateListViewWidgetDevelopment, AppPtMgrListViewWidgetDevelopment, {
    // 组件准备
    prepare: function () {},

    beforeRender: function (options, configuration) {},

    // 加载数据前回调方法
    beforeLoadData: function (options, configuration, request) {
      var _self = this;
      // 流程定义UUID条件过滤
      var processDefUuid = options.widgetDefinition.params.processDefUuid;
      if (StringUtils.isNotBlank(processDefUuid)) {
        _self.clearOtherConditions();
        _self.addOtherConditions([{ columnIndex: 'processDefUuid', type: 'eq', value: processDefUuid }]);
      }
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

    buildTemplateNameInput: function (value, dlgSelector) {
      formBuilder.buildInput({
        label: '模板名称',
        name: 'name',
        value: value,
        labelClass: 'required',
        placeholder: '',
        container: dlgSelector
      });
    },

    buildFormUuidSelect: function (value, dlgSelector, changeCallback) {
      formBuilder.buildSelect2({
        select2: {
          serviceName: 'bizProcessDefinitionFacadeService',
          queryMethod: 'listDyFormDefinitionSelectData',
          selectionMethod: 'getDyFormDefinitionSelectDataByUuids',
          defaultBlank: true,
          remoteSearch: false
        },
        label: '使用表单',
        name: 'formUuid',
        value: value,
        placeholder: '',
        container: dlgSelector,
        events: {
          change: function () {
            changeCallback.call(this);
          }
        }
      });
    },

    buildEntityNameFieldSelect: function (value, dlgSelector) {
      formBuilder.buildSelect2({
        select2: {
          data: [],
          defaultBlank: true,
          remoteSearch: false
        },
        label: '业务主体名称字段',
        name: 'entityNameField',
        value: value,
        placeholder: '',
        container: dlgSelector
      });
    },

    buildEntityIdFieldSelect: function (value, dlgSelector) {
      formBuilder.buildSelect2({
        select2: {
          data: [],
          defaultBlank: true,
          remoteSearch: false
        },
        label: '业务主体ID字段',
        name: 'entityIdField',
        value: value,
        placeholder: '',
        container: dlgSelector
      });
    },

    // 新增业务流程表单配置模板
    btn_add_process_form_tpl: function (event, ui) {
      this.showProcessFormTemplateDialog({
        type: '10',
        processDefUuid: ui.viewOptions.widgetDefinition.params.processDefUuid
      });
    },

    showProcessFormTemplateDialog: function (template) {
      var _self = this;
      var templateBean = template || {};
      var definitionJson = StringUtils.isNotBlank(templateBean.definitionJson) ? JSON.parse(templateBean.definitionJson) : {};
      var dlgId = UUID.createUUID();
      var dlgSelector = '#' + dlgId;
      var message = "<div id='" + dlgId + "'></div>";
      var dlgOptions = {
        title: (StringUtils.isBlank(templateBean.uuid) ? '新增' : '编辑') + '业务流程表单配置模板',
        size: 'middle',
        message: message,
        shown: function () {
          _self.buildTemplateNameInput(templateBean.name, dlgSelector);

          _self.buildFormUuidSelect(definitionJson.formUuid, dlgSelector, function () {
            var formUuid = $(this).val();
            if (StringUtils.isNotBlank(formUuid)) {
              JDS.restfulGet({
                url: `/proxy/api/biz/process/definition/getFormDefinitionByFormUuid/${formUuid}`,
                success: function (result) {
                  var formDefinition = JSON.parse(result.data);
                  _self.initProcessFormFields(formDefinition, $(dlgSelector));
                }
              });
            } else {
              _self.initProcessFormFields({ fields: {}, subforms: {} }, $(dlgSelector));
            }
          });

          _self.buildEntityNameFieldSelect(definitionJson.entityNameField, dlgSelector);

          _self.buildEntityIdFieldSelect(definitionJson.entityIdField, dlgSelector);

          formBuilder.buildSelect2({
            select2: {
              data: [],
              defaultBlank: true,
              remoteSearch: false
            },
            label: '过程节点办理显示位置',
            name: 'processNodePlaceHolder',
            value: definitionJson.processNodePlaceHolder,
            placeholder: '',
            container: dlgSelector
          });
          if (definitionJson.formUuid) {
            $('#formUuid', dlgSelector).trigger('change');
          }
        },
        buttons: {
          canfirm: {
            label: '确定',
            className: 'well-btn w-btn-primary',
            callback: function () {
              var name = $('#name', dlgSelector).val();
              var formUuid = $('#formUuid', dlgSelector).val();
              var entityNameField = $('#entityNameField', dlgSelector).val();
              var entityIdField = $('#entityIdField', dlgSelector).val();
              var processNodePlaceHolder = $('#processNodePlaceHolder', dlgSelector).val();
              if (StringUtils.isBlank(name)) {
                appModal.error('模板名称不能为空！');
                return false;
              }
              templateBean.name = name;
              templateBean.definitionJson = JSON.stringify(
                $.extend(true, templateBean, {
                  formUuid,
                  entityNameField,
                  entityIdField,
                  processNodePlaceHolder
                })
              );
              JDS.restfulPost({
                url: '/proxy/api/biz/definition/template/save',
                data: templateBean,
                success: function (result) {
                  appModal.success('保存成功！');
                  _self.getWidget().refresh();
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
      appModal.dialog(dlgOptions);
    },

    initProcessFormFields: function (formDefinition, $container) {
      var _self = this;
      var fieldSelectData = _self.getFieldSelectData(formDefinition);
      var blockSelectData = _self.getBlockSelectData(formDefinition);

      // 业务主体名称字段
      $('#entityNameField', $container).wSelect2({
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        data: fieldSelectData,
        width: '100%',
        height: 250
      });
      // 业务主体ID字段
      $('#entityIdField', $container).wSelect2({
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        data: fieldSelectData,
        width: '100%',
        height: 250
      });
      // 过程结点办理显示位置
      $('#processNodePlaceHolder', $container).wSelect2({
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        data: blockSelectData,
        width: '100%',
        height: 250
      });
    },

    // 新增过程节点表单配置模板
    btn_add_process_node_form_tpl: function (event, ui) {
      this.showProcessNodeFormTemplateDialog({
        type: '20',
        processDefUuid: ui.viewOptions.widgetDefinition.params.processDefUuid
      });
    },

    showProcessNodeFormTemplateDialog: function (template) {
      var _self = this;
      var templateBean = template || {};
      var definitionJson = StringUtils.isNotBlank(templateBean.definitionJson) ? JSON.parse(templateBean.definitionJson) : {};
      var dlgId = UUID.createUUID();
      var dlgSelector = '#' + dlgId;
      var message = "<div id='" + dlgId + "'></div>";
      var dlgOptions = {
        title: (StringUtils.isBlank(templateBean.uuid) ? '新增' : '编辑') + '过程节点表单配置模板',
        size: 'middle',
        message: message,
        shown: function () {
          _self.buildTemplateNameInput(templateBean.name, dlgSelector);

          _self.buildFormUuidSelect(definitionJson.formUuid, dlgSelector, function () {
            var formUuid = $(this).val();
            if (StringUtils.isNotBlank(formUuid)) {
              JDS.restfulGet({
                url: `/proxy/api/biz/process/definition/getFormDefinitionByFormUuid/${formUuid}`,
                success: function (result) {
                  var formDefinition = JSON.parse(result.data);
                  _self.initProcessNodeFormFields(formDefinition, $(dlgSelector));
                }
              });
            } else {
              _self.initProcessNodeFormFields({ fields: {}, subforms: {} }, $(dlgSelector));
            }
          });

          _self.buildEntityNameFieldSelect(definitionJson.entityNameField, dlgSelector);

          _self.buildEntityIdFieldSelect(definitionJson.entityIdField, dlgSelector);

          formBuilder.buildSelect2({
            select2: {
              data: [],
              defaultBlank: true,
              remoteSearch: false
            },
            label: '事项办理显示位置',
            name: 'itemPlaceHolder',
            value: definitionJson.itemPlaceHolder,
            placeholder: '',
            container: dlgSelector
          });
          if (definitionJson.formUuid) {
            $('#formUuid', dlgSelector).trigger('change');
          }
        },
        buttons: {
          canfirm: {
            label: '确定',
            className: 'well-btn w-btn-primary',
            callback: function () {
              var name = $('#name', dlgSelector).val();
              var formUuid = $('#formUuid', dlgSelector).val();
              var entityNameField = $('#entityNameField', dlgSelector).val();
              var entityIdField = $('#entityIdField', dlgSelector).val();
              var itemPlaceHolder = $('#itemPlaceHolder', dlgSelector).val();
              if (StringUtils.isBlank(name)) {
                appModal.error('模板名称不能为空！');
                return false;
              }
              templateBean.name = name;
              templateBean.definitionJson = JSON.stringify(
                $.extend(true, templateBean, {
                  formUuid,
                  entityNameField,
                  entityIdField,
                  itemPlaceHolder
                })
              );
              JDS.restfulPost({
                url: '/proxy/api/biz/definition/template/save',
                data: templateBean,
                success: function (result) {
                  appModal.success('保存成功！');
                  _self.getWidget().refresh();
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
      appModal.dialog(dlgOptions);
    },

    initProcessNodeFormFields: function (formDefinition, $container) {
      var _self = this;
      var fieldSelectData = _self.getFieldSelectData(formDefinition);
      var blockSelectData = _self.getBlockSelectData(formDefinition);

      // 业务主体名称字段
      $('#entityNameField', $container).wSelect2({
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        data: fieldSelectData,
        width: '100%',
        height: 250
      });
      // 业务主体ID字段
      $('#entityIdField', $container).wSelect2({
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        data: fieldSelectData,
        width: '100%',
        height: 250
      });
      // 事项办理显示位置
      $('#itemPlaceHolder', $container).wSelect2({
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        data: blockSelectData,
        width: '100%',
        height: 250
      });
    },

    // 新增事项表单配置模板
    btn_add_item_form_tpl: function (event, ui) {
      this.showItemFormTemplateDialog({
        type: '30',
        processDefUuid: ui.viewOptions.widgetDefinition.params.processDefUuid
      });
    },

    showItemFormTemplateDialog: function (template) {
      var _self = this;
      var templateBean = template || {};
      var formName = '';
      var definitionJson = StringUtils.isNotBlank(templateBean.definitionJson) ? JSON.parse(templateBean.definitionJson) : {};
      var dlgId = UUID.createUUID();
      var dlgSelector = '#' + dlgId;
      var message = "<div id='" + dlgId + "'></div>";
      var dlgOptions = {
        title: (StringUtils.isBlank(templateBean.uuid) ? '新增' : '编辑') + '事项表单配置模板',
        size: 'middle',
        message: message,
        shown: function () {
          _self.buildTemplateNameInput(templateBean.name, dlgSelector);

          _self.buildFormUuidSelect(definitionJson.formUuid, dlgSelector, function () {
            var formUuid = $(this).val();
            if (StringUtils.isNotBlank(formUuid)) {
              JDS.restfulGet({
                url: `/proxy/api/biz/process/definition/getFormDefinitionByFormUuid/${formUuid}`,
                success: function (result) {
                  var formDefinition = JSON.parse(result.data);
                  formName = formDefinition.name;
                  _self.initItemFormFields(formDefinition, $(dlgSelector));
                }
              });
            } else {
              _self.initItemFormFields({ fields: {}, subforms: {} }, $(dlgSelector));
            }
          });

          formBuilder.buildSelect2({
            select2: {
              data: [],
              defaultBlank: true,
              remoteSearch: false
            },
            label: '事项名称字段',
            name: 'itemNameField',
            value: definitionJson.itemNameField,
            placeholder: '',
            container: dlgSelector
          });

          formBuilder.buildSelect2({
            select2: {
              data: [],
              defaultBlank: true,
              remoteSearch: false
            },
            label: '事项编码字段',
            name: 'itemCodeField',
            value: definitionJson.itemCodeField,
            placeholder: '',
            container: dlgSelector
          });

          _self.buildEntityNameFieldSelect(definitionJson.entityNameField, dlgSelector);

          _self.buildEntityIdFieldSelect(definitionJson.entityIdField, dlgSelector);

          formBuilder.buildSelect2({
            select2: {
              data: [],
              defaultBlank: true,
              remoteSearch: false
            },
            label: '时限字段',
            name: 'timeLimitField',
            value: definitionJson.timeLimitField,
            placeholder: '',
            container: dlgSelector
          });

          formBuilder.buildSelect2({
            select2: {
              data: [],
              defaultBlank: true,
              remoteSearch: false
            },
            label: '材料从表',
            name: 'materialSubformId',
            value: definitionJson.materialSubformId,
            placeholder: '',
            container: dlgSelector
          });

          formBuilder.buildSelect2({
            select2: {
              data: [],
              defaultBlank: true,
              remoteSearch: false
            },
            label: '材料名称字段',
            name: 'materialNameField',
            value: definitionJson.materialNameField,
            placeholder: '',
            container: dlgSelector
          });

          formBuilder.buildSelect2({
            select2: {
              data: [],
              defaultBlank: true,
              remoteSearch: false
            },
            label: '材料编码字段',
            name: 'materialCodeField',
            value: definitionJson.materialCodeField,
            placeholder: '',
            container: dlgSelector
          });

          formBuilder.buildSelect2({
            select2: {
              data: [],
              defaultBlank: true,
              remoteSearch: false
            },
            label: '材料是否必填字段',
            name: 'materialRequiredField',
            value: definitionJson.materialRequiredField,
            placeholder: '',
            container: dlgSelector
          });

          formBuilder.buildSelect2({
            select2: {
              data: [],
              defaultBlank: true,
              remoteSearch: false
            },
            label: '材料附件字段',
            name: 'materialFileField',
            value: definitionJson.materialFileField,
            placeholder: '',
            container: dlgSelector
          });

          if (definitionJson.formUuid) {
            $('#formUuid', dlgSelector).trigger('change');
          }
        },
        buttons: {
          canfirm: {
            label: '确定',
            className: 'well-btn w-btn-primary',
            callback: function () {
              var name = $('#name', dlgSelector).val();
              var formUuid = $('#formUuid', dlgSelector).val();
              var itemNameField = $('#itemNameField', dlgSelector).val();
              var itemCodeField = $('#itemCodeField', dlgSelector).val();
              var entityNameField = $('#entityNameField', dlgSelector).val();
              var entityIdField = $('#entityIdField', dlgSelector).val();
              var timeLimitField = $('#timeLimitField', dlgSelector).val();
              var materialSubformId = $('#materialSubformId', dlgSelector).val();
              var materialNameField = $('#materialNameField', dlgSelector).val();
              var materialCodeField = $('#materialCodeField', dlgSelector).val();
              var materialRequiredField = $('#materialRequiredField', dlgSelector).val();
              var materialFileField = $('#materialFileField', dlgSelector).val();
              if (StringUtils.isBlank(name)) {
                appModal.error('模板名称不能为空！');
                return false;
              }
              templateBean.name = name;
              templateBean.definitionJson = JSON.stringify(
                $.extend(true, templateBean, {
                  formUuid,
                  formName,
                  itemNameField,
                  itemCodeField,
                  entityNameField,
                  entityIdField,
                  timeLimitField,
                  materialSubformId,
                  materialNameField,
                  materialCodeField,
                  materialRequiredField,
                  materialFileField
                })
              );
              JDS.restfulPost({
                url: '/proxy/api/biz/definition/template/save',
                data: templateBean,
                success: function (result) {
                  appModal.success('保存成功！');
                  _self.getWidget().refresh();
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
      appModal.dialog(dlgOptions);
    },

    initItemFormFields: function (formDefinition, $container) {
      var _self = this;
      var fieldSelectData = _self.getFieldSelectData(formDefinition);
      var subformSelectData = _self.getSubformSelectData(formDefinition);

      // 事项名称字段
      $('#itemNameField', $container).wSelect2({
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        data: fieldSelectData,
        width: '100%',
        height: 250
      });
      // 事项编码字段
      $('#itemCodeField', $container).wSelect2({
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        data: fieldSelectData,
        width: '100%',
        height: 250
      });
      // 业务主体名称字段
      $('#entityNameField', $container).wSelect2({
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        data: fieldSelectData,
        width: '100%',
        height: 250
      });
      // 业务主体ID字段
      $('#entityIdField', $container).wSelect2({
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        data: fieldSelectData,
        width: '100%',
        height: 250
      });
      // 办理时限字段
      $('#timeLimitField', $container).wSelect2({
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        data: fieldSelectData,
        width: '100%',
        height: 250
      });
      // 材料从表ID
      $('#materialSubformId', $container).wSelect2({
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        data: subformSelectData,
        width: '100%',
        height: 250
      });
      // 材料从表变更事件
      $('#materialSubformId', $container).off('change');
      $('#materialSubformId', $container)
        .on('change', function () {
          // 材料名称字段
          _self.onSubformIdChange(formDefinition, $(this).val(), 'materialNameField', $container);
          // 材料编码字段
          _self.onSubformIdChange(formDefinition, $(this).val(), 'materialCodeField', $container);
          // 材料是否必填字段
          _self.onSubformIdChange(formDefinition, $(this).val(), 'materialRequiredField');
          // 材料附件字段
          _self.onSubformIdChange(formDefinition, $(this).val(), 'materialFileField', $container);
        })
        .trigger('change');
    },

    // 新增事项集成工作流配置模板
    btn_add_item_workflow_form_tpl: function (event, ui) {
      this.showItemWorkflowTemplateDialog({
        type: '40',
        processDefUuid: ui.viewOptions.widgetDefinition.params.processDefUuid
      });
    },

    showItemWorkflowTemplateDialog: function (template) {
      var _self = this;
      var templateBean = template || {};
      var definitionJson = StringUtils.isNotBlank(templateBean.definitionJson) ? JSON.parse(templateBean.definitionJson) : {};
      var dlgId = UUID.createUUID();
      var dlgSelector = '#' + dlgId;
      var message = "<div id='" + dlgId + "'></div>";
      var dlgOptions = {
        title: (StringUtils.isBlank(templateBean.uuid) ? '新增' : '编辑') + '事项集成工作流配置模板',
        size: 'large',
        message: message,
        shown: function () {
          // 模板名称
          _self.buildTemplateNameInput(templateBean.name, dlgSelector);
          // 流程业务定义
          formBuilder.buildSelect2({
            select2: {
              serviceName: 'wfFlowBusinessDefinitionFacadeService',
              remoteSearch: false
            },
            label: '流程业务定义',
            name: 'flowBizDefId',
            value: definitionJson.flowBizDefId,
            placeholder: '',
            container: dlgSelector,
            events: {
              change: function () {
                var flowBizDefId = $(this).val();
                if (StringUtils.isNotBlank(flowBizDefId)) {
                  JDS.restfulGet({
                    url: `/proxy/api/workflow/business/definition/getSelectDataByFlowBizDefId/${flowBizDefId}`,
                    success: function (result) {
                      _self.initProcessItemFlowBizSelectFields(result.data, definitionJson.milestoneConfigs, dlgSelector);
                    }
                  });
                } else {
                  _self.initProcessItemFlowBizSelectFields(
                    { formFields: [], taskIds: [], directions: [] },
                    definitionJson.milestoneConfigs,
                    dlgSelector
                  );
                }
              }
            }
          });
          // 流程单据数据
          formBuilder.buildRadio({
            label: '流程单据数据',
            name: 'formDataType',
            value: definitionJson.formDataType || '1',
            placeholder: '',
            container: dlgSelector,
            items: [
              {
                id: '1',
                text: '使用事项表单数据'
              },
              {
                id: '2',
                text: '使用单据转换'
              }
            ],
            events: {
              change: function () {
                var formDataType = $(this).val();
                $('.form-data-type', dlgSelector).hide();
                $('.form-data-type-' + formDataType, dlgSelector).show();
                // 指定反馈流向
                if (formDataType == '2') {
                  $('input[name="returnType"]', dlgSelector).trigger('change');
                } else {
                  $('input[name="returnDirectionId"]', dlgSelector).closest('.form-group').hide();
                }
              }
            }
          });
          // 单据转换规则
          formBuilder.buildSelect2({
            select2: {
              serviceName: 'botRuleConfFacadeService',
              queryMethod: 'loadSelectData',
              remoteSearch: false
            },
            label: '单据转换规则',
            name: 'copyBotRuleId',
            value: definitionJson.copyBotRuleId,
            divClass: 'form-data-type form-data-type-2',
            placeholder: '',
            container: dlgSelector
          });
          // 单据反馈
          var returnTypes = [];
          if (definitionJson.returnWithOver) {
            returnTypes.push('returnWithOver');
          }
          if (definitionJson.returnWithDirection) {
            returnTypes.push('returnWithDirection');
          }
          formBuilder.buildCheckbox({
            label: '单据反馈',
            name: 'returnType',
            value: returnTypes,
            divClass: 'form-data-type form-data-type-2',
            placeholder: '',
            container: dlgSelector,
            items: [
              {
                id: 'returnWithOver',
                text: '办结时反馈'
              },
              {
                id: 'returnWithDirection',
                text: '指定反馈流向'
              }
            ],
            events: {
              change: function () {
                if ($('input[name="returnType"][value="returnWithDirection"]', dlgSelector)[0].checked) {
                  $('input[name="returnDirectionId"]', dlgSelector).closest('.form-group').show();
                } else {
                  $('input[name="returnDirectionId"]', dlgSelector).closest('.form-group').hide();
                }
              }
            }
          });
          // 反馈流向
          formBuilder.buildSelect2({
            select2: {
              data: [],
              remoteSearch: false
            },
            label: '反馈流向',
            name: 'returnDirectionId',
            value: definitionJson.returnDirectionId,
            divClass: 'form-data-type form-data-type-2',
            placeholder: '',
            container: dlgSelector
          });
          // 反馈规则
          formBuilder.buildSelect2({
            select2: {
              serviceName: 'botRuleConfFacadeService',
              queryMethod: 'loadSelectData',
              remoteSearch: false
            },
            label: '反馈规则',
            name: 'returnBotRuleId',
            value: definitionJson.returnBotRuleId,
            divClass: 'form-data-type form-data-type-2',
            placeholder: '',
            container: dlgSelector
          });
          // 里程碑
          $(dlgSelector).append(
            $(
              '<div class="form-group formbuilder clear">\
                   <label class="col-sm-2 control-label">里程碑</label>\
                    <div class="col-sm-10 milestone-configs"></div>\
                  </div>'
            )
          );
          // 计时信息
          formBuilder.buildCheckbox({
            label: '计时信息',
            name: 'syncTimerInfo',
            value: [definitionJson.syncTimerInfo ? 'true' : 'false'],
            placeholder: '',
            container: dlgSelector,
            items: [
              {
                id: 'true',
                text: '同步计时信息'
              }
            ]
          });
          $('input[name="flowBizDefId"]', dlgSelector).trigger('change');
          $('input[name="formDataType"]:checked', dlgSelector).trigger('change');
          $('input[name="returnType"]', dlgSelector).trigger('change');
        },
        buttons: {
          canfirm: {
            label: '确定',
            className: 'well-btn w-btn-primary',
            callback: function () {
              var name = $('#name', dlgSelector).val();
              var flowBizDefId = $('#flowBizDefId', dlgSelector).val();
              var formDataType = $('input[name="formDataType"]:checked', dlgSelector).val();
              var copyBotRuleId = $('#copyBotRuleId', dlgSelector).val();
              var returnWithOver = $('input[name="returnType"][value="returnWithOver"]', dlgSelector)[0].checked;
              var returnWithDirection = $('input[name="returnType"][value="returnWithDirection"]', dlgSelector)[0].checked;
              var returnDirectionId = $('#returnDirectionId', dlgSelector).val();
              var returnBotRuleId = $('#returnBotRuleId', dlgSelector).val();
              var milestoneConfigs = workflowBiMilestoneConfig.collect('.milestone-configs', dlgSelector);
              var syncTimerInfo = $('input[name="syncTimerInfo"]', dlgSelector)[0].checked;
              if (StringUtils.isBlank(name)) {
                appModal.error('模板名称不能为空！');
                return false;
              }
              templateBean.name = name;
              templateBean.definitionJson = JSON.stringify(
                $.extend(true, templateBean, {
                  flowBizDefId,
                  formDataType,
                  copyBotRuleId,
                  returnWithOver,
                  returnWithDirection,
                  returnDirectionId,
                  returnBotRuleId,
                  milestoneConfigs,
                  syncTimerInfo
                })
              );
              JDS.restfulPost({
                url: '/proxy/api/biz/definition/template/save',
                data: templateBean,
                success: function (result) {
                  appModal.success('保存成功！');
                  _self.getWidget().refresh();
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
      appModal.dialog(dlgOptions);
    },

    initProcessItemFlowBizSelectFields: function (selectData, milestoneConfigs, $container) {
      var _self = this;
      var directionSelectData = selectData.directions || [];

      // 反馈流向
      $('#returnDirectionId', $container).wSelect2({
        data: directionSelectData,
        placeholder: '请选择',
        multiple: false,
        remoteSearch: false
      });

      // 工作流集成里程碑配置
      workflowBiMilestoneConfig.show($('.milestone-configs', $container), milestoneConfigs || [], selectData);
    },

    getFieldSelectData: function (formDefinition) {
      var selectData = [];
      var fields = formDefinition.fields || {};
      $.each(fields, function (i, field) {
        selectData.push({ id: field.name, text: field.displayName });
      });
      return selectData;
    },

    getSubformSelectData(formDefinition) {
      var selectData = [];
      var subforms = formDefinition.subforms || {};
      $.each(subforms, function (_i, subform) {
        selectData.push({ id: subform.outerId, text: subform.displayName });
      });
      return selectData;
    },

    getBlockSelectData(formDefinition) {
      var selectData = [];
      var blocks = formDefinition.blocks || {};
      $.each(blocks, function (i, block) {
        selectData.push({ id: block.blockCode, text: block.blockTitle });
      });
      return selectData;
    },

    onSubformIdChange: function (formDefinition, subformId, field, $container) {
      var _self = this;
      var subforms = formDefinition.subforms || {};
      var resetSelect2 = false;
      $.each(subforms, function (i, subform) {
        if (subform.outerId == subformId) {
          var fields = _self.getFieldSelectData(subform);
          $('#' + field, $container).wSelect2({
            defaultBlank: true, // 默认空选项
            remoteSearch: false,
            data: fields,
            width: '100%',
            height: 250
          });
          resetSelect2 = true;
        }
      });
      // 从表不存在，对应字段下拉置空
      if (!resetSelect2) {
        $('#' + field, $container).wSelect2({
          defaultBlank: true, // 默认空选项
          remoteSearch: false,
          data: [],
          width: '100%',
          height: 250
        });
      }
    },

    // 删除
    btn_delete: function (event, ui) {
      var _self = this;
      var rowData = _self.getSelectRowData(event);
      if (rowData.length > 0) {
        var name = rowData[0].name;
        appModal.confirm(`确认要删除模板[${name}]吗?`, function (result) {
          if (result) {
            JDS.restfulPost({
              url: '/proxy/api/biz/definition/template/deleteAll',
              traditional: true,
              data: {
                uuids: (function () {
                  var uuids = [];
                  for (var i = 0, len = rowData.length; i < len; i++) {
                    uuids.push(rowData[i].uuid);
                  }
                  return uuids;
                })()
              },
              contentType: 'application/x-www-form-urlencoded',
              success: function (result) {
                appModal.info('刪除成功');
                _self.getWidget().refresh(); //刷新表格
              }
            });
          }
        });
      } else {
        appModal.error('请选择记录！');
        return false;
      }
    },

    // 行双击查看详情
    onDblClickRow: function (rowNum, rowData, $element, field) {
      var _self = this;
      if (rowData.type == '10') {
        _self.showProcessFormTemplateDialog(rowData);
      } else if (rowData.type == '20') {
        _self.showProcessNodeFormTemplateDialog(rowData);
      } else if (rowData.type == '30') {
        _self.showItemFormTemplateDialog(rowData);
      } else if (rowData.type == '40') {
        _self.showItemWorkflowTemplateDialog(rowData);
      }
    }
  });
  return AppBizDefinitionTemplateListViewWidgetDevelopment;
});
