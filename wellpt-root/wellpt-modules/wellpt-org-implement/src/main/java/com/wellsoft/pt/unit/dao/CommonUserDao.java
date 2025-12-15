package com.wellsoft.pt.unit.dao;

import com.wellsoft.pt.org.dao.OrgHibernateDao;
import com.wellsoft.pt.unit.entity.CommonUser;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: CommonUserDao.java
 *
 * @author liuzq
 * @date 2013-11-5
 */
@Repository
public class CommonUserDao extends OrgHibernateDao<CommonUser, String> {
    private static final String QUERY_BY_DEPARTMENT_USERID = "select user from CommonUser user left join user.departments dept where dept.uuid = :deptUuid and user.id = :userId";

    private static final String QUERY_COMMONUNIT_BY_CURRENTUSERID = "from CommonUser user where user.id=:userId and user.unit.tenantId=:tenantId";

    /**
     * 根据公共库部门ID和用户ID，获取公共库用户列表
     *
     * @param paramMap
     * @return
     */
    public CommonUser findByDepartmentAndUserId(Map<String, Object> paramMap) {
        return this.findUnique(QUERY_BY_DEPARTMENT_USERID, paramMap);
    }

    public CommonUser getByCurrentUserId(String userId, String tenantId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", userId);
        paramMap.put("tenantId", tenantId);
        return this.findUnique(QUERY_COMMONUNIT_BY_CURRENTUSERID, paramMap);
    }

}
