/*
 * @(#)2015-10-19 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.query.api;

import com.wellsoft.pt.jpa.query.Query;

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
 * 2015-10-19.1	zhulh		2015-10-19		Create
 * </pre>
 * @date 2015-10-19
 */
public interface TaskOperationQuery extends Query<TaskOperationQuery, TaskOperationQueryItem> {

    TaskOperationQuery action(String action);

    TaskOperationQuery actionType(String actionType);

    TaskOperationQuery actionCode(Integer actionCode);

    TaskOperationQuery opinionValue(String opinionValue);

    TaskOperationQuery opinionLabel(String opinionLabel);

    TaskOperationQuery opinionText(String opinionText);

    TaskOperationQuery opinionTextLike(String opinionText);

    TaskOperationQuery operatorId(String operatorId);

    TaskOperationQuery taskId(String taskId);

    TaskOperationQuery taskName(String taskName);

    TaskOperationQuery taskInstUuid(String taskInstUuid);

    TaskOperationQuery flowInstUuid(String flowInstUuid);

    TaskOperationQuery flowInstUuids(List<String> flowInstUuids);

    TaskOperationQuery orderByCreateTimeAsc();

    TaskOperationQuery orderByCreateTimeDesc();

}
