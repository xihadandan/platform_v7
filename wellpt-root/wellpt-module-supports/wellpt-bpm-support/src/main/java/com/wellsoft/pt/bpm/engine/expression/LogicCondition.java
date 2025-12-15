/*
 * @(#)2013-4-26 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.expression;

import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.node.Node;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-26.1	zhulh		2013-4-26		Create
 * </pre>
 * @date 2013-4-26
 */
public class LogicCondition extends AbstractCondition {

    private String logic;

    /**
     * @param expression
     */
    public LogicCondition(String expression) {
        this.logic = expression;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.expression.Condition#evaluate(com.wellsoft.pt.bpm.engine.core.Token)
     */
    @Override
    public String evaluate(Token token, Node to) {
        return this.logic;
    }

}
