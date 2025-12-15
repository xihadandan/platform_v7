/*
 * @(#)9/5/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.node;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.log.service.FlowLogService;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 9/5/24.1	    zhulh		9/5/24		    Create
 * </pre>
 * @date 9/5/24
 */
public class ScriptNode extends TaskNode {
    private static final long serialVersionUID = -7422294790029309157L;

    @Override
    public void execute(ExecutionContext executionContext) {
        logger.debug("execute task node: " + name + "[" + id + "]");

        // 办理人为空自动进入下一个环节标识
        executionContext.getToken().getTaskData().setIsEmptyToTask(this.id, true);

        // 节点执行处理
        handler.execute(this, executionContext);

        // 自动流转日志
        FlowLogService flowLogService = ApplicationContextHolder.getBean(FlowLogService.class);
        flowLogService.logAutoHandleTaskNode(executionContext, this);

        // 进入下一个环节
        executionContext.getToken().signal();
    }

    @Override
    public boolean complete(ExecutionContext executionContext) {
        return true;
    }

}
