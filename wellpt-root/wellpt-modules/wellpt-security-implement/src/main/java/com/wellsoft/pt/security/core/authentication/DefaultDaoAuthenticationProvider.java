package com.wellsoft.pt.security.core.authentication;

import com.google.common.base.Stopwatch;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.multi.org.util.PwdUtils;
import com.wellsoft.pt.security.access.LoginAuthenticationProcessingFilter;
import com.wellsoft.pt.security.audit.service.UserAttemptsService;
import com.wellsoft.pt.security.core.authentication.encoding.PasswordAlgorithm;
import com.wellsoft.pt.user.dto.UserDto;
import com.wellsoft.pt.user.facade.service.UserInfoFacadeService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年12月27日   chenq	 Create
 * </pre>
 */
public class DefaultDaoAuthenticationProvider extends DaoAuthenticationProvider {

    private Logger logger = LoggerFactory.getLogger(DefaultDaoAuthenticationProvider.class);

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserAttemptsService userAttemptsService = ApplicationContextHolder.getBean(UserAttemptsService.class);
        String loginName = authentication.getName();
        try {

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attr.getRequest();
            String loginType = request.getParameter("loginType");
            request.setAttribute("authentication", authentication);
            String[] utype = request.getParameterValues("utype");// 登录名类型，支持多种登录方式同时校验，手机号码、中文名等
            request.setAttribute("utype", utype);


            // bug:50412 去掉校验太频繁
            // if (userAttemptsService.isAttemptTooFrequently(loginName)) {
            // throw new BadCredentialsException("尝试登录太频繁，请稍候再试!");
            // }
            Stopwatch timer = Stopwatch.createStarted();
            Authentication auth = super.authenticate(authentication);
            logger.info("登录校验耗时：{}", timer.stop());
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

        UserInfoFacadeService userInfoFacadeService = ApplicationContextHolder.getBean(UserInfoFacadeService.class);
        //TODO: 根据前端提供的登录账号类型的多样性匹配登录：允许中文名、手机号等作为账号登录
        UserDto user = userInfoFacadeService.getUserByLoginName(userDetails.getUsername(), null);

        String userPwd = user.getPassword();
        if (PasswordAlgorithm.MD5.getCode().equals(passwordAlgorithmCode)) {
            String presentedPassword = authentication.getCredentials().toString();
            if (!StringUtils.equals(presentedPassword, userDetails.getPassword())) {
                throw new BadCredentialsException(messages
                        .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
            }
            return;
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
        if (!userPwd.equals(loginPwd)) {
            logger.debug("Authentication failed: password does not match stored value");
            throw new BadCredentialsException(
                    messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }

        // super.additionalAuthenticationChecks(userDetails, authentication);
    }
}
