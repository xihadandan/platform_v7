/*
 * @(#)2012-11-2 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.node;

import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.core.StartTransition;
import com.wellsoft.pt.bpm.engine.core.Transition;

/**
 * Description: 开始节点类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-2.1	zhulh		2012-11-2		Create
 * </pre>
 * @date 2012-11-2
 */
public class StartNode extends Node {

    private static final long serialVersionUID = 1337971164954859664L;

    protected String toID;

    /**
     * @return the toID
     */
    @Override
    public String getToID() {
        return toID;
    }

    /**
     * @param toID 要设置的toID
     */
    public void setToID(String toID) {
        this.toID = toID;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.node.Node#enter(com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void enter(ExecutionContext executionContext) {
        super.enter(executionContext);
        logger.debug("enter start node: " + name + "[" + id + "]");

        //进入节点时处理
        handler.enter(this, executionContext);

        //执行节点
        execute(executionContext);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.node.Node#execute(com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void execute(ExecutionContext executionContext) {
        logger.debug("execute start node: " + name + "[" + id + "]");

        // 节点执行处理
        handler.execute(this, executionContext);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.node.Node#leave(com.wellsoft.pt.workflow.engine.core.Transition, com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void leave(Transition transition, ExecutionContext executionContext) {
        logger.debug("leave start node: " + name + "[" + id + "]");
        //节点离开时处理
        handler.leave(this, executionContext);

        super.leave(transition, executionContext);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.node.Node#getLeavingTransition()
     */
    @Override
    public Transition getLeavingTransition() {
        Transition transition = new StartTransition();
        transition.setFrom(this);
        return transition;
    }

}
