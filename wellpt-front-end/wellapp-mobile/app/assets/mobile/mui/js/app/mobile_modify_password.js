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
  var modifyUserPassword = function (option) {
    var StringUtils = commons.StringUtils;
    $.ajax({
      url: ctx + '/mobile/mui/modifyPassword.html',
      async: false,
      success: function (data) {
        var wrapper = document.createElement('div');
        wrapper.id = 'mobile_modifyPassword';
        var pageContainer = appContext.getPageContainer();
        var renderPlaceholder = pageContainer.getRenderPlaceholder();
        renderPlaceholder[0].appendChild(wrapper);
        formBuilder.buildPanel({
          title: '修改密码',
          content: data.toString(),
          container: '#mobile_modifyPassword'
        });
        $.ui.loadContent('#mobile_modifyPassword');
        mui('#mobile_modifyPassword .mui-bar')[0].style.cssText =
          'position: fixed!important;background: #fff;color: #000;box-shadow: none;border-bottom: 1px solid #ccc;';
        ('position: fixed!important;background: #fff;color: #000;box-shadow: none;border-bottom: 1px solid #ccc;');
        mui('#mobile_modifyPassword .mui-title')[0].style.cssText = ' color: #000; font-weight: bold;';
        mui('#mobile_modifyPassword .mui-action-back')[0].style.cssText = ' color: #666';
        mui('#mobile_modifyPassword .mui-action-back').append('<span style="font-size: 17px;">返回</span>');
        mui('#mobile_modifyPassword .mui-bar').append(
          '<button type="button" class="mui-btn btn-modify-confirm" style="color: #0089ff;position: absolute;border: none;right: 5px;top: 4px;font-size: 17px;">确定</button>'
        );
        $('.mui-bar-nav').on('tap', '.btn-modify-confirm', function () {
          var userUuid = $("#mobile_modifyPassword input[name='userUuid']")[0].value;
          var oldPwd = $("#mobile_modifyPassword input[name='oldPwd']")[0].value;
          var newPwd = $("#mobile_modifyPassword input[name='newPwd']")[0].value;
          var confirmPwd = $("#mobile_modifyPassword input[name='confirmPwd']")[0].value;
          if (StringUtils.isBlank(oldPwd)) {
            $.alert('请输入旧密码');
            return false;
          } else if (StringUtils.isBlank(newPwd)) {
            $.alert('请输入新密码');
            return false;
          } else if (oldPwd == newPwd) {
            $.alert('新密码不能和旧密码一样');
            return false;
          } else if (newPwd != confirmPwd) {
            $.alert('输入的两次新密码不一致');
            return false;
          } else if (newPwd.length < 6) {
            $.alert('输入的新密码必须为6位至20位');
            return false;
          } else if (newPwd.length > 20) {
            $.alert('输入的新密码长度过长');
            return false;
          }
          JDS.call({
            service: 'personalInfoService.modifyCurrentUserPassword',
            async: false,
            data: [oldPwd, newPwd],
            success: function (result) {
              if (result.data == 'success') {
                mui.ui.goBack();
                setTimeout(function () {
                  $.toast('修改成功');
                }, 100);
              } else {
                $.toast(result.data);
              }
            },
            error: function () {
              $.toast('未知错误');
            }
          });
        });
      }
    });

    var JDS = server.JDS;
    JDS.call({
      service: 'personalInfoService.getUserInfo',
      async: false,
      data: [''],
      success: function (result) {
        var data = result.data;
        $("#mobile_modifyPassword input[name='userUuid']")[0].value = data.uuid;
      }
    });
  };

  return modifyUserPassword;
});
