/*
 * @(#)2012-10-29 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.access;

import com.wellsoft.context.enums.LoginType;
import com.wellsoft.pt.security.util.DesCipherUtil;
import com.wellsoft.pt.security.util.TenantContextHolder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 如何描述该类
 *
 * @author lilin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-29.1	lilin		2012-10-29		Create
 * 2012-12-25.1	lilin		2012-12-25		清除不需要的代码
 * </pre>
 * @date 2012-10-29
 */
public class LoginAuthenticationProcessingFilter extends UsernamePasswordAuthenticationFilter {
    public static final String SPRING_SECURITY_FORM_TENANT_KEY = "tenant";

    public static final String SPRING_SECURITY_FORM_UNIT_ID_KEY = "unitId";

    public static final String SPRING_SECURITY_FORM_TENANT_ID_KEY = "tenantId";

    public static final String SPRING_SECURITY_FORM_LOGIN_TYPE = "loginType";
    // 登录名哈希算法
    public static final String SPRING_SECURITY_FORM_LNALG_CODE_KEY = "j_lnalg_code";
    // 密码哈希算法
    public static final String SPRING_SECURITY_FORM_PWDALG_CODE_KEY = "j_pwdalg_code";

    public static final String SPRING_SECURITY_FORM_VERIFY_CODE_KEY = "j_verify_code";

    public static final String SPRING_SECURITY_FORM_SMS_VERIFY_CODE_KEY = "j_sms_verify_code";

    public static final String SPRING_SECURITY_FORM_PWD_ECRYPT_KEY = "j_pwd_encrypt_key";

    public static final String SPRING_SECURITY_FORM_PWD_ECRYPT_TYPE = "j_pwd_encrypt_type";

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        TenantContextHolder.reset();
        String loginType = obtainLoginType(request);
        if (StringUtils.isBlank(loginType)) {
            TenantContextHolder.setLoginType(LoginType.USER);
        } else {
            TenantContextHolder.setLoginType(loginType);
        }
        request.setAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_UNIT_ID_KEY,
                obtainJUnitId(request));
        request.setAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_TENANT_KEY, obtainTenant(request));
        request.setAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_TENANT_ID_KEY,
                obtainTenantId(request));
        request.setAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_LNALG_CODE_KEY,
                obtainLnalgCode(request));
        request.setAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_PWDALG_CODE_KEY,
                obtainPwdalgCode(request));
        request.setAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_VERIFY_CODE_KEY,
                obtainVerifyCode(request));
        return super.attemptAuthentication(request, response);
    }

    /**
     * @param request
     * @return
     */
    private String obtainJUnitId(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_UNIT_ID_KEY);
    }

    /**
     * @param request
     * @return
     */
    private String obtainTenant(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_TENANT_KEY);
    }

    /**
     * @param request
     * @return
     */
    private Object obtainTenantId(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_TENANT_ID_KEY);
    }

    /**
     * @param request
     * @return
     */
    private String obtainLoginType(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_LOGIN_TYPE);
    }

    /**
     * @param request
     * @return
     */
    private String obtainVerifyCode(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_VERIFY_CODE_KEY);
    }

    /**
     * @param request
     * @return
     */
    private Object obtainLnalgCode(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_LNALG_CODE_KEY);
    }

    /**
     * @param request
     * @return
     */
    private Object obtainPwdalgCode(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_PWDALG_CODE_KEY);
    }

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        String encryptKey = request.getParameter(SPRING_SECURITY_FORM_PWD_ECRYPT_KEY);
        String encryptType = request.getParameter(SPRING_SECURITY_FORM_PWD_ECRYPT_TYPE);
        String password = super.obtainPassword(request);
        if (StringUtils.isBlank(encryptType)) {
            return password;
        } else if ("1".equalsIgnoreCase(encryptType)) {
            return new String(Base64.decodeBase64(password));
        } else if ("2".equalsIgnoreCase(encryptType)) {
            return DesCipherUtil.decrypt(password, encryptKey);
        }
        return password;
    }
}
