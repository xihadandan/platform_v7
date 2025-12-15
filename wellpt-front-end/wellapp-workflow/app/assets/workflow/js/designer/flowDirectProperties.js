goWorkFlow = parent.goWorkFlow;
var saveSetting = true;

function setDirectionProperty() {
  var loLine = goWorkFlow.currloLine;
  var laOptName = new Array();
  var loTask = null;
  if (loLine.fromTask.Type == 'CONDITION') {
    loTask = loLine.fromTask.inLines != null && loLine.fromTask.inLines.length > 0 ? loLine.fromTask.inLines[0].fromTask : null;
  } else {
    loTask = loLine.fromTask;
  }
  if (loTask != null) {
    var loNode = loTask.xmlObject.selectSingleNode('optNames');
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
  }
  // 如果是条件任务的前流向则直接返回
  if (loLine.xmlObject == undefined) {
    return;
  }

  var laArg = new Array();
  laArg.optname = laOptName.join(';');

  // 保存当前设置
  saveCurrentSetting();
  DirectionLoadEvent(loLine, loLine.xmlObject, laArg.optname);
  DirectionInitEvent(loLine, loLine.xmlObject, laArg.optname);

  if (goWorkFlow.readonlyMode) {
    setFrameReadOnly(window);

    // 专属设置
    $('.line-set').addClass('disable').off('click');
    $('.line-direction').addClass('disable').css('pointer-events', 'none').off('click');
    $('#DCondition').addClass('form-disabled');
  }

  top.appModal.hideMask();
}

