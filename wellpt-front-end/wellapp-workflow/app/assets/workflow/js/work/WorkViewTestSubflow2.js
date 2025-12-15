define(['constant', 'commons', 'appContext', 'appModal', 'WorkView'], function(constant, commons, appContext, appModal, WorkView) {
	// 流程二开测试子流程分发人员选择组织选择框
	var WorkViewTestSubflow2 = function() { };
	commons.inherit(WorkViewTestSubflow2, WorkView, {
		// 获取主办或协办人员组织选项配置信息
		getMajorOrMinorUserUnitOptions: function() {
			return this._getCustomizeUnitOptions("do_leader_user");
		},
		// 获取主办人员组织选项配置信息
		getMajorUserUnitOptions: function() {
			return this._getCustomizeUnitOptions("do_main_user");
		},
		// 获取协办人员组织选项配置信息
		getMinorUserUnitOptions: function() {
			return this._getCustomizeUnitOptions("do_assist_user");
		},
		// 获取自定义的表单字段组织选择项配置信息
		_getCustomizeUnitOptions: function(fieldName) {
			var _self = this;
			if (StringUtils.isBlank(fieldName)) {
				return null;
			}
			var dyform = _self.getDyform();
			var field = dyform.getField(fieldName);
			if(field == null) {
				// console.error("表单字段不存在：" + fieldName)
				return null;
			}
			var orgOptions = field.getOptions();
			var excludeValues = [];
			var otherParams = {};
			var filterCondition = orgOptions.filterCondition;
			if (filterCondition) {
				var paramsSchema = "otherParams://";
				if (filterCondition.indexOf(paramsSchema) === 0) {
					try {
						otherParams = eval("(" + filterCondition.substr(paramsSchema.length) + ")");// $.parseJSON(filterCondition.substr(paramsSchema.length));
						if (otherParams && otherParams.filterCondition) {
							excludeValues = otherParams.filterCondition.split(/;|,|；|，/);
						}
					} catch (ex) {

					}
				} else {
					//按中英文的逗号和分号分割
					excludeValues = filterCondition.split(/;|,|；|，/);
				}
			}
			return {
				multiple: orgOptions.mutiSelect,
				type: orgOptions.typeList.join(";"),
				defaultType: orgOptions.defaultType,
				selectTypes: orgOptions.selectTypeList.join(";"),
				otherParams: otherParams,
				excludeValues: excludeValues
			}
		}
	});
	return WorkViewTestSubflow2;
});
