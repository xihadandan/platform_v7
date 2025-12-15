/*
 * @(#)2014-2-25 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.dispatcher;

import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.node.SubTaskNode;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-2-25.1	zhulh		2014-2-25		Create
 * </pre>
 * @date 2014-2-25
 */
public interface CustomDispatcherFlowResolver {

    /**
     * 获取自定义接口名称
     *
     * @return
     */
    String getName();

    /**
     * 自定义子流程分发
     *
     * @param executionContext
     * @param flowInstance
     * @param parentTask
     * @param subTaskNode
     * @param subFlowIndex
     */
    void create(ExecutionContext executionContext, FlowInstance flowInstance, TaskInstance parentTask,
                SubTaskNode subTaskNode, int subFlowIndex);
}
