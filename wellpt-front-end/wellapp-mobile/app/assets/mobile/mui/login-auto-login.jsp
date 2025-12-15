<%@page import="com.wellsoft.pt.utils.security.SpringSecurityUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html class="ui-page-login">

<head>
<%@ include file="/pt/common/taglibs.jsp"%>

<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="org.springframework.security.core.Authentication"%>

<%
	// 手机单点登录
	String accessToken = (String) request.getParameter("wellpt_access_token");
	Authentication authentication = null;
	if(SpringSecurityUtils.getCurrentUser()!=null) {
		// 已经登录
		response.sendRedirect(contextPath + "/web/app/pt-mobile/pt-mobile-base.html");
		return;
	}else if(StringUtils.isNotBlank(accessToken) && (authentication = SpringSecurityUtils.removeAccessToken(accessToken))!=null ){
		// 单点登录
		SpringSecurityUtils.setAuthentication(authentication);
		response.sendRedirect(contextPath + "/web/app/pt-mobile/pt-mobile-base.html");
		return;
	}
%>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<META HTTP-EQUIV="Expires" CONTENT="0">
<title>登录</title>
<link href="css/mui.min.css" rel="stylesheet" />
<style>
body,.mui-content{
	background:#fff;
}
.mui-table-view:before,.mui-table-view:after{
	background:none;
}
.mui-input-group:before,.mui-input-group:after{
	background:none;
}
.area {
	margin: 20px auto 0px auto;
}

.mui-input-group {
	margin-top: 10px;
}

.mui-input-group:first-child {
	margin-top: 20px;
}

.mui-input-group label {
	width: 22%;
}

.mui-input-row label ~input, .mui-input-row label ~select, .mui-input-row label ~textarea {
	width: 78%;
}

.mui-checkbox input[type=checkbox], .mui-radio input[type=radio] {
	top: 6px;
}

.mui-content-padded {
	margin-top: 25px;
}

.mui-btn {
	padding: 10px;
	background: #fb8505;
    border: 1px solid #fb8505;
	color:#fff;
}
.mui-btn:last-child{
	background:none;
	border-color:#ccc;
	color:#666;
}

.link-area {
	display: block;
	margin-top: 25px;
	text-align: center;
}

.spliter {
	color: #bbb;
	padding: 0px 8px;
}

.oauth-area {
	position: absolute;
	bottom: 20px;
	left: 0px;
	text-align: center;
	width: 100%;
	padding: 0px;
	margin: 0px;
}

.oauth-area .oauth-btn {
	display: inline-block;
	width: 50px;
	height: 50px;
	background-size: 30px 30px;
	background-position: center center;
	background-repeat: no-repeat;
	margin: 0px 20px;
	/*-webkit-filter: grayscale(100%); */
	border: solid 1px #ddd;
	border-radius: 25px;
}

.oauth-area .oauth-btn:active {
	border: solid 1px #aaa;
}

.oauth-area .oauth-btn.disabled {
	background-color: #ddd;
}
</style>
<script src="${ctx}/resources/requirejs/2.3.2/require.min.js?"></script>
<script  type="text/javascript">
	requirejs.config({
		baseUrl : "${ctx}/mobile",
		urlArgs : 'v=5.3.1.114326',
		waitSeconds : 0,
		paths : {
			"jquery" : "${ctx}/mobile/mui/js/mui",
			"login" : "${ctx}/mobile/mui/js/login/login-auto-login"
		},
		shim : {
			"jquery" : {
				deps : [],
				exports : "jquery"
			},
			"login" : {
				deps : [ "jquery" ],
				exports : "login"
			}
		}
	});
	requirejs.onError = function(error) {
		alert("JavaScript模块加载失败：" + error.message);
		throw error;
	}
</script>
</head>

<body>
	<header class="mui-bar mui-bar-nav">
		<h1 class="mui-title">登录</h1>
	</header>
	<div class="mui-content">
		<form id='login-form' class="mui-input-group">
			<div class="mui-input-row">
				<!-- <label>账号</label> --> <input id='account' type="text" class="mui-input-clear mui-input" placeholder="请输入账号">
			</div>
			<div class="mui-input-row">
				<!-- <label>密码</label>  --><input id='password' type="password" class="mui-input-clear mui-input" placeholder="请输入密码">
			</div>
			<ul class="mui-table-view mui-table-view-chevron" id="autoLoginUL">
				<li class="mui-table-view-cell">下次自动登录
					<div id="autoLogin" class="mui-switch">
						<div class="mui-switch-handle"></div>
					</div>
				</li>
			</ul>
		</form>
		<div class="mui-input-group">
			<div class="mui-content-padded">
				<button type="button" id='login' class="mui-btn mui-btn-block">登录</button>
				<div class="link-area" style="display: none;">
					<a id='reg'>注册账号</a> <span class="spliter">|</span> <a id='forgetPassword'>忘记密码</a>
				</div>
			</div>
		</div>
		<div class="mui-content-padded oauth-area"></div>
	</div>
	<script type="text/javascript">
		window.ctx = "${ctx}";
		require([ "jquery", "login" ], function($) {
			// require([ "pt/js/app/app" ]);
			$.plusReady(function() {
				// plus在外部设置自动登录
				document.getElementById("autoLoginUL").style.display = "none";
			})
		});
	</script>
</body>

</html>