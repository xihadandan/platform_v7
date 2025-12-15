/*
 * @(#)2015年8月28日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.access;

import com.google.common.collect.Lists;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.support.TaskUserExpressionConfigJson;
import com.wellsoft.pt.rule.engine.RuleEngine;
import com.wellsoft.pt.rule.engine.RuleEngineFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Description: 自定义用户解析
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
@Component
public class UserCustomIdentityResolver extends AbstractIdentityResolver {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * @param node
     * @param token
     * @param userUnitElement
     * @param participantType
     * @param sidGranularity
     * @return
     */
    @Override
    public List<FlowUserSid> resolve(Node node, Token token, UserUnitElement userUnitElement, ParticipantType participantType, String sidGranularity) {
        String value = userUnitElement.getValue();
        if (StringUtils.isBlank(value)) {
            return Collections.emptyList();
        }
        List<String> raws = Lists.newArrayList(value);
        return this.resolve(node, token, raws, participantType, sidGranularity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.access.IdentityResolver#resolve(com.wellsoft.pt.bpm.engine.node.Node, com.wellsoft.pt.bpm.engine.core.Token, java.util.List, com.wellsoft.pt.bpm.engine.enums.ParticipantType)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<FlowUserSid> resolve(Node node, Token token, List<String> rawUsers, ParticipantType participantType,
                                     String sidGranularity) {
        List<FlowUserSid> userIds = new ArrayList<FlowUserSid>(0);
        if (CollectionUtils.isEmpty(rawUsers)) {
            return userIds;
        }

        for (String rawUser : rawUsers) {
            try {
                TaskUserExpressionConfigJson json = JsonUtils.json2Object(rawUser, TaskUserExpressionConfigJson.class);
                String expression = json.getExpression(true);
                if (StringUtils.isBlank(expression)) {
                    continue;
                }
                String scriptText = "SetOperation A = " + expression + "; end";
                RuleEngine ruleEngine = RuleEngineFactory.getRuleEngine();
                ruleEngine.setVariable("node", node);
                ruleEngine.setVariable("token", token);
                ruleEngine.setVariable("participantType", participantType);
                ruleEngine.execute(scriptText);
                Object set = ruleEngine.getVariable("A");
                userIds.addAll((Collection<FlowUserSid>) set);
            } catch (Exception e) {
                String msg = "";
                if (token.getFlowInstance() != null) {
                    msg += "流程[" + token.getFlowInstance().getName() + "]";
                }
                if (token.getTransition() != null) {
                    msg += "流向[" + token.getTransition().getFrom().getId() + "]";
                }
                logger.warn("流程 = {} , rawUser = {} , 解析自定义用户表达式异常!", new Object[]{msg, rawUser});
                throw new RuntimeException(e);
            }

        }

        return userIds;
    }
}
