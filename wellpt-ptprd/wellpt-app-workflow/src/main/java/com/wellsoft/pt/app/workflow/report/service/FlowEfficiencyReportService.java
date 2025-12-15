/*
 * @(#)7/9/25 V1.0
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
 * 7/9/25.1	    zhulh		7/9/25		    Create
 * </pre>
 * @date 7/9/25
 */
public interface FlowEfficiencyReportService {

    /**
     * 查询平均完成时间
     *
     * @param params
     * @return
     */
    Double queryAverageCompletedTimeInMinute(Map<String, Object> params);

    /**
     * @param params
     * @return
     */
    List<QueryItem> listFlowAverageCompletedTimeInMinute(Map<String, Object> params, PagingInfo pagingInfo);

    /**
     * 查询环节平均用时
     *
     * @param params
     * @return
     */
    Double queryAverageTaskDurationInMinute(Map<String, Object> params);

    /**
     * 查询环节平均用时
     *
     * @param params
     * @return
     */
    List<QueryItem> listAverageTaskDurationInMinute(Map<String, Object> params, PagingInfo pagingInfo);

    /**
     * 查询环节完成数量
     *
     * @param params
     * @return
     */
    long queryTaskCompletedCount(Map<String, Object> params);

    long queryTaskAllCount(Map<String, Object> params);
}
