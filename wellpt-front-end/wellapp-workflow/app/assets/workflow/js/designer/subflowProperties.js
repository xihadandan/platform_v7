goWorkFlow = parent.goWorkFlow;
var x2js;

//设置子流程属性
function setSubFlowProperty() {
  if (goWorkFlow.curSubflow) {
    subFlowLoadEvent(goWorkFlow.curSubflow.xmlObject);
  }
  if (goWorkFlow.readonlyMode) {
    setFrameReadOnly(window);
  }
  top.appModal.hideMask();
}

function subFlowLoadEvent(subFlowProperty) {
  x2js = new X2JS();
  var goForm = $('#subflowForm')[0];
  subFlowBaseProperty(subFlowProperty, goForm);
  subFlowInfoProperty(subFlowProperty, goForm);
}

function subFlowBaseProperty(subFlowProperty, goForm) {
  goForm.name.value = subFlowProperty.attr('name');
  $('#name', goForm).on('input propertychange', function () {
    changeDesignerObjText();
  });
  goForm.id.value = subFlowProperty.attr('id');
  if (!subFlowProperty.attr('initId')) {
    subFlowProperty.attr('initId', goForm.id.value);
  }
  // ID显示为文本
  if ($(subFlowProperty).children().length > 0) {
    if ($(subFlowProperty).data('idAsLabel') === '0') {
    } else {
      $('#id', goForm).hide();
      $('<div>' + $('#id', goForm).val() + '</div>').insertAfter($('#id', goForm));
    }
  } else {
    $(subFlowProperty).data('idAsLabel', '0');
  }
  $('#id', goForm).on('change', function () {
    var initId = subFlowProperty.attr('initId');
    if (initId !== goForm.id.value) {
      goWorkFlow.changeNodeIds[initId] = {
        initId: initId,
        modifyId: goForm.id.value,
        name: goForm.name.value
      };
    } else {
      top.setModifyTaskStatusById(goWorkFlow.changeNodeIds[initId].modifyId, 'normal');
      delete goWorkFlow.changeNodeIds[initId];
    }
  });
  goForm.businessType.value = subFlowProperty.selectSingleNode('businessType')
    ? subFlowProperty.selectSingleNode('businessType').text()
    : '';
  goForm.businessRole.value = subFlowProperty.selectSingleNode('businessRole')
    ? subFlowProperty.selectSingleNode('businessRole').text()
    : '';
  $.get({
    url: ctx + '/api/workflow/definition/getBusinessTypes',
    success: function (result) {
      var data = $.map(result.data, function (item) {
        return {
          id: item.value,
          text: item.label
        };
      });
      $('#businessType')
        .wellSelect({
          data: data,
          searchable: false
        })
        .on('change', function () {
          var type = $(this).val();
          loadBusinessRole(subFlowProperty, goForm, type);
        })
        .trigger('change');
    }
  });

  var newFlows = subFlowProperty.selectSingleNode('newFlows');
  if (newFlows) {
    var newFlowArr = newFlows.selectNodes('newFlow');
    $.each(newFlowArr, function (i, item) {
      var newFlow = x2js.xml2js(item.outerHTML).newFlow;
      renderNewFlows(newFlow, i + 1);
    });
  }
  $('#DNewFlows').on('click', 'li .btn-set', function () {
    var data = $(this).closest('li').data('newFlow');
    data._index = $(this).closest('li').attr('data-index');
    openNewFlowDialog(subFlowProperty, goForm, data);
  });
  $('#DNewFlows').on('click', 'li .btn-remove', function () {
    $(this).closest('li').remove();
  });
  $('#addDNewFlows').on('click', function () {
    var index = $('#DNewFlows > li').length ? $('#DNewFlows > li:last').attr('data-index') : 0;
    openNewFlowDialog(subFlowProperty, goForm, null, index);
  });

  var relations = subFlowProperty.selectSingleNode('relations');
  if (relations) {
    var relationArr = relations.selectNodes('relation');
    $.each(relationArr, function (i, item) {
      var relation = x2js.xml2js(item.outerHTML).relation;
      renderRelations(relation, i + 1);
    });
  }
  $('#DRelations').on('click', 'li .btn-set', function () {
    var data = $(this).closest('li').data('relation');
    data._index = $(this).closest('li').attr('data-index');
    openRelationDialog(subFlowProperty, goForm, data);
  });
  $('#DRelations').on('click', 'li .btn-remove', function () {
    $(this).closest('li').remove();
  });
  $('#addDRelations').on('click', function () {
    var index = $('#DRelations > li').length ? $('#DRelations > li:last').attr('data-index') : 0;
    openRelationDialog(subFlowProperty, goForm, null, index);
  });

  $('#DsubTaskMonitors').on('click', function () {
    SelectUsers('unit/field/task/option', 'subTaskMonitor', '选择跟进人员', null, goForm);
  });
  setUnitXMLFieldValue(goForm, subFlowProperty, ['subTaskMonitor']);

  $('#traceTask').wellSelect({
    searchable: false
  });
  initTask(goForm, '#traceTask', goWorkFlow.flowXML.getAttribute('id'), false, false, false);
  var traceTask = subFlowProperty.selectSingleNode('traceTask');
  if (traceTask) {
    $('#traceTask').wellSelect('val', traceTask.text());
  }

  $('input:radio[name="childLookParent"]').click(function () {
    var checkValue = $('input:radio[name="childLookParent"]:checked').val();
    subFlowProperty.attr('childLookParent', checkValue);
  });

  if (!subFlowProperty.attr('childLookParent')) {
    $('#childLookParent1').attr('checked', true);
    subFlowProperty.attr('childLookParent', '0');
  } else {
    var checkValue = subFlowProperty.attr('childLookParent');
    if (checkValue == '0') {
      $('#childLookParent1').attr('checked', true);
    } else {
      $('#childLookParent2').attr('checked', true);
    }
  }

  $('#parentSetChild').change(function () {
    if ($('#parentSetChild').prop('checked')) {
      subFlowProperty.attr('parentSetChild', '1');
    } else {
      subFlowProperty.attr('parentSetChild', '0');
    }
  });

  if (!subFlowProperty.attr('parentSetChild')) {
    subFlowProperty.attr('parentSetChild', '0');
  } else {
    var parentSetChildVal = subFlowProperty.attr('parentSetChild');
    if (parentSetChildVal == '1') {
      $('#parentSetChild').attr('checked', true);
    }
  }

  $('#expandList').change(function () {
    if ($('#expandList').prop('checked')) {
      subFlowProperty.attr('expandList', '1');
    } else {
      subFlowProperty.attr('expandList', '0');
    }
  });

  if (!subFlowProperty.attr('expandList')) {
    subFlowProperty.attr('expandList', '1');
    $('#expandList').prop('checked', true);
  } else {
    const expandList = subFlowProperty.attr('expandList');
    if (expandList === '1') {
      $('#expandList').prop('checked', true);
    } else {
      $('#expandList').prop('checked', false);
    }
  }
}

function renderNewFlows(newFlow, index) {
  var $li = $('<li data-index="' + index + '"></li>');
  $li.data('newFlow', newFlow);
  var html =
    '<div class="content">' +
    '<div class="name">流程名称：' +
    newFlow.name +
    '</div>' +
    '<div class="remark">标签：' +
    newFlow.label +
    '</div>' +
    '<div class="toTaskName">提交环节：' +
    newFlow.toTaskName +
    '</div>' +
    '<div class="isMajor">角色：' +
    (newFlow.isMajor === '1' ? '主办' : '协办') +
    '</div>' +
    '</div>' +
    '<div class="btn-group">' +
    '<div class="well-btn w-btn-primary well-btn-sm w-noLine-btn btn-set" title="设置">' +
    '<i class="iconfont icon-ptkj-shezhi"></i>' +
    '</div>' +
    '<div class="well-btn w-btn-primary well-btn-sm w-noLine-btn btn-remove" title="删除">' +
    '<i class="iconfont icon-ptkj-shanchu"></i>' +
    '</div>' +
    '</div>';
  $li.append(html);
  $('#DNewFlows').append($li);
}

function renderRelations(relation, index) {
  var $li = $('<li data-index="' + index + '"></li>');
  $li.data('relation', relation);
  var html =
    '<div class="content">' +
    '<div class="newFlowName">子流程：' +
    relation.newFlowName +
    '</div>' +
    '<div class="taskName">环节：' +
    relation.taskName +
    '</div>' +
    '<div class="frontNewFlowName">前置子流程：' +
    relation.frontNewFlowName +
    '</div>' +
    '<div class="frontTaskName">前置环节：' +
    relation.frontTaskName +
    '</div>' +
    '</div>' +
    '<div class="btn-group">' +
    '<div class="well-btn w-btn-primary well-btn-sm w-noLine-btn btn-set" title="设置">' +
    '<i class="iconfont icon-ptkj-shezhi"></i>' +
    '</div>' +
    '<div class="well-btn w-btn-primary well-btn-sm w-noLine-btn btn-remove" title="删除">' +
    '<i class="iconfont icon-ptkj-shanchu"></i>' +
    '</div>' +
    '</div>';
  $li.append(html);
  $('#DRelations').append($li);
}

function loadBusinessRole(subFlowProperty, goForm, type) {
  $.get({
    url: ctx + '/api/workflow/definition/getBusinessRoles',
    data: {
      businessType: type
    },
    success: function (result) {
      var ids = [];
      var data = $.map(result.data, function (item) {
        ids.push(item.value);
        return {
          id: item.value,
          text: item.label
        };
      });
      var _val = $('#businessRole').val();
      if (ids.indexOf(_val) < 0) {
        $('#businessRole').val('');
      }
      if ($('#businessRole').data('wellSelect')) {
        $('#businessRole')
          .wellSelect('reRenderOption', {
            data: data
          })
          .trigger('change');
      } else {
        $('#businessRole').wellSelect({
          data: data,
          searchable: false
        });
      }
    }
  });
}

function openNewFlowDialog(subFlowProperty, goForm, data, index) {
  var _html = get_newFlow_dialog_html();
  var $dialog = top.appModal.dialog({
    title: '子流程',
    message: _html,
    height: '600px',
    size: 'large',
    zIndex: 999999998,
    shown: function () {
      newFlowLoadEvent($dialog, goForm, subFlowProperty, data, index);
    },
    buttons: {
      save: {
        label: '保存',
        className: 'well-btn w-btn-primary',
        callback: function () {
          if (!$('#newFlowValue', $dialog).val()) {
            top.appModal.error('请选择流程名称！');
            return false;
          }
          var result = NewFlowOKEvent($dialog);
          if (data) {
            var $li = $('#DNewFlows > li[data-index="' + data._index + '"]');
            $li.data('newFlow', result);
            $li.find('.name').text('流程名称：' + result.name);
            $li.find('.remark').text('标签：' + result.label);
            $li.find('.toTaskName').text('提交环节：' + result.toTaskName);
            $li.find('.isMajor').text('角色：' + (result.isMajor === '1' ? '主办' : '协办'));
          } else {
            renderNewFlows(result, index + 1);
          }
        }
      },
      close: {
        label: '取消',
        className: 'btn-default',
        callback: function () {
        }
      }
    }
  });
}

