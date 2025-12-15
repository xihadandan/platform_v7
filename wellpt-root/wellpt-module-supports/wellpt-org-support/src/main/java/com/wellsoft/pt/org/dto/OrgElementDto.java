package com.wellsoft.pt.org.dto;

import com.wellsoft.pt.org.entity.*;

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
public class OrgElementDto extends OrgElementEntity {

    private static final long serialVersionUID = 1967836975226646408L;


    private OrgElementManagementEntity management;

    private List<OrgElementRoleMemberEntity> orgElementRoleMembers;

    private OrgJobDutyDto jobDuty;

    private List<String> roleUuids;

    private List<OrgElementI18nEntity> i18ns;

    private List<OrgElementExtAttrEntity> attrs;


    public OrgElementManagementEntity getManagement() {
        return this.management;
    }

    public void setManagement(final OrgElementManagementEntity management) {
        this.management = management;
    }

    public List<OrgElementRoleMemberEntity> getOrgElementRoleMembers() {
        return this.orgElementRoleMembers;
    }

    public void setOrgElementRoleMembers(final List<OrgElementRoleMemberEntity> orgElementRoleMembers) {
        this.orgElementRoleMembers = orgElementRoleMembers;
    }

    public OrgJobDutyDto getJobDuty() {
        return this.jobDuty;
    }

    public void setJobDuty(final OrgJobDutyDto jobDuty) {
        this.jobDuty = jobDuty;
    }

    public List<String> getRoleUuids() {
        return this.roleUuids;
    }

    public void setRoleUuids(final List<String> roleUuids) {
        this.roleUuids = roleUuids;
    }

    public List<OrgElementI18nEntity> getI18ns() {
        return i18ns;
    }

    public void setI18ns(List<OrgElementI18nEntity> i18ns) {
        this.i18ns = i18ns;
    }

    public List<OrgElementExtAttrEntity> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<OrgElementExtAttrEntity> attrs) {
        this.attrs = attrs;
    }
}
