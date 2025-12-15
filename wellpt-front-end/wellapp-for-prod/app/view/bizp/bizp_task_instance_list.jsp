<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<%@ include file="/pt/common/taglibs.jsp"%>
<%@ include file="/pt/common/meta.jsp"%>
<title>BizpTaskInstance List</title>
<!--
@author zhulh
@date 2018-03-15
-->
<link rel="stylesheet" type="text/css"
	href="${ctx}/resources/jqueryui/css/base/jquery-ui.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx}/resources/jqgrid/css/ui.jqgrid.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx}/resources/layout/jquery.layout.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx}/resources/pt/css/custom-admin-style.css" />
</head>
<body>

	<div class="ui-layout-west">
		<div>
			<div class="btn-group btn-group-top">
				<div class="query-fields">
					<input id="query_bizp_task_instance" name="query_bizp_task_instance" />
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
			<form action="" id="bizp_task_instance_form">
				<div class="tabs">
					<ul>
						<li><a href="#tabs-1">基本信息</a></li>
					</ul>
					<input type="hidden" id="uuid" name="uuid" />
					<input type="hidden" id="recVer" name="recVer" />
					<div id="tabs-1">
					<table>
<tr>
<td style="width: 65px;"><label for="name">name</label></td><td><input id="name" name="name"  type="text" class="full-width" /></td><td></td></tr><tr>
<td><label for="id">id</label></td><td><input id="id" name="id"  type="text" class="full-width" /></td><td></td></tr><tr>
<td><label for="owner">owner</label></td><td><input id="owner" name="owner"  type="text" class="full-width" /></td><td></td></tr><tr>
<td><label for="startTime">startTime</label></td><td><input id="startTime" name="startTime"  type="text" class="full-width" /></td><td></td></tr><tr>
<td><label for="endTime">endTime</label></td><td><input id="endTime" name="endTime"  type="text" class="full-width" /></td><td></td></tr><tr>
<td><label for="timeLimitType">timeLimitType</label></td><td><input id="timeLimitType" name="timeLimitType"  type="text" class="full-width" /></td><td></td></tr><tr>
<td><label for="timeLimit">timeLimit</label></td><td><input id="timeLimit" name="timeLimit"  type="text" class="full-width" /></td><td></td></tr><tr>
<td><label for="currentState">currentState</label></td><td><input id="currentState" name="currentState"  type="text" class="full-width" /></td><td></td></tr><tr>
<td><label for="currentRatio">currentRatio</label></td><td><input id="currentRatio" name="currentRatio"  type="text" class="full-width" /></td><td></td></tr><tr>
<td><label for="completedState">completedState</label></td><td><input id="completedState" name="completedState"  type="text" class="full-width" /></td><td></td></tr><tr>
<td><label for="frontTaskId">frontTaskId</label></td><td><input id="frontTaskId" name="frontTaskId"  type="text" class="full-width" /></td><td></td></tr><tr>
<td><label for="timingState">timingState</label></td><td><input id="timingState" name="timingState"  type="text" class="full-width" /></td><td></td></tr><tr>
<td><label for="overDueState">overDueState</label></td><td><input id="overDueState" name="overDueState"  type="text" class="full-width" /></td><td></td></tr><tr>
<td><label for="dueTime">dueTime</label></td><td><input id="dueTime" name="dueTime"  type="text" class="full-width" /></td><td></td></tr><tr>
<td><label for="parentTaskInstUuid">parentTaskInstUuid</label></td><td><input id="parentTaskInstUuid" name="parentTaskInstUuid"  type="text" class="full-width" /></td><td></td></tr><tr>
<td><label for="proceInstUuid">proceInstUuid</label></td><td><input id="proceInstUuid" name="proceInstUuid"  type="text" class="full-width" /></td><td></td></tr><tr>
<td><label for="proceDefUuid">proceDefUuid</label></td><td><input id="proceDefUuid" name="proceDefUuid"  type="text" class="full-width" /></td><td></td></tr></table>
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
	<script src="${ctx}/resources/app/js/bizp/bizp_task_instance_list.js"></script>
</body>
</html>