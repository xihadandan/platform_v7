<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/taglibs.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>

    <title>新增应用</title>
    <%@include file="/meta.jsp" %>
</head>

<body>

<div class="layuimini-container">

    <div class="container-fluid layuimini-main">

        <form class="form-horizontal" id="form" action="" method="post">
            <div class="form-group">
                <label for="accountNumber" class="col-sm-2 control-label">登录账号</label>
                <div class="col-sm-10">
                    <input type="text" class="form-control" id="accountNumber" name="accountNumber" required>
                    <input type="hidden" id="uuid" name="uuid">
                </div>
            </div>

            <div class="form-group">
                <label for="password" class="col-sm-2 control-label">登录密码</label>
                <div class="col-sm-10">
                    <input type="password" class="form-control" id="password" name="password" required>
                </div>
            </div>

            <div class="form-group">
                <label for="cpassword" class="col-sm-2 control-label">确认密码</label>
                <div class="col-sm-10">
                    <input type="password" class="form-control" id="cpassword" name="cpassword" required>
                </div>
            </div>

            <div class="form-group">
                <label for="userName" class="col-sm-2 control-label">用户姓名</label>
                <div class="col-sm-10">
                    <input type="text" class="form-control" id="userName" name="userName" required>
                </div>
            </div>

            <div class="form-group">
                <label for="cellphoneNumber" class="col-sm-2 control-label">手机号码</label>
                <div class="col-sm-10">
                    <input type="text" class="form-control" id="cellphoneNumber" name="cellphoneNumber">
                </div>
            </div>

            <input type="submit" id="submit" class="hide"/>
        </form>

    </div>
</div>

</body>
<%@ include file="/commonJs.jsp" %>
<script type="text/javascript" src="${base}assets/js/user/user-new.js"></script>

</html>
