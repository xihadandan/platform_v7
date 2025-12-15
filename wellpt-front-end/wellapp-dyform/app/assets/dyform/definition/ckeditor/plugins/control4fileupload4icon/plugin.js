var controlConfig = {};
$.extend(controlConfig, $.ControlConfigUtil);

controlConfig.initProperty = function (field) {
  if (field == null || typeof field == 'undefined') {
    field = new WFileUpload4IconClass();
    field.inputMode = '6';
    field.valueCreateMethod = '1';
  } else {
  }
  //公共属性
  this.ctlPropertyComInitSet(field);
  //私有属性设置

  this.$('#fileMaxSize').val(field.fileMaxSize);
  this.$('#fileMaxNum')
    .val(field.fileMaxNum)
    .on('keyup', function () {
      var $this = $(this);
      var val = $this.val();
      var re = /^[0-9]\d*$/;
      if (val && !re.test(val)) {
        $this.val(val.substr(0, val.length - 1));
        appModal.error('允许上传附件数量为正整数');
      }
    });
  // add by wujx 20160606 begin -->
  var allowFileNameRepeat = field.allowFileNameRepeat;
  // add by wujx 20160606 end -->

  // add by wujx 20160606 begin -->
  if (allowFileNameRepeat == undefined || allowFileNameRepeat) {
    $("input[name='allowFileNameRepeat'][value='1']").attr('checked', true);
  } else {
    $("input[name='allowFileNameRepeat'][value='1']").attr('checked', false);
  }

  var isKeepOpLog = field.keepOpLog == keepOpLogType.yes;
  this.$('#keepOpLog').attr('checked', isKeepOpLog);

  if (!field.operateBtns) {
    this.$("input[name='oppower1']").each(function () {
      if ($(this).val() != '21') {
        $(this).attr('checked', true);
      }
    });
    this.$("input[name='oppower2']").each(function () {
      if ($(this).val() != '21') {
        $(this).attr('checked', true);
      }
    });
  } else {
    if (isKeepOpLog) {
      this.$("input[name='oppower2']").each(function () {
        if ($(this).val() != '21') {
          $(this).attr('checked', true);
        }
      });
    } else {
      this.$("input[name='oppower1']").each(function () {
        if ($(this).val() != '21') {
          $(this).attr('checked', true);
        }
      });
    }
  }
  // var createHistory = field.createHistory == 0 ? field.createHistory : 1;
  // this.$("#createHistory")[createHistory == "1" ? "addClass" : "removeClass"]("active").find(".switch-radio").data("status", field.createHistory);

  if (isKeepOpLog) {
    //正文控件，不限制大小
    this.hideFileMaxSizeInput();
    this.$('.tr_keepOplOg').hide();
    this.$('.tr_keepOplOg1').show();
    // this.$("input[name='oppower1']").val(field.operateBtns)
    $.each(field.operateBtns, function (index, item) {
      $("input[name='oppower1'][value='" + item + "']").attr('checked', true);
    });
    if (field.fileExt && field.fileExt != '') {
      var fileType = eval(field.fileExt);
      $.each(fileType, function (index, item) {
        $("input[name='fileExt1'][value='" + item + "']").attr('checked', true);
      });
    }
  } else {
    this.showFileMaxSizeInput();
    this.$('.tr_keepOplOg').show();
    this.$('.tr_keepOplOg1').hide();
    // this.$("input[name='oppower2']").val(field.operateBtns)
    $.each(field.operateBtns, function (index, item) {
      $("input[name='oppower2'][value='" + item + "']").attr('checked', true);
    });
    this.$('#fileExt').val(field.fileExt);
  }
  var _this = this;
  this.$('#keepOpLog')
    .off('click')
    .on('click', function () {
      if ($(this).attr('checked') == 'checked') {
        _this.hideFileMaxSizeInput();
        _this.$('.tr_keepOplOg').hide();
        _this.$('.tr_keepOplOg1').show();
      } else {
        _this.showFileMaxSizeInput();
        _this.$('.tr_keepOplOg').show();
        _this.$('.tr_keepOplOg1').hide();
      }
    });

  this.$('#readOnly').attr('checked', field.readOnly);
  if (field.defaultView) {
    $("input[name='defaultView'][value='" + field.defaultView + "']").attr('checked', true);
  } else {
    $("input[name='defaultView'][value='ui-image']").attr('checked', true);
  }

  // this.$("#createHistory").off("click").on("click", function() {
  //     if ($(this).hasClass("active")) {
  //         $(this).removeClass("active");
  //         $(this).find(".switch-radio").data("status", 0)
  //     } else {
  //         $(this).addClass("active");
  //         $(this).find(".switch-radio").data("status", 1)
  //     }
  // })

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
  this.$('.show-type-help-popup').hide();
  this.$("input[name='showType']").each(function () {
    if ($(this).val() != '5') {
      $(this).hide();
      $(this).next().hide();
    } else {
    }
  });
  var self = this;
  this.hasChacked = this.$("input[name='showType'][value='5']").prop('checked');
  this.$("input[name='showType'][value='5']").click(function () {
    self.hasChacked = !self.hasChacked;
    $(this).prop('checked', self.hasChacked);
  });

  var welloffice = false;
  if (window.SystemParams && SystemParams.getValue) {
    welloffice = SystemParams.getValue('app.fileupload.welloffice.enable');
  }
  if (welloffice == 'true') {
    this.$('.enable-way').text('DLL');
  } else {
    this.$('.enable-way').text('JSAPI');
  }

  $('.operateTips1').popover({
    html: true,
    placement: 'bottom',
    container: 'body',
    trigger: 'hover',
    template:
      '<div class="popover" role="tooltip" style="z-index: 99999;"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
    content: function () {
      return '显示类操作：不会影响数据存储的操作';
    }
  });
  $('.operateTips2').popover({
    html: true,
    placement: 'bottom',
    container: 'body',
    trigger: 'hover',
    template:
      '<div class="popover" role="tooltip" style="z-index: 99999;"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
    content: function () {
      return '编辑类操作：会影响数据存储的操作';
    }
  });

  field.downloadAllType = field.downloadAllType || 1;
  this.$("input[name='downloadAllType'][value='" + field.downloadAllType + "']").prop('checked', true);
};

