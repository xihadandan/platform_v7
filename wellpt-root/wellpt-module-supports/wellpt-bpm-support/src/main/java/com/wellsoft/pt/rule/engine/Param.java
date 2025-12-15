/*
 * @(#)2015-6-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine;

import com.wellsoft.context.web.controller.Message;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-24.1	zhulh		2015-6-24		Create
 * </pre>
 * @date 2015-6-24
 */
public class Param {
    private Context context;

    private Integer level;

    private StringBuilder expression;

    private Message msg;

    /**
     * @param context
     * @param level
     * @param expression
     * @param msg
     */
    public Param(Context context, Integer level, StringBuilder expression, Message msg) {
        super();
        this.context = context;
        this.level = level;
        this.expression = expression;
        this.msg = msg;
    }

    /**
     * @return the context
     */
    public Context getContext() {
        return context;
    }

    /**
     * @param context 要设置的context
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * @return the level
     */
    public Integer getLevel() {
        return level;
    }

    /**
     * @param level 要设置的level
     */
    public void setLevel(Integer level) {
        this.level = level;
    }

    /**
     * @return the expression
     */
    public StringBuilder getExpression() {
        return expression;
    }

    /**
     * @param expression 要设置的expression
     */
    public void setExpression(StringBuilder expression) {
        this.expression = expression;
    }

    /**
     * @return the msg
     */
    public Message getMsg() {
        return msg;
    }

    /**
     * @param msg 要设置的msg
     */
    public void setMsg(Message msg) {
        this.msg = msg;
    }

}
