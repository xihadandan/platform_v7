var controlConfig = {};

$.extend(controlConfig, $.ControlConfigUtil);

controlConfig.setDbDataType = function (dbDataType) {
  var _this = this;

  if (this.$('#dbDataType').size() == 0) {
    return;
  }
  if (this.$('#dbDataType').find('option').length > 0) {
    this.$('#dbDataType').val(dbDataType);
    return;
  }
  var dyFormInputTypeObj = {
    _text: { code: dyFormInputType._text, name: '文本' },
    _clob: { code: dyFormInputType._clob, name: '大字段' }
  };
  for (var type in dyFormInputTypeObj) {
    var obj = dyFormInputTypeObj[type];
    this.$('#dbDataType').append("<option value='" + obj.code + "'>" + obj.name + '</option>');
  }
  this.$('#dbDataType').change(function () {
    if ($(this).val() == dyFormInputType._clob) {
      //大字段,不需要设置长度
      _this.$('#length').parents('tr').first().hide();
    } else {
      _this.$('#length').parents('tr').first().show();
    }
  });
  if (typeof dbDataType == 'undefined') {
    return;
  }
  this.$('#dbDataType').val(dbDataType);
  this.$('#dbDataType').trigger('change');
};

controlConfig.initProperty = function (field) {
  if (field == null || typeof field == 'undefined') {
    field = new WTextAreaClass();
    field.dbDataType = dyFormInputType._text;
    field.valueCreateMethod = '1';
  } else {
    if (field.isHideNumTip === 'true') {
      $('#isHideNumTip').attr('checked', true);
    } else {
      $('#isHideNumTip').attr('checked', false);
    }
  }
  //控件属性初始化公共设置.
  this.ctlPropertyComInitSet(field);
  $('input[name=htmlCodec]')
    .filter('[value=' + field.htmlCodec + ']')
    .prop('checked', true);
  $('#unitUnique').val(field.unitUnique);
  if (field.unitUnique == 'true') {
    $('#checkRule_6').attr('checked', true);
    $('#checkRule_5').attr('checked', false);
  } else if (field.unitUnique == 'false') {
    $('#checkRule_5').attr('checked', true);
    $('#checkRule_6').attr('checked', false);
  }

  if ('allowResize' in field && !field.allowResize) {
    $('#allowResize').attr('checked', false);
  } else {
    $('#allowResize').attr('checked', true);
  }
};

controlConfig.collectFormAndFillCkeditor = function () {
  var field = new WTextAreaClass();
  field.noNullValidateReminder = $('#noNullValidateReminder').val();
  field.uniqueValidateReminder = $('#uniqueValidateReminder').val();

  field.unitUnique = $('#unitUnique').val();
  field.fieldCheckRules = [];
  //控件公共属性收集
  var checkpass = this.collectFormCtlComProperty(field);
  if (!checkpass) {
    return false;
  }

  if ($('#isHideNumTip').attr('checked')) {
    field.isHideNumTip = $('#isHideNumTip').val();
  } else {
    delete field.isHideNumTip;
  }

  if ($('#allowResize').attr('checked')) {
    field.allowResize = true;
  } else {
    field.allowResize = false;
  }

  field.htmlCodec = $('input[name=htmlCodec]:checked').val();
  field.inputMode = dyFormInputMode.textArea;
  //创建控件占位符
  this.createControlPlaceHolder(this, this.editor.placeHolderImage, field);

  formDefinition.addField(field.name, field);

  return true;
};

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

controlConfig.pluginName = CkPlugin.TEXTAREACTL;
addPlugin(controlConfig.pluginName, '多行文本控件', '多行文本控件属性设置', controlConfig);
