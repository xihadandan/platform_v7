/*
 * @(#)2015-6-26 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine.expression;

import com.wellsoft.pt.rule.engine.Param;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-26.1	zhulh		2015-6-26		Create
 * </pre>
 * @date 2015-6-26
 */
public class VariableExpression extends AbstractExpression {

    private String varName;

    /**
     * 如何描述该构造方法
     *
     * @param tmp
     */
    public VariableExpression(String varName) {
        this.varName = varName;
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
        return param.getContext().getValue(varName);
    }

}
