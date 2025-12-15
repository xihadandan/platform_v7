<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="base"
       value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/"/>

<%--
<c:set var="base" value="${pageContext.request.contextPath}"/>
--%>


<%--js全局变量 --%>
<script>
    var WEB_ROOT = "${base}";//根路径
    var _CSRF_HEADER = "${_csrf.headerName}";
    var _CSRF_TOKEN = "${_csrf.token}";
</script>