define(['constant', 'commons', 'server', 'appModal', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appModal,
  HtmlWidgetDevelopment
) {
  // 平台管理_产品集成_消息格式配置详情_HTML组件二开
  var AppLoginPageConfigWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppLoginPageConfigWidgetDevelopment, HtmlWidgetDevelopment, {
    // 组件初始化
    init: function () {
      var $element = this.widget.element;

      var url = location.host + '/login/unit/';
      $('#login-url', $element).text(url);

      getLoginConfig();

      $('.btn-radio', $element)
        .off()
        .on('click', function () {
          if ($(this).hasClass('btn-disabled')) {
            return false;
          }
          var val = $(this).data('val');
          $(this).addClass('active').siblings().removeClass('active');
          $('.custom-login-content', $element)[val == '1' ? 'show' : 'hide']();
        });

      $('#loginConfigSave', $element)
        .off()
        .on('click', function () {
          var formData = new FormData();
          var data = {};
          var unitLoginPageSwitch = $('.btn-radio.active').data('val');
          data.unitLoginPageSwitch = unitLoginPageSwitch;

          var status = false;
          if (unitLoginPageSwitch == '1') {
            $('form').each(function () {
              var _form = $(this);
              _form.find(':input').each(function () {
                if ($(this).is("[type='text']") || $(this).is('select') || $(this).is("[type='hidden']")) {
                  if ($(this).attr('name')) {
                    data[$(this).attr('name')] = $(this).val();
                  }
                }
              });
            });
            data['footerContent'] = $('#footerContent').summernote('code');
            data['systemUnitId'] = server.SpringSecurityUtils.getCurrentUserUnitId();

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
            var reg = /^[A-Za-z]+$/;
            if (data.unitLoginPageUri == '') {
              appModal.error('访问地址后缀不能为空！');
              return false;
            } else if (!reg.test(data.unitLoginPageUri)) {
              appModal.error('访问地址后缀仅支持英文字母！');
              return false;
            }

            $.ajax({
              type: 'get',
              url: ctx + '/web/login/config/loginPageSettings',
              data: {
                loginUrl: data.unitLoginPageUri
              },
              contentType: 'json',
              dataType: 'json',
              async: false,
              success: function (res) {
                if (null != res && res.systemUnitId !== data.systemUnitId) {
                  appModal.error('已存在相同的访问地址，请修改并保持访问地址唯一。');
                  return false;
                } else {
                  status = true;
                }
              }
            });
          } else {
            status = true;
          }

          if (status) {
            data.uuid = $('#pageConfUuid').val();
            formData.append('data', JSON.stringify(data));
            formData.append('loginType', '{}');

            $.ajax({
              type: 'post',
              url: ctx + '/web/login/config/saveUnitConf',
              data: formData,
              processData: false, // 告诉jQuery不要去处理发送的数据
              contentType: false, // 告诉jQuery不要去设置Content-Type请求头
              success: function (res) {
                if (res.code == 0) {
                  appModal.success('保存成功！');
                } else {
                  appModal.error('保存失败！');
                }
              }
            });
          }
        });

      function getLoginConfig() {
        $.ajax({
          type: 'get',
          url: ctx + '/web/login/config/unitLoginPageSetting',
          data: {
            systemUnitId: SpringSecurityUtils.getCurrentUserUnitId()
          },
          dataType: 'json',
          success: function (res) {
            if (res.code == 0) {
              var data = res.data;
              if (data.enableOauth == '1') {
                $(".btn-radio[data-val='0']").trigger('click');
                $(".btn-radio[data-val='1']").addClass('btn-disabled');
                $('.enableOauth').show();
              } else if (data.unitLoginPageSwitch && data.unitLoginPageSwitch == '1') {
                $(".btn-radio[data-val='1']").trigger('click');
                $('#unitLoginPageUri').val(data.unitLoginPageUri);
                $('#pageTitle').val(data.pageTitle);
                $('#uuid').val(data.uuid);
                $('#pageStyle').val(data.pageStyle);
                $('#footerContent').summernote('code', data.footerContent);
              } else {
                $('#unitLoginPageUri').val(data.unitLoginPageUri);
                $('#pageTitle').val(data.pageTitle);
                $('#pageStyle').val(data.pageStyle);
                $('#footerContent').summernote('code', data.footerContent);
                $(".btn-radio[data-val='0']").trigger('click');
              }

              var systemUnitId = data.systemUnitId;
              if ('T001' != systemUnitId && 'S0000000000' != systemUnitId) {
                $('#pageConfUuid').val(data.uuid);
              }
              $('#sysUnitId').val(SpringSecurityUtils.getCurrentUserUnitId());
              $('#pageLogoPreview').attr('src', 'data:image/png;base64,' + data.pageLogoBase64);
              $('#pageBackgroundImagePreview').attr('src', 'data:image/png;base64,' + data.pageBackgroundImageBase64);
              $('#loginBoxHardware').val(data.loginBoxHardware);
              $('#loginBoxKey').val(data.loginBoxKey);
              $('#loginBoxCode').val(data.loginBoxCode);
              $('#loginBoxDing').val(data.loginBoxDing);
              $('#loginBoxAccountRememberUse').val(data.loginBoxAccountRememberUse);
              $('#loginBoxAccountRememberPas').val(data.loginBoxAccountRememberPas);
              $('#loginBoxAccountForgetPassw').val(data.loginBoxAccountForgetPassw);
              $('#enableOauth').val(data.enableOauth);
              $('#userNamePlaceholder').val(data.userNamePlaceholder);
            }
          }
        });
      }

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
        var data = {};

        var unitLoginPageSwitch = $('.btn-radio.active').data('val');
        if (unitLoginPageSwitch == '1') {
          $('form').each(function () {
            var _form = $(this);
            _form.find(':input').each(function () {
              if ($(this).is("[type='text']") || $(this).is('select') || $(this).is("[type='hidden']")) {
                data[$(this).attr('name')] = $(this).val();
              }
            });
          });
          data['footerContent'] = $('#footerContent').summernote('code');
          if (data == null) {
            return false;
          }

          data.pageBackgroundImageBase64 = $('#pageBackgroundImagePreview').attr('src');
          data.pageLogoBase64 = $('#pageLogoPreview').attr('src');
          data.loginBoxHardware = $('#loginBoxHardware').val();
          data.loginBoxKey = $('#loginBoxKey').val();
          data.loginBoxCode = $('#loginBoxCode').val();
          data.loginBoxDing = $('#loginBoxDing').val();
          data.loginBoxAccountRememberUse = $('#loginBoxAccountRememberUse').val();
          data.loginBoxAccountRememberPas = $('#loginBoxAccountRememberPas').val();
          data.loginBoxAccountForgetPassw = $('#loginBoxAccountForgetPassw').val();
          data.enableOauth = $('#enableOauth').val();
          data.userNamePlaceholder = $('#userNamePlaceholder').val();
          var entity = {};
          entity['data'] = data;
          $.ajax({
            url: ctx + '/web/login/config/preview',
            type: 'POST',
            data: JSON.stringify(entity),
            contentType: 'application/json',
            success: function (res) {
              var w = window.top.open('', 'previwloginpage');
              w.document.write(res);
              w.document.title = '预览';
              w.document.close();
            },
            error: function (responseStr) {
              window.parent.appModal ? window.parent.appModal.error(error) : alert('error');
            }
          });
        } else {
          $.ajax({
            type: 'get',
            url: ctx + '/web/login/config/unitLoginPageSetting',
            data: {
              systemUnitId: ''
            },
            dataType: 'json',
            success: function (res) {
              if (res.code == 0) {
                var data = res.data;
                var entity = {};
                entity['data'] = data;
                $.ajax({
                  url: ctx + '/web/login/config/preview',
                  type: 'POST',
                  data: JSON.stringify(entity),
                  contentType: 'application/json',
                  success: function (res) {
                    var w = window.top.open('', 'previwloginpage');
                    w.document.write(res);
                    w.document.title = '预览';
                    w.document.close();
                  },
                  error: function (responseStr) {
                    window.parent.appModal ? window.parent.appModal.error(error) : alert('error');
                  }
                });
              }
            }
          });
        }
      });
    }
  });
  return AppLoginPageConfigWidgetDevelopment;
});
