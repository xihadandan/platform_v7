goWorkFlow = parent.goWorkFlow;
var saveSetting = true;
// var goWorkFlow.formFieldDatas = null;
var noEditField = ['formUuid', '6', '4', '33', '130', 'relaTableViewId', 'relaFileLibraryId'];

// 设置环节属性
function setTaskProperty() {
  var loTask = goWorkFlow.curTaskObj;
  saveCurrentSetting();
  TaskLoadEvent(loTask, loTask.xmlObject);
  TaskInitEvent(loTask, loTask.xmlObject);

  if (goWorkFlow.readonlyMode) {
    setFrameReadOnly(window);

    // 专属设置
    $('.form-right, .setFiles, .setSubForm', '#formSetting').hide();
    $('#formLayoutTable').addClass('form-disabled');
  }

  top.appModal.hideMask();
}

function TaskLoadEvent(loTask, taskProperty) {
  forbidEditRefFlow();
  var goForm = $('#taskForm')[0];
  if (goForm == null) {
    return;
  }

  taskBaseProperty(goForm, taskProperty);
  rightsProperty(goForm, taskProperty);
  parallelGateway(goForm, taskProperty);
  scripts(goForm, taskProperty);
  formTable(goForm, taskProperty);
}

function TaskInitEvent(loTask, taskProperty) {
  var goForm = $('#taskForm')[0];

  if (goForm == null) {
    return;
  }
  $('#btn_save', goForm)
    .button()
    .click(function (e) {
      TaskOKEvent(loTask, taskProperty);
      if (loTask.xmlObject != null) {
        bSetObjectText(loTask);
      }
      // alert("设置成功!");
    });
  $('#btn_abandon', goForm)
    .button()
    .click(function (e) {
      saveSetting = false;
      // $(loTask).trigger("dblclick");
      setTaskProperty(loTask, window.eWorkFlow.contentWindow, goWorkFlow);
    });
  // 清空
  $('.clearAll')
    .off()
    .on('click', function () {
      $(this).parent().next().empty(); // removeClass('work-flow-handler-list');
      // $(this).prev('.work-flow-set-tips').show();
      var type = $(this).data('type');

      if (type) {
        // $(this).hide();
        clearMember(type);
        //        if (type == 'copyUser') {
        //          $('#isSetCopyUser').val('2');
        //        } else if (type == 'user') {
        //          $('#isSetUser').val('2');
        //        } else if (type == 'monitor') {
        //          $('#isSetMonitor').val('2');
        //        }
      }
    });
  // 更多
  $('.work-flow-more')
    .off()
    .on('click', function () {
      if ($(this).next('.more-content:visible').length > 0) {
        $(this).next('.more-content').hide();
        $(this).find('i').removeClass('icon-ptkj-xianmiaoshuangjiantou-shang').addClass('icon-ptkj-xianmiaoshuangjiantou-xia');
      } else {
        $(this).next('.more-content').show();
        $(this).find('i').removeClass('icon-ptkj-xianmiaoshuangjiantou-xia').addClass('icon-ptkj-xianmiaoshuangjiantou-shang');
      }
    });
  initBaseEvent(goForm, taskProperty);
  initRightEvent(goForm, taskProperty);
  initFormEvent(goForm, taskProperty);
  initTransEvent(goForm, taskProperty);
  initAdvanceEvent(goForm, taskProperty);
}

function initBaseEvent(goForm, taskProperty) {
  // 设置办理人
  $('input[name="isSetUser"')
    .off()
    .on('change', function () {
      if ($(this).val() == '1') {
        $('.work-flow-set-users').show();
      } else {
        $('.work-flow-set-users').hide();
      }
    });
  // 设置转办人
  $('input[name="isSetTransferUser"')
    .off()
    .on('change', function () {
      if ($(this).val() == '1') {
        $('.work-flow-set-transfer-users').show();
      } else {
        $('.work-flow-set-transfer-users').hide();
      }
    });
  $('#handler')
    .off()
    .on('click', function (e) {
      e.target.title = '选择办理人';
      bFlowActions(26, e, goForm, function () {
        setMembers('user', 'Dusers', '办理人', 'isSetUser');
      });
    });
  $('#transfer')
    .off()
    .on('click', function (e) {
      e.target.title = '选择转办人';
      bFlowActions(33, e, goForm, function () {
        setMembers('transferUser', 'DtransferUsers', '转办人', 'isSetTransferUser');
      });
    });
  // 设置督办人
  $('input[name="isSetMonitor"')
    .off()
    .on('change', function () {
      if ($(this).val() == '1') {
        $('.work-flow-set-monitor').show();
      } else {
        $('.work-flow-set-monitor').hide();
      }
    });
  $('#supervise', '#baseSetting')
    .off()
    .on('click', function (e) {
      e.target.title = '选择督办人';
      bFlowActions(27, e, goForm, function () {
        setMembers('monitor', 'Dmonitors', '督办人', 'isSetMonitor');
      });
    });
  // 设置抄送人
  $('input[name="isSetCopyUser"')
    .off()
    .on('change', function () {
      if ($(this).val() == '1') {
        $('.work-flow-set-copy-users').show();
      } else {
        $('.work-flow-set-copy-users').hide();
      }
    });
  $('#copy', '#baseSetting')
    .off()
    .on('click', function (e) {
      e.target.title = '选择抄送人';
      bFlowActions(28, e, goForm, function () {
        setMembers('copyUser', 'DcopyUsers', '抄送人', 'isSetCopyUser');
      });
    });
  // 选择其他办理人
  $('#DemptyToUsers', goForm)
    .off()
    .on('click', function (e) {
      SelectUsers('', 'emptyToUser', '选择其他办理人', null, goForm);
    });

  switchFun('copySetting', $('#baseSetting'), function (bol) {
    if (bol == '0') {
      $('.copy-container').hide();
    } else {
      $('.copy-container').show();
      if ($('input[name="isSetCopyUser"]:checked').length == 0) {
        $('input[value="2"]', '#isSetCopyUser').attr('checked', 'checked').trigger('change');
      }
    }
    // $('#isSetCopyUser').val(bol == '0' ? bol : '2');
  });
  switchFun('superviseSetting', $('#baseSetting'), function (bol) {
    if (bol == '0') {
      $('.supervise-container').hide();
    } else {
      $('.supervise-container').show();
      if ($('input[name="isSetMonitor"]:checked').length == 0) {
        $('input[value="2"]', '#isSetMonitor').attr('checked', 'checked').trigger('change');
      }
    }
    // $('#isSetMonitor').val(bol == '0' ? bol : '2');
  });
  switchFun('mobileApproval', $('#baseSetting'), function (bol) {
    $('#isAllowApp').val(bol);
  });

  // 存在多个办理人时
  $('#isSelectAgain')
    .off()
    .on('click', function () {
      if ($(this).prop('checked')) {
        $(this).parent().find('.second-checkbox').show();
      } else {
        $(this).parent().find('.second-checkbox').hide();
      }
    });
}

function initRightEvent(goForm, taskProperty) {
  // 发起权限
  $('#startAuthSet', '#authSetting')
    .off()
    .on('click', function () {
      showAuthDialog('发起权限设置', taskProperty, 'startRights', '/diction/startRights/right', 3);
    });
  // 待办权限
  $('#waitAuthSet', '#authSetting')
    .off()
    .on('click', function () {
      showAuthDialog('待办权限设置', taskProperty, 'rights', '/diction/rights/right', 10);
    });
  // 已办权限
  $('#handleAuthSet', '#authSetting')
    .off()
    .on('click', function () {
      showAuthDialog('已办权限设置', taskProperty, 'doneRights', '/diction/doneRights/right', 4);
    });
  // 督办权限
  $('#superviseAuthSet', '#authSetting')
    .off()
    .on('click', function () {
      showAuthDialog('督办权限设置', taskProperty, 'monitorRights', '/diction/monitorRights/right', 4);
    });
  // 监控权限
  $('#monitorAuthSet', '#authSetting')
    .off()
    .on('click', function () {
      showAuthDialog('监控权限设置', taskProperty, 'adminRights', '/diction/adminRights/right', 5);
    });
  // 自定义权限
  $('#customAuthSet', '#authSetting')
    .off()
    .on('click', function () {
      showCustomAuthDialog(taskProperty);
    });
  // 编辑自定义权限
  $('#customRightList')
    .off()
    .on('click', '.work-flow-other-item button', function () {
      if ($(this).hasClass('setCustomBtn')) {
        var data = $(this).parent().data('obj');
        var index = $(this).parent().index();
        showCustomAuthDialog(taskProperty, data, index);
      } else {
        $(this).parent().remove();
      }
    });
  // 待办意见立场
  switchFun('enableOpinionPosition', $('#authSetting'), function (val) {
    if (val === '1') {
      $('.enableOpinionPosition').show();
    } else {
      $('.enableOpinionPosition').hide();
    }
  });
  // 待办意见立场开关
  setSwitchFieldValue(goForm, taskProperty, ['enableOpinionPosition']);
  if ($('[name="enableOpinionPosition"]:checked', $('#authSetting')).val() == '1') {
    $('#enableOpinionPosition', $('#authSetting')).closest('.switch-wrap').addClass('active').find('.switch-radio').data('value', '1');
    $('.enableOpinionPosition', $('#authSetting')).show();
  } else {
    $('.enableOpinionPosition', $('#authSetting')).hide();
  }
  // 添加待办意见立场
  $('#addOpinions')
    .off()
    .on('click', function () {
      showOpinionDialog();
    });
  //默认待办意见立场
  $('#defaultOpinions')
    .off()
    .on('click', function () {
      setOpinions(taskProperty, 'opinionList', true);
    });

  // 编辑和删除待办意见立场
  $('#opinionList')
    .off()
    .on('click', '.work-flow-other-item button', function () {
      if ($(this).hasClass('setOpinion')) {
        var dom = $(this).parent().find('div');
        var obj = {
          name: dom.data('name'),
          value: dom.data('value')
        };
        var index = $(this).parent().index();
        showOpinionDialog(obj, index);
      } else if ($(this).hasClass('delOpinion')) {
        $(this).parent().remove();
      }
    });

  $('#granularity').wellSelect({
    searchable: false
  });
}

function initFormEvent(goForm, taskProperty) {
  switchFun('editForm', $('#formSetting'), function (bol) {
    changeTableField(bol, $('#formFieldTable', '#formSetting'));
    $('#canEditForm').val(bol);
  });
  // 表单字段搜索
  $('.form-search')
    .off()
    .on('click', function () {
      $(this).next().find('input').show().focus();
    });

  // 显示清空按钮并搜索
  $('.field-input')
    .off()
    .on('keyup', function (e) {
      var table = $(this).data('table');
      if ($(this).val() != '') {
        $(this).parents('.form-right').find('.form-search-del').show();
      } else {
        $(this).parents('.form-right').find('.form-search-del').hide();
      }
      if (e.keyCode == 13) {
        searchFields(table, $(this).val(), $('#formSetting'));
      }
    })
    .on('blur', function () {
      var table = $(this).data('table');
      if ($(this).val() == '') {
        clearFieldColor(table, $('#formSetting'));
        $(this).hide();
      } else {
        searchFields(table, $(this).val(), $('#formSetting'));
      }
    });

  // 清空搜素内容
  $('.form-search-del')
    .off()
    .on('click', function () {
      $(this).parents('.form-right').find('input').val('').focus();
      $(this).hide();
      var table = $(this).parent().find('.field-input').data('table');
      clearFieldColor(table, $('#formSetting'));
    });

  // 表单字段设置
  $('.form-set-more')
    .off()
    .on('click', function () {
      showFormFieldDialog(taskProperty);
    });

  $('#show_formFieldTable,#edit_formFieldTable,#fill_formFieldTable,#showFormLayout')
    .off()
    .on('click', function () {
      var id = $(this).attr('id');
      var isChecked = $(this).prop('checked');
      var index = $(this).parents('th').index();
      var tbody = $(this).parents('.no-table-borders').find('tbody tr');
      $.each(tbody, function (i, item) {
        var inputs = $(item)
          .find('td:eq(' + index + ')')
          .find('input');
        //#bug 62445 :流程环节-表单设置-视图列表调整为默认勾选，不可编辑
        var inputs_data_id = $(inputs).attr('data-id');
        if (inputs_data_id != undefined && (inputs_data_id.indexOf('tableView') > -1 || inputs_data_id.indexOf('fileLibrary') > -1)) {
          return true;
        }
        if (index == '1' && id != 'showFormLayout') {
          goWorkFlow.formFieldDatas[i].isShowField = isChecked;
          $('.setSubForm')[isChecked ? 'show' : 'hide']();
          $('.setFiles')[isChecked ? 'show' : 'hide']();
          inputs.prop('checked', isChecked);
          if (isChecked) {
            $(item).find('td:eq(2)').find('input').removeAttr('disabled');
            inputs.remove('disabled');
          } else {
            var $item2 = $(item).find('td:eq(2)').find('input');
            $item2.prop('checked') && $item2.trigger('click');
            $item2.attr('disabled', 'disabled');
            // inputs.attr('disabled') != 'disabled' && inputs.prop('checked', isChecked);
          }
        } else {
          inputs.attr('disabled') != 'disabled' && inputs.prop('checked', isChecked);
        }
      });
    });

  $('#formFieldTable,#formLayoutTable')
    .off()
    .on('click', 'tbody tr input', function () {
      var index = $(this).parents('td').index();
      var isChecked = $(this).prop('checked');
      var allChecked = $(this)
        .parents('.no-table-borders')
        .find('thead th:eq(' + index + ')')
        .find('input')
        .prop('checked');

      if ($(this).data('uuid')) {
        var uuid = $(this).data('uuid');
        var subTr = $('#formFieldTable tbody tr');

        $.each(subTr, function (i, item) {
          var subInput = $(item)
            .find('td:eq(' + index + ')')
            .find('input');

          if (subInput.data('subformid') == uuid) {
            subInput.attr('disabled') != 'disabled' && subInput.prop('checked', isChecked);
            if (index == '1') {
              $(item).find('.setFiles')[isChecked ? 'show' : 'hide']();
            }
            if (isChecked && index == '1') {
              var subEdit = $(item).find('td:eq(2)').find('input');
              subEdit.removeAttr('disabled');
            } else if (index == '1' && !isChecked) {
              var subEdit = $(item).find('td:eq(2)').find('input');
              subEdit.prop('checked') && subEdit.trigger('click');
              subEdit.attr('disabled', 'disabled');
            }
          }
        });
      } else if ($(this).data('formuuid')) {
        $(this).parents('tr').find('.setSubForm')[isChecked ? 'show' : 'hide']();
        var uuid = $(this).data('formuuid');
        var subTr = $('#formFieldTable tbody tr');

        $.each(subTr, function (i, item) {
          var subInput = $(item)
            .find('td:eq(' + index + ')')
            .find('input');

          if (subInput.data('uuid') == uuid) {
            if (isChecked) {
              subInput.removeAttr('disabled');
              subInput.parents('td').next().find('input').removeAttr('disabled');
            } else {
              subInput.prop('checked') && subInput.prop('checked', isChecked);
              subInput.attr('disabled', 'disabled');
              subInput.parents('td').next().find('input').attr('disabled', 'disabled');
            }
          }

          if (subInput.data('subformid') == uuid) {
            if (isChecked) {
              subInput.removeAttr('disabled');
              // var subEdit = $(item).find('td:eq(2)').find('input');
              // subEdit.removeAttr('disabled');
            } else {
              $(item).find('.setFiles').hide();
              subInput.prop('checked', isChecked);
              subInput.attr('disabled', 'disabled');
              var subEdit = $(item).find('td:eq(2)').find('input');
              subEdit.prop('checked') && subEdit.trigger('click');
              subEdit.attr('disabled', 'disabled');
            }
            goWorkFlow.formFieldDatas[i].isShowField = isChecked;
          }
          var subInput3 = $(item)
            .find('td:eq(3)')
            .find('input[data-subformid="' + uuid + '"]');
          var subInput4 = $(item)
            .find('td:eq(3)')
            .find('input[data-uuid="' + uuid + '"]');
          if (isChecked) {
            subInput3.removeAttr('disabled');
            subInput4.removeAttr('disabled');
          } else {
            subInput3.prop('checked', false);
            subInput3.attr('disabled', 'disabled');
            subInput4.prop('checked', false);
            subInput4.attr('disabled', 'disabled');
          }
        });
      } else if ($(this).data('subformid')) {
        var uuid = $(this).data('subformid');
        var subformTd = $('#formFieldTable tbody tr')
          .find('td:eq("' + index + '")')
          .find("input[data-subformid='" + uuid + "']");
        var selectAll = true;

        for (var d = 0; d < subformTd.length; d++) {
          if (!$(subformTd[d]).prop('checked')) {
            $('#formFieldTable tbody tr')
              .find('td:eq("' + index + '")')
              .find("input[data-uuid='" + uuid + "']")
              .prop('checked', false);
            selectAll = false;
            break;
          }
        }
        if (selectAll) {
          $('#formFieldTable tbody tr')
            .find('td:eq("' + index + '")')
            .find("input[data-uuid='" + uuid + "']")
            .prop('checked', true);
        }
      }

      var trIndex = $(this).parents('tr').index();
      var tdEdit = $(this)
        .parents('tr')
        .find('#editField' + trIndex);

      if (tdEdit.length > 0 && $(this).attr('id') == 'showField' + trIndex) {
        if (isChecked) {
          tdEdit.removeAttr('disabled');
        } else {
          tdEdit.prop('checked') && tdEdit.trigger('click');
          tdEdit.attr('disabled', 'disabled');
        }
      }
      if ($(this).attr('id') == 'showField' + trIndex && $(this).parents('tr').find('.setFiles').length > 0) {
        $(this).parents('tr').find('.setFiles')[isChecked ? 'show' : 'hide']();
      }

      if (!isChecked && allChecked) {
        $(this)
          .parents('.no-table-borders')
          .find('thead th:eq(' + index + ')')
          .find('input')
          .prop('checked', isChecked);
      } else if (isChecked && !allChecked) {
        var subTr = $('#formFieldTable tbody tr');
        var headChecked = true;
        $.each(subTr, function (i, item) {
          var subInput = $(item)
            .find('td:eq(' + index + ')')
            .find('input');
          if (subInput.length > 0 && !subInput.prop('checked')) {
            headChecked = false;
          }
        });
        $(this)
          .parents('.no-table-borders')
          .find('thead th:eq(' + index + ')')
          .find('input')
          .prop('checked', headChecked);
      }
      if (index == 1) {
        goWorkFlow.formFieldDatas[$(this).parents('tr').index()].isShowField = isChecked;
      }
    });
}

function initTransEvent() {
  $("input[name='forkMode']")
    .off()
    .on('change', function () {
      if ($(this).val() == '1') {
        $('.many-flow-content').hide();
      } else {
        $('.many-flow-content').show();
      }
    });

  // 退回后允许直接提交至本环节
  $('#allowReturnAfterRollback')
    .off()
    .on('click', function () {
      if ($(this).prop('checked')) {
        $(this).parent().find('.second-checkbox').show();
      } else {
        $(this).parent().find('.second-checkbox').hide();
      }
    });
}

var printTemplateUuidComboTreeAfterSetValue = function (values, labels, index) {
  if (index) {
    var printTemplateId = $('#printTemplateId').val().split(';');
    printTemplateId.splice(index, 1);
    $('#printTemplate').val(labels);
    $('#printTemplateId').val(printTemplateId.join(';'));
    $('#printTemplateUuid').val(values);
  } else {
    var treeObj = $.fn.zTree.getZTreeObj('printTemplateUuid_ztree');
    if (treeObj != undefined && typeof treeObj != 'undefined') {
      var nodes = treeObj.getCheckedNodes(true);
      var texts = [],
        ids = [],
        uuids = [];
      $.each(nodes || [], function (i, item) {
        if (item.type === 'PrintTemplate') {
          texts.push(item.data.name);
          ids.push(item.data.id);
          uuids.push(item.data.uuid);
        }
      });
      $('#printTemplate').val(texts.join(';'));
      $('#printTemplateId').val(ids.join(';'));
      $('#printTemplateUuid').val(uuids.join(';'));
    }
  }
};

function initAdvanceEvent(goForm, taskProperty) {
  // 添加信息记录
  $('#addInfoRecord', '#advancedSetting')
    .off()
    .on('click', function () {
      showInfoDialog(taskProperty);
    });

  // 编辑信息记录
  $('#advancedSetting')
    .off()
    .on('click', '.work-flow-other-item button', function () {
      if ($(this).hasClass('editRecordInfo')) {
        var data = $(this).parent().find('div').data('obj');
        var index = $(this).parent().index();
        showInfoDialog(taskProperty, data, index);
      } else if ($(this).hasClass('delRecordInfo')) {
        $(this).parent().remove();
      }
    });

  $('#eventScripts').on('click', 'li .btn-set', function () {
    var data = $(this).closest('li').data('script');
    openEventScript(goForm, taskProperty, data, 'task');
  });

  $('#eventScripts').on('click', 'li .btn-remove', function () {
    $(this).closest('li').remove();
  });
  $('#addEventScript').on('click', function () {
    openEventScript(goForm, taskProperty, null, 'task');
  });

  // 事件脚本
  $('#addScript')
    .off()
    .on('click', function () {
      showScriptDialog();
    });

  // 生成流水号
  var setting = {
    view: {
      showIcon: false,
      showLine: false
    },
    check: {
      enable: true,
      chkStyle: 'radio',
      radioType: 'level'
    },
    async: {
      otherParam: {
        serviceName: 'flowSchemeService',
        methodName: 'getSerialNumbers'
      }
    }
  };
  $('#snName').comboTree({
    labelField: 'snName',
    valueField: 'serialNo',
    treeSetting: setting,
    autoInitValue: false,
    width: 540,
    height: 230,
    valueBy: 'data'
  });

  // 加载的JS模块
  $('#customJsModuleName').wSelect2({
    serviceName: 'appJavaScriptModuleMgr',
    params: {
      dependencyFilter: 'WorkViewFragment'
    },
    labelField: 'customJsModuleName',
    valueField: 'customJsModule',
    defaultBlank: true,
    remoteSearch: false,
    width: '100%',
    height: 250
  });

  var tIdNode = taskProperty.selectSingleNode('printTemplateId');
  var tIdValue = tIdNode != null ? tIdNode.text() : null;
  var tUuidNode = taskProperty.selectSingleNode('printTemplateUuid');
  var tUuidValue = tUuidNode != null ? tUuidNode.text() : null;
  var tNameNode = taskProperty.selectSingleNode('printTemplate');
  var tNameValue = tNameNode ? tNameNode.text() : null;

  var bOld = tUuidNode == null; // 旧版本
  if (bOld) {
    $.get({
      // todo printTemplateIds
      url: ctx + '/api/workflow/work/getPrintTemplateByIds?printtemplateUuid=' + printTemplateId,
      async: false,
      // data: [printTemplateId],
      success: function (result) {
        if (result.code === 0) {
          var printTemplateUuids = [];
          for (var i = 0; i < result.data.length; i++) {
            var datum = result.data[i];
            printTemplateUuids.push(datum.uuid);
          }
          printTemplateUuid = printTemplateUuids.join(';');
        }
      }
    });
  }

  $('#printTemplate').val(tNameValue);
  $('#printTemplateId').val(tIdValue);
  $('#printTemplateUuid').val(tUuidValue);

  var setting = {
    view: {
      showIcon: true,
      showLine: false
    },
    check: {
      enable: true,
      chkStyle: 'checkbox',
      radioType: 'all',
      chkboxType: {
        Y: 's',
        N: 'ps'
      }
    },
    async: {
      otherParam: {
        serviceName: 'printTemplateService',
        methodName: 'getPrintTemplateTree',
        data: []
      }
    }
  };

  $('#printTemplateUuid').comboTree({
    labelField: 'printTemplate',
    valueField: 'printTemplateUuid',
    treeSetting: setting,
    autoInitValue: false,
    width: 530,
    height: 260,
    mutiSelect: true,
    showCheckAll: true,
    showParentCheck: true,
    initExpandAll: true,
    searchWithChildNode: true,
    afterSetValue: printTemplateUuidComboTreeAfterSetValue
  });

  // 事件监听
  var listenerSetting = {
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
        serviceName: 'flowSchemeService',
        methodName: 'getTaskListeners'
      }
    }
  };
  $('#listenerName').comboTree({
    labelField: 'listenerName',
    valueField: 'listener',
    treeSetting: listenerSetting,
    autoInitValue: false,
    mutiSelect: true
  });
}

