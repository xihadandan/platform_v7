package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.DepartmentPrivilege;
import com.wellsoft.pt.org.entity.DepartmentPrivilegeId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
@Repository
public class DepartmentPrivilegeDao extends OrgHibernateDao<DepartmentPrivilege, String> {

    private static final String QUERY_DEPARTMENT_P_BY_DEPARTMRNTUUID =
            "select department.departmentPrivilegeId.departmentUuid as departmentUuid,"
                    + "department.departmentPrivilegeId.privilegeUuid as privilegeUuid,"
                    + "department.departmentPrivilegeId.tenantId  as tenantId  "
                    + "from DepartmentPrivilege department  "
                    + "where "
                    + "department.departmentPrivilegeId.departmentUuid=:departmentUuid";
    private static final String QUERY_DEPARTMENT_P_BY_PUUID =
            "select department.departmentPrivilegeId.departmentUuid as departmentUuid,"
                    + "department.departmentPrivilegeId.privilegeUuid as privilegeUuid,"
                    + "department.departmentPrivilegeId.tenantId  as tenantId  "
                    + "from DepartmentPrivilege department "
                    + " where "
                    + "department.departmentPrivilegeId.privilegeUuid=:privilegeUuid";
    private static final String QUERY_BY_P_AND_UUID = "select department.departmentPrivilegeId.departmentUuid as departmentUuid,"
            + "department.departmentPrivilegeId.privilegeUuid as privilegeUuid,"
            + "department.departmentPrivilegeId.tenantId  as tenantId  "
            + "from DepartmentPrivilege department "
            + " where "
            + "department.departmentPrivilegeId.privilegeUuid=:privilegeUuid and "
            + "department.departmentPrivilegeId.departmentUuid=:departmentUuid";

    public List<DepartmentPrivilege> getDepartmentPrivilegeByDepartmentUuid(String departmentUuid) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("departmentUuid", departmentUuid);
        List<DepartmentPrivilegeId> departmentPrivilegeIds = this.query(QUERY_DEPARTMENT_P_BY_DEPARTMRNTUUID, queryMap, DepartmentPrivilegeId.class);
        List<DepartmentPrivilege> departmentPrivileges = new ArrayList<DepartmentPrivilege>();
        DepartmentPrivilege departmentPrivilege;
        for (DepartmentPrivilegeId departmentPrivilegeId : departmentPrivilegeIds) {
            departmentPrivilege = new DepartmentPrivilege();
            departmentPrivilege.setDepartmentPrivilegeId(departmentPrivilegeId);
            departmentPrivileges.add(departmentPrivilege);
        }
        return departmentPrivileges;
    }

    public List<DepartmentPrivilege> getDepartmentPrivilegeByPrivilegeUuid(String privilegeUuid) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("privilegeUuid", privilegeUuid);
        List<DepartmentPrivilegeId> departmentPrivilegeIds = this.query(QUERY_DEPARTMENT_P_BY_PUUID, queryMap, DepartmentPrivilegeId.class);
        List<DepartmentPrivilege> departmentPrivileges = new ArrayList<DepartmentPrivilege>();
        DepartmentPrivilege departmentPrivilege;
        for (DepartmentPrivilegeId departmentPrivilegeId : departmentPrivilegeIds) {
            departmentPrivilege = new DepartmentPrivilege();
            departmentPrivilege.setDepartmentPrivilegeId(departmentPrivilegeId);
            departmentPrivileges.add(departmentPrivilege);
        }
        return departmentPrivileges;
    }

    public void saveDepartmentPrivilege(
            DepartmentPrivilege departmentPrivilege) {
        // TODO Auto-generated method stub
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("departmentUuid", departmentPrivilege.getDepartmentPrivilegeId().getDepartmentUuid());
        queryMap.put("privilegeUuid", departmentPrivilege.getDepartmentPrivilegeId().getPrivilegeUuid());
        List<DepartmentPrivilegeId> departmentPrivilegeIds = this.query(QUERY_DEPARTMENT_P_BY_PUUID, queryMap, DepartmentPrivilegeId.class);
        if (departmentPrivilegeIds.size() == 0) {
            this.save(departmentPrivilege);
        }
    }
}
