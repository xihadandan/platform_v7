/*
 * @(#)2014-3-5 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.util.str;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 * @date 2014-3-5
 * @see org.apache.commons.lang.StringUtils
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-3-5.1	Administrator		2014-3-5		Create
 * </pre>
 */
public class StringUtil {

    /**
     * 将字符串的第一个字母转成小写
     *
     * @param sourceStr
     * @return
     * @see org.apache.commons.lang.StringUtils.uncapitalize
     */
    public static String replaceFirstStrToLowercase(String sourceStr) {
        String str1 = sourceStr.substring(0, 1).toLowerCase();// 直接将字符串第一个不管是数字还是字母都小写
        String str2 = sourceStr.substring(1, sourceStr.length());// 截取字符串第二个以后
        sourceStr = str1 + str2;
        return sourceStr;
    }

    /**
     * 去除字符串"_"并让它的下一个字母为大写
     *
     * @param sourceStr
     * @return
     */
    public static String replaceUnderlineAndfirstToUpper(String sourceStr) {
        StringBuilder sb = new StringBuilder();
        sb.append(sourceStr);
        int count = sb.indexOf("_");
        while (count != 0) {
            int num = sb.indexOf("_", count);
            count = num + 1;
            if (num != -1) {
                char ss = sb.charAt(count);
                char ia = (char) (ss - 32);
                sb.replace(count, count + 1, ia + "");
            }
        }
        String ss = sb.toString().replaceAll("_", "");
        return ss;
    }

    /**
     * 字符在字符串中出现的次数
     *
     * @param string
     * @param a
     * @return
     * @see org.apache.commons.lang.StringUtils.countMatches
     */
    public static int occurTimes(String string, String a) {
        int pos = -2;
        int n = 0;
        if (!string.contains("-")) {
            return 0;
        } else {
            while (pos != -1) {
                if (pos == -2) {
                    pos = -1;
                }
                pos = string.indexOf(a, pos + 1);
                if (pos != -1) {
                    n++;
                }
            }
            return n;
        }
    }
}
