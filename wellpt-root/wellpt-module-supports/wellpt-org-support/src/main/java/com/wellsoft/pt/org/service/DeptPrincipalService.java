/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.pt.org.entity.DeptPrincipal;

import java.util.List;

public interface DeptPrincipalService {

    /**
     * 根据部门UUID，获取部门管理员
     *
     * @param uuid
     */
    public List<DeptPrincipal> getManager(String deptUuid);

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
    public List<DeptPrincipal> getBranched(String deptUuid);

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
    public List<DeptPrincipal> getPrincipal(String deptUuid);

    /**
     * 根据部门UUID，删除部门负责人
     *
     * @param uuid
     */
    public void deletePrincipal(String deptUuid);

    /**
     * 根据组织ID获得对应的部门
     */
    public List<DeptPrincipal> getPrincipalByOrgId(String userId, String jobId);
}