// 基础设置
function taskBaseProperty(goForm, taskProperty) {
  // 表单字段设置
  setInputXMLValue(goForm, taskProperty, ['name', 'id', 'sn']);
  setInputXMLValue(goForm, taskProperty, [
    'isSetUser',
    'isSetTransferUser',
    'isSetCopyUser',
    'isSetUserEmpty',
    'emptyToTask',
    'emptyNoteDone',
    'isSelectAgain',
    'isOnlyOne',
    'isAnyone',
    'isByOrder',
    'sameUserSubmit',
    'isSetMonitor',
    'isAllowApp',
    'granularity',
    'allowReturnAfterRollback',
    'onlyReturnAfterRollback',
    'notRollback',
    'snName',
    'serialNo',
    'printTemplate',
    'printTemplateId',
    'printTemplateUuid',
    'listenerName',
    'listener',
    'customJsModule',
    'copyUserCondition',
    'canEditForm'
  ]);
  // 前一环节办理人可二次选择抄送人
  setSelectSingleNode(goForm, taskProperty, ['isConfirmCopyUser']);

  $('#name', goForm).on('input propertychange', function () {
    changeDesignerObjText();
  });
  if (!taskProperty.attr('initId')) {
    taskProperty.attr('initId', goForm.id.value);
  }
  // ID显示为文本
  var isSetUserNode = taskProperty.selectSingleNode('isSetUser');
  if (isSetUserNode != null && StringUtils.isNotBlank(isSetUserNode.text())) {
    if ($(taskProperty).data('idAsLabel') === '0') {
    } else {
      $('#id', goForm).hide();
      $('<div>' + $('#id', goForm).val() + '</div>').insertAfter($('#id', goForm));
    }
  } else {
    $(taskProperty).data('idAsLabel', '0');
  }
  $('#id', goForm).on('change', function () {
    var initId = taskProperty.attr('initId');
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

  if ($('#isAllowApp').val() == '0') {
    $('#mobileApproval').removeClass('active');
  } else {
    $('#mobileApproval').addClass('active');
  }
  $('#mobileApproval').find('.switch-radio').data('value', $('#isAllowApp').val());

  changeCheckboxStatus([
    'isSelectAgain',
    'isOnlyOne',
    'isAnyone',
    'isByOrder',
    'emptyNoteDone',
    'allowReturnAfterRollback',
    'onlyReturnAfterRollback',
    'notRollback'
  ]);

  // 办理人、抄送人、督办人 具体字段设置
  // 显示办理人设置信息
  if (goForm.isSetUser.value === '1') {
    setUsers(taskProperty, 'users', 'isSetUser');
  } else {
    $('.work-flow-set-users').hide();
    setUsers(taskProperty, 'users', 'isSetUser');
  }
  if (goForm.isSetTransferUser.value === '1') {
    setUsers(taskProperty, 'transferUsers', 'isSetTransferUser');
  } else {
    $('.work-flow-set-transfer-users').hide();
    setUsers(taskProperty, 'transferUsers', 'isSetTransferUser');
  }
  if (goForm.isSetCopyUser.value === '1') {
    setUsers(taskProperty, 'copyUsers', 'isSetCopyUser');
  } else if (goForm.isSetCopyUser.value !== '' && goForm.isSetCopyUser.value !== '0' && goForm.isSetCopyUser.value !== '1') {
    $('#isSetCopyUser').parent().find('.switch-wrap').addClass('active').find('.switch-radio').data('value', '1');
    $('#isSetCopyUser').parent().find('.switch-wrap').parent().next().show();
    $('.work-flow-set-copy-users').hide();
  } else {
    $('.work-flow-set-copy-users').hide();
  }
  if (goForm.isSetMonitor.value === '1') {
    setUsers(taskProperty, 'monitors', 'isSetMonitor');
  } else if (goForm.isSetMonitor.value === '2') {
    $('#isSetMonitor').parent().find('.switch-wrap').addClass('active').find('.switch-radio').data('value', '1');
    $('#isSetMonitor').parent().find('.switch-wrap').parent().next().show();
    $('.work-flow-set-monitor').hide();
  } else {
    $('.work-flow-set-monitor').hide();
  }
  var unit = taskProperty.selectSingleNode('emptyToUsers') ? taskProperty.selectSingleNode('emptyToUsers').find('unit') : [];
  var allData = getAllDatas(unit);
  if (!allData.userCustom) {
    allData.userCustom = [];
  }
  renderUnitField(allData, goForm, 'emptyToUser');

  // 人员和过滤条件设置
  setMembers('user', 'Dusers', '办理人');
  setMembers('transferUser', 'DtransferUsers', '转办人');
  setMembers('copyUser', 'DcopyUsers', '抄送人');
  setMembers('monitor', 'Dmonitors', '督办人');

  var tasksObj = {};
  var tasksArr = $.map(goWorkFlow.tasks, function (item) {
    if (item && item.Type === 'TASK') {
      var id = item.xmlObject.context.id;
      var text = item.xmlObject.context.getAttribute('name');
      tasksObj[id] = text;
      return {
        id: id,
        text: text
      };
    }
  });

  $('#DemptyToTask', '#baseSetting').wellSelect({
    data: tasksArr.concat([
      {
        id: '<EndFlow>',
        text: '流程结束'
      }
    ]),
    valueField: 'emptyToTask',
    labelField: 'DemptyToTask'
  });
  if ($('#isSetUserEmpty').val() == null) {
    $('#isSetUserEmpty').val(1).trigger('change');
  }
  $('#isSetUserEmpty')
    .wellSelect({
      searchable: false
    })
    .on('change', function () {
      var val = $(this).val();
      if (val == '0') {
        // 由前一环节办理人指定
        $("div[name='DemptyToTask']").hide();
        $('#DemptyToUsers').hide();
        $('#alertOthers').hide();
      } else if (val == '1') {
        // 自动进入下一环节
        $("div[name='DemptyToTask']").show();
        $('#DemptyToUsers').hide();
        $('#alertOthers').show();
      } else {
        // 指定其他人办理
        $("div[name='DemptyToTask']").hide();
        $('#DemptyToUsers').show();
        $('#alertOthers').show();
      }
    })
    .trigger('change');
  if ($('#sameUserSubmit').val() == '') {
    $('#sameUserSubmit').val('0');
  }
  $('#sameUserSubmit').wellSelect({
    searchable: false,
    data: [
      {
        text: '不自动提交并关闭页面',
        id: '2'
      },
      {
        text: '不自动提交并刷新页面',
        id: '3'
      },
      {
        text: '自动提交，让办理人确认是否继承上一环节意见',
        id: '0'
      },
      {
        text: '自动提交，且自动继承意见',
        id: '1'
      }
    ],
    valueField: 'sameUserSubmit'
  });

  $('#isAnyone')
    .off()
    .on('click', function () {
      var propChecked = $(this).prop('checked');
      if ($('#isByOrder').prop('checked') == true && propChecked) {
        $('#isByOrder').prop('checked', false);
      }
    });

  $('#isByOrder')
    .off()
    .on('click', function () {
      var propChecked = $(this).prop('checked');
      if ($('#isAnyone').prop('checked') == true && propChecked) {
        $('#isAnyone').prop('checked', false);
      }
    });
}

// 基础设置 -- 办理人状态切换
function changeCheckboxStatus(arr) {
  for (var i = 0; i < arr.length; i++) {
    var field = arr[i];
    $('#' + field).prop('checked', $('#' + field).val() == '1' ? true : false);
    if ((field == 'isSelectAgain' || field == 'allowReturnAfterRollback') && $('#' + field).val() == '1') {
      $('#' + field)
        .parent()
        .find('.second-checkbox')
        .show();
    }
  }
}

// 基础设置 -- 办理人、督办人、抄送人填值
function setUsers(task, field, ele) {
  var inputField = field.substring(0, field.length - 1);
  var userList = task.selectSingleNode(field) ? task.selectSingleNode(field).find('unit') : [];
  $("input[name='D" + inputField + 1 + "']").val('');
  $("input[name='D" + inputField + 2 + "']").val('');
  $("input[name='D" + inputField + 4 + "']").val('');
  $("input[name='D" + inputField + 8 + "']").val('');
  $("input[name='D" + inputField + 16 + "']").val('');
  $("input[name='" + inputField + 1 + "']").val('');
  $("input[name='" + inputField + 2 + "']").val('');
  $("input[name='" + inputField + 4 + "']").val('');
  $("input[name='" + inputField + 8 + "']").val('');
  $("input[name='" + inputField + 16 + "']").val('');
  if (userList.length > 0) {
    for (var i = 0; i < userList.length; i++) {
      var type = $(userList[i]).attr('type');
      var value = $(userList[i]).find('value').text();
      if (type == '1') {
        var argValue = $(userList[i]).find('argValue').text();
        if ($("input[name='" + inputField + type + "']").val() != undefined && $("input[name='" + inputField + type + "']").val() != '') {
          var oldValue = $("input[name='" + inputField + type + "']").val() + ';';
          var oldArgValue = $("input[name='D" + inputField + type + "']").val() + ';';
        } else {
          var oldValue = '';
          var oldArgValue = '';
        }
        $("input[name='D" + inputField + type + "']").val(oldArgValue + argValue);
        $("input[name='" + inputField + type + "']").val(oldValue + value);
      } else if (type == '16') {
        var argValue = $(userList[i]).find('argValue').text();
        $("input[name='D" + inputField + type + "']").val(argValue);
        $("input[name='" + inputField + type + "']").val(value);
      } else {
        if ($("input[name='" + inputField + type + "']").val() != undefined && $("input[name='" + inputField + type + "']").val() != '') {
          var oldValue = $("input[name='" + inputField + type + "']").val() + ';';
        } else {
          var oldValue = '';
        }
        $("input[name='" + inputField + type + "']").val(oldValue + value);
      }
    }
    $('#' + ele)
      .parent()
      .find('.work-flow-set-tips')
      .hide();
    $('#' + ele)
      .parent()
      .find('.clearAll')
      .show();
  }
  if ($('#' + ele).val() != '0' && ele != 'isSetUser') {
    $('#' + ele)
      .parent()
      .find('.switch-wrap')
      .addClass('active')
      .find('.switch-radio')
      .data('value', '1');
    $('#' + ele)
      .parent()
      .find('.switch-wrap')
      .parent()
      .next()
      .show();
  }
}

// 基础设置 -- 办理人、督办人、抄送人 具体内容
function setMembers(field, container, title, user) {
  $('#' + container).empty(); // removeClass('work-flow-handler-list')
  var html = '';
  var value1 = $("input[name='D" + field + "1']").val();
  var value2 =
    $("input[name='" + field + "2']").val() == ''
      ? ''
      : $("input[name='" + field + "2']")
        .val()
        .split(';');
  var value4 =
    $("input[name='" + field + "4']").val() == ''
      ? ''
      : $("input[name='" + field + "4']")
        .val()
        .split(';');
  var value8 =
    $("input[name='" + field + "8']").val() == ''
      ? ''
      : $("input[name='" + field + "8']")
        .val()
        .split(';');
  var value16 = $("input[name='D" + field + "16']").val();
  var creator = [];
  var same = [];

  if (value1 == '' && value2 == '' && value4 == '' && value8 == '') {
    if ((value16 && value16 == '') || !value16) {
      $('#' + user).val('2');
      $('#' + container)
        .prev('.set-and-clear')
        .find('.clearAll')
        .trigger('click');
      return false;
    }
  }
  $('#' + user).val('1');
  if (value8 != '' && value8.length > 0) {
    for (var j = 0; j < value8.length; j++) {
      var newVal = value8[j];
      if (newVal == 'SameBizRoleOfPriorUser' || newVal == 'SameBizRoleOfCreator'
        || newVal.toLocaleLowerCase().indexOf('same') <= -1) {
        creator.push(handlerListAll[newVal]);
      } else {
        same.push(handlerListAll[newVal]);
      }
    }
  }

  if (value1 != '' || value2.length > 0 || value8.length > 0 || value4.length > 0 || value16 != '') {
    html += '<div class="work-flow-handler-title">' + title + '</div>';
    html += '<ul>';
    if (value1 != '') {
      html +=
        '<li class="work-flow-handler-item handler1"><i class="iconfont icon-ptkj-zuzhi" style="color:#77A7EE;"></i>' + value1 + '</li>';
    }
    if (value2.length > 0) {
      //表单字段
      var newVal2 = [];
      $.each(value2, function (i, item) {
        var itemArr = item.split(':');
        var showName = '';
        if (itemArr.length === 1) {
          showName = formFieldFormat(itemArr[0]);
        } else {
          showName = formFieldFormat(itemArr[1], itemArr[0]);
        }
        newVal2.push(showName);
      });
      html +=
        '<li class="work-flow-handler-item handler2"><i class="iconfont icon-ptkj-biaodanziduan" style="color:#D99066;"></i>' +
        newVal2.join(';') +
        '</li>';
    }
    if (value4.length > 0) {
      var newVal4 = [];
      $.each(value4, function (i, item) {
        $.each(goWorkFlow.tasks, function (i, item2) {
          if (item2.xmlObject.context.id === item) {
            newVal4.push(item2.htmlObject.textContent);
          }
        });
      });
      html +=
        '<li class="work-flow-handler-item handler4"><i class="iconfont icon-ptkj-huanjie" style="color:#F1B755;"></i>' +
        newVal4.join(';') +
        '</li>';
    }
    if (creator.length > 0) {
      html +=
        '<li class="work-flow-handler-item handler8"><i class="iconfont icon-ptkj-guolvxuanxiang" style="color:#F19E55;"></i>' +
        creator.join(';') +
        '</li>';
    }
    if (value16 && value16 != '') {
      html +=
        '<li class="work-flow-handler-item handler16"><i class="iconfont icon-ptkj-zidingyi" style="color:#D97E66;"></i>' +
        value16 +
        '</li>';
    }
    html += '</ul>';
  }
  if (same.length > 0) {
    html += '<div class="work-flow-handler-title">' + title + '过滤条件</div>';
    html += '<ul>';
    html +=
      '<li class="work-flow-handler-item handler18"><i class="iconfont icon-ptkj-guolvxuanxiang" style="color:#F19E55;"></i>' +
      same.join(';') +
      '</li>';
    html += '</ul>';
  }

  $('#' + container)
    .addClass('work-flow-handler-list')
    .html(html)
    .show();
  $('#' + container)
    .prev('.set-and-clear')
    .find('.work-flow-set-tips')
    .hide()
    .next()
    .show();
}

// 基础设置 -- 清空办理人、督办人、抄送人内容
function clearMember(type) {
  $('#D' + type + 'S').val('');
  $('#D' + type + '1').val('');
  $('#' + type + '1').val('');
  $('#' + type + '2').val('');
  $('#' + type + '4').val('');
  $('#' + type + '8').val('');
  if (type != 'monitor') {
    $('#D' + type + '16').val('');
    $('#' + type + '16').val('');
  }
}

// 权限设置
function rightsProperty(goForm, taskProperty) {
  // 发起、待办、已办、督办、监控权限按钮
  if (isFirstTask(taskProperty)) {
    setRightButtons(taskProperty, 'startRights', '/diction/startRights/right');
  } else {
    $('#startRightContainer').hide();
  }

  setRightButtons(taskProperty, 'rights', '/diction/rights/right');
  setRightButtons(taskProperty, 'doneRights', '/diction/doneRights/right');
  setRightButtons(taskProperty, 'monitorRights', '/diction/monitorRights/right');
  setRightButtons(taskProperty, 'adminRights', '/diction/adminRights/right');

  setCustomRightButtons(taskProperty, 'customRightList');

  setOpinions(taskProperty, 'opinionList');
}

// 权限设置 -- 获取权限按钮列表
function getRightList(task, field, path) {
  var laNode = goWorkFlow.dictionXML.selectNodes(path);
  var choseRightNode = $('#' + field).find('li');
  var rights = [];
  for (var i = 0; i < laNode.length; i++) {
    var name = $(laNode[i]).find('name').text();
    var value = $(laNode[i]).find('value').text();
    var code = $(laNode[i]).find('code').text();
    var isDefault = $(laNode[i]).find('isDefault').text();
    var isChecked = false;
    if (task) {
      for (var j = 0; j < choseRightNode.length; j++) {
        var cValue = $(choseRightNode[j]).data('value');
        if (cValue == value) {
          isChecked = true;
          continue;
        }
      }
    } else {
      isChecked = isDefault == 1 ? true : false;
    }

    rights.push({
      name: name,
      value: value,
      code: code,
      isDefault: isDefault,
      isChecked: isChecked
    });
  }
  return rights;
}

// 权限设置 -- 设置权限按钮
function setRightButtons(task, field, path) {
  var html = '';
  var laNode = goWorkFlow.dictionXML.selectNodes(path);
  var choseRightNode = task.selectSingleNode(field) ? task.selectSingleNode(field).find('unit') : [];
  if (task && choseRightNode.length > 0 && laNode != null && laNode.length > 0) {
    for (var j = 0; j < choseRightNode.length; j++) {
      var text = $(choseRightNode[j]).text();
      for (var i = 0; i < laNode.length; i++) {
        var lsValue = $(laNode[i]).find('value').text();
        var lsName = $(laNode[i]).find('name').text();
        var lsCode = $(laNode[i]).find('code').text();
        if (text == lsCode && lsCode != 'B004025') {
          html += '<li class="work-flow-auth-item" data-code="' + lsCode + '" data-value="' + lsValue + '">' + lsName + '</li>';
          continue;
        }
      }
    }
  } else if (!task.selectSingleNode(field)) {
    for (var i = 0; i < laNode.length; i++) {
      var lsValue = $(laNode[i]).find('value').text();
      var lsName = $(laNode[i]).find('name').text();
      var lsCode = $(laNode[i]).find('code').text();
      var lsDefault = $(laNode[i]).find('isDefault').text();
      if (lsDefault == '1' && lsCode != 'B004026') {
        html += '<li class="work-flow-auth-item" data-code="' + lsCode + '" data-value="' + lsValue + '">' + lsName + '</li>';
        continue;
      }
    }
  }

  $('#' + field).html(html);
}

// 权限设置 -- 设置自定义权限按钮值
function setCustomRightButtons(task, field) {
  var btnField = [
    'btnSource',
    'btnId',
    'piUuid',
    'piName',
    'btnClassName',
    'btnStyle',
    'sortOrder',
    'btnIcon',
    'btnRemark',
    'btnRole',
    'btnValue',
    'newName',
    'newCode',
    'newCodeName',
    'useWay',
    'btnArgument',
    'btnOwners',
    'btnOwnerIds',
    'btnUsers',
    'btnUserIds',
    'btnCopyUsers',
    'btnCopyUserIds',
    'btnHanlderPath',
    'hashType',
    'hash',
    'targetPosition',
    'eventParams'
  ];
  var btns = task.selectSingleNode('buttons') ? task.selectSingleNode('buttons').find('button') : [];
  var html = '';
  for (var i = 0; i < btns.length; i++) {
    var lamp = {};
    for (var j = 0; j < btnField.length; j++) {
      lamp[btnField[j]] = $(btns[i]).find(btnField[j]).text() || '';
    }
    html +=
      "<li class='work-flow-other-item' data-obj='" +
      JSON.stringify(lamp) +
      "'><div data-value='33'>" +
      lamp.newName +
      "</div><button class='setCustomBtn' type='button'><i class='iconfont icon-ptkj-shezhi' title='设置'></i></button><button class='delCustomBtn' type='button'><i class='iconfont icon-ptkj-shanchu' title='删除'></i></button></li>";
  }
  $('#' + field).html(html);
}

// 权限设置 -- 设置自定义权限弹窗参与人抄送人的值
function renderUnitValue(data, forms, field) {
  var allData = {
    unitLabel: data.split(',')[0] == '' ? [] : data.split(',')[0].split(';'),
    unitValue: data.split(',')[1] == '' ? [] : data.split(',')[1].split(';'),
    formField: data.split(',')[2] == '' ? [] : data.split(',')[2].split(';'),
    tasks: data.split(',')[3] == '' ? [] : data.split(',')[3].split(';'),
    options: data.split(',')[4] == '' ? [] : data.split(',')[4].split(';')
  };
  renderUnitField(allData, forms, field);
}

function ButtonOKCheck($customRightDialig) {
  var goForm = $customRightDialig.find('#customAuthForm')[0];
  var laTemp = new Array();
  var btnSource = $("input[name='btnSource']:checked", goForm).val();
  if (btnSource == '2') {
    if (goForm.btnName.value == '') {
      top.appModal.error('请输入按钮名称！');
      return false;
    }
    var piUuid = $("input[name='btnHanlderId']", goForm).val();
    if (piUuid == '') {
      top.appModal.error('请选择事件处理！');
      return false;
    }
    var btnHashType = $("input[name='btnHashType']:checked", goForm).val();
    if (btnHashType == '1' && $('#btnAppointHash', goForm).val() == '') {
      top.appModal.error('请选择指定锚点！');
      return false;
    } else if (btnHashType == '2' && $('#btnCustomHash', goForm).val() == '') {
      top.appModal.error('请输入自定义锚点！');
      return false;
    }
  } else {
    if (goForm.DButton.value == '') {
      top.appModal.error('请选择按钮功能！');
      return false;
    }
    if (goForm.newName.value == '') {
      top.appModal.error('请输入按钮名称！');
      return false;
    }
    if (
      (goForm.DButton.value == 'B004002' ||
        goForm.DButton.value == 'B004003' ||
        goForm.DButton.value == 'B004020' ||
        goForm.DButton.value == 'B004016') &&
      goForm.DTask.value == ''
    ) {
      top.appModal.error('请选择目标环节！');
      return false;
    }
  }
  return true;
}

function ButtonOKEvent($customRightDialig) {
  // var gsValue = laArg["Value"];
  var goForm = $customRightDialig.find('#customAuthForm')[0];
  var btnSource = $("input[name='btnSource']:checked", goForm).val();
  if (btnSource == '2') {
    var laTemp = {};
    laTemp.btnSource = btnSource;
    laTemp.newName = goForm.btnName.value;
    laTemp.btnId = goForm.btnId.value;
    laTemp.piUuid = $("input[name='btnHanlderId']", goForm).val();
    laTemp.piName = $("input[name='btnHanlderName']", goForm).val();
    laTemp.btnHanlderPath = $("input[name='btnHanlderPath']", goForm).val();
    laTemp.hashType = $("input[name='btnHashType']:checked", goForm).val();
    if (laTemp.hashType == '1') {
      laTemp.hash = $("input[name='btnAppointHash']", goForm).val();
    } else if (laTemp.hashType == '2') {
      laTemp.hash = $("input[name='btnCustomHash']", goForm).val();
    } else {
      laTemp.hash = '';
    }
    var eventParams = [];
    var $eventParams = $("#table_eventParams_info tbody tr:not('.no-records-found')", $customRightDialig);
    $.each($eventParams, function (i, eventParam) {
      var $eventParam = $(eventParam);
      var text = $eventParam.find("td[data-field='text'] a").text();
      var name = $eventParam.find("td[data-field='name'] a").text();
      var value = $eventParam.find("td[data-field='value'] a").text();
      eventParams.push({
        text: text == 'Empty' ? '' : text,
        name: name == 'Empty' ? '' : name,
        value: value == 'Empty' ? '' : value
      });
    });
    laTemp.eventParams = JSON.stringify(eventParams);
    laTemp.targetPosition = $("input[name='btnPosition']:checked", goForm).val();
    laTemp.btnClassName = goForm.btnClassName.value;
    laTemp.btnStyle = goForm.btnStyle.value;
    laTemp.sortOrder = goForm.sortOrder.value;
    laTemp.btnRemark = goForm.btnRemark2.value;
    laTemp.btnRole = goForm.btnRole2.value;
    return laTemp;
  } else {
    var laTemp = {};
    laTemp.unid = '';
    laTemp.btnSource = btnSource;
    laTemp.btnRemark = goForm.btnRemark1.value;
    laTemp.btnRole = goForm.btnRole.value;
    laTemp.btnValue = goForm.DButton.value;
    laTemp.btnName = goForm.DButtonName.value;
    laTemp.newName = goForm.newName.value;
    laTemp.newCode = goForm.newCode.value;
    laTemp.newCodeName = goForm.newCodeName.value;
    laTemp.useWay = $('input[name=useWay]:checked', goForm).val();
    laTemp.btnOwners = goForm.btnOwners.value;
    laTemp.btnOwnerIds = goForm.btnOwnerIds.value;

    if (laTemp.btnValue == 'B004002' || laTemp.btnValue == 'B004003' || laTemp.btnValue == 'B004020' || laTemp.btnValue == 'B004016') {
      laTemp.btnArgument = goForm.DTask.value;
    }
    if (laTemp.btnValue == 'B004002') {
      var userIds =
        $("input[name='Duser1']", goForm).val() +
        ',' +
        $("input[name='user1']", goForm).val() +
        ',' +
        $("input[name='user2']", goForm).val() +
        ',' +
        $("input[name='user4']", goForm).val() +
        ',' +
        $("input[name='user8']", goForm).val();
      laTemp.btnUserIds = userIds;
      var copyUserIds =
        $("input[name='DcopyUser1']", goForm).val() +
        ',' +
        $("input[name='copyUser1']", goForm).val() +
        ',' +
        $("input[name='copyUser2']", goForm).val() +
        ',' +
        $("input[name='copyUser4']", goForm).val() +
        ',' +
        $("input[name='copyUser8']", goForm).val();
      laTemp.btnCopyUserIds = copyUserIds;
    }
    return laTemp;
  }
}

// 权限设置 -- 待办意见立场
function setOpinions(task, field, reset) {
  var opinionList = task.selectSingleNode('optNames') ? task.selectSingleNode('optNames').find('unit') : [];
  var opinions = [];
  // 重置为默认意见立场
  if (reset === true) {
    opinions.push({
      name: '同意',
      value: '1'
    });
    opinions.push({
      name: '不同意',
      value: '0'
    });
  } else if (opinionList && opinionList.length > 0) {
    for (var i = 0; i < opinionList.length; i++) {
      var name = $(opinionList[i]).find('argValue').text();
      var value = $(opinionList[i]).find('value').text();
      opinions.push({
        name: name,
        value: value
      });
    }
  }
  var lis = '';
  for (var i = 0; i < opinions.length; i++) {
    var name = opinions[i].name;
    var value = opinions[i].value;
    lis +=
      '<li class="work-flow-other-item">' +
      '<div data-value="' +
      value +
      '" data-name="' +
      name +
      '">' +
      name +
      '(' +
      value +
      ')' +
      '</div>' +
      '<button class="setOpinion" type="button"><i class="iconfont icon-ptkj-shezhi" title="设置"></i></button>' +
      '<button class="delOpinion" type="button"><i class="iconfont icon-ptkj-shanchu" title="删除"></i></button>' +
      '</li>';
  }
  $('#' + field).html(lis);

  // 意见立场必填
  var requiredOpinionPosition = task.selectSingleNode('requiredOpinionPosition');
  if (requiredOpinionPosition && requiredOpinionPosition.text()) {
    if (requiredOpinionPosition.text() == '1') {
      $('#requiredOpinionPosition').attr('checked', 'checked');
    }
  }
  // 显示用户意见立场值
  var showUserOpinionPosition = task.selectSingleNode('showUserOpinionPosition');
  if (showUserOpinionPosition && showUserOpinionPosition.text()) {
    if (showUserOpinionPosition.text() == '1') {
      $('#showUserOpinionPosition').attr('checked', 'checked');
    }
  }
  // 显示意见立场统计
  var showOpinionPositionStatistics = task.selectSingleNode('showOpinionPositionStatistics');
  if (showOpinionPositionStatistics && showOpinionPositionStatistics.text()) {
    if (showOpinionPositionStatistics.text() == '1') {
      $('#showOpinionPositionStatistics').attr('checked', 'checked');
    }
  }
}

function formTable(goForm, taskProperty) {
  var form = $.extend(true, {}, goWorkFlow.DformDefinition);
  if (!form) return;
  var hasSave = taskProperty.selectSingleNode('allFormField') ? true : false;
  var hideBlocks = taskProperty.selectSingleNode('hideBlocks') ? taskProperty.selectSingleNode('hideBlocks').find('unit') : '';
  var hideTabs = taskProperty.selectSingleNode('hideTabs') ? taskProperty.selectSingleNode('hideTabs').find('unit') : '';

  initFormLayout();
  getSubTabs(hideTabs, form, hasSave);
  getBlocks(hideBlocks, form, hasSave);
  getFields(taskProperty, 'formFieldTable', $('#formSetting'));

  var rights =
    taskProperty.selectSingleNode('rights') && taskProperty.selectSingleNode('rights').find('unit')
      ? taskProperty.selectSingleNode('rights').find('unit')
      : [];
  var editFields =
    taskProperty.selectSingleNode('editFields') && taskProperty.selectSingleNode('editFields').find('unit')
      ? taskProperty.selectSingleNode('editFields').find('unit')
      : [];
  var showForm = '0';
  if (isFirstTask(taskProperty)) {
    showForm = '1';
  } else {
    if (rights.length > 0) {
      for (var i = 0; i < rights.length; i++) {
        var right = $(rights[i]).text();
        if (right == 'B004025') {
          showForm = '1';
          break;
        }
      }
    }
    if (editFields.length > 0) {
      showForm = '1';
    }
  }

  var taskEditForm = taskProperty.selectSingleNode('canEditForm');
  var canEditForm = taskEditForm && taskEditForm.text() != '' ? taskEditForm.text() : showForm;
  $('#canEditForm').val(canEditForm);
  if (canEditForm == '1') {
    $('#editForm').addClass('active').find('.switch-radio').data('value', '1');
    $('#formFieldTable tr th:eq(1)').width(75);
    $('#formFieldTable tr').find('.fieldNameWidth').width(110);
    $('#formFieldTable tr').find('.fieldNameWidth.subFieldNameWidth').width(82);

    $('#formFieldTable').css({
      'table-layout': 'fixed'
    });
  } else {
    $('#formFieldTable tr').find('th:eq(2),td:eq(2)').hide();
    $('#formFieldTable tr').find('.fieldNameWidth').width(100);
    $('#formFieldTable tr').find('.fieldNameWidth.subFieldNameWidth').width(82);
    $('#formFieldTable tr th:eq(1)').width(100);
    $('#formFieldTable tr th:eq(3)').width(100);
    $('#formFieldTable')
      .css({
        'table-layout': 'initial'
      })
      .parents('.fixed-table-body')
      .css({
        'overflow-x': 'hidden'
      });
  }
  $('#editForm').find('.switch-radio').data('value', canEditForm);

  setTimeout(function () {
    subformAllChecked();
    allFieldChecked(['show_formFieldTable', 'edit_formFieldTable', 'fill_formFieldTable', 'showFormLayout']);
    changeSubformStatus();
  }, 1000);

  var laNode = goWorkFlow.dictionXML.selectNodes('/diction/forms/form');
  if (laNode != null && laNode.length != 0) {
    var lsValue = taskProperty.selectSingleNode('formID') != null ? taskProperty.selectSingleNode('formID').text() : null;
    var formUuid = bGetForm();
    // 环节展示表单
    if (StringUtils.isNotBlank(formUuid)) {
      $.get({
        url: ctx + '/api/workflow/definition/getVformsByPformUuid',
        service: 'flowSchemeService.getVformsByPformUuid',
        data: {
          pformUuid: formUuid
        },
        success: function (result) {
          var dataList = result.data;
          var formData = [];
          $.each(dataList, function (index, item) {
            formData.push({
              id: item.value,
              text: item.label
            });
          });
          $('#DForm', '#formSetting').wellSelect({
            data: formData
          });
          $('#DForm', '#formSetting').wellSelect('val', lsValue);
        }
      });
    }
  }
}

function subformAllChecked() {
  $.each($('.isSubform'), function (index, item) {
    var uuid = $(item).data('uuid');
    var tdIndex = $(item).parent().index();
    var inputs = $(item)
      .parents('table.no-table-borders')
      .find('tbody tr')
      .find('td:eq(' + tdIndex + ')')
      .find("input[data-subformid='" + uuid + "']");
    for (var i = 0; i < inputs.length; i++) {
      if (!$(inputs[i]).prop('checked')) {
        $(item).prop('checked', false);
        break;
      }
    }
  });
}

function allFieldChecked(obj) {
  $.each(obj, function (index, item) {
    $('#' + item)
      .parent()
      .removeAttr('title');
    var tableTr = $('#' + item)
      .parents('table.no-table-borders')
      .find('tbody tr');
    var tdIndex = $('#' + item)
      .parents('th')
      .index();
    var allChecked = true;
    $.each(tableTr, function (index1, item1) {
      var $input = $(item1)
        .find('td:eq(' + tdIndex + ')')
        .find("input[type='checkbox']");

      if ($input.prop('checked') == false) {
        allChecked = false;
      }
    });
    $('#' + item).prop('checked', allChecked);
  });
}

function changeSubformStatus() {
  var subform = $('#formFieldTable').find('tbody tr');
  $.each(subform, function (index, item) {
    var uuid = $(item).find('input').data('formuuid');
    if (uuid && !$(item).find('input').prop('checked')) {
      $('#formFieldTable tr')
        .find('input[data-subformid="' + uuid + '"]')
        .prop('checked', false)
        .attr('disabled', 'disabled');

      $('#formFieldTable tr')
        .find('input[data-uuid="' + uuid + '"]')
        .prop('checked', false)
        .attr('disabled', 'disabled');
      $('#formFieldTable tr')
        .find('input[data-subformid="' + uuid + '"]')
        .parents('tr')
        .find('.setFiles')
        .hide();
    }
  });
}

function setFieldsValue(fieldData) {
  var data = [];
  for (var i = 0; i < fieldData.length; i++) {
    var field = $(fieldData[i]).text();
    if (field.indexOf(':') > -1) {
      var field1 = field.split(':')[0];
      var field2 = field.split(':')[1];
      if (field2.indexOf(field1) > -1) {
        continue;
      } else {
        data.push(field);
      }
    } else {
      data.push(field);
    }
  }
  return data;
}

// 表单设置-- 获取表单字段
function getFields(taskProperty, field, container) {
  var hasSave = taskProperty.selectSingleNode('allFormField') ? true : false;
  var editUnit = taskProperty.selectSingleNode('editFields') ? taskProperty.selectSingleNode('editFields').find('unit') : [];
  var readUnit = taskProperty.selectSingleNode('readFields') ? taskProperty.selectSingleNode('readFields').find('unit') : [];
  var hideUnit = taskProperty.selectSingleNode('hideFields') ? taskProperty.selectSingleNode('hideFields').find('unit') : [];
  var notNullUnit = taskProperty.selectSingleNode('notNullFields') ? taskProperty.selectSingleNode('notNullFields').find('unit') : [];
  var allFormField = taskProperty.selectSingleNode('allFormField') ? taskProperty.selectSingleNode('allFormField').find('unit') : [];
  var allFormBtns = taskProperty.selectSingleNode('allFormFieldBtns') ? taskProperty.selectSingleNode('allFormFieldBtns').find('unit') : [];
  var fileRights = taskProperty.selectSingleNode('fileRights') ? taskProperty.selectSingleNode('fileRights').find('unit') : [];
  var fieldRightObj = getFieldRightObj(editUnit);
  var fileRightsObj = getFileRightObj(fileRights); // 获取附件权限
  var allFormFieldWidgetIds = taskProperty.selectSingleNode('allFormFieldWidgetIds') ? taskProperty.selectSingleNode('allFormFieldWidgetIds').find('unit') : [];
  var formBtnRightSettings = taskProperty.selectSingleNode('formBtnRightSettings') ? taskProperty.selectSingleNode('formBtnRightSettings').find('unit') : []; // 表单配置右侧按钮设置
  var formBtnRightSettingsObj = getformBtnRightSettings(formBtnRightSettings);
  var allFormFieldObj = getAllFormFieldObj(allFormField); // 获取表单所有字段，方便对比是否有新增字段
  var editField = setFieldsValue(editUnit); // 获取字段值-编辑
  var readField = setFieldsValue(readUnit); // 获取字段值-只读
  var hideField = setFieldsValue(hideUnit); // 获取字段值-隐藏
  var notNullField = setFieldsValue(notNullUnit); // 获取字段值-非空
  var getAllFormBtn = getAllFormBtns(allFormBtns); // 获取所有从表和附件的按钮
  console.log(getAllFormBtn);
  var fileBtns = getFileBtns(); // 获取列表附件按钮
  var iconBtns = getIconBtns(); // 获取图标附件按钮
  var imgBtns = getImgBtns(); // 获取图片附件按钮
  var form = $.extend(true, {}, goWorkFlow.DformDefinition);
  if (!form) return;
  var newData = [];
  goWorkFlow.formFieldDatas = [];
  if (form.formTree) {
    var formDatas = getFormFieldObj(form); // 获取表单所有字段、视图列表、目录维护组件，并按顺序排列
  } else {
    var formDatas = getFormFieldNoOrder(form); // 获取表单所有字段、视图列表、目录维护组件，不排序
  }
  formDatas = getSubformDefinition(formDatas);

  console.log(formDatas);

  $.each(formDatas, function (index, item) {
    // 填充字段和从表相应的权限（编辑/显示/非空）
    if (item.formUuid) {
      var subform = item;

      if (subform.tableButtonInfo && subform.tableButtonInfo.length > 0) {
        var subField = fieldRightObj[subform.formUuid];
        for (var d = 0; d < subform.tableButtonInfo.length; d++) {
          if (!subField && hasSave) {
            // 按钮全不选的情况
            subform.tableButtonInfo[d].isChecked = false;
          } else if (subField) {
            subform.tableButtonInfo[d].isChecked = false;
            for (var s = 0; s < subField.length; s++) {
              var btnCode = subform.tableButtonInfo[d].code;
              if (
                ((btnCode == 'btn_imp_subform' || btnCode == 'btn_exp_subform') && btnCode.indexOf(subField[s]) > -1) ||
                btnCode == subField[s]
              ) {
                subform.tableButtonInfo[d].isChecked = true;
                continue;
              }
            }
          } else if (!hasSave) {
            subform.tableButtonInfo[d].isChecked = subform.tableButtonInfo[d].position.length > 0 ? true : false;
          } else {
            subform.tableButtonInfo[d].isChecked = true;
          }
          if (getAllFormBtn[subform.name]) {
            var tableCode = getAllFormBtn[subform.name].split(';');
            if (tableCode.indexOf(subform.tableButtonInfo[d].code) < 0) {
              subform.tableButtonInfo[d].isChecked = subform.tableButtonInfo[d].position.length > 0 ? true : false;
            }
          }
        }
      } else if (subform.configuration) {
        var subField = fieldRightObj[subform.formUuid];
        var subConfiguration = subform.configuration;
        subform.tableButtonInfo = getBtnToButtonInfo(subConfiguration, formBtnRightSettingsObj[subform.formUuid]);
      }

      var newField = getFieldTableData(subform, editField, readField, hideField, notNullField, hasSave);
      if (allFormFieldObj.length > 0 && allFormFieldObj.indexOf(newField.subform) == -1 && newField.showType == '5') {
        newField.isShowField = false;
      }
      newData.push(newField);
      newData.push({
        isSubform: true,
        name: '全选从表下的字段',
        subformUuid: subform.formUuid
      });
      if (subform.fields) {
        for (var k in subform.fields) {
          var subField = subform.fields[k];
          subField.subWidgetId = subform.id;
          subField.subFormId = subform.formUuid;
          var newField1 = subform.formUuid + ':' + subField.name;

          if (subField.configuration) {
            if (subField.inputMode == '6' || subField.inputMode == '4' || subField.inputMode == '33') {
              subField.buttons = getBtnToButtonInfo(subField.configuration, formBtnRightSettingsObj[subField.subFormId + '_' + subField.name]);
            }
          } else {
            if (subField.inputMode == '6') {
              // 列表附件按钮
              subField.buttons = fileFieldBtns(subField, fileBtns, fileRightsObj, getAllFormBtn);
            } else if (subField.inputMode == '4') {
              subField.buttons = fileFieldBtns(subField, iconBtns, fileRightsObj, getAllFormBtn);
            } else if (subField.inputMode == '33') {
              subField.buttons = fileFieldBtns(subField, imgBtns, fileRightsObj, getAllFormBtn);
            }
          }
          var newSubField = getFieldTableData(subField, editField, readField, hideField, notNullField, hasSave);
          if (allFormFieldObj.length > 0 && allFormFieldObj.indexOf(newField1) == -1 && !subField.hasOwnProperty("isShowField")) {
            if (subField.hidden != '1') {
              newSubField.isShowField = false;
              newSubField.isEditField = false;
            } else {
              newSubField.isShowField = true;
              if (subField.editable == '1') {
                newSubField.isEditField = true;
              } else {
                newSubField.isEditField = false;
              }
            }
          }

          newData.push(newSubField);
        }
      }
    } else {
      var mainfield = item;
      if (mainfield.configuration) {
        if (mainfield.inputMode == '6' || mainfield.inputMode == '4' || mainfield.inputMode == '33') {
          mainfield.buttons = getBtnToButtonInfo(mainfield.configuration, formBtnRightSettingsObj[mainfield.name]);
        }
      } else {
        if (mainfield.inputMode == '6') {
          // 列表附件按钮
          mainfield.buttons = fileFieldBtns(mainfield, fileBtns, fileRightsObj, getAllFormBtn);
        } else if (mainfield.inputMode == '4') {
          mainfield.buttons = fileFieldBtns(mainfield, iconBtns, fileRightsObj, getAllFormBtn);
        } else if (mainfield.inputMode == '33') {
          mainfield.buttons = fileFieldBtns(mainfield, imgBtns, fileRightsObj, getAllFormBtn);
        }
      }
      var newField = getFieldTableData(mainfield, editField, readField, hideField, notNullField, hasSave);
      if (allFormFieldObj.length > 0 && allFormFieldObj.indexOf(mainfield.name) == -1) {
        if (mainfield.showType == '5') {
          newField.isShowField = false;
          newField.isEditField = false;
        } else {
          if (mainfield.showType == '1') {
            newField.isEditField = true;
          } else {
            newField.isEditField = false;
          }
          newField.isShowField = true;
        }
      }
      newData.push(newField);
    }
  });
  goWorkFlow.formFieldDatas = newData;
  initFormFieldTable(taskProperty, field, container, newData);
}

function getformBtnRightSettings(setting) {
  // 附件和从表的按钮
  var obj = {};
  $.each(setting, function (index, item) {
    var text = $(item).text();
    var btns = text.split('=');
    obj[btns[0]] = btns[1];
  });
  return obj;
}

// 7.0生成按钮相关数据
function getBtnToButtonInfo(configuration, flowBtns) {
  var buttonInfo = {
    edit: { headerButton: [], rowButton: [] },
    show: { headerButton: [], rowButton: [] }
  };
  if (configuration.inputMode) {
    // 附件
    _.each(['headerButton', 'rowButton'], function (item) {
      var group = _.groupBy(configuration[item], 'btnShowType');
      buttonInfo.edit[item] = _.map(group.edit, function (btn) {
        btn.isChecked = btn.defaultFlag;
        return btn;
      });
      buttonInfo.show[item] = _.map(group.show, function (btn) {
        btn.isChecked = btn.defaultFlag;
        return btn;
      });
    })
  } else {
    _.each(['headerButton', 'rowButton'], function (item) {
      var btns = _.map(configuration[item].buttons, function (btn) {
        btn.isChecked = btn.defaultVisible;
        return btn;
      });
      _.each(['edit', 'show'], function (type) {
        buttonInfo[type][item] = JSON.parse(JSON.stringify(btns));
      })
    })
  }
  if (flowBtns) {
    var tableBtns = JSON.parse(flowBtns);
    _.each(['edit', 'show'], function (type) {
      _.each(tableBtns[type], function (btns, item) {
        let checkBtns = btns ? btns.split(";") : [];
        _.each(buttonInfo[type][item], function (btn) {
          if (checkBtns.indexOf(btn.code || btn.id) > -1) {
            btn.isChecked = true;
          } else {
            btn.isChecked = false;
          }
        })
      })
    })
  }
  return buttonInfo;
}

function getAllFormBtns(allFormBtns) {
  // 附件和从表的按钮
  var obj = {};
  $.each(allFormBtns, function (index, item) {
    var text = $(item).text();
    var btns = text.split(':');
    if (btns.length == 2) {
      obj[btns[0]] = btns[1];
    } else if (btns.length == 3) {
      obj[btns[0] + ':' + btns[1]] = btns[2];
    }
  });
  return obj;
}

function getFormFieldNoOrder(form) {
  var fields = [];
  if (form.fields && Object.keys(form.fields).length > 0) {
    for (var i in form.fields) {
      fields.push(form.fields[i]);
    }
  }
  if (form.subforms && Object.keys(form.subforms).length > 0) {
    for (var i in form.subforms) {
      fields.push(form.subforms[i]);
      if (form.subforms[i].fields) {
      }
    }
  }
  if (form.tableView && Object.keys(form.tableView).length > 0) {
    for (var i in form.tableView) {
      fields.push(form.tableView[i]);
    }
  }
  if (form.fileLibrary && Object.keys(form.fileLibrary).length > 0) {
    for (var i in form.fileLibrary) {
      fields.push(form.fileLibrary[i]);
    }
  }
  return fields;
}

function getFormFieldObj(form) {
  var fieldMap = {};
  var fields = [];
  var formTree = form.formTree;

  function getFormFilds(tree, name, templateId) {
    $.each(tree, function (index, item) {
      if (item.nodeType == 'field' && item.inputMode != '130') {
        if (templateId) {
          fields.push(templateId + item.fieldName);
        } else {
          // bug#59827
          if (fieldMap[item.fieldName] == null) {
            fields.push(item.fieldName);
          }
          fieldMap[item.fieldName] = item.fieldName;
        }
      }
      if (item.inputMode == '130') {
        fields.push('placeholder:' + item.fieldName);
      }
      if ((item.nodeType == 'block' || item.nodeType == 'tab' || item.nodeType == 'subTab') && item.children.length > 0) {
        getFormFilds(item.children, name, templateId);
      }
      if (item.nodeType == 'template') {
        getFormFilds(item.children, 'template', 'templateId;' + item.templateUuid + ':');
      }
      if (item.nodeType == 'fileLibrary' && !name) {
        fields.push('fileLibraryId:' + item.fileLibraryId);
      }
      if (item.nodeType == 'tableView' && !name) {
        fields.push('tableViewId:' + item.tableViewId);
      }
      if (item.nodeType == 'subform' && !name) {
        fields.push('subformUuid:' + item.subformUuid);
      }
    });
  }

  getFormFilds(formTree);

  var lastField = [];
  $.each(fields, function (index, item) {
    if (item.indexOf('fileLibraryId:') > -1) {
      var fileLibrary = item.split('fileLibraryId:')[1];
      lastField.push(form.fileLibrary[fileLibrary]);
    } else if (item.indexOf('tableViewId:') > -1) {
      var tableView = item.split('tableViewId:')[1];
      lastField.push(form.tableView[tableView]);
    } else if (item.indexOf('subformUuid:') > -1) {
      var subform = item.split('subformUuid:')[1];
      lastField.push(form.subforms[subform]);
    } else if (item.indexOf('placeholder:') > -1) {
      var placeholder = item.split('placeholder:')[1];
      lastField.push(form.placeholderCtr[placeholder]);
    } else if (item.indexOf('templateId;') > -1) {
      var templateId = item.split(':')[1];
      var template = item.split(':')[0];
      var field = form.fields[templateId];
      field.templateUuid = template.split(';')[1];
      lastField.push(field);
    } else if (form.fields[item] != null) {
      lastField.push(form.fields[item]);
    }
  });

  return lastField;
}

function getIconBtns() {
  return {
    show: [
      {
        code: '6',
        name: '批量下载'
      },
      {
        code: '15',
        name: '另存为'
      },
      {
        code: '7',
        name: '预览'
      },
      {
        code: '2',
        name: '下载'
      },
      {
        code: '13',
        name: '复制名称'
      },
      {
        code: '12',
        name: '历史记录'
      },
      {
        code: '22',
        name: '查阅'
      },
      {
        code: '24',
        name: '查阅正文'
      }
    ],
    edit: [
      {
        code: '1',
        name: '添加附件'
      },
      {
        code: '4',
        name: '粘贴附件'
      },
      {
        code: '14',
        name: '批量删除'
      },
      {
        code: '8',
        name: '编辑'
      },
      {
        code: '21',
        name: '替换'
      },
      {
        code: '9',
        name: '重命名'
      },
      {
        code: '3',
        name: '删除'
      },
      {
        code: '10',
        name: '上移'
      },
      {
        code: '11',
        name: '下移'
      },
      {
        code: '23',
        name: '盖章'
      }
    ]
  };
}

function getImgBtns() {
  return {
    edit: [
      {
        name: '添加',
        code: 'allowUpload'
      },
      {
        name: '删除',
        code: 'allowDelete'
      }
    ],
    show: [
      {
        name: '下载',
        code: 'allowDownload'
      },
      {
        name: '预览',
        code: 'allowPreview'
      }
    ]
  };
}

function fileFieldBtns(field, btns, btnsObj, oldBtns) {
  var newBtnObj = $.extend(true, {}, btns);
  if (field.inputMode == '4' && (field.keepOpLog == '1' || (field.refObj && field.refObj.keepOpLog == '1'))) {
    newBtnObj.show.splice(0, 1);
    newBtnObj.edit.splice(2, 0, {
      code: '5',
      name: '导入正文'
    });
    newBtnObj.edit[0].name = '新建正文';
  }
  var fieldname = field.subFormId ? field.subFormId + ':' + field.name : field.name;
  for (var i in newBtnObj) {
    $.each(newBtnObj[i], function (index, item) {
      item.isChecked = false;
      if (Object.keys(btnsObj).length > 0) {
        var val = field.inputMode == '6' ? item.uuid : item.code;
        if (btnsObj[fieldname] && btnsObj[fieldname].split(';').indexOf(val) > -1) {
          item.isChecked = true;
        }
      } else if (field.inputMode == '6') {
        if (!field.secDevBtnIdStr) {
          field.secDevBtnIdStr = [];
          field.allowDelete && field.secDevBtnIdStr.push('09691fb3-fdd3-4790-863d-6b77d4aa4bbd');
          field.allowDownload && field.secDevBtnIdStr.push('4ee25050-f1e5-49d2-a635-9c19ef1785dc');
          field.allowUpload && field.secDevBtnIdStr.push('00b13afb-8afc-4a9e-b1e2-28f321f48924');
          field.secDevBtnIdStr.push('5f82f10a-9450-4a18-8c8d-e38e3767b466');
        }
        var secDevBtnIdStr = typeof field.secDevBtnIdStr === 'string' ? field.secDevBtnIdStr.split(';') : field.secDevBtnIdStr;
        if (secDevBtnIdStr.indexOf(item.uuid) > -1) {
          item.isChecked = true;
        }
      } else if (field.inputMode == '4') {
        if (!field.operateBtns) {
          field.operateBtns = [];
          field.allowDelete && field.operateBtns.push('3', '14');
          if (field.allowDownload) {
            field.operateBtns.push('2');
            field.keepOpLog != '1' && field.operateBtns.push('6');
          }
          if (field.allowUpload) {
            field.operateBtns.push('1', '4');
            field.keepOpLog == '1' && field.operateBtns.push('5');
          }
          field.operateBtns.push('7');
        }
        if (field.operateBtns.indexOf(item.code) > -1) {
          item.isChecked = true;
        }
      } else if (field.inputMode == '33') {
        if (!field.allowPreview) {
          field.allowPreview = true;
        }
        if (field[item.code]) {
          item.isChecked = true;
        }
      }
      var oldFieldBtn = oldBtns[fieldname];
      if (oldFieldBtn) {
        // 新增的按钮根据表单设置的值
        var btns = oldFieldBtn.split(';');
        if (field.inputMode == '6') {
          var secDevBtnIdStr = typeof field.secDevBtnIdStr === 'string' ? field.secDevBtnIdStr.split(';') : field.secDevBtnIdStr;
          if (btns.indexOf(item.uuid) < 0 && secDevBtnIdStr.indexOf(item.uuid) > -1) {
            item.isChecked = true;
          }
        } else if (field.inputMode == '4') {
          if (btns.indexOf(item.code) < 0 && field.operateBtns.indexOf(item.code) > -1) {
            item.isChecked = true;
          }
        } else {
          if (btns.indexOf(item.code) < 0 && field[item.code]) {
            item.isChecked = true;
          }
        }
      }
    });
  }

  return newBtnObj;
}

function getFileBtns() {
  var rigths = {
    show: [],
    edit: []
  };
  JDS.call({
    service: 'dyformFileListButtonConfigService.getAllBean',
    version: '',
    async: false,
    success: function (result) {
      if (result.data && result.data.length > 0) {
        $.each(result.data, function (i, item) {
          rigths[item.btnShowType].push(item);
        });
      }
    }
  });
  return rigths;
}

function getSubformDefinition(form) {
  $.each(form, function (i, item) {
    if (item.formUuid) {
      $.ajax({
        url: '/pt/dyform/definition/getFormDefinition',
        async: false, // 同步完成
        type: 'POST',
        data: {
          formUuid: item.formUuid,
          justDataAndDef: false
        },
        dataType: 'json',
        success: function (data) {
          $.each(item.fields, function (index, field) {
            $.extend(true, field, data.fields[index]);
          });
        },
        error: function (data) {
          // 加载定义失败
        }
      });
    }
  });
  return form;
}

// 表单设置-- 表单字段表格
function initFormFieldTable(task, field, container, data) {
  $('#' + field, container).bootstrapTable({
    data: data,
    striped: false,
    idField: 'uuid',
    uniqueId: 'uuid',
    width: 500,
    parentIdField: 'isParent',
    undefinedText: 'empty',
    columns: [
      {
        field: 'id',
        title: 'id',
        visible: false
      },
      {
        field: 'name',
        title: '字段名',
        formatter: function (value, row, index) {
          var style = row.isShowField ? '' : "display:none;";
          var isSubFieldStyle = row.subFormId ? "margin-left:18px;" : '';
          if (row.fields) {
            return (
              "<i style='color:#F1B755; margin-right: 3px;vertical-align:middle;' class='iconfont icon-ptkj-wenjianjia-kai'></i><span title='" +
              row.displayName +
              "' class='fieldNameWidth' data-title='" +
              row.displayName +
              "' style='line-height:1;'>" +
              row.displayName +
              "</span><div data-index='" +
              index +
              "' " +
              "style='" + style +
              "margin-top:-11px;' class='setSubForm iconfont icon-ptkj-shezhi'></div>"
            );
          } else if (row.inputMode == '6' || row.inputMode == '4' || row.inputMode == '33') {
            var left = row.hidden ? 'margin-left:18px;' : '';
            var display = row.isShowField ? '' : 'display:none;';
            return (
              "<i style='color:#7994BB;margin-right: 3px;" +
              left + isSubFieldStyle +
              "' class='iconfont icon-ptkj-wenjian'></i><span title='" +
              row.displayName +
              "' class='fieldNameWidth'  data-title='" +
              row.displayName +
              "'>" +
              row.displayName +
              "</span><div data-index='" +
              index +
              "' " +
              "style='" + style +
              "margin-top:-11px;' class='setFiles iconfont icon-ptkj-shezhi'></div>"
            );
          } else if (row.hidden) {
            return (
              "<i style='color:#7994BB;margin-right: 3px;margin-left:18px;' class='iconfont icon-ptkj-wenjian'></i><span title='" +
              row.displayName +
              "' class='fieldNameWidth subFieldNameWidth'  data-title='" +
              row.displayName +
              "' style='width: calc(100% - 34px);'>" +
              row.displayName +
              '</span>'
            );
          } else if (row.isSubform) {
            return (
              "<span title='" +
              row.name +
              "' class='fieldNameWidth'  data-title='" +
              row.name +
              "' style='margin-left:18px;color:#FF9900;'>" +
              row.name +
              '</span>'
            );
          } else {
            var name = row.relaFileLibraryId ? row.relaFileLibraryText : row.relaTableViewId ? row.relaTableViewText : row.displayName;
            return (
              "<i style='color:#7994BB;margin-right: 3px;" + isSubFieldStyle + "' class='iconfont icon-ptkj-wenjian'></i><span title='" +
              name +
              "' class='fieldNameWidth'  data-title='" +
              name +
              "'>" +
              name +
              '</span>'
            );
          }
        }
      },
      {
        field: 'showField',
        title: "<input id='show_" + field + "' type='checkbox' checked><label for='show_" + field + "'>显示</label>",
        width: 100,
        formatter: function (value, row, index) {
          if (row.isSubform) {
            return (
              "<input class='isSubform' checked type='checkbox' id='showField" +
              index +
              "'  data-uuid='" +
              row.subformUuid +
              "' " +
              isShow +
              "/> <label for='showField" +
              index +
              "'></label>"
            );
          } else {
            var isShow = row.isShowField ? 'checked' : '';
            var name = row.name;
            if (row.inputMode == '130') {
              name = 'placeholderCtr:' + name;
            } else if (row.relaFileLibraryId) {
              if (name == '') {
                name = 'fileLibrary:' + row.relaFileLibraryId;
              } else {
                name = 'fileLibrary:' + name;
              }
              isShow += ' disabled="disabled"';
            } else if (row.relaTableViewId) {
              if (name == '') {
                name = 'tableView:' + row.relaTableViewId;
              } else {
                name = 'tableView:' + name;
              }
              isShow += ' disabled="disabled"';
            }
            /*row.relaFileLibraryId
              ? 'fileLibrary:' + row.relaFileLibraryId
              : row.relaTableViewId
              ? 'tableView:' + row.relaTableViewId
              : row.templateUuid
              ? row.templateUuid + ':' + row.name
              : row.name;*/
            return (
              "<input type='checkbox' id='showField" +
              index +
              "' data-id='" +
              name +
              "' data-subformid='" +
              (row.subFormId == null ? '' : row.subFormId) +
              "' data-formuuid='" +
              (row.fields == null ? '' : row.formUuid) +
              "'  " +
              isShow +
              "/> <label for='showField" +
              index +
              "'></label>"
            );
          }
        }
      },
      {
        field: 'editField',
        title: "<input id='edit_" + field + "' type='checkbox'><label for='edit_" + field + "'>编辑</label>",
        width: 80,
        formatter: function (value, row, index) {
          var isEditForm = task.selectSingleNode('canEditForm') ? task.selectSingleNode('canEditForm').text() : '';
          var editFields = task.selectSingleNode('editFields/unit') ? task.selectSingleNode('editFields/unit') : [];
          var readFields = task.selectSingleNode('readFields/unit') ? task.selectSingleNode('readFields/unit') : [];
          var isEdit = '';
          var disabled = row.isShowField ? '' : "disabled='disabled'";
          if (row.isShowField && isEditForm == '' && editFields.length == 0 && readFields.length == 0) {
            if (row.editable) {
              if (row.editable == '1') {
                isEdit = 'checked';
              }
            } else if (row.formUuid) {
              isEdit = 'checked';
            } else if (row.showType && row.showType == '1') {
              isEdit = 'checked';
            }
          } else if (isEditForm == '1' && editFields.length == 0) {
            isEdit = '';
          } else if (row.isShowField && row.isEditField) {
            isEdit = 'checked';
          }
          var fieldStyle = noEditField.indexOf(row.inputMode) > -1 || row.formUuid || row.relaTableViewId || row.relaFileLibraryId;
          if (row.isSubform) {
            return (
              "<input class='isSubform' checked type='checkbox' id='editField" +
              index +
              "'  data-uuid='" +
              row.subformUuid +
              "' " +
              "/> <label for='editField" +
              index +
              "'></label>"
            );
          } else if (fieldStyle) {
            return '';
          } else {
            var rowName = row.templateUuid ? row.templateUuid + ':' + row.name : row.name;
            return (
              "<input type='checkbox' " +
              disabled +
              " id='editField" +
              index +
              "' data-id='" +
              rowName +
              "' data-subformid='" +
              (row.subFormId == null ? '' : row.subFormId) +
              "'  data-formuuid='" +
              (row.fields == null ? '' : row.formUuid) +
              "' " +
              isEdit +
              "/> <label for='editField" +
              index +
              "'></label>"
            );
          }
        }
      },
      {
        field: 'fillField',
        title: "<input id='fill_" + field + "' type='checkbox'><label for='fill_" + field + "'>必填</label>",
        width: 80,
        formatter: function (value, row, index) {
          var isFill = row.isFillField ? 'checked' : '';
          var fieldStyle = row.formUuid || row.relaTableViewId || row.relaFileLibraryId;
          if (row.isSubform) {
            return (
              "<input class='isSubform' checked type='checkbox' id='fillField" +
              index +
              "'  data-uuid='" +
              row.subformUuid +
              "' " +
              "/> <label for='fillField" +
              index +
              "'></label>"
            );
          } else if (fieldStyle) {
            return '';
          } else {
            var rowName = row.templateUuid ? row.templateUuid + ':' + row.name : row.name;
            return (
              "<input type='checkbox' id='fillField" +
              index +
              "' data-id='" +
              rowName +
              "' data-subformid='" +
              (row.subFormId == null ? '' : row.subFormId) +
              "'  data-formuuid='" +
              (row.fields == null ? '' : row.formUuid) +
              "' " +
              isFill +
              "/> <label for='fillField" +
              index +
              "'></label>"
            );
          }
        }
      }
    ]
  });

  $('#' + field)
    .parents('.bootstrap-table')
    .addClass('ui-wBootstrapTable');

  // 设置表单
  container.on('click', '.setSubForm', function () {
    var index = $(this).data('index');
    var row = goWorkFlow.formFieldDatas[index];
    showExtendSetDialog('从表扩展设置', row, index, $('#formFieldTable', '#formSetting'));
  });

  // 设置附件
  container.on('click', '.setFiles', function () {
    var index = $(this).data('index');
    var row = goWorkFlow.formFieldDatas[index];
    showExtendSetDialog('附件扩展设置', row, index, $('#formFieldTable', '#formSetting'));
  });
}

function getFieldRightObj(fieldRight) {
  var obj = {};
  $.each(fieldRight, function (index, item) {
    var text = $(item).text();
    if (text.indexOf(':') > -1) {
      var field1 = text.split(':')[0];
      var field2 = text.split(':')[1];
      if (field2.indexOf(field1) > -1) {
        if (!obj[field1]) {
          obj[field1] = [];
        }
        obj[field1].push(field2.split('_' + field1)[0]);
      }
    }
  });
  return obj;
}

function getFileRightObj(fileRights) {
  var obj = {};
  $.each(fileRights, function (index, item) {
    var text = $(item).text();

    var fileArr = text.split(':');
    if (fileArr.length == 2) {
      obj[fileArr[0]] = fileArr[1];
    } else if (fileArr.length == 3) {
      obj[fileArr[0] + ':' + fileArr[1]] = fileArr[2];
    }
  });

  return obj;
}

function getAllFormFieldObj(allFormField) {
  var allFormFieldObj = [];
  if (allFormField.length > 0) {
    $.each(allFormField, function (i, item) {
      allFormFieldObj.push($(item).text());
    });
  }
  return allFormFieldObj;
}

// 7.0表单字段配置转为6.2
function getFieldByConfiguration(field) {
  var configuration = field.configuration;
  if (field.colConfiguration) {
    // 从表
    configuration = field.colConfiguration;
    configuration.uneditableDisplayState = field.configuration.defaultDisplayState;
  }
  field.isRequired = configuration.required || false;
  if (configuration.defaultDisplayState == "edit") {
    field.showType = '1'; // 可编辑
  } else if (configuration.defaultDisplayState == "hidden") {
    field.showType = '5'; // 隐藏
  } else if (configuration.defaultDisplayState == "unedit") {
    // 不可编辑
    if (configuration.uneditableDisplayState == "label") {
      field.showType = '2'; // 显示文本
    } else {
      field.showType = '3'; // 只读
    }
  }
  return field;
}

function getFieldTableData(field, edit, read, hide, fill, hasSave) {
  if (field.configuration) {
    field = getFieldByConfiguration(field);
  }
  field.isShowField = field.hidden ? (field.hidden == '1' ? true : false) : field.showType != '5' ? true : false;
  field.isFillField = false;
  if (field.fieldCheckRules) {
    for (var i = 0; i < field.fieldCheckRules.length; i++) {
      if (field.fieldCheckRules[i].value == '1') {
        field.isFillField = true;
        break;
      }
    }
  } else if (field.isRequired) {
    field.isFillField = true;
  }
  field.isEditField = field.hidden ? (field.hidden == '1' ? true : false) : field.showType == '1' ? true : false;

  if (read.length > 0) {
    field.isEditField = true;
    for (var i = 0; i < read.length; i++) {
      if (field.hidden || field.templateUuid) {
        if (
          (read[i].indexOf(':') > -1 && read[i].split(':')[1] == field.name && read[i].split(':')[0] == field.subFormId) ||
          field.editable != '1'
        ) {
          field.isEditField = false;
          break;
        }
      } else {
        if (field.showType != '1' || read[i] == field.name) {
          field.isEditField = false;
          break;
        }
      }
    }
  }

  if (edit.length > 0) {
    field.isEditField = false;
    if (read.length == 0) {
      field.isEditField = false;
    }
    for (var i = 0; i < edit.length; i++) {
      if (field.hidden || field.templateUuid) {
        if (edit[i].indexOf(':') > -1 && edit[i].split(':')[1] == field.name && edit[i].split(':')[0] == field.subFormId) {
          field.isEditField = true;
          break;
        }
      } else if (edit[i].indexOf(':') > -1 && edit[i].split(':')[1] == field.name && edit[i].split(':')[0] == field.subFormId) {
        field.isEditField = true;
        break;
      } else if (edit[i] == field.name) {
        field.isEditField = true;
        break;
      }
    }
  }

  if (hide.length > 0) {
    field.isShowField = true;
    for (var i = 0; i < hide.length; i++) {
      if (field.hidden || field.templateUuid) {
        if (hide[i].indexOf(':') > -1 && hide[i].split(':')[1] == field.name && hide[i].split(':')[0] == field.subFormId) {
          field.isShowField = false;
          break;
        }
      } else if (hide[i].indexOf(':') > -1 && hide[i].split(':')[1] == field.name && hide[i].split(':')[0] == field.subFormId) {
        field.isShowField = false;
        break;
      } else if (hide[i] == field.name || hide[i] == field.relaTableViewId || hide[i] == field.relaFileLibraryId) {
        field.isShowField = false;
        break;
      }
    }
  } else if (hasSave) {
    field.isShowField = true;
    if (edit.length == 0) {
      field.isEditField = false;
    }
  }

  if (fill.length > 0) {
    for (var i = 0; i < fill.length; i++) {
      if (field.hidden || field.templateUuid) {
        field.isFillField = false;
        if (fill[i].indexOf(':') > -1 && fill[i].split(':')[1] == field.name && fill[i].split(':')[0] == field.subFormId) {
          field.isFillField = true;
          break;
        }
      } else {
        if (fill[i].indexOf(':') > -1 && fill[i].split(':')[1] == field.name && fill[i].split(':')[0] == field.subFormId) {
          field.isFillField = true;
          break;
        } else if (fill[i] == field.name) {
          field.isFillField = true;
          break;
        } else {
          field.isFillField = false;
        }
      }
    }
  } else if (hasSave) {
    field.isFillField = false;
  }
  return field;
}

function changeTableField(bol, field) {
  if (bol == '1') {
    field.find('tr').find('td:eq(2),th:eq(2)').show();
    field.find('tr th').width(70);
    field.find('tr th:eq(0)').width(100);
    field.find('tr').find('.fieldNameWidth').width(100);
    field.find('tr').find('.fieldNameWidth.subFieldNameWidth').width(82);
  } else {
    field.find('tr').find('td:eq(2),th:eq(2)').hide();
    field.find('tr th:not(2)').width(75);
    field.find('tr th:eq(0)').width(100);
    field.find('tr').find('.fieldNameWidth').width(100);
    field.find('tr').find('.fieldNameWidth.subFieldNameWidth').width(82);
  }
}

function searchFields(tableId, val, ele) {
  var tableTr = $('#' + tableId + ' tbody', ele).find('tr');
  for (var i = 0; i < tableTr.length; i++) {
    var name = $(tableTr[i]).find('td:eq(0)').find('span').data('title').toString();
    if (name.indexOf(val) > -1) {
      var newName = name.split(val);
      var text = newName.join("<span style='color:red;'>" + val + '</span>');
      $(tableTr[i]).find('td:eq(0)').find('span').html(text);
    } else {
      var text = $(tableTr[i]).find('td:eq(0)').find('span').data('title');
      $(tableTr[i]).find('td:eq(0)').find('span').html(text);
    }
  }
}

function clearFieldColor(tableId, ele) {
  var tableTr = $('#' + tableId + ' tbody', ele).find('tr');
  for (var i = 0; i < tableTr.length; i++) {
    var text = $(tableTr[i]).find('td:eq(0)').find('span').data('title');
    $(tableTr[i]).find('td:eq(0)').find('span').html(text);
  }
}

// 表单设置-- 获取表单布局-页签
function getSubTabs(tabs, form, hasSave) {
  if (form.hasOwnProperty("useDataModel")) {
    // 7.0属性
    var hideTabs = getAllFormFieldObj(tabs);
    var i = 0;
    _.each(form.tabs, (item, index) => {
      if (item.parentId) {
        var row = {
          layoutType: "tabs",
          name: item.title,
          id: index,
          isShow: true
        }
        if (hasSave && hideTabs.indexOf(index) > -1) {
          row.isShow = false;
        }
        $('#formLayoutTable').bootstrapTable('insertRow', {
          index: i,
          row: row
        });
        i++;
      }
    });
  } else {
    var url = ctx + '/api/workflow/definition/getFormSubtabs';
    getFormLayoutData(url, tabs, 'tabs');
  }
}

// 表单设置-- 获取表单布局-区块
function getBlocks(blocks, form, hasSave) {
  if (form.hasOwnProperty("useDataModel")) {
    // 7.0属性
    var hideBlocks = getAllFormFieldObj(blocks);
    var i = 0;
    _.each(form.blocks, (item, index) => {
      var row = {
        layoutType: "blocks",
        name: item.blockTitle,
        id: index,
        widgetId: item.id,
        isShow: !item.hide
      }
      if (hasSave) {
        if (hideBlocks.indexOf(index) > -1 || hideBlocks.indexOf(item.id) > -1) {
          row.isShow = false;
        } else {
          row.isShow = true;
        }
      }
      $('#formLayoutTable').bootstrapTable('insertRow', {
        index: i,
        row: row
      });
      i++;
    });
  } else {
    var url = ctx + '/api/workflow/definition/getFormBlocks';
    getFormLayoutData(url, blocks, 'blocks');
  }
}

function getFormLayoutData(url, blocks, type) {
  $.get({
    url: url,
    data: {
      formUuid: getFormID()
    },
    version: '',
    success: function (result) {
      var data = result.data;
      var newData = [];

      for (var i = 0; i < data.length; i++) {
        var items = data[i];
        if (type == 'blocks') {
          var blockList = goWorkFlow.DformDefinition.blocks;
          if (blockList[items.id].hide) {
            continue;
          }
        }
        items.isShow = true;
        if (items.children.length > 0) {
          $.each(items.children, function (index, item) {
            item.isShow = true;
            if (blocks.length > 0) {
              for (var k = 0; k < blocks.length; k++) {
                var block = $(blocks[k]).text();
                if (block == item.id) {
                  item.isShow = false;
                  continue;
                }
              }
            }
            newData.push(item);
          });
        }
        if (blocks.length > 0) {
          for (var j = 0; j < blocks.length; j++) {
            var block = $(blocks[j]).text();
            if (block == items.id) {
              items.isShow = false;
              continue;
            }
          }
        }
        newData.push(items);
      }

      $.each(newData, function (i, l) {
        l.layoutType = type;
        $('#formLayoutTable').bootstrapTable('insertRow', {
          index: i,
          row: l
        });
      });
    }
  });
}

// 表单设置-- 表单布局表格
function initFormLayout() {
  $('#formLayoutTable').bootstrapTable({
    data: [],
    striped: false,
    idField: 'uuid',
    uniqueId: 'uuid',
    width: 500,
    undefinedText: 'empty',
    columns: [
      {
        field: 'id',
        title: 'id',
        visible: false
      },
      {
        field: 'name',
        title: '布局名称',
        formatter: function (value, row, index) {
          if (row.layoutType == 'blocks') {
            return (
              "<i style='color:#7994BB; margin-right: 3px;' class='iconfont icon-ptkj-mokuaiqukuai'></i><span data-title='" +
              row.name +
              "'>" +
              row.name +
              '</span>'
            );
          } else {
            return (
              "<i style='color:#7994BB; margin-right: 3px;' class='iconfont icon-ptkj-xianxingyeqian'></i><span data-title='" +
              row.name +
              "'>" +
              row.name +
              '</span>'
            );
          }
        }
      },
      {
        field: 'showLayout',
        title: "<input id='showFormLayout' type='checkbox' checked><label for='showFormLayout'>显示</label>",
        width: 150,
        formatter: function (value, row, index) {
          if (row.isShow) {
            return (
              "<input type='checkbox' id='showLayout" +
              index +
              "' checked data-field='" +
              (row.widgetId || row.id) +
              "' data-type='" +
              row.layoutType +
              "'/> <label for='showLayout" +
              index +
              "'></label>"
            );
          } else {
            return (
              "<input type='checkbox' id='showLayout" +
              index +
              "' data-field='" +
              (row.widgetId || row.id) +
              "' data-type='" +
              row.layoutType +
              "'/> <label for='showLayout" +
              index +
              "'></label>"
            );
          }
        }
      }
    ]
  });

  $('#formLayoutTable').parents('.bootstrap-table').addClass('ui-wBootstrapTable');
}

// 流转设置
function parallelGateway(goForm, taskProperty) {
  setConditions(goForm, taskProperty, 'forkMode');
  setConditions(goForm, taskProperty, 'joinMode');

  loadBusinessType($('#businessType', goForm), goForm);

  // 跟进人员
  $('#DbranchTaskMonitors', goForm)
    .off()
    .on('click', function (e) {
      e.target.title = '选择跟进人员';
      bFlowActions(31, e, goForm);
    });
  // 显示位置
  setPlaceholder(['undertakeSituationPlaceHolder', 'infoDistributionPlaceHolder', 'operationRecordPlaceHolder'], goForm);
  if (taskProperty.selectSingleNode('parallelGateway')) {
    var undertakeSituationTitleExpression = taskProperty
      .selectSingleNode('parallelGateway')
      .selectSingleNode('forkMode')
      .selectSingleNode('undertakeSituationTitleExpression')
      ? taskProperty
        .selectSingleNode('parallelGateway')
        .selectSingleNode('forkMode')
        .selectSingleNode('undertakeSituationTitleExpression')
        .text()
      : '';
    var infoDistributionTitleExpression = taskProperty
      .selectSingleNode('parallelGateway')
      .selectSingleNode('forkMode')
      .selectSingleNode('infoDistributionTitleExpression')
      ? taskProperty
        .selectSingleNode('parallelGateway')
        .selectSingleNode('forkMode')
        .selectSingleNode('infoDistributionTitleExpression')
        .text()
      : '';
    var operationRecordTitleExpression = taskProperty
      .selectSingleNode('parallelGateway')
      .selectSingleNode('forkMode')
      .selectSingleNode('operationRecordTitleExpression')
      ? taskProperty
        .selectSingleNode('parallelGateway')
        .selectSingleNode('forkMode')
        .selectSingleNode('operationRecordTitleExpression')
        .text()
      : '';

    $('#undertakeSituationTitleExpression').text(undertakeSituationTitleExpression);
    $('#infoDistributionTitleExpression').text(infoDistributionTitleExpression);
    $('#operationRecordTitleExpression').text(operationRecordTitleExpression);
  }

  $('#setUundertakeSituationTitleExpression').on('click', function () {
    openTitleExpressionDialog(goForm, taskProperty, 'undertakeSituationTitleExpression', '列表标题设置');
  });
  $('#setInfoDistributionTitleExpression').on('click', function () {
    openTitleExpressionDialog(goForm, taskProperty, 'infoDistributionTitleExpression', '列表标题设置');
  });
  $('#setOperationRecordTitleExpression').on('click', function () {
    openTitleExpressionDialog(goForm, taskProperty, 'operationRecordTitleExpression', '列表标题设置');
  });
  if (taskProperty.selectSingleNode('parallelGateway')) {
    var units = taskProperty.selectSingleNode('parallelGateway').selectSingleNode('forkMode').selectSingleNode('branchTaskMonitors')
      ? taskProperty.selectSingleNode('parallelGateway').selectSingleNode('forkMode').selectSingleNode('branchTaskMonitors').find('unit')
      : [];
    var allData = getAllDatas(units);
    renderUnitField(allData, goForm, 'branchTaskMonitor');
  }

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
    }
  ];

  var buttons = [];

  if (taskProperty.selectSingleNode('parallelGateway')) {
    var btns = taskProperty.selectSingleNode('parallelGateway').selectSingleNode('forkMode').selectSingleNode('undertakeSituationButtons')
      ? taskProperty
        .selectSingleNode('parallelGateway')
        .selectSingleNode('forkMode')
        .selectSingleNode('undertakeSituationButtons')
        .find('button')
      : [];
    for (var i = 0; i < btns.length; i++) {
      var btnData = {};
      btnData.uuid = i;
      btnData.id = $(btns[i]).find('id').text();
      btnData.name = $(btns[i]).find('name').text();
      btnData.newName = $(btns[i]).find('newName').text();
      buttons.push(btnData);
    }
  }

  var $btnSettingTable = $('#btnSettingTable', $('#transSetting'));
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
    moveDownBtn: '#btn_row_down_set'
  });

  var columnBean = {
    name: '',
    typeName: '',
    index: '',
    type: '',
    sources: '',
    uuid: ''
  };
  formBuilder.bootstrapTable.initTableTopButtonToolbar('columnSettingTable', 'column', $('#transSetting'), columnBean);
  var $columnSettingTable = $('#columnSettingTable', $('#transSetting'));
  $columnSettingTable.bootstrapTable('destroy').bootstrapTable({
    data: [],
    idField: 'uuid',
    showColumns: false,
    striped: true,
    width: 500,
    clickToSelect: true,
    toolbar: $('#div_column_toolbar', $('#transSetting')),
    columns: [
      {
        field: 'checked',
        checkbox: true
      },
      {
        field: 'uuid',
        visible: false
      },
      {
        field: 'index',
        title: 'index',
        visible: false
      },
      {
        field: 'type',
        title: 'type',
        visible: false
      },
      {
        field: 'name',
        title: '列名'
      },
      {
        field: 'typeName',
        title: '类型'
      },
      {
        field: 'sources',
        title: 'sources',
        visible: false
      }
    ]
  });

  $('#btn_add_columns', goForm)
    .off()
    .on('click', function () {
      showColumnSetDialog(goForm, taskProperty);
    });
  $('#btn_edit_column', goForm)
    .off()
    .on('click', function () {
      var trs = $('#columnSettingTable tbody').find('tr.selected');
      if (trs.length == 0) {
        top.appModal.error('请选择编辑数据！');
      } else if (trs.length == 1) {
        var index = $(trs[0]).data('index');
        var data = $('#columnSettingTable').bootstrapTable('getData')[index];
        showColumnSetDialog(goForm, taskProperty, data, index);
      } else if (trs.length > 1) {
        top.appModal.error('一次只能编辑一条数据！');
      }
    });

  if (taskProperty.selectSingleNode('parallelGateway')) {
    var columns = taskProperty
      .selectSingleNode('parallelGateway')
      .selectSingleNode('forkMode')
      .selectSingleNode('undertakeSituationColumns')
      ? taskProperty
        .selectSingleNode('parallelGateway')
        .selectSingleNode('forkMode')
        .selectSingleNode('undertakeSituationColumns')
        .find('column')
      : [];

    $columnSettingTable.bootstrapTable('removeAll');

    for (var j = 0; j < columns.length; j++) {
      var columnData = {};
      columnData.uuid = j;
      columnData.type = $(columns[j]).find('type').text();
      columnData.typeName = $(columns[j]).find('typeName').text();
      columnData.index = $(columns[j]).find('index').text();
      columnData.sources = $(columns[j]).find('sources').text();
      columnData.name = $(columns[j]).find('name').text();

      $columnSettingTable.bootstrapTable('insertRow', {
        index: j,
        row: columnData
      });
    }
  }
}

