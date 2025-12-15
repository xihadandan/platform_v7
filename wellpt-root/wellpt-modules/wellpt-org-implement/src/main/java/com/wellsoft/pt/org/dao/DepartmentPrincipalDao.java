/*
 * @(#)2013-1-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.DepartmentPrincipal;
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
public class DepartmentPrincipalDao extends OrgHibernateDao<DepartmentPrincipal, String> {


    private static final String GET_MANAGER_BY_DEPARTMENT_UUID = "from DepartmentPrincipal dp where dp.isManager = true and dp.department.uuid = :departmentUuid order by dp.user.code asc";
    private static final String DELETE_MANAGER_BY_DEPARTMENT_UUID = "delete from DepartmentPrincipal dp where dp.isManager = true and dp.department.uuid = :departmentUuid";

    private static final String GET_BRANCHED_BY_DEPARTMENT_UUID = "from DepartmentPrincipal dp where dp.isBranched = true and dp.department.uuid = :departmentUuid order by dp.user.code asc";
    private static final String DELETE_BRANCHED_BY_DEPARTMENT_UUID = "delete from DepartmentPrincipal dp where dp.isBranched = true and dp.department.uuid = :departmentUuid";

    private static final String GET_PRINCIPAL_BY_DEPARTMENT_UUID = "from DepartmentPrincipal dp where dp.isPrincipal = true and dp.department.uuid = :departmentUuid order by dp.user.code asc";
    private static final String DELETE_PRINCIPAL_BY_DEPARTMENT_UUID = "delete from DepartmentPrincipal dp where dp.isPrincipal = true and dp.department.uuid = :departmentUuid";
    private static final String DELETE_PRINCIPAL_BY_USER_UUID = "delete from DepartmentPrincipal dp where dp.user.uuid = :userUuid";
    private static final String DELETE_DEPT_PRINCIPAL_BY_USER_ID = "delete from DeptPrincipal where orgId = :userId";


    /**
     * 根据部门UUID，获取部门管理员
     *
     * @param uuid
     */
    public List<DepartmentPrincipal> getManager(String deptUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("departmentUuid", deptUuid);
        return this.query(GET_MANAGER_BY_DEPARTMENT_UUID, values, DepartmentPrincipal.class);
    }

    /**
     * 根据部门UUID，删除部门管理员
     *
     * @param uuid
     */
    public void deleteManager(String deptUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("departmentUuid", deptUuid);
        this.batchExecute(DELETE_MANAGER_BY_DEPARTMENT_UUID, values);
    }

    /**
     * 根据部门UUID，获取部门分管领导
     *
     * @param uuid
     */
    public List<DepartmentPrincipal> getBranched(String deptUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("departmentUuid", deptUuid);
        return this.query(GET_BRANCHED_BY_DEPARTMENT_UUID, values, DepartmentPrincipal.class);
    }

    /**
     * 根据部门UUID，删除部门分管领导
     *
     * @param uuid
     */
    public void deleteBranched(String deptUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("departmentUuid", deptUuid);
        this.batchExecute(DELETE_BRANCHED_BY_DEPARTMENT_UUID, values);
    }

    /**
     * 根据部门UUID，获取部门负责人
     *
     * @param uuid
     */
    public List<DepartmentPrincipal> getPrincipal(String deptUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("departmentUuid", deptUuid);
        return this.query(GET_PRINCIPAL_BY_DEPARTMENT_UUID, values, DepartmentPrincipal.class);
    }

    /**
     * 根据部门UUID，删除部门负责人
     *
     * @param uuid
     */
    public void deletePrincipal(String deptUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("departmentUuid", deptUuid);
        this.batchExecute(DELETE_PRINCIPAL_BY_DEPARTMENT_UUID, values);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteByUserUuid(String userUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userUuid", userUuid);
        this.batchExecute(DELETE_PRINCIPAL_BY_USER_UUID, values);
    }

    /**
     * 删除DeptPrincipal（部门负责人）中的用户
     *
     * @param userId
     */
    public void deleteByUserId(String userId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userId", userId);
        this.batchExecute(DELETE_DEPT_PRINCIPAL_BY_USER_ID, values);
    }

    public List<DepartmentPrincipal> getAll() {
        // TODO Auto-generated method stub
        return super.getAll();
    }

}
