var controlConfig = {};

$.extend(controlConfig, $.ControlConfigUtil);

controlConfig.initProperty = function (field) {
  //初始化数据字典树.

  $('#progressColor').minicolors({
    control: 'hue',
    format: 'hex',
    letterCase: 'lowercase',
    opacity: false,
    position: 'top left',
    theme: 'bootstrap',
    change: null
  });

  if (field == null || typeof field == undefined) {
    field = new WProgressClass();
    field.inputMode = dyFormInputMode.progress;
    field.valueCreateMethod = '1';
  }
  if (field.editProgress === '0') {
    field.showType = 2;
  } else if (field.editProgress === '1') {
    field.showType = 1;
  }
  delete field.editProgress;
  //控件属性初始化公共设置.
  this.ctlPropertyComInitSet(field);

  $('#defaultProgress').val(field.defaultProgress);
  $('#progressMin').val(field.progressMin);
  $('#progressMax').val(field.progressMax);
  $('#progressColor').val(field.progressColor);
  $('#progressUnit').val(field.progressUnit);
  /*
  if (field.editProgress == '0') {
    $("input[name='editProgress'][value='" + 0 + "']").attr('checked', true);
  } else {
    $("input[name='editProgress'][value='" + 1 + "']").attr('checked', true);
  }
	*/
  $('#progressHeight').val(field.progressHeight);
  //私有属性
  if (field.progressColor != '') {
    $('#progressColor').minicolors('value', field.progressColor); //设置值触发change事件
  }
};

controlConfig.exitDialog = function () {
  this.editor.focusedDom = null;
};

controlConfig.collectFormAndFillCkeditor = function () {
  var field = new WProgressClass();
  field.inputMode = dyFormInputMode.progress;
  field.fieldCheckRules = [];

  field.defaultProgress = $('#defaultProgress').val();
  field.progressMin = $('#progressMin').val();
  field.progressMax = $('#progressMax').val();
  field.progressColor = $('#progressColor').val();
  field.progressUnit = $('#progressUnit').val();
  // field.editProgress = $("input[name='editProgress']:checked").val();
  field.progressHeight = $('#progressHeight').val();
  field.noNullValidateReminder = $('#noNullValidateReminder').val();

  if ($('#progressMin').val() == '' || $('#progressMin').val() == undefined) {
    alert('[最小值]不能为空！');
    return false;
  }
  if ($('#progressMax').val() == '' || $('#progressMax').val() == undefined) {
    alert('[最大值]不能为空！');
    return false;
  }
  if (parseInt($('#progressMax').val()) - parseInt($('#progressMin').val()) <= 0) {
    alert('最大值不能小于等于最小值');
    return false;
  }
  if (
    parseInt($('#defaultProgress').val()) - parseInt($('#progressMin').val()) < 0 ||
    parseInt($('#defaultProgress').val()) - parseInt($('#progressMax').val()) > 0
  ) {
    alert('默认进度要在最小值和最大值之间');
    return false;
  }
  //end
  //控件公共属性收集
  var checkpass = this.collectFormCtlComProperty(field);

  if (!checkpass) {
    return false;
  }

  //创建控件占位符
  this.createControlPlaceHolder(this, this.editor.placeHolderImage, field);
  formDefinition.addField(field.name, field);
  return true;
};

controlConfig.getInputEvents = function () {
  return [
    {
      id: 'change',
      chkDisabled: true,
      name: 'change'
    }
  ];
};

if ($.fn.placeholder.input && $.fn.placeholder.textarea) {
} else {
  $('input, textarea').placeholder();
}

controlConfig.pluginName = CkPlugin.PROGRESS;
addPlugin(controlConfig.pluginName, '进度条控件', '进度条控件属性设置', controlConfig);
