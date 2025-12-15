package com.wellsoft.oauth2.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/24
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/24    chenq		2019/9/24		Create
 * </pre>
 */
@Entity
@Table(name = "user_authorities")
@DynamicUpdate
@DynamicInsert
public class UserAuthorityEntity extends BaseEntity {

    private String accountNumber;

    private String authority;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