function DirectionLoadEvent(loLine, directionProperty, optname) {
  forbidEditRefFlow();
  var goForm = $('#directionForm')[0];
  if (goForm == null) {
    return;
  }

  goForm.name.value = directionProperty.getAttribute('name') == null ? '' : directionProperty.getAttribute('name');

  $('#id', '#baseSetting').text(directionProperty.getAttribute('id') == null ? '' : directionProperty.getAttribute('id'));
  $('#name', goForm).on('input propertychange', function () {
    changeDesignerObjText();
  });
  $('#sortOrder', goForm).on('input propertychange', function () {
    var _reg = /^[0-9a-zA-Z]+$/;
    var _val = $.trim($(this).val());
    if (_reg.test(_val) || _val === '') {
      $(this).removeClass('error-style');
      $(this).next().remove();
      delete goWorkFlow.sortOrderError;
    } else {
      $(this).addClass('error-style');
      goWorkFlow.sortOrderError = true;
      if (!$(this).next().length) {
        $(this).after('<div style="margin-top: 10px;color: #e33033;font-size: 14px">只能输入英文字母或者数字！</div>');
      }
    }
  });
  var laField = [
    'isDefault',
    'isEveryCheck',
    'branchMode',
    'branchInstanceType',
    'branchCreateWay',
    'branchCreateInstanceFormId',
    'isMainFormBranchCreateWay',
    'branchTaskUsers',
    'branchCreateInstanceWay',
    'branchCreateInstanceBatch',
    'branchTimer',
    'branchInterface',
    'shareBranch',
    'isIndependentBranch',
    'isSendFile',
    'isSendMsg',
    'useAsButton',
    'buttonName',
    'buttonClassName',
    'btnStyle',
    'buttonOrder',
    'listenerName',
    'listener',
    'sortOrder',
    'remark',
    'showRemark'
  ];

  for (var i = 0; i < laField.length; i++) {
    var lsValue = directionProperty.selectSingleNode(laField[i]) != null ? directionProperty.selectSingleNode(laField[i]).text() : null;
    if (lsValue != null && lsValue != '') {
      bSetFormFieldValue(goForm, laField[i], lsValue);
    }
  }
  // 设置showRemark默认值为选中
  if (directionProperty.selectSingleNode('showRemark') == null) {
    bSetFormFieldValue(goForm, 'showRemark', '1');
  }

  var directType = goWorkFlow.currloLine.Type.toLowerCase();
  var lis = $('.line-set').find('li');
  $.each(lis, function (index, item) {
    var type = $(item).data('type');
    $(item).removeClass('active');
    if (directType.indexOf(type) > -1) {
      $(item).addClass('active');
    }
  });

  $('#branchCreateInstanceBatch').val(
    directionProperty.selectSingleNode('branchCreateInstanceBatch') != null
      ? directionProperty.selectSingleNode('branchCreateInstanceBatch').text()
      : '0'
  );
  $('#isEveryCheck').val(
    directionProperty.selectSingleNode('isEveryCheck') != null ? directionProperty.selectSingleNode('isEveryCheck').text() : '0'
  );
  changeCheckboxStatus(['branchCreateInstanceBatch', 'isEveryCheck']);

  var conditions = directionProperty.selectSingleNode('conditions') ? directionProperty.selectSingleNode('conditions').find('unit') : [];
  if (conditions.length > 0) {
    var html = '';
    $.each(conditions, function (index, item) {
      var objStr = $(item).find('value').text();
      if (objStr && objStr.split('%').length < 2) {
        //%太少,未编译过,需要编译
        objStr = encodeURIComponent(objStr);
      }
      html +=
        ' <li class="work-flow-other-item" data-obj="' +
        objStr +
        '">' +
        '<div>' +
        $(item).find('argValue').text() +
        '</div>' +
        "<button type='button' class='editCondition'><i class='iconfont icon-ptkj-shezhi' title='设置'></i></button>" +
        "<button type='button' class='clearCondition'><i class='iconfont icon-ptkj-shanchu' title='删除'></i></button>" +
        '</li>';
    });
    $('#DCondition .work-flow-other-list').append(html);
  }
  var archives = directionProperty.selectSingleNode('archives') ? directionProperty.selectSingleNode('archives').find('archive') : [];
  if (archives.length > 0) {
    var html = '';
    $.each(archives, function (index, item) {
      var archiveData = {};
      archiveData.archiveId = $(item).find('archiveId').text() || '';
      archiveData.archiveWay = $(item).find('archiveWay').text() || '1';
      archiveData.archiveWayName = $(item).find('archiveWayName').text() || '';
      archiveData.archiveStrategy = $(item).find('archiveStrategy').text() || '1';
      archiveData.botRuleId = $(item).find('botRuleId').text() || '';
      archiveData.botRuleName = $(item).find('botRuleName').text() || '';
      archiveData.destFolderUuid = $(item).find('destFolderUuid').text() || '';
      archiveData.destFolderName = $(item).find('destFolderName').text() || '';
      archiveData.subFolderRule = $(item).find('subFolderRule').text() || '';
      archiveData.fillDateTime = $(item).find('fillDateTime').text() || '';
      archiveData.archiveScriptType = $(item).find('archiveScriptType').text() || '';
      archiveData.archiveScript = $(item).find('archiveScript').text() || '';
      var archiveName = getArchiveDisplayName(archiveData);
      html +=
        " <li class='work-flow-other-item' data-obj='" +
        JSON.stringify(archiveData) +
        "'>" +
        '<div>' +
        archiveName +
        '</div>' +
        "<button type='button' class='archiveRuleEdit'><i class='iconfont icon-ptkj-shezhi' title='设置'></i></button>" +
        "<button type='button' class='archiveRuleDelete'><i class='iconfont icon-ptkj-shanchu' title='删除'></i></button>" +
        '</li>';
    });
    $('#archives .work-flow-other-list').append(html);
  }

  // 表单下拉数据
  var formUuid = bGetForm();
  var subForms = getSubForms(formUuid);

  var mainFormOption = "<optgroup label='主表'><option value='" + formUuid + "'>主表</option></optgroup>";
  $('#branchCreateInstanceFormId', goForm).append(mainFormOption);
  if (subForms && subForms.length > 0) {
    var subformOption = "<optgroup label='从表'>";
    $.each(subForms, function () {
      subformOption += "<option value='" + this.id + "'>" + this.name + '</option>';
    });
    subformOption += '</option>';
    $('#branchCreateInstanceFormId', goForm).append(subformOption);
  }

  $('#branchCreateInstanceFormId', goForm)
    .wellSelect()
    .on('change', function (e, type) {
      var selectFormId = $(this).val();

      var fields = getFormFields($('#branchCreateInstanceFormId').val());
      var fieldsData = [];
      $.each(fields, function (index, item) {
        fieldsData.push({
          id: item.id,
          text: item.name
        });
      });

      if (!(type && type === 'init')) {
        $('#branchTaskUsers').wellSelect('val', '');
      }
      $('#branchTaskUsers').wellSelect('destroy').wellSelect({
        data: fieldsData,
        valueField: 'branchTaskUsers'
      });

      var branchTaskUsersNode = $(directionProperty).selectSingleNode('branchTaskUsers');
      if (branchTaskUsersNode != null) {
        $('#branchTaskUsers', goForm).val(branchTaskUsersNode.text());
      }

      var isMainFormBranchCreateWay = formUuid == selectFormId ? '1' : '0';
      $('#isMainFormBranchCreateWay', goForm).val(isMainFormBranchCreateWay);

      // 显示是否可分阶段办理
      if (isMainFormBranchCreateWay != '1') {
        $('.branch-create-way-batch', goForm).show();
      } else {
        $('.branch-create-way-batch', goForm).hide();
      }
    })
    .trigger('change', 'init');

  // 分支计时实例
  var timers = getTimers();
  $.each(timers, function (i, timer) {
    var option = "<option value='" + timer.id + "'>" + timer.name + '</option>';
    $('#branchTimer').append(option);
  });

  if (directionProperty.getAttribute('type') == '2') {
    $('.showConditions').show();
  } else {
    $('.showConditions').hide();
  }

  // 归档设置
  loNode = directionProperty.selectSingleNode('archives');
  if (loNode != null && loNode.length != 0) {
    var laNode = loNode.selectNodes('archive');
    if (laNode != null && laNode.length != 0) {
      for (var i = 0; i < laNode.length; i++) {
        var archiveId = '';
        if ($(laNode[i]).selectSingleNode('archiveId') != null) {
          archiveId = $(laNode[i]).selectSingleNode('archiveId').text();
        }
        var archiveWay = $(laNode[i]).selectSingleNode('archiveWay').text();
        var archiveWayName = $(laNode[i]).selectSingleNode('archiveWayName').text();
        var archiveStrategy = '';
        if ($(laNode[i]).selectSingleNode('archiveStrategy') != null) {
          archiveStrategy = $(laNode[i]).selectSingleNode('archiveStrategy').text();
        }
        var botRuleId = $(laNode[i]).selectSingleNode('botRuleId').text();
        var botRuleName = $(laNode[i]).selectSingleNode('botRuleName').text();
        var destFolderUuid = $(laNode[i]).selectSingleNode('destFolderUuid').text();
        var destFolderName = $(laNode[i]).selectSingleNode('destFolderName').text();
        var subFolderRule = $(laNode[i]).selectSingleNode('subFolderRule').text();
        var fillDateTimeNode = $(laNode[i]).selectSingleNode('fillDateTime');
        var fillDateTime = null === fillDateTimeNode ? '' : fillDateTimeNode.text();
        var archiveScriptType = $(laNode[i]).selectSingleNode('archiveScriptType').text();
        var archiveScript = $(laNode[i]).selectSingleNode('archiveScript').text();
        var archive = {};
        archive.archiveId = archiveId;
        archive.archiveWay = archiveWay;
        archive.archiveWayName = archiveWayName;
        archive.archiveStrategy = archiveStrategy;
        archive.botRuleId = botRuleId;
        archive.botRuleName = botRuleName;
        archive.destFolderUuid = destFolderUuid;
        archive.destFolderName = destFolderName;
        archive.subFolderRule = subFolderRule;
        archive.fillDateTime = fillDateTime;
        archive.archiveScriptType = archiveScriptType;
        archive.archiveScript = archiveScript;
        var $option = $('<option>' + getArchiveDisplayName(archive) + '</option>');
        $option.data('value', archive);
        $(goForm.archives, goForm).append($option);
      }
    }
  }

  if ($('#useAsButton').val() == '0') {
    $('#useBtn').removeClass('active');
    $('.btn-container').hide();
  } else if ($('#useAsButton').val() == '1') {
    $('#useBtn').addClass('active');
    $('.btn-container').show();
  }
  $('#useBtn').find('.switch-radio').data('value', $('#useAsButton').val());

  var buttonClassName = $('#buttonClassName').val();
  if (buttonClassName != '') {
    if (isJSON(buttonClassName)) {
      var className = JSON.parse(buttonClassName);
      var html = '';
      if (className.btnColor) {
        var classType = className.btnInfo && className.btnInfo.class;
        html = '<a class="well-btn ' + className.btnColor + ' ' + classType + '">';
        if (className.iconInfo && className.iconInfo.filePaths) {
          html += '<i class="' + className.iconInfo.filePaths + '"></i>';
        }
        html += '按钮</a>';
      }
      $('#buttonClassName').parent().find('#btn_container_div').empty().html(html);
    } else {
      $('#buttonClassName')
        .parent()
        .find('#btn_container_div')
        .empty()
        .append("<a class='" + buttonClassName + "'>按钮</a>");
    }
  }

  $('#eventScripts').on('click', 'li .btn-set', function () {
    var data = $(this).closest('li').data('script');
    var index = $(this).closest('li').index();
    openEventScript(goForm, directionProperty, data, 'direction', index);
  });
  $('#eventScripts').on('click', 'li .btn-remove', function () {
    $(this).closest('li').remove();
  });

  $('#addScripts').on('click', function () {
    openEventScript(goForm, directionProperty, '', 'direction');
  });
  setScripts(goForm, directionProperty);
}

