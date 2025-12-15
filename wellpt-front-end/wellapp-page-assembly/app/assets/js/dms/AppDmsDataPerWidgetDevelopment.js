define(['constant', 'commons', 'server', 'appContext', 'appModal', 'wSelect2', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  wSelect2,
  HtmlWidgetDevelopment
) {
  var JDS = server.JDS;
  var StringUtils = commons.StringUtils;

  var AppDmsDataPerWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppDmsDataPerWidgetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var form_selector = '#dms_data_permission_definition_form';
      var rule_group_form_selector = '#dms_data_permission_rule_group_form';
      var rule_def_form_selector = '#dms_data_permission_rule_def_form';
      var uuid = this.getWidgetParams().uuid;
      var bean = {
        uuid: null, // UUID
        recVer: null, // 版本号
        id: null, // ID
        name: null, // NAME
        code: null, // CODE
        type: null, // TYPE
        typeName: null,
        tableName: null, // DATA_NAME
        viewName: null, // DATA_NAME
        remark: null // REMARK
      };
      var ruleGroupBean = {
        groupId: null, // 分组ID
        groupName: null, // 分组名称
        type: 1, // 规则类型
        groupConnector: null // 连接符
      };
      var ruleDefBean = {
        ruleId: null, // 规则ID
        ruleName: null, // 规则名称
        type: 2, // 规则类型
        ruleType: null, // 类型：1角色归属，2字段值比较
        fieldName: null, // 字段名
        operator: null, // 操作符
        fieldValue: null, // 字段值
        roleName: null, // 角色名
        roleId: null, // 角色ID
        ruleConnector: null // 连接符
      };
      var setting = {
        edit: {
          drag: {
            autoExpandTrigger: true,
            isCopy: false,
            isMove: true,
            prev: true,
            inner: true,
            next: true
          },
          enable: true,
          showRemoveBtn: false,
          showRenameBtn: false
        },
        view: {
          autoCancelSelected: true,
          selectedMulti: false,
          txtSelectedEnable: true,
          fontCss: $.noop,
          addDiyDom: addDiyDom
        },
        callback: {
          beforeClick: zTreeBeforeClick,
          beforeDrag: $.noop,
          beforeDrop: $.noop
        }
      };
      if (uuid) {
        getDmsDataPermissionDefinition(uuid);
      } else {
        $('#tableName').val('');
        $('#viewName').val('');
        $.fn.zTree.init($('#rule_tree'), setting, []);
        $.common.idGenerator.generate('#id', 'DP_');
        $('#id').prop('readonly', '');
        $('#typeName').trigger('change');
        $('#tableName').trigger('change');
        $('#viewName').trigger('change');
      }
      // 允许冲突检测
      jQuery.validator.addMethod(
        'isDataNameRequired',
        function (value, element) {
          var type = $('#type').val();
          var eleName = $(element).attr('name');
          if (type == '1') {
            if (eleName == 'viewName') {
              return true;
            }
          } else {
            if (eleName == 'tableName') {
              return true;
            }
          }
          if (StringUtils.isNotBlank(value)) {
            return true;
          }
          return false;
        },
        '不能为空！'
      );
      // 名称唯一性
      jQuery.validator.addMethod(
        'nameRemoteUnique',
        function (value, element) {
          var exists = false;
          JDS.call({
            service: 'dmsDataPermissionDefinitionFacadeService.checkExistsByUuidAndName',
            data: [$('#uuid').val(), $('#name').val()],
            async: false,
            success: function (result) {
              exists = result.data;
            }
          });
          if (StringUtils.isBlank($('#uuid').val())) {
            return !exists;
          }
          return exists;
        },
        '该字段值已经存在！'
      );
      // ID唯一性
      jQuery.validator.addMethod(
        'idRemoteUnique',
        function (value, element) {
          var exists = false;
          JDS.call({
            service: 'dmsDataPermissionDefinitionFacadeService.checkExistsByUuidAndId',
            data: [$('#uuid').val(), $('#name').val()],
            async: false,
            version: '',
            success: function (result) {
              exists = result.data;
            }
          });
          if (StringUtils.isBlank($('#uuid').val())) {
            return !exists;
          }
          return exists;
        },
        'ID不能重复！'
      );
      // 数据定义验证器
      var validator = $.common.validation.validate(form_selector, 'dmsDataPermissionDefinitionEntity', function (options) {
        // 名称唯一性
        options.rules.name.nameRemoteUnique = true;
        options.messages.name.nameRemoteUnique = '该字段值已经存在！';
        // ID唯一性
        options.rules.id.idRemoteUnique = true;
        options.messages.id.idRemoteUnique = 'ID不能重复！';
        // 数据库表非空
        options.rules.tableName = {
          isDataNameRequired: true
        };
        options.messages.tableName = {
          isDataNameRequired: '不能为空！'
        };
        // 数据库视图非空
        options.rules.viewName = {
          isDataNameRequired: true
        };
        options.messages.viewName = {
          isDataNameRequired: '不能为空！'
        };
        // 验证所有字段，默认忽略隐藏字段
        options.ignore = '';
      });
      // 分组名称唯一性
      jQuery.validator.addMethod(
        'groupNameUnique',
        function (value, element) {
          var nodes = getNodesByName(value);
          if (nodes.length > 1) {
            return false;
          } else if (nodes.length == 1) {
            var groupId = $('#groupId').val();
            if (StringUtils.isNotBlank(groupId) && nodes[0].id == groupId) {
              return true;
            } else {
              return false;
            }
          }
          return true;
        },
        '名称不能重复！'
      );
      function getNodesByName(name) {
        var ruleTree = getRuleTree();
        return (
          ruleTree.getNodesByFilter(
            function (node) {
              return name == node.name;
            },
            false,
            null
          ) || []
        );
      }
      // 规则分组验证器
      var ruleGroupValidator = $(rule_group_form_selector).validate({
        rules: {
          groupName: {
            required: true,
            groupNameUnique: true
          }
        },
        messages: {
          groupName: {
            required: '不能为空!',
            groupNameUnique: '名称不能重复！'
          }
        }
      });
      // 规则名称唯一性
      jQuery.validator.addMethod(
        'ruleNameUnique',
        function (value, element) {
          var nodes = getNodesByName(value);
          if (nodes.length > 1) {
            return false;
          } else if (nodes.length == 1) {
            var ruleId = $('#ruleId').val();
            if (StringUtils.isNotBlank(ruleId) && nodes[0].id == ruleId) {
              return true;
            } else {
              return false;
            }
          }
          return true;
        },
        '名称不能重复！'
      );
      // 规则定义验证规则
      jQuery.validator.addMethod(
        'ruleRequired',
        function (value, element) {
          var type = $('#ruleType').val();
          var eleName = $(element).attr('name');
          if (type == '1') {
            if (eleName == 'fieldName' || eleName == 'operator' || eleName == 'fieldValue') {
              return true;
            }
          } else {
            if (eleName == 'roleId') {
              return true;
            }
          }
          if (StringUtils.isNotBlank(value)) {
            return true;
          }
          return false;
        },
        '不能为空！'
      );
      // 规则定义验证器
      var ruleDefValidator = $(rule_def_form_selector).validate({
        rules: {
          ruleName: {
            required: true,
            ruleNameUnique: true
          },
          ruleType: {
            required: true
          },
          roleId: {
            ruleRequired: true
          },
          fieldName: {
            ruleRequired: true
          },
          operator: {
            ruleRequired: true
          },
          fieldValue: {
            ruleRequired: true
          }
        },
        messages: {
          ruleName: {
            required: '不能为空!',
            ruleNameUnique: '名称不能重复！'
          },
          ruleType: {
            required: '不能为空!'
          },
          roleId: {
            ruleRequired: '不能为空!'
          },
          fieldName: {
            ruleRequired: '不能为空!'
          },
          operator: {
            ruleRequired: '不能为空!'
          },
          fieldValue: {
            ruleRequired: '不能为空!'
          }
        },
        ignore: ''
      });

      function addDiyDom(treeId, treeNode) {
        var data = treeNode.data;
        if (StringUtils.isNotBlank(data.connector)) {
          var aObj = $('#' + treeNode.tId + '_a');
          var diyString = "<span id='" + treeNode.id + "_diy'></span>";
          aObj.append(diyString);
          updateDiyDom(treeNode);
        }
      }
      function updateDiyDom(treeNode) {
        var treeNodeId = treeNode.id;
        var connector = treeNode.data.connector;
        var $diy = $('#' + treeNodeId + '_diy');
        if ($diy.length == 0) {
          addDiyDom('rule_tree', treeNode);
        } else if (connector == 'and') {
          $diy
            .css({
              color: 'green'
            })
            .text('(并且)');
        } else if (connector == 'or') {
          $diy
            .css({
              color: 'green'
            })
            .text('(或者)');
        } else {
          $diy.text('');
        }
      }
      function getDmsDataPermissionDefinition(uuid) {
        JDS.call({
          service: 'dmsDataPermissionDefinitionFacadeService.getDto',
          data: uuid,
          version: '',
          success: function (result) {
            bean = result.data;
            if (bean.type == '1') {
              bean.tableName = bean.dataName;
              bean.viewName = '';
            } else {
              bean.viewName = bean.dataName;
              bean.tableName = '';
            }
            $(form_selector).json2form(bean);
            $('#tableName').trigger('change');
            $('#viewName').trigger('change');
            $('#typeName').trigger('change');
            // ID只读
            $('#id').prop('readonly', 'readonly');
            validator.form();
            var ruleDefinition = bean.ruleDefinition || '[]';
            var treeNodes = ruleDefinition2TreeNodes(JSON.parse(ruleDefinition));
            var zTree = $.fn.zTree.init($('#rule_tree'), setting, treeNodes);
            $('.tab-rule').hide();
            // 字段名下拉选择
            $('#fieldName').wSelect2({
              serviceName: 'dmsDataPermissionDefinitionFacadeService',
              queryMethod: 'loadFieldNamesSelectData',
              remoteSearch: false,
              params: {
                type: bean.type,
                dataName: bean.dataName
              }
            });
            $('#fieldName').trigger('change');
          }
        });
      }
      function getRuleTree() {
        return $.fn.zTree.getZTreeObj('rule_tree');
      }

      function getRuleTreeSelectedNode(required) {
        var ruleTree = getRuleTree();
        var nodes = ruleTree.getNodes();
        if (nodes == null || nodes.length == 0) {
          return null;
        }
        var selectedNodes = ruleTree.getSelectedNodes();
        if (selectedNodes.length == 0) {
          if (required != false) {
            appModal.error('请先选择节点!');
          }
          return null;
        }
        if (selectedNodes.length > 1) {
          if (required != false) {
            appModal.error('只能选择一个节点!');
          }
          return null;
        }
        return selectedNodes[0];
      }

      function getRuleGroupDatas() {
        var groupDatas = [];
        var ruleTree = getRuleTree();
        var nodes = ruleTree.getNodes();
        extractRuleGroup(nodes, groupDatas);
        return groupDatas;
      }
      function extractRuleGroup(nodes, groupDatas) {
        $.each(nodes, function (i, node) {
          if (node.data && node.data.type == 1) {
            groupDatas.push(node.data);
          }
          if (node.children) {
            extractRuleGroup(node.children, groupDatas);
          }
        });
      }

      // 规则定义转化为树结点
      function ruleDefinition2TreeNodes(ruleDefinition) {
        var nodes = [];
        convertRuleDefinition2TreeNodes(ruleDefinition, nodes);
        return nodes;
      }
      function convertRuleDefinition2TreeNodes(ruleDefinition, nodes) {
        $.each(ruleDefinition, function (i, rule) {
          var node = {
            id: rule.id,
            name: rule.name,
            data: rule,
            children: []
          };
          nodes.push(node);
          // 子结点
          if (rule.rules) {
            convertRuleDefinition2TreeNodes(rule.rules, node.children);
          }
        });
      }
      // 树结点转化为规则定义
      function treeNodes2RuleDefinition(ruleDefinition) {
        var ruleTree = getRuleTree();
        var ruleDefinition = [];
        var nodes = ruleTree.getNodes();
        convertTreeNodes2RuleDefinition(nodes, ruleDefinition);
        return JSON.stringify(ruleDefinition);
      }
      function convertTreeNodes2RuleDefinition(nodes, ruleDefinition) {
        $.each(nodes, function (i, node) {
          var data = node.data;
          var definition = {};
          if (data.type == 1) {
            definition = {
              id: data.id,
              name: data.name,
              type: 1,
              connector: data.connector,
              rules: []
            };
          } else {
            definition = {
              id: data.id,
              name: data.name,
              type: 2,
              ruleType: data.ruleType,
              fieldName: data.fieldName,
              operator: data.operator,
              fieldValue: data.fieldValue,
              roleName: data.roleName,
              roleId: data.roleId,
              connector: data.connector
            };
          }
          ruleDefinition.push(definition);
          // 子结点
          if (node.children) {
            convertTreeNodes2RuleDefinition(node.children, definition.rules);
          }
        });
      }
      $('#typeName')
        .wSelect2({
          data: [
            { id: '1', text: '数据库表' },
            { id: '2', text: '数据库视图' }
          ],
          valueField: 'type', // 下拉框值回填input的ID
          labelField: 'typeName',
          remoteSearch: false
        })
        .change(function () {
          var type = $('#type').val();
          $('.data-type').hide();
          $('.data-type-' + type).show();
        });
      // 数据库表下拉框
      $('#tableName').wSelect2({
        serviceName: 'cdDataStoreDefinitionService',
        queryMethod: 'loadSelectDataByTable',
        remoteSearch: false
      });

      // 数据库视图下拉框
      $('#viewName').wSelect2({
        serviceName: 'cdDataStoreDefinitionService',
        queryMethod: 'loadSelectDataByView',
        remoteSearch: false
      });
      var connectorData = [
        { id: 'and', text: '并且' },
        { id: 'or', text: '或者' }
      ];
      $('#groupConnector').wSelect2({
        data: connectorData,
        valueField: 'groupConnector',
        remoteSearch: false
      });
      $('#ruleConnector').wSelect2({
        data: connectorData,
        valueField: 'ruleConnector',
        remoteSearch: false
      });
      $('#operator').wSelect2({
        data: [
          { id: '<', text: '小于' },
          { id: '<=', text: '小于等于' },
          { id: '>', text: '大于' },
          { id: '>=', text: '大于等于' },
          { id: '=', text: '等于' },
          { id: '<>', text: '不等于' },
          { id: 'like', text: '包含' },
          { id: 'not like', text: '不包含' }
        ],
        valueField: 'operator',
        remoteSearch: false
      });
      // 角色列表
      $('#roleId').wSelect2({
        serviceName: 'dmsDataPermissionDefinitionFacadeService',
        queryMethod: 'loadRolesSelectData',
        remoteSearch: false
      });

      $('#ruleType')
        .wSelect2({
          data: [
            { id: '1', text: '角色归属' },
            { id: '2', text: '字段值比较' }
          ],
          valueField: 'ruleType',
          remoteSearch: false
        })
        .on('change', function () {
          var ruleType = $(this).val();
          $('.rule-type').hide();
          $('.rule-type-' + ruleType).show();
        })
        .trigger('change');

      $('#permission_btn_save').on('click', function () {
        if (!validator.form()) {
          return false;
        }
        $(form_selector).form2json(bean);
        if (bean.type == '1') {
          bean.dataName = bean.tableName;
        } else {
          bean.dataName = bean.viewName;
        }
        var ruleDefinition = treeNodes2RuleDefinition();
        // 检查数据规则的有效性
        if (!checkRuleDefinition(JSON.parse(ruleDefinition))) {
          return;
        }
        bean.ruleDefinition = ruleDefinition;
        JDS.call({
          service: 'dmsDataPermissionDefinitionFacadeService.saveDto',
          data: bean,
          version: '',
          success: function (result) {
            appModal.success('保存成功！');
            appContext.getNavTabWidget().closeTab();
          }
        });
      });

      // 检查数据规则的有效性
      function checkRuleDefinition(ruleDefinition) {
        if (ruleDefinition == null || ruleDefinition.length == 0) {
          return true;
        }
        for (var i = 0; i < ruleDefinition.length; i++) {
          if (i == 0) {
            if (StringUtils.isNotBlank(ruleDefinition[i].connector)) {
              if (ruleDefinition[i].type == 1) {
                appModal.error('分组[' + ruleDefinition[i].name + ']不能配置连接符！');
              } else {
                appModal.error('规则[' + ruleDefinition[i].name + ']不能配置连接符！');
              }
              return false;
            }
          } else {
            if (StringUtils.isBlank(ruleDefinition[i].connector)) {
              if (ruleDefinition[i].type == 1) {
                appModal.error('分组[' + ruleDefinition[i].name + ']连接符为空！');
              } else {
                appModal.error('规则[' + ruleDefinition[i].name + ']连接符为空！');
              }
              return false;
            }
          }
          if (ruleDefinition[i].rules && !checkRuleDefinition(ruleDefinition[i].rules)) {
            return false;
          }
        }
        return true;
      }

      $('#btn_rule_group_add').on('click', function () {
        // 选择分组结点判断
        var selectedNode = getRuleTreeSelectedNode(false);
        if (selectedNode != null && selectedNode.data.type != 1) {
          appModal.error('请先选择分组！');
          return;
        }
        $('.tab-rule').hide();
        $('.tab-rule-group').show();
        $.common.json.clearJson(ruleGroupBean, true);
        $(rule_group_form_selector).clearForm(true);
        // 重置上级分组下拉选择框
        resetParentGroupSelectElem();
        // 第一个分组隐藏连接符
        if (selectedNode == null) {
          var ruleTree = getRuleTree();
          var nodes = ruleTree.getNodes();
          if (nodes == null || nodes.length == 0) {
            $('.row-group-connector').hide();
          } else {
            $('.row-group-connector').show();
          }
        } else if (!selectedNode.children || selectedNode.children.length == 0) {
          $('.row-group-connector').hide();
        } else {
          $('.row-group-connector').show();
        }
      });
      // 规则分组确定处理
      $('#btn_rule_group_ok').on('click', function () {
        if (!ruleGroupValidator.form()) {
          return false;
        }
        $(rule_group_form_selector).form2json(ruleGroupBean);
        if (StringUtils.isBlank(ruleGroupBean.groupId)) {
          ruleGroupBean.groupId = new Date().getTime();
          $('#groupId', rule_group_form_selector).val(ruleGroupBean.groupId);
        }
        var treeNode = ruleGroupBean2TreeNode(ruleGroupBean);
        var parentNode = getRuleTreeNodeById($('#parentGroupId').val());
        var currentNode = getRuleTreeNodeById(treeNode.id);
        var ruleTree = getRuleTree();
        // 新增
        if (currentNode == null) {
          ruleTree.addNodes(parentNode, [treeNode]);
        } else {
          // 更新
          currentNode.name = treeNode.name;
          currentNode.data = treeNode.data;
          ruleTree.updateNode(currentNode);
          // 更新DiyDom内容
          updateDiyDom(currentNode);
        }
      });
      // 树点击
      function zTreeBeforeClick(treeId, treeNode, clickFlag) {
        var type = treeNode.data.type;
        // 规则分组
        if (type == 1) {
          ruleGroupBean = treeNode2RuleGroupBean(treeNode);
          $(rule_group_form_selector).json2form(ruleGroupBean);
          resetParentGroupSelectElem();
          // 更新分组上级结点
          if (treeNode.getParentNode() != null) {
            $('#parentGroupId').val(treeNode.getParentNode().id);
          } else {
            $('#parentGroupId').val('');
          }
          $('#parentGroupId').trigger('change');
          $('.tab-rule').hide();
          $('.tab-rule-group').show();
          // 第一个分组隐藏连接符
          if (treeNode.getPreNode() != null || StringUtils.isNotBlank(ruleGroupBean.groupConnector)) {
            $('.row-group-connector').show();
          } else {
            $('.row-group-connector').hide();
          }
        } else {
          ruleDefBean = treeNode2RuleDefBean(treeNode);
          $(rule_def_form_selector).json2form(ruleDefBean);
          // 规则定义
          $('.tab-rule').hide();
          $('.tab-rule-definition').show();
          $('#fieldName').trigger('change');
          $('#ruleType').trigger('change');
          $('#roleId').trigger('change');
          $('#operator').trigger('change');
          $('#ruleConnector').trigger('change');
          // 第一个规则隐藏连接符
          if (treeNode.getPreNode() != null || StringUtils.isNotBlank(ruleDefBean.ruleConnector)) {
            $('.row-rule-connector').show();
          } else {
            $('.row-rule-connector').hide();
          }
        }
      }
      function getRuleTreeNodeById(groupId) {
        if (StringUtils.isBlank(groupId)) {
          return null;
        }
        var ruleTree = getRuleTree();
        return ruleTree.getNodeByParam('id', groupId, null);
      }
      function resetParentGroupSelectElem() {
        var ruleGroups = getRuleGroupDatas();
        var datas = [];
        $.each(ruleGroups, function (i, ruleGroup) {
          datas.push({ id: ruleGroup.id, text: ruleGroup.name });
        });
        $('#parentGroupId').wSelect2({
          data: datas,
          valueField: 'parentGroupId', // 下拉框值回填input的ID
          remoteSearch: true
        });

        // 选中上级分组
        var selectedNode = getRuleTreeSelectedNode(false);
        if (selectedNode != null && selectedNode.data && selectedNode.data.type == 1) {
          $('#parentGroupId').val(selectedNode.id).trigger('change');
        }
      }
      function ruleGroupBean2TreeNode(ruleGroupBean) {
        var treeNode = {
          id: ruleGroupBean.groupId,
          name: ruleGroupBean.groupName,
          data: {
            id: ruleGroupBean.groupId,
            name: ruleGroupBean.groupName,
            type: 1,
            connector: ruleGroupBean.groupConnector
          }
        };
        return treeNode;
      }
      function treeNode2RuleGroupBean(treeNode) {
        var data = treeNode.data;
        var tmpRuleGroupBean = {
          groupId: data.id, // 分组ID
          groupName: data.name, // 分组名称
          type: data.type, // 规则类型
          groupConnector: data.connector
          // 连接符
        };
        return tmpRuleGroupBean;
      }
      // 新增规则定义
      $('#btn_rule_def_add').on('click', function () {
        // 选择分组结点判断
        var selectedNode = getRuleTreeSelectedNode(false);
        if (selectedNode == null || selectedNode.data.type != 1) {
          appModal.error('请选择分组结点！');
          return;
        }
        $('.tab-rule').hide();
        $('.tab-rule-definition').show();
        $.common.json.clearJson(ruleDefBean, true);
        $(rule_def_form_selector).clearForm(true);
        $('#ruleType').trigger('change');
        $('#roleId').trigger('change');
        // 第一个规则隐藏连接符
        if (!selectedNode.children || selectedNode.children.length == 0) {
          $('.row-rule-connector').hide();
        } else {
          $('.row-rule-connector').show();
        }
      });
      // 规则定义确定处理
      $('#btn_rule_def_ok')
        .off()
        .on('click', function () {
          if (!ruleDefValidator.form()) {
            return false;
          }
          $(rule_def_form_selector).form2json(ruleDefBean);
          if (StringUtils.isBlank(ruleDefBean.ruleId)) {
            ruleDefBean.ruleId = new Date().getTime();
            $('#ruleId', rule_def_form_selector).val(ruleDefBean.ruleId);
          }
          var treeNode = ruleDefBean2TreeNode(ruleDefBean);
          var parentNode = getRuleTreeSelectedNode(false);
          var currentNode = getRuleTreeNodeById(treeNode.id);
          var ruleTree = getRuleTree();
          // 新增
          if (currentNode == null) {
            ruleTree.addNodes(parentNode, [treeNode]);
          } else {
            // 更新
            currentNode.name = treeNode.name;
            currentNode.data = treeNode.data;
            ruleTree.updateNode(currentNode);
            // 更新DiyDom内容
            updateDiyDom(currentNode);
          }
        });
      // 删除规则
      $('#btn_rule_delete').on('click', function () {
        var selectedNode = getRuleTreeSelectedNode();
        if (selectedNode == null) {
          return;
        }

        appModal.confirm('确定要临时删除(保存后生效)所选节点吗?', function (result) {
          if (result) {
            var ruleTree = getRuleTree();
            ruleTree.removeNode(selectedNode);
          }
        });
        $('.tab-rule').hide();
      });

      function ruleDefBean2TreeNode(ruleDefBean) {
        var treeNode = {
          id: ruleDefBean.ruleId,
          name: ruleDefBean.ruleName,
          data: {
            id: ruleDefBean.ruleId,
            name: ruleDefBean.ruleName,
            type: 2,
            ruleType: ruleDefBean.ruleType,
            fieldName: ruleDefBean.fieldName,
            operator: ruleDefBean.operator,
            fieldValue: ruleDefBean.fieldValue,
            roleName: ruleDefBean.roleName,
            roleId: ruleDefBean.roleId,
            connector: ruleDefBean.ruleConnector
          }
        };
        return treeNode;
      }
      function treeNode2RuleDefBean(treeNode) {
        var data = treeNode.data;
        var tmpRuleDefBean = {
          ruleId: data.id,
          ruleName: data.name,
          type: data.type,
          ruleType: data.ruleType,
          fieldName: data.fieldName,
          operator: data.operator,
          fieldValue: data.fieldValue,
          roleName: data.roleName,
          roleId: data.roleId,
          ruleConnector: data.connector
        };
        return tmpRuleDefBean;
      }
    },
    refresh: function () {
      this.init();
    }
  });
  return AppDmsDataPerWidgetDevelopment;
});
