<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<%@ include file="/pt/common/taglibs.jsp"%>
<%@ include file="/pt/common/meta.jsp"%>
<title>业务流程设计器</title>
<style type="text/css">
#canvas_container {
	width: 100%;
	border: 1px solid #aaa;
}
</style>
<app:cssFile id="BizpProcessDesigner" extraModules="select2,jquery-ui,animate" />
<app:configScript id="BizpProcessDesigner"
	extraModules="server,appModal,Raphael,jquery-ui-contextmenu,jquery-validate,formBuilder,multiOrg,design_commons" />
</head>
<body class="bizp-designer">
	<div class="navbar navbar-default navbar-fixed-top">
		<div class="container-fluid">
			<div class="navbar-header">
				<a class="navbar-brand" href="#">业务流程</a>
			</div>
			<div class="collapse navbar-collapse">
				<ul class="nav navbar-nav navbar-right">
					<li><div class="btn-group">
							<a id="btn_save" class="btn btn-info" href="#">保存</a> <a id="btn_save_as_new_version" class="btn btn-info"
								href="#">保存为新版本</a>
						</div></li>
				</ul>
			</div>
		</div>
	</div>
	<input id="bizp_proc_def_uuid" value="${procDefUuid}" type="hidden" />
	<div class="container-fluid">
		<div div="row-fluid">
			<!-- 左边导航 -->
			<div class="designer-sidebar-nav bizp-designer-toolbar">
				<div class="panel panel-default">
					<div class="panel-body">
						<ul class="list-group">
							<li class="list-group-item" type="">选择</li>
							<li class="list-group-item" type="1">开始</li>
							<li class="list-group-item" type="2">过程</li>
							<li class="list-group-item" type="3">任务</li>
							<li class="list-group-item" type="4">连接</li>
							<li class="list-group-item" type="9">结束</li>
						</ul>
					</div>
				</div>
			</div>

			<!-- 页面容器 -->
			<div id="canvas_container" class="bizp-designer-container container-fluid"></div>

			<!-- 详细页内容 -->
			<div class="right-sidebar-open right-sidebar-dakai"></div>
			<div class="designer-right-sidebar animated sidebar-open">
				<div class="right-sidebar-close guanbi"></div>
				<div class="sidebar-container">
					<div class="panel"></div>
				</div>
			</div>
		</div>
	</div>
</body>
<app:requireScript requireModules="jquery-ui-contextmenu,jquery-validate,design_commons,multiOrg,BizpProcessDesigner"
	dataMain="${ctx}/resources/bizp/bizp_process_definition_designer.js" />
</html>