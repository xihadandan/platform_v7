$(function () {

  if (localStorage.rememberUser) {
    $("#username").val(localStorage.username);
    $(".remember-username").addClass('check');
  }
  // 登录跳转的hash
  if (window.location.hash) {
    localStorage.setItem("loginRedirectHash", window.location.hash);
  } else {
    localStorage.removeItem("loginRedirectHash");
  }

  if (localStorage.rememberPwd) {
    $("#password").val(decryptPwd(localStorage.password, localStorage.passwordEncryptKey));
    $(".remember-password").addClass('check');
  }
  //判断操作系统类型
  var os = (function () {
    var UserAgent = navigator.userAgent.toLowerCase();
    return {
      isIpad: /ipad/.test(UserAgent),
      isIphone: /iphone os/.test(UserAgent),
      isAndroid: /android/.test(UserAgent),
      isWindowsCe: /windows ce/.test(UserAgent),
      isWindowsMobile: /windows mobile/.test(UserAgent),
      isWin2K: /windows nt 5.0/.test(UserAgent),
      isXP: /windows nt 5.1/.test(UserAgent),
      isVista: /windows nt 6.0/.test(UserAgent),
      isWin7: /windows nt 6.1/.test(UserAgent),
      isWin8: /windows nt 6.2/.test(UserAgent),
      isWin81: /windows nt 6.3/.test(UserAgent)
    };
  }());
  //判断浏览器类型
  var bw = (function () {
    var UserAgent = navigator.userAgent.toLowerCase();
    return {
      isUc: /ucweb/.test(UserAgent), // UC浏览器
      isChrome: /chrome/.test(UserAgent.substr(-33, 6)), // Chrome浏览器
      isChromium: /chrome/.test(UserAgent.substr(-32, 7)), // Chromium浏览器
      isFirefox: /firefox/.test(UserAgent), // 火狐浏览器
      isOpera: /opera/.test(UserAgent), // Opera浏览器
      isSafire: /safari/.test(UserAgent) && !/chrome/.test(UserAgent), // safire浏览器
      is360: /360se/.test(UserAgent), // 360浏览器
      isBaidu: /bidubrowser/.test(UserAgent), // 百度浏览器
      isSougou: /metasr/.test(UserAgent), // 搜狗浏览器
      isIE6: /msie 6.0/.test(UserAgent), // IE6
      isIE7: /msie 7.0/.test(UserAgent), // IE7
      isIE8: /msie 8.0/.test(UserAgent), // IE8
      isIE9: /msie 9.0/.test(UserAgent), // IE9
      isIE10: /msie 10.0/.test(UserAgent), // IE10
      isIE11: /msie 11.0/.test(UserAgent), // IE11
      isLB: /lbbrowser/.test(UserAgent), // 猎豹浏览器
      isWX: /micromessenger/.test(UserAgent), // 微信内置浏览器
      isQQ: /qqbrowser/.test(UserAgent), // QQ浏览器
      isEdng: /windows nt 6.3/.test(UserAgent)
      // edng
    };
  }());

  function showErrorMsg(msg) {
    if ($('.error-div').length > 0) {
      $('.error-div').html(msg);
    } else {
      var html = '<div class="error-div">' + msg + '</div>';
      $('.box').append(html);
    }
  }

  $('.account button').click(function () {

    var username = $("#username").val();
    var password = $("#password").val();
    var encUsername = username;
    var encPassword = encryptPwd(password);
    var rememberUser = $(".remember-username").hasClass('check');
    var rememberPwd = $(".remember-password").hasClass('check');
    if (username == '') {
      showErrorMsg('用户名不能为空！');
      return;
    }

    if (password == '') {
      showErrorMsg('密码不能为空！');
      return;
    }

    var isCheck = false;
    if ($('#image-verify').length > 0) {
      if ($('#image-verify').val() == '') {
        showErrorMsg('验证码不能为空！');
        return;
      }

      $.ajax({
        type: "GET",
        url: "/captcha?verify=" + $('#image-verify').val() + "&ignorecase=" + $("#image-verify").attr('ignore-case'),
        async: false,
        dataType: 'json',
        success: function (data) {
          isCheck = !data.success;
          if (data.msg) {
            showErrorMsg(data.msg);
          }
        }
      });

      if (isCheck) {
        return;
      }
    }

    if ($('#sms-verify').length > 0) {
      if ($('#sms-verify').val() == '') {
        showErrorMsg('验证码不能为空！');
        return;
      }

      $.ajax({
        type: "POST",
        url: contextPath + "/security/aid/checkSmsCode",
        async: false,
        dataType: 'json',
        data: {
          code: $('#sms-verify').val(),
          username: username
        },
        success: function (data) {
          for (var i in data) {
            if (i == 1) {
              showErrorMsg(data[i]);
              isCheck = true;
            }
          }
        }
      });

      if (isCheck) {
        return;
      }

      $("input[name='j_verify_code']", "#loginForm").val($('#sms-verify').val());
    }

    var loginNameHashAlgorithmCode = SystemParams.getValue('security.login_name.hash_algorithm.code', '1');
    var passwordhHshAlgorithmCode = SystemParams.getValue('security.password.hash_algorithm.code', '1');

    //记住用户名
    if (rememberUser) {
      localStorage.setItem("username", username);
      localStorage.setItem("rememberUser", rememberUser);
    } else {
      localStorage.removeItem("username");
      localStorage.removeItem("rememberUser");
    }

    //记住密码
    if (rememberPwd) {
      localStorage.setItem("password", encPassword);
      localStorage.setItem("rememberPwd", rememberPwd);
    } else {
      localStorage.removeItem("password");
      localStorage.removeItem("rememberPwd");
    }

    // 登录名MD5加密
    //if (loginNameHashAlgorithmCode == "2") {
    //	encUsername = md5().fromUTF8(username + "{" + username + "}");
    //}
    // 密码MD5加密
    //if (passwordhHshAlgorithmCode == "2") {
    //	encPassword = md5().fromUTF8(password + "{" + username + "}");
    //}
    $("input[name='username']", "#loginForm").val(encUsername);
    $("input[name='password']", "#loginForm").val(encPassword);
    $("input[name='j_lnalg_code']", "#loginForm").val(loginNameHashAlgorithmCode);
    $("input[name='j_pwdalg_code']", "#loginForm").val(passwordhHshAlgorithmCode);
    $("input[name='userOs']", "#loginForm").val(getUserOs());
    $("input[name='browser']", "#loginForm").val(getBrowser());
    $("input[name='browserVersion']", "#loginForm").val(getBrowserVersion());
    $("#loginForm").submit();

  });

  $('.image-verify-li img').click(function () {
    var time = new Date().getTime();
    $(this).attr('src', contextPath + '/security/aid/image?time=' + time);
  });

  $("#image-verify").next('span').click(function () {
    var _this = $(this);
    $.get('/captcha', {}, function (res) {
      _this.empty();
      _this.html(res);
    });
  });

//	if($('#image-verify').length > 0){
//		$("#image-verify").keypress(function(e) {
//			if (e.keyCode == 13) {
//				$('.account button').trigger("click");
//			}
//		});
//
//	}else if($('#sms-verify').length > 0){
//		$("#sms-verify").keypress(function(e) {
//			if (e.keyCode == 13) {
//				$('.account button').trigger("click");
//			}
//		});
//
//	}else{
//		$("#password").keypress(function(e) {
//			if (e.keyCode == 13) {
//				$('.account button').trigger("click");
//			}
//		});
//	}

  $(document).on("keypress", function (e) {
    if (e.keyCode == 13) {
      if ($(e.target).parents(".key").length > 0) {
        return $('.key button').trigger("click");
      }
      $('.account button').trigger("click");
    }
  })

  var index = 60;
  var interval = null;
  $('#sendSms').click(function () {

    if ($(this).hasClass('spanA')) {
      return;
    }

    var username = $("#username").val();
    if (username == '') {
      showErrorMsg('用户名不能为空！');
      return;
    }

    $.ajax({
      type: "POST",
      url: contextPath + "/security/aid/sendSms",
      dataType: 'json',
      data: {
        username: username
      },
      success: function (data) {
        for (var i in data) {
          if (i == 1) {
            showErrorMsg(data[i]);
          } else {
            index = 60;
            $('#sendSms').addClass('spanA').html(index + 's后重发');
            interval = window.setInterval(intervalCode, 1000);
          }
        }
      }
    });
  });

  function intervalCode() {
    if (index > 0) {
      index = index - 1;
      $('#sendSms').addClass('spanA').html(index + 's后重发');
    } else {
      if (interval) {
        window.clearInterval(interval);
      }
      $('#sendSms').removeClass('spanA').html('获取验证码');
    }
  }

  function encryptPwd(rawPassword) {
    var pwdEncryptType = $("#pwdEncryptType").val();
    if (pwdEncryptType === '1') { //Base64
      //加密反过来 解决中文符号无法解析问题
      // return escape(base64.encode(base64.encode(rawPassword)));
      return base64.encode(urlencode(rawPassword));
    } else if (pwdEncryptType === '2') { //DES
      return CryptoJS.DES.encrypt(rawPassword, CryptoJS.enc.Utf8.parse($("#pwdEncryptKey").val()), {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.Pkcs7
      }).toString()
    }
    return rawPassword;
  }

  function decryptPwd(encryptPwd, passwordEncryptKey) {
    var pwdEncryptType = $("#pwdEncryptType").val();
    if (pwdEncryptType === '1') { //Base64
      // return base64.decode(base64.decode(unescape(encryptPwd)));
      return urldecode(base64.decode(encryptPwd));
    } else if (pwdEncryptType === '2') { //DES
      return CryptoJS.DES.decrypt(encryptPwd, CryptoJS.enc.Utf8.parse(passwordEncryptKey), {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.Pkcs7
      }).toString(CryptoJS.enc.Utf8);
    }
    return encryptPwd;
  }

  //获取用户的操作系统
  function getUserOs() {
    var UserAgent = navigator.userAgent.toLowerCase();
    if (/ipad/.test(UserAgent)) {
      return "Ipad";
    }
    if (/iphone os/.test(UserAgent)) {
      return "Iphone";
    }
    if (/android/.test(UserAgent)) {
      return "Android";
    }
    if (/windows ce/.test(UserAgent)) {
      return "WindowsCe";
    }
    if (/windows mobile/.test(UserAgent)) {
      return "WindowsMobile";
    }
    if (/windows nt 5.0/.test(UserAgent)) {
      return "windows nt 5.0";
    }
    if (/windows nt 5.1/.test(UserAgent)) {
      return "windows nt 5.1";
    }
    if (/windows nt 6.0/.test(UserAgent)) {
      return "windows nt 6.0/";
    }
    if (/windows nt 6.1/.test(UserAgent)) {
      return "windows nt 6.1";
    }
    if (/windows nt 6.2/.test(UserAgent)) {
      return "windows nt 6.2";
    }
    if (/windows nt 6.3/.test(UserAgent)) {
      return "windows nt 6.3";
    }
    if (/windows nt 10.0/.test(UserAgent)) {
      return "windows nt 10.0";
    }
    if (/mac os/.test(UserAgent)) {
      return "Mac OS";
    }
    if (/windows/.test(UserAgent)) {
      return "Win";
    }
    if (/linux/.test(UserAgent)) {
      return "linux";
    }
    return UserAgent;
  }

  function _mime(option, value) {
    var mimeTypes = navigator.mimeTypes;
    for (var mt in mimeTypes) {
      if (mimeTypes[mt][option] == value) {
        return true;
      }
    }
    return false;
  }

  var is360 = _mime("type", "application/360softmgrplugin");

  //获取用户的浏览器
  function getBrowser() {
    var UserAgent = navigator.userAgent.toLowerCase();
    if ((/chrome/.test(UserAgent) && /applewebkit/.test(UserAgent)) || /compatible/.test(UserAgent)) {
      if (is360 || /compatible/.test(UserAgent)) {
        return "360浏览器";
      }
    }
    if (/ubrowser/.test(UserAgent)) {
      return "UC浏览器";
    }

    if (/firefox/.test(UserAgent)) {
      return "火狐浏览器";
    }
    if (/opera/.test(UserAgent) || /opr/.test(UserAgent)) {
      return "Opera浏览器";
    }
    if (/mac os/.test(UserAgent) && /safari/.test(UserAgent) && !/chrome/.test(UserAgent)) {
      return "safari浏览器";
    }
    if (/360se/.test(UserAgent)) {
      return "360浏览器";
    }
    if (/bidubrowser/.test(UserAgent)) {
      return "百度浏览器";
    }
    if (/metasr/.test(UserAgent)) {
      return "搜狗浏览器";
    }
    if (/msie 6.0/.test(UserAgent) || /rv:6.0/.test(UserAgent)) {
      return "IE6";
    }
    if (/msie 7.0/.test(UserAgent) || /rv:7.0/.test(UserAgent)) {
      return "IE7";
    }
    if (/msie 8.0/.test(UserAgent) || /rv:8.0/.test(UserAgent)) {
      return "IE8";
    }
    if (/msie 9.0/.test(UserAgent) || /rv:9.0/.test(UserAgent)) {
      return "IE9";
    }
    if (/msie 10.0/.test(UserAgent) || /rv:10.0/.test(UserAgent)) {
      return "IE10";
    }
    if (/msie 11.0/.test(UserAgent) || /rv:11.0/.test(UserAgent)) {
      return "IE11";
    }
    if (/lbbrowser/.test(UserAgent)) {
      return "猎豹浏览器";
    }
    if (/micromessenger/.test(UserAgent)) {
      return "微信内置浏览器";
    }
    if (/qqbrowser/.test(UserAgent)) {
      return "QQ浏览器";
    }
    if (/chrome/.test(UserAgent)) {
      return "Chrome浏览器";
    }
    if (/edg/.test(UserAgent)) {
      return "Edge浏览器";
    }
  }

  //获取用户的软件浏览器
  function getBrowserVersion() {
    var UserAgent = navigator.userAgent.toLowerCase();
    var oldAppVersion = navigator.appVersion.toLowerCase();
    var appVersion = oldAppVersion;
    if (/chrome/.test(UserAgent)) {
      appVersion = "chrome" + appVersion.split("chrome")[1];
    }
    if ((/chrome/.test(UserAgent) && /applewebkit/.test(UserAgent)) || /compatible/.test(UserAgent)) {
      if (is360 || /compatible/.test(UserAgent)) {
        return appVersion;
      }
    }
    if (/ubrowser/.test(UserAgent)) {
      appVersion = appVersion.split("ubrowser");
      if (appVersion[1] == undefined || appVersion[1].split("safari")[0].length === 0) {
        return oldAppVersion;
      }
      return appVersion[1].split("safari")[0];
    }

    if (/firefox/.test(UserAgent)) {
      appVersion = appVersion.split("firefox");
      var agentVersion = UserAgent.split("firefox")[1];

      if (agentVersion != undefined && agentVersion.length > 0) {
        return agentVersion.split("/")[1];
      }
      if (appVersion[1] == undefined || appVersion[1].length === 0) {
        return oldAppVersion;
      }
      return appVersion[1];
    }
    if (/opera/.test(UserAgent) || /opr/.test(UserAgent)) {
      appVersion = appVersion.split("opr");
      if (appVersion[1] == undefined || appVersion[1].length === 0) {
        return oldAppVersion;
      }
      return appVersion[1];
    }
    if (/mac os/.test(UserAgent) && /safari/.test(UserAgent) && !/chrome/.test(UserAgent)) {
      appVersion = appVersion.split("version");
      if (appVersion[1] == undefined || appVersion[1].split("safari")[0].length === 0) {
        return oldAppVersion;
      }
      return appVersion[1].split("safari")[0];
    }
    if (/360se/.test(UserAgent)) {
      return appVersion;
    }
    if (/bidubrowser/.test(UserAgent)) {
      return appVersion;
    }
    if (/metasr/.test(UserAgent)) {
      return appVersion;
    }
    if (/msie 6.0/.test(UserAgent) || /rv:6.0/.test(UserAgent)) {
      return appVersion;
    }
    if (/msie 7.0/.test(UserAgent) || /rv:7.0/.test(UserAgent)) {
      return appVersion;
    }
    if (/msie 8.0/.test(UserAgent) || /rv:8.0/.test(UserAgent)) {
      return appVersion;
    }
    if (/msie 9.0/.test(UserAgent) || /rv:0.0/.test(UserAgent)) {
      return appVersion;
    }
    if (/msie 10.0/.test(UserAgent) || /rv:10.0/.test(UserAgent)) {
      return appVersion;
    }
    if (/msie 11.0/.test(UserAgent) || /rv:11.0/.test(UserAgent)) {
      return appVersion;
    }
    if (/lbbrowser/.test(UserAgent)) {
      return appVersion;
    }
    if (/micromessenger/.test(UserAgent)) {
      return appVersion;
    }
    if (/qqbrowser/.test(UserAgent)) {
      appVersion = appVersion.split("qqbrowser");
      if (appVersion[1] == undefined || appVersion[1].split("safari")[0].length === 0) {
        return oldAppVersion;
      }
      return appVersion[1].split("safari")[0];
    }
    if (/chrome/.test(UserAgent)) {
      appVersion = appVersion.split("chrome/");
      if (appVersion[1] == undefined || appVersion[1].split("safari")[0].length === 0) {
        return oldAppVersion;
      }
      return appVersion[1].split("safari")[0];
    }
    if (/edg/.test(UserAgent)) {
      appVersion = appVersion.split("edg");
      if (appVersion[1] == undefined || appVersion[1].length === 0) {
        return oldAppVersion;
      }
      return appVersion[1];
    }
  }

})
