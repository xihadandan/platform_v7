package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 组织单元实例角色成员
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "ORG_ELEMENT_ROLE_MEMBER")
@DynamicUpdate
@DynamicInsert
public class OrgElementRoleMemberEntity extends SysEntity {


    private static final long serialVersionUID = 6205621234562671906L;

    private Long orgVersionUuid;
    private Long orgRoleUuid;
    private String member;
    private String orgElementId;

    public Long getOrgVersionUuid() {
        return this.orgVersionUuid;
    }

    public void setOrgVersionUuid(final Long orgVersionUuid) {
        this.orgVersionUuid = orgVersionUuid;
    }

    public Long getOrgRoleUuid() {
        return this.orgRoleUuid;
    }

    public void setOrgRoleUuid(final Long orgRoleUuid) {
        this.orgRoleUuid = orgRoleUuid;
    }

    public String getMember() {
        return this.member;
    }

    public void setMember(final String member) {
        this.member = member;
    }

    public String getOrgElementId() {
        return this.orgElementId;
    }

    public void setOrgElementId(final String orgElementId) {
        this.orgElementId = orgElementId;
    }
}
