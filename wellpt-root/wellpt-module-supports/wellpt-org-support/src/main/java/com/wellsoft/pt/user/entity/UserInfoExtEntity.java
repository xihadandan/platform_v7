package com.wellsoft.pt.user.entity;

import com.wellsoft.context.jdbc.entity.Entity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年12月01日   chenq	 Create
 * </pre>
 */
@javax.persistence.Entity
@Table(name = "USER_INFO_EXT")
@DynamicUpdate
@DynamicInsert
public class UserInfoExtEntity extends Entity {

    private String attrKey;

    private String attrValue;

    private String userUuid;

    public String getAttrKey() {
        return this.attrKey;
    }

    public void setAttrKey(final String attrKey) {
        this.attrKey = attrKey;
    }

    public String getAttrValue() {
        return this.attrValue;
    }

    public void setAttrValue(final String attrValue) {
        this.attrValue = attrValue;
    }

    public String getUserUuid() {
        return this.userUuid;
    }

    public void setUserUuid(final String userUuid) {
        this.userUuid = userUuid;
    }
}
