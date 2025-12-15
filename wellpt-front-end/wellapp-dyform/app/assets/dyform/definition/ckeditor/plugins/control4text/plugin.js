var controlConfig = {};

$.extend(controlConfig, $.ControlConfigUtil);

var $ace;
controlConfig.initProperty = function (field) {
  if (field == null || typeof field == 'undefined') {
    field = new WTextInputClass();
    field.dbDataType = dyFormInputType._text;
    field.valueCreateMethod = dyFormInputValue.userImport;
  } else {
  }
  //added by linxr,事件管理框
  $ace = $.fn.aceBinder({
    id: 'validateEventManage',
    container: '#validateEventManageContainer',
    iframeId: 'validateEventManageIframe',
    value: field.validateEventManage,
    varSnippets: 'dyform.controlValidateEvent',
    codeHis: {
      enable: true,
      relaBusizUuid: $('#tableId').val(), //表单ID
      codeType:
        'dyform.' +
        $('#name').val() + //字段编码
        'validateRuleEvent'
    }
  });
  //end
  // $(".hongdy").hide();
  //选择字段值的具体时的响应动作
  $('.isChose').click(function (event) {
    $('#defaultValue').val($('#defaultValue').val() + '{' + $(this).html() + '}');
    $('input[name=fieldCheckRules][value=1]').trigger('change');
    $('.hongdy').hide();
  });
  $(document).mousedown(function (event) {
    var temp = $(event.target).attr('class');
    if (temp != 'noChose' && temp != 'isChose' && temp != 'hongdy') {
      $('.hongdy').hide();
    }
  });
  $('#defaultValue').addClass('input-tier');
  //公共属性
  this.ctlPropertyComInitSet(field);
  //私有属性设置
  //changed by linxr
  if (field.contentFormat && field.contentFormat != '0') {
    $('#contentFormat').val(field.contentFormat);
  }
  //默认值转换
  $('#defaultValue').val(this.reaplaceInitDefaultValue(field.defaultValue));
  $('input[name=fieldCheckRules][value=1]').trigger('change');
  //表单字段值获得焦点时下拉宏一定选项
  $('#defaultValue')
    .focus(function () {
      $('.hongdy').show();
    })
    .blur(function (e) {});
  $("input[name='valueCreateMethod'][value='" + field.valueCreateMethod + "']").each(function () {
    this.checked = true;
  });
  $("input[name='valueCreateMethod'][value='" + field.valueCreateMethod + "']").each(function () {
    this.checked = true;
  });

  //added by linxr
  function showOrHideRegularTextRow() {
    if ($('#contentFormat').val() == '16') {
      $('#checkRuleLabel').attr('rowspan', '3');
      $('#customizeRegularTextRow').show();
    } else {
      $('#checkRuleLabel').attr('rowspan', '2');
      $('#customizeRegularTextRow').hide();
    }
  }

  showOrHideRegularTextRow();
  $('#contentFormat').change(function (event) {
    showOrHideRegularTextRow();
  });
  //ended
  $('input[name=valueCreateMethod][value=3]:checked').trigger('change');
  if (field.toLinkFieldData == 1) {
    $("input[name='toLinkFieldData']").each(function () {
      this.checked = true;
    });
  }

  if (field.checkType === '2') {
    $('input[name=checkType][value=2]').prop('checked', 'checked');
  } else {
    $('input[name=checkType][value=1]').prop('checked', 'checked');
  }

  $('input[name="isPasswdInput"]').change(function () {
    var $this = $(this);
    if ($this.val() === 'true') {
      $('.not-passwd').hide();
      $('.is-passwd').show();
    } else {
      $('.is-passwd').hide();
      $('.addon').show();
      $('input[name="addon"]').each(function () {
        var $this = $(this);
        var $ele = $('.' + $this.val());
        var _status = $this.attr('checked');
        if (_status) {
          $ele.show();
          $('.realDisplay').show();
        } else {
          $ele.hide();
        }
      });
    }
  });

  $.each(field.addonValue, function (i, v) {
    $('#' + v).attr('checked', true);
    if (field.isPasswdInput !== 'true') {
      $('.' + v).show();
    }
  });

  if ($.inArray('addonFront', field.addonValue) > -1 || $.inArray('addonEnd', field.addonValue) > -1) {
    $('.realDisplay').show();
  }

  $('input[name="addonFrontValue"]').val(field.addonFrontValue);
  $('input[name="addonEndValue"]').val(field.addonEndValue);

  if (field.addonIcon) {
    if (field.addonIcon === 'front') {
      $('#addonFrontIcon').attr('checked', true);
    } else {
      $('#addonEndIcon').attr('checked', true);
    }
  }

  $('input[name="addon"]').change(function () {
    var $this = $(this);
    var $ele = $('.' + $this.val());
    var $realDisplay = $('.realDisplay');
    var _status = $this.attr('checked');

    if (_status) {
      $ele.show();
    } else {
      $ele.hide();
    }
    $realDisplay.hide();
    $('.addonFE').each(function () {
      if ($(this).attr('checked')) {
        $realDisplay.show();
      }
    });
  });

  if (field.chooseAddonIcon) {
    $('.chooseAddonIcon').addClass(field.chooseAddonIcon);
    $('input[name="chooseAddonIcon"]').val(field.chooseAddonIcon);
  }

  $('#unitUnique').val(field.unitUnique);
  if (field.unitUnique == 'true') {
    $('#checkRule_6').attr('checked', true);
    $('#checkRule_5').attr('checked', false);
  } else if (field.unitUnique == 'false') {
    $('#checkRule_5').attr('checked', true);
    $('#checkRule_6').attr('checked', false);
  }

  $('.chooseIcon').on('click', function () {
    var $this = $(this);
    $.WCommonPictureLib.show({
      selectTypes: [3],
      confirm: function (data) {
        var fileIDs = data.fileIDs;
        $this.next().removeClass().addClass('chooseAddonIcon').addClass(fileIDs);
        $this.siblings('input[name="chooseAddonIcon"]').val(fileIDs);
      }
    });
  });

  if (field.isPasswdInput) {
    if (field.isPasswdInput === 'true') {
      $('#isPasswdInputTrue').attr('checked', true);
      $('.not-passwd').hide();
      $('.is-passwd').show();
      if (field.showPasswordEye) {
        $('#showPasswordEye').attr('checked', true);
      }
    } else {
      $('#isPasswdInputFalse').attr('checked', true);
      $('.addon').show();
      $('.is-passwd').hide();
    }
  }
};

