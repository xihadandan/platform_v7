<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>
<head>
<%@ include file="/pt/common/taglibs.jsp"%>
<%@ include file="/pt/common/meta.jsp"%>
<title id="">${title}</title>
<link type="text/css" rel="stylesheet" href="${ctx}/mobile/mui/css/mui.min.css" />
<style type="text/css">
body {
	margin: 10px 4px;
	background-color: white;
}
</style>
</head>
<body>
	${content}
</body>
</html>
