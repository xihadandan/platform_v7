goWorkFlow = null;

//设置流程属性
function setFlowProperty(_goWorkFlow) {
  goWorkFlow = _goWorkFlow;
  var flowProperty = goWorkFlow.flowXML.selectSingleNode('property');
  // 保存当前设置
  saveCurrentSetting();
  FlowLoadEvent(flowProperty);
  // FlowInitEvent(flowProperty);
  $('#DForm').change(globalSetChangedEvent);
  forbidEditRefFlow();

  if (goWorkFlow.readonlyMode) {
    setFrameReadOnly(window);
  }
}

function forbidEditRefFlow() {
  var isRef = Browser.getQueryString('isRef');
  if (isRef === '1') {
    $(':input').prop('disabled', true); //引用的禁止编辑
  }
}

//保存当前设置
var saveSetting = true;

function saveCurrentSetting() {
  if (saveSetting) {
    $('#btn_save').trigger('click');
  } else {
    saveSetting = true;
  }
}

function bSetVersionInfo(psVersion, poWindow) {
  if (psVersion == null || psVersion == '') {
    return;
  }
  var objs = poWindow.document.getElementsByTagName('SPAN');
  if (objs != null) {
    for (var i = 0; i < objs.length; i++) {
      if (objs[i].Version == null) {
        continue;
      }
      if (objs[i].Version.indexOf(psVersion) != -1) {
        if (objs[i].id == 'ID_PageContent') {
          objs[i].id = '';
        }
        objs[i].style.display = 'none';
      } else {
        objs[i].style.display = '';
      }
    }
  }
  var objs = poWindow.document.getElementsByTagName('TR');
  if (objs != null) {
    for (var i = 0; i < objs.length; i++) {
      if (objs[i].Version == null) {
        continue;
      }
      if (objs[i].Version.indexOf(psVersion) != -1) {
        objs[i].style.display = 'none';
      } else {
        objs[i].style.display = '';
      }
    }
  }
  var objs = poWindow.document.getElementsByTagName('TD');
  if (objs != null) {
    for (var i = 0; i < objs.length; i++) {
      if (objs[i].Version == null) {
        continue;
      }
      if (objs[i].Version.indexOf(psVersion) != -1) {
        objs[i].style.display = 'none';
      } else {
        objs[i].style.display = '';
      }
    }
  }
  return true;
}

//流程属性->基本属性->表单字段初始化
function flowBaseProperty(goForm, flowProperty) {
  //名称、id、版本、流程编号
  setInputXMLAttrValue(goForm, ['name', 'id', 'version', 'code']);
  if (!goWorkFlow.isNewFlow) {
    $('#flow_id').attr('disabled', true);
  }
  goWorkFlow.initFormID = flowProperty.selectSingleNode('formID') && flowProperty.selectSingleNode('formID').text();
  //流程分类、使用表单
  setSelectXMLValue(goForm, flowProperty, [
    {
      nodes: '/diction/categorys/category',
      node1: 'categorySN',
      node2: 'code',
      eleId: 'DCategory'
    },
    {
      nodes: '/diction/forms/form',
      node1: 'formID',
      node2: 'id',
      eleId: 'DForm'
    }
  ]);

  //所属模块
  var moduleIdNode = flowProperty.selectSingleNode('moduleId');
  if (moduleIdNode && moduleIdNode.text()) {
    $('#moduleId').val(moduleIdNode.text());
  } else if (top.GetRequestParam().moduleId) {
    $('#moduleId').val(top.GetRequestParam().moduleId);
  }

  if ($('#moduleId').val()) {
    $('#moduleId').prop('readonly', true);
  }
  // 所属业务模块
  $('#moduleId')
    .wSelect2({
      valueField: 'moduleId',
      remoteSearch: false,
      serviceName: 'appModuleMgr',
      queryMethod: 'loadSelectData',
      width: '100%',
      params: {
        includeSuperAdmin: true, //是否包含超管的模块
        systemUnitId: SpringSecurityUtils.getCurrentUserUnitId()
      }
    })
    .on('change', function () {
      JDS.call({
        data: [$(this).val()],
        service: 'dyFormFacade.getDyFormDefinitionIncludeRefDyFormByModuleId',
        success: function (result) {
          var data = [];
          $.each(result.data, function (i, item) {
            data.push({
              id: item.uuid,
              text: item.name + '(' + item.version + ')',
              data: item
            });
          });
          $('#DForm')
            .wellSelect('val', '')
            .wellSelect('reRenderOption', {
              data: data
            })
            .trigger('change');
        }
      });
    });

  $('#DForm').on('change', function () {
    loadFormDefinition($(this).val());
  });

  //缓存表单定义
  if (!goWorkFlow.DformDefinition) {
    loadFormDefinition($('#DForm').val());
  }

  $('input[name="title_expression"]').on('change', function () {
    var _value = $(this).val();
    if (_value === 'custom') {
      $('.showCustomTitle').show();
      $('.showCustomTitle_default').hide();
    } else {
      $('.showCustomTitle_default').show();
      $('.showCustomTitle').hide();
      $('#title_expression_text_default').text('${流程名称}_${发起人姓名}-${发起人所在部门名称}_${发起年}-${发起月}-${发起日}');
    }
  });
  //todo 标题表达式
  var title_expression =
    goWorkFlow.flowXML.getAttribute('titleExpression') == null ? '' : goWorkFlow.flowXML.getAttribute('titleExpression');
  if (title_expression) {
    $('#title_expression_custom').attr('checked', 'checked').trigger('change');
  } else {
    $('#title_expression_default').attr('checked', 'checked').trigger('change');
  }
  $('#title_expression_text').text(title_expression);

  $('#set_title_expression').on('click', function () {
    openTitleExpressionDialog(goForm, flowProperty, 'title_expression_text');
  });
  //自动更新标题
  var autoUpdateTitle = flowProperty.selectSingleNode('autoUpdateTitle');
  if (autoUpdateTitle && autoUpdateTitle.text()) {
    if (autoUpdateTitle.text() == '1') {
      $('#autoUpdateTitle').attr('checked', 'checked');
    }
  }
  switchFun('isActive', undefined, function (_val) {
    //当流程状态为启用时，显示此设置块，否则隐藏
    if (_val == '1') {
      $('.pc_mobile_show').show();
    } else {
      $('.pc_mobile_show').hide();
    }
  });
  switchFun('pcShowFlag');
  switchFun('isMobileShow');

  //流程状态、移动端设置
  setSwitchFieldValue(goForm, flowProperty, ['isActive', 'pcShowFlag', 'isMobileShow']);
  if (!goWorkFlow.flowXML.attr('name')) {
    //新增流程时
    $('#isActive').trigger('click');
    $('#pcShowFlag').trigger('click');
    $('#isMobileShow').trigger('click');
  } else {
    var $pcShowFlagDom = $('#pcShowFlag');
    var pcShowFlag = $('input[name="pcShowFlag"]:checked').val();
    if (pcShowFlag == null || pcShowFlag == undefined) {
      //旧数据，默认开启PC端显示
      $pcShowFlagDom.val(1);
      $pcShowFlagDom.addClass('active');
      //打开
      $pcShowFlagDom.find('.switch-open').show();
      $pcShowFlagDom.find('.switch-close').hide();
      $pcShowFlagDom.find("input[type='radio']:eq(0)").prop('checked', true).trigger('change');
    }
    var isActiveVar = $('input[name="isActive"]:checked').val();
    if (isActiveVar == '0') {
      $('.pc_mobile_show').hide();
    }
  }
  //备注
  setSelectSingleNode(goForm, flowProperty, ['remark']);
}

//流程属性->流程权限->表单字段初始化
function flowAuthProperty(goForm, flowProperty) {
  $('[name="creator"]').on('change', function () {
    var _val = $(this).val();
    if (_val === 'custom') {
      $('#Dcreators').show();
    } else {
      $('#Dcreators').hide();
    }
  });
  $('[name="user"]').on('change', function () {
    var _val = $(this).val();
    if (_val === 'custom') {
      $('#Dusers').show();
    } else {
      $('#Dusers').hide();
    }
  });
  // 选择发起人
  $('#Dcreators').click(function (e) {
    e.target.title = '选择发起人';
    bFlowActions(1, e, goForm);
  });
  // 选择参与人
  $('#Dusers').click(function (e) {
    e.target.title = '选择参与人';
    bFlowActions(2, e, goForm);
  });
  // 选择督办人
  $('#Dmonitors').click(function (e) {
    e.target.title = '选择督办人';
    bFlowActions(3, e, goForm);
  });
  // 选择监控者
  $('#Dadmins').click(function (e) {
    e.target.title = '选择监控者';
    bFlowActions(4, e, goForm);
  });
  // 选择阅读者
  $('#Dviewers').click(function (e) {
    e.target.title = '选择阅读者';
    bFlowActions(21, e, goForm);
  });

  setUnitXMLFieldValue(goForm, flowProperty, ['creator', 'user', 'monitor', 'admin', 'viewer']);

  //办结时保留监控权限、最大权限粒度
  setSelectSingleNode(goForm, flowProperty, ['keepRuntimePermission', 'granularity']);
  $('#granularity').wellSelect({
    searchable: false
  });

  // 流程数据鉴权设置
  switchFun('enableAccessPermissionProvider', null, function (val) {
    if (val === '1') {
      $('.enableAccessPermissionProvider').show();
    } else {
      $('.enableAccessPermissionProvider').hide();
    }
  });
  // 流程数据鉴权设置
  setSwitchFieldValue(goForm, flowProperty, ['enableAccessPermissionProvider']);
  if ($('[name="enableAccessPermissionProvider"]:checked').val() == '1') {
    $('.enableAccessPermissionProvider').show();
  } else {
    $('.enableAccessPermissionProvider').hide();
  }

  var accessPermissionProvider = flowProperty.selectSingleNode('accessPermissionProvider')
    ? flowProperty.selectSingleNode('accessPermissionProvider').text()
    : '';
  goForm.accessPermissionProvider.value = accessPermissionProvider;
  var accessPermissionProviderName = flowProperty.selectSingleNode('accessPermissionProviderName')
    ? flowProperty.selectSingleNode('accessPermissionProviderName').text()
    : '';
  goForm.accessPermissionProviderName.value = accessPermissionProviderName;
  // 流程访问权限提供者
  var providerSetting = {
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
        methodName: 'getFlowAccessPermissionProvider'
      }
    }
  };
  $('#accessPermissionProviderName').comboTree({
    labelField: 'accessPermissionProviderName',
    valueField: 'accessPermissionProvider',
    treeSetting: providerSetting,
    autoInitValue: false,
    width: 530,
    height: 260,
    mutiSelect: true
  });
  // 仅通过接口鉴权（流程权限设置不再生效）
  setSelectSingleNode(goForm, flowProperty, ['onlyUseAccessPermissionProvider']);
}

//流程属性->流程计时->表单字段初始化
function flowTimerProperty(goForm, flowProperty) {
  var _timer = goWorkFlow.flowXML.selectNodes('./timers/timer');
  var _timerNames = [];
  if (_timer != null && _timer.length > 0) {
    _timer.each(function () {
      var _timerNode = $(this);
      if (!_timerNode.selectSingleNode('name')) {
        return true;
      }
      var _timeName = _timerNode.selectSingleNode('name').text();
      var _html =
        '<li data-name="' +
        _timeName +
        '">' +
        '<div class="name">' +
        _timeName +
        '</div>' +
        '<div class="btn-group">' +
        '<div class="well-btn w-btn-primary well-btn-sm w-noLine-btn btn-set" title="设置">' +
        '<i class="iconfont icon-ptkj-shezhi"></i>' +
        '</div>' +
        '<div class="well-btn w-btn-primary well-btn-sm w-noLine-btn btn-remove" title="删除">' +
        '<i class="iconfont icon-ptkj-shanchu"></i>' +
        '</div>' +
        '</div>' +
        '</li>';
      _timerNames.push(_timeName);
      $('#timerList').append(_html);
    });
  }
  $('#timerList').on('click', 'li .btn-set', function () {
    var $this = $(this);
    var _name = $this.closest('li').attr('data-name');
    openTimerDialog(goForm, flowProperty, _timerNames, _name);
  });
  $('#timerList').on('click', 'li .btn-remove', function () {
    var $this = $(this);
    var _name = $this.closest('li').attr('data-name');
    $this.closest('li').remove();
    var _timer = goWorkFlow.flowXML.selectSingleNode("./timers/timer[name='" + _name + "']");
    if (_timer != null) {
      _timer.remove();
    }
  });
  $('#addTimer').on('click', function () {
    openTimerDialog(goForm, flowProperty, _timerNames);
  });

  $('.moreProperty').on('click', '.text', function () {
    var $this = $(this);
    $this.toggleClass('open');
    $this.parent().siblings('.morePropertyWrap').toggle();
  });

  var timerListener = flowProperty.selectSingleNode('timerListener') ? flowProperty.selectSingleNode('timerListener').text() : '';
  goForm.timerListener.value = timerListener;
  var timerListenerName = flowProperty.selectSingleNode('timerListenerName')
    ? flowProperty.selectSingleNode('timerListenerName').text()
    : '';
  goForm.timerListenerName.value = timerListenerName;

  // 计时器事件监听
  var timerListernerSetting = {
    view: {
      showIcon: false,
      showLine: false
    },
    async: {
      otherParam: {
        serviceName: 'flowSchemeService',
        methodName: 'getTimerListeners'
      }
    }
  };
  $('#timerListenerName').comboTree({
    labelField: 'timerListenerName',
    valueField: 'timerListener',
    treeSetting: timerListernerSetting,
    autoInitValue: false,
    width: '100%',
    height: 260
  });
}

//流程属性->高级设置->表单字段初始化
function flowMoreProperty(goForm, flowProperty) {
  loadFlowMoreProperty(goForm, flowProperty);
  initFlowMoreProperty(goForm, flowProperty);
}

