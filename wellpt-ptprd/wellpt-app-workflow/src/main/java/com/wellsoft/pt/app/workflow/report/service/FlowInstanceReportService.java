/*
 * @(#)6/27/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.service;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.query.FlowInstanceCountByCategoryQueryItem;
import com.wellsoft.pt.app.workflow.report.query.FlowInstanceCountQueryItem;

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
 * 6/27/25.1	    zhulh		6/27/25		    Create
 * </pre>
 * @date 6/27/25
 */
public interface FlowInstanceReportService {

    /**
     * 流程实例发起数量统计
     *
     * @param params
     * @return
     */
    List<FlowInstanceCountQueryItem> queryFlowInstanceCount(Map<String, Object> params);

    /**
     * 流程实例发起数量按流程分类统计
     *
     * @param params
     * @return
     */
    List<FlowInstanceCountByCategoryQueryItem> queryFlowInstanceCountByCategory(Map<String, Object> params);

    /**
     * @param queryName
     * @param queryParams
     * @return
     */
    List<QueryItem> namedQuery(String queryName, Map<String, Object> queryParams, PagingInfo pagingInfo);

    long queryFlowInstanceStartCount(Map<String, Object> params);

}
