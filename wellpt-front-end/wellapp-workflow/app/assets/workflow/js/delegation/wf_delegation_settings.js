define([
  'jquery',
  'commons',
  'constant',
  'server',
  'appContext',
  'appModal',
  'jquery-dmsDocumentView',
  'layDate',
  'appWindowManager'
], function ($, commons, constant, server, appContext, appModal, dmsDocumentView, layDate, appWindowManager) {
  var Browser = commons.Browser;
  var UrlUtils = commons.UrlUtils;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var SpringSecurityUtils = server.SpringSecurityUtils;
  var JDS = server.JDS;
  var userDetails = SpringSecurityUtils.getUserDetails();
  // 查看
  var duty_agent_view = $('#duty_agent_view').val();
  // 征求意见
  var consult = $('#duty_agent_consult').val();
  var uuid = $('#uuid').val();
  if (StringUtils.isBlank(uuid)) {
    $('#consignor_name').val(userDetails.userName);
    $('#consignor').val(userDetails.userId);
  }

  var getNowFormatted = function () {
    var nowtime = new Date();
    var month = nowtime.getMonth() + 1;
    if (month < 10) {
      month = '0' + month;
    }
    var date = nowtime.getDate();
    if (date < 10) {
      date = '0' + date;
    }
    var hour = nowtime.getHours();
    if (hour < 10) {
      hour = '0' + hour;
    }
    var minute = nowtime.getMinutes();
    if (minute < 10) {
      minute = '0' + minute;
    }
    var ymd = new StringBuilder(nowtime.getFullYear(), month, date, '-');
    var hm = new StringBuilder(hour, minute, ':');
    return new StringBuilder(ymd, hm, ' ').toString();
  };

  // layDate风格日期控件
  var minToTime = null;
  layDate.render({
    elem: '#from_time',
    type: 'datetime',
    trigger: 'click',
    format: 'yyyy-MM-dd HH:mm',
    ready: function (date) {
      if (date != null) {
        minToTime = {
          year: date.year,
          month: date.month - 1,
          date: date.date,
          hours: date.hours,
          minutes: date.minutes,
          seconds: date.seconds
        };
      }
    },
    done: function (value, date) {
      if (date != null) {
        minToTime = {
          year: date.year,
          month: date.month - 1,
          date: date.date,
          hours: date.hours,
          minutes: date.minutes,
          seconds: date.seconds
        };
      }
    }
  });
  var flag = false;
  layDate.render({
    elem: '#to_time',
    type: 'datetime',
    istoday: false,
    trigger: 'click',
    format: 'yyyy-MM-dd HH:mm',
    ready: function (value, date) {
      if (minToTime != null) {
        this.min = minToTime;
      }
    },
    done: function (value, date) {
      if (flag == true) {
        return;
      }
      var now = getNowFormatted();
      if (value == now) {
        flag = true;
        appModal.error('不能选择当前时间！', function () {
          $('#to_time').val(null);
          flag = false;
        });
      }
    }
  });

  var bean = {
    uuid: null,
    consignor: null,
    trustee: null,
    content: null,
    contentName: null,
    includeCurrentWork: false,
    dueToTakeBackWork: false,
    deactiveToTakeBackWork: false,
    condition: null,
    delegationTaskVisible: false,
    status: null,
    fromTime: null,
    toTime: null,
    consignorName: null,
    trusteeName: null,
    formatedFromTime: null,
    formatedToTime: null
  };
  // 获得Bean
  if (uuid != undefined && uuid != '' && uuid != null) {
    JDS.restfulGet({
      url: ctx + '/api/workflow/delegation/settiongs/get',
      data: {
        uuid: uuid
      },
      contentType: 'application/x-www-form-urlencoded',
      async: false,
      success: function (result) {
        bean = result.data;
        $('#consignor_name').val(bean.consignorName);
        $('#consignor').val(bean.consignor);
        $('#trustee_name').val(bean.trusteeName);
        $('#trustee').val(bean.trustee);
        $('#condition').val(bean.condition);
        $('#content_name').val(bean.contentName);
        $('#content').val(bean.content);
        $('#includeCurrentWork').prop('checked', bean.includeCurrentWork);
        $('#dueToTakeBackWork').prop('checked', bean.dueToTakeBackWork);
        $('#deactiveToTakeBackWork').prop('checked', bean.deactiveToTakeBackWork);
        if (bean.delegationTaskVisible) {
          $('#status_delegation_1').attr('checked', true);
        } else {
          $('#status_delegation_0').attr('checked', true);
        }
        $('#status').val(bean.status);
        $('#from_time').val(bean.formatedFromTime);
        $('#to_time').val(bean.formatedToTime);
        $('input[type=radio][name=status][value=' + bean.status + ']').attr('checked', true);

        // 编辑模式
        var ep_view_mode = Browser.getQueryString('ep_view_mode');
        if (ep_view_mode == '1') {
          $('button[id=btn_save]').removeClass('hidden');
          $('button[id=btn_edit]').addClass('hidden');
        } else {
          // 激活状态不可
          $('input, textarea, select', '#duty_agent_form').each(function (i) {
            $(this).prop('disabled', 'disabled');
          });
          $('button[id=btn_condition_add]').hide();
          $('button[id=btn_condition_del]').hide();
          // $("input[id=status_deactive]").prop("disabled", "");
          // $("input[id=status_consult]").prop("disabled", "");
          $('button[id=btn_save]').addClass('hidden');
          $('button[id=btn_edit]').removeClass('hidden');
        }
      }
    });
  } else {
    $('button[id=btn_save]').removeClass('hidden');
    $('button[id=btn_edit]').addClass('hidden');
  }

  // 委托人
  if (userDetails.admin === true) {
    $('#consignor_name').removeAttr('readonly');
    $('#consignor_name').on('click', function (e) {
      $.unit2.open({
        labelField: 'consignor_name',
        valueField: 'consignor',
        excludeValues: [userDetails.userId],
        title: '选择委托人',
        type: 'all',
        multiple: false,
        selectTypes: 'U',
        valueFormat: 'justId',
        callback: function (values, labels) {
          if (values && values.length > 0) {
            if ($('#trustee').val() === values[0]) {
              appModal.error('委托人不能与受托人一致！', function () {
                $('#consignor_name').val('');
                $('#consignor').val('');
              });
            }
          }
        }
      });
    });
  }
  // 受托人
  $('#trustee_name').on('click', function (e) {
    $.unit2.open({
      labelField: 'trustee_name',
      valueField: 'trustee',
      excludeValues: [userDetails.userId],
      title: '选择受托人',
      type: 'all',
      multiple: false,
      selectTypes: 'U',
      valueFormat: 'justId',
      callback: function (values, labels) {
        if (values && values.length > 0) {
          if ($('#consignor').val() === values[0]) {
            appModal.error('受托人不允许选择自己或不能与委托人一致！', function () {
              $('#trustee_name').val('');
              $('#trustee').val('');
            });
          }
        }
      }
    });
  });

  // 保存用户信息
  $('#btn_save').on('click', function () {
    bean.uuid = uuid;
    bean.consignor = $('#consignor').val();
    bean.trustee = $('#trustee').val();
    bean.contentName = $('#content_name').val();
    bean.content = $('#content').val();
    bean.includeCurrentWork = $('#includeCurrentWork').prop('checked');
    bean.dueToTakeBackWork = $('#dueToTakeBackWork').prop('checked');
    bean.deactiveToTakeBackWork = $('#deactiveToTakeBackWork').prop('checked');
    bean.delegationTaskVisible = $('input[type=radio][name=delegation_task_visible]:checked').val() === '1';
    bean.condition = $('#condition').val();
    bean.status = $('input[type=radio][name=status]:checked').val();
    var fromTime = $('#from_time').val();
    if (StringUtils.isNotBlank(fromTime)) {
      bean.fromTime = fromTime + ':00';
    }
    var toTime = $('#to_time').val();
    if (StringUtils.isNotBlank(toTime)) {
      bean.toTime = toTime + ':00';
    }
    bean.formatedFromTime = $('#from_time').val();
    bean.formatedToTime = $('#to_time').val();
    bean.consignorName = $('#consignor_name').val();
    bean.trusteeName = $('#trustee_name').val();
    if (StringUtils.isBlank(bean.trustee)) {
      appModal.error('受托人不能为空!');
      return;
    }
    if (StringUtils.isBlank(bean.status)) {
      appModal.error('状态不能为空!');
      return;
    }

    JDS.restfulPost({
      url: ctx + '/api/workflow/delegation/settiongs/save',
      data: bean,
      mask: true,
      success: function (result) {
        appModal.info(result.msg, function () {
          // appContext.getWindowManager().closeAndRefreshParent();
          appWindowManager(appContext).closeAndRefreshParent();
        });
      }
    });
  });

  // 编辑
  $('#btn_edit').on('click', function () {
    var url = window.location.href;
    url = UrlUtils.appendUrlParams(url, {
      ep_view_mode: '1'
    });
    window.location = url;
  });

  // 查看
  if (duty_agent_view === 'true') {
    $('div.widget-toolbar').html('');
    $('input, textarea, select', '#duty_agent_form').each(function (i) {
      $(this).prop('disabled', 'disabled');
    });
  }
  // 征求意见
  if (consult === 'true') {
    $('input, textarea, select', '#duty_agent_form').each(function (i) {
      $(this).prop('disabled', 'disabled');
    });
    $('div.widget-toolbar').html('');
    var consult = $('<button id="btn_consult_active" class="btn btn-primary">委托生效</button>');
    var refuse = $('<button id="btn_consult_refuse" class="btn btn-primary">拒绝委托</button>');
    $(consult).on('click', function () {
      JDS.restfulPost({
        url: ctx + '/api/workflow/delegation/settiongs/delegationActive',
        data: {
          uuid: uuid
        },
        contentType: 'application/x-www-form-urlencoded',
        success: function (result) {
          appModal.info('委托已生效!', function () {
            // appContext.getWindowManager().closeAndRefreshParent();
            appWindowManager(appContext).closeAndRefreshParent();
          });
        }
      });
    });
    $(refuse).on('click', function () {
      JDS.restfulPost({
        url: ctx + '/api/workflow/delegation/settiongs/delegationRefuse',
        data: {
          uuid: uuid
        },
        contentType: 'application/x-www-form-urlencoded',
        success: function (result) {
          appModal.info('委托已终止!', function () {
            // appContext.getWindowManager().closeAndRefreshParent();
            appWindowManager(appContext).closeAndRefreshParent();
          });
        }
      });
    });
    $('div.widget-toolbar').append(consult);
    $('div.widget-toolbar').append(refuse);
  }

  // 初始化系统集成导航下拉树
  var contentNameDisabled = $('#content_name').prop('disabled');
  if (contentNameDisabled == false) {
    $('#content_name').wCommonComboTree({
      service: 'workflowDelegationSettiongsService.getContentAsTreeAsync',
      serviceParams: ['-1'],
      serviceVersion: '',
      valueField: 'data',
      value: $('#content').val(),
      width: '690px',
      separator: ';',
      searchEnable: true,
      multiSelect: true, // 是否多选
      parentSelect: true, // 父节点选择有效，默认无效
      onAfterSetValue: function (comboTree, value) {
        $('#content').val(value.options.value);
      }
    });
  }
});
