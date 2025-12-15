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

  // 平台管理_业务流程管理_业务流程定义过程节点条件
  var AppBizProcessDefinitionProcessNodeConditionDialog = {};

  AppBizProcessDefinitionProcessNodeConditionDialog.show = function (options) {
    var _self = this;
    var title = '过程状态变更条件';
    var dlgOptions = {
      title: title,
      templateId: 'biz-process-definition-node-condition',
      size: 'large',
      shown: function () {
        var $container = $('.process-definition-node-condition');
        _self.init($container, options);
        _self.bindEvent($container);
      },
      buttons: {
        confirm: {
          label: '确定',
          className: 'btn-primary',
          callback: function () {
            var $container = $('.process-definition-node-condition');
            var changedState = $("select[name='changedState']", $container).val();
            var changedStateName = $("select[name='changedState']", $container).find('option:selected').text();
            var $conditions = $("select[name='condition']", $container).find('option');
            var conditions = [];
            var conditionNames = [];
            $.each($conditions, function () {
              var condition = $(this).data('condition');
              conditions.push(condition);
              conditionNames.push(condition.name);
            });
            if ($.isFunction(options.onOk)) {
              options.onOk.call(_self, {
                changedState,
                changedStateName,
                conditionName: changedStateName + '(' + conditionNames.join(' ') + ')',
                configs: conditions
              });
            }
            return true;
          }
        },
        cancel: {
          label: '取消',
          className: 'btn-default'
        }
      }
    };
    appModal.dialog(dlgOptions);
  };
  AppBizProcessDefinitionProcessNodeConditionDialog.init = function ($container, options) {
    var _self = this;
    var stateCondition = options.stateCondition || {};
    var processDefinition = options.processDefinition;
    var processNodes = _self.getProcessNodes(processDefinition);
    var processItems = _self.getProcessItems(processDefinition);
    $.each(processNodes, function (i, processNode) {
      var $option = $('<option/>').text(processNode.name).attr('value', processNode.id);
      $(".condition-type-1 select[name='processNodeId']").append($option);
      $(".condition-type-2 select[name='processNodeId']").append($option.clone());
    });
    $.each(processItems, function (i, processItem) {
      var $option = $('<option/>').text(processItem.itemName).attr('value', processItem.itemCode);
      $(".condition-type-2 select[name='processItemCode']").append($option);
    });

    // stateCondition
    if (stateCondition.changedState) {
      $("select[name='changedState']", $container).val(stateCondition.changedState);
    }
    if (stateCondition.configs) {
      $.each(stateCondition.configs, function (i, condition) {
        var $option = $('<option/>').text(condition.name).data('condition', condition);
        $("select[name='condition']", $container).append($option);
      });
    }
  };
  AppBizProcessDefinitionProcessNodeConditionDialog.getProcessNodes = function (processDefinition) {
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
  AppBizProcessDefinitionProcessNodeConditionDialog.getProcessItems = function (processsDefinition) {
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
  AppBizProcessDefinitionProcessNodeConditionDialog.bindEvent = function ($container) {
    // 类型变更
    $("input[name='conditonType']", $container)
      .on('change', function () {
        var type = $("input[name='conditonType']:checked", $container).val();
        $('.condition-type', $container).hide();
        $('.condition-type-' + type, $container).show();
      })
      .trigger('change');
    var collecCondition = function () {
      var type = $("input[name='conditonType']:checked", $container).val();
      var connector = $("select[name='connector']", $container).val();
      var connectorName = $("select[name='connector']", $container).find('option:selected').text();
      var leftBracket = $("select[name='leftBracket']:visible", $container).val();
      var processNodeId = $("select[name='processNodeId']:visible", $container).val();
      var processNodeName = $("select[name='processNodeId']:visible", $container).find('option:selected').text();
      var processNodeState = $("select[name='processNodeState']", $container).val();
      var processNodeStateName = $("select[name='processNodeState']", $container).find('option:selected').text();
      var processItemCode = $("select[name='processItemCode']", $container).val();
      var processItemName = $("select[name='processItemCode']", $container).find('option:selected').text();
      var rightBracket = $("select[name='rightBracket']:visible", $container).val();
      var conditionName = '';
      if (type == '1') {
        conditionName = leftBracket + processNodeName + ' ' + processNodeStateName + rightBracket;
      } else if (type == '2') {
        conditionName = leftBracket + processNodeName + ' ' + processItemName + rightBracket;
      } else if (type == '9') {
        conditionName = connectorName;
      }
      return {
        name: conditionName,
        type,
        connector,
        leftBracket,
        processNodeId,
        processNodeName,
        processNodeState,
        processNodeStateName,
        processItemCode,
        processItemName,
        rightBracket
      };
    };
    var condition2Form = function (condition) {
      $('.condition-type', $container).hide();
      $('.condition-type-' + condition.type, $container).show();

      $(`input[name='conditonType'][value='${condition.type}']`, $container).attr('checked', 'checked');
      $("select[name='connector']", $container).val(condition.connector);
      $("select[name='leftBracket']:visible", $container).val(condition.leftBracket);
      $("select[name='processNodeId']:visible", $container).val(condition.processNodeId);
      $("select[name='processNodeState']", $container).val(condition.processNodeState);
      $("select[name='processItemCode']", $container).val(condition.processItemCode);
      $("select[name='rightBracket']:visible", $container).val(condition.rightBracket);
    };
    // 添加
    $('button[name="btn_add"]', $container).on('click', function () {
      var condition = collecCondition();
      var $option = $('<option/>').text(condition.name).data('condition', condition);
      $("select[name='condition']", $container).append($option);
    });

    // 更新
    $('button[name="btn_update"]', $container).on('click', function () {
      var condition = collecCondition();
      $("select[name='condition']", $container).find('option:selected').text(condition.name).data('condition', condition);
    });
    // 删除
    $('button[name="btn_delete"]', $container).on('click', function () {
      $("select[name='condition']", $container).find('option:selected').remove();
    });
    $("select[name='condition']", $container).on('click', 'option', function () {
      var condition = $(this).data('condition');
      condition2Form(condition);
    });
  };
  return AppBizProcessDefinitionProcessNodeConditionDialog;
});
