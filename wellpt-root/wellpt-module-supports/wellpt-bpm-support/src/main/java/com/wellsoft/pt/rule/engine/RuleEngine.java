/*
 * @(#)2015-6-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine;

import com.wellsoft.context.web.controller.Message;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.rule.engine.interpreter.ProgramNode;
import org.apache.commons.lang.exception.ExceptionUtils;
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
 * 2015-6-24.1	zhulh		2015-6-24		Create
 * </pre>
 * @date 2015-6-24
 */
public class RuleEngine {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Context context = new Context("");

    public Object parse(String scriptText) {
        String program = "program " + scriptText + " end";
        Context context = new Context(program);
        Node node = new ProgramNode();
        node.parse(context);
        return node;
    }

    public void setVariable(String name, Object value) {
        context.put(name, value, 0);
    }

    public Object getVariable(String name) {
        return context.getValue(name);
    }

    public Result execute(String scriptText) {
        Message msg = new ResultMessage();
        Node node = new ProgramNode();
        String program = "program " + scriptText + " end";
        context.init(program);
        Result result = new Result();
        try {
            node.parse(context);
            Integer level = 0;
            Param param = new Param(context, level, null, msg);
            result = node.execute(param);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new ExecuteException(e);
        }

        return result;
    }
}
