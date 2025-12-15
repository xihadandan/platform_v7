package com.wellsoft.pt.org.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.org.bean.UserBean;
import com.wellsoft.pt.org.entity.Department;
import com.wellsoft.pt.org.entity.Job;
import com.wellsoft.pt.org.entity.Organization;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.org.support.UserProfile;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.Role;
import org.hibernate.Query;

import java.util.*;

/**
 * Description: 主要实现用户、部门、群组、职位的相关等操作
 *
 * @author lilin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-21.1	lilin		2012-11-21		Create
 * 2013-9-25    liuzq       2013-9-25       Update
 * </pre>
 * @date 2012-11-21
 */
public interface UserService {

    /**
     * 根据UUID获取用户
     *
     * @param uuid
     * @return
     */
    User get(String uuid);

    /**
     * 根据用户ID获取用户
     *
     * @param id
     * @return
     */
    User getById(String id);

    /**
     * 根据用户登录名获取用户
     *
     * @param loginName
     * @return
     */
    User getByLoginName(String loginName);

    /**
     * 根据用户证书主体获取用户
     *
     * @param subjectDN
     * @return
     */
    User getBySubjectDN(String subjectDN);

    /**
     * 根据用户身份证获取用户
     *
     * @param idNumber
     * @return
     */
    User getByIdNumber(String idNumber);

    /**
     * 根据用户身份证获取用户和证书主体获取用户
     *
     * @param idNumber
     * @param subjectDN
     * @return
     */
    User getByIdNumberAndSubjectDN(String idNumber, String subjectDN);

    /**
     * 如何描述该方法
     *
     * @return
     */
    List<User> getAll();

    /**
     * 获取所有领导
     *
     * @param userid
     * @return
     */
    List<User> getLeaders(String userid);

    /**
     * @param uuid
     * @return
     */
    UserBean getBean(String uuid);

    /**
     * 如何描述该方法
     *
     * @param id
     * @return
     */
    UserBean getBeanById(String id);

    /**
     * 如何描述该方法
     *
     * @param bean
     */
    void saveBean(UserBean bean);

    /**
     * 根据用户ID删除用户
     *
     * @param id
     */
    void deleteById(String id);

    /**
     * 根据用户ID，批量删除用户
     *
     * @param ids
     */
    void deleteByIds(Collection<String> ids);

    /**
     * 根据用户UUID获取用户所在部门的顶级部门
     *
     * @param uuid
     * @return
     */
    Department getTopDepartment(String uuid);

    /**
     * 根据用户UUID加载角色树，自动选择已有角色
     *
     * @param uuid
     * @return
     */
    TreeNode getRoleTree(String uuid);

    /**
     * 根据UUID加载用户角色嵌套树, 包含角色嵌套及权限
     *
     * @param uuid
     * @return
     */
    TreeNode getUserRoleNestedRoleTree(String uuid);

    /**
     * 根据UUID获取指定人的直接下属
     *
     * @param uuid
     * @return
     */
    List<User> getSubordinate(String uuid);

    /**
     * 根据用户ID获取用户直接领导的ID
     *
     * @param userId
     * @return
     */
    List<String> getUserLeaderIds(String userId);

    /**
     * 根据用户获取用户直接领导的ID
     * add by xieming
     *
     * @param userId
     * @return
     */
    List<String> getUserLeaderIds(User user);

    /**
     * 根据用户ID获取用户所有领导的ID
     *
     * @param userId
     * @return
     */
    List<String> getAllUserLeaderIds(String userId);

    /**
     * 根据用户ID获取用户所属的部门ID
     *
     * @param userId
     * @return
     */
    List<String> getDepartmentIdsByUserId(String userId);

    /**
     * 根据用户ID获取用户所属的主部门
     *
     * @param userId
     * @return
     */
    Department getDepartmentByUserId(String userId);

    /**
     * 根据用户ID获取用户上级的部门ID
     *
     * @param userId
     * @return
     */
    String getParentDepartmentIdByUserId(String userId);

    /**
     * 根据用户ID获取用户所有的上级部门
     *
     * @param userId
     * @return
     */
    List<Department> getParentDepartmentsByUserId(String userId);

    /**
     * 如何描述该方法
     *
     * @param userId
     * @return
     */
    String getRootDepartmentIdByUserId(String userId);

    /**
     * 获取全组织下的所有用户ID
     *
     * @return
     */
    List<String> getAllDepartmentUserIds();

    /**
     * 获取所管理员的ID
     *
     * @return
     */
    List<String> getAllAdminIds();

    /**
     * 根据用户ID列表获取用户列表
     *
     * @param ids
     * @return
     */
    List<User> getByIds(Collection<String> ids);

    /**
     * 根据用户ID列表获取名列表
     *
     * @param ids
     * @return
     */
    // List<String> getUserNamesByIds(Collection<String> ids);

