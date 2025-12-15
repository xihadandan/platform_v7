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

        <form class="form-horizontal" action="" method="post" id="form">
            <div class="form-group">
                <label for="clientName" class="col-sm-2 control-label">应用ID</label>
                <div class="col-sm-10">
                    <input type="text" class="form-control" value="${entity.clientId}" readonly>
                    <input type="hidden" value="${entity.uuid}" id="uuid" name="uuid" required>
                </div>
            </div>

            <div class="form-group">
                <label for="clientName" class="col-sm-2 control-label">应用秘钥</label>
                <div class="col-sm-10">
                    <input type="text" class="form-control" value="${entity.clientSecret}" readonly>
                </div>
            </div>


            <div class="form-group">
                <label for="clientName" class="col-sm-2 control-label">应用名称</label>
                <div class="col-sm-10">
                    <input type="text" class="form-control" id="clientName" name="clientName"
                           value="${entity.clientName}" required>
                </div>
            </div>

            <%--<div class="form-group">
                <label for="additionalInformation" class="col-sm-2 control-label">应用描述</label>
                <div class="col-sm-10">
                    <textarea class="form-control" id="additionalInformation" name="additionalInformation">${entity.additionalInformation}</textarea>
                </div>
            </div>--%>


            <div class="form-group">
                <label for="webServerRedirectUri" class="col-sm-2 control-label">回调地址</label>
                <div class="col-sm-10">
                    <input type="text" class="form-control" id="webServerRedirectUri" name="webServerRedirectUri"
                           value="${entity.webServerRedirectUri}">
                </div>
            </div>

            <div class="form-group">
                <label for="loginPage" class="col-sm-2 control-label">登录页</label>
                <div class="col-sm-10">
                    <input type="text" class="form-control" id="loginPage" name="loginPage" value="${entity.loginPage}">
                </div>
            </div>

            <input type="submit" id="submit" class="hide"/>
        </form>

    </div>
</div>

</body>
<%@ include file="/commonJs.jsp" %>
<script type="text/javascript" src="${base}assets/js/client/client-new.js"></script>

</html>
