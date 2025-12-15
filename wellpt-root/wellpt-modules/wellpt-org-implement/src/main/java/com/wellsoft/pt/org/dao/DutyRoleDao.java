package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.DutyRole;
import com.wellsoft.pt.org.entity.DutyRoleId;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
@Repository
public class DutyRoleDao extends OrgHibernateDao<DutyRole, String> {

    private static final String QUERY_DUTY_ROLE_BY_DUTYUUID = "select dutyRole.dutyRoleId.dutyUuid as dutyUuid,"
            + "dutyRole.dutyRoleId.roleUuid as roleUuid," + "dutyRole.dutyRoleId.tenantId  as tenantId  "
            + "from DutyRole dutyRole  " + "where " + "dutyRole.dutyRoleId.dutyUuid=:dutyUuid";
    private static final String QUERY_DUTY_ROLE_UUID = "select dutyRole.dutyRoleId.dutyUuid as dutyUuid,"
            + "dutyRole.dutyRoleId.roleUuid as roleUuid," + "dutyRole.dutyRoleId.tenantId  as tenantId  "
            + "from DutyRole dutyRole  " + "where "
            + "dutyRole.dutyRoleId.roleUuid=:roleUuid and dutyRole.dutyRoleId.tenantId=:tenantId ";
    private static final String DELETE_DUTY_ROLE_BY_DUTYUUID_ROLE_UUID = "delete DutyRole u " + "where"
            + " u.dutyRoleId.dutyUuid=:dutyUuid and " + " u.dutyRoleId.roleUuid=:roleUuid";

    public List<DutyRole> getDutyRoleByDutyUuid(String dutyUuid) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("dutyUuid", dutyUuid);
        List<DutyRoleId> dutyRoleIds = this.query(QUERY_DUTY_ROLE_BY_DUTYUUID, queryMap, DutyRoleId.class);
        List<DutyRole> dutyRoles = new ArrayList<DutyRole>();
        DutyRole dutyRole = null;
        for (DutyRoleId dutyRoleId : dutyRoleIds) {
            dutyRole = new DutyRole();
            dutyRole.setDutyRoleId(dutyRoleId);
            dutyRoles.add(dutyRole);
        }
        return dutyRoles;
    }

    public List<DutyRole> getDutyRoleByRoleUuid(String roleUuid) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("roleUuid", roleUuid);
        queryMap.put("tenantId", SpringSecurityUtils.getCurrentTenantId());
        List<DutyRoleId> dutyRoleIds = this.query(QUERY_DUTY_ROLE_UUID, queryMap, DutyRoleId.class);
        List<DutyRole> dutyRoles = new ArrayList<DutyRole>();
        DutyRole dutyRole = null;
        for (DutyRoleId dutyRoleId : dutyRoleIds) {
            dutyRole = new DutyRole();
            dutyRole.setDutyRoleId(dutyRoleId);
            dutyRoles.add(dutyRole);
        }
        return dutyRoles;
    }

    public void deleteDutyRoleByDutyUuidAndRoleUuid(String dutyUuid, String roleUuid) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("dutyUuid", dutyUuid);
        queryMap.put("roleUuid", roleUuid);
        this.batchExecute(DELETE_DUTY_ROLE_BY_DUTYUUID_ROLE_UUID, queryMap);

    }

    public void saveDutyRole(DutyRole dutyRole) {
        // TODO Auto-generated method stub
        this.save(dutyRole);
    }

}
