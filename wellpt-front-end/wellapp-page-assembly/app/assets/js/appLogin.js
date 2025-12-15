define([ "server", "commons", "constant", "jquery-ui", "bootbox", "appContext", "appModal", "slimScroll", "cookie" ], function(
		server, commons, constant, ui, bootbox, appContext, appModal) {
	// 
	var StringUtils = commons.StringUtils;
	// 绑定事件
	function bindEvent($container, ui) {
		var collectFormData = function() {
			return {
				timestamp : (new Date()).getTime(),
				username : $("#j_username", $container).val(),
				password : $("#j_password", $container).val(),
				tenant : $("#tenant", $container).val(),
				unitId : $("#unitId", $container).val(),
				tenantId : $("#tenantId", $container).val(),
				unitName : $("#unitName", $container).val(),
				loginType : $("#loginType", $container).val(),
				idNumber : $("#idNumber", $container).val(),
				textCert : $("#textCert", $container).val(),
				certType : $("#certType", $container).val(),
				textSignData : $("#textSignData", $container).val()
			};
		}
		var $message = $(".message", $container);
		var showMessage = function(message){
			$message.html(message || "");
			return false;
		}
		var handleRemember = function(formData){
			if($.cookie === null || typeof $.cookie === "undefined"){
				return false;
			}else if($("#remember", $container).attr("checked") === "checked") {
				$.cookie("cookie.checked.remember", "checked", { expires: 7, path: '/' });
				$.cookie("cookie.remember.unitId", formData["unitId"], { expires: 7, path: '/' });
				$.cookie("cookie.remember.unitName", formData["unitName"], { expires: 7, path: '/' });
				$.cookie("cookie.remember.username", formData["username"], { expires: 7, path: '/' });
				$.cookie("cookie.remember.password", formData["password"], { expires: 7, path: '/' });
			}else{
				$.cookie("cookie.remember.unitId", "", { expires: 7, path: '/' });
				$.cookie("cookie.remember.unitName", "", { expires: 7, path: '/' });
				$.cookie("cookie.remember.username", "", { expires: 7, path: '/' });
				$.cookie("cookie.remember.password", "", { expires: 7, path: '/' });
				$.cookie("cookie.checked.remember", "unchecked", { expires: 7, path: '/' });
			}
		}
		$("#login-form", $container).on("submit", function(event){
			event.preventDefault();
			event.stopPropagation();
			var formData = collectFormData();
			if(StringUtils.isBlank(formData["username"])){
				return showMessage("用户名不能为空");
			}else if(StringUtils.isBlank(formData["password"])){
				return showMessage("密码不能为空");
			}else {
				handleRemember(formData);
			}
			$("#btn-login", $container).attr("disabled", "disabled");
			$.ajax({
				url : ctx + "/security/sign/on",
				type : "post",
				async : false,
				data : formData,
				complete : function() {
					$("#btn-login", $container).removeAttr("disabled");
				},
				success : function(result){
					if(result.success){
						location.href = (ctx + "/passport/user/login/success");
					}else {
						showMessage(result.data);// alert(result.data);
					}
				},
				error : function(result){
					alert("登录错误");
				}
			});
			return false;
		})
		if($.cookie && $.cookie("cookie.checked.remember") === "checked") {
			$("#remember", $container).attr("checked", "checked");
			$("#unitId", $container).val($.cookie("cookie.remember.unitId"));
			$("#unitName", $container).val($.cookie("cookie.remember.unitName"));
			$("#j_username", $container).val($.cookie("cookie.remember.username"));
			$("#j_password", $container).val($.cookie("cookie.remember.password"));
		}
	}

	var startLogin = function(options) {
		var $container = $(options.ui.element);
		$.get(ctx + "/resources/pt/html/appLogin.html", function(content) {
			var target = options.target;
			var targetWidgetId = options.targetWidgetId;
			if (constant.TARGET_POSITION.TARGET_WIDGET != target
					|| commons.StringUtils.isBlank(options.targetWidgetId)) {
				var $dialog = appModal.dialog({
					title : "登录",
					size : "small",
					message : content,
					container : $container
				});
				bindEvent($dialog, options.ui);
			} else {
				var $box = appContext.getWidgetRenderPlaceholder(targetWidgetId);
				if ($box.css('position') == 'static') {
					$remove_position = true;
					$box.addClass('position-relative');
				}
				var overlay = '<div class="widget-box-overlay"><div class="login-entry">' + content + '</div></div>';
				$box.html(overlay);
				bindEvent($box, options.ui);
			}
		});
	}
	return startLogin;
});