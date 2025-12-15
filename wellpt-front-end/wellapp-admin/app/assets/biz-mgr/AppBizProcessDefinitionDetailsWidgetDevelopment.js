define([
  'constant',
  'commons',
  'server',
  'appContext',
  'appModal',
  'AppPtMgrDetailsWidgetDevelopment',
  'AppPtMgrCommons',
  'AppBizProcessDefinitionItemSituationConfig',
  'AppBizProcessDefinitionItemWorkflowBiMilestoneConfig'
], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  AppPtMgrDetailsWidgetDevelopment,
  AppPtMgrCommons,
  itemSituationConfig,
  workflowBiMilestoneConfig
) {
  var Browser = commons.Browser;
  var StringUtils = commons.StringUtils;
  var UUID = commons.UUID;
  var JDS = server.JDS;
  var idCounter = 1;
  var AppBizItemDefinitionDetailsWidgetDevelopment = function () {
    AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppBizItemDefinitionDetailsWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
    init: function () {
      var _self = this;
      var $element = _self.getWidgetElement();
      var processDefUuid = Browser.getQueryString('uuid');
      // 加载业务树
      _self.loadTree(processDefUuid);

      // 绑定事件
      _self.bindEvents();

      $('#process_definition_tree', $element).slimScroll({
        height: '600px',
        wheelStep: navigator.userAgent.indexOf('Firefox') > -1 ? 1 : 10
      });
    },
    refresh: function () {
      var _self = this;
      _self.init();
    },
    loadTree: function (processDefUuid) {
      var _self = this;
      _self.processDefUuid = processDefUuid;
      var $element = _self.getWidgetElement();
      var setting = {
        callback: {
          beforeClick: function (treeId, treeNode) {
            _self.onBeforeClickTree(treeNode);
            return true;
          }
        },
        view: {
          selectedMulti: false
        }
      };

      JDS.restfulGet({
        url: ctx + `/proxy/api/biz/process/definition/get/${processDefUuid}`,
        success: function (result) {
          var processDefinition = result.data;
          var treeNodes = _self.parseProcessTree(processDefinition);
          var zTree = $.fn.zTree.init($('#process_definition_tree', $element), setting, treeNodes);
          _self.zTree = zTree;
          var nodes = zTree.getNodes();
          // 默认展开第一个节点
          if (nodes.length > 0) {
            var node = nodes[0];
            zTree.expandNode(node, true, false, false, true);
            zTree.selectNode(node);
            _self.onBeforeClickTree(node);
          }
        }
      });
    },

    onBeforeClickTree: function (treeNode) {
      var _self = this;
      if (treeNode == null) {
        return;
      }
      // 收集最新的数据到上次点击的树节点
      _self.collectFormData2TreeNodeData();

      _self.latestTreeNode = treeNode;
      // 业务流程信息
      if (treeNode.type == 'process') {
        _self.showProcessInfo(treeNode);
      } else if (treeNode.type == 'node') {
        // 过程节点信息
        _self.showProcessNodeInfo(treeNode);
      } else if (treeNode.type == 'item') {
        // 业务事项信息
        _self.showProcessItemInfo(treeNode);
      }
    },

    // 解析业务流程树
    parseProcessTree: function (processInfo) {
      var _self = this;
      var processDefinition = _self.getProcessInfoBean(processInfo);
      var definitionJson = processInfo.definitionJson;
      if (StringUtils.isBlank(definitionJson)) {
        return [{ id: processInfo.uuid, name: processInfo.name, data: processDefinition, type: 'process' }];
      }

      return _self.parseProcessDefinitionTree(processDefinition);
    },

    parseProcessDefinitionTree: function (processDefinition) {
      var _self = this;
      var treeNode = {
        id: processDefinition.uuid,
        name: processDefinition.name,
        data: processDefinition,
        type: 'process'
      };
      var nodes = processDefinition.nodes || [];
      var children = [];
      for (var i = 0; i < nodes.length; i++) {
        var child = _self.parseProcessNodeDefinition(nodes[i]);
        children.push(child);
      }
      treeNode.children = children;
      return [treeNode];
    },

    parseProcessNodeDefinition: function (nodeDefinition) {
      var _self = this;
      var treeNode = { id: nodeDefinition.id, name: nodeDefinition.name, data: nodeDefinition, type: 'node' };
      var children = [];
      var nodes = nodeDefinition.nodes || [];
      var items = nodeDefinition.items || [];
      for (var i = 0; i < nodes.length; i++) {
        children.push(_self.parseProcessNodeDefinition(nodes[i]));
      }
      for (var j = 0; j < items.length; j++) {
        children.push(_self.parseProcessItemDefinition(items[j]));
      }
      treeNode.children = children;
      return treeNode;
    },

    parseProcessItemDefinition: function (itemDefinition) {
      itemDefinition.itemMilestone = itemDefinition.milestone;
      return { id: itemDefinition.itemCode, name: itemDefinition.itemName, data: itemDefinition, type: 'item' };
    },

    collectFormData2TreeNodeData: function () {
      var _self = this;
      var treeNode = _self.latestTreeNode;
      if (treeNode == null) {
        return;
      }
      if (treeNode.type == 'process') {
        _self.collectProcessInfo2TreeNode(treeNode);
      } else if (treeNode.type == 'node') {
        // 过程节点信息
        _self.collectProcessNodeInfo2TreeNode(treeNode);
      } else if (treeNode.type == 'item') {
        // 业务事项信息
        _self.collectProcessItemInfo2TreeNode(treeNode);
      }
    },

    collectProcessInfo2TreeNode: function (treeNode) {
      var _self = this;
      var processBean = _self.collectProcessInfo();
      treeNode.data = processBean;
      treeNode.name = processBean.name;
      treeNode.id = processBean.id;
      _self.zTree.updateNode(treeNode);
    },

    collectProcessInfo: function () {
      var _self = this;
      var $processContainer = $('#process_definition_info', _self.getWidgetElement());
      var processBean = _self._processBean();
      AppPtMgrCommons.form2json({
        json: processBean,
        container: $processContainer
      });
      processBean.enabled = processBean.enabled == '1' ? true : false;
      // 表单设置
      var formConfig = _self._processFormConfigBean();
      AppPtMgrCommons.form2json({
        json: formConfig,
        container: $processContainer
      });
      formConfig.configType = formConfig.processFormConfigType;
      processBean.formConfig = formConfig;
      return processBean;
    },

    collectProcessNodeInfo2TreeNode: function (treeNode) {
      var _self = this;
      var processNodeBean = _self.collectProcessNodeInfo();
      treeNode.data = processNodeBean;
      treeNode.name = processNodeBean.name;
      treeNode.id = processNodeBean.id;
      _self.zTree.updateNode(treeNode);
    },

    collectProcessNodeInfo: function () {
      var _self = this;
      var $processContainer = $('#process_node_info', _self.getWidgetElement());
      var processNodeBean = _self._processNodeBean();
      AppPtMgrCommons.form2json({
        json: processNodeBean,
        container: $processContainer
      });
      processNodeBean.milestone = processNodeBean.milestone == '1' ? true : false;
      // 状态条件
      var stateConditions = [];
      $("select[name='stateConditions']", $processContainer)
        .find('option')
        .each(function () {
          stateConditions.push($(this).data('condition'));
        });
      processNodeBean.stateConditions = stateConditions;
      // 表单设置
      var formConfig = _self._processNodeFormConfigBean();
      AppPtMgrCommons.form2json({
        json: formConfig,
        container: $processContainer
      });
      formConfig.configType = formConfig.processNodeFormConfigType;
      processNodeBean.formConfig = formConfig;
      return processNodeBean;
    },

    collectProcessItemInfo2TreeNode: function (treeNode) {
      var _self = this;
      var processItemBean = _self.collectProcessItemInfo();
      treeNode.data.situationConfigs = itemSituationConfig.collect($('.enabled-situation'));
      treeNode.data.businessIntegrationConfigs = _self.collectProcessItemBusinessIntegrationConfigs();
      processItemBean.dispenseFormConfigs = processItemBean.dispenseItem ? treeNode.data.dispenseFormConfigs || [] : [];
      processItemBean.situationConfigs = treeNode.data.situationConfigs || [];
      processItemBean.businessIntegrationConfigs = treeNode.data.businessIntegrationConfigs || [];
      treeNode.data = processItemBean;
      treeNode.name = processItemBean.itemName;
      treeNode.id = processItemBean.itemCode;
      _self.zTree.updateNode(treeNode);
    },

    collectProcessItemInfo: function () {
      var _self = this;
      var $processContainer = $('#process_item_info', _self.getWidgetElement());
      var processItemBean = _self._processItemBean();
      AppPtMgrCommons.form2json({
        json: processItemBean,
        container: $processContainer
      });
      processItemBean.mandatory = processItemBean.mandatory == '1' ? true : false;
      processItemBean.itemMilestone = processItemBean.itemMilestone == '1' ? true : false;
      processItemBean.milestone = processItemBean.itemMilestone;
      processItemBean.dispenseItem = processItemBean.dispenseItem == '1' ? true : false;
      processItemBean.enabledSituation = processItemBean.enabledSituation == '1' ? true : false;

      // 表单设置
      var formConfig = _self._processItemFormConfigBean();
      AppPtMgrCommons.form2json({
        json: formConfig,
        container: $('#process_item_info_form', $processContainer)
      });
      formConfig.configType = formConfig.itemFormConfigType;
      processItemBean.formConfig = formConfig;
      return processItemBean;
    },

    collectProcessItemBusinessIntegrationConfigs: function () {
      var _self = this;
      var $processItemContainer = $('#process_item_info', _self.getWidgetElement());
      var biConfigs = [];
      var $checkedTypes = $('input[name="type"]:checked', $processItemContainer);
      $.each($checkedTypes, function () {
        var type = $(this).val();
        // 工作流集成
        if (type == '1') {
          var $biContainer = $('.bi-type-1', $processItemContainer);
          var workflowBiBean = _self._processItemWorkflowBiBean();
          AppPtMgrCommons.form2json({
            json: workflowBiBean,
            container: $biContainer
          });
          workflowBiBean.type = type;
          // 里程碑
          workflowBiBean.milestoneConfigs = workflowBiMilestoneConfig.collect('.milestone-configs', $biContainer);
          biConfigs.push(workflowBiBean);
          // 发起事项
          var newItemConfigs = [];
          $("select[name='newItemConfigs']", $biContainer)
            .find('option')
            .each(function () {
              newItemConfigs.push($(this).data('newItemConfig'));
            });
          workflowBiBean.newItemConfigs = newItemConfigs;
        }
      });
      return biConfigs;
    },

    // 显示业务流程信息
    showProcessInfo: function (treeNode) {
      var _self = this;
      var $container = $('#process_definition_info', _self.getWidgetElement());
      // 切换到过程结点页签
      $("a[href='#process_definition_info']", _self.getWidgetElement()).tab('show');

      // 设置数据
      AppPtMgrCommons.json2form({
        json: treeNode.data,
        container: $container
      });
      // 表单设置
      if (treeNode.data.formConfig) {
        treeNode.data.formConfig.processFormConfigType = treeNode.data.formConfig.configType;
        AppPtMgrCommons.json2form({
          json: treeNode.data.formConfig,
          container: $container
        });
      }

      // 所属业务
      $('#businessName', $container).wCommonComboTree({
        service: 'bizBusinessFacadeService.getBusinessTree',
        multiSelect: false, // 是否多选
        parentSelect: false, // 父节点选择有效，默认无效
        value: treeNode.data.businessId,
        onAfterSetValue: function (event, _self, value) {
          $('#businessId', $container).val(value);
        },
        beforeTreeExpand: function (treeId, treeNode) {
          // 非业务节点，不可选择
          $.each(treeNode.children, function (i, child) {
            if (child.type != 'business') {
              child.nocheck = true;
            }
          });
        }
      });

      // 设置方式
      $('input[name="processFormConfigType"]', $container).off('change');
      $('input[name="processFormConfigType"]', $container)
        .on('change', function () {
          var configType = $('input[name="processFormConfigType"]:checked', $container).val();
          $('.config-type', $container).hide();
          $('.config-type-' + configType, $container).show();
          if (configType == '1') {
            $('.form-config-mask', $container).show();
            if (StringUtils.isNotBlank($('#templateUuid', $container).val())) {
              $('#templateUuid', $container).trigger('change');
            }
          } else {
            $('.form-config-mask', $container).hide();
          }
        })
        .trigger('change');
      // 使用模板
      $('#templateUuid', $container).wSelect2({
        serviceName: 'bizDefinitionTemplateFacadeService',
        params: {
          processDefUuid: _self.processDefUuid,
          templateType: '10'
        },
        remoteSearch: false,
        width: '100%',
        height: 250
      });
      $('#templateUuid', $container).off('change');
      $('#templateUuid', $container)
        .on('change', function () {
          var configType = $('input[name="processFormConfigType"]:checked', $container).val();
          if (configType == '2') {
            return;
          }
          var templateUuid = $(this).val();
          if (StringUtils.isNotBlank(templateUuid)) {
            JDS.restfulGet({
              url: `/proxy/api/biz/definition/template/get/${templateUuid}`,
              success: function (result) {
                var templateJson = JSON.parse(result.data.definitionJson);
                _self.initProcessFormTemplateFields(templateJson, $('.form-config', $container));
              }
            });
          } else {
          }
        })
        .trigger('change');
      // 使用定义单
      $('#formUuid', $container).wSelect2({
        serviceName: 'bizProcessDefinitionFacadeService',
        queryMethod: 'listDyFormDefinitionSelectData',
        selectionMethod: 'getDyFormDefinitionSelectDataByUuids',
        remoteSearch: false,
        width: '100%',
        height: 250
      });
      $('#formUuid', $container).off('change');
      $('#formUuid', $container)
        .on('change', function () {
          var formUuid = $(this).val();
          if (StringUtils.isNotBlank(formUuid)) {
            JDS.restfulGet({
              url: `/proxy/api/biz/process/definition/getFormDefinitionByFormUuid/${formUuid}`,
              success: function (result) {
                var formDefinition = JSON.parse(result.data);
                _self.initProcessFormFields(formDefinition);
              }
            });
          } else {
            _self.initProcessFormFields({ fields: {}, subforms: {} });
          }
        })
        .trigger('change');
    },

    getProcessInfoBean: function (processDefinition) {
      var definitionJson = processDefinition.definitionJson;
      if (StringUtils.isBlank(definitionJson)) {
        return {
          uuid: processDefinition.uuid,
          name: processDefinition.name,
          id: processDefinition.id,
          code: processDefinition.code,
          version: processDefinition.version,
          enabled: processDefinition.enabled,
          formUuid: '',
          entityNameField: '',
          entityIdField: '',
          businessId: processDefinition.businessId,
          processNodePlaceHolder: '',
          listener: '',
          tagId: processDefinition.tagId,
          remark: processDefinition.remark
        };
      } else {
        return JSON.parse(definitionJson);
      }
    },
    _processBean: function () {
      return {
        uuid: null,
        name: null,
        id: null,
        code: null,
        version: null,
        enabled: null,
        formUuid: null,
        entityNameField: null,
        entityIdField: null,
        businessId: null,
        processNodePlaceHolder: null,
        listener: null,
        tagId: null,
        remark: null
      };
    },
    _processFormConfigBean: function () {
      return {
        processFormConfigType: null,
        configType: null,
        templateUuid: null,
        formUuid: null,
        entityNameField: null,
        entityIdField: null,
        processNodePlaceHolder: null
      };
    },
    _processNodeBean: function () {
      return {
        uuid: 'tree_id',
        name: '过程结点',
        id: 'node_id_' + (new Date().getTime() + idCounter++),
        formUuid: null,
        entityNameField: null,
        entityIdField: null,
        itemPlaceHolder: null,
        timeLimit: null,
        timeLimitType: null,
        milestone: null,
        stateConditions: null,
        listener: null,
        tagId: null,
        remark: null
      };
    },
    _processNodeFormConfigBean: function () {
      return {
        processNodeFormConfigType: null,
        configType: null,
        templateUuid: null,
        formUuid: null,
        entityNameField: null,
        entityIdField: null,
        itemPlaceHolder: null
      };
    },
    _processItemBean: function () {
      return {
        id: null,
        itemDefName: null,
        itemDefId: null,
        itemName: null,
        itemCode: null,
        itemType: null,
        formUuid: null,
        formName: null,
        itemNameField: null,
        itemCodeField: null,
        entityNameField: null,
        entityIdField: null,
        timeLimitField: null,
        timeLimitType: null,
        materialSubformId: null,
        materialNameField: null,
        materialCodeField: null,
        materialFileField: null,
        listener: null,
        mandatory: null,
        itemMilestone: null,
        dispenseItem: null,
        dispenseFormConfigs: null,
        dispenseItemPlaceHolder: null,
        enabledSituation: null,
        situationConfigs: null,
        businessIntegrationConfigs: null
      };
    },

    _processItemFormConfigBean: function () {
      return {
        itemFormConfigType: null,
        configType: null,
        templateUuid: null,
        formUuid: null,
        formName: null,
        itemNameField: null,
        itemCodeField: null,
        entityNameField: null,
        entityIdField: null,
        timeLimitField: null,
        materialSubformId: null,
        materialNameField: null,
        materialCodeField: null,
        materialRequiredField: null,
        materialFileField: null
      };
    },

    _processItemWorkflowBiBean: function () {
      return {
        type: null,
        configType: null,
        templateUuid: null,
        templateName: null,
        flowBizDefId: null,
        formDataType: null,
        copyBotRuleId: null,
        returnWithOver: null,
        returnWithDirection: null,
        returnDirectionId: null,
        returnBotRuleId: null,
        milestoneConfigs: null,
        syncTimerInfo: null
      };
    },

    initProcessFormTemplateFields: function (templateJson, $container) {
      // 设置数据
      AppPtMgrCommons.json2form({
        json: templateJson,
        container: $container
      });
      // 使用定义单
      $('#formUuid', $container).wSelect2({
        serviceName: 'bizProcessDefinitionFacadeService',
        queryMethod: 'listDyFormDefinitionSelectData',
        selectionMethod: 'getDyFormDefinitionSelectDataByUuids',
        remoteSearch: false,
        width: '100%',
        height: 250
      });
      $('#formUuid', $container).trigger('change');
    },

    initProcessFormFields: function (formDefinition) {
      var _self = this;
      var $container = $('#process_definition_info', _self.getWidgetElement());
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
      // 事件监听
      $('#listener', $container).wSelect2({
        serviceName: 'bizProcessDefinitionFacadeService',
        queryMethod: 'listBizProcessListenerSelectData',
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        multiple: true,
        width: '100%',
        height: 250
      });
      // 业务标签
      $('#tagId', $container).wSelect2({
        serviceName: 'bizTagFacadeService',
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        width: '100%',
        height: 250
      });
    },

    getFieldSelectData: function (formDefinition) {
      var selectData = [{ id: 'uuid', text: 'UUID' }];
      var fields = formDefinition.fields || {};
      $.each(fields, function (i, field) {
        selectData.push({ id: field.name, text: field.displayName });
      });
      return selectData;
    },

    getSubformSelectData(formDefinition) {
      var selectData = [];
      var subforms = formDefinition.subforms || {};
      $.each(subforms, function (i, subform) {
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

    bindEvents: function () {
      var _self = this;
      var $container = $(_self.getWidgetElement());
      // 展开业务流程树
      $('#btn_expand', $container).on('click', function () {
        _self.zTree.expandAll(true);
      });
      // 折叠业务流程树
      $('#btn_collapse', $container).on('click', function () {
        _self.zTree.expandAll(false);
      });
      // 删除业务流程树节点
      $('#btn_delete', $container).on('click', function () {
        var selectedNode = _self.getSelectTreeNode();
        if (selectedNode == null) {
          return;
        }
        if (selectedNode.type != 'node' && selectedNode.type != 'item') {
          appModal.error('请选择业务过程节点或事项节点！');
          return;
        }
        _self.zTree.removeNode(selectedNode);
        _self.latestTreeNode = null;
      });
      // 保存业务流程
      $('#btn_biz_process_definition_config_save', $container).on('click', function (event) {
        _self.saveProcessDefinitionConfig();
      });
      // 保存业务流程为新版本
      $('#btn_biz_process_definition_config_save_as_new_version', $container).on('click', function (event) {
        _self.saveProcessDefinitionConfigAsNewVersion();
      });
      // 新增过程节点
      $('#btn_process_node_add', $container).on('click', function (event) {
        _self.addProcessNode();
      });
      // 添加事项
      $('#btn_item_add', $container).on('click', function () {
        _self.addProcessItem();
      });
      // 配置项模板管理
      $('#btn_definition_template', $container).on('click', function () {
        appContext.require(['AppBizProcessDefinitionTemplateDialog'], function (templateDialog) {
          templateDialog.show({ processDefUuid: _self.processDefUuid });
        });
      });
    },

    saveProcessDefinitionConfig: function () {
      var _self = this;
      _self.collectFormData2TreeNodeData();
      var processDefinition = _self.collectProcessDefinition();
      JDS.restfulPost({
        url: ctx + '/proxy/api/biz/process/definition/config/save',
        data: processDefinition,
        success(result) {
          appModal.success('保存成功！');
        }
      });
    },

    saveProcessDefinitionConfigAsNewVersion: function () {
      var _self = this;
      _self.collectFormData2TreeNodeData();
      var processDefinition = _self.collectProcessDefinition();
      JDS.restfulPost({
        url: ctx + '/proxy/api/biz/process/definition/config/saveAsNewVersion',
        data: processDefinition,
        success(result) {
          appModal.success('保存成功！');
          _self.loadTree(result.data);
          appContext.getWindowManager().refreshParent();
        }
      });
    },

    collectProcessDefinition: function () {
      var _self = this;
      var rootNode = _self.zTree.getNodes()[0];
      var children = rootNode.children || [];
      var definition = rootNode.data;
      var nodes = [];
      for (var i = 0; i < children.length; i++) {
        var processNodeDefinition = _self.collectProcessNodeDefinition(children[i]);
        nodes.push(processNodeDefinition);
      }
      definition.nodes = nodes;
      return definition;
    },

    collectProcessNodeDefinition: function (treeNode) {
      var _self = this;
      var processNodeDefinition = treeNode.data;
      var children = treeNode.children || [];
      var nodes = [];
      var items = [];
      for (var i = 0; i < children.length; i++) {
        var child = children[i];
        if (child.type == 'node') {
          var nodeDefinition = _self.collectProcessNodeDefinition(child);
          nodes.push(nodeDefinition);
        } else if (child.type == 'item') {
          var itemDefinition = _self.collectProcessItemDefinition(child);
          items.push(itemDefinition);
        }
      }
      processNodeDefinition.nodes = nodes;
      processNodeDefinition.items = items;
      return processNodeDefinition;
    },

    collectProcessItemDefinition: function (treeNode) {
      return treeNode.data;
    },

    addProcessNode: function () {
      var _self = this;
      var selectedNode = _self.getSelectTreeNode();
      if (selectedNode == null) {
        return;
      }
      if (selectedNode.type != 'process' && selectedNode.type != 'node') {
        appModal.error('请选择业务流程或过程节点！');
        return;
      }

      // 添加树结点
      var processNodeBean = _self._processNodeBean();
      var treeNode = { id: processNodeBean.uuid, name: processNodeBean.name, data: processNodeBean, type: 'node' };

      var newTreeNodes = _self.zTree.addNodes(selectedNode, [treeNode]);
      // 收集最新的数据到上次点击的树节点
      _self.collectFormData2TreeNodeData();
      _self.latestTreeNode = newTreeNodes[0];
      _self.showProcessNodeInfo(_self.latestTreeNode);
    },

    getSelectTreeNode: function () {
      var _self = this;
      var selectedNodes = _self.zTree.getSelectedNodes();
      if (selectedNodes.length != 1) {
        appModal.error('请选择树节点！');
        return;
      }
      return selectedNodes[0];
    },

    showProcessNodeInfo: function (treeNode) {
      var _self = this;
      var $container = $('#process_node_info', _self.getWidgetElement());
      // 切换到过程结点页签
      $("a[href='#process_node_info']", _self.getWidgetElement()).tab('show');

      // 设置数据
      AppPtMgrCommons.json2form({
        json: treeNode.data,
        container: $container
      });
      // 表单设置
      if (treeNode.data.formConfig) {
        treeNode.data.formConfig.processNodeFormConfigType = treeNode.data.formConfig.configType;
        AppPtMgrCommons.json2form({
          json: treeNode.data.formConfig,
          container: $container
        });
      }

      // 设置方式
      $('input[name="processNodeFormConfigType"]', $container).off('change');
      $('input[name="processNodeFormConfigType"]', $container)
        .on('change', function () {
          var configType = $('input[name="processNodeFormConfigType"]:checked', $container).val();
          $('.config-type', $container).hide();
          $('.config-type-' + configType, $container).show();
          if (configType == '1') {
            $('.form-config-mask', $container).show();
            if (StringUtils.isNotBlank($('#templateUuid', $container).val())) {
              $('#templateUuid', $container).trigger('change');
            }
          } else {
            $('.form-config-mask', $container).hide();
          }
        })
        .trigger('change');
      // 使用模板
      $('#templateUuid', $container).wSelect2({
        serviceName: 'bizDefinitionTemplateFacadeService',
        params: {
          processDefUuid: _self.processDefUuid,
          templateType: '20'
        },
        remoteSearch: false,
        width: '100%',
        height: 250
      });
      $('#templateUuid', $container).off('change');
      $('#templateUuid', $container)
        .on('change', function () {
          var configType = $('input[name="processNodeFormConfigType"]:checked', $container).val();
          if (configType == '2') {
            return;
          }
          var templateUuid = $(this).val();
          if (StringUtils.isNotBlank(templateUuid)) {
            JDS.restfulGet({
              url: `/proxy/api/biz/definition/template/get/${templateUuid}`,
              success: function (result) {
                var templateJson = JSON.parse(result.data.definitionJson);
                _self.initProcessNodeFormTemplateFields(templateJson, $('.form-config', $container));
              }
            });
          } else {
          }
        })
        .trigger('change');
      // 使用定义单
      $('#formUuid', $container).wSelect2({
        serviceName: 'bizProcessDefinitionFacadeService',
        queryMethod: 'listDyFormDefinitionSelectData',
        selectionMethod: 'getDyFormDefinitionSelectDataByUuids',
        remoteSearch: false,
        width: '100%',
        height: 250
      });
      $('#formUuid', $container).off('change');
      $('#formUuid', $container)
        .on('change', function () {
          var formUuid = $(this).val();
          if (StringUtils.isNotBlank(formUuid)) {
            JDS.restfulGet({
              url: `/proxy/api/biz/process/definition/getFormDefinitionByFormUuid/${formUuid}`,
              success: function (result) {
                var formDefinition = JSON.parse(result.data);
                _self.initProcessNodeFormFields(formDefinition);
              }
            });
          } else {
            _self.initProcessNodeFormFields({ fields: {}, subforms: {} });
          }
        })
        .trigger('change');

      // 状态条件
      var stateConditions = treeNode.data.stateConditions || [];
      $("select[name='stateConditions']", $container).html('');
      $.each(stateConditions, function (i, stateCondition) {
        var $option = $('<option/>').text(stateCondition.conditionName).data('condition', stateCondition);
        $("select[name='stateConditions']", $container).append($option);
      });
      // 新增
      $('#btn_state_condition_add', $container).off('click');
      $('#btn_state_condition_add', $container).on('click', function () {
        appContext.require(['AppBizProcessDefinitionProcessNodeConditionDialog'], function (conditionDialog) {
          conditionDialog.show({
            processDefinition: _self.collectProcessDefinition(),
            onOk: function (stateCondition) {
              var $option = $('<option/>').text(stateCondition.conditionName).data('condition', stateCondition);
              $("select[name='stateConditions']", $container).append($option);
            }
          });
        });
      });
      // 删除
      $('#btn_state_condition_delete', $container).off('click');
      $('#btn_state_condition_delete', $container).on('click', function () {
        $("select[name='stateConditions']", $container).find('option:selected').remove();
      });
      $("select[name='stateConditions']", $container).off('dblclick');
      $("select[name='stateConditions']", $container).on('dblclick', 'option', function () {
        var $option = $(this);
        var stateCondition = $option.data('condition');
        appContext.require(['AppBizProcessDefinitionProcessNodeConditionDialog'], function (conditionDialog) {
          conditionDialog.show({
            processDefinition: _self.collectProcessDefinition(),
            stateCondition,
            onOk: function (stateCondition) {
              $option.text(stateCondition.conditionName).data('condition', stateCondition);
            }
          });
        });
      });

      // 事件监听
      $('#listener', $container).wSelect2({
        serviceName: 'bizProcessDefinitionFacadeService',
        queryMethod: 'listBizProcessNodeListenerSelectData',
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        multiple: true,
        width: '100%',
        height: 250
      });
      // 业务标签
      $('#tagId', $container).wSelect2({
        serviceName: 'bizTagFacadeService',
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        width: '100%',
        height: 250
      });
    },
    initProcessNodeFormFields: function (formDefinition) {
      var _self = this;
      var $container = $('#process_node_info', _self.getWidgetElement());
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
      // 事项办理信息显示位置
      $('#itemPlaceHolder', $container).wSelect2({
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        data: blockSelectData,
        width: '100%',
        height: 250
      });
    },
    initProcessNodeFormTemplateFields: function (templateJson, $container) {
      // 设置数据
      AppPtMgrCommons.json2form({
        json: templateJson,
        container: $container
      });
      // 使用定义单
      $('#formUuid', $container).wSelect2({
        serviceName: 'bizProcessDefinitionFacadeService',
        queryMethod: 'listDyFormDefinitionSelectData',
        selectionMethod: 'getDyFormDefinitionSelectDataByUuids',
        remoteSearch: false,
        width: '100%',
        height: 250
      });
      $('#formUuid', $container).trigger('change');
    },

    addProcessItem: function () {
      var _self = this;
      var selectedNode = _self.getSelectTreeNode();
      if (selectedNode == null) {
        return;
      }
      if (selectedNode.type != 'node') {
        appModal.error('请选择过程节点！');
        return;
      }

      var listViewWidget = null;
      var dlgId = UUID.createUUID();
      var title = '添加事项';
      var message = "<div id='" + dlgId + "'></div>";
      var dlgOptions = {
        title: title,
        message: message,
        size: 'large',
        shown: function () {
          appContext.renderWidget({
            renderTo: '#' + dlgId,
            widgetDefId: 'wBootstrapTable_CA02A1A84C6000018DFAE90D23B01679',
            forceRenderIfConflict: true,
            callback: function () {
              $('#' + dlgId + ' > .ui-wBootstrapTable').css({
                overflow: 'auto'
              });
            },
            onPrepare: {
              wBootstrapTable_CA02A1A84C6000018DFAE90D23B01679: function () {
                listViewWidget = this;
                listViewWidget.develops.push({
                  beforeLoadData: function () {
                    listViewWidget.addParam('processDefUuid', _self.processDefUuid);
                  }
                });
              }
            }
          });
        },
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              var selection = listViewWidget.getSelections();
              if (selection == null || selection.length == 0) {
                appModal.error('请选择事项！');
                return false;
              }
              var treeNodes = [];
              for (var i = 0; i < selection.length; i++) {
                var itemData = selection[i];
                // 添加树结点
                var processItemBean = _self._processItemBean();
                processItemBean.id = 'pid_' + (new Date().getTime() + idCounter++);
                processItemBean.itemDefName = itemData.itemDefName;
                processItemBean.itemDefId = itemData.itemDefId;
                processItemBean.itemName = itemData.itemName;
                processItemBean.itemCode = itemData.itemCode;
                processItemBean.itemType = itemData.itemType;
                var treeNode = {
                  id: processItemBean.itemCode,
                  name: processItemBean.itemName,
                  data: processItemBean,
                  type: 'item'
                };
                treeNodes.push(treeNode);
              }
              var newTreeNodes = _self.zTree.addNodes(selectedNode, treeNodes);
              // 收集最新的数据到上次点击的树节点
              _self.collectFormData2TreeNodeData();
              _self.latestTreeNode = newTreeNodes[0];
              _self.showProcessItemInfo(_self.latestTreeNode);
              return true;
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default',
            callback: function () {
              return true;
            }
          }
        }
      };
      appModal.dialog(dlgOptions);
    },
    showProcessItemInfo: function (treeNode) {
      var _self = this;
      var $container = $('#process_item_info', _self.getWidgetElement());
      var $formContainer = $('#process_item_info_form', $container);
      // 切换到过程结点页签
      $("a[href='#process_item_info']", _self.getWidgetElement()).tab('show');

      // 设置数据
      AppPtMgrCommons.json2form({
        json: treeNode.data,
        container: $container
      });
      // 表单设置
      if (treeNode.data.formConfig) {
        treeNode.data.formConfig.itemFormConfigType = treeNode.data.formConfig.configType;
        AppPtMgrCommons.json2form({
          json: treeNode.data.formConfig,
          container: $container
        });
      }

      // 设置方式
      $('input[name="itemFormConfigType"]', $container).off('change');
      $('input[name="itemFormConfigType"]', $container)
        .on('change', function () {
          var configType = $('input[name="itemFormConfigType"]:checked', $container).val();
          $('.config-type', $formContainer).hide();
          $('.config-type-' + configType, $formContainer).show();
          if (configType == '1') {
            $('.form-config-mask', $container).show();
            if (StringUtils.isNotBlank($('#templateUuid', $container).val())) {
              $('#templateUuid', $container).trigger('change');
            }
          } else {
            $('.form-config-mask', $container).hide();
          }
        })
        .trigger('change');
      // 使用模板
      $('#templateUuid', $formContainer).wSelect2({
        serviceName: 'bizDefinitionTemplateFacadeService',
        params: {
          processDefUuid: _self.processDefUuid,
          templateType: '30'
        },
        remoteSearch: false,
        width: '100%',
        height: 250
      });
      $('#templateUuid', $formContainer).off('change');
      $('#templateUuid', $formContainer)
        .on('change', function () {
          var configType = $('input[name="itemFormConfigType"]:checked', $container).val();
          if (configType == '2') {
            return;
          }
          var templateUuid = $(this).val();
          if (StringUtils.isNotBlank(templateUuid)) {
            JDS.restfulGet({
              url: `/proxy/api/biz/definition/template/get/${templateUuid}`,
              success: function (result) {
                var templateJson = JSON.parse(result.data.definitionJson);
                _self.initProcessItemFormTemplateFields(templateJson, $('.form-config', $container));
              }
            });
          } else {
          }
        })
        .trigger('change');
      // 使用定义单
      $('#formUuid', $container).wSelect2({
        valueField: 'formUuid',
        serviceName: 'bizProcessDefinitionFacadeService',
        queryMethod: 'listDyFormDefinitionSelectData',
        selectionMethod: 'getDyFormDefinitionSelectDataByUuids',
        remoteSearch: false,
        width: '100%',
        height: 250
      });
      $('#formUuid', $container).off('change');
      $('#formUuid', $container)
        .on('change', function () {
          var formUuid = $('#formUuid', $container).val();
          if (StringUtils.isNotBlank(formUuid)) {
            JDS.restfulGet({
              url: `/proxy/api/biz/process/definition/getFormDefinitionByFormUuid/${formUuid}`,
              success: function (result) {
                var formDefinition = JSON.parse(result.data);
                $('#formName', $container).val(formDefinition.name);
                _self.initProcessItemFormFields(formDefinition);
              }
            });
          } else {
            _self.initProcessItemFormFields({ fields: {}, subforms: {} });
          }
        })
        .trigger('change');

      // 事件监听
      $('#listener', $container).wSelect2({
        serviceName: 'bizProcessDefinitionFacadeService',
        queryMethod: 'listBizProcessItemListenerSelectData',
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        multiple: true,
        width: '100%',
        height: 250
      });

      // 是否分发事项
      if (treeNode.data.itemType == '20' || treeNode.data.itemType == '30') {
        $('.dispense-item', $container).show();
      } else {
        $('.dispense-item', $container).hide();
      }
      $("input[name='dispenseItem']", $container).off('change');
      $("input[name='dispenseItem']", $container)
        .on('change', function () {
          var dispenseItem = $("input[name='dispenseItem']:checked", $container).val();
          if (dispenseItem == '1') {
            $('.dispense-item-1', $container).show();
          } else {
            $('.dispense-item-1', $container).hide();
          }
        })
        .trigger('change');

      // 事项分发配置
      $('#btn_dispense_form_config', $container).off('click');
      $('#btn_dispense_form_config', $container).on('click', function () {
        appContext.require(['AppBizProcessDefinitionDispenseFormConfigDialog'], function (dispenseFormConfigDialog) {
          dispenseFormConfigDialog.show(treeNode.data);
        });
      });

      // 办理情形
      if (treeNode.data.enabledSituation == '1') {
        itemSituationConfig.show($('.enabled-situation', $container), treeNode.data);
      } else {
        itemSituationConfig.hide($('.enabled-situation', $container), treeNode.data);
      }
      $("input[name='enabledSituation']", $container).off('change');
      $("input[name='enabledSituation']", $container).on('change', function () {
        var enabledSituation = $("input[name='enabledSituation']:checked", $container).val();
        if (enabledSituation == '1') {
          itemSituationConfig.show($('.enabled-situation', $container), treeNode.data);
        } else {
          itemSituationConfig.hide($('.enabled-situation', $container), treeNode.data);
        }
      });

      // 业务集成
      _self.initProcessItemBusinessIntegrations(treeNode);
    },

    initProcessItemFormFields: function (formDefinition) {
      var _self = this;
      var $container = $('#process_item_info', _self.getWidgetElement());
      var fieldSelectData = _self.getFieldSelectData(formDefinition);
      var subformSelectData = _self.getSubformSelectData(formDefinition);
      var blockSelectData = _self.getBlockSelectData(formDefinition);

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
      // 时限字段
      $('#timeLimitField', $container).wSelect2({
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        data: fieldSelectData,
        width: '100%',
        height: 250
      });
      // 材料从表
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
          _self.onSubformIdChange(formDefinition, $(this).val(), 'materialNameField');
          // 材料编码字段
          _self.onSubformIdChange(formDefinition, $(this).val(), 'materialCodeField');
          // 材料是否必填字段
          _self.onSubformIdChange(formDefinition, $(this).val(), 'materialRequiredField');
          // 材料附件字段
          _self.onSubformIdChange(formDefinition, $(this).val(), 'materialFileField');
        })
        .trigger('change');

      // 分发事项办理信息显示位置
      $('#dispenseItemPlaceHolder', $container).wSelect2({
        defaultBlank: true, // 默认空选项
        remoteSearch: false,
        data: blockSelectData,
        width: '100%',
        height: 250
      });
    },
    initProcessItemFormTemplateFields: function (templateJson, $container) {
      // 设置数据
      AppPtMgrCommons.json2form({
        json: templateJson,
        container: $container
      });
      // 使用定义单
      $('#formUuid', $container).wSelect2({
        serviceName: 'bizProcessDefinitionFacadeService',
        queryMethod: 'listDyFormDefinitionSelectData',
        selectionMethod: 'getDyFormDefinitionSelectDataByUuids',
        remoteSearch: false,
        width: '100%',
        height: 250
      });
      $('#formUuid', $container).trigger('change');
    },
    onSubformIdChange: function (formDefinition, subformId, field) {
      var _self = this;
      var $container = _self.getWidgetElement();
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

    initProcessItemBusinessIntegrations: function (treeNode) {
      var _self = this;
      var $container = $('#process_item_info_bi', _self.getWidgetElement());
      var biConfigs = treeNode.data.businessIntegrationConfigs || [];
      var biConfigMap = {};
      _self.biConfigMap = biConfigMap;
      $.each(biConfigs, function (i, biConfig) {
        biConfigMap[biConfig.type] = biConfig;
      });
      var $types = $('input[name="type"]', $container);
      $.each($types, function () {
        var type = $(this).val();
        var biConfigData = biConfigMap[type];
        if (biConfigData) {
          $(this).attr('checked', 'checked');
          // 设置数据
          AppPtMgrCommons.json2form({
            json: biConfigData,
            container: $('.bi-type-' + type, $container)
          });
          // 流程集成
          if (type == '1') {
            // 发起事项
            var newItemConfigs = biConfigData.newItemConfigs || [];
            $("select[name='newItemConfigs']", $container).html('');
            $.each(newItemConfigs, function (i, newItemConfig) {
              var optionText = _self.getStartNewItemOptionText(newItemConfig);
              var $option = $('<option/>').text(optionText).data('newItemConfig', newItemConfig);
              $("select[name='newItemConfigs']", $container).append($option);
            });
          }
        } else {
          $(this).removeAttr('checked');
        }
      });

      // 设置方式
      $('input[name="configType"]', $container).off('change');
      $('input[name="configType"]', $container)
        .on('change', function () {
          var configType = $('input[name="configType"]:checked', $container).val();
          $('.config-type', $container).hide();
          $('.config-type-' + configType, $container).show();
          if (configType == '1') {
            $('.item-workflow-config-mask', $container).show();
            if (StringUtils.isNotBlank($('#templateUuid', $container).val())) {
              $('#templateUuid', $container).trigger('change');
            }
          } else {
            $('.item-workflow-config-mask', $container).hide();
          }
        })
        .trigger('change');
      // 使用模板
      $('#templateUuid', $container).wSelect2({
        serviceName: 'bizDefinitionTemplateFacadeService',
        params: {
          processDefUuid: _self.processDefUuid,
          templateType: '40'
        },
        remoteSearch: false,
        width: '100%',
        height: 250
      });
      $('#templateUuid', $container).off('change');
      $('#templateUuid', $container)
        .on('change', function () {
          var configType = $('input[name="configType"]:checked', $container).val();
          if (configType == '2') {
            return;
          }
          var templateUuid = $(this).val();
          if (StringUtils.isNotBlank(templateUuid)) {
            JDS.restfulGet({
              url: `/proxy/api/biz/definition/template/get/${templateUuid}`,
              success: function (result) {
                var templateJson = JSON.parse(result.data.definitionJson);
                _self.initProcessItemFlowBizTemplateFields(templateJson, $('.item-workflow-config', $container));
              }
            });
          } else {
          }
        })
        .trigger('change');

      // 集成方式
      $('input[name="type"]', $container).off('change');
      $('input[name="type"]', $container)
        .on('change', function () {
          $('.bi-type', $container).hide();
          var $checkedTypes = $('input[name="type"]:checked', $container);
          $.each($checkedTypes, function () {
            $('.bi-type-' + $(this).val(), $container).show();
          });
        })
        .trigger('change');

      // 流程业务定义
      $('#flowBizDefId', $container).wSelect2({
        serviceName: 'wfFlowBusinessDefinitionFacadeService',
        remoteSearch: false,
        width: '100%',
        height: 250
      });
      $('#flowBizDefId', $container).off('change');
      $('#flowBizDefId', $container)
        .on('change', function () {
          var flowBizDefId = $(this).val();
          if (StringUtils.isNotBlank(flowBizDefId)) {
            JDS.restfulGet({
              url: `/proxy/api/workflow/business/definition/getSelectDataByFlowBizDefId/${flowBizDefId}`,
              success: function (result) {
                _self.initProcessItemFlowBizSelectFields(result.data, biConfigMap);
              }
            });
          } else {
            _self.initProcessItemFlowBizSelectFields({ formFields: [], taskIds: [], directions: [] }, biConfigMap);
          }
        })
        .trigger('change');

      // 流程单据数据
      $('input[name="formDataType"]', $container).off('change');
      $('input[name="formDataType"]', $container).on('change', function () {
        var formDataType = $(this).val();
        $('.form-data-type', $container).hide();
        $('.form-data-type-' + formDataType, $container).show();
        // 指定反馈流向
        if (formDataType == '2') {
          $('input[name="returnWithDirection"]', $container).trigger('change');
        } else {
          $('input[name="returnDirectionId"]', $container).closest('.form-group').hide();
        }
      });
      $('input[name="formDataType"]:checked', $container).trigger('change');

      // 单据转换规则
      $('#copyBotRuleId', $container).wSelect2({
        serviceName: 'botRuleConfFacadeService',
        queryMethod: 'loadSelectData',
        placeholder: '请选择',
        multiple: false,
        remoteSearch: false
      });

      // 指定反馈流向
      $('input[name="returnWithDirection"]', $container).off('change');
      $('input[name="returnWithDirection"]', $container)
        .on('change', function () {
          if (this.checked) {
            $('input[name="returnDirectionId"]', $container).closest('.form-group').show();
          } else {
            $('input[name="returnDirectionId"]', $container).closest('.form-group').hide();
          }
        })
        .trigger('change');

      // 反馈规则
      $('#returnBotRuleId', $container).wSelect2({
        serviceName: 'botRuleConfFacadeService',
        queryMethod: 'loadSelectData',
        placeholder: '请选择',
        multiple: false,
        remoteSearch: false
      });
    },
    initProcessItemFlowBizSelectFields: function (selectData, biConfigMap) {
      var _self = this;
      var $container = $('#process_item_info', _self.getWidgetElement());
      var directionSelectData = selectData.directions || [];

      // 反馈流向
      $('#returnDirectionId', $container).wSelect2({
        data: directionSelectData,
        placeholder: '请选择',
        multiple: false,
        remoteSearch: false
      });

      // 工作流集成里程碑配置
      var workflowBiConfig = biConfigMap['1'] || {};
      workflowBiMilestoneConfig.show($('.milestone-configs', $container), workflowBiConfig.milestoneConfigs || [], selectData);

      // 新增
      $('#btn_new_item_add', $container).off('click');
      $('#btn_new_item_add', $container).on('click', function () {
        appContext.require(['AppBizProcessDefinitionItemWorkflowBiNewItemConfig'], function (newItemDialog) {
          newItemDialog.show({
            processDefinition: _self.collectProcessDefinition(),
            flowBizSelectData: selectData,
            onOk: function (newItemConfig) {
              var optionText = _self.getStartNewItemOptionText(newItemConfig);
              var $option = $('<option/>').text(optionText).data('newItemConfig', newItemConfig);
              $("select[name='newItemConfigs']", $container).append($option);
            }
          });
        });
      });
      // 删除
      $('#btn_new_item_delete', $container).off('click');
      $('#btn_new_item_delete', $container).on('click', function () {
        $("select[name='newItemConfigs']", $container).find('option:selected').remove();
      });
      $("select[name='newItemConfigs']", $container).off('dblclick');
      $("select[name='newItemConfigs']", $container).on('dblclick', 'option', function () {
        var $option = $(this);
        var newItemConfig = $option.data('newItemConfig');
        appContext.require(['AppBizProcessDefinitionItemWorkflowBiNewItemConfig'], function (newItemDialog) {
          newItemDialog.show({
            processDefinition: _self.collectProcessDefinition(),
            flowBizSelectData: selectData,
            newItemConfig,
            onOk: function (newItemConfig) {
              var optionText = _self.getStartNewItemOptionText(newItemConfig);
              $option.text(optionText).data('newItemConfig', newItemConfig);
            }
          });
        });
      });
    },
    getStartNewItemOptionText: function (newItemConfig) {
      var text = '';
      if (newItemConfig.startItemWay == '1') {
        text = '流程办结时发起' + '(' + newItemConfig.startItemName + ')';
      } else if (newItemConfig.startItemWay == '2') {
        text = '流向流转时发起' + '(' + newItemConfig.startItemDirectionName + '——' + newItemConfig.startItemName + ')';
      } else {
        text = newItemConfig.startItemName;
      }
      return text;
    },
    initProcessItemFlowBizTemplateFields: function (templateJson, $container) {
      var _self = this;
      // 设置数据
      AppPtMgrCommons.json2form({
        json: templateJson,
        container: $container
      });
      var workflowConfig = _self.biConfigMap['1'] || {};
      _self.biConfigMap['1'] = $.extend(
        true,
        {
          type: workflowConfig.type,
          configType: workflowConfig.configType
        },
        templateJson
      );
      // 流程业务定义
      $('#flowBizDefId', $container).wSelect2({
        serviceName: 'wfFlowBusinessDefinitionFacadeService',
        remoteSearch: false,
        width: '100%',
        height: 250
      });
      $('#flowBizDefId', $container).trigger('change');
      $('input[name="formDataType"]:checked', $container).trigger('change');
    }
  });
  return AppBizItemDefinitionDetailsWidgetDevelopment;
});
