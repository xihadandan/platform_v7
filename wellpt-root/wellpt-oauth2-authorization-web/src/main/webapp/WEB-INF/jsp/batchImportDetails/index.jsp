<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/taglibs.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>

    <title>用户管理</title>
    <%@include file="/meta.jsp" %>
</head>

<body>
<div class="layuimini-container">
    <div class="container-fluid layuimini-main">
        <div class="row">
            <input type="hidden" id="batchDataImportUuid" value="${batchDataImportUuid}">
            <div id="toolbar">
                <select class="form-control" id="detailsStatusSelect">
                    <option value=""></option>
                    <option value="1" <c:if test="${status == '1'}">selected="selected" </c:if>}>成功</option>
                    <option value="0" <c:if test="${status == '0'}">selected="selected" </c:if>}>失败</option>
                </select>
            </div>
            <table id="table"
                   class="table"
                   data-toggle="table"
                   data-toolbar="#toolbar"
                   data-height="660"
                   data-url="${base}batchImportDetails/pageData"
                   data-pagination="true"
                   data-side-pagination="server"
                   data-total-field="total"
                   data-page-size="50"
                   data-data-field="items"
                   data-search="false"
                   data-show-search-button="true"
                   data-click-to-select="true"
                   data-id-field="uuid">
                <thead>
                <tr>
                    <th data-field="uuid" data-visible="false">uuid</th>
                    <th data-field="row" data-width="100">行号</th>
                    <th data-field="status" data-formatter="statusFormatter" data-width="100">状态</th>
                    <th data-field="importData" data-width="400">导入数据</th>
                    <th data-field="errorMsg">错误信息</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>
</body>
<%@ include file="/commonJs.jsp" %>
<script>
    function statusFormatter(value, row) {
        return value == 1 ? '成功' : '失败';
    }


</script>
<script type="text/javascript" src="${base}assets/js/batchimportdetails/batchimportdetails-index.js"></script>

</html>
