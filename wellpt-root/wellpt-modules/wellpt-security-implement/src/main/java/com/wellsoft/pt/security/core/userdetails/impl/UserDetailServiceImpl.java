package com.wellsoft.pt.security.core.userdetails.impl;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.security.core.userdetails.UserDetailsServiceProvider;
import com.wellsoft.pt.security.util.TenantContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Map;

/**
 * 实现SpringSecurity的UserDetailsService接口,获取用户UserDetails信息.
 *
 * @author lilin
 */
public class UserDetailServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        String loginType = TenantContextHolder.getLoginType();
        Map<String, UserDetailsServiceProvider> providerMap = ApplicationContextHolder.getApplicationContext()
                .getBeansOfType(UserDetailsServiceProvider.class);
        Collection<UserDetailsServiceProvider> providers = providerMap.values();
        for (UserDetailsServiceProvider provider : providers) {
            if (loginType.equals(provider.getLoginType())) {
                return provider.getUserDetails(username);
            }
        }
        return null;
    }
}
