define([ "mui", "commons", "server", "appContext", "appModal", "mui-calendar", "formBuilder" ], function($, commons, server, appContext, appModal, muiCalendar, formBuilder) {
	// 列表显示发起工作
	var JDS = server.JDS;
	var StringUtils = commons.StringUtils;
	var StringBuilder = commons.StringBuilder;

	var showCalendar = function(options) {
		options = options || {};
		// 从传入参数新建日程
		if (options && options.action && options.action === "newCalendar" && options.startDate) {
			return newCalendar(options.startDate, options.title);
		}

		var content = new StringBuilder();
		JDS.call({
			service : "workflowNewWorkService.getFlowDefinitions",
			async : false,
			success : function(result) {
				var data = result.data;
				// getCagegoryDataWithCollapses(data, content);
			}
		})
		content.appendFormat('<link href="{0}/mobile/mui/css/calendar.css" rel="stylesheet" />', ctx);
		content.append('<div class="calendar-box contain mui-content" id="contain">');
		// 创建日程管理面板
		formBuilder.showPanel({
			title : "日程管理",
			content : content.toString(),
			container : "#mobile_calendar",
			actionBack : {
				label : "返回"
			},
			optButton : {
				label : "新建",
				callback : function(event, panel) {
					return newCalendar({

					});
				}
			},
			shown : function(options) {
				var panel = this;
				var curr_time = new Date();
				var year = curr_time.getFullYear();
				var month = curr_time.getMonth() + 1;
				// 日历插件
				var calendar = mui(".calendar-box").calendar({
					clickCallback : function() {

					},// 点击回调
					dragCallback : function() {

					},// 滑动回调
					time : {// 初始化传递时间
						year : year,
						month : month
					},
					year_unit : "年",
					month_unit : "月",
					day_unit : "日",
					showdate : "this-time"// 时间显示面板
				});
			},
		});
	}

	// 返回分类数据，按流程分类折叠
	function getCagegoryDataWithCollapses(data, text) {
		
	}
	function getCagegoryCollapseData(treeNode, text) {
		
	}

	// 绑定事件
	function bindEvent(wrapper, ui) {
		
	}

	// 发起工作
	function newCalendar(options) {

	}

	showCalendar.prototype.newCalendar = newCalendar;
	
	
	return showCalendar;
});