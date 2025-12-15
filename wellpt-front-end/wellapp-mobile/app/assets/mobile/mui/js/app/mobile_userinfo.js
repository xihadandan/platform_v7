define(['mui', 'commons', 'server', 'appContext', 'appModal', 'WorkView', 'WorkViewProxy', 'formBuilder'], function (
  $,
  commons,
  server,
  appContext,
  appModal,
  WorkView,
  workView,
  formBuilder
) {
  var modifyUserInfo = function (option) {
    var StringUtils = commons.StringUtils;
    $.ajax({
      url: ctx + '/mobile/mui/userInfo.html',
      async: false,
      success: function (data) {
        var wrapper = document.createElement('div');
        wrapper.id = 'mobile_userInfo';
        var pageContainer = appContext.getPageContainer();
        var renderPlaceholder = pageContainer.getRenderPlaceholder();
        renderPlaceholder[0].appendChild(wrapper);
        formBuilder.buildPanel({
          title: '修改个人信息',
          content: data.toString(),
          container: '#mobile_userInfo'
        });
        $.ui.loadContent('#mobile_userInfo');
      }
    });
    var userId = option.userId ? option.userId : '';
    var JDS = server.JDS;
    JDS.call({
      service: 'personalInfoService.getUserInfo',
      async: false,
      data: [userId],
      success: function (result) {
        var data = (window.userBean = result.data);
        // console.log(data);
        $('.user-name')[0].value = data.userName;
        if (data.sex == 1) {
          $('.male')[0].checked = true;
        } else {
          $('.female')[0].checked = true;
        }
        if (data.receiveSmsMessage == true) {
          $('#mobile_userInfo input.recevice-flg')[0].checked = true;
        }
        $('.idCard-id')[0].value = data.idNumber == null ? '' : data.idNumber;
        $('.phone-num')[0].value = data.mobilePhone || '';
        $('.phone-home')[0].value = data.homePhone || '';
        $('.phone-job')[0].value = data.officePhone || '';
        $('.mail')[0].value = data.mainEmail || '';
        $('.mail-other')[0].value = data.otherEmail || '';
        $("input[name='userUuid']")[0].value = data.uuid;
      }
    });
    var checkIdCard = function (idNum) {
      if (StringUtils.isBlank(idNum)) {
        return true;
      }
      var len = idNum.length,
        re;
      if (len == 15) re = new RegExp(/^(\d{6})()?(\d{2})(\d{2})(\d{2})(\d{2})(\w)$/);
      else if (len == 18) re = new RegExp(/^(\d{6})()?(\d{4})(\d{2})(\d{2})(\d{3})(\w)$/);
      else {
        // alert("输入的数字位数不对。");
        $.toast('输入的身份证号码格式不正确');
        return false;
      }
      var a = idNum.match(re);
      if (a != null) {
        if (len == 15) {
          var D = new Date('19' + a[3] + '/' + a[4] + '/' + a[5]);
          var B = D.getYear() == a[3] && D.getMonth() + 1 == a[4] && D.getDate() == a[5];
        } else {
          var D = new Date(a[3] + '/' + a[4] + '/' + a[5]);
          var B = D.getFullYear() == a[3] && D.getMonth() + 1 == a[4] && D.getDate() == a[5];
        }
        if (!B) {
          // alert("输入的身份证号 "+ a[0] +"
          // 里出生日期不对。");
          $.toast('输入的身份证号码格式不正确');
          return false;
        } else {
          // return true;
        }
      }
      if (len == 18) {
        var arr2 = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
        var arr3 = [1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2];
        var arr = idNum.split('');
        var s;
        var reg = /^\d+$/;
        var pd = 0;
        for (i = 0; i < 17; i++) {
          if (reg.test(arr[i])) {
            s = true;
            pd = arr[i] * arr2[i] + pd;
          } else {
            s = false;
            break;
          }
        }
        if (s == true) {
          var r = pd % 11;
          if (arr[17] == arr3[r]) {
          } else {
            $.toast('输入的身份证号码不合法');
            return false;
          }
        }
      }

      if (!re.test(idNum)) {
        // alert("身份证最后一位只能是数字和字母。");
        return false;
      } else {
        return true;
      }
    };

    var checkPhone = function (phoneNum, field, title) {
      var regMobilePhone = /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1})|(147)|(170)|(176)|(177)|(178))+\d{8})$/;
      if (StringUtils.isBlank(phoneNum)) {
        return true;
      } else {
        if (!(phoneNum.length == 11 && regMobilePhone.test(phoneNum))) {
          $.toast(field + ' ' + title);
          return false;
        } else {
          return true;
        }
      }
    };

    var checkEmail = function (email, field) {
      if (StringUtils.isBlank(email)) {
        return true;
      }
      var regMail = /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))$/i;
      if (!regMail.test(email)) {
        $.toast(field + '输入的邮件地址格式不正确');
        return false;
      } else {
        return true;
      }
    };

    var checkTel = function (telnum) {
      var tel = /0\d{2,3}-\d{7,8}/;
      if (StringUtils.isBlank(telnum)) {
        return true;
      } else {
        if (!tel.test(telnum)) {
          $.toast('输入的办公电话格式不正确,正确格式如：0597-1234567');
          return false;
        } else {
          return true;
        }
      }
    };

    /*	var fn = function(){
			$(".mui-btn-primary")[0].removeAttribute("disabled");
		}

		$("#mobile_userInfo").on("change","#user-form",fn);

		$("#mobile_userInfo").on("input","#user-form",fn);*/

    $('.btns').on('tap', '.mui-btn-primary', function () {
      var userUuid = $("#mobile_userInfo input[name='userUuid']")[0].value;
      var sex = $("#mobile_userInfo input[name='sex']:checked")[0].value;
      var idNumber = $.trim($('#mobile_userInfo input.idCard-id')[0].value);
      var mobilePhone = $.trim($('#mobile_userInfo input.phone-num')[0].value);
      var receiveSmsMessage = $('#mobile_userInfo input.recevice-flg')[0].checked ? true : false;
      var homePhone = $.trim($('#mobile_userInfo input.phone-home')[0].value);
      var officePhone = $.trim($('#mobile_userInfo input.phone-job')[0].value);
      var mainEmail = $.trim($('#mobile_userInfo input.mail')[0].value);
      var otherEmail = $.trim($('#mobile_userInfo input.mail-other')[0].value);
      var flag = true;
      flag =
        checkIdCard(idNumber) &&
        checkPhone(mobilePhone, '手机', '输入的手机号码格式不正确') &&
        checkPhone(homePhone, '家庭电话', '输入的电话号码格式不正确') &&
        checkTel(officePhone) &&
        checkEmail(mainEmail, '邮件(主)') &&
        checkEmail(otherEmail, '邮件(其他)');
      if (flag) {
        var userBean = window.userBean || {};
        $.extend(userBean, {
          uuid: userUuid,
          sex: sex,
          // "fax" : fax,
          password: '',
          idNumber: idNumber,
          // "photoUuid" : photoUuid,
          mainEmail: mainEmail,
          homePhone: homePhone,
          mobilePhone: mobilePhone,
          officePhone: officePhone
        });
        $.ajax({
          type: "POST",
          url: "/api/org/user/modifyUser",
          async: false,
          dataType: 'json',
          data: userBean,
          success: function (result) {
            $.back();
            setTimeout(function () {
              $.toast('保存成功');
            }, 100);
          },
          error: function () {
            $.toast('保存失败');
          }
        });

      }
    });
    $('.btns').on('tap', '.mui-btn-cancel', function () {
      $.back();
    });
  };

  return modifyUserInfo;
});
