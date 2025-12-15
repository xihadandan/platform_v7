(function($) {
	$.mobileUnit = {
		open : function(options) {
			options = $.extend({
				title : "人员选择",
				initUserIds : [],
				checkedType : "checkbox"
			// 是否设置显示在父窗口
			// 不允许选择节点值，多值以;分割。
			}, options);
			var selectedUsers = {};
			var selectOne = options.checkedType == "radio";
			var checkedType = options.checkedType;
			var panelSelector = "#af_unit_choose_users";
			createPanel(panelSelector);
			createFooter(panelSelector);
			$.ui.loadContent(panelSelector, false, false);
			$.ui.setTitle(options.title);

			var selector = "#af_unit_choose_users_list";
			$(selector).html("");

			var searchBox = '<p><input type="search" placeholder="查询" style="margin: 8px 12px 8px 0;"><input type="button" value="查询" class="button"></p><ul class="list"></ul>';
			$("#af_unit_choose_users_list").append(searchBox);
			$(window).bind(
					"resize",
					function() {
						$(selector).find("input[type='search']").css("width",
								$(document).width() - $(selector).find("input[type='button']").width() - 50);
					}).trigger("resize");

			$(selector).find("input[type='button']").bind("click", function(e) {
				var queryValue = $(selector).find("input[type='search']").val();
				listUsers(queryValue);
			}).trigger("click");

			function listUsers(queryValue) {
				JDS.call({
					service : "workService.queryForUsers",
					data : [ queryValue, options.initUserIds ],
					async : true,
					success : function(result) {
						initUsers(result.data);
					}
				});
			}

			function initUsers(data) {
				$(selector).find("ul").remove();
				if (selectOne) {
					selectedUsers = {};
				}
				var list = '<ul class="list">';
				for ( var i = 0; i < data.length; i++) {
					var user = data[i];
					var userId = user.id;
					var checked = "";
					if (selectedUsers[userId]) {
						checked = "checked='checked'";
					}
					var userName = user.userName;
					var dpName = user.departmentName;
					if (user.departmentName && user.departmentName.lastIndexOf("/") != -1) {
						dpName = user.departmentName.substring(user.departmentName.lastIndexOf("/") + 1);
					}
					var dpJob = user.majorJobName;
					if (user.majorJobName && user.majorJobName.lastIndexOf("/") != -1) {
						dpJob = user.majorJobName.substring(user.majorJobName.lastIndexOf("/") + 1);
					}
					var item = '<li><span>&nbsp;&nbsp;&nbsp;&nbsp;</span><input type="' + checkedType + '" name="todo_user" id="' + userId + '" '
							+ checked + ' /><label>' + userName + '</label>'
							+ '<div style="float:right;position:relative;text-align:right;"><div>' + dpName
							+ '</div><div>' + dpJob + '</div></div>' + '</li>';
					list += item;
				}
				list += '</ul>';
				$("#af_unit_choose_users_list").append(list);

				// 绑定checkbox事件
				$(selector).find("input[type='" + checkedType + "']").unbind("change");
				$(selector).find("input[type='" + checkedType + "']").bind("change", function() {
					if (selectOne) {
						selectedUsers = {};
					}
					var userId = $(this).attr("id");
					if (this.checked) {
						selectedUsers[userId] = userId;
					} else {
						delete selectedUsers[userId];
					}
				});
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
						var $checkbox = $(tmp).find("input[name='todo_user']");
						if ($checkbox.length == 1) {
							$checkbox.prop("checked", !$checkbox.prop("checked"));
							$checkbox.trigger("change");
						}
					}
				});
			}

			// 选择人员返回
			$("#af_unit_choose_users_ok").unbind("click");
			$("#af_unit_choose_users_ok").bind("click", function() {
				var userIds = [];
				for ( var key in selectedUsers) {
					userIds.push(key);
				}
				if (userIds.length == 0) {
					$.ui.popup("请选择人员！");
					return;
				}
				$.ui.goBack();

				if (options.afterSelect) {
					var returnVal = {};
					returnVal.id = userIds.join(";");
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
					+ '"><div id="af_unit_choose_users_list"></div></div>';
			$("#content").append(panel);
		}
	}
	// 创建DIV元素
	function createFooter(panelSelector, footer) {
		var $footer = $(panelSelector).find("footer");
		if ($footer.length == 0) {
			var footer = '<footer>';
			footer += '<a href="#" id="af_unit_choose_users_ok" class="icon check big" width="50%">确定</a>';
			footer += '<a href="#" onclick="$.ui.goBack();" class="icon close big" width="50%">取消</a>';
			footer += '</footer>';
			$(panelSelector).append(footer);
		}
	}
})(af);