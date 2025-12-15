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
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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
public class LogExecutor extends AbstractExecutor {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.rule.engine.Executor#execute(com.wellsoft.pt.rule.engine.Param)
     */
    @Override
    public Result execute(Param param) throws ExecuteException {
        StringBuilder exp = param.getExpression();
        Context context = param.getContext();

        String content = exp.toString();
        List<String> varNames = getVarNames(exp.toString());
        for (String varName : varNames) {
            if (varName.endsWith(".")) {
                varName = varName.substring(0, varName.length() - 1);
            }
            Object value = context.getValue(varName);
            if (value == null) {
                value = "null";
            }
            content = StringUtils.replaceOnce(content, varName, value.toString());
        }
        logger.debug(content);

        return Result.NORMAL;
    }

    private List<String> getVarNames(String string) {
        List<String> tokenList = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(string, " \t\n\r\f,，。()[]");
        String token = null;
        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();
            if (token.startsWith("$")) {
                tokenList.add(token);
            }

        }
        return tokenList;
    }
}
