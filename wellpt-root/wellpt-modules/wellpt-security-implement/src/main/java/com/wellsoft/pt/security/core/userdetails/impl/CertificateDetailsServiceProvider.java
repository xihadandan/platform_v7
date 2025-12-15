/*
 * @(#)2013-12-2 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.core.userdetails.impl;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.LoginType;
import com.wellsoft.context.util.sm.SM3Util;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.access.LoginAuthenticationProcessingFilter;
import com.wellsoft.pt.security.core.authentication.encoding.PasswordAlgorithm;
import com.wellsoft.pt.security.core.userdetails.DefaultUserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.FJCAUtils;
import fjca.FJCAApps;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-2.1	zhulh		2013-12-2		Create
 * </pre>
 * @date 2013-12-2
 */
@Service
@Transactional
public class CertificateDetailsServiceProvider extends UserDetailsServiceProviderImpl {

    @Autowired
    // private UserService userService;
    private MultiOrgUserService multiOrgUserService;

    @Autowired
    private TenantFacadeService tenantService;

    @Autowired
    private OrgApiFacade orgApiFacade;

    private ShaPasswordEncoder shaPasswordEncoder = new ShaPasswordEncoder();

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.core.userdetails.impl.UserDetailsServiceProviderImpl#getLoginType()
     */
    @Override
    public String getLoginType() {
        return LoginType.CERTIFICATE;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.core.userdetails.UserDetailsServiceProvider#getUserDetails(java.lang.String)
     */
    @Override
    public UserDetails getUserDetails(String loginName) {
        String tenant = null;
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attr.getRequest();
        Object tenantValue = request.getAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_TENANT_KEY);
        if (tenantValue != null) {
            tenant = tenantValue.toString();
        }
        if (StringUtils.isBlank(tenant)) {
            throw new RuntimeException("租户[" + tenant + "]不存在");
        }

        // 证书号
        String textCert = request.getParameter("textCert");

        // HttpSession httpSession = request.getSession();
        // String textOriginData = (String) httpSession.getAttribute("RANDOM");

        // 原文(随机数)
        String textOriginData = request.getParameter("textOriginData");
        // 签名
        String textSignData = request.getParameter("textSignData");
        FJCAApps ca = new fjca.FJCAApps();
        // 社保4000
        FJCAApps.setErrorBase(4000);
        FJCAApps.setServerURL(Config.getValue(KEY_FJCA_SERVER_URL));

        String subjectDN = ca.getSubject(textCert);
        if (StringUtils.isBlank(subjectDN)) {
            // 抛出证书错误
            throw new RuntimeException("证书信息错误");
        }

        // String strSubjectCN = null;
        // try {
        // // GBK
        // strSubjectCN = new String(bySubjectCN,
        // Config.getValue(UserDetailsServiceProvider.KEY_FJCA_CERT_ENCODING,
        // "GBK"));
        // } catch (UnsupportedEncodingException e) {
        // e.printStackTrace();
        // throw new RuntimeException(e);
        // }
        // 取证书序列号
        // String strSerial = ca.getSerialFromCert(textCert);

        // 对企业进行身份认证（HTTP环境下的数字证书认证）
        // String strRet = ca.FJCA_VerifyQY(textOriginData, textSignData,
        // textCert);
        ca.FJCA_VerifyQY(textOriginData, textSignData, textCert);

        int retCode = ca.getLastError();

        if (retCode != 0) {
            throw new RuntimeException(FJCAUtils.getErrorData(retCode));
        }

        Tenant tenantAccount = tenantService.getByAccount(tenant);
        Tenant tempTenant = new Tenant();
        BeanUtils.copyProperties(tenantAccount, tempTenant);
        request.setAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_TENANT_ID_KEY,
                tenantAccount.getId());
        OrgUserVo user = multiOrgUserService.getUserByLoginNameIgnoreCase(loginName,
                PasswordAlgorithm.Plaintext.getValue());
        if (user == null) {
            throw new RuntimeException("证书主体为[" + subjectDN + "]的用户不存在!");
        }
        if (1 == user.getIsForbidden()) {
            throw new RuntimeException("登录名为[" + user.getLoginName() + "]的用户已被禁用!");
        }

        // String password =
        // shaPasswordEncoder.encodePassword(user.getLoginName(),
        // textOriginData);
        // user.setPassword(Md5PasswordEncoderUtils.encodePassword(password,
        // loginName));

        user.setPassword(SM3Util.encrypt(textOriginData + "{" + user.getLoginName() + "}"));

        Map<String, Set<String>> userRoles = orgApiFacade.queryAllRoleListByUser(user);
        DefaultUserDetails userDetails = new DefaultUserDetails(tenantAccount, user,
                obtainGrantedAuthorities(user.getSystemUnitId(), userRoles), getLoginType());
        userDetails.putExtraData(FJCAUtils.KEY_LOGIN_TOKEN_ORIGIN_DATA, textOriginData);
        userDetails.putExtraData(FJCAUtils.KEY_LOGIN_TOKEN_SIGN_DATA, textSignData);
        return userDetails;
    }

}
