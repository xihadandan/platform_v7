(function ($) {

    var index = {

        initDocumentEvent: function () {

            $('.login-out').on("click", function () {
                $.post(WEB_ROOT + "logout", {}, function () {
                    window.location = WEB_ROOT;
                });

            });

            $("#modifyPassword").on('click', function () {

                layer.open({
                    type: 1,
                    title: '修改密码',
                    closeBtn: 1,
                    shadeClose: true,
                    skin: 'layui-layer-molv',
                    area: ['300px', 'auto'],
                    content: '<div class="container-fluid" style="margin-top: 10px;"><form class="form-horizontal">\n' +
                        '  <div class="form-group">\n' +
                        '    <div class="col-sm-12">\n' +
                        '      <input type="password" class="form-control" id="oldPassword" name="oldPassword" placeholder="请输入旧密码">\n' +
                        '    </div>\n' +
                        '  </div>\n' +
                        '  <div class="form-group">\n' +
                        '    <div class="col-sm-12">\n' +
                        '      <input type="password" class="form-control" id="newPassword"  placeholder="请输入新密码">\n' +
                        '    </div>\n' +
                        '  </div>\n' +
                        '</form></div>',
                    btn: ['确定'], yes: function (i, layero) {
                        $.post(WEB_ROOT + "user/password"
                            , {
                                _old: $(layero).find('#oldPassword').val(),
                                _new: $(layero).find('#newPassword').val()
                            },
                            function (res) {
                                if (res && res.code == 0) {
                                    layer.msg('修改密码成功');
                                    layer.close(i);
                                } else {
                                    layer.msg(res && res.code != 0 ? res.msg : '修改密码失败');
                                }
                            });
                        return false;
                    }
                });


            });
        },

        init: function () {
            this.initDocumentEvent();
        }
    };


    index.init();


})(jQuery);