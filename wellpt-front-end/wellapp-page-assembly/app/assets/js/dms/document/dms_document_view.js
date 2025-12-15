define([ "jquery", "commons", "constant", "server", "appContext", "jquery-dmsDocumentView" ], function($, commons,
		constant, server, appContext, dmsDocumentView) {
	var StringUtils = commons.StringUtils;
	var options = {};
	var $inputs = $("input", "form[name='dms-data-form']");
	$.each($inputs, function() {
		var name = $(this).attr("name");
		if (StringUtils.isNotBlank(name) && name.length > 4) {
			var subfix = name.substring(0, 4);
			if ("dms_" == subfix) {
				var key = name.substring(4);
				var value = $(this).val();
				options[key] = value;
			}
		}
	});
	//对应的目标位置 _dialog ：弹窗,_blank ：新页面
	options.target = $("#target").val();
	// // 数据管理服务ID
	// options.dmsId = $("#dms_dmsId").val();
	// // 业务数据初始化
	// options.formUuid = $("#dms_formUuid").val();
	// options.dataUuid = $("#dms_dataUuid").val();
	// 单据解析JS模块
	// var documentViewModule = $("#dms_documentViewModule").val();
	var documentViewModule = options.documentViewModule;
	if (StringUtils.isNotBlank(documentViewModule)) {
		documentViewModule = require(documentViewModule);
	}
	options.documentViewModule = documentViewModule;
	var app = WebApp || {};
	options.extraParams = app.extraParams || {};
	// 外部传入的二开片段模块
	var epDmsDocumentViewFragmentModule = options.extraParams.ep_dmsDocumentViewFragment;
	if (StringUtils.isNotBlank(epDmsDocumentViewFragmentModule)) {
		appContext.require([ epDmsDocumentViewFragmentModule ], function(dmsDocumentViewFragmentModule) {
			var InnerDocumentView = function() {
				documentViewModule.apply(this, arguments);
			};
			var dmsDocumentViewFragmentModuleObject = dmsDocumentViewFragmentModule;
			if ($.isFunction(dmsDocumentViewFragmentModule)) {
				dmsDocumentViewFragmentModuleObject = new dmsDocumentViewFragmentModule();
			}
			commons.inherit(InnerDocumentView, documentViewModule, dmsDocumentViewFragmentModuleObject);
			options.documentViewModule = InnerDocumentView;
			$("body").dmsDocumentView(options);
		});
	} else {
		$("body").dmsDocumentView(options);// jquery.dmsDocumentView.js
	}
	// 发布事件
	$(document).trigger(constant.WIDGET_EVENT.PageContainerCreationComplete);

    var dyformSelector = "#dyform";
	$(document.body).one("DyformCreationComplete", function(event){
		$(dyformSelector).dyform("fixedSubformsHeader", window, "div.widget-box>.widget-header");
	})
	// 窗口大小调整事件
	$(window).bind("resize", function(e) {
		// 调整自适应表单宽度
		adjustWidthToForm();
	});
	// 调整自适应表单宽度
	function adjustWidthToForm() {
		// $(document).scrollTop("0");

		var div_body_width = $(window).width() * 0.95;
		$(".form_header").css("width", div_body_width - 5);
		$(".div_body").css("width", div_body_width);

		$(".form_content").css("width", div_body_width - 44);
	}

	/*
	 * window.onscroll = function() { var $widgetHeader = $(".widget-header");
	 * if ($(document).scrollTop() > 0) { var width = $widgetHeader.width();
	 * $widgetHeader.addClass("fixed"); $widgetHeader.css("width", width);
	 * $widgetHeader.css("top", "4px"); } else {
	 * $widgetHeader.removeClass("fixed"); $widgetHeader.removeAttr("style"); } };
	 */
});