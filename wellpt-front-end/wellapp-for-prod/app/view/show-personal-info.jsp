<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.wellsoft.pt.multi.org.bean.OrgUserVo,org.apache.commons.lang.StringUtils"%>
<%@include file="/pt/common/taglibs.jsp"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div style="width: 280px" id="personal-info">
	<%
		OrgUserVo bean = (OrgUserVo) request.getAttribute("user");
	%>
	<div class="row">
		<input type="hidden" value="${user.uuid}" name="userUuid" />
	</div>
	<div class="row">
		<div class="col-md-12">
			上次登录时间：
			<fmt:formatDate value="${user.lastLoginTime}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate>
		</div>
	</div>
	<%
		if (StringUtils.isNotBlank(bean.getPhotoUuid())) {
	%>
	<div class="row" style="text-align: center;">
		<div class="col-md-12" style="text-align: center">
			<img class="img-responsive img-circle" src="${ctx}/org/user/view/photo/${user.photoUuid}"/>
		</div>
	</div>
	<%
		}
	%>
	<div class="row" style="text-align: center">
		<div class="col-md-12">
			<h3>您好，${user.userName}</h3>
		</div>
	</div>
	<div class="row" style="text-align: center">
		<div class="col-md-12">
			<%
				// OrgUserVo bean = (OrgUserVo) request.getAttribute("user");
				if (bean != null && bean.getMainJobName() != null) {
					String majorJobName = bean.getMainJobName();
					if (majorJobName.contains("/")) {
						out.write(majorJobName.substring(0, majorJobName.lastIndexOf("/")));
						out.write(majorJobName.substring(majorJobName.lastIndexOf("/") + 1));
					} else {
						out.write(majorJobName);
					}
				}
			%>
		</div>
	</div>
	<div class="row" style="text-align: center">
		<div class="col-md-12">
			<ul class="nav nav-pills nav-justified">
				<li class="modify-pwd"><a href="#" > <span
						class="ui-wIcon glyphicon glyphicon-lock"></span>修改密码
				</a></li>
				<li class="setting"><a href="#"> <span
						class="ui-wIcon glyphicon glyphicon-cog"></span>设置
				</a></li>
				<c:if test="${user.type == 1 || user.type == 2}">
					<li class="admin"><a href="#"> <span
							class="ui-wIcon glyphicon glyphicon-blackboard"></span>管理
					</a></li>
				</c:if>
				<li class="logout"><a href="#"> <span
						class="ui-wIcon glyphicon glyphicon-off"></span>注销
				</a></li>
			</ul>
		</div>
	</div>
</div>
<style type="text/css">

#personal-info .img-responsive{
	display:inline-block;
	width:100px;
	height:100px;
	margin:10px;
}
.popover .popover-content,#personal-info .nav-pills>li>a{
	padding:10px 0 0 0 ;
}
li[menuid=personInfo]>.popover, li[menuid=personInfo]>.popover h3 {
	color: white;
	max-width: none;
	padding:10px;
	background-color: #069dd5;
}

li[menuid=personInfo]>.popover>.arrow:after {
	border-bottom-color: #069dd5;
}

li[menuid=personInfo]>.popover .nav-justified>li>a {
	color: white;
}

li[menuid=personInfo]>.popover .nav-justified>li>a:HOVER {
	background-color: #50ccf0;
}
</style>