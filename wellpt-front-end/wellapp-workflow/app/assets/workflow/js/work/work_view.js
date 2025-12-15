var WorkFlow = WorkFlow || {};
$(function () {
  // 点击
  var btn_click = 'B004000';
  // 保存
  var btn_save = 'B004001';
  // 动态表单提交
  // var btn_submit_form = "btn_dyform";
  // 提交
  var btn_submit = 'B004002';
  // 退回
  var btn_rollback = 'B004003';
  // 直接退回
  var btn_direct_rollback = 'B004004';
  // 撤回
  var btn_cancel = 'B004005';
  // 转办
  var btn_transfer = 'B004006';
  // 会签
  var btn_counterSign = 'B004007';
  // 关注
  var btn_attention = 'B004008';
  // 套打
  var btn_print = 'B004009';
  // 抄送
  var btn_copyTo = 'B004010';
  // 签署意见
  var btn_sign_opinion = 'B004011';
  // 取消关注
  var btn_unfollow = 'B004012';
  // 办理过程
  var btn_view_process = 'B004013';
  // 催办
  var btn_remind = 'B004014';
  // 移交
  var btn_hand_over = 'B004015';
  // 跳转
  var btn_do_goto = 'B004016';
  // 办理意见
  var btn_view_opinion = '';
  // 挂起
  var btn_suspend = 'B004017';
  // 恢复
  var btn_resume = 'B004018';
  // 退件
  var btn_reject = 'B004019';
  // 删除
  var btn_delete = 'B004023';
  // 可编辑文档
  var btn_editable = 'B004025';
  // 必须签署意见
  var btn_required_sign_opinion = 'B004026';
  // 转办必填意见
  var btn_required_transfer_opinion = 'B004029';
  // 会签必填意见
  var btn_required_counterSign_opinion = 'B004030';
  // 退回必填意见
  var btn_required_rollback_opinion = 'B004031';
  // 特送个人必填意见
  var btn_required_hand_over_opinion = 'B004032';
  // 特送环节必填意见
  var btn_required_do_goto_opinion = 'B004033';
  // 签署意见模式
  var signOpinionModel = $('#wf_signOpinionModel').val();
  var wfSignOpinionModel = $('#ep_wf_sign_opinion_model').val();
  if (StringUtils.isNotBlank(wfSignOpinionModel)) {
    signOpinionModel = wfSignOpinionModel;
  }
  if (StringUtils.isBlank(signOpinionModel) || !(signOpinionModel === '1' || signOpinionModel === '2')) {
    signOpinionModel = '1';
  }

  // 流程错误代码
  var WorkFlowErrorCode = {
    WorkFlowException: 'WorkFlowException', // 工作流异常
    TaskNotAssignedUser: 'TaskNotAssignedUser', // 任务没有指定参与者
    TaskNotAssignedCopyUser: 'TaskNotAssignedCopyUser', // 任务没有指定抄送人
    TaskNotAssignedMonitor: 'TaskNotAssignedMonitor', // 任务没有指定督办人
    ChooseSpecificUser: 'ChooseSpecificUser', // 选择具体办理人
    OnlyChooseOneUser: 'OnlyChooseOneUser', // 只能选择一个办理人
    JudgmentBranchFlowNotFound: 'JudgmentBranchFlowNotFound', // 无法找到可用的判断分支流向
    MultiJudgmentBranch: 'MultiJudgmentBranch', // 找到多个判断分支流向
    SubFlowNotFound: 'SubFlowNotFound', // 没有指定子流程
    SubFlowMerge: 'SubFlowMerge', // 子流程合并等待
    IdentityNotFlowPermission: 'IdentityNotFlowPermission', // 用户没有权限访问流程
    RollbackTaskNotFound: 'RollbackTaskNotFound', // 找不到退回操作的退回环节异常类
    SaveData: 'SaveData', // 表单数据保存失败
    RequiredFieldIsBlank: 'RequiredFieldIsBlank' // 必填域为空
  };
  var bean = {
    taskInstUuid: null,
    flowInstUuid: null,
    flowDefUuid: null,
    flowDefId: null,
    title: null,
    name: null,
    suspensionState: null,
    serialNoDefId: null,
    userId: null,
    userName: null,
    formUuid: null,
    dataUuid: null,
    buttons: [],
    taskUsers: {},
    taskCopyUsers: {},
    taskTransferUsers: {},
    taskMonitors: {},
    createTime: null,
    fromTaskId: '<StartFlow>',
    toTaskId: null,
    toSubFlowId: null,
    waitForMerge: {},
    customDynamicButton: null,
    rollbackToTaskId: null,
    printTemplateId: null,
    opinionValue: '',
    opinionLabel: '',
    opinionText: '',
    records: []
  };

  // 判断字符串不为undefined、null、空串、空格串
  var isNotBlank = StringUtils.isNotBlank;
  // 判断字符串为undefined、null、空串、空格串
  var isBlank = StringUtils.isBlank;

  var dytableSelector = '#dyform';

  // 操作服务
  var saveService = 'workService.save';
  var submitService = 'workService.submit';
  // var rollbackService = "workService.rollback";
  var rollbackWithWorkDataService = 'workService.rollbackWithWorkData';
  // var directRollbackService = "workService.directRollback";
  var cancelService = 'workService.cancel';
  // var transferService = "workService.transfer";
  var transferWithWorkDataService = 'workService.transferWithWorkData';
  // var counterSignService = "workService.counterSign";
  var counterSignWithWorkDataService = 'workService.counterSignWithWorkData';
  var attentionService = 'workService.attention';
  var printUrl = '/workflow/work/print';
  var copyToService = 'workService.copyTo';
  var viewOpinionService = 'workService.getOpinions';
  var viewProcessService = 'workService.getProcess';
  var remindService = 'workService.remind';
  var unfollowdService = 'workService.unfollow';
  var handOverService = 'workService.handOver';
  var getGotoTaskService = 'workService.getGotoTasks';
  var gotoTaskService = 'workService.gotoTask';
  var suspendService = 'workService.suspend';
  var resumeService = 'workService.resume';
  var deleteService = 'workService.delete';
  var timelineService = 'workService.getTimeline';

  bean.flowDefUuid = $('#wf_flowDefUuid').val();
  bean.flowDefId = $('#wf_flowDefId').val();
  bean.flowInstUuid = $('#wf_flowInstUuid').val();
  bean.taskInstUuid = $('#wf_taskInstUuid').val();
  bean.taskId = $('#wf_taskId').val();
  bean.taskName = $('#wf_taskName').val();
  bean.taskIdentityUuid = $('#wf_taskIdentityUuid').val();
  bean.formUuid = $('#wf_formUuid').val();
  bean.dataUuid = $('#wf_dataUuid').val();
  bean.title = $('#wf_title').val();
  bean.aclRole = $('#wf_aclRole').val();
  bean.serialNoDefId = $('#wf_serialNoDefId').val();
  bean.suspensionState = $('#wf_suspensionState').val();
  bean.isFirstTaskNode = $('#wf_isFirstTaskNode').val();

  // 保留自定义运行时参数
  bean.extraParams = {};
  var $eps = $('input[name^=custom_rt_]', '#wf_form');
  $eps.each(function () {
    bean.extraParams[$(this).attr('name')] = $(this).val();
  });

  // 是否可编辑动态表单，如果可编辑按流程设置处理，不可编辑设置有只读
  var editDyform = $(":button[name='" + btn_editable + "']", '.wf_operate').length > 0;
  // 是否显示签署意见框
  var showSignOpinion = $(":button[name='" + btn_sign_opinion + "']", '.wf_operate').length > 0;
  // 是否需要签署意见
  var requiredSignOpinion = $(":button[name='" + btn_required_sign_opinion + "']", '.wf_operate').length > 0;
  // 是否转办必填意见
  var requiredTransferOpinion = $(":button[name='" + btn_required_transfer_opinion + "']", '.wf_operate').length > 0;
  // 是否会签必填意见
  var requiredCounterSignOpinion = $(":button[name='" + btn_required_counterSign_opinion + "']", '.wf_operate').length > 0;
  // 是否退回必填意见
  var requiredRollbackOpinion = $(":button[name='" + btn_required_rollback_opinion + "']", '.wf_operate').length > 0;
  // 是否特送个人必填意见
  var requiredHandOverOpinion = $(":button[name='" + btn_required_hand_over_opinion + "']", '.wf_operate').length > 0;
  // 是否特送环节必填意见
  var requiredDoGotoOpinion = $(":button[name='" + btn_required_do_goto_opinion + "']", '.wf_operate').length > 0;

  // 如果流程ID或流程定义UUID都为空，则提示错误并返回
  if (isBlank(bean.flowDefId) && isBlank(bean.flowDefUuid)) {
    oAlert('流程定义加载出错，没有指定流程或流程不存在!');
    return;
  }

  // JQuery UI按钮
  // $("input[type=submit], a, button", $("#toolbar")).button();
  // 绑定动态表单提交按钮
  $('.wf_operate').append("<button id='btn_dyform' name='btn_dyform' style='display: none'>保存</button>");

  var workProcess = null;
  var workDataDisplayAsLabel = false;
  var subDataParams = [];
  var $eps = $('input[name^=ep_]', '#wf_form');
  $eps.each(function () {
    subDataParams.push({
      id: $(this).val()
    });
  });

  $('.form_title>.form_close').click(function () {
    window.close();
  });

  // 初使化
  JDS.call({
    service: 'workService.getWorkData',
    data: [bean],
    success: function (result) {
      var isFirst = isBlank(bean.flowInstUuid);
      var workData = result.data;

      onWorkDataLoaded(workData);

      workProcess = workData.workProcess;
      var dyFormData = workData.dyFormData;
      // 如果是开始环节可编辑，若设置可编辑，则可编辑，否则不可编辑
      if (isBlank(bean.taskInstUuid) || bean.isFirstTaskNode === 'true') {
      } else if (bean.aclRole != 'TODO') {
        // $(dytableSelector).dyform("showAsLabel");
        workDataDisplayAsLabel = true;
      } else if (!editDyform) {
        // $(dytableSelector).dyform("showAsLabel");
        workDataDisplayAsLabel = true;
      }
      try {
        $(dytableSelector).dyform({
          formData: dyFormData,
          displayAsLabel: workDataDisplayAsLabel,
          optional: {
            isFirst: isFirst
          },
          success: function () {
            onDyformOpen(workData.developJson);
          },
          error: function () {
            oAlert('表单解析失败!');
          }
        });
      } catch (e) {
        oAlert2('表单解析失败： ' + e);
        $(':button', '.wf_operate').each(function () {
          $(this).hide();
        });
        throw e;
      }

      // 是否查看办理过程
      var requiredViewProcess = $(":button[name='" + btn_view_process + "']", '.wf_operate').length > 0;
      if (!(bean.aclRole == 'TODO')) {
        requiredViewProcess = true;
      } else {
        requiredViewProcess = isNotBlank(bean.taskInstUuid);
      }
      // 办理过程
      var process = result.data.workProcess;
      result.data.workProcess = null;
      // 1、流程新建时
      if (
        (process && !process['previous'] && process['current'] && process['next']) ||
        (process && process['previous'] && process['current'] && !process['next'])
      ) {
        var proce1 = null;
        var proce2 = null;
        if (process && process['current'] && process['next']) {
          proce1 = process['current'];
          proce2 = process['next'];
        } else {
          proce1 = process['previous'];
          proce2 = process['current'];
        }
        $('#process .proce1').hide();
        $('#process').css('min-width', '700px');
        $('#process .proce2').addClass('proce2_start');
        setProcessInfo($('#pre_task_name'), '', $('#pre_task_assignee'), '');
        setProcessInfo($('#cur_task_name'), proce1.taskName, $('#cur_task_assignee'), proce1.assignee);
        setProcessInfo($('#next_task_name'), proce2.taskName, $('#next_task_assignee'), proce2.assignee);

        $('#process').show();
      } else if (process && process['previous'] && process['current'] && process['next']) {
        // 2、流程办理中
        setProcessInfo($('#pre_task_name'), process['previous'].taskName, $('#pre_task_assignee'), process['previous'].assignee);
        setProcessInfo($('#cur_task_name'), process['current'].taskName, $('#cur_task_assignee'), process['current'].assignee);
        setProcessInfo($('#next_task_name'), process['next'].taskName, $('#next_task_assignee'), process['next'].assignee);

        $('#process').show();
      } else if (process && !process['previous'] && process['current'] && !process['next']) {
        // 3、流程已结束
        var proce1 = process['current'];
        $('#process .proce1').hide();
        $('#process').css('min-width', '700px');
        $('#process .proce2').addClass('proce2_start');
        setProcessInfo($('#pre_task_name'), '', $('#pre_task_assignee'), '');
        setProcessInfo($('#cur_task_name'), proce1.taskName, $('#cur_task_assignee'), proce1.assignee + '(已办结)');
        $('#process .proce3').hide();
        setProcessInfo($('#next_task_name'), '', $('#next_task_assignee'), '');

        $('#process').show();
      }

      // 如果不需要查看办理过程，则显示查阅流程图
      if (!requiredViewProcess) {
        $('#process .view_process').unbind('click');
        $('#process .view_process a').html('查阅流程图');
        var url = ctx + '/workflow/show?open&id=' + bean.flowDefUuid;
        $('#process .view_process').click(function () {
          window.open(url);
        });
      }

      // 时间轴事件绑定
      $('#process .timeline').click(function () {
        showTimeline();
        // open(ctx +
        // "/workflow/work/view/timeline?taskInstUuid=" +
        // bean.taskInstUuid);
      });

      // 如果流程实例UUID不为空，显示共享数据
      // 办理过程
      var shareData = result.data.shareData;
      result.data.shareData = null;
      showShareFlow(shareData);

      // 显示签署意见
      if (showSignOpinion == true) {
        showToSignOpinion();
      } else if (requiredSignOpinion == true) {
        showToSignOpinion();
      }
    }
  });

  function setProcessInfo(nameEle, name, assigneeEle, assignee) {
    nameEle.html(name);

    if (assignee.length > 20) {
      assigneeEle.html(assignee.substring(0, 20) + '...');
    } else {
      assigneeEle.html(assignee);
    }
    assigneeEle.attr('title', assignee);
  }

  // 工作数据加载后处理
  function onWorkDataLoaded(workData) {
    bean.records = workData.records;
    bean.taskStartTime = workData.taskStartTime;
    bean.todoType = workData.todoType;
    bean.taskIdentityUuid = workData.taskIdentityUuid;

    setReservedFields(workData);
  }

  function setReservedFields(workData) {
    bean['reservedText1'] = workData['reservedText1'];
    bean['reservedText2'] = workData['reservedText2'];
    bean['reservedText3'] = workData['reservedText3'];
    bean['reservedText4'] = workData['reservedText4'];
    bean['reservedText5'] = workData['reservedText5'];
    bean['reservedText6'] = workData['reservedText6'];
    bean['reservedText7'] = workData['reservedText7'];
    bean['reservedText8'] = workData['reservedText8'];
    bean['reservedText9'] = workData['reservedText9'];
    bean['reservedText10'] = workData['reservedText10'];
    bean['reservedText11'] = workData['reservedText11'];
    bean['reservedText12'] = workData['reservedText12'];
    bean['reservedNumber1'] = workData['reservedNumber1'];
    bean['reservedNumber2'] = workData['reservedNumber2'];
    bean['reservedNumber3'] = workData['reservedNumber3'];
    bean['reservedDate1'] = workData['reservedDate1'];
    bean['reservedDate2'] = workData['reservedDate2'];
  }

  // 动态表单初始化后回调处理
  function onDyformOpen(developJson) {
    // 调整自适应表单宽度
    adjustWidthToForm();

    var loadJs = function (jsUrl) {
      if (isBlank(jsUrl)) {
        return;
      }
      var jsUrls = jsUrl.split(';');
      if (jsUrls != null && jsUrls.length != 0) {
        for (var i = 0; i < jsUrls.length; i++) {
          if (isNotBlank(jsUrls[i])) {
            $.ajax({
              url: ctx + jsUrls[i],
              dataType: 'script',
              async: false
            });
          }
        }
      }
    };
    // 加载全局的JS
    var globalScriptUrl = $('#wf_custom_global_script_url').val();
    loadJs(globalScriptUrl);

    // 加载二次开发JS
    if (isNotBlank(developJson)) {
      var devJson = JSON.parse(developJson);
      if (devJson != null && isNotBlank(devJson['customJsUrl'])) {
        loadJs(devJson['customJsUrl']);
      }
    }

    // 加载执行运行时扩展js
    var epScriptUrl = $('#custom_rt_script_url').val();
    loadJs(epScriptUrl);

    // 加载自定义扩展JS
    var scriptUrl = $('#custom_script_url').val();
    loadJs(scriptUrl);

    // 输入的办理意见
    var opinionName = $('#ep_wf_opinion_name').val();
    var opinionValue = $('#ep_wf_opinion_value').val();
    var opinionText = $('#ep_wf_opinion_text').val();
    var wf_opinion_text = $('#wf_opinionText').val();
    if (isNotBlank(opinionName) && isNotBlank(opinionValue) && isNotBlank(opinionText)) {
      var data = {
        label: opinionName,
        value: opinionValue,
        text: opinionText
      };
      signOpinion(data);
    } else if (isNotBlank(wf_opinion_text)) {
      var wf_opinion_name = $('#wf_opinionLabel').val();
      var wf_opinion_value = $('#wf_opinionValue').val();
      var data = {
        label: wf_opinion_name,
        value: wf_opinion_value,
        text: wf_opinion_text
      };
      signOpinion(data);
    } else {
      // 本地存储还原bean.opinionText
      restoreOpinionText(bean);
      if (isNotBlank(bean.opinionText)) {
        var wf_opinion_name = $('#wf_opinionLabel').val();
        var wf_opinion_value = $('#wf_opinionValue').val();
        var data = {
          label: wf_opinion_name,
          value: wf_opinion_value,
          text: bean.opinionText
        };
        signOpinion(data);
      }
    }

    // 输入的动态表单值
    var $dyfields = $('input[name^=ep_dyfs_]');
    $dyfields.each(function () {
      var pName = $(this).attr('name');
      var fieldName = pName.substring(8);
      var fieldVal = $(this).val();
      $(dytableSelector).dyform('setFieldValue', fieldName, fieldVal);
    });

    // 是否自动提交
    setTimeout(function () {
      var autoSubmit = $('input[name=auto_submit]', '#wf_form').val();
      if (isNotBlank(autoSubmit) && 'true' === autoSubmit) {
        $("button[name='" + btn_submit + "']", '.wf_operate').trigger('click');
      }
    }, 500);
  }
  $(window).bind('resize', function (e) {
    // 调整自适应表单宽度
    adjustWidthToForm();
  });
  // 调整自适应表单宽度
  function adjustWidthToForm() {
    var div_body_width = $(window).width() * 0.95;
    $('.form_header').css('width', div_body_width - 5);
    $('.div_body').css('width', div_body_width);

    $('.form_content').css('width', div_body_width - 44);
    $('#process').css('width', div_body_width - 45);

    // 调整子过程办理过程宽度
    $('.share_flow_content').css('width', div_body_width - 45);

    // 显示签署意见
    if (showSignOpinion == true) {
      showToSignOpinion();
    } else if (requiredSignOpinion == true) {
      showToSignOpinion();
    }
  }

  // 显示共享数据
  function showShareFlow(shareData) {
    $('#share_flow_body').html('');
    $(shareData).each(function () {
      $('#share_flow').show();

      var rowData =
        '<tr>' +
        '<td>' +
        this.title +
        '</td>' +
        '<td>' +
        this.todoUser +
        '</td>' +
        '<td>' +
        this.opinion +
        '</td>' +
        '<td>' +
        this.currentTask +
        '</td>' +
        '<td>' +
        this.currentUser +
        '</td>' +
        '</tr>';
      $('#share_flow_body').append(rowData);
    });
  }

  /** ****************************** 按钮点击扩展开始 ****************************** */
  // 点击
  $(":button[name='" + btn_click + "']", '.wf_operate').each(function () {
    $(this).click($.proxy(onClick, this));
  });
  // 点击事件处理
  function onClick() {
    if (typeof WorkFlow.onClick == 'function') {
      WorkFlow.onClick.call(this, bean);
    }
  }
  /** ****************************** 按钮点击扩展结束 ****************************** */

  /** ********************************* 保存开始 ********************************* */
  // 保存
  $(":button[name='" + btn_save + "']", '.wf_operate').each(function () {
    $(this).click($.proxy(onSave, this, true));
    $(this).show();
  });
  // 保存事件处理
  function onSave(async, callback) {
    // 操作动作及类型
    bean.action = isBlank($(this).text()) ? '保存' : $(this).text();
    bean.actionType = 'Save';
    var $btn = $(this);
    // 保存前回调处理
    if (typeof WorkFlow.beforeSave == 'function') {
      var bsRetVal = WorkFlow.beforeSave.call(this, bean);
      if (bsRetVal == false) {
        return;
      }
    }
    // 获取表单数据
    bean.dyFormData = getDyformData();
    JDS.call({
      service: saveService,
      data: [bean],
      async: async,
      success: function (result) {
        var data = result.data;

        $('input[name=flowInstUuid]', $('#print_form')).val(data['flowInstUuid']);
        bean.flowInstUuid = data['flowInstUuid'];

        // 本地存储保存bean.opinionText
        storeOpinionText(bean);

        // 局部回调callback
        if (typeof callback == 'function') {
          callback.call(this, data);
          // 全局回调，WorkFlow.afterSuccessSave
          if (WorkFlow.afterSuccessSave) {
            WorkFlow.afterSuccessSave.call(this, bean);
          }
        } else {
          // 全局回调，WorkFlow.afterSuccessSave
          if (WorkFlow.afterSuccessSave) {
            WorkFlow.afterSuccessSave.call(this, bean);
          } else {
            oAlert('保存成功！', function () {
              // 保存成功，刷新当前页面
              reload();
            });
          }
        }
      },
      error: function (jqXHR) {
        // 处理流程操作返回 的错误信息
        handlerError(jqXHR, $btn);
      }
    });
  }

  function reload(urlSuffix) {
    var taskInstUuid = bean.taskInstUuid;
    var flowInstUuid = bean.flowInstUuid;
    if (isNotBlank(taskInstUuid)) {
      window.location.reload();
    } else if (isNotBlank(flowInstUuid)) {
      var suffix = '';
      if (isNotBlank(urlSuffix)) {
        suffix = urlSuffix;
      }
      window.location = ctx + '/workflow/work/view/draft?flowInstUuid=' + flowInstUuid + suffix;
    } else {
      window.location.reload();
    }
  }
  // 刷新当前页面
  WorkFlow.reload = reload;
  /** ********************************* 保存结束 ********************************* */

  /** ********************************* 提交开始 ********************************* */
  // 提交
  $(":button[name='" + btn_submit + "']", '.wf_operate').each(function () {
    $(this).click($.proxy(onSubmit, this));
    $(this).show();
  });
  // 提交事件处理
  function onSubmit(event) {
    // 设置提交按钮ID
    var btnId = $(this).attr('id');
    bean.submitButtonId = btnId;

    // 操作动作及类型
    bean.action = isBlank($(this).text()) ? '提交' : $(this).text();
    bean.actionType = 'Submit';

    // 提交前回调处理
    if (WorkFlow.beforeSubmit) {
      if ($(dytableSelector).dyform('validateForm') === true) {
        var bsRetVal = WorkFlow.beforeSubmit.call(this, bean);
        if (bsRetVal == false) {
          return;
        }
      } else {
        return;
      }
    }
    // 确保提交时有签署意见
    if (isRequiredOpinionPosition()) {
    } else if (isRequiredSignOpinion()) {
      openToSignOpinion(true, btnId);
    } else {
      // 处理自定义动态按钮
      if (btnId != btn_submit) {
        var customDynamicButton = {};
        customDynamicButton.id = btnId;
        customDynamicButton.code = $(this).attr('name');
        customDynamicButton.taskId = $(this).attr('taskId');
        // 去掉按钮提交主送对象字符串的字符'['、']'
        var customUserIds = $(this).attr('userIds');
        var customCopyUserIds = $(this).attr('copyUserIds');
        if (customUserIds.indexOf('[') == 0 && customUserIds.lastIndexOf(']') == customUserIds.length - 1) {
          var users = customUserIds.substring(1, customUserIds.length - 1);
          customDynamicButton.users = users.split(',');
        }
        // 去掉按钮提交抄送对象字符串的字符'['、']'
        if (customCopyUserIds.indexOf('[') == 0 && customCopyUserIds.lastIndexOf(']') == customCopyUserIds.length - 1) {
          var copyUsers = customCopyUserIds.substring(1, customCopyUserIds.length - 1);
          customDynamicButton.copyUsers = copyUsers.split(',');
        }
        bean.customDynamicButton = customDynamicButton;
      } else {
        bean.customDynamicButton = null;
      }

      // 提交动态表单
      // $("#" + btn_submit_form).trigger('click');
      try {
        // 会签待办提交不进行表单验证
        if (bean.todoType === 2 || $(dytableSelector).dyform('validateForm') === true) {
          submitForm(event, $(this));
        }
      } catch (e) {
        oAlert2('表单数据验证出错' + e + '，无法提交数据！');
        throw e;
      }
    }
  }
  // 判断是否需要选择意见立场
  function isRequiredOpinionPosition() {
    if (signOpinionModel === '1' && isNotBlank(bean.taskInstUuid) && bean.aclRole === 'TODO') {
      if ($('#mini_wf_opinion').workflowMiniOpinion('isRequiredOpinionPosition') === true) {
        oAlert2('请先选择意见立场!');
        return true;
      }
    }
    return false;
  }
  // 判断是否需要签署意见
  function isRequiredSignOpinion() {
    if (isNotBlank(bean.taskInstUuid) && !isNotBlank(bean.opinionText) && requiredSignOpinion == true) {
      return true;
    }
    return false;
  }
  // 判断是否转办必填意见
  function isRequiredTransferOpinion() {
    if (isNotBlank(bean.taskInstUuid) && !isNotBlank(bean.opinionText) && requiredTransferOpinion == true) {
      return true;
    }
    return false;
  }
  // 判断是否会签必填意见
  function isRequiredCounterSignOpinion() {
    if (isNotBlank(bean.taskInstUuid) && !isNotBlank(bean.opinionText) && requiredCounterSignOpinion == true) {
      return true;
    }
    return false;
  }
  // 判断是否退回必填意见
  function isRequiredRollbackOpinion() {
    if (isNotBlank(bean.taskInstUuid) && !isNotBlank(bean.opinionText) && requiredRollbackOpinion == true) {
      return true;
    }
    return false;
  }
  // 判断是否特送个人必填意见
  function isRequiredHandOverOpinion() {
    if (isNotBlank(bean.taskInstUuid) && !isNotBlank(bean.opinionText) && requiredHandOverOpinion == true) {
      return true;
    }
    return false;
  }
  // 判断是否特送环节必填意见
  function isRequiredDoGotoOpinion() {
    if (isNotBlank(bean.taskInstUuid) && !isNotBlank(bean.opinionText) && requiredDoGotoOpinion == true) {
      return true;
    }
    return false;
  }
  // 获取表单数据
  function getDyformData() {
    var dyFormData = null;
    try {
      dyFormData = $(dytableSelector).dyform('collectFormData');
    } catch (e) {
      oAlert2('表单数据收集失败[ + ' + e + ']，无法提交数据！');
      throw e;
    }
    return dyFormData;
  }

  // 提交动态表单操作
  function submitForm(event, $btn) {
    // 获取表单数据
    bean.dyFormData = getDyformData();
    JDS.call({
      service: submitService,
      data: [bean],
      success: function (result) {
        if (result.data) {
          var data = result.data;
          // 与前环节相同自动提交
          if (isNotBlank(data.sameUserSubmitTaskInstUuid) && isNotBlank(data.sameUserSubmitTaskOperationUuid)) {
            window.location =
              ctx +
              '/workflow/work/view/todo?taskInstUuid=' +
              data.sameUserSubmitTaskInstUuid +
              '&flowInstUuid=' +
              data.flowInstUuid +
              '&auto_submit=true&sameUserSubmitTaskOperationUuid=' +
              data.sameUserSubmitTaskOperationUuid +
              '&ep_wf_sign_opinion_model=' +
              signOpinionModel;
            return;
          }
        }

        var retVal = false;
        if (WorkFlow.afterSuccessSubmit) {
          retVal = WorkFlow.afterSuccessSubmit.call(this, bean, result);
        }
        if (retVal == false) {
          requiredSignOpinion = true;
          oAlert('提交成功!', function () {
            handlerSuccess(result);
          });
        }
      },
      error: function (jqXHR) {
        // 处理流程操作返回 的错误信息
        handlerError(jqXHR, $btn);
      }
    });
    event.stopPropagation();
  }
  WorkFlow.submit = function (btnId) {
    $('#' + btnId).trigger('click');
  };
  /** ********************************* 提交结束 ********************************* */

  /** ******************************** 回调处理开始 ******************************** */
  // 处理流程操作成功信息
  function handlerSuccess(result) {
    try {
      // 本地存储删除bean.opinionText
      removeOpinionText(bean);

      // 操作成功，关闭相关页面
      var taskInstUuid = bean.taskInstUuid; // 从待办工作中打开
      var flowInstUuid = bean.flowInstUuid; // 从草搞箱打开
      var flowDefUuid = bean.flowDefUuid; // 从新建工作打开
      if (isNotBlank(taskInstUuid)) {
        TabUtils.reloadAndRemoveTab('work_todo', taskInstUuid);
      } else if (flowInstUuid != null && flowInstUuid != '') {
        TabUtils.reloadAndRemoveTab('work_draft', flowInstUuid);
      } else if (flowDefUuid != null && flowDefUuid != '') {
        TabUtils.removeTab(bean.flowDefUuid, bean.flowDefUuid);
      }
    } catch (e) {
      if (isBlank(ctx)) {
        window.location.href = '/';
      } else {
        window.location.href = ctx;
      }
    }
  }
  // 处理流程操作返回 的错误信息
  function handlerError(jqXHR, $btn) {
    // 本地存储删除bean.opinionText
    removeOpinionText(bean);

    var msg = JSON.parse(jqXHR.responseText);
    if (WorkFlowErrorCode.WorkFlowException === msg.errorCode) {
      WorkFlowException(msg.data);
    } else if (WorkFlowErrorCode.TaskNotAssignedUser === msg.errorCode) {
      TaskNotAssignedUser(msg.data, $btn);
    } else if (WorkFlowErrorCode.TaskNotAssignedCopyUser === msg.errorCode) {
      TaskNotAssignedCopyUser(msg.data, $btn);
    } else if (WorkFlowErrorCode.TaskNotAssignedMonitor === msg.errorCode) {
      TaskNotAssignedMonitor(msg.data, $btn);
    } else if (WorkFlowErrorCode.ChooseSpecificUser === msg.errorCode) {
      if (msg.data.unitUser) {
        ChooseUnitUser(msg.data, $btn);
      } else {
        ChooseSpecificUser(msg.data, $btn);
      }
    } else if (WorkFlowErrorCode.OnlyChooseOneUser === msg.errorCode) {
      if (msg.data.unitUser) {
        ChooseUnitUser(msg.data, $btn);
      } else {
        OnlyChooseOneUser(msg.data, $btn);
      }
    } else if (WorkFlowErrorCode.JudgmentBranchFlowNotFound === msg.errorCode) {
      JudgmentBranchFlowNotFound(msg.data, $btn);
    } else if (WorkFlowErrorCode.MultiJudgmentBranch === msg.errorCode) {
      MultiJudgmentBranch(msg.data, $btn);
    } else if (WorkFlowErrorCode.SubFlowNotFound === msg.errorCode) {
      SubFlowNotFound(msg.data, $btn);
    } else if (WorkFlowErrorCode.SubFlowMerge === msg.errorCode) {
      SubFlowMerge(msg.data, $btn);
    } else if (WorkFlowErrorCode.IdentityNotFlowPermission === msg.errorCode) {
      IdentityNotFlowPermission(msg.data);
    } else if (WorkFlowErrorCode.RollbackTaskNotFound === msg.errorCode) {
      RollbackTaskNotFound(msg.data, $btn);
    } else if (WorkFlowErrorCode.SaveData === msg.errorCode) {
      /*
       * var msg = "<div><a id='wf_save_data_error_msg' title='" +
       * msg.msg + "' style='color: #000;text-decoration: none;'>表单数据保存失败！</a></div>";
       * oAlert2(msg); $("#wf_save_data_error_msg").mouseover(function() {
       * alert($(this).attr("title")); });
       */
      $(dytableSelector).dyform('handleException', msg.data);
    } else if (WorkFlowErrorCode.RequiredFieldIsBlank === msg.errorCode) {
      if (msg.data) {
        $.each(msg.data, function () {
          $(dytableSelector).dyform('setFieldEditableByFieldName', this.fieldName, this.dataUuid);
          $(dytableSelector).dyform('setFieldRequiredByFormUuid', this.fieldName, true, this.formUuid);
        });
        $(dytableSelector).dyform('validateForm');
      }
    } else {
      oAlert2('工作流无法处理的未知异常：' + jqXHR.responseText);
      // 出现未知异常，可能是数据选错，所以清除数据重头开始选择 add by xieming 2015-04-20
      clearBeanData();
    }
  }
  // add by xieming 2015-04-20
  function clearBeanData() {
    bean.toTaskId = '';
    bean.taskCopyUsers = {};
    bean.taskTransferUsers = {};
    bean.taskMonitors = {};
    bean.taskUsers = {};
    bean.toSubFlowId = '';
    bean.rollbackToTaskId = '';
    bean.rollbackToTaskInstUuid = '';
    bean.waitForMerge = {};
    bean.gotoTaskId = '';
  }
  // 1、工作流异常
  function WorkFlowException(eData) {
    if (eData.hasOwnProperty('autoClose')) {
      if (eData['autoClose'] == true) {
        oAlert(eData['msg']);
      } else {
        oAlert2(eData['msg']);
      }
    } else {
      oAlert(eData);
    }
  }

  // 2、任务没有指定参与者，弹出人员选择框选择参与人(人员、部门及群组)
  function TaskNotAssignedUser(eData, $btn) {
    var title = '';
    if (isNotBlank(eData.title)) {
      title = eData.title;
      title = title.indexOf('(') > -1 ? title.substring(1, title.length - 1) : title;
    }
    var isCancel = true;
    $.unit.open({
      title: "选择承办人<span class='taskFontWeight'>【" + title + '】</span>',
      close: function () {
        if (isCancel) {
          clearBeanData();
        }
      },
      afterSelect: function (laReturn) {
        isCancel = false;
        var taskUsers = {};
        var taskId = eData.taskId;
        if (isNotBlank(laReturn.id)) {
          // 在原来的环节办理人上增加环节办理人
          taskUsers = bean.taskUsers;
          var userIds = laReturn.id.split(';');
          taskUsers[taskId] = userIds;
        } else {
          taskUsers[taskId] = null;
          bean.taskUsers = taskUsers;
        }
        // 重新触发提交事件
        if (isNotBlank(eData.submitButtonId)) {
          $('#' + eData.submitButtonId).trigger('click');
        } else if ($btn) {
          $btn.trigger('click');
        } else {
          // $("#" + btn_submit).trigger('click');
          oAlert('请重新提交!');
        }
      }
    });
  }

  // 3、任务没有指定抄送人，弹出人员选择框选择抄送人(人员、部门及群组)
  function TaskNotAssignedCopyUser(eData, $btn) {
    var title = '';
    if (isNotBlank(eData.title)) {
      title = eData.title;
      title = title.indexOf('(') > -1 ? title.substring(1, title.length - 1) : title;
    }
    var isCancel = true;
    $.unit.open({
      title: "选择抄送人<span class='taskFontWeight'>【" + title + '】</span>',
      close: function () {
        if (isCancel) {
          clearBeanData();
        }
      },
      afterSelect: function (laReturn) {
        isCancel = false;
        var taskCopyUsers = bean.taskCopyUsers;
        var taskId = eData.taskId;
        if (isNotBlank(laReturn.id)) {
          var userIds = laReturn.id.split(';');
          taskCopyUsers[taskId] = userIds;
          bean.taskCopyUsers = taskCopyUsers;
        } else {
          taskCopyUsers[taskId] = null;
          bean.taskCopyUsers = taskCopyUsers;
        }
        // modify by xieming 2015-04-21 begin 添加$btn
        // 重新触发提交事件
        if (isNotBlank(eData.submitButtonId)) {
          $('#' + eData.submitButtonId).trigger('click');
        } else if ($btn) {
          $btn.trigger('click');
        } else {
          // $("#" + btn_submit).trigger('click');
          oAlert('请重新提交!');
        }
        // modify by xieming 2015-04-21 end
      }
    });
  }
  // 4、任务没有指定督办人，弹出人员选择框选择督办人(人员和部门及群组)
  function TaskNotAssignedMonitor(eData, $btn) {
    var title = '';
    if (isNotBlank(eData.title)) {
      title = eData.title;
      title = title.indexOf('(') > -1 ? title.substring(1, title.length - 1) : title;
    }
    var isCancel = true;
    $.unit.open({
      title: "选择督办人<span class='taskFontWeight'>【" + title + '】</span>',
      close: function () {
        if (isCancel) {
          clearBeanData();
        }
      },
      afterSelect: function (laReturn) {
        isCancel = false;
        var taskMonitors = bean.taskMonitors;
        var taskId = eData.taskId;
        if (isNotBlank(laReturn.id)) {
          var userIds = laReturn.id.split(';');
          taskMonitors[taskId] = userIds;
          bean.taskMonitors = taskMonitors;
        } else {
          taskMonitors[taskId] = null;
          bean.taskMonitors = taskMonitors;
        }
        // 重新触发提交事件
        if (isNotBlank(eData.submitButtonId)) {
          $('#' + eData.submitButtonId).trigger('click');
        } else if ($btn) {
          $btn.trigger('click');
        } else {
          // $("#" + btn_submit).trigger('click');
          oAlert('请重新提交!');
        }
      }
    });
  }
  // 选择组织选择框人员
  function ChooseUnitUser(eData, $btn) {
    var unitUser = eData.unitUser;
    var multiple = unitUser.multiple;
    var selectType = unitUser.selectType;
    var taskName = eData.taskName;
    var filterCondition = '';
    for (var i = 0; i < eData.unitUser.initIds.length; i++) {
      var val = eData.unitUser.initIds[i];
      if (filterCondition != '') {
        filterCondition += ',';
      }
      filterCondition += val;
    }
    filterCondition = encodeURI(filterCondition);
    var isCancel = true;
    $.unit.open({
      initNames: '',
      initIDs: '',
      title: "选择承办人<span class='taskFontWeight'>【" + taskName + '】</span>',
      multiple: multiple,
      selectType: selectType,
      nameType: '21',
      type: 'Mixture',
      loginStatus: false,
      excludeValues: null,
      showType: false,
      filterCondition: filterCondition,
      close: function () {
        if (isCancel) {
          clearBeanData();
        }
      },
      afterSelect: function (laReturn) {
        isCancel = false;
        var taskUsers = {};
        var taskId = eData.taskId;
        if (isNotBlank(laReturn.id)) {
          // 在原来的环节办理人上增加环节办理人
          taskUsers = bean.taskUsers;
          var userIds = laReturn.id.split(';');
          taskUsers[taskId] = userIds;
        } else {
          taskUsers[taskId] = null;
          bean.taskUsers = taskUsers;
        }
        // 重新触发提交事件
        if (isNotBlank(eData.submitButtonId)) {
          $('#' + eData.submitButtonId).trigger('click');
        } else if ($btn) {
          $btn.trigger('click');
        } else {
          oAlert('请重新提交!');
        }
      }
    });
  }
  // 5、选择具体办理人
  function ChooseSpecificUser(eData, $btn) {
    var taskName = eData.taskName;
    var taskId = eData.taskId;
    var userIds = eData.userIds;
    var dlgSelector = '#dlg_choose_specific_user';
    // 创建弹出框Div
    createDiv(dlgSelector);
    var searchBox =
      "<div class='query-fields form_operate'>" + "<input name='query_input' />" + "<button type='button' class='btn'>查询</button></div>";
    $(dlgSelector).append(searchBox);
    $(dlgSelector).append("<div class='user-to-choose'></div>");
    var listChooseUser = function (userIds, queryValue) {
      JDS.call({
        service: 'workService.queryUsers',
        data: [userIds, queryValue],
        async: false,
        success: function (result) {
          var users = result.data;
          $('div.user-to-choose', dlgSelector).html('');
          for (var i = 0; i < users.length; i++) {
            var user = users[i];
            var $checkbox = $(
              "<label class='checkbox inline'><input id='" +
                user.id +
                "' name='chooseSpecificUser' type='checkbox' value='" +
                user.id +
                "'>" +
                user.userName +
                '</label>'
            );
            if (i % 2 == 0) {
              $checkbox.css('width', '150px').css('margin-left', '50px');
            }
            $('div.user-to-choose', dlgSelector).append($checkbox);
          }
        }
      });
    };
    var isCancel = true;
    var options = {
      title: '选择具体办理人' + '(' + taskName + ')',
      modal: true,
      autoOpen: true,
      resizable: false,
      width: 400,
      height: 300,
      open: function () {
        // 列表查询
        $("input[name='query_input']", dlgSelector).keypress(function (e) {
          if (e.keyCode == 13) {
            listChooseUser(userIds, $(this).val());
            return false;
          }
        });
        $("button[type='button']", dlgSelector).click(function (e) {
          var queryValue = $("input[name='query_input']", dlgSelector).val();
          listChooseUser(userIds, queryValue);
        });
        listChooseUser(userIds, '');
      },
      buttons: {
        确定: function (e) {
          if ($('input[name=chooseSpecificUser]:checked').length < 1) {
            oAlert('请选择具体办理人!');
            return;
          }
          var $checkboxes = $('input[name=chooseSpecificUser]:checked');
          var userIds = [];
          $.each($checkboxes, function (i) {
            userIds.push($(this).val());
          });
          bean.taskUsers[taskId] = userIds;
          isCancel = false;
          e.stopPropagation();
          $(this).oDialog('close');
          // 重新触发提交事件
          if (isNotBlank(eData.submitButtonId)) {
            $('#' + eData.submitButtonId).trigger('click');
          } else if ($btn) {
            $btn.trigger('click');
          } else {
            oAlert('请重新提交!');
          }
        },
        取消: function (e) {
          isCancel = true;
          e.stopPropagation();
          $(this).oDialog('close');
        }
      },
      close: function () {
        if (isCancel) {
          clearBeanData();
        }
        $(dlgSelector).html('');
      }
    };
    $(dlgSelector).oDialog(options);
  }
  // 6、只能选择一个人办理
  function OnlyChooseOneUser(eData, $btn) {
    var taskName = eData.taskName;
    var taskId = eData.taskId;
    var userIds = eData.userIds;
    var dlgSelector = '#dlg_choose_one_user';
    // 创建弹出框Div
    createDiv(dlgSelector);
    var searchBox =
      "<div class='query-fields form_operate'>" + "<input name='query_input' />" + "<button type='button' class='btn'>查询</button></div>";
    $(dlgSelector).append(searchBox);
    $(dlgSelector).append("<div class='user-to-choose'></div>");
    var listChooseUser = function (userIds, queryValue) {
      JDS.call({
        service: 'workService.queryUsers',
        data: [userIds, queryValue],
        async: false,
        success: function (result) {
          var users = result.data;
          $('div.user-to-choose', dlgSelector).html('');
          for (var i = 0; i < users.length; i++) {
            var user = users[i];
            var $radio = $(
              "<label class='radio inline'><input id='" +
                user.id +
                "' name='onlyOneUser' type='radio' value='" +
                user.id +
                "'>" +
                user.userName +
                '</label>'
            );
            if (i % 2 == 0) {
              $radio.css('width', '150px').css('margin-left', '50px');
            }
            $('div.user-to-choose', dlgSelector).append($radio);
          }
        }
      });
    };
    var isCancel = true;
    var options = {
      title: '选择一个办理人' + '(' + taskName + ')',
      modal: true,
      autoOpen: true,
      resizable: false,
      width: 400,
      height: 300,
      open: function () {
        // 列表查询
        $("input[name='query_input']", dlgSelector).keypress(function (e) {
          if (e.keyCode == 13) {
            listChooseUser(userIds, $(this).val());
            return false;
          }
        });
        $("button[type='button']", dlgSelector).click(function (e) {
          var queryValue = $("input[name='query_input']", dlgSelector).val();
          listChooseUser(userIds, queryValue);
        });
        listChooseUser(userIds, '');
      },
      buttons: {
        确定: function (e) {
          if ($('input[name=onlyOneUser]:checked').val() == null) {
            oAlert('请选择一个承办人!');
            return;
          }
          bean.taskUsers[taskId] = [$('input[name=onlyOneUser]:checked').val()];
          isCancel = false;
          e.stopPropagation();
          $(this).oDialog('close');
          // 重新触发提交事件
          if (isNotBlank(eData.submitButtonId)) {
            $('#' + eData.submitButtonId).trigger('click');
          } else if ($btn) {
            $btn.trigger('click');
          } else {
            oAlert('请重新提交!');
          }
        },
        取消: function (e) {
          isCancel = true;
          bean.taskUsers = {};
          e.stopPropagation();
          $(this).oDialog('close');
        }
      },
      close: function () {
        if (isCancel) {
          clearBeanData();
        }
        $(dlgSelector).html('');
      }
    };
    $(dlgSelector).oDialog(options);
  }
  // 7、弹出环节选择框选择下一流程环节
  function JudgmentBranchFlowNotFound(eData, $btn) {
    var toTasks = eData.toTasks;
    bean.fromTaskId = eData.fromTaskId;
    var multiselect = eData.multiselect;
    if (toTasks != null) {
      for (var i = 0; i < toTasks.length; i++) {
        var task = toTasks[i];
        if (multiselect) {
          var checkbox =
            "<div><label class='checkbox inline'><input id='" +
            task.id +
            "' name='toTaskId' type='checkbox' value='" +
            task.id +
            "'>" +
            task.name +
            '</label></div>';
          $('#dlg_select_task').append(checkbox);
        } else {
          var radio =
            "<div><label class='radio inline' style='margin-left: 20px;'><input id='" +
            task.id +
            "' name='toTaskId' type='radio' value='" +
            task.id +
            "'>" +
            task.name +
            '</label></div>';
          $('#dlg_select_task').append(radio);
        }
      }
    }
    // 显示选择下一环节弹出框
    showSelectTaskDialog({}, eData, $btn);
  }
  // 显示选择下一环节弹出框
  function showSelectTaskDialog(option, eData, $btn) {
    var isCancel = true;
    // 初始化下一流程选择框
    var options = {
      title: '选择下一环节',
      autoOpen: true,
      height: 300,
      width: 350,
      resizable: false,
      modal: true,
      buttons: {
        确定: function (e) {
          var $checkbox = $('input[name=toTaskId]:checked');
          if ($checkbox.length == 0) {
            bean.fromTaskId = null;
            oAlert('请选择环节!');
            return;
          }

          var toTaskId = '';
          $.each($checkbox, function (i) {
            toTaskId += $(this).val();
            if (i != $checkbox.length - 1) {
              toTaskId += ';';
            }
          });
          isCancel = false;
          bean.toTaskId = toTaskId;
          e.stopPropagation();
          $(this).oDialog('close');
          // 重新触发提交事件
          if (isNotBlank(eData.submitButtonId)) {
            $('#' + eData.submitButtonId).trigger('click');
          } else if ($btn) {
            $btn.trigger('click');
          } else {
            // $("#" + btn_submit).trigger('click');
            oAlert('请重新提交!');
          }
        },
        取消: function (e) {
          isCancel = true;
          bean.fromTaskId = null;
          e.stopPropagation();
          $(this).oDialog('close');
        }
      },
      close: function () {
        if (isCancel) {
          clearBeanData();
        }
        $('#dlg_select_task').html('');
      }
    };
    options = $.extend(true, options, option);
    $('#dlg_select_task').oDialog(options);
  }
  // 9、找到多个判断分支流向
  function MultiJudgmentBranch(eData, $btn) {
    JudgmentBranchFlowNotFound(eData, $btn);
  }
  // JQuery zTree设置
  var selectSubFlowSetting = {
    view: {
      showIcon: false
    },
    check: {
      chkStyle: 'radio'
    },
    async: {
      enable: true,
      contentType: 'application/json',
      url: ctx + '/json/data/services',
      otherParam: {
        serviceName: 'flowSchemeService',
        methodName: 'getFlowTree',
        data: ''
      },
      type: 'POST'
    }
  };
  // 9、弹出环节选择框选择下一子流程
  function SubFlowNotFound(eData, $btn) {
    var isCancel = true;
    selectSubFlowSetting.async.otherParam.data = eData.excludeFlowId;
    $('#wf_select_sub_flow').popupTreeWindow({
      title: '选择子流程',
      autoOpen: true,
      treeSetting: selectSubFlowSetting,
      afterSelect: function (retVal) {
        bean.toSubFlowId = retVal.value;
        isCancel = false;
        // 重新触发提交事件
        if (isNotBlank(eData.submitButtonId)) {
          $('#' + eData.submitButtonId).trigger('click');
        } else if ($btn) {
          $btn.trigger('click');
        } else {
          // $("#" + btn_submit).trigger('click');
          oAlert('请重新提交!');
        }
      },
      afterCancel: function () {
        isCancel = true;
      },
      // add by xieming 2015-04-21 begin
      close: function () {
        if (isCancel) {
          clearBeanData();
        }
      }
      // add by xieming 2015-04-21 end
    });
  }
  // 10、子流程合并等待异常类
  function SubFlowMerge(eData, $btn) {
    $('#dlg_sub_flow_merge').html(eData.msg);
    $('#dlg_sub_flow_merge').oDialog({
      title: '子流程合并等待',
      autoOpen: true,
      height: 300,
      width: 350,
      resizable: false,
      modal: true,
      buttons: {
        不等待: function (e) {
          var waitForMerge = {};
          waitForMerge[eData.subFlowInstUuid] = false;
          bean.waitForMerge = waitForMerge;
          e.stopPropagation();
          $(this).oDialog('close');
          // 重新触发提交事件
          if (isNotBlank(eData.submitButtonId)) {
            $('#' + eData.submitButtonId).trigger('click');
          } else if ($btn) {
            $btn.trigger('click');
          } else {
            $('#' + btn_submit).trigger('click');
          }
        },
        等待: function (e) {
          e.stopPropagation();
          $(this).oDialog('close');
        }
      },
      close: function () {
        $('#dlg_sub_flow_merge').html('');
      }
    });
  }

  // 11、用户没有权限访问流程
  function IdentityNotFlowPermission(eData) {
    var taskId = eData.taskId;
    if (bean.taskUsers != null && bean.taskUsers.hasOwnProperty(taskId)) {
      delete bean.taskUsers[taskId];
    }
    oAlert2(eData.msg);
  }

  // 12、找不到退回操作的退回环节异常类
  function RollbackTaskNotFound(eData, $btn) {
    var toTasks = eData.rollbackTasks;
    if (toTasks != null) {
      for (var i = 0; i < toTasks.length; i++) {
        var task = toTasks[i];
        var radio =
          "<div><label class='radio inline' style='margin-left: 20px;'><input id='" +
          task.id +
          "' name='rollbackToTaskId' type='radio' taskInstUuid='" +
          task.taskInstUuid +
          "' value='" +
          task.id +
          "'>" +
          task.name +
          '</label></div>';
        $('#dlg_select_rollback_task').append(radio);
      }
    }
    // 显示选择退回环节弹出框
    showSelectRollbackTaskDialog($btn);
  }
  // 显示选择退回环节弹出框
  function showSelectRollbackTaskDialog($btn) {
    // 显示选择退回环节弹出框
    var isCancel = true;
    var options = {
      title: '选择退回环节',
      autoOpen: true,
      height: 300,
      width: 350,
      resizable: false,
      modal: true,
      buttons: {
        确定: function (e) {
          if ($('input[name=rollbackToTaskId]:checked').val() == null) {
            oAlert('请选择环节!');
            return;
          }
          bean.rollbackToTaskId = $('input[name=rollbackToTaskId]:checked').val();
          bean.rollbackToTaskInstUuid = $('input[name=rollbackToTaskId]:checked').attr('taskInstUuid');
          e.stopPropagation();
          isCancel = false;
          $(this).oDialog('close');
          var btn_rollback_id = btn_rollback;
          if ($btn) {
            btn_rollback_id = $($btn).prop('id');
          }
          // 重新触发提交事件
          $('#' + btn_rollback_id).trigger('click');
        },
        取消: function (e) {
          isCancel = true;
          e.stopPropagation();
          $(this).oDialog('close');
        }
      },
      close: function () {
        if (isCancel) {
          clearBeanData();
        }
        $('#dlg_select_rollback_task').html('');
      }
    };
    $('#dlg_select_rollback_task').oDialog(options);
  }
  /** ******************************** 回调处理结束 ******************************** */

  /** ********************************* 退回开始 ********************************* */
  // 如果任务不存在，则隐藏退回按钮
  hideTaskButtons(btn_rollback);
  // 如果任务不存在，则隐藏直接退回按钮
  hideTaskButtons(btn_direct_rollback);

  // 退回
  $(":button[name='" + btn_rollback + "']", '.wf_operate').each(function () {
    $(this).click($.proxy(onRollback, this));
    showTaskButton(this);
  });
  // 退回处理
  function onRollback() {
    var $btn = $(this);
    var btnId = $btn.attr('id');
    // 确保退回时有签署意见
    if (isRequiredRollbackOpinion()) {
      openToSignOpinion(true, btnId);
    } else {
      // 操作动作及类型
      bean.action = isBlank($(this).text()) ? '退回' : $(this).text();
      bean.actionType = 'Rollback';
      // 获取表单数据
      bean.dyFormData = getDyformData();
      bean.rollbackToPreTask = false;
      JDS.call({
        service: rollbackWithWorkDataService,
        data: [bean],
        success: function (result) {
          oAlert('退回成功!', function () {
            handlerSuccess(result);
          });
        },
        error: function (jqXHR) {
          // 处理流程操作返回 的错误信息
          handlerError(jqXHR, $btn);
        }
      });
    }
  }
  WorkFlow.rollback = onRollback;

  // 直接退回
  $(":button[name='" + btn_direct_rollback + "']", '.wf_operate').each(function () {
    $(this).click($.proxy(onDirectRollback, this));
    showTaskButton(this);
  });
  // 直接退回处理
  function onDirectRollback() {
    var $btn = $(this);
    var btnId = $btn.attr('id');
    // 确保退回时有签署意见
    if (isRequiredRollbackOpinion()) {
      openToSignOpinion(true, btnId);
    } else {
      // 操作动作及类型
      bean.action = isBlank($(this).text()) ? '直接退回' : $(this).text();
      bean.actionType = 'DirectRollback';
      // 获取表单数据
      bean.dyFormData = getDyformData();
      bean.rollbackToPreTask = true;
      JDS.call({
        service: rollbackWithWorkDataService,
        data: [bean],
        success: function (result) {
          oAlert('直接退回成功!', function () {
            handlerSuccess(result);
            bean.rollbackToPreTask = false;
          });
        },
        error: function (jqXHR) {
          // 处理流程操作返回 的错误信息
          handlerError(jqXHR, $btn);
          bean.rollbackToPreTask = false;
        }
      });
    }
  }
  /** ********************************* 退回结束 ********************************* */

  /** ********************************* 撤回开始 ********************************* */
  // 如果任务不存在，则隐藏撤回按钮
  hideTaskButtons(btn_cancel);

  // 撤回
  $(":button[name='" + btn_cancel + "']", '.wf_operate').each(function () {
    $(this).click($.proxy(onCancel, this));
    showTaskButton(this);
  });
  // 撤回处理
  function onCancel() {
    var $btn = $(this);
    if (typeof WorkFlow.onCancel == 'function') {
      WorkFlow.onCancel.call(this, bean);
    } else {
      var taskInstUuids = [];
      taskInstUuids.push(bean.taskInstUuid);
      JDS.call({
        service: cancelService,
        data: [taskInstUuids],
        success: function (result) {
          oAlert('撤回成功！', function () {
            // 打开待办工作界面
            JDS.call({
              service: 'workService.getTodoTaskByFlowInstUuid',
              data: [bean.flowInstUuid],
              success: function (result) {
                if (isNotBlank(result.data.uuid)) {
                  // 刷新父窗口
                  if (returnWindow) {
                    returnWindow();
                  }
                  var taskInstUuid = result.data.uuid;
                  var flowInstUuid = bean.flowInstUuid;
                  window.location =
                    ctx +
                    '/workflow/work/view/todo?taskInstUuid=' +
                    taskInstUuid +
                    '&flowInstUuid=' +
                    flowInstUuid +
                    '&ep_wf_sign_opinion_model=' +
                    signOpinionModel;
                } else {
                  handlerSuccess(result);
                }
              },
              error: function (jqXHR) {
                // 处理流程操作返回 的错误信息
                handlerError(jqXHR, $btn);
              }
            });
          });
        },
        error: function (jqXHR) {
          // 处理流程操作返回 的错误信息
          handlerError(jqXHR, $btn);
        }
      });
    }
  }
  /** ********************************* 撤回结束 ********************************* */

  /** ********************************* 转办开始 ********************************* */
  // 如果任务不存在，则隐藏转办按钮
  hideTaskButtons(btn_transfer);

  // 转办
  $(":button[name='" + btn_transfer + "']", '.wf_operate').each(function () {
    $(this).click($.proxy(onTransfer, this));
    showTaskButton(this);
  });
  // 转办处理
  function onTransfer() {
    var $btn = $(this);
    var btnId = $btn.attr('id');
    // 确保转办时有签署意见
    if (isRequiredTransferOpinion()) {
      openToSignOpinion(true, btnId);
    } else {
      // 获取表单数据
      bean.dyFormData = getDyformData();
      $.unit.open({
        title: '选择转办人员',
        afterSelect: function (laReturn) {
          if (laReturn.id != null && laReturn.id != '') {
            var transferUserIds = laReturn.id.split(';');
            JDS.call({
              service: transferWithWorkDataService,
              data: [bean, transferUserIds],
              success: function (result) {
                oAlert('转办成功!', function () {
                  handlerSuccess(result);
                });
              },
              error: function (jqXHR) {
                // 处理流程操作返回 的错误信息
                handlerError(jqXHR, $btn);
              }
            });
          } else {
            oAlert('转办人员不能为空!');
          }
        }
      });
    }
  }
  /** ********************************* 转办结束 ********************************* */

  /** ********************************* 会签开始 ********************************* */
  // 如果任务不存在，则隐藏会签按钮
  hideTaskButtons(btn_counterSign);

  // 会签
  $(":button[name='" + btn_counterSign + "']", '.wf_operate').each(function () {
    $(this).click($.proxy(onCounterSign, this));
    showTaskButton(this);
  });
  // 会签处理
  function onCounterSign() {
    var $btn = $(this);
    var btnId = $btn.attr('id');
    // 确保会签时有签署意见
    if (isRequiredCounterSignOpinion()) {
      openToSignOpinion(true, btnId);
    } else {
      bean.dyFormData = getDyformData();
      $.unit.open({
        title: '选择会签人员',
        afterSelect: function (laReturn) {
          if (laReturn.id != null && laReturn.id != '') {
            var counterSignUserIds = laReturn.id.split(';');
            JDS.call({
              service: counterSignWithWorkDataService,
              data: [bean, counterSignUserIds],
              success: function (result) {
                oAlert('会签成功!', function () {
                  handlerSuccess(result);
                });
              },
              error: function (jqXHR) {
                // 处理流程操作返回 的错误信息
                handlerError(jqXHR, $btn);
              }
            });
          } else {
            oAlert('会签人员不能为空!');
          }
        }
      });
    }
  }
  /** ********************************* 会签结束 ********************************* */

  /** ********************************* 关注开始 ********************************* */
  // 如果任务不存在，则隐藏关注按钮
  hideTaskButtons(btn_attention);

  // 关注
  $(":button[name='" + btn_attention + "']", '.wf_operate').each(function () {
    $(this).click($.proxy(onAttention, this));
    showTaskButton(this);
  });
  // 关注处理
  function onAttention(e) {
    var $btn = $(this);
    var taskInstUuids = [];
    taskInstUuids.push(bean.taskInstUuid);
    JDS.call({
      service: attentionService,
      data: [taskInstUuids],
      success: function (result) {
        oAlert('关注成功!', function () {
          reload();
        });
      },
      error: function (jqXHR) {
        // 处理流程操作返回 的错误信息
        handlerError(jqXHR, $btn);
      }
    });
  }
  /** ********************************* 关注结束 ********************************* */

  /** ********************************* 套打开始 ********************************* */
  // 如果任务不存在，则隐藏打印按钮
  hideTaskButtons(btn_print);

  // 套打
  $(":button[name='" + btn_print + "']", '.wf_operate').each(function () {
    $(this).click($.proxy(onPrint, this));
    showTaskButton(this);
  });
  // 套打处理
  function onPrint(e) {
    // CA检验
    if (checkCAKey() == false) {
      return false;
    }

    // 打印处理
    doPrint(e);
  }
  // 打印处理
  function doPrint(e) {
    var printToDisplay = bean['printToDisplay'];
    // 提交前回调处理
    if (isNotBlank(bean['printService'])) {
      var printService = bean['printService'];
      $('input[name=printService]', $('#print_form')).val(printService);
    }
    if (printToDisplay === true) {
      var fullPrintUrl = ctx + printUrl + '?printToDisplay=true&' + $('#print_form').serialize();
      window.open(fullPrintUrl);
    } else {
      var printTemplateId = $('input[name=printTemplateId]', $('#print_form')).val();
      var printType = 'doc';
      if (isNotBlank(printTemplateId)) {
        JDS.call({
          service: 'workService.getPrintType',
          data: [printTemplateId],
          async: false,
          success: function (result) {
            printType = result.data;
          }
        });
      }
      if (printType === 'html') {
        var fullPrintUrl = ctx + printUrl + '?printToDisplay=true&' + $('#print_form').serialize();
        window.open(fullPrintUrl);
      } else {
        var $printForm = $('#print_form');
        $printForm.attr('action', ctx + printUrl);
        $printForm[0].submit();
        if (e) {
          e.stopPropagation();
        }
        // 重置打印表单
        $printForm[0].reset();
      }
    }
  }
  // 打印成功后处理，对打开新浏览器的方式有效
  WorkFlow.onSuccessPrint = function () {
    if (typeof WorkFlow.afterSuccessPrint == 'function') {
      WorkFlow.afterSuccessPrint.call(this, bean);
    }
  };
  // iframe加载内容事件处理
  var document = this;
  $('#print_form_iframe').load(function (e) {
    var doc = this.contentWindow.document;
    var msg = $('#print_response_msg', doc).val();
    var pts = eval(msg);
    doPrintMsgLoad.call(document, pts);
  });
  // 提示后台返回的打印信息
  function doPrintMsgLoad(pts) {
    // 重置打印表单
    var $printForm = $('#print_form');
    $printForm[0].reset();

    if (pts && pts.constructor == Array) {
      for (var i = 0; i < pts.length; i++) {
        var printTemplate = pts[i];
        var radio =
          "<div><label class='radio inline'><input id='print_template_" +
          printTemplate.id +
          "' name='selectPrintTemplateId' type='radio' value='" +
          printTemplate.id +
          "'>" +
          printTemplate.name +
          '</label></div>';
        $('#dlg_select_print_template').append(radio);
      }
      // 显示选择套打模板弹出框
      showSelectPrintTemplateDialog();
    } else if (typeof pts == 'object') {
      oAlert(pts.msg);
    } else {
      oAlert(pts);
    }
  }
  // 显示选择套打模板弹出框
  function showSelectPrintTemplateDialog() {
    // 显示选择套打模板弹出框
    var options = {
      title: '选择套打模板',
      autoOpen: true,
      height: 300,
      width: 350,
      resizable: false,
      modal: true,
      buttons: {
        确定: function (e) {
          if ($('input[name=selectPrintTemplateId]:checked').val() == null) {
            oAlert('请选择环节!');
            return;
          }
          var printTemplateId = $('input[name=selectPrintTemplateId]:checked').val();
          // 设置套打模板
          $('input[name=printTemplateId]').val(printTemplateId);
          e.stopPropagation();
          $(this).oDialog('close');
          // 重新触发提交事件
          $('#' + btn_print).trigger('click');
        },
        取消: function (e) {
          e.stopPropagation();
          $(this).oDialog('close');
        }
      },
      close: function () {
        $('#dlg_select_print_template').html('');
      }
    };
    $('#dlg_select_print_template').oDialog(options);
  }
  /** ********************************* 套打结束 ********************************* */

  /** ********************************* 抄送开始 ********************************* */
  // 如果任务不存在，则隐藏抄送按钮
  hideTaskButtons(btn_copyTo);

  // 抄送
  $(":button[name='" + btn_copyTo + "']", '.wf_operate').each(function () {
    $(this).click($.proxy(onCopyTo, this));
    showTaskButton(this);
  });
  // 抄送处理
  function onCopyTo() {
    var $btn = $(this);
    $.unit.open({
      title: '选择抄送人员',
      afterSelect: function (laReturn) {
        if (laReturn.id != null && laReturn.id != '') {
          var copyToUserIds = laReturn.id.split(';');
          JDS.call({
            service: copyToService,
            data: [[bean.taskInstUuid], copyToUserIds],
            success: function (result) {
              oAlert('抄送成功!', function () {
                // handlerSuccess(result);
              });
            },
            error: function (jqXHR) {
              // 处理流程操作返回 的错误信息
              handlerError(jqXHR, $btn);
            }
          });
        }
      }
    });
  }
  /** ********************************* 抄送开始 ********************************* */

  /** ******************************** 签署意见开始 ******************************* */
  // 如果任务不存在，则隐藏签署意见按钮
  hideTaskButtons(btn_sign_opinion);

  // 签署意见
  $(":button[name='" + btn_sign_opinion + "']", '.wf_operate').hide();
  $(":button[name='" + btn_sign_opinion + "']", '.wf_operate').each(function () {
    $(this).click($.proxy(onSignOpinion, this));
  });
  // 签署意见事件处理
  function onSignOpinion(e) {
    if (bean.aclRole === 'TODO') {
      openToSignOpinion(true, btn_submit);
    } else {
      openToSignOpinion(false, btn_submit);
    }
  }
  // 显示签署意见
  function showToSignOpinion() {
    // 签署意见内嵌模式
    if (signOpinionModel === '1') {
      if (isNotBlank(bean.taskInstUuid) && (bean.aclRole === 'TODO' || bean.aclRole === 'MONITOR')) {
        var $btn = $('.form_toolbar>.wf_operate>button:visible:first');
        var $opinion = $('#mini_wf_opinion');
        var position1 = {
          left: 1000
        };
        if ($btn.length != 0) {
          position1 = $btn.position();
        }
        var position2 = $opinion.position();
        var opinionWidth = position1.left - position2.left - 120;
        var valStr = $('input[name=opinions]').val();
        var opinions = JSON.parse(valStr);
        $('#mini_wf_opinion').workflowMiniOpinion({
          width: opinionWidth,
          opinions: opinions,
          opinionChange: function (retVal) {
            bean.opinionValue = retVal.value;
            bean.opinionLabel = retVal.label;
            bean.opinionText = retVal.text;
          }
        });
      }
    } else if (signOpinionModel === '2') {
      // 签署意见弹出框模式
      showButtons(btn_sign_opinion);
    }
  }
  // 打开进行签署意见
  function openToSignOpinion(submitOpinion, btnId) {
    // 签署意见内嵌模式
    if (signOpinionModel === '1') {
      if (isNotBlank(bean.taskInstUuid) && ((submitOpinion === true && bean.aclRole === 'TODO') || bean.aclRole === 'MONITOR')) {
        $('#mini_wf_opinion').workflowMiniOpinion('open');
      }
    } else if (signOpinionModel === '2') {
      var dlgOptions = {
        value: bean.opinionValue,
        label: bean.opinionLabel,
        text: bean.opinionText,
        ok: function (retVal) {
          bean.opinionValue = retVal.value;
          bean.opinionLabel = retVal.label;
          bean.opinionText = retVal.text;
        },
        cancel: function () {}
      };
      if (submitOpinion == true && isNotBlank(btnId)) {
        dlgOptions.submitLable = $('#' + btnId, '.wf_operate').text();
        dlgOptions.submit = function (retVal) {
          bean.opinionValue = retVal.value;
          bean.opinionLabel = retVal.label;
          bean.opinionText = retVal.text;
          $('#' + btnId, '.wf_operate').trigger('click');
        };
      }
      // 签署意见弹出框模式
      $.workflowOpinion.open(dlgOptions);
    }
  }
  // 签署意见
  function signOpinion(data) {
    if (signOpinionModel === '1') {
      $('#mini_wf_opinion').workflowMiniOpinion('signOpinion', data);
    } else {
      bean.opinionValue = data.value;
      bean.opinionLabel = data.label;
      bean.opinionText = data.text;
    }
  }
  /** ******************************** 签署意见结束 ******************************* */

  /** ******************************** 办理意见开始 ******************************* */
  // 如果任务不存在，则隐藏办理意见按钮
  hideTaskButtons(btn_view_opinion);
  // 办理意见
  $(":button[name='" + btn_view_opinion + "']", '.wf_operate').hide();
  $(":button[name='" + btn_view_opinion + "']", '.wf_operate').each(function () {
    $(this).click($.proxy(onViewOpinion, this));
  });
  // 办理意见处理
  function onViewOpinion(e) {
    var $btn = $(this);
    $('#dlg_view_opinion').oDialog({
      title: '办理意见',
      autoOpen: true,
      height: 400,
      width: 450,
      open: function () {
        JDS.call({
          service: viewOpinionService,
          data: bean,
          success: function (result) {
            $('#dlg_view_opinion').html(result.data);
          },
          error: function (jqXHR) {
            // 处理流程操作返回 的错误信息
            handlerError(jqXHR, $btn);
          }
        });
      },
      close: function () {
        $('#dlg_view_opinion').html('');
      }
    });
  }
  /** ******************************** 办理意见结束 ******************************* */

  /** ******************************** 办理过程开始 ******************************* */
  // 如果任务不存在，则隐藏办理过程按钮
  hideTaskButtons(btn_view_process);
  // 办理过程
  if ($(":button[name='" + btn_view_process + "']").length == 0) {
    $(":button[name='" + btn_view_process + "']").hide();
  } else {
    $(":button[name='" + btn_view_process + "']").hide();
  }
  $(":button[name='" + btn_view_process + "'], #process .view_process").each(function () {
    $(this).click($.proxy(onViewProcess, this));
  });
  // 办理意见处理
  function onViewProcess(e) {
    showProcess();
  }

  $('#show_rollback_record, #show_no_opinion_record').change(function (e) {
    showProcess();
  });

  function showProcess(show_rollback_record, show_no_opinion_record) {
    var show_rollback_record = $('#show_rollback_record').attr('checked') == null ? false : true;
    var show_no_opinion_record = $('#show_no_opinion_record').attr('checked') == null ? false : true;
    JDS.call({
      service: viewProcessService,
      data: [bean.flowInstUuid, show_rollback_record, show_no_opinion_record],
      success: function (result) {
        $('#process_content').html(result.data);
        $('#dlg_view_process').oDialog('open');

        var url = ctx + '/workflow/show?open&id=' + bean.flowDefUuid;
        $('#dlg_view_process .view_process').unbind('click');
        $('#dlg_view_process .view_process').click(function () {
          window.open(url);
        });

        // 调整自适应表单宽度
        adjustWidthToForm();
      },
      error: function (jqXHR) {
        // 处理流程操作返回 的错误信息
        handlerError(jqXHR);
      }
    });
  }

  // 办理意见窗口
  $('#dlg_view_process').oDialog({
    title: '办理过程',
    modal: true,
    resizable: false,
    autoOpen: false,
    height: 540,
    width: 940
  });
  /** ******************************** 办理过程结束 ******************************* */

  /** ******************************** 时间轴开始 ******************************** */
  function showTimeline() {
    JDS.call({
      service: timelineService,
      data: [bean.flowInstUuid],
      success: function (result) {}
    });
  }
  /** ******************************** 时间轴结束 ******************************** */

  /** ******************************** 催办开始 ********************************** */
  // 如果任务不存在，则隐藏办理过程按钮
  hideTaskButtons(btn_remind);
  // 催办
  $(":button[name='" + btn_remind + "']", '.wf_operate').each(function () {
    $(this).click($.proxy(onRemind, this));
    showTaskButton(this);
  });
  // 催办处理
  function onRemind(e) {
    JDS.call({
      service: remindService,
      data: [[bean.taskInstUuid], bean.opinionLabel, bean.opinionValue, bean.opinionText],
      success: function (result) {
        oAlert('催办成功!');
      },
      error: function (jqXHR) {
        oAlert('催办失败!');
      }
    });
  }
  /** ******************************** 催办结束 ********************************** */

  /** ******************************** 取消关注 ********************************** */
  // 如果任务不存在，则隐藏取消关注按钮
  hideTaskButtons(btn_unfollow);
  // 取消关注
  $(":button[name='" + btn_unfollow + "']", '.wf_operate').each(function () {
    $(this).click($.proxy(onUnfollow, this));
    showTaskButton(this);
  });
  // 取消关注处理
  function onUnfollow(e) {
    JDS.call({
      service: unfollowdService,
      data: [[bean.taskInstUuid]],
      success: function (result) {
        oAlert('取消关注成功!', function () {
          reload();
        });
      },
      error: function (jqXHR) {
        oAlert('取消关注失败!');
      }
    });
  }
  /** ******************************* 取消关注结束 ********************************* */

  /** ******************************** 移交开始 ********************************** */
  // 如果任务不存在，则隐藏移交按钮
  hideTaskButtons(btn_hand_over);
  // 移交
  $(":button[name='" + btn_hand_over + "']", '.wf_operate').each(function () {
    $(this).click($.proxy(onHandOver, this));
    showTaskButton(this);
  });
  // 移交处理
  function onHandOver(e) {
    var $btn = $(this);
    var btnId = $btn.attr('id');
    // 确保特送个人时有签署意见
    if (isRequiredHandOverOpinion()) {
      openToSignOpinion(true, btnId);
    } else {
      $.unit.open({
        title: '选择移交人员',
        afterSelect: function (laReturn) {
          if (laReturn.id != null && laReturn.id != '') {
            var handOverUserIds = laReturn.id.split(';');
            JDS.call({
              service: handOverService,
              data: [bean, handOverUserIds],
              success: function (result) {
                oAlert('移交成功!', function () {
                  handlerSuccess(result);
                });
              },
              error: function (jqXHR) {
                // 处理流程操作返回 的错误信息
                handlerError(jqXHR, $btn);
              }
            });
          } else {
            oAlert('请选择移交人员!');
          }
        }
      });
    }
  }
  /** ******************************** 移交结束 ********************************** */

  /** ******************************** 跳转开始 ********************************** */
  // 如果任务不存在，则隐藏跳转按钮
  hideTaskButtons(btn_do_goto);
  // 跳转
  $(":button[name='" + btn_do_goto + "']", '.wf_operate').each(function () {
    $(this).click($.proxy(onDoGoto, this));
    showTaskButton(this);
  });
  // 跳转处理
  function onDoGoto(e) {
    var $btn = $(this);
    var btnId = $btn.attr('id');
    // 确保特送环节时有签署意见
    if (isRequiredDoGotoOpinion()) {
      openToSignOpinion(true, btnId);
    } else {
      var gotoTaskId = bean.gotoTaskId;
      if (isBlank(gotoTaskId)) {
        JDS.call({
          service: getGotoTaskService,
          data: [bean.taskInstUuid],
          success: function (result) {
            bean.gotoTask = true;
            showGotoTaskDialog($btn, result.data);
          },
          error: function (jqXHR) {
            // 处理流程操作返回 的错误信息
            handlerError(jqXHR);
          }
        });
      } else {
        JDS.call({
          service: gotoTaskService,
          data: [bean],
          success: function (result) {
            oAlert('跳转成功!', function () {
              handlerSuccess(result);
            });
          },
          error: function (jqXHR) {
            // 处理流程操作返回 的错误信息
            handlerError(jqXHR, $btn);
          }
        });
      }
    }
  }

  function showGotoTaskDialog($btn, data) {
    var toTasks = data.toTasks;
    bean.fromTaskId = data.fromTaskId;
    if (toTasks != null) {
      for (var i = 0; i < toTasks.length; i++) {
        var task = toTasks[i];
        var radio =
          "<div><label class='radio inline' style='margin-left: 20px;'><input id='" +
          task.id +
          "' name='toTaskId' type='radio' value='" +
          task.id +
          "'>" +
          task.name +
          '</label></div>';
        $('#dlg_select_goto_task').append(radio);
      }
    }
    // 初始化下一流程选择框
    var options = {
      title: '选择跳转环节',
      autoOpen: true,
      height: 300,
      width: 350,
      resizable: false,
      modal: true,
      buttons: {
        确定: function (e) {
          var $checkbox = $('input[name=toTaskId]:checked');
          if ($checkbox.length == 0) {
            bean.fromTaskId = null;
            oAlert('请选择跳转环节!');
            return;
          }
          var gotoTaskId = $checkbox.val();
          bean.gotoTaskId = gotoTaskId;
          e.stopPropagation();
          $(this).oDialog('close');
          // 重新触发跳转事件
          $btn.trigger('click');
        },
        取消: function (e) {
          bean.fromTaskId = null;
          e.stopPropagation();
          $(this).oDialog('close');
        }
      },
      close: function () {
        $('#dlg_select_goto_task').html('');
      }
    };
    $('#dlg_select_goto_task').oDialog(options);
  }
  /** ******************************** 跳转结束 ********************************** */

  /** ******************************** 挂起开始 ********************************** */
  // 如果任务不存在，则隐藏挂起按钮
  hideTaskButtons(btn_suspend);
  if (bean.suspensionState == 1) {
    hideButtons(btn_suspend);
  } else {
    showButtons(btn_suspend);
  }
  // 挂起
  $(":button[name='" + btn_suspend + "']", '.wf_operate').each(function () {
    $(this).click($.proxy(onSuspend, this));
    showTaskButton(this);
  });
  // 挂起处理
  function onSuspend(e) {
    var $btn = $(this);
    var taskInstUuids = [];
    taskInstUuids.push(bean.taskInstUuid);
    JDS.call({
      service: suspendService,
      data: [taskInstUuids],
      success: function (result) {
        oAlert('挂起成功!', function () {
          bean.suspensionState = 1;
          showButtons(btn_resume);
          hideButtons(btn_suspend);
        });
      },
      error: function (jqXHR) {
        // 处理流程操作返回 的错误信息
        handlerError(jqXHR, $btn);
      }
    });
  }
  /** ******************************** 挂起结束 ********************************** */

  /** ******************************** 恢复开始 ********************************** */
  // 如果任务不存在，则隐藏恢复按钮
  hideTaskButtons(btn_resume);
  if (bean.suspensionState == 1) {
    showButtons(btn_resume);
  } else {
    hideButtons(btn_resume);
  }
  // 恢复
  $(":button[name='" + btn_resume + "']", '.wf_operate').each(function () {
    $(this).click($.proxy(onResume, this));
    showTaskButton(this);
  });
  // 恢复处理
  function onResume(e) {
    var $btn = $(this);
    var taskInstUuids = [];
    taskInstUuids.push(bean.taskInstUuid);
    JDS.call({
      service: resumeService,
      data: [taskInstUuids],
      success: function (result) {
        oAlert('恢复成功!', function () {
          bean.suspensionState = 0;
          showButtons(btn_suspend);
          hideButtons(btn_resume);
        });
      },
      error: function (jqXHR) {
        // 处理流程操作返回 的错误信息
        handlerError(jqXHR, $btn);
      }
    });
  }
  /** ******************************** 恢复结束 ********************************** */

  /** ******************************** 退件开始 ********************************** */
  /** ******************************** 退件结束 ********************************** */

  /** ******************************** 删除开始 ********************************** */
  // 如果任务不存在，则隐藏删除按钮
  hideTaskButtons(btn_delete);

  // 挂起
  $(":button[name='" + btn_delete + "']", '.wf_operate').each(function () {
    $(this).click($.proxy(onDelete, this));
    showTaskButton(this);
  });
  // 删除处理
  function onDelete() {
    var $btn = $(this);
    var taskInstUuids = [];
    taskInstUuids.push(bean.taskInstUuid);
    JDS.call({
      service: deleteService,
      data: [taskInstUuids],
      success: function (result) {
        oAlert('删除成功!', function () {
          window.close();
        });
      },
      error: function (jqXHR) {
        // 处理流程操作返回 的错误信息
        handlerError(jqXHR, $btn);
      }
    });
  }
  /** ******************************** 删除结束 ********************************** */

  /** ****************************** 公共方法开始 ******************************** */
  // 任务不存在时隐藏名字为指定值的按钮
  function hideTaskButtons(name) {
    // 如果任务不存在，则隐藏签署意见按钮
    if (isBlank(bean.taskInstUuid)) {
      $(":button[name='" + name + "']").each(function () {
        $(this).hide();
      });
    }
  }

  function showTaskButton(button) {
    // 如果任务存在，则显示按钮
    if (isNotBlank(bean.taskInstUuid)) {
      $(button).show();
    }
  }

  function hideButtons(name) {
    $(":button[name='" + name + "']").each(function () {
      $(this).hide();
    });
  }

  function showButtons(name) {
    $(":button[name='" + name + "']").each(function () {
      $(this).show();
    });
  }

  // 创建DIV元素
  function createDiv(selector) {
    var id = selector;
    if (selector.indexOf('#') == 0) {
      id = selector.substring(1);
    }
    // 放置
    if ($(selector).length == 0) {
      $('body').append("<div id='" + id + "' />");
    }
  }
  /** ****************************** 公共方法结束 ******************************** */
  // 显示操作按钮
  $('.wf_operate').show();

  // 本地存储保存bean.opinionText
  function storeOpinionText(bean) {
    if (sessionStorage && isNotBlank(bean.opinionText)) {
      var key = bean.flowInstUuid + bean.userId;
      sessionStorage.setItem(key, bean.opinionText);
    }
  }
  // 本地存储还原bean.opinionText
  function restoreOpinionText(bean) {
    if (sessionStorage) {
      var key = bean.flowInstUuid + bean.userId;
      var text = sessionStorage.getItem(key);
      if (isNotBlank(text)) {
        bean.opinionText = text;
      }
    }
  }
  // 本地存储删除bean.opinionText
  function removeOpinionText(bean) {
    if (sessionStorage) {
      sessionStorage.clear();
    }
  }

  /** ****************************** 对外接口开始 ******************************** */
  // Java扩展
  // 1、printService 自定义打印服务处理
  // 2、afterSaveService 保存后服务处理
  // 3、beforeSubmitService 提交前服务处理
  // 4、afterSubmitService 提交后服务处理
  // JS扩展
  // 1、WorkFlow.afterSuccessSave 保存成功后全局回调扩展
  // 2、WorkFlow.beforeSubmit 提交前回调处理
  // 3、WorkFlow.afterSuccessSubmit 提交成功后全局回调扩展
  // 对外JS接口
  // 判断当前环节实例(工作)是否存在
  WorkFlow.isExistsWorkData = function () {
    return isNotBlank(bean.taskInstUuid) || isNotBlank(bean.flowInstUuid);
  };
  // 返回当前环节ID
  WorkFlow.getTaskId = function () {
    return $('#wf_taskId').val();
  };
  // 返回当前环节名称
  WorkFlow.getTaskName = function () {
    return $('#wf_taskName').val();
  };
  // 返回当前环节实例UUID
  WorkFlow.getTaskInstUuid = function () {
    return bean.taskInstUuid;
  };
  // 返回当前流程实例UUID
  WorkFlow.getFlowInstUuid = function () {
    return bean.flowInstUuid;
  };
  // 添加按钮
  WorkFlow.addButton = function (button) {
    $('.wf_operate').append(button);
  };
  // 隐藏按钮
  WorkFlow.hideButton = function (buttonId) {
    $("button[id='" + buttonId + "']", '.wf_operate').hide();
  };
  // 隐藏所有按钮
  WorkFlow.hideAllButton = function () {
    // 隐藏所有操作按钮
    $(':button', '.wf_operate').each(function () {
      $(this).hide();
    });
  };
  // 显示按钮
  WorkFlow.showButton = function (buttonId) {
    $("button[id='" + buttonId + "']", '.wf_operate').show();
  };
  // 绑定工作流事件
  WorkFlow.bind = function (option) {
    var functionName = option.functionName;
    var id = option.id;
    var name = option.name;
    if (functionName == 'print') {
      if ($('#' + btn_print).length == 0) {
        var print = '<button type="button" id="' + btn_print + '">' + name + '</button>';
        $('.wf_operate').append(print);
        var $button = $('#' + btn_print, '.wf_operate');
        $button.click($.proxy(onPrint, $button));
      } else {
        $('#' + btn_print).show();
        $('#' + btn_print).text(name);
      }
    } else if (functionName == 'submit') {
      var $button = $('#' + id);
      $button.click($.proxy(onSubmit, $button[0]));
    } else if (functionName == 'save') {
      var $button = $('#' + id);
      var callBack = option.onSuccess;
      $button.click($.proxy(onSave, $button[0], true, callBack));
    }
    if (id != null && functionName == null) {
      var $button = $("button[id='" + id + "']", '.wf_operate');
      if ($button.length == 0) {
        var button = '<button type="button" id="' + id + '">' + name + '</button>';
        $('.wf_operate').append(button);
        $button = $("button[id='" + id + "']", '.wf_operate');
      }
      var onClick = option.onClick;
      if (onClick != null) {
        $button.click($.proxy(onClick, $button));
      }
    }
  };
  // 设置套打模板
  WorkFlow.setPrintTemplateId = function (templateId) {
    $('input[name=printTemplateId]', $('#print_form')).val(templateId);
  };
  WorkFlow.print = function (templateId) {
    $('input[name=printTemplateId]', $('#print_form')).val(templateId);

    doPrint();
  };
  // 设置工作数据
  WorkFlow.setWorkData = function (propName, propValue) {
    bean[propName] = propValue;
  };
  // 获取工作数据
  WorkFlow.getWorkData = function () {
    return bean;
  };
  // 放置工作额外数据
  WorkFlow.putExtraParam = function (paramName, paramValue) {
    bean.extraParams[paramName] = paramValue;
  };
  // 获取工作额外数据
  WorkFlow.getExtraParams = function () {
    return bean.extraParams;
  };
  // 判断是否需要签署意见
  WorkFlow.isRequiredSignOpinion = function () {
    return isRequiredSignOpinion();
  };
  // 设置是否需要签署意见
  WorkFlow.setRequiredSignOpinion = function (required) {
    requiredSignOpinion = required;
  };
  // 显示签署意见框，已过时，使用WorkFlow.showToSignOpinion
  WorkFlow.showOpinion = function () {
    showToSignOpinion();
  };
  // 显示签署意见框或按钮
  WorkFlow.showToSignOpinion = function () {
    showToSignOpinion();
  };
  // 打开进行签署意见，已过时，使用WorkFlow.openToSignOpinion
  WorkFlow.showSignOpinionDialog = function (submitOpinion) {
    openToSignOpinion(submitOpinion);
  };
  // 打开进行签署意见
  WorkFlow.openToSignOpinion = function (submitOpinion, btnId) {
    openToSignOpinion(submitOpinion, btnId);
  };
  // 签署意见
  WorkFlow.signOpinion = signOpinion;
  // 错误处理
  WorkFlow.handlerError = handlerError;
  // 获取动态表单选择器
  WorkFlow.getDyformSelector = function () {
    return dytableSelector;
  };
  // 获取办理过程信息
  WorkFlow.getWorkProcess = function () {
    return workProcess;
  };
  // 工作流数据是否显示为文本
  WorkFlow.isWorkDataDisplayAsLabel = function () {
    return workDataDisplayAsLabel;
  };
  /** ****************************** 对外接口结束 ******************************** */

  /* add by huanglinchuan 2014.10.18 begin */
  window.onscroll = function () {
    if ($(document).scrollTop() > 10) {
      $('#topHeader').addClass('floatHeader');
      $('.form_header').addClass('formHeader_top0');
      $('.floatHeaderBack').show();
    } else {
      $('#topHeader').removeClass('floatHeader');
      $('.form_header').removeClass('formHeader_top0');
      $('.floatHeaderBack').hide();
    }
  };
  /* add by huanglinchuan 2014.10.18 end */
});