controlConfig.showFileMaxSizeInput = function () {
  this.$('#fileMaxSize').parents('tr').show();
};

controlConfig.hideFileMaxSizeInput = function () {
  this.$('#fileMaxSize').val('');
  this.$('#fileMaxSize').parents('tr').first().hide();
};

controlConfig.collectFormAndFillCkeditor = function () {
  var fileMaxSize = this.$('#fileMaxSize').val();
  var keepOpLog = this.$('#keepOpLog').attr('checked');
  var readOnly = this.$('#readOnly').attr('checked');
  var isRequired = this.$('#isRequired').attr('checked');
  var field = new WFileUpload4IconClass();
  field.fieldCheckRules = [];
  //added by linxr
  field.noNullValidateReminder = $('#noNullValidateReminder').val();
  //控件公共属性收集
  var checkpass = this.collectFormCtlComProperty(field);
  if (!checkpass) {
    return false;
  }
  //特殊属性收集
  field.inputMode = dyFormInputMode.accessory1;
  // field.fieldCheckRules.push({value:field.contentFormat, label:$("#contentFormat").find("option:selected").text()});
  if (keepOpLog) {
    getOppowerVal('oppower1');
    var fileType = [];
    this.$("input[name='fileExt1']:checked").each(function (index, item) {
      if ($(item).val() != '') {
        fileType.push($(item).val());
      }
    });
    field.fileExt = fileType.length > 0 ? JSON.stringify(fileType) : '';
    if (field.fileExt == '') {
      appModal.error('允许上传的文件扩展名不能为空！');
      return false;
    }
  } else {
    getOppowerVal('oppower2');
    field.fileExt = this.$('#fileExt').val();
  }

  function getOppowerVal(name) {
    var btns = [];
    this.$("input[name='" + name + "']:checked").each(function () {
      if ($(this).val() == '1') {
        field.allowUpload = true;
      } else if ($(this).val() == '6') {
        field.allowDownload = true;
      } else if ($(this).val() == '14') {
        field.allowDelete = true;
      }
      btns.push($(this).val());
    });
    field.operateBtns = btns;

    this.$("input[name='" + name + "']:not(:checked)").each(function () {
      if ($(this).val() == '1') {
        field.allowUpload = false;
      } else if ($(this).val() == '6') {
        field.allowDownload = false;
      } else if ($(this).val() == '14') {
        field.allowDelete = false;
      }
    });
  }
  //add by wujx 20160606 begin -->
  $("input[name='allowFileNameRepeat']:checked").each(function () {
    if (this.checked) {
      if ($(this).val() == '1') {
        field.allowFileNameRepeat = true;
      }
    }
  });
  $("input[name='allowFileNameRepeat']:not(:checked)").each(function () {
    if ($(this).val() == '1') {
      field.allowFileNameRepeat = false;
    }
  });
  // add by wujx 20160606 end -->

  field.fileMaxSize = fileMaxSize;
  field.fileMaxNum = this.$('#fileMaxNum').val();
  field.keepOpLog = keepOpLog == 'checked' ? keepOpLogType.yes : keepOpLogType.no;
  field.readOnly = readOnly == 'checked' ? true : false;

  field.defaultView = this.$("input[name='defaultView']:checked").val();
  field.createHistory = 1;

  field.downloadAllType = this.$("input[name='downloadAllType']:checked").val();

  //创建控件占位符
  this.createControlPlaceHolder(this, this.editor.placeHolderImage, field);
  formDefinition.addField(field.name, field);

  return true;
};

controlConfig.pluginName = CkPlugin.FILEUPLOAD4ICONCTL;
addPlugin(controlConfig.pluginName, '图标式附件控件', '图标式附件控件属性设置', controlConfig);
