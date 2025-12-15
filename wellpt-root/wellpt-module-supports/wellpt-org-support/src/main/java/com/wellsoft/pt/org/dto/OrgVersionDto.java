package com.wellsoft.pt.org.dto;

import com.wellsoft.pt.org.entity.OrgElementEntity;
import com.wellsoft.pt.org.entity.OrgRoleEntity;
import com.wellsoft.pt.org.entity.OrgVersionEntity;
import com.wellsoft.pt.org.entity.OrganizationEntity;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月23日   chenq	 Create
 * </pre>
 */
public class OrgVersionDto extends OrgVersionEntity {
    private static final long serialVersionUID = -4459715210948090696L;

    private List<OrgRoleEntity> orgRoles;

    private List<OrgElementEntity> orgElements;

    private OrganizationEntity organization;

    public List<OrgElementEntity> getOrgElements() {
        return this.orgElements;
    }

    public void setOrgElements(final List<OrgElementEntity> orgElements) {
        this.orgElements = orgElements;
    }

    public List<OrgRoleEntity> getOrgRoles() {
        return this.orgRoles;
    }

    public void setOrgRoles(final List<OrgRoleEntity> orgRoles) {
        this.orgRoles = orgRoles;
    }

    public OrganizationEntity getOrganization() {
        return this.organization;
    }

    public void setOrganization(final OrganizationEntity organization) {
        this.organization = organization;
    }
}
