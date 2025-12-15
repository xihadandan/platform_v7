var controlConfig = {};

$.extend(controlConfig, $.ControlConfigUtil);

$.extend(controlConfig, {
  initProperty: function (field) {
    var _this = this;
    if (field == null || typeof field == 'undefined') {
      field = new WTextInputClass();
      // 默认展示
      field.showType = dyshowType.showAsLabel;
      field.dbDataType = dyFormInputType._text;
      field.valueCreateMethod = dyFormInputValue.userImport;
    } else {
    }

    //控件属性初始化公共设置.
    if (formDefinition.placeholderCtr && formDefinition.placeholderCtr[field.name]) {
      field.displayName = formDefinition.placeholderCtr[field.name].displayName;
      field.showType = formDefinition.placeholderCtr[field.name].showType;
    }
    this.ctlPropertyComInitSet(field);
    this.$('.show-type-help-popup').hide();
    this.$('input[name=readStyle]').closest('tr').hide();
    this.$("input[name='showType']").each(function () {
      if ($(this).val() != '5') {
        $(this).hide();
        $(this).next().hide();
      }
    });

    var hasChecked = this.$("input[name='showType'][value='5']").prop('checked');
    this.$("input[name='showType'][value='5']").click(function () {
      hasChecked = !hasChecked;
      if (hasChecked) {
        $(this).prop('checked', 'checked');
      } else {
        $(this).removeProp('checked');
        _this.$("input[name='showType'][value='2']").prop('checked', 'checked');
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
    field.inputMode = dyFormInputMode.placeholder;
    //创建控件占位符
    this.createControlPlaceHolder(this, this.editor.placeHolderImage, field);

    // formDefinition.addField(field.name, field);

    if (typeof formDefinition.placeholderCtr === 'undefined') {
      formDefinition.placeholderCtr = {};
    }

    formDefinition.placeholderCtr[field.name] = field;

    return true;
  }
});

controlConfig.pluginName = CkPlugin.PLACEHOLDER;
addPlugin(controlConfig.pluginName, '真实值占位符控件', '真实值占位符控件属性设置', controlConfig);
