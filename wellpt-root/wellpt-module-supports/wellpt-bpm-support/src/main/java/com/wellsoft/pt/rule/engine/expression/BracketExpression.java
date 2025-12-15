/*
 * @(#)2015年8月28日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine.expression;

import com.wellsoft.pt.rule.engine.Param;
import com.wellsoft.pt.rule.engine.suport.CommandName;

/**
 * Description: 括号表达式
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
public class BracketExpression implements Expression {
    private String expression;
    private String leftBracket;
    private Expression paramExpression;
    private String rightBracket;

    /**
     * 如何描述该构造方法
     *
     * @param expression
     */
    public BracketExpression(String expression) {
        this.expression = expression;
        this.leftBracket = expression.substring(0, 1);
        int length = expression.length();
        paramExpression = ExpressionParserFactory.getParser().parse(expression.substring(1, length - 1));
        this.rightBracket = expression.substring(length - 1, length);
        if (!this.rightBracket.equals(CommandName.RightBracket)) {
            throw new RuntimeException("表达式[" + expression + "]缺失右括号!");
        }
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
        return paramExpression.evaluate(param);
    }

}
