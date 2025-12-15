<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/taglibs.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>

    <title>编辑应用</title>
    <%@include file="/meta.jsp" %>
</head>

<body>

<div class="layuimini-container">

    <div class="container-fluid layuimini-main">

        <form class="form-horizontal" id="form" action="" method="post">
            <div class="form-group">
                <label for="uuid" class="col-sm-2 control-label">登录账号</label>
                <div class="col-sm-10">
                    <p class="form-control-static">${entity.accountNumber}</p>
                    <input type="hidden" id="uuid" name="uuid" value="${entity.uuid}">
                </div>
            </div>


            <div class="form-group">
                <label for="userName" class="col-sm-2 control-label">用户姓名</label>
                <div class="col-sm-10">
                    <input type="text" class="form-control" id="userName" name="userName" value="${entity.userName}"
                           required>
                </div>
            </div>

            <div class="form-group">
                <label for="cellphoneNumber" class="col-sm-2 control-label">手机号码</label>
                <div class="col-sm-10">
                    <input type="text" class="form-control" id="cellphoneNumber" name="cellphoneNumber"
                           value="${entity.cellphoneNumber}">
                </div>
            </div>

            <div class="row" style=" text-align: center; ">
                <input type="submit" id="submit" class="${allowSave?'layui-btn layui-btn-sm':'hide'}">
            </div>

        </form>

    </div>
</div>

</body>
<%@ include file="/commonJs.jsp" %>
<script type="text/javascript" src="${base}assets/js/user/user-new.js"></script>

</html>
