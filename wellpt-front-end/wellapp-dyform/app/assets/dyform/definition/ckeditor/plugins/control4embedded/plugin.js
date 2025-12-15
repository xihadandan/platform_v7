var controlConfig = {};

$.extend(controlConfig, $.ControlConfigUtil);

$.extend(controlConfig, {
  initProperty: function (field) {
    if (field == null || typeof field == 'undefined') {
      field = new WTextInputClass();
      // 默认展示
      field.showType = dyshowType.showAsLabel;
      field.dbDataType = dyFormInputType._text;
      field.inputMode = dyFormInputMode.embedded;
      field.valueCreateMethod = dyFormInputValue.userImport;
    } else {
    }
    //控件属性初始化公共设置.
    this.ctlPropertyComInitSet(field);

    $('#unitUnique').val(field.unitUnique);
    if (field.unitUnique == 'true') {
      $('#checkRule_6').attr('checked', true);
      $('#checkRule_5').attr('checked', false);
    } else if (field.unitUnique == 'false') {
      $('#checkRule_5').attr('checked', true);
      $('#checkRule_6').attr('checked', false);
    }

    this.$("input[name='valueCreateMethod']").each(function () {
      if ($(this).val() == '1') {
        $(this).checked = true;
      } else {
        $(this).hide();
        $(this).next().hide();
      }
    });
  },

  collectFormAndFillCkeditor: function () {
    var field = new WTextInputClass();

    //控件公共属性收集
    var checkpass = this.collectFormCtlComProperty(field);
    if (!checkpass) {
      return false;
    }
    field.fieldCheckRules = [];
    field.unitUnique = $('#unitUnique').val();
    field.inputMode = dyFormInputMode.embedded;
    //创建控件占位符
    this.createControlPlaceHolder(this, this.editor.placeHolderImage, field);

    formDefinition.addField(field.name, field);

    return true;
  }
});

controlConfig.pluginName = CkPlugin.EMBEDDED;
addPlugin(controlConfig.pluginName, '嵌入页面控件', '嵌入页面控件属性设置', controlConfig);
