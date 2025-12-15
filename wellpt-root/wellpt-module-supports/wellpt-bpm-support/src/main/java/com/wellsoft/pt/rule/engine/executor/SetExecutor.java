/*
 * @(#)2015-6-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine.executor;

import com.wellsoft.pt.rule.engine.Context;
import com.wellsoft.pt.rule.engine.ExecuteException;
import com.wellsoft.pt.rule.engine.Param;
import com.wellsoft.pt.rule.engine.Result;

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
public class SetExecutor extends AbstractExecutor {

    public static String getVarName(String expression) {
        if (!expression.startsWith("$") || expression.indexOf("=") == -1) {
            throw new ExecuteException("var define error of expression: " + expression);
        }
        return expression.substring(0, expression.indexOf("="));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.rule.engine.Executor#execute(com.wellsoft.pt.rule.engine.Param)
     */
    @Override
    public Result execute(Param param) throws ExecuteException {
        StringBuilder exp = param.getExpression();
        Integer level = param.getLevel();
        Context context = param.getContext();
        Result result = Result.NORMAL;
        context.exeExpression(exp.toString());
        logger.debug("set " + exp + " at leve " + level);

        return result;
    }
}
