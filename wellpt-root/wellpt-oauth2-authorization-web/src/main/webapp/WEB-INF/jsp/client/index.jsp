<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/taglibs.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>

    <title>应用管理</title>
    <%@include file="/meta.jsp" %>
</head>

<body>
<div class="layuimini-container">
    <div class="container-fluid layuimini-main">
        <div class="row">
            <div id="toolbar">
                <div class="form-inline" role="form">
                    <button id="add" type="button" class="layui-btn layui-btn-sm">新增</button>
                    <button id="del" type="button" class="layui-btn layui-btn-danger layui-btn-sm">删除</button>
                </div>
            </div>
            <table id="table"
                   class="table"
                   data-toggle="table"
                   data-toolbar="#toolbar"
                   data-height="660"
                   data-url="${base}client/pageData"
                   data-pagination="true"
                   data-side-pagination="server"
                   data-total-field="total"
                   data-page-size="25"
                   data-data-field="items"
                   data-search="true"
                   data-show-search-button="true"
                   data-click-to-select="true"
                   data-id-field="uuid">
                <thead>
                <tr>
                    <th data-checkbox="true"></th>
                    <th data-field="uuid" data-visible="false">uuid</th>
                    <th data-field="clientName">应用名称</th>
                    <th data-field="clientId">应用ID</th>
                    <th data-field="operation" data-width="100" data-formatter="lineButtons">操作</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>
</body>
<%@ include file="/commonJs.jsp" %>
<script type="text/javascript" src="${base}assets/js/client/client-index.js"></script>

</html>
