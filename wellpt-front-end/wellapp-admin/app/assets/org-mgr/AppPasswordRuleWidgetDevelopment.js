define(['constant', 'commons', 'server', 'HtmlWidgetDevelopment'], function (constant, commons, server, HtmlWidgetDevelopment) {
  // 页面组件二开基础
  var AppPasswordRuleWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppPasswordRuleWidgetDevelopment, HtmlWidgetDevelopment, {
    // 组件初始化
    init: function () {
      var self = this;
      var $element = $(this.widget.element);
      var bean = {
        accountLockMinute: 0,
        advancePwdDay: 0,
        isEnforceUpdatePwd: 0,
        isPwdErrorLock: 0,
        isPwdValidity: 0,
        letterAsk: 'LA03',
        letterLimited: 'LL01',
        maxLength: 0,
        minLength: 0,
        moreTheanErrorNumber: 0,
        pwdValidity: 0,
        uuid: '',
        isPwdErrorLockAutoUnlock: '',
        isNotUserSetPwdUpdate: '',
        adminSetPwdType: ''
      };
      var stringData = [
        {
          text: '至少包含1种',
          id: 'LA01'
        },
        {
          text: '至少包含2种',
          id: 'LA02'
        },
        {
          text: '包含3种',
          id: 'LA03'
        }
      ];

      $('#letterAsk', $element).wellSelect({
        data: stringData,
        searchable: false,
        valueField: 'letterAsk',
        showEmpty: false
      });

      $('input[type="number"]', $element)
        .off('keydown')
        .on('keydown', function (e) {
          if (e.key == '.' || e.key == 'e') {
            return false;
          }
        })
        .on('paste', function (e) {
          var $this = $(this);
          setTimeout(function () {
            if ($this.val() == '' || $this.val().indexOf('.') > -1 || $this.val().indexOf('e') > -1) {
              $this.val('');
            }
          }, 16);
        });

      getPwdSettingTimerPageUrl();
      // getSystemParamsPwdTiming();
      getPasswordRuleInfo(bean);

      $('#loginTimer', $element)
        .off()
        .on('click', function () {
          window.open(self.url, '_blank');
        });

      $element.off('click', '.switch-wrap').on('click', '.switch-wrap', function () {
        if ($(this).hasClass('active')) {
          $(this).removeClass('active');
          $(this).siblings('.isHide').addClass('textHide');
          $(this).css({
            // marginTop: '7px'
          });
        } else {
          $(this).addClass('active');
          $(this).siblings('.isHide').removeClass('textHide');
          $(this).css({
            marginTop: ''
          });
        }
      });

      $('#password_rule_btn_save', $element)
        .off()
        .on('click', function () {
          $('#password_rule_form').form2json(bean);
          bean.letterLimited = $('#letterLimited', $element).prop('checked') ? 'LL01' : 'LL02';
          bean.isPwdValidity = $('#isPwdValidity', $element).parent().hasClass('active') ? '1' : '0';
          bean.isEnforceUpdatePwd = $('#isEnforceUpdatePwd', $element).parent().hasClass('active') ? '1' : '0';
          bean.isPwdErrorLock = $('#isPwdErrorLock', $element).parent().hasClass('active') ? '1' : '0';
          bean.isNotUserSetPwdUpdate = $('#isNotUserSetPwdUpdate', $element).parent().hasClass('active') ? '1' : '0';
          bean.isPwdErrorLockAutoUnlock = $('#isPwdErrorLockAutoUnlock', $element).prop('checked') ? '1' : '0';
          bean.adminSetPwdType = $('input[name="adminSetPwdType"]:checked', $element).val();
          var isSave = true;
          var tips3 = [];
          var minMaxTips = [];
          if (bean.minLength == '' && bean.maxLength == '') {
            tips3.push('最小值/最大值不能为空！');
          } else if (bean.minLength == '') {
            tips3.push('最小值不能为空！');
          } else if (bean.maxLength == '') {
            tips3.push('最大值不能为空！');
          }
          if (bean.minLength != '' && (bean.minLength < 1 || bean.minLength > 20)) {
            minMaxTips.push('最小值必须是1~20之间的正整数！');
          }
          if (bean.maxLength != '' && (bean.maxLength < 1 || bean.maxLength > 20)) {
            minMaxTips.push('最大值必须是1~20之间的正整数！');
          }
          if (minMaxTips.length == 2) {
            tips3.push('最小值/最大值必须是1~20之间的正整数！');
          } else if (minMaxTips.length == 1) {
            tips3.push(minMaxTips[0]);
          }

          if (bean.minLength != '' && bean.maxLength != '' && bean.maxLength - bean.minLength < 0) {
            tips3.push('最小值≤最大值！');
          }
          //#需求5592 密码最小长度支持配置为1 2021-12-20
          var letterAskValue = $('#letterAsk').val();
          if (letterAskValue == 'LA01') {
            //至少包含1种
          } else if (letterAskValue == 'LA02') {
            //至少包含2种
            if (bean.minLength < 2) {
              tips3.push('长度要求不符合字符要求和字母限制的约束！');
            }
          } else {
            //包含3种

            if ((bean.letterLimited == 'LL01' && bean.minLength >= 4) || (bean.letterLimited == 'LL02' && bean.minLength >= 3)) {
              // $('#letterAsk').parent().parent('div').find('.error').hide();
            } else {
              tips3.push('长度要求不符合字符要求和字母限制的约束！');
            }
          }

          if (tips3.length > 0) {
            $('#minLength').parents('div').first().find('.error').html(tips3.join('')).show();
            isSave = false;
          } else {
            $('#minLength').parents('div').first().find('.error').html('').hide();
          }
          if (bean.isPwdValidity == '1') {
            var tips = [];
            if (bean.pwdValidity == '') {
              tips.push('过期天数不能为空！');
            } else if (bean.pwdValidity < 1 || bean.pwdValidity > 999) {
              tips.push('过期天数必须是1~999之间的正整数！');
            }
            if (bean.advancePwdDay == '') {
              tips.push('提醒天数不能为空！');
            } else if (bean.advancePwdDay < 1 || bean.advancePwdDay > 10) {
              tips.push('提醒天数必须是1~10之间的正整数！');
            }
            if (tips.length > 0) {
              $('#advancePwdDay').parents('.isHide').find('.error').html(tips.join('')).show();
              isSave = false;
            } else {
              $('#advancePwdDay').parents('.isHide').find('.error').html('').hide();
            }
          }

          if (bean.isPwdErrorLock == '1') {
            var tips2 = [];
            if (bean.moreTheanErrorNumber == '') {
              tips2.push('错误次数不能为空！');
            } else if (bean.moreTheanErrorNumber < 1 || bean.moreTheanErrorNumber > 999) {
              tips2.push('错误次数必须是1~999之间的正整数！');
            }
            if (bean.accountLockMinute == '') {
              tips2.push('锁定时间不能为空！');
            } else if (bean.accountLockMinute < 1 || bean.accountLockMinute > 99999) {
              tips2.push('锁定时间必须是1~99999之间的正整数！');
            }
            if (tips2.length > 0) {
              $('#accountLockMinute').parents('.isHide').find('.error').html(tips2.join('')).show();
              isSave = false;
            } else {
              $('#accountLockMinute').parents('.isHide').find('.error').html('').hide();
            }
          }
          if (!isSave) {
            return false;
          }

          $.ajax({
            type: 'POST',
            url: ctx + '/api/pwd/setting/saveMultiOrgPwdSetting',
            dataType: 'json',
            data: bean,
            success: function (result) {
              if (result.code == 0) {
                appModal.success('保存成功！');
                self.refresh();
              } else {
                appModal.success('保存失败！');
              }
            }
          });
        });

      function getPasswordRuleInfo(bean) {
        $.ajax({
          type: 'GET',
          url: ctx + '/api/pwd/setting/getMultiOrgPwdSetting',
          dataType: 'json',
          success: function (result) {
            bean = result.data;
            $('#password_rule_form', $element).json2form(bean);
            $('#letterAsk', $element).wellSelect('val', bean.letterAsk || 'LA03');
            $('#letterLimited').prop('checked', bean.letterLimited == 'LL01' ? true : false);
            $('#minLength', $element).val(bean.minLength || '8');
            $('#maxLength', $element).val(bean.maxLength || '20');
            $('#pwdValidity', $element).val(bean.pwdValidity || '90');
            $('#advancePwdDay', $element).val(bean.advancePwdDay || '3');
            $('#moreTheanErrorNumber', $element).val(bean.moreTheanErrorNumber || '5');
            $('#accountLockMinute', $element).val(bean.accountLockMinute || '30');
            var isPwdValidity = $('#isPwdValidity', $element).parent().hasClass('active');
            var isEnforceUpdatePwd = $('#isEnforceUpdatePwd', $element).parent().hasClass('active');
            var isPwdErrorLock = $('#isPwdErrorLock', $element).parent().hasClass('active');
            var isPwdErrorLock = $('#isPwdErrorLock', $element).parent().hasClass('active');
            var isNotUserSetPwdUpdate = $('#isNotUserSetPwdUpdate', $element).parent().hasClass('active');
            if ((bean.isPwdValidity == '1' && !isPwdValidity) || (bean.isPwdValidity == '0' && isPwdValidity)) {
              $('#isPwdValidity', $element).parent().trigger('click');
            }
            if ((bean.isEnforceUpdatePwd == '1' && !isEnforceUpdatePwd) || (bean.isEnforceUpdatePwd == '0' && isEnforceUpdatePwd)) {
              $('#isEnforceUpdatePwd', $element).parent().trigger('click');
            }
            if ((bean.isPwdErrorLock == '1' && !isPwdErrorLock) || (bean.isPwdErrorLock == '0' && isPwdErrorLock)) {
              $('#isPwdErrorLock', $element).parent().trigger('click');
            }
            if (
              (bean.isNotUserSetPwdUpdate == '1' && !isNotUserSetPwdUpdate) ||
              (bean.isNotUserSetPwdUpdate == '0' && isNotUserSetPwdUpdate)
            ) {
              $('#isNotUserSetPwdUpdate', $element).parent().trigger('click');
            }
            $('#isPwdErrorLockAutoUnlock').prop('checked', bean.isPwdErrorLockAutoUnlock == '1' ? true : false);

            $('input[name="adminSetPwdType"][value="' + (bean.adminSetPwdType || 'ASPT01') + '"]').prop('checked', true);
          }
        });
      }

      function getPwdSettingTimerPageUrl() {
        $.ajax({
          type: 'GET',
          url: ctx + '/api/pwd/setting/getPwdSettingTimerPageUrl',
          dataType: 'json',
          success: function (result) {
            if (result.code == 0) {
              self.url = result.data;
            }
          }
        });
      }

      // function getSystemParamsPwdTiming() {
      //   $.ajax({
      //     type: 'GET',
      //     url: ctx + '/api/pwd/setting/getSystemParamsPwdTiming',
      //     dataType: 'json',
      //     success: function (result) {
      //       if (result.code == 0) {
      //         var msg = result.data == 'TIMED' ? '定时任务' : '消息队列';
      //         $('.expiredTips', $element).text('判断是否过期，通过' + msg + '触发');
      //         $('.lockTips', $element).text('到达锁定时间后自动解锁，通过' + msg + '触发');
      //       }
      //     }
      //   });
      // }
    },
    refresh: function () {
      this.init();
    }
  });
  return AppPasswordRuleWidgetDevelopment;
});
