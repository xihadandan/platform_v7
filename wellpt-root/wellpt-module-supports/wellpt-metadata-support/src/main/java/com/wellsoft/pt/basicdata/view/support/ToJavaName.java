/*
 * @(#)2014-10-24 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.view.support;

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-24.1	wubin		2014-10-24		Create
 * </pre>
 * @date 2014-10-24
 */
public class ToJavaName {
    public static String dbNameToJavaName(String dbName, boolean firstCharUppered) {
        String name = dbName;
        if (name == null || name.trim().length() == 0) {
            return "";
        }
        String[] parts = null;
        if (name.indexOf("_") != -1) {
            parts = name.toLowerCase().split("_");
        } else {
            parts = name.split("_");
        }
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.length() == 0) {
                continue;
            }
            sb.append(part.substring(0, 1).toUpperCase());
            sb.append(part.substring(1));
        }
        if (firstCharUppered) {
            return sb.toString();
        } else {
            return sb.substring(0, 1).toLowerCase() + sb.substring(1);
        }
    }
}
