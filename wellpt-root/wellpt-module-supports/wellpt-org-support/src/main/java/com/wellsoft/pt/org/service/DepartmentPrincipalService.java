/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.pt.org.entity.DepartmentPrincipal;

import java.util.List;

public interface DepartmentPrincipalService {
    /**
     * 根据部门UUID，获取部门管理员
     *
     * @param uuid
     */
    public List<DepartmentPrincipal> getManager(String deptUuid);

    /**
     * 根据部门UUID，删除部门管理员
     *
     * @param uuid
     */
    public void deleteManager(String deptUuid);

    /**
     * 根据部门UUID，获取部门分管领导
     *
     * @param uuid
     */
    public List<DepartmentPrincipal> getBranched(String deptUuid);

    /**
     * 根据部门UUID，删除部门分管领导
     *
     * @param uuid
     */
    public void deleteBranched(String deptUuid);

    /**
     * 根据部门UUID，获取部门负责人
     *
     * @param uuid
     */
    public List<DepartmentPrincipal> getPrincipal(String deptUuid);

    /**
     * 根据部门UUID，删除部门负责人
     *
     * @param uuid
     */
    public void deletePrincipal(String deptUuid);

    /**
     * @param userUuid
     */
    public void deleteByUserUuid(String userUuid);

    /**
     * 删除DeptPrincipal（部门负责人）中的用户
     *
     * @param userId
     */
    public void deleteByUserId(String userId);

    /**
     * 获取所有
     *
     * @return
     */
    public List<DepartmentPrincipal> getAll();
}
