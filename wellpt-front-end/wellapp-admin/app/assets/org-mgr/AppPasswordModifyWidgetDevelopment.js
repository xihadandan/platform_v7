define(['constant', 'commons', 'server', 'HtmlWidgetDevelopment', 'js-base64'], function (
  constant,
  commons,
  server,
  HtmlWidgetDevelopment,
  base64
) {
  // 页面组件二开基础
  var AppPasswordModifyWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppPasswordModifyWidgetDevelopment, HtmlWidgetDevelopment, {
    // 组件初始化
    init: function () {
      var self = this;
      var $element = $(this.widget.element);
      var urlform = GetRequestParam().tag;

      if (urlform == '1') {
        var msg = getCookie('pwdMsg');
        if (msg) {
          $('#pwdMsg').html(msg).show();
        }
      }

      getPasswordRuleInfo();

      $('#oldPwd', $element)
        .off('blur')
        .on('blur', function () {
          if ($(this).val() == '') {
            $(this).next('.error').html('旧密码不能为空！').show();
          } else {
            $(this).next('.error').html('').hide();
          }
        });

      $('#newPwd', $element)
        .off('blur')
        .on('blur', function () {
          var val = $(this).val();
          if (val == '') {
            $(this).next('.error').html('新密码不能为空！').show();
          } else {
            var latter = self.rules.letterAsk;
            var minLength = self.rules.minLength || 4;
            var maxLength = self.rules.maxLength || 20;
            var isHide = true;
            var latterRegLower = /[a-z]+/;
            var latterRegupper = /[A-Z]+/;
            var latters = /[a-zA-Z]+/;
            var numReg = /[0-9]+/;
            var others = /[`~!@#$%^&*()_\-+=<>?:"{}|,.\/;'\\[\]·~！@#￥%……&*（）——\-+={}|《》？：“”【】、；‘'，。、]+/im;

            if (minLength > val.length || maxLength < val.length) {
              isHide = false;
            } else if (/[\u4E00-\u9FA5]+/.test(val)) {
              isHide = false;
            } else {
              var i = 0;
              if (latterRegLower.test(val) || latterRegupper.test(val)) {
                i++;
              }

              if (numReg.test(val)) {
                i++;
              }
              if (others.test(val)) {
                i++;
              }
              if (i - (latter == 'LA02' ? '2' : latter == 'LA01' ? '1' : '3') < 0) {
                isHide = false;
              } else if (
                self.rules.letterLimited == 'LL01' &&
                latters.test(val) &&
                (!latterRegLower.test(val) || !latterRegupper.test(val))
              ) {
                isHide = false;
              }
            }
            if (!isHide) {
              $(this)
                .next('.error')
                .html('不符合密码格式：' + self.placeholder + '！')
                .show();
              return false;
            } else {
              $(this).next('.error').html('').hide();
            }
          }
        });

      $('#confirmPwd', $element)
        .off('blur')
        .on('blur', function () {
          if ($(this).val() == '') {
            $(this).next('.error').html('请再次输入新密码！').show();
          } else if ($(this).val() != $('#newPwd', $element).val()) {
            $(this).next('.error').html('两次密码输入不一致，请再次输入新密码！').show();
          } else {
            $(this).next('.error').html('').hide();
          }
        });

      $('#password_rule_btn_save', $element)
        .off()
        .on('click', function () {
          var len = $('#personalInfoModifyPwdForm', $element).find('.error:visible').length;
          var oldPwd = $('#oldPwd', $element).val();
          var newPwd = $('#newPwd', $element).val();
          var confirmPwd = $('#confirmPwd', $element).val();
          var isSave = true;
          if (oldPwd == '') {
            $('#oldPwd', $element).next('.error').html('旧密码不能为空！').show();
            isSave = false;
          }
          if (newPwd == '') {
            $('#newPwd', $element)
              .next('.error')
              .html(self.placeholder + '！')
              .show();
            isSave = false;
          }
          if (confirmPwd == '') {
            $('#confirmPwd', $element).next('.error').html('请再次输入新密码！').show();
            isSave = false;
          }
          if (newPwd != confirmPwd) {
            $('#confirmPwd', $element).next('.error').html('两次密码输入不一致，请再次输入新密码！').show();
            isSave = false;
          }
          if (!isSave || len > 0) {
            return false;
          }
          $.ajax({
            type: 'POST',
            url: ctx + '/api/personalinforest/modifyCurrentUserPasswordEncrypt',
            dataType: 'json',
            data: {newPwd: base64.encode(urlencode(newPwd)), oldPwd: base64.encode(urlencode(oldPwd))},
            success: function (result) {
              var data = result.data;
              if (data && data.success) {
                appModal.success(
                  {
                    message: '修改成功！'
                  },
                  function () {
                    if (urlform == '1') {
                      var newUrl = location.origin;
                      window.open(newUrl, '_self');
                    } else {
                      window.close();
                      delCookie('pwdMsg');
                    }
                  }
                );
              } else {
                if (data.locked) {
                  appModal.dialog({
                    message: data.message.split('isLocked:')[0],
                    title: '账号锁定',
                    buttons: {
                      ok: {
                        label: '确定',
                        className: 'well-btn w-btn-primary',
                        callback: function () {
                          //因账号锁定，当前登录已被强制退出，请重新登录！
                          window.open(ctx + '/security_logout', '_self');
                        }
                      }
                    }
                  });
                } else {
                  appModal.alert(data.message);
                }
              }
            }
          });
        });

      function getPasswordRuleInfo() {
        $.ajax({
          type: 'GET',
          url: ctx + '/api/pwd/setting/getMultiOrgPwdSetting',
          dataType: 'json',
          success: function (result) {
            self.rules = result.data;
            var latter = self.rules.letterAsk == 'LA02' ? '至少包含2种' : self.rules.letterAsk == 'LA01' ? '至少包含1种' : '包含3种';
            var minLength = self.rules.minLength || 4;
            var maxLength = self.rules.maxLength || 20;
            var letterLimited = self.rules.letterLimited == 'LL01' ? '(必须要有大写、小写)' : '';
            self.placeholder = '字母' + letterLimited + '、数字、特殊字符中' + latter + '，' + minLength + '~' + maxLength + '位';
            $('#newPwd', $element).attr('placeholder', self.placeholder);
          }
        });
      }

      //添加cookie time 单位s
      function addCookie(name, value, time) {
        var exp = new Date();
        exp.setTime(exp.getTime() + time);
        //设置cookie的名称、值、失效时间
        document.cookie = name + "=" + value + ";expires=" + exp.toGMTString();
      }

      function getCookie(name) {
        var reg = new RegExp('(^| )' + name + '=([^;]*)(;|$)');
        var arr = document.cookie.match(reg);
        if (arr) {
          return unescape(arr[2]);
        }
        return null;
      }

      function delCookie(name) {
        var cookies = document.cookie.split(';');
        var newCookie = [];
        for (var i = 0; i < cookies.length; i++) {
          var cookie = cookies[i].split('=');
          if (cookie[0] == name) {
            continue;
          }
          newCookie.push(cookie);
        }
        document.cookie = newCookie.join(';');
      }
    },
    refresh: function () {
      this.init();
    }
  });
  return AppPasswordModifyWidgetDevelopment;
});
