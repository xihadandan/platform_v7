define([ "jquery", "server", "commons", "constant", "appContext", "appModal", "ProcessViewer" ], function($, server, commons, constant,
		appContext, appModal, ProcessViewer) {
	var StringUtils = commons.StringUtils;
	// 办理过程
	var WorkFlowWorkProcessViewer = function(workView, options) {
		ProcessViewer.call(this, workView, options);
	};
	commons.inherit(WorkFlowWorkProcessViewer, ProcessViewer, {
		init : function() {
			var _self = this;
			var options = _self.options;
			var $viewProcess = $("button[name='" + options.buttonCode + "']");
			var $viewProcess2 = $(".right-sidebar-open");
			var $viewProcess3 = $(".right-sidebar .right-sidebar-close");
			// 配置隐藏按钮或草稿件，隐藏按钮
			if (options.clickButtonToShow === false || this.workView.isDraft()) {
				$viewProcess.hide();
				$viewProcess2.hide();
			} else {
				$viewProcess.show();
				var toggleViewProcess = function() {
					var $rightSidebar = $(".right-sidebar");
					if ($rightSidebar.is(":visible")) {
						// 动画效果
						$rightSidebar.removeClass("fadeInRight");
						$rightSidebar.addClass("animated fadeOutRight");
						setTimeout(function() {
							$rightSidebar.removeClass("fadeOutRight");
							$rightSidebar.hide();
						}, 500);
					} else {
						// 动画效果
						$rightSidebar.addClass("animated fadeInRight");
						$rightSidebar.show();
					}
					_self.show();
				};
				$viewProcess.on("click", toggleViewProcess);
				$viewProcess2.on("click", toggleViewProcess);
				$viewProcess3.on("click", toggleViewProcess);
			}
		},
		show : function() {
			var _self = this;
			if (_self.loaded === true) {
				return;
			}
			_self.loaded = true;
			var workData = _self.workView.getWorkData();
			var flowInstUuid = workData.flowInstUuid;
			var url;
			if(appContext.isMobileApp()){
				url = ctx + "/workflow/work/v53/view/flow/mobile/process?flowInstUuid=" + flowInstUuid;
			}else {
				url = ctx + "/workflow/work/v53/view/flow/process?flowInstUuid=" + flowInstUuid;
			}
			$.get(url, function(content) {
				_self.loaded = true;
				$(".work-process-tab-pane")[0].innerHTML = "";
				$(".work-process-tab-pane")[0].innerHTML = content;
			});
		}
	});
	return WorkFlowWorkProcessViewer;
});