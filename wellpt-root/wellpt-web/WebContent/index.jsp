<%@page import="com.wellsoft.context.Context" %>
<%@page import="com.wellsoft.context.config.Config" %>
<%@page import="com.wellsoft.pt.security.core.userdetails.InternetUserDetails" %>
<%@page import="com.wellsoft.pt.security.core.userdetails.UserDetails" %>
<%@ page import="com.wellsoft.pt.security.support.CasLoginUtils" %>
<%@ page import="com.wellsoft.pt.security.util.SpringSecurityUtils" %>
<%
    UserDetails user = SpringSecurityUtils.getCurrentUser();
//TODO: 互联网用户的主页
    if (user != null) {
        if (user instanceof InternetUserDetails) {
            request.getRequestDispatcher("/passport/iuser/login/success").forward(request, response);
            return;
        }
        request.getRequestDispatcher("/tenant/" + user.getTenant()).forward(request, response);
        return;
    }

    if (Context.isOauth2Enable()) {//启用oauth2认证服务
        request.getRequestDispatcher("/iuser/login").forward(request, response);
        return;
    }

    if (CasLoginUtils.isUseCas()) {
        response.sendRedirect(request.getContextPath() + "/passport/admin/main");
    } else {
        String forwardUrl = "/tenant/" + Config.DEFAULT_TENANT;
        request.getRequestDispatcher(forwardUrl).forward(request, response);
    }
%>