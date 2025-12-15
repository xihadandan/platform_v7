package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 组织群组
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年02月08日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "ORG_GROUP_ROLE")
@DynamicUpdate
@DynamicInsert
public class OrgGroupRoleEntity extends SysEntity {


    private static final long serialVersionUID = -4222063078776403396L;
    private Long groupUuid;

    private String roleUuid;

    public Long getGroupUuid() {
        return this.groupUuid;
    }

    public void setGroupUuid(final Long groupUuid) {
        this.groupUuid = groupUuid;
    }

    public String getRoleUuid() {
        return this.roleUuid;
    }

    public void setRoleUuid(final String roleUuid) {
        this.roleUuid = roleUuid;
    }
}