function setScripts(goForm, directionProperty) {
  //事件脚本
  var eventScripts = directionProperty.selectSingleNode('eventScript');
  if (eventScripts) {
    var text = $.trim(eventScripts.text());
    if (text) {
      var data = {
        type: eventScripts.attr('type'),
        contentType: eventScripts.attr('contentType'),
        text: text
      };
      addEventScript(goForm, directionProperty, data);
    }
  }
}

function DirectionInitEvent(loLine, directionProperty, optname) {
  var goForm = $('#directionForm')[0];
  if (goForm == null) {
    return;
  }

  $('.line-set')
    .off()
    .on('click', 'li', function () {
      $(this).addClass('active').siblings().removeClass('active');
      if ($(this).index() == 0) {
        $(this)
          .css({
            borderRight: '1px solid #488cee'
          })
          .next()
          .css({
            borderLeft: 'none'
          });
      } else {
        $(this)
          .css({
            borderLeft: '1px solid #488cee'
          })
          .prev()
          .css({
            borderRight: 'none'
          });
      }
      var type = $(this).data('type');
      var designer = $('#newWorkFlowDesigner', top.document)[0].contentWindow;
      designer.bSetDirection(type);
    });

  $('.line-direction')
    .off()
    .on('click', function () {
      // if($(this).hasClass("active")){
      //     $(this).removeClass("active");
      // }else{
      //     $(this).addClass("active");
      // }
      var type = $(this).data('type');
      var designer = $('#newWorkFlowDesigner', top.document)[0].contentWindow;
      designer.bSetDirection(type);
    });

  $("input[name='isDefault']")
    .off()
    .on('change', function () {
      var value = $("input[name='isDefault']:checked").val();
      if (value == '0') {
        $('.condition-branch').show();
      } else {
        $('.condition-branch').hide();
      }
    });

  // 分支模式切换
  $("input[name='branchMode']")
    .off()
    .on('change', function () {
      var value = $("input[name='branchMode']:checked").val();
      if (value == '2') {
        $('.poly-container').show();
      } else {
        $('.poly-container').hide();
      }
    });

  var isDefault = directionProperty.selectSingleNode('isDefault') ? directionProperty.selectSingleNode('isDefault').text() : '0';
  $("input[name='isDefault'][value='" + isDefault + "']")
    .attr('checked', true)
    .trigger('change');

  var branchMode = directionProperty.selectSingleNode('branchMode') ? directionProperty.selectSingleNode('branchMode').text() : '1';
  $("input[name='branchMode'][value='" + branchMode + "']")
    .attr('checked', true)
    .trigger('change');

  //添加判断条件
  $('#addConditions')
    .off()
    .on('click', function () {
      showConditionDialog({
        optionData: optname,
        data: null,
        index: null,
        types: ['1', '2', '3', '4'],
        listContainer: $('#DCondition .work-flow-other-list')
      });
    });

  // 编辑分支条件
  $('#DCondition')
    .off()
    .on('click', 'button', function (e) {
      if ($(this).hasClass('editCondition')) {
        var index = $(this).parent().index();
        var data = {};
        data.value = $(this).parent().data('obj') ? decodeURIComponent($(this).parent().data('obj')) : null;
        data.text = $(this).parent().find('div').text();
        showConditionDialog({
          optionData: optname,
          data: data,
          index: index,
          types: ['1', '2', '3', '4'],
          listContainer: $('#DCondition .work-flow-other-list')
        });
      } else {
        $(this).parent().remove();
      }
    });

  // 流向化为提交按钮使用
  switchFun('useBtn', '#advancedSetting', function (bol) {
    if (bol == '1') {
      $('.btn-container').show();
    } else {
      $('.btn-container').hide();
    }
    $('#useAsButton').val(bol);
  });

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
        methodName: 'getDirectionListeners'
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

  var getBranchDispatcherCustomInterfaces = function () {
    var nodes = [];
    $.get({
      url: ctx + '/api/workflow/definition/getCustomDispatcherBranchTaskInterfaces',
      async: false,
      success: function (result) {
        nodes = result.data;
      }
    });
    return nodes;
  };

  $('#branchCreateWay')
    .wellSelect({
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
      var branchCreateWay = $(this).val();
      if (branchCreateWay == 12) {
        $('.from-form', '#branchFlow').show();
        $('.form-interface', '#branchFlow').hide();
      } else if (branchCreateWay == 3) {
        $('.from-form', '#branchFlow').hide();
        $('.form-interface', '#branchFlow').show();
        var interfaces = getBranchDispatcherCustomInterfaces();
        var interfaceData = [];
        if (interfaces.length > 0) {
          $.each(interfaces, function (index, item) {
            interfaceData.push({
              id: item.id,
              text: item.name
            });
          });
        }
        $('#branchInterface').wellSelect({
          data: interfaceData
        });
        var branchInterfaceNode = $(directionProperty).selectSingleNode('branchInterface');
        if (branchInterfaceNode != null) {
          $('#branchInterface', goForm).val(branchInterfaceNode.text());
        }
      }
    })
    .trigger('change');

  $('#branchCreateInstanceWay').wellSelect({
    data: [
      {
        id: '1',
        text: '生成单一实例'
      },
      {
        id: '2',
        text: '按办理人生成实例'
      }
    ],
    searchable: false
  });
  // 添加归档设置
  $('#addDocumentSet')
    .off()
    .on('click', function () {
      showDocumentDialog();
    });

  // 编辑归档规则
  $('#archives .work-flow-other-list')
    .off()
    .on('click', 'button', function (e) {
      if ($(this).hasClass('archiveRuleEdit')) {
        var data = $(this).parent().data('obj');
        var index = $(this).parent().index();
        showDocumentDialog(data, index);
      } else if ($(this).hasClass('archiveRuleDelete')) {
        $(this).parent().remove();
      }
    });

  $('#setBtnClass')
    .off()
    .on('click', function () {
      var _buttonClassName = $('#buttonClassName').val();
      top.$.WCommonBtnLib.show({
        value: _buttonClassName && isJSON(_buttonClassName) ? JSON.parse(_buttonClassName) : {},
        confirm: function (data) {
          var btnHtml = '';
          var buttonStyle = '';
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
              buttonStyle = 'well-btn ' + data.btnColor + ' ' + data.btnInfo['class'] + ' ' + data.btnSize + ' ' + data.iconInfo.fileIDs;
            } else {
              btnHtml += '<a class="well-btn ' + data.btnColor + ' ' + data.btnInfo['class'] + ' ' + data.btnSize + '">按钮</a>';
              buttonStyle = 'well-btn ' + data.btnColor + ' ' + data.btnInfo['class'] + ' ' + data.btnSize;
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
              buttonStyle = 'well-btn ' + data.btnInfo['class'] + ' ' + data.btnSize + ' ' + data.btnInfo.icon;
            } else {
              btnHtml += '<a class="well-btn ' + data.btnInfo['class'] + ' ' + data.btnSize + '">' + data.btnInfo.text + '</a>';
              buttonStyle = 'well-btn ' + data.btnInfo['class'] + ' ' + data.btnSize;
            }
          }
          $('#buttonClassName', '#advancedSetting').parent().find('#btn_container_div').empty().append(btnHtml);

          $('#buttonClassName', '#advancedSetting').val(JSON.stringify(data));
          $('#btnStyle', '#advancedSetting').val(buttonStyle);
        }
      });
    });
}

