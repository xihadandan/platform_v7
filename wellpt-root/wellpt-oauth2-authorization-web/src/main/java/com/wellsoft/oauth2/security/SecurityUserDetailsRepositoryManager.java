package com.wellsoft.oauth2.security;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.wellsoft.oauth2.entity.UserAccountEntity;
import com.wellsoft.oauth2.entity.UserAuthorityEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/23
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/23    chenq		2019/9/23		Create
 * </pre>
 */
public class SecurityUserDetailsRepositoryManager implements
        UserDetailsManager {

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    protected boolean enableAuthorities = true;
    UserDetailsServiceHolder userDetailsServiceHolder;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private AuthenticationManager authenticationManager;
    private String rolePrefix = "";
    private boolean usernameBasedPrimaryKey = true;

    private UserCache userCache = new NullUserCache();

    public SecurityUserDetailsRepositoryManager() {
    }

    public SecurityUserDetailsRepositoryManager(
            UserDetailsServiceHolder userDetailsServiceHolder) {
        this.userDetailsServiceHolder = userDetailsServiceHolder;
    }

    @Override
    public void createUser(UserDetails user) {

    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    public void changePassword(String oldPassword, String newPassword)
            throws AuthenticationException {
        Authentication currentUser = SecurityContextHolder.getContext()
                .getAuthentication();

        if (currentUser == null) {
            throw new AccessDeniedException("未找到当前授权用户");
        }

        String username = currentUser.getName();

        if (authenticationManager != null) {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    username, oldPassword));
        } else {
            logger.debug("No authentication manager set. Password won't be re-checked.");
        }

        userDetailsServiceHolder.changePassword(newPassword, null, username);

        SecurityContextHolder.getContext().setAuthentication(
                createNewAuthentication(currentUser, newPassword));

        userCache.removeUserFromCache(username);
    }

    public void changePassword(String oldPassword, String newPassword, String accountNumber) {
        if (StringUtils.isBlank(accountNumber)) {
            this.changePassword(oldPassword, newPassword);
        } else {
            userDetailsServiceHolder.changePassword(newPassword, oldPassword, accountNumber);
        }
    }

    protected Authentication createNewAuthentication(Authentication currentAuth,
                                                     String newPassword) {
        UserDetails user = loadUserByUsername(currentAuth.getName());

        UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        newAuthentication.setDetails(currentAuth.getDetails());

        return newAuthentication;
    }

    @Override
    public boolean userExists(String username) {
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccountEntity accountEntity = userDetailsServiceHolder.loadUserByAccountNumber(
                username);
        if (accountEntity == null) {
            logger.info("未查询到账号[" + username + "]");
            throw new UsernameNotFoundException(
                    this.messages.getMessage("SecurityUserDetailsRepositoryManager.notFound",
                            new Object[]{username}, "username {0} not found"));
        }
        SecurityUserDetails userDetails = new SecurityUserDetails(accountEntity.getAccountNumber(),
                accountEntity.getUserName(), accountEntity.getPassword(),
                accountEntity.getEnabled(),
                true, true,
                true,
                AuthorityUtils.NO_AUTHORITIES);
        userDetails.setUuid(accountEntity.getUuid());

        Set<GrantedAuthority> dbAuthsSet = new HashSet<GrantedAuthority>();

        if (this.enableAuthorities) {
            dbAuthsSet.addAll(loadUserAuthorities(accountEntity.getAccountNumber()));
        }


        if (dbAuthsSet.size() == 0) {
            this.logger.debug("用户[" + username
                    + "]没有权限数据，将被认为未查询到用户");

            throw new UsernameNotFoundException(this.messages.getMessage(
                    "JdbcDaoImpl.noAuthority", new Object[]{username},
                    "User {0} has no GrantedAuthority"));
        }

        return createUserDetails(username, userDetails,
                Lists.<GrantedAuthority>newArrayList(dbAuthsSet));
    }

    protected SecurityUserDetails createUserDetails(String accountNumber,
                                                    SecurityUserDetails userFromUserQuery,
                                                    List<GrantedAuthority> combinedAuthorities) {
        String returnUsername = userFromUserQuery.getUsername();

        if (!this.usernameBasedPrimaryKey) {
            returnUsername = accountNumber;
        }

        return new SecurityUserDetails(returnUsername, userFromUserQuery.getName(),
                userFromUserQuery.getPassword(),
                userFromUserQuery.isEnabled(), true, true, true, combinedAuthorities);
    }

    private Collection<? extends GrantedAuthority> loadUserAuthorities(String accountNumber) {
        List<UserAuthorityEntity> userAuthorityEntityList = userDetailsServiceHolder.getAllAuthorityByAccountNumber(
                accountNumber);
        List<SimpleGrantedAuthority> authorities = Lists.newArrayList();
        if (userAuthorityEntityList.isEmpty()) {// 无角色，默认添加用户角色
            UserAuthorityEntity authorityEntity = new UserAuthorityEntity();
            authorityEntity.setAuthority("ROLE_USER");
            userAuthorityEntityList.add(new UserAuthorityEntity());
        }
        if (!userAuthorityEntityList.isEmpty()) {
            return Lists.transform(userAuthorityEntityList,
                    new Function<UserAuthorityEntity, SimpleGrantedAuthority>() {
                        @Override
                        public SimpleGrantedAuthority apply(
                                UserAuthorityEntity userAuthorityEntity) {
                            return new SimpleGrantedAuthority(
                                    SecurityUserDetailsRepositoryManager.this.rolePrefix + userAuthorityEntity.getAuthority());
                        }
                    });

        }
        return authorities;
    }

    public UserDetailsServiceHolder getUserDetailsServiceHolder() {
        return userDetailsServiceHolder;
    }

    public void setUserDetailsServiceHolder(
            UserDetailsServiceHolder userDetailsServiceHolder) {
        this.userDetailsServiceHolder = userDetailsServiceHolder;
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(
            AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public UserCache getUserCache() {
        return userCache;
    }

    public void setUserCache(UserCache userCache) {
        this.userCache = userCache;
    }
}
