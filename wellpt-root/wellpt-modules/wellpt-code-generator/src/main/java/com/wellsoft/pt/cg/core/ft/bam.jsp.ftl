<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/pt/common/taglibs.jsp"%>
    <%@ include file="/pt/common/meta.jsp"%>
    <title>${entity} List</title>
    <!--
@author ${author}
@date ${createDate}
-->
    <link rel="stylesheet" type="text/css"
          href="${r'${ctx}'}/resources/jqueryui/css/base/jquery-ui.css"/>
    <link rel="stylesheet" type="text/css"
          href="${r'${ctx}'}/resources/jqgrid/css/ui.jqgrid.css"/>
    <link rel="stylesheet" type="text/css"
          href="${r'${ctx}'}/resources/layout/jquery.layout.css"/>
    <link rel="stylesheet" type="text/css"
          href="${r'${ctx}'}/resources/pt/css/custom-admin-style.css"/>
</head>
<body>

<div class="ui-layout-west">
    <div>
        <div class="btn-group btn-group-top">
            <div class="query-fields">
                <input id="query_${tableName}" name="query_${tableName}"/>
                <button id="btn_query" type="button" class="btn">查询</button>
            </div>
            <button id="btn_add" type="button" class="btn">新 增</button>
            <button id="btn_del_all" type="button" class="btn">删除</button>
        </div>
        <table id="list"></table>
        <div id="pager"></div>
    </div>
</div>
<div class="ui-layout-center">
    <div>
        <form action="" id="${tableName}_form">
            <div class="tabs">
                <ul>
                    <li><a href="#tabs-1">基本信息</a></li>
                </ul>
                <input type="hidden" id="uuid" name="uuid"/>
                <input type="hidden" id="recVer" name="recVer"/>
                <div id="tabs-1">
                    ${generateTable(props)}
                </div>
            </div>
            <div class="btn-group btn-group-bottom">
                <button id="btn_save" type="button" class="btn">保存</button>
                <button id="btn_del" type="button" class="btn">删除</button>
            </div>
        </form>
    </div>
</div>

<!-- Project -->
<script src="${r'${ctx}'}/resources/jquery/jquery.js"></script>
<script src="${r'${ctx}'}/resources/jqgrid/js/i18n/grid.locale-cn.js"></script>
<script src="${r'${ctx}'}/resources/jqgrid/js/jquery.jqGrid.js"></script>
<script src="${r'${ctx}'}/resources/jqueryui/js/jquery-ui.js"></script>
<script src="${r'${ctx}'}/resources/layout/jquery.layout.js"></script>
<script src="${r'${ctx}'}/resources/form/jquery.form.js"></script>
<script src="${r'${ctx}'}/resources/validate/js/jquery.validate.js"></script>
<script src="${r'${ctx}'}/resources/pt/js/global.js"></script>
<script src="${r'${ctx}'}/resources/pt/js/org/unit/jquery.unit.js"></script>
<script src="${r'${ctx}'}/resources/pt/js/system_admin.js"></script>
<script src="${r'${ctx}'}/resources/app/js/${moduleRequestPath!}/${jspViewName}_list.js"></script>
</body>
</html>