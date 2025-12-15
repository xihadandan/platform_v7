define(["jquery", "commons", "constant", "server", "appModal"], function ($, commons, constant, server, appModal) {

    var ModifyPwd = function () {

        var dialog = appModal.dialog({
            title: "邮件客户端密码",
            size: "small",
            message: (function () {

                return '<form class="form-horizontal">\n' +
                    '  <div class="form-group">\n' +
                    '    <span for="systemLoginPwdInput" class="col-sm-4 control-label">系统登录密码</span>\n' +
                    '    <div class="col-sm-8">\n' +
                    '      <input type="password" class="form-control" id="systemLoginPwdInput">\n' +
                    '    </div>\n' +
                    '  </div>\n' +
                    '  <div class="form-group">\n' +
                    '    <span for="mailClientPwdInput" class="col-sm-4 control-label">客户端新密码</span>\n' +
                    '    <div class="col-sm-8">\n' +
                    '      <input type="password" class="form-control" id="mailClientPwdInput">\n' +
                    '    </div>\n' +
                    '  </div>\n' +
                    '  <div class="form-group">\n' +
                    '    <span for="mailClientPwdAgainInput" class="col-sm-4 control-label">确认客户端新密码</span>\n' +
                    '    <div class="col-sm-8">\n' +
                    '      <input type="password" class="form-control" id="mailClientPwdAgainInput">\n' +
                    '    </div>\n' +
                    '  </div>\n' +
                    '</form>';
            })(),
            shown: function () {
            },
            buttons: {
                confirm: {
                    label: "保存",
                    className: "btn-primary",
                    callback: function () {
                        var f = function () {
                            var sysLoginPwd = dialog.find('#systemLoginPwdInput').val();
                            if (!sysLoginPwd) {
                                appModal.toast('请输入系统登录密码');
                                dialog.find('#systemLoginPwdInput').focus();
                                return false;
                            }
                            var mailClientPwd = dialog.find('#mailClientPwdInput').val();
                            if (!mailClientPwd) {
                                appModal.toast('请输入客户端新密码');
                                dialog.find('#mailClientPwdInput').focus();
                                return false;
                            }
                            var mailClientPwdAgain = dialog.find('#mailClientPwdAgainInput').val();
                            if (!mailClientPwdAgain) {
                                appModal.toast('请输入确认客户端新密码');
                                dialog.find('#mailClientPwdAgainInput').focus();
                                return false;
                            }

                            if (mailClientPwd != mailClientPwdAgain) {
                                appModal.toast('确认客户端新密码不一致，请重新输入');
                                dialog.find('#mailClientPwdAgainInput').focus();
                                return false;
                            }

                            server.JDS.call({
                                service: 'securityApiFacade.checkCurrentUserPassword',
                                data: sysLoginPwd,
                                version: '',
                                success: function (res) {
                                    if (!res.data) {
                                        appModal.toast({
                                            message: "系统登录密码错误或者错误次数太多",
                                            type: "error",
                                            timer: 3000
                                        });
                                    } else {
                                        server.JDS.call({
                                            service: 'wmMailUserService.updateMailUserPassword',
                                            data: [server.SpringSecurityUtils.getCurrentUserId(), mailClientPwdAgain],
                                            version: '',
                                            success: function (result) {
                                                if (result.data == 1) {
                                                    dialog.modal('hide');
                                                    appModal.success({
                                                        message: "成功提示",
                                                        timer: 3000
                                                    })
                                                } else {
                                                    appModal.toast({
                                                        message: "保存失败",
                                                        type: "error",
                                                        timer: 5000
                                                    })
                                                }
                                            }
                                        });
                                    }
                                }
                            });

                        };
                        _.debounce(f, 500, {
                            'leading': true,
                            'trailing': false
                        })();
                        return false;
                    }
                },
                cancel: {
                    label: "取消",
                    className: "btn-default",
                    callback: function () {
                    }
                }
            }
        })

    };


    return ModifyPwd;
});