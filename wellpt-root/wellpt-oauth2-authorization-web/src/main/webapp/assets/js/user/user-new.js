(function ($) {


    var user = {

        initDomcumentEvent: function () {
        },

        init: function () {
            this.initDomcumentEvent();
        }
    };

    var validator = $("#form").validate({
        onkeyup: false,
        errorPlacement: function (error, element) {
            layer.tips(error.text(), element, {
                tips: 3, tipsMore: true
            });
        },
        submitHandler: function () { //提交函数
            var is = validator.form();
            var isEdit = !!$("#uuid").val();
            if (is) {
                $.ajax({
                    url: WEB_ROOT + "user" + (isEdit ? "/update" : "/addAccount"),
                    dataType: 'json',
                    data: JSON.stringify($("#form").getFormJson()),
                    type: 'POST',
                    contentType: 'application/json',
                    success: function (res) {
                        if (res && res.code == -1) {
                            top.layer.alert('保存失败', {icon: 2});
                            console.error(res.msg);
                            throw new Error('保存用户异常');
                        }

                        if (res && res.data) {
                            window.location.href = WEB_ROOT + "user/" + (window.location.href.indexOf('editByAccoutNumber') == -1 ? res.data.uuid + "/edit" : res.data.accountNumber + "/editByAccoutNumber");
                            top.layer.alert('保存成功', {icon: 1});
                        } else {
                            top.layer.alert('保存失败', {icon: 2});
                            throw new Error('保存用户异常');
                        }
                    },
                    async: false
                });
            }

        }
    });


    user.init();


})(jQuery);