function get_newFlow_dialog_html() {
  return (
    '<form id="newFlowForm" name="newFlowForm" class="workflow-popup form-widget well-workflow-form">' +
    '<div class="well-form form-horizontal">' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label">流程名称</label>' +
    '<div class="well-form-control">' +
    '<input type="hidden" class="form-control" id="newFlowId" name="newFlowId">' +
    '<input type="hidden" class="form-control" id="newFlowValue" name="newFlowValue">' +
    '<input type="hidden" class="form-control" id="newFlowName" name="newFlowName">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label">流程标签</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="newFlowLabel" name="newFlowLabel">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label">分发条件</label>' +
    '<div class="well-form-control">' +
    '<div class="multiBox">' +
    '<ul id="conditions" class="work-flow-settings"></ul>' +
    '<div id="addDistributeNodeTypes" class="well-btn w-btn-primary w-noLine-btn well-btn-sm">' +
    '<i class="iconfont icon-ptkj-jiahao"></i>添加分发条件' +
    '</div>' +
    '</div>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label">流程实例</label>' +
    '<div class="well-form-control">' +
    '<div class="form-group inner-form-group">' +
    '<label class="well-form-label control-label">来源</label>' +
    '<input type="text" class="form-control" id="createWay" name="createWay">' +
    '<input type="hidden" class="form-control" id="createWayName" name="createWayName">' +
    '</div>' +
    '<div class="multiBox clear">' +
    '<div class="fl createWay-field" style="width: 50%">' +
    '<div class="form-group inner-form-group">' +
    '<label class="well-form-label control-label">表单</label>' +
    '<input type="text" class="form-control" id="createInstanceFormId" name="createInstanceFormId">' +
    '<input type="hidden" class="form-control" id="createInstanceFormName" name="createInstanceFormName">' +
    '</div>' +
    '</div>' +
    '<div class="fl createWay-field" style="width: 50%">' +
    '<div class="form-group inner-form-group">' +
    '<label class="well-form-label control-label">字段</label>' +
    '<input type="text" class="form-control" id="taskUsers" name="taskUsers">' +
    '<input type="hidden" class="form-control" id="taskUsersName" name="taskUsersName">' +
    '</div>' +
    '</div>' +
    '<div class="fl createWay-field" style="width: 50%">' +
    '<div class="form-group inner-form-group" style="margin-bottom: 0">' +
    '<label class="well-form-label control-label">创建方式</label>' +
    '<input type="text" class="form-control" id="createInstanceWay" name="createInstanceWay">' +
    '<input type="hidden" class="form-control" id="createInstanceWayName" name="createInstanceWayName">' +
    '</div>' +
    '</div>' +
    '<div class="fl createWay-subform" style="display:none; width: 50%;padding-left: 52px">' +
    '<input type="checkbox" name="createInstanceBatch" id="createInstanceBatch" value="1">' +
    '<label class="checkbox-inline" for="createInstanceBatch">按从表行分批次生成实例</label>' +
    '</div>' +
    '<div class="createWay-interface" style="display: none">' +
    '<div class="form-group inner-form-group" style="margin: 0">' +
    '<label class="well-form-label control-label">接口</label>' +
    '<input type="text" class="form-control" id="interface" name="interface">' +
    '<input type="hidden" class="form-control" id="interfaceName" name="interfaceName">' +
    '</div>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<input type="checkbox" name="isMajor" id="isMajor" value="0">' +
    '<label class="checkbox-inline" for="isMajor">主办</label>' +
    // '<input type="checkbox" name="isMerge" id="isMerge" value="0">' +
    // '<label class="checkbox-inline" for="isMerge">合并流程</label>' +
    '<input type="checkbox" name="isWait" id="isWait" value="0">' +
    '<label class="checkbox-inline" for="isWait">等待流程</label>' +
    '<input type="checkbox" name="isShare" id="isShare" value="0">' +
    '<label class="checkbox-inline" for="isShare">共享流程</label>' +
    '<input type="checkbox" name="notifyDoing" id="notifyDoing" value="1">' +
    '<label class="checkbox-inline" for="notifyDoing">办结通知其他子流程</label>' +
    '</div>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '  <label class="well-form-label control-label">' +
    '    <span>流程标题</span>' +
    '  </label>' +
    '  <div class="well-form-control">' +
    '    <div class="form-group ">' +
    '      <input type="radio" name="title_expression" id="title_expression_default" value="default" checked="checked"/>' +
    '      <label class="radio-inline" for="title_expression_default">默认</label>' +
    '      <input type="radio" name="title_expression" id="title_expression_custom" value="custom" />' +
    '      <label class="radio-inline" for="title_expression_custom">自定义标题格式</label>' +
    '    </div>' +
    '    <div class="multiBox clear showCustomTitle" style="display: none;">' +
    '      <div id="set_title_expression" class="well-btn w-btn-primary well-btn-sm w-noLine-btn" title="设置">\n' +
    '        <i class="iconfont icon-ptkj-shezhi"></i>\n' +
    '        <span>设置</span>\n' +
    '      </div>\n' +
    '      <span style="font-size: 13px; margin-left: -10px">自定义标题为空时，等同于默认，保存后显示为默认</span>\n' +
    '    </div>' +
    '    <div class="multiBox clear showCustomTitle" style="display: none;">' +
    '      <div id="title_expression_text"></div>' +
    '    </div>' +
    '    <div class="multiBox clear showCustomTitle_default">' +
    '      <div id="title_expression_text_default">${流程名称}_${子流程实例办理人}</div>' +
    '    </div>' +
    '  </div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label">提交环节</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="toTaskId" name="toTaskId">' +
    '<input type="hidden" class="form-control" id="toTaskName" name="toTaskName">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label">拷贝信息</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="copyBotRuleId" name="copyBotRuleId">' +
    '<input type="hidden" class="form-control" id="copyBotRuleName" name="copyBotRuleName">' +
    '</div>' +
    '<div class="well-form-right">' +
    '<div id="copyBotRuleIdView" class="well-btn w-btn-primary">查看</div>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label">实时同步</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="syncBotRuleId" name="syncBotRuleId">' +
    '<input type="hidden" class="form-control" id="syncBotRuleName" name="syncBotRuleName">' +
    '</div>' +
    '<div class="well-form-right">' +
    '<div id="syncBotRuleIdView" class="well-btn w-btn-primary">查看</div>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label">信息反馈父流程</label>' +
    '<div class="well-form-control">' +
    '<input type="checkbox" name="returnWithOver" id="returnWithOver" value="0">' +
    '<label class="checkbox-inline" for="returnWithOver">办结时反馈</label>' +
    '<input type="checkbox" name="returnWithDirection" id="returnWithDirection" value="0">' +
    '<label class="checkbox-inline" for="returnWithDirection">指定反馈流向</label>' +
    '</div>' +
    '</div>' +
    '<div class="form-group return-with-direction" style="display:none">' +
    '<label class="well-form-label control-label">反馈流向</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="returnDirectionId" name="returnDirectionId">' +
    '<input type="hidden" class="form-control" id="returnDirectionName" name="returnDirectionName">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label">反馈信息</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="returnBotRuleId" name="returnBotRuleId">' +
    '<input type="hidden" class="form-control" id="returnBotRuleName" name="returnBotRuleName">' +
    '</div>' +
    '<div class="well-form-right">' +
    '<div id="returnBotRuleIdView" class="well-btn w-btn-primary">查看</div>' +
    '</div>' +
    '</div>' +
    '<div class="form-group clearfix" style="margin-top: 15px;">' +
    '<label for="">办理信息展示' +
    '<div class="w-tooltip">' +
    '<i class="iconfont icon-ptkj-tishishuoming"></i>' +
    '<div class="w-tooltip-content">设置子流程实例中，办理进度、信息分发和操作记录等表格的展示位置。注意：只有主流程和子流程表单相同时，同主流程显示位置才生效。</div>' +
    '</div>' +
    '</label>' +
    '</div>' +
    '<div class="form-group">' +
    '	<label class="well-form-label control-label">办理进度</label>' +
    '	<div class="controls well-form-control" style="width: 300px;">' +
    '		<input type="radio" name="undertakeSituationPlaceHolder" id="undertakeSituationPlaceHolder_1" value="1" checked="checked">' +
    '		<label class="radio-inline" for="undertakeSituationPlaceHolder_1">同主流程显示位置</label>' +
    '		<input type="radio" name="undertakeSituationPlaceHolder" id="undertakeSituationPlaceHolder_2" value="2">' +
    '		<label class="radio-inline" for="undertakeSituationPlaceHolder_2">指定显示位置</label>' +
    '	</div>' +
    '	<div class="well-form-right undertakeSituationPlaceHolder undertakeSituationPlaceHolder-2" style="width: 250px;">' +
    '		<input type="text" class="form-control" id="undertakeSituationPlaceHolderValue" name="undertakeSituationPlaceHolderValue">' +
    '		<input type="hidden" class="form-control" id="undertakeSituationPlaceHolderName" name="undertakeSituationPlaceHolderName">' +
    '	</div>' +
    '</div>' +
    '<div class="form-group">' +
    '	<label class="well-form-label control-label">信息分发</label>' +
    '	<div class="controls well-form-control" style="width: 300px;">' +
    '		<input type="radio" name="infoDistributionPlaceHolder" id="infoDistributionPlaceHolder_1" value="1" checked="checked">' +
    '		<label class="radio-inline" for="infoDistributionPlaceHolder_1">同主流程显示位置</label>' +
    '		<input type="radio" name="infoDistributionPlaceHolder" id="infoDistributionPlaceHolder_2" value="2">' +
    '		<label class="radio-inline" for="infoDistributionPlaceHolder_2">指定显示位置</label>' +
    '	</div>' +
    '	<div class="well-form-right infoDistributionPlaceHolder infoDistributionPlaceHolder-2" style="width: 250px;">' +
    '		<input type="text" class="form-control" id="infoDistributionPlaceHolderValue" name="infoDistributionPlaceHolderValue">' +
    '		<input type="hidden" class="form-control" id="infoDistributionPlaceHolderName" name="infoDistributionPlaceHolderName">' +
    '	</div>' +
    '</div>' +
    '<div class="form-group">' +
    '	<label class="well-form-label control-label">操作记录</label>' +
    '	<div class="controls well-form-control" style="width: 300px;">' +
    '		<input type="radio" name="operationRecordPlaceHolder" id="operationRecordPlaceHolder_1" value="1" checked="checked">' +
    '		<label class="radio-inline" for="operationRecordPlaceHolder_1">同主流程显示位置</label>' +
    '		<input type="radio" name="operationRecordPlaceHolder" id="operationRecordPlaceHolder_2" value="2">' +
    '		<label class="radio-inline" for="operationRecordPlaceHolder_2">指定显示位置</label>' +
    '	</div>' +
    '	<div class="well-form-right operationRecordPlaceHolder operationRecordPlaceHolder-2" style="width: 250px;">' +
    '		<input type="text" class="form-control" id="operationRecordPlaceHolderValue" name="operationRecordPlaceHolderValue">' +
    '		<input type="hidden" class="form-control" id="operationRecordPlaceHolderName" name="operationRecordPlaceHolderName">' +
    '	</div>' +
    '</div>' +
    '</div>' +
    '</form>'
  );
}

function getConditionDialogHtml() {
  return (
    '<form id="newFlowConditionForm" name="newFlowConditionForm" class="workflow-popup form-widget">' +
    '<div class="well-form form-horizontal">' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label">条件类型</label>' +
    '<div class="well-form-control">' +
    '<input type="radio" name="LogicType" id="LogicType_1" checked value="1">' +
    '<label class="radio-inline" for="LogicType_1">通过字段值比较</label>' +
    '<input type="radio" name="LogicType" id="LogicType_2" value="3">' +
    '<label class="radio-inline" for="LogicType_2">按办理人归属</label>' +
    '<input type="radio" name="LogicType" id="LogicType_3" value="5">' +
    '<label class="radio-inline" for="LogicType_3">自定义条件</label>' +
    '<input type="radio" name="LogicType" id="LogicType_4" value="4">' +
    '<label class="radio-inline" for="LogicType_4">逻辑条件</label>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label">条件逻辑关系</label>' +
    '<div class="well-form-control">' +
    '<div style="width: 100px">' +
    '<input type="text" class="form-control" id="AndOr" name="AndOr">' +
    '</div>' +
    '</div>' +
    '</div>' +
    '<div class="condition-wrap">' +
    '<div class="clear">' +
    '<div class="fl" style="width: calc(10% - 5px); margin-right: 5px">' +
    '<input type="text" class="form-control" id="LBracket" name="LBracket">' +
    '</div>' +
    '<div class="fl condition-field LogicValue_1" style="width: calc(30% - 5px); margin-right: 5px">' +
    '<input type="text" class="form-control" id="FieldName" name="FieldName">' +
    '<input type="hidden" class="form-control" id="FieldText" name="FieldText">' +
    '</div>' +
    '<div class="fl condition-field LogicValue_1" style="width: calc(20% - 5px); margin-right: 5px">' +
    '<input type="text" class="form-control" id="Operator" name="Operator">' +
    '<input type="hidden" class="form-control" id="OperatorName" name="OperatorName">' +
    '</div>' +
    '<div class="fl condition-field LogicValue_1" style="width: 30%; margin-right: 5px">' +
    '<input type="text" class="form-control" id="Value" name="Value">' +
    '</div>' +
    '<div class="fl condition-field LogicValue_3" style="width: calc(35% - 5px); margin-right: 5px;display: none">' +
    '<input type="text" class="form-control" id="GroupType" name="GroupType">' +
    '<input type="hidden" class="form-control" id="GroupTypeName" name="GroupTypeName">' +
    '</div>' +
    '<div class="fl condition-field LogicValue_3" style="width: 45%; margin-right: 5px;display: none">' +
    '<div class="org-select-container org-style3 org-select-placeholder" data-id="Group" id="DGroups">' +
    '<input type="hidden" id="DGroup1">' +
    '<input type="hidden" id="Group1">' +
    '<input type="hidden" id="Group2">' +
    '<input type="hidden" id="Group4">' +
    '<input type="hidden" id="Group8">' +
    '<ul class="org-select"></ul>' +
    '<i class="icon iconfont icon-ptkj-zuzhixuanze"></i>' +
    '</div>' +
    '</div>' +
    '<div class="fl condition-field LogicValue_5" style="width: 80%; margin-right: 5px;display: none">' +
    '<textarea rows="5" class="form-control" id="expression" name="expression" placeholder="请输入自定义条件"></textarea>' +
    '</div>' +
    '<div class="fl" style="width: calc(10% - 5px)">' +
    '<input type="text" class="form-control" id="RBracket" name="RBracket">' +
    '</div>' +
    '</div>' +
    '<div class="condition-field LogicValue_5 tip-text mt10" style="display: none">动态表单变量使用${dyform.表单字段}。变量支持>、>=、<、<=、==、!=、contains、not contains等操作。字符串常量用单引号括起来，多个条件用逻辑与&&、逻辑或||、左右括号等组合。</div>' +
    '</div>' +
    '</div>' +
    '</form>'
  );
}

