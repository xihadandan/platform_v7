/*
 * @(#)2018年11月6日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.htmlparser.Parser;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.TextExtractingVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年11月6日.1	zhongzh		2018年11月6日		Create
 * </pre>
 * @date 2018年11月6日
 */
public class TextUtils {

    private static Logger LOG = LoggerFactory.getLogger(TextUtils.class);

    /**
     * Java过滤HTML标签实例
     *
     * @param inputString
     * @return
     */
    public static String html2Text(String inputString) {
        if (StringUtils.isBlank(inputString)) {
            return StringUtils.EMPTY;
        }
        Parser parser;
        String textStr = inputString;
        try {
            parser = new Parser(inputString);
            parser.setEncoding("UTF-8");
            // 创建StringFindingVisitor对象
            TextExtractingVisitor visitor = new TextExtractingVisitor();
            // 去除网页中的所有标签,提出纯文本内容
            parser.visitAllNodesWith(visitor);
            textStr = visitor.getExtractedText();
        } catch (ParserException e) {
            LOG.error(ExceptionUtils.getStackTrace(e));
        }
        return textStr;
    }
}
