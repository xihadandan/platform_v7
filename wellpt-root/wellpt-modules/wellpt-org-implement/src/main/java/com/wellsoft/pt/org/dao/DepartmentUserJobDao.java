/*
 * @(#)2013-1-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.DepartmentUserJob;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-15.1	zhulh		2013-1-15		Create
 * </pre>
 * @date 2013-1-15
 */
@Repository
public class DepartmentUserJobDao extends OrgHibernateDao<DepartmentUserJob, String> {
    private static final String QUERY_MAJOR_BY_USER_UUID = "from DepartmentUserJob dept_user_job where dept_user_job.isMajor = true and dept_user_job.user.uuid = :userUuid";
    private static final String QUERY_BY_DEPARTMENT_UUID = "from DepartmentUserJob dept_user_job where dept_user_job.department.uuid = :departmentUuid order by dept_user_job.user.code asc";
    private static final String DELETE_BY_USER_UUID = "delete from DepartmentUserJob dept_user_job where dept_user_job.user.uuid = :userUuid";

    public DepartmentUserJob getMajor(String userUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userUuid", userUuid);
        return this.findUnique(QUERY_MAJOR_BY_USER_UUID, values);
    }

    /**
     * @param uuid
     * @return
     */
    public List<DepartmentUserJob> getByDepartment(String uuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("departmentUuid", uuid);
        return this.find(QUERY_BY_DEPARTMENT_UUID, values);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteByUser(String uuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userUuid", uuid);
        this.batchExecute(DELETE_BY_USER_UUID, values);
    }

    public List<DepartmentUserJob> getAllByTenantId(String tenantId) {
        return this.findBy("tenantId", tenantId);
    }

}