controlConfig.exitDialog = function () {
  this.editor.focusedDom = null;
};

controlConfig.collectFormAndFillCkeditor = function () {
  var field = new WTextInputClass();
  field.fieldCheckRules = [];

  //控件公共属性收集
  var checkpass = this.collectFormCtlComProperty(field);
  if (!checkpass) {
    return false;
  }
  //默认值转换
  field.defaultValue = this.reaplaceDefaultValue($('#defaultValue').val());
  //特殊属性收集
  //field.formula = this.$("#formula").val();
  field.contentFormat = $('#contentFormat').val();
  field.valueCreateField = $('#valueCreateField').val();
  field.toLinkFieldData = $("input[name='toLinkFieldData']:checked").val() || '0';
  field.inputMode = dyFormInputMode.text;
  field.fieldCheckRules.push({
    value: field.contentFormat,
    label: $('#contentFormat').find('option:selected').text()
  });

  field.unitUnique = $('#unitUnique').val();

  //added by linxr
  field.customizedRegularText = $('#customizeRegularText').val();
  field.validateEventManage = $ace.getValue();
  field.noNullValidateReminder = $('#noNullValidateReminder').val();
  field.uniqueValidateReminder = $('#uniqueValidateReminder').val();
  field.regularValidateReminder = $('#regularValidateReminder').val();
  field.eventValidateReminder = $('#eventValidateReminder').val();
  //end
  formDefinition.addField(field.name, field);

  field.checkType = $('input[name="checkType"]:checked').val();

  //是否为密码输入框
  field.isPasswdInput = $('input[name="isPasswdInput"]:checked').val();
  field.showPasswordEye = $('input[name="showPasswordEye"]:checked').val();

  //单行文本额外元素数据
  var addonValue = [];
  $('input[name="addon"]:checked').each(function () {
    addonValue.push($(this).val());
  });
  field.addonValue = addonValue;
  field.addonFrontValue = $('input[name="addonFrontValue"]').val();
  field.addonEndValue = $('input[name="addonEndValue"]').val();
  field.addonIcon = $('input[name="addonIcon"]:checked').val();
  field.chooseAddonIcon = $('input[name="chooseAddonIcon"]').val();
  //创建控件占位符
  this.createControlPlaceHolder(this, this.editor.placeHolderImage, field);

  formDefinition.addField(field.name, field);
  return true;
};

