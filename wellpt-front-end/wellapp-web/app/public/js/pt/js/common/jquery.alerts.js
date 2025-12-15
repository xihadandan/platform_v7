(function($) {
	$.alerts = {
		overlayOpacity : .5,
		overlayColor : '#000',
		time_s : 30,
		queren : "确认",
		quxiao : "取消",
		prompt : "提示框",
		prompt1 : "确认框",
		isSubmit : "no",
		callback_ : null,
		flag : 0,// 判断是否点击自动关闭的确认，点击后到时间不调用回调函数
		width : 400,
		height : 180,
		alert : function(message, callback, title, width, height) {
			// modify by wujx 20160818 begin
			// 调用之前，先关闭弹出框，避免连续调用该方法，引起倒计时错乱
			if ($("#popup_close").length > 0) {
				$("#popup_close").trigger("click");
			}
			// modify by wujx 20160818 end
			$.alerts.isSubmit = "no";
			callback_ = callback;
			if (title == null)
				title = $.alerts.prompt;
			if (callback == null)
				$.alerts.callback_ = callback;
			if (width == null)
				width = $.alerts.width;
			if (height == null)
				height = $.alerts.height;
			$.alerts.show(title, message, 'alert_', width, height);
			$("#submit_button").focus();
			// 绑定关闭事件
			$("#close_button").remove();
			$("#popup_close").click(function() {
				$.alerts._hideAndCallBack(callback);
				return false
			});
			// 绑定关闭并调用callback事件
			$("#submit_button").click(function(e) {
				$.alerts._hideAndCallBack(callback);
				return false;
			});
			// 自动关闭
			$.alerts.countDown($.alerts.time_s);
		},
		alert2 : function(message, callback, title, width, height) {
			if (title == null)
				title = $.alerts.prompt;
			if (width == null)
				width = $.alerts.width;
			if (height == null)
				height = $.alerts.height;
			$.alerts.show(title, message, 'alert_', width, height);
			$("#submit_button").focus();
			$("#close_time_div").hide();
			// 绑定关闭事件
			$("#close_button").remove();
			$("#popup_close").click(function() {
				$.alerts._hideAndCallBack(callback);
				return false
			});
			// 绑定关闭并调用callback事件
			$("#submit_button").click(function(e) {
				$.alerts._hideAndCallBack(callback);
				return false
			});
		},
		alert3 : function(message, callback, title, argument, width, height) {
			callback_ = callback;
			if (title == null)
				title = $.alerts.prompt1;
			if (width == null)
				width = $.alerts.width;
			if (height == null)
				height = $.alerts.height;
			if (callback == null)
				$.alerts.callback_ = callback;
			$.alerts.show(title, message, 'alert_', width, height);
			$("#close_button").focus();
			$("#close_time_div").hide();
			// 绑定关闭事件
			$("#close_button").click(function() {
				$.alerts._hide();
			});
			$("#popup_close").click(function() {
				$.alerts._hide();
			});
			// 绑定关闭并调用callback事件
			$("#submit_button").click(function(e) {
				$.alerts._hideAndCallBack(callback, argument);
			});
		},
		alert4 : function(obj) {
			callback_ = obj.callback;
			if (obj.title == null)
				obj.title = $.alerts.prompt1;
			if (obj.width == null)
				obj.width = $.alerts.width;
			if (obj.height == null)
				obj.height = $.alerts.height;
			if (obj.callback == null)
				$.alerts.callback_ = obj.callback;
			if (obj.queren != null)
				$.alerts.queren = obj.queren;
			if (obj.quxiao != null)
				$.alerts.quxiao = obj.quxiao;
			$.alerts.show(obj.title, obj.message, 'alert_', obj.width, obj.height);
			$("#close_button").focus();
			$("#close_time_div").hide();
			// 绑定关闭事件
			$("#close_button").click(function() {
				$.alerts._hideAndCallBack(obj.callback2, obj.argument2);
			});
			$("#popup_close").click(function() {
				$.alerts._hide();
			});
			// 绑定关闭并调用callback事件
			$("#submit_button").click(function(e) {
				$.alerts._hideAndCallBack(obj.callback, obj.argument);
			});
		},
		countDown : function(time_s) {
			// zyguo 计时器有问题，暂时关闭了
//			var $timeEl = $.alerts.$timeEl  = $('div');
//			if ($timeEl.length == 0) {
//				$timeEl = $.alerts.$timeEl = $('body');
//			} else {
//				var divIndex = parseInt(Math.random() * $timeEl.length)
//				$timeEl = $($timeEl[divIndex]);
//			}
//			$.alerts.$timeEl.everyTime('1s', "a", function() {
//				time_s--;
//				$(".close_time").html(time_s);
//				if (time_s == 0 && $(".close_time").is(":visible")) {
//					$.alerts._hide();
//					if (callback_ != null) {
//						callback_();
//					}
//				}
//			}, time_s);
		},
		show : function(title, message, type, width, height) {
			$.alerts._hide();
			$.alerts._overlay('show');

			$("body")
					.append(
							'<div id="popup_container">'
									+ '<div id="popup_title"><div id="popup_title_name"></div><div id="popup_close"></div></div>'
									+ '<div id="popup_content">'
									+ '<div id="popup_message_div"><div id="popup_message_icon"></div><div id="popup_message"></div></div>'
									+ '<div id="close_button_div"><button id="submit_button">' + $.alerts.queren
									+ '</button><button id="close_button">' + $.alerts.quxiao + '</button></div>'
									+ '<div id="close_time_div"><span class="close_time">' + $.alerts.time_s
									+ '</span>秒后,窗口将自动关闭</div>' + '</div>' + '</div>');
			$("#popup_title_name").html(title);
			$("#popup_content").addClass(type);
			$("#popup_message").html(message);
			$("#popup_content").css("height", height - 40);
			$("#popup_message_div").css("height", height - 85);
			$("#popup_message").css("width", width - 90).css("height", height - 115).css("overflow", "auto");

			$("#popup_container").css({
				position : "fixed",
				zIndex : 99999,
				padding : 0,
				margin : 0,
				width : width,
				height : height,
				background : '#fff',
				fontFamily : "Microsoft YaHei",
				fontSize : 12
			});

			$.alerts._reposition();
		},
		_hide : function() {
			$("#popup_container").remove();
			$.alerts._overlay('hide');
			// 计时器有问题，暂时关闭了 zyguo
//			if ($.alerts.$timeEl) {
//				$.alerts.$timeEl.stopTime("a");
//			}
			// $('body').stopTime();
			// zyguo end
		},
		_hideAndCallBack : function(callback, argument) {
			$.alerts.flag = 1;
			$("#popup_container").remove();
			$.alerts._overlay('hide');
			if (callback != null && argument != null) {
				callback(argument);
			}
			if (callback != null && argument == null) {
				callback();
			}
			$.alerts.isSubmit = "yes";
			//$('body').stopTime();
		},
		_overlay : function(status) {
			switch (status) {
			case 'show':
				$.alerts._overlay('hide');
				$("BODY").append('<div id="popup_overlay"></div>');
				$("#popup_overlay").css({
					position : 'fixed',
					zIndex : 99998,
					top : '0px',
					left : '0px',
					width : '100%',
					height : $(document).height(),
					background : $.alerts.overlayColor,
					opacity : $.alerts.overlayOpacity
				});
				break;
			case 'hide':
				$("#popup_overlay").remove();
				break;
			}
		},
		_reposition : function() {
			var top = (($(window).height() / 2) - ($("#popup_container").outerHeight()) / 2 - 150);
			var left = (($(window).width() / 2) - ($("#popup_container").outerWidth()) / 2);
			if (top < 0)
				top = 0;
			if (left < 0)
				left = 0;
			$("#popup_container").css({
				top : top + 'px',
				left : left + 'px'
			});
		},
		pageLock : function(status, msg) {
			if (msg == "" || msg == undefined || msg == null) {
				msg = "正在加载，请稍后...";
			}
			if (status == "show") {
				if ($("#page_lock").size() > 0) {
					status = "refresh";
				}
                if($("#appModalLoading").size() > 0){
                    return false
                }
			} else {

			}

			switch (status) {
			case 'show':
				$.alerts._overlay('hide');
				var $page_lock = $('<div id="page_lock"><img class="page_lock_icon" src="' + ctx
						+ '/resources/pt/images/loading.gif"><div class="page_lock_msg">' + msg + '</div></div>');
				$page_lock.css({
					position : 'fixed',
					zIndex : 99998,
					top : '0px',
					left : '0px',
					width : '100%',
					textAlign : 'center',
					height : $(document).height(),
					background : $.alerts.overlayColor,
					opacity : $.alerts.overlayOpacity
				});
				$page_lock.find(".page_lock_icon").css({
					marginTop : $(window).height() / 2 - 100
				});
				$page_lock.find(".page_lock_msg").css({
					color : '#fff',
					marginTop : '2px'
				});
				$page_lock.appendTo($("BODY"));
				break;
			case 'refresh':
				$(".page_lock_msg").html(msg);
				$("#page_lock").show();
				break;
			case 'hide':
				$("#page_lock").remove();
				break;
			}
		},
		dialog_ : function(jsonArray) {
			var options = {
				title : "弹框标题",
				/** ***标题***** */
				autoOpen : true,
				/** ***初始化之后，是否立即显示对话框***** */
				modal : true,
				/** ***是否模式对话框***** */
				closeOnEscape : true,
				/** ***当用户按 Esc 键之后，是否应该关闭对话框***** */
				/** ***窗口关闭后事件 add by wujx 20160902**** */
				close : function() {
					closeDialog(jsonArray.dialogId || "dialogModule");
				},
				draggable : true,
				/** ***是否允许拖动***** */
				resizable : true,
				/** ***是否可以调整对话框的大小***** */
				// stack : false, /*****对话框是否叠在其他对话框之上******/
				defaultBtnName : "取消",
				height : 400,
				/** ***标题***** */
				width : 860,
				/** ***标题***** */
				buttons : {}
			};
			options = $.extend(true, options, jsonArray);
			var openFunction = options.open;
			if (openFunction != null && openFunction != undefined) {
				options.open = function() {
					if (jsonArray.defaultBtnName != null) {
						$(".ui-dialog-buttonset button").each(function() {
							if ($(this).text() == jsonArray.defaultBtnName) {
								$(this).attr("class", 'cancel');
							}
						});
					} else {
						$(".ui-dialog-buttonset button").each(function() {
							if ($(this).text() == "取消") {
								$(this).attr("class", 'cancel');
							}
						});
					}

					$(".ui-widget-overlay").css("background", "#000");
					$(".ui-widget-overlay").css("opacity", "0.5");
					if (jsonArray.content != null)
						$(".dialogcontent").html(jsonArray.content);
					openFunction();
				};
			} else {
				options.open = function() {
					if (jsonArray.defaultBtnName != null) {
						$(".ui-dialog-buttonset button").each(function() {
							if ($(this).text() == jsonArray.defaultBtnName) {
								$(this).attr("class", 'cancel');
							}
						});
					} else {
						$(".ui-dialog-buttonset button").each(function() {
							if ($(this).text() == "取消") {
								$(this).attr("class", 'cancel');
							}
						});
					}

					$(".ui-widget-overlay").css("background", "#000");
					$(".ui-widget-overlay").css("opacity", "0.5");
					if (jsonArray.content != null)
						$(".dialogcontent").html(jsonArray.content);
				};
			}
			// if(jsonArray.defaultBtnName!=null){
			// options.buttons[jsonArray.defaultBtnName] = function() {
			// $(this).dialog( "close" );
			// };
			// }else{
			// options.buttons.取消 = function() {
			// $(this).dialog( "close" );
			// };
			// }

			$("#dialogModule").dialog(options);
		},
		closeDialog_ : function() {
			$("#dialogModule").dialog("close");
		}
	};
	// 提示框5秒自动关闭
	oAlert = function(message, callback, title, width, height) {
		$.alerts.alert(message, callback, title, width, height);
	};
	// 提示框不自动关闭
	oAlert2 = function(message, callback, title, width, height) {
		$.alerts.alert2(message, callback, title, width, height);
	};
	// 确认框
	oConfirm = function(message, callback, argument, title, width, height) {
		$.alerts.alert3(message, callback, title, argument, width, height);
	};
	// 确认框(取消带回调)
	oConfirm2 = function(obj) {
		$.alerts.alert4(obj);
	};
	// 正在加载，遮罩
	pageLock = function(status) {
		$.alerts.pageLock(status);
	};
	// 打开弹出框
	showDialog = function(jsonArray) {
		str = '<div id="dialogModule" title="" style="padding:0;margin:0; display:none;"><div class="dialogcontent"></div></div>';
		if ($("#dialogModule .dialogcontent").html() == undefined) {
			$("body").after(str);
		}
		$.alerts.dialog_(jsonArray);
	};
	closeDialog = function() {
		$.alerts.closeDialog_();
	};

	$.fn.oDialog = function(option) {
		return this.each(function() {
			if (typeof option == "string") {
				$(this).dialog(option);
			} else {
				if (option.open != null) {
					var callback = option.open;
					option.open = function(e) {
						$.fn.oDialog.defaults.open.call(this, e);
						callback.call(this, e);
					};
				} else {
					option.open = function(e) {
						$.fn.oDialog.defaults.open.call(this, e);
					};
				}
				$(this).dialog(option);
			}
		});
	};
	$.fn.oDialog.defaults = {
		open : function(e) {
			$(".ui-dialog-buttonset button").each(function() {
				if ($(this).text() == "取消") {
					$(this).attr("class", 'cancel');
				}
			});
			$(".ui-widget-overlay").css("background", "#000");
			$(".ui-widget-overlay").css("opacity", "0.5");
		}
	};
	// availableTags,数组对象
	$.fn.Autocomplete = function(availableTags) {
		function split(val) {
			return val.split(/;\s*/);
		}
		function extractLast(term) {
			return split(term).pop();
		}
		$(this)
		// don't navigate away from the field on tab when selecting an item
		.bind("keydown", function(event) {
			if (event.keyCode === $.ui.keyCode.TAB && $(this).data("ui-autocomplete").menu.active) {
				event.preventDefault();
			}
		}).autocomplete({
			minLength : 0,
			source : function(request, response) {
				// delegate back to autocomplete, but extract the last term
				response($.ui.autocomplete.filter(availableTags, extractLast(request.term)));
			},
			focus : function() {
				// prevent value inserted on focus
				return false;
			},
			select : function(event, ui) {
				var terms = split(this.value);
				// remove the current input
				terms.pop();
				// add the selected item
				terms.push(ui.item.value);
				// add placeholder to get the comma-and-space at the end
				terms.push("");
				this.value = terms.join(";");
				return false;
			}
		});
	};
})(jQuery);