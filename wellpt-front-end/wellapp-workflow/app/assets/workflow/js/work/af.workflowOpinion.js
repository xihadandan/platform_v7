(function($) {
	$.workflowOpinion = {
		open : function(options) {
			options = $.extend({
				title : "签署意见",
				opinionText : "",
				submitHandler : null,
				workData : null
			// 是否设置显示在父窗口
			// 不允许选择节点值，多值以;分割。
			}, options);
			var panelSelector = "#af_wf_sign_opinion";
			createPanel(panelSelector);
			createFooter(panelSelector, options);
			$.ui.loadContent(panelSelector, false, false);
			$.ui.setTitle(options.title);

			var selector = "#af_wf_opinion_content";
			$(selector).html("");

			var getAllOpinionCategoryBeansService = "flowOpinionService.getAllOpinionCategoryBeans";
			var searchBox = '<p><textarea placeholder="签署意见" rows="6" class=""></textarea></p>';
			$(selector).append(searchBox);
			function initOpinions() {
				JDS.call({
					service : getAllOpinionCategoryBeansService,
					data : [],
					async : true,
					success : function(result) {
						var data = result.data;
						var list = '<ul class="list">';
						for ( var i = 0; i < data.length; i++) {
							var d = data[i];
							if (d["opinions"]) {
								for ( var j = 0; j < d["opinions"].length; j++) {
									var opinion = d["opinions"][j];
									var item = '<li>' + opinion.content + '</li>';
									list += item;
								}
							}
						}
						list += '</ul>';
						$("#af_wf_opinion_content").append(list);

						$(selector).find("textarea").val(options.opinionText);
						// 意见点击事件处理
						$(selector).find("li").unbind("click");
						$(selector).find("li").bind(
								"click",
								function(e) {
									$(selector).find("textarea").val(
											$(selector).find("textarea").val() + $(this).text());
								});
					}
				});
			}
			initOpinions();

			// 提交处理
			if (options.submitHandler != null) {
				$("#af_wf_sign_opinion_submit").unbind("click");
				$("#af_wf_sign_opinion_submit").bind("click", function() {
					if (!checkedOpinion(selector)) {
						return;
					}
					$.ui.goBack();
					var opinionText = $(selector).find("textarea").val();
					options.workData.opinionText = opinionText;
					options.submitHandler.call($(this), options.workData);
				});
			}
			// 选择人员返回
			$("#af_wf_sign_opinion_ok").unbind("click");
			$("#af_wf_sign_opinion_ok").bind("click", function() {
				if (!checkedOpinion(selector)) {
					return;
				}
				$.ui.goBack();

				if (options.afterSelect) {
					var opinionText = $(selector).find("textarea").val();
					options.afterSelect.call(this, opinionText);
				}
			});
		}
	};

	function checkedOpinion(selector) {
		var opinionText = $(selector).find("textarea").val();
		if (opinionText == null) {
			$.ui.popup("意见不能为空!");
			return false;
		}
		if (opinionText.replace(/^(\s|\xA0)+|(\s|\xA0)+$/g, '') == "") {
			$.ui.popup("意见不能为空!");
			return false;
		}

		return true;
	}

	// 创建DIV元素
	function createPanel(panelSelector) {
		var id = panelSelector;
		if (panelSelector.indexOf("#") == 0) {
			id = panelSelector.substring(1);
		}
		var $panel = $("#content").find(panelSelector);
		// 放置
		if ($panel.length == 0) {
			var panel = '<div title="签署意见" class="panel" js-scrolling="true" id="' + id
					+ '"><div id="af_wf_opinion_content"></div></div>';
			$("#content").append(panel);
		}
	}
	// 创建DIV元素
	function createFooter(panelSelector, options) {
		$(panelSelector).find("footer").remove();
		var footer = '<footer>';
		footer += '<a href="#" id="af_wf_sign_opinion_ok" class="icon check big" width="50%">确定</a>';
		if (options.submitHandler != null) {
			footer += '<a href="#" id="af_wf_sign_opinion_submit" class="icon stack" width="50%">提交</a>';
		}
		footer += '<a href="#" onclick="$.ui.goBack();" class="icon close big" width="50%">取消</a>';
		footer += '</footer>';
		$(panelSelector).append(footer);
	}
})(af);