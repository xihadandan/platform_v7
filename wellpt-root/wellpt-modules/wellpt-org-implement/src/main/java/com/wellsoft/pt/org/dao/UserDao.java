package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.Organization;
import com.wellsoft.pt.org.entity.User;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 *
 */
@Repository
public class UserDao extends OrgHibernateDao<User, String> {

    private static final String QUERY_ALL_ADMIN_ID = "select user.id from User user where user.issys = true and user.enabled = true";
    private static final String QUERY_ALL_BY_DEPARTMENT = "select user.id from User user where user.enabled = true and exists ( select 1 from DepartmentUserJob userJob where userJob.user = user and userJob.department.uuid = :departmentUuid )";

    private static final String UPDATE_LAST_LOGIN_TIME = "update User user set user.lastLoginTime = user.modifyTime, modifyTime = :modifyTime where uuid = :uuid";

    private static final String GET_USER_ID_LIKE_NAME = "select id from User user where user.loginName like '%' || :name || '%' or user.userName like '%' || :name || '%'";

    private static final String GET_BY_ID_NUMBER_AND_SUBJECT_DN = "from User user where user.idNumber = :idNumber and "
            + " exists (select up.uuid from UserProperty up where user.uuid = up.userUuid and up.name = 'certificate.subject' and up.value = :subjectDN)";

    private static final String UPDATE_USER_MAJORNAME = "update User set majorJobName = :newJobName where uuid = :userUuid";

    private static final String UPDATE_USER_OTHERNAME = "update User set otherJobNames = :newJobName where uuid = :userUuid";

    private static final String UPDATE_USER_DEPTNAME = "update User set departmentName = :newDeptName where uuid = :uuid";

    private static final String UPDATE_USER_PASSWORD = "update User set password = :password where uuid = :uuid";

    private static final String FILTER_USER_BY_ORG_ID = "select t.id from User t where t.id in(:userIds) and exists (select duj.user.uuid from DepartmentUserJob duj where duj.department.orgId = :orgId and duj.user.uuid = t.uuid)";
    private static final String FILTER_DEPARTMENT_PRINCIPAL_BY_ORG_ID = "select t.id from User t where t.id in(:userIds) and exists (select dp.orgId from DeptPrincipal dp, Department d where dp.orgId = t.id and dp.departmentUuid = d.uuid and d.orgId = :orgId)";
    private static final String GET_USER_ORGS = "from Organization t1 where "
            + "exists(select t2.uuid from Department t2 left join t2.departmentUsers as t3 where t1.id = t2.orgId "
            + "and exists(select t4.uuid from User t4 where t4.uuid = t3.user.uuid and t4.id = :userId))"
            + " order by t1.code asc";

    /**
     * @param leaderId
     * @return
     */
    public User getById(String id) {
        return this.findUniqueBy("id", id);
    }

    /**
     * 获取所管理员的ID
     *
     * @return
     */
    public List<String> getAllAdminIds() {
        return this.find(QUERY_ALL_ADMIN_ID, new HashMap<String, String>(0));
    }

    /**
     * 更新上次登录时间
     *
     * @param uuid
     * @param modifyTime
     */
    public void updateLastLoginTime(String uuid, Date modifyTime) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("uuid", uuid);
        values.put("modifyTime", modifyTime);
        this.batchExecute(UPDATE_LAST_LOGIN_TIME, values);
    }

    /**
     * 传递一个字符串参数，可以匹配出包含该字符串的所有用户的ID，作为数组返回，没有就返回空；
     *
     * @param rawName
     * @return
     */
    public List<String> getUserIdsLikeName(String rawName) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("name", rawName);
        return this.find(GET_USER_ID_LIKE_NAME, values);
    }

    /**
     * @param idNumber
     * @param subjectDN
     * @return
     */
    public List<User> getByIdNumberAndSubjectDN(String idNumber, String subjectDN) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("idNumber", idNumber);
        values.put("subjectDN", subjectDN);
        return this.find(GET_BY_ID_NUMBER_AND_SUBJECT_DN, values);
    }

    /**
     * @param oldJobName
     * @param newJobName
     * @author liyb
     * date    2015.1.4
     * 修改用户的主职位名称
     */
    public void updateMajorName(String newJobName, String userUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("newJobName", newJobName);
        values.put("userUuid", userUuid);
        this.batchExecute(UPDATE_USER_MAJORNAME, values);
    }

    /**
     * @param oldJobName
     * @param newJobName
     * @author liyb
     * date    2015.1.4
     * 修改用户的其他职位名称
     */
    public void updateOtherName(String newJobName, String userUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("newJobName", newJobName);
        values.put("userUuid", userUuid);
        this.batchExecute(UPDATE_USER_OTHERNAME, values);
    }

    /**
     * @param newDeptName
     * @param uuid
     * @author liyb
     * date    2015.1.6
     * 修改用户的部门
     */
    public void updateUserDeptName(String newDeptName, String uuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("newDeptName", newDeptName);
        values.put("uuid", uuid);
        this.batchExecute(UPDATE_USER_DEPTNAME, values);
    }

    /**
     * 通过员工编号获取用户信息
     *
     * @param employeeNumber
     * @return
     */
    public User getUserByEmployeeNumber(String employeeNumber) {
        return this.findUniqueBy("employeeNumber", employeeNumber);
    }

    /**
     * 作者： yuyq
     * 创建时间：2015-3-26 下午4:18:45
     * 类描述：TODO
     * 备注：设置用户初始化密码为0
     *
     * @param password
     * @param uuid
     */
    public void updateUserPassword(String password, String uuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("password", password);
        values.put("uuid", uuid);
        this.batchExecute(UPDATE_USER_PASSWORD, values);
    }

    /**
     * 根据部门id查询挂在该部门下的所有人员
     *
     * @param rawName
     * @return
     */
    public List<String> getUserIdsByDepartment(String departmentUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("departmentUuid", departmentUuid);
        return this.find(QUERY_ALL_BY_DEPARTMENT, values);
    }

    /**
     * @param orgId
     * @param userIds
     * @return
     */
    public List<String> filterUserIdsByOrgId(String orgId, Collection<String> userIds) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userIds", userIds);
        values.put("orgId", orgId);
        return this.find(FILTER_USER_BY_ORG_ID, values);
    }

    /**
     * @param orgId
     * @param userIds
     * @return
     */
    public List<String> filterDepartmentPrincipalsByOrgId(String orgId, Collection<String> userIds) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userIds", userIds);
        values.put("orgId", orgId);
        return this.find(FILTER_DEPARTMENT_PRINCIPAL_BY_ORG_ID, values);
    }

    /**
     * @param userId
     * @return
     */
    public List<Organization> getUserOrganizations(String userId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userId", userId);
        return this.find(GET_USER_ORGS, values);
    }
}
