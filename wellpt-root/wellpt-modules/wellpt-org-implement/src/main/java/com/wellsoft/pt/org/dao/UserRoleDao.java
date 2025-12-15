package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.UserRole;
import com.wellsoft.pt.org.entity.UserRoleId;
import com.wellsoft.pt.security.util.SpringSecurityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
//@Repository
@Deprecated
public class UserRoleDao extends OrgHibernateDao<UserRole, String> {
    private static final String QUERY_USER_ROLE_BY_USERUUID = "select userRole.userRoleId.userUuid as userUuid,userRole.userRoleId.roleUuid as roleUuid,userRole.userRoleId.tenantId  as tenantId  from UserRole userRole  where userRole.userRoleId.userUuid=:userUuid";
    private static final String QUERY_USER_ROLE_BY_ROLEUUID = "select userRole.userRoleId.userUuid as userUuid,userRole.userRoleId.roleUuid as roleUuid,userRole.userRoleId.tenantId  as tenantId  from UserRole userRole  where userRole.userRoleId.roleUuid=:roleUuid and userRole.userRoleId.tenantId=:tenantId";
    private static final String DELETE_USER_ROLE_BY_USERUUID_ROLE_UUID = "delete UserRole u where u.userRoleId.userUuid=:userUuid and u.userRoleId.roleUuid=:roleUuid";
    private static final String GET_BY_ROLE_AND_UUID = "select userRole.userRoleId.userUuid as userUuid,userRole.userRoleId.roleUuid as roleUuid,userRole.userRoleId.tenantId  as tenantId  from UserRole userRole  where userRole.userRoleId.userUuid=:userUuid and userRole.userRoleId.roleUuid=:roleUuid";

    public List<UserRole> getUserRoleByUserUuid(String userUuid) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("userUuid", userUuid);
        List<UserRoleId> userRoleIds = this.query(QUERY_USER_ROLE_BY_USERUUID, queryMap, UserRoleId.class);
        List<UserRole> userRoles = new ArrayList<UserRole>();
        UserRole userRole = null;
        for (UserRoleId userRoleId : userRoleIds) {
            userRole = new UserRole();
            userRole.setUserRoleId(userRoleId);
            userRoles.add(userRole);
        }
        return userRoles;
    }

    public List<UserRole> getUserRoleByRoleUuid(String roleUuid) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("roleUuid", roleUuid);
        queryMap.put("tenantId", SpringSecurityUtils.getCurrentTenantId());
        List<UserRoleId> userRoleIds = this.query(QUERY_USER_ROLE_BY_ROLEUUID, queryMap, UserRoleId.class);
        List<UserRole> userRoles = new ArrayList<UserRole>();
        UserRole userRole = null;
        for (UserRoleId userRoleId : userRoleIds) {
            userRole = new UserRole();
            userRole.setUserRoleId(userRoleId);
            userRoles.add(userRole);
        }
        return userRoles;
    }

    public void deleteUserRoleByUserUuidAndRoleUuid(String userUuid, String roleUuid) {
        // TODO Auto-generated method stub
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("userUuid", userUuid);
        queryMap.put("roleUuid", roleUuid);
        this.batchExecute(DELETE_USER_ROLE_BY_USERUUID_ROLE_UUID, queryMap);
    }

    public void saveRole(UserRole userRole) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("roleUuid", userRole.getUserRoleId().getRoleUuid());
        queryMap.put("userUuid", userRole.getUserRoleId().getUserUuid());
        List<UserRoleId> userRoleIds = this.query(GET_BY_ROLE_AND_UUID, queryMap, UserRoleId.class);
        if (userRoleIds.size() > 0) {
            return;
        } else {
            this.save(userRole);
        }
    }

}