function newFlowLoadEvent($dialog, goForm, subFlowProperty, data, index) {
  var poForm = $('#newFlowForm', $dialog)[0];
  var formID = goWorkFlow.flowXML.selectSingleNode('./property/formID');
  var formUuid = formID ? formID.text() : '';
  if (data) {
    newFlowInitEvent($dialog, poForm, subFlowProperty, data, index);
  }

  // 流程选择
  var setting = {
    view: {
      showIcon: true,
      showLine: false
    },
    async: {
      otherParam: {
        serviceName: 'flowSchemeService',
        methodName: 'getFlowTree',
        data: [-1, formUuid]
      }
    },
    check: {
      enable: false,
      chkStyle: 'radio'
    }
  };

  // 流程名称
  $('#newFlowName', $dialog).comboTree({
    labelField: 'newFlowName',
    valueField: 'newFlowValue',
    autoInitValue: false,
    treeSetting: setting,
    labelBy: 'name',
    afterSetValue: function (value, label) {
      initTask($dialog, '#toTaskId', value, true, true);
      loadDirectionSelectData(value, $dialog);
      $('#newFlowName', $dialog).comboTree('hide');
      setNewFlowPlaceholder(value, $dialog);
    }
  });

  $dialog.on('click', '#conditions > li .btn-set', function () {
    var data = $(this).closest('li').data('condition');
    data._index = $(this).closest('li').attr('data-index');
    openNewConditionDialog(subFlowProperty, $dialog, data);
  });
  $dialog.on('click', '#conditions > li .btn-remove', function () {
    $(this).closest('li').remove();
  });
  $('#addDistributeNodeTypes', $dialog).on('click', function () {
    var index = $('#conditions > li', $dialog).length ? $('#conditions > li:last', $dialog).attr('data-index') : 0;
    openNewConditionDialog(subFlowProperty, $dialog, null, index);
  });

  $('#createWay', $dialog)
    .wellSelect({
      labelField: 'createWayName',
      valueField: 'createWay',
      showEmpty: false,
      data: [
        {
          id: '12',
          text: '表单字段'
        },
        {
          id: '3',
          text: '接口定义'
        }
      ],
      searchable: false
    })
    .on('change', function () {
      var value = $(this).val();
      if (value === '12') {
        $('.createWay-field', $dialog).show();
        $('.createWay-interface', $dialog).hide();
        $('#createInstanceFormId', $dialog).trigger('change');
      } else if (value === '3') {
        $('.createWay-interface', $dialog).show();
        $('.createWay-field,.createWay-subform', $dialog).hide();
      }
    });

  $('#taskUsers', $dialog).wellSelect({
    labelField: 'taskUsersName',
    valueField: 'taskUsers',
    searchable: false
  });

  $('#createInstanceFormId', $dialog)
    .wellSelect({
      labelField: 'createInstanceFormName',
      valueField: 'createInstanceFormId',
      data: goWorkFlow.formGroupData || [],
      searchable: false,
      isGroup: true
    })
    .on('change', function (e, param) {
      var val = $(this).val();
      var _data;
      if (!(param && param === 'init')) {
        $('#taskUsers', $dialog).wellSelect('val', '');
      }
      if (!val) {
        return;
      }
      if (val === goWorkFlow.DformDefinition.uuid) {
        _data = goWorkFlow.formFields;
        $('.createWay-subform', $dialog).hide();
      } else {
        _data = goWorkFlow.subFormFields[val];
        $('.createWay-subform', $dialog).show();
      }
      $('#taskUsers', $dialog).wellSelect('reRenderOption', {
        data: _data
      });
    })
    .trigger('change', ['init']);

  $('#createInstanceWay', $dialog).wellSelect({
    labelField: 'createInstanceWayName',
    valueField: 'createInstanceWay',
    showEmpty: false,
    searchable: false,
    data: [
      {
        id: '1',
        text: '生成单一实例'
      },
      {
        id: '2',
        text: '按办理人生成实例'
      }
    ]
  });

  $.get({
    url: ctx + '/api/workflow/definition/getSubtaskDispatcherCustomInterfaces',
    success: function (result) {
      var data = $.map(result.data, function (item) {
        return {
          id: item.id,
          text: item.name
        };
      });
      $('#interface', $dialog).wellSelect({
        labelField: 'interfaceName',
        valueField: 'interface',
        showEmpty: false,
        data: data
      });
    }
  });

  $('#toTaskId', $dialog).wellSelect({
    labelField: 'toTaskName',
    valueField: 'toTaskId',
    showEmpty: false,
    searchable: false
  });

  // 拷贝信息转换规则
  $('#copyBotRuleId', $dialog).wSelect2({
    serviceName: 'botRuleConfFacadeService',
    queryMethod: 'loadSelectData',
    labelField: 'copyBotRuleName',
    valueField: 'copyBotRuleId',
    placeholder: '请选择',
    multiple: false,
    remoteSearch: false
  });

  $('#copyBotRuleIdView', $dialog).on('click', function () {
    viewBotRuleInfo($('#copyBotRuleId', $dialog).val());
  });

  // 实时同步转换规则
  $('#syncBotRuleId', $dialog).wSelect2({
    serviceName: 'botRuleConfFacadeService',
    queryMethod: 'loadSelectData',
    labelField: 'syncBotRuleName',
    valueField: 'syncBotRuleId',
    placeholder: '请选择',
    multiple: false,
    remoteSearch: false,
    width: '100%',
    height: 250
  });
  $('#syncBotRuleIdView', $dialog).on('click', function () {
    viewBotRuleInfo($('#syncBotRuleId', $dialog).val());
  });

  // 返回信息转换规则
  $('#returnBotRuleId', $dialog).wSelect2({
    serviceName: 'botRuleConfFacadeService',
    queryMethod: 'loadSelectData',
    labelField: 'returnBotRuleName',
    valueField: 'returnBotRuleId',
    placeholder: '请选择',
    multiple: false,
    remoteSearch: false,
    width: '100%',
    height: 250
  });
  $('#returnBotRuleIdView', $dialog).on('click', function () {
    viewBotRuleInfo($('#returnBotRuleId', $dialog).val());
  });

  $('#returnWithDirection', $dialog)
    .on('change', function () {
      var checked = $(this).attr('checked');
      if (checked) {
        $('.return-with-direction', $dialog).show();
      } else {
        $('.return-with-direction', $dialog).hide();
      }
    })
    .trigger('change');

  loadDirectionSelectData($('#newFlowValue', $dialog).val(), $dialog);

  // 子流程标题
  $("input[name='title_expression']", $dialog).on('change', function () {
    var _value = $(this).val();
    if (_value === 'custom') {
      $('.showCustomTitle', $dialog).show();
      $('.showCustomTitle_default', $dialog).hide();
    } else {
      $('.showCustomTitle', $dialog).hide();
      $('.showCustomTitle_default', $dialog).show();
    }
  });

  $('#set_title_expression', $dialog).on('click', function () {
    openSubTitleExpressionDialog(goForm, $dialog, 'title_expression_text');
  });

  // 办理信息展示
  // 办理进度
  $("input[name='undertakeSituationPlaceHolder']", $dialog)
    .on('change', function () {
      if (this.checked == true) {
        $('.undertakeSituationPlaceHolder', $dialog).hide();
        $('.undertakeSituationPlaceHolder-' + $(this).val(), $dialog).show();
      }
    })
    .trigger('change');
  // 信息分发
  $("input[name='infoDistributionPlaceHolder']", $dialog)
    .on('change', function () {
      if (this.checked == true) {
        $('.infoDistributionPlaceHolder', $dialog).hide();
        $('.infoDistributionPlaceHolder-' + $(this).val(), $dialog).show();
      }
    })
    .trigger('change');
  // 操作记录
  $("input[name='operationRecordPlaceHolder']", $dialog)
    .on('change', function () {
      if (this.checked == true) {
        $('.operationRecordPlaceHolder', $dialog).hide();
        $('.operationRecordPlaceHolder-' + $(this).val(), $dialog).show();
      }
    })
    .trigger('change');
  if (data && data.value) {
    setNewFlowPlaceholder(data.value, $dialog);
  } else {
    setEmptyNewFlowPlaceholder($dialog);
  }
}

/**
 * 子流程标题设置弹框
 * @param goForm        流向表单信息
 * @param parentDialog  父弹出框对象
 * @param container     标题回显的容器id
 * @param title         弹框标题
 * @param tips          提示信息
 */
function openSubTitleExpressionDialog(goForm, parentDialog, container, title, tips) {
  var _html = get_title_expression(tips);
  var $dialog = top.appModal.dialog({
    title: title || '子流程标题设置',
    message: _html,
    size: 'large',
    shown: function () {
      $('textarea', $dialog).val($('#' + container, parentDialog).text());
      setTitle_expressionSelect($dialog, 'defaultField', true, [
        '子流程实例办理人',
        '流程名称',
        '流程ID',
        '流程编号',
        '发起人姓名',
        '发起人所在部门名称',
        '发起人所在部门名称全路径',
        '年',
        '简年',
        '月',
        '日',
        '时',
        '分',
        '秒',
        '发起年',
        '发起简年',
        '发起月',
        '发起日',
        '发起时',
        '发起分',
        '发起秒'
      ]);
      var formDefinition = goWorkFlow.DformDefinition || loadFormDefinition($('#DForm').val());
      var formFieldData = [];
      var field = {};
      field.label = formDefinition.name;
      field.labelID = '';
      field.optgroup = [];
      $.each(formDefinition.fields, function (i, v) {
        field.optgroup.push({
          value: i,
          name: v.displayName,
          data: v
        });
      });
      formFieldData.push(field);
      $.each(formDefinition.subforms, function (i, item) {
        var field = {};
        field.label = item.name;
        field.labelID = item.outerId;
        field.optgroup = [];
        $.each(item.fields, function (i, v) {
          field.optgroup.push({
            value: i,
            name: v.displayName,
            data: v
          });
        });
        formFieldData.push(field);
      });
      $(top.document)
        .off()
        .on('click', function (e) {
          if ($('.choose-item', $dialog)[0] === $(e.target).parents('.well-select')[0]) return;
          $('.choose-item', $dialog).removeClass('well-select-visible');
        });
      setTitle_expressionSelect($dialog, 'formField', true, formFieldData);
      $('.choose-item', $dialog)
        .off()
        .on('click', function (e) {
          var $this = $(this);
          e.stopPropagation();
          $this.toggleClass('well-select-visible');
          $('.choose-item', $dialog).each(function () {
            var _$this = $(this);
            if (!_$this.is($this)) {
              _$this.removeClass('well-select-visible');
            }
          });
        });

      $('.well-select-input', $dialog)
        .off()
        .on('input propertychange', function () {
          var $that = $(this);
          var keyword = $.trim($that.val()).toUpperCase();
          var $wellSelect = $that.closest('.well-select');
          var $wellSelectItem = $wellSelect.find('.well-select-item');
          var $wellSelectNotFound = $wellSelect.find('.well-select-not-found');
          var showNum = 0;
          $wellSelectItem.each(function () {
            var $this = $(this);
            if ($this.data('value').toUpperCase().indexOf(keyword) > -1 || $this.data('name').toUpperCase().indexOf(keyword) > -1) {
              $this.show();
              showNum++;
            } else {
              $this.hide();
            }
          });
          if (showNum) {
            $wellSelectNotFound.hide();
          } else {
            $wellSelectNotFound.show();
          }
        });

      $('.well-select-dropdown', $dialog)
        .off()
        .on('click', function (e) {
          e.stopPropagation();
        });
      $('.well-select-item', $dialog)
        .off()
        .on('click', function (e) {
          var $titleControl = $('#titleControl', $dialog)[0];
          var value = $titleControl.value;
          var start = $titleControl.selectionStart;
          var value1 = value.substr(0, start);
          var value2 = value.substr(start);
          var finalValue = value1 + '${' + $(this).attr('data-value') + '}' + value2;
          $('textarea', $dialog).val(finalValue);
          $('.choose-item', $dialog).removeClass('well-select-visible');
        });
    },
    buttons: {
      save: {
        label: '保存',
        className: 'well-btn w-btn-primary',
        callback: function () {
          // 页面上有2个弹框，子流程设置弹框-->子流程标题设置弹框，文字回显需要指定父弹出框
          $('#' + container, $(parentDialog)).text($('textarea', $dialog).val());
        }
      },
      close: {
        label: '关闭',
        className: 'btn-default',
        callback: function () {
        }
      }
    }
  });
}

// 流转设置 -- 显示位置
function setNewFlowPlaceholder(flowDefId, goForm) {
  var obj = ['undertakeSituationPlaceHolderValue', 'infoDistributionPlaceHolderValue', 'operationRecordPlaceHolderValue'];
  $.get({
    url: ctx + '/api/workflow/definition/getFormBlocksByFlowDefId',
    data: {
      flowDefId: flowDefId
    },
    success: function (result) {
      var data = [];
      for (var i = 0; i < result.data.length; i++) {
        data.push({
          id: result.data[i].data,
          text: result.data[i].name
        });
      }
      $.each(obj, function (index, item) {
        $('#' + item, goForm).wellSelect({
          data: data
        });
      });
    }
  });
}

function setEmptyNewFlowPlaceholder(goForm) {
  var obj = ['undertakeSituationPlaceHolderValue', 'infoDistributionPlaceHolderValue', 'operationRecordPlaceHolderValue'];
  $.each(obj, function (index, item) {
    $('#' + item, goForm).wellSelect({
      data: []
    });
  });
}

//初始化环节
function initTask(ele, selector, flowDefId, draft, autoSubmit, excludeValue) {
  if (!flowDefId) {
    return;
  }

  // 是否隐藏自动提交，当选择的子流程定义，发起环节有多个流出流向，或者流出流向存在判断节点时，自动提交选项隐藏
  var hideAutoSubmit = false;
  if (autoSubmit) {
    hideAutoSubmit = isHideAutoSubmit(flowDefId);
  }

  $.get({
    url: ctx + '/api/workflow/definition/getFlowTasks',
    data: {
      flowDefId: flowDefId
    },
    success: function (result) {
      var data = [];
      if (draft) {
        data.push({
          id: 'DRAFT',
          text: '草稿'
        });
      }
      if (autoSubmit && !hideAutoSubmit) {
        data.push({
          id: 'AUTO_SUBMIT',
          text: '自动提交'
        });
      }
      $.each(result.data, function (i, item) {
        var id = item.id;
        var text = item.name;
        if (excludeValue === id) {
          return;
        }
        data.push({
          id: id,
          text: text
        });
      });
      var initVal = $(selector, ele).wellSelect('val');
      var valueChange = true;
      $.each(data, function (i, item) {
        if (initVal == item.id) {
          valueChange = false;
        }
      });
      $(selector, ele).wellSelect('reRenderOption', {
        data: data
      });
      // 选项值不存在，设置为值
      if (valueChange) {
        $(selector, ele).wellSelect('val', '');
      }
    }
  });
}

// 是否隐藏自动提交，当选择的子流程定义，发起环节有多个流出流向，或者流出流向存在判断节点时，自动提交选项隐藏
function isHideAutoSubmit(flowDefId) {
  var hideAutoSubmit = false;
  $.get({
    url: ctx + '/api/workflow/definition/isAutoSubmitForkTask',
    data: {
      flowDefId: flowDefId
    },
    async: false,
    success: function (result) {
      hideAutoSubmit = result.data;
    }
  });
  return hideAutoSubmit;
}

