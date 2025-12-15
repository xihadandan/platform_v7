/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.pt.org.entity.DepartmentFunction;

import java.util.List;

public interface DepartmentFunctionService {
    public List<DepartmentFunction> getFunctions(String uuid);

    public void deleteFunction(String uuid);

    public void deleteByDepartment(String uuid);

    public List<DepartmentFunction> getByDepartment(String uuid);
}
