/*
 * @(#)2012-11-21 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.core;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.node.StartNode;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-21.1	zhulh		2012-11-21		Create
 * </pre>
 * @date 2012-11-21
 */
@Component
public class StartTransitionResolver implements TransitionResolver {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.core.TransitionResolver#resolveForkTask(com.wellsoft.pt.workflow.engine.core.Transition, com.wellsoft.pt.workflow.engine.core.Token)
     */
    @Override
    public void resolveForkTask(Transition transition, Token token) {
        Node from = transition.getFrom();
        // 如果用户已经选择流向或子流程提交到指定环节，则返回该流向
        String toTaskId = token.getTaskData().getToTaskId(transition.getFromId());
        if (StringUtils.isNotBlank(toTaskId) && (!(from instanceof StartNode) || (from instanceof StartNode && token.getParentTask() != null))) {
            List<Node> tos = new ArrayList<Node>();
            if (StringUtils.contains(toTaskId, Separator.SEMICOLON.getValue())) {
                String[] toTaskIds = StringUtils.split(toTaskId, Separator.SEMICOLON.getValue());
                for (String taskId : toTaskIds) {
                    Node to = token.getFlowDelegate().getTaskNode(taskId);
                    tos.add(to);
                }
            } else {
                Node to = token.getFlowDelegate().getTaskNode(toTaskId);
                tos.add(to);
            }
            transition.setTos(tos);
        } else {
            if (from instanceof StartNode) {
                toTaskId = Objects.toString(token.getTaskData().get("startToNodeId"), StringUtils.EMPTY);
            }
            if (StringUtils.isBlank(toTaskId)) {
                toTaskId = from.getToID();
            }
            Node to = token.getFlowDelegate().getTaskNode(toTaskId);
            List<Node> tos = new ArrayList<Node>();
            tos.add(to);
            transition.setTos(tos);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.core.TransitionResolver#checkAndPrepare(com.wellsoft.pt.bpm.engine.core.Transition, com.wellsoft.pt.bpm.engine.core.Token)
     */
    @Override
    public void checkAndPrepare(Transition transition, Token token) {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.core.TransitionResolver#resolveJoinTask(com.wellsoft.pt.bpm.engine.core.Transition, com.wellsoft.pt.bpm.engine.core.Token)
     */
    @Override
    public boolean resolveJoinTask(Transition transition, Token token) {
        return true;
    }

}
