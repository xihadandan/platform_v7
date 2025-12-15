package com.wellsoft.oauth2.security;

import com.wellsoft.oauth2.service.UserAccountService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContextException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;

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
public class SecurityUserDetailsManager extends SecurityUserJdbcDaoImpl implements
        UserDetailsManager {

    // UserDetailsManager SQL
    public static final String DEF_CREATE_USER_SQL = "insert into user_account (account_number, password, enabled) values (?,?,?)";
    public static final String DEF_DELETE_USER_SQL = "delete from user_account where account_number = ?";
    public static final String DEF_UPDATE_USER_SQL = "update user_account set password = ?, enabled = ? where user_account = ?";
    public static final String DEF_INSERT_AUTHORITY_SQL = "insert into user_authorities (account_number, authority) values (?,?)";
    public static final String DEF_DELETE_USER_AUTHORITIES_SQL = "delete from user_authorities where account_number = ?";
    public static final String DEF_USER_EXISTS_SQL = "select account_number from user_account where account_number = ?";
    public static final String DEF_CHANGE_PASSWORD_SQL = "update user_account set password = ? where account_number = ?";


    // ~ Instance fields
    // ================================================================================================

    protected final Log logger = LogFactory.getLog(getClass());

    private String createUserSql = DEF_CREATE_USER_SQL;
    private String deleteUserSql = DEF_DELETE_USER_SQL;
    private String updateUserSql = DEF_UPDATE_USER_SQL;
    private String createAuthoritySql = DEF_INSERT_AUTHORITY_SQL;
    private String deleteUserAuthoritiesSql = DEF_DELETE_USER_AUTHORITIES_SQL;
    private String userExistsSql = DEF_USER_EXISTS_SQL;
    private String changePasswordSql = DEF_CHANGE_PASSWORD_SQL;


    private AuthenticationManager authenticationManager;

    private UserAccountService userService;

    private UserCache userCache = new NullUserCache();

    // ~ Methods
    // ========================================================================================================

    protected void initDao() throws ApplicationContextException {
        if (authenticationManager == null) {
            logger.info(
                    "No authentication manager set. Reauthentication of users when changing passwords will "
                            + "not be performed.");
        }

        super.initDao();
    }

    // ~ UserDetailsManager implementation
    // ==============================================================================

    public void createUser(final UserDetails user) {

    }

    public void updateUser(final UserDetails user) {

    }


    public void deleteUser(String username) {

    }


    public void changePassword(String oldPassword, String newPassword)
            throws AuthenticationException {
        Authentication currentUser = SecurityContextHolder.getContext()
                .getAuthentication();

        if (currentUser == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context "
                            + "for current user.");
        }

        String username = currentUser.getName();

        // If an authentication manager has been set, re-authenticate the user with the
        // supplied password.
        if (authenticationManager != null) {
            logger.debug("Reauthenticating user '" + username
                    + "' for password change request.");

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    username, oldPassword));
        } else {
            logger.debug("No authentication manager set. Password won't be re-checked.");
        }

        logger.debug("Changing password for user '" + username + "'");

        getJdbcTemplate().update(changePasswordSql, newPassword, username);

        SecurityContextHolder.getContext().setAuthentication(
                createNewAuthentication(currentUser, newPassword));

        userCache.removeUserFromCache(username);
    }

    protected Authentication createNewAuthentication(Authentication currentAuth,
                                                     String newPassword) {
        UserDetails user = loadUserByUsername(currentAuth.getName());

        UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        newAuthentication.setDetails(currentAuth.getDetails());

        return newAuthentication;
    }

    public boolean userExists(String username) {
        List<String> users = getJdbcTemplate().queryForList(userExistsSql,
                new String[]{username}, String.class);

        if (users.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(
                    "More than one user found with name '" + username + "'", 1);
        }

        return users.size() == 1;
    }


    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setCreateUserSql(String createUserSql) {
        Assert.hasText(createUserSql, "createUserSql should have text");
        ;
        this.createUserSql = createUserSql;
    }

    public void setDeleteUserSql(String deleteUserSql) {
        Assert.hasText(deleteUserSql, "deleteUserSql should have text");
        ;
        this.deleteUserSql = deleteUserSql;
    }

    public void setUpdateUserSql(String updateUserSql) {
        Assert.hasText(updateUserSql, "updateUserSql should have text");
        ;
        this.updateUserSql = updateUserSql;
    }

    public void setCreateAuthoritySql(String createAuthoritySql) {
        Assert.hasText(createAuthoritySql, "createAuthoritySql should have text");
        ;
        this.createAuthoritySql = createAuthoritySql;
    }

    public void setDeleteUserAuthoritiesSql(String deleteUserAuthoritiesSql) {
        Assert.hasText(deleteUserAuthoritiesSql, "deleteUserAuthoritiesSql should have text");
        ;
        this.deleteUserAuthoritiesSql = deleteUserAuthoritiesSql;
    }

    public void setUserExistsSql(String userExistsSql) {
        Assert.hasText(userExistsSql, "userExistsSql should have text");
        ;
        this.userExistsSql = userExistsSql;
    }

    public void setChangePasswordSql(String changePasswordSql) {
        Assert.hasText(changePasswordSql, "changePasswordSql should have text");
        ;
        this.changePasswordSql = changePasswordSql;
    }


    /**
     * Optionally sets the UserCache if one is in use in the application. This allows the
     * user to be removed from the cache after updates have taken place to avoid stale
     * data.
     *
     * @param userCache the cache used by the AuthenticationManager.
     */
    public void setUserCache(UserCache userCache) {
        Assert.notNull(userCache, "userCache cannot be null");
        this.userCache = userCache;
    }

    private void validateUserDetails(UserDetails user) {
        Assert.hasText(user.getUsername(), "Username may not be empty or null");
        validateAuthorities(user.getAuthorities());
    }

    private void validateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Authorities list must not be null");

        for (GrantedAuthority authority : authorities) {
            Assert.notNull(authority, "Authorities list contains a null entry");
            Assert.hasText(authority.getAuthority(),
                    "getAuthority() method must return a non-empty string");
        }
    }

    public UserAccountService getUserService() {
        return userService;
    }

    public void setUserService(UserAccountService userService) {
        this.userService = userService;
    }
}
