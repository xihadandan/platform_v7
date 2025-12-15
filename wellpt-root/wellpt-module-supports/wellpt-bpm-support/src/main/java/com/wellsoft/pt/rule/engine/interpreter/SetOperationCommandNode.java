/*
 * @(#)2015-6-25 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine.interpreter;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.rule.engine.*;
import com.wellsoft.pt.rule.engine.expression.Expression;
import com.wellsoft.pt.rule.engine.expression.ExpressionParserFactory;
import com.wellsoft.pt.rule.engine.suport.CommandName;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description: 集合运算
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
public class SetOperationCommandNode extends Node {
    private String name;

    private StringBuilder sb = new StringBuilder();
    private List<String> rawExpressions = new ArrayList<String>();
    private List<Expression> expressions = new ArrayList<Expression>();

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
        if (!CommandName.SetOperation.equals(name)) {
            throw new ParseException(name + " is undefined");
        }
        while (!CommandName.End.equals(context.currentToken())) {
            sb.append(context.currentToken());
            sb.append(CommandName.Blank);
            context.nextToken();
        }
        context.skipToken("end");

        rawExpressions = Arrays.asList(StringUtils.split(sb.toString().trim(), Separator.SEMICOLON.getValue()));

        for (String rawExpression : rawExpressions) {
            Expression expression = ExpressionParserFactory.getParser().parse(rawExpression);
            expressions.add(expression);
        }

    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.rule.engine.Executor#execute(com.wellsoft.pt.rule.engine.Param)
     */
    @Override
    public Result execute(Param param) throws ExecuteException {
        for (Expression expression : expressions) {
            expression.evaluate(param);
        }
        return Result.NORMAL;
    }

}
