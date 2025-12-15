/*
 * @(#)2015-6-25 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine.expression;

import com.wellsoft.pt.rule.engine.Param;
import com.wellsoft.pt.rule.engine.suport.Operator;

import java.util.Collection;
import java.util.HashSet;

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
public class SetOperationExpression extends AbstractExpression {

    private Expression left;

    private String operator;

    private Expression right;

    /**
     * @param tmp
     */
    public SetOperationExpression(Expression leftExp, String operator, Expression rightExp) {
        this.left = leftExp;
        this.operator = operator;
        this.right = rightExp;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.rule.engine.expression.Expression#evaluate(com.wellsoft.pt.rule.engine.Param)
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object evaluate(Param param) {
        Collection<Object> leftSet = new HashSet<Object>();
        Collection<Object> rightSet = new HashSet<Object>();
        // 并集计算
        if (Operator.NAME_UNION.equals(operator)) {
            leftSet.addAll((Collection<Object>) left.evaluate(param));
            rightSet.addAll((Collection<Object>) right.evaluate(param));
            leftSet.addAll(rightSet);
        } else if (Operator.NAME_INTERSECTION.equals(operator)) {
            // 交集计算
            leftSet.addAll((Collection<Object>) left.evaluate(param));
            rightSet.addAll((Collection<Object>) right.evaluate(param));
            leftSet.retainAll(rightSet);
        }
        return leftSet;
    }

}
