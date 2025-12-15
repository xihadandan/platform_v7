package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 组织单元实例关联角色
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "ORG_ELEMENT_ROLE_RELA")
@DynamicUpdate
@DynamicInsert
public class OrgElementRoleRelaEntity extends SysEntity {


    private static final long serialVersionUID = -2100534995482836346L;
    private Long orgVersionUuid;
    private Long orgElementUuid;
    private String roleUuid;

    public Long getOrgElementUuid() {
        return this.orgElementUuid;
    }

    public void setOrgElementUuid(final Long orgElementUuid) {
        this.orgElementUuid = orgElementUuid;
    }

    public String getRoleUuid() {
        return this.roleUuid;
    }

    public void setRoleUuid(final String roleUuid) {
        this.roleUuid = roleUuid;
    }

    public Long getOrgVersionUuid() {
        return this.orgVersionUuid;
    }

    public void setOrgVersionUuid(final Long orgVersionUuid) {
        this.orgVersionUuid = orgVersionUuid;
    }
}
