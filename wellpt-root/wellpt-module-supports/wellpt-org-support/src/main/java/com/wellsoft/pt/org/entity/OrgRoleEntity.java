package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 组织角色
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "ORG_ROLE")
@DynamicUpdate
@DynamicInsert
public class OrgRoleEntity extends SysEntity {


    private static final long serialVersionUID = 6205621234562671906L;

    private Long orgVersionUuid;
    private Long orgUuid;
    private String id;
    private String name;
    private String remark;

    public Long getOrgVersionUuid() {
        return this.orgVersionUuid;
    }

    public void setOrgVersionUuid(final Long orgVersionUuid) {
        this.orgVersionUuid = orgVersionUuid;
    }

    public Long getOrgUuid() {
        return this.orgUuid;
    }

    public void setOrgUuid(final Long orgUuid) {
        this.orgUuid = orgUuid;
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }


    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(final String remark) {
        this.remark = remark;
    }
}