// 查看单据转换规则信息
function viewBotRuleInfo(botRuleId) {
  if (StringUtils.isBlank(botRuleId)) {
    top.appModal.error('请选择要查看的单据转换规则！');
    return;
  }
  JDS.call({
    service: 'botRuleConfService.getById',
    data: [botRuleId],
    validate: true,
    version: '',
    success: function (result) {
      if (result.success) {
        top.appContext.removeWidgetById('page_20200115092455', true);
        top.appModal.dialog({
          message: '<div id="dialog_page_20200115092455"></div>',
          size: 'large',
          title: '单据转换规则配置详情',
          width: '1200',
          onEscape: function () {
            top.appContext.removeWidgetById('page_20200115092455', true);
          },
          buttons: {
            close: {
              label: '取消',
              className: 'btn-default',
              callback: function () {
                top.appContext.removeWidgetById('page_20200115092455', true);
              }
            }
          },
          shown: function () {
            var opt = {
              target: '_dialog',
              targetWidgetId: '',
              widgetDefId: 'page_20200115092455',
              params: {
                uuid: result.data.uuid,
                readonly: true
              },
              renderTo: '#dialog_page_20200115092455',
              refresh: true,
              text: '单据转换规则'
            };
            top.appContext.renderWidget(opt);
          }
        });
      }
    }
  });
}

function openNewConditionDialog(subFlowProperty, $dialog, data, index) {
  var _html = getConditionDialogHtml();
  var $dialog2 = top.appModal.dialog({
    title: '判断条件',
    message: _html,
    size: 'large',
    shown: function () {
      newConditionLoadEvent($dialog2, subFlowProperty, data, index);
    },
    buttons: {
      save: {
        label: '保存',
        className: 'well-btn w-btn-primary',
        callback: function () {
          var poForm = $('#newFlowConditionForm', $dialog2)[0];
          if (!NewFlowConditionCheckValue(poForm)) {
            return false;
          }
          var returnValue = NewFlowConditionOkEvent($dialog2, poForm);
          if (data) {
            returnValue._index = data._index;
            renderCondition($dialog, returnValue);
          } else {
            renderCondition($dialog, returnValue, index + 1);
          }
        }
      },
      close: {
        label: '取消',
        className: 'btn-default',
        callback: function () {
        }
      }
    }
  });
}

function newConditionInitEvent($dialog, subFlowProperty, data, index) {
  console.log(data);
  var goForm = $('#newFlowConditionForm', $dialog)[0];
  $('[name="LogicType"]', $dialog).each(function () {
    var val = $(this).val();
    if (val === data.type) {
      $(this).attr('checked', true);
      if (val === '4') {
        $('.condition-wrap', $dialog).hide();
      } else {
        $('.condition-wrap', $dialog).show();
        $('.condition-field', $dialog).hide();
        $('.LogicValue_' + val, $dialog).show();
      }
    }
  });
  $('#AndOr', $dialog).val(data.connector);
  $('#LBracket', $dialog).val(data.leftBracket);
  $('#FieldName', $dialog).val(data.fieldName);
  $('#Operator', $dialog).val(data.operator);
  $('#Value', $dialog).val(data.value);
  $('#RBracket', $dialog).val(data.rightBracket);
  $('#GroupType', $dialog).val(data.groupType);
  var GroupUnitData = {
    unitLabel: [],
    unitValue: [],
    formField: [],
    tasks: [],
    options: [],
    custom: []
  };
  if (data.groupType === '1') {
    if (data.groupValue != null) {
      var groupValues = data.groupValue.split(',');
      if (groupValues.length === 5) {
        goForm.DGroup1.value = groupValues[0];
        GroupUnitData.unitLabel = groupValues[0].split(';');
        goForm.Group1.value = groupValues[1];
        GroupUnitData.unitValue = groupValues[1].split(';');
        goForm.Group2.value = groupValues[2];
        GroupUnitData.formField = groupValues[2].split(';');
        goForm.Group4.value = groupValues[3];
        GroupUnitData.tasks = groupValues[3].split(';');
        goForm.Group8.value = groupValues[4];
        GroupUnitData.options = groupValues[4].split(';');
      }
    }
  } else if (data.groupType === '0') {
    var groupValues = data.groupValue;
    var groupArgValue = data.groupArgValue;
    goForm.DGroup1.value = groupArgValue;
    GroupUnitData.unitLabel = groupArgValue.split(';');
    goForm.Group1.value = groupValues;
    GroupUnitData.unitValue = groupValues.split(';');
  }
  renderUnitField(GroupUnitData, goForm, 'Group');
  $('#expression', $dialog).val(data.expression);
}

function newConditionLoadEvent($dialog, subFlowProperty, data, index) {
  if (data) {
    newConditionInitEvent($dialog, subFlowProperty, data, index);
  }
  var goForm = $('#newFlowConditionForm', $dialog)[0];
  $('[name="LogicType"]', $dialog).on('change', function () {
    var val = $(this).val();
    if (val === '4') {
      $('.condition-wrap', $dialog).hide();
    } else {
      $('.condition-wrap', $dialog).show();
      $('.condition-field', $dialog).hide();
      $('.LogicValue_' + val, $dialog).show();
    }
  });

  $('#AndOr', $dialog).wellSelect({
    data: [
      {
        id: '&&',
        text: '并且'
      },
      {
        id: '||',
        text: '或者'
      }
    ],
    searchable: false
  });

  $('#LBracket', $dialog).wellSelect({
    data: [
      {
        id: '(',
        text: '('
      }
    ],
    searchable: false
  });
  $('#RBracket', $dialog).wellSelect({
    data: [
      {
        id: ')',
        text: ')'
      }
    ],
    searchable: false
  });

  $('#FieldName', $dialog).wellSelect({
    labelField: 'FieldText',
    valueField: 'FieldName',
    data: goWorkFlow.formFields
  });

  $('#Operator', $dialog).wellSelect({
    labelField: 'OperatorName',
    valueField: 'Operator',
    data: [
      {
        id: '>',
        text: '大于'
      },
      {
        id: '>=',
        text: '大于等于'
      },
      {
        id: '<',
        text: '小于'
      },
      {
        id: '<=',
        text: '小于等于'
      },
      {
        id: '==',
        text: '等于'
      },
      {
        id: '!=',
        text: '不等于'
      },
      {
        id: 'contains',
        text: '包含'
      },
      {
        id: 'not contains',
        text: '不包含'
      }
    ],
    searchable: false
  });

  $('#GroupType', $dialog)
    .wellSelect({
      labelField: 'GroupTypeName',
      valueField: 'GroupType',
      data: [
        {
          id: '1',
          text: '分发的办理人在指定人员范围内'
        },
        {
          id: '0',
          text: '分发的单位在指定业务组织内'
        }
      ],
      searchable: false
    })
    .on('change', function () {
      $('#DGroups', $dialog).find('.org-select').empty();
      $('#DGroup1,#group1,#group2,#group4,#group8', $dialog).val('');
    });

  // 选择群组
  $('#DGroups', $dialog).on('click', function (e) {
    var groupType = $('#GroupType', $dialog).val();
    if (groupType === '1') {
      SelectUsers('unit/field/task/option', 'Group', '选择群组', null, goForm, null, true);
    } else {
      var businessType = $('#businessType').val();
      var initValues = $('#Group1', $dialog).val();
      var initLabels = $('#DGroup1', $dialog).val();
      var orgVersionId = undefined;
      if (businessType) {
        $.get({
          url: `/proxy/api/org/organization/version/getOrgVersionIdByOrgId?orgId=${businessType}`,
          async: false,
          success: function (result) {
            orgVersionId = result.data;
          }
        })
      }
      top.$.unit2.open({
        targetWindow: top,
        valueField: 'Group1',
        labelField: 'DGroup1',
        title: '业务组织',
        type: 'all',
        multiple: true,
        inputReadOnly: false,
        isNeedUser: '0',
        initValues: initValues,
        initLabels: initLabels,
        otherParams: {
          categoryId: businessType //此处分类的UUID,必填
        },
        v: "7.0",
        orgVersionId: orgVersionId,
        callback: function (values, labels) {
          var $target = $('#DGroups', $dialog);
          $target.find('.org-select').empty();
          $.each(labels, function (i, item) {
            $target
              .find('.org-select')
              .append('<li class="org-entity ' + values[i][0] + '"><span class="org-label">' + item + '</span></li>');
          });
          var liLength = $target.find('.org-select li').length;
          if (liLength) {
            $target.removeClass('org-select-placeholder');
          } else {
            $target.addClass('org-select-placeholder');
          }
          goForm.DGroup1.value = labels.join(';');
          goForm.Group1.value = values.join(';');
        }
      });
    }
  });
}

function newFlowInitEvent($dialog, goForm, subFlowProperty, data, index) {
  var loNode = goWorkFlow.flowXML.selectSingleNode('./property/formID');
  var formUuid = loNode != null ? loNode.text() : '';

  $('#newFlowId', goForm).val(data.id);
  $('#newFlowName', goForm).val(data.name);
  $('#newFlowValue', goForm).val(data.value);
  $('#newFlowLabel', goForm).val(data.label);

  var conditionJson = data.conditions || '{}';
  conditionJson = JSON.parse(conditionJson);
  var conditions = conditionJson.conditionConfigs || [];
  $.each(conditions, function (i, condition) {
    renderCondition($dialog, condition, i + 1);
  });

  //源代码找不到dom
  $('#createName', goForm).val(data.createName);
  $('#createValue', goForm).val(data.createValue);

  $('#createWay', goForm).val(data.createWay);
  $('#createInstanceFormId', goForm).val(data.createInstanceFormId);
  $('#createInstanceWay', goForm).val(data.createInstanceWay);
  $('#isMainFormCreateWay', goForm).val(data.isMainFormCreateWay);
  $('#subformId', goForm).val(data.subformId);
  $('#title', goForm).val(data.title);
  $('#taskUsers', goForm).val(data.taskUsers);
  $('#taskUsersName', goForm).val(data.taskUsersName);
  $('#createInstanceBatch', goForm)[0].checked = data.createInstanceBatch === '1' ? true : false;
  $('#isMajor', goForm)[0].checked = data.isMajor === '1' ? true : false;
  // $("#isMerge", goForm)[0].checked = (data.isMerge === "1" ? true : false);
  $('#isWait', goForm)[0].checked = data.isWait === '1' ? true : false;
  $('#isShare', goForm)[0].checked = data.isShare === '1' ? true : false;
  $('#copyFieldNames', goForm).val(data.copyFieldNames);
  $('#copyFields', goForm).val(data.copyFields);
  $('#copyBotRuleName', goForm).val(data.copyBotRuleName);
  $('#copyBotRuleId', goForm).val(data.copyBotRuleId);
  $('#syncBotRuleName', goForm).val(data.syncBotRuleName);
  $('#syncBotRuleId', goForm).val(data.syncBotRuleId);
  $('#returnWithOver', goForm)[0].checked = data.returnWithOver === '1' ? true : false;
  $('#returnWithDirection', goForm)[0].checked = data.returnWithDirection === '1' ? true : false;
  $('#returnDirectionName', goForm).val(data.returnDirectionName);
  $('#returnDirectionId', goForm).val(data.returnDirectionId);
  $('#returnBotRuleName', goForm).val(data.returnBotRuleName);
  $('#returnBotRuleId', goForm).val(data.returnBotRuleId);
  $('#returnOverrideFieldNames', goForm).val(data.returnOverrideFieldNames);
  $('#returnOverrideFields', goForm).val(data.returnOverrideFields);
  $('#returnAdditionFieldNames', goForm).val(data.returnAdditionFieldNames);
  $('#returnAdditionFields', goForm).val(data.returnAdditionFields);
  $('#notifyDoing', goForm)[0].checked = data.notifyDoing === '1' ? true : false;

  // 子流程标题设置
  var title = data.title;
  var titleExpression = data.titleExpression;
  if (title === 'custom') {
    $('input[name="title_expression"][value="custom"]', $dialog).attr("checked", true);
    $('#title_expression_text', $dialog).text(titleExpression);
    $('.showCustomTitle', $dialog).show();
    $('.showCustomTitle_default', $dialog).hide();
  }

  // 办理信息展示
  var undertakeSituationPlaceHolder = data.undertakeSituationPlaceHolder || '1';
  var infoDistributionPlaceHolder = data.infoDistributionPlaceHolder || '1';
  var operationRecordPlaceHolder = data.operationRecordPlaceHolder || '1';
  if (undertakeSituationPlaceHolder == '1') {
    $('input[name="undertakeSituationPlaceHolder"][value="1"]', $dialog).attr('checked', 'checked');
  } else {
    $('input[name="undertakeSituationPlaceHolder"][value="2"]', $dialog).attr('checked', 'checked');
    $('#undertakeSituationPlaceHolderValue', $dialog).val(undertakeSituationPlaceHolder);
  }
  if (infoDistributionPlaceHolder == '1') {
    $('input[name="infoDistributionPlaceHolder"][value="1"]', $dialog).attr('checked', 'checked');
  } else {
    $('input[name="infoDistributionPlaceHolder"][value="2"]', $dialog).attr('checked', 'checked');
    $('#infoDistributionPlaceHolderValue', $dialog).val(infoDistributionPlaceHolder);
  }
  if (operationRecordPlaceHolder == '1') {
    $('input[name="operationRecordPlaceHolder"][value="1"]', $dialog).attr('checked', 'checked');
  } else {
    $('input[name="operationRecordPlaceHolder"][value="2"]', $dialog).attr('checked', 'checked');
    $('#operationRecordPlaceHolderValue', $dialog).val(operationRecordPlaceHolder);
  }
  if (data.value) {
    $('#toTaskName', goForm).val(data.toTaskName);
    $('#toTaskId', goForm).val(data.toTaskId);
    initTask($dialog, '#toTaskId', data.value, true, true);
  }
}

