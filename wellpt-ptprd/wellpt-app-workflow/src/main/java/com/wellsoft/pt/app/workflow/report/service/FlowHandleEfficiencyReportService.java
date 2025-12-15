/*
 * @(#)8/5/25 V1.0
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
 * 8/5/25.1	    zhulh		8/5/25		    Create
 * </pre>
 * @date 8/5/25
 */
public interface FlowHandleEfficiencyReportService {
    double queryAverageHandleTimeInMinuteByUser(Map<String, Object> params);

    double queryMaxHandleTimeInMinuteByUser(Map<String, Object> params);

    List<QueryItem> queryAverageHandleTimeInMinuteByUserGroupByUser(Map<String, Object> params, PagingInfo pagingInfo);

    double queryAverageHandleTimeInMinuteByDept(List<String> deptIds, Map<String, Object> params);

    List<QueryItem> queryAverageHandleTimeInMinuteByDeptGroupByDept(List<String> deptIds, Map<String, Object> params, PagingInfo pagingInfo);
}
