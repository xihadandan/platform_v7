/*
 * @(#)2013-1-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.DepartmentEmployeeJob;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-9-24.1  zhengky	2014-9-24	  Create
 * </pre>
 * @date 2014-9-24
 */
@Repository
public class DepartmentEmployeeJobDao extends OrgHibernateDao<DepartmentEmployeeJob, String> {
    private static final String QUERY_MAJOR_BY_EMPLOYEE_UUID = "from DepartmentEmployeeJob dept_employee_job where dept_employee_job.isMajor = true and dept_employee_job.employee.uuid = :employeeUuid";
    private static final String QUERY_BY_DEPARTMENT_UUID = "from DepartmentEmployeeJob dept_employee_job where dept_employee_job.department.uuid = :departmentUuid order by dept_employee_job.employee.code asc";
    private static final String DELETE_BY_EMPLOYEE_UUID = "delete from DepartmentEmployeeJob dept_employee_job where dept_employee_job.employee.uuid = :employeeUuid";

    public DepartmentEmployeeJob getMajor(String employeeUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("employeeUuid", employeeUuid);
        return this.findUnique(QUERY_MAJOR_BY_EMPLOYEE_UUID, values);
    }

    /**
     * @param uuid
     * @return
     */
    public List<DepartmentEmployeeJob> getByDepartment(String uuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("departmentUuid", uuid);
        return this.find(QUERY_BY_DEPARTMENT_UUID, values);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteByEmployee(String uuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("employeeUuid", uuid);
        this.batchExecute(DELETE_BY_EMPLOYEE_UUID, values);
    }
}
