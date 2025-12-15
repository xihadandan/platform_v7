(function($) {
	$.taskChosen = {
		open : function(options) {
			options = $.extend({
				title : "环节选择",
				tasks : []
			// 是否设置显示在父窗口
			// 不允许选择节点值，多值以;分割。
			}, options);
			var panelSelector = "#af_wf_choose_task";
			createPanel(panelSelector);
			createFooter(panelSelector);
			$.ui.loadContent(panelSelector, false, false);
			$.ui.setTitle(options.title);

			var selector = "#af_wf_choose_task_list";
			$(selector).html("");

			function initTasks(data) {
				$(selector).find("ul").remove();
				var list = '<ul class="list">';
				for ( var i = 0; i < data.length; i++) {
					var d = data[i];
					var taskName = d.name;
					var taskId = d.id;
					var item = '<li>' + taskName + '<input name="task_id" type="radio" id="' + taskId
							+ '" value="' + taskId + '" style="display:block;float:right"/><label/></li>';
					list += item;
				}
				;
				list += '</ul>';
				$("#af_wf_choose_task_list").append(list);

				// 绑定radio事件
				$(selector).find("li").unbind("click");
				$(selector).find("li").bind("click", function(e) {
					var tmp = $(e.target);
					var tagName = tmp.prop("tagName");
					tagName = tagName == null ? "" : tagName.toUpperCase();
					if (tagName.toUpperCase() == "INPUT") {
					} else {
						while (tagName != "LI") {
							tmp = tmp.parent();
							tagName = tmp.prop("tagName");
						}
						var $checkbox = $(tmp).find("input[name='task_id']");
						if ($checkbox.length == 1) {
							$checkbox.prop("checked", !$checkbox.prop("checked"));
							$checkbox.trigger("change");
						}
					}
				});
			}
			initTasks(options.tasks);

			// 选择环节返回
			$("#af_wf_choose_task_ok").unbind("click");
			$("#af_wf_choose_task_ok").bind("click", function() {
				var $radio = $(selector).find("input[type='radio']:checked");
				var returnVal = $radio.prop("id");
				if ($radio.length != 1 || returnVal == null) {
					$.ui.popup("请选择环节！");
					return;
				}
				$.ui.goBack();
				if (options.afterSelect) {
					options.afterSelect.call(this, returnVal);
				}
			});
		}
	};

	// 创建DIV元素
	function createPanel(panelSelector) {
		var id = panelSelector;
		if (panelSelector.indexOf("#") == 0) {
			id = panelSelector.substring(1);
		}
		var $panel = $("#content").find(panelSelector);
		// 放置
		if ($panel.length == 0) {
			var panel = '<div title="人员选择" class="panel" id="' + id
					+ '"><div id="af_wf_choose_task_list"></div></div>';
			$("#content").append(panel);
		}
	}
	// 创建DIV元素
	function createFooter(panelSelector, footer) {
		var $footer = $(panelSelector).find("footer");
		if ($footer.length == 0) {
			var footer = '<footer>';
			footer += '<a href="#" id="af_wf_choose_task_ok" class="icon check big" width="50%">确定</a>';
			footer += '<a href="#" onclick="$.ui.goBack();" class="icon close big" width="50%">取消</a>';
			footer += '</footer>';
			$(panelSelector).append(footer);
		}
	}
})(af);