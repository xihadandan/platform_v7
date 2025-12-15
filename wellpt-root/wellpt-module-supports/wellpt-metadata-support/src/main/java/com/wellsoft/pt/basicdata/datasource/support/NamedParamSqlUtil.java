/*
 * @(#)2014-8-20 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datasource.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: 用来模拟实现命名参数功能
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-20.1	wubin		2014-8-20		Create
 * </pre>
 * @date 2014-8-20
 */
public class NamedParamSqlUtil {
    private Logger logger = LoggerFactory.getLogger(NamedParamSqlUtil.class);

    private Map<Integer, String> paramsMap = new HashMap<Integer, String>();

    public Map<Integer, String> getParamsMap() {
        return paramsMap;
    }

    public void emptyMap() {
        paramsMap.clear();
    }

    /**
     * 分析处理带命名参数的SQL语句。使用Map存储参数，然后将参数替换成?
     * <br/>
     * 作者：wallimn　时间：2009-1-8　下午12:14:10<br/>
     * 邮件：wallimn@sohu.com<br/>
     * 博客：http://blog.csdn.net/wallimn<br/>
     * 参数：<br/>
     *
     * @param sql
     * @return
     */
    public String parseSql(String sql) {
        String regex = "(:(\\w+))";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(sql);
        emptyMap();
        int idx = 1;
        while (m.find()) {
            //参数名称可能有重复，使用序号来做Key
            paramsMap.put(new Integer(idx++), m.group(2));
            //System.out.println(m.group(2));
        }
        String result = sql.replaceAll(regex, "?");
        logger.info("分析前：" + sql);
        logger.info("分析后：" + result);
        return result;
    }

    /**
     * 使用参数值Map，填充pStat
     * <br/>
     * 作者：wallimn　时间：2009-1-8　下午12:15:36<br/>
     * 邮件：wallimn@sohu.com<br/>
     * 博客：http://blog.csdn.net/wallimn<br/>
     * 参数：<br/>
     *
     * @param pStat
     * @param pMap  命名参数的值表，其中的值可以比较所需的参数多。
     * @return
     */
    public boolean fillParameters(PreparedStatement pStat, Map<String, Object> pMap) {
        boolean result = true;
        String paramName = null;
        Object paramValue = null;
        int idx = 1;
        for (java.util.Iterator<Entry<Integer, String>> itr = paramsMap.entrySet().iterator(); itr.hasNext(); ) {
            Entry<Integer, String> entry = (Entry<Integer, String>) itr.next();
            paramName = entry.getValue();
            idx = entry.getKey().intValue();
            //不包含会返回null
            if (pMap != null) {
                paramValue = pMap.get(paramName);
            }
            try {
                //paramValue为null，会出错吗？需要测试
                pStat.setObject(idx, paramValue);
            } catch (Exception e) {
                logger.error("填充参数出错，原因：" + e.getMessage(), e);
                result = false;
            }
        }
        return result;
    }
}
