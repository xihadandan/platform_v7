/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.pt.org.entity.DepartmentEmployeeJob;

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
public interface DepartmentEmployeeJobService {
    public DepartmentEmployeeJob getMajor(String employeeUuid);

    public List<DepartmentEmployeeJob> getByDepartment(String uuid);

    public void deleteByEmployee(String uuid);
}
