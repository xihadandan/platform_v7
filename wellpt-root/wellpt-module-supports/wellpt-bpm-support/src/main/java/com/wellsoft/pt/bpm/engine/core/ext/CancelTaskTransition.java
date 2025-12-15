/*
 * @(#)2014-11-11 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.core.ext;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.core.Transition;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.support.TaskData;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-11-11.1	zhulh		2014-11-11		Create
 * </pre>
 * @date 2014-11-11
 */
public class CancelTaskTransition extends Transition {

    private Node to;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.core.Transition#take(com.wellsoft.pt.bpm.engine.context.ExecutionContext)
     */
    @Override
    public void take(ExecutionContext executionContext) {
        this.to = getTos().get(0);
        Token token = executionContext.getToken();
        String preTaskId = null;
        String preTaskInstUuid = null;
        TaskInstance preTask = token.getTask();
        TaskData taskData = token.getTaskData();
        if (preTask != null) {
            preTaskId = preTask.getId();
            preTaskInstUuid = preTask.getUuid();
        }
        String toTaskId = to.getId();
        // if (preTask != null) {
        // taskData.setPreTaskId(toTaskId, preTaskId);
        // taskData.setPreTaskInstUuid(toTaskId, preTaskInstUuid);
        // taskData.setPreTaskProperties(toTaskId, "isParallel",
        // preTask.getIsParallel());
        // taskData.setPreTaskProperties(toTaskId, "parallelTaskInstUuid",
        // preTask.getParallelTaskInstUuid());
        // }
        token.setNode(null);
        token.setTask(null);

        // 流向变换处理
        CancelTaskTransitionHanlder hanlder = ApplicationContextHolder.getBean(CancelTaskTransitionHanlder.class);
        hanlder.execute(this, executionContext);

        // pass the token to the destinationNode node
        to.enter(executionContext);
    }
}