// 流转设置--业务类别
function loadBusinessType($businessType, goForm) {
  $.get({
    url: ctx + '/api/workflow/definition/getBusinessTypes',
    // async: false,
    success: function (result) {
      var data = [];
      $.each(result.data, function (i, item) {
        data.push({
          id: item.value,
          text: item.label
        });
      });
      $businessType
        .wellSelect({
          data: data
        })
        .change(function (e, type) {
          if (!(type && type == 'init')) {
            $('#businessRole', goForm).val('');
          }
          loadBusinessRole($('#businessRole', goForm), $('#businessType', goForm));
        })
        .trigger('change', 'init');
    }
  });
}

// 流转设置--业务角色
function loadBusinessRole($businessRole, $businessType) {
  var businessType = $businessType.val();
  if (StringUtils.isBlank(businessType)) {
    $businessRole.wellSelect({
      valueField: 'businessRole'
    });
    return;
  }
  $.get({
    url: ctx + '/api/workflow/definition/getBusinessRoles',
    data: {
      businessType: businessType
    },
    // async: false,
    version: '',
    success: function (result) {
      var data = [];
      $.each(result.data, function (i, item) {
        data.push({
          id: item.value,
          text: item.label
        });
      });
      $businessRole.wellSelect({
        data: data,
        valueField: 'businessRole'
      });
    }
  });
}

