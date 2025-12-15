package com.wellsoft.oauth2.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

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
public class SecurityUserDetails extends User {


    private String accountNumber;

    private String name;

    private Long uuid;


    public SecurityUserDetails(String accountNumber, String name, String password,
                               boolean enabled,
                               boolean accountNonExpired, boolean credentialsNonExpired,
                               boolean accountNonLocked,
                               Collection<? extends GrantedAuthority> authorities) {
        super(accountNumber, password, enabled, accountNonExpired, credentialsNonExpired,
                accountNonLocked,
                authorities);
        this.accountNumber = accountNumber;
        this.name = name;
    }


    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }
}
