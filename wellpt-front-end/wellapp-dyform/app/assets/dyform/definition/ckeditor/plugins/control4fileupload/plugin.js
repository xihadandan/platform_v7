// function checkUpload(_this){
// 	var $deleteBtn = $(_this).parent().find('input[code="delete_btn"]');
// 	if(_this.checked){
// 		$deleteBtn.each(function(){
// 			  this.checked=true;
// 		  });
// 	}else{
// 		$deleteBtn.each(function(){
// 			  this.checked=false;
// 		  });
// 	}
// }

var controlConfig = {};
$.extend(controlConfig, $.ControlConfigUtil);

controlConfig.initProperty = function (field) {
  var _self = this;

  if (field == null || typeof field == 'undefined') {
    field = new WFileUploadClass();
    field.inputMode = '4';
    field.valueCreateMethod = '1';
  } else {
  }
  //公共属性
  this.ctlPropertyComInitSet(field);
  //私有属性设置

  var allowUpload = field.allowUpload;
  var allowDownload = field.allowDownload;
  var allowDelete = field.allowDelete;
  var mutiselect = field.mutiselect;
  // add by wujx 20160606 begin -->
  var allowFileNameRepeat = field.allowFileNameRepeat;
  //add by wujx 20160606 end -->
  var isShowFileSourceIcon = field.isShowFileSourceIcon;
  var isShowFileFormatIcon = field.isShowFileFormatIcon;
  var secDevBtnIdStr = field.secDevBtnIdStr ? field.secDevBtnIdStr : '';
  var fileSourceIdStr = field.fileSourceIdStr ? field.fileSourceIdStr : '';

  JDS.call({
    service: 'dyformFileListButtonConfigService.getAllBean',
    data: [],
    version: '',
    success: function (result) {
      //addElement
      var secDevBtnIds = secDevBtnIdStr.split(';');

      $(result.data).each(function () {
        var _this = this;
        var $secDevBtnDiv = $(
          '<span style="display:inline-block;"><input type="checkbox" name="sec_dev_btn" id="sec_dev_btn_' +
            this.uuid +
            '" code="' +
            this.code +
            '" value="' +
            this.uuid +
            '"/><label for="sec_dev_btn_' +
            this.uuid +
            '">' +
            this.buttonName +
            '</label></span>'
        );
        //提示框
        if (_this.fileExtensions) {
          var $secDevBtnDivTip = $('<span class="tip"><i class="iconfont icon-ptkj-tishishuoming"></i></span>');
          $secDevBtnDivTip.popover({
            html: true,
            placement: 'bottom',
            container: 'body',
            trigger: 'hover',
            template:
              '<div class="popover" role="tooltip" style="z-index: 99999;"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
            content: function () {
              return '<span>支持的文件扩展名：' + _this.fileExtensions.split(';').join('、') + '</span><br>';
            }
          });
          $secDevBtnDivTip.appendTo($secDevBtnDiv.find('label'));
        }

        if (_this.btnShowType == 'edit') {
          if (_this.btnType == '1') {
            $secDevBtnDiv.appendTo(_self.$('.btn_edit_inner'));
          } else {
            $secDevBtnDiv.appendTo(_self.$('.btn_edit_outer'));
          }
        } else {
          if (_this.btnType == '1') {
            $secDevBtnDiv.appendTo(_self.$('.btn_show_inner'));
          } else {
            $secDevBtnDiv.appendTo(_self.$('.btn_show_outer'));
          }
        }

        var $secDevBtnInput = $secDevBtnDiv.find('input[name="sec_dev_btn"]');
        if (
          secDevBtnIds.indexOf(this.uuid) > -1 || //
          (field.secDevBtnIdStr === null && this.defaultFlag === 1) // 二开按钮配置默认选中
        ) {
          $secDevBtnInput.attr('checked', true);
        }

        // if ($secDevBtnInput.attr('code') == 'upload_btn') {
        //   $secDevBtnInput.attr('checked', true);
        //   if (allowUpload) {
        //     $secDevBtnInput.attr('checked', true);
        //   }
        //   $secDevBtnDiv.unbind('click', $secDevBtnInput).bind('click', $secDevBtnInput, function () {
        //     var _this = this;
        //     var $deleteBtn = $(_this).parent().find('input[code="delete_btn"]');
        //     if ($(_this).find('input').prop('checked')) {
        //       $deleteBtn.prop('checked', true);
        //     } else {
        //       $deleteBtn.prop('checked', false);
        //     }
        //   });
        // }

        // if ($secDevBtnInput.attr('code') == 'preview_btn') {
        //   $secDevBtnInput.attr('disabled', 'disabled');
        //   $secDevBtnInput.attr('checked', true);
        // }
        // if ($secDevBtnInput.attr('code') == 'download_btn') {
        //   $secDevBtnInput.attr('disabled', 'disabled');
        //   if (allowDownload) {
        //     $secDevBtnInput.attr('checked', true);
        //   }
        // }
        // if ($secDevBtnInput.attr('code') == 'delete_btn') {
        //   $secDevBtnInput.attr('disabled', 'disabled');
        //   if (allowDelete) {
        //     $secDevBtnInput.attr('checked', true);
        //   }
        // }
        // if ($secDevBtnInput.attr('code') == 'copy_name_btn') {
        //   $secDevBtnInput.attr('checked', true);
        //   $secDevBtnInput.attr('disabled', 'disabled');
        // }
      });
    }
  });

  JDS.call({
    service: 'dyformFileListSourceConfigService.getAllBean',
    data: [],
    version: '',
    success: function (result) {
      var fileSourceIds = fileSourceIdStr.split(';');
      //addElement
      $(result.data).each(function () {
        var $fileSourceDiv = $(
          '<span style="display:inline-block;"><input type="checkbox" name="file_source" id="file_source_' +
            this.uuid +
            '" value="' +
            this.uuid +
            '"/><label for="file_source_' +
            this.uuid +
            '">' +
            this.sourceName +
            '</label></span>'
        );
        $fileSourceDiv.appendTo(_self.$('.file_source_span'));
        var $fileSourceInput = $fileSourceDiv.find('input[name="file_source"]');
        if (
          fileSourceIds.indexOf(this.uuid) > -1 || //
          (field.fileSourceIdStr === null && this.defaultFlag === 1) // 默认选中
        ) {
          $fileSourceInput.attr('checked', true);
        }

        if (!field.fileSourceIdStr && this.code === 'local_upload') {
          // 旧数据没有选值 选中本地上传
          $fileSourceInput.attr('checked', true);
        }
      });
    }
  });

  //add by wujx 20160606 begin -->
  if (allowFileNameRepeat == undefined || allowFileNameRepeat) {
    $("input[name='allowFileNameRepeat'][value='1']").attr('checked', true);
  } else {
    $("input[name='allowFileNameRepeat'][value='1']").attr('checked', false);
  }
  // add by wujx 20160606 end -->

  if (isShowFileSourceIcon == undefined || isShowFileSourceIcon) {
    $("input[name='isShowFileSourceIcon'][value='1']").attr('checked', true);
  } else {
    $("input[name='isShowFileSourceIcon'][value='1']").attr('checked', false);
  }
  if (isShowFileFormatIcon == undefined || isShowFileFormatIcon) {
    $("input[name='isShowFileFormatIcon'][value='1']").attr('checked', true);
  } else {
    $("input[name='isShowFileFormatIcon'][value='1']").attr('checked', false);
  }

  this.$('#fileExt').val(field.fileExt);
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
  this.$('.show-type-help-popup').hide();
  this.$("input[name='showType']").each(function () {
    if ($(this).val() != '5') {
      $(this).hide();
      $(this).next().hide();
    } else {
      /*$(this).click(function(){

		 		console.log($(this).attr("checked"));
		 		if($(this).attr("checked") == "checked"){
		 			$(this).attr("checked", "") ;
		 		}else{
		 			$(this).attr("checked","checked")   ;
		 		}
		 	})*/
    }
  });

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

  var self = this;
  this.hasChacked = this.$("input[name='showType'][value='5']").prop('checked');
  this.$("input[name='showType'][value='5']").click(function () {
    self.hasChacked = !self.hasChacked;
    $(this).prop('checked', self.hasChacked);
  });

  field.downloadAllType = field.downloadAllType || 1;
  this.$("input[name='downloadAllType'][value='" + field.downloadAllType + "']").prop('checked', true);

  field.btnShowType = field.btnShowType || 1;
  this.$("input[name='btnShowType'][value='" + field.btnShowType + "']").prop('checked', true);
};

