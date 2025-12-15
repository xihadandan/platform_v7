/*
 * @(#)7/14/25 V1.0
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
 * 7/14/25.1	    zhulh		7/14/25		    Create
 * </pre>
 * @date 7/14/25
 */
public interface FlowOverdueReportService {

    long queryOverdueCount(Map<String, Object> params);

    List<QueryItem> listOverdue(Map<String, Object> params, PagingInfo pagingInfo);

    QueryItem getMaxOverdue(Map<String, Object> params);

    Double getMaxOverdueTime(Map<String, Object> params);

    Double getAvgOverdueTime(Map<String, Object> params);

    List<QueryItem> listOverdueCount(Map<String, Object> params, PagingInfo pagingInfo);

    List<QueryItem> listAvgOverdueTime(Map<String, Object> params, PagingInfo pagingInfo);

    QueryItem getMaxOverdueCountByDefinition(Map<String, Object> params);
}
