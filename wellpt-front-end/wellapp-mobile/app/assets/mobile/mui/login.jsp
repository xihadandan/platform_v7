<%@page import="com.wellsoft.pt.security.util.SpringSecurityUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html class="ui-page-login">

<head>
<%@ include file="/pt/common/taglibs.jsp"%>

<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="org.springframework.security.core.Authentication"%>
<%@page import="com.wellsoft.context.util.web.HttpRequestDeviceUtils"%>

<%
	// 手机单点登录
	String accessToken = (String) request
			.getParameter("wellpt_access_token"), gotoUrl = null;
	Authentication authentication = null;
	if (SpringSecurityUtils.getCurrentUser() != null) {
		// 已经登录
		if (HttpRequestDeviceUtils.isTablet(request)) {
			gotoUrl = "/security_homepage";
		} else {
			gotoUrl = "/web/app/pt-mobile/pt-mobile-base.html";
		}
		response.sendRedirect(contextPath + gotoUrl);
		return;
	} else if (StringUtils.isNotBlank(accessToken)
			&& (authentication = SpringSecurityUtils
					.removeAccessToken(accessToken)) != null) {
		// 单点登录
		SpringSecurityUtils.setAuthentication(authentication);
		if (HttpRequestDeviceUtils.isTablet(request)) {
			gotoUrl = "/security_homepage";
		} else {
			gotoUrl = "/web/app/pt-mobile/pt-mobile-base.html";
		}
		response.sendRedirect(contextPath + gotoUrl);
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
<link href="customfont/style.css" rel="stylesheet" />
<link href="css/login.css" rel="stylesheet" />
<script type="text/javascript">
	window.ctx = "${ctx}";
</script>
<script src="${ctx}/resources/requirejs/2.3.2/require.min.js?"></script>
<script type="text/javascript">
	requirejs.config({
		baseUrl : "${ctx}/mobile",
		urlArgs : 'v=5.3.1.114326',
		waitSeconds : 0,
		paths : {
			"jquery" : "${ctx}/mobile/mui/js/mui",
			"login" : "${ctx}/mobile/mui/js/login/login"
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
	<div class="login-page">
		<div class="login-bg">
			<img src='images/loginBg.jpg' width=100% height=100%>
		</div>
		<div class="logo">
			<div class="logo-img"></div>
			<div class="logo-title">
				<h3>一体化办公</h3>
			</div>
		</div>
		<div class="mui-content">
			<form id='login-form' class="mui-input-group">
				<div class="mui-input-row">
					<label for="account"><i class="mui-icon mui-icon-person"></i></label><input id='account' type="text"
						class="mui-input-clear mui-input" placeholder="请输入账号">
				</div>
				<div class="mui-input-row">
					<label for="password"><i class="mui-icon mui-icon-locked"></i></label><input id='password' type="password"
						class="mui-input-clear mui-input" placeholder="请输入密码"> <a id="toggle-view"><i
						class="mui-icon icon-unviewable"></i></a>
				</div>
			</form>
			<div class="mui-input-group">
				<div class="mui-content-padded">
					<button type="button" id='login' class="mui-btn mui-btn-block">登录</button>
					<div class="link-area mui-clearfix">
						<a id='reg' class="mui-pull-left mui-checkbox mui-left"> <label>记住密码</label> <input name="checkbox1"
							value="Item 1" type="checkbox" id="remember-pwd">
						</a> <a id='setting' class="mui-pull-right mui-hidden">设备设置</a>
					</div>
				</div>
			</div>
			<div class="mui-content-padded oauth-area"></div>
		</div>
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