define([ "mui", "server", "commons", "DyformExplain" ], function($, server, commons, DyformExplain) {
	var preview = {};
	var services = {
		loadFormDefinition : ctx + "/dyform/getFormDefinition",
		loadFormDefinitionData : ctx + "/dyformdata/getFormDefinitionData",
	}
	var StringBuilder = commons.StringBuilder;
	var noScrollContent = new StringBuilder();
	noScrollContent.append('<div class="mui-content">');
	noScrollContent.append('<div id="dyform_designer_preview_container" class="mui-dyform"></div>');
	noScrollContent.append('</div>');
	var scrollContent = new StringBuilder();
	scrollContent.append('<div class="mui-content">');
	scrollContent.append('<div class="mui-scroll-wrapper">');
	scrollContent.append('<div class="mui-scroll">');
	scrollContent.append('<div id="dyform_designer_preview_container" class="mui-dyform"></div>');
	scrollContent.append('</div>');
	scrollContent.append('</div>');
	scrollContent.append('</div>');

	preview.loadFormDefinition = function(uuid) {
		if (uuid == "" || uuid == null || typeof uuid == "undefined" || uuid == "undefined") {
			return {};
		}
		var definitionObj = null;
		var time1 = (new Date()).getTime();
		$.ajax({
			url : services.loadFormDefinition,
			cache : false,
			async : false,// 同步完成
			type : "POST",
			data : {
				formUuid : uuid
			},
			dataType : "json",
			success : function(data) {
				definitionObj = data;
			},
			error : function() {// 加载定义失败
			}
		});
		var time2 = (new Date()).getTime();
		console.log("加载定义所用时间:" + (time2 - time1) / 1000.0 + "s");
		return definitionObj;
	};
	preview.loadFormDefinitionData = function(formUuid, dataUuid) {

		if (typeof dataUuid == "undefined" || dataUuid == "undefined") {// 未初始化
			dataUuid = "";
		}

		var formDatas = null;
		$.ajax({
			url : services.loadFormDefinitionData,
			cache : false,
			async : false,// 同步完成
			type : "POST",
			data : {
				formUuid : formUuid,
				dataUuid : dataUuid
			},
			dataType : "json",
			success : function(result) {
				if (result.success == "true" || result.success == true) {
					formDatas = result.data;
				} else {
					alert("数据获取失败");
				}
			},
			error : function() {// 加载定义失败
			}
		});
		return formDatas;
	};
	preview.loadDefinitionJsonDefaultInfo = function(formDefinition) {
		server.JDS.call({
			service : "dyFormDefinitionService.loadDefinitionJsonDefaultInfo",
			async : false,
			data : [ JSON.stringify(formDefinition) ],
			success : function(result) {
				formDefinition = result.data;
			}
		});
		return formDefinition;
	}
	preview.initDyformExplain = function(dyFormData) {
		// var formDefinition = JSON.parse(dyFormData.formDefinition);
		// 检查下，如果是string 需要转成json
		var formDefinition = dyFormData.formDefinition;
		if (typeof dyFormData.formDefinition == "string") {
			formDefinition = JSON.parse(dyFormData.formDefinition);
		}

		var parentContainer = $("#dyform_designer_preview_parent_container");
		// 滚动条由表单解析内部处理掉
		// if (formDefinition && formDefinition.mobileConfiguration
		// && formDefinition.mobileConfiguration.blockLayout === "4") {
		parentContainer[0].innerHTML = noScrollContent.toString();
		// } else {
		// parentContainer[0].innerHTML = scrollContent.toString();
		// $('.mui-scroll-wrapper').scroll({
		// deceleration : 0.0005
		// });
		// }
		var container = $("#dyform_designer_preview_container");
		container[0].innerHTML = "";
		var queue = $.ui.loadContentQueue;
		if (queue.length) {
			queue[0].classList.remove("mui-hidden");
			while (queue.length > 1) {
				queue.pop().remove();
			}
		}
		var dyformExplain = new DyformExplain(container, {
			renderTo : "dyform_designer_preview_container",
			height : 420,
			formData : dyFormData
		});
	}
	preview.preview = function(formUuid, ctx) {
		if (!window.ctx) {
			window.ctx = ctx;
		}
		if (formUuid) {
			var dyFormData = preview.loadFormDefinitionData(formUuid, "");
			preview.initDyformExplain(dyFormData);
		}
		window.preview = function(formDefinition) {
			var dyFormData = {
				formDefinition : formDefinition,
				formUuid : formDefinition.uuid,
				formDatas : {}
			}
			preview.initDyformExplain(dyFormData);
		}
	}
	return preview;
});