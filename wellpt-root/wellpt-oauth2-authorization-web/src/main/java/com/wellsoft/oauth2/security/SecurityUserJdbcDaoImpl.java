/*
 * Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wellsoft.oauth2.security;

import org.springframework.context.ApplicationContextException;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SecurityUserJdbcDaoImpl extends JdbcDaoSupport
        implements UserDetailsService, MessageSourceAware {

    public static final String DEF_USERS_BY_USERNAME_QUERY = "select a.account_number,a.password,a.enabled,i.user_name "
            + "from user_account a,user_info i" + " where a.account_number = ? and a.account_number=i.account_number";
    public static final String DEF_AUTHORITIES_BY_USERNAME_QUERY = "select account_number,authority "
            + "from user_authorities " + "where account_number = ?";


    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    private String authoritiesByUsernameQuery;
    private String groupAuthoritiesByUsernameQuery;
    private String usersByUsernameQuery;
    private String rolePrefix = "";
    private boolean usernameBasedPrimaryKey = true;
    private boolean enableAuthorities = true;
    private boolean enableGroups = false;

    // ~ Constructors
    // ===================================================================================================

    public SecurityUserJdbcDaoImpl() {
        this.usersByUsernameQuery = DEF_USERS_BY_USERNAME_QUERY;
        this.authoritiesByUsernameQuery = DEF_AUTHORITIES_BY_USERNAME_QUERY;
    }

    // ~ Methods
    // ========================================================================================================

    /**
     * @return the messages
     */
    protected MessageSourceAccessor getMessages() {
        return this.messages;
    }

    /**
     * Allows subclasses to add their own granted authorities to the list to be returned
     * in the <tt>UserDetails</tt>.
     *
     * @param username    the username, for use by finder methods
     * @param authorities the current granted authorities, as populated from the
     *                    <code>authoritiesByUsername</code> mapping
     */
    protected void addCustomAuthorities(String username,
                                        List<GrantedAuthority> authorities) {
    }

    public String getUsersByUsernameQuery() {
        return this.usersByUsernameQuery;
    }

    /**
     * Allows the default query string used to retrieve users based on username to be
     * overridden, if default table or column names need to be changed. The default query
     * is {@link #DEF_USERS_BY_USERNAME_QUERY}; when modifying this query, ensure that all
     * returned columns are mapped back to the same column positions as in the default query.
     * If the 'enabled' column does not exist in the source database, a permanent true
     * value for this column may be returned by using a query similar to
     *
     * <pre>
     * &quot;select username,password,'true' as enabled from users where username = ?&quot;
     * </pre>
     *
     * @param usersByUsernameQueryString The query string to set
     */
    public void setUsersByUsernameQuery(String usersByUsernameQueryString) {
        this.usersByUsernameQuery = usersByUsernameQueryString;
    }

    @Override
    protected void initDao() throws ApplicationContextException {
        Assert.isTrue(this.enableAuthorities || this.enableGroups,
                "Use of either authorities or groups must be enabled");
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        List<SecurityUserDetails> users = loadUsersByUsername(username);

        if (users.size() == 0) {
            this.logger.debug("Query returned no results for user '" + username + "'");

            throw new UsernameNotFoundException(
                    this.messages.getMessage("JdbcDaoImpl.notFound",
                            new Object[]{username}, "Username {0} not found"));
        }

        SecurityUserDetails user = users.get(0); // contains no GrantedAuthority[]

        Set<GrantedAuthority> dbAuthsSet = new HashSet<GrantedAuthority>();

        if (this.enableAuthorities) {
            dbAuthsSet.addAll(loadUserAuthorities(user.getAccountNumber()));
        }

        if (this.enableGroups) {
            dbAuthsSet.addAll(loadGroupAuthorities(user.getAccountNumber()));
        }

        List<GrantedAuthority> dbAuths = new ArrayList<GrantedAuthority>(dbAuthsSet);

        addCustomAuthorities(user.getAccountNumber(), dbAuths);

        if (dbAuths.size() == 0) {
            this.logger.debug("User '" + username
                    + "' has no authorities and will be treated as 'not found'");

            throw new UsernameNotFoundException(this.messages.getMessage(
                    "JdbcDaoImpl.noAuthority", new Object[]{username},
                    "User {0} has no GrantedAuthority"));
        }

        return createUserDetails(username, user, dbAuths);
    }

    /**
     * Executes the SQL <tt>usersByUsernameQuery</tt> and returns a list of UserDetails
     * objects. There should normally only be one matching user.
     */
    protected List<SecurityUserDetails> loadUsersByUsername(String username) {
        return getJdbcTemplate().query(this.usersByUsernameQuery,
                new String[]{username}, new RowMapper<SecurityUserDetails>() {
                    @Override
                    public SecurityUserDetails mapRow(ResultSet rs, int rowNum)
                            throws SQLException {
                        String accountNumber = rs.getString(1);
                        String password = rs.getString(2);
                        String userName = rs.getString(4);
                        boolean enabled = rs.getBoolean(3);
                        return new SecurityUserDetails(accountNumber, userName, password, enabled,
                                true, true,
                                true,
                                AuthorityUtils.NO_AUTHORITIES);
                    }

                });
    }

    /**
     * Loads authorities by executing the SQL from <tt>authoritiesByUsernameQuery</tt>.
     *
     * @return a list of GrantedAuthority objects for the user
     */
    protected List<GrantedAuthority> loadUserAuthorities(String username) {
        return getJdbcTemplate().query(this.authoritiesByUsernameQuery,
                new String[]{username}, new RowMapper<GrantedAuthority>() {
                    @Override
                    public GrantedAuthority mapRow(ResultSet rs, int rowNum)
                            throws SQLException {
                        String roleName = SecurityUserJdbcDaoImpl.this.rolePrefix + rs.getString(2);

                        return new SimpleGrantedAuthority(roleName);
                    }
                });
    }

    /**
     * Loads authorities by executing the SQL from
     * <tt>groupAuthoritiesByUsernameQuery</tt>.
     *
     * @return a list of GrantedAuthority objects for the user
     */
    protected List<GrantedAuthority> loadGroupAuthorities(String username) {
        return getJdbcTemplate().query(this.groupAuthoritiesByUsernameQuery,
                new String[]{username}, new RowMapper<GrantedAuthority>() {
                    @Override
                    public GrantedAuthority mapRow(ResultSet rs, int rowNum)
                            throws SQLException {
                        String roleName = getRolePrefix() + rs.getString(3);

                        return new SimpleGrantedAuthority(roleName);
                    }
                });
    }

    /**
     * Can be overridden to customize the creation of the final UserDetailsObject which is
     * returned by the <tt>loadUserByUsername</tt> method.
     *
     * @param accountNumber       the name originally passed to loadUserByUsername
     * @param userFromUserQuery   the object returned from the execution of the
     * @param combinedAuthorities the combined array of authorities from all the authority
     *                            loading queries.
     * @return the final UserDetails which should be used in the system.
     */
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

    protected String getAuthoritiesByUsernameQuery() {
        return this.authoritiesByUsernameQuery;
    }

    /**
     * Allows the default query string used to retrieve authorities based on username to
     * be overridden, if default table or column names need to be changed. The default
     * query is {@link #DEF_AUTHORITIES_BY_USERNAME_QUERY}; when modifying this query,
     * ensure that all returned columns are mapped back to the same column positions as in the
     * default query.
     *
     * @param queryString The SQL query string to set
     */
    public void setAuthoritiesByUsernameQuery(String queryString) {
        this.authoritiesByUsernameQuery = queryString;
    }

    /**
     * Allows the default query string used to retrieve group authorities based on
     * username to be overridden, if default table or column names need to be changed. The
     * default query is {@link #DEF_GROUP_AUTHORITIES_BY_USERNAME_QUERY}; when modifying
     * this query, ensure that all returned columns are mapped back to the same column
     * positions as in the default query.
     *
     * @param queryString The SQL query string to set
     */
    public void setGroupAuthoritiesByUsernameQuery(String queryString) {
        this.groupAuthoritiesByUsernameQuery = queryString;
    }

    protected String getRolePrefix() {
        return this.rolePrefix;
    }

    /**
     * Allows a default role prefix to be specified. If this is set to a non-empty value,
     * then it is automatically prepended to any roles read in from the db. This may for
     * example be used to add the <tt>ROLE_</tt> prefix expected to exist in role names
     * (by default) by some other Spring Security classes, in the case that the prefix is
     * not already present in the db.
     *
     * @param rolePrefix the new prefix
     */
    public void setRolePrefix(String rolePrefix) {
        this.rolePrefix = rolePrefix;
    }

    protected boolean isUsernameBasedPrimaryKey() {
        return this.usernameBasedPrimaryKey;
    }

    /**
     * If <code>true</code> (the default), indicates the
     * {@link #getUsersByUsernameQuery()} returns a username in response to a query. If
     * <code>false</code>, indicates that a primary key is used instead. If set to
     * <code>true</code>, the class will use the database-derived username in the returned
     * <code>UserDetails</code>. If <code>false</code>, the class will use the
     * {@link #loadUserByUsername(String)} derived username in the returned
     * <code>UserDetails</code>.
     *
     * @param usernameBasedPrimaryKey <code>true</code> if the mapping queries return the
     *                                username <code>String</code>, or <code>false</code> if the mapping returns a
     *                                database primary key.
     */
    public void setUsernameBasedPrimaryKey(boolean usernameBasedPrimaryKey) {
        this.usernameBasedPrimaryKey = usernameBasedPrimaryKey;
    }

    protected boolean getEnableAuthorities() {
        return this.enableAuthorities;
    }

    /**
     * Enables loading of authorities (roles) from the authorities table. Defaults to true
     */
    public void setEnableAuthorities(boolean enableAuthorities) {
        this.enableAuthorities = enableAuthorities;
    }

    protected boolean getEnableGroups() {
        return this.enableGroups;
    }

    /**
     * Enables support for group authorities. Defaults to false
     *
     * @param enableGroups
     */
    public void setEnableGroups(boolean enableGroups) {
        this.enableGroups = enableGroups;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        Assert.notNull(messageSource, "messageSource cannot be null");
        this.messages = new MessageSourceAccessor(messageSource);
    }
}
