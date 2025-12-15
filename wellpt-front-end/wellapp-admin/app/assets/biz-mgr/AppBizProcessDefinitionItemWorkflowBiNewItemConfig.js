define(['constant', 'commons', 'server', 'appContext', 'appModal', 'formBuilder', 'AppPtMgrCommons'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  formBuilder,
  AppPtMgrCommons
) {
  var UUID = commons.UUID;
  var StringUtils = commons.StringUtils;
  var JDS = server.JDS;
  var Validation = server.Validation;
  var SelectiveDatas = server.SelectiveDatas;

  // 平台管理_业务流程管理_业务流程定义_业务事项工作流集成里程碑配置
  var AppBizProcessDefinitionItemWorkflowBiNewItemConfig = {};

  AppBizProcessDefinitionItemWorkflowBiNewItemConfig.show = function (options) {
    var _self = this;
    var newItemConfig = options.newItemConfig || {};
    var dlgId = UUID.createUUID();
    var dlgSelector = '#' + dlgId;
    var message = "<div id='" + dlgId + "'></div>";
    var dlgOptions = {
      title: '发起业务事项配置',
      size: 'large',
      message: message,
      shown: function () {
        // 发起方式
        formBuilder.buildRadio({
          label: '发起方式',
          name: 'startItemWay',
          value: newItemConfig.startItemWay || '1',
          placeholder: '',
          container: dlgSelector,
          items: [
            {
              id: '1',
              text: '流程办结时发起'
            },
            {
              id: '2',
              text: '流向流转时发起'
            }
          ],
          events: {
            change: function () {
              var startItemWay = $(this).val();
              $('.start-item-way', dlgSelector).hide();
              $('.start-item-way-' + startItemWay, dlgSelector).show();
            }
          }
        });
        // 发起流向
        formBuilder.buildSelect2({
          select2: {
            data: options.flowBizSelectData.directions || [],
            remoteSearch: false
          },
          label: '发起流向',
          name: 'startItemDirectionId',
          value: newItemConfig.startItemDirectionId || '',
          display: 'startItemDirectionName',
          displayValue: newItemConfig.startItemDirectionName || '',
          divClass: 'start-item-way start-item-way-2',
          placeholder: '',
          container: dlgSelector
        });
        // 发起事项
        _self.buildStartItem(newItemConfig, options.processDefinition, dlgSelector);
        // 流程单据数据
        formBuilder.buildRadio({
          label: '流程单据数据',
          name: 'flowFormDataType',
          value: newItemConfig.formDataType || '1',
          placeholder: '',
          container: dlgSelector,
          items: [
            {
              id: '1',
              text: '使用源流程数据'
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
          value: newItemConfig.copyBotRuleId,
          divClass: 'form-data-type form-data-type-2',
          placeholder: '',
          container: dlgSelector
        });

        // 流程集成提交环节
        formBuilder.buildSelect2({
          select2: {
            data: [],
            remoteSearch: false
          },
          label: '流程集成提交环节',
          name: 'toTaskId',
          value: newItemConfig.toTaskId || '',
          divClass: 'workflow-bi',
          placeholder: '',
          container: dlgSelector
        });
        // 办理人来源
        formBuilder.buildRadio({
          label: '办理人来源',
          name: 'taskUserSource',
          value: newItemConfig.taskUserSource || '1',
          divClass: 'workflow-bi',
          placeholder: '',
          container: dlgSelector,
          items: [
            {
              id: '1',
              text: '按流程定义配置'
            },
            {
              id: '2',
              text: '现在指定'
            }
          ],
          events: {
            change: function () {
              var taskUserSource = $(this).val();
              $('.task-user-source', dlgSelector).hide();
              $('.task-user-source-' + taskUserSource, dlgSelector).show();
              if (taskUserSource == '2') {
                $('input[name="taskUserType"]:checked', dlgSelector).trigger('change');
              }
            }
          }
        });
        // 办理人类型
        formBuilder.buildRadio({
          label: '办理人类型',
          name: 'taskUserType',
          value: newItemConfig.taskUserType || '1',
          placeholder: '',
          divClass: 'task-user-source task-user-source-2',
          container: dlgSelector,
          items: [
            {
              id: '1',
              text: '组织机构'
            },
            {
              id: '4',
              text: '源流程历史环节办理人'
            },
            {
              id: '8',
              text: '人员选项'
            }
          ],
          events: {
            change: function () {
              var taskUserType = $(this).val();
              $('.task-user-type', dlgSelector).hide();
              $('.task-user-type-' + taskUserType, dlgSelector).show();
            }
          }
        });
        var taskUserName = newItemConfig.taskUserName || '';
        var taskUserId = newItemConfig.taskUserId || '';
        // 组织机构
        formBuilder.buildUnit({
          label: '组织机构',
          name: 'taskUserId1',
          value: taskUserId,
          display: 'taskUserName1',
          displayValue: taskUserName,
          divClass: 'task-user-source task-user-source-2 task-user-type task-user-type-1',
          placeholder: '',
          container: dlgSelector
        });
        // 办理环节
        formBuilder.buildSelect2({
          select2: {
            data: options.flowBizSelectData.taskIds,
            multiple: true,
            remoteSearch: false
          },
          label: '办理环节',
          name: 'taskUserId2',
          value: taskUserId,
          display: 'taskUserName2',
          displayValue: taskUserName,
          divClass: 'task-user-source task-user-source-2 task-user-type task-user-type-4',
          placeholder: '',
          container: dlgSelector
        });
        // 人员选项
        formBuilder.buildCheckbox({
          label: '人员选项',
          name: 'taskUserId3',
          value: taskUserId.split(';'),
          divClass: 'task-user-source task-user-source-2 task-user-type task-user-type-8',
          placeholder: '',
          container: dlgSelector,
          items: [
            {
              id: 'PriorUser',
              text: '当前用户'
            },
            {
              id: 'Creator',
              text: '源流程申请人'
            }
          ]
        });
        $('input[name="startItemWay"]:checked', dlgSelector).trigger('change');
        $('input[name="startItemId"]', dlgSelector).trigger('change');
        $('input[name="flowFormDataType"]:checked', dlgSelector).trigger('change');
        $('input[name="taskUserType"]:checked', dlgSelector).trigger('change');
        $('input[name="taskUserSource"]:checked', dlgSelector).trigger('change');
      },
      buttons: {
        canfirm: {
          label: '确定',
          className: 'well-btn w-btn-primary',
          callback: function () {
            var startItemWay = $('input[name="startItemWay"]:checked', dlgSelector).val();
            var startItemDirectionId = $('#startItemDirectionId', dlgSelector).val();
            var startItemDirectionName = $('#startItemDirectionName', dlgSelector).val();
            var startItemId = $('#startItemId', dlgSelector).val();
            var startItemName = $('#startItemName', dlgSelector).val();
            var formDataType = $('input[name="flowFormDataType"]:checked', dlgSelector).val();
            var copyBotRuleId = $('#copyBotRuleId', dlgSelector).val();
            var toTaskId = $('#toTaskId', dlgSelector).val();
            var taskUserSource = $('input[name="taskUserSource"]:checked', dlgSelector).val();
            var taskUserType = $('input[name="taskUserType"]:checked', dlgSelector).val();
            var taskUserId = '';
            var taskUserName = '';
            var taskUserId1 = $('input[name="taskUserId1"]', dlgSelector).val();
            var taskUserName1 = $('input[name="taskUserName1"]', dlgSelector).val();
            var taskUserId2 = $('#taskUserId2', dlgSelector).val();
            var taskUserName2 = $('#taskUserName2', dlgSelector).val();
            var $taskUserId3 = $('input[name="taskUserId3"]:checked', dlgSelector);
            var taskUserId3s = [];
            var taskUserName3s = [];
            $.each($taskUserId3, function () {
              var val = $(this).val();
              taskUserId3s.push(val);
              if (val == 'PriorUser') {
                taskUserName3s.push('当前用户');
              } else if (val == 'Creator') {
                taskUserName3s.push('源流程申请人');
              }
            });
            if (taskUserType == '1') {
              taskUserId = taskUserId1;
              taskUserName = taskUserName1;
            } else if (taskUserType == '4') {
              taskUserId = taskUserId2;
              taskUserName = taskUserName2;
            } else {
              taskUserId = taskUserId3s.join(';');
              taskUserName = taskUserName3s.join(';');
            }
            if (StringUtils.isBlank(startItemId)) {
              appModal.error('事项名称不能为空！');
              return false;
            }
            newItemConfig = $.extend(true, newItemConfig, {
              startItemWay,
              startItemDirectionId,
              startItemDirectionName,
              startItemId,
              startItemName,
              formDataType,
              copyBotRuleId,
              toTaskId,
              taskUserSource,
              taskUserType,
              taskUserId,
              taskUserName
            });
            if (options.onOk) {
              options.onOk.call(_self, newItemConfig);
            }
          }
        },
        cancel: {
          label: '取消',
          className: 'btn btn-default'
        }
      }
    };
    appModal.dialog(dlgOptions);
  };
  AppBizProcessDefinitionItemWorkflowBiNewItemConfig.buildStartItem = function (newItemConfig, processDefinition, dlgSelector) {
    var _self = this;
    var items = _self.getProcessItems(processDefinition);
    formBuilder.buildSelect2({
      select2: {
        data: $.map(items, function (item) {
          return { id: item.id, text: item.itemName };
        }),
        remoteSearch: false
      },
      label: '事项名称',
      name: 'startItemId',
      value: newItemConfig.startItemId,
      display: 'startItemName',
      displayValue: newItemConfig.startItemName,
      labelClass: 'required',
      placeholder: '',
      container: dlgSelector,
      events: {
        change: function () {
          var startItemId = $(this).val();
          var flowBizDefId = _self.getFlowBizDefIdByItemId(startItemId, items);
          if (StringUtils.isNotBlank(flowBizDefId)) {
            $('.workflow-bi', dlgSelector).show();
            JDS.restfulGet({
              url: `/proxy/api/workflow/business/definition/getSelectDataByFlowBizDefId/${flowBizDefId}`,
              success: function (result) {
                _self.initProcessItemFlowBizSelectFields(result.data);
              }
            });
          } else {
            $('.workflow-bi', dlgSelector).hide();
            _self.initProcessItemFlowBizSelectFields({ formFields: [], taskIds: [], directions: [] });
          }
        }
      }
    });
  };
  AppBizProcessDefinitionItemWorkflowBiNewItemConfig.initProcessItemFlowBizSelectFields = function (selectData, $container) {
    // 流程集成提交环节
    var toTaskIds = [{ id: 'AUTO_SUBMIT', text: '自动提交' }].concat(selectData.taskIds);
    $('#toTaskId', $container).wSelect2({
      data: toTaskIds,
      placeholder: '请选择',
      multiple: false,
      remoteSearch: false
    });
  };
  AppBizProcessDefinitionItemWorkflowBiNewItemConfig.getProcessNodes = function (processDefinition) {
    var processNodes = [];
    var getNodes = function (nodes, processNodes) {
      for (var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        processNodes.push(node);
        if (node.nodes) {
          getNodes(node.nodes, processNodes);
        }
      }
    };
    getNodes(processDefinition.nodes || [], processNodes);
    return processNodes;
  };
  AppBizProcessDefinitionItemWorkflowBiNewItemConfig.getProcessItems = function (processsDefinition) {
    var processItems = [];
    var getItems = function (nodes, processItems) {
      for (var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        var items = node.items || [];
        for (var j = 0; j < items.length; j++) {
          processItems.push(items[j]);
        }
        if (node.nodes) {
          getItems(node.nodes, processItems);
        }
      }
    };
    getItems(processsDefinition.nodes || [], processItems);
    return processItems;
  };
  AppBizProcessDefinitionItemWorkflowBiNewItemConfig.getFlowBizDefIdByItemId = function (itemId, items) {
    var _self = this;
    var flowIntegration = _self.getFlowIntegrationByItemId(itemId, items);
    if (flowIntegration == null) {
      return null;
    }
    return flowIntegration.flowBizDefId;
  };
  AppBizProcessDefinitionItemWorkflowBiNewItemConfig.getFlowIntegrationByItemId = function (itemId, items) {
    var _self = this;
    var item = _self.getItemById(itemId, items);
    if (item == null) {
      return null;
    }
    var businessIntegrationConfigs = item.businessIntegrationConfigs || [];
    for (var i = 0; i < businessIntegrationConfigs.length; i++) {
      if (businessIntegrationConfigs[i].type == '1') {
        return businessIntegrationConfigs[i];
      }
    }
    return null;
  };
  AppBizProcessDefinitionItemWorkflowBiNewItemConfig.getItemById = function (itemId, items) {
    for (var i = 0; i < items.length; i++) {
      if (itemId == items[i].id) {
        return items[i];
      }
    }
    return null;
  };
  return AppBizProcessDefinitionItemWorkflowBiNewItemConfig;
});
