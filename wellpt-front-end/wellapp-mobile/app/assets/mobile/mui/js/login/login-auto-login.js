define([ "jquery" ], function($) {
	var indexUrl = location.origin+ctx+ "/web/app/pt-mobile/pt-mobile-base.html";
	if ($.os.plus && $.os.android) {
		indexUrl = location.origin+ctx+ "/web/app/pt-mobile/pt-mobile-base.html";
	}
	var loginUrl = location.origin+ctx+ "/webservices/wellpt/rest/service";
	if ($.os.plus && $.os.android) {
		loginUrl = location.origin+ctx+"/webservices/wellpt/rest/service";
	}
	
	function isBlank(string){
		return typeof string === "undefined" || string == null || string === "";
	}
	function _loginSuccess(access_token) {
		access_token = access_token || "";
		if(access_token){
			indexUrl = location.origin+ctx+"/mobile/mui/login.jsp";
		}
		var date = new Date();
		var t = date.getTime();
		$.openWindow({
			url : indexUrl + "?wellpt_access_token="+access_token,
			id : "index-default-" + t,
			waiting : {
				autoShow : true, // 自动显示等待框，默认为true
				title : '正在加载...' // 等待对话框上显示的提示内容
			}
		})
	}
	function _loginFailure(msg) {
		alert(msg || "登录失败!");
	}

	var autoLogin = false;
	var tmpAutoLogin = localStorage.getItem("autoLogin");
	if (tmpAutoLogin == "true") {
		autoLogin = true;
		document.getElementById("autoLogin").classList.add("mui-active");
		var username = localStorage.getItem("autoLogin_username");
		var password = localStorage.getItem("autoLogin_password");
		document.getElementById("account").value = username;
		document.getElementById("password").value = "xxxxxx";
		doLogin(username, password);
	}

	document.getElementById("autoLogin").addEventListener('toggle', function(event) {
		autoLogin = event.detail.isActive;
	});

	$("#login")[0].addEventListener('click', function() {
		var username = document.getElementById("account").value;
		var password = document.getElementById("password").value;
		doLogin(username, password);
	})

	function doLogin(username, password) {
		if(isBlank(username)){
			return $.alert("用户名不能为空");
		}else if(isBlank(password)){
			return $.alert("密码不能为空");
		}
		localStorage.setItem("autoLogin", autoLogin);
		if (autoLogin == true || autoLogin == "true") {
			localStorage.setItem("autoLogin_username", username);
			localStorage.setItem("autoLogin_password", password);
		} else {
			localStorage.removeItem("autoLogin_username");
			localStorage.removeItem("autoLogin_password");
		}
		var jsonData = {};
		jsonData.apiServiceName = "security.login";
		$.ajax({
			type : "POST",
			url : loginUrl,
			async : false,
			data : {
				"username" : username,
				"password" : password,
				"json" : JSON.stringify(jsonData)
			},
			success : function(result) {
				var data = result;
				if(data.success){
					_loginSuccess(data.access_token);
				} else {
					_loginFailure(data.msg);
				}
			},
			error : function() {
				_loginFailure();
			}
		});
	}
});