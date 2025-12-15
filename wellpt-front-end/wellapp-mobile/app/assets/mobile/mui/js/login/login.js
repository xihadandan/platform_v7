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
	$.plusReady(function(){
		// $("#setting")[0].classList.remove("mui-hidden");
		$("#setting")[0].addEventListener('tap', function(event) {
			plus.io.requestFileSystem(plus.io.PRIVATE_WWW, function( fs ) {
				// 可通过fs进行文件操作 
				// $.toast( "File system name: " + fs.name );
				mui.openWindow({
					url: "file://" + fs.root.fullPath + "html/setting.html",
					id: 'setting',
					preload: true,
					show: {
						aniShow: 'pop-in'
					},
					styles: {
						popGesture: 'hide'
					},
					waiting: {
						autoShow: false
					}
				});
				// 通过fs.root获取DirectoryEntry对象进行操作 
				// fs.root 
			}, function ( e ) {
				mui.toast( "Request file system failed: " + e.message );
			});
		})
	})
	var remember = false;
	var tmpRemember = localStorage.getItem("rememberLogin");
	if (tmpRemember == true || tmpRemember == "true") {
		remember = tmpRemember;
		document.getElementById("remember-pwd").setAttribute("checked",true);
		var username = localStorage.getItem("remember_username");
		var password = localStorage.getItem("remember_password");
		document.getElementById("account").value = username;
		document.getElementById("password").value = password;
	}
	$("#toggle-view")[0].addEventListener("tap",function(){
		var i = this.children[0];
		if(i.classList.contains('icon-unviewable')){
			i.classList.remove("icon-unviewable");
			i.classList.add("icon-visible");
			$("#password")[0].setAttribute("type","text")
		}else{
			i.classList.remove("icon-visible");
			i.classList.add("icon-unviewable");
			$("#password")[0].setAttribute("type","password")
		}	
	})
	$("#login")[0].addEventListener('click', function() {
		var username = document.getElementById("account").value;
		var password = document.getElementById("password").value;
		doLogin(username, password);
	})
	document.getElementById("remember-pwd").addEventListener('change', function(event) {
		remember = this.checked;
	});
	function doLogin(username, password) {
		if(isBlank(username)){
			return $.alert("用户名不能为空");
		}else if(isBlank(password)){
			return $.alert("密码不能为空");
		}
		localStorage.setItem("rememberLogin", remember);
		if (remember == true || remember == "true") {
			localStorage.setItem("remember_username", username);
			localStorage.setItem("remember_password", password);
		} else {
			localStorage.removeItem("remember_username");
			localStorage.removeItem("remember_password");
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