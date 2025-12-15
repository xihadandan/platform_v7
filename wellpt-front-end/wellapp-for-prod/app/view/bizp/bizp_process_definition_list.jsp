<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<%@ include file="/pt/common/taglibs.jsp"%>
<%@ include file="/pt/common/meta.jsp"%>
<title>BizpProcessDefinition List</title>
<!--
@author zhulh
@date 2018-03-15
-->
<link rel="stylesheet" type="text/css" href="${ctx}/resources/jqueryui/css/base/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/resources/jqgrid/css/ui.jqgrid.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/resources/layout/jquery.layout.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/resources/ztree/css/zTreeStyle/zTreeStyle.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/resources/pt/css/custom-admin-style.css" />
</head>
<body>

	<div class="ui-layout-west">
		<div>
			<div class="btn-group btn-group-top"></div>
			<ul id="bizp_process_category_tree" class="ztree"></ul>
		</div>
	</div>
	<div class="ui-layout-center">
		<div>
			<div class="btn-group btn-group-top">
				<div class="query-fields">
					<input id="query_bizp_process_definition" name="query_bizp_process_definition" />
					<button id="btn_query" type="button" class="btn">查询</button>
				</div>
				<button id="btn_add" type="button" class="btn">新 增</button>
				<button id="btn_edit" type="button" class="btn">编辑</button>
				<button id="btn_del" type="button" class="btn">删除</button>
			</div>
			<table id="list"></table>
			<div id="pager"></div>
		</div>
	</div>

	<!-- Project -->
	<script src="${ctx}/resources/jquery/jquery.js"></script>
	<script src="${ctx}/resources/jqgrid/js/i18n/grid.locale-cn.js"></script>
	<script src="${ctx}/resources/jqgrid/js/jquery.jqGrid.js"></script>
	<script src="${ctx}/resources/jqueryui/js/jquery-ui.js"></script>
	<script src="${ctx}/resources/layout/jquery.layout.js"></script>
	<script src="${ctx}/resources/form/jquery.form.js"></script>
	<script src="${ctx}/resources/ztree/js/jquery.ztree.js"></script>
	<script src="${ctx}/resources/ztree/js/jquery.ztree.exhide-3.5.js"></script>
	<script src="${ctx}/resources/validate/js/jquery.validate.js"></script>
	<script src="${ctx}/resources/pt/js/global.js"></script>
	<script src="${ctx}/resources/pt/js/org/unit/jquery.unit.js"></script>
	<script src="${ctx}/resources/pt/js/system_admin.js"></script>
	<script src="${ctx}/resources/bizp/bizp_process_definition_list.js"></script>
</body>
</html>