// 流转设置 -- 分支模式状态切换和设置
function setConditions(goForm, taskProperty, field) {
  var mode = taskProperty.selectSingleNode('parallelGateway') ? taskProperty.selectSingleNode('parallelGateway').find(field) : [];
  if (mode.length > 0) {
    var value = $(mode).find('value')[0].innerHTML;
    $("input[name='" + field + "'][value='" + value + "']")
      .attr('checked', true)
      .trigger('change');
    if (field == 'forkMode' && value == '2') {
      $('.many-flow-content').show();
      var chooseForkingDirection = $(mode).find('chooseForkingDirection').text();

      $('#chooseForkingDirection').prop('checked', chooseForkingDirection == '1' ? true : false);
      setInputXMLValue(goForm, taskProperty, [
        'businessType',
        'businessRole',
        'undertakeSituationPlaceHolder',
        'infoDistributionPlaceHolder',
        'operationRecordPlaceHolder'
      ]);
    }
  }
}

// 流转设置 -- 显示位置
function setPlaceholder(obj, goForm) {
  var formUuid = bGetForm();
  $.get({
    url: ctx + '/api/workflow/definition/getFormBlocks',
    data: {
      formUuid: formUuid
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

// 流转设置--获取主表字段函数
function getFormFields(formUuid) {
  var fields = [];
  $.get({
    url: ctx + '/api/workflow/definition/getFormFields',
    data: {
      formUuid: formUuid
    },
    // async: false,
    success: function (result) {
      fields = result.data;
    }
  });
  return fields;
}

// 高级设置
function scripts(goForm, taskProperty) {
  setRecords(goForm, taskProperty);
  setScripts(goForm, taskProperty);
}

// 高级设置 -- 信息记录
function setRecords(goForm, taskProperty) {
  var records = taskProperty.selectSingleNode('records') ? taskProperty.selectSingleNode('records').find('record') : [];
  if (records.length > 0) {
    // var html = '';
    for (var i = 0; i < records.length; i++) {
      var lamp = {};
      lamp.name = $(records[i]).find('name').text();
      lamp.field = $(records[i]).find('field').text();
      lamp.way = $(records[i]).find('way').text();
      lamp.assembler = $(records[i]).find('assembler').text();
      lamp.contentOrigin = $(records[i]).find('contentOrigin').text();
      lamp.ignoreEmpty = $(records[i]).find('ignoreEmpty').text();
      lamp.fieldNotValidate = $(records[i]).find('fieldNotValidate').text();
      lamp.enableWysiwyg = $(records[i]).find('enableWysiwyg') != null ? $(records[i]).find('enableWysiwyg').text() : '0';
      lamp.enablePreCondition = $(records[i]).find('enablePreCondition') != null ? $(records[i]).find('enablePreCondition').text() : '0';
      lamp.value = $(records[i]).selectSingleNode('value').text();
      lamp.conditions = [];
      var conditionUnitsEle = $(records[i]).selectSingleNode('conditions') ? $(records[i]).selectSingleNode('conditions').find('unit') : [];
      $.each(conditionUnitsEle, function (index, item) {
        lamp.conditions.push({
          name: $(item).find('argValue').text(),
          value: $(item).find('value').text(),
          type: $(item).attr('type')
        });
      });
      var li =
        "<li class='work-flow-other-item'>" +
        "<div class='record-info'>" +
        lamp.name +
        '</div>' +
        "<button type='button' class='editRecordInfo'><i class='iconfont icon-ptkj-shezhi' title='设置'></i></button>" +
        "<button type='button' class='delRecordInfo'><i class='iconfont icon-ptkj-shanchu' title='删除'></i></button>" +
        '</li>';
      var $li = $(li);
      $li.find('.record-info').data('obj', lamp);
      $('#recordsList', '#advancedSetting').append($li);
    }
    // $('#recordsList', '#advancedSetting').html(html);
  }
}

// 高级设置 -- 事件脚本
function setScripts(goForm, taskProperty) {
  //事件脚本
  var eventScripts = taskProperty.selectSingleNode('eventScripts');
  if (eventScripts) {
    var eventScript = eventScripts.selectNodes('eventScript');
    $.each(eventScript, function (i, item) {
      var text = $.trim($(item).text());
      if (text) {
        var data = {
          pointcut: $(item).attr('pointcut'),
          type: $(item).attr('type'),
          contentType: $(item).attr('contentType'),
          text: text
        };
        addEventScript(goForm, taskProperty, data, 'task');
      }
    });
  }
}

//保存当前设置
function saveCurrentSetting() {
  if (saveSetting) {
    $('#btn_save').trigger('click');
  } else {
    saveSetting = true;
  }
}

function forbidEditRefFlow() {
  var isRef = Browser.getQueryString('isRef');
  if (isRef === '1') {
    $(':input').prop('disabled', true); //引用的禁止编辑
  }
}

function getFormID() {
  var loNode = goWorkFlow.flowXML.selectSingleNode('./property/formID');
  var formUuid = loNode != null ? loNode.text() : null;
  return formUuid;
}

function getOrgVersionId() {
  var orgVersionId = null;
  var loNode = goWorkFlow.flowXML.selectSingleNode('./property/orgId');
  var orgId = loNode != null ? loNode.text() : null;
  if (orgId) {
    $.get({
      url: `/proxy/api/org/organization/version/getOrgVersionIdByOrgId?orgId=${orgId}`,
      async: false,
      success: function (result) {
        orgVersionId = result.data;
      }
    })
  }
  return orgVersionId;
}

function bGetForm() {
  var loNode = goWorkFlow.flowXML.selectSingleNode('./property/formID');
  var lsFormName = loNode != null ? loNode.text() : '';
  return lsFormName;
}

// 从xml获取数据填充到input元素
function setInputXMLValue(goForm, dataObj, obj) {
  $.each(obj, function (i, item) {
    if (item == 'sn') {
      goForm[item].value =
        dataObj.getAttribute('code') == null
          ? dataObj.selectSingleNode('code') == null
            ? ''
            : dataObj.selectSingleNode('code').text()
          : dataObj.getAttribute('code');
    } else {
      goForm[item].value =
        dataObj.getAttribute(item) == null
          ? dataObj.selectSingleNode(item) == null
            ? ''
            : dataObj.selectSingleNode(item).text()
          : dataObj.getAttribute(item);
    }
  });
}

// 通用的表单元素填充值  input、wellselect、radio、checkbox
function setInputValue(goForm, dataObj, obj) {
  for (var i = 0; i < obj.length; i++) {
    $('#' + obj[i], goForm).val(dataObj[obj[i]]);
  }
}

// 通用获取人员数据
function getAllDatas(units) {
  var allData = {};

  var unitLabel = [];
  var unitValue = [];
  var formField = [];
  var tasks = [];
  var options = [];
  var custom = [];

  for (var i = 0; i < units.length; i++) {
    if ($(units[i]).attr('type') == '1') {
      unitLabel.push($(units[i]).find('argValue').text());
      unitValue.push($(units[i]).find('value').text());
    }
    if ($(units[i]).attr('type') == '2') {
      formField.push($(units[i]).find('value').text());
    }
    if ($(units[i]).attr('type') == '4') {
      tasks.push($(units[i]).find('value').text());
    }
    if ($(units[i]).attr('type') == '8') {
      options.push($(units[i]).find('value').text());
    }
    if ($(units[i]).attr('type') == '16') {
      custom.push($(units[i]).find('argValue').text());
      custom.push($(units[i]).find('value').text());
    }
  }
  allData.unitLabel = unitLabel;
  allData.unitValue = unitValue;
  allData.formField = formField;
  allData.tasks = tasks;
  allData.options = options;
  allData.userCustom = custom;

  return allData;
}

// 权限设置 -- 权限弹窗
function showAuthDialog(title, task, field, url, num) {
  var rightList = getRightList(task, field, url);
  var html = initAuthDialog(rightList, num);
  var $rightDialig = top.appModal.dialog({
    title: title,
    message: html,
    size: 'large',
    height: '500',
    shown: function () { },
    buttons: {
      save: {
        label: '保存',
        className: 'well-btn w-btn-primary',
        callback: function () {
          var inputs = $rightDialig.find("input[type='checkbox']");
          var html = '';
          $.each(inputs, function (index, item) {
            if ($(item).prop('checked') == true) {
              var name = $(item).data('name');
              var value = $(item).val();
              html += '<li class="work-flow-auth-item" data-code="' + value + '" data-value="' + value + '">' + name + '</li>';
            }
          });
          $('#' + field).html(html);
        }
      },
      reset: {
        label: '恢复默认',
        className: 'well-btn w-btn-primary w-line-btn',
        callback: function () {
          var laNode = goWorkFlow.dictionXML.selectNodes(url);
          var html = '';
          $.each(laNode, function (index, item) {
            if ($(item).find('isDefault').text() == 1) {
              var name = $(item).find('name').text();
              var value = $(item).find('value').text();
              // 发起权限 默认不显示必须签署意见
              if (value == 'B004026' && field == 'startRights') {
                return true;
              }
              // 监督权限 默认不显示抄送、关注、取消关注
              if ((value == 'B004008' || value == 'B004010' || value == 'B004012') && field == 'adminRights') {
                return true;
              }
              html += '<li class="work-flow-auth-item" data-code="' + value + '" data-value="' + value + '">' + name + '</li>';
            }
          });
          $('#' + field).html(html);
        }
      },
      close: {
        label: '关闭',
        className: 'btn-default',
        callback: function () { }
      }
    }
  });
}

// 权限设置 -- 自定义权限弹窗
function showCustomAuthDialog(taskProperty, data, index) {
  var html = initCustomAuthDialog();
  var $customRightDialig = top.appModal.dialog({
    title: '自定义',
    message: html,
    size: 'large',
    zIndex: 9999998,
    shown: function () {
      var btnData = [];
      var taskData = [];
      var rollBackTaskData = [];
      var laNode = goWorkFlow.dictionXML.selectNodes('/diction/buttons/button');
      var form = $customRightDialig.find('#customAuthForm')[0];

      if (laNode.length > 0) {
        for (var i = 0; i < laNode.length; i++) {
          var id = $(laNode[i]).find('value').text();
          var text = $(laNode[i]).find('name').text();
          // 去掉提交时自动套打
          if (id == 'B004020') {
            continue;
          }
          btnData.push({
            id: id,
            text: text
          });
        }
      }

      // 新的名称
      var selectSubFlowSetting = {
        view: {
          showIcon: false
        },
        check: {
          enable: true,
          chkStyle: 'radio',
          radioType: 'level'
        },
        async: {
          otherParam: {
            serviceName: 'resourceFacadeService',
            methodName: 'getResourceButtonTree',
            data: [-1, null]
          },
          type: 'POST'
        },
        callback: {}
      };

      $("input[name='btnSource']", $customRightDialig)
        .off()
        .on('change', function () {
          var val = $(this).val();
          $customRightDialig.find('.btnSource1').hide();
          $customRightDialig.find('.btnSource2').hide();
          $customRightDialig.find('.btnSource' + val).show();
        });
      // 使用人
      $('#btnOwners', $customRightDialig)
        .off()
        .on('click', function (e) {
          top.$.unit2.open({
            valueField: 'btnOwnerIds',
            labelField: 'btnOwners',
            title: '人员选择',
            multiple: true,
            type: 'MyUnit',
            v: "7.0",
            orgVersionId: getOrgVersionId(),
          });
        });
      // 选择参与人
      $customRightDialig
        .find('#Dusers')
        .off()
        .on('click', function (e) {
          e.target.title = '选择参与人';
          bFlowActions(29, e, form);
        });
      // 选择抄送人
      $customRightDialig
        .find('#DcopyUsers')
        .off()
        .on('click', function (e) {
          e.target.title = '选择抄送人';
          bFlowActions(30, e, form);
        });

      $('#addBtnClass', $customRightDialig)
        .off()
        .on('click', function () {
          var btnClassNameVal = $('#btnClassName', $customRightDialig).val();
          top.$.WCommonBtnLib.show({
            value: btnClassNameVal && isJSON(btnClassNameVal) ? JSON.parse(btnClassNameVal) : {},
            confirm: function (data) {
              var btnHtml = '';
              var btnStyle = '';
              if ($.inArray(data.btnInfo.type, ['primary', 'minor', 'line', 'noLine']) > -1) {
                if (data.iconInfo) {
                  btnHtml +=
                    '<a class="well-btn ' +
                    data.btnColor +
                    ' ' +
                    data.btnInfo['class'] +
                    ' ' +
                    data.btnSize +
                    '"><i class="' +
                    data.iconInfo.fileIDs +
                    '"></i>按钮</a>';
                  btnStyle = 'well-btn ' + data.btnColor + ' ' + data.btnInfo['class'] + ' ' + data.btnSize + ' ' + data.iconInfo.fileIDs;
                } else {
                  btnHtml += '<a class="well-btn ' + data.btnColor + ' ' + data.btnInfo['class'] + ' ' + data.btnSize + '">按钮</a>';
                  btnStyle = 'well-btn ' + data.btnColor + ' ' + data.btnInfo['class'] + ' ' + data.btnSize;
                }
              } else {
                if (data.btnInfo.icon) {
                  btnHtml +=
                    '<a class="well-btn ' +
                    data.btnInfo['class'] +
                    ' ' +
                    data.btnSize +
                    '"><i class="' +
                    data.btnInfo.icon +
                    '"></i>' +
                    data.btnInfo.text +
                    '</a>';
                  btnStyle = 'well-btn ' + data.btnInfo['class'] + ' ' + data.btnSize + '' + data.btnInfo.icon;
                } else {
                  btnHtml += '<a class="well-btn ' + data.btnInfo['class'] + ' ' + data.btnSize + '">' + data.btnInfo.text + '</a>';
                  btnStyle = 'well-btn ' + data.btnInfo['class'] + ' ' + data.btnSize;
                }
              }
              $('#btnClassName', $customRightDialig).parent().find('#btn_container_div').empty().append(btnHtml);

              $('#btnClassName', $customRightDialig).val(JSON.stringify(data));
              $('#btnStyle', $customRightDialig).val(btnStyle);
              $('#btnIcon', $customRightDialig).val();
            }
          });
        });

      var builEventParamsBootstrapTable = function ($element, name, data) {
        $element.bootstrapTable('destroy');
        $($element).html('');
        formBuilder.bootstrapTable.build({
          container: $element,
          name: name,
          ediableNest: true,
          table: {
            data: data,
            striped: true,
            idField: 'uuid',
            undefinedText: 'empty',
            columns: [
              {
                field: 'checked',
                formatter: function (value, row, index) {
                  if (value) {
                    return true;
                  }
                  return false;
                },
                checkbox: true
              },
              {
                field: 'uuid',
                title: 'UUID',
                visible: false,
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline'
                }
              },
              {
                field: 'text',
                title: '参数名称',
                align: 'left',
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline'
                }
              },
              {
                field: 'name',
                title: '参数名',
                align: 'left',
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline'
                }
              },
              {
                field: 'value',
                title: '参数值',
                align: 'left',
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline'
                }
              }
            ]
          }
        });
      };

      var system = top.appContext.getCurrentUserAppData().getSystem();
      var productUuid = system.productUuid;
      $('#btnHanlderName', $customRightDialig).each(function () {
        $(this, $customRightDialig).AppEvent({
          ztree: {
            params: [productUuid]
          },
          okCallback: function ($el, data) {
            if (data) {
              $('#btnHanlderId', $customRightDialig).val(data.id);
              $('#btnHanlderType', $customRightDialig).val(data.data.type);
              $('#btnHanlderPath', $customRightDialig).val(data.data.path);
              // 锚点设置
              $("input[name='btnHashType']", $customRightDialig).removeAttr('checked');
              $("input[name='btnHashType']", $customRightDialig).trigger('change');
              if (isSupportsAppHashByAppPath(data.data.path)) {
                $("input[name='btnHashType']", $customRightDialig).removeAttr('disabled');
              } else {
                $("input[name='btnHashType']", $customRightDialig).attr('disabled', 'disabled');
                $('.showHashType1,.showHashType2', $customRightDialig).hide();
              }
              $("input[name='btnHash']", $customRightDialig).val('');
              $('#btnHashTree', $customRightDialig).wCommonComboTree({
                value: ''
              });
            }
          },
          clearCallback: function ($el) {
            $('#btnHanlderId,#btnHanlderType,#btnHanlderPath', $customRightDialig).val('');
            // 锚点设置
            $("input[name='btnHashType']", $customRightDialig).removeAttr('checked');
            $("input[name='btnHashType']", $customRightDialig).trigger('change');
            $("input[name='btnHashType']", $customRightDialig).attr('disabled', 'disabled');
            $("input[name='btnHash']", $customRightDialig).val('');
            $('#btnHashTree', $customRightDialig).wCommonComboTree({
              value: ''
            });
          }
        });
      });

      var isSupportsAppHashByAppPath = function (appPath) {
        var isSupportsAppHash = false;
        if (StringUtils.isBlank(appPath)) {
          return isSupportsAppHash;
        }
        JDS.call({
          service: 'appPageDefinitionMgr.isSupportsAppHashByAppPath',
          data: [appPath],
          async: false,
          version: '',
          success: function (result) {
            isSupportsAppHash = result.data;
          }
        });
        return isSupportsAppHash;
      };

      $("label[for='btnHashType1'],label[for='btnHashType2']", $customRightDialig).on('mouseup', function () {
        var $radio = $(this).prev();
        if ($radio.attr('checked') == 'checked') {
          setTimeout(function () {
            console.log(1);
            $radio.removeAttr('checked');
            $('.showHashType2', $customRightDialig).hide();
            $('.showHashType1', $customRightDialig).hide();
          }, 1);
        }
      });

      $("input[name='btnHashType']", $customRightDialig)
        .off()
        .on('click', function () {
          var val = $("input[name='btnHashType']:checked", $customRightDialig).val();
          if (val == '1') {
            $('.showHashType2', $customRightDialig).hide();
            $('.showHashType1', $customRightDialig).show();
            $('#btnAppointHashTree', $customRightDialig).wCommonComboTree({
              service: 'pageDefinitionService.getAppHashTreeByAppPath',
              serviceParams: [$('#btnHanlderPath', $customRightDialig).val()],
              multiSelect: false, // 是否多选
              parentSelect: false, // 父节点选择有效，默认无效
              onAfterSetValue: function (event, self, value) {
                $('#btnAppointHash', $customRightDialig).val(value);
                var valueNodes = self.options.valueNodes;
                if (valueNodes && valueNodes.length == 1) {
                  var parantNode = valueNodes[0].getParentNode();
                  if (parantNode) {
                    $(this).val('/' + parantNode.name + '/' + valueNodes[0].name);
                  } else {
                    $(this).val('/' + valueNodes[0].name);
                  }
                }
              }
            });
          } else if (val == '2') {
            $('.showHashType1', $customRightDialig).hide();
            $('.showHashType2', $customRightDialig).show();
          }
        })
        .trigger('change');

      var $element = $('.event-params', $customRightDialig);
      builEventParamsBootstrapTable($element, 'eventParams', []);
      $($customRightDialig).find('#btnRole2').val('DRAFT;TODO');

      if (data) {
        $("input[name='btnSource'][value='" + data.btnSource + "']", $customRightDialig)
          .attr('checked', true)
          .trigger('change');
        if (data.btnSource && data.btnSource == '1') {
          var source1 = ['newName', 'newCode', 'newCodeName', 'btnUsers', 'btnUserIds', 'btnCopyUsers', 'btnCopyUserIds'];

          setInputValue($customRightDialig, data, source1);
          if ($('#newCode', $customRightDialig).val() != '' && $('#newCodeName', $customRightDialig).val() == '') {
            $('#newCodeName', $customRightDialig).val($('#newName', $customRightDialig).val());
          }

          $($customRightDialig).find('#DTask').val(data['btnArgument']);
          $($customRightDialig).find('#DButton').val(data['btnValue']).trigger('change');
          $($customRightDialig).find('#DButtonName').val(data['btnName']);
          $($customRightDialig).find('#btnRole').val(data['btnRole']);
          $("input[name='useWay'][value='" + data.useWay + "']", $customRightDialig)
            .attr('checked', true)
            .trigger('change');

          $($customRightDialig).find('#btnOwners').val(data['btnOwners']);
          $($customRightDialig).find('#btnOwnerIds').val(data['btnOwnerIds']);
          $($customRightDialig).find('#btnRemark1').val(data['btnRemark']);
          if (data.btnUserIds) {
            renderUnitValue(data.btnUserIds, form, 'user');
          }
          if (data.btnCopyUserIds) {
            renderUnitValue(data.btnCopyUserIds, form, 'copyUser');
          }
        } else if (data.btnSource && data.btnSource == '2') {
          var source2 = ['btnSource', 'btnId', 'btnClassName', 'btnStyle', 'btnIcon', 'sortOrder'];
          setInputValue($customRightDialig, data, source2);
          $($customRightDialig).find('#btnName').val(data['newName']);
          $('#btnHanlderName', $customRightDialig).val(data['piName']);
          $('#btnHanlderPath', $customRightDialig).val(data['btnHanlderPath']);
          $('#btnHanlderId', $customRightDialig).val(data['piUuid']).trigger('change');
          $('#btnHanlderName', $customRightDialig).each(function () {
            $(this, $customRightDialig).AppEvent('setValue', data['piUuid']);
          });
          $("input[name='btnHashType'][value='" + data.hashType + "']", $customRightDialig)
            .attr('checked', true)
            .trigger('change');
          if (data.hashType == '1') {
            $('#btnHashType1', $customRightDialig).trigger('click');
            $('#btnAppointHashTree', $customRightDialig).wCommonComboTree({
              value: data['hash']
            });
          } else if (data.hashType == '2') {
            $('#btnHashType2', $customRightDialig).trigger('click');
            $('#btnCustomHash', $customRightDialig).val(data['hash']);
          }
          if (isSupportsAppHashByAppPath(data['btnHanlderPath'])) {
            $("input[name='btnHashType']", $customRightDialig).removeAttr('disabled');
          } else {
            $("input[name='btnHashType']", $customRightDialig).attr('disabled', 'disabled');
          }
          var eventParams = data['eventParams'] == '' ? '' : JSON.parse(data['eventParams']);
          $.each(eventParams, function (i, item) {
            item.uuid = new UUID();
          });
          builEventParamsBootstrapTable($element, 'eventParams', eventParams);
          $("input[name='btnPosition'][value='" + data.targetPosition + "']", $customRightDialig)
            .attr('checked', true)
            .trigger('change');
          $($customRightDialig).find('#btnRemark2').val(data['btnRemark']);
          $($customRightDialig).find('#btnRole2').val(data['btnRole']);
          var className = isJSON($('#btnClassName', $customRightDialig).val())
            ? JSON.parse($('#btnClassName', $customRightDialig).val())
            : $('#btnClassName', $customRightDialig).val();
          var iconName = $('#btnIcon', $customRightDialig).val();
          var html = '';
          if (className != '' && typeof className == 'string') {
            html = '<a class="' + className + '">';
            if (iconName != '') {
              html += '<i class="' + iconName + '"></i>';
            }
            html += '按钮</a>';
          } else if (className != '' && typeof className != 'string') {
            if (className.btnColor) {
              var classType = className.btnInfo && className.btnInfo.class;
              html = '<a class="well-btn ' + className.btnColor + ' ' + classType + '">';
              if (className.iconInfo && className.iconInfo.filePaths) {
                html += '<i class="' + className.iconInfo.filePaths + '"></i>';
              }
              html += '按钮</a>';
            }
          }
          $('#btn_container_div', $customRightDialig).append(html);
        }
      }

      var form = $customRightDialig.find('#customAuthForm')[0];
      // // 产品集成信息-树设置
      // $("#piName", $customRightDialig).wCommonComboTree({
      //     async: true,
      //     value: $("#piUuid", $customRightDialig).val(),
      //     service: "appProductIntegrationMgr.getTreeByDataType",
      //     serviceParams: [
      //         ["1", "2", "3", "4"]
      //     ],
      //     multiSelect: false, // 是否多选
      //     parentSelect: true, // 父节点选择有效，默认无效
      //     onAfterSetValue: function(event, self, value) {
      //         $("#piUuid", $customRightDialig).val(value);
      //     }
      // });

      var laTask = aGetTasks('TASK/SUBFLOW', form.id.value, true);
      if (laTask.length > 0) {
        for (var i = 0; i < laTask.length; i++) {
          var id = laTask[i].split('|')[1];
          var text = laTask[i].split('|')[0];
          taskData.push({
            id: id,
            text: text
          });
          if (id != taskProperty.attr('id') && id != '<EndFlow>') {
            rollBackTaskData.push({
              id: id,
              text: text
            });
          }
        }
      }
      taskData.unshift({
        id: 'AUTO_SUBMIT',
        text: '按流程自动流转'
      });
      $('#DTask', $customRightDialig)
        .wellSelect({
          data: taskData,
          valueField: 'DTask'
        })
        .trigger('change');

      // 应用场景
      $('#btnRole', $customRightDialig)
        .wellSelect({
          data: [
            {
              id: 'DRAFT',
              text: '草稿'
            },
            {
              id: 'TODO',
              text: '待办'
            },
            {
              id: 'DONE',
              text: '已办'
            },
            {
              id: 'OVER',
              text: '办结'
            },
            {
              id: 'UNREAD',
              text: '未阅'
            },
            {
              id: 'FLAG_READ',
              text: '已阅'
            },
            {
              id: 'ATTENTION',
              text: '关注'
            },
            {
              id: 'SUPERVISE',
              text: '督办'
            },
            {
              id: 'MONITOR',
              text: '监控'
            }
          ],
          valueField: 'btnRole',
          multiple: true,
          separator: ';'
        })
        .trigger('change');

      // 应用场景
      $('#btnRole2', $customRightDialig)
        .wellSelect({
          data: [
            {
              id: 'DRAFT',
              text: '草稿'
            },
            {
              id: 'TODO',
              text: '待办'
            },
            {
              id: 'DONE',
              text: '已办'
            },
            {
              id: 'OVER',
              text: '办结'
            },
            {
              id: 'UNREAD',
              text: '未阅'
            },
            {
              id: 'FLAG_READ',
              text: '已阅'
            },
            {
              id: 'ATTENTION',
              text: '关注'
            },
            {
              id: 'SUPERVISE',
              text: '督办'
            },
            {
              id: 'MONITOR',
              text: '监控'
            }
          ],
          valueField: 'btnRole2',
          multiple: true,
          separator: ';'
        })
        .trigger('change');

      $('#newCode', $customRightDialig)
        .comboTree({
          labelField: 'newCodeName',
          valueField: 'newCode',
          treeSetting: selectSubFlowSetting,
          autoInitValue: false,
          width: '100%',
          height: 260,
          autoCheckByValue: true,
          valueBy: 'data',
          labelBy: 'name'
        })
        .trigger('change');

      $('#DButton', $customRightDialig)
        .wellSelect({
          data: btnData,
          labelField: 'DButtonName',
          valueField: 'DButton'
        })
        .on('change', function () {
          $('#DTask', $customRightDialig)
            .wellSelect({
              data: taskData,
              valueField: 'DTask'
            })
            .trigger('change');
          if ($(this).val() == 'B004002') {
            $('.showTasks', $customRightDialig).show();
            $('.showUsers', $customRightDialig).show();
          } else if ($(this).val() == 'B004003' || $(this).val() == 'B004020' || $(this).val() == 'B004016') {
            // "B004002" "B004003" "B004020"
            $('.showTasks', $customRightDialig).show();
            $('.showUsers', $customRightDialig).hide();
            // 退回按钮更新退回可选的环节
            if ($(this).val() == 'B004003') {
              $('#DTask', $customRightDialig)
                .wellSelect({
                  data: rollBackTaskData,
                  valueField: 'DTask'
                })
                .trigger('change');
            }
          } else {
            $('.showTasks', $customRightDialig).hide();
            $('.showUsers', $customRightDialig).hide();
          }
        })
        .trigger('change');
    },
    buttons: {
      save: {
        label: '保存',
        className: 'well-btn w-btn-primary',
        callback: function () {
          if (!ButtonOKCheck($customRightDialig)) {
            return false;
          }
          var returnValue = ButtonOKEvent($customRightDialig);
          if (index - 0 >= 0) {
            $('#customRightList')
              .find('li:eq(' + index + ')')
              .data('obj', returnValue)
              .find('div')
              .text(returnValue.newName);
          } else {
            var html =
              "<li class='work-flow-other-item' data-obj='" +
              JSON.stringify(returnValue) +
              "'>" +
              "<div data-value='33'>" +
              returnValue.newName +
              '</div>' +
              "<button class='setCustomBtn' type='button'><i class='iconfont icon-ptkj-shezhi' title='设置'></i></button>" +
              "<button class='delCustomBtn' type='button'><i class='iconfont icon-ptkj-shanchu' title='删除'></i></button>" +
              '</li>';
            $('#customRightList').append(html);
          }
        }
      },
      close: {
        label: '取消',
        className: 'btn-default',
        callback: function () { }
      }
    }
  });
}

