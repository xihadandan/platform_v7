/*
 * @(#)2012-12-25 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service.impl;

import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.dao.DepartmentEmployeeJobDao;
import com.wellsoft.pt.org.entity.DepartmentEmployeeJob;
import com.wellsoft.pt.org.service.DepartmentEmployeeJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Description: DepartmentServiceImpl.java
 *
 * @author zhulh
 * @date 2012-12-23
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-12-23.1	zhulh		2012-12-23		Create
 * 2013-9-25    liuzq       2013-9-25       Update
 * </pre>
 */
@Service
@Transactional
public class DepartmentEmployeeJobServiceImpl extends BaseServiceImpl implements DepartmentEmployeeJobService {
    @Autowired
    DepartmentEmployeeJobDao departmentEmployeeJobDao;

    public DepartmentEmployeeJob getMajor(String employeeUuid) {
        return departmentEmployeeJobDao.getMajor(employeeUuid);
    }

    /**
     * @param uuid
     * @return
     */
    public List<DepartmentEmployeeJob> getByDepartment(String uuid) {
        return departmentEmployeeJobDao.getByDepartment(uuid);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteByEmployee(String uuid) {
        this.departmentEmployeeJobDao.deleteByEmployee(uuid);
        ;
    }
}
