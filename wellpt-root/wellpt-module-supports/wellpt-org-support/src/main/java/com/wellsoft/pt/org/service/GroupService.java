/*
 * @(#)2012-10-23 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.org.bean.GroupBean;
import com.wellsoft.pt.org.entity.Department;
import com.wellsoft.pt.org.entity.Group;
import com.wellsoft.pt.org.entity.GroupMember;
import com.wellsoft.pt.org.entity.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: GroupService.java
 *
 * @author zhulh
 * @date 2013-1-14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-14.1	zhulh		2013-1-14		Create
 * </pre>
 */
public interface GroupService {

    /**
     * 根据ID获取群组
     *
     * @param id
     * @return
     */
    // Group getById(String id);

    /**
     * Description how to use this method
     *
     * @param id
     * @return
     */
    GroupBean getBeanById(String id);

    /**
     * 通过uuid找到对应的数据
     *
     * @param uuid
     * @return
     */
    GroupBean getBean(String uuid);

    /**
     * Description how to use this method
     *
     * @param bean
     */
    void saveBean(GroupBean bean);

    /**
     * 根据群组ID删除群组
     *
     * @param id
     */
    void removeById(String id);

    /**
     * 根据群组ID，批量删除群组
     *
     * @param ids
     */
    void removeAllById(Collection<String> ids);

    /**
     * Description how to use this method
     *
     * @param excludeUuid
     * @return
     */
    TreeNode getAsTree(String excludeUuid);

    /**
     * 根据群组UUID加载角色树，自动选择已有角色
     *
     * @param uuid
     * @return
     */
    TreeNode getRoleTree(String uuid);

    /**
     * 根据UUID加载群组角色嵌套树, 包含角色嵌套及权限
     *
     * @param uuid
     * @return
     */
    TreeNode getGroupRoleNestedRoleTree(String uuid);

    /**
     * 如何描述该方法
     *
     * @param id
     * @return
     */
    String getFullPath(String id);

    /**
     * 根据群组id，获取该群组下的所有叶子结点群组
     *
     * @param id
     * @return
     */
    List<Group> getLeafGroups(String id);

    /**
     * 通过群组的ID获取该群组下的所有用户ID
     *
     * @param groupId
     * @return
     */
    List<String> getAllUserIdsByGroupId(String id);

    /**
     * 获取jqgrid树形结点数据
     *
     * @param jqGridQueryInfo
     * @param queryInfo
     * @return
     */
    QueryData getForPageAsTree(JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo);

    /**
     * 获取个人群组jqgrid树形结点数据
     *
     * @param jqGridQueryInfo
     * @param queryInfo
     * @return
     */
    QueryData getForPageAsSelfTree(JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo);

    /**
     * 加载权限树，选择权限
     *
     * @param uuid
     * @return
     */
    TreeNode getPrivilegeTree(String uuid);

    /**
     * 根据UUID加载部门权限嵌套树
     *
     * @param uuid
     * @return
     */
    public TreeNode getGroupPrivilegeTree(String uuid);

    /**
     * 通过群组的ID获取该群组下的所有用户ID
     *
     * @param groupId
     * @return
     */
    List<User> getAllUsersByGroupId(String id);

    /**
     * 通过群组的ID获取该群组下的所有用户ID
     *
     * @param groupId
     * @return
     */
    List<Department> getAllDepartmentByGroupId(String id);

    /**
     *
     * 根据分类获得群组
     *
     * @param category
     * @return
     */
    // List<Group> getByCategory(String category);

    /**
     * 获得群组下面所有成员ID(部门、职位、职务、人员)
     *
     * @param id
     * @return
     */
    public Set<String> getAllMemberIdsByGroupId(String id);

    /**
     * 获得群组下面所有成员ID(部门、职位、职务、人员)
     *
     * @param uuid
     * @return
     */
    List<Map<String, Object>> getGroupMembers(String uuid);

    /**
     * @return
     * @Title: getAll
     * @Description: 获取全部群组
     */
    public List<Group> getAll(int i);

    /**
     * @param group_uuid
     * @return
     * @author yuyq 2014-12-19
     * 通过群组的uuid获取第一个加入群的用户
     */
    public GroupMember getFirstGroupUser(String group_uuid);

    //
    // /**
    // * @author yuyq 2014-12-22
    // * 通过uuid（主键）获取群组
    // * @param uuid
    // * @return
    // */
    // Group getByUuid(String uuid);

    /**
     * @param bean
     * @author yuyq    2014-12-22
     * 保存群组信息
     */
    void save(Group group);

    /**
     * @param group
     * @author Lmw 2015-3-26
     * 修改,该功能只为手机api提供调用
     */
    void modifyApplyToApi(Group group);

    List<Group> getAll();

    Set<Group> getGroupByRoleUuid(String roleUuid);

    public void delete(String uuid);

    public List<Group> querySelf();

    public List<Group> queryPublic();

    public List<Group> getTopLevel();

    public Group getById(String id);

    public List<Group> getByCategory(String category);

    public Group getByUuid(String uuid);

    List<Group> namedQuery(String string, Map<String, Object> values, Class<Group> class1, PagingInfo pagingInfo);

    List<Group> findBy(String string, String filterCondition);

}