// 权限设置 -- 待办意见立场弹窗
function showOpinionDialog(obj, index) {
  var html = initOpinionDialog(obj);
  var $opinionDialog = top.appModal.dialog({
    title: '意见立场',
    message: html,
    size: 'middle',
    shown: function () {
      $('#Name').val('');
      $('#Value').val('');
    },
    buttons: {
      save: {
        label: '确定',
        className: 'well-btn w-btn-primary',
        callback: function () {
          var name = $opinionDialog.find('#Name').val(),
            value = $opinionDialog.find('#Value').val();
          if (name == '') {
            top.appModal.error('请输入意见立场名称。');
            return false;
          }
          if (value == '') {
            top.appModal.error('请输入意见立场值。');
            return false;
          }
          if (index - 0 >= 0) {
            $('#opinionList', '#authSetting')
              .find('li:eq(' + index + ')')
              .find('div')
              .data('value', value)
              .data('name', name)
              .text(name + '(' + value + ')');
          } else {
            var lis =
              '<li class="work-flow-other-item">' +
              '<div data-value="' +
              value +
              '" data-name="' +
              name +
              '">' +
              name +
              '(' +
              value +
              ')' +
              '</div>' +
              '<button class="setOpinion" type="button" "><i class="iconfont icon-ptkj-shezhi" title="设置"></i></button>' +
              '<button class="delOpinion" type="button"><i class="iconfont icon-ptkj-shanchu" title="删除"></i></button>' +
              '</li>';
            $('#opinionList', '#authSetting').append(lis);
          }
        }
      },
      close: {
        label: '取消',
        className: 'btn-default'
      }
    }
  });
}

