var controlConfig = {};

$.extend(controlConfig, $.ControlConfigUtil);

controlConfig.initProperty = function (field) {
  if (field == null || typeof field == 'undefined') {
    field = new WSerialNumberClass();
    field.dbDataType = dyFormInputType._text;
    field.valueCreateMethod = '1';
  } else {
  }

  //控件属性初始化公共设置.
  this.ctlPropertyComInitSet(field);

  //私有属性设置
  $('#isOverride')
    .find("option[value='" + field.isOverride + "']")
    .attr('selected', true);
  $('#isSaveDb')
    .find("option[value='" + field.isSaveDb + "']")
    .attr('selected', true);
  $('#isCreateWhenSave')
    .find("option[value='" + field.isCreateWhenSave + "']")
    .attr('selected', true);
  $("input[name='inputMode'][value='" + field.inputMode + "']").attr('checked', true);
  $('#defaultValue').val(field.defaultValue).closest('tr').hide(); // 隐藏默认值
  $('#onlyreadUrl').val(field.onlyreadUrl);
  $('#dialogTitle').val(field.dialogTitle);
  $('#serialNumberTypeLabel').val(field.serialNumberTypeLabel);
  $('#serialNumberSelLabel').val(field.serialNumberSelLabel);
  $('#serialNumberLabel').val(field.serialNumberLabel);
  $('#serialNumberFill').prop('checked', field.serialNumberFill);
  $('#automaticNumberSupplement').prop('checked', field.automaticNumberSupplement);
  $('#serialNumberTips').val(field.serialNumberTips);

  if (field.serialNumberFill) {
    $('#automaticNumberSupplement').parent().show();
  }
  $('#serialNumberFill').change(function () {
    if (this.checked) {
      $('#automaticNumberSupplement').parent().show();
    } else {
      $('#automaticNumberSupplement').prop('checked', false);
      $('#automaticNumberSupplement').parent().hide();
    }
  });
  //设置约束条件
  if (typeof field.fieldCheckRules != 'undefined' && field.fieldCheckRules != null) {
    var fieldCheckRules = field.fieldCheckRules;
    for (var i = 0; i < fieldCheckRules.length; i++) {
      var fieldCheckRule = fieldCheckRules[i];
      $("input[name='fieldCheckRules'][value='" + fieldCheckRule.value + "']").each(function () {
        this.checked = true;
      });
    }
  }

  initDesignatedSelect(field);

  $("input[name='inputMode']")
    .change(function () {
      var value = $("input[name='inputMode']:checked").val();
      if (value == dyFormInputMode.unEditSerialNumber) {
        $('#tr_isOverride').hide();
        $('#tr_designatedType').hide();
        $('#tr_isSaveDb').show();
        $('#tr_isCreateWhenSave').hide();
        /*$("input[name='showType'][value='1']").attr("disabled", "disabled");
            $("input[name='showType'][value='3']").attr("disabled", "disabled");
            $("input[name='showType'][value='4']").attr("disabled", "disabled");
            $("input[name='showType'][value='2']").prop("checked", "true");*/
        $('.editDialogInputLabel').hide();
      } else {
        $('#tr_isOverride').show();
        $('#tr_designatedType').show();
        $('#tr_isSaveDb').hide();
        $('#tr_isCreateWhenSave').show();
        /*$("input[name='showType'][value='1']").removeAttr("disabled");
            $("input[name='showType'][value='3']").removeAttr("disabled");
            $("input[name='showType'][value='4']").removeAttr("disabled");
            $("input[name='showType'][value='" + !!field.showType ? field.showType : 1 + "']").prop("checked", "true");*/
        $('.editDialogInputLabel').show();
      }
    })
    .trigger('change');

  if (field.inputMode == dyFormInputMode.unEditSerialNumber) {
    $('#tr_isOverride').hide();
    $('#tr_designatedType').hide();
    $('#tr_isSaveDb').show();
    $('#tr_isCreateWhenSave').hide();
    /*$("input[name='showType'][value='1']").attr("disabled", "disabled");
        $("input[name='showType'][value='3']").attr("disabled", "disabled");
        $("input[name='showType'][value='4']").attr("disabled", "disabled");*/
  } else {
    $('#tr_isOverride').show();
    $('#tr_designatedType').show();
    $('#tr_isSaveDb').hide();
    $('#tr_isCreateWhenSave').show();
    /*$("input[name='showType'][value='1']").removeAttr("disabled");
        $("input[name='showType'][value='3']").removeAttr("disabled");
        $("input[name='showType'][value='4']").removeAttr("disabled");*/
  }

  $('#unitUnique').val(field.unitUnique);
  if (field.unitUnique == 'true') {
    $('#checkRule_6').attr('checked', true);
    $('#checkRule_5').attr('checked', false);
  } else if (field.unitUnique == 'false') {
    $('#checkRule_5').attr('checked', true);
    $('#checkRule_6').attr('checked', false);
  }
};

