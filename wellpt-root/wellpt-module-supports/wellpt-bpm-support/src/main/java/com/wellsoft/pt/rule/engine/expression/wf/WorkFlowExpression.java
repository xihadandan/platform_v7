/*
 * @(#)2015-6-25 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine.expression.wf;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.access.OptionOfIdentityResolver;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.enums.Participant;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import com.wellsoft.pt.rule.engine.Context;
import com.wellsoft.pt.rule.engine.Param;
import com.wellsoft.pt.rule.engine.expression.AbstractExpression;
import com.wellsoft.pt.rule.engine.expression.Expression;
import com.wellsoft.pt.rule.engine.expression.ExpressionParserFactory;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
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
public abstract class WorkFlowExpression extends AbstractExpression {

    protected static final String KEY_NODE = "node";
    protected static final String KEY_TOKEN = "token";

    private String orgVersionIdString;

    /**
     * @param expression
     * @return
     */
    protected List<String> getParamAsList(String expression, String methodName) {
        String tmp = expression;
        if (tmp.startsWith(methodName + "('") && tmp.endsWith("')")) {
            tmp = tmp.substring(methodName.length() + 2, tmp.length() - 2);
        }
        return Arrays.asList(StringUtils.split(tmp, Separator.COMMA.getValue()));
    }

    protected Expression parseOptionOf(String expression, String optionOf) {
        String[] exps = StringUtils.splitByWholeSeparator(expression, ",'");
        String exp0 = StringUtils.trim(exps[0]) + ")";
        String exp1 = StringUtils.trim(exps[1]);
        int last = exp1.lastIndexOf("'");
        orgVersionIdString = exp1.substring(0, last);
        return ExpressionParserFactory.getParser().parseParam(exp0, optionOf);
    }

    @SuppressWarnings("unchecked")
    protected List<FlowUserSid> evaluate(Param param, Participant optionOf, Expression paramExpression) {
        List<FlowUserSid> ids = (List<FlowUserSid>) paramExpression.evaluate(param);
        OptionOfIdentityResolver optionOfIdentityResolver = ApplicationContextHolder
                .getBean(OptionOfIdentityResolver.class);
        Context context = param.getContext();

        Node node = (Node) context.getValue(KEY_NODE);
        Token token = (Token) context.getValue(KEY_TOKEN);
        String orgVersionId = OrgVersionUtils.resolve(token, orgVersionIdString);
        token.getTaskData().put(OptionOfIdentityResolver.USER_IDS_FOR_OPTION_OF, ids);
        token.getTaskData().put(OptionOfIdentityResolver.ORG_ID_FOR_OPTION_OF, orgVersionId);

        List<String> raws = new ArrayList<String>();
        raws.add(optionOf.name());

        List<FlowUserSid> userIds = optionOfIdentityResolver.resolve(node, token, raws, ParticipantType.TodoUser);
        return userIds;
    }

}