/* 这两个通用方法，移到jquery.wFieldDefinitionUtils.js 文件中去，保证其他控件 如果需要用到的话，
 * 可以直接用
controlConfig.reaplaceInitDefaultValue=function(initdefaultvalue){
	var tempStr=initdefaultvalue;
	tempStr = tempStr.replace(dySysVariable.currentYearMonthDate,"{当前日期(2000-01-01)}" );
	tempStr = tempStr.replace(dySysVariable.currentYearMonthDateCn,"{当前日期(2000年1月1日)}" );
	tempStr = tempStr.replace(dySysVariable.currentYearCn,"{当前日期(2000年)}");
	tempStr = tempStr.replace(dySysVariable.currentYearMonthCn,"{当前日期(2000年1月)}" );
	tempStr = tempStr.replace(dySysVariable.currentMonthDateCn,"{当前日期(1月1日)}" );
	tempStr = tempStr.replace(dySysVariable.currentWeekCn,"{当前日期(星期一)}" );
	tempStr = tempStr.replace(dySysVariable.currentYear,"{当前年份(2000)}" );
	tempStr = tempStr.replace(dySysVariable.currentTimeMin,"{当前时间(12:00)}" );
	tempStr = tempStr.replace(dySysVariable.currentTimeSec,"{当前时间(12:00:00)}" );
	tempStr = tempStr.replace(dySysVariable.currentDateTimeMin,"{当前日期时间(2000-01-01 12:00)}");
	tempStr = tempStr.replace(dySysVariable.currentDateTimeSec,"{当前日期时间(2000-01-01 12:00:00)}");
	tempStr = tempStr.replace(dySysVariable.currentUserId,"{当前用户ID}" );
	tempStr = tempStr.replace(dySysVariable.currentUserName,"{当前用户姓名}" );
	tempStr = tempStr.replace(dySysVariable.currentUserDepartmentPath,"{当前用户部门(长名称)}");
	tempStr = tempStr.replace(dySysVariable.currentUserDepartmentName,"{当前用户部门(短名称)}");
	tempStr = tempStr.replace(dySysVariable.currentUserMainJobName,"{当前用户主职位}");
	tempStr = tempStr.replace(dySysVariable.currentUserUnit,"{当前用户单位}");
	tempStr = tempStr.replace(dySysVariable.currentCreatorId,"{创建人ID}" );
	tempStr = tempStr.replace(dySysVariable.currentCreatorName,"{创建人姓名}" );
	tempStr = tempStr.replace(dySysVariable.currentCreatorDepartmentId,"{创建人部门ID}");
	tempStr = tempStr.replace(dySysVariable.currentCreatorDepartmentPath,"{创建人部门(长名称)}");
	tempStr = tempStr.replace(dySysVariable.currentCreatorDepartmentName,"{创建人部门(短名称)}");
	tempStr = tempStr.replace(dySysVariable.currentCreatorMainJobName,"{创建人主职位}");
	return tempStr;
}

controlConfig.reaplaceDefaultValue=function(defaultvalue){
	var tempStr=defaultvalue;
	tempStr = tempStr.replace("{当前日期(2000-01-01)}", dySysVariable.currentYearMonthDate);
	tempStr = tempStr.replace("{当前日期(2000年1月1日)}", dySysVariable.currentYearMonthDateCn);
	tempStr = tempStr.replace("{当前日期(2000年)}", dySysVariable.currentYearCn);
	tempStr = tempStr.replace("{当前日期(2000年1月)}", dySysVariable.currentYearMonthCn);
	tempStr = tempStr.replace("{当前日期(1月1日)}", dySysVariable.currentMonthDateCn);
	tempStr = tempStr.replace("{当前日期(星期一)}", dySysVariable.currentWeekCn);
	tempStr = tempStr.replace("{当前年份(2000)}", dySysVariable.currentYear);
	tempStr = tempStr.replace("{当前时间(12:00)}", dySysVariable.currentTimeMin);
	tempStr = tempStr.replace("{当前时间(12:00:00)}", dySysVariable.currentTimeSec);
	tempStr = tempStr.replace("{当前日期时间(2000-01-01 12:00)}",dySysVariable.currentDateTimeMin);
	tempStr = tempStr.replace("{当前日期时间(2000-01-01 12:00:00)}", dySysVariable.currentDateTimeSec);
	tempStr = tempStr.replace("{当前用户ID}", dySysVariable.currentUserId);
	tempStr = tempStr.replace("{当前用户姓名}", dySysVariable.currentUserName);
	tempStr = tempStr.replace("{当前用户部门(长名称)}",dySysVariable.currentUserDepartmentPath);
	tempStr = tempStr.replace("{当前用户部门(短名称)}",dySysVariable.currentUserDepartmentName);
	tempStr = tempStr.replace("{当前用户主职位}", dySysVariable.currentUserMainJobName);
	tempStr = tempStr.replace("{当前用户单位}",dySysVariable.currentUserUnit);
	tempStr = tempStr.replace("{创建人ID}", dySysVariable.currentCreatorId);
	tempStr = tempStr.replace("{创建人姓名}", dySysVariable.currentCreatorName);
	tempStr = tempStr.replace("{创建人部门ID}",dySysVariable.currentCreatorDepartmentId);
	tempStr = tempStr.replace("{创建人部门(长名称)}",dySysVariable.currentCreatorDepartmentPath);
	tempStr = tempStr.replace("{创建人部门(短名称)}",dySysVariable.currentCreatorDepartmentName);
	tempStr = tempStr.replace("{创建人主职位}", dySysVariable.currentCreatorMainJobName);
	return tempStr;
}
*/

controlConfig.getInputEvents = function () {
  return [
    {
      id: 'focus',
      chkDisabled: true,
      name: 'focus'
    },
    {
      id: 'blur',
      chkDisabled: true,
      name: 'blur'
    },
    {
      id: 'change',
      chkDisabled: true,
      name: 'change'
    },
    {
      id: 'keypress',
      chkDisabled: true,
      name: 'keypress'
    },
    {
      id: 'keyup',
      chkDisabled: true,
      name: 'keyup'
    },
    {
      id: 'keydown',
      chkDisabled: true,
      name: 'keydown'
    }
  ];
};

controlConfig.pluginName = CkPlugin.TEXTCTL;
addPlugin(controlConfig.pluginName, '单行文本控件', '单行文本控件属性设置', controlConfig);
