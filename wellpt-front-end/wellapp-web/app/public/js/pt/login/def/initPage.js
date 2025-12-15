$(document).ready(function () {
  var width = $(window).width();
  var height = $(window).height();

  var boxWidth = $('.box').width() + 16; //加上padding
  var boxHeight = $('.box').height() + 22;

  var left = (width - boxWidth) / 2;
  var top = (height - boxHeight) / 2;

  $('.box').css('top', top).css('left', left);
  if (left < 360) {
    $('.footer').css('left', left);
  }
  window.onresize = function () {
    var width = $(window).width();
    var height = $(window).height();

    var boxWidth = $('.box').width() + 16; //加上padding
    var boxHeight = $('.box').height() + 22;

    var left = (width - boxWidth) / 2 < 0 ? 0 : (width - boxWidth) / 2;
    var top = (height - boxHeight) / 2 < 0 ? 0 : (height - boxHeight) / 2;

    $('.box').css('top', top).css('left', left);
    if (left > 360) {
      $('.footer').css('left', 360);
    } else {
      $('.footer').css('left', left);
    }
  };

  $('.password-li > a').on('click', function () {
    var type = $(this).prev().attr('type');
    var value = $(this).prev().val();
    if (type == 'password') {
      $(this).prev().replaceWith('<input type="text" id="password" placeholder="请输入密码" value = "' + value + '">');
      $(this).addClass('aA');
    } else {
      $(this).prev().replaceWith('<input type="password" id="password" placeholder="请输入密码" value = "' + value + '">');
      $(this).removeClass('aA');
    }
    $('.password-li > input').on('click', function () {
      inputFocus(this);
    });
  });

  $('.operating-li > .remember-username').on('click', function () {
    if ($(this).hasClass('check')) {
      $(this).removeClass('check');

    } else {
      $(this).addClass('check');
    }
  });

  $('.operating-li > .remember-password').on('click', function () {
    if ($(this).hasClass('check')) {
      $(this).removeClass('check');
    } else {
      $(this).addClass('check');
    }
  });

  $('.account input[type="text"], .account input[type="password"], .key input[type="password"]').on('click', function () {
    inputFocus(this);
  });

  $('.nav li').on('click', function () {

    var target = $(this).attr('target');
    var loginSource = $(this).data("source");
    $("#loginSource").val(loginSource)
    $('.nav li').each(function () {
      $(this).find('span').removeClass('spanA');
    });
    $(this).find('span').addClass('spanA');
    $('.login > div').hide();
    $('.' + target).show();
    $('.error-div').remove();
    if (target == "ding") {
      dingLogin();
    }
  });
})

function dingLogin() {
  // "/mobile/pt/dingtalk//getconnect/oauth2?uri="+encodeURIComponent("/passport/user/login/success")
  var GOTO_URL = ctx + "/mobile/pt/dingtalk/getconnect/oauth2?uri=" + encodeURIComponent("/passport/user/login/success");
  $.getScript("https://g.alicdn.com/dingding/dinglogin/0.0.5/ddLogin.js", function (success, statusText, jqXHR) {
    $.get(GOTO_URL, function (gotoUri) {
      console.log("gotoUri", gotoUri);
      var obj = DDLogin({
        id: "dingCode", // 这里需要你在自己的页面定义一个HTML标签并设置id
        "goto": encodeURIComponent(gotoUri), // 请参考注释里的方式:encodeURIComponent
        style: $("#dingCodeStyle").val(),
        width: $("#dingCodeWidth").val(),
        height: $("#dingCodeHeight").val()
      });

      var handleMessage = function (event) {
        var origin = event.origin;
        console.log("origin", event.origin);
        if (origin == "https://login.dingtalk.com") { // 判断是否来自ddLogin扫码事件。
          var loginTmpCode = event.data;
          // 获取到loginTmpCode后就可以在这里构造跳转链接进行跳转了
          console.log("loginTmpCode", loginTmpCode);
          $.get(GOTO_URL, {
            loginTmpCode: loginTmpCode
          }, function (jumpUri) {
            console.log("jumpUri", jumpUri);
            location.href = jumpUri;
          });
        }
      };
      if (typeof window.addEventListener != 'undefined') {
        window.addEventListener('message', handleMessage, false);
      } else if (typeof window.attachEvent != 'undefined') {
        window.attachEvent('onmessage', handleMessage);
      }
    })
  });
}

function inputFocus(target) {
  $('.account input[type="text"], .account input[type="password"], .key input[type="password"]').each(function () {
    $(this).parent().removeClass('select');
  })
  var $li = $(target).parent();
  $li.addClass('select');
}
