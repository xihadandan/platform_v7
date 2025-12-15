/*
 * @(#)Sep 7, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.core.authentication;

import com.google.common.base.Stopwatch;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserAccountFacadeService;
import com.wellsoft.pt.multi.org.util.PwdUtils;
import com.wellsoft.pt.security.access.LoginAuthenticationProcessingFilter;
import com.wellsoft.pt.security.audit.service.UserAttemptsService;
import com.wellsoft.pt.security.core.authentication.encoding.PasswordAlgorithm;
import com.wellsoft.pt.user.entity.UserAccountEntity;
import com.wellsoft.pt.user.facade.service.UserInfoFacadeService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Sep 7, 2017.1	zhulh		Sep 7, 2017		Create
 * </pre>
 * @date Sep 7, 2017
 */
@Deprecated
public class CustomDaoAuthenticationProvider extends DaoAuthenticationProvider {

    public static final String IMAGE_CODE = "imageCode";
    public static final String SMS_CODE = "smsCode";
    public static final String IMAGE_CODE_TIMEOUT = "imageCodeTimeout";
    public static final String SMS_CODE_TIMEOUT = "smsCodeTimeout";
    public static final String SMS_USERNAME = "smsUsername";
    public static final String PRESET_ENCRYPED_PASSWORD = "preset_encryped_password";
    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserAttemptsService userAttemptsService = ApplicationContextHolder.getBean(UserAttemptsService.class);
        String loginName = authentication.getName();
        try {

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attr.getRequest();
            String loginType = request.getParameter("loginType");
            request.setAttribute("authentication", authentication);


            // bug:50412 去掉校验太频繁
            // if (userAttemptsService.isAttemptTooFrequently(loginName)) {
            // throw new BadCredentialsException("尝试登录太频繁，请稍候再试!");
            // }
            Stopwatch timer = Stopwatch.createStarted();
            log.info("开始登录校验流程");
            Authentication auth = super.authenticate(authentication);
            log.info("登录校验耗时：{}", timer.stop());
            userAttemptsService.resetAttempts(loginName);
            return auth;
        } catch (RuntimeException exception) {
            userAttemptsService.addAttempts(loginName);
            throw exception;
        }
    }


    /**
     * (non-Javadoc)
     *
     * @see org.springframework.security.authentication.dao.DaoAuthenticationProvider#additionalAuthenticationChecks(org.springframework.security.core.userdetails.UserDetails, org.springframework.security.authentication.UsernamePasswordAuthenticationToken)
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        Object passwordAlgorithmCode = null;
        if (RequestContextHolder.getRequestAttributes() != null) {
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attr.getRequest();
            passwordAlgorithmCode = request
                    .getAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_PWDALG_CODE_KEY);
        }

        if (PasswordAlgorithm.MD5.getCode().equals(passwordAlgorithmCode)) {
            String presentedPassword = authentication.getCredentials().toString();
            if (!StringUtils.equals(presentedPassword, userDetails.getPassword())) {
                throw new BadCredentialsException(messages
                        .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
            }
            return;
        }
        UserInfoFacadeService userInfoFacadeService = ApplicationContextHolder.getBean(UserInfoFacadeService.class);
        UserAccountEntity userAccountEntity = userInfoFacadeService.getUserAccountByLoginName(userDetails.getUsername());
        if (!userAccountEntity.getIsAccountNonLocked()) {
            throw new LockedException("Account Locked");
        }
        if (!userAccountEntity.getIsEnabled()) {
            throw new BadCredentialsException(messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
        String password = null;
        if (userAccountEntity != null) {
            password = userAccountEntity.getPassword();
        } else {
            // 旧版组织登录
            // 临时解决方案 todo-zhw 密码不会更新，后面要找出来
            MultiOrgUserAccountFacadeService accountFacadeService = ApplicationContextHolder
                    .getBean(MultiOrgUserAccountFacadeService.class);
            //中文登录修改,根据登录信息获取账户
            List<MultiOrgUserAccount> userAccounts = accountFacadeService.getUserAccountByLoginNameIgnoreCase(userDetails.getUsername());
            MultiOrgUserAccount multiOrgUserAccount = null;
            if (userAccounts == null || userAccounts.isEmpty()) {
                multiOrgUserAccount = accountFacadeService
                        .getUserByLoginNameIgnoreCase(userDetails.getUsername(), null);
            } else if (userAccounts.size() == 1) {
                multiOrgUserAccount = userAccounts.get(0);
            } else {
                throw new BadCredentialsException(messages
                        .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
            }
            password = multiOrgUserAccount.getPassword();
        }
        // 密码验证改为SM3
        Object salt = null;
        if (this.getSaltSource() != null) {
            salt = this.getSaltSource().getSalt(userDetails);
        }
        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");
            throw new BadCredentialsException(
                    messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
        String presentedPassword = authentication.getCredentials().toString();
        String loginPwd = PwdUtils.createSm3Password(salt.toString(), presentedPassword);
        if (!loginPwd.equals(password)) {
            logger.debug("Authentication failed: password does not match stored value");
            throw new BadCredentialsException(
                    messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }

        // super.additionalAuthenticationChecks(userDetails, authentication);
    }


}
