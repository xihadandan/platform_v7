/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.org.entity.Department;

import java.util.List;

/**
 * Description: 部门管理员服务层接口
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-1-5.1	zhouyq		2014-1-5		Create
 * </pre>
 * @date 2014-1-5
 */
public interface DepartmentAdminService {
    /**
     * 根据用户id判断是否为部门管理员
     *
     * @param userId
     * @return
     */
    boolean isDepartmentAdmin(String userId);

    /**
     * 根据用户id和部门id判断是否为该部门管理员
     *
     * @param userId
     * @return
     */
    Boolean isCurrentDepartmentAdmin(String userId, String deptId);

    /**
     * 根据用户id获得该用户作为部门管理员的所有部门UUID集合
     *
     * @param userId
     * @return
     */
    List<String> getDepartmentUuidListByUserId(String userId);

    /**
     * 判断当前节点的部门UUID是否为该用户作为部门管理员的所有部门UUID集合或其子部门集合
     *
     * @param departmentUuid
     * @return
     */
    boolean isAdminDepartmentUuidList(String departmentUuid);

    /**
     * 根据用户id获取对应的主部门
     *
     * @param currentUserId
     * @return
     */
    Department getMajorDepartmentByUserId(String currentUserId);

    /**
     * 根据UUID加载部门嵌套树
     *
     * @param uuid
     * @return
     */
    List<TreeNode> getDepartmentNestedDepartmentTree(String uuid);

}
