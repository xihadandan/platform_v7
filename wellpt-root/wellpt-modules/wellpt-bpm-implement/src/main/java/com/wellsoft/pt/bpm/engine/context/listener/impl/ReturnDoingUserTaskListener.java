/*
 * @(#)2015年8月31日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.context.listener.impl;

import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.timer.service.AbstractTaskTimerService;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年12月11日.1	zhongzh		2018年12月11日		Create
 * </pre>
 * @date 2018年12月11日
 */
@Component
public class ReturnDoingUserTaskListener extends TaskListenerAdapter {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.impl.TaskListenerAdapter#getName()
     */
    @Override
    public String getName() {
        return "返回环节在办人";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.impl.TaskListenerAdapter#getOrder()
     */
    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.impl.TaskListenerAdapter#getCandidateUsers(com.wellsoft.pt.bpm.engine.node.Node, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    public Set<String> getCandidateUsers(Node node, TaskData taskData) {
        Set<String> candidateUsers = new HashSet<String>();
        Token token = taskData.getToken();
        if (token != null && token.getTask() != null) {
            candidateUsers.addAll(AbstractTaskTimerService.getDoingUserIds(token.getTask()));
        }
        if (candidateUsers.isEmpty()) {
            // SpringSecurityUtils.getCurrentUserId();
            candidateUsers.add(taskData.getUserId());
        }
        return candidateUsers;
    }
}