function loadFlowMoreProperty(goForm, flowProperty) {
  //设置为等价流程
  switchFun('EQFlowSwitch', goForm, function (val) {
    if (val === '1') {
      top.appModal.confirm('启用等价流程将清空现有流程图，是否启用？', function (result) {
        if (result) {
          $('#EQFlowSwitch', goForm).next().show().parent().next().show();
          top.equalSwitch(true);
        } else {
          $('#EQFlowSwitch', goForm).trigger('click');
        }
      });
    } else {
      $('#EQFlowSwitch').next().hide().parent().next().hide();
      top.equalSwitch(false);
    }
  });

  $('#EQFlowID').wSelect2({
    serviceName: 'flowSchemeService',
    valueField: 'EQFlowID',
    labelField: 'EQFlowName',
    defaultBlank: false
  });
  //设置为自由流程
  switchFun('isFree', null, function (val) {
    if (val === '1') {
      $('#isFree').next().show();
    } else {
      $('#isFree').next().hide();
    }
  });

  // 是否使用默认组织
  $('input[name="useDefaultOrg"]', goForm).on('change', function () {
    var val = $(this).val();
    if (val == "1") {
      $('.useDefaultOrg', goForm).hide();
    } else {
      $('.useDefaultOrg', goForm).show();
    }
  });
  // 加载默认组织提示
  $.ajax({
    url: ctx + '/proxy/api/org/organization/getDefaultOrgBySystem',
    type: 'get',
    dataType: 'json',
    success: function (result) {
      if (result.code === 0) {
        $("label[for='useDefaultOrg_1']").attr("title", "默认组织：" + result.data.name);
      }
    }
  });

  var userDetail = SpringSecurityUtils.getUserDetails();
  // 选择的组织
  $('#orgId').wSelect2({
    serviceName: 'organizationService',
    queryMethod: 'loadSelectData',
    valueField: 'orgId',
    params: {
      unitId: userDetail['systemUnitId'],
      status: '1'
    },
    multiple: false,
    remoteSearch: false
  });

  // 多组织审批
  switchFun('enableMultiOrg', null, function (val) {
    if (val === '1') {
      $('#enableMultiOrg', goForm).next().show();
      $('.enableMultiOrg', goForm).show();
    } else {
      $('#enableMultiOrg', goForm).next().hide();
      $('.enableMultiOrg', goForm).hide();
    }
  });
  $('#multiOrgId').wSelect2({
    serviceName: 'organizationService',
    queryMethod: 'loadSelectData',
    valueField: 'multiOrgId',
    params: {
      unitId: userDetail['systemUnitId'],
      status: '1'
    },
    multiple: true,
    remoteSearch: false
  });


  $('#multiJobFlowType')
    .wellSelect({
      data: [
        {
          id: 'flow_by_user_all_jobs',
          text: '一人多职时按全部部门或职位流转'
        },
        {
          id: 'flow_by_user_main_job',
          text: '一人多职时按主部门或职位流转'
        },
        {
          id: 'flow_by_user_select_job',
          text: '一人多职时按每次选择的部门或职位流转'
        }
      ],
      searchable: false,
      defaultValue: 'flow_by_user_all_jobs'
    })
    .on('change', function () {
      var _value = $(this).val();
      if (_value === 'flow_by_user_select_job') {
        $('.jobFieldBlock').show();
      } else {
        $('.jobFieldBlock').hide();
      }
    });

  //职位选择字段
  var jobFieldNode = flowProperty.selectSingleNode('jobField');
  var jobField = jobFieldNode ? jobFieldNode.text() : '';
  $('#jobField').val(jobField);

  $('#jobField').wSelect2({
    valueField: 'jobField',
    remoteSearch: true,
    defaultBlank: true,
    serviceName: 'flowSchemeService',
    queryMethod: 'getFormFieldSelections',
    params: {
      formUuid: getFormID(),
      inputMode: '41'
    }
  });

  $('#DBackUser').on('click', 'li .btn-remove', function () {
    $(this).closest('li').remove();
  });
  $('#addBackUser').on('click', function () {
    var liLen = $('#DBackUser > li').length ? $('#DBackUser > li:last').attr('data-index') : 0;
    $('#DBackUser').append(getBackUserHtml(++liLen));
    //A岗、B岗
    $('#DAUser_' + liLen).on('click', function () {
      SetUnit('AUser', '人员选择', goForm, 'DAUser_' + liLen, 'AUser_' + liLen, 'AUserID_' + liLen);
    });
    $('#BUser_' + liLen).on('click', function () {
      SetUnit('BUser', '人员选择', goForm, 'BUser_' + liLen, 'BUsers_' + liLen, 'BUserIDs_' + liLen);
    });
  });

  //消息分发
  $('#DMessageTemplate').on('click', 'li .btn-set', function () {
    var data = $(this).closest('li').data('template');
    openMessageTemplateDialog(goForm, flowProperty, data);
  });
  $('#DMessageTemplate').on('click', 'li .btn-remove', function () {
    $(this).closest('li').remove();
  });
  $('#addMessageTemplate').on('click', function () {
    var index = $('#DMessageTemplate > li').length ? $('#DMessageTemplate > li:last').attr('data-index') : 0;
    openMessageTemplateDialog(goForm, flowProperty, null, parseInt(index) + 1);
  });

  //信息记录
  $('#DRecord').on('click', 'li .btn-set', function () {
    var data = $(this).closest('li').data('record');
    openDRecordDialog(goForm, flowProperty, data);
  });
  $('#DRecord').on('click', 'li .btn-remove', function () {
    $(this).closest('li').remove();
  });
  $('#addDRecord').on('click', function () {
    var index = $('#DRecord > li').length ? $('#DRecord > li:last').attr('data-index') : 0;
    openDRecordDialog(goForm, flowProperty, null, index);
  });

  $('#FlowStates').on('click', function () {
    openFlowStatesDialog(goForm, flowProperty);
  });

  //签署意见校验设置
  $('#addOpinionCheckSet').on('click', function () {
    // top.appModal.info('签署意见校验设置-开发中！');
    openOpinionCheckSetDialog(goForm, flowProperty);
  });

  var customJsModule = flowProperty.selectSingleNode('customJsModule') ? flowProperty.selectSingleNode('customJsModule').text() : '';
  goForm.customJsModule.value = customJsModule;
  // 加载的JS模块
  $('#customJsModule').wSelect2({
    serviceName: 'appJavaScriptModuleMgr',
    params: {
      dependencyFilter: 'WorkView'
    },
    labelField: 'customJsModuleName',
    valueField: 'customJsModule',
    defaultBlank: true,
    remoteSearch: false
  });

  var listener = flowProperty.selectSingleNode('listener') ? flowProperty.selectSingleNode('listener').text() : '';
  goForm.listener.value = listener;
  var listenerName = flowProperty.selectSingleNode('listenerName') ? flowProperty.selectSingleNode('listenerName').text() : '';
  goForm.listenerName.value = listenerName;

  // 事件监听
  var listernerSetting = {
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
        methodName: 'getFlowListeners'
      }
    }
  };
  $('#listenerName').comboTree({
    labelField: 'listenerName',
    valueField: 'listener',
    treeSetting: listernerSetting,
    autoInitValue: false,
    width: 530,
    height: 260,
    mutiSelect: true
  });

  var globalTaskListener = flowProperty.selectSingleNode('globalTaskListener')
    ? flowProperty.selectSingleNode('globalTaskListener').text()
    : '';
  goForm.globalTaskListener.value = globalTaskListener;
  var globalTaskListenerName = flowProperty.selectSingleNode('globalTaskListenerName')
    ? flowProperty.selectSingleNode('globalTaskListenerName').text()
    : '';
  goForm.globalTaskListenerName.value = globalTaskListenerName;

  // 全局环节事件监听
  var globalTaskListenerSetting = {
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
  $('#globalTaskListenerName').comboTree({
    labelField: 'globalTaskListenerName',
    valueField: 'globalTaskListener',
    treeSetting: globalTaskListenerSetting,
    autoInitValue: false,
    width: 530,
    height: 260,
    mutiSelect: true
  });

  $('#eventScripts').on('click', 'li .btn-set', function () {
    var data = $(this).closest('li').data('script');
    openEventScript(goForm, flowProperty, data);
  });
  $('#eventScripts').on('click', 'li .btn-remove', function () {
    $(this).closest('li').remove();
    $('#addEventScript').show();
  });
  $('#addEventScript').on('click', function () {
    openEventScript(goForm, flowProperty);
  });

  $('input[name="indexType"]').on('change', function () {
    var _value = $(this).val();
    if (_value === 'define') {
      $('.showIndexExp').show();
    } else {
      $('.showIndexExp').hide();
    }
  });
  //todo 标题表达式
  var indexType = flowProperty.selectSingleNode('indexType') == null ? '' : flowProperty.selectSingleNode('indexType').text();
  var indexTitleExps =
    flowProperty.selectSingleNode('indexTitleExps') == null ? '' : flowProperty.selectSingleNode('indexTitleExps').text();
  var indexContentExps =
    flowProperty.selectSingleNode('indexContentExps') == null ? '' : flowProperty.selectSingleNode('indexContentExps').text();
  if (indexType == 'define') {
    $('#indexType2').attr('checked', 'checked').trigger('change');
  } else {
    $('#indexType1').attr('checked', 'checked').trigger('change');
  }
  $('#indexTitleExps').text(indexTitleExps);
  $('#indexContentExps').text(indexContentExps);

  $('#set_index_title_expression').on('click', function () {
    openTitleExpressionDialog(
      goForm,
      flowProperty,
      'indexTitleExps',
      '索引设置',
      '在下方编辑索引表达式，可插入流程内置变量、表单字段和文本。'
    );
  });
  $('#set_index_content_expression').on('click', function () {
    openTitleExpressionDialog(
      goForm,
      flowProperty,
      'indexContentExps',
      '索引设置',
      '在下方编辑索引表达式，可插入流程内置变量、表单字段和文本。'
    );
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

function initFlowMoreProperty(goForm, flowProperty) {
  //等价流程
  if (flowProperty.selectSingleNode('./property/equalFlow/name')) {
    var EQFlowName = flowProperty.selectSingleNode('./property/equalFlow/name').text();
    var EQFlowID = flowProperty.selectSingleNode('./property/equalFlow/id').text();
    if (EQFlowID) {
      $('#EQFlowName').val(EQFlowName);
      $('#EQFlowID').wellSelect('val', EQFlowID);
    } else {
      $('#EQFlowSwitch').trigger('click');
    }
  } else {
    $('#EQFlowSwitch').trigger('click');
  }

  //自由流程
  setSwitchFieldValue(goForm, flowProperty, ['isFree']);
  //应用类型
  $('#apply_id').val(goWorkFlow.flowXML.getAttribute('applyId') == null ? '' : goWorkFlow.flowXML.getAttribute('applyId'));
  // 是否使用系统默认组织
  var useDefaultOrgNode = flowProperty.selectSingleNode('useDefaultOrg');
  var useDefaultOrg = useDefaultOrgNode ? useDefaultOrgNode.text() : '1';
  $("input[name='useDefaultOrg'][value='" + useDefaultOrg + "']").attr("checked", "checked");
  if (useDefaultOrgNode != null && useDefaultOrg == '1') {
    $(".useDefaultOrg", goForm).hide();
  } else {
    $(".useDefaultOrg", goForm).show();
  }
  // 使用组织
  var orgIdNode = flowProperty.selectSingleNode('orgId');
  var orgId = orgIdNode ? orgIdNode.text() : null;
  $('#orgId').wellSelect('val', orgId);
  //多组织审批
  setSwitchFieldValue(goForm, flowProperty, ['enableMultiOrg']);
  if ($('[name="enableMultiOrg"]:checked', goForm).val() == '1') {
    $('.enableMultiOrg', goForm).show();
  } else {
    $('.enableMultiOrg', goForm).hide();
  }
  var multiOrgIdNode = flowProperty.selectSingleNode('multiOrgId');
  var multiOrgId = multiOrgIdNode ? multiOrgIdNode.text() : null;
  $('#multiOrgId').wellSelect('val', multiOrgId);
  //自动使用最新版本
  var autoUpgradeOrgVersionNode = flowProperty.selectSingleNode('autoUpgradeOrgVersion');
  var autoUpgradeOrgVersion = autoUpgradeOrgVersionNode ? autoUpgradeOrgVersionNode.text() : '0';
  $("input[name='autoUpgradeOrgVersion'][value='" + autoUpgradeOrgVersion + "']").attr('checked', 'checked');
  //一人多职流转设置
  var multiJobFlowTypeNode = flowProperty.selectSingleNode('multiJobFlowType');
  var multiJobFlowType = multiJobFlowTypeNode ? multiJobFlowTypeNode.text() : 'flow_by_user_all_jobs';
  $('#multiJobFlowType').wellSelect('val', multiJobFlowType);

  //逾期流程代理
  var bakUsers = flowProperty.selectSingleNode('bakUsers');
  var bakUsersUnits = bakUsers ? bakUsers.selectNodes('unit') : [];
  $.each(bakUsersUnits, function (i, item) {
    var index = i + 1;
    $('#addBackUser').trigger('click');
    var AUserArr = $(item).selectSingleNode('value').text().split('|');
    var BUserArr = $(item).selectSingleNode('argValue').text().split('|');
    $('#AUser_' + index).val(AUserArr[0]);
    $('#AUserID_' + index).val(AUserArr[1]);
    $('#BUsers_' + index).val(BUserArr[0]);
    $('#BUserIDs_' + index).val(BUserArr[1]);
    setUnitFieldValue('DAUser_' + index, goForm, {
      ids: AUserArr[1],
      texts: AUserArr[0]
    });
    setUnitFieldValue('BUser_' + index, goForm, {
      ids: BUserArr[1],
      texts: BUserArr[0]
    });
    var $aEle = $('#DAUser_' + index);
    var aLength = $aEle.find('.org-select li').length;
    if (aLength) {
      $aEle.removeClass('org-select-placeholder');
    } else {
      $aEle.addClass('org-select-placeholder');
    }
    var $bEle = $('#BUser_' + index);
    var bLength = $aEle.find('.org-select li').length;
    if (bLength) {
      $bEle.removeClass('org-select-placeholder');
    } else {
      $bEle.addClass('org-select-placeholder');
    }
  });

  //消息分发
  var messageTemplates = flowProperty.selectSingleNode('messageTemplates');
  var template = messageTemplates ? messageTemplates.selectNodes('template') : [];
  $.each(template, function (i, item) {
    var x2js = new X2JS();
    var newTemplate = x2js.xml2js(item.outerHTML).template;

    if (newTemplate.distributers) {
      newTemplate.distributers = [].concat(newTemplate.distributers.distributer);
      $.each(newTemplate.distributers, function (i, item) {
        if (item.designees) {
          item.designees = formatUnitXMLData(item.designees.unit);
        }
      });
    }

    if (newTemplate.copyMsgRecipients) {
      newTemplate.copyMsgRecipients = formatUnitXMLData(newTemplate.copyMsgRecipients.unit);
    }

    if (newTemplate.distributions) {
      newTemplate.distributions = [].concat(newTemplate.distributions.distribution);
    }
    if (newTemplate.conditions) {
      newTemplate.conditions = [].concat(newTemplate.conditions.condition);
    }
    //新数据字段
    template = newTemplate;

    addMessageTemplate(template, i + 1);
  });

  //新建流程默认增加消息模版
  if (goWorkFlow.isNewFlow) {
    var defaultMessageTemplate = [
      {
        distributers: [
          {
            dtypeName: '办理人',
            id: 'WF_WORK_TODO'
          }
        ],
        isSendMsg: '1',
        type: 'TODO',
        typeName: '流程到达通知'
      },
      {
        distributers: [
          {
            dtypeName: '抄送人员',
            id: 'WF_WORK_COPY'
          }
        ],
        isSendMsg: '1',
        type: 'COPY',
        typeName: '流程到达抄送通知'
      },
      {
        distributers: [
          {
            dtypeName: '督办人员',
            id: 'WF_WORK_SUPERVISE'
          }
        ],
        isSendMsg: '1',
        type: 'SUPERVISE',
        typeName: '督办流程到达通知'
      },
      {
        distributers: [
          {
            dtypeName: '办理人',
            id: 'WF_WORK_COUNTER_SIGN'
          }
        ],
        isSendMsg: '1',
        type: 'COUNTER_SIGN',
        typeName: '会签流程到达通知'
      },
      {
        distributers: [
          {
            dtypeName: '办理人',
            id: 'WF_WORK_TRANSFER'
          }
        ],
        isSendMsg: '1',
        type: 'TRANSFER',
        typeName: '转办流程到达通知'
      },
      {
        distributers: [
          {
            dtypeName: '办理人',
            id: 'WF_WORK_ENTRUST'
          }
        ],
        isSendMsg: '1',
        type: 'ENTRUST',
        typeName: '流程委托到达通知'
      },
      {
        distributers: [
          {
            dtypeName: '办理人',
            id: 'WF_WORK_COUNTER_SIGN_RETURN'
          }
        ],
        isSendMsg: '1',
        type: 'COUNTER_SIGN_RETURN',
        typeName: '会签流程返回通知'
      },
      {
        distributers: [
          {
            dtypeName: '办理人',
            id: 'WF_WORK_ROLL_BACK'
          }
        ],
        isSendMsg: '1',
        type: 'ROLL_BACK',
        typeName: '流程退回通知'
      },
      {
        distributers: [
          {
            dtypeName: '原办理人',
            id: 'WF_WORK_REVOKE'
          }
        ],
        isSendMsg: '1',
        type: 'REVOKE',
        typeName: '流程撤回通知'
      },
      {
        distributers: [
          {
            dtypeName: '办理人',
            id: 'WF_WORK_ALARM_DOING'
          },
          {
            dtypeName: '办理人的上级领导',
            id: 'WF_WORK_ALARM_DOING_SUPERIOR'
          },
          {
            dtypeName: '督办人',
            id: 'WF_WORK_ALARM_SUPERVISE'
          },
          {
            dtypeName: '跟踪人',
            id: 'WF_WORK_ALARM_TRACER'
          },
          {
            dtypeName: '其他人员',
            id: 'WF_WORK_ALARM_OTHER'
          },
          {
            dtypeName: '流程管理员',
            id: 'WF_WORK_ALARM_ADMIN'
          }
        ],
        isSendMsg: '1',
        type: 'FLOW_ALARM',
        typeName: '预警提醒'
      },
      {
        distributers: [
          {
            dtypeName: '办理人',
            id: 'WF_WORK_DUE_DOING'
          },
          {
            dtypeName: '办理人的上级领导',
            id: 'WF_WORK_DUE_DOING_SUPERIOR'
          },
          {
            dtypeName: '督办人',
            id: 'WF_WORK_DUE_SUPERVISE'
          },
          {
            dtypeName: '跟踪人',
            id: 'WF_WORK_DUE_TRACER'
          },
          {
            dtypeName: '其他人员',
            id: 'WF_WORK_DUE_OTHER'
          },
          {
            dtypeName: '流程管理员',
            id: 'WF_WORK_DUE_ADMIN'
          }
        ],
        isSendMsg: '1',
        type: 'FLOW_DUE',
        typeName: '逾期提醒'
      },
      {
        distributers: [
          {
            dtypeName: '当前办理人',
            id: 'WF_WORK_EMPTY_NOTE_DONE'
          }
        ],
        isSendMsg: '1',
        type: 'EMPTY_NOTE_DONE',
        typeName: '办理人为空跳过环节消息通知'
      }
    ];
    $.each(defaultMessageTemplate, function (i, item) {
      addMessageTemplate(item, i + 1);
    });
  }

  //信息格式
  var records = flowProperty.selectSingleNode('records');
  if (records) {
    var record = records.selectNodes('record');
    $.each(record, function (i, item) {
      var name = $(item).selectSingleNode('name').text();
      var field = $(item).selectSingleNode('field').text();
      var $way = $(item).selectSingleNode('way');
      var way = $way ? $way.text() : '';
      var $assembler = $(item).selectSingleNode('assembler');
      var assembler = $assembler ? $assembler.text() : '';
      var $contentOrigin = $(item).selectSingleNode('contentOrigin');
      var contentOrigin = $contentOrigin ? $contentOrigin.text() : '';

      var $ignoreEmpty = $(item).selectSingleNode('ignoreEmpty');
      var ignoreEmpty = $ignoreEmpty ? $ignoreEmpty.text() : '';
      var enableWysiwyg = $(item).selectSingleNode('enableWysiwyg') != null ? $(item).selectSingleNode('enableWysiwyg').text() : '0';
      var fieldNotValidate =
        $(item).selectSingleNode('fieldNotValidate') != null ? $(item).selectSingleNode('fieldNotValidate').text() : '1';
      var enablePreCondition =
        $(item).selectSingleNode('enablePreCondition') != null ? $(item).selectSingleNode('enablePreCondition').text() : '0';
      var value = $(item).selectSingleNode('value').text();
      var $taskIds = $(item).selectSingleNode('taskIds');
      var taskIds = $taskIds ? $taskIds.text() : '';
      var conditions = [];
      var conditionUnitsEle = $(item).selectSingleNode('conditions') ? $(item).selectSingleNode('conditions').find('unit') : [];
      $.each(conditionUnitsEle, function (index, item) {
        conditions.push({
          name: $(item).find('argValue').text(),
          value: $(item).find('value').text(),
          type: $(item).attr('type')
        });
      });
      var data = {
        index: i + 1,
        name: name,
        field: field,
        way: way,
        assembler: assembler,
        contentOrigin: contentOrigin,
        ignoreEmpty: ignoreEmpty,
        enableWysiwyg: enableWysiwyg,
        fieldNotValidate: fieldNotValidate,
        value: value,
        taskIds: taskIds,
        enablePreCondition: enablePreCondition,
        conditions: conditions
      };
      var $li = $('<li data-name="' + name + '" data-index="' + (i + 1) + '"></li>');
      $li.data('record', data);
      $li.append(
        '<div class="name">' +
        name +
        '</div>' +
        '<div class="btn-group">' +
        '<div class="well-btn w-btn-primary well-btn-sm w-noLine-btn btn-set" title="设置">' +
        '<i class="iconfont icon-ptkj-shezhi"></i>' +
        '</div>' +
        '<div class="well-btn w-btn-primary well-btn-sm w-noLine-btn btn-remove" title="删除">' +
        '<i class="iconfont icon-ptkj-shanchu"></i>' +
        '</div>' +
        '</div>'
      );
      $('#DRecord').append($li);
    });
  }

  var printTemplateIdNode = flowProperty.selectSingleNode('printTemplateId');
  var printTemplateId = printTemplateIdNode ? printTemplateIdNode.text() : null;
  var printTemplateNameNode = flowProperty.selectSingleNode('printTemplate');
  var printTemplate = printTemplateNameNode ? printTemplateNameNode.text() : null;
  var printTemplateUuidNode = flowProperty.selectSingleNode('printTemplateUuid');
  var printTemplateUuid = printTemplateUuidNode ? printTemplateUuidNode.text() : null;

  var bOld = printTemplateUuid == null; // 旧版本
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

  $('#printTemplate').val(printTemplate);
  $('#printTemplateId').val(printTemplateId);
  $('#printTemplateUuid').val(printTemplateUuid);

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

  //事件脚本
  var eventScripts = flowProperty.selectSingleNode('eventScripts');
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
        addEventScript(goForm, flowProperty, data);
      }
    });
  }
}

function addMessageTemplate(template, index) {
  var $li = $('<li data-name="' + template.typeName + '" data-index="' + index + '" style="padding-right: 130px"></li>');
  template.index = index;
  $li.data('template', template);
  $li.append(
    '<div class="name">' +
    template.typeName +
    '</div>' +
    '<div class="btn-group">' +
    '<div class="switch-wrap" id="templateUsed_' +
    index +
    '">' +
    '<span class="switch-text switch-open">启用</span>' +
    '<span class="switch-radio" data-status="0"></span>' +
    '<span class="switch-text switch-close">停用</span>' +
    '<input value="1" style="display: none;" class="w-search-option" name="templateUsed_' +
    index +
    '" type="radio">' +
    '<input value="0" style="display: none;" class="w-search-option" name="templateUsed_' +
    index +
    '" type="radio">' +
    '</div>' +
    '<div class="well-btn w-btn-primary well-btn-sm w-noLine-btn btn-set" title="设置">' +
    '<i class="iconfont icon-ptkj-shezhi"></i>' +
    '</div>' +
    '<div class="well-btn w-btn-primary well-btn-sm w-noLine-btn btn-remove" title="删除">' +
    '<i class="iconfont icon-ptkj-shanchu"></i>' +
    '</div>' +
    '</div>' +
    '</li>'
  );
  $('#DMessageTemplate').append($li);
  switchFun('templateUsed_' + index, null, function (val) {
    var $li = $('#templateUsed_' + index).closest('li');
    var _template = $li.data('template');
    if (val === '1') {
      _template.isSendMsg = '1';
    } else {
      _template.isSendMsg = '0';
    }
    $li.data('template', _template);
  });
  if (template.isSendMsg === '1') {
    $('#templateUsed_' + index).trigger('click');
  }
}

