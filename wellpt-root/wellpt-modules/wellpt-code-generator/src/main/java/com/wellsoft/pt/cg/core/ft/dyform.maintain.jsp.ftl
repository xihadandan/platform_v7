<%@ page language="java" contentType="text/html;charset=UTF-8"
pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <%@ include file="/pt/common/taglibs.jsp"%>
    <%@ include file="/pt/common/meta.jsp"%>
    <%@ include file="/pt/dyform/explain/dyform_css.jsp"%>
    <title id="title">${displayName}</title>
</head>
<body>
<div class="div_body">
    <div class="form_header">
        <div class="form_title">
            <h2>${displayName}</h2>
            <div title="关闭" class="form_close"></div>
        </div>
        <div id="toolbar" class="form_toolbar">
            <div class="form_operate">
                <%--
                <privilege:button authority="B001001" id="btn_save" class="btn">保存</privilege:button>
                --%>
                <button id="btn_save" name="B001001" class="btn">保存</button>
                <button id="btn_submit" name="B001002" class="btn">提交</button>
                <button id="btn_delete" name="B001003" class="btn">删除</button>
            </div>
        </div>
    </div>

    <!-- 业务数据 -->
    <form:form id="${tableName}_form" commandName="bizData" cssClass="cleanform">
        <form:hidden id="biz_formUuid" path="formUuid"></form:hidden>
        <form:hidden id="biz_dataUuid" path="dataUuid"></form:hidden>
    </form:form>

    <!-- 动态表单 -->
    <form id="dyform"></form>
</div>

<!-- Project -->
<%@ include file="/pt/dyform/dyform_js.jsp"%>
<script src="${r'$'}{ctx}/resources/pt/js/common/jquery.cmsWindown.js"></script>
<script src='${r'$'}{ctx}/resources/app/js/${moduleRequestPath!}/${jspViewName}_form.js'></script>
</body>
</html>