function onGetFormFields(select, form, data) {
  /*modified by huanglinchuan 2014.10.23 begin*/
  $(select, form).html('');
  /*modified by huanglinchuan 2014.10.23 end*/
  $.each(data, function () {
    var option = '<option value="' + this.data + '">' + this.name + '</option>';
    /*modified by huanglinchuan 2014.10.23 begin*/
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
    /*modified by huanglinchuan 2014.10.23 end*/
  });
}

function getTimers() {
  var loNodes = goWorkFlow.flowXML.selectNodes('./timers/timer');
  var timers = [];
  if (loNodes != null) {
    for (var i = 0; i < loNodes.length; i++) {
      var loNode = loNodes[i];
      var timer = {};
      timer.name = $(loNode).selectSingleNode('name') != null ? $(loNode).selectSingleNode('name').text() : '';
      timer.id = $(loNode).selectSingleNode('timerId') != null ? $(loNode).selectSingleNode('timerId').text() : '';
      timers.push(timer);
    }
  }
  return timers;
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

function bGetForm() {
  var loNode = goWorkFlow.flowXML.selectSingleNode('./property/formID');
  var lsFormName = loNode != null ? loNode.text() : '';
  return lsFormName;
}

function getSubForms(formUuid) {
  var fields = [];
  $.get({
    url: ctx + '/api/workflow/definition/getSubForms',
    data: {
      formUuid: formUuid
    },
    async: false,
    success: function (result) {
      fields = result.data;
    }
  });
  return fields;
}
// 获取主表字段函数
function getFormFields(formUuid) {
  var fields = [];
  $.get({
    url: ctx + '/api/workflow/definition/getFormFields',
    data: {
      formUuid: formUuid
    },
    async: false,
    success: function (result) {
      fields = result.data;
    }
  });
  return fields;
}

function changeCheckboxStatus(arr) {
  for (var i = 0; i < arr.length; i++) {
    var field = arr[i];
    $('#' + field).prop('checked', $('#' + field).val() == '1' ? true : false);
  }
}

// 归档设置弹窗
function showDocumentDialog(psValue, index) {
  var html = initDocumentDialog();
  var $documentDialog = top.appModal.dialog({
    title: '归档规则',
    message: html,
    size: 'large',
    height: '600',
    shown: function () {
      if (psValue) {
        var goForm = $('#archiveRuleForm', $documentDialog)[0];
        // 归档脚本类型
        var archiveId = psValue.archiveId || '';
        var archiveWay = psValue.archiveWay || '1';
        var archiveStrategy = psValue.archiveStrategy || '1';
        var botRuleName = psValue.botRuleName || '';
        var botRuleId = psValue.botRuleId || '';
        var destFolderName = psValue.destFolderName || '';
        var destFolderUuid = psValue.destFolderUuid || '';
        var subFolderRule = psValue.subFolderRule || '';
        var fillDateTime = psValue.fillDateTime || '';
        var archiveScriptType = psValue.archiveScriptType || '';
        var archiveScript = psValue.archiveScript || '';

        $('#archiveId', $documentDialog).val(archiveId);
        $('#archiveWay', $documentDialog).val(archiveWay).trigger('change');
        $("input[name='archiveStrategy'][value='" + archiveStrategy + "']", $documentDialog)
          .attr('checked', true)
          .trigger('change');
        $('#botRuleName', $documentDialog).val(botRuleName);
        $('#botRuleId', $documentDialog).val(botRuleId).trigger('change');
        $('#destFolderName', $documentDialog).val(destFolderName);
        $('#destFolderUuid', $documentDialog).val(destFolderUuid).trigger('change');
        $('#archiveScriptType', $documentDialog).val(archiveScriptType).trigger('change');
        $('#archiveScript', $documentDialog).val(archiveScript);
        $('#subFolderRule', $documentDialog).val(subFolderRule);
        $('#subFolderRule-sw', $documentDialog).prop('checked', $.trim(subFolderRule).length > 0);
        $('#fillDateTime', $documentDialog).prop('checked', fillDateTime == 'true');
      } else {
        $('#archiveWay', $documentDialog).val('1').trigger('change');
      }

      $('#archiveWay', $documentDialog)
        .wellSelect({
          data: [
            {
              id: '1',
              text: '流程数据归档'
            },
            {
              id: '2',
              text: '表单数据归档'
            },
            {
              id: '3',
              text: '表单数据转换后归档'
            },
            {
              id: '4',
              text: '脚本归档'
            },
            {
              id: '5',
              text: '弹窗由用户确认表单数据归档范围'
            }
          ],
          valueField: 'archiveWay',
          searchable: false
        })
        .change(function () {
          var value = $(this).val();
          $('.archive-config', $documentDialog).hide();
          $('.archive-config-' + value, $documentDialog).show();
          $('#subFolderRule-sw', $documentDialog).trigger('change');
        })
        .trigger('change');
      var $subFolderRule = $('#subFolderRule', $documentDialog);
      $('#subFolderRule-sw', $documentDialog)
        .change(function (event) {
          $subFolderRule.closest('.form-group')[this.checked ? 'show' : 'hide']();
        })
        .trigger('change');
      // 转换规则
      $('#botRuleName', $documentDialog)
        .wSelect2({
          serviceName: 'botRuleConfFacadeService',
          queryMethod: 'loadSelectData',
          labelField: 'botRuleName',
          valueField: 'botRuleId',
          placeholder: '请选择',
          multiple: false,
          remoteSearch: false,
          width: '100%',
          height: 250
        })
        .trigger('change');
      var setting = {
        view: {
          showIcon: true,
          showLine: false
        },
        check: {
          enable: true,
          chkStyle: 'checkbox',
          radioType: 'all'
        },
        callback: {},
        async: {
          otherParam: {
            serviceName: 'dmsFileManagerService',
            methodName: 'getFolderTreeAsync'
          }
        }
      };
      $('#destFolderName', $documentDialog).comboTree({
        labelField: 'destFolderName',
        valueField: 'destFolderUuid',
        treeSetting: setting,
        autoInitValue: false,
        showSelect: false,
        showCheckAll: false,
        readonly: false,
        selectParent: true,
        autoCheckByValue: true,
        includeParentValue: true,
        mutiSelect: true
      });

      var scriptTypes = SelectiveDatas.getItems('BASIC_DATA_SCRIPT_TYPE');
      var scriptData = [];
      $.each(scriptTypes, function (j, scriptType) {
        scriptData.push({
          id: scriptType.value,
          text: scriptType.label
        });
      });
      $('#archiveScriptType', $documentDialog)
        .wellSelect({
          data: scriptData,
          searchable: false
        })
        .trigger('change');
    },
    buttons: {
      ok: {
        label: '保存',
        className: 'well-btn w-btn-primary',
        callback: function () {
          if (!sArchiveRuleCheck($documentDialog)) {
            return false;
          }
          var returnValue = sArchiveRuleBuildValue($documentDialog);
          var name = getArchiveDisplayName(returnValue);
          if (psValue) {
            $('#archives .work-flow-other-list')
              .find('li:eq(' + index + ')')
              .data('obj', returnValue)
              .find('div')
              .text(name);
          } else {
            var html =
              "<li class='work-flow-other-item' data-obj='" +
              JSON.stringify(returnValue) +
              "'>" +
              '<div>' +
              name +
              '</div>' +
              "<button type='button' class='archiveRuleEdit'><i class='iconfont icon-ptkj-shezhi' title='设置'></i></button>" +
              "<button type='button' class='archiveRuleDelete'><i class='iconfont icon-ptkj-shanchu' title='删除'></i></button>" +
              '</li>';
            $('#archives .work-flow-other-list').append(html);
          }
        }
      },
      cancel: {
        label: '取消',
        className: 'btn-default',
        callback: function () {}
      }
    }
  });
}

function getArchiveDisplayName(archiveRule) {
  var displayName = archiveRule.archiveWayName;
  var archiveWay = archiveRule.archiveWay;
  // 转换规则
  if (archiveWay == '3' || archiveWay == '5') {
    if (StringUtils.isNotBlank(archiveRule.botRuleName)) {
      displayName += ' 转换规则(' + archiveRule.botRuleName + ')';
    }
  }
  // 文件夹
  if (archiveWay == '1' || archiveWay == '2' || archiveWay == '3' || archiveWay == '5') {
    if (StringUtils.isNotBlank(archiveRule.destFolderName)) {
      displayName += ' 文件夹(' + archiveRule.destFolderName + ')';
    }
  }
  return displayName;
}

function sArchiveRuleCheck($documentDialog) {
  var goForm = $('#archiveRuleForm', $documentDialog)[0];
  var archiveWay = goForm.archiveWay.value;
  var destFolderUuid = goForm.destFolderUuid.value;
  // 文件夹
  if (archiveWay == '1' || archiveWay == '2') {
    if (StringUtils.isBlank(destFolderUuid)) {
      top.appModal.error('文件夹不能为空！');
      return false;
    }
  }
  // 表单数据转换后归档
  var botRuleId = goForm.botRuleId.value;
  if (archiveWay == '3') {
    if (StringUtils.isBlank(botRuleId)) {
      top.appModal.error('单据转换规则不能为空！');
      return false;
    }
  }
  if (goForm['subFolderRule-sw'].checked) {
    if (StringUtils.isBlank(goForm.subFolderRule.value)) {
      top.appModal.error('子夹生成规则不能为空！');
      return false;
    }
  }
  return true;
}

function sArchiveRuleBuildValue($documentDialog) {
  var goForm = $('#archiveRuleForm', $documentDialog)[0];
  var archiveRule = {};
  archiveRule.archiveId = goForm.archiveId.value;
  if (StringUtils.isBlank(archiveRule.archiveId)) {
    var date = new Date();
    archiveRule.archiveId = date.getTime();
  }
  archiveRule.archiveWay = goForm.archiveWay.value;
  archiveRule.archiveWayName = $('#archiveWay', $documentDialog).wellSelect('data').text;
  archiveRule.archiveStrategy = goForm.archiveStrategy.value;
  archiveRule.botRuleName = goForm.botRuleName.value;
  archiveRule.botRuleId = goForm.botRuleId.value;
  archiveRule.destFolderName = goForm.destFolderName.value;
  archiveRule.destFolderUuid = goForm.destFolderUuid.value;
  archiveRule.subFolderRule = goForm['subFolderRule-sw'].checked ? goForm.subFolderRule.value : '';
  archiveRule.fillDateTime = $(goForm.fillDateTime).prop('checked') ? 'true' : 'false';
  archiveRule.archiveScriptType = goForm.archiveScriptType.value;
  archiveRule.archiveScript = goForm.archiveScript.value;
  return archiveRule;
}

function initDocumentDialog() {
  var html = '';
  html +=
    '<form id="archiveRuleForm" class="workflow-popup form-widget">' +
    '<div class="well-form form-horizontal">' +
    '<div class="form-group">' +
    '<label for="archiveWay" class="well-form-label control-label">归档方式</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="archiveWay" name="archiveWay">' +
    '<input type="hidden" id="archiveId" name="archiveId">' +
    '</div>' +
    '</div>' +
    '<div class="form-group archive-config archive-config-3">' +
    '<label class="well-form-label control-label">归档策略</label>' +
    '<div class="well-form-control">' +
    '<input type="radio" id="archiveStrategy_1" name="archiveStrategy" value="1" checked><label for="archiveStrategy_1">新增</label>' +
    '<input type="radio" id="archiveStrategy_2" name="archiveStrategy" value="2"><label for="archiveStrategy_2">替换</label>' +
    '<input type="radio" id="archiveStrategy_3" name="archiveStrategy" value="3"><label for="archiveStrategy_3">忽略</label>' +
    '</div>' +
    '</div>' +
    '<div class="form-group archive-config archive-config-3 archive-config-5">' +
    '<label for="botRuleName" class="well-form-label control-label">转换规则</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="botRuleName" name="botRuleName" placeholder="请选择">' +
    '<input type="hidden" id="botRuleId" name="botRuleId">' +
    '</div>' +
    '</div>' +
    '<div class="form-group archive-config archive-config-3 archive-config-5">' +
    '<label for="" class="well-form-label control-label"></label>' +
    '<div class="well-form-control">注：没有配置归档夹时，数据直接归档到单据转换的目标单据</div>' +
    '</div>' +
    '<div class="form-group archive-config archive-config-1 archive-config-2 archive-config-3 archive-config-5">' +
    '<label for="fieldName" class="well-form-label control-label">文件夹</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="destFolderName" name="destFolderName" placeholder="请选择">' +
    '<input type="hidden" id="destFolderUuid" name="destFolderUuid">' +
    '</div>' +
    '</div>' +
    '<div class="form-group archive-config archive-config-1 archive-config-2 archive-config-3">' +
    '<label for="subFolderRule-sw" class="well-form-label control-label">生成子夹</label>' +
    '<div class="well-form-control">' +
    '<input class="form-control switch" type="checkbox" id="subFolderRule-sw" name="subFolderRule-sw"/><label for="subFolderRule-sw" style="padding:0;"></label>' +
    '</div>' +
    '</div>' +
    '<div class="form-group archive-config archive-config-1 archive-config-2 archive-config-3" style="display:none;">' +
    '<label for="operator" class="well-form-label control-label">子夹生成规则</label>' +
    '<div class="well-form-control">' +
    '<textarea class="form-control" id="subFolderRule" name="subFolderRule"></textarea>' +
    '</div>' +
    '</div>' +
    '<div class="form-group archive-config archive-config-1 archive-config-2 archive-config-3">' +
    '<label class="well-form-label control-label no-suffix"></label>' +
    '<div class="well-form-control">' +
    '<div><input id="fillDateTime" name="fillDateTime" type="checkbox" /><label for="fillDateTime">时间变量补位</label><span style="position: relative;left: 20px;">子夹名称中月、日、时、分、秒的时间变量，选中即补位，如1月显示为01月</span></div>' +
    '1、绑定流程内置变量：${流程名称},${流程ID}<br>' +
    '2、绑定当前用户数据变量：${当前用户名},${当前用户登录名},${当前用户ID},${当前用户主部门名称},${当前用户主部门ID}<br>' +
    '3、绑定表单的所有字段编码：${dyform.字段编码 }<br>' +
    '4、绑定通用的内置变量：${大写年},${大写月},${大写日},${简年},${年},${月},${日},${时},${分},${秒}<br>' +
    '例如按年/月份的格式：${年} 年/${月} 月</div>' +
    '</div>' +
    '<div class="form-group archive-config archive-config-4">' +
    '<label class="well-form-label control-label">归档脚本类型</label>' +
    '<div class="well-form-control">' +
    '<input type="text" class="form-control" id="archiveScriptType" name="archiveScriptType">' +
    '</div>' +
    '</div>' +
    '<div class="form-group archive-config archive-config-4">' +
    '<label class="well-form-label control-label">归档脚本</label>' +
    '<div class="well-form-control">' +
    '<textarea type="text" class="form-control" id="archiveScript" name="archiveScript"></textarea>' +
    '</div>' +
    '</div>' +
    '<div class="form-group archive-config archive-config-4">' +
    '<label class="well-form-label control-label no-suffix"></label>' +
    '<div class="well-form-control">' +
    'groovy流程数据归档示例：<br>' +
    'def workflowArchiveService= applicationContext.getBean("workflowArchiveService");<br>' +
    'def folderUuid = "2a3b8709-5532-4800-b4f4-16d042b75e11";<br>' +
    'workflowArchiveService.archive(folderUuid, event);<br>' +
    '支持的脚本变量 <br>' +
    'applicationContext：spring应用上下文\tcurrentUser：当前用户信息<br>' +
    'event：流程相关事件信息\tflowInstUuid：流程实例UUID<br>' +
    'taskInstUuid：环节实例UUID\ttaskData：环节数据<br>' +
    'formUuid：表单定义UUID\tdataUuid：表单数据UUID<br>' +
    'dyFormData：表单数据\tdyFormFacade：表单接口<br>' +
    'actionType：操作类型，如Submit、Rollback等\topinionText：办理意见<br>' +
    'resultMessage：事件脚本执行结果，调用resultMessage.isSuccess()方法返回true，通过resultMessage.setSuccess(true/false)设置脚本执行是否成功</div>' +
    '</div>' +
    '</div>' +
    '</form>';
  return html;
}

function resetLine(obj) {
  top.$('#' + obj.name).show();
  top
    .$('#' + obj.labelObject.name + 'div')
    .show()
    .css('visibility', 'visible');
  $.each(goWorkFlow.tasks, function (i, item) {
    if (item.name === obj.fromTask.name) {
      item.outLines.push(obj);
      return true;
    } else if (item.name === obj.toTask.name) {
      item.inLines.push(obj);
      return true;
    }
  });
}

function changeDesignerObjText() {
  var goForm = $('#directionForm')[0];
  var flowXML = goWorkFlow.currloLine;
  var directProperty = flowXML.xmlObject;
  directProperty.setAttribute('name', goForm.name.value);
  top.changeObjText(flowXML.labelObject);
}

function checkPropertiesData() {
  if (!$('#name', '#baseSetting').val()) {
    top.appModal.error('请输入流向名称！');
    return false;
  }
  return true;
}

function collectPropertiesData(check) {
  var directProperty1 = goWorkFlow.currloLine.xmlObject;
  var goForm = $('#directionForm')[0];
  if (check) {
    var checkStatus = checkPropertiesData();
    if (!checkStatus) {
      return false;
    }
  }
  collectDirectPropertiesData(directProperty1, goForm);
  top.changeObjText(goWorkFlow.currloLine.labelObject);
  return true;
}

function collectDirectPropertiesData(directProperty, goForm) {
  // 基本设置
  $(directProperty).attr('name', $('#name').val());
  $(directProperty).attr('id', $('#flow_id').val());
  $(directProperty).attr('type', $(directProperty).attr('type'));
  $(directProperty).attr('fromID', $('#fromID').val());
  oSetElement(directProperty, 'sortOrder', $.trim($('input[name="sortOrder"]').val()));
  oSetElement(directProperty, 'remark', $.trim($('textarea[name="remark"]', goForm).val()));
  oSetElement(directProperty, 'showRemark', $('input[name="showRemark"]', goForm).attr('checked') ? '1' : '');
  if ($(directProperty).attr('type') == 2) {
    oSetElement(directProperty, 'isDefault', $('input[name="isDefault"]:checked').val());
    if ($('input[name="isDefault"]:checked').val() == '0') {
      var conditions = directProperty.selectSingleNode('conditions');
      if (conditions) {
        directProperty.removeChild(conditions);
      }

      conditions = oSetElement(directProperty, 'conditions');
      $('#DCondition  li').each(function () {
        var $this = $(this);
        var conditionData = $this.data('obj');
        var conditionName = $this.find('div:eq(0)').text();
        var conditionUnit = oAddElement(conditions, 'unit');
        oSetElement(conditionUnit, 'value', conditionData);
        oSetElement(conditionUnit, 'argValue', conditionName);
        conditionUnit.attr('type', 16);
      });
    }
    oSetElement(directProperty, 'isEveryCheck', $('input[name="isEveryCheck"]').attr('checked') ? '1' : '');
  }

  // 分支流
  oSetElement(directProperty, 'branchMode', $('input[name="branchMode"]:checked').val());
  oSetElement(directProperty, 'shareBranch', $('input[name="shareBranch"]').attr('checked') ? '1' : '');
  oSetElement(directProperty, 'isIndependentBranch', $('input[name="isIndependentBranch"]').attr('checked') ? '1' : '');

  if ($('input[name="branchMode"]:checked').val() == '2') {
    oSetElement(directProperty, 'branchInstanceType', $('input[name="branchInstanceType"]:checked').val());
    oSetElement(directProperty, 'branchCreateWay', $('input[name="branchCreateWay"]').val());

    if ($('input[name="branchCreateWay"]').val() == '12') {
      oSetElement(directProperty, 'branchCreateInstanceFormId', $('input[name="branchCreateInstanceFormId"]').val());
      oSetElement(directProperty, 'isMainFormBranchCreateWay', $('input[name="isMainFormBranchCreateWay"]').val());
      oSetElement(directProperty, 'branchTaskUsers', $('input[name="branchTaskUsers"]').val());
      oSetElement(directProperty, 'branchCreateInstanceWay', $('input[name="branchCreateInstanceWay"]').val());
      if ($("input[name='isMainFormBranchCreateWay']").val() == '0') {
        oSetElement(directProperty, 'branchCreateInstanceBatch', $('input[name="branchCreateInstanceBatch"]').attr('checked') ? '1' : '');
      }
    } else if ($('input[name="branchCreateWay"]').val() == '3') {
      oSetElement(directProperty, 'branchInterface', $('input[name="branchInterface"]').val());
    }
  }
  // 归档设置
  var archives = directProperty.selectSingleNode('archives');
  if (archives) {
    directProperty.removeChild(archives);
  }

  archives = oSetElement(directProperty, 'archives');
  $('#archives  li.work-flow-other-item').each(function () {
    var $this = $(this);
    var scriptData = $this.data('obj');
    var archive = oAddElement(archives, 'archive');
    oSetElement(archive, 'archiveId', scriptData.archiveId);
    oSetElement(archive, 'archiveWay', scriptData.archiveWay);
    oSetElement(archive, 'archiveWayName', scriptData.archiveWayName);
    oSetElement(archive, 'archiveStrategy', scriptData.archiveStrategy);
    oSetElement(archive, 'botRuleId', scriptData.botRuleId);
    oSetElement(archive, 'botRuleName', scriptData.botRuleName);
    oSetElement(archive, 'destFolderUuid', scriptData.destFolderUuid);
    oSetElement(archive, 'destFolderName', scriptData.destFolderName);
    oSetElement(archive, 'subFolderRule', scriptData.subFolderRule);
    oSetElement(archive, 'fillDateTime', scriptData.fillDateTime);
    oSetElement(archive, 'archiveScriptType', scriptData.archiveScriptType);
    oSetElement(archive, 'archiveScript', scriptData.archiveScript);
  });

  // 高级设置
  oSetElement(directProperty, 'useAsButton', $('input[name="useAsButton"]').val());
  if ($('#useAsButton').val() == '1') {
    oSetElement(directProperty, 'buttonName', $('input[name="buttonName"]').val());
    oSetElement(directProperty, 'buttonOrder', $('input[name="buttonOrder"]').val());
    oSetElement(directProperty, 'buttonClassName', $('input[name="buttonClassName"]').val());
    oSetElement(directProperty, 'btnStyle', $('input[name="btnStyle"]').val());
  }
  oSetElement(directProperty, 'listenerName', $('input[name="listenerName"]').val());
  oSetElement(directProperty, 'listener', $('input[name="listener"]').val());

  var eventScript = directProperty.selectSingleNode('eventScript');
  if (eventScript) {
    directProperty.removeChild(eventScript);
  }
  if ($('#eventScripts').find('li').length > 0) {
    var scriptData = $('#eventScripts').find('li:eq(0)').data('script');
    eventScript = oSetElement(directProperty, 'eventScript', scriptData.text);
    eventScript.attr('type', scriptData.type);
    eventScript.attr('contentType', scriptData.contentType);
  }
}
