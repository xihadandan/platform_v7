/*
 * @(#)2012-12-4 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.core.userdetails;

import com.wellsoft.pt.user.enums.UserTypeEnum;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Description: 如何描述该类
 *
 * @author lilin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-12-4.1	lilin		2012-12-4		Create
 * </pre>
 * @date 2012-12-4
 */
public class InternetUserDetails extends AbstractUserDetails {

    private static final long serialVersionUID = -2292186205063556173L;
    private UserTypeEnum type;
    private String loginNameLowerCase;

    private String loginName;

    public InternetUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public InternetUserDetails(String username, String password, boolean enabled, boolean accountNonExpired,
                               boolean credentialsNonExpired, boolean accountNonLocked,
                               Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public UserTypeEnum getType() {
        return type;
    }

    public void setType(UserTypeEnum type) {
        this.type = type;
    }

    @Override
    public String getLoginNameLowerCase() {
        return loginNameLowerCase;
    }

    public void setLoginNameLowerCase(String loginNameLowerCase) {
        this.loginNameLowerCase = loginNameLowerCase;
    }

    @Override
    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @Override
    public String getUserId() {
        return getLoginName();
    }
}
