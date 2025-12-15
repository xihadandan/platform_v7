/*
 * @(#)2015-6-25 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine.expression.wf;

import com.wellsoft.pt.bpm.engine.enums.Participant;
import com.wellsoft.pt.rule.engine.Param;
import com.wellsoft.pt.rule.engine.expression.Expression;

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
public class RootDeptOfExpression extends WorkFlowExpression {

    private String expression;

    private Expression paramExpression;

    /**
     * @param expression
     */
    public RootDeptOfExpression(String expression) {
        this.expression = expression;
        this.paramExpression = parseOptionOf(expression, Participant.RootDeptOf.name());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.rule.engine.expression.Expression#evaluate(com.wellsoft.pt.rule.engine.Param)
     */
    @Override
    public Object evaluate(Param param) {
        return evaluate(param, Participant.RootDeptOf, paramExpression);
    }

}