    /**
     * 根据用户ID列表获取用户登录名列表
     *
     * @param ids
     * @return
     */
    List<String> getLoginNamesByIds(Collection<String> ids);

    /**
     * 根据用户登录名获取用户ID
     *
     * @param loginName
     * @return
     */
    String getIdByLoginName(String loginName);

    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    UserProfile getUserProfileByUserId(String userId);

    /**
     * 更新上次登录时间
     *
     * @param uuid
     * @param date
     */
    void updateLastLoginTime(String uuid);

    /**
     * 更改用户密码
     *
     * @param uuid
     * @param oldPassword
     * @param newPassword
     */
    String changePassword(String uuid, String oldPassword, String newPassword);

    /**
     * 根据用户ID，更改用户密码
     *
     * @param userId
     * @param oldPassword
     * @param newPassword
     */
    boolean changePasswordById(String userId, String oldPassword, String newPassword);

    /**
     * 根据用户ID，重置用户密码
     *
     * @param userId
     * @param newPassword
     * @return
     */
    boolean resetPasswordById(String userId, String newPassword);

    /**
     * 传递一个字符串参数，可以匹配出包含该字符串的所有用户的ID，作为数组返回，没有就返回空；
     *
     * @param rawName
     * @return
     */
    List<String> getUserIdsLikeName(String rawName);

    /**
     * 根据用户登录名查询用户
     *
     * @param loginName
     * @return
     */
    List<User> queryByLoginName(String loginName);

    /**
     * 将用户数据列表保存到用户表中
     *
     * @param list
     */
    HashMap<String, Object> saveUserFromList(List list);

    /**
     * 保存用户设置
     *
     * @param uuid     用户UUID
     * @param userBean 用户对象
     */
    void saveUserSet(String uuid, UserBean userBean) throws Exception;

    /**
     * 保存从集团中导入的人员
     *
     * @param commonUserNames
     * @param commonUserIds
     * @param enableTenantId
     */
    void saveCommonUsers(String commonUserNames, String commonUserIds);

    /**
     * @param user
     * @return
     */
    List<User> findByExample(User example);

    void createUser(User user);

    /**
     * 获取系统管理员用户
     *
     * @return
     */
    User getSystemAdmin();

    /**
     * 根据用户ID列表获取相应的手机号码
     *
     * @param userIds
     * @return
     */
    List<QueryItem> getMobilesByUserIds(List<String> userIds);

    /**
     * 根据用户ID列表获取相应的手机号码，用于发送短信
     *
     * @param userIds
     * @return
     */
    List<QueryItem> getMobilesForSmsByUserIds(List<String> userIds);

    /**
     * 判定用户ID是否属于所选的部门/群组ID，多个部门/群组用;分割
     *
     * @param userId
     * @param memberOf
     * @return
     */
    boolean isMemberOf(String userId, String memberOf);

    /**
     * 根据用户UUID获取联系人信息
     *
     * @param uuid
     * @return
     */
    String getContactNameByUserUuid(String uuid);

    /**
     * 根据hql分页获取用户数据
     *
     * @param hql
     * @param values
     * @param pagingInfo
     * @return
     */
    List<QueryItem> queryUserQueryItemData(String hql, Map<String, Object> values,
                                           PagingInfo pagingInfo);

    /**
     * 根据hql语句获取结果集总条数
     *
     * @param string
     * @param values
     * @return
     */
    Long findCountByHql(String string, Map<String, Object> values);

    /**
     * 加载权限树，选择权限
     *
     * @param uuid
     * @return
     */
    TreeNode getPrivilegeTree(String uuid);

    /**
     * 根据UUID加载权限树
     *
     * @param uuid
     * @return
     */
    public TreeNode getUserPrivilegeTree(String uuid);

    /**
     * 用户导入后更新部门负责人
     *
     * @param userRsMp
     */
    public void updateUserLeadersAfterImport(HashMap<String, Object> userRsMp);

    /**
     * 保存用户的职位、部门信息
     *
     * @param list
     */
    public HashMap<String, Object> saveUserJobRelactionFromList(List userList, List userJoblist);

    /**
     * 根据用户登录名获得主职位信息
     */
    public Job getMajorJobByUserLoginName(String loginName);

    /**
     * 根据用户登录名获得其他职位信息
     *
     * @return
     */
    public List<Job> getOtherJobsByUserLoginName(String loginName);

    /**
     * 根据用户Id获得主职位信息
     */
    public Job getMajorJobByUserId(String id);

    /**
     * 根据用户ID获得其他职位信息
     *
     * @return
     */
    public List<Job> getOtherJobsByUserId(String id);

    /**
     * 获得用户所属组织相关的角色
     *
     * @param user
     * @return
     */
    public Set<Role> getUserOrgRoles(User user);

