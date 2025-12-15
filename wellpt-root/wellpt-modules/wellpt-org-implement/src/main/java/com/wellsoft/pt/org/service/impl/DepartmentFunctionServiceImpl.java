/*
 * @(#)2012-12-25 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service.impl;

import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.dao.DepartmentFunctionDao;
import com.wellsoft.pt.org.entity.DepartmentFunction;
import com.wellsoft.pt.org.service.DepartmentFunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DepartmentFunctionServiceImpl extends BaseServiceImpl implements DepartmentFunctionService {
    @Autowired
    DepartmentFunctionDao departmentFunctionDao;

    /**
     * @param uuid
     * @return
     */
    public List<DepartmentFunction> getByDepartment(String uuid) {
        return departmentFunctionDao.getByDepartment(uuid);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteByDepartment(String uuid) {
        this.departmentFunctionDao.deleteByDepartment(uuid);
        ;
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteFunction(String uuid) {
        this.departmentFunctionDao.deleteFunction(uuid);
    }

    /**
     * @param uuid
     * @return
     */
    public List<DepartmentFunction> getFunctions(String uuid) {
        return departmentFunctionDao.getFunctions(uuid);
    }

}
