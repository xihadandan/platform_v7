/*
 * @(#)2014-5-24 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.org.entity.DepartmentUserJob;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-5-24.1	zhulh		2014-5-24		Create
 * </pre>
 * @date 2014-5-24
 */
public interface DepartmentUserService extends BaseService {
    /**
     * 获取部门的树形结构
     *
     * @param excludeUuid
     * @return
     */
    TreeNode getDepartmentAsTree(String excludeUuid);

    /**
     * 查询指定部门下的用户
     *
     * @param departmentUuid
     * @param jqGridQueryInfo
     * @param queryInfo
     * @return
     */
    QueryData queryByDepartment(String departmentUuid, JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo);

    /**
     * @return
     * @Title: getAll
     * @Description: 获取所有部门用户
     */
    public List<DepartmentUserJob> getAll();
}
