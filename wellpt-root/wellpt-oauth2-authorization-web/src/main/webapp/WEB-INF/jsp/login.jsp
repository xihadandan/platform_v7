<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/taglibs.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>


    <title>统一认证登录</title>
    <link rel="stylesheet" href="${base}assets/3rd-libs/font-awesome-4.7.0/css/font-awesome.min.css" media="all">
    <link rel="stylesheet" href="${base}assets/styles/css/login.css">
    <script type="application/x-javascript"> addEventListener("load", function () {
        setTimeout(hideURLbar, 0);
    }, false);

    function hideURLbar() {
        window.scrollTo(0, 1);
    } </script>
</head>

<body>

<div class="content">
    <div class="box">
        <div class="header">
            <span>统一认证服务</span>
        </div>
        <div class="login">
            <div class="account">
                <form action="${base}login" method="post">
                    <ul>
                        <li class="username-li select">
                            <input type="text" placeholder="用户名" name="username" id="username" autofocus="autofocus">
                        </li>
                        <li class="password-li">
                            <input type="password" id="password" name="password" placeholder="请输入密码">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        </li>
                        <li class="button-li">
                            <button type="button" id="login">登录</button>
                        </li>
                        <%--<li class="button-li">
                            <button onclick="window.location.href='${base}login/github'"><i class="fa fa-github"></i></button>
                        </li>--%>
                        <li class="operating-li">
                            <a class="remember-username">记住账号</a>
                            <a class="remember-password">记住密码</a>
                            <%--<a class="register">注册</a>
                            <a class="forget-password">忘记密码？</a>--%>
                        </li>

                        <li class="other-login-types-li">
                            <a>其他登录方式：</a>
                            <span onclick="window.location.href='${base}login/github'" title="使用github账号登录"><i
                                    class="fa fa-github"></i></span>

                            <span class="wechat" onclick="window.location.href='${base}login/wechat'"
                                  title="使用微信账号登录"><i class="fa fa-wechat"></i></span>
                        </li>
                    </ul>
                </form>
            </div>


        </div>

        <div class="error-div">
        </div>

    </div>
    <div class="footer"></div>
</div>
<%@ include file="/commonJs.jsp" %>
<script type="text/javascript" src="${base}assets/js/login.js"></script>

</body>
</html>
