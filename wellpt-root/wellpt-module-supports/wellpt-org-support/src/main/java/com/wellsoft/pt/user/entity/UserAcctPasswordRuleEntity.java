package com.wellsoft.pt.user.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年04月15日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "USER_ACCT_PASSWORD_RULE")
@DynamicUpdate
@DynamicInsert
public class UserAcctPasswordRuleEntity extends com.wellsoft.context.jdbc.entity.Entity {
    private static final long serialVersionUID = 2241190532572108678L;

    private String attrKey;
    private String attrVal;
    private String remark;

    public String getAttrKey() {
        return attrKey;
    }

    public void setAttrKey(String attrKey) {
        this.attrKey = attrKey;
    }

    public String getAttrVal() {
        return attrVal;
    }

    public void setAttrVal(String attrVal) {
        this.attrVal = attrVal;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
