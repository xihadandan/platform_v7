package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 业务组织单元实例关联权限角色
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年11月29日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "BIZ_ORG_ELEMENT_ROLE_RELA")
@DynamicUpdate
@DynamicInsert
public class BizOrgElementRoleRelaEntity extends SysEntity {

    private String roleUuid;
    private String bizOrgElementId;

    public BizOrgElementRoleRelaEntity() {
    }

    public BizOrgElementRoleRelaEntity(String roleUuid, String bizOrgElementId) {
        this.roleUuid = roleUuid;
        this.bizOrgElementId = bizOrgElementId;
    }

    public String getRoleUuid() {
        return roleUuid;
    }

    public void setRoleUuid(String roleUuid) {
        this.roleUuid = roleUuid;
    }

    public String getBizOrgElementId() {
        return bizOrgElementId;
    }

    public void setBizOrgElementId(String bizOrgElementId) {
        this.bizOrgElementId = bizOrgElementId;
    }
}
