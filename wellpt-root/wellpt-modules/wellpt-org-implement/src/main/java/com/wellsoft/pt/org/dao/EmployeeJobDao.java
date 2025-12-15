/*
 * @(#)2013-1-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.EmployeeJob;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Description: 员工职位DAO
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-25.1  zhengky	2014-8-25	  Create
 * </pre>
 * @date 2014-8-25
 */
@Repository
public class EmployeeJobDao extends OrgHibernateDao<EmployeeJob, String> {

    private String QUERY_JOB = "from EmployeeJob employee_job where employee_job.job.uuid = ?";
    private String QUERY_BY_EMPLOYEE_UUID = "from EmployeeJob employee_job where employee_job.employee.uuid = ?";

    private String DELETE_BY_EMPLOYEE_UUID = "delete from EmployeeJob employee_job where employee_job.employee.uuid = ?";
    private String DELETE_JOB_BY_JOB_UUID = "delete from EmployeeJob employee_job where employee_job.job.uuid = ?";

    private String QUERY_MAJOR_BY_EMPLOYEE_UUID = "from EmployeeJob employee_job where employee_job.isMajor = true and employee_job.employee.uuid = ?";
    private String QUERY_OTHER_BY_EMPLOYEE_UUID = "from EmployeeJob employee_job where employee_job.isMajor = false and employee_job.employee.uuid = ?";

    private String DELETE_MAJOR_BY_EMPLOYEE_UUID = "delete from EmployeeJob employee_job where employee_job.isMajor = true and employee_job.employee.uuid = ?";
    private String DELETE_OTHER_BY_EMPLOYEE_UUID = "delete from EmployeeJob employee_job where employee_job.isMajor = false and employee_job.employee.uuid = ?";

    /**
     * @param uuid
     * @return
     */
    public List<EmployeeJob> getByEmployee(String uuid) {
        return this.find(QUERY_BY_EMPLOYEE_UUID, uuid);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteByEmployee(String uuid) {
        this.batchExecute(DELETE_BY_EMPLOYEE_UUID, uuid);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteJob(String uuid) {
        this.batchExecute(DELETE_JOB_BY_JOB_UUID, uuid);
    }

    /**
     * @param uuid
     * @return
     */
    public List<EmployeeJob> getJobs(String uuid) {
        return this.find(QUERY_JOB, uuid);
    }

    /**
     * @param uuid
     * @return
     */
    public List<EmployeeJob> getMajorJobs(String uuid) {
        return this.find(QUERY_MAJOR_BY_EMPLOYEE_UUID, uuid);
    }

    /**
     * @param uuid
     * @return
     */
    public List<EmployeeJob> getOtherJobs(String uuid) {
        return this.find(QUERY_OTHER_BY_EMPLOYEE_UUID, uuid);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteMajorByEmployee(String uuid) {
        this.batchExecute(DELETE_MAJOR_BY_EMPLOYEE_UUID, uuid);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteOtherByEmployee(String uuid) {
        this.batchExecute(DELETE_OTHER_BY_EMPLOYEE_UUID, uuid);
    }

}
