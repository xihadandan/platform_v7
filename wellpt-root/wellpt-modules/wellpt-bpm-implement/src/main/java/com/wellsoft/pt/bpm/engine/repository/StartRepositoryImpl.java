/*
 * @(#)2012-11-23 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.repository;

import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.node.Node;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Service
@Transactional
public class StartRepositoryImpl implements StartRepository {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.repository.Repository#storeEnter(com.wellsoft.pt.workflow.engine.node.Node, com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void storeEnter(Node node, ExecutionContext executionContext) {

    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.repository.Repository#store(com.wellsoft.pt.workflow.engine.node.Node, com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void store(Node node, ExecutionContext executionContext) {

    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.repository.Repository#storeLeave(com.wellsoft.pt.workflow.engine.node.Node, com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void storeLeave(Node node, ExecutionContext executionContext) {

    }

}