function renderCondition($dialog, data, index) {
  var $li = $('<li data-index="' + index + '" style="padding: 0 60px 0 10px"></li>');
  $li.data('condition', data);
  var html =
    '<div class="name">' +
    data.label +
    '</div>' +
    '<div class="btn-group">' +
    '<div class="well-btn w-btn-primary well-btn-sm w-noLine-btn btn-set" title="设置">' +
    '<i class="iconfont icon-ptkj-shezhi"></i>' +
    '</div>' +
    '<div class="well-btn w-btn-primary well-btn-sm w-noLine-btn btn-remove" title="删除">' +
    '<i class="iconfont icon-ptkj-shanchu"></i>' +
    '</div>' +
    '</div>';
  $li.append(html);
  if (index) {
    $('#conditions', $dialog).append($li);
  } else {
    var $oldli = $('#conditions > li[data-index="' + data._index + '"]', $dialog);
    $li.attr('data-index', data._index);
    $oldli.after($li);
    $oldli.remove();
  }
}

function subFlowInfoProperty(subFlowProperty, goForm) {
  //流程字段
  var flowFields = [];
  var fixedField = [
    {
      id: 'todoName',
      text: '承办部门'
    },
    {
      id: 'currentTaskName',
      text: '当前环节'
    },
    {
      id: 'currentTodoUserName',
      text: '当前环节办理人'
    },
    {
      id: 'dueTime',
      text: '办理时限'
    },
    {
      id: 'remainingTime',
      text: '剩余时限'
    },
    {
      id: 'workProcesses',
      text: '办理情况'
    },
    {
      id: 'resultFiles',
      text: '附件'
    }
  ];

  var fixedSortField = [
    {
      id: 'todoName',
      text: '承办部门'
    },
    {
      id: 'currentTaskName',
      text: '当前环节'
    },
    {
      id: 'currentTodoUserName',
      text: '当前环节办理人'
    },
    {
      id: 'dueTime',
      text: '办理时限'
    }
  ];

  $.ajax({
    type: 'get',
    url: ctx + '/api/workflow/work/sortFields',
    contentType: 'application/json',
    dataType: 'json',
    async: false,
    success: function (res) {
      if (res.code == 0) {
        $.each(res.data, function (index, item) {
          var curIndex = _.findIndex(fixedField, function (o) {
            return item.id == fixedField.id;
          });
          if (curIndex == -1) {
            flowFields.push(item);
          }
        });
        // flowFields = _.differenceBy(res.data, fixedField, 'id');
      }
    }
  });

  // 显示位置，子流程定义有相同属性，不能通过setInputXMLValue设值
  var undertakeSituationPlaceHolderNode = subFlowProperty.children('undertakeSituationPlaceHolder');
  var infoDistributionPlaceHolderNode = subFlowProperty.children('infoDistributionPlaceHolder');
  var operationRecordPlaceHolderNode = subFlowProperty.children('operationRecordPlaceHolder');
  if (undertakeSituationPlaceHolderNode != null) {
    goForm['undertakeSituationPlaceHolder'].value = undertakeSituationPlaceHolderNode.text();
  }
  if (infoDistributionPlaceHolderNode != null) {
    goForm['infoDistributionPlaceHolder'].value = infoDistributionPlaceHolderNode.text();
  }
  if (operationRecordPlaceHolderNode != null) {
    goForm['operationRecordPlaceHolder'].value = operationRecordPlaceHolderNode.text();
  }
  // setInputXMLValue(goForm, subFlowProperty, ['undertakeSituationPlaceHolder', 'infoDistributionPlaceHolder', 'operationRecordPlaceHolder']);
  setPlaceholder(['undertakeSituationPlaceHolder', 'infoDistributionPlaceHolder', 'operationRecordPlaceHolder'], goForm);

  var undertakeSituationTitleExpression = subFlowProperty.selectSingleNode('undertakeSituationTitleExpression');
  if (undertakeSituationTitleExpression) {
    $('#undertakeSituationTitleExpression').text(undertakeSituationTitleExpression.text());
  }

  var infoDistributionTitleExpression = subFlowProperty.selectSingleNode('infoDistributionTitleExpression');
  if (infoDistributionTitleExpression) {
    $('#infoDistributionTitleExpression').text(infoDistributionTitleExpression.text());
  }

  var operationRecordTitleExpression = subFlowProperty.selectSingleNode('operationRecordTitleExpression');
  if (operationRecordTitleExpression) {
    $('#operationRecordTitleExpression').text(operationRecordTitleExpression.text());
  }

  $('#setUundertakeSituationTitleExpression').on('click', function () {
    openTitleExpressionDialog(goForm, subFlowProperty, 'undertakeSituationTitleExpression', '列表标题设置');
  });
  $('#setInfoDistributionTitleExpression').on('click', function () {
    openTitleExpressionDialog(goForm, subFlowProperty, 'infoDistributionTitleExpression', '列表标题设置');
  });
  $('#setOperationRecordTitleExpression').on('click', function () {
    openTitleExpressionDialog(goForm, subFlowProperty, 'operationRecordTitleExpression', '列表标题设置');
  });

  var btnSource = [
    {
      id: 'add-subflow',
      text: '添加承办(全部)'
    },
    {
      id: 'add-major-flow',
      text: '添加主办'
    },
    {
      id: 'add-minor-flow',
      text: '添加协办'
    },
    {
      id: 'remind',
      text: '催办'
    },
    {
      id: 'send-message',
      text: '信息分发'
    },
    {
      id: 'limit-time',
      text: '协办时限'
    },
    {
      id: 'redo',
      text: '重办'
    },
    {
      id: 'stop',
      text: '终止'
    },
    {
      id: 'closeSubView',
      text: '关闭子流程查看本流程'
    },
    {
      id: 'allowSubView',
      text: '允许子流程查看本流程'
    }
  ];

  var sortSource = [
    {
      id: 'asc',
      text: '升序'
    },
    {
      id: 'desc',
      text: '降序'
    }
  ];

  var undertakeSituationButtons = subFlowProperty.selectSingleNode('undertakeSituationButtons');
  var buttons = [];
  if (undertakeSituationButtons) {
    var buttonArr = undertakeSituationButtons.selectNodes('button');
    $.each(buttonArr, function (i, item) {
      var button = x2js.xml2js(item.outerHTML).button;
      buttons.push(button);
    });
  }

  var $btnSettingTable = $('#btnSettingTable', $('#subFlowInfo'));
  $btnSettingTable.wellEasyTable({
    data: buttons,
    checkAll: true,
    columns: [
      {
        field: 'checked',
        checkbox: true
      },
      {
        field: 'id',
        title: '按钮',
        editable: {
          type: 'select',
          options: {
            data: btnSource,
            searchable: false,
            showEmpty: false
          }
        }
      },
      {
        field: 'newName',
        title: '新名称',
        editable: {
          type: 'input'
        }
      }
    ],
    addBtn: '#btn_add_set',
    removeBtn: '#btn_delete_set',
    moveUpBtn: '#btn_row_up_set',
    moveDownBtn: '#btn_row_down_set',
    afterEditCell: function (rowKey, field, value, label) {
      if (field === 'id') {
        var allData = $btnSettingTable.wellEasyTable('getAllData');
        var error = false;
        $.each(allData, function (i, item) {
          if (item.id === value && rowKey !== item.rowKey) {
            top.appModal.error('按钮[' + label + ']已存在,请重新选择');
            $btnSettingTable.wellEasyTable('updateRowData', rowKey, {
              rowKey: rowKey,
              id: '',
              name: '',
              newName: ''
            });
            error = true;
            return false;
          }
        });
        if (error) {
          return false;
        }
        var newData = {
          rowKey: rowKey,
          id: value,
          name: label,
          newName: label
        };
        $('#newName_' + rowKey).val(label);
        $btnSettingTable.wellEasyTable('updateRowData', rowKey, newData);
      }
    }
  });

  var undertakeSituationColumns = subFlowProperty.selectSingleNode('undertakeSituationColumns');
  var columns = [];
  if (undertakeSituationColumns) {
    var columnArr = undertakeSituationColumns.selectNodes('column');
    $.each(columnArr, function (i, item) {
      var column = x2js.xml2js(item.outerHTML).column;
      columns.push(column);
    });
  }

  // 列排序字段备选项
  var sortFields = getSortFields(fixedSortField, flowFields, columns);

  var $columnSettingTable = $('#columnSettingTable', $('#subFlowInfo'));
  $columnSettingTable.wellEasyTable({
    data: columns,
    checkAll: true,
    columns: [
      {
        field: 'checked',
        checkbox: true
      },
      {
        field: 'name',
        title: '列名',
        formatter: function (value, row, index) {
          return row.name || row.tempName;
        }
      },
      {
        field: 'typeName',
        title: '类型'
      }
    ],
    removeBtn: '#btn_delete_column',
    moveUpBtn: '#btn_row_up_column',
    moveDownBtn: '#btn_row_down_column'
  });

  $('#btn_add_column').on('click', function () {
    showColumnSetDialog(goForm, subFlowProperty, $columnSettingTable, fixedField);
  });
  $('#btn_edit_column').on('click', function () {
    var chooseRows = $columnSettingTable.wellEasyTable('getChooseRow');
    if (!chooseRows.length) {
      top.appModal.error('请选择编辑数据！');
      return;
    } else if (chooseRows.length > 1) {
      top.appModal.error('一次只能编辑一条数据！');
      return;
    }
    var data = $columnSettingTable.wellEasyTable('getRowData', chooseRows)[0];
    showColumnSetDialog(goForm, subFlowProperty, $columnSettingTable, fixedField, data, chooseRows);
  });

  $("input[name='sortRule']", $('#subFlowInfo'))
    .off()
    .on('change', function () {
      var val = $('input[name="sortRule"]:checked', $('#subFlowInfo')).val();
      $('.sort-config').hide();
      $('.sort-config-' + val).show();
    });

  var sortRule = subFlowProperty.selectSingleNode('sortRule') ? subFlowProperty.selectSingleNode('sortRule').text() : '';
  if (sortRule == 'custom') {
    $("input[name='sortRule'][value='custom']", $('#subFlowInfo')).prop('checked', true).trigger('change');
  }

  var expandList = subFlowProperty.selectSingleNode('expandList') ? subFlowProperty.selectSingleNode('expandList').text() : '1';
  if (expandList === '1') {
    $("input[name='expandList']", $('#subFlowInfo')).prop('checked', true);
  } else {
    $("input[name='expandList']", $('#subFlowInfo')).prop('checked', false);
  }

  var undertakeSituationOrders = subFlowProperty.selectSingleNode('undertakeSituationOrders');
  var orders = [];
  if (undertakeSituationOrders) {
    var orderArr = undertakeSituationOrders.selectNodes('order');
    $.each(orderArr, function (i, item) {
      var order = x2js.xml2js(item.outerHTML).order;
      orders.push(order);
    });
  }

  var $sortSettingTable = $('#sortSettingTable', $('#subFlowInfo'));
  $sortSettingTable.wellEasyTable({
    data: orders,
    checkAll: true,
    columns: [
      {
        field: 'checked',
        checkbox: true
      },
      {
        field: 'name',
        title: '排序列',
        editable: {
          type: 'select',
          options: {
            data: sortFields,
            searchable: true,
            showEmpty: false
          }
        }
      },
      {
        field: 'direction',
        title: '排序方式',
        editable: {
          type: 'select',
          options: {
            defaultValue: 'asc',
            data: sortSource,
            searchable: false,
            showEmpty: false
          }
        }
      }
    ],
    addBtn: '#btn_add_sort',
    removeBtn: '#btn_delete_sort',
    moveUpBtn: '#btn_row_up_sort',
    moveDownBtn: '#btn_row_down_sort'
  });

  $columnSettingTable.on('updateRowData', function () {
    var columnsData = $columnSettingTable.wellEasyTable('getAllData');
    var sortData = getSortFields(fixedSortField, flowFields, columnsData);
    var $td = $sortSettingTable.find("td[data-field='name']");
    if ($td.length > 0) {
      $.each($td, function (index, item) {
        $(item).find('input').wellSelect('reRenderOption', {
          data: sortData
        });
      });
    } else {
    }
  });
}

function getSortFields(field1, field2, datas) {
  var currSortFields = _.cloneDeep(field1);
  $.each(datas, function (index, item) {
    if (item.type == '1') {
      // 固定列替换新名词
      var sameFieldIndex = _.findIndex(currSortFields, function (o) {
        return o.id == item.index;
      });
      if (sameFieldIndex > -1) {
        currSortFields[sameFieldIndex].text = item.name;
      }
    } else {
      // 扩展列添加字段
      if (!item.extraColumn) {
        item.extraColumn = getExtraColumn(datas);
      }
      currSortFields.push({
        id: item.extraColumn,
        text: item.name
      });
    }
  });

  return currSortFields.concat(field2);
}

function loadDirectionSelectData(newFlowId, $dialog) {
  $('#returnDirectionId', $dialog).wSelect2({
    serviceName: 'flowSchemeService',
    queryMethod: 'loadDirectionSelectData',
    labelField: 'returnDirectionName',
    valueField: 'returnDirectionId',
    placeholder: '请选择',
    multiple: false,
    remoteSearch: false,
    params: {
      flowDefId: newFlowId
    }
  });
}