function openFlowStatesDialog(goForm, flowProperty) {
  var _html =
    '<form id="flowStatesForm" name="flowStatesForm" class="workflow-popup form-widget">' +
    '<div class="flowStatesTable work-flow-table">' +
    '<div class="t-header clear">' +
    '<div class="t-item t-item-label">流程状态</div>' +
    '<div class="t-item t-item-value">状态名称</div>' +
    '</div>' +
    '<ul>' +
    '</ul>' +
    '</div>' +
    '</form>';
  var $dialog = top.appModal.dialog({
    title: '流程状态',
    message: _html,
    size: 'middle',
    shown: function () {
      var states = [
        {
          code: '0',
          name: '草稿'
        },
        {
          code: '1',
          name: '审批'
        },
        {
          code: '2',
          name: '办结'
        }
      ];
      var flowStates = flowProperty.selectSingleNode('flowStates');
      if (flowStates) {
        var flowState = flowStates.selectNodes('flowState');
        if (flowState) {
          states = $.map(flowState, function (item) {
            return {
              code: $(item).selectSingleNode('code').text(),
              name: $(item).selectSingleNode('name').text()
            };
          });
        }
      }
      $.each(states, function (i, item) {
        $('.flowStatesTable ul', $dialog).append(
          '<li class="clear">' +
          '<div class="t-item t-item-label">' +
          item.code +
          '</div>' +
          '<div class="t-item t-item-value">' +
          '<input class="form-control" placeholder="请输入流程名称" value="' +
          item.name +
          '">' +
          '</div>' +
          '</li>'
        );
      });

      $('input', $dialog).on('input propertychange', function () {
        var $this = $(this);
        var _val = $.trim($this.val());
        if (!_val) {
          $this.addClass('error');
        } else {
          $this.removeClass('error');
        }
      });
    },
    buttons: {
      save: {
        label: '保存',
        className: 'well-btn w-btn-primary',
        callback: function () {
          var isOK = true;
          var states = [];
          $('input', $dialog).each(function (i, item) {
            var $this = $(item);
            var _val = $.trim($this.val());
            if (_val) {
              states.push({
                code: i,
                name: _val
              });
            } else {
              isOK = false;
            }
          });
          if (!isOK) {
            top.appModal.error('请完善流程状态信息！');
            return false;
          }
          var flowStates = flowProperty.selectSingleNode('flowStates');
          if (flowStates) {
            flowStates.remove();
          }
          flowStates = oSetElement(flowProperty, 'flowStates');
          $.each(states, function (i, item) {
            var flowState = oAddElement(flowStates, 'flowState');
            oAddElement(flowState, 'code', item.code);
            oAddElement(flowState, 'name', item.name);
          });
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

function remarkLoadEvent($dialog, goForm, flowProperty, data) {
  var poForm = $('#remarkForm', $dialog)[0];
  if (data) {
    poForm.name.value = data.name;
    poForm.field.value = data.field;
    poForm.way.value = data.way;
    poForm.assembler.value = data.assembler;
    poForm.contentOrigin.value = data.contentOrigin;
    poForm.DFormat.value = data.value;
    poForm.taskIds.value = data.taskIds;
    if (data.ignoreEmpty === '1') {
      poForm.ignoreEmpty.checked = true;
    }
    if (data.enableWysiwyg === '1') {
      poForm.enableWysiwyg.checked = true;
    }
    if (data.fieldNotValidate !== '1') {
      poForm.fieldNotValidate.checked = false;
    }
    var $inputs = $('[name="enablePreCondition"]', $dialog);
    $.each($inputs, function (i, input) {
      if (input.value == data.enablePreCondition) {
        input.checked = true;
      }
      if (data.enablePreCondition == '1') {
        $('.enablePreCondition', $dialog).parent().find('.tip-text').show();
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
      $('#preConditions', $dialog).append($li);
    });
    // $('#preConditions', $dialog).append(html);
  }
  //记录字段
  var formID = goWorkFlow.flowXML.selectSingleNode('./property/formID');
  var formUuid = formID ? formID.text() : goWorkFlow.DformDefinition ? goWorkFlow.DformDefinition.uuid : '';
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
  $('#field', $dialog).comboTree({
    labelField: 'fieldLabel',
    valueField: 'field',
    initService: 'flowSchemeService.getFormFieldByFieldNames',
    initServiceParam: [formUuid],
    treeSetting: setting,
    width: 283,
    height: 230
  });

  $('#assembler', $dialog).wellSelect({
    data: [
      {
        //     id: 'userJobGradeTaskFormOpinionAssembler',
        //     text: '按用户职位级别组装'
        // },{
        id: 'defaultTaskFormOpinionAssembler',
        text: '按时间升序组织'
      },
      {
        id: 'descTaskFormOpinionAssembler',
        text: '按时间降序组织'
      }
    ],
    searchable: false
  });

  //信息格式
  var laNode = goWorkFlow.dictionXML.selectNodes('/diction/formats/format');
  if (laNode != null) {
    var DFormatData = $.map(laNode, function (item) {
      return {
        id: $(item).selectSingleNode('value').text(),
        text: $(item).selectSingleNode('name').text()
      };
    });
    $('#DFormat', $dialog).wellSelect({
      data: DFormatData
    });
  }

  // 意见立场备选项
  var laOptNamMap = {};
  var laOptName = [];
  //记录环节
  var tasksArr = $.map(goWorkFlow.tasks, function (item) {
    if (item.Type === 'TASK') {
      var loNode = $(item.xmlObject).selectSingleNode('optNames');
      if (loNode != null) {
        var laNode = loNode.selectNodes('unit');
        if (laNode != null) {
          for (var i = 0; i < laNode.length; i++) {
            var lsValue = $(laNode[i]).selectSingleNode('value').text();
            var lsText = $(laNode[i]).selectSingleNode('argValue').text();
            laOptNamMap[lsText + '|' + lsValue] = lsText + '|' + lsValue;
          }
        }
      }
      var id = item.xmlObject.context.id;
      var text = item.xmlObject.context.getAttribute('name');
      return {
        id: id,
        text: text,
        desc: id
      };
    }
  });
  for (var key in laOptNamMap) {
    laOptName.push(key);
  }
  var optname = laOptName.join(';');

  $('#taskIds', $dialog).wellSelect({
    data: tasksArr,
    multiple: true,
    chooseAll: true
  });
  // 前置条件
  switchFun('enablePreCondition', $dialog, function (val) {
    if (val === '1') {
      $('.enablePreCondition', $dialog).show();
      $('.enablePreCondition', $dialog).parent().find('.tip-text').show();
    } else {
      $('.enablePreCondition', $dialog).hide();
      $('.enablePreCondition', $dialog).parent().find('.tip-text').hide();
    }
  });
  if ($('[name="enablePreCondition"]:checked', $dialog).val() == '1') {
    $('#enablePreCondition', $dialog).closest('.switch-wrap').addClass('active').find('.switch-radio').data('value', '1');
    $('.enablePreCondition', $dialog).show();
  } else {
    $('.enablePreCondition', $dialog).hide();
  }
  // 添加前置条件
  $('#addPreCondition', $dialog)
    .off()
    .on('click', function () {
      showConditionDialog({
        action: 'add',
        optionData: optname,
        data: null,
        index: null,
        types: ['1', '3', '4', '5', '6'],
        listContainer: $('#preConditions', $dialog),
        title: '添加前置条件'
      });
    });
  // 编辑前置条件
  $('#preConditions', $dialog)
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
          listContainer: $('#preConditions', $dialog),
          title: '前置条件'
        });
      } else {
        $(this).parent().remove();
      }
    });
}

function get_remark_dialog_html() {
  return (
    '<form id="remarkForm" name="remarkForm" class="workflow-popup form-widget">' +
    '    <div class="well-form well-workflow-form form-horizontal">' +
    '        <div class="form-group">' +
    '            <label for="name" class="well-form-label control-label required">信息名称</label>' +
    '            <div class="well-form-control">' +
    '                <input type="text" class="form-control" id="name" name="name">' +
    '            </div>' +
    '        </div>' +
    '        <div class="form-group">' +
    '            <label for="name" class="well-form-label control-label required">记录字段</label>' +
    '            <div class="well-form-control">' +
    '                <input type="hidden" class="form-control" id="fieldLabel" name="fieldLabel">' +
    '                <input type="hidden" class="form-control" id="field" name="field">' +
    '                 <div>' +
    '                    <div class="field-validate-checkbox">' +
    '                     <input type="checkbox" class="form-control" id="fieldNotValidate" name="fieldNotValidate" checked value="1">' +
    '                     <label for="fieldNotValidate">字段不参与表单数据的变更校验</label>' +
    '                    </div>' +
    '                    <div class="w-tooltip">' +
    '                      <i class="iconfont icon-ptkj-tishishuoming"></i>' +
    '                      <div class="w-tooltip-content">选中后，记录字段的字段值发生变更时，不影响整个表单数据的数据变更校验结果</div>' +
    '                    </div>' +
    '                  </div>' +
    '            </div>' +
    '        </div>' +
    '        <div class="form-group">' +
    '            <label for="name" class="well-form-label control-label">记录方式</label>' +
    '            <div class="well-form-control">' +
    '                <input type="radio" name="way" id="way_1" checked value="1">' +
    '                <label class="radio-inline" for="way_1">重复记录时不替换</label>' +
    '                <input type="radio" name="way" id="way_2" value="2">' +
    '                <label class="radio-inline" for="way_2">重复记录时替换</label>' +
    '                <input type="radio" name="way" id="way_3" value="3">' +
    '                <label class="radio-inline" for="way_3">附加</label>' +
    '            </div>' +
    '        </div>' +
    '        <div class="form-group">' +
    '            <label for="name" class="well-form-label control-label">内容组织</label>' +
    '            <div class="well-form-control">' +
    '                <input type="text" class="form-control" id="assembler" name="assembler" value="">' +
    '            </div>' +
    '        </div>' +
    '        <div class="form-group">' +
    '            <label for="name" class="well-form-label control-label">历史内容来源</label>' +
    '            <div class="well-form-control">' +
    '                <input type="radio" name="contentOrigin" id="contentOrigin_1" checked value="1">' +
    '                <label class="radio-inline" for="contentOrigin_1">流程信息记录</label>' +
    '                <input type="radio" name="contentOrigin" id="contentOrigin_2" value="2">' +
    '                <label class="radio-inline" for="contentOrigin_2">表单字段值</label>' +
    '            </div>' +
    '        </div>' +
    '        <div class="form-group">' +
    '            <div class="well-form-control no-left-label">' +
    '                   <input type="checkbox" name="ignoreEmpty" id="ignoreEmpty" value="1">' +
    '                   <label class="checkbox-inline" for="ignoreEmpty">忽略空意见</label>' +
    '            </div>' +
    '        </div>' +
    '        <div class="form-group">' +
    '            <div class="well-form-control no-left-label">' +
    '                   <input type="checkbox" class="form-control" id="enableWysiwyg" name="enableWysiwyg" value="1">' +
    '                   <label for="enableWysiwyg">意见即时显示<span style="margin-left: 3em; color:#ccc">| 记录的信息包含用户签署的意见时，即时显示</span></label>' +
    '            </div>' +
    '        </div>' +
    '        <div class="form-group">' +
    '            <label for="name" class="well-form-label control-label required">信息格式</label>' +
    '            <div class="well-form-control">' +
    '                <input type="hidden" class="form-control" id="DFormat" name="DFormat">' +
    '            </div>' +
    '        </div>' +
    '        <div class="form-group">' +
    '            <label for="name" class="well-form-label control-label">记录环节</label>' +
    '            <div class="well-form-control">' +
    '                <input type="hidden" class="form-control" id="taskNames" name="taskNames">' +
    '                <input type="hidden" class="form-control" id="taskIds" name="taskIds">' +
    '                <div class="tip-text" style="font-size: 12px;color: #999">(为空时默认对所有未配置信息记录的环节生效)</div>' +
    '            </div>' +
    '        </div>' +
    '        <div class="form-group">' +
    '			<label class="well-form-label control-label">前置条件</label>' +
    '			<div class="well-form-control">' +
    '				<div class="switch-box">' +
    '					<div class="switch-wrap" id="enablePreCondition">' +
    '						<span class="switch-text switch-open"></span>' +
    '						<span class="switch-radio" data-status="0"></span>' +
    '						<span class="switch-text switch-close"></span>' +
    '						<input value="1" style="display: none" class="w-search-option" name="enablePreCondition" type="radio" />' +
    '						<input value="0" style="display: none" class="w-search-option" name="enablePreCondition" type="radio" />' +
    '					</div>' +
    '					<div style="display: inline-block; position: relative">' +
    '					<span class="tip-text" style="display: none; position: absolute; top: -10px; width: 15em;">&nbsp;&nbsp;满足以下前置条件时记录信息</span>' +
    '					</div>' +
    '				</div>' +
    '				<div class="multiBox enablePreCondition" style="display: none">' +
    '					<ul id="preConditions" class="work-flow-other-list"></ul>' +
    '					<div id="addPreCondition" class="well-btn w-btn-primary w-noLine-btn well-btn-sm">' +
    '						<i class="iconfont icon-ptkj-jiahao"></i>添加前置条件' +
    '					</div>' +
    '				</div>' +
    '			</div>' +
    '		</div>' +
    '   </div>' +
    '</form>'
  );
}

// 签署意见校验设置-html模板
function get_opinionCheckSet_dialog_html() {
  return (
    '<form id="opinionCheckSetTemplateForm" name="opinionCheckSetTemplateForm" class="workflow-popup form-widget">' +
    '<div class="well-form form-horizontal">' +
    '<div class="form-group" style="min-height: 66px;">' +
    '<div class="" style="margin-bottom: 20px;"><strong>提交签署意见</strong></div>' +
    '<div class="multiBox">' +
    '   <ul class="opinionRuleList" id="sceneS001"></ul>' +
    '   <div  class="addopinionRule well-btn w-btn-primary w-noLine-btn well-btn-sm">' +
    '       <i class="iconfont icon-ptkj-jiahao"></i>添加校验规则</div>' +
    '</div>' +
    '</div>' +
    '<div class="form-group" style="min-height: 66px;">' +
    '<div class="" style="margin-bottom: 20px;"><strong>退回签署意见 </strong></div> ' +
    '<div class="multiBox">' +
    '   <ul class="opinionRuleList" id="sceneS002"></ul>' +
    '   <div  class="addopinionRule well-btn w-btn-primary w-noLine-btn well-btn-sm">' +
    '       <i class="iconfont icon-ptkj-jiahao"></i>添加校验规则</div>' +
    '</div>' +
    '</div>' +
    '<div class="form-group" style="min-height: 66px;">' +
    '<div class="" style="margin-bottom: 20px;"><strong>转办签署意见</strong></div>' +
    '<div class="multiBox">' +
    '   <ul class="opinionRuleList" id="sceneS003"></ul>' +
    '   <div  class="addopinionRule well-btn w-btn-primary w-noLine-btn well-btn-sm">' +
    '       <i class="iconfont icon-ptkj-jiahao"></i>添加校验规则</div>' +
    '</div>' +
    '</div>' +
    '<div class="form-group" style="min-height: 66px;">' +
    '<div class="" style="margin-bottom: 20px;"><strong>会签签署意见</strong></div>' +
    '<div class="multiBox">' +
    '   <ul class="opinionRuleList" id="sceneS004"></ul>' +
    '   <div  class="addopinionRule well-btn w-btn-primary w-noLine-btn well-btn-sm">' +
    '       <i class="iconfont icon-ptkj-jiahao"></i>添加校验规则</div>' +
    '</div>' +
    '</div>' +
    '</div>' +
    '</form>'
  );
}

function get_messageTemplate_dialog_html() {
  return (
    '<form id="messageTemplateForm" name="messageTemplateForm" class="workflow-popup form-widget">' +
    '<div class="well-form form-horizontal">' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label required">消息类型</label>' +
    '<div class="well-form-control">' +
    '<input type="hidden" class="form-control" id="type" name="type">' +
    '<input type="hidden" class="form-control" id="typeName" name="typeName">' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label required">分发人员</label>' +
    '<div class="well-form-control">' +
    '<div id="templateIdWrap">' +
    '</div>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label">抄送人员</label>' +
    '<div class="well-form-control">' +
    '<div class="org-select-container org-style3 org-select-placeholder" data-id="extraMsgRecipient" id="DextraMsgRecipients">' +
    '<input type="hidden" id="DextraMsgRecipient1">' +
    '<input type="hidden" id="extraMsgRecipient1">' +
    '<input type="hidden" id="extraMsgRecipient2">' +
    '<input type="hidden" id="extraMsgRecipient4">' +
    '<input type="hidden" id="extraMsgRecipient8">' +
    '<input type="hidden" id="DextraMsgRecipient16">' +
    '<input type="hidden" id="extraMsgRecipient16">' +
    '<ul class="org-select"></ul>' +
    '<i class="icon iconfont icon-ptkj-zuzhixuanze"></i>' +
    '</div>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label">分发节点</label>' +
    '<div class="well-form-control">' +
    '<div class="multiBox">' +
    '<ul id="distributeNodeList">' +
    '</ul>' +
    '<div id="addDistributeNodeTypes" class="well-btn w-btn-primary w-noLine-btn well-btn-sm">' +
    '<i class="iconfont icon-ptkj-jiahao"></i>添加分发节点' +
    '</div>' +
    '</div>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label">分发条件</label>' +
    '<div class="well-form-control">' +
    '<div class="switch-wrap" id="distributeCondition">' +
    '<span class="switch-text switch-open" style=""></span>' +
    '<span class="switch-radio" data-status="0"></span>' +
    '<span class="switch-text switch-close" style="display: none;"></span>' +
    '<input value="1" style="display: none;" class="w-search-option" id="distributeCondition_true" name="distributeCondition" type="radio">' +
    '<input value="0" style="display: none;" class="w-search-option" id="distributeCondition_false" name="distributeCondition" type="radio">' +
    '</div>' +
    '<div class="distributeConditionType-wrap">' +
    '<span>满足以下</span>' +
    '<div class="control">' +
    '<input class="form-control" name="distributeConditionType" id="distributeConditionType">' +
    '</div>' +
    '<span>条件时分发消息</span>' +
    '</div>' +
    '</div>' +
    '</div>' +
    '<div class="form-group distributeConditionType-list">' +
    '<div class="well-form-control no-left-label">' +
    '<div class="multiBox">' +
    '<ul id="distributeConditionList">' +
    '</ul>' +
    '<div id="addDistributeCondition" class="well-btn w-btn-primary w-noLine-btn well-btn-sm">' +
    '<i class="iconfont icon-ptkj-jiahao"></i>添加分发条件' +
    '</div>' +
    '</div>' +
    '</div>' +
    '</div>' +
    '<div class="form-group">' +
    '<label class="well-form-label control-label">是否启用</label>' +
    '<div class="well-form-control">' +
    '<div class="switch-wrap active" id="isSendMsg">' +
    '<span class="switch-text switch-open"></span>' +
    '<span class="switch-radio" data-status="1"></span>' +
    '<span class="switch-text switch-close"></span>' +
    '<input value="1" style="display: none;" checked class="w-search-option" id="isSendMsg_true" name="isSendMsg" type="radio">' +
    '<input value="0" style="display: none;" class="w-search-option" id="isSendMsg_false" name="isSendMsg" type="radio">' +
    '</div>' +
    '</div>' +
    '</div>' +
    '</div>' +
    '</form>'
  );
}

//签署意见校验设置-弹窗
function openOpinionCheckSetDialog(goForm, flowProperty) {
  var _html = get_opinionCheckSet_dialog_html();
  var $dialog = top.appModal.dialog({
    title: '签署意见校验设置',
    message: _html,
    height: '600px',
    size: 'large',
    shown: function () {
      openOpinionCheckSetLoadEvent($dialog, goForm, flowProperty);
    },
    buttons: {
      save: {
        label: '保存',
        className: 'well-btn w-btn-primary',
        callback: function () {
          var opinionCheckSetValues = [];
          //收集数据
          var isSuccess = true;
          $.each($('#opinionCheckSetTemplateForm', this).find('.opinionRuleList'), function (i, item) {
            var id = $(item).attr('id');
            var opinionRuleUuid = '';
            var taskIds = '';
            if ($(item).find('.item_li').length > 0) {
              $.each($(item).find('.item_li'), function (i, item1) {
                opinionRuleUuid = $(item1).find('.opinionRuleUuid').val();
                taskIds = $(item1).find('.taskIds').val();
                var obj = {
                  id: id,
                  opinionRuleUuid: opinionRuleUuid,
                  taskIds: taskIds
                };
                if (opinionRuleUuid == '') {
                  top.appModal.error('校验规则不能为空！');
                  isSuccess = false;
                  return;
                }
                opinionCheckSetValues.push(obj);
              });
            }
          });
          //收集数据失败 ，不能保存
          if (!isSuccess) {
            return false;
          }
          var opinionCheckSets = flowProperty.selectSingleNode('opinionCheckSets');
          if (opinionCheckSets) {
            opinionCheckSets.remove();
          }
          opinionCheckSets = oSetElement(flowProperty, 'opinionCheckSets');
          $.each(opinionCheckSetValues, function (i, item) {
            var opinionCheckSet = oAddElement(opinionCheckSets, 'opinionCheckSet');
            opinionCheckSet.attr('id', item.id);
            opinionCheckSet.attr('opinionRuleUuid', item.opinionRuleUuid);
            opinionCheckSet.attr('taskIds', item.taskIds);
          });
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

function openMessageTemplateDialog(goForm, flowProperty, data, index) {
  var _html = get_messageTemplate_dialog_html();
  var $dialog = top.appModal.dialog({
    title: '消息分发',
    message: _html,
    height: '600px',
    size: 'large',
    shown: function () {
      messageTemplateLoadEvent($dialog, goForm, flowProperty, data);
      messageTemplateInitEvent($dialog, goForm, flowProperty, data, index);
    },
    buttons: {
      save: {
        label: '保存',
        className: 'well-btn w-btn-primary',
        callback: function () {
          var poForm = $('#messageTemplateForm', $dialog)[0];
          var distributers = [];
          var ispass = true;
          $('[name="templateU"]:checked', $dialog).each(function () {
            var $this = $(this);
            var val = $this.val();
            var templateIdData = $('[data-type="' + val + '"]', $dialog).wellSelect('data');
            if (!templateIdData.id) {
              top.appModal.error('请选择' + val + '的消息格式！');
              ispass = false;
              return false;
            }
            var designees = null;
            if ($this.closest('.controls').find('.org-select-container').length) {
              designees = {
                unitLabel: poForm.DtemplateUser1.value !== '' ? poForm.DtemplateUser1.value.split(';') : [],
                unitValue: poForm.templateUser1.value !== '' ? poForm.templateUser1.value.split(';') : [],
                formField: poForm.templateUser2.value !== '' ? poForm.templateUser2.value.split(';') : [],
                tasks: poForm.templateUser4.value !== '' ? poForm.templateUser4.value.split(';') : [],
                options: poForm.templateUser8.value !== '' ? poForm.templateUser8.value.split(';') : [],
                userCustom: []
              };
              designees.userCustom[0] = poForm.DtemplateUser16.value !== '' ? poForm.DtemplateUser16.value : '';
              designees.userCustom[1] = poForm.templateUser16.value !== '' ? poForm.templateUser16.value : '';
            }
            distributers.push({
              dtypeName: $(this).val(),
              id: templateIdData.id,
              name: templateIdData.text,
              designees: designees
            });
          });
          if (!ispass) {
            return false;
          }
          if (!distributers.length) {
            top.appModal.error('请选择分发人员！');
            return false;
          }
          //指定人员
          if ($('#DtemplateUsers', $dialog).length > 0) {
            if (!$('#DtemplateUsers', $dialog).find('.org-select').html()) {
              top.appModal.error('请选择分发人员！');
              return false;
            }
          }
          var distributions = [];
          $('#distributeNodeList > li', $dialog).each(function () {
            var $this = $(this);
            var index = $this.attr('data-index');
            var dtype = poForm['distributeNodeType_' + index].value;
            var _value;
            switch (dtype) {
              case 'task':
                _value = poForm['assignNodeCtl_' + index].value;
                break;
              case 'direction':
                _value = poForm['assignDirectionCtl_' + index].value;
                break;
              case 'jumptask':
                var form = poForm['assignTurnFromCtl_' + index].value;
                var to = poForm['assignTurnToCtl_' + index].value;
                _value = [form, to].join(',');
                break;
            }
            distributions.push({
              dtype: dtype,
              value: _value
            });
          });

          var conditionEnable = $('[name="distributeCondition"]:checked', $dialog).val();
          var condExpressionSignal = poForm.distributeConditionType.value;
          var conditions = [];
          $('#distributeConditionList > li', $dialog).each(function () {
            var $this = $(this);
            var index = $this.attr('data-index');
            conditions.push({
              ctype: poForm['distributeConditionType_' + index].value,
              code: poForm['distributeConditionField_' + index].value,
              symbols: poForm['distributeConditionSymbol_' + index].value,
              value: poForm['distributeConditionText_' + index].value
            });
          });

          if (conditionEnable == '1') {
            //遇到true就停止
            var nopass = _.some(conditions, function (item, index) {
              if (item.ctype == 'formField') {
                if (!(item.code && item.symbols) || (item.symbols && !(item.symbols == '10' || item.symbols == '11') && !item.value)) {
                  return true;
                }
              } else if (item.ctype == 'userComment') {
                if (!item.symbols || (item.symbols && !(item.symbols == '10' || item.symbols == '11') && !item.value)) {
                  return true;
                }
              } else {
                return true;
              }
            });
            if (nopass) {
              top.appModal.error('请完善分发条件！');
              return false;
            }
          }

          var copyMsgRecipients = {
            unitLabel: poForm.DextraMsgRecipient1.value !== '' ? poForm.DextraMsgRecipient1.value.split(';') : [],
            unitValue: poForm.extraMsgRecipient1.value !== '' ? poForm.extraMsgRecipient1.value.split(';') : [],
            formField: poForm.extraMsgRecipient2.value !== '' ? poForm.extraMsgRecipient2.value.split(';') : [],
            tasks: poForm.extraMsgRecipient4.value !== '' ? poForm.extraMsgRecipient4.value.split(';') : [],
            options: poForm.extraMsgRecipient8.value !== '' ? poForm.extraMsgRecipient8.value.split(';') : [],
            userCustom: []
          };
          copyMsgRecipients.userCustom[0] = poForm.DextraMsgRecipient16.value !== '' ? poForm.DextraMsgRecipient16.value : '';
          copyMsgRecipients.userCustom[1] = poForm.extraMsgRecipient16.value !== '' ? poForm.extraMsgRecipient16.value : '';

          var template = {
            type: poForm.type.value, //消息类型id
            typeName: $('#type', $dialog).wellSelect('data').text, //消息类型text
            distributers: distributers, //分发人员
            copyMsgRecipients: copyMsgRecipients, //抄送人员
            distributions: distributions, //分发节点
            conditionEnable: conditionEnable, //分发条件
            condExpressionSignal: condExpressionSignal,
            conditions: conditions,
            isSendMsg: $('[name="isSendMsg"]:checked', $dialog).val() //是否启用
          };
          if (data) {
            template.index = data.index;
            $('#DMessageTemplate li[data-index="' + template.index + '"]')
              .attr('data-name', template.typeName)
              .data('template', template)
              .find('.name')
              .text(template.typeName);
            setSingleSwitchFieldValue(goForm, 'templateUsed_' + template.index, template.isSendMsg);
          } else {
            addMessageTemplate(template, index);
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

function renderMessageTemplateControl($dialog, poForm, types) {
  $('#templateIdWrap', $dialog).empty();
  $.each(types, function (i, item) {
    var _label = item.label;
    var _html =
      '<div class="controls"><div>' +
      '<input type="checkbox" name="templateU" id="templateU_' +
      i +
      '" value="' +
      _label.split('|')[0] +
      '">';
    if (_label.indexOf('|other') > -1) {
      _html +=
        '<label class="checkbox-inline" for="templateU_' +
        i +
        '">' +
        _label.split('|')[0] +
        '</label></div>' +
        '<div class="multiBox">' +
        '<div class="inner-form-group" style="margin-top: 0;margin-bottom: 5px">' +
        '<label class="well-form-label control-label">选择人员</label>' +
        '<div class="org-select-container org-style3 org-select-placeholder" data-id="templateUser" id="DtemplateUsers">' +
        '<input type="hidden" id="DtemplateUser1">' +
        '<input type="hidden" id="templateUser1">' +
        '<input type="hidden" id="templateUser2">' +
        '<input type="hidden" id="templateUser4">' +
        '<input type="hidden" id="templateUser8">' +
        '<input type="hidden" id="DtemplateUser16">' +
        '<input type="hidden" id="templateUser16">' +
        '<ul class="org-select"></ul>' +
        '<i class="icon iconfont icon-ptkj-zuzhixuanze"></i>' +
        '</div>' +
        '</div>';
    } else {
      _html += '<label class="checkbox-inline" for="templateU_' + i + '">' + _label + '</label></div><div class="multiBox">';
    }
    _html +=
      '<div class="inner-form-group" style="margin-top: 0">' +
      '<label class="well-form-label control-label">消息格式</label>' +
      '<input type="text" class="form-control templateId" data-type="' +
      _label.split('|')[0] +
      '" value="' +
      item.defaultValue +
      '" id="templateId_' +
      i +
      '" name="templateId_' +
      i +
      '">' +
      '</div>' +
      '</div></div>';
    $('#templateIdWrap', $dialog).append(_html);
  });
}

//打开弹窗后加载数据后相关事件
function openOpinionCheckSetLoadEvent($dialog, goForm, flowProperty) {
  var poForm = $('#opinionCheckSetTemplateForm', $dialog)[0];
  var flowId = goWorkFlow.flowXML.attr('id');
  var opinionRuleTemplates = [];
  var taskIdsTemplates = [
    {
      id: 'all',
      text: '全部'
    }
  ];
  var taskIds = [];
  JDS.call({
    service: 'opinionRuleFacadeService.getCurrentUserBelongOpinionRuleList',
    data: [flowId],
    async: false,
    success: function (result) {
      opinionRuleTemplates = $.map(result.data, function (item) {
        return {
          id: item.uuid,
          text: item.opinionRuleName
        };
      });
    },
    error: function () { }
  });
  if (flowId != null && flowId != undefined && flowId.length > 0) {
    JDS.call({
      service: 'flowSchemeService.getFlowTasks',
      data: [flowId],
      async: false,
      success: function (result) {
        var datas = result.data;
        $.each(datas, function (i, item) {
          var itemObj = {
            id: item.id,
            text: item.name
          };
          taskIds.push(item.id);
          taskIdsTemplates.push(itemObj);
        });
      }
    });
  } else {
    //记录环节
    $.each(goWorkFlow.tasks, function (i, item) {
      if (item.Type === 'TASK') {
        var id = item.xmlObject.context.id;
        var text = item.xmlObject.context.getAttribute('name');
        var itemObj = {
          id: id,
          text: text
        };
        taskIds.push(item.id);
        taskIdsTemplates.push(itemObj);
      }
    });
  }
  var opinionCheckSets = flowProperty.selectSingleNode('opinionCheckSets');
  if (
    opinionCheckSets != null &&
    opinionCheckSets.selectNodes('opinionCheckSet') &&
    opinionCheckSets.selectNodes('opinionCheckSet').length > 0
  ) {
    //加载数据渲染
    $.each(opinionCheckSets.selectNodes('opinionCheckSet'), function (i, item) {
      var item_id = $(item).attr('id');
      var item_opinionRuleUuid = $(item).attr('opinionRuleUuid');
      var item_taskIds = $(item).attr('taskIds');
      addOpenOpinionCheckSetItem($dialog, $dialog.find('#' + item_id), opinionRuleTemplates, taskIdsTemplates, taskIds);
      var dom = $dialog.find('#' + item_id).find('> li:last-child');
      $dialog.find(dom).find('.opinionRuleUuid').wellSelect('val', item_opinionRuleUuid);
      $dialog.find(dom).find('.taskIds').wellSelect('val', item_taskIds);
    });
  }

  //添加校验规则按钮事件
  $dialog.find('#opinionCheckSetTemplateForm .addopinionRule').on('click', function () {
    addOpenOpinionCheckSetItem($dialog, $(this).parent().find('.opinionRuleList'), opinionRuleTemplates, taskIdsTemplates, taskIds);
    var item_taskIds = 'all';
    var dom = $(this).parent().find('.opinionRuleList').find('> li:last-child');
    $dialog.find(dom).find('.taskIds').wellSelect('val', item_taskIds);
  });
}

/**
 * //添加新的规则选择项
 * @param $dialog
 * @param $dom dialog下的u标签dom
 * @param opinionRuleTemplates 校验规则选择项备选项
 * @param taskIdsTemplates 应用环节规则选择项备选项
 * @param taskIds 除了all的应用环节规则选择项Id集合
 */
function addOpenOpinionCheckSetItem($dialog, $dom, opinionRuleTemplates, taskIdsTemplates, taskIds) {
  var html = openOpinionCheckSetItemHtml();
  $dom.append(html);
  var dom = $dom.find('> li:last-child');

  //校验规则选择项
  $dialog.find(dom).find('.opinionRuleUuid').wellSelect({
    data: opinionRuleTemplates,
    searchable: true,
    remoteSearch: false
  });
  //应用环节规则选择项
  $dialog
    .find(dom)
    .find('.taskIds')
    .wellSelect({
      data: taskIdsTemplates,
      searchable: true,
      remoteSearch: false,
      multiple: true,
      showEmpty: false
    })
    .on('change.wellSelect', function () {
      setTimeout(() => {
        var data = $dialog.find(this).wellSelect('data');
        if (data.some(result => result.id === 'all')) {
          $dialog.find(this).wellSelect('val', ['all']).wellSelect('setOptionsStatus', taskIds, false);
        } else {
          $dialog.find(this).wellSelect('setOptionsStatus', taskIds, true);
        }
      }, 100);
    });
  //删除校验项事件
  $(dom).on('click', '.btn-remove', function () {
    $(this).parent().parent().remove();
  });
}

//校验规则选择项html
function openOpinionCheckSetItemHtml() {
  return (
    '  <li class="item_li" data-index="0" style="position: relative;margin-bottom: 5px;padding: 8px 40px 8px 7px;background: #f6f6f6;border-radius: 4px;">' +
    '  <div class="name" style="display: inline-block;width: 85%;vertical-align: middle;">' +
    '  <div class="openOpinionCheckDiv" style=" width: 80%;">' +
    '   <div style="display: inline-block;margin-bottom: 15px;margin-right: 10px;"><i class="iconfont icon-oa-renwuguanli" style="color: #488CEE;margin-right: 10px;"></i><font color="red" size="2">*</font>校验规则</div> ' +
    '       <div style="display: inline-block;width: 80%;"><input type="text" name="opinionRuleUuid" class="opinionRuleUuid form-control"/></div> ' +
    '   </div>' +
    '  <div class="openOpinionCheckDiv" style=" width: 80%;">' +
    '    <div style="display: inline-block;margin-bottom: 15px;margin-right: 10px;"><i class="iconfont icon-ptkj-liuchengfangzhen"  style="color: #488CEE;margin-right: 10px;"></i>应用环节</div>  ' +
    '        <div style="display: inline-block;width: 80%;"><input type="text" name="taskIds" class="taskIds form-control" /></div>' +
    '   </div>' +
    '  </div>' +
    '   <div class="btn-group">' +
    '     <div class="well-btn w-btn-primary well-btn-sm w-noLine-btn btn-remove" title="删除">' +
    '     <i class="iconfont icon-ptkj-shanchu"></i>删除' +
    '   </div>' +
    '  </div>' +
    '  </li>'
  );
}

function messageTemplateLoadEvent($dialog, goForm, flowProperty, data) {
  var poForm = $('#messageTemplateForm', $dialog)[0];
  var types = {
    TODO: [
      {
        label: '办理人',
        defaultValue: 'WF_WORK_TODO'
      }
    ],
    COPY: [
      {
        label: '抄送人员',
        defaultValue: 'WF_WORK_COPY'
      }
    ],
    SUPERVISE: [
      {
        label: '督办人员',
        defaultValue: 'WF_WORK_SUPERVISE'
      }
    ],
    COUNTER_SIGN: [
      {
        label: '办理人',
        defaultValue: 'WF_WORK_COUNTER_SIGN'
      }
    ],
    TRANSFER: [
      {
        label: '办理人',
        defaultValue: 'WF_WORK_TRANSFER'
      }
    ],
    ENTRUST: [
      {
        label: '办理人',
        defaultValue: 'WF_WORK_ENTRUST'
      }
    ],
    COUNTER_SIGN_RETURN: [
      {
        label: '办理人',
        defaultValue: 'WF_WORK_COUNTER_SIGN_RETURN'
      }
    ],
    ROLL_BACK: [
      {
        label: '办理人',
        defaultValue: 'WF_WORK_ROLL_BACK'
      },
      {
        label: '全部已办人员',
        defaultValue: 'WF_WORK_ROLL_BACK_DONE'
      }
    ],
    REVOKE: [
      {
        label: '原办理人',
        defaultValue: 'WF_WORK_REVOKE'
      }
    ],
    EMPTY_NOTE_DONE: [
      {
        label: '当前办理人',
        defaultValue: 'WF_WORK_EMPTY_NOTE_DONE'
      }
    ],
    READ_RETURN_RECEIPT: [
      {
        label: '上一环节办理人',
        defaultValue: 'WF_WORK_READ_RETURN_RECEIPT'
      }
    ],
    REMIND: [
      {
        label: '办理人',
        defaultValue: 'WF_WORK_REMIND'
      }
    ],
    OVER: [
      {
        label: '流程发起人',
        defaultValue: 'WF_WORK_OVER'
      },
      {
        label: '指定人员|other',
        defaultValue: 'WF_WORK_OVER_NOTIFY_SPECIFIC_USER'
      }
    ],
    FLOW_ALARM: [
      {
        label: '办理人',
        defaultValue: 'WF_WORK_ALARM_DOING'
      },
      {
        label: '办理人的上级领导',
        defaultValue: 'WF_WORK_ALARM_DOING_SUPERIOR'
      },
      {
        label: '督办人',
        defaultValue: 'WF_WORK_ALARM_SUPERVISE'
      },
      {
        label: '跟踪人',
        defaultValue: 'WF_WORK_ALARM_TRACER'
      },
      {
        label: '其他人员',
        defaultValue: 'WF_WORK_ALARM_OTHER'
      },
      {
        label: '流程管理员',
        defaultValue: 'WF_WORK_ALARM_ADMIN'
      }
    ],
    FLOW_DUE: [
      {
        label: '办理人',
        defaultValue: 'WF_WORK_DUE_DOING'
      },
      {
        label: '办理人的上级领导',
        defaultValue: 'WF_WORK_DUE_DOING_SUPERIOR'
      },
      {
        label: '督办人',
        defaultValue: 'WF_WORK_DUE_SUPERVISE'
      },
      {
        label: '跟踪人',
        defaultValue: 'WF_WORK_DUE_TRACER'
      },
      {
        label: '其他人员',
        defaultValue: 'WF_WORK_DUE_OTHER'
      },
      {
        label: '流程管理员',
        defaultValue: 'WF_WORK_DUE_ADMIN'
      }
    ],
    DUE_TURN_OVER_TRUSTEE: [
      {
        label: '逾期流程代理B岗人员',
        defaultValue: 'WF_WORK_DUE_TURN_OVER_TRUSTEE'
      }
    ],
    DUE_TURN_OVER_SUPERVISE: [
      {
        label: '督办人',
        defaultValue: 'WF_WORK_DUE_TURN_OVER_SUPERVISE'
      },
      {
        label: '原办理人',
        defaultValue: 'WF_WORK_DUE_TURN_OVER_SUPERVISE_NOTIFY_DOING'
      }
    ],
    DUE_TURN_OVER_OTHER: [
      {
        label: '原办理人',
        defaultValue: 'WF_WORK_DUE_TURN_OVER_OTHER_NOTIFY_OLD_DOING'
      },
      {
        label: '其他人员',
        defaultValue: 'WF_WORK_DUE_TURN_OVER_OTHER'
      }
    ],
    DUE_RETURN_PREV_TASK: [
      {
        label: '原办理人',
        defaultValue: 'WF_WORK_DUE_RETRUN_PREV_TASK_NOTIFY_OLD_DOING'
      },
      {
        label: '当前办理人',
        defaultValue: 'WF_WORK_DUE_RETRUN_PREV_TASK'
      }
    ],
    DUE_ENTER_NEXT_TASK: [
      {
        label: '原办理人',
        defaultValue: 'WF_WORK_DUE_RETRUN_NEXT_TASK_NOTIFY_OLD_DOING'
      },
      {
        label: '当前办理人',
        defaultValue: 'WF_WORK_DUE_ENTER_NEXT_TASK'
      }
    ],
    NOTIFY_SUB_FLOW_DOING: [
      {
        label: '子流程办理人',
        defaultValue: 'WF_WORK_NOTIFY_SUB_FLOW_DOING'
      }
    ],
    MAIN_SUB_FLOW_RETURN_OVER_OTHER_SUB_FLOW: [
      {
        label: '子流程办理人',
        defaultValue: 'WF_WORK_MAIN_SUB_FLOW_RETURN_OVER_OTHER_SUB_FLOW'
      }
    ],
    SUB_FLOW_REDO: [
      {
        label: '子流程办理人',
        defaultValue: 'WF_WORK_SUB_FLOW_REDO'
      },
      {
        label: '子流程全部已办人员',
        defaultValue: 'WF_WORK_SUB_FLOW_REDO'
      }
    ],
    SUB_FLOW_END: [
      {
        label: '子流程办理人',
        defaultValue: 'WF_WORK_SUB_FLOW_STOP'
      },
      {
        label: '子流程全部已办人员',
        defaultValue: 'WF_WORK_SUB_FLOW_STOP'
      }
    ],
    SUB_FLOW_REMIND: [
      {
        label: '办理人',
        defaultValue: 'WF_WORK_SUB_FLOW_REMIND'
      },
      {
        label: '子流程办理人上级领导',
        defaultValue: 'WF_WORK_SUB_FLOW_REMIND'
      },
      {
        label: '子流程督办人员',
        defaultValue: 'WF_WORK_SUB_FLOW_REMIND'
      },
      {
        label: '子流程跟踪人员',
        defaultValue: 'WF_WORK_SUB_FLOW_REMIND'
      },
      {
        label: '子流程流程管理人员',
        defaultValue: 'WF_WORK_SUB_FLOW_REMIND'
      }
    ],
    SUB_FLOW_TIMELIMIT_MODIFY: [
      {
        label: '子流程办理人',
        defaultValue: 'WF_WORK_SUB_FLOW_TIMELIMIT_MODIFY'
      }
    ],
    TASK_ARRIVE_NOTIFY: [
      {
        label: '指定人员|other',
        defaultValue: 'WF_WORK_TASK_ARRIVE_NOTIFY'
      }
    ],
    TASK_LEAVE_NOTIFY: [
      {
        label: '指定人员|other',
        defaultValue: 'WF_WORK_TASK_LEAVE_NOTIFY'
      }
    ],
    TASK_JUMP_FORWARD: [
      {
        label: '指定人员|other',
        defaultValue: 'WF_WORK_TASK_JUMP_FORWARD_NOTIFY'
      }
    ],
    TASK_SUBMIT_NOTIFY: [
      {
        //环节提交消息通知
        label: '指定人员|other',
        defaultValue: ''
      }
    ],
    TASK_TRANSFER_NOTIFY: [
      {
        //环节转办消息通知
        label: '指定人员|other',
        defaultValue: ''
      }
    ],
    TASK_COUNTERSIGN_NOTIFY: [
      {
        //环节会签消息通知
        label: '指定人员|other',
        defaultValue: ''
      }
    ],
    TASK_RETURN_NOTIFY: [
      {
        //环节退回消息通知
        label: '指定人员|other',
        defaultValue: ''
      }
    ],
    DIRECTION_SEND_MSG: [
      {
        label: '指定人员|other',
        defaultValue: 'WF_WORK_DIRECTION_SEND_MSG'
      }
    ]
  };

  var templates = null;
  $.get({
    url: ctx + '/api/workflow/definition/getMessageTemplates',
    async: false,
    success: function (result) {
      templates = $.map(result.data, function (item) {
        return {
          id: item.id,
          text: item.name
        };
      });
    }
  });

  $.get({
    url: ctx + '/api/workflow/definition/getSelectFlowMessageTemplateType',
    success: function (result) {
      $dialog.on('change', '[name="templateU"]', function () {
        var val = $(this).attr('checked');
        if (val) {
          $(this).closest('.controls').addClass('show-control');
        } else {
          $(this).closest('.controls').removeClass('show-control');
        }
      });
      $('#type', $dialog)
        .wellSelect({
          data: result.data.results,
          isGroup: true
        })
        .on(
          'change',
          _.debounce(function () {
            var val = $(this).val();
            renderMessageTemplateControl($dialog, poForm, types[val]);
            $('[name="templateU"]', $dialog).each(function () {
              var _$this = $(this);
              //环节提交\转办\会签\退回消息通知的分发人员默认选中
              if (
                _$this.val() !== '指定人员' ||
                val == 'TASK_SUBMIT_NOTIFY' ||
                val == 'TASK_TRANSFER_NOTIFY' ||
                val == 'TASK_COUNTERSIGN_NOTIFY' ||
                val == 'TASK_RETURN_NOTIFY'
              ) {
                _$this.attr('checked', true);
                _$this.closest('.controls').addClass('show-control');
              }
            });
            $('.templateId', $dialog).wellSelect({
              data: templates
            });
            reRenderDistributeConditionType($dialog, true);
          }, 100)
        )
        .wellSelect('val', 'TODO');

      if (data) {
        $('#type', $dialog).wellSelect('val', data.type);
        setTimeout(function () {
          //监听#type有防抖时长
          $('[name="templateU"]', $dialog).attr('checked', false).trigger('change');
          $.each(data.distributers, function (i, item) {
            $('[name="templateU"]', $dialog).each(function (j, item2) {
              if ($(item2).val() === item.dtypeName) {
                $(item2).attr('checked', true).closest('.controls').addClass('show-control');
                $('.templateId[data-type="' + item.dtypeName + '"]', $dialog).wellSelect('val', item.id);
                if (item.designees) {
                  renderUnitField(item.designees, poForm, 'templateUser');
                }
              }
            });
          });
        }, 200);
      }
    }
  });

  $dialog.on('click', '#DtemplateUsers', function () {
    SelectUsers('', 'templateUser', '选择消息分发人员', null, poForm, null, '设置消息分发人员');
  });

  $('#DextraMsgRecipients', $dialog).on('click', function () {
    SelectUsers('', 'extraMsgRecipient', '选择消息抄送人员', null, poForm, null, '设置消息抄送人员');
  });

  var distributeNodeType = [
    {
      id: 'all',
      text: '全部'
    },
    {
      id: 'task',
      text: '指定环节'
    },
    {
      id: 'direction',
      text: '指定流向'
    },
    {
      id: 'jumptask',
      text: '环节跳转'
    }
  ];
  var tasksObj = {};
  var tasksArr = $.map(goWorkFlow.tasks, function (item) {
    if (item.Type === 'TASK') {
      var id = item.xmlObject.context.id;
      var text = item.xmlObject.context.getAttribute('name');
      tasksObj[id] = text;
      return {
        id: id,
        text: text,
        desc: id
      };
    }
  });
  var directions = $.map(getDirections(), function (item) {
    var textArr = item.text.split(' | ');
    return {
      id: item.id,
      text: textArr[0],
      desc: textArr[1]
    };
  });

  $('#addDistributeNodeTypes', $dialog).on('click', function () {
    var liLen = $('#distributeNodeList > li', $dialog).length ? $('#distributeNodeList > li:last', $dialog).attr('data-index') : 0;
    var distributeNodeItemHtml = getDistributeNodeItemHtml(++liLen);
    $('#distributeNodeList', $dialog).append(distributeNodeItemHtml);

    $('#distributeNodeType_' + liLen, $dialog)
      .wellSelect({
        data: distributeNodeType,
        searchable: false,
        showEmpty: false,
        defaultValue: 'all'
      })
      .on('change', function () {
        var _val = $(this).val();
        $(this).closest('li').find('.assignCtl').hide();
        switch (_val) {
          case 'task':
            $('#assignNode_' + liLen, $dialog).show();
            break;
          case 'direction':
            $('#assignDirection_' + liLen, $dialog).show();
            break;
          case 'jumptask':
            $('#assignTurn_' + liLen, $dialog).show();
            break;
        }
      });
    $('#assignNodeCtl_' + liLen, $dialog).wellSelect({
      data: tasksArr,
      multiple: true
    });
    $('#assignDirectionCtl_' + liLen, $dialog).wellSelect({
      data: directions,
      multiple: true
    });
    $('#assignTurnFromCtl_' + liLen, $dialog).wellSelect({
      data: tasksArr
    });
    var turnToCtlList = $.map(tasksArr, function (item) {
      return item;
    });
    turnToCtlList.push({
      text: '送结束',
      id: '<EndFlow>'
    });
    $('#assignTurnToCtl_' + liLen, $dialog).wellSelect({
      data: turnToCtlList
    });
  });

  $dialog.on('click', '#distributeNodeList .delete-btn', function () {
    var $this = $(this);
    top.appModal.confirm('确认删除本条分发节点？', function (result) {
      if (result) {
        $this.closest('li').remove();
        var liLen = $('#distributeNodeList > li', $dialog).length;
        if (!liLen) {
          $('#distributeNodeList', $dialog).trigger('click');
        }
      }
    });
  });

  conditionEnableChange($dialog, data && data.conditionEnable);
  switchFun('distributeCondition', $dialog, function (val) {
    conditionEnableChange($dialog, val);
  });

  $('#distributeConditionType', $dialog).wellSelect({
    data: [
      {
        id: 'and',
        text: '全部'
      },
      {
        id: 'or',
        text: '任何'
      }
    ],
    showEmpty: false,
    searchable: false,
    defaultValue: 'and'
  });

  $('#addDistributeCondition', $dialog).on('click', function () {
    var liLen = $('#distributeConditionList > li', $dialog).length
      ? $('#distributeConditionList > li:last', $dialog).attr('data-index')
      : 0;
    var distributeConditionHtml = getDistributeConditionHtml(++liLen);
    $('#distributeConditionList', $dialog).append(distributeConditionHtml);
    reRenderDistributeConditionType($dialog);
    var fieldData = [];
    $.each(goWorkFlow.DformDefinition.fields, function (i, v) {
      fieldData.push({
        id: i,
        text: v.displayName,
        data: v
      });
    });
    $('#distributeConditionField_' + liLen, $dialog).wellSelect({
      data: fieldData
    });
    reRenderDistributeConditionSymbol($dialog, '', liLen);
  });

  $dialog.on('click', '#distributeConditionList .delete-btn', function () {
    var $this = $(this);
    top.appModal.confirm('确认删除本条分发条件？', function (result) {
      if (result) {
        $this.closest('li').remove();
        var liLen = $('#distributeConditionList > li', $dialog).length;
        if (!liLen) {
          $('#distributeConditionList', $dialog).trigger('click');
        }
      }
    });
  });
  switchFun('isSendMsg', $dialog);
}

function messageTemplateInitEvent($dialog, goForm, flowProperty, data, index) {
  if (!data) return;
  var poForm = $('#messageTemplateForm', $dialog)[0];
  var extraMsgRecipientUserData = {
    unitLabel: [],
    unitValue: [],
    formField: [],
    tasks: [],
    options: [],
    userCustom: []
  };
  if (data.extraMsgRecipients) {
    var extraMsgRecipientUserIdArray = data.extraMsgRecipientUserIds.split(',');
    if (extraMsgRecipientUserIdArray.length === 5) {
      extraMsgRecipientUserData.unitLabel = extraMsgRecipientUserIdArray[0] ? extraMsgRecipientUserIdArray[0].split(';') : [];
      extraMsgRecipientUserData.unitValue = extraMsgRecipientUserIdArray[1] ? extraMsgRecipientUserIdArray[1].split(';') : [];
      extraMsgRecipientUserData.formField = extraMsgRecipientUserIdArray[2] ? extraMsgRecipientUserIdArray[2].split(';') : [];
      extraMsgRecipientUserData.tasks = extraMsgRecipientUserIdArray[3] ? extraMsgRecipientUserIdArray[3].split(';') : [];
      extraMsgRecipientUserData.options = extraMsgRecipientUserIdArray[4] ? extraMsgRecipientUserIdArray[4].split(';') : [];
    }
  }
  if (data.extraMsgCustomRecipients) {
    extraMsgRecipientUserData.userCustom.push(data.extraMsgCustomRecipients);
    extraMsgRecipientUserData.userCustom.push(data.extraMsgCustomRecipientUserIds);
  }

  if (data.copyMsgRecipients) {
    extraMsgRecipientUserData = data.copyMsgRecipients;
  }

  renderUnitField(extraMsgRecipientUserData, poForm, 'extraMsgRecipient');

  $.each(data.distributions || [], function (i, item) {
    var index = i + 1;
    $('#addDistributeNodeTypes', $dialog).trigger('click');
    $('#distributeNodeType_' + index, $dialog).wellSelect('val', item.dtype);
    switch (item.dtype) {
      case 'task':
        $('#assignNodeCtl_' + index, $dialog).wellSelect('val', item.value);
        break;
      case 'direction':
        $('#assignDirectionCtl_' + index, $dialog).wellSelect('val', item.value);
        break;
      case 'jumptask':
        var valueArr = item.value.split(',');
        $('#assignTurnFromCtl_' + index, $dialog).wellSelect('val', valueArr[0]);
        $('#assignTurnToCtl_' + index, $dialog).wellSelect('val', valueArr[1]);
        break;
    }
  });

  if (data.conditionEnable === '1') {
    $('#distributeCondition', $dialog).trigger('click');
  }
  $('#distributeConditionType', $dialog).wellSelect('val', data.condExpressionSignal || 'and');

  $.each(data.conditions || [], function (i, item) {
    var index = i + 1;
    $('#addDistributeCondition', $dialog).trigger('click');
    $('#distributeConditionType_' + index, $dialog).wellSelect('val', item.ctype);
    $('#distributeConditionField_' + index, $dialog).wellSelect('val', item.code);
    $('#distributeConditionSymbol_' + index, $dialog).wellSelect('val', item.symbols);
    $('#distributeConditionText_' + index, $dialog).val(item.value);
    reRenderDistributeConditionSymbol($dialog, item.ctype, index);
  });
  if (data.isSendMsg === '0') {
    $('#isSendMsg', $dialog).trigger('click');
  }
}

function conditionEnableChange($dialog, val) {
  if (val == '1') {
    $('.distributeConditionType-wrap', $dialog).show();
    $('.distributeConditionType-list', $dialog).show();
  } else {
    $('.distributeConditionType-wrap', $dialog).hide();
    $('.distributeConditionType-list', $dialog).hide();
  }
}

//消息分发：切换消息类型或者添加分发条件时，重新渲染分发条件下拉框  isclear:是否清空下拉框内不存在的数据
function reRenderDistributeConditionType($dialog, isclear) {
  var selectData = [
    {
      id: 'formField',
      text: '表单字段值'
    }
  ];
  var typeVal = $('#type', $dialog).val(); //消息类型
  if (
    typeVal == 'TASK_SUBMIT_NOTIFY' ||
    typeVal == 'TASK_TRANSFER_NOTIFY' ||
    typeVal == 'TASK_COUNTERSIGN_NOTIFY' ||
    typeVal == 'TASK_RETURN_NOTIFY'
  ) {
    selectData.push({
      id: 'userComment',
      text: '用户意见内容'
    });
  }
  $('#distributeConditionList', $dialog)
    .find('>li')
    .each(function (index, item) {
      var dindex = index + 1;
      //当下拉选项没有用户意见时，选择用户意见清空
      var ctypeVal = $('#distributeConditionType_' + dindex, $dialog).val();
      if (
        _.findIndex(selectData, {
          id: 'userComment'
        }) == -1 &&
        ctypeVal == 'userComment' &&
        isclear
      ) {
        ctypeVal = '';
        $('#distributeConditionField_' + dindex, $dialog)
          .parent()
          .show();
      }
      $('#distributeConditionType_' + dindex, $dialog)
        .wellSelect({
          data: selectData,
          searchable: false
        })
        .on('change', function () {
          var val = $(this).val();
          if (val == 'userComment') {
            $('#distributeConditionField_' + dindex, $dialog)
              .parent()
              .hide();
            $('#distributeConditionField_' + dindex, $dialog).wellSelect('val', '');
          } else {
            $('#distributeConditionField_' + dindex, $dialog)
              .parent()
              .show();
          }
          reRenderDistributeConditionSymbol($dialog, val, dindex);
        })
        .wellSelect('val', ctypeVal);
    });
}

//消息分发：切换分发条件下拉框时，重新渲染分发条件运算符下拉框
function reRenderDistributeConditionSymbol($dialog, typeVal, index) {
  var sVal = $('#distributeConditionSymbol_' + index, $dialog).val();
  var SymbolData = [
    {
      id: '10',
      text: '=='
    },
    {
      id: '11',
      text: '!='
    },
    {
      id: '8',
      text: '>'
    },
    {
      id: '9',
      text: '>='
    },
    {
      id: '12',
      text: '<'
    },
    {
      id: '13',
      text: '<='
    },
    {
      id: '14',
      text: '包含'
    },
    {
      id: '15',
      text: '不包含'
    }
  ];
  if (typeVal == 'userComment') {
    SymbolData = [
      {
        id: '10',
        text: '等于'
      },
      {
        id: '11',
        text: '不等于'
      },
      {
        id: '14',
        text: '包含'
      },
      {
        id: '15',
        text: '不包含'
      }
    ];
  }
  if (
    _.findIndex(SymbolData, {
      id: sVal
    }) == -1
  ) {
    sVal = '';
  }
  $('#distributeConditionSymbol_' + index, $dialog)
    .wellSelect({
      data: SymbolData,
      searchable: false
    })
    .wellSelect('val', sVal);
}

function openDRecordDialog(goForm, flowProperty, data, index) {
  var _html = get_remark_dialog_html();
  var $dialog = top.appModal.dialog({
    title: '信息记录',
    message: _html,
    height: '600px',
    size: 'large',
    shown: function () {
      remarkLoadEvent($dialog, goForm, flowProperty, data);
    },
    buttons: {
      save: {
        label: '保存',
        className: 'well-btn w-btn-primary',
        callback: function () {
          var form = $('#remarkForm', $dialog)[0];
          if (form.name.value === '') {
            top.appModal.error('请输入信息名称！');
            return false;
          }
          if (form.field.value === '') {
            top.appModal.error('请选择记录字段');
            return false;
          }
          if (form.DFormat.value === '') {
            top.appModal.error('请选择信息格式');
            return false;
          }
          var formData = {};
          formData.index = data && data.index;
          formData.name = form.name.value;
          formData.field = form.field.value;
          formData.way = form.way.value;
          formData.assembler = form.assembler.value;
          formData.contentOrigin = form.contentOrigin.value;
          formData.ignoreEmpty = form.ignoreEmpty.checked ? '1' : '0';
          formData.fieldNotValidate = $('#fieldNotValidate', form).prop('checked') ? '1' : '0';
          formData.enableWysiwyg = $('#enableWysiwyg', form).prop('checked') ? '1' : '0';
          formData.enablePreCondition = $('[name="enablePreCondition"]:checked', form).val();
          formData.conditions = [];
          var leftBreakCount = 0;
          var rightBreakCount = 0;
          $('#preConditions  li', $dialog).each(function () {
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
            formData.conditions.push({
              name: conditionName,
              value: conditionData,
              type: $this.attr('logicType')
            });
          });
          if (formData.enablePreCondition == '1' && formData.conditions.length == 0) {
            top.appModal.error('前置条件必填');
            return false;
          }
          if (leftBreakCount != rightBreakCount) {
            top.appModal.error('请检查条件的合法性。');
            return false;
          }

          formData.value = form.DFormat.value;
          formData.taskIds = form.taskIds.value;
          if (formData.index) {
            $('#DRecord li[data-index="' + formData.index + '"]', goForm)
              .attr('data-name', formData.name)
              .data('record', formData)
              .find('.name')
              .text(formData.name);
          } else {
            formData.index = index + 1;
            var $li = $('<li data-name="' + formData.name + '" data-index="' + formData.index + '"></li>');
            $li.data('record', formData);
            $li.append(
              '<div class="name">' +
              formData.name +
              '</div>' +
              '<div class="btn-group">' +
              '<div class="well-btn w-btn-primary well-btn-sm w-noLine-btn btn-set" title="设置">' +
              '<i class="iconfont icon-ptkj-shezhi"></i>' +
              '</div>' +
              '<div class="well-btn w-btn-primary well-btn-sm w-noLine-btn btn-remove" title="删除">' +
              '<i class="iconfont icon-ptkj-shanchu"></i>' +
              '</div>' +
              '</div>'
            );
            $('#DRecord', goForm).append($li);
          }

          top.appModal.success('保存成功！');
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

function getBackUserHtml(index) {
  return (
    '<li style="padding-right: 44px" data-index="' +
    index +
    '">' +
    '<div class="work-flow-settings-item has-label">' +
    '<label>A岗</label>' +
    '<div class="org-select-container org-style3 org-select-placeholder" id="DAUser_' +
    index +
    '">' +
    '<input type="hidden" id="AUser_' +
    index +
    '">' +
    '<input type="hidden" id="AUserID_' +
    index +
    '">' +
    '<ul class="org-select"></ul>' +
    '<i class="icon iconfont icon-ptkj-zuzhixuanze"></i>' +
    '</div>' +
    '</div>' +
    '<div class="work-flow-settings-item has-label pb5">' +
    '<label>B岗</label>' +
    '<div class="org-select-container org-style3 org-select-placeholder" id="BUser_' +
    index +
    '">' +
    '<input type="hidden" id="BUsers_' +
    index +
    '">' +
    '<input type="hidden" id="BUserIDs_' +
    index +
    '">' +
    '<ul class="org-select"></ul>' +
    '<i class="icon iconfont icon-ptkj-zuzhixuanze"></i>' +
    '</div>' +
    '</div>' +
    '<div class="btn-group">' +
    '<div class="well-btn w-btn-primary well-btn-sm w-noLine-btn btn-remove" title="删除"><i class="iconfont icon-ptkj-shanchu"></i> </div>' +
    '</div>' +
    '</li>'
  );
}

function get_timer_popup_html() {
  return (
    '<form id="timerForm" name="timerForm" class="workflow-popup well-workflow-form form-widget">' +
    '    <div class="well-form label-widening form-horizontal">' +
    '        <input name="timerConfigUuid" id="timerConfigUuid" type="hidden"/>' +
    '        <input name="introductionType" id="introductionType" type="hidden"/>' +
    '        <div class="form-group">' +
    '            <label for="name" class="well-form-label control-label required">计时器名称</label>' +
    '            <div class="well-form-control">' +
    '                <input type="text" class="form-control" id="name" name="name" value="">' +
    '                <input type="hidden" class="form-control" id="timerId" name="timerId" value="">' +
    '            </div>' +
    '        </div>' +
    '        <div class="form-group">' +
    '            <label for="name" class="well-form-label control-label required">计时方式' +
    '               <div class="w-tooltip">' +
    '                 <i class="iconfont icon-ptkj-tishishuoming"></i>' +
    '                   <div class="w-tooltip-content t-bottom">' +
    '                     <strong>工作日：</strong>根据工作时间中的工作日，以及每个工作日的工作时段进行计时<br>' +
    '                     <strong>工作日（一天24小时）：</strong>根据工作时间中的工作日，每个工作日固定按24小时进行计时<br>' +
    '                     <strong>自然日：</strong>根据自然日，每天固定按24小时进行计时' +
    '                   </div>' +
    '                 </div>' +
    '            </label>' +
    '            <div class="well-form-control">' +
    '                <div style="width:300px;display:inline-block;vertical-align:middle;"><input type="text" class="form-control" id="timingMode" name="timingMode" value=""></div>' +
    '                <button type="button" class="well-btn w-btn-minor w-btn-primary quoteTimeService"><i class="iconfont icon-ptkj-yinyong"></i>引用计时服务</button>' +
    '            </div>' +
    '        </div>' +
    '        <div class="form-group">' +
    '            <label for="name" class="well-form-label control-label"></label>' +
    '            <div class="well-form-control">' +
    '                <div style="display:flex;align-items:center;">启动后' +
    '                   <input type="radio" name="includeStartTimePoint" id="includeStartTimePoint0" value="0" checked>' +
    '                   <label for="includeStartTimePoint0" style="display:flex;align-items:center;">从下一</label>' +
    '                     <div style="width:190px;margin:0 8px;"><input type="text" class="form-control" id="timingModeUnit" name="timingModeUnit" value=""></div>' +
    '                     <label for="includeStartTimePoint0" style="display:flex;align-items:center;">开始计时</label>' +
    '                   <input type="radio" class="form-control" id="includeStartTimePoint1" name="includeStartTimePoint" value="1"><label for="includeStartTimePoint1">立即开始计时</label>' +
    '                 </div>' +
    '                <div class="autoDelay" style="display:flex;align-items:center;">到期时间在非工作时间时<input type="checkbox" name="autoDelay" id="autoDelay"><label for="autoDelay">自动推迟到下一工作时间的起始点前</label></div>' +
    '            </div>' +
    '        </div>' +
    '        <div class="form-group">' +
    '            <label for="name" class="well-form-label control-label required">时限类型</label>' +
    '            <div class="well-form-control">' +
    '                <input type="text" class="form-control" id="limitTimeType" name="limitTimeType" value="">' +
    '            </div>' +
    '        </div>' +
    '        <div class="form-group timeLimitType timeLimitType1" style="display:none;">' +
    '            <label for="name" class="well-form-label control-label required">时限</label>' +
    '            <div class="well-form-control">' +
    '                <div><input type="number" class="form-control" id="timeLimit1" name="timeLimit1" value=""></div>' +
    '                <div><input type="text" class="form-control" id="timeLimitUnit1" name="timeLimitUnit1" value=""></div>' +
    '            </div>' +
    '        </div>' +
    '        <div class="form-group timeLimitType timeLimitType4" style="display:none;">' +
    '            <label for="name" class="well-form-label control-label required">截止时间</label>' +
    '            <div class="well-form-control">' +
    '                <div><input type="text" class="form-control" id="timeLimitUnit2" name="timeLimitUnit2" value=""></div>' +
    '                <div class="timeD-date-wrap"><input type="text" class="form-control" id="timeLimit" name="timeLimit" value=""></div>' +
    '            </div>' +
    '        </div>' +
    '        <div class="form-group timeLimitType timeLimitType2" style="display:none;">' +
    '            <label for="name" class="well-form-label control-label required">时限字段</label>' +
    '            <div class="well-form-control">' +
    '                <div><input type="text" class="form-control" id="limitTimeField1" name="limitTimeField1" value=""></div>' +
    '                <div><input type="text" class="form-control" id="timeLimitUnit3" name="timeLimitUnit3" value=""></div>' +
    '            </div>' +
    '        </div>' +
    '        <div class="form-group timeLimitType timeLimitType3" style="display:none;">' +
    '            <label for="name" class="well-form-label control-label required">截止时间字段</label>' +
    '            <div class="well-form-control">' +
    '                <div><input type="text" class="form-control" id="timeLimitUnit4" name="timeLimitUnit4" value=""></div>' +
    '                <div><input type="text" class="form-control" id="limitTimeField2" name="limitTimeField2" value=""></div>' +
    '            </div>' +
    '        </div>' +
    '        <div class="form-group">' +
    '            <label for="name" class="well-form-label control-label required">工作时间</label>' +
    '            <div class="well-form-control">' +
    '                <input type="text" class="form-control" id="workTimePlanName" name="workTimePlanName" value="">' +
    '                <input type="hidden" class="form-control" id="workTimePlanId" name="workTimePlanId" value="">' +
    '            </div>' +
    '        </div>' +
    // '        <div class="form-group">' +
    // '            <label class="well-form-label control-label">设置方式</label>' +
    // '            <div class="well-form-control">' +
    // '                <input type="radio" name="limitTimeType" id="limitTimeType1" checked value="1">' +
    // '                <label class="radio-inline" for="limitTimeType1">设置固定时限</label>' +
    // '                <input type="radio" name="limitTimeType" id="limitTimeType2" value="2">' +
    // '                <label class="radio-inline" for="limitTimeType2">按字段设置动态时限</label>' +
    // '                <input type="radio" name="limitTimeType" id="limitTimeType3" value="3">' +
    // '                <label class="radio-inline" for="limitTimeType3">按字段设置动态截止时间</label>' +
    // '            </div>' +
    // '        </div>' +
    // '        <div class="form-group">' +
    // '            <label class="showLimitTimeTypeLabel well-form-label control-label required">办理时限</label>' +
    // '            <div class="well-form-control clear">' +
    // '               <div class="showLimitTimeCtl showLimitTimeType1">' +
    // '                   <input type="number" class="form-control" id="limitTime1" name="limitTime1">' +
    // '               </div>' +
    // '               <div class="showLimitTimeCtl showLimitTimeType2">' +
    // '                   <input class="form-control" id="limitTime" name="limitTime">' +
    // '               </div>' +
    // '               <div class="showLimitTimeCtl showLimitTimeType3">' +
    // '                   <input class="form-control" id="limitTime2" name="limitTime2">' +
    // '               </div>' +
    // '               <div class="showLimitTimeCtl showLimitTimeType1 showLimitTimeType2">' +
    // '                   <input class="form-control" id="limitUnit" name="limitUnit">' +
    // '               </div>' +
    // '               <div class="showLimitTimeCtl showLimitTimeType3">' +
    // '                   <input class="form-control" id="limitUnit2" name="limitUnit2">' +
    // '               </div>' +
    // '            </div>' +
    // '        </div>' +
    '        <div class="form-group">' +
    '            <label class="well-form-label control-label required">计时环节(普通)</label>' +
    '            <div class="well-form-control">' +
    '                <input type="text" class="form-control" id="tasks" name="tasks">' +
    '            </div>' +
    '        </div>' +
    '        <div class="form-group">' +
    '            <label class="well-form-label control-label required">计时环节(子流程)</label>' +
    '            <div class="well-form-control">' +
    '               <ul id="subTaskList" class="work-flow-settings">' +
    '               </ul>' +
    '               <div id="addSubTasks" class="well-btn w-btn-primary w-noLine-btn well-btn-sm" style="margin-top: 4px">' +
    '                   <i class="iconfont icon-ptkj-jiahao"></i>' +
    '                   <span>添加</span>' +
    '               </div>' +
    '            </div>' +
    '        </div>' +
    '        <div class="form-group">' +
    '            <label for="enableAlarm" class="well-form-label control-label">预警提醒</label>' +
    '            <div class="well-form-control">' +
    '                <div class="switch-wrap" id="enableAlarm">' +
    '                    <span class="switch-text switch-open" style=""></span>' +
    '                    <span class="switch-radio" data-status="0"></span>' +
    '                    <span class="switch-text switch-close" style="display: none;"></span>' +
    '                    <input value="1" style="display: none;" class="w-search-option" id="enableAlarm_true" name="enableAlarm" type="radio">' +
    '                    <input value="0" style="display: none;" class="w-search-option" id="enableAlarm_false" name="enableAlarm" type="radio">' +
    '                </div>' +
    '            </div>' +
    '        </div>' +
    '        <div id="enableAlarmBox" class="form-group" style="display: none">' +
    '            <div class="well-form-control no-left-label">' +
    '               <div class="timerWarnBox multiBox">' +
    '                   <ul id="alarmObjectsList">' +
    '                   </ul>' +
    '                   <div id="addAlarmObjects" class="well-btn w-btn-primary w-noLine-btn well-btn-sm">' +
    '                       <i class="iconfont icon-ptkj-jiahao"></i>' +
    '                       <span>添加消息提醒</span>' +
    '                   </div>' +
    '               </div>' +
    '            </div>' +
    '        </div>' +
    '        <div class="form-group">' +
    '            <label class="well-form-label control-label">逾期提醒</label>' +
    '            <div class="well-form-control">' +
    '                <div class="switch-wrap" id="enableDueDoing">' +
    '                    <span class="switch-text switch-open" style=""></span>' +
    '                    <span class="switch-radio" data-status="0"></span>' +
    '                    <span class="switch-text switch-close" style="display: none;"></span>' +
    '                    <input value="1" style="display: none;" class="w-search-option" name="enableDueDoing" type="radio">' +
    '                    <input value="0" style="display: none;" class="w-search-option" name="enableDueDoing" type="radio">' +
    '                </div>' +
    '            </div>' +
    '        </div>' +
    '        <div id="enableDueDoingBox" class="form-group" style="display: none">' +
    '            <div class="well-form-control no-left-label">' +
    '               <div class="timerWarnBox multiBox">' +
    '                   <ul id="dueObjectsList">' +
    '                       <li>' +
    '                           <div class="timerWarnBox-item">' +
    '                               <span>消息催办</span>' +
    '                               <input class="form-control num" type="number" name="dueTime" value="1" min="0">' +
    '                               <div class="select" style="width:155px;">' +
    '                                   <input class="form-control" id="dueUnit" name="dueUnit" value="2">' +
    '                               </div>' +
    '                               <span>自动催办在办人员一次，共</span>' +
    '                               <input class="form-control num" type="number" name="dueFrequency" value="1">' +
    '                               <span>次</span>' +
    '                           </div>' +
    '                           <div class="timerWarnBox-item" style="padding-left: 55px">' +
    '                               <div class="label">消息通知</div>' +
    '                               <div class="checkbox">' +
    '                                   <input type="checkbox" checked name="dueObjects" id="dueObjects_Doing" value="Doing">' +
    '                                   <label class="checkbox-inline" for="dueObjects_Doing">在办人员</label>' +
    '                                   <input type="checkbox" name="dueObjects" id="dueObjects_DoingSuperior" value="DoingSuperior">' +
    '                                   <label class="checkbox-inline" for="dueObjects_DoingSuperior">在办人员上级领导</label>' +
    '                                   <input type="checkbox" name="dueObjects" id="dueObjects_Monitor" value="Monitor">' +
    '                                   <label class="checkbox-inline" for="dueObjects_Monitor">督办人员</label>' +
    '                                   <input type="checkbox" name="dueObjects" id="dueObjects_Tracer" value="Tracer">' +
    '                                   <label class="checkbox-inline" for="dueObjects_Tracer">跟踪人员</label>' +
    '                                   <input type="checkbox" name="dueObjects" id="dueObjects_Admin" value="Admin">' +
    '                                   <label class="checkbox-inline" for="dueObjects_Admin">流程管理人员</label>' +
    '                                   <input type="checkbox" name="dueObjects" id="dueObjects_Other" value="Other">' +
    '                                   <label class="checkbox-inline" for="dueObjects_Other">其他人员</label>' +
    '                               </div>' +
    '                               <div class="org-select-container org-style3 org-select-placeholder" data-id="dueUser" id="DdueUsers" style="display: none">' +
    '                                   <input type="hidden" id="DdueUser1">' +
    '                                   <input type="hidden" id="dueUser1">' +
    '                                   <input type="hidden" id="dueUser2">' +
    '                                   <input type="hidden" id="dueUser4">' +
    '                                   <input type="hidden" id="dueUser8">' +
    '                                   <ul class="org-select"></ul>' +
    '                                   <i class="icon iconfont icon-ptkj-zuzhixuanze"></i>' +
    '                               </div>' +
    '                           </div>' +
    '                       </li>' +
    '                   </ul>' +
    '               </div>' +
    '            </div>' +
    '        </div>' +
    '       <div class="morePropertyWrap" style="display: none">' +
    '           <div class="form-group">' +
    '               <label class="well-form-label control-label">逾期时</label>' +
    '               <div class="well-form-control">' +
    '                   <input type="text" class="form-control" id="dueAction" name="dueAction" value="0">' +
    '               </div>' +
    '           </div>' +
    '           <div id="dueToTaskCtl" class="form-group" style="display: none">' +
    '               <div class="well-form-control no-left-label">' +
    '                   <input type="text" class="form-control" id="dueToTask" name="dueToTask">' +
    '               </div>' +
    '           </div>' +
    '           <div id="dueToUserCtl" class="form-group" style="display: none">' +
    '               <div class="well-form-control no-left-label">' +
    '                   <div class="org-select-container org-style3 org-select-placeholder" data-id="dueToUser" id="DdueToUsers">' +
    '                       <input type="hidden" id="DdueToUser1">' +
    '                       <input type="hidden" id="dueToUser1">' +
    '                       <input type="hidden" id="dueToUser2">' +
    '                       <input type="hidden" id="dueToUser4">' +
    '                       <input type="hidden" id="dueToUser8">' +
    '                       <ul class="org-select"></ul>' +
    '                       <i class="icon iconfont icon-ptkj-zuzhixuanze"></i>' +
    '                   </div>' +
    '               </div>' +
    '           </div>' +
    '           <div class="form-group">' +
    '               <label class="well-form-label control-label">计时结束设置</label>' +
    '               <div class="well-form-control">' +
    '                   <input type="radio" name="timeEndType" id="timeEndType0" checked value="0">' +
    '                   <label class="radio-inline" for="timeEndType0">流程办结结束计时</label>' +
    '                   <input type="radio" name="timeEndType" id="timeEndType1" value="1">' +
    '                   <label class="radio-inline" for="timeEndType1">流出计时环节结束计时</label>' +
    '                   <input type="radio" name="timeEndType" id="timeEndType2" value="2">' +
    '                   <label class="radio-inline" for="timeEndType2">流经指定流向结束计时</label>' +
    '               </div>' +
    '           </div>' +
    '           <div id="timeEndTypeCtl" class="form-group" style="display: none">' +
    '               <div class="well-form-control no-left-label">' +
    '                   <input type="text" class="form-control" id="overDirections" name="overDirections">' +
    '               </div>' +
    '           </div>' +
    '           <div class="form-group">' +
    '               <label class="well-form-label control-label">其他设置</label>' +
    '               <div class="well-form-control" style="width: 80%">' +
    '                   <input type="checkbox" name="autoUpdateLimitTime" id="autoUpdateLimitTime" value="1">' +
    '                   <label class="checkbox-inline" for="autoUpdateLimitTime">自动更新时限</label>' +
    '                   <div class="w-tooltip" style="vertical-align: text-top;">' +
    '                       <i class="iconfont icon-ptkj-tishishuoming"></i>' +
    '                       <div class="w-tooltip-content">' +
    '                        设置时限的字段值变更时，自动更新时限；设置固定时限值时，可通过代码调用自动变更时限' +
    '                       </div>' +
    '                   </div>' +
    '                   <br>' +
    '                   <input type="checkbox" name="ignoreEmptyLimitTime" id="ignoreEmptyLimitTime" value="1">' +
    '                   <label class="checkbox-inline" for="ignoreEmptyLimitTime">时限为空时不计时</label>' +
    '                   <div class="w-tooltip" style="vertical-align: text-top;">' +
    '                       <i class="iconfont icon-ptkj-tishishuoming"></i>' +
    '                       <div class="w-tooltip-content">' +
    '                        设置时限的字段值为空时，本计时器不计时' +
    '                       </div>' +
    '                   </div>' +
    '               </div>' +
    '           </div>' +
    '       </div>' +
    '       <div class="moreProperty">' +
    '           <div class="text">更多<i class="iconfont icon-ptkj-xianmiaoshuangjiantou-xia"></i></div>' +
    '       </div>' +
    '    </div>' +
    '</form>'
  );
}

function get_subTask_dialog_html() {
  return (
    '<form id="subTaskForm" name="subTaskForm" class="workflow-popup well-workflow-form form-widget">' +
    '    <div class="well-form form-horizontal">' +
    '        <div class="form-group">' +
    '            <label class="well-form-label control-label required">环节名称</label>' +
    '            <div class="well-form-control">' +
    '                <input type="text" class="form-control" id="taskId" name="taskId">' +
    '                <input type="hidden" class="form-control" id="taskName" name="taskName">' +
    '            </div>' +
    '        </div>' +
    '        <div class="form-group">' +
    '            <label class="well-form-label control-label required">计时方式' +
    '               <div class="w-tooltip" style="vertical-align: text-top;">' +
    '                   <i class="iconfont icon-ptkj-tishishuoming"></i>' +
    '                   <div class="w-tooltip-content show-down">' +
    '                       按当前计时器计时：当前子流程环节的办理时限同普通流程环节，和子流程实例的办理时限无关。<br/>' +
    '                       按子流程计时器计时：当前子流程环节的办理时限和子流程实例的办理时限保持一致。' +
    '                   </div>' +
    '               </div>' +
    '            </label>' +
    '            <div class="well-form-control">' +
    '                <input type="radio" name="timingMode" id="timingMode_1" checked value="1">' +
    '                <label class="radio-inline" for="timingMode_1">按当前计时器计时</label>' +
    // '                <input type="radio" name="timingMode" id="timingMode_2" value="2">' +
    // '                <label class="radio-inline" for="timingMode_2">按子流程计时器计时</label>' +
    '            </div>' +
    '        </div>' +
    '       <div id="subFlowTimers" style="display: none"></div>' +
    '   </div>' +
    '</form>'
  );
}

function subTaskLoadEvent($dialog, goForm, flowProperty, data) {
  var poForm = $('#subTaskForm', $dialog)[0];
  if (data) {
    poForm.taskId.value = data.taskId;
    poForm.taskName.value = data.taskName;
    poForm['timingMode_' + data.timingMode].checked = true;
  }
  var subTasksObj = {};
  var subTaskArr = $.map(goWorkFlow.tasks, function (item) {
    if (item.Type === 'SUBFLOW') {
      var id = item.xmlObject.context.id;
      var text = item.xmlObject.context.getAttribute('name');
      subTasksObj[id] = item.xmlObject;
      return {
        id: id,
        text: text
      };
    }
  });

  $('#taskId', $dialog)
    .wellSelect({
      data: subTaskArr,
      labelField: 'taskName',
      valueField: 'taskId',
      showEmpty: false
    })
    .on('change', function () {
      if (!poForm.taskId.value) {
        return;
      }
      var _subTaskXml = subTasksObj[poForm.taskId.value];
      var newFlows = _subTaskXml.selectSingleNode('newFlows');
      var newFlow = newFlows ? newFlows.selectNodes('newFlow') : [];
      $('#subFlowTimers', $dialog).empty();
      $.each(newFlow, function (i, item) {
        var x2js = new X2JS();
        var _newFlow = x2js.xml2js(item.outerHTML).newFlow;
        addSubFlowItem($dialog, _newFlow, i, data);
      });
    })
    .trigger('change');

  $('[name="timingMode"]', $dialog).on('change', function () {
    var timingMode = $(this).val();
    if (timingMode === '1') {
      $('#subFlowTimers', $dialog).hide();
    } else {
      $('#subFlowTimers', $dialog).show();
    }
  });
  $('[name="timingMode"]:checked', $dialog).trigger('change');
}

function addSubFlowItem($dialog, _newFlow, i, data) {
  var _html =
    '<div class="form-group subflow-item" data-value="' +
    _newFlow.id +
    '" data-name="' +
    _newFlow.name +
    '">' +
    '<label class="well-form-label control-label">' +
    _newFlow.name +
    '</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control subflow-timerId" id="timerId_' +
    i +
    '" name="timerId_' +
    i +
    '">' +
    '<input type="hidden" class="form-control subflow-timerName" id="timerName_' +
    i +
    '" name="timerName_' +
    i +
    '">' +
    '</div>' +
    '</div>';
  $('#subFlowTimers', $dialog).append(_html);
  if (data && data.timers) {
    $.each(data.timers, function (j, item) {
      if (item.newFlowId === _newFlow.id) {
        $('#timerId_' + i, $dialog).val(item.newFlowTimerId);
      }
    });
  }
  $.get({
    url: ctx + '/api/workflow/definition/listFlowTimerByFlowId',
    data: {
      flowDefId: _newFlow.value
    },
    service: 'flowSchemeService.listFlowTimerByFlowId',
    success: function (result) {
      var _data = $.map(result.data, function (item) {
        return {
          id: item.id,
          text: item.name
        };
      });
      $('#timerId_' + i, $dialog).wellSelect({
        data: _data,
        labelField: 'timerName_' + i,
        valueField: 'timerId_' + i
      });
    }
  });
}

function openSubTaskDialog(goForm, flowProperty, data, index) {
  var _html = get_subTask_dialog_html();
  var $dialog = top.appModal.dialog({
    title: '子流程环节计时设置',
    message: _html,
    height: '500px',
    size: 'large',
    shown: function () {
      subTaskLoadEvent($dialog, goForm, flowProperty, data);
    },
    buttons: {
      save: {
        label: '保存',
        className: 'well-btn w-btn-primary',
        callback: function () {
          var form = $('#subTaskForm', $dialog)[0];
          if (form.taskName.value === '') {
            top.appModal.error('请选择环节名称！');
            return false;
          }
          var timingMode = $('[name="timingMode"]:checked', $dialog).val();
          var timers = [];
          if (timingMode === '2') {
            $('.subflow-timerId', $dialog).each(function () {
              var $this = $(this);
              if ($this.val()) {
                timers.push({
                  newFlowId: $this.closest('.subflow-item', $dialog).attr('data-value'),
                  newFlowTimerName: $this.next().val(),
                  newFlowTimerId: $this.val()
                });
              }
            });
            if (timers.length === 0) {
              top.appModal.error('至少选择一个子流程的计时器！');
              return false;
            }
          }
          top.appModal.success('保存成功！');
          var formData = data || {};
          formData.taskId = form.taskId.value;
          formData.taskName = form.taskName.value;
          formData.timingMode = timingMode;
          if (timingMode === '2') {
            formData.timers = timers;
          }
          if (formData.index !== undefined) {
            $('#subTaskList li[data-index="' + formData.index + '"]', goForm)
              .attr('data-name', formData.taskName)
              .data('subTask', formData)
              .find('.name')
              .text(formData.taskName);
          } else {
            formData.index = index + 1;
            addSubTask(formData, goForm);
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

function addSubTask(formData, $dialog) {
  var $li = $('<li data-name="' + formData.taskName + '" data-index="' + formData.index + '" style="padding: 0 8px"></li>');
  $li.data('subTask', formData);
  $li.append(
    '<div class="name">' +
    formData.taskName +
    '</div>' +
    '<div class="btn-group">' +
    '<div class="well-btn w-btn-primary well-btn-sm w-noLine-btn btn-set" title="设置">' +
    '<i class="iconfont icon-ptkj-shezhi"></i>' +
    '</div>' +
    '<div class="well-btn w-btn-primary well-btn-sm w-noLine-btn btn-remove" title="删除">' +
    '<i class="iconfont icon-ptkj-shanchu"></i>' +
    '</div>' +
    '</div>'
  );
  $('#subTaskList', $dialog).append($li);
}

function timerLoadEvent($dialog, goForm, flowProperty, _timerNames, _name) {
  var poForm = $('#timerForm', $dialog)[0];
  var fields = goWorkFlow.DformDefinition ? goWorkFlow.DformDefinition.fields : [];
  var formFields = $.map(fields, function (item) {
    //1:text 16:树形下拉框 17:radio 18:checkbox 20:textArea 31:number  19*:下拉框
    if ($.inArray(item.inputMode, ['1', '16', '17', '18', '20', '31', '19', '191', '199']) > -1) {
      return {
        id: item.name,
        text: item.displayName
      };
    }
  });
  var formFields1 = $.map(fields, function (item) {
    //1:text 16:树形下拉框 17:radio 18:checkbox 20:textArea 31:number  19*:下拉框
    if ($.inArray(item.inputMode, ['30']) > -1) {
      return {
        id: item.name,
        text: item.displayName
      };
    }
  });
  var formFieldsOnlyTime = $.map(fields, function (item) {
    // 30:date
    if (item.inputMode === '30') {
      return {
        id: item.name,
        text: item.displayName
      };
    }
  });
  var timerArr1 = [
    {
      id: '3',
      text: '工作日(工作时间)'
    },
    {
      id: '2',
      text: '工作小时(工作时间)'
    },
    {
      id: '1',
      text: '工作分钟(工作时间)'
    },
    {
      id: '13',
      text: '工作日(24小时制)'
    },
    {
      id: '12',
      text: '工作小时(24小时制)'
    },
    {
      id: '11',
      text: '工作分钟(24小时制)'
    },
    {
      id: '86400',
      text: '天'
    },
    {
      id: '3600',
      text: '小时'
    },
    {
      id: '60',
      text: '分钟'
    }
  ];
  var timerArr2 = [
    {
      id: '20',
      text: '日期 2000-01-01 (工作时间)'
    },
    {
      id: '19',
      text: '日期 2000-01-01 12 (工作时间)'
    },
    {
      id: '18',
      text: '日期 2000-01-01 12:00 (工作时间)'
    },
    {
      id: '23',
      text: '日期 2000-01-01 (24小时制)'
    },
    {
      id: '22',
      text: '日期 2000-01-01 12 (24小时制)'
    },
    {
      id: '21',
      text: '日期 2000-01-01 12:00 (24小时制)'
    }
  ];
  var timingMode = [
    {
      id: '1',
      text: '工作日'
    },
    {
      id: '2',
      text: '工作日（一天24小时）'
    },
    {
      id: '3',
      text: '自然日'
    }
  ];
  var timeLimitType = [
    {
      id: '1',
      text: '固定时限'
    },
    {
      id: '4',
      text: '固定截止时间'
    },
    {
      id: '2',
      text: '动态时限'
    },
    {
      id: '3',
      text: '动态截止时间'
    }
  ];
  var timingModeUnit1 = [
    {
      id: '3',
      text: '工作日'
    },
    {
      id: '2',
      text: '工作小时'
    },
    {
      id: '1',
      text: '工作分钟'
    }
  ];
  var timingModeUnit2 = [
    {
      id: '13',
      text: '工作日'
    },
    {
      id: '12',
      text: '小时（工作日）'
    },
    {
      id: '11',
      text: '分钟（工作日）'
    }
  ];
  var timingModeUnit3 = [
    {
      id: '86400',
      text: '天'
    },
    {
      id: '3600',
      text: '小时'
    },
    {
      id: '60',
      text: '分钟'
    }
  ];
  var timeLimitUnit1 = [
    {
      id: '1',
      text: '天'
    },
    {
      id: '2',
      text: '小时'
    },
    {
      id: '3',
      text: '分钟'
    }
  ];
  var timeLimitUnit4 = [
    {
      id: '1',
      text: '日期 2000-01-01'
    },
    {
      id: '2',
      text: '日期到时 2000-01-01 12'
    },
    {
      id: '3',
      text: '日期到分 2000-01-01 12:00'
    }
  ];

  var timerArr3 = [
    {
      id: '3',
      text: '工作日'
    },
    {
      id: '2',
      text: '工作小时'
    },
    {
      id: '1',
      text: '工作分钟'
    },
    {
      id: '86400',
      text: '天'
    },
    {
      id: '3600',
      text: '小时'
    },
    {
      id: '60',
      text: '分钟'
    }
  ];
  var dueAction = [
    {
      id: '0',
      text: '默认不自动处理'
    },
    {
      id: '1',
      text: '自动移交给B岗人员办理'
    },
    {
      id: '2',
      text: '自动移交给督办人员办理'
    },
    {
      id: '4',
      text: '自动移交给其他人员办理'
    },
    {
      id: '8',
      text: '自动退回上一个办理环节'
    },
    {
      id: '16',
      text: '自动进入下一个办理环节'
    }
  ];
  // 固定时限的时限单位
  $('#timeLimitUnit1', $dialog).wellSelect({
    searchable: false,
    showEmpty: false,
    data: timeLimitUnit1,
    valueField: 'timeLimitUnit1'
  });
  // 动态时限的时限单位
  $('#timeLimitUnit3', $dialog).wellSelect({
    searchable: false,
    showEmpty: false,
    data: timeLimitUnit1,
    valueField: 'timeLimitUnit3'
  });
  // 固定截止时间的时限单位
  $('#timeLimitUnit2', $dialog)
    .wellSelect({
      searchable: false,
      showEmpty: false,
      data: timeLimitUnit4,
      valueField: 'timeLimitUnit2'
    })
    .off()
    .on('change', function () {
      var val = $(this).val();
      var $time = $('.timeLimitType4', $dialog).find('.well-form-control').find('.timeD-date-wrap');
      $time.empty();
      $time.append('<input type="text" class="form-control" id="timeLimit" name="timeLimit" value="">');
      if (val == '1') {
        layDate.render({
          elem: $('#timeLimit', $dialog),
          format: 'yyyy-MM-dd',
          trigger: 'click',
          type: 'date',
          scope: $dialog.find('.timeD-date-wrap')
        });
      } else if (val == '2') {
        layDate.render({
          elem: $('#timeLimit', $dialog),
          format: 'yyyy-MM-dd HH',
          trigger: 'click',
          type: 'datetime',
          scope: $dialog.find('.timeD-date-wrap')
        });
      } else if (val == '3') {
        layDate.render({
          elem: $('#timeLimit', $dialog),
          format: 'yyyy-MM-dd HH:mm',
          trigger: 'click',
          type: 'datetime',
          scope: $dialog.find('.timeD-date-wrap')
        });
      }
    });
  // 动态截止时间的时限单位
  $('#timeLimitUnit4', $dialog).wellSelect({
    searchable: false,
    showEmpty: false,
    data: timeLimitUnit4,
    valueField: 'timeLimitUnit4'
  });
  // 计时方式
  $('#timingMode', $dialog).wellSelect({
    searchable: false,
    showEmpty: false,
    data: timingMode
  });
  // 计时方式的单位
  $('#timingModeUnit', $dialog)
    .wellSelect({
      searchable: false,
      showEmpty: false,
      data: []
    })
    .off()
    .on('change', function () {
      var includeStartTimePoint = $("input[name='includeStartTimePoint']:checked", $dialog).val();
      if (includeStartTimePoint == '0') {
        var val = $(this).val();
        var num = val == '3' || val == '13' || val == '86400' ? 2 : val == '2' || val == '12' || val == '3600' ? 1 : 0;
        $('#timeLimitUnit1', $dialog)
          .val('')
          .wellSelect('reRenderOption', {
            data: _.dropRight(timeLimitUnit1, num)
          });
        $('#timeLimitUnit2', $dialog)
          .val('')
          .wellSelect('reRenderOption', {
            data: _.dropRight(timeLimitUnit4, num)
          });
        $('#timeLimitUnit3', $dialog)
          .val('')
          .wellSelect('reRenderOption', {
            data: _.dropRight(timeLimitUnit1, num)
          });
        $('#timeLimitUnit4', $dialog)
          .val('')
          .wellSelect('reRenderOption', {
            data: _.dropRight(timeLimitUnit4, num)
          });
      }
    });

  $("input[name='includeStartTimePoint']", $dialog)
    .off()
    .on('change', function () {
      var includeStartTimePoint = $(this).val();
      var val = $('#timingModeUnit', $dialog).val();
      var num =
        includeStartTimePoint == '0'
          ? val == '3' || val == '13' || val == '86400'
            ? 2
            : val == '2' || val == '12' || val == '3600'
              ? 1
              : 0
          : 0;
      $('#timeLimitUnit1', $dialog)
        .val('')
        .wellSelect('reRenderOption', {
          data: _.dropRight(timeLimitUnit1, num)
        });
      $('#timeLimitUnit2', $dialog)
        .val('')
        .wellSelect('reRenderOption', {
          data: _.dropRight(timeLimitUnit4, num)
        });
      $('#timeLimitUnit3', $dialog)
        .val('')
        .wellSelect('reRenderOption', {
          data: _.dropRight(timeLimitUnit1, num)
        });
      $('#timeLimitUnit4', $dialog)
        .val('')
        .wellSelect('reRenderOption', {
          data: _.dropRight(timeLimitUnit4, num)
        });
    });

  $('#limitTimeType', $dialog).wellSelect({
    searchable: false,
    showEmpty: false,
    data: timeLimitType
  });
  $('#limitTimeField1', $dialog).wellSelect({
    data: formFields
  });
  $('#limitTimeField2', $dialog).wellSelect({
    data: formFields1
  });

  // $('#limitTime', $dialog).wellSelect({
  //   data: formFields
  // });
  // $('#limitTime2', $dialog).wellSelect({
  //   data: formFieldsOnlyTime
  // });
  // $('#limitUnit', $dialog).wellSelect({
  //   searchable: false,
  //   data: timerArr1
  // });
  // $('#limitUnit2', $dialog).wellSelect({
  //   searchable: false,
  //   data: timerArr2
  // });

  $.ajax({
    type: 'post',
    url: ctx + '/api/ts/work/time/plan/getAllBySystemUnitIdsLikeName',
    dataType: 'json',
    data: {
      name: ''
    },
    success: function (res) {
      if (res.code == 0) {
        var data = [];
        var defaultVal = '';
        var workTimePlanData = res.data;

        $.each(res.data, function (index, item) {
          data.push({
            id: item.id,
            text: item.name + 'v' + item.version,
            data: item.uuid
          });
          if (item.isDefault) {
            defaultVal = item.uuid;
          }
        });

        $('#workTimePlanName', $dialog)
          .wellSelect({
            data: data,
            valueField: 'workTimePlanId'
          })
          .off()
          .on('change', function () {
            var val = $('#workTimePlanId', $dialog).val();
            var detail = _.find(workTimePlanData, function (o) {
              return o.id == val;
            });
            var item = JSON.parse(detail && detail.workTimeSchedule || '[]');
            for (var i = 0; i < item.length; i++) {
              if (item[i].workTimeType == '3') {
                top.appModal.warning('该工作时间方案包含弹性工时，弹性工时部分将不参与计时！');
                break;
              } else {
                var workTimes = item[i].workTimes;
                for (var j = 0; j < workTimes.length; j++) {
                  if (workTimes[j].type == '2') {
                    top.appModal.warning('该工作时间方案包含弹性工时，弹性工时部分将不参与计时！');
                    break;
                  }
                }
              }
            }
          });

        if ($('#workTimePlanId', $dialog).val() == '') {
          $('#workTimePlanName', $dialog).wellSelect('val', defaultVal);
        }
      }
    }
  });

  var tasksObj = {};
  var tasksArr = $.map(goWorkFlow.tasks, function (item) {
    if (item.Type === 'TASK') {
      var id = item.xmlObject.context.id;
      var text = item.xmlObject.context.getAttribute('name');
      tasksObj[id] = text;
      return {
        id: id,
        text: text
      };
    }
  });

  $('#tasks', $dialog)
    .wellSelect({
      multiple: true,
      data: tasksArr,
      chooseAll: true,
      callback: function (data) {
        if (!poForm.name.value) {
          var _name = $.map(data, function (item) {
            return item.text;
          });
          poForm.name.value = _name.join('|');
        }
      }
    })
    .on('change', function () {
      var $this = $(this);
      var _tasks = $this.val().split(';');
      var laNodes = goWorkFlow.flowXML.selectNodes('./timers/timer');
      if (laNodes) {
        var timerTaskIds = [];
        for (var i = 0; i < laNodes.length; i++) {
          var timerIdNode = $(laNodes[i]).selectSingleNode('timerId');
          if (!timerIdNode) {
            continue;
          }
          var timerId = timerIdNode.text();
          if (timerId === poForm.timerId.value) {
            continue;
          }
          var taskIds = aGetXMLFieldValue($(laNodes[i]), 'tasks');
          if (taskIds != null) {
            for (var j = 0; j < taskIds.length; j++) {
              timerTaskIds.push(taskIds[j]);
            }
          }
        }
        // 重复检测
        $.each(_tasks, function (i, item) {
          if (timerTaskIds.indexOf(item) > -1) {
            top.appModal.error('环节[' + tasksObj[item] + ']已存在计时配置，不能再设置!');
            _tasks.splice(i, 1);
            setTimeout(function () {
              $('#tasks', $dialog).wellSelect('val', _tasks.join(';'));
            }, 0);
            return false;
          }
        });
      }
    });

  //引用计时器
  $('.quoteTimeService', $dialog)
    .off()
    .on('click', function () {
      var timerConfigUuidValue = $('#timerConfigUuid', $dialog).val() || '';
      var introductionType = $('#introductionType', $dialog).val() || '';
      var url =
        ctx +
        '/web/app/page/preview/1526fca2143e81de76c3e0b7569fbd3f?pageUuid=11c89506-5379-458e-988b-d57757f54f79&timerConfigUuidValue=' +
        timerConfigUuidValue;
      var iframe =
        '<div class="embed-responsive embed-responsive-4by3"><iframe name="tsIframe" src="' +
        url +
        '" class="embed-responsive-item" style="width:100%;"></iframe></div>';
      var timingMode = [
        [3, 2, 1],
        [13, 12, 11],
        [86400, 3600, 60]
      ];
      var ifQuote = false;
      if (introductionType != '') {
        ifQuote = true; // 是否有引用计时服务
      }
      var $tsDialog = top.appModal.dialog({
        title: '引用计时服务',
        message: iframe,
        size: 'large',
        width: '1200',
        height: '750',
        shown: function () {
          if (ifQuote) {
            $('.disabled-btn', $tsDialog).attr('disabled', false);
          } else {
            $('.disabled-btn', $tsDialog).attr('disabled', true);
          }
        },
        buttons: {
          cancleQuote: {
            label: '取消引用',
            className: 'well-btn w-btn-minor w-btn-primary disabled-btn',
            callback: function () {
              $('#timingMode', $dialog).wellSelect('disable', false);
              $("[name='includeStartTimePoint']", $dialog).removeAttr('disabled');
              $('#timingModeUnit', $dialog).wellSelect('disable', false);
              $('#autoDelay', $dialog).removeAttr('disabled');
              $('#limitTimeType', $dialog).wellSelect('disable', false);
              $('#timeLimit1', $dialog).removeAttr('disabled');
              $('#timeLimitUnit1', $dialog).wellSelect('disable', false);
              $('#timeLimitUnit2', $dialog).wellSelect('disable', false);
              $('#timeLimit', $dialog).removeAttr('disabled');
              $('#limitTimeField1', $dialog).wellSelect('disable', false);
              $('#timeLimitUnit3', $dialog).wellSelect('disable', false);
              $('#timeLimitUnit4', $dialog).wellSelect('disable', false);
              $('#limitTimeField2', $dialog).wellSelect('disable', false);

              // 请空uuid
              $('#timerConfigUuid', $dialog).val('');
            }
          },
          ok: {
            label: '确定',
            className: 'well-btn w-btn-primary',
            callback: function () {
              $tsDialog.find('iframe')[0].contentWindow;
              var $table = $('html', top.window.frames['tsIframe'].document).find('.ui-wBootstrapTable');
              var id = $table.attr('id');
              var $tr = $table.find('#' + id + '_table').find('tbody tr');
              var obj = {};
              var hasChecked = false;
              for (var i = 0; i < $tr.length; i++) {
                if ($($tr[i]).find('td input').prop('checked')) {
                  obj = $($tr[i]).data('obj');
                  hasChecked = true;
                  break;
                }
              }
              if (!hasChecked) {
                top.appModal.error('请选择计时服务！');
                return false;
              }
              $('#timerConfigUuid', $dialog).val(obj.uuid);
              $('#introductionType', $dialog).val('introduction');

              for (var i = 0; i < timingMode.length; i++) {
                for (var j = 0; j < timingMode[i].length; j++) {
                  if (obj.timingMode == timingMode[i][j]) {
                    $('#timingMode', $dialog)
                      .wellSelect('val', (i + 1).toString())
                      .trigger('change');
                    $('#timingModeUnit', $dialog).wellSelect('val', obj.timingMode).trigger('change');
                    break;
                  }
                }
              }

              $("[name='includeStartTimePoint'][value='" + obj.includeStartTimePoint + "']", $dialog)
                .prop('checked', true)
                .trigger('change');
              $("[name='includeStartTimePoint']", $dialog).attr('disabled', true);

              setTimeout(function () {
                $('#timingModeUnit', $dialog).wellSelect('disable', true);
                $('#timingMode', $dialog).wellSelect('disable', true);
              }, 0);

              $('#autoDelay', $dialog).prop('checked', obj.autoDelay).attr('disabled', true);

              if (obj.timeLimitType == '10') {
                $('#limitTimeType', $dialog).wellSelect('val', '1').wellSelect('disable', true);
                $('#timeLimit1', $dialog).val(obj.timeLimit).attr('disabled', true);
                $('#timeLimitUnit1', $dialog).wellSelect('val', obj.timeLimitUnit).wellSelect('disable', true);
              } else if (obj.timeLimitType == '20') {
                $('#limitTimeType', $dialog).wellSelect('val', '4').wellSelect('disable', true);
                $('#timeLimitUnit2', $dialog).wellSelect('val', obj.timeLimitUnit).wellSelect('disable', true);
                $('#timeLimit', $dialog).val(obj.timeLimit).attr('disabled', true);
              } else if (obj.timeLimitType == '30') {
                $('#limitTimeType', $dialog).wellSelect('val', '2').wellSelect('disable', true);
                // $('#timeLimitUnit3', $dialog).wellSelect('val', obj.timeLimitUnit).wellSelect('disable', true);
              } else if (obj.timeLimitType == '40') {
                $('#limitTimeType', $dialog).wellSelect('val', '3').wellSelect('disable', true);
                // $('#timeLimitUnit4', $dialog).wellSelect('val', obj.timeLimitUnit).wellSelect('disable', true);
              }
            }
          },
          cancle: {
            label: '取消',
            className: 'btn btn-default'
          }
        }
      });
    });

  $('#timingMode', $dialog)
    .off()
    .on('change', function () {
      var val = $(this).val();
      $('.autoDelay', $dialog)[val == '3' ? 'hide' : 'show']();
      var data = val == '1' ? timingModeUnit1 : val == '2' ? timingModeUnit2 : timingModeUnit3;
      // 计时方式的单位
      $('#timingModeUnit', $dialog).wellSelect('destroy').wellSelect({
        searchable: false,
        data: data,
        showEmpty: false
      });
      $('#timingModeUnit', $dialog).wellSelect('val', data[0].id);
    });

  $('#limitTimeType', $dialog)
    .off()
    .on('change', function () {
      var val = $(this).val();
      $('.timeLimitType', $dialog).hide();
      $('.timeLimitType' + val, $dialog).show();
    });

  // $('input[name="limitTimeType"]', $dialog).on('change', function () {
  //   var _val = $(this).val();
  //   $('.showLimitTimeCtl', $dialog).hide();
  //   $('.showLimitTimeType' + _val, $dialog).show();

  //   if (_val === '1') {
  //     $('.showLimitTimeTypeLabel', $dialog).text('办理时限');
  //   } else if (_val === '2') {
  //     $('.showLimitTimeTypeLabel', $dialog).text('时限字段');
  //   } else if (_val === '3') {
  //     $('.showLimitTimeTypeLabel', $dialog).text('截止时间字段');
  //   }
  // });

  $('input[name="limitTimeType"][value="1"]', $dialog).trigger('change');

  $dialog.on('click', '#addSubTasks', function () {
    var index = $('#subTaskList > li', $dialog).length ? $('#subTaskList > li:last', $dialog).attr('data-index') : 0;
    openSubTaskDialog(poForm, flowProperty, null, index);
  });

  $dialog.on('click', '#subTaskList .btn-set', function () {
    var $li = $(this).closest('li');
    var index = $li.attr('data-index');
    var subTask = $li.data('subTask');
    openSubTaskDialog(poForm, flowProperty, subTask, index);
  });

  $dialog.on('click', '#subTaskList .btn-remove', function () {
    var $this = $(this);
    $this.closest('li').remove();
  });

  switchFun('enableAlarm', $dialog, function (_val) {
    if (_val === '1') {
      $('#enableAlarmBox', $dialog).show();
      var liLen = $('#alarmObjectsList > li', $dialog).length;
      if (!liLen) {
        $('#addAlarmObjects', $dialog).trigger('click');
      }
    } else {
      $('#enableAlarmBox', $dialog).hide();
    }
  });
  switchFun('enableDueDoing', $dialog, function (_val) {
    if (_val === '1') {
      $('#enableDueDoingBox', $dialog).show();
    } else {
      $('#enableDueDoingBox', $dialog).hide();
    }
  });

  $dialog.on('click', '#addAlarmObjects', function () {
    var liLen = $('#alarmObjectsList > li', $dialog).length ? $('#alarmObjectsList > li:last', $dialog).attr('data-index') : 0;
    var alarmObjectsHtml = getAlarmObjectsHtml(++liLen);
    $('#alarmObjectsList', $dialog).append(alarmObjectsHtml);
    $('[name="alarmUnit_' + liLen + '"]', $dialog).wellSelect({
      data: timerArr3
    });
    $dialog.on('change', '[name="alarmObjects_' + liLen + '"]', function () {
      var $this = $(this);
      var hasOther = false;
      $.each($('[name="alarmObjects_' + liLen + '"]:checked', $dialog), function () {
        if ($(this).val() === 'Other') {
          hasOther = true;
        }
      });
      if (hasOther) {
        $this.closest('.checkbox').next().show();
      } else {
        $this.closest('.checkbox').next().hide();
      }
    });
  });

  $dialog.on('click', '#alarmObjectsList .delete-btn', function () {
    var $this = $(this);
    top.appModal.confirm('确认删除本条预警提醒？', function (result) {
      if (result) {
        $this.closest('li').remove();
        var liLen = $('#alarmObjectsList > li', $dialog).length;
        if (!liLen) {
          $('#addAlarmObjects', $dialog).trigger('click');
        }
      }
    });
  });

  $dialog.on('change', '[name="dueObjects"]', function () {
    var $this = $(this);
    var hasOther = false;
    $.each($('[name="dueObjects"]:checked', $dialog), function () {
      if ($(this).val() === 'Other') {
        hasOther = true;
      }
    });
    if (hasOther) {
      $this.closest('.checkbox').next().show();
    } else {
      $this.closest('.checkbox').next().hide();
    }
  });

  $('#dueUnit', $dialog).wellSelect({
    data: timerArr3
  });

  $dialog.on('click', '.org-select-container', function () {
    var _goForm = $('#timerForm', $dialog)[0];
    var id = $(this).attr('data-id');
    SelectUsers('unit/field/task/option', id, '选择人员', null, _goForm);
  });

  $dialog.on('click', '.moreProperty .text', function () {
    var $this = $(this);
    $this.toggleClass('open');
    $('.morePropertyWrap', $dialog).toggle();
  });

  $('#dueAction', $dialog)
    .wellSelect({
      data: dueAction,
      searchable: false
    })
    .on('change', function () {
      var $this = $(this);
      var _val = $this.val();
      if (_val === '4') {
        $('#dueToUserCtl', $dialog).show();
        $('#dueToTaskCtl', $dialog).hide();
      } else if (_val === '16') {
        $('#dueToUserCtl', $dialog).hide();
        $('#dueToTaskCtl', $dialog).show();
      } else {
        $('#dueToUserCtl', $dialog).hide();
        $('#dueToTaskCtl', $dialog).hide();
      }
    });

  var allTask = aGetTasks('TASK/SUBFLOW', null, true);
  var allTaskArr = $.map(allTask, function (item) {
    var itemArr = item.split('|');
    return {
      id: itemArr[1],
      text: itemArr[0]
    };
  });
  $('#dueToTask', $dialog).wellSelect({
    data: allTaskArr
  });

  var directions = $.map(getDirections(), function (item) {
    var textArr = item.text.split(' | ');
    return {
      id: item.id,
      text: textArr[0],
      desc: textArr[1]
    };
  });
  $('#overDirections', $dialog).wellSelect({
    data: directions,
    multiple: true
  });
  $('[name="timeEndType"]', $dialog).on('change', function () {
    var $this = $(this);
    var _val = $this.val();
    if (_val === '2') {
      $('#timeEndTypeCtl', $dialog).show();
    } else {
      $('#timeEndTypeCtl', $dialog).hide();
    }
  });

  $dialog.on('mousewheel', 'input[type="number"]', function () {
    return false;
  });
}

function timerInitEvent($dialog, goForm, flowProperty, _timerNames, _name) {
  var timerProperty = null;
  if (_name) {
    timerProperty = goWorkFlow.flowXML.selectSingleNode("./timers/timer[name='" + _name + "']");
  }
  var laArg = {};
  laArg.Names = _timerNames;
  laArg.Form = sGetFormFieldValue(goForm, 'DForm');
  laArg.opener = opener;
  var poForm = $('#timerForm', $dialog)[0];
  if (timerProperty == null) {
    if (StringUtils.isBlank(poForm.timerId.value)) {
      var date = new Date();
      poForm.timerId.value = date.getTime();
    }
    return;
  }
  //名称、id、自动更新时限、时限为空时不计时
  var textInputArr = ['name', 'timerId', 'autoUpdateLimitTime', 'ignoreEmptyLimitTime'];
  $.each(textInputArr, function (i, item) {
    var _value = getSelectSingleNode(timerProperty, item);
    if ($.inArray(item, ['autoUpdateLimitTime', 'ignoreEmptyLimitTime']) > -1) {
      poForm[item].checked = _value === '1' ? true : false;
    } else {
      poForm[item].value = _value;
    }
  });

  //设置方式
  var timerConfigUuid = getSelectSingleNode(timerProperty, 'timerConfigUuid');
  var includeStartTimePoint = getSelectSingleNode(timerProperty, 'includeStartTimePoint');
  var autoDelay = getSelectSingleNode(timerProperty, 'autoDelay');
  var workTimePlanName = getSelectSingleNode(timerProperty, 'workTimePlanName');
  var workTimePlanId = getSelectSingleNode(timerProperty, 'workTimePlanId');

  var limitTimeType = getSelectSingleNode(timerProperty, 'limitTimeType');
  var limitUnit = getSelectSingleNode(timerProperty, 'limitUnit');

  var introductionType = getSelectSingleNode(timerProperty, 'introductionType');
  // if ($.inArray(limitUnit, ['18', '19', '20', '21', '22', '23']) > -1) {
  //   limitTimeType = '3';
  //   $('#limitUnit2', $dialog).wellSelect('val', limitUnit);
  // } else {
  //   $('#limitUnit', $dialog).wellSelect('val', limitUnit);
  // }

  poForm.timingModeUnit.value = limitUnit;
  if ($.inArray(limitUnit, ['3', '1', '2']) > -1) {
    $('#timingMode', $dialog).wellSelect('val', '1').trigger('change');
  } else if ($.inArray(limitUnit, ['13', '11', '12']) > -1) {
    $('#timingMode', $dialog).wellSelect('val', '2').trigger('change');
  } else if ($.inArray(limitUnit, ['86400', '3600', '60']) > -1) {
    $('#timingMode', $dialog).wellSelect('val', '3').trigger('change');
  }
  $('#timingModeUnit', $dialog).wellSelect('val', limitUnit).trigger('change');

  var timeLimitUnit = getSelectSingleNode(timerProperty, 'timeLimitUnit');
  $('#timerConfigUuid', $dialog).val(timerConfigUuid);
  $('#introductionType', $dialog).val(introductionType);
  poForm.limitTimeType.value = limitTimeType;
  $('#limitTimeType', $dialog).wellSelect('val', limitTimeType).trigger('change');
  poForm.includeStartTimePoint.value = includeStartTimePoint;
  $('[name="includeStartTimePoint"][value="' + includeStartTimePoint + '"]', $dialog)
    .prop('checked', true)
    .trigger('change');
  if (limitTimeType === '1') {
    var limitTime = getSelectSingleNode(timerProperty, 'limitTime1');
    $('#timeLimit1', $dialog).val(limitTime);
    $('#timeLimitUnit1', $dialog).wellSelect('val', timeLimitUnit).trigger('change');
  } else if (limitTimeType === '4') {
    var limitTime = getSelectSingleNode(timerProperty, 'limitTime1');
    $('#timeLimitUnit2', $dialog).wellSelect('val', timeLimitUnit).trigger('change');
    $('#timeLimit', $dialog).val(limitTime);
  } else if (limitTimeType === '2') {
    var limitTime = getSelectSingleNode(timerProperty, 'limitTime');
    $('#limitTimeField1', $dialog).wellSelect('val', limitTime);
    $('#timeLimitUnit3', $dialog).wellSelect('val', timeLimitUnit);
  } else if (limitTimeType === '3') {
    var limitTime = getSelectSingleNode(timerProperty, 'limitTime');
    $('#limitTimeField2', $dialog).wellSelect('val', limitTime);
    $('#timeLimitUnit4', $dialog).wellSelect('val', timeLimitUnit);
  }

  poForm.autoDelay.value = autoDelay;
  poForm.workTimePlanName.value = workTimePlanName;
  poForm.workTimePlanId.value = workTimePlanId;

  $('#autoDelay', $dialog).prop('checked', autoDelay == '1' ? true : false);

  if (introductionType == 'introduction') {
    // 计时方式、时限类型 不可编辑
    $('#timingMode', $dialog).wellSelect('disable', true);
    $('#limitTimeType', $dialog).wellSelect('disable', true);
    $('#timingModeUnit', $dialog).wellSelect('disable', true);
    $('#includeStartTimePoint1', $dialog).attr('disabled', true); // 计时方式
    $('#includeStartTimePoint0', $dialog).attr('disabled', true);
    $('#autoDelay', $dialog).attr('disabled', true); // 时限
    if ($('#limitTimeType', $dialog).val() == '1') {
      $('#timeLimit1', $dialog).attr('disabled', true);
      $('#timeLimitUnit1', $dialog).wellSelect('disable', true);
    } else if ($('#limitTimeType', $dialog).val() == '4') {
      $('#timeLimitUnit2', $dialog).wellSelect('disable', true);
      $('#timeLimit', $dialog).attr('disabled', true);
    }
  }

  //环节
  var tasks = aGetXMLFieldValue(timerProperty, 'tasks');
  if (tasks != null) {
    $('#tasks', $dialog).wellSelect('val', tasks);
  }

  //子流程环节
  var subTasks = timerProperty.selectSingleNode('subTasks');
  if (subTasks) {
    var subTaskArr = subTasks.selectNodes('subTask');
    $.each(subTaskArr, function (i, item) {
      var x2js = new X2JS();
      var subTask = x2js.xml2js(item.outerHTML).subTask;
      subTask.index = i;
      if (subTask.timers) {
        subTask.timers = $.isArray(subTask.timers.timer) ? subTask.timers.timer : [subTask.timers.timer];
        console.log(subTask);
      }
      addSubTask(subTask, $dialog);
    });
  }

  //预警提醒
  var enableAlarm = getSelectSingleNode(timerProperty, 'enableAlarm');
  if (enableAlarm === '1') {
    $('#enableAlarm', $dialog).trigger('click');
  }
  var alarms = timerProperty.selectSingleNode('alarms');
  //多个预警提醒
  if (alarms) {
    var alarmArr = alarms.selectNodes('alarm');
    $.each(alarmArr, function (i, item) {
      addAlarm($dialog, $(item), poForm, i + 1);
    });
  } else {
    addAlarm($dialog, timerProperty, poForm, 1);
  }

  //逾期提醒
  var enableDueDoing = getSelectSingleNode(timerProperty, 'enableDueDoing');
  if (enableDueDoing === '1') {
    $('#enableDueDoing', $dialog).trigger('click');
    renderDueDoing($dialog, timerProperty, poForm);
  }

  //逾期时
  var dueAction = getSelectSingleNode(timerProperty, 'dueAction');
  $('#dueAction', $dialog).wellSelect('val', dueAction);

  var dueToTask = getSelectSingleNode(timerProperty, 'dueToTask');
  $('#dueToTask', $dialog).wellSelect('val', dueToTask);

  setUnitXMLFieldValue(poForm, timerProperty, ['dueToUser']);

  //计时结束流向
  var timeEndType = getSelectSingleNode(timerProperty, 'timeEndType');
  var overDirections = getSelectSingleNode(timerProperty, 'overDirections');

  if ((!timeEndType || timeEndType == '2') && overDirections) {
    timeEndType = '2';
    $('#overDirections', $dialog).wellSelect('val', overDirections);
  }
  poForm.timeEndType.value = timeEndType;
  $('[name="timeEndType"]:checked', $dialog).trigger('change');
}

function addAlarm($dialog, timerProperty, poForm, index) {
  $('#addAlarmObjects', $dialog).trigger('click');
  var alarmTime = getSelectSingleNode(timerProperty, 'alarmTime');
  var alarmUnit = getSelectSingleNode(timerProperty, 'alarmUnit');
  var alarmFrequency = getSelectSingleNode(timerProperty, 'alarmFrequency');
  var alarmObjects = aGetXMLFieldValue(timerProperty, 'alarmObjects');
  var alarmUsers = timerProperty.selectSingleNode('alarmUsers');
  var unit = alarmUsers ? alarmUsers.selectNodes('unit') : [];
  var data = {
    alarmTime: alarmTime,
    alarmUnit: alarmUnit,
    alarmFrequency: alarmFrequency,
    alarmObjects: alarmObjects
  };
  var alarmUsersData = {
    unitLabel: [],
    unitValue: [],
    formField: [],
    tasks: [],
    options: []
  };
  setAlarmValue($dialog, poForm, data, index);
  $.each(unit, function (i, item) {
    var type = item.getAttribute('type');
    var $item = $(item);
    switch (type) {
      case '1':
        alarmUsersData.unitLabel.push($item.selectSingleNode('argValue').text());
        alarmUsersData.unitValue.push($item.selectSingleNode('value').text());
        break;
      case '2':
        alarmUsersData.formField.push($item.selectSingleNode('value').text());
        break;
      case '4':
        alarmUsersData.tasks.push($item.selectSingleNode('value').text());
        break;
      case '8':
        alarmUsersData.options.push($item.selectSingleNode('value').text());
        break;
    }
  });
  renderUnitField(alarmUsersData, poForm, 'alarmUser_' + index + '_');
}

function renderDueDoing($dialog, timerProperty, poForm) {
  var dueTime = getSelectSingleNode(timerProperty, 'dueTime');
  var dueUnit = getSelectSingleNode(timerProperty, 'dueUnit');
  var dueFrequency = getSelectSingleNode(timerProperty, 'dueFrequency');
  var dueObjects = aGetXMLFieldValue(timerProperty, 'dueObjects');
  var dueUsers = timerProperty.selectSingleNode('dueUsers');
  var unit = dueUsers.selectNodes('unit');
  var dueUsersData = {
    unitLabel: [],
    unitValue: [],
    formField: [],
    tasks: [],
    options: []
  };
  $.each(unit, function (i, item) {
    var type = item.getAttribute('type');
    var $item = $(item);
    switch (type) {
      case '1':
        dueUsersData.unitLabel.push($item.selectSingleNode('argValue').text());
        dueUsersData.unitValue.push($item.selectSingleNode('value').text());
        break;
      case '2':
        dueUsersData.formField.push($item.selectSingleNode('value').text());
        break;
      case '4':
        dueUsersData.tasks.push($item.selectSingleNode('value').text());
        break;
      case '8':
        dueUsersData.options.push($item.selectSingleNode('value').text());
        break;
    }
  });
  renderUnitField(dueUsersData, poForm, 'dueUser');
  var data = {
    dueTime: dueTime,
    dueUnit: dueUnit,
    dueFrequency: dueFrequency,
    dueObjects: dueObjects
  };
  $.each(data, function (i, item) {
    if (i === 'dueObjects') {
      var checkboxCtl = $('[name="' + i + '"]', poForm);
      $.each(checkboxCtl, function () {
        var $this = $(this);
        var _val = $this.val();
        if (item.indexOf(_val) > -1) {
          $this.attr('checked', 'checked');
          if (_val === 'Other') {
            $this.closest('.checkbox').next().show();
          }
        }
      });
    } else {
      poForm[i].value = item;
      if (i === 'dueUnit') {
        $('#dueUnit', $dialog).wellSelect('val', item);
      }
    }
  });

  $dialog.on('change', '[name="dueObjects"]', function () {
    var $this = $(this);
    var hasOther = false;
    $.each($('[name="dueObjects"]:checked', $dialog), function () {
      if ($(this).val() === 'Other') {
        hasOther = true;
      }
    });
    if (hasOther) {
      $this.closest('.checkbox').next().show();
    } else {
      $this.closest('.checkbox').next().hide();
    }
  });
}

function setAlarmValue($dialog, poForm, data, index) {
  $.each(data, function (i, item) {
    var _name = i + '_' + index;
    if (i === 'alarmObjects') {
      var checkboxCtl = $('[name="' + _name + '"]', poForm);
      $.each(checkboxCtl, function () {
        var $this = $(this);
        var _val = $this.val();
        if (item && item.indexOf(_val) > -1) {
          $this.attr('checked', 'checked');
          if (_val === 'Other') {
            $this.closest('.checkbox').next().show();
          }
        }
      });
    } else {
      if (i === 'alarmUnit') {
        $('[name="' + _name + '"]', $dialog).wellSelect('val', item);
      } else {
        poForm[_name] && (poForm[_name].value = item);
      }
    }
  });
}

function openTimerDialog(goForm, flowProperty, _timerNames, _name) {
  var laArg = {};
  laArg.Names = _timerNames;
  laArg.Form = sGetFormFieldValue(goForm, 'DForm');
  laArg.opener = opener;
  var _html = get_timer_popup_html();
  var $dialog = top.appModal.dialog({
    title: '计时器',
    message: _html,
    size: 'large',
    zIndex: 999999998,
    shown: function () {
      timerLoadEvent($dialog, goForm, flowProperty, _timerNames, _name);
      timerInitEvent($dialog, goForm, flowProperty, _timerNames, _name);
    },
    buttons: {
      save: {
        label: '保存',
        className: 'well-btn w-btn-primary',
        callback: function () {
          var form = $('#timerForm', $dialog)[0];
          var timerProperty = null;
          if (_name) {
            timerProperty = goWorkFlow.flowXML.selectSingleNode("./timers/timer[name='" + _name + "']");
          }
          if (!TimerOKCheck($dialog, form, timerProperty, laArg)) {
            return false;
          }
          var newTimerName = TimerOKEvent($dialog, form, timerProperty, laArg);
          top.appModal.success('保存成功！');
          if (_name) {
            $('#timerList li[data-name="' + _name + '"]', goForm)
              .attr('data-name', newTimerName)
              .find('.name')
              .text(newTimerName);
          } else {
            $('#timerList', goForm).append(
              '<li data-name="' +
              newTimerName +
              '">' +
              '<div class="name">' +
              newTimerName +
              '</div>' +
              '<div class="btn-group">' +
              '<div class="well-btn w-btn-primary well-btn-sm w-noLine-btn btn-set" title="设置">' +
              '<i class="iconfont icon-ptkj-shezhi"></i>' +
              '</div>' +
              '<div class="well-btn w-btn-primary well-btn-sm w-noLine-btn btn-remove" title="删除">' +
              '<i class="iconfont icon-ptkj-shanchu"></i>' +
              '</div>' +
              '</div>' +
              '</li>'
            );
          }
          // // 根据计时器配置，更新所有环节相应的图标
          // goWorkFlow.updateAllTaskImageWithTimers();
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

function TimerOKCheck($dialog, goForm, timerProperty, laArg) {
  var lsOldName = '';
  if (timerProperty != null && timerProperty.selectSingleNode('name') != null) {
    lsOldName = timerProperty.selectSingleNode('name').text();
  }
  if (goForm.name.value === '') {
    top.appModal.error('请输入计时器名称！');
    return false;
  }
  var gsExistNames = laArg.Names;
  if (gsExistNames.indexOf(goForm.name.value) > -1 && goForm.name.value !== lsOldName) {
    top.appModal.error('名称已存在，请重新输入！');
    goForm.name.focus();
    return false;
  }
  if (goForm.timingMode.value == '') {
    top.appModal.error('请选择计时方式！');
    return false;
  }
  if (goForm.limitTimeType.value === '') {
    top.appModal.error('请选择时限类型！');
    return false;
  }
  // 固定时限
  if (goForm.limitTimeType.value === '1' && (goForm.timeLimit1.value === '' || goForm.timeLimitUnit1.value == '')) {
    top.appModal.error('时限不能为空！');
    return false;
  }
  if (goForm.limitTimeType.value === '1' && goForm.timeLimit1.value - 0 <= 0) {
    top.appModal.error('时限只能输入正数！');
    return false;
  }
  // 固定截止时间
  if (goForm.limitTimeType.value === '4' && (goForm.timeLimitUnit2.value === '' || goForm.timeLimit.value == '')) {
    top.appModal.error('截止时间不能为空！');
    return false;
  }
  // 动态时限  $('#limitTime2', $dialog).wellSelect('data').id === ''
  if (goForm.limitTimeType.value === '2' && (goForm.limitTimeField1.value === '' || goForm.timeLimitUnit3.value == '')) {
    top.appModal.error('时限字段不能为空！');
    return false;
  }
  // 动态截止时间
  if (goForm.limitTimeType.value === '3' && (goForm.timeLimitUnit4.value === '' || goForm.limitTimeField2.value == '')) {
    top.appModal.error('截止时间字段不能为空！');
    return false;
  }
  if (goForm.workTimePlanId.value == '' || goForm.workTimePlanId.value == '') {
    top.appModal.error('请选择工作时间！');
    return false;
  }
  if (goForm.tasks.value === '' && $('#subTaskList > li', $dialog).length === 0) {
    top.appModal.error('计时环节(普通)或者计时环节(子流程)必选其一！');
    return false;
  }
  var otherStatus = true;
  var errorOtherArr = [];
  $('#alarmObjectsList > li', $dialog).each(function (i, item) {
    var index = i + 1;
    var $orgSelect = $('#DalarmUser_' + index + '_s', $dialog).find('.org-select');
    if ($('#alarmObjects_Other_' + index, $dialog).attr('checked') && !$orgSelect.find('li').length) {
      otherStatus = false;
      $orgSelect.addClass('control-error');
      errorOtherArr.push(index);
    }
  });
  if (errorOtherArr.length) {
    top.appModal.error('第' + errorOtherArr.join('、') + '条预警提醒未完善其他人员！');
    return false;
  }
  if (!otherStatus) {
    return false;
  }
  return true;
}

function TimerOKEvent($dialog, goForm, timerProperty, laArg) {
  if (timerProperty == null) {
    timerProperty = $(goWorkFlow.xmlDOM.createElement('timer'));
    var loNode = goWorkFlow.flowXML.selectSingleNode('timers');
    loNode.appendChild(timerProperty);
  }
  bSetXMLFieldValue(timerProperty, 'tasks', goForm.tasks.value !== '' ? goForm.tasks.value.split(';') : null);
  var alarmsLen = $('#alarmObjectsList > li', $dialog).length;
  if (alarmsLen) {
    var alarms = oSetElement(timerProperty, 'alarms');
    var alarmArr = alarms.selectNodes('alarm');
    $.each(alarmArr, function (i, item) {
      $(item).remove();
    });
  }
  for (var i = 1; i <= alarmsLen; i++) {
    var alarm = oAddElement(alarms, 'alarm');
    var fields = ['alarmTime', 'alarmUnit', 'alarmFrequency'];
    $.each(fields, function (k, item) {
      var _fieldName = item + '_' + i;
      oSetElement(alarm, item, sGetFormFieldValue(goForm, _fieldName));
    });
    var lsValue = sGetFormFieldValue(goForm, 'alarmObjects_' + i);
    bSetXMLFieldValue(alarm, 'alarmObjects', lsValue !== '' ? lsValue.split(';') : null);
    bSetUnitFieldToXML(alarm, goForm, 'alarmUser', 'alarmUser_' + i + '_');
  }
  var lsValue = sGetFormFieldValue(goForm, 'dueObjects');
  bSetXMLFieldValue(timerProperty, 'dueObjects', lsValue !== '' ? lsValue.split(';') : null);
  var laField = ['dueUser', 'dueToUser'];
  for (var i = 0; i < laField.length; i++) {
    bSetUnitFieldToXML(timerProperty, goForm, laField[i]);
  }
  if ($('#subTaskList > li', $dialog).length) {
    var subTasks = oSetElement(timerProperty, 'subTasks');
    var subTasksArr = subTasks.selectNodes('subTask');
    $.each(subTasksArr, function (i, item) {
      $(item).remove();
    });
    $('#subTaskList > li', $dialog).each(function () {
      console.log($(this).data(), '====');
      var subTaskData = $(this).data('subTask');
      var subTask = oAddElement(subTasks, 'subTask');
      oSetElement(subTask, 'taskId', subTaskData.taskId);
      oSetElement(subTask, 'taskName', subTaskData.taskName);
      oSetElement(subTask, 'timingMode', subTaskData.timingMode);
      if (subTaskData.timingMode === '2') {
        var _timers = oSetElement(subTask, 'timers');
        $.each(subTaskData.timers, function (i, item) {
          var _timer = oAddElement(_timers, 'timer');
          oSetElement(_timer, 'newFlowId', item.newFlowId);
          oSetElement(_timer, 'newFlowTimerId', item.newFlowTimerId);
          oSetElement(_timer, 'newFlowTimerName', item.newFlowTimerName);
        });
      }
    });
  } else {
    var subTasks = oSetElement(timerProperty, 'subTasks');
    subTasks.remove();
  }

  laField = [
    'name',
    'timerId',
    'includeStartTimePoint',
    'limitTimeType',
    'limitTime',
    'workTimePlanName',
    'workTimePlanId',
    'autoUpdateLimitTime',
    'ignoreEmptyLimitTime',
    'overDirections',
    'enableAlarm',
    'enableDueDoing',
    'dueTime',
    'dueUnit',
    'dueFrequency',
    'dueAction',
    'dueToTask',
    'timeEndType'
  ];
  for (var i = 0; i < laField.length; i++) {
    oSetElement(timerProperty, laField[i], sGetFormFieldValue(goForm, laField[i]));
  }
  var limitTimeTypeV = sGetFormFieldValue(goForm, 'limitTimeType');
  if (limitTimeTypeV === '1') {
    oSetElement(timerProperty, 'limitTime1', sGetFormFieldValue(goForm, 'timeLimit1'));
    oSetElement(timerProperty, 'timeLimitUnit', sGetFormFieldValue(goForm, 'timeLimitUnit1'));
  } else if (limitTimeTypeV === '4') {
    oSetElement(timerProperty, 'timeLimitUnit', sGetFormFieldValue(goForm, 'timeLimitUnit2'));
    oSetElement(timerProperty, 'limitTime1', sGetFormFieldValue(goForm, 'timeLimit'));
  } else if (limitTimeTypeV === '2') {
    oSetElement(timerProperty, 'timeLimitUnit', sGetFormFieldValue(goForm, 'timeLimitUnit3'));
    oSetElement(timerProperty, 'limitTime', sGetFormFieldValue(goForm, 'limitTimeField1'));
  } else if (limitTimeTypeV === '3') {
    oSetElement(timerProperty, 'timeLimitUnit', sGetFormFieldValue(goForm, 'timeLimitUnit4'));
    oSetElement(timerProperty, 'limitTime', sGetFormFieldValue(goForm, 'limitTimeField2'));
  }

  oSetElement(timerProperty, 'includeStartTimePoint', $('[name="includeStartTimePoint"]:checked', $dialog).val());
  oSetElement(timerProperty, 'autoDelay', $('#autoDelay', $dialog).prop('checked') ? 1 : 0);
  oSetElement(timerProperty, 'limitUnit', $('#timingModeUnit', $dialog).val());

  oSetElement(timerProperty, 'timerConfigUuid', $('#timerConfigUuid', $dialog).val());
  oSetElement(timerProperty, 'introductionType', $('#introductionType', $dialog).val());

  return goForm.name.value;
}

function getDistributeConditionHtml(index) {
  return (
    '<li class="clear" data-index="' +
    index +
    '">' +
    '<div class="fl" style="width: calc(22% - 5px); margin-right: 5px">' +
    '<input type="text" class="form-control" id="distributeConditionType_' +
    index +
    '" name="distributeConditionType_' +
    index +
    '">' +
    '</div>' +
    '<div class="fl" style="width: calc(35% - 5px); margin-right: 5px">' +
    '<input type="text" class="form-control" id="distributeConditionField_' +
    index +
    '" name="distributeConditionField_' +
    index +
    '">' +
    '</div>' +
    '<div class="fl" style="width: calc(15% - 5px); margin-right: 5px">' +
    '<input type="text" class="form-control" id="distributeConditionSymbol_' +
    index +
    '" name="distributeConditionSymbol_' +
    index +
    '">' +
    '</div>' +
    '<div class="fl" style="width: 28%">' +
    '<input type="text" class="form-control" id="distributeConditionText_' +
    index +
    '" name="distributeConditionText_' +
    index +
    '">' +
    '</div>' +
    '<div class="delete-btn well-btn w-btn-primary w-noLine-btn well-btn-sm">' +
    '<i class="iconfont icon-ptkj-shanchu"></i>' +
    '</div>' +
    '</li>'
  );
}

function getDistributeNodeItemHtml(index) {
  return (
    '<li class="clear" data-index="' +
    index +
    '">' +
    '<div class="fl" style="width: calc(20% - 5px); margin-right: 5px">' +
    '<input type="text" class="form-control" id="distributeNodeType_' +
    index +
    '" name="distributeNodeType_' +
    index +
    '">' +
    '</div>' +
    '<div class="fl" id="distributeNodeTypeControl_' +
    index +
    '" style="width: 80%">' +
    '<div class="assignCtl" id="assignNode_' +
    index +
    '" style="display: none">' +
    '<input type="text" class="form-control" id="assignNodeCtl_' +
    index +
    '" name="assignNodeCtl_' +
    index +
    '">' +
    '</div>' +
    '<div class="assignCtl" id="assignDirection_' +
    index +
    '" style="display: none">' +
    '<input type="text" class="form-control" id="assignDirectionCtl_' +
    index +
    '" name="assignDirectionCtl_' +
    index +
    '">' +
    '</div>' +
    '<div class="assignCtl clear" id="assignTurn_' +
    index +
    '" style="display: none">' +
    '<div class="text">从</div>' +
    '<div class="half-ctl">' +
    '<input type="text" class="form-control" id="assignTurnFromCtl_' +
    index +
    '" name="assignTurnFromCtl_' +
    index +
    '">' +
    '</div>' +
    '<div class="text">至</div>' +
    '<div class="half-ctl">' +
    '<input type="text" class="form-control" id="assignTurnToCtl_' +
    index +
    '" name="assignTurnToCtl_' +
    index +
    '">' +
    '</div>' +
    '</div>' +
    '</div>' +
    '<div class="delete-btn well-btn w-btn-primary w-noLine-btn well-btn-sm">' +
    '<i class="iconfont icon-ptkj-shanchu"></i>' +
    '</div>' +
    '</li>'
  );
}

function getAlarmObjectsHtml(index) {
  return (html =
    '<li data-index="' +
    index +
    '">' +
    '<div class="timerWarnBox-item">' +
    '<span>提前</span>' +
    '<input class="form-control num" type="number" name="alarmTime_' +
    index +
    '" value="1" min="0">' +
    '<div class="select">' +
    '<input class="form-control alarmUnitSelect" name="alarmUnit_' +
    index +
    '" value="2">' +
    '</div>' +
    '<span> 开始消息提醒，共</span>' +
    '<input class="form-control num" type="number" name="alarmFrequency_' +
    index +
    '" value="1">' +
    '<span>次</span>' +
    '</div>' +
    '<div class="timerWarnBox-item" style="padding-left: 30px">' +
    '<div class="label">提醒</div>' +
    '<div class="checkbox">' +
    '<input type="checkbox" checked name="alarmObjects_' +
    index +
    '" id="alarmObjects_Doing_' +
    index +
    '" value="Doing">' +
    '<label class="checkbox-inline" for="alarmObjects_Doing_' +
    index +
    '">在办人员</label>' +
    '<input type="checkbox" name="alarmObjects_' +
    index +
    '" id="alarmObjects_DoingSuperior_' +
    index +
    '" value="DoingSuperior">' +
    '<label class="checkbox-inline" for="alarmObjects_DoingSuperior_' +
    index +
    '">在办人员上级领导</label>' +
    '<input type="checkbox" name="alarmObjects_' +
    index +
    '" id="alarmObjects_Monitor_' +
    index +
    '" value="Monitor">' +
    '<label class="checkbox-inline" for="alarmObjects_Monitor_' +
    index +
    '">督办人员</label>' +
    '<input type="checkbox" name="alarmObjects_' +
    index +
    '" id="alarmObjects_Tracer_' +
    index +
    '" value="Tracer">' +
    '<label class="checkbox-inline" for="alarmObjects_Tracer_' +
    index +
    '">跟踪人员</label>' +
    '<input type="checkbox" name="alarmObjects_' +
    index +
    '" id="alarmObjects_Admin_' +
    index +
    '" value="Admin">' +
    '<label class="checkbox-inline" for="alarmObjects_Admin_' +
    index +
    '">流程管理人员</label>' +
    '<input type="checkbox" name="alarmObjects_' +
    index +
    '" id="alarmObjects_Other_' +
    index +
    '" value="Other">' +
    '<label class="checkbox-inline" for="alarmObjects_Other_' +
    index +
    '">其他人员</label>' +
    '</div>' +
    '<div class="org-select-container org-style3 org-select-placeholder" style="display: none" data-id="alarmUser_' +
    index +
    '_" id="DalarmUser_' +
    index +
    '_s">' +
    '<input type="hidden" id="DalarmUsers_' +
    index +
    '_1" name="DalarmUser_' +
    index +
    '_1">' +
    '<input type="hidden" id="alarmUsers_' +
    index +
    '_1" name="alarmUser_' +
    index +
    '_1">' +
    '<input type="hidden" id="alarmUsers_' +
    index +
    '_2" name="alarmUser_' +
    index +
    '_2">' +
    '<input type="hidden" id="alarmUsers_' +
    index +
    '_4" name="alarmUser_' +
    index +
    '_4">' +
    '<input type="hidden" id="alarmUsers_' +
    index +
    '_8" name="alarmUser_' +
    index +
    '_8">' +
    '<ul class="org-select"></ul>' +
    '<i class="icon iconfont icon-ptkj-zuzhixuanze"></i>' +
    '</div>' +
    '</div>' +
    '<div class="delete-btn well-btn w-btn-primary w-noLine-btn well-btn-sm">' +
    '<i class="iconfont icon-ptkj-shanchu"></i>' +
    '</div>' +
    '</li>');
}

function loadFormDefinition(uuid) {
  var definitionObj = null;
  if (!uuid) {
    return null;
  }
  $.ajax({
    url: '/pt/dyform/definition/getFormDefinition',
    async: false, // 同步完成
    type: 'POST',
    data: {
      formUuid: uuid,
      justDataAndDef: false
    },
    dataType: 'json',
    success: function (data) {
      definitionObj = data;
    },
    error: function (data) {
      // 加载定义失败
    }
  });
  goWorkFlow.DformDefinition = definitionObj;
  goWorkFlow.formFields = $.map(definitionObj.fields, function (item) {
    return {
      id: item.name,
      text: item.displayName
    };
  });
  goWorkFlow.formGroupData = [
    {
      text: '主表',
      children: [
        {
          id: definitionObj.uuid,
          text: '主表'
        }
      ]
    }
  ];
  goWorkFlow.subFormFields = {};
  $.each(definitionObj.subforms, function (i, item) {
    if (!goWorkFlow.formGroupData[1]) {
      goWorkFlow.formGroupData[1] = {
        text: '从表',
        children: []
      };
    }
    goWorkFlow.formGroupData[1].children.push({
      id: i,
      text: item.displayName
    });
    goWorkFlow.subFormFields[i] = $.map(item.fields, function (item2) {
      return {
        id: item2.name,
        text: item2.displayName
      };
    });
  });
  return definitionObj;
}

function FlowLoadEvent(flowProperty) {
  forbidEditRefFlow();
  var goForm = $('#flowForm')[0];
  if (goForm == null) {
    return;
  }
  flowBaseProperty(goForm, flowProperty);
  flowAuthProperty(goForm, flowProperty);
  flowTimerProperty(goForm, flowProperty);
  flowMoreProperty(goForm, flowProperty);
  goWorkFlow.equalFlowID = null;
  top.appModal.hideMask();
}

function FlowInitEvent2(flowProperty) {
  var goForm = $('#flowForm')[0];
  if (goForm == null) {
    return;
  }
  $('#btn_save', goForm)
    .button()
    .click(function (e) {
      FlowOKEvent(flowProperty);
      $('body').trigger('ace_$saveCodeHis'); //触发代码变更历史保存
    });
}

/*
 * 当全局设置中的表单设置更改时触发
 * 设置task中的编辑域、只读域、隐藏域、必填域、隐藏区块，如果这新属性在新的表单中存在则不改变，否则清除掉不存在的属性设置
 * lmw 2015-4-27 12:33
 */
function globalSetChangedEvent() {
  // 获取全局表单设置
  var loNode = goWorkFlow.flowXML.selectSingleNode('./property/formID');

  // 获取新选择的表单设置
  var f = $('#flowForm').find('#DForm').val(); // 新的表单设置项
  var globalFormUuid = loNode != null ? loNode.text() : ''; // 原有表单设置项

  // 全局表单设置没有更改时直接返回
  if (globalFormUuid === f) {
    return;
  }
}

// 加载脚本定义
function loadScriptDefinition($scriptDefinition, type) {
  $scriptDefinition.wSelect2({
    serviceName: 'cdScriptDefinitionFacadeService',
    queryMethod: 'loadSelectData',
    valueField: $scriptDefinition.attr('id'),
    placeholder: '请选择',
    params: {
      type: type
    },
    multiple: false,
    remoteSearch: false,
    width: '100%',
    height: 250
  });
}

function onGetFormFields(select, form, data) {
  $(select, form).html('');
  $.each(data, function () {
    var option = '<option value="' + this.data + '">' + this.name + '</option>';
    if (select == 'select[name=fields1]') {
      var createWay = $('#createWay', $('#newFlowForm')).val();
      var subformIdName = $('#subformId > option:selected', $('#newFlowForm')).text();
      if (this.children.length > 0 && createWay == '2' && subformIdName == this.name) {
        $.each(this.children, function () {
          if (this.name != '添加删除行') {
            var option = '<option value="' + this.data + '">' + subformIdName + '.' + this.name + '</option>';
            $(select, form).append(option);
          }
        });
      } else {
        $(select, form).append(option);
      }
    } else {
      $(select, form).append(option);
    }
  });
}

function collectPropertiesData() {
  //清空的时候newWorkFlowDesigner页面会刷新会生成新的goWorkFlow对象，配置页面未重新定义goWorkflow，这边重新定义
  goWorkFlow = top.goWorkFlow;
  var flowXML = goWorkFlow.flowXML;
  var flowProperty = flowXML.selectSingleNode('property');
  var userDetail = SpringSecurityUtils.getUserDetails();
  var goForm = $('#flowForm')[0];
  collectBasePropertiesData(flowXML, flowProperty, goForm, userDetail);
  collectAuthPropertiesData(flowXML, flowProperty, goForm, userDetail);
  collectTimePropertiesData(flowXML, flowProperty, goForm, userDetail);
  collectMorePropertiesData(flowXML, flowProperty, goForm, userDetail);
  return true;
}

//基本属性-收集数据
function collectBasePropertiesData(flowXML, flowProperty, goForm, userDetail) {
  flowXML.attr('name', $('#flow_name').val());
  flowXML.attr('id', $('#flow_id').val());
  flowXML.attr('code', $('#code').val());
  flowXML.attr('systemUnitId', userDetail.systemUnitId);
  flowXML.attr('moduleId', $('#moduleId').val());
  if ($('[name="title_expression"]:checked').val() === 'custom') {
    flowXML.attr('titleExpression', $('#title_expression_text').text());
  } else {
    flowXML.attr('titleExpression', '');
  }
  flowXML.attr('version', $('#flow_version').val());
  oSetElement(flowProperty, 'categorySN', $('#DCategory').val());
  oSetElement(flowProperty, 'moduleId', $('#moduleId').val());
  oSetElement(flowProperty, 'formID', $('#DForm').val());
  oSetElement(flowProperty, 'isActive', $('[name="isActive"]:checked').val());
  oSetElement(flowProperty, 'pcShowFlag', $('[name="pcShowFlag"]:checked').val());
  oSetElement(flowProperty, 'isMobileShow', $('[name="isMobileShow"]:checked').val());
  oSetElement(flowProperty, 'remark', $('#remark').val());
  oSetElement(flowProperty, 'autoUpdateTitle', $('#autoUpdateTitle').attr('checked') ? '1' : '0');
}

//流程权限-收集数据
function collectAuthPropertiesData(flowXML, flowProperty, goForm, userDetail) {
  if ($('[name="creator"]:checked').val() === 'custom') {
    bSetUnitFieldToXML(flowProperty, goForm, 'creator');
  } else {
    if (flowProperty.selectSingleNode('creators')) {
      flowProperty.selectSingleNode('creators').remove();
    }
    oSetElement(flowProperty, 'creators', '');
  }
  if ($('[name="user"]:checked').val() === 'custom') {
    bSetUnitFieldToXML(flowProperty, goForm, 'user');
  } else {
    if (flowProperty.selectSingleNode('users')) {
      flowProperty.selectSingleNode('users').remove();
    }
    oSetElement(flowProperty, 'users', '');
  }
  var laField = ['monitor', 'admin', 'viewer'];
  $.each(laField, function (i, item) {
    bSetUnitFieldToXML(flowProperty, goForm, item);
  });
  oSetElement(flowProperty, 'keepRuntimePermission', $('#keepRuntimePermission').attr('checked') ? '1' : '');
  oSetElement(flowProperty, 'granularity', $('#granularity').val());

  oSetElement(flowProperty, 'enableAccessPermissionProvider', $('[name="enableAccessPermissionProvider"]:checked').val());
  oSetElement(flowProperty, 'accessPermissionProvider', $('#accessPermissionProvider').val());
  oSetElement(flowProperty, 'accessPermissionProviderName', $('#accessPermissionProviderName').val());
  oSetElement(flowProperty, 'onlyUseAccessPermissionProvider', $('#onlyUseAccessPermissionProvider').attr('checked') ? '1' : '');
}

//流程计时-收集数据
function collectTimePropertiesData(flowXML, flowProperty, goForm, userDetail) {
  //直接保存的xml 无需收集
  oSetElement(flowProperty, 'timerListener', $('#timerListener').val());
  oSetElement(flowProperty, 'timerListenerName', $('#timerListenerName').val());
}

//高级属性-收集数据
function collectMorePropertiesData(flowXML, flowProperty, goForm, userDetail) {
  var equalFlow = flowProperty.selectSingleNode('equalFlow');
  if (equalFlow) {
    flowProperty.removeChild(equalFlow);
  }
  equalFlow = oSetElement(flowProperty, 'equalFlow');
  if ($('#EQFlowSwitch').hasClass('active')) {
    oSetElement(equalFlow, 'id', $('#EQFlowID').val());
    // 标记没有选择等价流程
    if (StringUtils.isBlank($('#EQFlowID').val())) {
      oSetElement(equalFlow, 'name', '-1');
    } else {
      oSetElement(equalFlow, 'name', $('#EQFlowName').val());
    }
  } else {
    oSetElement(equalFlow, 'id', '');
    oSetElement(equalFlow, 'name', '');
  }
  oSetElement(flowProperty, 'isFree', $('[name="isFree"]:checked').val());
  flowXML.attr('applyId', $('#apply_id').val());
  oSetElement(flowProperty, 'useDefaultOrg', $('input[name="useDefaultOrg"]:checked').val());
  oSetElement(flowProperty, 'orgId', $('#orgId').val());
  oSetElement(flowProperty, 'enableMultiOrg', $('input[name="enableMultiOrg"]:checked').val());
  oSetElement(flowProperty, 'multiOrgId', $('#multiOrgId').val());
  oSetElement(flowProperty, 'autoUpgradeOrgVersion', $('input[name="autoUpgradeOrgVersion"]:checked').val());
  oSetElement(flowProperty, 'multiJobFlowType', $('#multiJobFlowType').val());
  oSetElement(flowProperty, 'jobField', $('#jobField').val());

  var bakUsers = flowProperty.selectSingleNode('bakUsers');
  if (bakUsers) {
    flowProperty.removeChild(bakUsers);
  }
  bakUsers = oSetElement(flowProperty, 'bakUsers');
  $('#DBackUser > li').each(function () {
    var $this = $(this);
    var index = $this.attr('data-index');
    var userA = $('#AUser_' + index).val() + '|' + $('#AUserID_' + index).val();
    var userB = $('#BUsers_' + index).val() + '|' + $('#BUserIDs_' + index).val();
    var loUnit = oAddElement(bakUsers, 'unit');
    loUnit.setAttribute('type', '16');
    oAddElement(loUnit, 'value', userA);
    oAddElement(loUnit, 'argValue', userB);
  });

  var messageTemplates = flowProperty.selectSingleNode('messageTemplates');
  if (messageTemplates) {
    flowProperty.removeChild(messageTemplates);
  }
  messageTemplates = oSetElement(flowProperty, 'messageTemplates');
  $('#DMessageTemplate > li').each(function () {
    var $this = $(this);
    var templateData = $this.data('template');
    var template = oAddElement(messageTemplates, 'template');
    oSetElement(template, 'type', templateData.type);
    oSetElement(template, 'typeName', templateData.typeName);
    oSetElement(template, 'id', templateData.id);
    oSetElement(template, 'name', templateData.name);
    oSetElement(template, 'isSendMsg', templateData.isSendMsg);
    if (!templateData.distributers) {
      //旧数据
      oSetElement(template, 'condition', templateData.condition);
      oSetElement(template, 'extraMsgRecipients', templateData.extraMsgRecipients);
      oSetElement(template, 'extraMsgRecipientUserIds', templateData.extraMsgRecipientUserIds);
      oSetElement(template, 'extraMsgCustomRecipients', templateData.extraMsgCustomRecipients);
      oSetElement(template, 'extraMsgCustomRecipientUserIds', templateData.extraMsgCustomRecipientUserIds);
    } else {
      oSetElement(template, 'conditionEnable', templateData.conditionEnable || '');
      oSetElement(template, 'condExpressionSignal', templateData.condExpressionSignal || '');
      var distributers = oSetElement(template, 'distributers');
      $.each(templateData.distributers || [], function (i, item) {
        var distributer = oAddElement(distributers, 'distributer');
        oSetElement(distributer, 'dtypeName', item.dtypeName);
        oSetElement(distributer, 'id', item.id);
        oSetElement(distributer, 'name', item.value || '');
        var designees = oSetElement(distributer, 'designees');
        if (item.designees) {
          bSetUnitFieldDataToXML(designees, item.designees);
        }
      });

      var copyMsgRecipients = oSetElement(template, 'copyMsgRecipients');
      bSetUnitFieldDataToXML(copyMsgRecipients, templateData.copyMsgRecipients || {});

      var distributions = oSetElement(template, 'distributions');
      $.each(templateData.distributions || [], function (i, item) {
        var distribution = oAddElement(distributions, 'distribution');
        oSetElement(distribution, 'dtype', item.dtype);
        oSetElement(distribution, 'value', item.value);
      });

      var conditions = oSetElement(template, 'conditions');
      $.each(templateData.conditions || [], function (i, item) {
        var condition = oAddElement(conditions, 'condition');
        oSetElement(condition, 'ctype', item.ctype);
        oSetElement(condition, 'code', item.code);
        oSetElement(condition, 'symbols', item.symbols);
        oSetElement(condition, 'value', item.value);
      });
    }
  });

  var records = flowProperty.selectSingleNode('records');
  if (records) {
    flowProperty.removeChild(records);
  }
  records = oSetElement(flowProperty, 'records');
  $('#DRecord > li').each(function () {
    var $this = $(this);
    var recordData = $this.data('record');
    var record = oAddElement(records, 'record');
    oSetElement(record, 'name', recordData.name);
    oSetElement(record, 'field', recordData.field);
    oSetElement(record, 'way', recordData.way);
    oSetElement(record, 'assembler', recordData.assembler);
    oSetElement(record, 'contentOrigin', recordData.contentOrigin);
    oSetElement(record, 'ignoreEmpty', recordData.ignoreEmpty);
    oSetElement(record, 'enableWysiwyg', recordData.enableWysiwyg);
    oSetElement(record, 'fieldNotValidate', recordData.fieldNotValidate);
    oSetElement(record, 'value', recordData.value);
    oSetElement(record, 'taskIds', recordData.taskIds);
    oSetElement(record, 'enablePreCondition', recordData.enablePreCondition);
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

  oSetElement(flowProperty, 'printTemplate', $('#printTemplate').val());
  oSetElement(flowProperty, 'printTemplateId', $('#printTemplateId').val());
  oSetElement(flowProperty, 'printTemplateUuid', $('#printTemplateUuid').val());
  oSetElement(flowProperty, 'customJsModule', $('#customJsModule').val());
  oSetElement(flowProperty, 'listener', $('#listener').val());
  oSetElement(flowProperty, 'listenerName', $('#listenerName').val());
  oSetElement(flowProperty, 'globalTaskListener', $('#globalTaskListener').val());
  oSetElement(flowProperty, 'globalTaskListenerName', $('#globalTaskListenerName').val());

  var eventScripts = flowProperty.selectSingleNode('eventScripts');
  if (eventScripts) {
    flowProperty.removeChild(eventScripts);
  }
  eventScripts = oSetElement(flowProperty, 'eventScripts');
  $('#eventScripts > li').each(function () {
    var $this = $(this);
    var scriptData = $this.data('script');
    var eventScript = oAddElement(eventScripts, 'eventScript', scriptData.text);
    eventScript.attr('pointcut', scriptData.pointcut);
    eventScript.attr('type', scriptData.type);
    eventScript.attr('contentType', scriptData.contentType);
  });
  oSetElement(flowProperty, 'indexType', $('[name="indexType"]:checked').val());
  oSetElement(flowProperty, 'indexTitleExps', $('#indexTitleExps').text());
  oSetElement(flowProperty, 'indexContentExps', $('#indexContentExps').text());
}
