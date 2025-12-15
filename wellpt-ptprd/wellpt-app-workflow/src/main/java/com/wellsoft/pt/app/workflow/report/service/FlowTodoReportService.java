/*
 * @(#)7/23/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.service;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 7/23/25.1	    zhulh		7/23/25		    Create
 * </pre>
 * @date 7/23/25
 */
public interface FlowTodoReportService {
    long getFlowTodoCountByUser(Map<String, Object> params);

    QueryItem getMaxFlowTodoCountByUser(Map<String, Object> params);

    List<QueryItem> listTodoCountByUserGroupByUser(Map<String, Object> params, PagingInfo pagingInfo);

    List<String> listFlowTodoDeptId(Map<String, Object> params);

    Map<String, String> listFlowTodoDeptIdAndName(Map<String, Object> params);

    long getFlowTodoCountByDept(List<String> deptIds, Map<String, Object> params);

    QueryItem getMaxFlowTodoCountByDept(List<String> deptIds, Map<String, Object> params);

    List<QueryItem> listTodoCountByDeptGroupByDept(List<String> deptIds, Map<String, Object> params, PagingInfo pagingInfo);
}
