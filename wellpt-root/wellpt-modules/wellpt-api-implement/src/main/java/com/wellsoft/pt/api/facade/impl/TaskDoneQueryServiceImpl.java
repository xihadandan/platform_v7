/*
 * @(#)2014-8-12 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.api.WellptQueryRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.bpm.engine.query.api.TaskQuery;
import com.wellsoft.pt.security.acl.support.AclPermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
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
 * 2014-8-12.1	zhulh		2014-8-12		Create
 * </pre>
 * @date 2014-8-12
 */
@Service(ApiServiceName.TASK_DONE_QUERY)
public class TaskDoneQueryServiceImpl extends TaskQueryServiceImpl {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.impl.TaskQueryServiceImpl#preTaskQuery(com.wellsoft.pt.api.WellptQueryRequest)
     */
    @Override
    protected TaskQuery preTaskQuery(WellptQueryRequest<?> queryRequest) {
        TaskQuery taskQuery = super.preTaskQuery(queryRequest);
        StringBuilder whereSql = new StringBuilder();
        whereSql.append("(t1.end_time is null and t2.end_time is null)");
        Map<String, Object> values = Maps.newHashMap();
        taskQuery.where(whereSql.toString(), values);
        return taskQuery;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.impl.TaskQueryServiceImpl#getPermissions()
     */
    @Override
    protected List<Permission> getPermissions() {
        return Arrays.asList(AclPermission.DONE);
    }

}
