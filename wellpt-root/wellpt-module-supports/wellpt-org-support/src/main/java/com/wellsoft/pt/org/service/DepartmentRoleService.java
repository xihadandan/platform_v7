/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.pt.org.entity.DepartmentRole;

import java.util.List;

/**
 * Description:部门角色
 *
 * </pre>
 */
public interface DepartmentRoleService {
    /**
     * 根据部门uuid获取权限中间表
     *
     * @param departmentUuid 部门uuid
     * @return
     */
    public List<DepartmentRole> getDepartmentRoleByDepartmentUuid(String departmentUuid);

    /**
     * 根据权限uuid获取对应的角色中间表
     *
     * @param roleUuid 角色uuid
     * @return
     */
    public List<DepartmentRole> getDepartmentRoleByRoleUuid(String roleUuid);

    /**
     * 根据部门UUID和角色UUID删除对应的中间表数据
     *
     * @param departmentUuid 部门uuid
     * @param roleUuid       角色uuid
     */
    public void deleteDepartmentRoleByDepartmentUuidAndRoleUuid(String departmentUuid, String roleUuid);


    public void save(DepartmentRole departmentRole);
}
