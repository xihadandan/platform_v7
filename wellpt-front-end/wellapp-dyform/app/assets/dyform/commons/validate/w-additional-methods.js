/*******************************************************************************
 * jQuery Validate扩展验证方法 (linjq)
 ******************************************************************************/
// 加载全局国际化资源
if (typeof I18nLoader != "undefined")
	I18nLoader.load("/js/pt/js/global");
// 加载动态表单定义模块国际化资源
if (typeof I18nLoader != "undefined")
	I18nLoader.load("/dyform/commons/validate/messages");

$(function() {
	jQuery.wValidator.addMethod("regexCaseNotSensitive", // addMethod第1个参数:方法名称
	function(value, ctl, params) { // addMethod第2个参数:验证方法，
		// 验证方法参数（被验证元素的值，被验证元素，参数）
		var exp = new RegExp(params, "i"); // 实例化正则对象，参数为用户传入的正则表达式
		return exp.test(value); // 测试是否匹配
	}, "格式错误");
	jQuery.wValidator.addMethod("regex", // addMethod第1个参数:方法名称
	function(value, ctl, params) { // addMethod第2个参数:验证方法，
		// 验证方法参数（被验证元素的值，被验证元素，参数）
		var exp = new RegExp(params); // 实例化正则对象，参数为用户传入的正则表达式
		return exp.test(value); // 测试是否匹配
	}, "格式错误");

	jQuery.wValidator.addMethod("isUnique", // addMethod第1个参数:方法名称
	function(value, ctl, params) { // addMethod第2个参数:验证方法，

		var data = {};
		$.extend(data, params.data);
		for ( var i in params.data) {
			if (typeof params.data[i] == "function") {
				data[i] = params.data[i]();
			}
		}

		// 先验证页面上的唯一分组里面的元素有没有重复
		var valid = true;
		var uniqueGroup = data[validateGroupType.unique];
		$(".value[" + validateGroupType.unique + "='" + uniqueGroup + "']").each(function() {
			var controlname = $(this).attr("name");
			if (data.fieldName == controlname) {
				return true;
			}

			var control = $.ControlManager.getCtl(controlname);
			var value = control.getValue();
			if (value == data.fieldValue) {
				valid = valid && false;
			}
		});

		if (!valid) {
			return valid;
		}

		// modify by wujx 20160928 begin
		var checked = true;
		var control = data.control;
		var subformDefinition = control.getFormDefinition();
		var subformcontrol = data.form.getSubformControl(subformDefinition.uuid);
		if (subformcontrol) {
			// 如果是位于从表中的控件，则对当前从表数据范围内做唯一校验
			// 如果是位于从表中的控件，则对当前从表数据范围内做唯一校验
			var subformData = [];
			subformcontrol.collectSubformData(function(subformDatas){
				subformData = subformDatas;
			}, function(errorInfo){
				
			});
			for(var i = 0; i < subformData.length; i++) {
				if (subformData[i][data.fieldName] == data.fieldValue && subformData[i]["uuid"] != data.uuid) {
					return false;
				}
			}
		} else {
			// 如果是位于主表中的控件，则做后台数据库校验
			var data2 = {};
			data2.uuid = data.uuid;
			data2.tblName = data.tblName;
			data2.fieldName = data.fieldName;
            data2.fieldValue = data.fieldValue;
            data2.unitUnique = data.unitUnique;
			$.ajax({
				url : params.url,
				type : params.type,
				async : params.async,
				data : JSON.cStringify(data2),
				dataType : 'json',
				contentType : 'application/json',
				type : "POST",
				success : function(result) {
					if (result.success == "true" || result.success == true) {
						if (result.data == "false" || result.data == false) {
							checked = true;
						} else {
							checked = false;
						}
					} else {
						checked = false;
					}
				},
				error : function(result) {
					checked = true;
				}
			});
		}
		// modify by wujx 20160928 end

		return checked;// 测试是否匹配
	}, "须唯一");

});