// 高级设置 -- 信息记录弹窗
function showInfoDialog(task, data, index) {
  var html = initInfoDialog();
  var $recordInfo = top.appModal.dialog({
    title: '信息记录',
    message: html,
    size: 'large',
    height: '600',
    shown: function () {
      var forms = $recordInfo.find('#recordForm')[0];
      // 信息格式
      var formatData = [];
      var laNode = goWorkFlow.dictionXML.selectNodes('/diction/formats/format');
      for (var i = 0; i < laNode.length; i++) {
        var id = $(laNode[i]).find('value').text();
        var text = $(laNode[i]).find('name').text();
        formatData.push({
          id: id,
          text: text
        });
      }

      $('#DFormat', $recordInfo).wellSelect({
        data: formatData
      });

      $('#assembler', $recordInfo).wellSelect({
        data: [
          {
            id: 'defaultTaskFormOpinionAssembler',
            text: '按时间升序组织'
          },
          {
            id: 'descTaskFormOpinionAssembler',
            text: '按时间降序组织'
          }
        ],
        valueField: 'assembler',
        searchable: false
      });

      // 显示位置
      var loNode = goWorkFlow.flowXML.selectSingleNode('./property/formID');
      var formUuid = loNode != null ? loNode.text() : '';
      var setting = {
        view: {
          showIcon: true,
          showLine: false
        },
        check: {
          enable: true,
          chkStyle: 'radio',
          radioType: 'level'
        },
        async: {
          otherParam: {
            serviceName: 'flowSchemeService',
            methodName: 'getFormFields',
            data: [-1, formUuid]
          }
        }
      };

      if (data) {
        var info = ['name', 'way'];
        setInputValue($recordInfo, data, info);
        $('#field', $recordInfo).val(data['field']).trigger('change');
        $('#assembler', $recordInfo).wellSelect('val', data['assembler']).trigger('change');
        $('#DFormat', $recordInfo).wellSelect('val', data['value']).trigger('change');
        $("input[name='way'][value='" + data.way + "']", $recordInfo)
          .attr('checked', true)
          .trigger('change');
        $("input[name='contentOrigin'][value='" + data.contentOrigin + "']", $recordInfo)
          .attr('checked', true)
          .trigger('change');
        $("input[name='ignoreEmpty'][value='" + data.ignoreEmpty + "']", $recordInfo)
          .attr('checked', true)
          .trigger('change');
        if (data.fieldNotValidate != '1') {
          $('#fieldNotValidate', $recordInfo).attr('checked', false).trigger('change');
          $("input[name='contentOrigin'][value='1']", $recordInfo).attr('checked', true).trigger('change');
        }

        $("input[name='enableWysiwyg'][value='" + data.enableWysiwyg + "']", $recordInfo)
          .attr('checked', true)
          .trigger('change');
        var $inputs = $('[name="enablePreCondition"]', $recordInfo);
        $.each($inputs, function (i, input) {
          if (input.value == data.enablePreCondition) {
            input.checked = true;
          }
          if (data.enablePreCondition == '1') {
            $('.enablePreCondition', $recordInfo).parent().find('.tip-text').show();
          }
        });
        // var html = '';
        var conditions = data.conditions || [];
        $.each(conditions, function (index, item) {
          var li =
            " <li class='work-flow-other-item' style='padding: 0px;'" +
            " logicType='" +
            item.type +
            "'>" +
            '<div>' +
            item.name +
            '</div>' +
            "<button type='button' class='editCondition'><i class='iconfont icon-ptkj-shezhi' title='设置'></i></button>" +
            "<button type='button' class='clearCondition'><i class='iconfont icon-ptkj-shanchu' title='删除'></i></button>" +
            '</li>';
          var $li = $(li);
          $li.data('obj', item.value);
          $('#preConditions', $recordInfo).append($li);
        });
        // $('#preConditions', $recordInfo).append(html);
      } else {
        $('#assembler', $recordInfo).wellSelect('val', 'defaultTaskFormOpinionAssembler').trigger('change');
        $("input[name='way'][value='1']", $recordInfo).attr('checked', true).trigger('change');
      }

      $('#field', $recordInfo)
        .comboTree({
          labelField: 'fieldLabel',
          valueField: 'field',
          initService: 'flowSchemeService.getFormFieldByFieldNames',
          initServiceParam: [formUuid],
          treeSetting: setting,
          showSelect: true
        })
        .trigger('change');
      // 前置条件
      switchFun('enablePreCondition', $recordInfo, function (val) {
        if (val === '1') {
          $('.enablePreCondition', $recordInfo).show();
          $('.enablePreCondition', $recordInfo).parent().find('.tip-text').show();
        } else {
          $('.enablePreCondition', $recordInfo).hide();
          $('.enablePreCondition', $recordInfo).parent().find('.tip-text').hide();
        }
      });
      if ($('[name="enablePreCondition"]:checked', $recordInfo).val() == '1') {
        $('#enablePreCondition', $recordInfo).closest('.switch-wrap').addClass('active').find('.switch-radio').data('value', '1');
        $('.enablePreCondition', $recordInfo).show();
      } else {
        $('.enablePreCondition', $recordInfo).hide();
      }
      // 意见立场备选项
      var laOptName = [];
      var loNode = task.selectSingleNode('optNames');
      if (loNode != null) {
        var laNode = loNode.selectNodes('unit');
        if (laNode != null) {
          for (var i = 0; i < laNode.length; i++) {
            var lsValue = $(laNode[i]).selectSingleNode('value').text();
            var lsText = $(laNode[i]).selectSingleNode('argValue').text();
            laOptName.push(lsText + '|' + lsValue);
          }
        }
      }
      var optname = laOptName.join(';');
      // 添加前置条件
      $('#addPreCondition', $recordInfo)
        .off()
        .on('click', function () {
          showConditionDialog({
            action: 'add',
            optionData: optname,
            data: null,
            index: null,
            types: ['1', '3', '4', '5', '6'],
            listContainer: $('#preConditions', $recordInfo),
            title: '添加前置条件'
          });
        });
      // 编辑前置条件
      $('#preConditions', $recordInfo)
        .off()
        .on('click', 'button', function (e) {
          if ($(this).hasClass('editCondition')) {
            var index = $(this).parent().index();
            var data = {};
            data.value = $(this).parent().data('obj');
            data.text = $(this).parent().find('div').text();
            data.type = $(this).parent().attr('logicType');
            showConditionDialog({
              optionData: optname,
              data: data,
              index: index,
              types: ['1', '3', '4', '5', '6'],
              listContainer: $('#preConditions', $recordInfo),
              title: '前置条件'
            });
          } else {
            $(this).parent().remove();
          }
        });
    },
    buttons: {
      save: {
        label: '保存',
        className: 'well-btn w-btn-primary',
        callback: function () {
          var forms = $recordInfo.find('#recordForm')[0];
          if (forms.name.value == '') {
            top.appModal.error('名称不能为空！');
            return false;
          }
          if (forms.field.value == '') {
            top.appModal.error('记录字段不能为空！');
            return false;
          }
          if (forms.DFormat.value == '') {
            top.appModal.error('信息格式不能为空！');
            return false;
          }
          var lamp = {};
          lamp.name = forms.name.value;
          lamp.field = forms.field.value;
          lamp.value = forms.DFormat.value;
          lamp.fieldNotValidate = $('#fieldNotValidate', $recordInfo).prop('checked') ? '1' : '0';
          lamp.fieldLabel = forms.fieldLabel.value;
          lamp.way = forms.way.value;
          lamp.assembler = forms.assembler.value;
          lamp.contentOrigin = forms.contentOrigin.value;
          lamp.ignoreEmpty = $('#ignoreEmpty', $recordInfo).prop('checked') ? '1' : '0';
          lamp.enableWysiwyg = $('#enableWysiwyg', $recordInfo).prop('checked') ? '1' : '0';
          lamp.enablePreCondition = $('[name="enablePreCondition"]:checked', $recordInfo).val();
          lamp.conditions = [];
          var leftBreakCount = 0;
          var rightBreakCount = 0;
          $('#preConditions  li', $recordInfo).each(function () {
            var $this = $(this);
            var conditionData = $this.data('obj');
            var conditionName = $this.find('div:eq(0)').text();
            // 条件表达式括号匹配判断
            if (conditionData) {
              for (var i = 0; i < conditionData.length; i++) {
                if (conditionData[i] == '(') {
                  leftBreakCount++;
                } else if (conditionData[i] == ')') {
                  rightBreakCount++;
                }
              }
            }
            lamp.conditions.push({
              name: conditionName,
              value: conditionData,
              type: $this.attr('logicType')
            });
          });
          if (lamp.enablePreCondition == '1' && lamp.conditions.length == 0) {
            top.appModal.error('前置条件必填');
            return false;
          }
          if (leftBreakCount != rightBreakCount) {
            top.appModal.error('请检查条件的合法性。');
            return false;
          }

          if (index - 0 >= 0) {
            $('#recordsList', '#advancedSetting')
              .find('li:eq(' + index + ')')
              .find('div')
              .data('obj', lamp)
              .text(lamp.name);
          } else {
            var html = '';

            html +=
              "<li class='work-flow-other-item'>" +
              "<div data-obj='" +
              JSON.stringify(lamp) +
              "'>" +
              lamp.name +
              '</div>' +
              "<button type='button' class='editRecordInfo'><i class='iconfont icon-ptkj-shezhi' title='设置'></i></button>" +
              "<button type='button' class='delRecordInfo'><i class='iconfont icon-ptkj-shanchu' title='删除'></i></button>" +
              '</li>';

            $('#recordsList', '#advancedSetting').append(html);
          }
        }
      },
      cancel: {
        label: '取消',
        className: 'btn-default',
        callback: function () { }
      }
    }
  });
}

// 高级设置 -- 事件脚本弹窗
function showScriptDialog() {
  var html = initScriptDialog();
  var $scriptDialog = top.appModal.dialog({
    title: '事件脚本',
    message: html,
    size: 'large',
    height: '600',
    shown: function () {
      $('#eventType', $scriptDialog).wellSelect({
        data: [
          {
            id: '1',
            text: '环节创建'
          },
          {
            id: '2',
            text: '环节完成'
          }
        ]
      });
    },
    buttons: {
      save: {
        label: '保存',
        className: 'well-btn w-btn-primary',
        callback: function () { }
      },
      cancel: {
        label: '取消',
        className: 'btn-default',
        callback: function () { }
      }
    }
  });
}

