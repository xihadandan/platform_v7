(function ($) {
  $('.top li').each(function () {
    $(this).on('click', function () {
      var target = $(this).attr('target');
      $('.top li').removeClass('liA');
      $('.main > div').hide();
      $(this).addClass('liA');
      $('.' + target).show();
    });
  });

  $('#systemUnitSelect').on('change', function () {
    window.location.href = ctx + '/web/login/config/index?u=' + $(this).val();
  });
  var collectionConfigLoginType = function () {
    var data = {};

    $('#loginType').each(function () {
      var _form = $(this);
      _form.find(':input').each(function () {
        if ($(this).is("[type='text']") || $(this).is('select') || $(this).is("[type='hidden']")) {
          data[$(this).attr('name')] = $(this).val();
        } else if ($(this).is("[type='checkbox']")) {
          data[$(this).attr('name')] = $(this).is(':checked') ? '0' : '1';
        }
      });
    });
    return data;
  };
  var collectionConfigData = function () {
    if (!/^[0-9]*$/.test($('#loginBoxAccountCodeTimeout').val())) {
      appModal.error('请填写数字');
      $('#loginBoxAccountCodeTimeout').focus();
      return null;
    }

    if (!/^[0-9]*$/.test($('#loginBoxAccountSmsTimeout').val())) {
      appModal.error('请填写数字');
      $('#loginBoxAccountSmsTimeout').focus();
      return null;
    }

    var data = {};

    $('form').each(function () {
      var _form = $(this);
      _form.find(':input').each(function () {
        if ($(this).is("[type='text']") || $(this).is('select') || $(this).is("[type='hidden']")) {
          data[$(this).attr('name')] = $(this).val();
        } else if ($(this).is("[type='checkbox']")) {
          data[$(this).attr('name')] = $(this).is(':checked') ? '0' : '1';
        }
      });
    });
    data['footerContent'] = $('#footerContent').summernote('code');
    data['systemUnitId'] = $('#systemUnitSelect').val();
    data['enableOauthLogin'] = $('#enableOauthLogin').prop('checked') ? '1' : '0';
    return data;
  };

  $('#save').on('click', function () {
    var data = collectionConfigData();

    if (data.enableCustomizeOauthLogin == 1 && !data.customizeOauthLoginUri) {
      window.parent.appModal ? window.parent.appModal.error('自定义登录页面URL必填') : alert('自定义登录页面URL必填');
      throw new Error('统一认证登录设置：自定义登录页面URL必填');
    }

    if (data == null) {
      return false;
    }
    var formData = new FormData();
    var $pageBackgroundImage = $('[name="pageBackgroundImage"]')[0];
    if ($pageBackgroundImage.files && $pageBackgroundImage.files.length > 0) {
      formData.append('pageBackgroundImage', $pageBackgroundImage.files[0]);
    } else {
      formData.append('pageBackgroundImage', dataURLtoFile($('#pageBackgroundImagePreview').attr('src'), 'body.png'));
    }

    var $pageLogo = $('[name="pageLogo"]')[0];
    if ($pageLogo.files && $pageLogo.files.length > 0) {
      formData.append('pageLogo', $pageLogo.files[0]);
    } else {
      formData.append('pageLogo', dataURLtoFile($('#pageLogoPreview').attr('src'), 'header.png'));
    }

    formData.append('data', JSON.stringify(data));

    formData.append('loginType', JSON.stringify(collectionConfigLoginType()));
    $.ajax({
      url: ctx + '/web/login/config/save',
      type: 'POST',
      data: formData,
      processData: false, // 告诉jQuery不要去处理发送的数据
      contentType: false, // 告诉jQuery不要去设置Content-Type请求头
      success: function (result) {
        if (result.code == 0) {
          window.parent.appModal ? window.parent.appModal.success('保存成功！') : alert('保存成功！');
          $('#pageLogoPreview').attr('src', staticPrefix + '/js/pt/login/def/imgs/header.png?t=' + new Date().getTime());
          $('#pageBackgroundImage').attr('src', staticPrefix + '/js/pt/login/def/imgs/body.png?t=' + new Date().getTime());
          window.location.reload();
          if (result.data) {
            resultDialog({
              name: '温馨提示',
              pageId: 'page_20211123110115',
              params: ''
            });
          }
        } else {
          window.parent.appModal ? window.parent.appModal.success('保存失败！') : alert('保存失败！');
        }
      },
      error: function (responseStr) {
        window.parent.appModal ? window.parent.appModal.error('error') : alert('error');
      }
    });
  });

  var resultDialog = function (args) {
    var message =
      "<div id='newdialogdiv' ><div class='data-import-export-mod' style='padding: 15px;color: #EAA900; background-color: rgba(253, 250, 242, 1);'>" +
      "<span style='padding: 6px;'><i class='iconfont icon-ptkj-xinxiwenxintishi'></i></span>当前系统已开启多种登录账号类型，以下用户的账号信息存在重复！重复的账号类型无法正常登录！</div>" +
      "<div id='tableDiv'></div></div>";
    var dialogOptions = {
      title: args.name,
      size: 'large',
      message: message,
      className: 'custom-modal-dialog',
      // height: "600px",
      onEscape: function () {},
      // 显示弹出框后事件
      shown: function () {
        $('.bootbox-body').css({
          'max-height': 'initial'
        });
        window.parent.appContext.renderWidget({
          target: '_dialog',
          targetWidgetId: '',
          renderTo: '#tableDiv',
          widgetDefId: args.pageId,
          params: args.params,
          callback: function (widget) {
            $(widget.document).find('#newdialogdiv .fixed-table-search-toolbar').hide();
            $(widget.document).find('#newdialogdiv .fixed-table-toolbar').hide();
          },
          refresh: true
        });
      },
      buttons: {
        export: {
          label: '导出重复数据',
          className: 'btn-primary',
          callback: function () {
            var widget = window.parent.appContext.getWidgetById('wBootstrapTable_C99B3730B130000129831BD01ED06CB0');
            var params = {
              Api: widget.Api,
              beforeExportCode: '',
              exportTemplateId: '',
              fileName: '用户重复数据',
              pagination: {
                pageSize: 100000000,
                currentPage: 1
              },
              type: 'excel_ooxml_column'
            };
            widget.getDataProvider().exportData(params, function () {
              window.parent.appModal.error('导出异常');
            });
          }
        },
        cancel: {
          label: '关闭',
          className: 'btn-default',
          callback: function () {}
        }
      }
    };
    window.parent.appModal.dialog(dialogOptions);
  };

  var dataURLtoFile = function (dataurl, filename) {
    var arr = dataurl.split(',');
    var mime = arr[0].match(/:(.*?);/)[1];
    var bstr = atob(arr[1]);
    var n = bstr.length;
    var u8arr = new Uint8Array(n);
    while (n--) {
      u8arr[n] = bstr.charCodeAt(n);
    }
    return new File([u8arr], filename, {
      type: mime
    });
  };

  var uploadFile = function (context) {
    var ui = $.summernote.ui;

    var button = ui.button({
      contents: '<i class="note-icon-video"/>', //按钮样式
      tooltip: '上传文件',
      click: function () {
        $('#uploadFile').click();
      },
      callback: function () {}
    });

    return button.render(); //按钮渲染
  };

  $('#footerContent').summernote({
    fontNames: ['Microsoft Yahei'],
    lang: 'zh-CN',
    height: 200,
    toolbar: [
      ['color', ['color']],
      ['insert', ['uploadFile']], //['insert', ['uploadFile','link', 'picture', 'video']],
      ['view', ['codeview']]
    ],
    buttons: {
      uploadFile: uploadFile
    }
  });

  var code = $('#footerContent').summernote('code');
  if ($.trim(code).length <= 0 || code.indexOf('Microsoft Yahei') <= 0) {
    $('#footerContent').summernote('fontName', 'Microsoft Yahei');
  }

  var uploadFooterContentFile = function () {
    $('#uploadFile').on('change', function () {
      if ($(this)[0].files.length == 0) {
        return;
      }
      var formData = new FormData();
      formData.append('footerContentFile', $(this)[0].files[0]);
      $.ajax({
        url: ctx + '/web/login/config/uploadFooterContentFile',
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function (data) {
          $('#uploadFile').replaceWith('<input type="file" id="uploadFile" style="display: none;"/>');
          uploadFooterContentFile();
          if (data == 'error') {
            window.parent.appModal ? window.parent.appModal.error('系统异常，上传失败！') : alert('系统异常，上传失败！');
          } else {
            var code = $('#footerContent').summernote('code');
            $('#footerContent').summernote('code', code + data);
          }
        },
        error: function (responseStr) {
          console.log('error');
        }
      });
    });
  };

  uploadFooterContentFile();

  //是否启动验证码点击事件
  $('#loginBoxAccountCode').click(function () {
    if ($(this).is(':checked')) {
      $('#loginBoxAccountSms').attr('checked', false);
      $('#accountCodeIgnoreCase').prop('checked', true);
      $('#ignoreCaseFormGroup').show();
      $('#loginBoxAccountCodeTypeGroup').show();
      $('#accountCodeIgnoreCase').prop('checked', false);
      $('#loginBoxAccountCodeType').val('letterAndNumber');
    } else {
      $('#accountCodeIgnoreCase').prop('checked', false);
      $('#ignoreCaseFormGroup').hide();
      $('#loginBoxAccountCodeTypeGroup').hide();
    }
  });

  //验证码类型点击事件
  $('#loginBoxAccountCodeType').change(function () {
    if ($(this).val() == 'letterAndNumber') {
      $('#ignoreCaseFormGroup').show();
    } else {
      $('#accountCodeIgnoreCase').prop('checked', false);
      $('#ignoreCaseFormGroup').hide();
    }
  });

  $('#loginBoxAccountSms').click(function () {
    if ($(this).is(':checked')) {
      $('#loginBoxAccountCode').attr('checked', false);
      $('#accountCodeIgnoreCase').prop('checked', false);
      $('#ignoreCaseFormGroup').hide();
    }
  });

  $('.loginImages').on('change', function (e) {
    // 检查是否是图片
    var filePath = $(this).val(),
      id = $(this).attr('id'),
      fileFormat = filePath.substring(filePath.lastIndexOf('.')).toLowerCase(); //获取文件后缀名
    //检查后缀名
    if (!fileFormat.match(/.png/)) {
      window.parent.appModal ? window.parent.appModal.error('文件格式必须为：png') : alert('文件格式必须为：png');
      $(this).val('');
      return false;
    }

    //获取并记录图片的base64编码
    var reader = new FileReader();
    reader.readAsDataURL(e.target.files[0]); // 读出 base64
    reader.onloadend = function () {
      // 图片的 base64 格式, 可以直接当成 img 的 src 属性值
      var dataURL = reader.result; //base64
      // 显示图片
      $('#' + id + 'Preview').attr('src', dataURL);
    };
  });

  $('.btn-preview').on('click', function () {
    var image = new Image();
    image.src = $(this).prev().attr('src');
    var w = window.top.open('', '_blank');
    w.document.write(image.outerHTML);
    w.document.title = '预览';
    w.document.close();
  });

  $('#preview-login').on('click', function () {
    var data = collectionConfigData();
    if (data == null) {
      return false;
    }
    var userNamePlaceholder = function () {
      var placeholder = '请输入用户名';
      var loginType = collectionConfigLoginType();
      if (loginType.accountAlias != '') {
        placeholder = '请输入' + loginType.accountAlias;
      }
      placeholder = appendMsg(placeholder, loginType.accountZhEnable, loginType.accountZhAlias, '中文账号');
      placeholder = appendMsg(placeholder, loginType.nameEnEnable, loginType.nameEnAlias, '英文名');
      placeholder = appendMsg(placeholder, loginType.tellEnable, loginType.tellAlias, '手机号');
      placeholder = appendMsg(placeholder, loginType.identifierCodeEnable, loginType.identifierCodeAlias, '身份证号');
      placeholder = appendMsg(placeholder, loginType.emailEnable, loginType.emailAlias, '邮箱');
      placeholder = appendMsg(placeholder, loginType.empCodeEnable, loginType.empCodeAlias, '员工编号');
      return placeholder;
    };
    data.userNamePlaceholder = userNamePlaceholder();
    data.pageBackgroundImageBase64 = $('#pageBackgroundImagePreview').attr('src');
    data.pageLogoBase64 == $('#pageLogoPreview').attr('src');
    var entity = {};
    entity['data'] = data;
    $.ajax({
      url: ctx + '/web/login/config/preview',
      type: 'POST',
      data: JSON.stringify(entity),
      contentType: 'application/json',
      success: function (res) {
        console.log(res);
        var w = window.top.open('', 'previwloginpage');
        w.document.write(res);
        w.document.title = '预览';
        w.document.close();
      },
      error: function (responseStr) {
        window.parent.appModal ? window.parent.appModal.error(error) : alert('error');
      }
    });
  });

  var appendMsg = function (message, enable, placeholder, defaultPlaceholder) {
    if (enable == 1) {
      message = message + '/';
      message = placeholder == '' ? message + defaultPlaceholder : message + placeholder;
    }
    return message;
  };

  $('.settings-btn')
    .off()
    .on('click', function () {
      var _self = this;
      var alias = $(_self.parentNode).find(':input').val();
      var html = $('#aliasSetting').html();
      data = _self.getAttribute('data').split('_');
      html = html.replaceAll('#loginTypeName#', data[0]).replace('#userField#', data[1]).replace('#loginTypeAlias#', alias);
      window.parent.appModal.dialog({
        title: '登录账号类型设置',
        message: html,
        size: 'large',
        width: '600',
        height: '335',
        shown: function () {},
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function (value, a) {
              var alias = $(this).find('#alias-input').val();
              $(_self.parentNode).find(':input').val(alias);
              if (alias == '') {
                $(_self.parentNode.parentNode).find('.data-alias').html('');
              } else {
                $(_self.parentNode.parentNode)
                  .find('.data-alias')
                  .html('(' + alias + ')');
              }

              return true;
            }
          },
          cancel: {
            label: '关闭',
            className: 'btn-default',
            callback: function () {}
          }
        }
      });
    });
  //登录配置开启
  $('.loginType')
    .off()
    .on('click', function () {
      if ($(this).hasClass('active')) {
        $(this).removeClass('active').data('value', 0);
      } else {
        $(this).addClass('active').data('value', 1);
      }
      $(this).find(':input').val($(this).data('value'));
    });
  // 是否开启钉钉登录
  $('#isDingLogin')
    .off()
    .on('click', function () {
      if ($(this).hasClass('active')) {
        $(this).removeClass('active').data('value', 1);
        $('.dingLoginDetail').hide();
      } else {
        $(this).addClass('active').data('value', 0);
        $('.dingLoginDetail').show();
      }
      $('#loginBoxDing').val($(this).data('value'));
    });

  $('#enableOauthSwitch')
    .off()
    .on('click', function () {
      if ($(this).hasClass('active')) {
        // 关闭
        $(this).removeClass('active').data('value', 0);
        $('#oauthLoginSetting').find('.form-group:gt(0)').hide();
      } else {
        $(this).addClass('active').data('value', 1);
        if ($('#enableCustomizeOauthLogin').val() == 1) {
          $('#oauthLoginSetting').find('.form-group').show();
        } else {
          $('#oauthLoginSetting').find('.form-group:eq(1),.form-group:eq(2)').show();
        }
      }
      $('#enableOauth').val($(this).data('value'));
    });

  $('#enableCustomizeOauthLoginSwitch')
    .off()
    .on('click', function () {
      if ($(this).hasClass('active')) {
        // 关闭
        $(this).removeClass('active').data('value', 0);
        $(this).parents('.form-group').next().hide();
      } else {
        $(this).addClass('active').data('value', 1);
        $(this).parents('.form-group').next().show();
      }
      $('#enableCustomizeOauthLogin').val($(this).data('value'));
    });
})(jQuery);
