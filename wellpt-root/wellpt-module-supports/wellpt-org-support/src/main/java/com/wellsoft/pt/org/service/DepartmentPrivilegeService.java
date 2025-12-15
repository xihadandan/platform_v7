/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.pt.org.entity.DepartmentPrivilege;

import java.util.List;

/**
 * Description:部门权限
 *
 * </pre>
 */
public interface DepartmentPrivilegeService {
    public List<DepartmentPrivilege> getDepartmentPrivilegeByDepartmentUuid(String departmentUuid);

    public List<DepartmentPrivilege> getDepartmentPrivilegeByPrivilegeUuid(String privilegeUuid);

    public void save(DepartmentPrivilege departmentPrivilege);
}
