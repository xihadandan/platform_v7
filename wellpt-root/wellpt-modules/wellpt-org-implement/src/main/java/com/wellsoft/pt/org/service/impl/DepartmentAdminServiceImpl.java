/*
 * @(#)2012-12-25 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service.impl;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.org.entity.Department;
import com.wellsoft.pt.org.entity.DepartmentPrincipal;
import com.wellsoft.pt.org.entity.DepartmentUserJob;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.org.service.DepartmentAdminService;
import com.wellsoft.pt.org.service.DepartmentPrincipalService;
import com.wellsoft.pt.org.service.DepartmentService;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Description: 部门管理员服务层接口实现类
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
@Service
@Transactional
public class DepartmentAdminServiceImpl extends BaseServiceImpl implements DepartmentAdminService {
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private UserService userService;
    @Autowired
    private DepartmentPrincipalService departmentPrincipalService;
    @Autowired
    private SecurityApiFacade securityApiFacade;

    /**
     * 根据用户id判断是否为部门管理员
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DepartmentService#isDepartmentAdmin(java.lang.String)
     */
    @Override
    public boolean isDepartmentAdmin(String userId) {
        User user = userService.getById(userId);
        // Set<Role> userRoles =
        // securityApiFacade.getRolesByUserUuid(user.getUuid());
        // String userUuid = user.getUuid();
        // List<DepartmentPrincipal> departmentPrincipalList =
        // departmentPrincipalService.getAll();
        // if (departmentPrincipalList.size() > 0) {
        // for (DepartmentPrincipal departmentPrincipal :
        // departmentPrincipalList) {
        // // 若当前登录人存在DepartmentPrincipal中且为部门管理员，则进入部门管理员界面
        // if (departmentPrincipal.getUser().getUuid().equals(userUuid) &&
        // departmentPrincipal.getIsManager()) {
        // return true;
        // }
        // }
        // }

        boolean deptadmin = false;
        // for (Role role : userRoles) {
        // if ("ROLE_DEPARTMENT_ADMIN".equals(role.getId())) {
        // deptadmin = true;
        // }
        // if ("ROLE_ADMIN".equals(role.getId())) {
        // return false; // 平台管理员优先级高
        // }
        // }
        return deptadmin;
    }

    /**
     * 根据用户id和部门id判断是否为该部门管理员
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DepartmentAdminService#isCurrentDepartmentAdmin(java.lang.String, java.lang.String)
     */
    @Override
    public Boolean isCurrentDepartmentAdmin(String userId, String deptId) {
        User user = userService.getById(userId);
        Department department = departmentService.getById(deptId);
        String managerNames = department.getManagerNames();// 部门管理员
        if (!(StringUtils.isBlank(managerNames)) && managerNames.contains(user.getUserName())) {
            return true;
        }
        return false;
    }

    /**
     * 根据用户id获得该用户作为部门管理员的所有部门UUID集合
     *
     * @param userId
     * @return
     */
    @Override
    public List<String> getDepartmentUuidListByUserId(String userId) {
        User user = userService.getById(userId);
        String userUuid = user.getUuid();
        List<String> departmentUuidList = new ArrayList<String>();// 部门管理员所属部门uuid集合
        List<DepartmentPrincipal> departmentPrincipalList = departmentPrincipalService.getAll();
        if (departmentPrincipalList.size() > 0) {
            for (DepartmentPrincipal departmentPrincipal : departmentPrincipalList) {
                if (departmentPrincipal.getUser().getUuid().equals(userUuid) && departmentPrincipal.getIsManager()) {
                    departmentUuidList.add(departmentPrincipal.getDepartment().getUuid());
                }
            }

        }
        if (departmentUuidList.size() > 0) {
            return departmentUuidList;
        }

        // 不是部门管理员，返回用户所在部门
        List<String> departmentUuids = new ArrayList<String>();
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        if (StringUtils.isNotBlank(userDetails.getMainDepartmentId())) {
            departmentUuids.add(departmentService.getById(userDetails.getMainDepartmentId()).getUuid());
        }
        return departmentUuids;
    }

    /**
     * 判断当前节点的部门UUID是否为该用户作为部门管理员的所有部门UUID集合或其子部门集合
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DepartmentAdminService#isAdminDepartmentUuidList(java.lang.String)
     */
    @Override
    public boolean isAdminDepartmentUuidList(String departmentUuid) {
        Department department = departmentService.get(departmentUuid);
        String currentUseId = SpringSecurityUtils.getCurrentUserId();
        List<String> departmentUuidList = getDepartmentUuidListByUserId(currentUseId);
        for (String deptUuid : departmentUuidList) {
            Department currentDepartment = departmentService.get(deptUuid);
            if (department.getPath().contains(currentDepartment.getPath())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据用户id获取对应的主部门
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DepartmentAdminService#getMajorDepartmentByUserId(java.lang.String)
     */
    @Override
    public Department getMajorDepartmentByUserId(String userId) {
        User user = userService.getById(userId);
        Set<DepartmentUserJob> departmentUsers = user.getDepartmentUsers();
        Department majorDepartment = new Department();
        for (DepartmentUserJob departmentUserJob : departmentUsers) {
            // 只取主部门
            if (Boolean.TRUE.equals(departmentUserJob.getIsMajor())) {
                majorDepartment = departmentUserJob.getDepartment();
            }
        }
        return majorDepartment;
    }

    @Override
    public List<TreeNode> getDepartmentNestedDepartmentTree(String uuid) {
        List<TreeNode> rootTreeNodeList = new ArrayList<TreeNode>();
        if (uuid != null && !uuid.equals("")) {
            // 根据uuid获得当前部门
            Department department = departmentService.get(uuid);
            TreeNode root = new TreeNode();
            root.setName(department.getName());
            root.setId(department.getUuid());
            Department data = new Department();
            BeanUtils.copyProperties(department, data);
            root.setData(data);
            rootTreeNodeList.add(root);
            this.getDepartChildren(department, root);
        } else {
            // 获得顶级部门
            List<Department> departmentList = this.departmentService.getTopDepartment();

            for (Department department : departmentList) {
                TreeNode root = new TreeNode();
                root.setId(department.getUuid());
                root.setName(department.getName());
                Department data = new Department();
                BeanUtils.copyProperties(department, data);
                root.setData(data);
                rootTreeNodeList.add(root);
                this.getDepartChildren(department, root);
            }
        }
        return rootTreeNodeList;
    }

    public void getDepartChildren(Department department, TreeNode currNode) {
        String currentUseId = SpringSecurityUtils.getCurrentUserId();
        List<String> departmentUuidList = getDepartmentUuidListByUserId(currentUseId);
        List<Department> childrenDepts = department.getChildren();
        if (!childrenDepts.isEmpty()) {
            List<TreeNode> children = new ArrayList<TreeNode>();
            for (Department dept : childrenDepts) {
                Department firstDepartment = departmentService.get(departmentUuidList.get(0));
                if (firstDepartment.getPath().contains(dept.getPath())
                        && !(firstDepartment.getPath().equals(dept.getPath()))) {//
                    setChildTreeNode(children, dept);
                } else {// 当前部门uuid是否属于所有设置部门管理员的部门集合中
                    for (String departmentUuid : departmentUuidList) {
                        Department currentDepartment = departmentService.get(departmentUuid);// 当前节点部门
                        if (dept.getPath().contains(currentDepartment.getPath())) {
                            setChildTreeNode(children, dept);
                        }
                    }
                }
            }
            Set<String> newChildrenIdSet = new HashSet<String>();
            List<TreeNode> newChildren = new ArrayList<TreeNode>();
            for (TreeNode treeNode : children) {
                // 去除重复的节点
                if (newChildrenIdSet.add(treeNode.getId())) {
                    newChildren.add(treeNode);
                }
            }
            currNode.setChildren(newChildren);
        }
    }

    /**
     * 设置部门管理员左导航树节点
     */
    public void setChildTreeNode(List<TreeNode> children, Department dept) {
        TreeNode child = new TreeNode();
        child.setId(dept.getUuid());
        child.setName(dept.getName());
        Department data = new Department();
        BeanUtils.copyProperties(dept, data);
        child.setData(data);
        children.add(child);
        this.getDepartChildren(dept, child);
    }

}
