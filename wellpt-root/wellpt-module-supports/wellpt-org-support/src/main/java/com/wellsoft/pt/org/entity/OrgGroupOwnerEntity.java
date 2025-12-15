package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 组织群组使用者
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年02月08日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "ORG_GROUP_OWNER")
@DynamicUpdate
@DynamicInsert
public class OrgGroupOwnerEntity extends SysEntity {

    private String ownerId;

    private Long groupUuid;


    public String getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(final String ownerId) {
        this.ownerId = ownerId;
    }

    public Long getGroupUuid() {
        return this.groupUuid;
    }

    public void setGroupUuid(final Long groupUuid) {
        this.groupUuid = groupUuid;
    }
}