    /**
     * 获得用户的直接领导(找到最近的
     * 先找用户职位汇报对象（暂不处理）
     * 再找用户职位的汇报对象（暂不处理）
     * 再找用户所在部门的部门负责人 ，找到就返回.
     * )
     *
     * @param userId
     * @return
     */
    public Set<String> getUserLeaderIds_new(String userId);

    /**
     * 根据用户ID获取用户所有领导id 遍历所有上级部门 取得部门负责人.
     *
     * @param userId
     * @return
     */
    Set<String> getAllUserLeaderIds_new(String userId);

    /**
     * 获得用户组织相关info
     *
     * @param userId
     * @return
     */
    public String getUserOrgInfo(String userId);

    /**
     * 获得用户组织相关IDS
     *
     * @param userId
     * @return
     */
    public List<String> getUserOrgIds(String userId);

    /**
     * 根据UUID获取指定人的直接下属
     *
     * @param uuid
     * @return
     */
    List<String> getSubordinate_new(String uuid);

    /**
     * 根据登录名获得用户email
     *
     * @param loginName
     * @return
     */
    public String getUserEmail(String loginName);

    /**
     * 根据用户名获得用户id
     *
     * @return
     */
    public List<User> getUsersByUserName(String userName);

    /**
     * 获得该用户直系部门所有成员id
     *
     * @param userId
     * @return
     */
    public Set<String> getDirectDepartmentUserIds(String userId);

    /**
     * 获得该用户所在的直系部门
     *
     * @param userId
     * @return
     */
    public Set<Department> getDirectDepartments(String userId);

    /**
     * 根据用户登录名获取用户(忽略大小写)
     * 租户ID
     *
     * @param loginName
     * @return
     */
    User getByLoginNameIgnoreCase(String loginName, String tenantId);

    /**
     * 获得用户AD路径
     *
     * @param loginName
     */
    public String getUserDnByLoginName(String loginName);

    /**
     * liyb 2014.12.24
     * 获取用户部门负责人
     *
     * @param userId
     * @return
     */
    public Set<String> getDirectLeaderIds(String userId);

    /**
     * liyb 2014.12.24
     * 获取职位的汇报对象
     *
     * @param userId
     * @return
     */
    public Set<String> getReportLeaderIds(String userId);

    /**
     * xiem 2015.05.05
     * 获取职位的汇报对象
     *
     * @param userId
     * @return
     */
    public Set<String> getReportLeaderIds(User user);

    /**
     * @Title: saveSmallPhoto
     * @Description: 将原始图片缩小成60*60的图片保存
     * @param photoUUID 原图的uuid
     * @throws IOException
     */
    // public MongoFileEntity saveSmallPhoto(String photoUUID) throws
    // IOException;

    /**
     * 通过员工编号获取用户
     *
     * @param employeeNumber
     * @return
     */
    public User getUserByEmployeeNumber(String employeeNumber);

    /* add by huanglinchuan 2015.1.15 begin */

    /**
     * 根据用户获取其所有组织相关的ID
     *
     * @param userId
     * @return
     */
    public List<String> getAllRelatedGroupOrgs(String userId);

    /* add by huanglinchuan 2015.1.15 end */

    /* add by huanglinchuan 2015.3.22 begin */

    /**
     * 根据关键字搜索用户，可检索姓名/登录名/英文名/拼音简称
     *
     * @param keyWords
     * @param p
     * @return
     */
    public List<QueryItem> queryUserWithKeyWords(String keyWords, PagingInfo p);

    /* add by huanglinchuan 2015.3.22 end */

    /**
     * 判断用户是否为领导（同一部门内）
     * 规则 同一级部门内 职级必须大于前用户
     *
     * @param leader
     * @param user
     * @param department
     * @return
     */
    public boolean compareUserIsLeaderInTheSameDept(User leader, User user, Department department);

    /**
     * 获取自己能看到人员
     *
     * @param userId
     * @return
     */
    public Set<String> getUserIds(String userId);

    /**
     * 作者： yuyq
     * 创建时间：2015-3-26 下午4:15:10
     * 类描述：TODO
     * 备注：设置用户初始化密码为0
     *
     * @return
     */
    public void initPassword();

    /**
     * 根据用户ID获取用户部门领导id
     *
     * @param userId
     * @return
     */
    public Set<String> getUserDepartmentLeaderIds(String userId);

    /**
     * 根据用户获取用户部门领导id
     *
     * @param userId
     * @return
     */
    public Set<String> getUserDepartmentLeaderIds(User user);

    /**
     * 根据用户ID获取用户部门领导id(包含所有上级部门的部门领导)
     *
     * @param userId
     * @return
     */
    public Set<String> getUserAllDepartmentLeaderIds(String userId);

    /**
     * 根据用户获取用户部门领导id(包含所有上级部门的部门领导)
     *
     * @param userId
     * @return
     */
    public Set<String> getUserAllDepartmentLeaderIds(User user);

