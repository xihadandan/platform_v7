/*
 * @(#)2015-6-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine.interpreter;

import com.wellsoft.context.web.controller.Message;
import com.wellsoft.pt.rule.engine.*;
import com.wellsoft.pt.rule.engine.executor.ReturnExecutor;

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
public class ReturnCommandNode extends Node {

    private String name;
    private StringBuilder expression = new StringBuilder();
    private Executor executor;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.rule.engine.Node#parse(com.wellsoft.pt.rule.engine.Context)
     */
    @Override
    public void parse(Context context) throws ParseException {
        name = context.currentToken();
        context.skipToken(name);
        if (!name.equals("return")) {
            throw new ParseException(name + " is undefined");
        }
        while (!"end".equals(context.currentToken())) {
            expression.append(context.currentToken());
            context.nextToken();
        }
        context.skipToken("end");

        executor = new ReturnExecutor();// context.createExecutor(name);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.rule.engine.Executor#execute(com.wellsoft.pt.rule.engine.Param)
     */
    @Override
    public Result execute(Param param) throws ExecuteException {
        Context context = param.getContext();
        Integer level = param.getLevel();
        Message msg = param.getMsg();

        return executor.execute(new Param(context, level, null, msg));
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[return " + expression.toString() + " end]";
    }

}