function NewFlowConditionCheckValue(goForm) {
  var logicType = $("[name='LogicType']:checked", goForm).val();
  // 通过字段值比较
  if (logicType === '1') {
    var value = $('#Value', goForm).val();
    if (StringUtils.isBlank(value)) {
      top.appModal.error('字段值比较值不能为空！');
      return false;
    }
  } else if (logicType === '3') {
    // 按办理人归属
    var groupType = $('#GroupType', goForm).val();
    var groups = $('#DGroups', goForm).val();
    if (StringUtils.isBlank(groupType)) {
      top.appModal.error('办理人类型不能为空！');
      return false;
    }
    // if(StringUtils.isBlank(groups)) {
    //     top.appModal.error("办理人不能为空！");
    //     return false;
    // }
  } else if (logicType === '4') {
    // 逻辑条件
    var logicType = $('#AndOr', goForm).val();
    if (StringUtils.isBlank(logicType)) {
      top.appModal.error('条件逻辑关系不能为空！');
      return false;
    }
  } else if (logicType === '5') {
    // 自定义条件
    var expression = $('#expression', goForm).val();
    if (StringUtils.isBlank(expression)) {
      top.appModal.error('自定义条件不能为空！');
      return false;
    }
  }
  return true;
}

function NewFlowConditionOkEvent($dialog, goForm) {
  // 条件类型
  var type = $("[name='LogicType']:checked", goForm).val();
  // 连接符
  var connector = $("[name='AndOr']", goForm).val();
  // 左括号
  var leftBracket = '';
  // 表单字段
  var fieldName = '';
  // 运算符
  var operator = '';
  // 比较值
  var value = '';
  // 群组类型
  var groupType = '';
  // 群组值
  var groupValue = '';
  // 群组值显示值
  var groupArgValue = '';
  // 表达式
  var expression = '';
  // 右括号
  var rightBracket = '';
  // 条件显示信息
  var label = '';
  if (connector === '&&') {
    label = '并且 ';
  } else if (connector === '||') {
    label = '或者 ';
  }

  leftBracket = $('#LBracket', goForm).val();
  rightBracket = $('#RBracket', goForm).val();
  // 通过字段值比较
  if (type === '1') {
    fieldName = $('#FieldName', goForm).val();
    operator = $('#Operator', goForm).val();
    value = $('#Value', goForm).val();
    label += leftBracket + '[' + $('#FieldText', goForm).val() + '] ';
    label += $('#OperatorName', goForm).val() + ' ' + value + rightBracket;
  } else if (type === '3') {
    // 按办理人归属
    groupType = $('#GroupType', goForm).val();
    var groupValues = [];
    groupValues.push(goForm.DGroup1.value);
    groupValues.push(goForm.Group1.value);
    groupValues.push(goForm.Group2.value);
    groupValues.push(goForm.Group4.value);
    groupValues.push(goForm.Group8.value);
    groupValue = groupValues.join(',');
    if (groupType === '1') {
      groupArgValue = $('#DGroups', goForm).data('allValueText');
    } else if (groupType === '0') {
      groupArgValue = $('#DGroup1', goForm).val();
      groupValue = $('#Group1', goForm).val();
    }
    label += leftBracket + '[' + $('#GroupTypeName', goForm).val() + '] ' + groupArgValue + rightBracket;
  } else if (type === '5') {
    // 自定义条件
    expression = $('#expression', goForm).val();
    label += leftBracket + '[自定义条件] ' + expression + rightBracket;
  }

  var condition = {};
  condition.type = type;
  condition.connector = connector;
  condition.leftBracket = leftBracket;
  condition.fieldName = fieldName;
  condition.operator = operator;
  condition.value = value;
  condition.rightBracket = rightBracket;
  condition.groupType = groupType;
  condition.groupValue = groupValue;
  condition.groupArgValue = groupArgValue;
  condition.expression = expression;
  condition.label = label;
  console.log(label);
  return condition;
}

function NewFlowOKEvent($dialog) {
  var newFlow = {};
  newFlow.id = $('#newFlowId', $dialog).val();
  if (newFlow.id == null || $.trim(newFlow.id) == '') {
    var date = new Date();
    newFlow.id = date.getTime();
  }
  var conditions = [];
  var conditionEle = $('#conditions > li', $dialog);
  $.each(conditionEle, function () {
    conditions.push($(this).data('condition'));
  });

  var conditionJson = {
    conditionConfigs: conditions
  };
  newFlow.name = $('#newFlowName', $dialog).val();
  newFlow.value = $('#newFlowValue', $dialog).val();
  newFlow.label = $('#newFlowLabel', $dialog).val();
  newFlow.conditions = JSON.stringify(conditionJson);
  newFlow.interfaceValue = $('#interface', $dialog).val();
  newFlow.interfaceName = $('#interfaceName', $dialog).val();

  // newFlow.createValue = $("#createValue", $dialog).val();
  // newFlow.createName = $("#createName > option:selected", $dialog).text();

  newFlow.createWay = $('#createWay', $dialog).val();
  newFlow.createWayName = $('#createWayName', $dialog).val();
  newFlow.createInstanceFormId = $('#createInstanceFormId', $dialog).val();
  newFlow.createInstanceFormName = $('#createInstanceFormName', $dialog).val();
  newFlow.createInstanceWay = $('#createInstanceWay', $dialog).val();
  newFlow.createInstanceWayName = $('#createInstanceWayName', $dialog).val();

  // newFlow.isMainFormCreateWay = $("#isMainFormCreateWay", $dialog).val();
  // newFlow.subformId = $("#subformId", $dialog).val();
  // newFlow.subformIdName = $("#subformId > option:selected", goForm).text();

  newFlow.taskUsers = $('#taskUsers', $dialog).val();
  newFlow.taskUsersName = $('#taskUsersName', $dialog).val();
  newFlow.createInstanceBatch = $('#createInstanceBatch', $dialog)[0].checked ? '1' : '0';
  newFlow.isMajor = $('#isMajor', $dialog)[0].checked ? '1' : '0';
  // newFlow.isMerge = $("#isMerge", $dialog)[0].checked ? "1" : "0";
  newFlow.isWait = $('#isWait', $dialog)[0].checked ? '1' : '0';
  newFlow.isShare = $('#isShare', $dialog)[0].checked ? '1' : '0';
  newFlow.toTaskName = $('#toTaskName', $dialog).val();
  newFlow.toTaskId = $('#toTaskId', $dialog).val();

  // newFlow.copyFieldNames = $("#copyFieldNames", $dialog).val();
  // newFlow.copyFields = $("#copyFields", $dialog).val();

  // 子流程标题设置
  newFlow.title = $('input[name=title_expression]:checked', $dialog).val();
  // 子流程标题表达式
  if (newFlow.title === 'default') {
    newFlow.titleExpression = '${流程名称}_${子流程实例办理人}';
  } else if (newFlow.title === 'custom') {
    newFlow.titleExpression = $('#title_expression_text', $dialog).text();
  }

  // 自定义标题为空时，等同于默认，保存后显示为默认
  if (newFlow.titleExpression === '') {
    newFlow.title = 'default';
    newFlow.titleExpression = '${流程名称}_${子流程实例办理人}';
  }

  newFlow.copyBotRuleName = $('#copyBotRuleName', $dialog).val();
  newFlow.copyBotRuleId = $('#copyBotRuleId', $dialog).val();
  newFlow.syncBotRuleName = $('#syncBotRuleName', $dialog).val();
  newFlow.syncBotRuleId = $('#syncBotRuleId', $dialog).val();
  newFlow.returnWithOver = $('#returnWithOver', $dialog)[0].checked ? '1' : '0';
  newFlow.returnWithDirection = $('#returnWithDirection', $dialog)[0].checked ? '1' : '0';
  newFlow.returnDirectionName = $('#returnDirectionName', $dialog).val();
  newFlow.returnDirectionId = $('#returnDirectionId', $dialog).val();
  newFlow.returnBotRuleName = $('#returnBotRuleName', $dialog).val();
  newFlow.returnBotRuleId = $('#returnBotRuleId', $dialog).val();
  newFlow.returnOverrideFieldNames = $('#returnOverrideFieldNames', $dialog).val();
  newFlow.returnOverrideFields = $('#returnOverrideFields', $dialog).val();
  newFlow.returnAdditionFieldNames = $('#returnAdditionFieldNames', $dialog).val();
  newFlow.returnAdditionFields = $('#returnAdditionFields', $dialog).val();
  newFlow.notifyDoing = $('#notifyDoing', $dialog)[0].checked ? '1' : '0';
  // 办理信息展示
  // 办理进度
  var undertakeSituationPlaceHolder = $("input[name='undertakeSituationPlaceHolder']:checked", $dialog).val();
  newFlow.undertakeSituationPlaceHolder =
    undertakeSituationPlaceHolder == '1'
      ? undertakeSituationPlaceHolder
      : $("input[name='undertakeSituationPlaceHolderValue']", $dialog).val();
  // 信息分发
  var infoDistributionPlaceHolder = $("input[name='infoDistributionPlaceHolder']:checked", $dialog).val();
  newFlow.infoDistributionPlaceHolder =
    infoDistributionPlaceHolder == '1' ? infoDistributionPlaceHolder : $("input[name='infoDistributionPlaceHolderValue']", $dialog).val();
  // 操作记录
  var operationRecordPlaceHolder = $("input[name='operationRecordPlaceHolder']:checked", $dialog).val();
  newFlow.operationRecordPlaceHolder =
    operationRecordPlaceHolder == '1' ? operationRecordPlaceHolder : $("input[name='operationRecordPlaceHolderValue']", $dialog).val();
  return newFlow;
}

function get_relation_dialog_html() {
  return (
    '<form id="relationForm" name="relationForm" class="workflow-popup form-widget">' +
    '<div class="well-form form-horizontal">' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label">子流程</label>' +
    '<div class="well-form-control">' +
    '<input type="text" id="newFlowId" name="newFlowId">' +
    '<input type="hidden" id="newFlowName" name="newFlowName">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label">环节</label>' +
    '<div class="well-form-control">' +
    '<input type="text" id="taskId" name="taskId">' +
    '<input type="hidden" id="taskName" name="taskName">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label">前置子流程</label>' +
    '<div class="well-form-control">' +
    '<input type="text" id="frontNewFlowId" name="frontNewFlowId">' +
    '<input type="hidden" id="frontNewFlowName" name="frontNewFlowName">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label">前置环节</label>' +
    '<div class="well-form-control">' +
    '<input type="text" id="frontTaskId" name="frontTaskId">' +
    '<input type="hidden" id="frontTaskName" name="frontTaskName">' +
    '</div>' +
    '</div>' +
    '</div>' +
    '</form>'
  );
}

function relationLoadEvent($dialog, goForm, subFlowProperty, data, index) {
  var poForm = $('#relationForm', $dialog)[0];
  var newFlows = [];
  $('#DNewFlows > li').each(function () {
    var data = $(this).data('newFlow');
    newFlows.push({
      id: data.value,
      text: data.name,
      data: data
    });
  });
  if (data) {
    poForm.newFlowId.value = data.newFlowId || newFlows[0].id;
    poForm.newFlowName.value = data.newFlowName;
    poForm.taskId.value = data.taskId;
    poForm.taskName.value = data.taskName;
    poForm.frontNewFlowId.value = data.frontNewFlowId;
    poForm.frontNewFlowName.value = data.frontNewFlowName;
    poForm.frontTaskId.value = data.frontTaskId;
    poForm.frontTaskName.value = data.frontTaskName;
  }

  $('#taskId', $dialog).wellSelect({
    labelField: 'taskName',
    valueField: 'taskId',
    searchable: false
  });
  $('#frontTaskId', $dialog).wellSelect({
    labelField: 'frontTaskName',
    valueField: 'frontTaskId',
    searchable: false
  });

  $('#frontNewFlowId', $dialog)
    .wellSelect({
      labelField: 'frontNewFlowName',
      valueField: 'frontNewFlowId',
      defaultBlank: true,
      defaultBlankText: '--选择子流程--',
      defaultBlankValue: '',
      searchable: false
    })
    .on('change', function () {
      var _val = $(this).val();
      $('#frontTaskId', $dialog).wellSelect('val', '');
      if (_val) {
        initTask($dialog, '#frontTaskId', _val, false, false, false);
      } else {
        $('#frontTaskId', $dialog).wellSelect('reRenderOption', {
          data: []
        });
      }
    });

  $('#newFlowId', $dialog)
    .wellSelect({
      labelField: 'newFlowName',
      valueField: 'newFlowId',
      showEmpty: false,
      searchable: false,
      data: newFlows
    })
    .on('change', function (e, type) {
      var _val = $(this).val();
      var _newFlows = $.map(newFlows, function (item) {
        if (item.id !== _val && item.id) {
          return item;
        }
      });

      $('#frontNewFlowId', $dialog)
        .wellSelect('reRenderOption', {
          data: _newFlows
        })
        .trigger('change');

      if (!(type && type === 'init')) {
        $('#frontNewFlowId', $dialog).wellSelect('val', '');
        $('#taskId', $dialog).wellSelect('val', '');
      } else {
        setTimeout(function () {
          if (data) {
            $('#frontTaskId', $dialog).wellSelect('val', data.frontTaskId);
          }
        }, 100);
      }
      // 子流程环节
      initTask($dialog, '#taskId', _val, false, false, false);
    })
    .trigger('change', 'init');
}

