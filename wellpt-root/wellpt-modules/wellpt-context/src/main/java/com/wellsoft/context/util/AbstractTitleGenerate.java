/*
 * @(#)2021年1月18日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.util;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.date.DateUtil;

import java.util.Calendar;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年1月18日.1	zhongzh		2021年1月18日		Create
 * </pre>
 * @date 2021年1月18日
 */
public abstract class AbstractTitleGenerate {

    /**
     * 如何描述该方法
     *
     * @return
     */
    public static Map<String, Object> getCommonVariables() {
        Map<String, Object> commonVariables = Maps.newHashMap();
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);// 获取年份
        int month = cal.get(Calendar.MONTH) + 1;// 获取月份
        int day = cal.get(Calendar.DATE);// 获取日
        int hour = cal.get(Calendar.HOUR_OF_DAY);// 小时
        int minute = cal.get(Calendar.MINUTE);// 分
        int second = cal.get(Calendar.SECOND);// 秒
        commonVariables.put("年", String.valueOf(year));
        commonVariables.put("月", DateUtil.getFormatDate(month));
        commonVariables.put("日", DateUtil.getFormatDate(day));
        commonVariables.put("时", DateUtil.getFormatDate(hour));
        commonVariables.put("分", DateUtil.getFormatDate(minute));
        commonVariables.put("秒", DateUtil.getFormatDate(second));
        commonVariables.put("简年", String.valueOf(year).substring(2));
        return commonVariables;
    }

    /**
     * 取出字符串中非字符
     *
     * @param str
     * @return
     */
    protected static String fetchLetter(String str) {
        if (str != null) {
            StringBuilder builder = new StringBuilder();
            char[] chs = str.toCharArray();
            for (char ch : chs) {
                if ((ch < 'Z' && ch > 'A') || (ch < 'z' && ch > 'a')) {
                    builder.append(ch);
                }
            }
            if (builder.length() > 0) {
                return builder.toString();
            }
        }
        return null;
    }

    public static class TitleExpression {
        private static String prefix = "${";
        private static String suffix = "}";
        private StringBuilder express;
        private int currentPositon = 0;

        public TitleExpression(String expression) {
            express = new StringBuilder(expression != null ? expression : "");
        }

        public boolean hasNext() {
            if (currentPositon >= express.length()) {
                return false;
            } else {
                int prefixPos = getPrefixPos();
                int suffixPos = getSuffixPos();
                return !(prefixPos >= suffixPos && prefixPos < currentPositon && suffixPos < currentPositon);
            }
        }

        public String next() {
            if (currentPositon >= express.length()) {
                return null;
            }
            int prefixPos = getPrefixPos();
            int suffixPos = getSuffixPos();
            if (prefixPos < currentPositon || suffixPos < currentPositon) {
                return null;
            }
            String next = express.substring(prefixPos + 2, suffixPos);
            currentPositon = suffixPos + 1;
            return next;
        }

        public void clear() {
            currentPositon = 0;
        }

        private int getPrefixPos() {
            return express.indexOf(prefix, currentPositon);
        }

        private int getSuffixPos() {
            return express.indexOf(suffix, currentPositon);
        }
    }
}
