package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/10/9
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/10/9    chenq		2019/10/9		Create
 * </pre>
 */
@Entity
@Table(name = "MULTI_ORG_USER_EXT_ACCOUNT")
@DynamicUpdate
@DynamicInsert
public class MultiOrgUsrExtAccountEntity extends IdEntity {
    private static final long serialVersionUID = 8328916673717374729L;

    private String accountUuid;

    private String externalAccountId;

    private String externalAccountType;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getExternalAccountId() {
        return externalAccountId;
    }

    public void setExternalAccountId(String externalAccountId) {
        this.externalAccountId = externalAccountId;
    }

    public String getExternalAccountType() {
        return externalAccountType;
    }

    public void setExternalAccountType(String externalAccountType) {
        this.externalAccountType = externalAccountType;
    }
}