controlConfig.collectFormAndFillCkeditor = function () {
  var field = new WSerialNumberClass();
  //added by linxr
  field.noNullValidateReminder = $('#noNullValidateReminder').val();
  field.uniqueValidateReminder = $('#uniqueValidateReminder').val();
  field.unitUnique = $('#unitUnique').val();

  //控件公共属性收集
  var checkpass = this.collectFormCtlComProperty(field);
  if (!checkpass) {
    return false;
  }

  field.inputMode = $("input[name='inputMode']:checked").val();

  field.designated = $('#designated').val();
  field.designatedId = $('#designatedId').val();
  if (field.inputMode == dyFormInputMode.serialNumber) {
    field.designatedType = $('#designatedType').val();
    field.designatedTypeId = $('#designatedTypeId').val();
    field.isOverride = $('#isOverride').val();
    field.isCreateWhenSave = $('#isCreateWhenSave').val();
  } else if (field.inputMode == dyFormInputMode.unEditSerialNumber) {
    field.isSaveDb = $('#isSaveDb').val();
  }

  if (field.showType == '' || field.showType == undefined) {
    alert('请选择编辑模式!');
    return false;
  }
  //创建控件占位符
  this.createControlPlaceHolder(this, this.editor.placeHolderImage, field);

  formDefinition.addField(field.name, field);

  field.dialogTitle = $('#dialogTitle').val();
  field.serialNumberTypeLabel = $('#serialNumberTypeLabel').val();
  field.serialNumberSelLabel = $('#serialNumberSelLabel').val();
  field.serialNumberLabel = $('#serialNumberLabel').val();
  field.serialNumberFill = $('#serialNumberFill').prop('checked');
  field.automaticNumberSupplement = $('#automaticNumberSupplement').prop('checked');
  field.serialNumberTips = $('#serialNumberTips').val();
  return true;
};

/**
 * 初始化流水号下拉选项
 */
function initDesignatedSelect(field) {
  $('#designated').val(field['designated']);
  $('#designatedId').val(field['designatedId']);
  $('#designatedType').val(field['designatedType']);
  $('#designatedTypeId').val(field['designatedTypeId']);
  //初始化流水号
  initDesignated();
  //初始化流水类型
  initDesignatedType();
}

// 获取树结点的绝对路径
function getAbsolutePath(treeNode) {
  var path = treeNode.name;
  var parentNode = treeNode.getParentNode();
  while (parentNode != null) {
    path = parentNode.name + '/' + path;
    parentNode = parentNode.getParentNode();
  }
  return path;
}

//初始化流水类型
function initDesignatedType() {
  function treeNodeOnCheck5(event, treeId, treeNode) {
    // 设置值
    var zTree = $.fn.zTree.getZTreeObj(treeId);
    var checkNodes = zTree.getCheckedNodes(true);
    var path = '';
    var value = '';
    for (var index = 0; index < checkNodes.length; index++) {
      path += getAbsolutePath(checkNodes[index]) + ';';
      value += checkNodes[index].id + ';';
    }

    $('#designatedType').val(path.length > 0 ? path.substr(0, path.length - 1) : '');
    $('#designatedTypeId').val(value.length > 0 ? value.substr(0, path.length - 1) : '');
  }

  var setting5 = {
    async: {
      otherParam: {
        serviceName: 'serialNumberService',
        methodName: 'listAllSerialTypeNodes',
        data: [],
        version: ''
      }
    },
    check: {
      enable: true,
      chkStyle: 'checkbox'
    },
    callback: {
      // onCheck: treeNodeOnCheck5,
    }
  };

  $('#designatedType').comboTree({
    autoInitValue: false,

    labelField: 'designatedType',
    valueField: 'designatedTypeId',
    treeSetting: setting5,
    autoCheckByValue: true
  });
}

//初始化流水号
function initDesignated() {
  var _this = this;

  function treeNodeOnCheck6(event, treeId, treeNode) {
    // 设置值
    var zTree = $.fn.zTree.getZTreeObj(treeId);
    var checkNodes = zTree.getCheckedNodes(true);
    var path = '',
      value = '';
    if (checkNodes.length > 0) {
      path = getAbsolutePath(checkNodes[0]);
      value = checkNodes[0].id;
    }
    $('#designated').val(path);
    $('#designatedId').val(value);
  }

  var setting6 = {
    async: {
      otherParam: {
        serviceName: 'serialNumberService',
        methodName: 'listSerialNumberNodes',
        data: [],
        version: ''
      }
    },
    check: {
      chkStyle: 'radio',
      radioType: 'all',
      enable: true
    },
    src: 'control',
    callback: {
      //onClick:treeNodeOnClick6,
      onCheck: treeNodeOnCheck6
    }
  };
  $('#designated').comboTree({
    autoInitValue: true,
    labelField: 'designated',
    valueField: 'designatedId',
    treeSetting: setting6
  });
}

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

controlConfig.pluginName = CkPlugin.SERIALNUMBERCTL;
addPlugin(controlConfig.pluginName, '流水号控件', '流水号控件属性设置', controlConfig);
