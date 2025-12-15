package com.wellsoft.oauth2.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 外部用户账号
 *
 * @author chenq
 * @date 2019/9/21
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/21    chenq		2019/9/21		Create
 * </pre>
 */
@Entity
@Table(name = "user_ext_account")
@DynamicUpdate
@DynamicInsert
public class UserExtAccountEntity extends BaseEntity {
    private static final long serialVersionUID = 8083224222736632361L;

    private Long accountUuid; //关联的内部账号

    private String extAccountNumber;//外部账号

    private String source;//外部来源

    public Long getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(Long accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getExtAccountNumber() {
        return extAccountNumber;
    }

    public void setExtAccountNumber(String extAccountNumber) {
        this.extAccountNumber = extAccountNumber;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