    /**
     * 根据用户ID获取用户部门领导id(人员为部门负责人，包含本身和同是负责人的人)
     *
     * @param userId
     * @return
     */
    public Set<String> getUserDepartmentLeaderIdsHasSelf(String userId);

    /**
     * 根据用户获取用户部门领导id(包含所有上级部门的部门领导,如果人员为部门负责人，包含本身和同是负责人的人)
     *
     * @param userId
     * @return
     */
    public Set<String> getUserAllDepartmentLeaderIdsHasSelf(String userId);

    /**
     * 根据部门uuid获取部门人员（不包含子部门）
     *
     * @param departmentUuid
     * @return
     */
    public List<String> getUserIdsByDepartment(String departmentUuid);

    /**
     * 获取用户的分管领导
     *
     * @param userId
     * @return
     */
    public Set<String> getUserDepartmentBranchedLeaders(String userId);

    /**
     * 保存用户
     *
     * @param user
     */
    public void save(User user);

    /**
     * 2015-06-19  yuyq
     * 如何描述该方法
     * 获取职位角色
     *
     * @param department
     * @return
     */
    public Set<Role> getRole(String userUuid);

    /**
     * 2015-06-19  yuyq
     * 如何描述该方法
     * 获取职位权限
     *
     * @param department
     * @return
     */
    public Set<Privilege> getPrivilege(String userUuid);

    /**
     * 2015-06-19  yuyq
     * 如何描述该方法
     * 获取用户群组
     *
     * @param department
     * @return
     */
    public String getGroups(String userUuid);

    /**
     * 根据用户ID获取用户所在群组的ID列表
     *
     * @param userId
     * @return
     */
    List<String> getGroupIdsByUserId(String userId);

    /**
     * 2015-06-19  yuyq
     * 如何描述该方法
     * 获取用户上级领导
     *
     * @param department
     * @return
     */
    public String getLeaderIds(String userUuid);

    /**
     * 过滤出属于组织ID为orgId下的用户
     *
     * @param userIds
     * @param orgId
     * @return
     */
    List<String> filterUserIdsByOrgId(Collection<String> userIds, String orgId);

    /**
     * 过滤出属于组织ID为orgId下的(0 负责人,1分管领导,2管理员)用户
     *
     * @param userIds
     * @param orgId
     * @return
     */
    List<String> filterDepartmentPrincipalsByOrgId(Collection<String> userIds, String orgId);

    /**
     * 根据用户ID，获取用户所在的组织，以所在部门为依据
     *
     * @param userId
     * @return
     */
    List<Organization> getUserOrganizationsByUserId(String userId);

    /**
     * 根据用户ID及组织机构ID获取用户所属的部门ID
     *
     * @param userId
     * @param orgId
     * @return
     */
    List<String> getDepartmentIdsByUserId(String userId, String orgId);

    /**
     * 根据用户ID及组织机构ID获取用户上级的部门ID
     *
     * @param userId
     * @param orgId
     * @return
     */
    List<String> getParentDepartmentIdsByUserId(String userId, String orgId);

    /**
     * 根据用户ID及组织机构ID获取用户根部门的部门ID
     *
     * @param userId
     * @param orgId
     * @return
     */
    List<String> getRootDepartmentIdsByUserId(String userId, String orgId);

    /**
     * 根据用户ID及组织机构ID获得该用户直系部门所有成员id
     *
     * @param userId
     * @param orgId
     * @return
     */
    Set<String> getDirectDepartmentUserIds(String userId, String orgId);

    void resetUserPassword(String loginName, String tenantId);

    /**
     * 根据租户ID获取用户
     */
    List<User> getByTenantId(String tenantId);

    /**
     * 根据权限获取用户
     *
     * @param roleUuid
     * @return
     */
    Set<User> getUserByRoleUuid(String roleUuid);

    void updateMajorName(String majorJobName, String uuid);

    void updateUserDeptName(String deptPathName, String uuid);

    void updateOtherName(String otherJobName, String uuid);

    List<User> getUserByName(String userName);

    List<User> findBy(String string, String userCode);

    Query createSQLQuery(String createSearchSql);

    /**
     * 设置当前用户的配置数据
     *
     * @param propertyName
     * @param propertyValue
     */
    void saveUserProperty(String propertyName, String propertyValue);

    /**
     * 获取当前用户的配置数据
     *
     * @param propertyName
     * @return
     */
    String getUserProperty(String propertyName);

    /**
     * 设置当前用户的配置数据
     *
     * @param propertyName
     * @param propertyClobValue
     */
    void saveUserClobProperty(String propertyName, String propertyClobValue);

    /**
     * 获取当前用户的配置数据
     *
     * @param propertyName
     * @return
     */
    String getUserClobProperty(String propertyName);

}
