/*
 * @(#)2015年8月28日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine.expression.wf;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.access.SidGranularityResolver;
import com.wellsoft.pt.bpm.engine.context.listener.TaskUserIndicate;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.support.TaskUserIndicateComparator;
import com.wellsoft.pt.rule.engine.Context;
import com.wellsoft.pt.rule.engine.Param;
import com.wellsoft.pt.rule.engine.suport.CommandName;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年8月28日.1	zhulh		2015年8月28日		Create
 * </pre>
 * @date 2015年8月28日
 */
public class InterfaceExpression extends WorkFlowExpression {

    private String expression;

    /**
     * 如何描述该构造方法
     *
     * @param expression
     */
    public InterfaceExpression(String expression) {
        this.expression = expression;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.rule.engine.expression.Expression#evaluate(com.wellsoft.pt.rule.engine.Param)
     */
    @Override
    public Object evaluate(Param param) {
        // 获取接口
        List<String> interfaces = getParamAsList(this.expression, CommandName.Interface);
        if (interfaces.isEmpty()) {
            return Collections.emptySet();
        }
        List<TaskUserIndicate> taskUserIndicates = new ArrayList<TaskUserIndicate>();
        for (String tmpInterface : interfaces) {
            TaskUserIndicate taskUserIndicate = ApplicationContextHolder.getBean(tmpInterface, TaskUserIndicate.class);
            taskUserIndicates.add(taskUserIndicate);
        }
        Collections.sort(taskUserIndicates, new TaskUserIndicateComparator());

        SidGranularityResolver sidGranularityResolver = ApplicationContextHolder.getBean(SidGranularityResolver.class);
        // 获取用户
        Set<FlowUserSid> userIds = new LinkedHashSet<FlowUserSid>(0);
        Context context = param.getContext();
        Node node = (Node) context.getValue(KEY_NODE);
        Token token = (Token) context.getValue(KEY_TOKEN);
        for (TaskUserIndicate taskUserIndicate : taskUserIndicates) {
            Set<String> candidateUsers = taskUserIndicate.getCandidateUsers(node, token.getTaskData());
            userIds.addAll(sidGranularityResolver.resolve(node, token, candidateUsers));
        }

        return Arrays.asList(userIds.toArray(new FlowUserSid[0]));
    }

}
