/*
 * @(#)2014-3-7 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.report.support;

import com.wellsoft.context.util.json.JsonUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Description: 报表获取json值自定义函数
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-3-7.1	zhouyq		2014-3-7		Create
 * </pre>
 * @date 2014-3-7
 */
@Deprecated
public class JsonValue /*extends AbstractFunction*/ {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 获取JSON的值
     * <p>
     * (non-Javadoc)
     *
     * @see com.fr.script.AbstractFunction#run(java.lang.Object[])
     */
    //@Override
    public Object run(Object[] args) {
        String result = "";
        Object para;
        for (int i = 0; i < args.length; i++) {
            para = args[i];
            String json = para.toString();
            try {
                Map map = JsonUtils.toMap(json);
                Set<String> key = map.keySet();
                for (Iterator it = key.iterator(); it.hasNext(); ) {
                    String str = (String) it.next();
                    result = (String) map.get(str);
                }
            } catch (Exception e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return result;
    }
}
