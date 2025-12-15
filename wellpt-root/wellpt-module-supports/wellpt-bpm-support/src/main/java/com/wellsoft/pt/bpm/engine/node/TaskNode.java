/*
 * @(#)2012-11-2 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.node;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.core.TaskTransition;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.core.Transition;
import com.wellsoft.pt.bpm.engine.enums.TransferCode;
import com.wellsoft.pt.bpm.engine.log.service.FlowLogService;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * Description: 任务节点类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-2.1	zhulh		2012-11-2		Create
 * </pre>
 * @date 2012-11-2
 */
public class TaskNode extends Node {

    private static final long serialVersionUID = 2406217552987708155L;
    protected List<String> toIDs;
    private String toID;
    /**
     * 与前环节相同
     */
    private String sameUserSubmit;
    /**
     * 只需要其中一个人办理
     */
    private boolean isAnyone;
    /**
     * 按人员顺序依次办理
     */
    private boolean isByOrder;


    /**
     * @return the toID
     */
    @Override
    public String getToID() {
        return toID != null ? toID : CollectionUtils.isNotEmpty(toIDs) ? toIDs.get(0) : null;
    }

    /**
     * @param toID 要设置的toID
     */
    public void setToID(String toID) {
        this.toID = toID;
    }

    /**
     * @return the toIDs
     */
    public List<String> getToIDs() {
        return toIDs;
    }

    /**
     * @param toIDs 要设置的toIDs
     */
    public void setToIDs(List<String> toIDs) {
        this.toIDs = toIDs;
    }

    /**
     * @return the sameUserSubmit
     */
    public String getSameUserSubmit() {
        return sameUserSubmit;
    }

    /**
     * @param sameUserSubmit 要设置的sameUserSubmit
     */
    public void setSameUserSubmit(String sameUserSubmit) {
        this.sameUserSubmit = sameUserSubmit;
    }

    /**
     * @return the isAnyone
     */
    public boolean isAnyone() {
        return isAnyone;
    }

    /**
     * @param isAnyone 要设置的isAnyone
     */
    public void setAnyone(boolean isAnyone) {
        this.isAnyone = isAnyone;
    }

    /**
     * @return the isByOrder
     */
    public boolean isByOrder() {
        return isByOrder;
    }

    /**
     * @param isByOrder 要设置的isByOrder
     */
    public void setByOrder(boolean isByOrder) {
        this.isByOrder = isByOrder;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.node.Node#enter(com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void enter(ExecutionContext executionContext) {
        super.enter(executionContext);
        logger.debug("enter task node: " + name + "[" + id + "]");

        // 进入节点时处理
        handler.enter(this, executionContext);

        // 执行节点
        execute(executionContext);

    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.node.Node#execute(com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void execute(ExecutionContext executionContext) {
        if (logger.isDebugEnabled()) {
            logger.debug("execute task node: " + name + "[" + id + "]");
        }

        // 节点执行处理
        handler.execute(this, executionContext);

        Token token = executionContext.getToken();
        // 办理人为空自动进入下一个环节
        if (token.getTaskData().isEmptyToTask(this.id)) {
            // 自动流转日志
            FlowLogService flowLogService = ApplicationContextHolder.getBean(FlowLogService.class);
            flowLogService.logAutoHandleTaskNode(executionContext, this);
            token.signal();
        }

        // 与前环节相同处理
        handler.afterExecuted(this, executionContext);

        // 自动完成进入下一个环节
        if (token.getTaskData().isAutoCompletedTask(this.id)) {
            token.getTaskData().setTransferCode(token.getTask().getUuid(), TransferCode.SkipTask.getCode());
            token.signal();
        }
    }

    @Override
    public boolean complete(ExecutionContext executionContext) {
        return handler.complete(this, executionContext);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.node.Node#leave(com.wellsoft.pt.workflow.engine.core.Transition, com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void leave(Transition transition, ExecutionContext executionContext) {
        logger.debug("leave task node: " + name + "[" + id + "]");

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
        Transition transition = new TaskTransition();
        transition.setFrom(this);
        return transition;
    }


}
