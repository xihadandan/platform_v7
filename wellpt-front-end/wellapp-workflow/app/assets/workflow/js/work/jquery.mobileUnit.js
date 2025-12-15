;
(function($) {
	$.mobileUnit = {
		open : function(options) {
			options = $.extend({
				title : "人员选择"
			// 是否设置显示在父窗口
			// 不允许选择节点值，多值以;分割。
			}, options);
			var selectedUsers = {};
			var dlgSelector = "#dlg_mobile_unit";
			// 创建弹出框DIV
			createDiv(dlgSelector);
			var searchBox = "<div class='query-fields form_operate'>"
					+ "<input name='query_input' style='width: 85%' />"
					+ "<button type='button' class='btn'>查询</button></div>";
			$(dlgSelector).append(searchBox);
			$(dlgSelector).append("<ul class='user-to-choose'></ul>");
			var dlgOptions = {
				title : "选择办理人",
				modal : true,
				autoOpen : true,
				resizable : true,
				width : 350,
				height : 300,
				open : function() {
					// 列表查询
					$("button[type='button']", dlgSelector).on("click", function(e) {
						var queryValue = $("input[name='query_input']", dlgSelector).val();
						listUsers(queryValue);
					}).trigger("click");
				},
				buttons : {
					"确定" : function(e) {
						var userIds = [];
						for ( var key in selectedUsers) {
							userIds.push(key);
						}
						if (options.afterSelect) {
							var returnVal = {};
							returnVal.id = userIds.join(";");
							options.afterSelect.call(this, returnVal);
						}
						$(dlgSelector).oDialog("close");
					},
					"取消" : function(e) {
						$(dlgSelector).oDialog("close");
					}
				},
				close : function(e) {
					if (options.close) {
						options.close.call(this, e);
					}
					$(dlgSelector).html("");
				}
			};
			$(dlgSelector).oDialog(dlgOptions);

			function listUsers(queryValue) {
				JDS.call({
					service : "workService.queryForUsers",
					data : [ queryValue ],
					async : true,
					success : function(result) {
						$("ul.user-to-choose", dlgSelector).html("");
						initUsers(result.data);
					}
				});
			}

			function initUsers(data) {
				$.each(data, function() {
					var checked = "";
					if (selectedUsers[this.id]) {
						checked = "checked='checked'";
					}
					var dpName = this.departmentName;
					if (this.departmentName && this.departmentName.lastIndexOf("/") != -1) {
						dpName = this.departmentName.substring(this.departmentName.lastIndexOf("/") + 1);
					}
					var dpJob = this.majorJobName;
					if (this.majorJobName && this.majorJobName.lastIndexOf("/") != -1) {
						dpJob = this.majorJobName.substring(this.majorJobName.lastIndexOf("/") + 1);
					}
					var item = "<li><div><div style='float:left;margin-top:0.8em;'>" + this.userName
							+ "</div>";
					item += "<div style='float:right;margin-top:0.8em;'><input style='margin-left:10px' id='"
							+ this.id + "' name='todo_user' type='checkbox' " + checked + "/></div>"
							+ "<div style='float:right'><span>" + dpName
							+ "</span><span style='display:block; text-align:right;font-size:0.9em'>" + dpJob
							+ "</span></div>" + "</div><div style='clear:both'></div></li>";
					item = $(item);
					$(".user-to-choose").append(item);
				});

				$("input[name='todo_user']", "ul.user-to-choose").off("change");
				$("input[name='todo_user']", "ul.user-to-choose").on("change", function() {
					var userId = $(this).attr("id");
					if (this.checked) {
						selectedUsers[userId] = userId;
					} else {
						delete selectedUsers[userId];
					}
				});
				$("ul.user-to-choose", dlgSelector).off("click.li");
				$("ul.user-to-choose", dlgSelector).on("click.li", function(e) {
					var tmp = $(e.target);
					var tagName = tmp.prop("tagName");
					tagName = tagName == null ? "" : tagName.toUpperCase();
					if (tagName.toUpperCase() == "INPUT") {
					} else {
						while (tagName != "LI") {
							tmp = tmp.parent();
							tagName = tmp.prop("tagName");
						}
						var $checkbox = $("input[name='todo_user']", tmp);
						if ($checkbox.length == 1) {
							$checkbox.trigger("click");
						}
					}
				});
			}
			/*
			 * var unitDialog = $().dialog({ title : options.title, bgiframe :
			 * true, autoOpen : true, resizable : true, stack : true, // zIndex :
			 * 9999, width : 850, height : 560, modal : true, overlay : {
			 * background : '#000', opacity : 0.5 }, close : function(e, ui) {
			 * if (this.contentWindow.goUnitTree) { var returnValue =
			 * this.contentWindow.goUnitTree.returnValue; if (returnValue) { if
			 * (options.labelField != null) { $("#" +
			 * options.labelField).val(returnValue.name); $("#" +
			 * options.labelField).attr("hiddenValue", returnValue.id); } if
			 * (options.valueField != null) { $("#" +
			 * options.valueField).val(returnValue.id); } if (options.sexField !=
			 * null) { $("#" + options.sexField).val(returnValue.sex); } if
			 * (options.emailField != null) { $("#" +
			 * options.emailField).val(returnValue.email); } if
			 * (options.employeeNumberField != null) { $("#" +
			 * options.employeeNumberField).val(returnValue.employeeNumber); }
			 * if (options.loginNameField != null) { $("#" +
			 * options.loginNameField).val(returnValue.loginName); } if
			 * (options.isSetChildWin) { setChildWin(); } } if (returnValue &&
			 * options.afterSelect) { options.afterSelect.call(this,
			 * this.contentWindow.goUnitTree.returnValue); } if (returnValue &&
			 * options.ok) { options.ok.call(this,
			 * this.contentWindow.goUnitTree.returnValue); } } if
			 * (options.close) { options.close.call(this, e); }
			 * $(layoutSelector).css("overflow-x", layoutOverflowX);
			 * $(layoutSelector).css("overflow-y", layoutOverflowY); }, open :
			 * function(e) { $(".ui-widget-overlay").css("background", "#000");
			 * $(".ui-widget-overlay").css("opacity", "0.5");
			 * $(this).css("width", "100%"); $(this).css("margin", "0");
			 * $(this).css("padding", "0"); this.contentWindow.dialogArguments =
			 * laArg; $(layoutSelector).css("overflow-x", "scroll");
			 * $(layoutSelector).css("overflow-y", "scroll"); } });
			 */
		}
	};
	// 创建DIV元素
	function createDiv(selector) {
		$("#content").find(selector);
		var id = selector;
		if (selector.indexOf("#") == 0) {
			id = selector.substring(1);
		}
		// 放置
		if ($(selector).length == 0) {
			var panel = '<div title="workTodoDetail" class="panel" id="workTodoDetail">';
			$("body").append(panel);
		}
	}
})(jQuery);