/*
 * @(#)2014-11-11 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.core.ext;

import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.core.TaskTransition;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.core.Transition;
import com.wellsoft.pt.bpm.engine.core.handler.HandlerFactory;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.TransferCode;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.support.TaskData;

import java.util.ArrayList;
import java.util.HashMap;
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
 * 2014-11-11.1	zhulh		2014-11-11		Create
 * </pre>
 * @date 2014-11-11
 */
public class CancelToken extends Token {
    private TaskInstance targetTaskInstance;
    private String todoUserId;

    public CancelToken(TaskInstance sourceTaskInstance, TaskInstance targetTaskInstance, String todoUserId,
                       TaskData taskData) {
        super(sourceTaskInstance.getFlowInstance(), taskData);

        this.setTask(sourceTaskInstance);
        this.setParentTask(sourceTaskInstance.getParent());
        this.setNode(getFlowDelegate().getTaskNode(sourceTaskInstance.getId()));

        this.targetTaskInstance = targetTaskInstance;
        this.todoUserId = todoUserId;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.core.Token#signal()
     */
    @Override
    public void signal() {
        Transition transition = new TaskTransition();
        transition.setFrom(getNode());
        signal(transition, new ExecutionContext(this));
    }

    /**
     * @param leavingTransition
     * @param executionContext
     */
    private void signal(Transition trans, ExecutionContext executionContext) {
        String toTaskId = targetTaskInstance.getId();
        // 设置下一结点
        List<Node> tos = new ArrayList<Node>();
        Node to = getFlowDelegate().getTaskNode(targetTaskInstance.getId());
        to.setHandler(HandlerFactory.getHanlder(TransferCode.Cancel));
        tos.add(to);
        trans.setTos(tos);
        setTransition(trans);

        // 设置办理人
        Map<String, List<String>> taskUsers = new HashMap<String, List<String>>();
        List<String> userIds = new ArrayList<String>();
        userIds.add(todoUserId);
        taskUsers.put(toTaskId, userIds);
        // 计算结点用户
        getTaskData().setTaskUsers(taskUsers);

        getNode().setHandler(HandlerFactory.getHanlder(TransferCode.Cancel));

        getNode().leave(trans, executionContext);
    }
}
