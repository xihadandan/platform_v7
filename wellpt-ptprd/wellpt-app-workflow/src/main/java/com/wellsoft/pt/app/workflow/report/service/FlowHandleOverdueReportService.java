/*
 * @(#)8/8/25 V1.0
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
 * 8/8/25.1	    zhulh		8/8/25		    Create
 * </pre>
 * @date 8/8/25
 */
public interface FlowHandleOverdueReportService {

    long queryOverdueCountByUser(Map<String, Object> params);

    double queryAvgOverdueCountByUser(Map<String, Object> params);

    double queryAvgOverdueTimeByUser(Map<String, Object> params);

    /**
     * 平均逾期时长最长流程
     *
     * @param params
     * @return
     */
    QueryItem getAvgMaxOverdueTimeByUser(Map<String, Object> params);

    List<QueryItem> queryAvgOverdueTimeByUserGroupByUser(Map<String, Object> params, PagingInfo pagingInfo);

    List<QueryItem> queryOverdueCountByUserGroupByUser(Map<String, Object> params, PagingInfo pagingInfo);

    long queryOverdueCountByDept(Map<String, Object> params);

    double queryAvgOverdueTimeByDept(Map<String, Object> params);

    double queryAvgOverdueCountByDept(Map<String, Object> params);

    QueryItem getAvgMaxOverdueTimeByDept(Map<String, Object> params);

    List<QueryItem> queryAvgOverdueTimeByDeptGroupByDept(List<String> deptIds, Map<String, Object> params, PagingInfo pagingInfo);

    List<QueryItem> queryOverdueCountByDeptGroupByDept(List<String> deptIds, Map<String, Object> params, PagingInfo pagingInfo);
}
