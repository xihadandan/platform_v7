define([ "jquery", "commons", "constant", "server", "appContext", "ViewDevelopmentBase", "DmsListViewActionBase",
		"DmsDataServices", "DmsDataManagementViewDevelopment" ], function($, commons, constant, server, appContext,
		ViewDevelopmentBase, DmsListViewActionBase, DmsDataServices, DmsDataManagementViewDevelopment) {
	var StringUtils = commons.StringUtils;
	var SpringSecurityUtils = server.SpringSecurityUtils;
	// 公告管理者角色
	var ROLE_PT_APP_NOTICE_ADMIN = "ROLE_PT_APP_NOTICE_ADMIN";
	// 数据管理——视图二开
	var AppNoticeDmsDataManagementViewDevelopment = function() {
		DmsDataManagementViewDevelopment.apply(this, arguments);
	};
	commons.inherit(AppNoticeDmsDataManagementViewDevelopment, DmsDataManagementViewDevelopment, {
		beforeRender : function() {
			var _self = this;
			var userDetails = SpringSecurityUtils.getUserDetails();
			// 添加发布对象关联当前用户相关ID的查询
			var conditions = [];
			var userOrgIdsCondition = {
				columnIndex : "NOTICE_OBJECT",
				value : userDetails.userId,
				type : "like",
				customCriterion : "userOrgIdsCriterion"
			};
			// 添加创建者关联当前用户ID的查询
			var creatorCondition = {
				columnIndex : "CREATOR",
				value : userDetails.userId,
				type : "eq"
			};
			conditions.push(userOrgIdsCondition);
			conditions.push(creatorCondition);

			// OR关联查询
			var orCondition = {
				type : "or",
				conditions : conditions
			};
			var otherConditions = [];
			otherConditions.push(orCondition);

			// 添加视图查询条件
			var listViewWidget = _self.getWidget();
			// 当前用户没有管理员角色，添加条件过滤
			if (SpringSecurityUtils.hasRole(ROLE_PT_APP_NOTICE_ADMIN) == false) {
				listViewWidget.addOtherConditions(otherConditions);
			}

			// 调用父类提交方法
			_self._superApply(arguments);
		}
	});

	return AppNoticeDmsDataManagementViewDevelopment;
});