function openRelationDialog(subFlowProperty, goForm, data, index) {
  var _html = get_relation_dialog_html();
  var $dialog = top.appModal.dialog({
    title: '前置流程关系',
    message: _html,
    height: '330px',
    size: 'middle',
    zIndex: 999999998,
    shown: function () {
      relationLoadEvent($dialog, goForm, subFlowProperty, data, index);
    },
    buttons: {
      save: {
        label: '保存',
        className: 'well-btn w-btn-primary',
        callback: function () {
          var poForm = $('#relationForm', $dialog)[0];
          if (!$('#newFlowId', $dialog).val()) {
            top.appModal.error('请选择子流程！');
            return false;
          }

          if (!$('#taskId', $dialog).val()) {
            top.appModal.error('请选择环节！');
            return false;
          }
          if (!$('#frontNewFlowId', $dialog).val()) {
            top.appModal.error('请选择前置子流程！');
            return false;
          }
          if (!$('#frontTaskId', $dialog).val()) {
            top.appModal.error('请选择前置环节！');
            return false;
          }

          var result = {
            newFlowId: poForm.newFlowId.value,
            newFlowName: poForm.newFlowName.value,
            taskId: poForm.taskId.value,
            taskName: poForm.taskName.value,
            frontNewFlowId: poForm.frontNewFlowId.value,
            frontNewFlowName: poForm.frontNewFlowName.value,
            frontTaskId: poForm.frontTaskId.value,
            frontTaskName: poForm.frontTaskName.value
          };
          if (data) {
            var $li = $('#DRelations > li[data-index="' + data._index + '"]');
            $li.data('relation', result);
            $li.find('.newFlowName').text('子流程：' + result.newFlowName);
            $li.find('.taskName').text('环节：' + result.taskName);
            $li.find('.frontNewFlowName').text('前置子流程：' + result.frontNewFlowName);
            $li.find('.frontTaskName').text('前置环节：' + result.frontTaskName);
          } else {
            renderRelations(result, index + 1);
          }
        }
      },
      close: {
        label: '取消',
        className: 'btn-default',
        callback: function () {
        }
      }
    }
  });
}

// 流转设置 -- 列定义弹窗 html
function initColumnSetDialog() {
  var html = '';
  html +=
    '<form  id="columnSetForm" class="workflow-popup form-widget">' +
    '<div class="well-form form-horizontal">' +
    '<div class="form-group">' +
    '<label for="ruleName" class="well-form-label control-label required">类型</label>' +
    '<div class="well-form-control">' +
    '<input type="radio" class="form-control" id="columnType1" name="columnType" value="1">' +
    '<label for="columnType1">固定列</label>' +
    '<input type="radio" class="form-control" id="columnType2" name="columnType" value="2">' +
    '<label for="columnType2">扩展列</label>' +
    '</div>' +
    '</div>' +
    '<div class="form-group column_type column_type_1">' +
    '<label for="fieldName" class="well-form-label control-label required">选择固定列</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="columnName_1" name="columnName_1"  placeholder="请选择固定列">' +
    '</div>' +
    '</div>' +
    '<div class="form-group column_type column_type_1">' +
    '<label for="operator" class="well-form-label control-label">新名称</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="columnNewName" name="columnNewName">' +
    '</div>' +
    '</div>' +
    '<div class="form-group column_type column_type_2">' +
    '<label for="fieldName" class="well-form-label control-label required">列名</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="columnName_2" name="columnName_2">' +
    '</div>' +
    '</div>' +
    '<div class="form-group column_type column_type_2">' +
    '<label for="fieldName" class="well-form-label control-label required">列值来源</label>' +
    '<div class="well-form-control">' +
    '<div id="columnFromTable">' +
    '<div id="btn_add_column" class="well-btn w-btn-primary well-btn-sm">增加</div>' +
    '<div id="btn_remove_column" class="well-btn w-btn-primary well-btn-sm">删除</div>' +
    '</div>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label">参与关键字查询</label>' +
    '<div class="well-form-control">' +
    '<input type="checkbox" id="searchFlag" name="searchFlag">' +
    '<label for="searchFlag"></label>' +
    '</div>' +
    '</div>' +
    '</div>' +
    '</form>';
  return html;
}

function showColumnSetDialog(goForm, subFlowProperty, table, fixedField, data, rowKey) {
  var html = initColumnSetDialog();
  var $columnSetDialog = top.appModal.dialog({
    title: '办理进度列定义',
    message: html,
    size: 'large',
    height: '500',
    shown: function () {
      var columnFromTableData = [];
      if (data) {
        $('input[name="columnType"][value="' + data.type + '"]', $columnSetDialog)
          .prop('checked', true)
          .trigger('change');
        if (data.type === '1') {
          $('#columnName_1', $columnSetDialog).val(data.index);
          $('#columnNewName', $columnSetDialog).val(data.name);
        } else {
          $('#columnName_2', $columnSetDialog).val(data.name);
          var newSources = data.sources.split(';');
          columnFromTableData = $.map(newSources, function (item) {
            var itemArr = item.split(',');
            return {
              id: itemArr[0],
              field: itemArr[1]
            };
          });
        }

        $('#searchFlag', $columnSetDialog)
          .prop('checked', data.searchFlag == '1' ? true : false)
          .trigger('change');
      } else {
        $('input[name="columnType"][value="1"]', $columnSetDialog).prop('checked', true).trigger('change');
      }

      $('input[name="columnType"]', $columnSetDialog)
        .on('change', function () {
          var val = $('input[name="columnType"]:checked', $columnSetDialog).val();
          $('.column_type', $columnSetDialog).hide();
          $('.column_type_' + val, $columnSetDialog).show();
          if (val == '1') {
            // 除时间类外，其他默认选中且可以点击
            var columnName_1 = $('#columnName_1', $columnSetDialog).val();
            if (columnName_1 == 'dueTime' || columnName_1 == 'remainingTime') {
              //|| columnName_1 == 'workProcesses'
              $('#searchFlag', $columnSetDialog).prop('checked', false).attr('disabled', 'disabled');
            } else {
              $('#searchFlag', $columnSetDialog).prop('checked', true).removeAttr('disabled', 'disabled');
            }
            // if (
            //   columnName_1 == 'todoName' ||
            //   columnName_1 == 'currentTaskName' ||
            //   columnName_1 == 'currentTodoUserName' ||
            //   columnName_1 == 'resultFiles'
            // ) {
            //   $('#searchFlag', $columnSetDialog).prop('checked', true).removeAttr('disabled', 'disabled');
            // } else if (columnName_1 == 'dueTime' || columnName_1 == 'remainingTime' || columnName_1 == 'workProcesses') {
            //   $('#searchFlag', $columnSetDialog).prop('checked', false).attr('disabled', 'disabled');
            // }
          } else {
            var typeName = $('.column_type_' + val, $columnSetDialog).val();
            if (columnFromTableData.length == 0 || !typeName) {
              $('#searchFlag', $columnSetDialog).prop('checked', false);
            }
            $('#searchFlag', $columnSetDialog).removeAttr('disabled', 'disabled');
          }
        })
        .trigger('change');

      $('#columnName_1', $columnSetDialog)
        .val(data && data.index ? data.index : 'todoName')
        .wellSelect({
          data: fixedField,
          valueField: 'columnName_1',
          searchable: false,
          showEmpty: false
        })
        .on('change', function (e, type) {
          var val = $(this).val();
          // 除时间类外，其他默认选中且可以点击
          if (val == 'dueTime' || val == 'remainingTime') {
            // || val == 'workProcesses'
            $('#searchFlag', $columnSetDialog).prop('checked', false).attr('disabled', 'disabled');
          } else {
            $('#searchFlag', $columnSetDialog).prop('checked', true).removeAttr('disabled', 'disabled');
          }
          // if (val == 'todoName' || val == 'currentTaskName' || val == 'currentTodoUserName' || val == 'resultFiles') {
          //   $('#searchFlag', $columnSetDialog).prop('checked', true).removeAttr('disabled', 'disabled');
          // } else if (val == 'dueTime' || val == 'remainingTime') {
          //   // || val == 'workProcesses'
          //   $('#searchFlag', $columnSetDialog).prop('checked', false).attr('disabled', 'disabled');
          // }
          if ($('#columnNewName', $columnSetDialog).val() === '') {
            var text = $('#columnName_1', $columnSetDialog).wellSelect('data').text;
            if (type && type === 'init') {
              return;
            }
            $('#columnNewName', $columnSetDialog).val(text);
          }
        })
        .trigger('change', 'init');
      if (data && data.type === '1' && data.index) {
        $('#columnName_1', $columnSetDialog).wellSelect('val', data.index);
      }

      var newFlows = [
        {
          id: '',
          text: '请选择子流程'
        }
      ];

      $('#DNewFlows > li').each(function (i, item) {
        var newFlow = $(item).data('newFlow');
        newFlows.push({
          id: newFlow.value,
          text: newFlow.name
        });
      });

      var $columnFromTable = $('#columnFromTable', $columnSetDialog);
      $columnFromTable.wellEasyTable({
        data: columnFromTableData,
        checkAll: true,
        columns: [
          {
            field: 'checked',
            checkbox: true
          },
          {
            field: 'id',
            title: '子流程',
            editable: {
              type: 'select',
              options: {
                data: newFlows,
                defaultBlankText: '请选择子流程',
                searchable: false,
                showEmpty: false
              }
            }
          },
          {
            field: 'field',
            title: '字段',
            editable: {
              type: 'select',
              options: {
                data: []
              }
            }
          }
        ],
        addBtn: '#btn_add_column',
        removeBtn: '#btn_remove_column',
        rowDataChangeEvent: function ($tr, rowKey) {
          $tr
            .find('#id_' + rowKey)
            .on('change', function (e, type) {
              var val = $(this).val();
              if (type !== 'init') {
                $tr.find('#field_' + rowKey).val('');
              }
              if (val) {
                $.get({
                  url: ctx + '/api/workflow/definition/getFormFieldsByFlowDefId',
                  data: {
                    flowDefId: val
                  },
                  success: function (result) {
                    var data = $.map(result.data, function (item) {
                      return {
                        id: item.id,
                        text: item.name
                      };
                    });
                    $tr.find('#field_' + rowKey).wellSelect('reRenderOption', {
                      data: data
                    });
                  }
                });
              } else {
                $tr.find('#field_' + rowKey).wellSelect('reRenderOption', {
                  data: []
                });
              }
            })
            .trigger('change', 'init');
        }
      });
    },
    buttons: {
      save: {
        label: '确定',
        className: 'well-btn w-btn-primary',
        callback: function () {
          var tableData = table.wellEasyTable('getAllData');
          var lamp = {};
          lamp.type = $('input[name="columnType"]:checked', $columnSetDialog).val();
          lamp.typeName = lamp.type == '1' ? '固定' : '扩展';
          if (lamp.type === '') {
            top.appModal.error('类型不能为空');
            return false;
          }

          if (lamp.type === '1') {
            if ($('#columnName_1', $columnSetDialog).val() === '') {
              top.appModal.error('列名不能为空');
              return false;
            }
            lamp.tempName = $('#columnName_1', $columnSetDialog).wellSelect('data').text;
            lamp.index = $('#columnName_1', $columnSetDialog).val();
            lamp.name = $('#columnNewName', $columnSetDialog).val() || lamp.tempName;
            lamp.sources = '';
            lamp.extraColumn = (data && data.extraColumn) || lamp.index;
          } else {
            if ($('#columnName_2', $columnSetDialog).val() === '') {
              top.appModal.error('列名不能为空');
              return false;
            }
            lamp.tempName = '';
            lamp.index = data && data.extraColumn;
            lamp.name = $('#columnName_2', $columnSetDialog).val();
            var columnsData = $('#columnFromTable', $columnSetDialog).wellEasyTable('getAllData');
            var _sources = $.map(columnsData, function (item) {
              return [item.id, item.field].join(',');
            });
            lamp.sources = _sources.join(';');
            lamp.extraColumn = data && data.extraColumn;
          }
          lamp.searchFlag = $('#searchFlag', $columnSetDialog).prop('checked') ? '1' : '0';

          if (lamp.type === '2' && !lamp.extraColumn) {
            // 扩展列增加一个字段
            lamp.extraColumn = getExtraColumn(tableData);
            lamp.index = lamp.extraColumn;
          }

          if (data) {
            table.wellEasyTable('updateRowData', rowKey, lamp);
          } else {
            if (lamp.type == '1') {
              var sameFieldIndex = _.findIndex(tableData, function (o) {
                return o.index == lamp.index;
              });
              if (sameFieldIndex > -1) {
                top.appModal.error('固定列不能选择重复！');
                return false;
              }
            }
            table.wellEasyTable('addRowData', lamp);
          }
        }
      },
      cancel: {
        label: '取消',
        className: 'btn-default',
        callback: function () {
        }
      }
    }
  });
}

function getExtraColumn(tableData) {
  var extraColumns = _.filter(tableData, function (o) {
    return o.type == '2' && o.extraColumn;
  });
  if (extraColumns.length == 0) {
    var extraColumn = 'extraColumn0';
  } else {
    var newExtraColumns = _.sortBy(extraColumns, function (o) {
      return o.extraColumn;
    });
    var currExtraColumn = newExtraColumns[newExtraColumns.length - 1].extraColumn;
    var len = currExtraColumn.split('extraColumn')[1] - 0 + 1;
    var extraColumn = 'extraColumn' + len;
  }
  return extraColumn;
}

function changeDesignerObjText() {
  var goForm = $('#subflowForm')[0];
  var subFlowProperty = goWorkFlow.curSubflow.xmlObject;
  subFlowProperty.setAttribute('name', goForm.name.value);
  top.changeObjText(goWorkFlow.curSubflow);
}

function collectPropertiesData() {
  var flowXML = goWorkFlow.flowXML;
  var subFlowProperty = goWorkFlow.curSubflow.xmlObject;
  var userDetail = SpringSecurityUtils.getUserDetails();
  var goForm = $('#subflowForm')[0];
  collectSubFormPropertiesData(flowXML, subFlowProperty, goForm, userDetail);
  collectSubFormInfoPropertiesData(flowXML, subFlowProperty, goForm, userDetail);
  top.changeObjText(goWorkFlow.curSubflow);
  return true;
}

/**
 * 收集子流程tab属性数据
 * @param flowXML
 * @param subFlowProperty
 * @param goForm
 * @param userDetail
 */
