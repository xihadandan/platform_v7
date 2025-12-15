/*
 * @(#)2015年8月31日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.context.listener.impl;

import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年8月31日.1	zhulh		2015年8月31日		Create
 * </pre>
 * @date 2015年8月31日
 */
@Component
public class ReturnCurrentUserTaskListener extends TaskListenerAdapter {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.impl.TaskListenerAdapter#getName()
     */
    @Override
    public String getName() {
        return "返回当前用户";
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
        candidateUsers.add(SpringSecurityUtils.getCurrentUserId());
        return candidateUsers;
    }

}
