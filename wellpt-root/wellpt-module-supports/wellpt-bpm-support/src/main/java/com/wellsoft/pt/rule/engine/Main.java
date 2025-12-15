/*
 * @(#)2015-6-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine;

import com.wellsoft.context.web.controller.Message;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.rule.engine.interpreter.ProgramNode;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;

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
public class Main {

    /**
     * 如何描述该方法
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String path = "E:\\zhulh\\workspace\\wellpt-core\\src\\main\\java\\com\\wellsoft\\pt\\rule\\engine\\program.txt";
        FileInputStream input = new FileInputStream(new File(path));

        Message msg = new ResultMessage();
        Node node = new ProgramNode();
        // String program =
        // "program define $a = 3 end return end set $a = 123 end log $a end end";
        // a M b U c
        // String program = "program union(intersection('', ''), '') end";
        String program = IOUtils.toString(input);
        System.out.println(program);
        Context context = new Context(program);
        Result result = new Result();
        try {
            node.parse(context);
            Integer level = 0;
            Param param = new Param(context, level, null, msg);
            result = node.execute(param);
        } catch (Exception e) {
            throw new ExecuteException(e);
        }
        msg.setData(result.data);
    }

}