// 流转设置 -- 列配置弹窗
function showColumnSetDialog(goForm, task, data, index) {
  var html = initColumnSetDialog();
  var $columnSetDialog = top.appModal.dialog({
    title: '办理进度列定义',
    message: html,
    size: 'large',
    height: '400',
    shown: function () {
      var formUuid = bGetForm();
      var formFields = getFormFields(formUuid);
      var mainFields = [];
      $.each(formFields, function (i, field) {
        if (field.children && field.children.length == 0) {
          mainFields.push({
            id: field.id,
            text: field.name
          });
        }
      });

      if (data) {
        $('#columnType', $columnSetDialog).val(data.type).trigger('change');

        if (data.type == '1') {
          $('#columnName_1', $columnSetDialog).val(data.index).trigger('change');
          $('#columnNewName', $columnSetDialog).val(data.name);
        } else {
          $('#columnSources', $columnSetDialog).val(data.sources).trigger('change');
          $('#columnName_2', $columnSetDialog).val(data.name);
        }
      } else {
        $('#columnType', $columnSetDialog).val('1').trigger('change');
      }

      $('#columnType', $columnSetDialog)
        .wellSelect({
          data: [
            {
              id: '1',
              text: '固定'
            },
            {
              id: '2',
              text: '扩展'
            }
          ],
          searchable: false,
          valueField: 'columnType'
        })
        .on('change', function () {
          var val = $(this).val();
          $('.column_type', $columnSetDialog).hide();
          $('.column_type_' + val, $columnSetDialog).show();
        })
        .trigger('change');

      if (data) {
        $('#columnType', $columnSetDialog).wellSelect('disable', true);
      }

      $('#columnName_1', $columnSetDialog)
        .val('todoName')
        .wellSelect({
          data: [
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
          ],
          valueField: 'columnName_1',
          searchable: false,
          showEmpty: false
        })
        .on('change', function (e, type) {
          if ($('#columnNewName', $columnSetDialog).val() === '') {
            var text = $('#columnName_1', $columnSetDialog).wellSelect('data').text;
            if (type && type === 'init') {
              return;
            }
            $('#columnNewName', $columnSetDialog).val(text);
          }
        })
        .trigger('change', 'init');

      $('#columnSources', $columnSetDialog)
        .wellSelect({
          data: mainFields,
          valueField: 'columnSources'
        })
        .trigger('change');
    },
    buttons: {
      save: {
        label: '确定',
        className: 'well-btn w-btn-primary',
        callback: function () {
          var allData = $('#columnSettingTable', goForm).bootstrapTable('getData');
          var lamp = {};
          lamp.type = $('#columnType', $columnSetDialog).val();
          lamp.typeName = $('#columnType', $columnSetDialog).wellSelect('data').text;
          if ($('#columnType', $columnSetDialog).val() == '') {
            top.appModal.error('类型不能为空');
            return false;
          }

          if ($('#columnType', $columnSetDialog).val() == '1') {
            if ($('#columnName_1', $columnSetDialog).val() == '') {
              top.appModal.error('列名不能为空');
              return false;
            }
            if ($('#columnNewName', $columnSetDialog).val() == '') {
              top.appModal.error('新名称不能为空');
              return false;
            }
            lamp.index = $('#columnName_1', $columnSetDialog).val();
            lamp.name = $('#columnNewName', $columnSetDialog).val();
            lamp.sources = '';
          } else {
            if ($('#columnName_2', $columnSetDialog).val() == '') {
              top.appModal.error('列名不能为空');
              return false;
            }
            if ($('#columnSources', $columnSetDialog).val() == '') {
              top.appModal.error('列值来源不能为空');
              return false;
            }
            lamp.index = $('#columnName_2', $columnSetDialog).val();
            lamp.name = $('#columnName_2', $columnSetDialog).val();
            lamp.sources = $('#columnSources', $columnSetDialog).val();
          }
          lamp.uuid = allData.length;
          if (data) {
            $('#columnSettingTable', goForm).bootstrapTable('updateRow', {
              index: index,
              row: lamp
            });
          } else {
            $('#columnSettingTable', goForm).bootstrapTable('insertRow', {
              index: allData.length + 1,
              row: lamp
            });
          }
        }
      },
      cancel: {
        label: '取消',
        className: 'btn-default',
        callback: function () { }
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
    '<input type="text" class="form-control" id="columnType" name="columnType">' +
    '</div>' +
    '</div>' +
    '<div class="form-group column_type column_type_1">' +
    '<label for="fieldName" class="well-form-label control-label required">列名</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="columnName_1" name="columnName_1">' +
    '</div>' +
    '</div>' +
    '<div class="form-group column_type column_type_1">' +
    '<label for="operator" class="well-form-label control-label required">新名称</label>' +
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
    '<input type="text" class="form-control" id="columnSources" name="columnSources">' +
    '</div>' +
    '</div>' +
    '</div>' +
    '</form>';
  return html;
}

// 权限设置 -- 权限弹窗 html
function initAuthDialog(list, num) {
  var html = '';
  html +=
    '<form id="" class="workflow-popup form-widget">' +
    '<div class="well-form form-horizontal">' +
    '<div class="form-group">' +
    '<div class="list-vertical rights">';
  for (var i = 0; i < list.length; i++) {
    var value1 = list[i].value;
    var name1 = list[i].name;
    var isChecked = list[i].isChecked ? 'checked' : '';
    if (value1 == 'B004025') {
      continue;
    }

    html +=
      "<input type='checkbox' id='" +
      value1 +
      "' " +
      isChecked +
      " name='UserOptions' data-name='" +
      name1 +
      "' value='" +
      value1 +
      "' ><label for='" +
      value1 +
      "'>" +
      name1 +
      '</label>';
    if (i % num == num - 1) {
      html += '</div><div class="list-vertical rights">';
    }
  }
  html += '</div>' + '</div></div></form>';
  return html;
}

// 权限设置 --  自定义权限弹窗
function initCustomAuthDialog() {
  var html = '';
  html +=
    '<form name="customAuthForm" id="customAuthForm" class="workflow-popup form-widget well-workflow-form">' +
    '<div class="well-form form-horizontal">' +
    '<div class="form-group">' +
    '<label for="btnSource" class="well-form-label control-label required">按钮类型</label>' +
    '<div class="well-form-control">' +
    '<input type="radio" class="form-control" id="btnSourceInner" name="btnSource" value="1" checked>' +
    '<label for="btnSourceInner">内置功能</label>' +
    '<input type="radio" class="form-control" id="btnSourceEvent" name="btnSource" value="2">' +
    '<label for="btnSourceEvent">事件处理</label>' +
    '</div>' +
    '</div>' +
    '<div class="btnSource1">' +
    '<div class="form-group">' +
    '<label for="DButton" class="well-form-label control-label required">按钮功能</label>' +
    '<div class="well-form-control">' +
    '<input type="hidden" class="form-control" id="DButtonName" name="DButtonName" placeholder="请选择">' +
    '<input type="hidden" class="form-control" id="DButton" name="DButton" placeholder="请选择">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="newName" class="well-form-label control-label required">按钮名称</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="newName" name="newName">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="" class="well-form-label control-label no-suffix"></label>' +
    '<div class="well-form-control">' +
    '<input type="radio" class="form-control" id="useWay_2" name="useWay" value="2" checked>' +
    '<label for="useWay_2">新增按钮</label>' +
    '<input type="radio" class="form-control" id="useWay_1" name="useWay" value="1">' +
    '<label for="useWay_1">替换原按钮</label>' +
    '</div>' +
    '</div>' +
    '<div class="form-group showTasks" style="display: none;">' +
    '<label for="DTask" class="well-form-label control-label required">目标环节</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="DTask" name="DTask" placeholder="请选择">' +
    '</div>' +
    '</div>' +
    '<div class="form-group showUsers" style="display: none;">' +
    '<label for="button_Dusers" class="well-form-label control-label">参与人</label>' +
    '<div class="well-form-control org-select-container org-style3" id="Dusers">' +
    '<input type="hidden" class="form-control" id="Duser1" name="Duser1" >' +
    '<input type="hidden" class="form-control" id="user1" name="user1" >' +
    '<input type="hidden" class="form-control" id="user2" name="user2">' +
    '<input type="hidden" class="form-control" id="user4" name="user4" >' +
    '<input type="hidden" class="form-control" id="user8" name="user8" >' +
    '<ul class="org-select"></ul>' +
    '<i class="icon iconfont icon-ptkj-zuzhixuanze"></i>' +
    '</div>' +
    '</div>' +
    '<div class="form-group showUsers" style="display:none;">' +
    '<label for="button_DcopyUsers" class="well-form-label control-label">抄送人</label>' +
    '<div class="well-form-control org-select-container org-style3" id="DcopyUsers">' +
    '<input type="hidden" class="form-control" id="DcopyUser1" name="DcopyUser1">' +
    '<input type="hidden" class="form-control" id="copyUser1" name="copyUser1" >' +
    '<input type="hidden" class="form-control" id="copyUser2" name="copyUser2" >' +
    '<input type="hidden" class="form-control" id="copyUser4" name="copyUser4">' +
    '<input type="hidden" class="form-control" id="copyUser8" name="copyUser8">' +
    '<ul class="org-select"></ul>' +
    '<i class="icon iconfont icon-ptkj-zuzhixuanze"></i>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="fieldName" class="well-form-label control-label">备注</label>' +
    '<div class="well-form-control">' +
    '<textarea type="text" class="form-control" id="btnRemark1" name="btnRemark1" ></textarea>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="" class="well-form-label control-label" style="font-weight: bold">按钮权限设置</label>' +
    '<div class="well-form-control">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="btnRole" class="well-form-label control-label">应用场景</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="btnRole" name="btnRole" placeholder="请选择">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="btnOwnerIds" class="well-form-label control-label">使用人</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="btnOwners" name="btnOwners" placeholder="请选择">' +
    '<input type="hidden" class="form-control" id="btnOwnerIds" name="btnOwnerIds" >' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="newCode" class="well-form-label control-label">关联权限按钮' +
    '<div class="w-tooltip">' +
    '<i class="iconfont icon-ptkj-tishishuoming"></i>' +
    '<div class="w-tooltip-content">指定关联权限按钮后，当前按钮的权限等同于关联权限按钮的权限，可通过角色权限进行设置</div>' +
    '</div>' +
    '</label>' +
    '<div class="well-form-control">' +
    '<input type="hidden" class="form-control" id="newCodeName" name="newCodeName" placeholder="请选择">' +
    '<input type="hidden" class="form-control" id="newCode" name="newCode" placeholder="请选择">' +
    '</div>' +
    '</div>' +
    '</div>' +
    '<div class="btnSource2" style="display: none;">' +
    '<div class="form-group">' +
    '<label for="btnName" class="well-form-label control-label required">按钮名称</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="btnName" name="btnName">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="btnId" class="well-form-label control-label">按钮ID</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="btnId" name="btnId">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="piUuid" class="well-form-label control-label required">事件处理</label>' +
    '<div class="well-form-control">' +
    // '<input type="text" class="form-control" id="piName" name="piName" placeholder="请选择">' +
    // '<input type="hidden" class="form-control" id="piUuid" name="piUuid">' +
    '<input type="text" class="form-control w-nav-tree-option" name="btnHanlderName" id="btnHanlderName"/>' +
    '<input type="hidden" class="form-control w-nav-tree-option" name="btnHanlderId" id="btnHanlderId"/>' +
    '<input type="hidden" class="form-control w-nav-tree-option" name="btnHanlderPath" id="btnHanlderPath"/>' +
    '<input type="hidden" class="form-control w-nav-tree-option" name="btnHanlderType" id="btnHanlderType"/>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="piUuid" class="well-form-label control-label">锚点设置</label>' +
    '<div class="well-form-control">' +
    '<input type="radio" class="form-control" id="btnHashType1" name="btnHashType" value="1" data-checked="0">' +
    '<label for="btnHashType1">指定锚点</label>' +
    '<input type="radio" class="form-control" id="btnHashType2" name="btnHashType" value="2" data-checked="0">' +
    '<label for="btnHashType2">自定义</label>' +
    '</div>' +
    '</div>' +
    '<div class="form-group showHashType1" style="display:none;">' +
    '<label for="piUuid" class="well-form-label control-label"></label>' +
    '<div class="well-form-control">' +
    '<input type="hidden" class="form-control" id="btnAppointHash" name="btnAppointHash" >' +
    '<input type="text" class="form-control" id="btnAppointHashTree" name="btnAppointHashTree" >' +
    '</div>' +
    '</div>' +
    '<div class="form-group showHashType2" style="display:none;">' +
    '<label for="piUuid" class="well-form-label control-label">' +
    '<div class="w-tooltip">' +
    '<i class="iconfont icon-ptkj-tishishuoming"></i>' +
    '<div class="form-tooltip-content">锚点格式：/{组件ID}/{菜单、导航、页签等ID}</div>' +
    '</div>' +
    '</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="btnCustomHash" name="btnCustomHash">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="piUuid" class="well-form-label control-label">事件参数</label>' +
    '<div class="well-form-control event-params">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="piUuid" class="well-form-label control-label">目标位置</label>' +
    '<div class="well-form-control">' +
    '<input type="radio" class="form-control" id="btnPosition1" name="btnPosition" value="_blank">' +
    '<label for="btnPosition1">新窗口</label>' +
    '<input type="radio" class="form-control" id="btnPosition2" name="btnPosition" value="_self" checked>' +
    '<label for="btnPosition2">当前窗口</label>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="btnClassName" class="well-form-label control-label">按钮样式</label>' +
    '<div class="well-form-control">' +
    '<div id="btn_container_div" style="display: inline"></div>' +
    '<input type="hidden" class="form-control" id="btnClassName" name="btnClassName" >' +
    '<input type="hidden" class="form-control" id="btnStyle" name="btnStyle" >' +
    '<input type="hidden" id="btnIcon" name="btnIcon">' +
    '<button type="button" class="well-btn w-noLine-btn" id="addBtnClass">设置</button>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="sortOrder" class="well-form-label control-label">按钮排序号</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="sortOrder" name="sortOrder">' +
    '<div>内置按钮排序号：提交(10)、退回(20)、直接退回(30)、撤回(40)、保存(50)、转办(60)、会签(70)、抄送(80)、关注(90)、取消关注(100)、打印(110)、签署意见(120)、挂起(130)、恢复(140)、催办(150)、特送个人(160)、特送环节(170)、删除(180)、管理员删除(190)</div>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="" class="well-form-label control-label">备注</label>' +
    '<div class="well-form-control">' +
    '<textarea type="text" class="form-control" id="btnRemark2" name="btnRemark2"></textarea>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="" class="well-form-label control-label" style="font-weight: bold">按钮权限设置</label>' +
    '<div class="well-form-control">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="btnRole" class="well-form-label control-label">应用场景</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="btnRole2" name="btnRole2" placeholder="请选择">' +
    '</div>' +
    '</div>' +
    '</div>' +
    '</div>' +
    '</form>';
  return html;
}

// 权限设置 -- 待办意见立场弹窗 html
function initOpinionDialog(obj) {
  var name = obj ? obj.name : '';
  var value = obj ? obj.value : '';
  var html = '';
  html +=
    '<form name="optionForm" id="optionForm" class="workflow-popup form-widget">' +
    '<div class="well-form form-horizontal">' +
    '<div class="form-group">' +
    '<label for="ruleName" class="well-form-label control-label required">意见名称</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="Name" name="Name" value="' +
    name +
    '">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="Value" class="well-form-label control-label required">意见值</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="Value" name="Value" value="' +
    value +
    '">' +
    '</div>' +
    '</div>' +
    '</div>' +
    '</form>';
  return html;
}

// 表单设置 -- 表单字段弹窗
function showFormFieldDialog(taskProperty) {
  var html = initFormFieldDialog();
  var $fieldDialog = top.appModal.dialog({
    title: '表单字段设置',
    message: html,
    size: 'large',
    height: '700',
    zIndex: 10000000,
    className: 'primary-bg formFieldSet',
    shown: function () {
      var tableData = $('#formFieldTable').bootstrapTable('getData');
      initFormFieldTable(taskProperty, 'formLeftTable', $fieldDialog, tableData);
      changeTableField($('#canEditForm').val(), $('#formLeftTable', $fieldDialog));
      $('#formLeftTable', $fieldDialog).find('th .th-inner').removeAttr('title');
      if ($('#canEditForm').val() == '1') {
        $fieldDialog.find('.form-right-field:eq(1)').show();
      } else {
        $fieldDialog.find('.form-right-field:eq(1)').hide();
      }
      var data = $('#formLeftTable', $fieldDialog).bootstrapTable('getData');
      var formFieldTable = $('#formFieldTable tr');
      $.each(formFieldTable, function (index1, t) {
        var input = $(t).find('input');
        $.each(input, function (k, s) {
          if ($(s).prop('checked') && !$(s).hasClass('isSubform')) {
            if (index1 != 0) {
              var list = $(s).attr('id').substr(0, 9) + 'List';
              var text = data[index1 - 1].displayName || data[index1 - 1].relaTableViewText || data[index1 - 1].relaFileLibraryText;
              $('#' + list, $fieldDialog).append("<li data-index='" + (index1 - 1) + "'>" + text + '</li>');
            }
          }
          $('#formLeftTable', $fieldDialog)
            .find('tr:eq(' + index1 + ')')
            .find('input:eq(' + k + ')')
            .prop('checked', $(s).prop('checked'));

          if ($(s).data('formuuid') && !$(s).prop('checked')) {
            var uuid = $(s).data('formuuid');
            $('#formLeftTable', $fieldDialog)
              .find('input[data-uuid="' + uuid + '"]')
              .prop('checked', false);
            $('#formLeftTable', $fieldDialog)
              .find('input[data-uuid="' + uuid + '"]')
              .attr('disabled', 'disabled');
            $('#formLeftTable', $fieldDialog)
              .find('input[data-subformid="' + uuid + '"]')
              .prop('checked', false);
            $('#formLeftTable', $fieldDialog)
              .find('input[data-subformid="' + uuid + '"]')
              .attr('disabled', 'disabled');
            $('#formLeftTable', $fieldDialog)
              .find('input[data-subformid="' + uuid + '"]')
              .parents('tr')
              .find('.setFiles')
              .hide();
          }
        });
      });

      $.each(goWorkFlow.formFieldDatas, function (j, h) {
        if (h.buttons && h.buttons.edit) {
          var edits = h.buttons.edit;
          for (var i = 0; i < edits.length; i++) {
            if (edits[i].isChecked && h.isShowField) {
              $('#editFieldList', $fieldDialog).append('<li data-index="' + j + '">' + h.displayName + '</li>');
              break;
            }
          }
        } else if (h.formUuid && h.tableButtonInfo && h.tableButtonInfo.length > 0) {
          var btnInfo = h.tableButtonInfo;
          for (var i = 0; i < btnInfo.length; i++) {
            if (btnInfo[i].operate == '编辑类操作' && btnInfo[i].isChecked && h.isShowField) {
              $('#editFieldList', $fieldDialog).append('<li data-index="' + j + '">' + h.displayName + '</li>');
              break;
            }
          }
        }
      });

      // 全选
      $('#show_formLeftTable,#edit_formLeftTable,#fill_formLeftTable', $fieldDialog)
        .off()
        .on('click', function () {
          var isChecked = $(this).prop('checked');
          var index = $(this).parents('th').index();
          var tbody = $(this).parents('.no-table-borders').find('tbody tr');
          var id = $(this).attr('id').substr(0, 4);
          var list = id + 'FieldList';
          $('#formFieldTable thead')
            .find('tr th:eq(' + index + ')')
            .find('input')
            .trigger('click');

          $.each(tbody, function (i, item) {
            var input = $(item)
              .find('td:eq(' + index + ')')
              .find('input');
            if (isChecked) {
              if (input.attr('disabled') != 'disabled' && !input.prop('checked') && !input.hasClass('isSubform') && input.length > 0) {
                var name = data[i].displayName || data[i].relaTableViewText || data[i].relaFileLibraryText;
                $('#' + list, $fieldDialog).append("<li data-index='" + i + "'>" + name + '</li>');
              } else if (
                input.length == 0 &&
                $(item)
                  .find('td:eq(' + index + ')')
                  .data('field') == 'editField' &&
                $(item)
                  .find('td:eq(' + index + ')')
                  .prev()
                  .find('input')
                  .prop('checked') &&
                $('#editFieldList', $fieldDialog).find('li[data-index="' + i + '"]').length == 0
              ) {
                if (goWorkFlow.formFieldDatas[i].buttons && goWorkFlow.formFieldDatas[i].buttons.edit) {
                  var edits = goWorkFlow.formFieldDatas[i].buttons.edit;
                  for (var j = 0; j < edits.length; j++) {
                    if (edits[j].isChecked) {
                      $('#editFieldList', $fieldDialog).append(
                        '<li data-index="' + i + '" >' + goWorkFlow.formFieldDatas[i].displayName + '</li>'
                      );
                      break;
                    }
                  }
                } else if (goWorkFlow.formFieldDatas[i].formUuid && goWorkFlow.formFieldDatas[i].tableButtonInfo.length > 0) {
                  var btnInfo = goWorkFlow.formFieldDatas[i].tableButtonInfo;
                  for (var j = 0; j < btnInfo.length; j++) {
                    if (btnInfo[j].operate == '编辑类操作' && btnInfo[j].isChecked) {
                      $('#editFieldList', $fieldDialog).append(
                        '<li data-index="' + i + '" >' + goWorkFlow.formFieldDatas[i].displayName + '</li>'
                      );
                      break;
                    }
                  }
                }
              }
            } else if (!isChecked) {
              $('#' + list, $fieldDialog).empty();
            }
            input.attr('disabled') != 'disabled' && input.prop('checked', isChecked);
            var topInput = $('#formFieldTable')
              .find('tbody tr')
              .find('td:eq(' + index + ')')
              .find('input');
            topInput.attr('disabled') != 'disabled' && topInput.prop('checked', isChecked);
            $('#formFieldTable')
              .find('#' + id + '_formFieldTable')
              .prop('checked', isChecked);

            if (index == '1') {
              goWorkFlow.formFieldDatas[i].isShowField = isChecked;
              $('.setSubForm', $fieldDialog)[isChecked ? 'show' : 'hide']();
              $('.setFiles', $fieldDialog)[isChecked ? 'show' : 'hide']();
              if (isChecked) {
                $(item).find('td:eq(2)').find('input').removeAttr('disabled');
              } else {
                var $item2 = $(item).find('td:eq(2)').find('input');
                $item2.prop('checked') && $item2.trigger('click');
                $item2.attr('disabled', 'disabled');
              }
            }
          });
        });

      // 反选
      $('#formLeftTable', $fieldDialog)
        .off()
        .on('click', 'tbody tr input', function () {
          var index = $(this).parents('td').index();
          var isChecked = $(this).prop('checked');
          var allChecked = $(this)
            .parents('.no-table-borders')
            .find('thead th:eq(' + index + ')')
            .find('input')
            .prop('checked');

          var subTr = $('#formLeftTable tbody tr', $fieldDialog);

          if ($(this).data('uuid')) {
            var uuid = $(this).data('uuid');

            $.each(subTr, function (i, item) {
              var $list = $('.form-right-field', $fieldDialog)
                .eq(index - 1)
                .find('.form-field-list');
              var trIndex = $(item).data('index');
              var subInput = $(item)
                .find('td:eq(' + index + ')')
                .find('input');

              if (subInput.data('subformid') == uuid) {
                if (index == '1') {
                  subInput.parents('tr').find('.setFiles')[isChecked ? 'show' : 'hide']();
                }
                subInput.attr('disabled') != 'disabled' && subInput.prop('checked', isChecked);

                if (isChecked) {
                  if (index == '1') {
                    var subEdit = $(item).find('td:eq(2)').find('input');
                    subEdit.removeAttr('disabled');
                  }
                  if ($list.find('li[data-index="' + trIndex + '"]').length == 0) {
                    $list.append("<li data-index='" + trIndex + "'>" + goWorkFlow.formFieldDatas[trIndex].displayName + '</li>');
                  }
                } else if (!isChecked) {
                  if (index == '1') {
                    var subEdit = $(item).find('td:eq(2)').find('input');
                    subEdit.prop('checked') && subEdit.trigger('click');
                    subEdit.attr('disabled', 'disabled');
                  }
                  if ($list.find('li[data-index="' + trIndex + '"]').length > 0) {
                    $list.find('li[data-index="' + trIndex + '"]').remove();
                  }
                }
              }
            });
          } else if ($(this).data('formuuid')) {
            $(this).parents('tr').find('.setSubForm')[isChecked ? 'show' : 'hide']();
            var uuid = $(this).data('formuuid');

            $.each(subTr, function (i, item) {
              var subInput = $(item)
                .find('td:eq(' + index + ')')
                .find('input');
              if (subInput.data('uuid') == uuid) {
                if (isChecked) {
                  subInput.removeAttr('disabled');
                  subInput.parents('td').next().find('input').removeAttr('disabled');
                } else {
                  subInput.prop('checked') && subInput.prop('checked', isChecked);
                  subInput.attr('disabled', 'disabled');
                  subInput.parents('td').next().find('input').attr('disabled', 'disabled');
                }
              }

              if (subInput.data('subformid') == uuid) {
                if (isChecked) {
                  subInput.removeAttr('disabled');
                  // var subEdit = $(item).find('td:eq(2)').find('input');
                  // subEdit.removeAttr('disabled');
                } else {
                  subInput.prop('checked') && subInput.trigger('click');
                  subInput.attr('disabled', 'disabled');
                }
              }

              var subInput3 = $(item)
                .find('td:eq(3)')
                .find('input[data-subformid="' + uuid + '"]');
              var subInput4 = $(item)
                .find('td:eq(3)')
                .find('input[data-uuid="' + uuid + '"]');
              if (isChecked) {
                subInput3.removeAttr('disabled');
                subInput4.removeAttr('disabled');
              } else {
                subInput3.prop('checked', false);
                subInput3.attr('disabled', 'disabled');
                subInput4.prop('checked', false);
                subInput4.attr('disabled', 'disabled');
              }
            });
          } else if ($(this).data('subformid')) {
            var uuid = $(this).data('subformid');
            var subformTd = $('#formLeftTable tbody tr', $fieldDialog)
              .find('td:eq("' + index + '")')
              .find("input[data-subformid='" + uuid + "']");
            var selectAll = true;

            for (var d = 0; d < subformTd.length; d++) {
              if (!$(subformTd[d]).prop('checked')) {
                $('#formLeftTable tbody tr', $fieldDialog)
                  .find('td:eq("' + index + '")')
                  .find("input[data-uuid='" + uuid + "']")
                  .prop('checked', false);
                selectAll = false;
                break;
              }
            }
            if (selectAll) {
              $('#formLeftTable tbody tr', $fieldDialog)
                .find('td:eq("' + index + '")')
                .find("input[data-uuid='" + uuid + "']")
                .prop('checked', true);
            }
          }

          var trIndex = $(this).parents('tr').index();
          var tdEdit = $(this)
            .parents('tr')
            .find('#editField' + trIndex);
          if (tdEdit.length > 0 && $(this).attr('id') == 'showField' + trIndex) {
            if (isChecked) {
              tdEdit.removeAttr('disabled');
            } else {
              tdEdit.prop('checked') && tdEdit.trigger('click');
              tdEdit.attr('disabled', 'disabled');
            }
          }
          if ($(this).attr('id') == 'showField' + trIndex && $(this).parents('tr').find('.setFiles').length > 0) {
            $(this).parents('tr').find('.setFiles')[isChecked ? 'show' : 'hide']();
          }

          if (!isChecked && allChecked) {
            $(this)
              .parents('.no-table-borders')
              .find('thead th:eq(' + index + ')')
              .find('input')
              .prop('checked', isChecked);
            $('#formFieldTable')
              .find('thead th:eq(' + index + ')')
              .find('input')
              .prop('checked', isChecked);
          } else {
            var allFieldChecked = true;
            var subTr = $('#formLeftTable tbody tr', $fieldDialog);
            $.each(subTr, function (i, item) {
              var subInput = $(item)
                .find('td:eq(' + index + ')')
                .find('input');
              if (subInput.length > 0 && !subInput.prop('checked')) {
                allFieldChecked = false;
              }
            });
            $(this)
              .parents('.no-table-borders')
              .find('thead th:eq(' + index + ')')
              .find('input')
              .prop('checked', allFieldChecked);
            $('#formFieldTable')
              .find('thead th:eq(' + index + ')')
              .find('input')
              .prop('checked', allFieldChecked);
          }

          var currTrIndex = $(this).parents('tr').index();
          $('#formFieldTable tbody')
            .find('tr:eq(' + currTrIndex + ')')
            .find('td:eq(' + index + ')')
            .find('input')
            .trigger('click');
        });

      $('.form-left-main', $fieldDialog).niceScroll({
        cursorcolor: '#ccc'
      });

      // 右侧折叠
      $('.form-right-label', $fieldDialog)
        .off()
        .on('click', function () {
          if ($(this).find('i').hasClass('icon-ptkj-xianmiaojiantou-shang')) {
            $(this).find('i').removeClass('icon-ptkj-xianmiaojiantou-shang').addClass('icon-ptkj-xianmiaojiantou-xia');
            $(this).parent().find('.form-field-list').slideUp();
          } else {
            $(this)
              .parents('.form-right-container')
              .find('.form-right-label i')
              .removeClass('icon-ptkj-xianmiaojiantou-shang')
              .addClass('icon-ptkj-xianmiaojiantou-xia');
            $(this).parents('.form-right-container').find('.form-field-list').slideUp();
            $(this).find('i').removeClass('icon-ptkj-xianmiaojiantou-xia').addClass('icon-ptkj-xianmiaojiantou-shang');
            $(this).parent().find('.form-field-list').slideDown();
          }
        });

      var checkboxs = $('#formLeftTable tr td', $fieldDialog).find('input');
      $.each(checkboxs, function (index, item) {
        $(item)
          .off()
          .on('click', function () {
            var id = $(this).attr('id');
            var i = $(this).parents('tr').index();
            var text = data[i].displayName || data[i].relaTableViewText || data[i].relaFileLibraryText;
            var list = id.substr(0, 9) + 'List';
            var isChecked = $(this).prop('checked');
            if (id == 'showField' + i) {
              $('#formFieldTable')
                .find('tbody tr:eq(' + i + ')')
                .find('.setFiles')
              [isChecked ? 'show' : 'hide']();
            }
            if (isChecked) {
              if (!$(this).hasClass('isSubform')) {
                $('#' + list, $fieldDialog).append("<li data-index='" + i + "'>" + text + '</li>');
              }
              if (
                $(this).parent().data('field') == 'showField' &&
                $(this).parent().next().find('input').length == 0 &&
                $('#editFieldList', $fieldDialog).find("li[data-index='" + i + "']").length == 0
              ) {
                if (goWorkFlow.formFieldDatas[i].buttons && goWorkFlow.formFieldDatas[i].buttons.edit) {
                  var edits = goWorkFlow.formFieldDatas[i].buttons.edit;
                  for (var j = 0; j < edits.length; j++) {
                    if (edits[j].isChecked) {
                      $('#editFieldList', $fieldDialog).append(
                        '<li data-index="' + i + '" >' + goWorkFlow.formFieldDatas[i].displayName + '</li>'
                      );
                      break;
                    }
                  }
                } else if (goWorkFlow.formFieldDatas[i].formUuid && goWorkFlow.formFieldDatas[i].tableButtonInfo.length > 0) {
                  var btnInfo = goWorkFlow.formFieldDatas[i].tableButtonInfo;
                  for (var j = 0; j < btnInfo.length; j++) {
                    if (btnInfo[j].operate == '编辑类操作' && btnInfo[j].isChecked) {
                      $('#editFieldList', $fieldDialog).append(
                        '<li data-index="' + i + '" >' + goWorkFlow.formFieldDatas[i].displayName + '</li>'
                      );
                      break;
                    }
                  }
                }
              }

              // $('#formFieldTable')
              //   .find('tbody tr:eq(' + i + ')')
              //   .find('#' + id.substr(0, 9) + i)
              //   .prop('checked', true);
            } else {
              if (!$(this).hasClass('isSubform')) {
                $('#' + list, $fieldDialog)
                  .find("li[data-index='" + i + "']")
                  .remove();
              }

              if (
                $(this).parent().data('field') == 'showField' &&
                $(this).parent().next().find('input').length == 0 &&
                $('#editFieldList', $fieldDialog).find("li[data-index='" + i + "']").length > 0
              ) {
                $('#editFieldList', $fieldDialog)
                  .find("li[data-index='" + i + "']")
                  .remove();
              }

              // $('#formFieldTable')
              //   .find('tbody tr:eq(' + i + ')')
              //   .find('#' + id.substr(0, 9) + i)
              //   .prop('checked', '');
            }
            if (id.indexOf('showField') > -1) {
              goWorkFlow.formFieldDatas[$(this).parents('tr').index()].isShowField = isChecked;
            }
          });
      });

      $('.field-input', $fieldDialog)
        .off()
        .on('keyup', function (e) {
          var table = $(this).data('table');
          if ($(this).val() != '') {
            $(this).next('.delete-icon').show();
          } else {
            $(this).next('.delete-icon').hide();
          }
          if (e.keyCode == 13) {
            searchFields(table, $(this).val(), $fieldDialog);
          }
        })
        .on('blur', function () {
          var table = $(this).data('table');
          if ($(this).val() == '') {
            clearFieldColor(table, $fieldDialog);
          } else {
            searchFields(table, $(this).val(), $fieldDialog);
          }
        });

      // 清空搜素内容
      $('.delete-icon', $fieldDialog)
        .off()
        .on('click', function () {
          $(this).prev('.field-input').val('').focus();
          $(this).hide();
          var table = $(this).prev('.field-input').data('table');
          clearFieldColor(table, $fieldDialog);
        });
    },
    buttons: {
      cancel: {
        label: '关闭',
        className: 'btn-default',
        callback: function () { }
      }
    }
  });
}

// 表单设置 -- 表单字段弹窗 html
function initFormFieldDialog() {
  var layout = $('#canEditForm').val() == '1' ? 'fixed' : 'initial';
  var html = '';
  html +=
    "<div class='workflow-popup form-widget'>" +
    "<div class='form-left-container'>" +
    "<div class='form-left-search'><input type='text' class='field-input' data-table='formLeftTable' placeholder='搜索'><i class='delete-icon iconfont icon-ptkj-dacha-xiao'></i><i class='search-icon iconfont icon-ptkj-sousuochaxun'></i></div>" +
    "<div class='form-left-main' style='height:calc(100% - 50px);padding-top:1px;'>" +
    "<table id='formLeftTable' class=' no-table-borders' style='table-layout: " +
    layout +
    ";'></table>" +
    '</div>' +
    '</div>' +
    "<div class='form-right-container'>" +
    "<div class='form-right-title'>字段设置结果</div>" +
    "<div class='form-right-field'>" +
    "<div class='form-right-label'>显示字段 <i class='iconfont icon-ptkj-xianmiaojiantou-shang'></i></div>" +
    "<ul class='form-field-list' id='showFieldList'></ul>" +
    '</div>' +
    "<div class='form-right-field'>" +
    "<div class='form-right-label'>可编辑字段<i class='iconfont icon-ptkj-xianmiaojiantou-xia'></i></div>" +
    "<ul class='form-field-list' id='editFieldList' style='display:none;'></ul>" +
    '</div>' +
    "<div class='form-right-field'>" +
    "<div class='form-right-label'>必填字段<i class='iconfont icon-ptkj-xianmiaojiantou-xia'></i></div>" +
    "<ul class='form-field-list' id='fillFieldList' style='display:none;'></ul>" +
    '</div>' +
    '</div>' +
    '</div>';
  return html;
}

// 表单设置 -- 附件/从表弹窗html
function initFileDialog() {
  var html = '';
  html += '<form id="fieldRightForm" class="workflow-popup form-widget">' + '<div class="well-form form-horizontal">';
  html += '</div>' + '</form>';

  return html;
}

//  表单设置 -- 附件、从表弹窗
function showExtendSetDialog(title, data, index, $tableContainer) {
  var html = initFileDialog();
  var $extendDialog = top.appModal.dialog({
    title: title,
    message: html,
    size: 'middle',
    height: 420,
    width: 640,
    shown: function () {
      var html = '';
      if (data.inputMode == '6' || data.inputMode == '4' || data.inputMode == '33') {
        html = getBtnDom(data, data.buttons);
        $('#fieldRightForm', $extendDialog).find('.form-horizontal').html(html);
      } else {
        var tableBtn = data.configuration ? data.tableButtonInfo : getTableBtn(data.tableButtonInfo);
        html = getBtnDom(data, tableBtn, true);
        $('#fieldRightForm', $extendDialog).find('.form-horizontal').html(html);
      }

      if ($('#canEditForm').val() != '1') {
        $("a[href='#editBtn']", $extendDialog).hide();
      }

      $.each($('.tab-pane', $extendDialog), function (index, item) {
        var btnInput = $(item).find('.btnList input');
        for (var j = 0; j < btnInput.length; j++) {
          if (!$(btnInput[j]).prop('checked')) {
            $(item).find('.selectAll').data('all', false);
            break;
          }
        }
        if ($(item).find('.selectAll').data('all') == true) {
          $(item).find('.selectAll').prop('checked', true);
        }
      });

      $('.selectAll', $extendDialog)
        .off()
        .on('click', function () {
          var isChecked = $(this).prop('checked');
          var inputs = $(this).parents('.tab-pane').first().find('span input');
          $.each(inputs, function (index, item) {
            $(item).prop('checked', isChecked);
          });
        });
      $('.btnList input', $extendDialog)
        .off()
        .change(function () {
          var checked = $(this).prop('checked');
          if (!checked) {
            $(this).parents('.tab-pane').first().find('.selectAll').prop('checked', false);
          } else {
            var allChecked = true;
            var inputs = $(this).parents('.tab-pane').first().find('span input');
            for (var i = 0; i < inputs.length; i++) {
              var isChecked = $(inputs[i]).prop('checked');
              if (!isChecked) {
                allChecked = false;
                break;
              }
            }
            if (allChecked) {
              $(this).parents('.tab-pane').first().find('.selectAll').prop('checked', true);
            }
          }
        });
    },
    buttons: {
      save: {
        label: '保存',
        className: 'well-btn w-btn-primary',
        callback: function () {
          var inputs = $('#fieldRightForm', $extendDialog).find('.tab-content').find('input[data-uuid]');
          var uuids = { edit: [], show: [] };
          var _uuids = [];
          $.each(inputs, function (k, kk) {
            var uuid = $(kk).data('uuid');
            var type = $(kk).data('type');
            if ($(kk).prop('checked')) {
              if (type) {
                uuids[type].push(uuid);
              }
              _uuids.push(uuid);
            }
          });
          if (data.tableButtonInfo) {
            if (data.tableButtonInfo.edit) {
              _.each(data.tableButtonInfo, function (obj, idx) {
                _.each(obj, function (item, index) {
                  _.each(item, function (citem, cindex) {
                    citem.isChecked = false;
                    var uuid = (citem.code || citem.id) + '_' + index;
                    if (uuids[idx].indexOf(uuid) > -1) {
                      citem.isChecked = true;
                    }
                  })
                })
              })
            } else {
              _.each(data.tableButtonInfo, function (item, index) {
                item.isChecked = false;
                if (_uuids.indexOf(item.uuid) > -1) {
                  item.isChecked = true;
                }
              });
            }
          } else {
            for (var i in data.buttons) {
              $.each(data.buttons[i], function (index, item) {
                if (index == "headerButton" || index == "rowButton") {
                  $.each(item, function (cindex, citem) {
                    citem.isChecked = false;
                    var uuid = (citem.code || citem.id) + '_' + index;
                    if (uuids[i].indexOf(uuid) > -1) {
                      citem.isChecked = true;
                    }
                  })
                } else {
                  item.isChecked = false;
                  var uuid = data.inputMode == '6' ? item.uuid : data.inputMode == '4' ? parseInt(item.code) : item.code;
                  if (_uuids.indexOf(uuid) > -1) {
                    item.isChecked = true;
                  }
                }
              });
            }
            goWorkFlow.formFieldDatas[index] = data;
          }

          var lis = top.$('.formFieldSet').find('#editFieldList li[data-index="' + index + '"]');
          var editInput = $('#editBtn', $extendDialog).find('.btnList input:checked');

          if (editInput.length > 0 && lis.length == 0) {
            top
              .$('.formFieldSet')
              .find('#editFieldList')
              .append('<li data-index="' + index + '">' + data.displayName + '</li>');
          } else if (editInput.length == 0 && lis.length > 0) {
            top
              .$('.formFieldSet')
              .find('#editFieldList li[data-index="' + index + '"]')
              .remove();
          }
        }
      },
      cancel: {
        label: '关闭',
        className: 'btn-default',
        callback: function () { }
      }
    }
  });
}

function getTableBtn(tableBtn) {
  var btns = {
    show: [],
    edit: []
  };
  $.each(tableBtn, function (index, item) {
    btns[item.operate == '编辑类操作' || item.operate == 'edit' ? 'edit' : 'show'].push(item);
  });

  return btns;
}

function getBtnDom(field, data, isTable) {
  var html = '';
  html +=
    "<ul class='nav nav-tabs' role='tablist'>" +
    "<li class='tab-noborder active'><a href='#showBtn' data-toggle='tab' aria-expanded='true'>显示类操作</a></li>" +
    "<li class='tab-noborder'><a href='#editBtn' data-toggle='tab' aria-expanded='false'>编辑类操作</a></li>" +
    '</ul>' +
    "<div class='tab-content'>" +
    "<div class='tab-pane subtab active' id='showBtn'>" +
    initBtnInput(field, data.show, 'show', isTable) +
    '</div>' +
    "<div class='tab-pane subtab' id='editBtn'>" +
    initBtnInput(field, data.edit, 'edit', isTable) +
    '</div>' +
    '</div>';

  return html;
}

function initBtnInput(field, data, name, isTable) {
  var html = '';
  html +=
    "<div style='margin-top:20px;'><input data-all='true' type='checkbox' id='selectAll" +
    name +
    "' class='selectAll'><label for='selectAll" +
    name +
    "'>全选</label></div><div class='btnList'>";
  var inner =
    '<div><span  style="vertical-align:top;line-height:35px;">内置按钮：</span><span style="display:inline-block;width:calc(100% - 80px);vertical-top:top;">';
  var outer =
    '<div><span style="vertical-align:top;line-height:35px;">扩展按钮：</span><span style="display:inline-block;width:calc(100% - 80px);vertical-top:top;">';
  var header =
    '<div><span  style="vertical-align:top;line-height:35px;">' + (isTable ? '表头按钮' : '列表操作') + '：</span><span style="display:inline-block;width:calc(100% - 80px);vertical-top:top;">';
  var row =
    '<div><span style="vertical-align:top;line-height:35px;">' + (isTable ? '行按钮' : '行操作') + '：</span><span style="display:inline-block;width:calc(100% - 80px);vertical-top:top;">';
  $.each(data, function (index, item) {
    if (index == "headerButton") {
      _.each(item, function (citem) {
        header += getBtnInputHtml(field, citem, name, index);
      })
      inner = '';
    } else if (index == "rowButton") {
      _.each(item, function (citem) {
        row += getBtnInputHtml(field, citem, name, index);
      })
      inner = '';
    } else if (item.btnType == '0' || item.type == '扩展按钮') {
      outer += getBtnInputHtml1(field, item);
    } else {
      inner += getBtnInputHtml1(field, item)
    }
  });
  if (inner) {
    inner += '</span></div>';
    outer += '</span></div>';
    html += inner + (field.inputMode == '6' || field.fields ? outer : '') + '</div>';
  } else {
    header += '</span></div>';
    row += '</span></div>';
    html += header + row + '</div>';
  }
  return html;
}

// 6.2表单按钮
function getBtnInputHtml1(field, item) {
  var uuid = field.inputMode == '4' || field.inputMode == '33' ? item.code : item.uuid;
  var title = field.inputMode == '4' || field.inputMode == '33' ? item.name : field.inputMode == '6' ? item.buttonName : item.text;
  var isChecked = item.isChecked ? 'checked' : '';
  return '<input data-uuid="' +
    uuid +
    '" id="file_btn' +
    item.code +
    '"  type="checkbox" ' +
    isChecked +
    '><label for="file_btn' +
    item.code +
    '">' +
    title +
    '</label>';
}
// 7.0表单按钮
function getBtnInputHtml(field, item, name, index) {
  var uuid = field.inputMode == '4' || field.inputMode == '33' ? item.code : (item.code || item.uuid || item.id);
  if (!index) {
    index = '';
  } else {
    uuid += '_' + index;
  }
  var title = field.inputMode == '4' || field.inputMode == '33' ? item.name : field.inputMode == '6' ? item.buttonName : (item.text || item.title);
  var isChecked = item.isChecked ? 'checked' : '';
  return '<input data-uuid="' +
    uuid +
    '" data-type=' + name + ' id="file_btn' +
    item.code + '_' + name + index +
    '"  type="checkbox" ' +
    isChecked +
    '><label for="file_btn' +
    item.code + '_' + name + index +
    '">' +
    title +
    '</label>'
}

// 高级设置 -- 信息记录弹窗 html
function initInfoDialog() {
  var html = '';
  html +=
    '<form  id="recordForm" class="workflow-popup form-widget">' +
    '<div class="well-form well-workflow-form form-horizontal">' +
    '<div class="form-group">' +
    '<label for="ruleName" class="well-form-label control-label required">信息名称</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="name" name="name">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="field" class="well-form-label control-label required">记录字段</label>' +
    '<div class="well-form-control">' +
    '<input type="hidden" class="form-control" id="fieldLabel" name="fieldLabel">' +
    '<input type="text" class="form-control" id="field" name="field">' +
    '<div>' +
    '<div class="field-validate-checkbox">' +
    '<input type="checkbox" class="form-control" id="fieldNotValidate" name="fieldNotValidate" checked value="1">' +
    '<label for="fieldNotValidate">字段不参与表单数据的变更校验</label>' +
    '</div>' +
    '<div class="w-tooltip">' +
    '<i class="iconfont icon-ptkj-tishishuoming"></i>' +
    '<div class="w-tooltip-content">选中后，记录字段的字段值发生变更时，不影响整个表单数据的数据变更校验结果</div>' +
    '</div>' +
    '</div>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="operator" class="well-form-label control-label">记录方式</label>' +
    '<div class="well-form-control">' +
    '<input type="radio" class="form-control" id="way_1" name="way" value="1">' +
    '<label for="way_1">重复记录时不替换</label>' +
    '<input type="radio" class="form-control" id="way_2" name="way" value="2">' +
    '<label for="way_2">重复记录时替换</label>' +
    '<input type="radio" class="form-control" id="way_3" name="way"  value="3">' +
    '<label for="way_3">附加</label>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="fieldName" class="well-form-label control-label ">内容组织</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="assembler" name="assembler">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="operator" class="well-form-label control-label">历史内容来源</label>' +
    '<div class="well-form-control">' +
    '<input type="radio" class="form-control" id="contentOrigin_1" name="contentOrigin" checked value="1">' +
    '<label for="contentOrigin_1">流程信息记录</label>' +
    '<input type="radio" class="form-control" id="contentOrigin_2" name="contentOrigin" value="2">' +
    '<label for="contentOrigin_2">表单字段值</label>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="operator" class="well-form-label control-label no-suffix"></label>' +
    '<div class="well-form-control">' +
    '<input type="checkbox" class="form-control" id="ignoreEmpty" name="ignoreEmpty" value="1">' +
    '<label for="ignoreEmpty">忽略空意见</label><br/>' +
    '<input type="checkbox" class="form-control" id="enableWysiwyg" name="enableWysiwyg" value="1">' +
    '<label for="enableWysiwyg">意见即时显示<span style="margin-left: 3em; color:#ccc">| 记录的信息包含用户签署的意见时，即时显示</span></label>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="fieldName" class="well-form-label control-label required">信息格式</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="DFormat" name="DFormat">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label">前置条件</label>' +
    '<div class="well-form-control">' +
    '<div class="switch-box">' +
    '<div class="switch-wrap" id="enablePreCondition">' +
    '<span class="switch-text switch-open"></span>' +
    '<span class="switch-radio" data-status="0"></span>' +
    '<span class="switch-text switch-close"></span>' +
    '<input value="1" style="display: none" class="w-search-option" name="enablePreCondition" type="radio" />' +
    '<input value="0" style="display: none" class="w-search-option" name="enablePreCondition" type="radio" />' +
    '</div>' +
    '<div style="display: inline-block; position: relative">' +
    '<span class="tip-text" style="display: none; position: absolute; top: -10px; width: 15em;">&nbsp;&nbsp;满足以下前置条件时记录信息</span>' +
    '</div>' +
    '</div>' +
    '<div class="multiBox enablePreCondition" style="display: none">' +
    '<ul id="preConditions" class="work-flow-other-list"></ul>' +
    '<div id="addPreCondition" class="well-btn w-btn-primary w-noLine-btn well-btn-sm">' +
    '<i class="iconfont icon-ptkj-jiahao"></i>添加前置条件' +
    '</div>' +
    '</div>' +
    '</div>' +
    '</div>' +
    '</form>';
  return html;
}

// 高级设置 -- 脚本事件弹窗 html
function initScriptDialog() {
  var html = '';
  html +=
    '<form id="" class="workflow-popup form-widget">' +
    '<div class="well-form form-horizontal">' +
    '<div class="form-group">' +
    '<label for="ruleName" class="well-form-label control-label required">事件节点</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="eventType" name="eventType">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="ruleName" class="well-form-label control-label">脚本类型</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="createdScriptType" name="createdScriptType">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label">脚本内容</label>' +
    '<div class="well-form-control">' +
    '<input type="text" id="createdScriptContentType" name="createdScriptContentType" class="form-control">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="fieldName" class="well-form-label control-label">脚本定义</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="createdScriptDefinition" name="createdScriptDefinition" placeholder="请选择">' +
    '<input type="hidden" id="Fields" name="Fields" class="form-control">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label for="fieldName" class="well-form-label control-label">自定义</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="createdScriptContent" name="createdScriptContent">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label></label>' +
    '<div>' +
    '<div>支持的脚本变量 </div>' +
    '<div><div>applicationContext：spring应用上下文</div><div>currentUser：当前用户信息</div> </div>' +
    '<div><div>event：流程相关事件信息</div><div>flowInstUuid：流程实例UUID</div> </div>' +
    '<div><div>taskInstUuid：环节实例UUID</div><div>taskData：环节数据</div> </div>' +
    '<div><div>formUuid：表单定义UUID</div><div>dataUuid：表单数据UUID</div> </div>' +
    '<div><div>dyFormData：表单数据</div><div>dyFormFacade：表单接口</div> </div>' +
    '<div><div>actionType：操作类型，如Submit、Rollback等</div><div>opinionText：办理意见</div> </div>' +
    '<div>resultMessage：事件脚本执行结果，调用resultMessage.isSuccess()方法返回true，通过resultMessage.setSuccess(true/false)设置脚本执行是否成功 </div>' +
    '</div>' +
    '</div>' +
    '</div>' +
    '</form>';
  return html;
}

function checkPropertiesData() {
  if (!$('#name', '#baseSetting').val()) {
    top.appModal.error('请输入环节名称！');
    top.appModal.hideMask();
    return false;
  }
  if (!$('#id', '#baseSetting').val()) {
    top.appModal.error('请输入环节ID！');
    top.appModal.hideMask();
    return false;
  }
  var enableOpinionPosition = $('[name="enableOpinionPosition"]:checked', '#authSetting').val();
  var positionSize = $('.enableOpinionPosition ul.work-flow-other-list', '#authSetting').children().length;
  if (enableOpinionPosition == 1 && positionSize == 0) {
    top.appModal.error('请设置意见立场！');
    top.appModal.hideMask();
    return false;
  }
  return true;
}

function changeDesignerObjText() {
  var goForm = $('#taskForm')[0];
  var taskProperty = goWorkFlow.curTaskObj.xmlObject;
  taskProperty.setAttribute('name', goForm.name.value);
  top.changeObjText(goWorkFlow.curTaskObj);
}

function collectPropertiesData(check) {
  var flowXML = goWorkFlow.curTaskObj;
  var taskProperty = flowXML.xmlObject;
  var goForm = $('#taskForm')[0];
  if (check) {
    var checkStatus = checkPropertiesData();
    if (!checkStatus) {
      return false;
    }
  }
  collectBasePropertiesData(taskProperty, goForm);
  collectAuthPropertiesData(taskProperty, goForm);
  collectFormPropertiesData(taskProperty, goForm);
  collectParallelGatewayData(taskProperty, goForm);
  collectAdvanceData(taskProperty, goForm);
  top.changeObjText(flowXML);
  return true;
}

function collectBasePropertiesData(taskProperty, goForm) {
  $(taskProperty).attr('name', $('#name').val());
  $(taskProperty).attr('id', $('#id').val());
  $(taskProperty).attr('code', $('#sn').val());
  $(taskProperty).attr('type', 1);
  var isSetUser = $('input[name="isSetUser"]:checked').val();
  oSetElement(taskProperty, 'isSetUser', isSetUser); // 是否设置办理人
  oSetElement(taskProperty, 'isInheritMonitor', '1');
  if (isSetUser != '0' && isSetUser != '2') {
    bSetUnitFieldToXML(taskProperty, goForm, 'user'); // 办理人
  }
  oSetElement(taskProperty, 'isSetUserEmpty', $('#isSetUserEmpty').val()); // 找不到办理人时
  if ($('#isSetUserEmpty').val() == '1') {
    // 自动进入下一环节
    oSetElement(taskProperty, 'emptyToTask', $('#emptyToTask').val());
    oSetElement(taskProperty, 'emptyNoteDone', $('#emptyNoteDone').attr('checked') ? '1' : '');
  } else if ($('#isSetUserEmpty').val() == '2') {
    // 指定其他办理人
    bSetUnitFieldToXML(taskProperty, goForm, 'emptyToUser');
    oSetElement(taskProperty, 'emptyNoteDone', $('#emptyNoteDone').attr('checked') ? '1' : '');
  }
  oSetElement(taskProperty, 'isSelectAgain', $('#isSelectAgain').attr('checked') ? '1' : '');
  if ($('#isSelectAgain').attr('checked')) {
    oSetElement(taskProperty, 'isOnlyOne', $('#isOnlyOne').attr('checked') ? '1' : '');
  }
  oSetElement(taskProperty, 'isAnyone', $('#isAnyone').attr('checked') ? '1' : '');
  oSetElement(taskProperty, 'isByOrder', $('#isByOrder').attr('checked') ? '1' : '');

  oSetElement(taskProperty, 'sameUserSubmit', $('#sameUserSubmit').val());
  var isSetTransferUser = $('input[name="isSetTransferUser"]:checked').val();
  oSetElement(taskProperty, 'isSetTransferUser', isSetTransferUser); // 是否转办人员
  if (isSetTransferUser != '0' && isSetTransferUser != '2') {
    bSetUnitFieldToXML(taskProperty, goForm, 'transferUser'); // 办理人
  }

  if ($('#copySetting').hasClass('active')) {
    // if ($('#isSetCopyUser').val() != '0' && $('#isSetCopyUser').val() != '') {
    bSetUnitFieldToXML(taskProperty, goForm, 'copyUser');
    var isSetCopyUser = $("input[name='isSetCopyUser']:checked", goForm).val();
    oSetElement(taskProperty, 'isSetCopyUser', isSetCopyUser);
    // 指定抄送人
    if (isSetCopyUser == '1') {
      oSetElement(taskProperty, 'isConfirmCopyUser', $('#isConfirmCopyUser', goForm).attr('checked') ? '1' : '0');
    } else {
      oSetElement(taskProperty, 'isConfirmCopyUser', '0');
    }
    oSetElement(taskProperty, 'copyUserCondition', $('#copyUserCondition', goForm).val());
  } else {
    oSetElement(taskProperty, 'isSetCopyUser', '0');
  }

  if ($('#superviseSetting').hasClass('active')) {
    bSetUnitFieldToXML(taskProperty, goForm, 'monitor');
    var isSetMonitor = $("input[name='isSetMonitor']:checked").val();
    oSetElement(taskProperty, 'isSetMonitor', isSetMonitor);
  } else {
    oSetElement(taskProperty, 'isSetMonitor', '0');
  }

  oSetElement(taskProperty, 'isAllowApp', $('#isAllowApp').val());
}

function collectAuthPropertiesData(taskProperty, goForm) {
  collectUnitData(taskProperty, 'startRights', 'unit', 'code');
  collectUnitData(taskProperty, 'rights', 'unit', 'code');
  collectUnitData(taskProperty, 'doneRights', 'unit', 'code');
  collectUnitData(taskProperty, 'monitorRights', 'unit', 'code');
  collectUnitData(taskProperty, 'adminRights', 'unit', 'code');
  collectOpinionData(taskProperty);
  collectButtonsData(taskProperty);
  oSetElement(taskProperty, 'granularity', $('#granularity').val());
}

function collectFormPropertiesData(taskProperty, goForm) {
  oSetElement(taskProperty, 'canEditForm', $('#canEditForm').val());

  var hideBlocks = taskProperty.selectSingleNode('hideBlocks');
  var hideTabs = taskProperty.selectSingleNode('hideTabs');
  if (hideBlocks) {
    taskProperty.removeChild(hideBlocks);
  }
  hideBlocks = oSetElement(taskProperty, 'hideBlocks');
  if (hideTabs) {
    taskProperty.removeChild(hideTabs);
  }
  hideTabs = oSetElement(taskProperty, 'hideTabs');

  var layoutInputs = $('#formLayoutTable tbody').find('input');
  $.each(layoutInputs, function (index, item) {
    var $this = $(item);
    if ($this.data('type') == 'blocks' && !$this.prop('checked')) {
      var block = oAddElement(hideBlocks, 'unit', $this.data('field'));
      block.attr('type', '32');
    }
    if ($this.data('type') == 'tabs' && !$this.prop('checked')) {
      var tab = oAddElement(hideTabs, 'unit', $this.data('field'));
      tab.attr('type', '32');
    }
  });

  collectFormFieldData(taskProperty, 'hideFields', 1);
  collectFormFieldData(taskProperty, 'editFields', 2);
  collectFormFieldData(taskProperty, 'notNullFields', 3);

  oSetElement(taskProperty, 'formID', $('#DForm').val());
  collectFormFieldRightData(taskProperty);
  // goWorkFlow.formFieldDatas = null;
}

function collectFormFieldData(taskProperty, fields, index) {
  var fieldsXml = taskProperty.selectSingleNode(fields);
  if (fieldsXml) {
    taskProperty.removeChild(fieldsXml);
  }
  fieldsXml = oSetElement(taskProperty, fields);
  var inputs = $('#formFieldTable tbody tr').find('td:eq(' + index + ')');
  $.each(inputs, function (i, item) {
    if ($(item).find('input').length > 0 && !$(item).find('input').hasClass('isSubform')) {
      var subformid = $(item).find('input').data('subformid');
      var text = $(item).find('input').data('id');
      if (fields == 'hideFields' && !$(item).find('input').prop('checked')) {
        var fieldXml = oAddElement(fieldsXml, 'unit', subformid ? subformid + ':' + text : text);
        fieldXml.attr('type', '32');
      } else if (fields != 'hideFields' && $(item).find('input').prop('checked')) {
        var fieldXml = oAddElement(fieldsXml, 'unit', subformid ? subformid + ':' + text : text);
        fieldXml.attr('type', '32');
      }
    }
  });
}

function collectFormFieldRightData(taskProperty) {
  var fieldRight = taskProperty.selectSingleNode('editFields');
  var fileRights = taskProperty.selectSingleNode('fileRights');
  var allFormField = taskProperty.selectSingleNode('allFormField');
  var allFormFieldBtns = taskProperty.selectSingleNode('allFormFieldBtns');
  var formBtnRightSettings = taskProperty.selectSingleNode('formBtnRightSettings');
  var allFormFieldWidgetIds = taskProperty.selectSingleNode('allFormFieldWidgetIds');

  if (formBtnRightSettings) {
    taskProperty.removeChild(formBtnRightSettings);
  }
  formBtnRightSettings = oSetElement(taskProperty, 'formBtnRightSettings');
  if (fileRights) {
    taskProperty.removeChild(fileRights);
  }
  fileRights = oSetElement(taskProperty, 'fileRights');
  if (allFormField) {
    // 表单所有字段
    taskProperty.removeChild(allFormField);
  }
  allFormField = oSetElement(taskProperty, 'allFormField');
  if (allFormFieldWidgetIds) {
    // 表单所有字段
    taskProperty.removeChild(allFormFieldWidgetIds);
  }
  allFormFieldWidgetIds = oSetElement(taskProperty, 'allFormFieldWidgetIds');
  if (allFormFieldBtns) {
    // 表单所有按钮（从表/附件）
    taskProperty.removeChild(allFormFieldBtns);
  }
  allFormFieldBtns = oSetElement(taskProperty, 'allFormFieldBtns');
  $.each(goWorkFlow.formFieldDatas, function (i, item) {
    if (item.buttons) {
      var fieldBtns = [];
      var uuids = [];
      if (item.configuration) {
        var btnsData = collectFormFieldRightButtonData(item.buttons);
        var unit = oAddElement(formBtnRightSettings, 'unit', (item.subFormId ? item.subFormId + '_' : '') + item.name + '=' + JSON.stringify(btnsData.uuids));
        unit.attr('type', 32);
        // oAddElement(allFormFieldBtns, 'unit', (item.subFormId ? item.subFormId + ':' : '') + item.name + ':' + JSON.stringify(btnsData.fieldBtns));
      } else {
        for (var k in item.buttons) {
          var kItem = item.buttons[k];
          for (var h = 0; h < kItem.length; h++) {
            if (kItem[h].isChecked) {
              if (item.inputMode == '6') {
                uuids.push(kItem[h].uuid);
              } else {
                uuids.push(kItem[h].code);
              }
            }
            fieldBtns.push(item.inputMode == '6' ? kItem[h].uuid : kItem[h].code);
          }
        }
        if (uuids.length > 0) {
          var unit = oAddElement(fileRights, 'unit', (item.subFormId ? item.subFormId + ':' : '') + item.name + ':' + uuids.join(';'));
          unit.attr('type', 32);
        }
        oAddElement(allFormFieldBtns, 'unit', (item.subFormId ? item.subFormId + ':' : '') + item.name + ':' + fieldBtns.join(';'));
      }
    }
    if (item.tableButtonInfo) {
      if (item.configuration) {
        var btnsData = collectFormFieldRightButtonData(item.tableButtonInfo);
        var unit = oAddElement(formBtnRightSettings, 'unit', item.formUuid + '=' + JSON.stringify(btnsData.uuids));
        unit.attr('type', 32);
        // oAddElement(allFormFieldBtns, 'unit', item.outerId + ':' + JSON.stringify(btnsData.fieldBtns));
      } else {
        var tableBtns = [];
        $.each(item.tableButtonInfo, function (k, kItem) {
          // bug#60219
          if (kItem.isChecked == true) {
            var unit = oAddElement(fieldRight, 'unit', item.formUuid + ':' + kItem.code + '_' + item.formUuid);
            unit.attr('type', '32');
          }
          tableBtns.push(kItem.code);
        });
        oAddElement(allFormFieldBtns, 'unit', item.name + ':' + tableBtns.join(';'));
      }
    }
    if (item.subFormId) {
      var unit = oAddElement(allFormField, 'unit', item.subFormId + ':' + item.name);
      unit.attr('type', '32');
      var param = {
        id: item.id,
        subWidgetId: item.subWidgetId
      }
      var unit1 = oAddElement(allFormFieldWidgetIds, 'unit', item.subFormId + ':' + item.name + '=' + JSON.stringify(param));
      unit1.attr('type', '32');
    } else if (!item.isSubform) {
      var unit = oAddElement(allFormField, 'unit', item.name);
      unit.attr('type', '32');
      var param = {
        id: item.id,
        outerId: item.outerId,
        formUuid: item.formUuid
      }
      var unit1 = oAddElement(allFormFieldWidgetIds, 'unit', item.name + '=' + JSON.stringify(param));
      unit1.attr('type', '32');
    }
  });
}

function collectFormFieldRightButtonData(buttons) {
  var fieldBtns = {};
  var uuids = {};
  _.each(buttons, function (item, index) {
    fieldBtns[index] = {};
    uuids[index] = {};
    _.each(item, function (citem, cindex) {
      var fields = [];
      var checks = [];
      _.each(citem, function (itm, idx) {
        if (itm.isChecked) {
          checks.push(itm.code || itm.id);
        }
        fields.push(itm.code || itm.id);
      })
      uuids[index][cindex] = checks.join(';');
      fieldBtns[index][cindex] = fields.join(';');
    })
  })
  return {
    fieldBtns: fieldBtns,
    uuids: uuids
  }
}

function collectParallelGatewayData(taskProperty, goForm) {
  var parallelGateway = taskProperty.selectSingleNode('parallelGateway');
  if (parallelGateway) {
    taskProperty.removeChild(parallelGateway);
  }
  parallelGateway = oSetElement(taskProperty, 'parallelGateway');

  var forkMode = oSetElement(parallelGateway, 'forkMode');
  oSetElement(forkMode, 'value', $('input[name="forkMode"]:checked').val());

  if ($('input[name="forkMode"]:checked').val() == '2') {
    oSetElement(forkMode, 'chooseForkingDirection', $('#chooseForkingDirection').attr('checked') ? '1' : '');
    oSetElement(forkMode, 'businessType', $('#businessType').val());
    oSetElement(forkMode, 'businessRole', $('#businessRole').val());
    oSetElement(forkMode, 'undertakeSituationPlaceHolder', $('#undertakeSituationPlaceHolder').val());
    oSetElement(forkMode, 'infoDistributionPlaceHolder', $('#infoDistributionPlaceHolder').val());
    oSetElement(forkMode, 'operationRecordPlaceHolder', $('#operationRecordPlaceHolder').val());
    oSetElement(forkMode, 'undertakeSituationTitleExpression', $('#undertakeSituationTitleExpression').text());
    oSetElement(forkMode, 'infoDistributionTitleExpression', $('#infoDistributionTitleExpression').text());
    oSetElement(forkMode, 'operationRecordTitleExpression', $('#operationRecordTitleExpression').text());
    oSetElement(forkMode, 'businessRoleName', $('#businessRoleName').val());
    bSetUnitFieldToXML(forkMode, goForm, 'branchTaskMonitor');
    var undertakeSituationButtons = oAddElement(forkMode, 'undertakeSituationButtons');
    var $btnSettingTable = $('#btnSettingTable', $('#transSetting'));
    var btnData = $btnSettingTable.wellEasyTable('getAllData');
    $.each(btnData, function (index, item) {
      var button = oAddElement(undertakeSituationButtons, 'button');
      oSetElement(button, 'id', item.id);
      oSetElement(button, 'name', item.name);
      oSetElement(button, 'newName', item.newName);
    });
    var undertakeSituationColumns = oAddElement(forkMode, 'undertakeSituationColumns');
    var columnData = $('#columnSettingTable').bootstrapTable('getData');
    if (columnData.length > 0) {
      $.each(columnData, function (index, item) {
        var column = oAddElement(undertakeSituationColumns, 'column');
        oSetElement(column, 'type', item.type);
        oSetElement(column, 'name', item.name);
        oSetElement(column, 'typeName', item.typeName);
        oSetElement(column, 'index', item.index);
        oSetElement(column, 'sources', item.sources);
      });
    }
  }
  var joinMode = oSetElement(parallelGateway, 'joinMode');
  oSetElement(joinMode, 'value', $('input[name="joinMode"]:checked').val());

  // 退回设置
  oSetElement(taskProperty, 'allowReturnAfterRollback', $('#allowReturnAfterRollback').attr('checked') ? '1' : '');
  oSetElement(taskProperty, 'onlyReturnAfterRollback', $('#onlyReturnAfterRollback').attr('checked') ? '1' : '');
  oSetElement(taskProperty, 'notRollback', $('#notRollback').attr('checked') ? '1' : '');
}

function collectAdvanceData(taskProperty, goForm) {
  var records = taskProperty.selectSingleNode('records');
  if (records) {
    taskProperty.removeChild(records);
  }
  records = oSetElement(taskProperty, 'records');
  $.each($('#recordsList > li'), function (index, item) {
    var $this = $(item);
    var recordData = $this.find('div:eq(0)').data('obj');
    var record = oAddElement(records, 'record');
    oSetElement(record, 'name', recordData.name);
    oSetElement(record, 'field', recordData.field);
    oSetElement(record, 'way', recordData.way);
    oSetElement(record, 'assembler', recordData.assembler);
    oSetElement(record, 'contentOrigin', recordData.contentOrigin);
    oSetElement(record, 'ignoreEmpty', recordData.ignoreEmpty);
    oSetElement(record, 'fieldNotValidate', recordData.fieldNotValidate);
    oSetElement(record, 'enableWysiwyg', recordData.enableWysiwyg);
    oSetElement(record, 'enablePreCondition', recordData.enablePreCondition);
    oSetElement(record, 'value', recordData.value);
    var conditionsEle = oSetElement(record, 'conditions');
    var conditions = recordData.conditions || [];
    $.each(conditions, function (i, condition) {
      var conditionUnit = oAddElement(conditionsEle, 'unit');
      oSetElement(conditionUnit, 'value', condition.value);
      oSetElement(conditionUnit, 'argValue', condition.name);
      conditionUnit.attr('type', condition.type);
    });
  });

  // 打印模版设置 重新获取树节点值
  printTemplateUuidComboTreeAfterSetValue();

  oSetElement(taskProperty, 'printTemplate', $('#printTemplate').val());
  oSetElement(taskProperty, 'printTemplateId', $('#printTemplateId').val());
  oSetElement(taskProperty, 'printTemplateUuid', $('#printTemplateUuid').val());
  oSetElement(taskProperty, 'snName', $('#snName').val());
  oSetElement(taskProperty, 'serialNo', $('#serialNo').val());
  oSetElement(taskProperty, 'customJsModule', $('#customJsModule').val());
  oSetElement(taskProperty, 'listener', $('#listener').val());
  oSetElement(taskProperty, 'listenerName', $('#listenerName').val());

  var eventScripts = taskProperty.selectSingleNode('eventScripts');
  if (eventScripts) {
    taskProperty.removeChild(eventScripts);
  }
  eventScripts = oSetElement(taskProperty, 'eventScripts');
  $('#eventScripts > li').each(function () {
    var $this = $(this);
    var scriptData = $this.data('script');
    var eventScript = oAddElement(eventScripts, 'eventScript', scriptData.text);
    eventScript.attr('pointcut', scriptData.pointcut);
    eventScript.attr('type', scriptData.type);
    eventScript.attr('contentType', scriptData.contentType);
  });
}

function collectUnitData(taskProperty, field, childrenField, dataField) {
  var fieldDom = taskProperty.selectSingleNode(field);
  if (fieldDom) {
    taskProperty.removeChild(fieldDom);
  }
  fieldDom = oSetElement(taskProperty, field);
  $('#' + field + '  li').each(function () {
    var $this = $(this);
    var data = $this.data(dataField);
    var unit = oAddElement(fieldDom, childrenField, data);
    unit.attr('type', '32');
  });
}

function collectOpinionData(taskProperty) {
  // 待办意见立场开关
  oSetElement(taskProperty, 'enableOpinionPosition', $('[name="enableOpinionPosition"]:checked').val());
  // 待办意见立场
  var optNames = taskProperty.selectSingleNode('optNames');
  if (optNames) {
    taskProperty.removeChild(optNames);
  }
  optNames = oSetElement(taskProperty, 'optNames');
  $.each($('#opinionList > li'), function (index, item) {
    var $this = $(item);
    var opinionValue = $this.find('div:eq(0)').data('value');
    var opinionName = $this.find('div:eq(0)').data('name');
    var opinionUnit = oAddElement(optNames, 'unit');
    oSetElement(opinionUnit, 'value', opinionValue);
    oSetElement(opinionUnit, 'argValue', opinionName);
    opinionUnit.attr('type', '16');
  });
  // 意见立场必填
  oSetElement(taskProperty, 'requiredOpinionPosition', $('#requiredOpinionPosition').attr('checked') ? '1' : '0');
  // 显示用户意见立场值
  oSetElement(taskProperty, 'showUserOpinionPosition', $('#showUserOpinionPosition').attr('checked') ? '1' : '0');
  // 显示意见立场统计
  oSetElement(taskProperty, 'showOpinionPositionStatistics', $('#showOpinionPositionStatistics').attr('checked') ? '1' : '0');
}

function collectButtonsData(taskProperty) {
  var buttons = taskProperty.selectSingleNode('buttons');
  if (buttons) {
    taskProperty.removeChild(buttons);
  }
  buttons = oSetElement(taskProperty, 'buttons');
  $('#customRightList > li').each(function () {
    var $this = $(this);
    var buttonData = $this.data('obj');
    var button = oAddElement(buttons, 'button');
    oSetElement(button, 'btnSource', buttonData.btnSource);
    oSetElement(button, 'btnId', buttonData.btnId);
    oSetElement(button, 'piUuid', buttonData.piUuid);
    oSetElement(button, 'piName', buttonData.piName);
    oSetElement(button, 'btnHanlderPath', buttonData.btnHanlderPath);
    oSetElement(button, 'hash', buttonData.hash);
    oSetElement(button, 'hashType', buttonData.hashType);
    oSetElement(button, 'targetPosition', buttonData.targetPosition);
    oSetElement(button, 'eventParams', buttonData.eventParams);
    oSetElement(button, 'btnClassName', buttonData.btnClassName);
    oSetElement(button, 'btnStyle', buttonData.btnStyle);
    oSetElement(button, 'btnIcon', buttonData.btnIcon);
    oSetElement(button, 'sortOrder', buttonData.sortOrder);
    oSetElement(button, 'btnRemark', buttonData.btnRemark);
    oSetElement(button, 'btnRole', buttonData.btnRole);
    oSetElement(button, 'btnValue', buttonData.btnValue);
    oSetElement(button, 'newName', buttonData.newName);
    oSetElement(button, 'newCode', buttonData.newCode);
    oSetElement(button, 'newCodeName', buttonData.newCodeName);
    oSetElement(button, 'useWay', buttonData.useWay);
    oSetElement(button, 'btnArgument', buttonData.btnArgument);
    oSetElement(button, 'btnOwners', buttonData.btnOwners);
    oSetElement(button, 'btnOwnerIds', buttonData.btnOwnerIds);
    oSetElement(button, 'btnUsers', buttonData.btnUsers);
    oSetElement(button, 'btnUserIds', buttonData.btnUserIds);
    oSetElement(button, 'btnCopyUsers', buttonData.btnCopyUsers);
    oSetElement(button, 'btnCopyUserIds', buttonData.btnCopyUserIds);
  });
}

// 判断是否为第一个环节
function isFirstTask(taskProperty) {
  var taskId = $(taskProperty).getAttribute('id');
  var directions = aGetDirections();
  for (var i = 0; i < directions.length; i++) {
    var laTemp = directions[i].split('|');
    if (laTemp[3] == '<StartFlow>' && laTemp[5] == taskId) {
      return true;
    }
  }
  return false;
}

function aGetDirections() {
  var laDirection = new Array();
  var lsSource, lsTarget;
  if (bGetEQFlowXML() == true) {
    var laNode = goWorkFlow.equalFlowXML.selectNodes('./directions/direction');
    if (laNode != null) {
      for (var i = 0; i < laNode.length; i++) {
        lsSource = null;
        lsTarget = null;
        if (laNode[i].getAttribute('fromID') == '<StartFlow>') {
          lsSource = parent.sGetLang('FLOW_WF_BEGINTASKNAME') + '|<StartFlow>';
        } else {
          var loNode = goWorkFlow.equalFlowXML.selectSingleNode("./tasks/task[@id='" + laNode[i].getAttribute('fromID') + "']");
          if (loNode != null) {
            lsSource = loNode.getAttribute('name') + '|' + loNode.getAttribute('id');
          }
        }
        if (laNode[i].getAttribute('toID') == '<EndFlow>') {
          lsTarget = parent.sGetLang('FLOW_WF_ENDTASKNAME') + '|<EndFlow>';
        } else {
          var loNode = goWorkFlow.equalFlowXML.selectSingleNode("./tasks/task[@id='" + laNode[i].getAttribute('toID') + "']");
          if (loNode != null) {
            lsTarget = loNode.getAttribute('name') + '|' + loNode.getAttribute('id');
          }
        }
        if (lsSource != null && lsSource != '' && lsTarget != null && lsTarget != '') {
          laDirection.push(laNode[i].getAttribute('name') + '|' + laNode[i].getAttribute('id') + '|' + lsSource + '|' + lsTarget);
        }
      }
    }
  } else {
    for (var i = 0; i < goWorkFlow.lines.length; i++) {
      lsSource = null;
      lsTarget = null;
      if (
        goWorkFlow.lines[i] == null ||
        goWorkFlow.lines[i].xmlObject == null ||
        goWorkFlow.lines[i].xmlObject.getAttribute('name') == null ||
        goWorkFlow.lines[i].toTask.Type == 'CONDITION'
      ) {
        continue;
      }
      if (goWorkFlow.lines[i].fromTask.Type == 'BEGIN') {
        lsSource = parent.sGetLang('FLOW_WF_BEGINTASKNAME') + '|<StartFlow>';
      } else {
        if (goWorkFlow.lines[i].fromTask.Type == 'CONDITION') {
          var loLine = goWorkFlow.lines[i].fromTask.inLines[0];
          if (loLine == null) {
            continue;
          }
          if (loLine.fromTask.xmlObject != null && loLine.fromTask.xmlObject.getAttribute('id') != null) {
            lsSource = loLine.fromTask.xmlObject.getAttribute('name') + '|' + loLine.fromTask.xmlObject.getAttribute('id');
          }
        } else {
          if (goWorkFlow.lines[i].fromTask.xmlObject != null && goWorkFlow.lines[i].fromTask.xmlObject.getAttribute('id') != null) {
            lsSource =
              goWorkFlow.lines[i].fromTask.xmlObject.getAttribute('name') + '|' + goWorkFlow.lines[i].fromTask.xmlObject.getAttribute('id');
          }
        }
      }
      if (goWorkFlow.lines[i].toTask.Type == 'END') {
        lsTarget = parent.sGetLang('FLOW_WF_ENDTASKNAME') + '|<EndFlow>';
      } else {
        if (goWorkFlow.lines[i].toTask.xmlObject != null && goWorkFlow.lines[i].toTask.xmlObject.getAttribute('id') != null) {
          lsTarget =
            goWorkFlow.lines[i].toTask.xmlObject.getAttribute('name') + '|' + goWorkFlow.lines[i].toTask.xmlObject.getAttribute('id');
        }
      }
      if (lsSource != null && lsSource != '' && lsTarget != null && lsTarget != '') {
        laDirection.push(
          goWorkFlow.lines[i].xmlObject.getAttribute('name') +
          '|' +
          goWorkFlow.lines[i].xmlObject.getAttribute('id') +
          '|' +
          lsSource +
          '|' +
          lsTarget
        );
      }
    }
  }
  return laDirection;
}
