/*
 * @(#)2012-11-23 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.repository;

import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.node.Node;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-23.1	zhulh		2012-11-23		Create
 * </pre>
 * @date 2012-11-23
 */
public interface TaskRepository extends Repository {

    boolean complete(Node node, ExecutionContext executionContext);

    /**
     * @param userId
     * @param taskInstance
     */
    void copyPermissions2OtherParallelTaskInstances(String userId, TaskInstance taskInstance);

}
