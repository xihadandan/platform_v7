/*
 * @(#)2013-4-26 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.expression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public abstract class AbstractCondition implements Condition {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private String expression;

    /**
     * 如何描述该构造方法
     */
    public AbstractCondition() {
        super();
    }

    public AbstractCondition(String expression) {
        this.expression = expression;
    }

    /**
     * @return the expression
     */
    public String getExpression() {
        return expression;
    }

    /**
     * @param expression 要设置的expression
     */
    protected void setExpression(String expression) {
        this.expression = expression;
    }

}
