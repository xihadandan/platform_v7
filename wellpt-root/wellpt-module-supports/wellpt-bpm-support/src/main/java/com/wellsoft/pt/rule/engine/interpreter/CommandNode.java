/*
 * @(#)2015-6-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine.interpreter;

import com.wellsoft.context.web.controller.Message;
import com.wellsoft.pt.rule.engine.*;
import com.wellsoft.pt.rule.engine.suport.CommandName;

/**
 * Description: 命令结点类 <command> ::= <repeat command> | <primitive command>
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
public class CommandNode extends Node {

    private Node node;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.rule.engine.Node#parse(com.wellsoft.pt.rule.engine.Context)
     */
    @Override
    public void parse(Context context) throws ParseException {
        String token = context.currentToken();
        if (token.equals("define")) {
            node = new DefineCommandNode();
            node.parse(context);
        } else if (token.equals("set")) {
            node = new SetCommandNode();
            node.parse(context);
        } else if (token.equals("if")) {
            node = new IfCommandNode();
            node.parse(context);
        } else if (token.equals("log")) {
            node = new LogCommandNode();
            node.parse(context);
        } else if (token.equals("return")) {
            node = new ReturnCommandNode();
            node.parse(context);
        } else if (token.equalsIgnoreCase(CommandName.SetOperation)) {
            node = new SetOperationCommandNode();
            node.parse(context);
        }
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

        logger.debug("start execute CommandNode at level " + level);
        Result result = node.execute(new Param(context, level, null, msg));
        logger.debug("over execute CommandNode at level " + level);
        return result;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return node.toString();
    }

}