function collectSubFormPropertiesData(flowXML, subFlowProperty, goForm, userDetail) {
  subFlowProperty.setAttribute('name', goForm.name.value);
  subFlowProperty.setAttribute('id', goForm.id.value);
  // 业务类别
  var businessType = subFlowProperty.selectSingleNode('businessType');
  if (businessType != null) {
    subFlowProperty.removeChild(businessType);
  }
  oAddElement(subFlowProperty, 'businessType', $('#businessType', goForm).val());
  var businessTypeName = subFlowProperty.selectSingleNode('businessTypeName');
  if (businessTypeName != null) {
    subFlowProperty.removeChild(businessTypeName);
  }
  oAddElement(subFlowProperty, 'businessTypeName', $('#businessTypeName', goForm).val());
  // 业务角色
  var businessRole = subFlowProperty.selectSingleNode('businessRole');
  if (businessRole != null) {
    subFlowProperty.removeChild(businessRole);
  }
  oAddElement(subFlowProperty, 'businessRole', $('#businessRole', goForm).val());
  var businessRoleName = subFlowProperty.selectSingleNode('businessRoleName');
  if (businessRoleName != null) {
    subFlowProperty.removeChild(businessRoleName);
  }
  oAddElement(subFlowProperty, 'businessRoleName', $('#businessRoleName', goForm).val());
  // 子流程
  var newFlows = subFlowProperty.selectSingleNode('newFlows');
  if (newFlows != null) {
    subFlowProperty.removeChild(newFlows);
  }
  newFlows = oSetElement(subFlowProperty, 'newFlows');
  var $newFlows = $('#DNewFlows > li', goForm);
  $.each($newFlows, function (i, newFlow) {
    var newFlow = $(newFlow).data('newFlow');
    var loNewFlow = oAddElement(newFlows, 'newFlow');
    oAddElement(loNewFlow, 'id', newFlow.id);
    oAddElement(loNewFlow, 'name', newFlow.name);
    oAddElement(loNewFlow, 'value', newFlow.value);
    oAddElement(loNewFlow, 'label', newFlow.label);
    oAddElement(loNewFlow, 'conditions', newFlow.conditions);
    oAddElement(loNewFlow, 'interfaceName', newFlow.interfaceName);
    oAddElement(loNewFlow, 'interfaceValue', newFlow.interfaceValue);
    oAddElement(loNewFlow, 'createName', newFlow.createName);
    oAddElement(loNewFlow, 'createValue', newFlow.createValue);
    oAddElement(loNewFlow, 'createWay', newFlow.createWay);
    oAddElement(loNewFlow, 'createWayName', newFlow.createWayName);
    oAddElement(loNewFlow, 'createInstanceFormId', newFlow.createInstanceFormId);
    oAddElement(loNewFlow, 'createInstanceFormName', newFlow.createInstanceFormName);
    oAddElement(loNewFlow, 'createInstanceWay', newFlow.createInstanceWay);
    oAddElement(loNewFlow, 'createInstanceWayName', newFlow.createInstanceWayName);
    oAddElement(loNewFlow, 'isMainFormCreateWay', newFlow.isMainFormCreateWay);
    oAddElement(loNewFlow, 'subformId', newFlow.subformId);
    oAddElement(loNewFlow, 'subformIdName', newFlow.subformIdName);
    oAddElement(loNewFlow, 'taskUsers', newFlow.taskUsers);
    oAddElement(loNewFlow, 'taskUsersName', newFlow.taskUsersName);
    oAddElement(loNewFlow, 'createInstanceBatch', newFlow.createInstanceBatch);
    oAddElement(loNewFlow, 'isMajor', newFlow.isMajor);
    oAddElement(loNewFlow, 'isMerge', newFlow.isMerge);
    oAddElement(loNewFlow, 'isWait', newFlow.isWait);
    oAddElement(loNewFlow, 'isShare', newFlow.isShare);
    oAddElement(loNewFlow, 'toTaskName', newFlow.toTaskName);
    oAddElement(loNewFlow, 'toTaskId', newFlow.toTaskId);
    oAddElement(loNewFlow, 'copyFieldNames', newFlow.copyFieldNames);
    oAddElement(loNewFlow, 'copyFields', newFlow.copyFields);
    oAddElement(loNewFlow, 'copyBotRuleName', newFlow.copyBotRuleName);
    oAddElement(loNewFlow, 'copyBotRuleId', newFlow.copyBotRuleId);
    oAddElement(loNewFlow, 'syncBotRuleName', newFlow.syncBotRuleName);
    oAddElement(loNewFlow, 'syncBotRuleId', newFlow.syncBotRuleId);
    oAddElement(loNewFlow, 'returnWithOver', newFlow.returnWithOver);
    oAddElement(loNewFlow, 'returnWithDirection', newFlow.returnWithDirection);
    oAddElement(loNewFlow, 'returnDirectionName', newFlow.returnDirectionName);
    oAddElement(loNewFlow, 'returnDirectionId', newFlow.returnDirectionId);
    oAddElement(loNewFlow, 'returnBotRuleName', newFlow.returnBotRuleName);
    oAddElement(loNewFlow, 'returnBotRuleId', newFlow.returnBotRuleId);
    oAddElement(loNewFlow, 'returnOverrideFieldNames', newFlow.returnOverrideFieldNames);
    oAddElement(loNewFlow, 'returnOverrideFields', newFlow.returnOverrideFields);
    oAddElement(loNewFlow, 'returnAdditionFieldNames', newFlow.returnAdditionFieldNames);
    oAddElement(loNewFlow, 'returnAdditionFields', newFlow.returnAdditionFields);
    oAddElement(loNewFlow, 'notifyDoing', newFlow.notifyDoing);
    oAddElement(loNewFlow, 'undertakeSituationPlaceHolder', newFlow.undertakeSituationPlaceHolder);
    oAddElement(loNewFlow, 'infoDistributionPlaceHolder', newFlow.infoDistributionPlaceHolder);
    oAddElement(loNewFlow, 'operationRecordPlaceHolder', newFlow.operationRecordPlaceHolder);
    oAddElement(loNewFlow, 'title', newFlow.title);
    oAddElement(loNewFlow, 'titleExpression', newFlow.titleExpression);
    // oAddElement(loNewFlow, 'expandList', newFlow.expandList);
  });

  var relations = subFlowProperty.selectSingleNode('relations');
  if (relations != null) {
    subFlowProperty.removeChild(relations);
  }
  relations = oSetElement(subFlowProperty, 'relations');
  var $relations = $('#DRelations > li', goForm);
  $.each($relations, function (i, relation) {
    var relation = $(relation).data('relation');
    var loRelation = oAddElement(relations, 'relation');
    oAddElement(loRelation, 'newFlowName', relation.newFlowName);
    oAddElement(loRelation, 'newFlowId', relation.newFlowId);
    oAddElement(loRelation, 'taskName', relation.taskName);
    oAddElement(loRelation, 'taskId', relation.taskId);
    oAddElement(loRelation, 'frontNewFlowName', relation.frontNewFlowName);
    oAddElement(loRelation, 'frontNewFlowId', relation.frontNewFlowId);
    oAddElement(loRelation, 'frontTaskName', relation.frontTaskName);
    oAddElement(loRelation, 'frontTaskId', relation.frontTaskId);
  });

  bSetUnitFieldToXML(subFlowProperty, goForm, 'subTaskMonitor');

  var traceTask = subFlowProperty.selectSingleNode('traceTask');
  if (traceTask != null) {
    subFlowProperty.removeChild(traceTask);
  }
  oAddElement(subFlowProperty, 'traceTask', $('#traceTask', goForm).val());
}

function collectSubFormInfoPropertiesData(flowXML, subFlowProperty, goForm, userDetail) {
  // 办理进度显示位置
  var undertakeSituationPlaceHolder = $('#undertakeSituationPlaceHolder', goForm).val();
  var $undertakeSituationPlaceHolder = subFlowProperty.children('undertakeSituationPlaceHolder');
  if ($undertakeSituationPlaceHolder != null) {
    $undertakeSituationPlaceHolder.remove();
    // subFlowProperty.removeChild($undertakeSituationPlaceHolder);
  }
  oAddElement(subFlowProperty, 'undertakeSituationPlaceHolder', undertakeSituationPlaceHolder);

  // 列表默认展开
  var expandList = $('input[name="expandList"]', goForm).prop('checked') ? '1' : '0';
  var $expandList = subFlowProperty.selectSingleNode('expandList');
  if ($expandList != null) {
    subFlowProperty.removeChild($expandList);
  }
  oAddElement(subFlowProperty, 'expandList', expandList);

  // 办理进度列表标题表达式
  var undertakeSituationTitleExpression = $('#undertakeSituationTitleExpression', goForm).text();
  var $undertakeSituationTitleExpression = subFlowProperty.selectSingleNode('undertakeSituationTitleExpression');
  if ($undertakeSituationTitleExpression != null) {
    subFlowProperty.removeChild($undertakeSituationTitleExpression);
  }

  // 承办情况操作按钮
  var $undertakeSituationButtons = subFlowProperty.selectSingleNode('undertakeSituationButtons');
  if ($undertakeSituationButtons != null) {
    subFlowProperty.removeChild($undertakeSituationButtons);
  }
  $undertakeSituationButtons = oSetElement(subFlowProperty, 'undertakeSituationButtons');
  var $btnSettingTable = $('#btnSettingTable', $('#subFlowInfo'));
  var undertakeSituationButtons = $btnSettingTable.wellEasyTable('getAllData');
  $.each(undertakeSituationButtons, function (i, item) {
    var loButton = oAddElement($undertakeSituationButtons, 'button');
    oAddElement(loButton, 'id', item.id);
    oAddElement(loButton, 'name', item.name);
    oAddElement(loButton, 'newName', item.newName || item.name);
  });

  // 承办情况列配置
  var $undertakeSituationColumns = subFlowProperty.selectSingleNode('undertakeSituationColumns');
  if ($undertakeSituationColumns != null) {
    subFlowProperty.removeChild($undertakeSituationColumns);
  }
  $undertakeSituationColumns = oSetElement(subFlowProperty, 'undertakeSituationColumns');
  var $columnSettingTable = $('#columnSettingTable', $('#subFlowInfo'));
  var undertakeSituationColumns = $columnSettingTable.wellEasyTable('getAllData');
  $.each(undertakeSituationColumns, function (i, item) {
    var loColumn = oAddElement($undertakeSituationColumns, 'column');
    oAddElement(loColumn, 'type', item.type);
    oAddElement(loColumn, 'typeName', item.typeName);
    oAddElement(loColumn, 'index', item.index);
    oAddElement(loColumn, 'name', item.name);
    oAddElement(loColumn, 'sources', item.sources);
    oAddElement(loColumn, 'searchFlag', item.searchFlag);
    oAddElement(loColumn, 'tempName', item.tempName);
    oAddElement(loColumn, 'extraColumn', item.extraColumn);
  });

  var sortRule = $('input[name="sortRule"]:checked', goForm).val();
  var $sortRule = subFlowProperty.selectSingleNode('sortRule');
  if ($sortRule != null) {
    subFlowProperty.removeChild($sortRule);
  }
  oAddElement(subFlowProperty, 'sortRule', sortRule);

  // 列排序配置
  var $undertakeSituationOrders = subFlowProperty.selectSingleNode('undertakeSituationOrders');
  if ($undertakeSituationOrders != null) {
    subFlowProperty.removeChild($undertakeSituationOrders);
  }
  $undertakeSituationOrders = oSetElement(subFlowProperty, 'undertakeSituationOrders');
  var $sortSettingTable = $('#sortSettingTable', $('#subFlowInfo'));
  var undertakeSituationOrders = $sortSettingTable.wellEasyTable('getAllData');
  $.each(undertakeSituationOrders, function (i, item) {
    var loSort = oAddElement($undertakeSituationOrders, 'order');
    oAddElement(loSort, 'name', item.name);
    oAddElement(loSort, 'direction', item.direction || 'asc');
  });

  oAddElement(subFlowProperty, 'undertakeSituationTitleExpression', undertakeSituationTitleExpression);
  // 信息分发显示位置
  var infoDistributionPlaceHolder = $('#infoDistributionPlaceHolder', goForm).val();
  var $infoDistributionPlaceHolder = subFlowProperty.children('infoDistributionPlaceHolder');
  if ($infoDistributionPlaceHolder != null) {
    $infoDistributionPlaceHolder.remove();
    // subFlowProperty.removeChild($infoDistributionPlaceHolder);
  }
  oAddElement(subFlowProperty, 'infoDistributionPlaceHolder', infoDistributionPlaceHolder);

  // 信息分发列表标题表达式
  var infoDistributionTitleExpression = $('#infoDistributionTitleExpression', goForm).text();
  var $infoDistributionTitleExpression = subFlowProperty.selectSingleNode('infoDistributionTitleExpression');
  if ($infoDistributionTitleExpression != null) {
    subFlowProperty.removeChild($infoDistributionTitleExpression);
  }
  oAddElement(subFlowProperty, 'infoDistributionTitleExpression', infoDistributionTitleExpression);
  // 操作记录显示位置
  var operationRecordPlaceHolder = $('#operationRecordPlaceHolder', goForm).val();
  var $operationRecordPlaceHolder = subFlowProperty.children('operationRecordPlaceHolder');
  if ($operationRecordPlaceHolder != null) {
    $operationRecordPlaceHolder.remove();
    // subFlowProperty.removeChild($operationRecordPlaceHolder);
  }
  oAddElement(subFlowProperty, 'operationRecordPlaceHolder', operationRecordPlaceHolder);

  // 操作记录列表标题表达式
  var operationRecordTitleExpression = $('#operationRecordTitleExpression', goForm).text();
  var $operationRecordTitleExpression = subFlowProperty.selectSingleNode('operationRecordTitleExpression');
  if ($operationRecordTitleExpression != null) {
    subFlowProperty.removeChild($operationRecordTitleExpression);
  }
  oAddElement(subFlowProperty, 'operationRecordTitleExpression', operationRecordTitleExpression);
}
