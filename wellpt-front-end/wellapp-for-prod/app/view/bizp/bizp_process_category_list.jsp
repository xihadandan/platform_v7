<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<%@ include file="/pt/common/taglibs.jsp"%>
<%@ include file="/pt/common/meta.jsp"%>
<title>BizpProcessCategory List</title>
<!--
@author zhulh
@date 2018-03-15
-->
<link rel="stylesheet" type="text/css" href="${ctx}/resources/jqueryui/css/base/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/resources/jqgrid/css/ui.jqgrid.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/resources/layout/jquery.layout.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/resources/pt/css/custom-admin-style.css" />
</head>
<body>

	<div class="ui-layout-west">
		<div>
			<div class="btn-group btn-group-top">
				<div class="query-fields">
					<input id="query_bizp_process_category" name="query_bizp_process_category" />
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
			<form action="" id="bizp_process_category_form">
				<div class="tabs">
					<ul>
						<li><a href="#tabs-1">基本信息</a></li>
					</ul>
					<input type="hidden" id="uuid" name="uuid" /> <input type="hidden" id="recVer" name="recVer" />
					<div id="tabs-1">
						<table>
							<tr>
								<td style="width: 65px;"><label for="name">名称</label></td>
								<td><input id="name" name="name" type="text" class="full-width" /></td>
								<td></td>
							</tr>
							<tr>
								<td><label for="id">ID</label></td>
								<td><input id="id" name="id" type="text" class="full-width" /></td>
								<td></td>
							</tr>
							<tr>
								<td><label for="code">编号</label></td>
								<td><input id="code" name="code" type="text" class="full-width" /></td>
								<td></td>
							</tr>
							<tr>
								<td class="align-top"><label for="remark">备注</label></td>
								<td><textarea id="remark" name="remark" class="full-width"></textarea></td>
								<td></td>
							</tr>
						</table>
					</div>
				</div>
				<div class="btn-group btn-group-bottom">
					<button id="btn_save" type="button" class="btn">保存</button>
				</div>
			</form>
		</div>
	</div>

	<!-- Project -->
	<script src="${ctx}/resources/jquery/jquery.js"></script>
	<script src="${ctx}/resources/jqgrid/js/i18n/grid.locale-cn.js"></script>
	<script src="${ctx}/resources/jqgrid/js/jquery.jqGrid.js"></script>
	<script src="${ctx}/resources/jqueryui/js/jquery-ui.js"></script>
	<script src="${ctx}/resources/layout/jquery.layout.js"></script>
	<script src="${ctx}/resources/form/jquery.form.js"></script>
	<script src="${ctx}/resources/validate/js/jquery.validate.js"></script>
	<script src="${ctx}/resources/pt/js/global.js"></script>
	<script src="${ctx}/resources/pt/js/org/unit/jquery.unit.js"></script>
	<script src="${ctx}/resources/pt/js/system_admin.js"></script>
	<script src="${ctx}/resources/bizp/bizp_process_category_list.js"></script>
</body>
</html>