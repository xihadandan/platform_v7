/*
 * @(#)2015-5-27 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.web;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.LoginType;
import com.wellsoft.context.util.sm.SM3Util;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.security.access.LoginAuthenticationProcessingFilter;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetailsServiceProvider;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.TenantContextHolder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.wss4j.dom.WSConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-5-27.1	zhulh		2015-5-27		Create
 * </pre>
 * @date 2015-5-27
 */
@Controller
@RequestMapping(value = "/security/sign")
public class SingleSignOnController extends BaseController {
    private RequestCache requestCache = new HttpSessionRequestCache();
    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceProvider userDetailsServiceProvider;

    @Autowired
    private Map<String, UserDetailsServiceProvider> userDetailsProviders;

    @Autowired
    private List<CompositeSessionAuthenticationStrategy> sessionAuthenticationStrategies;

    /**
     * 单点登录
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/on", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ResultMessage> on(HttpServletRequest request,
                                            HttpServletResponse response,
                                            @RequestParam(value = "username", required = true) String username,
                                            @RequestParam(value = "password", required = true) String password,
                                            @RequestParam(value = "tenantId", required = true) String tenantId,
                                            @RequestParam(value = "timestamp", required = true) Long t,
                                            @RequestParam(value = "lnAlgCode", required = false, defaultValue = "1") String lnAlgCode,
                                            @RequestParam(value = "pwdAlgCode", required = false, defaultValue = "1") String pwdAlgCode) {
        ResultMessage resultMessage = new ResultMessage("", false);
        String tenant = request.getParameter("tenant");
        String unitId = request.getParameter("unitId");
        String loginType = request.getParameter("loginType");
        // String idNumber = request.getParameter("idNumber");
        // String unitName = request.getParameter("unitName");
        // String textCert = request.getParameter("textCert");
        // String certType = request.getParameter("certType");
        // String textSignData = request.getParameter("textSignData");
        String jVerifyCode = request.getParameter("j_verify_code");
        String pwType = request.getParameter("pw_type");
        loginType = StringUtils.isBlank(loginType) ? LoginType.RESTful : loginType;
        TenantContextHolder.setLoginType(loginType);
        request.setAttribute(
                LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_LNALG_CODE_KEY, lnAlgCode);
        request.setAttribute(
                LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_PWDALG_CODE_KEY,
                pwdAlgCode);
        request.setAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_UNIT_ID_KEY,
                unitId);
        request.setAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_TENANT_KEY,
                tenant);
        request.setAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_TENANT_ID_KEY,
                tenantId);
        request.setAttribute(
                LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_VERIFY_CODE_KEY,
                jVerifyCode);
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
        try {
            IgnoreLoginUtils.login(Config.DEFAULT_TENANT, tenantId); // 登录默认租户
            UserDetails userDetails = getUserDetailsServiceProvider(loginType).getUserDetails(
                    username);
            String pw = "";
            if (WSConstants.PW_DIGEST.equals(pwType)) {// 密文
                pw = password;
            } else {
                pw = SM3Util.encrypt(password + "{" + username.toLowerCase() + "}");
            }
            if (userDetails != null && pw.equalsIgnoreCase(userDetails.getPassword())) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        password, userDetails.getAuthorities());
                auth.setDetails(userDetails.getPassword());
                SecurityContextHolder.getContext().setAuthentication(auth);
                for (SessionAuthenticationStrategy sessionAuthenticationStrategy : sessionAuthenticationStrategies) {
                    sessionAuthenticationStrategy.onAuthentication(auth, request, response);
                }
                resultMessage.setSuccess(true);
                resultMessage.setData("login success");
            } else {
                resultMessage.setSuccess(false);
                resultMessage.setData(userDetails == null ? "用户名[" + username + "]不存在" : "凭证不正确");
            }
        } catch (Exception ex) {
            resultMessage.setSuccess(false);
            resultMessage.setData(ex.getMessage());
            logger.error(ExceptionUtils.getStackTrace(ex));
        } finally {
            RequestContextHolder.resetRequestAttributes();
            TenantContextHolder.reset();
            IgnoreLoginUtils.logout();
        }

        // 从请求缓存获取拦截跳转登录的地址
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("redirectUrl", savedRequest.getRedirectUrl());
            dataMap.put("msg", resultMessage.getData());
            resultMessage.setData(dataMap);
            // 移除请求缓存
            requestCache.removeRequest(request, response);
        }
        return new ResponseEntity<ResultMessage>(resultMessage, HttpStatus.OK);
    }

    private UserDetailsServiceProvider getUserDetailsServiceProvider(String loginType) {
        for (UserDetailsServiceProvider userDetailsProvider : userDetailsProviders.values()) {
            if (loginType.equals(userDetailsProvider.getLoginType())) {
                return userDetailsProvider;
            }
        }
        return userDetailsServiceProvider; // default
    }

    /**
     * 单点登出
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/out", method = RequestMethod.GET)
    public String out(HttpServletRequest request, HttpServletResponse response) {
        return redirect("/security_logout");
    }
}
