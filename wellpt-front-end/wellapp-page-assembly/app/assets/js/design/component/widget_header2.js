wRequire([ "jquery", "server", "commons", "design_commons", "appContext", "wCommonPictureLib" ], function($, server, commons, DesignUtils, appContext) {
	var StringUtils = commons.StringUtils;
	// 初始化基本信息
	$("#logoFileImageSelectBtn").on("click", function(){
		$.WCommonPictureLib.show({
			initPrevImg : $("#logoFilePath").val(),
			confirm : function(data) {
				var pictureFilePath = data.filePaths;
				if (StringUtils.isBlank(pictureFilePath)) {
					return;
				}
				$("#logoFilePath").val(pictureFilePath);
				$("#logoFileImage").show();
				$("#logoFileImage").attr("src", ctx + pictureFilePath);
			}
		});
	});


	// 定义导航/工具栏配置信息
	// 事件类型选项
	var eventTypeOption = "<option></option>";
	for(var i = 0; i < Constant.EVENT_TYPE.length; i++) {
		var type = Constant.EVENT_TYPE[i];
		eventTypeOption += "<option value='" + type.value +"'>" + type.name + "</option>";
	}
	
	var $mainNav = $("#widget_header_tabs_main_nav");
	var $subNav = $("#widget_header_tabs_sub_nav");
	var $toolBar = $("#widget_header_tabs_tool_bar");
	
	// 对外接口
	var WidgetHeaderConfigurer = {};
	
	// 渲染一级导航、二级导航、工具栏配置界面
	WidgetHeaderConfigurer.renderNavConfigurer = function() {
		var navConfigeHtml = '<div class="btn-group" role="group">'	//
			+ '	<button type="button" class="btn btn-primary btn-sm btn-add">新增</button>'	//
			+ '</div>'	//
			+ '<table class="table w-configurer-table">'	//
			+ '	<thead>'	//
			+ '	    <tr>'	//
			+ '	     	<th>名称</th>'	//
			+ '	      	<th>图标</th>'	//
			+ '	      	<th style="min-width : 50px;">隐藏</th>'	//
			+ '	      	<th>顺序</th>'	//
			+ '	      	<th>事件类别</th>'	//
			+ '	      	<th>事件逻辑</th>'	//
			+ '	      	<th>操作</th>'	//
			+ '	    </tr>'	//
			+ '</thead>'	//
			+ '	<tbody>'	//
			+ '	</tbody>'	//
			+ '</table>';	//
		$mainNav.append(navConfigeHtml);
		$subNav.append(navConfigeHtml);
		$toolBar.append(navConfigeHtml);
		
		// 绑定新增按钮事件
		$(".btn-add").each(function(i, btn) {
			var $btn = $(btn);
			$btn.on("click", function(){
				var $nav = $btn.parents(".tab-pane");
				WidgetHeaderConfigurer.addNavConfigurer({}, $nav);
			});
		});
	}
	// 增加NAV配置行
	WidgetHeaderConfigurer.addNavConfigurer = function(data, $table) {
		if (!data) {
			data = {};
		} 
		var id = data.id;
		
		var $newHtml = $('<tr></tr>');
		$newHtml.append('<td fieldName="name"><input type="text" class="form-control w-configurer-table-option" name="name" value="' 
				+ (data.name ? data.name : "") + '"/></td>'
			+ '<td fieldName="icon"><div class="has-feedback">' 
				+ '<input type="text" class="form-control w-configurer-table-option" name="icon" value="'
				+ (data.icon ? data.icon : "") + '" style="padding-right: 34px"/>' 
				+ '<span class="' + (data.icon ? data.icon : "") + ' form-control-feedback" aria-hidden="true" style="right: 0"></span></div></td>'
			+ '<td fieldName="hidden"><input type="checkbox" class="w-configurer-table-option" value="1" name="hidden" ' 
				+ (data.hidden == 1 ? "checked":"") + '/></td>'
			+ '<td fieldName="sortOrder"><input type="text" class="form-control w-configurer-table-option" name="sortOrder" value="' 
				+ (data.sortOrder ? data.sortOrder : "") + '"/></td>'
      		+ '<td fieldName="eventType"><select class="form-control w-configurer-table-option" name="eventType"></select></td>'
			+ '<td fieldName="eventHandler"><input type="text" class="form-control w-configurer-table-option" name="eventHandler" value="' 
				+ (data.eventHandler ? data.eventHandler : "") + '"/></td>'
			+ '<td fieldName="eventHandlerId" style="display: none"><input type="text" class="form-control w-configurer-table-option" name="eventHandlerId" value="' 
				+ (data.eventHandlerId ? data.eventHandlerId : "") + '"/></td>'				
			+ '<td fieldName="appType" style="display: none"><input type="text" class="form-control w-configurer-table-option" name="appType" value="' 
			+ (data.appType ? data.appType : "") + '"/></td>');
		$("table tbody", $table).append($newHtml);
		
		if (id) {
			$idTd = $('<input type="hidden" class="form-control w-configurer-table-option" name="id" value="' + id + '"/>');
			$("td[fieldName='name']", $newHtml).prepend($idTd);
			$("input[name='name']", $newHtml).attr("readonly", true);
		} else {
			// 非默认项才可以删除
			var $delBtn = $('<td><button type="button" class="btn btn-danger btn-sm btn-del">删除</button></td>');
			$newHtml.append($delBtn);
			$delBtn.on("click", function(){
				$(this).parent().remove();
			});
		}

		// 图标识别
		$("input[name='icon']", $newHtml).on("change", function(){
			$(this).next("span").removeClass();
			$(this).next("span").addClass($(this).val() + " form-control-feedback");
		});
		
		// 初始化事件类型下拉框
		$("select[name='eventType']", $newHtml).append(eventTypeOption).val(data.eventType ? data.eventType : "");
		
		// 初始化事件逻辑下拉树
		var system = appContext.getCurrentUserAppData().getSystem();
		var productUuid = system.productUuid;
		
		if (id && id == "theme") {
			$("td[fieldName='eventHandler']", $newHtml).empty();
		} else {
			var eventHandlerTree = $("input[name='eventHandler']", $newHtml).wCommonComboTree({
				service : "appProductIntegrationMgr.getTreeWithPtProduct",
				serviceParams : [productUuid],
				multiSelect : false, // 是否多选
				parentSelect : true, // 父节点选择有效，默认无效
				onAfterSetValue : function(event, self, value) {
					var valueNodes = self.options.valueNodes;
					var appType = [];
					var eventHandlerId = [];
					for(var i = 0; i < valueNodes.length; i++) {
						appType.push(valueNodes[i].data.type);
						eventHandlerId.push(valueNodes[i].data.path);
					}
					$("input[name='appType']", $newHtml).val(appType.join(","));
					$("input[name='eventHandlerId']", $newHtml).val(eventHandlerId.join(","));
				}
			});
		}
	}
	
	// 收集数据
	WidgetHeaderConfigurer.collectOption = function() {
		var opt = DesignUtils.collectConfigurerData($("#widget_header"), "w-configurer-option");
		opt.mainNav = DesignUtils.collectConfigurerTableData($mainNav,
				"w-configurer-table", "w-configurer-table-option")[0];
		opt.subNav = DesignUtils.collectConfigurerTableData($subNav)[0];
		opt.toolBar = DesignUtils.collectConfigurerTableData($toolBar)[0];
		return opt;
	}

	// 初始化数据
	WidgetHeaderConfigurer.initOption = function(options) {
		// 加载CSS样式配置页面
		$("#widget_header_tabs_base_css").load(ctx + "/web/app/page/configurer/widget_base_css", function(){
			// 处理基本数据
			DesignUtils.fillConfigurerData(options, $("#widget_header"), "w-configurer-option");
			// 处理LOGO预览
			if (StringUtils.isBlank(options.logoFilePath)) {
				$("#logoFileImage").hide();
			} else {
				$("#logoFileImage").attr("src", ctx + options.logoFilePath);
			}
			
			// 初始化一级导航数据
			var mainNavData = options.mainNav;
			if (mainNavData && mainNavData.length > 0) {
				for(var i = 0; i < mainNavData.length; i++) {
					WidgetHeaderConfigurer.addNavConfigurer(mainNavData[i], $mainNav);
				}
			} else {
				//初始化一级导航默认数据
				var click = Constant.getValueByKey(Constant.EVENT_TYPE, "CLICK");
				WidgetHeaderConfigurer.addNavConfigurer({id : "theme", name : "主题设置", icon : "glyphicon glyphicon-text-size", 
					sortOrder : 90, eventType : click}, $mainNav);
				WidgetHeaderConfigurer.addNavConfigurer({id : "personInfo", name : "个人信息", icon : "glyphicon glyphicon-user", 
					sortOrder : 80, eventType : click}, $mainNav);
				WidgetHeaderConfigurer.addNavConfigurer({id : "myMsg", name : "我的消息", icon : "glyphicon glyphicon-comment", 
					sortOrder : 70, eventType : click}, $mainNav);
			}
			
			// 初始化二级导航数据
			var subNavData = options.subNav;
			if (subNavData && subNavData.length > 0) {
				for(var i = 0; i < subNavData.length; i++) {
					WidgetHeaderConfigurer.addNavConfigurer(subNavData[i], $subNav);
				}
			}
			// 初始化导航工具数据
			var toolBarData = options.toolBar;
			if (toolBarData && toolBarData.length > 0) {
				for(var i = 0; i < toolBarData.length; i++) {
					WidgetHeaderConfigurer.addNavConfigurer(toolBarData[i], $toolBar);
				}
			}
		});
	}
	
	window.beforeSaveConfig = function(helper){
		var opt = WidgetHeaderConfigurer.collectOption();
		if(StringUtils.isBlank(opt["title"])) {
			alert("标题不能为空");
			return false;
		}
		$.extend(helper.json, opt);
		
		console && console.log(helper);
	};
	
	window.afterSaveConfig = function(helper){
		// debugger;
	};
	
	window.beforeInitConfig = function(helper){
		console.log(new Date().getTime() + " : beforeInitConfig");
		$("#logoFileImage").attr("src", helper["logoFilePath"]);
		// debugger;
	};
	
	window.afterInitConfig = function(helper){
		console.log(new Date().getTime() + " : afterInitConfig");
		// debugger;
	};
	
	WidgetHeaderConfigurer.renderNavConfigurer();
	
	return null;
});