controlConfig.collectFormAndFillCkeditor = function () {
  var field = new WFileUploadClass();
  //added by linxr
  field.noNullValidateReminder = $('#noNullValidateReminder').val();
  //控件公共属性收集
  var checkpass = this.collectFormCtlComProperty(field);
  if (!checkpass) {
    return false;
  }
  //特殊属性收集
  field.inputMode = dyFormInputMode.accessory3;
  field.mutiselect = true;
  // field.fieldCheckRules.push({ value: field.contentFormat, label: $('#contentFormat').find('option:selected').text() });
  // var mutiselect= $("input[name='selecttype']:checked").val();
  var sign = $("input[name='sign']:checked").val();

  field.secDevBtnIdStr = '';
  $("input[name='sec_dev_btn']:checked").each(function () {
    if (this.checked) {
      if (this.code === 'upload_btn') {
        field.allowUpload = true;
      }
      if (this.code === 'download_btn') {
        field.allowDownload = true;
      }
      if (this.code === 'delete_btn') {
        field.allowDelete = true;
      }
      if (field.secDevBtnIdStr) {
        field.secDevBtnIdStr += ';' + $(this).val();
      } else {
        field.secDevBtnIdStr = $(this).val();
      }
    }
  });

  $("input[name='sec_dev_btn']:not(:checked)").each(function () {
    if (this.code === 'upload_btn') {
      field.allowUpload = false;
    }
    if (this.code === 'download_btn') {
      field.allowDownload = false;
    }
    if (this.code === 'delete_btn') {
      field.allowDelete = false;
    }
  });

  field.fileSourceIdStr = '';
  $("input[name='file_source']:checked").each(function () {
    if (this.checked) {
      if (field.fileSourceIdStr) {
        field.fileSourceIdStr += ';' + $(this).val();
      } else {
        field.fileSourceIdStr = $(this).val();
      }
    }
  });
  if (!field.fileSourceIdStr) {
    // 必须至少选中1个附件来源
    alert('至少选中1个附件来源');
    return false;
  }

  // add by wujx 20160606 begin -->
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
  $("input[name='isShowFileSourceIcon']:checked").each(function () {
    if (this.checked) {
      if ($(this).val() == '1') {
        field.isShowFileSourceIcon = true;
      }
    }
  });
  $("input[name='isShowFileSourceIcon']:not(:checked)").each(function () {
    if ($(this).val() == '1') {
      field.isShowFileSourceIcon = false;
    }
  });

  $("input[name='isShowFileFormatIcon']:checked").each(function () {
    if (this.checked) {
      if ($(this).val() == '1') {
        field.isShowFileFormatIcon = true;
      }
    }
  });
  $("input[name='isShowFileFormatIcon']:not(:checked)").each(function () {
    if ($(this).val() == '1') {
      field.isShowFileFormatIcon = false;
    }
  });
  /* if(mutiselect=='true'){
		field.mutiselect=true;
	}else{
		field.mutiselect=false;
	} */
  field.fileExt = this.$('#fileExt').val();
  field.fileMaxSize = this.$('#fileMaxSize').val();
  field.fileMaxNum = this.$('#fileMaxNum').val();
  field.downloadAllType = this.$("input[name='downloadAllType']:checked").val();
  field.btnShowType = this.$("input[name='btnShowType']:checked").val();
  //创建控件占位符
  this.createControlPlaceHolder(this, this.editor.placeHolderImage, field);

  formDefinition.addField(field.name, field);

  return true;
};

controlConfig.pluginName = CkPlugin.FILEUPLOADCTL;
addPlugin(controlConfig.pluginName, '列表式附件控件', '列表式附件控件属性设置', controlConfig);
