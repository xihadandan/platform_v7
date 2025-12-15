/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.org.bean.DepartmentBean;
import com.wellsoft.pt.org.entity.*;

import java.util.*;

/**
 * Description: DepartmentService.java
 *
 * @author zhulh
 * @date 2012-12-23
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-12-23.1	zhulh		2012-12-23		Create
 * 2013-9-25    liuzq       2013-9-25       Update
 * </pre>
 */
public interface DepartmentService extends BaseService {
    Department get(String uuid);

    void save(Department department);

    void remove(String uuid);

    /**
     * @param excludeUuid
     * @return
     */
    TreeNode getAsTree(String excludeUuid);

    /**
     * @param uuid
     * @return
     */
    DepartmentBean getBean(String uuid);

    /**
     * Description how to use this method
     *
     * @param newDepartment
     */
    void saveBean(DepartmentBean newDepartment);

    /**
     * @param queryInfo
     * @return
     */
    QueryData getForPageAsTree(JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo);

    /**
     * @param id
     * @return
     */
    DepartmentBean getBeanById(String id);

    /**
     * 如何描述该方法
     *
     * @param id
     */
    void removeById(String id);

    /**
     * @return
     */
    List<Department> getTopLevel();

    /**
     * @param id
     * @return
     */
    String getFullPath(String id);

    /**
     * 如何描述该方法
     *
     * @param id
     * @return
     */
    Department getById(String id);

    /**
     * 根据部门id，获取该部门下的所有叶子结点部门
     *
     * @param id
     * @return
     */
    List<Department> getLeafDepartments(String id);

    /**
     * 根据部门UUID获取该部门的最顶级部门
     *
     * @param uuid
     * @return
     */
    Department getTopDepartment(String uuid);

    /**
     * 根据部门UUID加载角色树，自动选择已有角色
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
    public TreeNode getDepartmentRoleNestedRoleTree(String uuid);

    /**
     * 根据部门ID获取部门下的所有用户ID(包括子部门)
     *
     * @param departmentId
     * @return
     */
    List<String> getAllUserIdsByDepartmentId(String departmentId);

    /**
     * 根据部门ID获取部门下的所有用户ID(包括子部门)
     *
     * @param departmentId
     * @param includeDisable
     * @return
     */
    List<String> getAllUserIdsByDepartmentId(String departmentId, boolean includeDisable);

    /**
     * 根据部门ID获取部门下的用户ID(不包括子部门)
     *
     * @param departmentId
     * @return
     */
    List<String> getUserIdsByDepartmentId(String departmentId);

    /**
     * 如何描述该方法
     *
     * @param departmentIds
     * @return
     */
    List<Department> getByIds(Collection<String> departmentIds);

    /**
     * 如何描述该方法
     *
     * @param rawName
     * @return
     */
    List<String> getDeptmentIdsLikeName(String rawName);

    /**
     * 根据UUID加载部门嵌套树
     *
     * @param uuid
     * @return
     */
    List<TreeNode> getDepartmentNestedDepartmentTree(String uuid);

    /**
     * 根据用户ID获取用户所在的部门信息
     *
     * @param userId
     * @return
     */
    List<QueryItem> queryDepartmentsByUserId(String userId);

    /**
     * 加载权限树，选择权限
     *
     * @param uuid
     * @return
     */
    TreeNode getPrivilegeTree(String uuid);

    /**
     * 根据UUID加载部门权限树
     *
     * @param uuid
     * @return
     */
    public TreeNode getDepartmentPrivilegeTree(String uuid);

    /**
     * 将部门数据列表保存到部门表中(Excel导入)
     *
     * @param list
     */
    HashMap<String, Object> saveDepartmentFromList(List list);

    /**
     * 更新部门负责人(在数据导入后)
     *
     * @param deptRsMp
     */
    public void updateDeptPrincipalAfterImport(HashMap<String, Object> deptRsMp);

    /**
     * 根据部门ID获取部门下的用户(不包括子部门)
     *
     * @param departmentId
     * @return
     */
    List<User> getUsersByDepartmentId(String departmentId);

    /**
     * 根据部门ID获取部门下的所有用户(包括子部门)
     *
     * @param departmentId
     * @return
     */
    List<User> getAllUsersByDepartmentId(String departmentId);

    /**
     * @return
     * @Title: getAll
     * @Description: 获取全部部门
     */
    public List<Department> getAll();

    /**
     * @param deptUuid
     * @return
     * @author liyb  2015.01.10
     * 根据部门uuid，获取父部门（上一级的）
     */
    public Department getParentDepartment(String deptUuid);

    /**
     * 获取某个部门的所有子部门UUID
     *
     * @param departmentUuid
     * @return
     */
    public List<String> getAllChildrenDeptUuids(String departmentUuid);

    /**
     * 根据部门UUID获取部门下的所有子部门（包括本部门）
     *
     * @param uuid
     * @return
     */
    public Set<String> getAllChildDeptUuidByUuid(String uuid);

    /**
     * 根据单位ID获取挂接的部门
     *
     * @param unitId
     * @return
     */
    public List<DepartmentBean> getListDepartmentByUnitId(String unitId);

    /**
     * 部门挂接
     *
     * @param unitId  单位ID
     * @param depJson [{ID:部门ID,isVisible:是否显示},{ID:部门ID,isVisible:是否显示}]
     */
    void saveOrUpdateCommonDepartment(String unitId, String depJson);

    Set<DepartmentUserJob> getDepartmentJobUserByDepartment(Department department);

    public User getUserByDepartmentJobUser(DepartmentUserJob departmentUserJob);

    public List<Department> getChildren(Department department);

    Set<Department> getDepartmentByRoleUuid(String roleUuid);

    List<Department> getTopDepartment();

    void traverseAndAddChildrenIds(String departmentId, Collection<String> childrenIds);

    List<Department> getChildrenById(String id);

    /**
     * 只支持hql
     * 如何描述该方法
     *
     * @param string
     * @param values
     * @param class1
     * @param pagingInfo
     * @return
     */
    List<Department> namedQuery(String string, Map<String, Object> values, Class<Department> class1,
                                PagingInfo pagingInfo);

    List<Job> getAllJobByDepartmentPath(String path);

    Collection<? extends Department> getChildren(String id);

    /**
     * 只支持hql
     * 如何描述该方法
     *
     * @param string
     * @param values
     * @param class1
     * @return
     */
    List<Department> namedQuery(String string, Map<String, Object> values, Class<Department> class1);

    Collection<? extends Department> getAllChildren(String id);

    List<User> getAllUserByDepartmentPath(String path);

    /**
     * 根据单位ID获取所有用户
     *
     * @param id
     * @return
     */
    List<User> getAllUserByUnitId(String id);

    /**
     * 根据部门uuid,取得部门的所有领导人  createtime 2016-3-11
     *
     * @param deptUuid
     * @return
     * @author hongjz
     */
    public List<DeptPrincipal> getAllPrincipals(String deptUuid);

    /**
     * 获取部门管理员,如果本部门没有则向上追溯到其父部门
     *
     * @param deptUuid
     * @return
     */
    public String getManagerWithRecursionByDeptUuid(String deptUuid);

    /**
     * 获取部门管理员,如果本部门没有则向上追溯到其父部门
     *
     * @param deptId
     * @return
     */
    public String getManagerWithRecursionByDeptId(String deptId);

}
