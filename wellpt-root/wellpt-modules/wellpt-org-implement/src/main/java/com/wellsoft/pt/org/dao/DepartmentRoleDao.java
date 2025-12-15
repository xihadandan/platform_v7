package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.DepartmentRole;
import com.wellsoft.pt.org.entity.DepartmentRoleId;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
@Repository
public class DepartmentRoleDao extends OrgHibernateDao<DepartmentRole, String> {
    private static final String QUERY_DEPARTMENT_ROLE_BY_DEPARTMENTUUID = "select departmentRole.departmentRoleId.departmentUuid as departmentUuid,"
            + "departmentRole.departmentRoleId.roleUuid as roleUuid,"
            + "departmentRole.departmentRoleId.tenantId  as tenantId  "
            + "from DepartmentRole departmentRole  "
            + "where " + "departmentRole.departmentRoleId.departmentUuid=:departmentUuid";
    private static final String QUERY_DEPARTMENT_ROLE_UUID = "select departmentRole.departmentRoleId.departmentUuid as departmentUuid,"
            + "departmentRole.departmentRoleId.roleUuid as roleUuid,"
            + "departmentRole.departmentRoleId.tenantId  as tenantId  "
            + "from DepartmentRole departmentRole  "
            + "where "
            + "departmentRole.departmentRoleId.roleUuid=:roleUuid and departmentRole.departmentRoleId.tenantId=:tenantId ";
    private static final String DELETE_DEPARTMENT_ROLE_BY_DEPARTMENTUUID_ROLE_UUID = "delete DepartmentRole u "
            + "where" + " u.departmentRoleId.departmentUuid=:departmentUuid and "
            + " u.departmentRoleId.roleUuid=:roleUuid";

    private static final String GET_BY_ROLE_AND_UUID = "select departmentRole.departmentRoleId.departmentUuid as departmentUuid,"
            + "departmentRole.departmentRoleId.roleUuid as roleUuid,"
            + "departmentRole.departmentRoleId.tenantId  as tenantId  "
            + "from DepartmentRole departmentRole  "
            + "where"
            + " departmentRole.departmentRoleId.departmentUuid=:departmentUuid and "
            + " departmentRole.departmentRoleId.roleUuid=:roleUuid";

    public List<DepartmentRole> getDepartmentRoleByDepartmentUuid(String departmentUuid) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("departmentUuid", departmentUuid);
        List<DepartmentRoleId> departmentRoleIds = this.query(QUERY_DEPARTMENT_ROLE_BY_DEPARTMENTUUID, queryMap,
                DepartmentRoleId.class);
        List<DepartmentRole> departmentRoles = new ArrayList<DepartmentRole>();
        DepartmentRole departmentRole = null;
        for (DepartmentRoleId departmentRoleId : departmentRoleIds) {
            departmentRole = new DepartmentRole();
            departmentRole.setDepartmentRoleId(departmentRoleId);
            departmentRoles.add(departmentRole);
        }
        return departmentRoles;
    }

    public List<DepartmentRole> getDepartmentRoleByRoleUuid(String roleUuid) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("roleUuid", roleUuid);
        queryMap.put("tenantId", SpringSecurityUtils.getCurrentTenantId());
        List<DepartmentRoleId> departmentRoleIds = this.query(QUERY_DEPARTMENT_ROLE_UUID, queryMap,
                DepartmentRoleId.class);
        List<DepartmentRole> departmentRoles = new ArrayList<DepartmentRole>();
        DepartmentRole departmentRole = null;
        for (DepartmentRoleId departmentRoleId : departmentRoleIds) {
            departmentRole = new DepartmentRole();
            departmentRole.setDepartmentRoleId(departmentRoleId);
            departmentRoles.add(departmentRole);
        }
        return departmentRoles;
    }

    public void deleteDepartmentRoleByDepartmentUuidAndRoleUuid(String departmentUuid, String roleUuid) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("departmentUuid", departmentUuid);
        queryMap.put("roleUuid", roleUuid);
        this.batchExecute(DELETE_DEPARTMENT_ROLE_BY_DEPARTMENTUUID_ROLE_UUID, queryMap);
    }

    public void saveDepartmentRole(DepartmentRole departmentRole) {
        // TODO Auto-generated method stub
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("departmentUuid", departmentRole.getDepartmentRoleId().getDepartmentUuid());
        queryMap.put("roleUuid", departmentRole.getDepartmentRoleId().getRoleUuid());
        List<DepartmentRoleId> departmentRoleIds = this.query(GET_BY_ROLE_AND_UUID, queryMap, DepartmentRoleId.class);
        if (departmentRoleIds.size() == 0) {
            this.save(departmentRole);
        }
    }
}
