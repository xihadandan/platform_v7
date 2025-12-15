package com.wellsoft.pt.security.cas.filter;

import com.wellsoft.context.enums.LoginType;
import com.wellsoft.pt.security.access.LoginAuthenticationProcessingFilter;
import com.wellsoft.pt.security.util.TenantContextHolder;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Cas过滤器
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-7-17.1	Administrator		2014-7-17		Create
 * </pre>
 * @date 2014-7-17
 */
public class CasLoginAuthenticationFilter extends CasAuthenticationFilter {

    public static final String SPRING_SECURITY_FORM_TENANT_KEY = "tenant";
    public static final String SPRING_SECURITY_FORM_TENANT_ID_KEY = "tenantId";

    public static final String SPRING_SECURITY_FORM_LOGIN_TYPE = "loginType";

    public static final String SPRING_SECURITY_FORM_VERIFY_CODE_KEY = "j_verify_code";

    public static final String SPRING_SECURITY_FORM_SMS_VERIFY_CODE_KEY = "j_sms_verify_code";

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {

        TenantContextHolder.reset();
        String loginType = obtainLoginType(request);
        request.getParameter("loginType");
        request.getParameter("successurl");
        request.getParameterNames();
        request.getUserPrincipal();
        request.getRequestURL();
        if (StringUtils.isBlank(loginType)) {
            TenantContextHolder.setLoginType(LoginType.USER);
        } else {
            TenantContextHolder.setLoginType(loginType);
        }

        // TenantContextHolder.setLoginType(LoginType.SUPER_ADMIN);
        request.setAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_TENANT_KEY, obtainTenant(request));
        request.setAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_TENANT_ID_KEY,
                obtainTenantId(request));
        request.setAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_VERIFY_CODE_KEY,
                obtainVerifyCode(request));

        return super.attemptAuthentication(request, response);
    }

    /**
     * @param request
     * @return
     */
    private Object obtainTenantId(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_TENANT_ID_KEY);
    }

    private String obtainTenant(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_TENANT_KEY);
    }

    private String obtainLoginType(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_LOGIN_TYPE);
    }

    private String obtainVerifyCode(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_VERIFY_CODE_KEY);
    }
}
