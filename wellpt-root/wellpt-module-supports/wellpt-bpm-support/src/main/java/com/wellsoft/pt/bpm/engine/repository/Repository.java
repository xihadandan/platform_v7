/*
 * @(#)2012-11-23 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.repository;

import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.node.Node;

/**
 * Description: 任务存储服务接口
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
public interface Repository {
    /**
     * 进入时存储当前任务结点信息
     *
     * @param node
     * @param executionContext
     */
    void storeEnter(Node node, ExecutionContext executionContext);

    /**
     * 执行时存储当前任务结点信息
     *
     * @param node
     * @param executionContext
     */
    void store(Node node, ExecutionContext executionContext);

    /**
     * 离开时存储当前任务结点信息
     *
     * @param node
     * @param executionContext
     */
    void storeLeave(Node node, ExecutionContext executionContext);
}
