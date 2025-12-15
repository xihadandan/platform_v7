package com.wellsoft.pt.user.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 用户证件表
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年08月10日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "USER_CREDENTIAL")
@DynamicUpdate
@DynamicInsert
public class UserCredentialEntity extends IdEntity {

    private static final long serialVersionUID = -7101189016479466L;

    private String loginName;

    private String type;

    private String code;

    public UserCredentialEntity() {
    }

    public UserCredentialEntity(String loginName, String type, String code) {
        this.loginName = loginName;
        this.type = type;
        this.code = code;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
