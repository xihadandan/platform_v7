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

/**
 * Description: 如何描述该类
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-12.1  zhengky	2014-8-12	  Create
 * </pre>
 * @date 2014-8-12
 */
public interface DepartmentJobService extends BaseService {
    /**
     * 获取部门的树形结构
     *
     * @param excludeUuid
     * @return
     */
    TreeNode getDepartmentAsTree(String excludeUuid);

    /**
     * 查询指定部门下的岗位
     *
     * @param departmentUuid
     * @param jqGridQueryInfo
     * @param queryInfo
     * @return
     */
    QueryData queryByDepartment(String departmentUuid, JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo);

}
