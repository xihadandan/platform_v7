/*
 * @(#)2015-6-25 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine.expression.wf;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.access.OptionIdentityResolver;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.rule.engine.Context;
import com.wellsoft.pt.rule.engine.Param;
import com.wellsoft.pt.rule.engine.suport.CommandName;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-25.1	zhulh		2015-6-25		Create
 * </pre>
 * @date 2015-6-25
 */
public class OptionExpression extends WorkFlowExpression {

    private String expression;

    /**
     * 如何描述该构造方法
     *
     * @param tmp
     */
    public OptionExpression(String expression) {
        this.expression = expression;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.rule.engine.expression.Expression#evaluate(com.wellsoft.pt.rule.engine.Param)
     */
    @Override
    public Object evaluate(Param param) {
        Context context = param.getContext();
        OptionIdentityResolver optionIdentityResolver = ApplicationContextHolder.getBean(OptionIdentityResolver.class);

        Node node = (Node) context.getValue(KEY_NODE);
        Token token = (Token) context.getValue(KEY_TOKEN);

        List<String> options = getParamAsList(this.expression, CommandName.Option);
        List<FlowUserSid> userIds = optionIdentityResolver.resolve(node, token, options, ParticipantType.TodoUser);
        return userIds;
    }

}
