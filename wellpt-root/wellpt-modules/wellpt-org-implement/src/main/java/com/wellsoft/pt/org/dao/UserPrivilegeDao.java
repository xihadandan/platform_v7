package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.UserPrivilege;
import com.wellsoft.pt.org.entity.UserPrivilegeId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
@Repository
public class UserPrivilegeDao extends OrgHibernateDao<UserPrivilege, String> {

    private static final String QUERY_USER_P_BY_USERUUID =
            "select userPrivilege.userPrivilegeId.userUuid as userUuid,"
                    + "userPrivilege.userPrivilegeId.privilegeUuid as privilegeUuid,"
                    + "userPrivilege.userPrivilegeId.tenantId  as tenantId  "
                    + "from UserPrivilege userPrivilege  "
                    + "where "
                    + "userPrivilege.userPrivilegeId.userUuid=:userUuid";
    private static final String QUERY_USER_P_BY_PUUID =
            "select userPrivilege.userPrivilegeId.userUuid as userUuid,"
                    + "userPrivilege.userPrivilegeId.privilegeUuid as privilegeUuid,"
                    + "userPrivilege.userPrivilegeId.tenantId  as tenantId  "
                    + "from UserPrivilege userPrivilege  "
                    + "where "
                    + "userPrivilege.userPrivilegeId.privilegeUuid=:privilegeUuid";

    private static final String QUERY_USER_P_BY_PUUID_UUUID =
            "select userPrivilege.userPrivilegeId.userUuid as userUuid,"
                    + "userPrivilege.userPrivilegeId.privilegeUuid as privilegeUuid,"
                    + "userPrivilege.userPrivilegeId.tenantId  as tenantId  "
                    + "from UserPrivilege userPrivilege  "
                    + "where "
                    + "userPrivilege.userPrivilegeId.privilegeUuid=:privilegeUuid and "
                    + "userPrivilege.userPrivilegeId.userUuid=:userUuid";

    public List<UserPrivilege> getUserPrivilegeByUserUuid(String userUuid) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("userUuid", userUuid);
        List<UserPrivilegeId> userPrivilegeIds = this.query(QUERY_USER_P_BY_USERUUID, queryMap, UserPrivilegeId.class);
        List<UserPrivilege> userPrivileges = new ArrayList<UserPrivilege>();
        UserPrivilege userPrivilege;
        for (UserPrivilegeId userPrivilegeId : userPrivilegeIds) {
            userPrivilege = new UserPrivilege();
            userPrivilege.setUserPrivilegeId(userPrivilegeId);
            userPrivileges.add(userPrivilege);
        }
        return userPrivileges;
    }

    public List<UserPrivilege> getUserPrivilegeByPrivilegeUuid(String privilegeUuid) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("privilegeUuid", privilegeUuid);
        List<UserPrivilegeId> userPrivilegeIds = this.query(QUERY_USER_P_BY_PUUID, queryMap, UserPrivilegeId.class);
        List<UserPrivilege> userPrivileges = new ArrayList<UserPrivilege>();
        UserPrivilege userPrivilege;
        for (UserPrivilegeId userPrivilegeId : userPrivilegeIds) {
            userPrivilege = new UserPrivilege();
            userPrivilege.setUserPrivilegeId(userPrivilegeId);
            userPrivileges.add(userPrivilege);
        }
        return userPrivileges;
    }

    public void saveUserPrivilege(UserPrivilege userPrivilege) {
        // TODO Auto-generated method stub
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("privilegeUuid", userPrivilege.getUserPrivilegeId().getPrivilegeUuid());
        queryMap.put("userUuid", userPrivilege.getUserPrivilegeId().getUserUuid());
        List<UserPrivilegeId> userPrivilegeIds = this.query(QUERY_USER_P_BY_PUUID, queryMap, UserPrivilegeId.class);
        if (userPrivilegeIds.size() == 0) {
            this.save(userPrivilege);
        }
    }
}
