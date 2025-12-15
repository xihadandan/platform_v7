/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.pt.org.entity.DepartmentPrincipal;
import com.wellsoft.pt.org.entity.DepartmentUserJob;

import java.util.List;

public interface DepartmentUserJobService {
    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteByUser(String uuid);

    /**
     * @param uuid
     * @return
     */
    public List<DepartmentUserJob> getByDepartment(String uuid);

    public DepartmentUserJob getMajor(String userUuid);

    public DepartmentPrincipal get(String uuid);

    public List<DepartmentUserJob> getAll();

}
