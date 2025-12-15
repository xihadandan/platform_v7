(function ($) {
    var submit = true;
    if (localStorage.rememberUser) {
        $("#username").val(localStorage.username);
        $(".remember-username").addClass('check');
    }

    if (localStorage.rememberPwd) {
        $("#password").val(localStorage.password);
        $(".remember-password").addClass('check');
    }


    $("#login").on('click', function () {
        var rememberUser = $(".remember-username").hasClass('check');
        var rememberPwd = $(".remember-password").hasClass('check');
        var username = $("#username").val();
        var password = $("#password").val();
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
            localStorage.setItem("password", password);
            localStorage.setItem("rememberPwd", rememberPwd);
        } else {
            localStorage.removeItem("password");
            localStorage.removeItem("rememberPwd");
        }
        submit = true;
        $('.error-div').html('');
        if (username == '') {
            showErrorMsg('用户名不能为空！');
        }

        if (password == '') {
            showErrorMsg('密码不能为空！');
        }
        if (submit)
            $("form").submit();


    });

    $(document).on("keypress", function (e) {
        if (e.keyCode == 13) {
            $('.account button').trigger("click");
        }
    });

    $(".operating-li a").on('click', function () {
        if ($(this).hasClass('check')) {
            $(this).removeClass('check');
        } else {
            $(this).addClass('check');
        }

    });

    function showErrorMsg(msg) {
        if ($('.error-div').length > 0) {
            $('.error-div').append(msg);
        } else {
            var html = '<div class="error-div">' + msg + '</div>';
            $('.box').append(html);
        }
        submit = false;
    }

})(jQuery);