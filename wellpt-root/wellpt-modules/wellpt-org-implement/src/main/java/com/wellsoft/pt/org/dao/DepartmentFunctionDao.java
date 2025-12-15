/*
 * @(#)2013-1-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.DepartmentFunction;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 部门职能DAO
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-24.1  zhengky	2014-8-24	  Create
 * </pre>
 * @date 2014-8-24
 */
@Repository
public class DepartmentFunctionDao extends OrgHibernateDao<DepartmentFunction, String> {

    private String QUERY_FUNCTION = "from DepartmentFunction department_function where department_function.functionUuid = :uuid";
    private String QUERY_BY_DEPARTMENT_UUID = "from DepartmentFunction department_function where department_function.department.uuid = :uuid";
    private String DELETE_BY_DEPARTMENT_UUID = "delete from DepartmentFunction department_function where department_function.department.uuid = :uuid";
    private String DELETE_FUNCTION_BY_DEPARTMENT_UUID = "delete from DepartmentFunction department_function where department_function.functionUuid = :uuid";

    /**
     * @param uuid
     * @return
     */
    public List<DepartmentFunction> getByDepartment(String uuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("uuid", uuid);
        return this.query(QUERY_BY_DEPARTMENT_UUID, values, DepartmentFunction.class);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteByDepartment(String uuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("uuid", uuid);
        this.batchExecute(DELETE_BY_DEPARTMENT_UUID, values);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteFunction(String uuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("uuid", uuid);
        this.batchExecute(DELETE_FUNCTION_BY_DEPARTMENT_UUID, values);
    }

    /**
     * @param uuid
     * @return
     */
    public List<DepartmentFunction> getFunctions(String uuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("uuid", uuid);
        return this.query(QUERY_FUNCTION, values, DepartmentFunction.class);
    }

}
