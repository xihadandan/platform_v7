package com.wellsoft.pt.security.cas;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.web.AjaxUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WellCasAuthenticationEntryPoint extends CasAuthenticationEntryPoint {

    private String serviceUrlBak = null;
    private String tenantLoginUrl = null;
    private String superAdminLoginUrl = null;

    @Override
    protected String createServiceUrl(HttpServletRequest request, HttpServletResponse response) {
        if (serviceUrlBak == null)
            serviceUrlBak = getServiceProperties().getService();
        if (serviceUrlBak != null) {
            String ctx = request.getContextPath();
            String queryString = request.getQueryString();
            String requestURI = request.getRequestURI();
            String logintype = "1";
            String successurl = "";
            if (requestURI.endsWith("superadmin/login/success")) {
                logintype = "3";// 超级管理员暂时不考虑.
                if (StringUtils.isNotBlank(superAdminLoginUrl)) {
                    super.setLoginUrl(superAdminLoginUrl);
                }
                // successurl = "&successurl=" + "/superadmin/login/success";
            } else {
                if (StringUtils.isNotBlank(tenantLoginUrl)) {
                    super.setLoginUrl(tenantLoginUrl);
                }
                // successurl = "&successurl=" + "/passport/user/login/success";
            }
            requestURI = requestURI.substring(requestURI.indexOf(ctx) + ctx.length(), requestURI.length());
            String serviceUrl = "";
            /*
             * if (!requestURI.equals("/") && requestURI.length() > 0) {
             * //serviceUrl = "?" + "loginType"; //serviceUrl += "=" +
             * logintype; if (StringUtils.isNotBlank(queryString)) {
             * //serviceUrl += "&" + queryString; } }
             */
            if (!AjaxUtils.isAjaxRequest(request)) {
                if (serviceUrlBak.indexOf("?") > -1) {
                    successurl = "&successurl=" + requestURI;
                } else {
                    successurl = "?successurl=" + requestURI;
                }
                if (StringUtils.isNotBlank(queryString)) {
                    successurl = successurl + "?" + URLEncoder.encode(queryString);
                }
            }

            String service = "";
            // 在建管单点登录系统创建时，新增代码
            String host = Config.getValue("security.cas.application.url");
            String defaultLoginUrl = Config.getValue("security.cas.application.login.url");
            String loginurl = defaultLoginUrl;
            if (StringUtils.isNotBlank(host) && StringUtils.isNotBlank(defaultLoginUrl)) {// 组装本系统登录地址
                if (requestURI.indexOf("getUserDetails") != -1) {
                    loginurl = host + "/xxgs/loginForm";
                }
                try {

                    if (serviceUrlBak.indexOf("?") > -1) {
                        service = serviceUrlBak + serviceUrl + "&loginurl=" + URLEncoder.encode(loginurl, "utf-8")
                                + successurl + "&loginType=" + logintype;
                    } else {
                        service = serviceUrlBak + serviceUrl + "?loginurl=" + URLEncoder.encode(loginurl, "utf-8")
                                + successurl + "&loginType=" + logintype;
                    }

                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                service = serviceUrlBak + serviceUrl + successurl + "&loginType=" + logintype;
            }

            getServiceProperties().setService(service);
        }
        return super.createServiceUrl(request, response);
    }

    /**
     * @return the tenantLoginUrl
     */
    public String getTenantLoginUrl() {
        return tenantLoginUrl;
    }

    /**
     * @param tenantLoginUrl 要设置的tenantLoginUrl
     */
    public void setTenantLoginUrl(String tenantLoginUrl) {
        this.tenantLoginUrl = tenantLoginUrl;
    }

    /**
     * @return the superAdminLoginUrl
     */
    public String getSuperAdminLoginUrl() {
        return superAdminLoginUrl;
    }

    /**
     * @param superAdminLoginUrl 要设置的superAdminLoginUrl
     */
    public void setSuperAdminLoginUrl(String superAdminLoginUrl) {
        this.superAdminLoginUrl = superAdminLoginUrl;
    }

}
