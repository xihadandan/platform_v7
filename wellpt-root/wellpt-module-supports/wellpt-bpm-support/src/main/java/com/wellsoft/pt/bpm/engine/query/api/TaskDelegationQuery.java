/*
 * @(#)2016年7月13日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.query.api;

import com.wellsoft.pt.jpa.query.Query;

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
 * 2016年7月13日.1	zhulh		2016年7月13日		Create
 * </pre>
 * @date 2016年7月13日
 */
public interface TaskDelegationQuery extends Query<TaskDelegationQuery, TaskDelegationQueryItem> {
    TaskDelegationQuery consignor(String consignor);

    TaskDelegationQuery trustee(String trustee);

    TaskDelegationQuery completionState(Integer... completionStates);

    /**
     * 查询字段语句
     *
     * @param projection
     */
    TaskDelegationQuery projection(String projection);

    TaskDelegationQuery where(String whereSql, Map<String, Object> params);

    TaskDelegationQuery order(String orderBy);
}
