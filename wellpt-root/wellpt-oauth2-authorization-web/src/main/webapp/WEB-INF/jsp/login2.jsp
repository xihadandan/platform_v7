<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/taglibs.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>


    <title>统一认证登录</title>
    <%@ include file="/meta.jsp" %>

    <script type="application/x-javascript"> addEventListener("load", function () {
        setTimeout(hideURLbar, 0);
    }, false);

    function hideURLbar() {
        window.scrollTo(0, 1);
    } </script>
</head>

<body class="login-body">

<div class="login-form w3_form">
    <!--  Title-->
    <div class="login-title w3_title">
        <h1>统一认证登录</h1>
    </div>
    <div class="login w3_login">
        <%--<h2 class="login-header w3_header"></h2>--%>
        <div class="w3l_grid">
            <form class="login-container" action="${base}login" method="post">
                <input type="text" placeholder="用户名" Name="username" required="">
                <input type="password" placeholder="密码" Name="password" required="">
                <input type="submit" value="登录">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </form>
            <div class="second-section w3_section">
                <div class="bottom-header w3_bottom">
                    <h3>或者</h3>
                </div>
                <div class="social-links w3_social">
                    <ul>
                        <!-- github -->
                        <li><a class="facebook" href="${base}login/github" target="_self"><i
                                class="fa fa-github"></i></a></li>


                    </ul>
                </div>
            </div>

            <%--<div class="bottom-text w3_bottom_text">
                <p>没有账号?<a href="#">注册</a></p>
                <h4><a href="#">忘记密码?</a></h4>
            </div>--%>

        </div>
    </div>

    <%--<button onclick="window.frames[0].window.publishMessage('hello'+new Date().getTime(), '*')">测试发送消息</button>
    <iframe src="${base}sp-message"></iframe>--%>

</div>


</body>
</html>
