define([ "jquery", "commons", "constant", "server", "PanelWidgetDevelopment" ], function($, commons, constant, server,
		PanelWidgetDevelopment) {
	var PanelDevelopmentTips = function() {
		PanelWidgetDevelopment.apply(this, arguments);
	};
	var content = '<div class="row clearfix">\
		<div class="col-xs-4 column collapse1"><span class="count">0</span>今日行程</div>\
		<div class="col-xs-4 column collapse2"><span class="count">0</span>明日行程</div>\
		<div class="col-xs-4 column collapse7"><span class="count">0</span>一周行程</div>\
	</div>\
	<div class="panel-group" id="accordion">\
		<div class="panel panel-default">\
			<div class="panel-heading">\
				<h4 class="panel-title">\
					<a data-toggle="collapse" data-parent="#accordion" href="#collapse1">今天 </a>\
				</h4>\
			</div>\
			<div id="collapse1" class="panel-collapse collapse in">\
				<div class="panel-body"><ul class="list-body"><li>还没有安排</li></ul></div>\
			</div>\
		</div>\
		<div class="panel panel-default">\
			<div class="panel-heading">\
				<h4 class="panel-title">\
					<a data-toggle="collapse" data-parent="#accordion" href="#collapse2">明天</a>\
				</h4>\
			</div>\
			<div id="collapse2" class="panel-collapse collapse">\
				<div class="panel-body"><ul class="list-body"><li>还没有安排</li></ul></div>\
			</div>\
		</div>\
	</div>';
	commons.inherit(PanelDevelopmentTips, PanelWidgetDevelopment, {
		prepare : function() {

		},
		create : function() {

		},
		init : function() {

		},			
		//获取相应日期的日程安排
		getOneday:function(callback) {
			var list = {};
			return list;
		},
		beforeRender : function(options, configuration) {
			var self = this;
			var element = self.widget.element;
			$(element).addClass("jquery-ui-schedule");
			var $panel = $(".panel-body", element).append(content);
			$.ajax({
				type:"post",
				async:true,
				dataType : "json",
				url: ctx + "/schedule/recent/seven",
				success:function(result){
					var list = result["date1"];
					if(list != null && list.length > 0){
						$(".collapse1>.count", element).html(list.length);
						var $today = $("#collapse1 .list-body", element).html("");
						$.each(list, function(idx, schedule){
							var li = '<li><span class="time">'+schedule["startTime"]+'</span><span class="todo">'+schedule["scheduleName"]+'</span></li>'
							$today.append(li);
						})
					}
					list = result["date2"];
					if(list != null && list.length > 0) {
						$(".collapse2>.count", element).html(list.length);
						var $tomorrow = $("#collapse2 .list-body", element).html("");
						$.each(list, function(idx, schedule){
							var li = '<li><span class="time">'+schedule["startTime"]+'</span><span class="todo">'+schedule["scheduleName"]+'</span></li>'
							$tomorrow.append(li);
						})
					}
					list = result["date7"] || [];
					$(".collapse7>.count", element).html(list.length);
				}
			});
		},
		afterRender : function(options, configuration) {
		}
	});
	return PanelDevelopmentTips;
});
