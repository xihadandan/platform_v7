/*
 * @(#)2015-6-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine.interpreter;

import com.wellsoft.context.web.controller.Message;
import com.wellsoft.pt.rule.engine.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 命令列表类 <command list> ::= <command>* end
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
public class CommandListNode extends Node {

    private List<Node> list = new ArrayList<Node>();

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.rule.engine.Node#parse(com.wellsoft.pt.rule.engine.Context)
     */
    @Override
    public void parse(Context context) throws ParseException {
        while (true) {
            if (context.currentToken() == null) {
                throw new ParseException("Missing 'end'");
            } else if (context.currentToken().equals("end")) {
                context.skipToken("end");
                break;
            } else if (context.currentToken().equals("}")) {
                break;
            } else {
                Node commandNode = new CommandNode();
                commandNode.parse(context);
                list.add(commandNode);
            }
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
        Result result = Result.NORMAL;
        logger.debug("start execute CommandListNode at level " + level);
        for (int index = 0; index < list.size(); index++) {
            result = list.get(index).execute(new Param(context, level, null, msg));
            if (result.equals(Result.RETURN)) {
                return Result.RETURN;
            }
        }
        // 清除这个级别中定义的变量
        clearTempVariable(context, level);
        logger.debug("over execute CommandListNode at level " + level);
        return result;
    }

    /**
     * 清除临时变量
     *
     * @param context
     * @param level
     */
    private void clearTempVariable(Context context, Integer level) {
        context.clearVar(level);
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "" + list.toString();
    }

}
