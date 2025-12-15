/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.core.userdetails.impl;

import com.google.common.collect.Sets;
import com.wellsoft.context.enums.LoginType;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.security.access.LoginAuthenticationProcessingFilter;
import com.wellsoft.pt.security.core.authentication.encoding.PasswordAlgorithm;
import com.wellsoft.pt.security.core.userdetails.InternetUserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetailsServiceProvider;
import com.wellsoft.pt.security.enums.BuildInRole;
import com.wellsoft.pt.user.dto.UserDto;
import com.wellsoft.pt.user.facade.service.UserInfoFacadeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * 互联网用户详情服务
 */
@Transactional(readOnly = true)
@Service(value = "internetUserDetailsServiceProvider")
public class InternetUserDetailsServiceProviderImpl extends BaseServiceImpl implements UserDetailsServiceProvider {

    protected Set<String> defaultRoles = Sets.newHashSet(BuildInRole.ROLE_ANONYMOUS.name());
    @Autowired
    UserInfoFacadeService userInfoFacadeService;

    /**
     * (non-Javadoc)
     *
     * @see UserDetailsServiceProvider#getLoginType()
     */
    @Override
    public String getLoginType() {
        return LoginType.INTERNET_USER;
    }

    public UserDetails getUserDetails(String loginName) {
        if (StringUtils.isBlank(loginName)) {
            throw new RuntimeException("登录名不能为空！");
        }
        String loginNameHashAlgorithmCode = PasswordAlgorithm.Plaintext.getCode();
        if (RequestContextHolder.getRequestAttributes() != null) {
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attr.getRequest();
            Object loginNameHashAlgorithmCodeObj = request
                    .getAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_LNALG_CODE_KEY);
            if (loginNameHashAlgorithmCodeObj != null) {
                loginNameHashAlgorithmCode = loginNameHashAlgorithmCodeObj.toString();
            }

        }
        // 忽略登录验证租户用户信息
        UserDetails userDetails = checkAndGetUserDetails(loginName, null, loginNameHashAlgorithmCode);
        // 验证成功，清空session的tenant信息
        return userDetails;

    }

    protected UserDetails checkAndGetUserDetails(String loginName, Tenant tenantAccount,
                                                 String loginNameHashAlgorithmCode) {
        try {
            UserDto user = userInfoFacadeService.getFullInternetUserByLoginName(loginName, loginNameHashAlgorithmCode);
            if (user == null) {
                throw new RuntimeException("账号不存在！");
            }

            // 获取互联网用户：按个人类型、法人类型加载不同的默认角色
            Set<GrantedAuthority> grantedAuths = authoritiesByType(user.getLoginName());
            InternetUserDetails userdetails = new InternetUserDetails(user.getUserName(), user.getPassword(),
                    user.getIsEnabled(), user.getIsAccountNonExpired(), user.getIsCredentialsNonExpired(),
                    user.getIsAccountNonLocked(), grantedAuths);
            userdetails.setLoginName(user.getLoginName());
            userdetails.setLoginNameLowerCase(user.getLoginName().toLowerCase());
            userdetails.setLoginType(LoginType.INTERNET_USER);
            return userdetails;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }

    @Override
    public Set<GrantedAuthority> queryAllGrantedAuthoritiesByUserId(String loginName) {
        return authoritiesByType(loginName);
    }

    public Set<GrantedAuthority> authoritiesByType(String loginName) {
        Set<GrantedAuthority> authorities = Sets.newHashSet();
        for (String r : this.defaultRoles) {
            authorities.add(new SimpleGrantedAuthority(r));
        }

        authorities.add(new SimpleGrantedAuthority(BuildInRole.ROLE_INTERNET_USER.name()));

        Set<String> roles = userInfoFacadeService.getUserTypeAllRoles(loginName);
        for (String r : roles) {
            authorities.add(new SimpleGrantedAuthority(r));
        }
        return authorities;
    }

}
